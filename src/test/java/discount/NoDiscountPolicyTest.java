package discount;

import org.example.discount.DiscountPolicy;
import org.example.discount.NoDiscountPolicy;
import org.example.model.Order;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class NoDiscountPolicyTest {
    @Test
    public void shouldAlwaysReturnZero() {
        Order order = new Order("ORDER1", new BigDecimal("115.57"), null);
        Map<String, BigDecimal> paidByMethodId = Map.of("mZysk", new BigDecimal("115.57"));
        DiscountPolicy policy = new NoDiscountPolicy();

        BigDecimal discount = policy.calculateDiscount(order, paidByMethodId);
        assertEquals(BigDecimal.ZERO, discount);
    }
}