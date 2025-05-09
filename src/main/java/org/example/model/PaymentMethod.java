package org.example.model;

import java.math.BigDecimal;

public class PaymentMethod {
    private final String id;
    private BigDecimal discount;
    private BigDecimal limit;

    PaymentMethod(String id, BigDecimal discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }
}