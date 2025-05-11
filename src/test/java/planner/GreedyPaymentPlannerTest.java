package planner;

import org.example.breakdown.OrderPaymentBreakdown;
import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.planner.greedy.GreedyPaymentPlanner;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class GreedyPaymentPlannerTest {
    @Test
    public void shouldCorrectlyAllocateMultipleOrdersInFullFlow() {
        // Given
        Order order1 = new Order("ORDER1", BigDecimal.valueOf(100), List.of("CARD_A", "CARD_B"));
        Order order2 = new Order("ORDER2", BigDecimal.valueOf(50), List.of("CARD_A"));
        Order order3 = new Order("ORDER3", BigDecimal.valueOf(80), List.of()); // no promotions => only eligible for points

        PaymentMethod points = new PaymentMethod("POINTS", BigDecimal.valueOf(15), BigDecimal.valueOf(50)); // 15% discount, 50 of limit
        PaymentMethod cardA = new PaymentMethod("CARD_A", BigDecimal.valueOf(50), BigDecimal.valueOf(150)); // 50% discount, 150 of limit
        PaymentMethod cardB = new PaymentMethod("CARD_B", BigDecimal.valueOf(20), BigDecimal.valueOf(100)); // 20% discount, 100 of limit

        PlannerConfig config = new PlannerConfig("POINTS", BigDecimal.valueOf(0.2), BigDecimal.valueOf(10)); // 20% points required, 10% discount applied

        GreedyPaymentPlanner planner = new GreedyPaymentPlanner(config);

        // When
        List<OrderPaymentBreakdown> breakdowns = planner.plan(
                List.of(order1, order2, order3),
                List.of(points, cardA, cardB)
        );

        // Then
        assertEquals(3, breakdowns.size());

        OrderPaymentBreakdown b1 = breakdowns.get(0);
        OrderPaymentBreakdown b2 = breakdowns.get(1);
        OrderPaymentBreakdown b3 = breakdowns.get(2);

        // ORDER1{CARD_A=50.0}
        assertEquals(1, b1.getAmountPaidByMethodId().size());
        assertEquals(0, b1.getAmountPaidByMethodId().get("CARD_A").compareTo(BigDecimal.valueOf(50)));

        // ORDER2{CARD_A=25.0}
        assertEquals(1, b2.getAmountPaidByMethodId().size());
        assertEquals(0, b2.getAmountPaidByMethodId().get("CARD_A").compareTo(BigDecimal.valueOf(25)));

        // ORDER3{POINTS=50, CARD_B=22.0}
        assertEquals(2, b3.getAmountPaidByMethodId().size());
        assertEquals(0, b3.getAmountPaidByMethodId().get("POINTS").compareTo(BigDecimal.valueOf(50)));
        assertEquals(0, b3.getAmountPaidByMethodId().get("CARD_B").compareTo(BigDecimal.valueOf(22)));
    }
}