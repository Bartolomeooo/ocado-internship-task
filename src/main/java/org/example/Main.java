package org.example;

import org.example.application.ApplicationRunner;
import org.example.config.PlannerConfig;
import org.example.planner.PaymentPlanner;
import org.example.planner.greedy.GreedyPaymentPlanner;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar app.jar <orders.json> <paymentmethods.json>");
            return;
        }

        PlannerConfig config = new PlannerConfig("PUNKTY", BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.1));
        PaymentPlanner planner = new GreedyPaymentPlanner(config);
        ApplicationRunner runner = new ApplicationRunner(planner);

        runner.run(args[0], args[1]);
    }
}