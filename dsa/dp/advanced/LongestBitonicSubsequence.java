package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 1671: Minimum Number of Removals to Make Mountain Array (Longest
 * Bitonic Subsequence)
 * https://leetcode.com/problems/minimum-number-of-removals-to-make-mountain-array/
 *
 * Description:
 * You may recall that an array arr is a mountain array if and only if:
 * - arr.length >= 3
 * - There exists some index i (0-indexed) with 0 < i < arr.length - 1 such
 * that:
 * - arr[0] < arr[1] < ... < arr[i - 1] < arr[i]
 * - arr[i] > arr[i + 1] > ... > arr[arr.length - 1]
 * Given an integer array nums, return the minimum number of elements to remove
 * to make nums a mountain array.
 *
 * Constraints:
 * - 3 <= nums.length <= 1000
 * - 1 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 * - What if we need the actual bitonic subsequence?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard (categorized as Medium for this collection)
 */
public class LongestBitonicSubsequence {

    // Approach 1: Two LIS Arrays - O(n^2) time, O(n) space
    public int longestBitonicSubsequence(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        // LIS ending at each position
        int[] lisLeft = new int[n];
        Arrays.fill(lisLeft, 1);

        // LIS starting at each position (LDS from right)
        int[] lisRight = new int[n];
        Arrays.fill(lisRight, 1);

        // Calculate LIS from left
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    lisLeft[i] = Math.max(lisLeft[i], lisLeft[j] + 1);
                }
            }
        }

        // Calculate LIS from right (LDS)
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    lisRight[i] = Math.max(lisRight[i], lisRight[j] + 1);
                }
            }
        }

        // Find maximum bitonic length
        int maxBitonic = 0;
        for (int i = 1; i < n - 1; i++) {
            if (lisLeft[i] > 1 && lisRight[i] > 1) {
                maxBitonic = Math.max(maxBitonic, lisLeft[i] + lisRight[i] - 1);
            }
        }

        return maxBitonic;
    }

    // Approach 2: Minimum Removals to Make Mountain Array - O(n^2) time, O(n) space
    public int minimumMountainRemovals(int[] nums) {
        int n = nums.length;

        int[] left = new int[n];
        int[] right = new int[n];
        Arrays.fill(left, 1);
        Arrays.fill(right, 1);

        // LIS from left
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    left[i] = Math.max(left[i], left[j] + 1);
                }
            }
        }

        // LIS from right
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j]) {
                    right[i] = Math.max(right[i], right[j] + 1);
                }
            }
        }

        int maxMountain = 0;
        for (int i = 1; i < n - 1; i++) {
            if (left[i] > 1 && right[i] > 1) {
                maxMountain = Math.max(maxMountain, left[i] + right[i] - 1);
            }
        }

        return n - maxMountain;
    }

    // Approach 3: Binary Search Optimization - O(n log n) time, O(n) space
    public int longestBitonicSubsequenceBinarySearch(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int[] left = new int[n];
        int[] right = new int[n];

        // Calculate LIS lengths using binary search
        left = calculateLISLengths(nums);

        // Reverse array and calculate LIS lengths (which gives LDS for original)
        int[] reversed = new int[n];
        for (int i = 0; i < n; i++) {
            reversed[i] = nums[n - 1 - i];
        }
        int[] reversedLIS = calculateLISLengths(reversed);

        // Convert back to original order
        for (int i = 0; i < n; i++) {
            right[i] = reversedLIS[n - 1 - i];
        }

        int maxBitonic = 0;
        for (int i = 1; i < n - 1; i++) {
            if (left[i] > 1 && right[i] > 1) {
                maxBitonic = Math.max(maxBitonic, left[i] + right[i] - 1);
            }
        }

        return maxBitonic;
    }

    private int[] calculateLISLengths(int[] nums) {
        int n = nums.length;
        int[] lengths = new int[n];
        java.util.List<Integer> tails = new java.util.ArrayList<>();

        for (int i = 0; i < n; i++) {
            int pos = binarySearch(tails, nums[i]);

            if (pos == tails.size()) {
                tails.add(nums[i]);
            } else {
                tails.set(pos, nums[i]);
            }

            lengths[i] = pos + 1;
        }

        return lengths;
    }

    private int binarySearch(java.util.List<Integer> tails, int target) {
        int left = 0, right = tails.size();

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (tails.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // Approach 4: Get Actual Bitonic Subsequence - O(n^2) time, O(n) space
    public int[] getLongestBitonicSubsequence(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return new int[0];

        int[] left = new int[n];
        int[] right = new int[n];
        int[] leftParent = new int[n];
        int[] rightParent = new int[n];

        Arrays.fill(left, 1);
        Arrays.fill(right, 1);
        Arrays.fill(leftParent, -1);
        Arrays.fill(rightParent, -1);

        // Calculate LIS from left with parent tracking
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && left[j] + 1 > left[i]) {
                    left[i] = left[j] + 1;
                    leftParent[i] = j;
                }
            }
        }

        // Calculate LIS from right with parent tracking
        for (int i = n - 2; i >= 0; i--) {
            for (int j = i + 1; j < n; j++) {
                if (nums[i] > nums[j] && right[j] + 1 > right[i]) {
                    right[i] = right[j] + 1;
                    rightParent[i] = j;
                }
            }
        }

        // Find peak of longest bitonic subsequence
        int maxLength = 0;
        int peakIndex = -1;

        for (int i = 1; i < n - 1; i++) {
            if (left[i] > 1 && right[i] > 1) {
                int bitonicLength = left[i] + right[i] - 1;
                if (bitonicLength > maxLength) {
                    maxLength = bitonicLength;
                    peakIndex = i;
                }
            }
        }

        if (peakIndex == -1)
            return new int[0];

        // Reconstruct the sequence
        java.util.List<Integer> result = new java.util.ArrayList<>();

        // Add left part
        int curr = peakIndex;
        java.util.List<Integer> leftPart = new java.util.ArrayList<>();
        while (curr != -1) {
            leftPart.add(nums[curr]);
            curr = leftParent[curr];
        }

        for (int i = leftPart.size() - 1; i >= 0; i--) {
            result.add(leftPart.get(i));
        }

        // Add right part (excluding peak)
        curr = rightParent[peakIndex];
        while (curr != -1) {
            result.add(nums[curr]);
            curr = rightParent[curr];
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    // Approach 5: Space Optimized for Min Removals - O(n^2) time, O(1) extra space
    public int minimumMountainRemovalsOptimized(int[] nums) {
        int n = nums.length;

        int maxMountain = 0;

        // For each potential peak
        for (int peak = 1; peak < n - 1; peak++) {
            // Calculate left LIS ending at peak
            int leftLIS = 1;
            int[] leftDP = new int[peak + 1];
            Arrays.fill(leftDP, 1);

            for (int i = 1; i <= peak; i++) {
                for (int j = 0; j < i; j++) {
                    if (nums[j] < nums[i]) {
                        leftDP[i] = Math.max(leftDP[i], leftDP[j] + 1);
                    }
                }
            }
            leftLIS = leftDP[peak];

            // Calculate right LIS starting at peak
            int rightLIS = 1;
            int[] rightDP = new int[n - peak];
            Arrays.fill(rightDP, 1);

            for (int i = 1; i < n - peak; i++) {
                for (int j = 0; j < i; j++) {
                    if (nums[peak + j] > nums[peak + i]) {
                        rightDP[i] = Math.max(rightDP[i], rightDP[j] + 1);
                    }
                }
            }
            rightLIS = rightDP[0];

            if (leftLIS > 1 && rightLIS > 1) {
                maxMountain = Math.max(maxMountain, leftLIS + rightLIS - 1);
            }
        }

        return n - maxMountain;
    }

    public static void main(String[] args) {
        LongestBitonicSubsequence solution = new LongestBitonicSubsequence();

        System.out.println("=== Longest Bitonic Subsequence Test Cases ===");

        // Test Case 1: Example bitonic sequence
        int[] nums1 = { 1, 11, 2, 10, 4, 5, 2, 1 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("Longest Bitonic: " + solution.longestBitonicSubsequence(nums1));
        System.out.println("Binary Search: " + solution.longestBitonicSubsequenceBinarySearch(nums1));
        System.out.println("Actual Sequence: " + Arrays.toString(solution.getLongestBitonicSubsequence(nums1)));
        System.out.println("Expected: 6\n");

        // Test Case 2: Mountain array problem
        int[] nums2 = { 4, 3, 2, 1, 1, 2, 3, 1 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("Min Removals: " + solution.minimumMountainRemovals(nums2));
        System.out.println("Expected: 4\n");

        // Test Case 3: Already mountain
        int[] nums3 = { 1, 3, 1 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("Min Removals: " + solution.minimumMountainRemovals(nums3));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestBitonicSubsequence solution = new LongestBitonicSubsequence();

        int[] largeArray = new int[1000];
        for (int i = 0; i < 500; i++) {
            largeArray[i] = i + 1; // Increasing part
        }
        for (int i = 500; i < 1000; i++) {
            largeArray[i] = 1000 - i; // Decreasing part
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.longestBitonicSubsequence(largeArray);
        long end = System.nanoTime();
        System.out.println("O(n²): " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.longestBitonicSubsequenceBinarySearch(largeArray);
        end = System.nanoTime();
        System.out.println("O(n log n): " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minimumMountainRemovals(largeArray);
        end = System.nanoTime();
        System.out.println(
                "Min Removals: " + (largeArray.length - result3) + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
