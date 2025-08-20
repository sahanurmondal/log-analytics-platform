package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 1423: Maximum Points You Can Obtain from Cards
 * https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given an array of cardPoints and an integer k, pick k cards from
 * either end to maximize the sum.
 *
 * Constraints:
 * - 1 <= cardPoints.length <= 10^5
 * - 1 <= cardPoints[i] <= 10^4
 * - 1 <= k <= cardPoints.length
 * 
 * Follow-up Questions:
 * 1. Can you find the minimum sum?
 * 2. Can you handle updates to the array?
 * 3. Can you find the indices of the cards picked?
 */
public class MaxPointsFromCards {

    // Approach 1: Sliding window for minimum sum of n-k consecutive cards
    public int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length, total = 0;
        for (int p : cardPoints)
            total += p;
        int minSum = 0, window = n - k;
        for (int i = 0; i < window; i++)
            minSum += cardPoints[i];
        int currSum = minSum;
        for (int i = window; i < n; i++) {
            currSum += cardPoints[i] - cardPoints[i - window];
            minSum = Math.min(minSum, currSum);
        }
        return total - minSum;
    }

    // Follow-up 1: Minimum sum from k cards
    public int minScore(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int minSum = Integer.MAX_VALUE;
        for (int left = 0; left <= k; left++) {
            int sum = 0;
            for (int i = 0; i < left; i++)
                sum += cardPoints[i];
            for (int i = n - (k - left); i < n; i++)
                sum += cardPoints[i];
            minSum = Math.min(minSum, sum);
        }
        return minSum;
    }

    // Follow-up 2: Indices of cards picked for max score
    public List<Integer> indicesForMaxScore(int[] cardPoints, int k) {
        int n = cardPoints.length, total = 0;
        for (int p : cardPoints)
            total += p;
        int minSum = 0, window = n - k;
        for (int i = 0; i < window; i++)
            minSum += cardPoints[i];
        int currSum = minSum, minStart = 0;
        for (int i = window; i < n; i++) {
            currSum += cardPoints[i] - cardPoints[i - window];
            if (currSum < minSum) {
                minSum = currSum;
                minStart = i - window + 1;
            }
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < minStart; i++)
            indices.add(i);
        for (int i = minStart + window; i < n; i++)
            indices.add(i);
        return indices;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaxPointsFromCards solution = new MaxPointsFromCards();

        // Test case 1: Basic case
        int[] cardPoints1 = { 1, 2, 3, 4, 5, 6, 1 };
        int k1 = 3;
        System.out.println("Test 1 - cardPoints: " + Arrays.toString(cardPoints1) + ", k: " + k1 + " Expected: 12");
        System.out.println("Result: " + solution.maxScore(cardPoints1, k1));

        // Test case 2: Minimum sum
        System.out.println("\nTest 2 - Minimum sum:");
        System.out.println(solution.minScore(cardPoints1, k1));

        // Test case 3: Indices for max score
        System.out.println("\nTest 3 - Indices for max score:");
        System.out.println(solution.indicesForMaxScore(cardPoints1, k1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("All same: " + solution.maxScore(new int[] { 5, 5, 5, 5, 5 }, 2));
        System.out.println("Single card: " + solution.maxScore(new int[] { 10 }, 1));
        System.out.println("k equals length: " + solution.maxScore(cardPoints1, cardPoints1.length));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.maxScore(large, 5000);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
