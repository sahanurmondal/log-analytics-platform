package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 45: Jump Game II
 * https://leetcode.com/problems/jump-game-ii/
 *
 * Description:
 * Given an array of non-negative integers nums, you are initially positioned at
 * the first index of the array.
 * Each element in the array represents your maximum jump length at that
 * position.
 * Your goal is to reach the last index in the minimum number of jumps.
 * You can assume that you can always reach the last index.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - 0 <= nums[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - What if we need to return the actual path?
 * 
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Medium
 */
public class JumpGameII {

    // Approach 1: BFS/Level-order traversal - O(n) time, O(1) space
    public int jumpBFS(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return 0;

        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);

            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
            }
        }

        return jumps;
    }

    // Approach 2: Greedy - O(n) time, O(1) space
    public int jumpGreedy(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return 0;

        int jumps = 0;
        int currentMax = 0;
        int nextMax = 0;
        int i = 0;

        while (currentMax < n - 1) {
            jumps++;
            while (i <= currentMax) {
                nextMax = Math.max(nextMax, i + nums[i]);
                i++;
            }
            currentMax = nextMax;
        }

        return jumps;
    }

    // Approach 3: DP - O(n^2) time, O(n) space
    public int jumpDP(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 0; i < n; i++) {
            if (dp[i] == Integer.MAX_VALUE)
                continue;

            for (int j = 1; j <= nums[i] && i + j < n; j++) {
                dp[i + j] = Math.min(dp[i + j], dp[i] + 1);
            }
        }

        return dp[n - 1];
    }

    // Approach 4: Optimized DP - O(n) time, O(n) space
    public int jumpDPOptimized(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return 0;

        int[] minJumps = new int[n];
        Arrays.fill(minJumps, Integer.MAX_VALUE);
        minJumps[0] = 0;

        for (int i = 0; i < n; i++) {
            if (minJumps[i] == Integer.MAX_VALUE)
                continue;

            int maxReach = Math.min(i + nums[i], n - 1);
            for (int j = i + 1; j <= maxReach; j++) {
                if (minJumps[j] > minJumps[i] + 1) {
                    minJumps[j] = minJumps[i] + 1;
                }
            }
        }

        return minJumps[n - 1];
    }

    // Approach 5: Backtracking with path - O(n) time, O(n) space
    public int[] jumpWithPath(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return new int[] { 0 };

        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n - 1; i++) {
            int newFarthest = Math.max(farthest, i + nums[i]);

            // Update parent for all reachable positions
            for (int j = farthest + 1; j <= Math.min(newFarthest, n - 1); j++) {
                if (parent[j] == -1) {
                    parent[j] = i;
                }
            }

            farthest = newFarthest;

            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
            }
        }

        // Reconstruct path
        java.util.List<Integer> path = new java.util.ArrayList<>();
        int current = n - 1;
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }

        java.util.Collections.reverse(path);

        // Convert to array with jumps count
        int[] result = new int[path.size() + 1];
        result[0] = jumps;
        for (int i = 0; i < path.size(); i++) {
            result[i + 1] = path.get(i);
        }

        return result;
    }

    public static void main(String[] args) {
        JumpGameII solution = new JumpGameII();

        System.out.println("=== Jump Game II Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 2, 3, 1, 1, 4 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("BFS: " + solution.jumpBFS(nums1));
        System.out.println("Greedy: " + solution.jumpGreedy(nums1));
        System.out.println("DP: " + solution.jumpDP(nums1));
        System.out.println("DP Optimized: " + solution.jumpDPOptimized(nums1));
        int[] pathResult1 = solution.jumpWithPath(nums1);
        System.out.println("With Path - Jumps: " + pathResult1[0] + ", Path: " +
                Arrays.toString(Arrays.copyOfRange(pathResult1, 1, pathResult1.length)));
        System.out.println("Expected: 2\n");

        // Test Case 2: Single element
        int[] nums2 = { 0 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("BFS: " + solution.jumpBFS(nums2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Large jumps
        int[] nums3 = { 2, 3, 0, 1, 4 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("BFS: " + solution.jumpBFS(nums3));
        System.out.println("Greedy: " + solution.jumpGreedy(nums3));
        System.out.println("Expected: 2\n");

        // Test Case 4: All ones
        int[] nums4 = { 1, 1, 1, 1, 1 };
        System.out.println("Test 4 - Array: " + Arrays.toString(nums4));
        System.out.println("BFS: " + solution.jumpBFS(nums4));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        JumpGameII solution = new JumpGameII();

        int[] largeArray = new int[10000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = Math.max(1, (int) (Math.random() * 10));
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.jumpBFS(largeArray);
        long end = System.nanoTime();
        System.out.println("BFS: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.jumpGreedy(largeArray);
        end = System.nanoTime();
        System.out.println("Greedy: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.jumpDPOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("DP Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
