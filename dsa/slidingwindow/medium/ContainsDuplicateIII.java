package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 220: Contains Duplicate III
 * https://leetcode.com/problems/contains-duplicate-iii/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an integer array nums and two integers k and t, return
 * true if there are two distinct indices i and j in the array such that
 * abs(nums[i] - nums[j]) <= t and abs(i - j) <= k.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - 0 <= k <= 10^4
 * - 0 <= t <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you find all such pairs?
 * 2. Can you optimize for large k and t?
 * 3. Can you solve for k = 0 or t = 0?
 */
public class ContainsDuplicateIII {

    // Approach 1: TreeSet sliding window (O(n log k) time)
    public boolean containsNearbyAlmostDuplicate(int[] nums, int k, int t) {
        TreeSet<Long> window = new TreeSet<>();
        for (int i = 0; i < nums.length; i++) {
            Long floor = window.floor((long) nums[i] + t);
            Long ceil = window.ceiling((long) nums[i] - t);
            if ((floor != null && floor >= nums[i]) || (ceil != null && ceil <= nums[i]))
                return true;
            window.add((long) nums[i]);
            if (window.size() > k)
                window.remove((long) nums[i - k]);
        }
        return false;
    }

    // Approach 2: Bucket sort (O(n) time)
    public boolean containsNearbyAlmostDuplicateBucket(int[] nums, int k, int t) {
        if (t < 0)
            return false;
        Map<Long, Long> buckets = new HashMap<>();
        long w = (long) t + 1;
        for (int i = 0; i < nums.length; i++) {
            long m = ((long) nums[i] - Integer.MIN_VALUE) / w;
            if (buckets.containsKey(m))
                return true;
            if (buckets.containsKey(m - 1) && Math.abs(nums[i] - buckets.get(m - 1)) < w)
                return true;
            if (buckets.containsKey(m + 1) && Math.abs(nums[i] - buckets.get(m + 1)) < w)
                return true;
            buckets.put(m, (long) nums[i]);
            if (i >= k)
                buckets.remove(((long) nums[i - k] - Integer.MIN_VALUE) / w);
        }
        return false;
    }

    // Follow-up 1: Find all such pairs
    public List<int[]> allNearbyAlmostDuplicates(int[] nums, int k, int t) {
        List<int[]> result = new ArrayList<>();
        TreeSet<Long> window = new TreeSet<>();
        for (int i = 0; i < nums.length; i++) {
            for (Long val : window.subSet((long) nums[i] - t, true, (long) nums[i] + t, true)) {
                result.add(new int[] { i, Arrays.asList(nums).indexOf(val.intValue()) });
            }
            window.add((long) nums[i]);
            if (window.size() > k)
                window.remove((long) nums[i - k]);
        }
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ContainsDuplicateIII solution = new ContainsDuplicateIII();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 3, 1 };
        int k1 = 3, t1 = 0;
        System.out
                .println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1 + ", t: " + t1 + " Expected: true");
        System.out.println("Result: " + solution.containsNearbyAlmostDuplicate(nums1, k1, t1));

        // Test case 2: No such pair
        int[] nums2 = { 1, 5, 9, 1, 5, 9 };
        int k2 = 2, t2 = 3;
        System.out.println("\nTest 2 - No such pair:");
        System.out.println("Result: " + solution.containsNearbyAlmostDuplicate(nums2, k2, t2));

        // Test case 3: Bucket sort
        System.out.println("\nTest 3 - Bucket sort:");
        System.out.println(solution.containsNearbyAlmostDuplicateBucket(nums1, k1, t1));

        // Test case 4: All pairs
        System.out.println("\nTest 4 - All pairs:");
        List<int[]> pairs = solution.allNearbyAlmostDuplicates(nums1, k1, t1);
        for (int[] p : pairs)
            System.out.println(Arrays.toString(p));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.containsNearbyAlmostDuplicate(new int[] {}, 1, 1));
        System.out.println("Single element: " + solution.containsNearbyAlmostDuplicate(new int[] { 1 }, 1, 1));
        System.out.println("k = 0: " + solution.containsNearbyAlmostDuplicate(nums1, 0, t1));
        System.out.println("t = 0: " + solution.containsNearbyAlmostDuplicate(nums1, k1, 0));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        boolean result = solution.containsNearbyAlmostDuplicate(large, 9999, 0);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
