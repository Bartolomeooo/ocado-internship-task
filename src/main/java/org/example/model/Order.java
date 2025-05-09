package org.example.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final String id;
    private BigDecimal value;
    private List<String> promotions;

    Order(String id, BigDecimal value, List<String> promotions) {
        this.id = id;
        this.value = value;
        this.promotions = promotions;
    }

    public BigDecimal getValue() {
        return value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public String getId() {
        return id;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}