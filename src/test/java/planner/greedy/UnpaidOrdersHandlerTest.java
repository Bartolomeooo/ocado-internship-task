package planner.greedy;

import org.example.breakdown.OrderPaymentBreakdown;
import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.planner.greedy.GreedyPaymentPlannerContext;
import org.example.planner.greedy.UnpaidOrdersHandler;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class UnpaidOrdersHandlerTest {
    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenRemainingCannotBeCovered() {
        // Given
        Order order = new Order("ORDER4", BigDecimal.valueOf(100), List.of("CARD"));
        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.ZERO, BigDecimal.ZERO);
        PaymentMethod card = new PaymentMethod("CARD", BigDecimal.ZERO, BigDecimal.valueOf(20));

        // When
        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.1), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(points, card));
        UnpaidOrdersHandler handler = new UnpaidOrdersHandler(config);

        handler.handleUnpaidOrders(context, List.of(points, card));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfOnlyCombinationOfMethodsCanCoverRemaining() {
        // Given
        Order order = new Order("ORDER_FAIL", BigDecimal.valueOf(100), List.of("A", "B"));
        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.ZERO, BigDecimal.valueOf(20));
        PaymentMethod methodA = new PaymentMethod("A", BigDecimal.ZERO, BigDecimal.valueOf(60));
        PaymentMethod methodB = new PaymentMethod("B", BigDecimal.ZERO, BigDecimal.valueOf(60));

        // When
        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.2), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(points, methodA, methodB));
        UnpaidOrdersHandler handler = new UnpaidOrdersHandler(config);

        handler.handleUnpaidOrders(context, List.of(points, methodA, methodB));
    }

    @Test
    public void shouldUsePointsAndOneTraditionalMethodToPayOrder() {
        // Given
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of("CARD"));
        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.ZERO, BigDecimal.valueOf(20));
        PaymentMethod card = new PaymentMethod("CARD", BigDecimal.ZERO, BigDecimal.valueOf(90));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.2), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(points, card));

        // When
        UnpaidOrdersHandler handler = new UnpaidOrdersHandler(config);
        handler.handleUnpaidOrders(context, List.of(points, card));
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown("ORDER1");

        // Then
        assertEquals(0, breakdown.getAmountPaidByMethodId().get("POINTS").compareTo(BigDecimal.valueOf(20)));
        assertEquals(0, breakdown.getAmountPaidByMethodId().get("CARD").compareTo(BigDecimal.valueOf(70)));
    }

    @Test
    public void shouldNotUsePointsIfBelowThreshold() {
        // Given
        Order order = new Order("ORDER1", BigDecimal.valueOf(100), List.of("CARD"));
        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.valueOf(15), BigDecimal.valueOf(10)); // too little for 20%
        PaymentMethod card = new PaymentMethod("CARD", BigDecimal.ZERO, BigDecimal.valueOf(100));

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.2), BigDecimal.valueOf(10));
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(List.of(order), List.of(points, card));

        // When
        UnpaidOrdersHandler handler = new UnpaidOrdersHandler(config);
        handler.handleUnpaidOrders(context, List.of(points, card));
        OrderPaymentBreakdown breakdown = context.getOrderPaymentBreakdown("ORDER1");

        // Then
        assertFalse(breakdown.getAmountPaidByMethodId().containsKey("POINTS"));
        assertEquals(0, breakdown.getAmountPaidByMethodId().get("CARD").compareTo(BigDecimal.valueOf(100)));
    }
}