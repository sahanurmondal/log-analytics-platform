package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 2444: Count Subarrays With Fixed Bounds
 * https://leetcode.com/problems/count-subarrays-with-fixed-bounds/
 * 
 * Companies: Meta, Google, Amazon, Microsoft
 * Frequency: High (Asked in 180+ interviews)
 *
 * Description: You are given an integer array nums and two integers minK and maxK.
 * A fixed-bound subarray of nums is a subarray that satisfies the following conditions:
 * - The minimum value in the subarray is equal to minK.
 * - The maximum value in the subarray is equal to maxK.
 * Return the number of such subarrays.
 *
 * Constraints:
 * - 2 <= nums.length <= 10^5
 * - 1 <= minK, maxK <= 10^6
 * - 1 <= nums[i] <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you handle the case where minK = maxK?
 * 2. Can you extend to multiple bound pairs simultaneously?
 * 3. Can you find the longest subarray with fixed bounds?
 * 4. Can you handle negative numbers and different constraints?
 */
public class CountSubarraysWithFixedBounds {

    // Approach 1: Single pass with boundary tracking - O(n) time, O(1) space
    public long countSubarrays(int[] nums, int minK, int maxK) {
        long result = 0;
        int minPos = -1, maxPos = -1, leftBound = -1;
        
        for (int i = 0; i < nums.length; i++) {
            // If current element is out of bounds, reset left boundary
            if (nums[i] < minK || nums[i] > maxK) {
                leftBound = i;
            }
            
            // Update positions of minK and maxK
            if (nums[i] == minK) minPos = i;
            if (nums[i] == maxK) maxPos = i;
            
            // Count valid subarrays ending at position i
            if (minPos > leftBound && maxPos > leftBound) {
                result += Math.min(minPos, maxPos) - leftBound;
            }
        }
        
        return result;
    }

    // Approach 2: Two-pointer with state tracking - O(n) time, O(1) space
    public long countSubarraysWithState(int[] nums, int minK, int maxK) {
        long count = 0;
        int left = 0;
        boolean hasMin = false, hasMax = false;
        int minIndex = -1, maxIndex = -1;
        
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] < minK || nums[right] > maxK) {
                left = right + 1;
                hasMin = hasMax = false;
                minIndex = maxIndex = -1;
                continue;
            }
            
            if (nums[right] == minK) {
                hasMin = true;
                minIndex = right;
            }
            if (nums[right] == maxK) {
                hasMax = true;
                maxIndex = right;
            }
            
            if (hasMin && hasMax) {
                count += Math.min(minIndex, maxIndex) - left + 1;
            }
        }
        
        return count;
    }

    // Approach 3: Deque-based for complex bounds - O(n) time, O(k) space
    public long countSubarraysDeque(int[] nums, int minK, int maxK) {
        long count = 0;
        Deque<Integer> minDeque = new ArrayDeque<>();
        Deque<Integer> maxDeque = new ArrayDeque<>();
        int left = 0;
        
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] < minK || nums[right] > maxK) {
                left = right + 1;
                minDeque.clear();
                maxDeque.clear();
                continue;
            }
            
            // Maintain deques for min and max tracking
            while (!minDeque.isEmpty() && nums[minDeque.peekLast()] >= nums[right]) {
                minDeque.pollLast();
            }
            while (!maxDeque.isEmpty() && nums[maxDeque.peekLast()] <= nums[right]) {
                maxDeque.pollLast();
            }
            
            minDeque.offerLast(right);
            maxDeque.offerLast(right);
            
            // Remove elements outside window
            while (!minDeque.isEmpty() && minDeque.peekFirst() < left) {
                minDeque.pollFirst();
            }
            while (!maxDeque.isEmpty() && maxDeque.peekFirst() < left) {
                maxDeque.pollFirst();
            }
            
            // Check if current window has both bounds
            if (!minDeque.isEmpty() && !maxDeque.isEmpty() &&
                nums[minDeque.peekFirst()] == minK && nums[maxDeque.peekFirst()] == maxK) {
                count += Math.min(minDeque.peekFirst(), maxDeque.peekFirst()) - left + 1;
            }
        }
        
        return count;
    }

    // Follow-up 1: Handle case where minK = maxK
    public long countSubarraysEqualBounds(int[] nums, int k) {
        long count = 0;
        int left = 0;
        
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] < k || nums[right] > k) {
                left = right + 1;
                continue;
            }
            
            if (nums[right] == k) {
                count += right - left + 1;
            }
        }
        
        return count;
    }

    // Follow-up 2: Multiple bound pairs
    public long countSubarraysMultipleBounds(int[] nums, int[][] bounds) {
        long totalCount = 0;
        
        for (int[] bound : bounds) {
            totalCount += countSubarrays(nums, bound[0], bound[1]);
        }
        
        return totalCount;
    }

    // Follow-up 3: Find longest subarray with fixed bounds
    public int longestSubarrayWithBounds(int[] nums, int minK, int maxK) {
        int maxLength = 0;
        int minPos = -1, maxPos = -1, leftBound = -1;
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < minK || nums[i] > maxK) {
                leftBound = i;
            }
            
            if (nums[i] == minK) minPos = i;
            if (nums[i] == maxK) maxPos = i;
            
            if (minPos > leftBound && maxPos > leftBound) {
                maxLength = Math.max(maxLength, i - leftBound);
            }
        }
        
        return maxLength;
    }

    // Follow-up 4: Handle negative numbers and different constraints
    public long countSubarraysExtended(int[] nums, int minK, int maxK, boolean allowNegative) {
        if (!allowNegative) {
            return countSubarrays(nums, minK, maxK);
        }
        
        long count = 0;
        int minPos = -1, maxPos = -1, leftBound = -1;
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < minK || nums[i] > maxK) {
                leftBound = i;
            }
            
            if (nums[i] == minK) minPos = i;
            if (nums[i] == maxK) maxPos = i;
            
            if (minPos > leftBound && maxPos > leftBound) {
                count += Math.min(minPos, maxPos) - leftBound;
            }
        }
        
        return count;
    }

    // Helper method: Get all valid subarrays with their indices
    public List<int[]> getAllValidSubarrays(int[] nums, int minK, int maxK) {
        List<int[]> result = new ArrayList<>();
        int minPos = -1, maxPos = -1, leftBound = -1;
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < minK || nums[i] > maxK) {
                leftBound = i;
            }
            
            if (nums[i] == minK) minPos = i;
            if (nums[i] == maxK) maxPos = i;
            
            if (minPos > leftBound && maxPos > leftBound) {
                int start = leftBound + 1;
                int end = Math.min(minPos, maxPos);
                for (int j = start; j <= end; j++) {
                    result.add(new int[]{j, i});
                }
            }
        }
        
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountSubarraysWithFixedBounds solution = new CountSubarraysWithFixedBounds();

        // Test case 1: Basic case with mixed values
        int[] nums1 = {1, 3, 5, 2, 7, 5};
        int minK1 = 1, maxK1 = 5;
        System.out.println("Test 1 - Expected: 2");
        System.out.println("Approach 1: " + solution.countSubarrays(nums1, minK1, maxK1));
        System.out.println("Approach 2: " + solution.countSubarraysWithState(nums1, minK1, maxK1));
        System.out.println("Approach 3: " + solution.countSubarraysDeque(nums1, minK1, maxK1));

        // Test case 2: Array with out-of-bound elements
        int[] nums2 = {1, 1, 1, 1};
        int minK2 = 1, maxK2 = 1;
        System.out.println("\nTest 2 - Expected: 10 (Equal bounds case)");
        System.out.println("Regular: " + solution.countSubarrays(nums2, minK2, maxK2));
        System.out.println("Equal bounds: " + solution.countSubarraysEqualBounds(nums2, minK2));

        // Test case 3: No valid subarrays
        int[] nums3 = {1, 2, 3};
        int minK3 = 4, maxK3 = 5;
        System.out.println("\nTest 3 - Expected: 0 (No valid subarrays)");
        System.out.println("Result: " + solution.countSubarrays(nums3, minK3, maxK3));

        // Test case 4: All elements out of bounds
        int[] nums4 = {10, 20, 30};
        int minK4 = 1, maxK4 = 5;
        System.out.println("\nTest 4 - Expected: 0 (All out of bounds)");
        System.out.println("Result: " + solution.countSubarrays(nums4, minK4, maxK4));

        // Test case 5: Complex case with interruptions
        int[] nums5 = {1, 3, 5, 2, 7, 5, 1, 3, 5};
        int minK5 = 1, maxK5 = 5;
        System.out.println("\nTest 5 - Expected: 7 (Complex with interruptions)");
        System.out.println("Result: " + solution.countSubarrays(nums5, minK5, maxK5));

        // Test case 6: Large array with repeated pattern
        int[] nums6 = new int[1000];
        for (int i = 0; i < 1000; i++) {
            nums6[i] = (i % 3) + 1; // Pattern: 1,2,3,1,2,3...
        }
        System.out.println("\nTest 6 - Large array (1000 elements)");
        System.out.println("Result: " + solution.countSubarrays(nums6, 1, 3));

        // Test Follow-ups
        System.out.println("\nFollow-up tests:");

        // Multiple bounds
        int[][] bounds = {{1, 5}, {2, 4}};
        System.out.println("Multiple bounds count: " + solution.countSubarraysMultipleBounds(nums1, bounds));

        // Longest subarray
        System.out.println("Longest subarray length: " + solution.longestSubarrayWithBounds(nums1, minK1, maxK1));

        // Get all valid subarrays
        List<int[]> validSubarrays = solution.getAllValidSubarrays(nums1, minK1, maxK1);
        System.out.println("Valid subarrays count: " + validSubarrays.size());
        System.out.println("Valid subarrays indices:");
        for (int[] subarray : validSubarrays) {
            System.out.println("  [" + subarray[0] + ", " + subarray[1] + "]");
        }

        // Edge case: Single element arrays
        int[] singleMin = {1};
        int[] singleMax = {5};
        int[] singleEqual = {3};
        System.out.println("\nEdge case - Single element arrays:");
        System.out.println("Single min only: " + solution.countSubarrays(singleMin, 1, 5));
        System.out.println("Single max only: " + solution.countSubarrays(singleMax, 1, 5));
        System.out.println("Single equal: " + solution.countSubarrays(singleEqual, 3, 3));

        // Edge case: Two element arrays
        int[] twoValid = {1, 5};
        int[] twoInvalid = {2, 3};
        System.out.println("\nEdge case - Two element arrays:");
        System.out.println("Two valid bounds: " + solution.countSubarrays(twoValid, 1, 5));
        System.out.println("Two within bounds: " + solution.countSubarrays(twoInvalid, 1, 5));

        // Performance test
        System.out.println("\nPerformance test with 100,000 elements:");
        int[] largeArray = new int[100000];
        for (int i = 0; i < 100000; i++) {
            largeArray[i] = (i % 10) + 1;
        }
        long startTime = System.nanoTime();
        long result = solution.countSubarrays(largeArray, 1, 10);
        long endTime = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");
    }
}
