package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 219: Contains Duplicate II
 * https://leetcode.com/problems/contains-duplicate-ii/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given an integer array nums and an integer k, return true if
 * there are two distinct indices i and j in the array such that nums[i] ==
 * nums[j] and abs(i - j) <= k.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - 0 <= k <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find all such pairs?
 * 2. Can you find the minimum/maximum distance for duplicates?
 * 3. Can you solve for k = 0?
 */
public class ContainsDuplicateII {

    // Approach 1: HashSet sliding window (O(n) time, O(k) space)
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Set<Integer> window = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            if (window.contains(nums[i]))
                return true;
            window.add(nums[i]);
            if (window.size() > k)
                window.remove(nums[i - k]);
        }
        return false;
    }

    // Follow-up 1: Find all such pairs
    public List<int[]> allNearbyDuplicates(int[] nums, int k) {
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (map.containsKey(nums[i])) {
                for (int j : map.get(nums[i])) {
                    if (i - j <= k)
                        result.add(new int[] { j, i });
                }
            }
            map.computeIfAbsent(nums[i], x -> new ArrayList<>()).add(i);
        }
        return result;
    }

    // Follow-up 2: Minimum distance for duplicates
    public int minDistanceForDuplicates(int[] nums) {
        Map<Integer, Integer> lastIndex = new HashMap<>();
        int minDist = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            if (lastIndex.containsKey(nums[i])) {
                minDist = Math.min(minDist, i - lastIndex.get(nums[i]));
            }
            lastIndex.put(nums[i], i);
        }
        return minDist == Integer.MAX_VALUE ? -1 : minDist;
    }

    // Follow-up 3: Maximum distance for duplicates
    public int maxDistanceForDuplicates(int[] nums) {
        Map<Integer, Integer> firstIndex = new HashMap<>();
        int maxDist = -1;
        for (int i = 0; i < nums.length; i++) {
            if (firstIndex.containsKey(nums[i])) {
                maxDist = Math.max(maxDist, i - firstIndex.get(nums[i]));
            } else {
                firstIndex.put(nums[i], i);
            }
        }
        return maxDist;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ContainsDuplicateII solution = new ContainsDuplicateII();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 3, 1 };
        int k1 = 3;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1 + " Expected: true");
        System.out.println("Result: " + solution.containsNearbyDuplicate(nums1, k1));

        // Test case 2: No duplicates
        int[] nums2 = { 1, 2, 3, 4, 5 };
        int k2 = 2;
        System.out.println("\nTest 2 - No duplicates:");
        System.out.println("Result: " + solution.containsNearbyDuplicate(nums2, k2));

        // Test case 3: All nearby duplicates
        System.out.println("\nTest 3 - All nearby duplicates:");
        List<int[]> pairs = solution.allNearbyDuplicates(nums1, k1);
        for (int[] p : pairs)
            System.out.println(Arrays.toString(p));

        // Test case 4: Minimum distance
        int[] nums3 = { 1, 2, 3, 1, 2, 3 };
        System.out.println("\nTest 4 - Minimum distance for duplicates:");
        System.out.println(solution.minDistanceForDuplicates(nums3));

        // Test case 5: Maximum distance
        System.out.println("\nTest 5 - Maximum distance for duplicates:");
        System.out.println(solution.maxDistanceForDuplicates(nums3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.containsNearbyDuplicate(new int[] {}, 1));
        System.out.println("Single element: " + solution.containsNearbyDuplicate(new int[] { 1 }, 1));
        System.out.println("k = 0: " + solution.containsNearbyDuplicate(nums1, 0));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        boolean result = solution.containsNearbyDuplicate(large, 9999);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
