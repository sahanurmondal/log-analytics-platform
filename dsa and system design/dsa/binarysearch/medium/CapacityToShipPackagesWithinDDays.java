package binarysearch.medium;

/**
 * LeetCode 1011: Capacity To Ship Packages Within D Days
 * https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/
 *
 * Description:
 * A conveyor belt has packages that must be shipped from one port to another
 * within days days.
 * The ith package on the conveyor belt has a weight of weights[i]. Each day, we
 * load the ship with packages
 * on the conveyor belt (in the order given by weights). We may not load more
 * weight than the maximum weight capacity of the ship.
 * Return the least weight capacity of the ship that will result in all the
 * packages on the conveyor belt being shipped within days days.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Uber,
 * Airbnb
 * Difficulty: Medium
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= days <= weights.length <= 5 * 10^4
 * - 1 <= weights[i] <= 500
 *
 * Follow-ups:
 * - What if we can rearrange the packages?
 * - How would you handle if some packages cannot be split?
 * - Can you solve this with greedy approach?
 */
public class CapacityToShipPackagesWithinDDays {

    // Binary Search on Answer - O(n * log(sum)) time, O(1) space
    public int shipWithinDays(int[] weights, int days) {
        int left = getMaxWeight(weights); // Minimum capacity needed
        int right = getTotalWeight(weights); // Maximum capacity needed

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canShip(weights, days, mid)) {
                right = mid; // Try smaller capacity
            } else {
                left = mid + 1; // Need larger capacity
            }
        }

        return left;
    }

    private boolean canShip(int[] weights, int days, int capacity) {
        int daysNeeded = 1;
        int currentWeight = 0;

        for (int weight : weights) {
            if (currentWeight + weight > capacity) {
                daysNeeded++;
                currentWeight = weight;

                if (daysNeeded > days) {
                    return false; // Early termination
                }
            } else {
                currentWeight += weight;
            }
        }

        return daysNeeded <= days;
    }

    private int getMaxWeight(int[] weights) {
        int max = weights[0];
        for (int weight : weights) {
            max = Math.max(max, weight);
        }
        return max;
    }

    private int getTotalWeight(int[] weights) {
        int total = 0;
        for (int weight : weights) {
            total += weight;
        }
        return total;
    }

    // Alternative implementation with explicit result tracking
    public int shipWithinDaysAlt(int[] weights, int days) {
        int left = getMaxWeight(weights);
        int right = getTotalWeight(weights);
        int result = right;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (canShip(weights, days, mid)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Get the actual shipping schedule
    public int[][] getShippingSchedule(int[] weights, int days) {
        int capacity = shipWithinDays(weights, days);
        java.util.List<java.util.List<Integer>> schedule = new java.util.ArrayList<>();

        java.util.List<Integer> currentDay = new java.util.ArrayList<>();
        int currentWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            if (currentWeight + weights[i] > capacity) {
                if (!currentDay.isEmpty()) {
                    schedule.add(new java.util.ArrayList<>(currentDay));
                }
                currentDay.clear();
                currentWeight = 0;
            }

            currentDay.add(i); // Add package index
            currentWeight += weights[i];
        }

        if (!currentDay.isEmpty()) {
            schedule.add(currentDay);
        }

        // Convert to 2D array
        int[][] result = new int[schedule.size()][];
        for (int i = 0; i < schedule.size(); i++) {
            result[i] = schedule.get(i).stream().mapToInt(Integer::intValue).toArray();
        }

        return result;
    }

    // Calculate the exact number of days needed for given capacity
    public int calculateDaysNeeded(int[] weights, int capacity) {
        int days = 1;
        int currentWeight = 0;

        for (int weight : weights) {
            if (weight > capacity) {
                return Integer.MAX_VALUE; // Impossible
            }

            if (currentWeight + weight > capacity) {
                days++;
                currentWeight = weight;
            } else {
                currentWeight += weight;
            }
        }

        return days;
    }

    // Find minimum days if we can use unlimited capacity
    public int minDaysUnlimitedCapacity(int[] weights) {
        // With unlimited capacity, we can ship everything in 1 day
        return 1;
    }

    // Find maximum days (worst case scenario)
    public int maxDays(int[] weights) {
        // Worst case: ship one package per day
        return weights.length;
    }

    // Greedy approach for comparison - O(n^2) time
    public int shipWithinDaysGreedy(int[] weights, int days) {
        int maxWeight = getMaxWeight(weights);
        int totalWeight = getTotalWeight(weights);

        // Try all possible capacities
        for (int capacity = maxWeight; capacity <= totalWeight; capacity++) {
            if (canShip(weights, days, capacity)) {
                return capacity;
            }
        }

        return totalWeight;
    }

    // Optimized bounds calculation
    public int shipWithinDaysOptimized(int[] weights, int days) {
        int maxWeight = getMaxWeight(weights);
        int totalWeight = getTotalWeight(weights);

        // Better lower bound: ceiling of (total weight / days)
        int left = Math.max(maxWeight, (totalWeight + days - 1) / days);
        int right = totalWeight;

        // If we have enough days, minimum capacity is max weight
        if (days >= weights.length) {
            return maxWeight;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canShip(weights, days, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    // Find the capacity range for given days
    public int[] getCapacityRange(int[] weights, int days) {
        int minCapacity = getMaxWeight(weights);
        int maxCapacity = getTotalWeight(weights);

        // Find minimum feasible capacity
        int left = minCapacity;
        int right = maxCapacity;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canShip(weights, days, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return new int[] { left, maxCapacity };
    }

    // Validate solution
    public boolean validateSolution(int[] weights, int days, int capacity) {
        // Check if capacity can ship within days
        if (!canShip(weights, days, capacity)) {
            return false;
        }

        // Check if smaller capacity fails (if capacity > max weight)
        if (capacity > getMaxWeight(weights) && canShip(weights, days, capacity - 1)) {
            return false;
        }

        return true;
    }

    // Handle edge cases
    public int shipWithinDaysRobust(int[] weights, int days) {
        if (weights == null || weights.length == 0 || days <= 0) {
            return 0;
        }

        if (days == 1) {
            return getTotalWeight(weights);
        }

        if (days >= weights.length) {
            return getMaxWeight(weights);
        }

        return shipWithinDays(weights, days);
    }

    public static void main(String[] args) {
        CapacityToShipPackagesWithinDDays solution = new CapacityToShipPackagesWithinDDays();

        // Test Case 1: [1,2,3,4,5,6,7,8,9,10], days = 5
        int[] weights1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println(solution.shipWithinDays(weights1, 5)); // Expected: 15

        // Test Case 2: [3,2,2,4,1,4], days = 3
        int[] weights2 = { 3, 2, 2, 4, 1, 4 };
        System.out.println(solution.shipWithinDays(weights2, 3)); // Expected: 6

        // Test Case 3: [1,2,3,1,1], days = 4
        int[] weights3 = { 1, 2, 3, 1, 1 };
        System.out.println(solution.shipWithinDays(weights3, 4)); // Expected: 3

        // Test Case 4: Single package
        int[] weights4 = { 10 };
        System.out.println(solution.shipWithinDays(weights4, 1)); // Expected: 10

        // Test Case 5: One day shipping
        int[] weights5 = { 1, 2, 3, 4 };
        System.out.println(solution.shipWithinDays(weights5, 1)); // Expected: 10

        // Test Case 6: Each package per day
        System.out.println(solution.shipWithinDays(weights5, 4)); // Expected: 4

        // Test alternative implementation
        System.out.println("Alternative: " + solution.shipWithinDaysAlt(weights1, 5)); // Expected: 15

        // Test optimized version
        System.out.println("Optimized: " + solution.shipWithinDaysOptimized(weights1, 5)); // Expected: 15

        // Test greedy approach (small input only)
        System.out.println("Greedy: " + solution.shipWithinDaysGreedy(weights3, 4)); // Expected: 3

        // Test shipping schedule
        int[][] schedule = solution.getShippingSchedule(weights2, 3);
        System.out.println("Shipping schedule:");
        for (int i = 0; i < schedule.length; i++) {
            System.out.print("Day " + (i + 1) + ": packages ");
            for (int j = 0; j < schedule[i].length; j++) {
                System.out.print(schedule[i][j] + " ");
            }
            System.out.println();
        }

        // Test days calculation
        System.out.println("Days needed with capacity 6: " + solution.calculateDaysNeeded(weights2, 6)); // Expected: 3
        System.out.println("Days needed with capacity 5: " + solution.calculateDaysNeeded(weights2, 5)); // Expected: 4

        // Test capacity range
        int[] range = solution.getCapacityRange(weights1, 5);
        System.out.println("Capacity range: [" + range[0] + ", " + range[1] + "]"); // Expected: [15, 55]

        // Test validation
        int result = solution.shipWithinDays(weights1, 5);
        System.out.println("Solution valid: " + solution.validateSolution(weights1, 5, result)); // Expected: true

        // Test robust version
        System.out.println("Robust: " + solution.shipWithinDaysRobust(weights1, 5)); // Expected: 15

        // Edge cases
        int[] edge1 = { 500 };
        System.out.println("Single heavy package: " + solution.shipWithinDays(edge1, 1)); // Expected: 500

        int[] edge2 = { 1, 1, 1, 1, 1 };
        System.out.println("All same weight: " + solution.shipWithinDays(edge2, 3)); // Expected: 2

        // Large test case
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = (i % 10) + 1;
        }

        long startTime = System.currentTimeMillis();
        int largeResult = solution.shipWithinDays(large, 100);
        long endTime = System.currentTimeMillis();
        System.out.println("Large test result: " + largeResult + " (time: " + (endTime - startTime) + "ms)");

        // Performance comparison
        startTime = System.currentTimeMillis();
        solution.shipWithinDays(weights1, 5);
        long binaryTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        solution.shipWithinDaysGreedy(weights1, 5);
        long greedyTime = System.currentTimeMillis() - startTime;

        System.out.println("Binary search time: " + binaryTime + "ms");
        System.out.println("Greedy time: " + greedyTime + "ms");

        // Stress test with different day counts
        int[] testDays = { 1, 2, 3, 5, 10, weights1.length };
        for (int d : testDays) {
            int capacity = solution.shipWithinDays(weights1, d);
            System.out.println("Days: " + d + ", Min capacity: " + capacity);
        }
    }
}
