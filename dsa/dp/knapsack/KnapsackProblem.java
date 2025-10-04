package dp.knapsack;

/**
 * Classic 0/1 Knapsack Problem
 * https://en.wikipedia.org/wiki/Knapsack_problem
 *
 * Description:
 * Given weights and values of n items, put these items in a knapsack of
 * capacity W
 * to get the maximum total value in the knapsack.
 *
 * Constraints:
 * - You cannot break an item (0/1 knapsack)
 * - Each item can be picked at most once
 *
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple, Goldman Sachs
 * Difficulty: Medium
 */
public class KnapsackProblem {

    // Approach 1: 2D Dynamic Programming - O(n*W) time, O(n*W) space
    public int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;

        // dp[i][w] = maximum value using first i items with capacity w
        int[][] dp = new int[n + 1][capacity + 1];

        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                // Don't include current item
                dp[i][w] = dp[i - 1][w];

                // Include current item if it fits
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i][w],
                            dp[i - 1][w - weights[i - 1]] + values[i - 1]);
                }
            }
        }

        return dp[n][capacity];
    }

    // Approach 2: Space Optimized DP - O(n*W) time, O(W) space
    public int knapsackOptimized(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[] dp = new int[capacity + 1];

        for (int i = 0; i < n; i++) {
            // Traverse backwards to avoid using updated values
            for (int w = capacity; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
            }
        }

        return dp[capacity];
    }

    // Approach 3: Recursive with Memoization - O(n*W) time, O(n*W) space
    public int knapsackMemo(int[] weights, int[] values, int capacity) {
        Integer[][] memo = new Integer[weights.length][capacity + 1];
        return knapsackRecursive(weights, values, capacity, 0, memo);
    }

    private int knapsackRecursive(int[] weights, int[] values, int capacity,
            int index, Integer[][] memo) {
        if (index >= weights.length || capacity == 0) {
            return 0;
        }

        if (memo[index][capacity] != null) {
            return memo[index][capacity];
        }

        // Don't include current item
        int exclude = knapsackRecursive(weights, values, capacity, index + 1, memo);

        // Include current item if it fits
        int include = 0;
        if (weights[index] <= capacity) {
            include = values[index] +
                    knapsackRecursive(weights, values, capacity - weights[index], index + 1, memo);
        }

        memo[index][capacity] = Math.max(include, exclude);
        return memo[index][capacity];
    }

    // Approach 4: Find which items are selected - O(n*W) time, O(n*W) space
    public java.util.List<Integer> knapsackWithItems(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        int[][] dp = new int[n + 1][capacity + 1];

        // Build DP table
        for (int i = 1; i <= n; i++) {
            for (int w = 1; w <= capacity; w++) {
                dp[i][w] = dp[i - 1][w];
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(dp[i][w],
                            dp[i - 1][w - weights[i - 1]] + values[i - 1]);
                }
            }
        }

        // Backtrack to find selected items
        java.util.List<Integer> selectedItems = new java.util.ArrayList<>();
        int w = capacity;

        for (int i = n; i > 0 && w > 0; i--) {
            if (dp[i][w] != dp[i - 1][w]) {
                selectedItems.add(i - 1); // Add item index
                w -= weights[i - 1];
            }
        }

        java.util.Collections.reverse(selectedItems);
        return selectedItems;
    }

    // Approach 5: Fractional Knapsack (Greedy) - O(n log n) time, O(n) space
    public double fractionalKnapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;

        // Create items with value-to-weight ratio
        java.util.List<Item> items = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++) {
            items.add(new Item(weights[i], values[i], i));
        }

        // Sort by value-to-weight ratio in descending order
        items.sort((a, b) -> Double.compare(b.ratio, a.ratio));

        double totalValue = 0.0;
        int remainingCapacity = capacity;

        for (Item item : items) {
            if (remainingCapacity == 0)
                break;

            if (item.weight <= remainingCapacity) {
                // Take the whole item
                totalValue += item.value;
                remainingCapacity -= item.weight;
            } else {
                // Take fraction of the item
                totalValue += item.value * ((double) remainingCapacity / item.weight);
                remainingCapacity = 0;
            }
        }

        return totalValue;
    }

    // Helper class for fractional knapsack
    private static class Item {
        int weight, value, index;
        double ratio;

        Item(int weight, int value, int index) {
            this.weight = weight;
            this.value = value;
            this.index = index;
            this.ratio = (double) value / weight;
        }
    }

    public static void main(String[] args) {
        KnapsackProblem solution = new KnapsackProblem();

        System.out.println("=== 0/1 Knapsack Problem Test Cases ===");

        // Test case 1: Basic example
        int[] weights1 = { 10, 20, 30 };
        int[] values1 = { 60, 100, 120 };
        int capacity1 = 50;

        System.out.println("Weights: [10,20,30], Values: [60,100,120], Capacity: 50");
        System.out.println("2D DP: " + solution.knapsack(weights1, values1, capacity1)); // Expected: 220
        System.out.println("Optimized: " + solution.knapsackOptimized(weights1, values1, capacity1)); // Expected: 220
        System.out.println("Memoization: " + solution.knapsackMemo(weights1, values1, capacity1)); // Expected: 220
        System.out.println("Selected items: " + solution.knapsackWithItems(weights1, values1, capacity1));
        System.out.println("Fractional: " + solution.fractionalKnapsack(weights1, values1, capacity1)); // Expected:
                                                                                                        // 240.0

        // Test case 2: Classic example
        int[] weights2 = { 1, 3, 4, 5 };
        int[] values2 = { 1, 4, 5, 7 };
        int capacity2 = 7;

        System.out.println("\nWeights: [1,3,4,5], Values: [1,4,5,7], Capacity: 7");
        System.out.println("2D DP: " + solution.knapsack(weights2, values2, capacity2)); // Expected: 9
        System.out.println("Optimized: " + solution.knapsackOptimized(weights2, values2, capacity2)); // Expected: 9
        System.out.println("Selected items: " + solution.knapsackWithItems(weights2, values2, capacity2));

        // Test case 3: All items don't fit
        int[] weights3 = { 10, 20, 30 };
        int[] values3 = { 60, 100, 120 };
        int capacity3 = 5;

        System.out.println("\nWeights: [10,20,30], Values: [60,100,120], Capacity: 5");
        System.out.println("2D DP: " + solution.knapsack(weights3, values3, capacity3)); // Expected: 0
        System.out.println("Fractional: " + solution.fractionalKnapsack(weights3, values3, capacity3)); // Expected:
                                                                                                        // 30.0

        // Test case 4: Single item
        int[] weights4 = { 10 };
        int[] values4 = { 10 };
        int capacity4 = 15;

        System.out.println("\nWeights: [10], Values: [10], Capacity: 15");
        System.out.println("2D DP: " + solution.knapsack(weights4, values4, capacity4)); // Expected: 10

        // Test case 5: Zero capacity
        System.out.println("\nCapacity: 0");
        System.out.println("2D DP: " + solution.knapsack(weights1, values1, 0)); // Expected: 0

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        KnapsackProblem solution = new KnapsackProblem();

        // Create larger test case
        int n = 100;
        int[] weights = new int[n];
        int[] values = new int[n];

        for (int i = 0; i < n; i++) {
            weights[i] = (int) (Math.random() * 20) + 1;
            values[i] = (int) (Math.random() * 50) + 1;
        }

        int capacity = 200;
        long startTime, endTime;

        // Test 2D DP approach
        startTime = System.nanoTime();
        int result1 = solution.knapsack(weights, values, capacity);
        endTime = System.nanoTime();
        System.out.println("2D DP: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result2 = solution.knapsackOptimized(weights, values, capacity);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test memoization approach
        startTime = System.nanoTime();
        int result3 = solution.knapsackMemo(weights, values, capacity);
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test fractional approach
        startTime = System.nanoTime();
        double result4 = solution.fractionalKnapsack(weights, values, capacity);
        endTime = System.nanoTime();
        System.out.println("Fractional: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
