package org.example.planner;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;

import java.util.List;

public interface PaymentPlanner {
    List<OrderPaymentBreakdown> plan(List<Order> orders, List<PaymentMethod> methods);
}