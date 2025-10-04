package sorting.hard;

/**
 * LeetCode 327: Count of Range Sum
 * https://leetcode.com/problems/count-of-range-sum/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Count the number of range sums that lie in [lower, upper].
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 *
 * Follow-ups:
 * 1. Can you return the actual ranges?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle updates to the array?
 */
public class CountRangeSum {
    public int countRangeSum(int[] nums, int lower, int upper) {
        long[] sums = new long[nums.length + 1];
        for (int i = 0; i < nums.length; i++)
            sums[i + 1] = sums[i] + nums[i];
        return countWhileMerge(sums, 0, sums.length, lower, upper);
    }

    private int countWhileMerge(long[] sums, int left, int right, int lower, int upper) {
        if (right - left <= 1)
            return 0;
        int mid = (left + right) / 2;
        int count = countWhileMerge(sums, left, mid, lower, upper) +
                countWhileMerge(sums, mid, right, lower, upper);
        int j = mid, k = mid, t = mid;
        long[] cache = new long[right - left];
        int r = 0;
        for (int i = left; i < mid; i++) {
            while (k < right && sums[k] - sums[i] < lower)
                k++;
            while (j < right && sums[j] - sums[i] <= upper)
                j++;
            while (t < right && sums[t] < sums[i])
                cache[r++] = sums[t++];
            cache[r++] = sums[i];
            count += j - k;
        }
        System.arraycopy(cache, 0, sums, left, t - left);
        return count;
    }

    // Follow-up 1: Return actual ranges
    public java.util.List<int[]> actualRanges(int[] nums, int lower, int upper) {
        java.util.List<int[]> res = new java.util.ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            long sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (sum >= lower && sum <= upper)
                    res.add(new int[] { i, j });
            }
        }
        return res;
    }

    // Follow-up 2: Optimize for large arrays (already handled above)
    // Follow-up 3: Handle updates (not implemented)

    public static void main(String[] args) {
        CountRangeSum solution = new CountRangeSum();

        System.out.println(solution.countRangeSum(new int[] { -2, 5, -1 }, -2, 2)); // 3
        System.out.println(solution.countRangeSum(new int[] { 0 }, 0, 0)); // 1

        // Edge Case: All negative
        System.out.println(solution.countRangeSum(new int[] { -3, -2, -1 }, -3, -1)); // 6

        // Edge Case: Large range
        System.out.println(solution.countRangeSum(new int[] { 1, 2, 3, 4, 5 }, 0, 100)); // 15

        // Edge Case: No valid range sums
        System.out.println(solution.countRangeSum(new int[] { 1, 2, 3 }, 10, 20)); // 0

        // Edge Case: Single element
        System.out.println(solution.countRangeSum(new int[] { 5 }, 5, 5)); // 1

        // Edge Case: Range sum overflow
        System.out.println(solution.countRangeSum(new int[] { 2147483647, -2147483648, -1, 0 }, -1, 0)); // 4

        // Follow-up 1: Actual ranges
        System.out.println(solution.actualRanges(new int[] { -2, 5, -1 }, -2, 2)); // [[0,0],[0,2],[2,2]]
    }
}
