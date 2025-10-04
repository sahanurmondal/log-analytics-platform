package arrays.easy;

/**
 * LeetCode 448: Find All Numbers Disappeared in an Array
 * https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/
 *
 * Given an array nums of n integers where nums[i] is in the range [1, n],
 * return an array of all the integers in the range [1, n] that do not appear in
 * nums.
 *
 * Example:
 * - findDisappearedNumbers([4,3,2,7,8,2,3,1]) -> [5,6]
 *
 * Constraints: nums.length == n, 1 <= nums[i] <= n
 *
 * Discussion / Follow-ups:
 * - Best time: O(n), best extra space: O(1) (disregarding output) using
 * index-marking.
 * - If input must not be mutated, use a HashSet or copy the array first.
 * - For streaming or external memory, use hashing on chunks or sort externally.
 */
public class FindAllNumbersDisappearedInArray {

    /**
     * In-place marking approach (negation). Mutates the input array.
     * Time: O(n), Space: O(1) extra (excluding output list)
     *
     * Note: This method will change the sign of elements in nums. If the caller
     * must preserve nums, pass a copy to this method.
     */
    public java.util.List<Integer> findDisappearedNumbers(int[] nums) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;

        int n = nums.length;
        for (int i = 0; i < n; i++) {
            int val = Math.abs(nums[i]);
            int idx = val - 1;
            if (nums[idx] > 0)
                nums[idx] = -nums[idx];
        }

        for (int i = 0; i < n; i++) {
            if (nums[i] > 0)
                res.add(i + 1);
        }
        return res;
    }

    /**
     * HashSet approach: collect present numbers then scan 1..n for missing ones.
     * Time: O(n), Space: O(n)
     */
    public java.util.List<Integer> findDisappearedNumbersUsingSet(int[] nums) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (int v : nums)
            seen.add(v);
        for (int i = 1; i <= nums.length; i++) {
            if (!seen.contains(i))
                res.add(i);
        }
        return res;
    }

    /**
     * Cyclic sort approach: place each number at its correct index then collect
     * indices that don't match. This mutates the input array.
     * Time: O(n), Space: O(1) extra
     */
    public java.util.List<Integer> findDisappearedNumbersCyclicSort(int[] nums) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;
        int i = 0;
        while (i < nums.length) {
            int correct = nums[i] - 1;
            if (nums[i] >= 1 && nums[i] <= nums.length && nums[i] != nums[correct]) {
                int tmp = nums[i];
                nums[i] = nums[correct];
                nums[correct] = tmp;
            } else {
                i++;
            }
        }
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != j + 1)
                res.add(j + 1);
        }
        return res;
    }

    private static void runTest(String name, FindAllNumbersDisappearedInArray solver, int[] arr) {
        // Copy inputs where methods mutate the array to keep test outputs comparable
        int[] copy1 = java.util.Arrays.copyOf(arr, arr.length);
        int[] copy2 = java.util.Arrays.copyOf(arr, arr.length);
        int[] copy3 = java.util.Arrays.copyOf(arr, arr.length);
        System.out.println(name + " -> in-place: " + solver.findDisappearedNumbers(copy1)
                + ", set: " + solver.findDisappearedNumbersUsingSet(copy2)
                + ", cyclic: " + solver.findDisappearedNumbersCyclicSort(copy3));
    }

    public static void main(String[] args) {
        FindAllNumbersDisappearedInArray sol = new FindAllNumbersDisappearedInArray();

        // Examples
        runTest("example1 [4,3,2,7,8,2,3,1]", sol, new int[] { 4, 3, 2, 7, 8, 2, 3, 1 }); // [5,6]

        // Edge cases
        runTest("empty []", sol, new int[] {}); // []
        runTest("single [1]", sol, new int[] { 1 }); // []
        runTest("single missing [2] (n=1 invalid but test) ", sol, new int[] { 2 }); // behavior depends on constraints
        runTest("all present [1,2,3,4]", sol, new int[] { 1, 2, 3, 4 }); // []
        runTest("all duplicates [2,2,2,2]", sol, new int[] { 2, 2, 2, 2 }); // [1,3,4]

        // Large-ish test
        int n = 20;
        int[] arr = new int[n];
        for (int i = 0; i < n; i++)
            arr[i] = (i % 5) + 1; // repeats 1..5
        runTest("repeating 1..5 in 20 slots", sol, arr);
    }
}
