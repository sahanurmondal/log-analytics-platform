package hashmaps.medium;

import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 2395: Find Subarrays With Equal Sum
 * https://leetcode.com/problems/find-subarrays-with-equal-sum/
 * 
 * Companies: Amazon, Microsoft, Google
 * Frequency: Low
 *
 * Description: Given a 0-indexed integer array `nums`, determine if there exist
 * two subarrays of length 2 with equal sum. Note that the two subarrays must
 * begin at different indices.
 *
 * Constraints:
 * - 2 <= nums.length <= 1000
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. What if the subarrays can have any length?
 * 2. Can you find the actual subarrays?
 * 3. How would you solve this for subarrays of length `k`?
 */
public class FindSubarraysWithEqualSum {

    // Approach 1: HashSet - O(n) time, O(n) space
    public boolean findSubarrays(int[] nums) {
        Set<Integer> sums = new HashSet<>();
        for (int i = 0; i < nums.length - 1; i++) {
            int sum = nums[i] + nums[i + 1];
            if (!sums.add(sum)) {
                return true;
            }
        }
        return false;
    }

    // Approach 2: Brute Force - O(n^2) time, O(1) space
    public boolean findSubarraysBruteForce(int[] nums) {
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                if (nums[i] + nums[i + 1] == nums[j] + nums[j + 1]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        FindSubarraysWithEqualSum solution = new FindSubarraysWithEqualSum();

        // Test case 1
        int[] nums1 = { 4, 2, 4 };
        System.out.println("Result 1: " + solution.findSubarrays(nums1)); // true

        // Test case 2
        int[] nums2 = { 1, 2, 3, 4, 5 };
        System.out.println("Result 2: " + solution.findSubarrays(nums2)); // false

        // Test case 3
        int[] nums3 = { 0, 0, 0 };
        System.out.println("Result 3: " + solution.findSubarrays(nums3)); // true

        // Test case 4: Negative numbers
        int[] nums4 = { 1, -1, 1, -1 };
        System.out.println("Result 4: " + solution.findSubarrays(nums4)); // true
    }
}
