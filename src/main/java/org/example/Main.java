package org.example;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.planner.GreedyPaymentPlanner;
import org.example.planner.PaymentPlanner;
import org.example.breakdown.OrderPaymentBreakdown;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Order> orders = new ArrayList<>();
        List<PaymentMethod> methods = new ArrayList<>();
        String pointsId = "PUNKTY";

        PaymentPlanner planner = new GreedyPaymentPlanner();
        List<OrderPaymentBreakdown> results = planner.plan(orders, methods, pointsId);
    }
}