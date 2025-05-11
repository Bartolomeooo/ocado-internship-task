package planner.greedy;

import org.example.breakdown.OrderPaymentBreakdown;
import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.planner.greedy.GreedyAllocator;
import org.example.planner.greedy.GreedyPaymentPlannerContext;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class GreedyAllocatorTest {
    @Test
    public void shouldPreferMethodWithHigherDiscountTimesLimit() {
        // Given
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of("A", "B", "C", "D"));

        PaymentMethod methodA = new PaymentMethod("A", BigDecimal.valueOf(50), BigDecimal.valueOf(200));
        PaymentMethod methodB = new PaymentMethod("B", BigDecimal.valueOf(80), BigDecimal.valueOf(200));
        PaymentMethod methodC = new PaymentMethod("C", BigDecimal.valueOf(90), BigDecimal.valueOf(200));
        PaymentMethod methodD = new PaymentMethod("D", BigDecimal.valueOf(30), BigDecimal.valueOf(200));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.1), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(methodA, methodB, methodC, methodD));

        // When
        new GreedyAllocator(config).allocatePayments(context, List.of(methodA, methodB, methodC, methodD));
        OrderPaymentBreakdown orderPaymentBreakdown = context.getOrderPaymentBreakdown("ORDER1");

        // Then
        assertTrue(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("C"));
        assertFalse(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("A"));
        assertFalse(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("B"));
        assertFalse(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("D"));
    }

    @Test
    public void shouldSkipMethodThatCannotAffordAnyOrderEvenIfPriorityIsHighest() {
        // Given
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of("A", "B"));

        PaymentMethod methodA = new PaymentMethod("A", BigDecimal.valueOf(99), BigDecimal.valueOf(10));
        PaymentMethod methodB = new PaymentMethod("B", BigDecimal.valueOf(50), BigDecimal.valueOf(200));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.1), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(methodA, methodB));

        // When
        new GreedyAllocator(config).allocatePayments(context, List.of(methodA, methodB));
        OrderPaymentBreakdown orderPaymentBreakdown = context.getOrderPaymentBreakdown("ORDER1");

        // Then
        assertFalse(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("A"));
        assertTrue(orderPaymentBreakdown.getAmountPaidByMethodId().containsKey("B"));
    }

    @Test
    public void shouldAllowUsingPointsEvenIfNotInPromotionsList() {
        // Given
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of());
        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.valueOf(10), BigDecimal.valueOf(100));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.1), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(points));

        // When
        new GreedyAllocator(config).allocatePayments(context, List.of(points));
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown("ORDER1");

        // Then
        assertTrue(breakdown.getAmountPaidByMethodId().containsKey("POINTS"));
    }

    @Test
    public void shouldAllocateBestMethodToMostValuableEligibleOrder() {
        // Given
        Order cheap = new Order("ORDER1", BigDecimal.valueOf(80), List.of("A"));
        Order expensive = new Order("ORDER2", BigDecimal.valueOf(100), List.of("A"));

        PaymentMethod methodA = new PaymentMethod("A", BigDecimal.valueOf(20), BigDecimal.valueOf(120));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.1), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(cheap, expensive), List.of(methodA));

        // When
        new GreedyAllocator(config).allocatePayments(context, List.of(methodA));

        // Then
        OrderPaymentBreakdown breakdown1 = context.getOrderPaymentBreakdown("ORDER1");
        OrderPaymentBreakdown breakdown2 = context.getOrderPaymentBreakdown("ORDER2");

        assertFalse(breakdown1.getAmountPaidByMethodId().containsKey("A"));
        assertTrue(breakdown2.getAmountPaidByMethodId().containsKey("A"));
    }
}