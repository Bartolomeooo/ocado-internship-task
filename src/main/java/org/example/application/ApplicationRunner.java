package org.example.application;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.parser.OrderParser;
import org.example.parser.PaymentMethodParser;
import org.example.planner.PaymentPlanner;
import org.example.breakdown.OrderPaymentBreakdown;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationRunner {
    private final PaymentPlanner planner;

    public ApplicationRunner(PaymentPlanner planner) {
        this.planner = planner;
    }

    public void run(String ordersPath, String methodsPath) {
        List<Order> orders = OrderParser.parse(ordersPath);
        List<PaymentMethod> methods = PaymentMethodParser.parse(methodsPath);
        List<OrderPaymentBreakdown> result = planner.plan(orders, methods);

        Map<String, BigDecimal> totalPerMethod = new HashMap<>();

        for (OrderPaymentBreakdown breakdown : result) {
            for (Map.Entry<String, BigDecimal> entry : breakdown.getAmountPaidByMethodId().entrySet()) {
                String method = entry.getKey();
                BigDecimal paid = entry.getValue();
                totalPerMethod.merge(method, paid, BigDecimal::add);
            }
        }

        totalPerMethod.forEach((method, total) ->
                System.out.printf("%s: %.2f%n", method, total));
    }
}