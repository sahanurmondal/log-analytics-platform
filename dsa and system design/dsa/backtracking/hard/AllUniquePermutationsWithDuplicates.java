package backtracking.hard;

import java.util.*;

/**
 * Problem: All Unique Permutations With Duplicates (Hard)
 * 
 * Description:
 * Given a collection of numbers that might contain duplicates, return all
 * possible unique permutations of length k.
 * 
 * Companies: [Google, Amazon, Microsoft, Facebook, Apple]
 * 
 * Constraints:
 * - 1 <= nums.length <= 10
 * - -10 <= nums[i] <= 10
 * - 1 <= k <= nums.length
 * 
 * Example:
 * Input: nums = [1, 1, 2], k = 2
 * Output: [[1,1], [1,2], [2,1]]
 * 
 * Follow-up:
 * 1. Can you solve it recursively with optimal pruning?
 * 2. Can you generate permutations in lexicographical order?
 * 3. Can you optimize for large k?
 * 4. What if we need only the nth permutation?
 */
public class AllUniquePermutationsWithDuplicates {

    // Approach 1: Backtracking with Set - O(P(n,k)) time, O(k) space
    public List<List<Integer>> permuteUniqueK(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0 || k <= 0 || k > nums.length) {
            return result;
        }

        Arrays.sort(nums); // Sort to handle duplicates
        backtrack(nums, k, new ArrayList<>(), new boolean[nums.length], result);
        return result;
    }

    private void backtrack(int[] nums, int k, List<Integer> current, boolean[] used,
            List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            // Skip used elements and duplicates
            if (used[i] || (i > 0 && nums[i] == nums[i - 1] && !used[i - 1])) {
                continue;
            }

            used[i] = true;
            current.add(nums[i]);
            backtrack(nums, k, current, used, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }

    // Approach 2: Using Frequency Map - O(P(n,k)) time, O(n) space
    public List<List<Integer>> permuteUniqueKFreqMap(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0 || k <= 0 || k > nums.length) {
            return result;
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        backtrackFreqMap(k, freqMap, new ArrayList<>(), result);
        return result;
    }

    private void backtrackFreqMap(int k, Map<Integer, Integer> freqMap, List<Integer> current,
            List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            int num = entry.getKey();
            int count = entry.getValue();

            if (count == 0)
                continue;

            current.add(num);
            freqMap.put(num, count - 1);
            backtrackFreqMap(k, freqMap, current, result);
            freqMap.put(num, count);
            current.remove(current.size() - 1);
        }
    }

    // Approach 3: Next Permutation with Length K - O(P(n,k)) time, O(n) space
    public List<List<Integer>> permuteUniqueKIterative(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length == 0 || k <= 0 || k > nums.length) {
            return result;
        }

        Arrays.sort(nums);
        do {
            result.add(new ArrayList<>(Arrays.asList(Arrays.stream(nums).boxed()
                    .limit(k).toArray(Integer[]::new))));
        } while (nextPermutation(nums));

        return result;
    }

    private boolean nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1])
            i--;

        if (i < 0)
            return false;

        int j = nums.length - 1;
        while (nums[j] <= nums[i])
            j--;

        swap(nums, i, j);
        reverse(nums, i + 1);
        return true;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private void reverse(int[] nums, int start) {
        int i = start, j = nums.length - 1;
        while (i < j) {
            swap(nums, i++, j--);
        }
    }

    // Follow-up: Get nth permutation of length k
    public List<Integer> getNthPermutationK(int[] nums, int k, int n) {
        int len = nums.length;
        List<Integer> numbers = new ArrayList<>();
        int[] factorial = new int[len + 1];
        factorial[0] = 1;

        for (int i = 1; i <= len; i++) {
            factorial[i] = factorial[i - 1] * i;
        }

        for (int num : nums) {
            numbers.add(num);
        }
        Collections.sort(numbers);

        // Validate n is within bounds
        int totalPerms = factorial[len] / factorial[len - k];
        if (n > totalPerms)
            return new ArrayList<>();

        n--; // Convert to 0-based index
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            int index = n / factorial[len - 1 - i];
            n %= factorial[len - 1 - i];
            result.add(numbers.get(index));
            numbers.remove(index);
            len--;
        }

        return result;
    }

    public static void main(String[] args) {
        AllUniquePermutationsWithDuplicates solution = new AllUniquePermutationsWithDuplicates();

        // Test cases with expected results
        test(solution, new int[] { 1, 1, 2 }, 2, "Test 1: Basic case with duplicates");
        test(solution, new int[] { 1, 2, 3 }, 2, "Test 2: All unique elements");
        test(solution, new int[] { 1, 1, 1 }, 2, "Test 3: All same elements");
        test(solution, new int[] { 1, 2 }, 2, "Test 4: k equals array length");

        // Test frequency map approach
        System.out.println("Test 5 (FreqMap): " +
                solution.permuteUniqueKFreqMap(new int[] { 1, 1, 2 }, 2));

        // Test iterative approach
        System.out.println("Test 6 (Iterative): " +
                solution.permuteUniqueKIterative(new int[] { 1, 1, 2 }, 2));

        // Additional test cases
        test(solution, new int[] { 1, 1, 2, 2 }, 2, "Test 7: Multiple duplicates");
        test(solution, new int[] {}, 1, "Test 8: Empty array");

        // Test nth permutation
        System.out.println("Test 9 (Nth): " +
                solution.getNthPermutationK(new int[] { 1, 2, 3 }, 2, 2));

        test(solution, new int[] { -1, -1, 2 }, 2, "Test 10: Negative numbers");

        // Performance test
        performanceTest(solution);

        test(solution, new int[] { 1, 1, 2 }, 1, "Test 12: k = 1");

        // Consistency check
        consistencyCheck(solution);

        test(solution, new int[] { 1, 2 }, 3, "Test 14: k > array length");
        test(solution, new int[] { 1, 2, 3, 4 }, 4, "Test 15: Maximum valid k");
    }

    private static void test(AllUniquePermutationsWithDuplicates solution, int[] nums, int k, String message) {
        System.out.println(message + ": " + solution.permuteUniqueK(nums, k));
    }

    private static void performanceTest(AllUniquePermutationsWithDuplicates solution) {
        int[] largeInput = new int[10];
        for (int i = 0; i < 10; i++) {
            largeInput[i] = i % 3;
        }
        long start = System.currentTimeMillis();
        List<List<Integer>> result = solution.permuteUniqueK(largeInput, 4);
        long end = System.currentTimeMillis();
        System.out.println("Test 11 (Performance): " + result.size() +
                " permutations in " + (end - start) + "ms");
    }

    private static void consistencyCheck(AllUniquePermutationsWithDuplicates solution) {
        int[] test13 = { 1, 1, 2 };
        boolean consistent = solution.permuteUniqueK(test13, 2).size() == solution.permuteUniqueKFreqMap(test13, 2)
                .size();
        System.out.println("Test 13 (Consistency): " + consistent);
    }
}