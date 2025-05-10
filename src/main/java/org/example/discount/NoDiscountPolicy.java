package org.example.discount;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.Map;

public class NoDiscountPolicy implements DiscountPolicy {
    @Override
    public BigDecimal calculateDiscount(Order order, Map<String, BigDecimal> paidByMethodId) {
        return BigDecimal.ZERO;
    }
}