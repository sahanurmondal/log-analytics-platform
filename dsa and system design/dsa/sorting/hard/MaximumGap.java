package sorting.hard;

/**
 * LeetCode 164: Maximum Gap
 * https://leetcode.com/problems/maximum-gap/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given an unsorted array, find the maximum difference between the successive
 * elements in its sorted form.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= nums[i] <= 10^9
 *
 * Follow-ups:
 * 1. Can you do it in linear time?
 * 2. Can you return the actual pair?
 * 3. Can you handle negative numbers?
 */
public class MaximumGap {
    public int maximumGap(int[] nums) {
        int n = nums.length;
        if (n < 2)
            return 0;
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int num : nums) {
            min = Math.min(min, num);
            max = Math.max(max, num);
        }
        int gap = (int) Math.ceil((double) (max - min) / (n - 1));
        int[] bucketMin = new int[n - 1], bucketMax = new int[n - 1];
        java.util.Arrays.fill(bucketMin, Integer.MAX_VALUE);
        java.util.Arrays.fill(bucketMax, Integer.MIN_VALUE);
        for (int num : nums) {
            if (num == min || num == max)
                continue;
            int idx = (num - min) / gap;
            bucketMin[idx] = Math.min(bucketMin[idx], num);
            bucketMax[idx] = Math.max(bucketMax[idx], num);
        }
        int res = 0, prev = min;
        for (int i = 0; i < n - 1; i++) {
            if (bucketMin[i] == Integer.MAX_VALUE)
                continue;
            res = Math.max(res, bucketMin[i] - prev);
            prev = bucketMax[i];
        }
        res = Math.max(res, max - prev);
        return res;
    }

    // Follow-up 1: Linear time (already handled above)
    // Follow-up 2: Return actual pair
    public int[] maximumGapPair(int[] nums) {
        int n = nums.length;
        if (n < 2)
            return new int[0];
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int num : nums) {
            min = Math.min(min, num);
            max = Math.max(max, num);
        }
        int gap = (int) Math.ceil((double) (max - min) / (n - 1));
        int[] bucketMin = new int[n - 1], bucketMax = new int[n - 1];
        java.util.Arrays.fill(bucketMin, Integer.MAX_VALUE);
        java.util.Arrays.fill(bucketMax, Integer.MIN_VALUE);
        for (int num : nums) {
            if (num == min || num == max)
                continue;
            int idx = (num - min) / gap;
            bucketMin[idx] = Math.min(bucketMin[idx], num);
            bucketMax[idx] = Math.max(bucketMax[idx], num);
        }
        int prev = min, maxGap = 0;
        int[] pair = new int[2];
        for (int i = 0; i < n - 1; i++) {
            if (bucketMin[i] == Integer.MAX_VALUE)
                continue;
            if (bucketMin[i] - prev > maxGap) {
                maxGap = bucketMin[i] - prev;
                pair[0] = prev;
                pair[1] = bucketMin[i];
            }
            prev = bucketMax[i];
        }
        if (max - prev > maxGap) {
            pair[0] = prev;
            pair[1] = max;
        }
        return pair;
    }

    // Follow-up 3: Handle negative numbers (already handled above)

    public static void main(String[] args) {
        MaximumGap solution = new MaximumGap();

        System.out.println(solution.maximumGap(new int[] { 3, 6, 9, 1 })); // 3
        System.out.println(solution.maximumGap(new int[] { 10 })); // 0

        // Edge Case: All same elements
        System.out.println(solution.maximumGap(new int[] { 1, 1, 1, 1 })); // 0

        // Edge Case: Two elements
        System.out.println(solution.maximumGap(new int[] { 1, 10 })); // 9

        // Edge Case: Already sorted
        System.out.println(solution.maximumGap(new int[] { 1, 2, 3, 4, 5 })); // 1

        // Edge Case: Large gap at end
        System.out.println(solution.maximumGap(new int[] { 1, 2, 3, 1000000 })); // 999997

        // Edge Case: Zero in array
        System.out.println(solution.maximumGap(new int[] { 0, 1, 2, 5 })); // 3

        System.out.println(java.util.Arrays.toString(solution.maximumGapPair(new int[] { 3, 6, 9, 1 }))); // [6,9]
    }
}
