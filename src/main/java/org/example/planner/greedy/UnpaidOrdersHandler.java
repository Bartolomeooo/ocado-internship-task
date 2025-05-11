package org.example.planner.greedy;

import org.example.breakdown.OrderPaymentBreakdown;
import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UnpaidOrdersHandler {
    private final PlannerConfig config;

    public UnpaidOrdersHandler(PlannerConfig config) {
        this.config = config;
    }

    public void handleUnpaidOrders(GreedyPaymentPlannerContext context,
                                   List<PaymentMethod> allPaymentMethods) {
        List<String> unfulfilledIds = context.getUnfulfilledOrderIds().stream()
                .sorted(Comparator.comparing(
                        id -> context.getOrderPaymentBreakdown(id).getOrder().getValue(), Comparator.reverseOrder()
                ))
                .toList();

        for (String orderId : unfulfilledIds) {
            OrderPaymentBreakdown orderPaymentBreakdown = context.getOrderPaymentBreakdown(orderId);
            Order order = orderPaymentBreakdown.getOrder();
            BigDecimal remainingUnpaidOrderValue = order.getValue();

            remainingUnpaidOrderValue = tryToApplyPoints(context, orderId, remainingUnpaidOrderValue, config.getPointsMethodId());
            payRemainingWithOtherMethods(context, allPaymentMethods, orderId, remainingUnpaidOrderValue);
        }
    }

    private BigDecimal tryToApplyPoints(GreedyPaymentPlannerContext context,
                                        String orderId, BigDecimal remainingUnpaidOrderValue,
                                        String pointsId) {
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown(orderId);
        BigDecimal pointsThresholdForDiscount = remainingUnpaidOrderValue.multiply(config.getMinPointsRatioForDiscount());
        BigDecimal pointsAvailable = context.getRemainingLimit(pointsId);
        boolean isLastUnpaidOrder = context.getUnfulfilledOrderIds().size() == 1;

        if (pointsAvailable.compareTo(pointsThresholdForDiscount) >= 0) {
            BigDecimal pointsAmountAllocatedToOrder = isLastUnpaidOrder ? pointsAvailable : pointsThresholdForDiscount;
            breakdown.getAmountPaidByMethodId().put(pointsId, pointsAmountAllocatedToOrder);
            context.decreaseLimit(pointsId, pointsAmountAllocatedToOrder);
            remainingUnpaidOrderValue = remainingUnpaidOrderValue
                    .subtract(remainingUnpaidOrderValue.multiply(config.getDiscountPercentage()))
                    .subtract(pointsAmountAllocatedToOrder);
        }

        return remainingUnpaidOrderValue;
    }

    private void payRemainingWithOtherMethods(GreedyPaymentPlannerContext context,
                                              List<PaymentMethod> allPaymentMethods,
                                              String orderId,
                                              BigDecimal remainingUnpaidOrderValue) {
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown(orderId);

        List<PaymentMethod> sortedPaymentMethods = allPaymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getId().equals(config.getPointsMethodId()))
                .sorted(Comparator.comparing(method -> context.getRemainingLimit(method.getId()), Comparator.reverseOrder()))
                .toList();

        for (PaymentMethod paymentMethod : sortedPaymentMethods) {
            BigDecimal paymentMethodLimit = context.getRemainingLimit(paymentMethod.getId());
            if (paymentMethodLimit .compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal amountToAllocateFromMethod = remainingUnpaidOrderValue.min(paymentMethodLimit );
            if (amountToAllocateFromMethod.compareTo(BigDecimal.ZERO) > 0) {
                breakdown.getAmountPaidByMethodId().put(paymentMethod.getId(), amountToAllocateFromMethod);
                context.decreaseLimit(paymentMethod.getId(), amountToAllocateFromMethod);
                remainingUnpaidOrderValue = remainingUnpaidOrderValue.subtract(amountToAllocateFromMethod);
            }
            break;
        }

        if (remainingUnpaidOrderValue.compareTo(BigDecimal.ZERO) <= 0) {
            context.markAsFulfilled(orderId);
        } else {
            throw new IllegalStateException("Unable to fully pay order: " + orderId);
        }
    }
}