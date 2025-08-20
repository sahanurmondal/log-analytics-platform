package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 220: Contains Duplicate III
 * https://leetcode.com/problems/contains-duplicate-iii/
 *
 * Description: You are given an integer array nums and two integers indexDiff
 * and valueDiff.
 * Find a pair of indices (i, j) such that: i != j, abs(i - j) <= indexDiff,
 * abs(nums[i] - nums[j]) <= valueDiff
 * 
 * Constraints:
 * - 2 <= nums.length <= 2 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - 0 <= indexDiff <= nums.length
 * - 0 <= valueDiff <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using bucket sort?
 * 
 * Time Complexity: O(n log k)
 * Space Complexity: O(k)
 * 
 * Company Tags: Google, Facebook
 */
public class ContainsDuplicateIII {

    // Main optimized solution - TreeSet (BST)
    public boolean containsNearbyAlmostDuplicate(int[] nums, int indexDiff, int valueDiff) {
        TreeSet<Long> set = new TreeSet<>();

        for (int i = 0; i < nums.length; i++) {
            Long ceiling = set.ceiling((long) nums[i] - valueDiff);
            if (ceiling != null && ceiling <= (long) nums[i] + valueDiff) {
                return true;
            }

            set.add((long) nums[i]);

            if (i >= indexDiff) {
                set.remove((long) nums[i - indexDiff]);
            }
        }

        return false;
    }

    // Alternative solution - Bucket sort
    public boolean containsNearbyAlmostDuplicateBucket(int[] nums, int indexDiff, int valueDiff) {
        if (valueDiff < 0)
            return false;

        Map<Long, Long> buckets = new HashMap<>();
        long bucketSize = (long) valueDiff + 1;

        for (int i = 0; i < nums.length; i++) {
            long bucket = getBucket(nums[i], bucketSize);

            // Check current bucket
            if (buckets.containsKey(bucket)) {
                return true;
            }

            // Check adjacent buckets
            if (buckets.containsKey(bucket - 1) &&
                    Math.abs(buckets.get(bucket - 1) - nums[i]) < bucketSize) {
                return true;
            }

            if (buckets.containsKey(bucket + 1) &&
                    Math.abs(buckets.get(bucket + 1) - nums[i]) < bucketSize) {
                return true;
            }

            buckets.put(bucket, (long) nums[i]);

            if (i >= indexDiff) {
                buckets.remove(getBucket(nums[i - indexDiff], bucketSize));
            }
        }

        return false;
    }

    private long getBucket(long value, long bucketSize) {
        return value < 0 ? (value + 1) / bucketSize - 1 : value / bucketSize;
    }

    public static void main(String[] args) {
        ContainsDuplicateIII solution = new ContainsDuplicateIII();

        System.out.println(solution.containsNearbyAlmostDuplicate(new int[] { 1, 2, 3, 1 }, 3, 0)); // Expected: true
        System.out.println(solution.containsNearbyAlmostDuplicate(new int[] { 1, 5, 9, 1, 5, 9 }, 2, 3)); // Expected:
                                                                                                          // false
        System.out.println(solution.containsNearbyAlmostDuplicateBucket(new int[] { 1, 2, 3, 1 }, 3, 0)); // Expected:
                                                                                                          // true
    }
}
