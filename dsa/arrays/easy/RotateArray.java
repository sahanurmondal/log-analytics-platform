package arrays.easy;

/**
 * LeetCode 189: Rotate Array
 * https://leetcode.com/problems/rotate-array/
 *
 * Given an array, rotate the array to the right by k steps, where k is
 * non-negative.
 *
 * Example:
 * - rotate([1,2,3,4,5,6,7], k=3) -> [5,6,7,1,2,3,4]
 *
 * Follow-ups:
 * - Can you do it in-place with O(1) extra space? (Yes — reverse trick or
 * cyclic replacements)
 * - What about very large k (k > n)? Use k = k % n.
 */
public class RotateArray {

    /**
     * Extra-array approach: allocate a new array and copy elements to rotated
     * positions.
     * Time: O(n), Space: O(n)
     */
    public void rotateExtra(int[] nums, int k) {
        if (nums == null || nums.length < 2)
            return;
        int n = nums.length;
        k = ((k % n) + n) % n;
        if (k == 0)
            return;
        int[] tmp = new int[n];
        for (int i = 0; i < n; i++) {
            tmp[(i + k) % n] = nums[i];
        }
        System.arraycopy(tmp, 0, nums, 0, n);
    }

    /**
     * In-place reverse approach: reverse whole array, then reverse first k, then
     * reverse k..n-1.
     * Time: O(n), Space: O(1)
     */
    public void rotate(int[] nums, int k) {
        if (nums == null || nums.length < 2)
            return;
        int n = nums.length;
        k = ((k % n) + n) % n;
        if (k == 0)
            return;
        reverse(nums, 0, n - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, n - 1);
    }

    /**
     * Cyclic replacements using GCD to determine cycles. In-place, O(1) extra.
     */
    public void rotateCyclic(int[] nums, int k) {
        if (nums == null || nums.length < 2)
            return;
        int n = nums.length;
        k = ((k % n) + n) % n;
        if (k == 0)
            return;
        int count = 0;
        for (int start = 0; count < n; start++) {
            int current = start;
            int prev = nums[start];
            do {
                int next = (current + k) % n;
                int tmp = nums[next];
                nums[next] = prev;
                prev = tmp;
                current = next;
                count++;
            } while (start != current);
        }
    }

    private void reverse(int[] nums, int i, int j) {
        while (i < j) {
            int t = nums[i];
            nums[i] = nums[j];
            nums[j] = t;
            i++;
            j--;
        }
    }

    private static void runTest(String name, RotateArray solver, int[] arr, int k) {
        int[] a1 = java.util.Arrays.copyOf(arr, arr.length);
        int[] a2 = java.util.Arrays.copyOf(arr, arr.length);
        int[] a3 = java.util.Arrays.copyOf(arr, arr.length);
        solver.rotateExtra(a1, k);
        solver.rotate(a2, k);
        solver.rotateCyclic(a3, k);
        System.out.println(name + " -> extra: " + java.util.Arrays.toString(a1)
                + ", reverse: " + java.util.Arrays.toString(a2)
                + ", cyclic: " + java.util.Arrays.toString(a3));
    }

    public static void main(String[] args) {
        RotateArray sol = new RotateArray();

        runTest("example k=3", sol, new int[] { 1, 2, 3, 4, 5, 6, 7 }, 3);
        runTest("k=0 no-op", sol, new int[] { 1, 2, 3, 4 }, 0);
        runTest("k>n", sol, new int[] { 1, 2, 3, 4 }, 6); // k=6 -> k%4=2
        runTest("empty array", sol, new int[] {}, 3);
        runTest("single element", sol, new int[] { 42 }, 10);
        runTest("negative k (handled) -3", sol, new int[] { 1, 2, 3, 4, 5 }, -3);
    }
}
