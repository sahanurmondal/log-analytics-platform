package arrays.easy;

/**
 * LeetCode 350: Intersection of Two Arrays II
 * https://leetcode.com/problems/intersection-of-two-arrays-ii/
 *
 * Given two integer arrays nums1 and nums2, return an array of their
 * intersection.
 * Each element in the result should appear as many times as it shows in both
 * arrays. The result can be returned in any order.
 *
 * Example:
 * - intersect([1,2,2,1], [2,2]) -> [2,2]
 * - intersect([4,9,5], [9,4,9,8,4]) -> [4,9] (order may vary)
 *
 * Follow-ups / discussion:
 * - Use a HashMap to count occurrences in the smaller array and scan the larger
 * one.
 * - If arrays are sorted, use two pointers for O(n + m) time and O(1) extra
 * space.
 * - For streaming inputs or huge arrays, use hashing on chunks or external
 * sort.
 */
public class IntersectionOfTwoArraysII {

    /**
     * HashMap counts approach. Build counts from the smaller array to save memory.
     * Time: O(n + m) average, Space: O(min(n,m))
     */
    public int[] intersect(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null)
            return new int[0];
        // ensure nums1 is the smaller
        if (nums1.length > nums2.length)
            return intersect(nums2, nums1);

        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int v : nums1)
            counts.put(v, counts.getOrDefault(v, 0) + 1);

        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int v : nums2) {
            Integer c = counts.get(v);
            if (c != null && c > 0) {
                res.add(v);
                counts.put(v, c - 1);
            }
        }

        // convert to int[]
        int[] out = new int[res.size()];
        for (int i = 0; i < res.size(); i++)
            out[i] = res.get(i);
        return out;
    }

    /**
     * Sorting + two-pointer approach. Sort both arrays and walk them.
     * Time: O(n log n + m log m), Space: O(1) extra if sorted in place.
     */
    public int[] intersectSorted(int[] nums1, int[] nums2) {
        if (nums1 == null || nums2 == null)
            return new int[0];
        int[] a = java.util.Arrays.copyOf(nums1, nums1.length);
        int[] b = java.util.Arrays.copyOf(nums2, nums2.length);
        java.util.Arrays.sort(a);
        java.util.Arrays.sort(b);
        int i = 0, j = 0;
        java.util.List<Integer> res = new java.util.ArrayList<>();
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                res.add(a[i]);
                i++;
                j++;
            } else if (a[i] < b[j]) {
                i++;
            } else {
                j++;
            }
        }
        int[] out = new int[res.size()];
        for (int k = 0; k < res.size(); k++)
            out[k] = res.get(k);
        return out;
    }

    private static void runTest(String name, IntersectionOfTwoArraysII solver, int[] a, int[] b) {
        int[] a1 = java.util.Arrays.copyOf(a, a.length);
        int[] b1 = java.util.Arrays.copyOf(b, b.length);
        int[] r1 = solver.intersect(a1, b1);
        int[] a2 = java.util.Arrays.copyOf(a, a.length);
        int[] b2 = java.util.Arrays.copyOf(b, b.length);
        int[] r2 = solver.intersectSorted(a2, b2);
        System.out.println(
                name + " -> hashmap: " + java.util.Arrays.toString(r1) + ", sorted: " + java.util.Arrays.toString(r2));
    }

    public static void main(String[] args) {
        IntersectionOfTwoArraysII sol = new IntersectionOfTwoArraysII();

        // Examples
        runTest("example1 [1,2,2,1] & [2,2]", sol, new int[] { 1, 2, 2, 1 }, new int[] { 2, 2 });
        runTest("example2 [4,9,5] & [9,4,9,8,4]", sol, new int[] { 4, 9, 5 }, new int[] { 9, 4, 9, 8, 4 });

        // Edge cases
        runTest("both empty", sol, new int[] {}, new int[] {});
        runTest("one empty", sol, new int[] { 1, 2, 3 }, new int[] {});
        runTest("no intersection", sol, new int[] { 1, 2, 3 }, new int[] { 4, 5, 6 });
        runTest("all duplicates", sol, new int[] { 2, 2, 2 }, new int[] { 2, 2 });

        // Larger / shuffled
        runTest("larger shuffled", sol, new int[] { 5, 4, 3, 2, 1, 2, 3 }, new int[] { 3, 3, 2, 7, 5 });
    }
}
