package dp.medium;

import java.util.*;

/**
 * LeetCode 377: Combination Sum IV
 * https://leetcode.com/problems/combination-sum-iv/
 *
 * Description:
 * Given an array of distinct integers nums and a target integer target, return
 * the number of possible combinations that add up to target.
 * The test cases are generated so that the answer can fit in a 32-bit integer.
 *
 * Constraints:
 * - 1 <= nums.length <= 200
 * - 1 <= nums[i] <= 1000
 * - All the elements of nums are unique.
 * - 1 <= target <= 1000
 *
 * Follow-up:
 * - What if negative numbers are allowed in the given array?
 * - How does it change the problem?
 * - What limitation we need to add to the question to allow negative numbers?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class CombinationSumIV {

    // Approach 1: Bottom-up DP - O(target * nums.length) time, O(target) space
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1; // One way to make 0: use no numbers

        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) {
                    dp[i] += dp[i - num];
                }
            }
        }

        return dp[target];
    }

    // Approach 2: Top-down DP with Memoization - O(target * nums.length) time,
    // O(target) space
    public int combinationSum4Memo(int[] nums, int target) {
        Integer[] memo = new Integer[target + 1];
        return combinationSum4Helper(nums, target, memo);
    }

    private int combinationSum4Helper(int[] nums, int target, Integer[] memo) {
        if (target == 0)
            return 1;
        if (target < 0)
            return 0;

        if (memo[target] != null)
            return memo[target];

        int result = 0;
        for (int num : nums) {
            result += combinationSum4Helper(nums, target - num, memo);
        }

        memo[target] = result;
        return result;
    }

    // Approach 3: Optimized with Sorting - O(target * nums.length) time, O(target)
    // space
    public int combinationSum4Optimized(int[] nums, int target) {
        Arrays.sort(nums);
        int[] dp = new int[target + 1];
        dp[0] = 1;

        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (num > i)
                    break; // Early termination due to sorting
                dp[i] += dp[i - num];
            }
        }

        return dp[target];
    }

    // Approach 4: BFS Approach - O(target * nums.length) time, O(target) space
    public int combinationSum4BFS(int[] nums, int target) {
        Map<Integer, Integer> ways = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(0);
        ways.put(0, 1);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int num : nums) {
                int next = current + num;
                if (next <= target) {
                    ways.put(next, ways.getOrDefault(next, 0) + ways.get(current));

                    if (!ways.containsKey(next) || ways.get(next) == ways.get(current)) {
                        queue.offer(next);
                    }
                }
            }
        }

        return ways.getOrDefault(target, 0);
    }

    // Approach 5: Get All Combinations - O(exponential) time, O(exponential) space
    public List<List<Integer>> getAllCombinations(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        getAllCombinationsHelper(nums, target, current, result);
        return result;
    }

    private void getAllCombinationsHelper(int[] nums, int target, List<Integer> current, List<List<Integer>> result) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (target < 0)
            return;

        for (int num : nums) {
            current.add(num);
            getAllCombinationsHelper(nums, target - num, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        CombinationSumIV solution = new CombinationSumIV();

        System.out.println("=== Combination Sum IV Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 2, 3 };
        int target1 = 4;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", target: " + target1);
        System.out.println("Bottom-up DP: " + solution.combinationSum4(nums1, target1));
        System.out.println("Top-down DP: " + solution.combinationSum4Memo(nums1, target1));
        System.out.println("Optimized: " + solution.combinationSum4Optimized(nums1, target1));
        System.out.println("BFS: " + solution.combinationSum4BFS(nums1, target1));

        List<List<Integer>> combinations1 = solution.getAllCombinations(nums1, target1);
        System.out.println("All combinations (" + combinations1.size() + " total):");
        for (List<Integer> combo : combinations1) {
            System.out.println("  " + combo);
        }
        System.out.println("Expected: 7\n");

        // Test Case 2: Single element
        int[] nums2 = { 9 };
        int target2 = 3;
        System.out.println("Test 2 - nums: " + Arrays.toString(nums2) + ", target: " + target2);
        System.out.println("Bottom-up DP: " + solution.combinationSum4(nums2, target2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Target equals element
        int[] nums3 = { 1, 2, 3 };
        int target3 = 1;
        System.out.println("Test 3 - nums: " + Arrays.toString(nums3) + ", target: " + target3);
        System.out.println("Bottom-up DP: " + solution.combinationSum4(nums3, target3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        CombinationSumIV solution = new CombinationSumIV();

        int[] nums = new int[50];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = i + 1;
        }
        int target = 100;

        System.out.println("=== Performance Test (nums length: " + nums.length + ", target: " + target + ") ===");

        long start = System.nanoTime();
        int result1 = solution.combinationSum4(nums, target);
        long end = System.nanoTime();
        System.out.println("Bottom-up DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.combinationSum4Memo(nums, target);
        end = System.nanoTime();
        System.out.println("Top-down DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.combinationSum4Optimized(nums, target);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
