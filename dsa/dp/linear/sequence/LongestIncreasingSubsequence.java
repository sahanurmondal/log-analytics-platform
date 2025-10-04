package dp.linear.sequence;

/**
 * LeetCode 300: Longest Increasing Subsequence
 * https://leetcode.com/problems/longest-increasing-subsequence/
 *
 * Description:
 * Given an unsorted array of integers, find the length of longest increasing
 * subsequence.
 *
 * Constraints:
 * - 1 <= nums.length <= 2500
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 * 
 * Company Tags: Microsoft, Google, Amazon, Apple, Facebook, Adobe
 * Difficulty: Medium
 */
public class LongestIncreasingSubsequence {

    // Approach 1: Dynamic Programming - O(n^2) time, O(n) space
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;

        int n = nums.length;
        int[] dp = new int[n];
        java.util.Arrays.fill(dp, 1); // Each element forms LIS of length 1

        int maxLen = 1;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLen = Math.max(maxLen, dp[i]);
        }

        return maxLen;
    }

    // Approach 2: Binary Search (Patience Sorting) - O(n log n) time, O(n) space
    public int lengthOfLISOptimal(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;

        java.util.List<Integer> tails = new java.util.ArrayList<>();

        for (int num : nums) {
            int left = 0, right = tails.size();

            // Binary search for the position to insert/replace
            while (left < right) {
                int mid = left + (right - left) / 2;
                if (tails.get(mid) < num) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            // If left == tails.size(), append to the end
            if (left == tails.size()) {
                tails.add(num);
            } else {
                tails.set(left, num); // Replace with smaller value
            }
        }

        return tails.size();
    }

    // Approach 3: Binary Search with built-in function - O(n log n) time, O(n)
    // space
    public int lengthOfLISBuiltIn(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;

        java.util.List<Integer> lis = new java.util.ArrayList<>();

        for (int num : nums) {
            int pos = java.util.Collections.binarySearch(lis, num);

            if (pos < 0) {
                pos = -(pos + 1); // Convert to insertion point
            }

            if (pos == lis.size()) {
                lis.add(num);
            } else {
                lis.set(pos, num);
            }
        }

        return lis.size();
    }

    // Approach 4: Get the actual LIS sequence - O(n^2) time, O(n) space
    public java.util.List<Integer> getLISSequence(int[] nums) {
        if (nums == null || nums.length == 0)
            return new java.util.ArrayList<>();

        int n = nums.length;
        int[] dp = new int[n];
        int[] parent = new int[n];
        java.util.Arrays.fill(dp, 1);
        java.util.Arrays.fill(parent, -1);

        int maxLen = 1;
        int maxIdx = 0;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                maxIdx = i;
            }
        }

        // Reconstruct the sequence
        java.util.List<Integer> result = new java.util.ArrayList<>();
        int curr = maxIdx;
        while (curr != -1) {
            result.add(0, nums[curr]);
            curr = parent[curr];
        }

        return result;
    }

    // Approach 5: Count of all LIS - O(n^2) time, O(n) space
    public int findNumberOfLIS(int[] nums) {
        if (nums == null || nums.length == 0)
            return 0;

        int n = nums.length;
        int[] lengths = new int[n]; // lengths[i] = length of LIS ending at i
        int[] counts = new int[n]; // counts[i] = number of LIS ending at i

        java.util.Arrays.fill(lengths, 1);
        java.util.Arrays.fill(counts, 1);

        int maxLength = 1;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    if (lengths[j] + 1 > lengths[i]) {
                        lengths[i] = lengths[j] + 1;
                        counts[i] = counts[j];
                    } else if (lengths[j] + 1 == lengths[i]) {
                        counts[i] += counts[j];
                    }
                }
            }
            maxLength = Math.max(maxLength, lengths[i]);
        }

        int result = 0;
        for (int i = 0; i < n; i++) {
            if (lengths[i] == maxLength) {
                result += counts[i];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        LongestIncreasingSubsequence solution = new LongestIncreasingSubsequence();

        System.out.println("=== Longest Increasing Subsequence Test Cases ===");

        // Test case 1: Normal case
        int[] nums1 = { 10, 9, 2, 5, 3, 7, 101, 18 };
        System.out.println("Array: " + java.util.Arrays.toString(nums1));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums1)); // Expected: 4
        System.out.println("Binary Search O(n log n): " + solution.lengthOfLISOptimal(nums1)); // Expected: 4
        System.out.println("Built-in Binary Search: " + solution.lengthOfLISBuiltIn(nums1)); // Expected: 4
        System.out.println("LIS Sequence: " + solution.getLISSequence(nums1)); // Expected: [2, 3, 7, 18] or [2, 3, 7,
                                                                               // 101]
        System.out.println("Count of LIS: " + solution.findNumberOfLIS(nums1)); // Expected: 2

        // Test case 2: All decreasing
        int[] nums2 = { 5, 4, 3, 2, 1 };
        System.out.println("\nArray: " + java.util.Arrays.toString(nums2));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums2)); // Expected: 1
        System.out.println("Binary Search O(n log n): " + solution.lengthOfLISOptimal(nums2)); // Expected: 1
        System.out.println("LIS Sequence: " + solution.getLISSequence(nums2)); // Expected: [5] or any single element

        // Test case 3: All increasing
        int[] nums3 = { 1, 2, 3, 4, 5 };
        System.out.println("\nArray: " + java.util.Arrays.toString(nums3));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums3)); // Expected: 5
        System.out.println("Binary Search O(n log n): " + solution.lengthOfLISOptimal(nums3)); // Expected: 5
        System.out.println("LIS Sequence: " + solution.getLISSequence(nums3)); // Expected: [1, 2, 3, 4, 5]

        // Test case 4: Single element
        int[] nums4 = { 42 };
        System.out.println("\nArray: " + java.util.Arrays.toString(nums4));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums4)); // Expected: 1

        // Test case 5: Duplicates
        int[] nums5 = { 1, 3, 6, 7, 9, 4, 10, 5, 6 };
        System.out.println("\nArray: " + java.util.Arrays.toString(nums5));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums5)); // Expected: 6
        System.out.println("Binary Search O(n log n): " + solution.lengthOfLISOptimal(nums5)); // Expected: 6
        System.out.println("LIS Sequence: " + solution.getLISSequence(nums5));

        // Test case 6: Empty array
        int[] nums6 = {};
        System.out.println("\nEmpty Array: " + java.util.Arrays.toString(nums6));
        System.out.println("DP O(n^2): " + solution.lengthOfLIS(nums6)); // Expected: 0

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        LongestIncreasingSubsequence solution = new LongestIncreasingSubsequence();

        // Create large test array
        int[] large = new int[2500];
        java.util.Random random = new java.util.Random(42); // Fixed seed for reproducibility
        for (int i = 0; i < large.length; i++) {
            large[i] = random.nextInt(10000) - 5000; // Range: -5000 to 4999
        }

        long startTime, endTime;

        // Test DP approach
        startTime = System.nanoTime();
        int result1 = solution.lengthOfLIS(large);
        endTime = System.nanoTime();
        System.out.println("DP O(n^2): " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Binary Search approach
        startTime = System.nanoTime();
        int result2 = solution.lengthOfLISOptimal(large);
        endTime = System.nanoTime();
        System.out.println("Binary Search O(n log n): " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Built-in Binary Search approach
        startTime = System.nanoTime();
        int result3 = solution.lengthOfLISBuiltIn(large);
        endTime = System.nanoTime();
        System.out.println("Built-in Binary Search: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        System.out.println("All approaches should return the same result: " +
                (result1 == result2 && result2 == result3));
    }
}
