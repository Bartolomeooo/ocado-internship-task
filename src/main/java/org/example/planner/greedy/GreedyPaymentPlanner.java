package org.example.planner.greedy;

import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;
import org.example.planner.PaymentPlanner;

import java.util.*;

public class GreedyPaymentPlanner implements PaymentPlanner {
    private final PlannerConfig config;

    public GreedyPaymentPlanner(PlannerConfig config) {
        this.config = config;
    }

    @Override
    public List<OrderPaymentBreakdown> plan(List<Order> orders, List<PaymentMethod> paymentMethods) {
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(orders, paymentMethods);
        new GreedyAllocator(config).allocatePayments(context, paymentMethods);
        new UnpaidOrdersHandler(config).handleUnpaidOrders(context, paymentMethods);
        return new ArrayList<>(context.getAllOrderPaymentBreakdowns());
    }
}