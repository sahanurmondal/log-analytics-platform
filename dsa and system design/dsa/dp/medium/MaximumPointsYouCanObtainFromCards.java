package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 1423: Maximum Points You Can Obtain from Cards
 * https://leetcode.com/problems/maximum-points-you-can-obtain-from-cards/
 *
 * Description:
 * There are several cards arranged in a row, and each card has an associated
 * number of points.
 * The points are given in the integer array cardPoints.
 * In one step, you can take one card from the beginning or from the end of the
 * row. You have to take exactly k cards.
 * Your score is the sum of the points of the cards you have taken.
 * Given the integer array cardPoints and the integer k, return the maximum
 * score you can obtain.
 *
 * Constraints:
 * - 1 <= cardPoints.length <= 10^5
 * - 1 <= cardPoints[i] <= 10^4
 * - 1 <= k <= cardPoints.length
 *
 * Follow-up:
 * - Can you solve it in O(k) time?
 * - What if we can take cards from middle as well?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class MaximumPointsYouCanObtainFromCards {

    // Approach 1: Sliding Window (Optimal) - O(k) time, O(1) space
    public int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length;

        // Calculate sum of first k cards
        int leftSum = 0;
        for (int i = 0; i < k; i++) {
            leftSum += cardPoints[i];
        }

        int maxScore = leftSum;
        int rightSum = 0;

        // Try all combinations: i cards from left, k-i cards from right
        for (int i = k - 1; i >= 0; i--) {
            leftSum -= cardPoints[i];
            rightSum += cardPoints[n - (k - i)];
            maxScore = Math.max(maxScore, leftSum + rightSum);
        }

        return maxScore;
    }

    // Approach 2: Prefix Sum - O(k) time, O(k) space
    public int maxScorePrefix(int[] cardPoints, int k) {
        int n = cardPoints.length;

        // Calculate prefix sums from left
        int[] leftPrefix = new int[k + 1];
        for (int i = 0; i < k; i++) {
            leftPrefix[i + 1] = leftPrefix[i] + cardPoints[i];
        }

        // Calculate prefix sums from right
        int[] rightPrefix = new int[k + 1];
        for (int i = 0; i < k; i++) {
            rightPrefix[i + 1] = rightPrefix[i] + cardPoints[n - 1 - i];
        }

        int maxScore = 0;

        // Try all combinations
        for (int i = 0; i <= k; i++) {
            int leftSum = leftPrefix[i];
            int rightSum = rightPrefix[k - i];
            maxScore = Math.max(maxScore, leftSum + rightSum);
        }

        return maxScore;
    }

    // Approach 3: Two Pointers - O(k) time, O(1) space
    public int maxScoreTwoPointers(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int totalSum = 0;

        // Calculate sum of first k cards
        for (int i = 0; i < k; i++) {
            totalSum += cardPoints[i];
        }

        int maxScore = totalSum;

        // Replace cards from left with cards from right
        for (int i = 0; i < k; i++) {
            totalSum = totalSum - cardPoints[k - 1 - i] + cardPoints[n - 1 - i];
            maxScore = Math.max(maxScore, totalSum);
        }

        return maxScore;
    }

    // Approach 4: Minimum Subarray (Complement) - O(n) time, O(1) space
    public int maxScoreComplement(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int totalSum = Arrays.stream(cardPoints).sum();

        if (k == n)
            return totalSum;

        // Find minimum sum subarray of length (n - k)
        int windowSize = n - k;
        int currentSum = 0;

        // Calculate sum of first window
        for (int i = 0; i < windowSize; i++) {
            currentSum += cardPoints[i];
        }

        int minSum = currentSum;

        // Slide the window
        for (int i = windowSize; i < n; i++) {
            currentSum = currentSum - cardPoints[i - windowSize] + cardPoints[i];
            minSum = Math.min(minSum, currentSum);
        }

        return totalSum - minSum;
    }

    // Approach 5: DP with Memoization - O(k^2) time, O(k^2) space
    public int maxScoreDP(int[] cardPoints, int k) {
        int n = cardPoints.length;
        Integer[][] memo = new Integer[k + 1][k + 1];
        return maxScoreDPHelper(cardPoints, 0, n - 1, k, memo);
    }

    private int maxScoreDPHelper(int[] cardPoints, int left, int right, int k, Integer[][] memo) {
        if (k == 0)
            return 0;
        if (left > right)
            return 0;

        int leftIndex = left;
        int rightIndex = cardPoints.length - 1 - right;

        if (memo[leftIndex][rightIndex] != null) {
            return memo[leftIndex][rightIndex];
        }

        // Take from left
        int takeLeft = cardPoints[left] + maxScoreDPHelper(cardPoints, left + 1, right, k - 1, memo);

        // Take from right
        int takeRight = cardPoints[right] + maxScoreDPHelper(cardPoints, left, right - 1, k - 1, memo);

        memo[leftIndex][rightIndex] = Math.max(takeLeft, takeRight);
        return memo[leftIndex][rightIndex];
    }

    public static void main(String[] args) {
        MaximumPointsYouCanObtainFromCards solution = new MaximumPointsYouCanObtainFromCards();

        System.out.println("=== Maximum Points You Can Obtain from Cards Test Cases ===");

        // Test Case 1: Example from problem
        int[] cardPoints1 = { 1, 2, 3, 4, 5, 6, 1 };
        int k1 = 3;
        System.out.println("Test 1 - Cards: " + Arrays.toString(cardPoints1) + ", k: " + k1);
        System.out.println("Sliding Window: " + solution.maxScore(cardPoints1, k1));
        System.out.println("Prefix Sum: " + solution.maxScorePrefix(cardPoints1, k1));
        System.out.println("Two Pointers: " + solution.maxScoreTwoPointers(cardPoints1, k1));
        System.out.println("Complement: " + solution.maxScoreComplement(cardPoints1, k1));
        System.out.println("DP: " + solution.maxScoreDP(cardPoints1, k1));
        System.out.println("Expected: 12\n");

        // Test Case 2: Take all cards
        int[] cardPoints2 = { 2, 2, 2 };
        int k2 = 3;
        System.out.println("Test 2 - Cards: " + Arrays.toString(cardPoints2) + ", k: " + k2);
        System.out.println("Sliding Window: " + solution.maxScore(cardPoints2, k2));
        System.out.println("Expected: 6\n");

        // Test Case 3: Optimal from one side
        int[] cardPoints3 = { 9, 7, 7, 9, 7, 7, 9 };
        int k3 = 7;
        System.out.println("Test 3 - Cards: " + Arrays.toString(cardPoints3) + ", k: " + k3);
        System.out.println("Sliding Window: " + solution.maxScore(cardPoints3, k3));
        System.out.println("Expected: 55\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumPointsYouCanObtainFromCards solution = new MaximumPointsYouCanObtainFromCards();

        int[] largeCards = new int[100000];
        for (int i = 0; i < largeCards.length; i++) {
            largeCards[i] = (int) (Math.random() * 10000) + 1;
        }
        int k = 5000;

        System.out.println("=== Performance Test (Array size: " + largeCards.length + ", k: " + k + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxScore(largeCards, k);
        long end = System.nanoTime();
        System.out.println("Sliding Window: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxScorePrefix(largeCards, k);
        end = System.nanoTime();
        System.out.println("Prefix Sum: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxScoreComplement(largeCards, k);
        end = System.nanoTime();
        System.out.println("Complement: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
