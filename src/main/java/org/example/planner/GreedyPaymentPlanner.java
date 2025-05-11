package org.example.planner;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;

import java.math.BigDecimal;
import java.util.*;

public class GreedyPaymentPlanner implements PaymentPlanner {
    @Override
    public List<OrderPaymentBreakdown> plan(List<Order> orders, List<PaymentMethod> paymentMethods, String pointsId) {
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(orders, paymentMethods);
        allocatePayments(context, paymentMethods, pointsId);
        handleUnpaidOrders(context, paymentMethods, pointsId);
        return new ArrayList<>(context.getAllOrderPaymentBreakdowns());
    }

    private void allocatePayments(GreedyPaymentPlannerContext context,
                                  List<PaymentMethod> paymentMethods,
                                  String pointsId) {
        PriorityQueue<PaymentMethod> queue = createPrioritizedMethodQueue(paymentMethods, context);

        while (!queue.isEmpty()) {
            PaymentMethod method = queue.poll();
            Optional<Order> mostValuableEligibleOrderOpt = selectMostValuableEligibleOrder(context, method, pointsId);

            if (mostValuableEligibleOrderOpt.isEmpty()) continue;

            Order order = mostValuableEligibleOrderOpt.get();
            allocateOrderWithMethod(context, method, order);

            if (context.getRemainingLimit(method.getId()).compareTo(BigDecimal.ZERO) > 0) {
                queue.add(method);
            }
        }
    }

    private PriorityQueue<PaymentMethod> createPrioritizedMethodQueue(List<PaymentMethod> methods, GreedyPaymentPlannerContext context) {
        return new PriorityQueue<>(
                Comparator.comparing((PaymentMethod paymentMethod) ->
                        paymentMethod.getDiscount().multiply(context.getRemainingLimit(paymentMethod.getId()))
                ).reversed()
        ) {{
            addAll(methods);
        }};
    }

    private Optional<Order> selectMostValuableEligibleOrder(GreedyPaymentPlannerContext context, PaymentMethod method, String pointsId) {
        BigDecimal remainingPaymentMethodLimit = context.getRemainingLimit(method.getId());

        return context.getUnfulfilledOrderIds().stream()
                .map(context::getOrderPaymentBreakdown)
                .map(OrderPaymentBreakdown::getOrder)
                .filter(order -> pointsId.equals(method.getId()) ||
                        (order.getPromotions() != null && order.getPromotions().contains(method.getId())))
                .filter(order -> {
                    // Assumption: An order is considered affordable if it can be fully paid
                    // using the current method before applying any discount
                    // Alternatively, we could pre-calculate the discounted amount (as shown below):

                    // BigDecimal discount = order.getValue().multiply(method.getDiscount()).divide(BigDecimal.valueOf(100));
                    // BigDecimal amountToPayAfterDiscount = order.getValue().subtract(discount);
                    // return amountToPayAfterDiscount.compareTo(remainingPaymentMethodLimit) <= 0;
                    return order.getValue().compareTo(remainingPaymentMethodLimit) <= 0;
                })
                .max(Comparator.comparing(Order::getValue));
    }

    private void allocateOrderWithMethod(GreedyPaymentPlannerContext context, PaymentMethod method, Order order) {
        BigDecimal orderValue = order.getValue();
        BigDecimal discount = orderValue.multiply(method.getDiscount()).divide(BigDecimal.valueOf(100));
        BigDecimal amountToPayAfterDiscount = orderValue.subtract(discount);

        context.getOrderPaymentBreakdown(order.getId())
                .getAmountPaidByMethodId()
                .put(method.getId(), amountToPayAfterDiscount);

        context.decreaseLimit(method.getId(), amountToPayAfterDiscount);
        context.markAsFulfilled(order.getId());
    }

    private void handleUnpaidOrders(GreedyPaymentPlannerContext context,
                                    List<PaymentMethod> allPaymentMethods,
                                    String pointsId) {
        Set<String> unfulfilledIds = new HashSet<>(context.getUnfulfilledOrderIds());

        for (String orderId : unfulfilledIds) {
            OrderPaymentBreakdown orderPaymentBreakdown = context.getOrderPaymentBreakdown(orderId);
            Order order = orderPaymentBreakdown.getOrder();
            BigDecimal remainingUnpaidOrderValue = order.getValue();

            remainingUnpaidOrderValue = tryToApplyPoints(context, orderId, remainingUnpaidOrderValue, pointsId);
            payRemainingWithOtherMethods(context, allPaymentMethods, orderId, remainingUnpaidOrderValue);
        }
    }

    private BigDecimal tryToApplyPoints(GreedyPaymentPlannerContext context, String orderId, BigDecimal remainingUnpaidOrderValue, String pointsId) {
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown(orderId);
        BigDecimal pointsThresholdForDiscount = remainingUnpaidOrderValue.multiply(BigDecimal.valueOf(0.1));
        BigDecimal pointsAvailable = context.getRemainingLimit(pointsId);
        boolean isLastUnpaidOrder = context.getUnfulfilledOrderIds().size() == 1;

        if (pointsAvailable.compareTo(pointsThresholdForDiscount) >= 0) {
            BigDecimal pointsAmountAllocatedToOrder = isLastUnpaidOrder ? pointsAvailable : pointsThresholdForDiscount;
            breakdown.getAmountPaidByMethodId().put(pointsId, pointsAmountAllocatedToOrder);
            context.decreaseLimit(pointsId, pointsAmountAllocatedToOrder);
            remainingUnpaidOrderValue = remainingUnpaidOrderValue
                    .subtract(remainingUnpaidOrderValue.multiply(BigDecimal.valueOf(0.1)))
                    .subtract(pointsAmountAllocatedToOrder);
        }

        return remainingUnpaidOrderValue;
    }

    private void payRemainingWithOtherMethods(GreedyPaymentPlannerContext context,
                                              List<PaymentMethod> allPaymentMethods,
                                              String orderId,
                                              BigDecimal remainingUnpaidOrderValue) {
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown(orderId);

        // Sort methods by available limit descending
        // This step may be redundant for correctness but it aligns with the example logic described in the task pdf
        List<PaymentMethod> sortedPaymentMethods = allPaymentMethods.stream()
                .sorted(Comparator.comparing(method -> context.getRemainingLimit(method.getId()), Comparator.reverseOrder()))
                .toList();

        for (PaymentMethod method : sortedPaymentMethods) {
            BigDecimal methodLimit = context.getRemainingLimit(method.getId());
            if (methodLimit.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal amountToAllocateFromMethod = remainingUnpaidOrderValue.min(methodLimit);
            if (amountToAllocateFromMethod.compareTo(BigDecimal.ZERO) > 0) {
                breakdown.getAmountPaidByMethodId().merge(method.getId(), amountToAllocateFromMethod, BigDecimal::add);
                context.decreaseLimit(method.getId(), amountToAllocateFromMethod);
                remainingUnpaidOrderValue = remainingUnpaidOrderValue.subtract(amountToAllocateFromMethod);
                if (remainingUnpaidOrderValue.compareTo(BigDecimal.ZERO) <= 0) break;
            }
        }

        if (remainingUnpaidOrderValue.compareTo(BigDecimal.ZERO) <= 0) {
            context.markAsFulfilled(orderId);
        } else {
            throw new IllegalStateException("Unable to fully pay order: " + orderId);
        }
    }
}