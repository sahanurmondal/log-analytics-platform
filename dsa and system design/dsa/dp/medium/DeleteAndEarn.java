package dp.medium;

import java.util.*;

/**
 * LeetCode 740: Delete and Earn
 * https://leetcode.com/problems/delete-and-earn/
 *
 * Description:
 * You are given an integer array nums. You want to maximize the number of
 * points you get by performing the following operation any number of times:
 * - Pick any nums[i] and delete it to earn nums[i] points. Afterwards, you must
 * delete every element equal to nums[i] - 1 and every element equal to nums[i]
 * + 1.
 * Return the maximum number of points you can earn by applying the above
 * operation some number of times.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - What if we need to track which numbers to delete?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class DeleteAndEarn {

    // Approach 1: Transform to House Robber - O(n + k) time, O(k) space where k =
    // max(nums)
    public int deleteAndEarn(int[] nums) {
        if (nums.length == 0)
            return 0;

        int maxNum = Arrays.stream(nums).max().orElse(0);
        int[] counts = new int[maxNum + 1];

        // Count frequency and calculate total points for each number
        for (int num : nums) {
            counts[num] += num;
        }

        // Apply House Robber logic
        int prev2 = 0, prev1 = 0;

        for (int i = 0; i <= maxNum; i++) {
            int current = Math.max(prev1, prev2 + counts[i]);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 2: Map-based DP - O(n log k) time, O(k) space
    public int deleteAndEarnMap(int[] nums) {
        Map<Integer, Integer> points = new HashMap<>();

        // Calculate total points for each unique number
        for (int num : nums) {
            points.put(num, points.getOrDefault(num, 0) + num);
        }

        List<Integer> sortedNums = new ArrayList<>(points.keySet());
        Collections.sort(sortedNums);

        int prev2 = 0, prev1 = 0;

        for (int i = 0; i < sortedNums.size(); i++) {
            int num = sortedNums.get(i);
            int currentPoints = points.get(num);

            // Check if current number is consecutive to previous
            if (i > 0 && sortedNums.get(i - 1) == num - 1) {
                int current = Math.max(prev1, prev2 + currentPoints);
                prev2 = prev1;
                prev1 = current;
            } else {
                // Not consecutive, we can take both
                prev2 = prev1;
                prev1 = prev1 + currentPoints;
            }
        }

        return prev1;
    }

    // Approach 3: Memoization - O(n * k) time, O(n * k) space
    public int deleteAndEarnMemo(int[] nums) {
        Map<Integer, Integer> points = new HashMap<>();
        for (int num : nums) {
            points.put(num, points.getOrDefault(num, 0) + num);
        }

        Set<Integer> uniqueNums = points.keySet();
        Map<Integer, Integer> memo = new HashMap<>();

        int maxPoints = 0;
        for (int num : uniqueNums) {
            maxPoints = Math.max(maxPoints, dfs(num, points, uniqueNums, memo));
        }

        return maxPoints;
    }

    private int dfs(int num, Map<Integer, Integer> points, Set<Integer> uniqueNums, Map<Integer, Integer> memo) {
        if (memo.containsKey(num))
            return memo.get(num);

        int currentPoints = points.get(num);
        int maxFuture = 0;

        // Try all numbers that are not adjacent
        for (int next : uniqueNums) {
            if (next != num - 1 && next != num + 1 && next != num) {
                maxFuture = Math.max(maxFuture, dfs(next, points, uniqueNums, memo));
            }
        }

        int result = currentPoints + maxFuture;
        memo.put(num, result);
        return result;
    }

    // Approach 4: Optimized with TreeMap - O(n log k) time, O(k) space
    public int deleteAndEarnTreeMap(int[] nums) {
        TreeMap<Integer, Integer> points = new TreeMap<>();

        for (int num : nums) {
            points.put(num, points.getOrDefault(num, 0) + num);
        }

        int prev2 = 0, prev1 = 0;
        Integer prevKey = null;

        for (Map.Entry<Integer, Integer> entry : points.entrySet()) {
            int num = entry.getKey();
            int currentPoints = entry.getValue();

            if (prevKey != null && prevKey == num - 1) {
                // Consecutive numbers - apply House Robber logic
                int current = Math.max(prev1, prev2 + currentPoints);
                prev2 = prev1;
                prev1 = current;
            } else {
                // Not consecutive - can take both
                prev2 = prev1;
                prev1 = prev1 + currentPoints;
            }

            prevKey = num;
        }

        return prev1;
    }

    // Approach 5: Get Optimal Selection - O(n + k) time, O(k) space
    public List<Integer> getOptimalSelection(int[] nums) {
        if (nums.length == 0)
            return new ArrayList<>();

        int maxNum = Arrays.stream(nums).max().orElse(0);
        int[] counts = new int[maxNum + 1];

        for (int num : nums) {
            counts[num] += num;
        }

        // DP with selection tracking
        int[] dp = new int[maxNum + 1];
        boolean[] selected = new boolean[maxNum + 1];

        dp[0] = 0;
        for (int i = 1; i <= maxNum; i++) {
            if (dp[i - 1] > (i >= 2 ? dp[i - 2] : 0) + counts[i]) {
                dp[i] = dp[i - 1];
                selected[i] = false;
            } else {
                dp[i] = (i >= 2 ? dp[i - 2] : 0) + counts[i];
                selected[i] = counts[i] > 0;
            }
        }

        // Reconstruct selection
        List<Integer> result = new ArrayList<>();
        for (int i = maxNum; i >= 1; i--) {
            if (selected[i] && counts[i] > 0) {
                result.add(i);
                i--; // Skip next number due to constraint
            }
        }

        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        DeleteAndEarn solution = new DeleteAndEarn();

        System.out.println("=== Delete and Earn Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 3, 4, 2 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("House Robber: " + solution.deleteAndEarn(nums1));
        System.out.println("Map DP: " + solution.deleteAndEarnMap(nums1));
        System.out.println("TreeMap: " + solution.deleteAndEarnTreeMap(nums1));
        System.out.println("Optimal Selection: " + solution.getOptimalSelection(nums1));
        System.out.println("Expected: 6\n");

        // Test Case 2: Another example
        int[] nums2 = { 2, 2, 3, 3, 3, 4 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("House Robber: " + solution.deleteAndEarn(nums2));
        System.out.println("Expected: 9\n");

        // Test Case 3: Large gaps
        int[] nums3 = { 1, 1, 1, 2, 4, 5, 5, 5, 6 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("Map DP: " + solution.deleteAndEarnMap(nums3));
        System.out.println("Expected: 18\n");

        performanceTest();
    }

    private static void performanceTest() {
        DeleteAndEarn solution = new DeleteAndEarn();

        int[] largeArray = new int[20000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 10000) + 1;
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.deleteAndEarn(largeArray);
        long end = System.nanoTime();
        System.out.println("House Robber: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.deleteAndEarnMap(largeArray);
        end = System.nanoTime();
        System.out.println("Map DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.deleteAndEarnTreeMap(largeArray);
        end = System.nanoTime();
        System.out.println("TreeMap: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
