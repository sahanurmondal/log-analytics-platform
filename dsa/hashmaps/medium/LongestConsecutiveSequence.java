package hashmaps.medium;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 128: Longest Consecutive Sequence
 * https://leetcode.com/problems/longest-consecutive-sequence/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Given an unsorted array of integers `nums`, return the length of
 * the longest consecutive elements sequence.
 * You must write an algorithm that runs in O(n) time.
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you solve this with sorting? What would be the complexity?
 * 2. How does the HashSet approach achieve O(n) complexity?
 * 3. What if the numbers are in a stream?
 */
public class LongestConsecutiveSequence {

    // Approach 1: HashSet - O(n) time, O(n) space
    public int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int longestStreak = 0;

        for (int num : numSet) {
            // Only start counting if 'num' is the start of a sequence
            if (!numSet.contains(num - 1)) {
                int currentNum = num;
                int currentStreak = 1;

                while (numSet.contains(currentNum + 1)) {
                    currentNum++;
                    currentStreak++;
                }

                longestStreak = Math.max(longestStreak, currentStreak);
            }
        }

        return longestStreak;
    }

    // Approach 2: Sorting - O(n log n) time, O(1) or O(n) space depending on sort
    // implementation
    public int longestConsecutiveWithSort(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        Arrays.sort(nums);

        int longestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1]) { // Handle duplicates
                if (nums[i] == nums[i - 1] + 1) {
                    currentStreak++;
                } else {
                    longestStreak = Math.max(longestStreak, currentStreak);
                    currentStreak = 1;
                }
            }
        }

        return Math.max(longestStreak, currentStreak);
    }

    public static void main(String[] args) {
        LongestConsecutiveSequence solution = new LongestConsecutiveSequence();

        // Test case 1
        int[] nums1 = { 100, 4, 200, 1, 3, 2 };
        System.out.println("Longest streak 1 (HashSet): " + solution.longestConsecutive(nums1)); // 4
        System.out.println("Longest streak 1 (Sort): " + solution.longestConsecutiveWithSort(nums1)); // 4

        // Test case 2
        int[] nums2 = { 0, 3, 7, 2, 5, 8, 4, 6, 0, 1 };
        System.out.println("Longest streak 2: " + solution.longestConsecutive(nums2)); // 9

        // Test case 3: Empty array
        int[] nums3 = {};
        System.out.println("Longest streak 3: " + solution.longestConsecutive(nums3)); // 0

        // Test case 4: Array with duplicates
        int[] nums4 = { 1, 2, 0, 1 };
        System.out.println("Longest streak 4: " + solution.longestConsecutive(nums4)); // 3

        // Test case 5: Negative numbers
        int[] nums5 = { -1, 0, 1, -2 };
        System.out.println("Longest streak 5: " + solution.longestConsecutive(nums5)); // 4
    }
}
