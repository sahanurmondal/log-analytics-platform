package backtracking.hard;

import java.util.*;

/**
 * LeetCode 546: Remove Boxes
 * URL: https://leetcode.com/problems/remove-boxes/
 * Difficulty: Hard
 * Companies: Google, Facebook, Amazon, Microsoft
 * Frequency: Medium
 *
 * Description:
 * You have several boxes with different colors represented by different
 * positive numbers.
 * You may remove boxes by selecting k consecutive boxes of the same color and
 * receive k*k points.
 * Find the maximum points you can obtain.
 *
 * Constraints:
 * - 1 <= boxes.length <= 100
 * - 1 <= boxes[i] <= 100
 *
 * Follow-up Questions:
 * 1. Can you solve it using dynamic programming with memoization?
 * 2. What if we need to track the removal sequence?
 * 3. How to optimize for sparse arrays?
 * 4. Can you solve it with interval DP?
 */
public class RemoveBoxes {

    // Approach 1: DP with memoization - O(n^4)
    public int removeBoxes(int[] boxes) {
        if (boxes == null || boxes.length == 0)
            return 0;

        int n = boxes.length;
        int[][][] memo = new int[n][n][n];
        return removeBoxesHelper(boxes, 0, n - 1, 0, memo);
    }

    private int removeBoxesHelper(int[] boxes, int left, int right, int k, int[][][] memo) {
        if (left > right)
            return 0;

        if (memo[left][right][k] != 0) {
            return memo[left][right][k];
        }

        // Optimize: merge consecutive same colors
        while (right > left && boxes[right] == boxes[right - 1]) {
            right--;
            k++;
        }

        // Option 1: Remove the rightmost group directly
        int result = removeBoxesHelper(boxes, left, right - 1, 0, memo) + (k + 1) * (k + 1);

        // Option 2: Try to merge with same color boxes on the left
        for (int i = left; i < right; i++) {
            if (boxes[i] == boxes[right]) {
                result = Math.max(result,
                        removeBoxesHelper(boxes, left, i, k + 1, memo) +
                                removeBoxesHelper(boxes, i + 1, right - 1, 0, memo));
            }
        }

        memo[left][right][k] = result;
        return result;
    }

    // Approach 2: Bottom-up DP - O(n^4)
    public int removeBoxesBottomUp(int[] boxes) {
        if (boxes == null || boxes.length == 0)
            return 0;

        int n = boxes.length;
        int[][][] dp = new int[n][n][n];

        // Base case: single box
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                dp[i][i][k] = (k + 1) * (k + 1);
            }
        }

        // Fill dp table
        for (int len = 2; len <= n; len++) {
            for (int left = 0; left <= n - len; left++) {
                int right = left + len - 1;
                for (int k = 0; k < n; k++) {
                    // Remove right box directly
                    dp[left][right][k] = dp[left][right - 1][0] + (k + 1) * (k + 1);

                    // Try to merge with same color
                    for (int i = left; i < right; i++) {
                        if (boxes[i] == boxes[right]) {
                            dp[left][right][k] = Math.max(dp[left][right][k],
                                    dp[left][i][k + 1] + dp[i + 1][right - 1][0]);
                        }
                    }
                }
            }
        }

        return dp[0][n - 1][0];
    }

    // Approach 3: Backtracking (less efficient but intuitive) - O(2^n)
    public int removeBoxesBacktrack(int[] boxes) {
        if (boxes == null || boxes.length == 0)
            return 0;

        List<Integer> boxList = new ArrayList<>();
        for (int box : boxes) {
            boxList.add(box);
        }

        return backtrack(boxList);
    }

    private int backtrack(List<Integer> boxes) {
        if (boxes.isEmpty())
            return 0;

        int maxPoints = 0;

        // Try removing each consecutive group
        for (int i = 0; i < boxes.size(); i++) {
            int j = i;
            while (j < boxes.size() && boxes.get(j).equals(boxes.get(i))) {
                j++;
            }

            // Remove group from i to j-1
            int groupSize = j - i;
            int points = groupSize * groupSize;

            List<Integer> remaining = new ArrayList<>();
            remaining.addAll(boxes.subList(0, i));
            remaining.addAll(boxes.subList(j, boxes.size()));

            maxPoints = Math.max(maxPoints, points + backtrack(remaining));

            i = j - 1; // Skip to end of current group
        }

        return maxPoints;
    }

    // Follow-up 2: Track removal sequence
    public List<int[]> getOptimalRemovalSequence(int[] boxes) {
        List<int[]> sequence = new ArrayList<>();
        List<Integer> boxList = new ArrayList<>();
        for (int box : boxes) {
            boxList.add(box);
        }

        backtrackWithSequence(boxList, sequence, new ArrayList<>());
        return sequence;
    }

    private int backtrackWithSequence(List<Integer> boxes, List<int[]> bestSequence,
            List<int[]> currentSequence) {
        if (boxes.isEmpty()) {
            if (currentSequence.size() < bestSequence.size() || bestSequence.isEmpty()) {
                bestSequence.clear();
                bestSequence.addAll(currentSequence);
            }
            return 0;
        }

        int maxPoints = 0;

        for (int i = 0; i < boxes.size(); i++) {
            int j = i;
            while (j < boxes.size() && boxes.get(j).equals(boxes.get(i))) {
                j++;
            }

            int groupSize = j - i;
            int points = groupSize * groupSize;

            List<Integer> remaining = new ArrayList<>();
            remaining.addAll(boxes.subList(0, i));
            remaining.addAll(boxes.subList(j, boxes.size()));

            currentSequence.add(new int[] { i, j - 1, boxes.get(i), points });
            int totalPoints = points + backtrackWithSequence(remaining, bestSequence, currentSequence);
            currentSequence.remove(currentSequence.size() - 1);

            maxPoints = Math.max(maxPoints, totalPoints);

            i = j - 1;
        }

        return maxPoints;
    }

    // Helper method to validate solution
    public boolean validateSolution(int[] boxes, List<int[]> moves) {
        List<Integer> boxList = new ArrayList<>();
        for (int box : boxes) {
            boxList.add(box);
        }

        for (int[] move : moves) {
            int start = move[0];
            int end = move[1];
            int color = move[2];

            if (start >= boxList.size() || end >= boxList.size())
                return false;

            for (int i = start; i <= end; i++) {
                if (!boxList.get(i).equals(color))
                    return false;
            }

            // Remove boxes (from right to left to maintain indices)
            for (int i = end; i >= start; i--) {
                boxList.remove(i);
            }
        }

        return boxList.isEmpty();
    }

    // Helper method to calculate points from moves
    public int calculatePoints(List<int[]> moves) {
        int totalPoints = 0;
        for (int[] move : moves) {
            int groupSize = move[1] - move[0] + 1;
            totalPoints += groupSize * groupSize;
        }
        return totalPoints;
    }

    public static void main(String[] args) {
        RemoveBoxes solution = new RemoveBoxes();

        // Test Case 1: Complex example
        System.out.println("Test 1: " + solution.removeBoxes(new int[] { 1, 3, 2, 2, 2, 3, 4, 3, 1 }));
        // Expected: 23

        // Test Case 2: All same boxes
        System.out.println("Test 2: " + solution.removeBoxes(new int[] { 1, 1, 1 }));
        // Expected: 9

        // Test Case 3: Single box
        System.out.println("Test 3: " + solution.removeBoxes(new int[] { 1 }));
        // Expected: 1

        // Test Case 4: Empty array
        System.out.println("Test 4: " + solution.removeBoxes(new int[] {}));
        // Expected: 0

        // Test Case 5: All same color - optimal case
        System.out.println("Test 5: " + solution.removeBoxes(new int[] { 2, 2, 2, 2 }));
        // Expected: 16

        // Test Case 6: Bottom-up approach
        System.out.println(
                "Test 6 (Bottom-up): " + solution.removeBoxesBottomUp(new int[] { 1, 3, 2, 2, 2, 3, 4, 3, 1 }));
        // Expected: 23

        // Test Case 7: Backtracking approach
        System.out.println("Test 7 (Backtrack): " + solution.removeBoxesBacktrack(new int[] { 1, 1, 1 }));
        // Expected: 9

        // Test Case 8: Alternating pattern
        System.out.println("Test 8: " + solution.removeBoxes(new int[] { 1, 2, 1, 2, 1 }));
        // Expected: 5

        // Test Case 9: Get optimal sequence
        System.out.println("Test 9 (Sequence): " + solution.getOptimalRemovalSequence(new int[] { 1, 1, 1 }).size());
        // Expected: sequence of moves

        // Test Case 10: Validate solution
        System.out.println("Test 10 (Validate): " + solution.validateSolution(new int[] { 1, 1, 1 },
                Arrays.asList(new int[] { 0, 2, 1, 9 })));
        // Expected: true

        // Test Case 11: Calculate points
        System.out.println("Test 11 (Points): " + solution.calculatePoints(
                Arrays.asList(new int[] { 0, 2, 1, 9 })));
        // Expected: 9

        // Test Case 12: Performance comparison
        long start = System.currentTimeMillis();
        int result12 = solution.removeBoxes(new int[] { 1, 2, 3, 1, 2, 3, 1, 2, 3 });
        long end = System.currentTimeMillis();
        System.out.println("Test 12 (Performance): " + result12 + " in " + (end - start) + "ms");

        // Test Case 13: Large same-color groups
        System.out.println("Test 13: " + solution.removeBoxes(new int[] { 1, 1, 2, 2, 2, 2, 3, 3, 3, 1, 1 }));
        // Expected: optimal points

        // Test Case 14: Consistency check
        int[] test14 = { 1, 3, 2, 2, 2, 3, 4, 3, 1 };
        boolean consistent = solution.removeBoxes(test14) == solution.removeBoxesBottomUp(test14);
        System.out.println("Test 14 (Consistency): " + consistent);
        // Expected: true

        // Test Case 15: Edge case - two colors alternating
        System.out.println("Test 15: " + solution.removeBoxes(new int[] { 1, 2, 1, 2 }));
        // Expected: 4
    }
}
