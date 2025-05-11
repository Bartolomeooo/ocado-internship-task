package org.example.config;

import java.math.BigDecimal;

public class PlannerConfig {
    private final String pointsMethodId;
    private final BigDecimal minPointsRatioForDiscount;
    private final BigDecimal discountPercentage;

    public PlannerConfig(String pointsMethodId, BigDecimal minPointsRatioForDiscount, BigDecimal discountPercentage) {
        this.pointsMethodId = pointsMethodId;
        this.minPointsRatioForDiscount = minPointsRatioForDiscount;
        this.discountPercentage = discountPercentage;
    }

    public String getPointsMethodId() {
        return pointsMethodId;
    }

    public BigDecimal getMinPointsRatioForDiscount() {
        return minPointsRatioForDiscount;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
}