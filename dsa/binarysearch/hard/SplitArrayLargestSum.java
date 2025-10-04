package binarysearch.hard;

/**
 * LeetCode 410: Split Array Largest Sum
 * URL: <a href="https://leetcode.com/problems/split-array-largest-sum/">https://leetcode.com/problems/split-array-largest-sum/</a>
 * Company Tags: Google, Facebook (Meta), Amazon, ByteDance, Airbnb
 * Frequency: High
 *
 * Problem:
 * Given an integer array nums and an integer k, split nums into k non-empty subarrays such that the largest sum among these subarrays is minimized.
 * Return the minimized largest sum of the split.
 * A subarray is a contiguous part of the array.
 *
 * Example 1:
 * Input: nums = [7,2,5,10,8], k = 2
 * Output: 18
 * Explanation: There are four ways to split nums into two subarrays.
 * The best way is to split it into [7,2,5] and [10,8], where the largest sum among the two subarrays is max(14, 18) = 18.
 *
 * Example 2:
 * Input: nums = [1,2,3,4,5], k = 2
 * Output: 9
 *
 * Constraints:
 * 1 <= nums.length <= 1000
 * 0 <= nums[i] <= 10^6
 * 1 <= k <= min(50, nums.length)
 */
public class SplitArrayLargestSum {

    /**
     * Solution Approach: Binary Search on the Answer
     *
     * Algorithm:
     * The problem asks to minimize the largest sum. This structure suggests that we can binary search for the answer.
     * The answer (the minimized largest sum) must lie between a lower and upper bound.
     * - Lower bound (`left`): The maximum single element in the array. This is the smallest possible "largest sum" if k is equal to the number of elements.
     * - Upper bound (`right`): The total sum of all elements in the array. This is the "largest sum" if k=1.
     *
     * We can now binary search in the range `[left, right]`. For each `mid` value (which is a potential answer for the minimized largest sum):
     * 1. We need a helper function `canSplit(maxSum)` that checks if it's possible to split the array into `k` or fewer subarrays such that no subarray sum exceeds `maxSum`.
     * 2. The `canSplit` function works greedily:
     *    a. Initialize `subarrays = 1` and `currentSum = 0`.
     *    b. Iterate through `nums`. For each number, add it to `currentSum`.
     *    c. If `currentSum` exceeds `maxSum`, we must end the current subarray here. Increment `subarrays`, and start a new subarray with the current number (`currentSum = num`).
     *    d. After iterating, return `true` if `subarrays <= k`, otherwise `false`.
     *
     * The main binary search logic:
     * 1. `left = max(nums)`, `right = sum(nums)`.
     * 2. While `left <= right`:
     *    a. `mid = left + (right - left) / 2`.
     *    b. If `canSplit(mid)` is true, it means `mid` is a possible answer. We try for a smaller sum, so we store `mid` and set `right = mid - 1`.
     *    c. If `canSplit(mid)` is false, `mid` is too small. We need to allow for a larger sum, so set `left = mid + 1`.
     * 3. The stored answer is the result.
     *
     * Time Complexity: O(n * log(S)), where n is the number of elements and S is the sum of elements in the array. The binary search takes log(S) steps, and in each step, we do a linear scan (O(n)) in `canSplit`.
     * Space Complexity: O(1).
     */
    public int splitArray(int[] nums, int k) {
        long sum = 0;
        int max = 0;
        for (int num : nums) {
            sum += num;
            max = Math.max(max, num);
        }

        long left = max;
        long right = sum;
        int result = (int) right;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            if (canSplit(nums, k, mid)) {
                result = (int) mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return result;
    }

    private boolean canSplit(int[] nums, int k, long maxSum) {
        int subarrays = 1;
        long currentSum = 0;
        for (int num : nums) {
            currentSum += num;
            if (currentSum > maxSum) {
                subarrays++;
                currentSum = num; // Start a new subarray with the current number
            }
        }
        return subarrays <= k;
    }

    public static void main(String[] args) {
        SplitArrayLargestSum solution = new SplitArrayLargestSum();

        // Example 1
        int[] nums1 = {7, 2, 5, 10, 8};
        int k1 = 2;
        System.out.println("Minimized largest sum: " + solution.splitArray(nums1, k1)); // Expected: 18

        // Example 2
        int[] nums2 = {1, 2, 3, 4, 5};
        int k2 = 2;
        System.out.println("Minimized largest sum: " + solution.splitArray(nums2, k2)); // Expected: 9

        // Edge Cases
        // 1. k is equal to the number of elements
        int[] nums3 = {1, 4, 4};
        int k3 = 3;
        System.out.println("k = n: " + solution.splitArray(nums3, k3)); // Expected: 4 (max element)

        // 2. k is 1
        int[] nums4 = {10, 20, 30};
        int k4 = 1;
        System.out.println("k = 1: " + solution.splitArray(nums4, k4)); // Expected: 60 (total sum)
    }
}

