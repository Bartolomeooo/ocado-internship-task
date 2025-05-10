package org.example.discount;

import org.example.model.Order;

import java.math.BigDecimal;
import java.util.Map;

public class PointsPartialPaymentPolicy implements DiscountPolicy {
    private final String pointsId;
    private static final BigDecimal MIN_REQUIRED_POINTS_RATIO = BigDecimal.valueOf(0.10);
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.TEN;

    public PointsPartialPaymentPolicy(String pointsId) {
        this.pointsId = pointsId;
    }

    @Override
    public BigDecimal calculateDiscount(Order order, Map<String, BigDecimal> paidByMethodId) {
        BigDecimal pointsAmount = paidByMethodId.getOrDefault(pointsId, BigDecimal.ZERO);
        BigDecimal minRequired = order.getValue().multiply(MIN_REQUIRED_POINTS_RATIO);

        if (pointsAmount.compareTo(minRequired) >= 0) {
            return order.getValue().multiply(DISCOUNT_PERCENTAGE).divide(BigDecimal.valueOf(100));
        }
        return BigDecimal.ZERO;
    }
}