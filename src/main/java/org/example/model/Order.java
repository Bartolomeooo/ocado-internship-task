package org.example.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private String id;
    private BigDecimal value;
    private List<String> promotions;

    public BigDecimal getValue() {
        return value;
    }

    public List<String> getPromotions() {
        return promotions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPromotions(List<String> promotions) {
        this.promotions = promotions;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}