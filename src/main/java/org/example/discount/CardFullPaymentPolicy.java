package org.example.discount;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CardFullPaymentPolicy implements DiscountPolicy {
    private final String methodId;
    private final BigDecimal discountPercentage;

    public CardFullPaymentPolicy(String methodId, BigDecimal discountPercentage) {
        this.methodId = methodId;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculateDiscount(Order order, Map<String, BigDecimal> paidByMethodId) {
        BigDecimal paidAmount = paidByMethodId.getOrDefault(methodId, BigDecimal.ZERO);
        List<String> promotions = order.getPromotions();
        boolean eligible = promotions != null && promotions.contains(methodId);

        if (paidAmount.compareTo(order.getValue()) == 0 && eligible) {
            return order.getValue().multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }

        return BigDecimal.ZERO;
    }
}