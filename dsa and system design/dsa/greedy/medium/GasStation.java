package greedy.medium;

import java.util.*;

/**
 * LeetCode 134: Gas Station
 * https://leetcode.com/problems/gas-station/
 * 
 * Companies: Amazon, Google, Meta, Microsoft, Apple
 * Frequency: High (Asked in 180+ interviews)
 *
 * Description:
 * There are n gas stations along a circular route, where the amount of gas at
 * the ith station is gas[i].
 * You have a car with an unlimited gas tank and it costs cost[i] of gas to
 * travel from the ith station
 * to its next (i + 1)th station. You begin the journey with an empty tank at
 * one of the gas stations.
 * 
 * Given two integer arrays gas and cost, return the starting gas station's
 * index if you can travel
 * around the circuit once in the clockwise direction, otherwise return -1.
 * 
 * If there exists a solution, it is guaranteed to be unique.
 *
 * Constraints:
 * - n == gas.length == cost.length
 * - 1 <= n <= 10^5
 * - 0 <= gas[i], cost[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find all possible starting points?
 * 2. What's the minimum amount of extra gas needed if no solution exists?
 * 3. Can you solve it with multiple cars?
 */
public class GasStation {

    // Approach 1: Greedy Single Pass - O(n) time, O(1) space
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int totalTank = 0; // Total gas - total cost
        int currentTank = 0; // Current gas in tank
        int start = 0; // Starting position

        for (int i = 0; i < gas.length; i++) {
            int gain = gas[i] - cost[i];
            totalTank += gain;
            currentTank += gain;

            // If we can't reach next station, reset starting point
            if (currentTank < 0) {
                start = i + 1;
                currentTank = 0;
            }
        }

        // If total tank >= 0, we can complete the circuit
        return totalTank >= 0 ? start : -1;
    }

    // Approach 2: Brute Force - O(n²) time, O(1) space
    public int canCompleteCircuitBruteForce(int[] gas, int[] cost) {
        int n = gas.length;

        for (int start = 0; start < n; start++) {
            int tank = 0;
            boolean canComplete = true;

            for (int i = 0; i < n; i++) {
                int index = (start + i) % n;
                tank += gas[index] - cost[index];

                if (tank < 0) {
                    canComplete = false;
                    break;
                }
            }

            if (canComplete) {
                return start;
            }
        }

        return -1;
    }

    // Follow-up 1: Find all possible starting points
    public List<Integer> findAllStartingPoints(int[] gas, int[] cost) {
        List<Integer> startingPoints = new ArrayList<>();
        int n = gas.length;

        for (int start = 0; start < n; start++) {
            int tank = 0;
            boolean canComplete = true;

            for (int i = 0; i < n; i++) {
                int index = (start + i) % n;
                tank += gas[index] - cost[index];

                if (tank < 0) {
                    canComplete = false;
                    break;
                }
            }

            if (canComplete) {
                startingPoints.add(start);
            }
        }

        return startingPoints;
    }

    // Follow-up 2: Minimum extra gas needed if no solution exists
    public int minExtraGas(int[] gas, int[] cost) {
        int totalGas = Arrays.stream(gas).sum();
        int totalCost = Arrays.stream(cost).sum();

        if (totalGas >= totalCost) {
            return 0; // Already solvable
        }

        return totalCost - totalGas;
    }

    // Follow-up 3: Multiple cars solution
    public int minCarsNeeded(int[] gas, int[] cost) {
        int n = gas.length;
        int[] netGain = new int[n];

        for (int i = 0; i < n; i++) {
            netGain[i] = gas[i] - cost[i];
        }

        // Find minimum prefix sum (most negative point)
        int minPrefixSum = 0;
        int currentSum = 0;

        for (int gain : netGain) {
            currentSum += gain;
            minPrefixSum = Math.min(minPrefixSum, currentSum);
        }

        // If minPrefixSum is negative, we need additional cars
        return minPrefixSum < 0 ? Math.abs(minPrefixSum) : 1;
    }

    // Helper: Validate if we can complete circuit from given start
    private boolean canCompleteFromStart(int[] gas, int[] cost, int start) {
        int tank = 0;
        int n = gas.length;

        for (int i = 0; i < n; i++) {
            int index = (start + i) % n;
            tank += gas[index] - cost[index];

            if (tank < 0) {
                return false;
            }
        }

        return true;
    }

    // Helper: Get detailed journey information
    public String getJourneyDetails(int[] gas, int[] cost, int start) {
        if (start == -1) {
            return "No valid starting point";
        }

        StringBuilder sb = new StringBuilder();
        int tank = 0;
        int n = gas.length;

        sb.append("Journey starting from station ").append(start).append(":\n");

        for (int i = 0; i < n; i++) {
            int index = (start + i) % n;
            tank += gas[index];
            sb.append("Station ").append(index).append(": Add ").append(gas[index])
                    .append(" gas, tank = ").append(tank);

            tank -= cost[index];
            sb.append(", Travel to next (cost ").append(cost[index])
                    .append("), tank = ").append(tank).append("\n");

            if (tank < 0) {
                sb.append("Failed at station ").append(index);
                break;
            }
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        GasStation solution = new GasStation();

        // Test Case 1: Valid circuit
        int[] gas1 = { 1, 2, 3, 4, 5 };
        int[] cost1 = { 3, 4, 5, 1, 2 };
        int result1 = solution.canCompleteCircuit(gas1, cost1);
        System.out.println("Test 1 - Starting station: " + result1); // Expected: 3
        System.out.println(solution.getJourneyDetails(gas1, cost1, result1));

        // Test Case 2: No valid circuit
        int[] gas2 = { 2, 3, 4 };
        int[] cost2 = { 3, 4, 3 };
        int result2 = solution.canCompleteCircuit(gas2, cost2);
        System.out.println("Test 2 - Starting station: " + result2); // Expected: -1

        // Test Case 3: Single station
        int[] gas3 = { 5 };
        int[] cost3 = { 4 };
        int result3 = solution.canCompleteCircuit(gas3, cost3);
        System.out.println("Test 3 - Starting station: " + result3); // Expected: 0

        // Follow-up tests
        System.out.println("\nFollow-up 1 - All starting points: " +
                solution.findAllStartingPoints(gas1, cost1));

        System.out.println("Follow-up 2 - Min extra gas for test 2: " +
                solution.minExtraGas(gas2, cost2));

        System.out.println("Follow-up 3 - Min cars needed for test 2: " +
                solution.minCarsNeeded(gas2, cost2));

        // Edge case: All zeros
        int[] gas4 = { 0, 0, 0 };
        int[] cost4 = { 0, 0, 0 };
        System.out.println("Edge case - All zeros: " +
                solution.canCompleteCircuit(gas4, cost4)); // Expected: 0
    }
}
