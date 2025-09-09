package searching.hard;

import java.util.*;

/**
 * SPOJ: Aggressive Cows
 * https://www.spoj.com/problems/AGGRCOW/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given N stalls and C cows, place cows such that minimum distance
 * between any two cows is maximized.
 *
 * Constraints:
 * - 2 <= N <= 100000
 * - 2 <= C <= N
 * - 0 <= stall[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you return the actual positions of cows?
 * 2. What if we want to minimize the maximum distance?
 * 3. Can you handle weighted cows?
 */
public class AggressiveCows {

    // Approach 1: Binary search on answer - O(n log n + n log(max-min)) time, O(1)
    // space
    public int largestMinDistance(int[] stalls, int cows) {
        if (stalls == null || stalls.length < cows)
            return -1;

        Arrays.sort(stalls);
        int left = 1, right = stalls[stalls.length - 1] - stalls[0];
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canPlaceCows(stalls, cows, mid)) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private boolean canPlaceCows(int[] stalls, int cows, int minDist) {
        int count = 1, lastPos = stalls[0];
        for (int i = 1; i < stalls.length; i++) {
            if (stalls[i] - lastPos >= minDist) {
                count++;
                lastPos = stalls[i];
                if (count >= cows)
                    return true;
            }
        }
        return false;
    }

    // Follow-up 1: Return actual positions of cows
    public List<Integer> getCowPositions(int[] stalls, int cows) {
        List<Integer> positions = new ArrayList<>();
        if (stalls == null || stalls.length < cows)
            return positions;

        Arrays.sort(stalls);
        int minDist = largestMinDistance(stalls, cows);

        int count = 1, lastPos = stalls[0];
        positions.add(lastPos);

        for (int i = 1; i < stalls.length && count < cows; i++) {
            if (stalls[i] - lastPos >= minDist) {
                count++;
                lastPos = stalls[i];
                positions.add(lastPos);
            }
        }
        return positions;
    }

    // Follow-up 2: Minimize the maximum distance
    public int smallestMaxDistance(int[] stalls, int cows) {
        if (stalls == null || stalls.length < cows)
            return -1;

        Arrays.sort(stalls);
        int left = 1, right = stalls[stalls.length - 1] - stalls[0];
        int result = right;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canPlaceWithMaxDist(stalls, cows, mid)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    private boolean canPlaceWithMaxDist(int[] stalls, int cows, int maxDist) {
        int count = 1, lastPos = stalls[0];
        for (int i = 1; i < stalls.length; i++) {
            if (stalls[i] - lastPos <= maxDist) {
                count++;
                lastPos = stalls[i];
                if (count >= cows)
                    return true;
            }
        }
        return false;
    }

    // Follow-up 3: Handle weighted cows
    public int largestMinDistanceWeighted(int[] stalls, int[] weights, int totalWeight) {
        if (stalls == null || stalls.length == 0)
            return -1;

        Arrays.sort(stalls);
        int left = 1, right = stalls[stalls.length - 1] - stalls[0];
        int result = 0;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (canPlaceWeightedCows(stalls, weights, totalWeight, mid)) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    private boolean canPlaceWeightedCows(int[] stalls, int[] weights, int totalWeight, int minDist) {
        int currentWeight = 0, lastPos = -1;
        for (int i = 0; i < stalls.length; i++) {
            if (lastPos == -1 || stalls[i] - lastPos >= minDist) {
                currentWeight += weights[i % weights.length];
                lastPos = stalls[i];
                if (currentWeight >= totalWeight)
                    return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        AggressiveCows solution = new AggressiveCows();

        // Test case 1: Basic case
        int[] stalls1 = { 1, 2, 4, 8, 9 };
        System.out.println("Test 1 - Basic case with 3 cows:");
        System.out.println("Expected: 3, Got: " + solution.largestMinDistance(stalls1, 3));
        System.out.println("Cow positions: " + solution.getCowPositions(stalls1, 3));

        // Test case 2: Minimum cows (2)
        int[] stalls2 = { 1, 10 };
        System.out.println("\nTest 2 - Minimum cows (2):");
        System.out.println("Expected: 9, Got: " + solution.largestMinDistance(stalls2, 2));

        // Test case 3: Maximum cows (all stalls)
        int[] stalls3 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest 3 - Maximum cows (all stalls):");
        System.out.println("Expected: 1, Got: " + solution.largestMinDistance(stalls3, 5));

        // Test case 4: Large distances
        int[] stalls4 = { 0, 1000000, 2000000 };
        System.out.println("\nTest 4 - Large distances:");
        System.out.println("Expected: 1000000, Got: " + solution.largestMinDistance(stalls4, 2));

        // Test case 5: Duplicate stalls (after sorting)
        int[] stalls5 = { 1, 1, 2, 2, 3, 3 };
        System.out.println("\nTest 5 - Duplicate stalls:");
        System.out.println("Expected: 1, Got: " + solution.largestMinDistance(stalls5, 3));

        // Edge case: Not enough stalls
        int[] stalls6 = { 1, 2 };
        System.out.println("\nEdge case - Not enough stalls:");
        System.out.println("Expected: -1, Got: " + solution.largestMinDistance(stalls6, 3));

        // Follow-up 2: Minimize maximum distance
        System.out.println("\nFollow-up 2 - Minimize maximum distance:");
        System.out.println("Expected: 4, Got: " + solution.smallestMaxDistance(stalls1, 3));

        // Follow-up 3: Weighted cows
        int[] weights = { 1, 2, 3, 4, 5 };
        System.out.println("\nFollow-up 3 - Weighted cows:");
        System.out.println("Expected result: " + solution.largestMinDistanceWeighted(stalls1, weights, 6));

        // Performance test with large input
        int[] largeStalls = new int[100000];
        for (int i = 0; i < largeStalls.length; i++) {
            largeStalls[i] = i * 1000;
        }
        long startTime = System.currentTimeMillis();
        int result = solution.largestMinDistance(largeStalls, 1000);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (100k stalls, 1k cows): " + result +
                " in " + (endTime - startTime) + "ms");
    }
}
