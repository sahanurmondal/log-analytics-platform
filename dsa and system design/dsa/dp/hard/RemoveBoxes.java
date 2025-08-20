package dp.hard;

/**
 * LeetCode 546: Remove Boxes
 * https://leetcode.com/problems/remove-boxes/
 *
 * Description:
 * Given several boxes with different colors represented by different positive
 * numbers, you may remove boxes of the same color that are adjacent, and get
 * points. Return the maximum points you can get.
 *
 * Constraints:
 * - 1 <= boxes.length <= 100
 * - 1 <= boxes[i] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(n^4) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class RemoveBoxes {

    // Approach 1: 3D DP with Memoization - O(n^4) time, O(n^3) space
    public int removeBoxes(int[] boxes) {
        int n = boxes.length;
        Integer[][][] memo = new Integer[n][n][n];
        return removeBoxesHelper(boxes, 0, n - 1, 0, memo);
    }

    private int removeBoxesHelper(int[] boxes, int left, int right, int k, Integer[][][] memo) {
        if (left > right)
            return 0;

        if (memo[left][right][k] != null) {
            return memo[left][right][k];
        }

        // Find the leftmost position with same color as boxes[left]
        int originalLeft = left;
        int originalK = k;

        while (left + 1 <= right && boxes[left + 1] == boxes[left]) {
            left++;
            k++;
        }

        // Option 1: Remove the left segment of same-colored boxes
        int result = (k + 1) * (k + 1) + removeBoxesHelper(boxes, left + 1, right, 0, memo);

        // Option 2: Try to connect with same-colored boxes on the right
        for (int i = left + 1; i <= right; i++) {
            if (boxes[i] == boxes[left]) {
                result = Math.max(result,
                        removeBoxesHelper(boxes, left + 1, i - 1, 0, memo) +
                                removeBoxesHelper(boxes, i, right, k + 1, memo));
            }
        }

        memo[originalLeft][right][originalK] = result;
        return result;
    }

    // Approach 2: Bottom-up DP - O(n^4) time, O(n^3) space
    public int removeBoxesBottomUp(int[] boxes) {
        int n = boxes.length;
        int[][][] dp = new int[n][n][n];

        // Base case: single box
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                dp[i][i][k] = (k + 1) * (k + 1);
            }
        }

        // Fill for increasing lengths
        for (int len = 2; len <= n; len++) {
            for (int left = 0; left <= n - len; left++) {
                int right = left + len - 1;

                for (int k = 0; k < n; k++) {
                    // Option 1: Remove left box immediately
                    dp[left][right][k] = (k + 1) * (k + 1) + dp[left + 1][right][0];

                    // Option 2: Connect with same-colored boxes
                    for (int i = left + 1; i <= right; i++) {
                        if (boxes[i] == boxes[left]) {
                            dp[left][right][k] = Math.max(dp[left][right][k],
                                    dp[left + 1][i - 1][0] + dp[i][right][k + 1]);
                        }
                    }
                }
            }
        }

        return dp[0][n - 1][0];
    }

    public static void main(String[] args) {
        RemoveBoxes solution = new RemoveBoxes();

        System.out.println("=== Remove Boxes Test Cases ===");

        // Test Case 1: Example from problem
        int[] boxes1 = { 1, 3, 2, 2, 2, 3, 4, 3, 1 };
        System.out.println("Test 1 - Boxes: " + java.util.Arrays.toString(boxes1));
        System.out.println("Memoization: " + solution.removeBoxes(boxes1));
        System.out.println("Bottom-up: " + solution.removeBoxesBottomUp(boxes1));
        System.out.println("Expected: 23\n");

        // Test Case 2: All same
        int[] boxes2 = { 1, 1, 1 };
        System.out.println("Test 2 - Boxes: " + java.util.Arrays.toString(boxes2));
        System.out.println("Memoization: " + solution.removeBoxes(boxes2));
        System.out.println("Expected: 9\n");

        // Test Case 3: Single box
        int[] boxes3 = { 1 };
        System.out.println("Test 3 - Boxes: " + java.util.Arrays.toString(boxes3));
        System.out.println("Memoization: " + solution.removeBoxes(boxes3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        RemoveBoxes solution = new RemoveBoxes();

        // Create test case with pattern
        int[] boxes = new int[50];
        for (int i = 0; i < 50; i++) {
            boxes[i] = (i % 5) + 1;
        }

        System.out.println("=== Performance Test (Array length: " + boxes.length + ") ===");

        long start = System.nanoTime();
        int result = solution.removeBoxes(boxes);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
