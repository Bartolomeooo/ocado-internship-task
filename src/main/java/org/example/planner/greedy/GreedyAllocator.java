package org.example.planner.greedy;

import org.example.breakdown.OrderPaymentBreakdown;
import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class GreedyAllocator {
    private final PlannerConfig config;

    public GreedyAllocator(PlannerConfig config) {
        this.config = config;
    }

    public void allocatePayments(GreedyPaymentPlannerContext context,
                                 List<PaymentMethod> paymentMethods) {
        PriorityQueue<PaymentMethod> queue = createPrioritizedMethodQueue(paymentMethods, context);

        while (!queue.isEmpty()) {
            PaymentMethod method = queue.poll();
            Optional<Order> mostValuableEligibleOrderOpt = selectMostValuableEligibleOrder(context, method);

            if (mostValuableEligibleOrderOpt.isEmpty()) continue;

            Order order = mostValuableEligibleOrderOpt.get();
            allocateOrderWithMethod(context, method, order);

            if (context.getRemainingLimit(method.getId()).compareTo(BigDecimal.ZERO) > 0) {
                queue.add(method);
            }
        }
    }

    private PriorityQueue<PaymentMethod> createPrioritizedMethodQueue(List<PaymentMethod> methods,
                                                                      GreedyPaymentPlannerContext context) {
        return new PriorityQueue<>(
                Comparator.comparing((PaymentMethod paymentMethod) ->
                        paymentMethod.getDiscount().multiply(context.getRemainingLimit(paymentMethod.getId()))
                ).reversed()
        ) {{
            addAll(methods);
        }};
    }

    private Optional<Order> selectMostValuableEligibleOrder(GreedyPaymentPlannerContext context,
                                                            PaymentMethod method) {
        BigDecimal remainingPaymentMethodLimit = context.getRemainingLimit(method.getId());

        return context.getUnfulfilledOrderIds().stream()
                .map(context::getOrderPaymentBreakdown)
                .map(OrderPaymentBreakdown::getOrder)
                .filter(order -> config.getPointsMethodId().equals(method.getId()) ||
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

    private void allocateOrderWithMethod(GreedyPaymentPlannerContext context,
                                         PaymentMethod method,
                                         Order order) {
        BigDecimal orderValue = order.getValue();
        BigDecimal discount = orderValue.multiply(method.getDiscount()).divide(BigDecimal.valueOf(100));
        BigDecimal amountToPayAfterDiscount = orderValue.subtract(discount);

        context.getOrderPaymentBreakdown(order.getId())
                .getAmountPaidByMethodId()
                .put(method.getId(), amountToPayAfterDiscount);

        context.decreaseLimit(method.getId(), amountToPayAfterDiscount);
        context.markAsFulfilled(order.getId());
    }

}