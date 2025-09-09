package arrays.easy;

/**
 * LeetCode 217: Contains Duplicate
 * https://leetcode.com/problems/contains-duplicate/
 *
 * Given an integer array nums, return true if any value appears at least
 * twice in the array, and return false if every element is distinct.
 *
 * Input / Output examples:
 * - containsDuplicate([1,2,3,1]) -> true
 * - containsDuplicate([1,2,3,4]) -> false
 * - containsDuplicate([]) -> false
 *
 * Follow-ups / discussion points:
 * - If elements are in a limited range, a boolean array or counting sort can be
 * used.
 * - For streaming or very large datasets that don't fit in memory, use external
 * sorting or a probabilistic structure (Bloom filter) with caveats.
 * - For memory-constrained environments, sort in place (if allowed) and scan.
 *
 * Time / Space:
 * - HashSet approach: O(n) time, O(n) space
 * - Sorting approach: O(n log n) time, O(1) extra space (if sorting in place)
 */
public class ContainsDuplicate {

    /**
     * HashSet approach: return true if any duplicate is found while inserting
     * into a set. Handles null/empty inputs by returning false.
     *
     * Time: O(n) on average
     * Space: O(n)
     */
    public boolean containsDuplicate(int[] nums) {
        if (nums == null || nums.length < 2)
            return false;
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        for (int v : nums) {
            if (!seen.add(v))
                return true;
        }
        return false;
    }

    /**
     * Sorting approach: sort a copy and check adjacent elements for equality.
     * Useful when you want to minimize extra memory (sort in place if allowed).
     *
     * Time: O(n log n)
     * Space: O(n) for the copy, O(1) extra if sorting in place
     */
    public boolean containsDuplicateSorting(int[] nums) {
        if (nums == null || nums.length < 2)
            return false;
        int[] a = java.util.Arrays.copyOf(nums, nums.length);
        java.util.Arrays.sort(a);
        for (int i = 1; i < a.length; i++) {
            if (a[i] == a[i - 1])
                return true;
        }
        return false;
    }

    /**
     * Small utility to run a single test and format the result.
     */
    private static void runTest(String name, ContainsDuplicate solver, int[] arr) {
        System.out.println(name + " -> containsDuplicate (set): " + solver.containsDuplicate(arr)
                + ", containsDuplicateSorting: " + solver.containsDuplicateSorting(arr));
    }

    public static void main(String[] args) {
        ContainsDuplicate sol = new ContainsDuplicate();

        // Basic examples
        runTest("example1 [1,2,3,1]", sol, new int[] { 1, 2, 3, 1 }); // true
        runTest("example2 [1,2,3,4]", sol, new int[] { 1, 2, 3, 4 }); // false

        // Edge cases
        runTest("empty []", sol, new int[] {}); // false
        runTest("single [42]", sol, new int[] { 42 }); // false
        runTest("all same [7,7,7]", sol, new int[] { 7, 7, 7 }); // true

        // Larger case: duplicate at end
        runTest("large duplicate at end", sol, new int[] { 1, 2, 3, 4, 5, 6, 1 }); // true

        // Negative and mixed values
        runTest("mixed negatives", sol, new int[] { -1, 0, 1, -1 }); // true

        // Null input (handled gracefully)
        System.out.println("null input -> " + sol.containsDuplicate(null));
    }
}
