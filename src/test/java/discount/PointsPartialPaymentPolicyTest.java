package discount;

import org.example.discount.DiscountPolicy;
import org.example.discount.PointsPartialPaymentPolicy;
import org.example.model.Order;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PointsPartialPaymentPolicyTest {
    @Test
    public void shouldApplyDiscountWhenAtLeastTenPercentPaidWithPoints() {
        Order order = new Order("ORDER1", new BigDecimal("100.00"), null);
        Map<String, BigDecimal> paid = Map.of("PUNKTY", new BigDecimal("10.00"));
        DiscountPolicy policy = new PointsPartialPaymentPolicy("PUNKTY");

        BigDecimal discount = policy.calculateDiscount(order, paid);
        assertEquals(new BigDecimal("10.00"), discount);
    }

    @Test
    public void shouldNotApplyDiscountWhenLessThanTenPercentPaidWithPoints() {
        Order order = new Order("ORDER1", new BigDecimal("100.00"), null);
        Map<String, BigDecimal> paid = Map.of("PUNKTY", new BigDecimal("5.00"));
        DiscountPolicy policy = new PointsPartialPaymentPolicy("PUNKTY");

        BigDecimal discount = policy.calculateDiscount(order, paid);
        assertEquals(BigDecimal.ZERO, discount);
    }
}