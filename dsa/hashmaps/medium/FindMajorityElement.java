package hashmaps.medium;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 169: Majority Element
 * https://leetcode.com/problems/majority-element/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Given an array `nums` of size `n`, return the majority element.
 * The majority element is the element that appears more than `n / 2` times.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5 * 10^4
 * - -10^9 <= nums[i] <= 10^9
 * - The majority element always exists in the array.
 * 
 * Follow-up Questions:
 * 1. Can you solve this in linear time and in O(1) space? (Boyer-Moore Voting
 * Algorithm)
 * 2. What if the majority element is not guaranteed to exist?
 * 3. What if you need to find all elements that appear more than `n / 3` times?
 * (LeetCode 229)
 */
public class FindMajorityElement {

    // Approach 1: HashMap - O(n) time, O(n) space
    public int majorityElement(int[] nums) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        int n = nums.length;
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > n / 2) {
                return entry.getKey();
            }
        }

        return -1; // Should not happen based on problem constraints
    }

    // Approach 2: Boyer-Moore Voting Algorithm - O(n) time, O(1) space
    public int majorityElementBoyerMoore(int[] nums) {
        int count = 0;
        Integer candidate = null;

        for (int num : nums) {
            if (count == 0) {
                candidate = num;
            }
            count += (num == candidate) ? 1 : -1;
        }

        return candidate;
    }

    public static void main(String[] args) {
        FindMajorityElement solution = new FindMajorityElement();

        // Test case 1
        int[] nums1 = { 3, 2, 3 };
        System.out.println("Majority Element 1 (Map): " + solution.majorityElement(nums1)); // 3
        System.out.println("Majority Element 1 (Boyer-Moore): " + solution.majorityElementBoyerMoore(nums1)); // 3

        // Test case 2
        int[] nums2 = { 2, 2, 1, 1, 1, 2, 2 };
        System.out.println("Majority Element 2: " + solution.majorityElement(nums2)); // 2

        // Test case 3: Single element
        int[] nums3 = { 1 };
        System.out.println("Majority Element 3: " + solution.majorityElement(nums3)); // 1
    }
}
