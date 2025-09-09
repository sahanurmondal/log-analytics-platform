package backtracking.medium;

import java.util.*;

/**
 * Variation: Combination Sum V (with constraints)
 * Similar to LeetCode 39/40 but with custom constraints
 * 
 * Company Tags: Amazon, Microsoft, Google
 * Difficulty: Medium
 * 
 * Given an array of positive integers nums and a target, return all unique
 * combinations where the chosen numbers sum to target. Each number may be used
 * at most twice.
 * 
 * Constraints:
 * - 1 <= nums.length <= 20
 * - 1 <= nums[i] <= 30
 * - 1 <= target <= 100
 * 
 * Follow-up:
 * - Can you solve it recursively?
 * - Can you generalize for at most k times?
 */
public class CombinationSumV {

    /**
     * Approach 1: Backtracking with Count Limit
     * Time: O(2^n * target) where n is length of nums
     * Space: O(target) for recursion depth
     */
    public List<List<Integer>> combinationSumV(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrack(result, new ArrayList<>(), nums, target, 0, new int[nums.length]);
        return result;
    }

    private void backtrack(List<List<Integer>> result, List<Integer> current,
            int[] nums, int target, int start, int[] count) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (target < 0)
            return;

        for (int i = start; i < nums.length; i++) {
            // Skip duplicates
            if (i > start && nums[i] == nums[i - 1])
                continue;

            // Each number can be used at most twice
            if (count[i] < 2) {
                current.add(nums[i]);
                count[i]++;
                backtrack(result, current, nums, target - nums[i], i, count);
                current.remove(current.size() - 1);
                count[i]--;
            }
        }
    }

    /**
     * Approach 2: Backtracking with K-limit generalization
     * Time: O(k^n * target)
     * Space: O(target)
     */
    public List<List<Integer>> combinationSumKLimit(int[] nums, int target, int k) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        backtrackKLimit(result, new ArrayList<>(), nums, target, 0, new int[nums.length], k);
        return result;
    }

    private void backtrackKLimit(List<List<Integer>> result, List<Integer> current,
            int[] nums, int target, int start, int[] count, int k) {
        if (target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (target < 0)
            return;

        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1])
                continue;

            if (count[i] < k) {
                current.add(nums[i]);
                count[i]++;
                backtrackKLimit(result, current, nums, target - nums[i], i, count, k);
                current.remove(current.size() - 1);
                count[i]--;
            }
        }
    }

    /**
     * Approach 3: Dynamic Programming with Count
     * Time: O(n * target * k)
     * Space: O(target * k)
     */
    public List<List<Integer>> combinationSumDP(int[] nums, int target) {
        Map<String, List<List<Integer>>> dp = new HashMap<>();
        return dpHelper(nums, target, 0, new int[nums.length], dp);
    }

    private List<List<Integer>> dpHelper(int[] nums, int target, int index,
            int[] count, Map<String, List<List<Integer>>> dp) {
        if (target == 0) {
            return Arrays.asList(new ArrayList<>());
        }

        if (target < 0 || index >= nums.length) {
            return new ArrayList<>();
        }

        String key = target + "," + index + "," + Arrays.toString(count);
        if (dp.containsKey(key)) {
            return dp.get(key);
        }

        List<List<Integer>> result = new ArrayList<>();

        // Don't use current number
        result.addAll(dpHelper(nums, target, index + 1, count, dp));

        // Use current number (up to 2 times)
        for (int use = 1; use <= Math.min(2, target / nums[index]); use++) {
            if (count[index] + use <= 2) {
                count[index] += use;
                List<List<Integer>> subResults = dpHelper(nums, target - nums[index] * use, index + 1, count, dp);
                for (List<Integer> subResult : subResults) {
                    List<Integer> newResult = new ArrayList<>();
                    for (int i = 0; i < use; i++) {
                        newResult.add(nums[index]);
                    }
                    newResult.addAll(subResult);
                    result.add(newResult);
                }
                count[index] -= use;
            }
        }

        dp.put(key, result);
        return result;
    }

    /**
     * Approach 4: Iterative with Queue
     * Time: O(2^n * target)
     * Space: O(2^n * target)
     */
    public List<List<Integer>> combinationSumIterative(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        Queue<State> queue = new LinkedList<>();
        queue.offer(new State(new ArrayList<>(), target, 0, new int[nums.length]));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.remaining == 0) {
                result.add(new ArrayList<>(current.path));
                continue;
            }

            if (current.remaining < 0 || current.index >= nums.length) {
                continue;
            }

            for (int i = current.index; i < nums.length; i++) {
                if (i > current.index && nums[i] == nums[i - 1])
                    continue;

                if (current.count[i] < 2) {
                    List<Integer> newPath = new ArrayList<>(current.path);
                    newPath.add(nums[i]);
                    int[] newCount = Arrays.copyOf(current.count, current.count.length);
                    newCount[i]++;
                    queue.offer(new State(newPath, current.remaining - nums[i], i, newCount));
                }
            }
        }

        return result;
    }

    static class State {
        List<Integer> path;
        int remaining;
        int index;
        int[] count;

        State(List<Integer> path, int remaining, int index, int[] count) {
            this.path = path;
            this.remaining = remaining;
            this.index = index;
            this.count = count;
        }
    }

    public static void main(String[] args) {
        CombinationSumV solution = new CombinationSumV();

        // Test Case 1: Basic example
        System.out.println("Test Case 1: nums=[2,3,5], target=8");
        List<List<Integer>> result1 = solution.combinationSumV(new int[] { 2, 3, 5 }, 8);
        System.out.println("Result: " + result1);

        // Test Case 2: Simple case
        System.out.println("\nTest Case 2: nums=[1,2], target=3");
        List<List<Integer>> result2 = solution.combinationSumV(new int[] { 1, 2 }, 3);
        System.out.println("Result: " + result2);

        // Test Case 3: All candidates same
        System.out.println("\nTest Case 3: nums=[2,2,2], target=4");
        System.out.println("Result: " + solution.combinationSumV(new int[] { 2, 2, 2 }, 4));

        // Test Case 4: K-limit generalization
        System.out.println("\nTest Case 4: K-limit with k=3, nums=[1,2], target=4");
        List<List<Integer>> result4 = solution.combinationSumKLimit(new int[] { 1, 2 }, 4, 3);
        System.out.println("Result: " + result4);

        // Test Case 5: No solution
        System.out.println("\nTest Case 5: nums=[5,6], target=3");
        System.out.println("Result: " + solution.combinationSumV(new int[] { 5, 6 }, 3));

        // Test Case 6: Compare approaches
        System.out.println("\nTest Case 6: Compare approaches");
        int[] nums = { 2, 3, 6, 7 };
        int target = 7;

        long start = System.nanoTime();
        List<List<Integer>> backtrack = solution.combinationSumV(nums, target);
        long backtrackTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<List<Integer>> iterative = solution.combinationSumIterative(nums, target);
        long iterativeTime = System.nanoTime() - start;

        System.out.println("Backtrack result count: " + backtrack.size());
        System.out.println("Iterative result count: " + iterative.size());
        System.out.println("Backtrack time: " + backtrackTime / 1_000_000.0 + " ms");
        System.out.println("Iterative time: " + iterativeTime / 1_000_000.0 + " ms");

        // Test Case 7: Large target
        System.out.println("\nTest Case 7: nums=[1,2], target=10");
        List<List<Integer>> result7 = solution.combinationSumV(new int[] { 1, 2 }, 10);
        System.out.println("Result count: " + result7.size());

        // Test Case 8: Edge case - single element
        System.out.println("\nTest Case 8: nums=[5], target=10");
        System.out.println("Result: " + solution.combinationSumV(new int[] { 5 }, 10));

        // Test Case 9: Duplicates handling
        System.out.println("\nTest Case 9: nums=[1,1,2], target=3");
        List<List<Integer>> result9 = solution.combinationSumV(new int[] { 1, 1, 2 }, 3);
        System.out.println("Result: " + result9);

        // Test Case 10: Performance test
        System.out.println("\nTest Case 10: Performance test");
        start = System.nanoTime();
        List<List<Integer>> perf = solution.combinationSumV(new int[] { 1, 2, 3, 4, 5 }, 15);
        long perfTime = System.nanoTime() - start;
        System.out.println("Performance result count: " + perf.size());
        System.out.println("Performance time: " + perfTime / 1_000_000.0 + " ms");

        // Test Cases 11-15: Additional comprehensive testing
        System.out.println("\nTest Case 11: Empty result validation");
        List<List<Integer>> empty = solution.combinationSumV(new int[] { 10, 20 }, 5);
        System.out.println("Empty result: " + empty.isEmpty());

        System.out.println("\nTest Case 12: Sum validation");
        boolean validSums = result1.stream().allMatch(list -> list.stream().mapToInt(Integer::intValue).sum() == 8);
        System.out.println("All sums valid: " + validSums);

        System.out.println("\nTest Case 13: Uniqueness check");
        Set<List<Integer>> unique = new HashSet<>(result1);
        System.out.println("All unique: " + (unique.size() == result1.size()));

        System.out.println("\nTest Case 14: Count limit validation");
        boolean validCounts = result9.stream().allMatch(list -> {
            Map<Integer, Integer> count = new HashMap<>();
            for (int num : list) {
                count.put(num, count.getOrDefault(num, 0) + 1);
            }
            return count.values().stream().allMatch(c -> c <= 2);
        });
        System.out.println("All count limits respected: " + validCounts);

        System.out.println("\nTest Case 15: Stress test with k=4");
        start = System.nanoTime();
        List<List<Integer>> stress = solution.combinationSumKLimit(new int[] { 1, 2, 3 }, 12, 4);
        long stressTime = System.nanoTime() - start;
        System.out.println("Stress test count: " + stress.size());
        System.out.println("Stress test time: " + stressTime / 1_000_000.0 + " ms");
    }
}
