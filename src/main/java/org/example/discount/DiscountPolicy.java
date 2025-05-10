package org.example.discount;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.Map;

public interface DiscountPolicy {
    BigDecimal calculateDiscount(Order order, Map<String, BigDecimal> paidByMethodId);
}