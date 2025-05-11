# Ocado Internship Task

## Overview

This project solves the problem of assigning payment methods to a list of customer orders in a way that **maximizes applied discounts** under given constraints (such as payment method limits and allowed discount policies). It simulates how a system might select payment options to reduce costs.

---

### Build

This project uses **Java 17** and **Maven**. To build a fat JAR with dependencies:

```bash
mvn clean package
```

### Run

```bash 
java -jar target/ocado-internship-task-1.0-SNAPSHOT-jar-with-dependencies.jar <orders.json> <methods.json>
```
---

### Algorithmic approach

The solution uses a two-phase greedy algorithm to assign payment methods to orders:

#### Phase 1 – Greedy Allocation
- It uses a priority queue of payment methods sorted by `discount * remaining limit`
- It iteratively assigns the best method to the highest-value eligible order trying to pay it in full (after applying the method's discount)


#### Phase 2 – Handling unpaid orders

If any orders remain unpaid after phase 1:

- Unpaid orders are first sorted in descending order of value, so that larger orders are handled first while more resources are available
- For each order, it attempts to use the "points" method to cover at least a minimum required percentage and unlock a discount
- If the order qualifies, the discount is applied and the remaining amount is paid 
- The remaining amount is paid using a single traditional payment method with the highest available limit - only one such method is allowed per order

This approach does not guarantee global optimality but provides a reasonable heuristic that maximizes discounts locally
