package org.example.model;

import java.math.BigDecimal;

public class PaymentMethod {
    String id;
    BigDecimal discount;
    BigDecimal limit;

    public String getId() {
        return id;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }
}