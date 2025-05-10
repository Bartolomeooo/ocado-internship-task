package discount;

import org.example.discount.CardFullPaymentPolicy;
import org.example.discount.DiscountPolicy;
import org.example.model.Order;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CardFullPaymentPolicyTest {
    @Test
    public void shouldApplyCardDiscountWhenFullyPaidAndPromotionExists() {
        Order order = new Order("ORDER1", new BigDecimal("100.00"), List.of("mZysk", "BosBankrut"));
        Map<String, BigDecimal> paidByMethodId = Map.of("mZysk", new BigDecimal("100.00"));
        DiscountPolicy policy = new CardFullPaymentPolicy("mZysk", new BigDecimal("10"));

        BigDecimal discount = policy.calculateDiscount(order, paidByMethodId);
        assertEquals(new BigDecimal("10.00"), discount);
    }

    @Test
    public void shouldNotApplyDiscountIfPromotionIsMissing() {
        Order order = new Order("ORDER1", new BigDecimal("100.00"), List.of("BosBankrut"));
        Map<String, BigDecimal> paidByMethodId = Map.of("mZysk", new BigDecimal("100.00"));
        DiscountPolicy policy = new CardFullPaymentPolicy("mZysk", new BigDecimal("10"));

        BigDecimal discount = policy.calculateDiscount(order, paidByMethodId);
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    public void shouldNotApplyDiscountIfPaymentIsPartial() {
        Order order = new Order("ORDER1", new BigDecimal("100.00"), List.of("mZysk"));
        Map<String, BigDecimal> paidByMethodId = Map.of("mZysk", new BigDecimal("80.00"));
        DiscountPolicy policy = new CardFullPaymentPolicy("mZysk", new BigDecimal("10"));

        BigDecimal discount = policy.calculateDiscount(order, paidByMethodId);
        assertEquals(BigDecimal.ZERO, discount);
    }
}