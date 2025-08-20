package backtracking.medium;

import java.util.*;

/**
 * LeetCode 377: Combination Sum IV
 * https://leetcode.com/problems/combination-sum-iv/
 * 
 * Company Tags: Google, Facebook, Amazon
 * Difficulty: Medium
 * 
 * Given an array of distinct integers nums and a target integer target, return
 * the number of possible combinations that add up to target.
 * 
 * The test cases are generated so that the answer can fit in a 32-bit integer.
 * 
 * Example 1:
 * Input: nums = [1,2,3], target = 4
 * Output: 7
 * Explanation: The possible combinations are:
 * (1, 1, 1, 1), (1, 1, 2), (1, 2, 1), (1, 3), (2, 1, 1), (2, 2), (3, 1)
 * 
 * Constraints:
 * - 1 <= nums.length <= 200
 * - 1 <= nums[i] <= 1000
 * - All elements of nums are unique
 * - 1 <= target <= 1000
 */
public class CombinationSumIV {
    
    /**
     * Approach 1: Dynamic Programming (Bottom-up)
     * Time: O(target * nums.length)
     * Space: O(target)
     */
    public int combinationSum4(int[] nums, int target) {
        int[] dp = new int[target + 1];
        dp[0] = 1; // Base case: one way to make 0 (empty combination)
        
        for (int i = 1; i <= target; i++) {
            for (int num : nums) {
                if (i >= num) {
                    dp[i] += dp[i - num];
                }
            }
        }
        
        return dp[target];
    }
    
    /**
     * Approach 2: Recursion with Memoization (Top-down)
     * Time: O(target * nums.length)
     * Space: O(target) for memoization + O(target) for recursion stack
     */
    public int combinationSum4Memo(int[] nums, int target) {
        Map<Integer, Integer> memo = new HashMap<>();
        return backtrack(nums, target, memo);
    }
    
    private int backtrack(int[] nums, int target, Map<Integer, Integer> memo) {
        if (target == 0) return 1;
        if (target < 0) return 0;
        
        if (memo.containsKey(target)) {
            return memo.get(target);
        }
        
        int result = 0;
        for (int num : nums) {
            result += backtrack(nums, target - num, memo);
        }
        
        memo.put(target, result);
        return result;
    }
    
    /**
     * Approach 3: Pure Backtracking (for understanding, inefficient)
     * Time: O(nums.length^target) - exponential
     * Space: O(target) for recursion stack
     */
    public int combinationSum4Backtrack(int[] nums, int target) {
        if (target == 0) return 1;
        if (target < 0) return 0;
        
        int result = 0;
        for (int num : nums) {
            result += combinationSum4Backtrack(nums, target - num);
        }
        
        return result;
    }
    
    /**
     * Approach 4: BFS (Breadth-First Search)
     * Time: O(target * nums.length)
     * Space: O(target)
     */
    public int combinationSum4BFS(int[] nums, int target) {
        Map<Integer, Integer> dp = new HashMap<>();
        dp.put(0, 1);
        
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(0);
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            
            for (int num : nums) {
                int next = current + num;
                if (next <= target) {
                    dp.put(next, dp.getOrDefault(next, 0) + dp.get(current));
                    if (!dp.containsKey(next - dp.get(current))) {
                        queue.offer(next);
                    }
                }
            }
        }
        
        return dp.getOrDefault(target, 0);
    }

    public static void main(String[] args) {
        CombinationSumIV solution = new CombinationSumIV();
        
        // Test Case 1: Basic example
        System.out.println("Test Case 1: nums=[1,2,3], target=4");
        int result1 = solution.combinationSum4(new int[]{1, 2, 3}, 4);
        System.out.println("Result: " + result1); // Expected: 7
        
        // Test Case 2: No solution
        System.out.println("\nTest Case 2: nums=[9], target=3");
        System.out.println("Result: " + solution.combinationSum4(new int[]{9}, 3)); // Expected: 0
        
        // Test Case 3: Multiple solutions
        System.out.println("\nTest Case 3: nums=[2,3,5], target=8");
        System.out.println("Result: " + solution.combinationSum4(new int[]{2, 3, 5}, 8)); // Expected: 6
        
        // Test Case 4: Target is 0
        System.out.println("\nTest Case 4: nums=[1,2,3], target=0");
        System.out.println("Result: " + solution.combinationSum4(new int[]{1, 2, 3}, 0)); // Expected: 1
        
        // Test Case 5: Single element array
        System.out.println("\nTest Case 5: nums=[1], target=5");
        System.out.println("Result: " + solution.combinationSum4(new int[]{1}, 5)); // Expected: 1
        
        // Test Case 6: Compare approaches
        System.out.println("\nTest Case 6: Compare approaches for nums=[1,2,3], target=10");
        int[] nums = {1, 2, 3};
        int target = 10;
        
        long start = System.nanoTime();
        int dp_result = solution.combinationSum4(nums, target);
        long dp_time = System.nanoTime() - start;
        
        start = System.nanoTime();
        int memo_result = solution.combinationSum4Memo(nums, target);
        long memo_time = System.nanoTime() - start;
        
        System.out.println("DP Result: " + dp_result);
        System.out.println("Memo Result: " + memo_result);
        System.out.println("Results match: " + (dp_result == memo_result));
        System.out.println("DP Time: " + dp_time / 1_000_000.0 + " ms");
        System.out.println("Memo Time: " + memo_time / 1_000_000.0 + " ms");
        
        // Test Case 7: Large target
        System.out.println("\nTest Case 7: Large target nums=[1,2], target=20");
        int result7 = solution.combinationSum4(new int[]{1, 2}, 20);
        System.out.println("Result: " + result7);
        
        // Test Case 8: Edge case - large numbers
        System.out.println("\nTest Case 8: nums=[10,20,30], target=40");
        System.out.println("Result: " + solution.combinationSum4(new int[]{10, 20, 30}, 40));
        
        // Test Case 9: Performance test
        System.out.println("\nTest Case 9: Performance test nums=[1,2,3,4], target=15");
        start = System.nanoTime();
        int perf_result = solution.combinationSum4(new int[]{1, 2, 3, 4}, 15);
        long perf_time = System.nanoTime() - start;
        System.out.println("Result: " + perf_result);
        System.out.println("Time: " + perf_time / 1_000_000.0 + " ms");
        
        // Test Case 10: Memory usage
        System.out.println("\nTest Case 10: Memory test");
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        solution.combinationSum4(new int[]{1, 2, 3, 4, 5}, 25);
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used: " + (afterMemory - beforeMemory) / 1024.0 + " KB");
        
        // Test Cases 11-15: Additional validation
        System.out.println("\nTest Case 11: Validation - order matters");
        // [1,2,3] target 3 should give: (1,1,1), (1,2), (2,1), (3) = 4 ways
        int validation = solution.combinationSum4(new int[]{1, 2, 3}, 3);
        System.out.println("Result for target 3: " + validation + " (Expected: 4)");
        
        System.out.println("\nTest Case 12: BFS approach");
        int bfs_result = solution.combinationSum4BFS(new int[]{1, 2, 3}, 4);
        System.out.println("BFS Result: " + bfs_result + " (Expected: 7)");
        
        System.out.println("\nTest Case 13: Stress test");
        start = System.nanoTime();
        int stress_result = solution.combinationSum4(new int[]{1, 2, 3, 4, 5}, 30);
        long stress_time = System.nanoTime() - start;
        System.out.println("Stress result: " + stress_result);
        System.out.println("Stress time: " + stress_time / 1_000_000.0 + " ms");
        
        System.out.println("\nTest Case 14: Edge - single number equals target");
        System.out.println("Result: " + solution.combinationSum4(new int[]{5}, 5)); // Expected: 1
        
        System.out.println("\nTest Case 15: Edge - all numbers greater than target");
        System.out.println("Result: " + solution.combinationSum4(new int[]{5, 6, 7}, 4)); // Expected: 0
    }
}
