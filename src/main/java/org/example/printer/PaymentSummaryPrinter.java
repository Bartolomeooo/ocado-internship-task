package org.example.printer;

import org.example.breakdown.OrderPaymentBreakdown;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentSummaryPrinter {
    public static void print(List<OrderPaymentBreakdown> OrderPaymentBreakdowns) {
        Map<String, BigDecimal> totalPerMethod = new HashMap<>();

        for (OrderPaymentBreakdown OrderPaymentBreakdown : OrderPaymentBreakdowns) {
            for (Map.Entry<String, BigDecimal> entry : OrderPaymentBreakdown.getAmountPaidByMethodId().entrySet()) {
                String methodId = entry.getKey();
                BigDecimal paid = entry.getValue();
                totalPerMethod.merge(methodId, paid, BigDecimal::add);
            }
        }

        totalPerMethod.forEach((methodId, amount) ->
                System.out.printf("%s: %.2f%n", methodId, amount)
        );
    }
}