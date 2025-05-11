package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class PaymentMethod {
    private final String id;
    private BigDecimal discount; // Percentage
    private BigDecimal limit;

    @JsonCreator
    public PaymentMethod(@JsonProperty("id") String id,
                         @JsonProperty("discount") BigDecimal discount,
                         @JsonProperty("limit") BigDecimal limit) {
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
}