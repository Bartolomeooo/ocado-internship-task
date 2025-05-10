package org.example.discount;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.Map;

public class PointsFullPaymentPolicy implements DiscountPolicy {
    private final String pointsId;
    private final BigDecimal discountPercentage;

    public PointsFullPaymentPolicy(String pointsId, BigDecimal discountPercentage) {
        this.pointsId = pointsId;
        this.discountPercentage = discountPercentage;
    }

    @Override
    public BigDecimal calculateDiscount(Order order, Map<String, BigDecimal> paidByMethodId) {
        BigDecimal paidAmount = paidByMethodId.getOrDefault(pointsId, BigDecimal.ZERO);
        if (paidAmount.compareTo(order.getValue()) == 0) {
            return order.getValue().multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}