package dp.hard;

import java.util.*;

/**
 * LeetCode 312: Burst Balloons
 * https://leetcode.com/problems/burst-balloons/
 *
 * Description:
 * You are given n balloons, indexed from 0 to n - 1. Each balloon is painted
 * with a number on it represented by an array nums.
 * You are asked to burst all the balloons. If you burst the ith balloon, you
 * will get nums[i - 1] * nums[i] * nums[i + 1] coins.
 * If i - 1 or i + 1 goes out of bounds of the array, then treat it as if there
 * is a balloon with a 1 painted on it.
 * Return the maximum coins you can collect by bursting the balloons wisely.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 300
 * - 0 <= nums[i] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(n^3) time?
 * - What if we need to track the optimal bursting sequence?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class BurstBalloons {

    // Approach 1: Interval DP (Bottom-up) - O(n^3) time, O(n^2) space
    public int maxCoins(int[] nums) {
        int n = nums.length;
        // Add boundary balloons with value 1
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        // dp[i][j] = max coins from bursting balloons between i and j (exclusive)
        int[][] dp = new int[n + 2][n + 2];

        // Length of interval
        for (int len = 2; len <= n + 1; len++) {
            for (int i = 0; i <= n + 1 - len; i++) {
                int j = i + len;

                // Try bursting each balloon k as the last one in interval (i, j)
                for (int k = i + 1; k < j; k++) {
                    int coins = balloons[i] * balloons[k] * balloons[j];
                    dp[i][j] = Math.max(dp[i][j], dp[i][k] + coins + dp[k][j]);
                }
            }
        }

        return dp[0][n + 1];
    }

    // Approach 2: Top-down DP with Memoization - O(n^3) time, O(n^2) space
    public int maxCoinsMemo(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        Integer[][] memo = new Integer[n + 2][n + 2];
        return maxCoinsHelper(balloons, 0, n + 1, memo);
    }

    private int maxCoinsHelper(int[] balloons, int left, int right, Integer[][] memo) {
        if (left + 1 >= right)
            return 0;

        if (memo[left][right] != null)
            return memo[left][right];

        int maxCoins = 0;
        for (int k = left + 1; k < right; k++) {
            int coins = balloons[left] * balloons[k] * balloons[right];
            int total = maxCoinsHelper(balloons, left, k, memo) + coins +
                    maxCoinsHelper(balloons, k, right, memo);
            maxCoins = Math.max(maxCoins, total);
        }

        memo[left][right] = maxCoins;
        return maxCoins;
    }

    // Approach 3: Divide and Conquer - O(n^3) time, O(n^2) space
    public int maxCoinsDivideConquer(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        return divideConquer(balloons, 0, n + 1, new HashMap<>());
    }

    private int divideConquer(int[] balloons, int left, int right, Map<String, Integer> cache) {
        if (left + 1 >= right)
            return 0;

        String key = left + "," + right;
        if (cache.containsKey(key))
            return cache.get(key);

        int result = 0;
        for (int k = left + 1; k < right; k++) {
            int coins = balloons[left] * balloons[k] * balloons[right];
            int leftCoins = divideConquer(balloons, left, k, cache);
            int rightCoins = divideConquer(balloons, k, right, cache);
            result = Math.max(result, leftCoins + coins + rightCoins);
        }

        cache.put(key, result);
        return result;
    }

    // Approach 4: Get Optimal Bursting Sequence - O(n^3) time, O(n^2) space
    public List<Integer> getOptimalSequence(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];
        int[][] choice = new int[n + 2][n + 2];

        // Fill DP table and track choices
        for (int len = 2; len <= n + 1; len++) {
            for (int i = 0; i <= n + 1 - len; i++) {
                int j = i + len;

                for (int k = i + 1; k < j; k++) {
                    int coins = balloons[i] * balloons[k] * balloons[j];
                    int total = dp[i][k] + coins + dp[k][j];

                    if (total > dp[i][j]) {
                        dp[i][j] = total;
                        choice[i][j] = k;
                    }
                }
            }
        }

        // Reconstruct sequence
        List<Integer> sequence = new ArrayList<>();
        reconstructSequence(choice, 0, n + 1, sequence);

        // Convert back to original indices
        for (int i = 0; i < sequence.size(); i++) {
            sequence.set(i, sequence.get(i) - 1);
        }

        return sequence;
    }

    private void reconstructSequence(int[][] choice, int left, int right, List<Integer> sequence) {
        if (left + 1 >= right)
            return;

        int k = choice[left][right];
        sequence.add(k);

        reconstructSequence(choice, left, k, sequence);
        reconstructSequence(choice, k, right, sequence);
    }

    // Approach 5: Iterative with Stack - O(n^3) time, O(n^2) space
    public int maxCoinsIterative(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;
        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        // Use stack to simulate recursion iteratively
        Stack<int[]> stack = new Stack<>();
        Map<String, Integer> computed = new HashMap<>();

        stack.push(new int[] { 0, n + 1 });

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int left = current[0], right = current[1];

            String key = left + "," + right;
            if (computed.containsKey(key))
                continue;

            if (left + 1 >= right) {
                computed.put(key, 0);
                continue;
            }

            boolean allSubproblemsComputed = true;
            int maxCoins = 0;

            for (int k = left + 1; k < right; k++) {
                String leftKey = left + "," + k;
                String rightKey = k + "," + right;

                if (!computed.containsKey(leftKey)) {
                    stack.push(new int[] { left, k });
                    allSubproblemsComputed = false;
                }

                if (!computed.containsKey(rightKey)) {
                    stack.push(new int[] { k, right });
                    allSubproblemsComputed = false;
                }

                if (computed.containsKey(leftKey) && computed.containsKey(rightKey)) {
                    int coins = balloons[left] * balloons[k] * balloons[right];
                    int total = computed.get(leftKey) + coins + computed.get(rightKey);
                    maxCoins = Math.max(maxCoins, total);
                }
            }

            if (allSubproblemsComputed) {
                computed.put(key, maxCoins);
            } else {
                stack.push(current); // Put back for later processing
            }
        }

        return computed.get("0," + (n + 1));
    }

    public static void main(String[] args) {
        BurstBalloons solution = new BurstBalloons();

        System.out.println("=== Burst Balloons Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 3, 1, 5, 8 };
        System.out.println("Test 1 - Balloons: " + Arrays.toString(nums1));
        System.out.println("Interval DP: " + solution.maxCoins(nums1));
        System.out.println("Memoization: " + solution.maxCoinsMemo(nums1));
        System.out.println("Divide & Conquer: " + solution.maxCoinsDivideConquer(nums1));
        System.out.println("Optimal Sequence: " + solution.getOptimalSequence(nums1));
        System.out.println("Expected: 167\n");

        // Test Case 2: Single balloon
        int[] nums2 = { 1, 5 };
        System.out.println("Test 2 - Balloons: " + Arrays.toString(nums2));
        System.out.println("Interval DP: " + solution.maxCoins(nums2));
        System.out.println("Expected: 10\n");

        // Test Case 3: All same values
        int[] nums3 = { 3, 3, 3 };
        System.out.println("Test 3 - Balloons: " + Arrays.toString(nums3));
        System.out.println("Interval DP: " + solution.maxCoins(nums3));
        System.out.println("Expected: 45\n");

        performanceTest();
    }

    private static void performanceTest() {
        BurstBalloons solution = new BurstBalloons();

        int[] largeNums = new int[100];
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = (int) (Math.random() * 100) + 1;
        }

        System.out.println("=== Performance Test (Array size: " + largeNums.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxCoins(largeNums);
        long end = System.nanoTime();
        System.out.println("Interval DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxCoinsMemo(largeNums);
        end = System.nanoTime();
        System.out.println("Memoization: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxCoinsDivideConquer(largeNums);
        end = System.nanoTime();
        System.out.println("Divide & Conquer: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
