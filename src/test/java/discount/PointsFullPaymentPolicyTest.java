package discount;

import org.example.discount.DiscountPolicy;
import org.example.discount.PointsFullPaymentPolicy;
import org.example.model.Order;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PointsFullPaymentPolicyTest {
    @Test
    public void shouldApplyDiscountWhenPaidFullyWithPoints() {
        Order order = new Order("ORDER1", new BigDecimal("200.00"), null);
        Map<String, BigDecimal> paid = Map.of("PUNKTY", new BigDecimal("200.00"));
        DiscountPolicy policy = new PointsFullPaymentPolicy("PUNKTY", new BigDecimal("15"));

        BigDecimal discount = policy.calculateDiscount(order, paid);
        assertEquals(new BigDecimal("30.00"), discount);
    }

    @Test
    public void shouldNotApplyDiscountWhenPaidPartiallyWithPoints() {
        Order order = new Order("ORDER1", new BigDecimal("200.00"), null);
        Map<String, BigDecimal> paid = Map.of("PUNKTY", new BigDecimal("100.00"));
        DiscountPolicy policy = new PointsFullPaymentPolicy("PUNKTY", new BigDecimal("15"));

        BigDecimal discount = policy.calculateDiscount(order, paid);
        assertEquals(BigDecimal.ZERO, discount);
    }
}