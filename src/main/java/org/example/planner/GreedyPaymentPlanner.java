package org.example.planner;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;

import java.util.Collections;
import java.util.List;

public class GreedyPaymentPlanner implements PaymentPlanner {

    @Override
    public List<OrderPaymentBreakdown> plan(List<Order> orders, List<PaymentMethod> methods, String pointsId) {
        return Collections.emptyList();
    }
}