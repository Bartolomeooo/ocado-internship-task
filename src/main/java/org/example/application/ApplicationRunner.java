package org.example.application;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.parser.OrderParser;
import org.example.parser.PaymentMethodParser;
import org.example.planner.PaymentPlanner;
import org.example.breakdown.OrderPaymentBreakdown;
import org.example.printer.PaymentSummaryPrinter;

import java.util.List;

public class ApplicationRunner {
    private final PaymentPlanner planner;

    public ApplicationRunner(PaymentPlanner planner) {
        this.planner = planner;
    }

    public void run(String ordersPath, String methodsPath) {
        List<Order> orders = OrderParser.parse(ordersPath);
        List<PaymentMethod> methods = PaymentMethodParser.parse(methodsPath);
        try {
            List<OrderPaymentBreakdown> result = planner.plan(orders, methods);
            PaymentSummaryPrinter.print(result);
        } catch (IllegalStateException e) {
            System.err.println("Payment planning failed - no valid solution could be found: " + e.getMessage());
            System.exit(1);
        }
    }
}