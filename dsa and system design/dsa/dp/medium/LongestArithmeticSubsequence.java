package dp.medium;

import java.util.*;

/**
 * LeetCode 1027: Longest Arithmetic Subsequence
 * https://leetcode.com/problems/longest-arithmetic-subsequence/
 *
 * Description:
 * Given an array nums of integers, return the length of the longest arithmetic
 * subsequence in nums.
 * Recall that a subsequence of an array nums is a list nums[i1], nums[i2], ...,
 * nums[ik] with 0 <= i1 < i2 < ... < ik <= nums.length - 1,
 * and that a sequence seq is arithmetic if seq[i+1] - seq[i] are all the same
 * value (for 0 <= i < seq.length - 1).
 *
 * Constraints:
 * - 2 <= nums.length <= 1000
 * - 0 <= nums[i] <= 500
 *
 * Follow-up:
 * - What if we need to find all arithmetic subsequences?
 * - Can you optimize space complexity?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class LongestArithmeticSubsequence {

    // Approach 1: HashMap DP - O(n^2) time, O(n^2) space
    public int longestArithSeqLength(int[] nums) {
        int n = nums.length;
        if (n <= 2)
            return n;

        // dp[i][diff] = length of arithmetic sequence ending at i with difference diff
        Map<Integer, Integer>[] dp = new HashMap[n];
        for (int i = 0; i < n; i++) {
            dp[i] = new HashMap<>();
        }

        int maxLength = 2;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int diff = nums[i] - nums[j];
                int length = dp[j].getOrDefault(diff, 1) + 1;
                dp[i].put(diff, Math.max(dp[i].getOrDefault(diff, 0), length));
                maxLength = Math.max(maxLength, length);
            }
        }

        return maxLength;
    }

    // Approach 2: 2D Array DP (with offset for negative differences) - O(n^2) time,
    // O(n*range) space
    public int longestArithSeqLength2D(int[] nums) {
        int n = nums.length;
        if (n <= 2)
            return n;

        int minVal = Arrays.stream(nums).min().orElse(0);
        int maxVal = Arrays.stream(nums).max().orElse(500);
        int offset = 500; // Handle negative differences
        int range = maxVal - minVal + 2 * offset;

        // dp[i][diff+offset] = length of arithmetic sequence ending at i with
        // difference diff
        int[][] dp = new int[n][range + 1];
        int maxLength = 2;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int diff = nums[i] - nums[j] + offset;
                if (diff >= 0 && diff <= range) {
                    dp[i][diff] = Math.max(dp[i][diff], Math.max(dp[j][diff], 1) + 1);
                    maxLength = Math.max(maxLength, dp[i][diff]);
                }
            }
        }

        return maxLength;
    }

    // Approach 3: Optimized with Early Termination - O(n^2) time, O(n^2) space
    public int longestArithSeqLengthOptimized(int[] nums) {
        int n = nums.length;
        if (n <= 2)
            return n;

        Map<Integer, Integer>[] dp = new HashMap[n];
        for (int i = 0; i < n; i++) {
            dp[i] = new HashMap<>();
        }

        int maxLength = 2;

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int diff = nums[i] - nums[j];
                int prevLength = dp[j].getOrDefault(diff, 1);
                int newLength = prevLength + 1;

                dp[i].put(diff, Math.max(dp[i].getOrDefault(diff, 0), newLength));
                maxLength = Math.max(maxLength, newLength);

                // Early termination: if we found a sequence of length n-i+1,
                // it's impossible to find longer
                if (maxLength >= n - i + 1) {
                    return maxLength;
                }
            }
        }

        return maxLength;
    }

    // Approach 4: Space Optimized with Rolling HashMap - O(n^2) time, O(n) space
    public int longestArithSeqLengthSpaceOptimized(int[] nums) {
        int n = nums.length;
        if (n <= 2)
            return n;

        int maxLength = 2;

        // For each potential difference
        Set<Integer> allDiffs = new HashSet<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                allDiffs.add(nums[j] - nums[i]);
            }
        }

        for (int diff : allDiffs) {
            Map<Integer, Integer> valueToLength = new HashMap<>();

            for (int num : nums) {
                int prevNum = num - diff;
                int length = valueToLength.getOrDefault(prevNum, 1) + 1;
                valueToLength.put(num, Math.max(valueToLength.getOrDefault(num, 0), length));
                maxLength = Math.max(maxLength, length);
            }
        }

        return maxLength;
    }

    // Approach 5: Get All Arithmetic Subsequences - O(n^3) time, O(n^2) space
    public List<List<Integer>> getAllArithmeticSubsequences(int[] nums) {
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();

        // dp[i][diff] = list of arithmetic subsequences ending at i with difference
        // diff
        Map<Integer, List<List<Integer>>>[] dp = new HashMap[n];
        for (int i = 0; i < n; i++) {
            dp[i] = new HashMap<>();
        }

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int diff = nums[i] - nums[j];

                // Initialize lists for this difference if not present
                dp[i].putIfAbsent(diff, new ArrayList<>());
                dp[j].putIfAbsent(diff, new ArrayList<>());

                if (dp[j].get(diff).isEmpty()) {
                    // Start new sequence
                    List<Integer> newSeq = new ArrayList<>();
                    newSeq.add(nums[j]);
                    newSeq.add(nums[i]);
                    dp[i].get(diff).add(newSeq);
                } else {
                    // Extend existing sequences
                    for (List<Integer> seq : dp[j].get(diff)) {
                        List<Integer> newSeq = new ArrayList<>(seq);
                        newSeq.add(nums[i]);
                        dp[i].get(diff).add(newSeq);
                    }
                }
            }
        }

        // Collect all sequences of length >= 3
        for (int i = 0; i < n; i++) {
            for (List<List<Integer>> sequences : dp[i].values()) {
                for (List<Integer> seq : sequences) {
                    if (seq.size() >= 3) {
                        result.add(new ArrayList<>(seq));
                    }
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        LongestArithmeticSubsequence solution = new LongestArithmeticSubsequence();

        System.out.println("=== Longest Arithmetic Subsequence Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 3, 6, 9, 12 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("HashMap DP: " + solution.longestArithSeqLength(nums1));
        System.out.println("2D Array: " + solution.longestArithSeqLength2D(nums1));
        System.out.println("Optimized: " + solution.longestArithSeqLengthOptimized(nums1));
        System.out.println("Space Optimized: " + solution.longestArithSeqLengthSpaceOptimized(nums1));
        System.out.println("Expected: 4\n");

        // Test Case 2: Mixed differences
        int[] nums2 = { 9, 4, 7, 2, 10 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("HashMap DP: " + solution.longestArithSeqLength(nums2));
        System.out.println("Expected: 3\n");

        // Test Case 3: No arithmetic sequence > 2
        int[] nums3 = { 20, 1, 15, 3, 10, 5, 8 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("HashMap DP: " + solution.longestArithSeqLength(nums3));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestArithmeticSubsequence solution = new LongestArithmeticSubsequence();

        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 500);
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.longestArithSeqLength(largeArray);
        long end = System.nanoTime();
        System.out.println("HashMap DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.longestArithSeqLengthOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.longestArithSeqLengthSpaceOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("Space Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
