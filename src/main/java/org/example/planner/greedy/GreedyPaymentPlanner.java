package org.example.planner.greedy;

import org.example.config.PlannerConfig;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.breakdown.OrderPaymentBreakdown;
import org.example.planner.PaymentPlanner;

import java.util.*;

/**
 * GreedyPaymentPlanner assigns payment methods to orders in two phases:
 *
 * Phase 1 (Greedy Allocation):
 * - It uses a priority queue of payment methods sorted by (discount * limit)
 * - It iteratively assigns the best method to the highest-value eligible order
 *   trying to pay it in full (after applying the method's discount)
 *
 * Phase 2 (Handling Unpaid Orders):
 * - Unpaid orders are first sorted in descending order of value, so that larger
 *   orders are handled first while more resources are available
 * - For each order, it attempts to use the "points" method to cover at least
 *   a minimum required percentage and unlock a discount
 * - If the order qualifies, the discount is applied and the remaining amount is paid
 * - The remaining amount is paid using a single traditional payment method
 *   with the highest available limit — only one such method is allowed per order
 *
 * This approach does not guarantee global optimality but provides a reasonable
 * heuristic that maximizes discounts locally
 */
public class GreedyPaymentPlanner implements PaymentPlanner {
    private final PlannerConfig config;

    public GreedyPaymentPlanner(PlannerConfig config) {
        this.config = config;
    }

    @Override
    public List<OrderPaymentBreakdown> plan(List<Order> orders, List<PaymentMethod> paymentMethods) {
        GreedyPaymentPlannerContext context = new GreedyPaymentPlannerContext(orders, paymentMethods);
        new GreedyAllocator(config).allocatePayments(context, paymentMethods);
        new UnpaidOrdersHandler(config).handleUnpaidOrders(context, paymentMethods);
        return new ArrayList<>(context.getAllOrderPaymentBreakdowns());
    }
}