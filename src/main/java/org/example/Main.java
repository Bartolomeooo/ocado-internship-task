package org.example;

import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.parser.OrderParser;
import org.example.parser.PaymentMethodParser;
import org.example.planner.GreedyPaymentPlanner;
import org.example.planner.PaymentPlanner;
import org.example.breakdown.OrderPaymentBreakdown;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar app.jar <orders.json> <paymentmethods.json>");
            return;
        }

        String ordersPath = args[0];
        String methodsPath = args[1];
        String pointsId = "PUNKTY";

        List<Order> orders = OrderParser.parse(ordersPath);
        List<PaymentMethod> methods = PaymentMethodParser.parse(methodsPath);

        PlannerConfig config = new PlannerConfig("PUNKTY", BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.1));
        PaymentPlanner planner = new GreedyPaymentPlanner(config);
        List<OrderPaymentBreakdown> breakdowns = planner.plan(orders, methods);

        Map<String, BigDecimal> totalPerMethod = new HashMap<>();
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (OrderPaymentBreakdown breakdown : breakdowns) {
            Order order = breakdown.getOrder();
            System.out.printf("Order %s (%.2f)%n", order.getId(), order.getValue());

            BigDecimal paidTotal = BigDecimal.ZERO;
            for (Map.Entry<String, BigDecimal> entry : breakdown.getAmountPaidByMethodId().entrySet()) {
                String method = entry.getKey();
                BigDecimal paid = entry.getValue();
                paidTotal = paidTotal.add(paid);
                totalPerMethod.merge(method, paid, BigDecimal::add);

                System.out.printf("%s: %.2f%n", method, paid);
            }

            BigDecimal discount = order.getValue().subtract(paidTotal);
            totalDiscount = totalDiscount.add(discount);
            if (discount.compareTo(BigDecimal.ZERO) > 0) {
                System.out.printf("Discount: %.2f%n", discount);
            }

            System.out.println();
        }

        System.out.println("Total spent per method");
        totalPerMethod.forEach((method, amount) ->
                System.out.printf("%s: %.2f%n", method, amount)
        );

        System.out.printf("%nTotal discount: %.2f%n", totalDiscount);
    }
}
