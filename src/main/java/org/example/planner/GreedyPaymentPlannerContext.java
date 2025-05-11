package org.example.planner;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;

import java.math.BigDecimal;
import java.util.*;

public class GreedyPaymentPlannerContext {
    private final Map<String, BigDecimal> remainingLimitsByPaymentMethod;
    private final Map<String, OrderPaymentBreakdown> orderPaymentBreakdownByOrderId;
    private final Set<String> unfulfilledOrderIds;

    public GreedyPaymentPlannerContext(List<Order> orders, List<PaymentMethod> paymentMethods) {
        this.remainingLimitsByPaymentMethod = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            this.remainingLimitsByPaymentMethod.put(paymentMethod.getId(), paymentMethod.getLimit());
        }

        this.orderPaymentBreakdownByOrderId = new HashMap<>();
        for (Order order : orders) {
            orderPaymentBreakdownByOrderId.put(order.getId(), new OrderPaymentBreakdown(order));
        }

        this.unfulfilledOrderIds = new HashSet<>();
        for (Order order : orders) {
            unfulfilledOrderIds.add(order.getId());
        }
    }
}