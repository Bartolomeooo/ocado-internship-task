package org.example.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private final String id;
    private BigDecimal value;
    private List<String> promotions;

    @JsonCreator
    public Order(@JsonProperty("id") String id,
          @JsonProperty("value") BigDecimal value,
          @JsonProperty("promotions") List<String> promotions) {
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
}