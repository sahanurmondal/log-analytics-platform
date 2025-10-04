package searching.medium;

/**
 * LeetCode 1011: Capacity To Ship Packages Within D Days
 * https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given an array of package weights and a number of days D,
 * find the least weight capacity of a ship that can ship all packages within D
 * days.
 *
 * Constraints:
 * - 1 <= D <= weights.length <= 5 * 10^4
 * - 1 <= weights[i] <= 500
 * 
 * Follow-up Questions:
 * 1. Can you return the schedule for each day?
 * 2. What if there's a constraint on the number of packages per day?
 * 3. What if packages can be split?
 */
public class CapacityToShipPackages {

    // Approach 1: Binary Search on Answer - O(n * log(sum of weights)) time, O(1)
    // space
    public int shipWithinDays(int[] weights, int D) {
        int left = 0, right = 0;
        for (int w : weights) {
            left = Math.max(left, w);
            right += w;
        }

        int result = right;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canShip(weights, D, mid)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    private boolean canShip(int[] weights, int D, int capacity) {
        int days = 1;
        int currentWeight = 0;
        for (int w : weights) {
            if (currentWeight + w > capacity) {
                days++;
                currentWeight = w;
            } else {
                currentWeight += w;
            }
        }
        return days <= D;
    }

    // Follow-up 1: Return the schedule for each day
    public java.util.List<java.util.List<Integer>> getShippingSchedule(int[] weights, int D) {
        int capacity = shipWithinDays(weights, D);
        java.util.List<java.util.List<Integer>> schedule = new java.util.ArrayList<>();
        java.util.List<Integer> currentDayPackages = new java.util.ArrayList<>();
        int currentWeight = 0;

        for (int w : weights) {
            if (currentWeight + w > capacity) {
                schedule.add(new java.util.ArrayList<>(currentDayPackages));
                currentDayPackages.clear();
                currentWeight = 0;
            }
            currentDayPackages.add(w);
            currentWeight += w;
        }
        if (!currentDayPackages.isEmpty()) {
            schedule.add(currentDayPackages);
        }
        return schedule;
    }

    // Follow-up 2: Constraint on number of packages per day
    public int shipWithPackageLimit(int[] weights, int D, int maxPackagesPerDay) {
        int left = 0, right = 0;
        for (int w : weights) {
            left = Math.max(left, w);
            right += w;
        }

        int result = right;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canShipWithPackageLimit(weights, D, mid, maxPackagesPerDay)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    private boolean canShipWithPackageLimit(int[] weights, int D, int capacity, int maxPackages) {
        int days = 1;
        int currentWeight = 0;
        int currentPackages = 0;
        for (int w : weights) {
            if (currentWeight + w > capacity || currentPackages + 1 > maxPackages) {
                days++;
                currentWeight = w;
                currentPackages = 1;
            } else {
                currentWeight += w;
                currentPackages++;
            }
        }
        return days <= D;
    }

    // Follow-up 3: Packages can be split
    public double shipWithSplittablePackages(int[] weights, int D) {
        long totalWeight = 0;
        for (int w : weights) {
            totalWeight += w;
        }
        return (double) totalWeight / D;
    }

    public static void main(String[] args) {
        CapacityToShipPackages solution = new CapacityToShipPackages();

        // Test case 1: Basic case
        int[] weights1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println("Test 1: weights=[1..10], D=5");
        System.out.println("Expected: 15, Got: " + solution.shipWithinDays(weights1, 5));

        // Test case 2: D is large
        System.out.println("\nTest 2: weights=[3,2,2,4,1,4], D=3");
        int[] weights2 = { 3, 2, 2, 4, 1, 4 };
        System.out.println("Expected: 6, Got: " + solution.shipWithinDays(weights2, 3));

        // Test case 3: D is 1
        System.out.println("\nTest 3: weights=[1,2,3,1,1], D=1");
        int[] weights3 = { 1, 2, 3, 1, 1 };
        System.out.println("Expected: 8, Got: " + solution.shipWithinDays(weights3, 1));

        // Edge case: D equals weights.length
        System.out.println("\nEdge case: D = weights.length");
        System.out.println("Expected: 10, Got: " + solution.shipWithinDays(weights1, 10));

        // Follow-up 1: Get schedule
        System.out.println("\nFollow-up 1: Get schedule for Test 1");
        System.out.println("Schedule: " + solution.getShippingSchedule(weights1, 5));

        // Follow-up 2: Package limit
        System.out.println("\nFollow-up 2: Package limit for Test 1, maxPackages=3");
        System.out.println("Capacity with limit: " + solution.shipWithPackageLimit(weights1, 5, 3));

        // Follow-up 3: Splittable packages
        System.out.println("\nFollow-up 3: Splittable packages for Test 1");
        System.out.println("Capacity for splittable: " + solution.shipWithSplittablePackages(weights1, 5));
    }
}
