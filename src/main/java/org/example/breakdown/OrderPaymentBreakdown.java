package org.example.breakdown;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrderPaymentBreakdown {
    private final Order order;
    private final Map<String, BigDecimal> amountPaidByMethodId = new HashMap<>();

    public OrderPaymentBreakdown(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
    public Map<String, BigDecimal> getAmountPaidByMethodId() {
        return amountPaidByMethodId;
    }
}