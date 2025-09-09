package searching.hard;

import java.util.*;

/**
 * LeetCode 659: Split Array into Consecutive Subsequences
 * https://leetcode.com/problems/split-array-into-consecutive-subsequences/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Check if array can be split into consecutive subsequences of
 * length >= 3.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -1000 <= nums[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you return the actual subsequences?
 * 2. What if minimum length is k instead of 3?
 * 3. Can you handle duplicates efficiently?
 */
public class SplitArrayIntoConsecutiveSubsequences {

    // Approach 1: Greedy with frequency maps - O(n) time, O(n) space
    public boolean isPossible(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Integer> need = new HashMap<>();

        // Count frequencies
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        for (int num : nums) {
            if (freq.get(num) == 0)
                continue;

            freq.put(num, freq.get(num) - 1);

            if (need.getOrDefault(num, 0) > 0) {
                // Extend existing subsequence
                need.put(num, need.get(num) - 1);
                need.put(num + 1, need.getOrDefault(num + 1, 0) + 1);
            } else if (freq.getOrDefault(num + 1, 0) > 0 &&
                    freq.getOrDefault(num + 2, 0) > 0) {
                // Start new subsequence
                freq.put(num + 1, freq.get(num + 1) - 1);
                freq.put(num + 2, freq.get(num + 2) - 1);
                need.put(num + 3, need.getOrDefault(num + 3, 0) + 1);
            } else {
                return false;
            }
        }

        return true;
    }

    // Approach 2: Priority queue approach - O(n log n) time, O(n) space
    public boolean isPossiblePQ(int[] nums) {
        Map<Integer, PriorityQueue<Integer>> tails = new HashMap<>();

        for (int num : nums) {
            if (!tails.containsKey(num - 1) || tails.get(num - 1).isEmpty()) {
                // Start new subsequence
                tails.computeIfAbsent(num, k -> new PriorityQueue<>()).offer(1);
            } else {
                // Extend existing subsequence
                int len = tails.get(num - 1).poll();
                tails.computeIfAbsent(num, k -> new PriorityQueue<>()).offer(len + 1);
            }
        }

        // Check if all subsequences have length >= 3
        for (PriorityQueue<Integer> pq : tails.values()) {
            for (int len : pq) {
                if (len < 3)
                    return false;
            }
        }

        return true;
    }

    // Follow-up 1: Return actual subsequences
    public List<List<Integer>> getSubsequences(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, Queue<List<Integer>>> tails = new HashMap<>();

        for (int num : nums) {
            if (tails.containsKey(num - 1) && !tails.get(num - 1).isEmpty()) {
                // Extend existing subsequence
                List<Integer> seq = tails.get(num - 1).poll();
                seq.add(num);
                tails.computeIfAbsent(num, k -> new LinkedList<>()).offer(seq);
            } else {
                // Start new subsequence
                List<Integer> newSeq = new ArrayList<>();
                newSeq.add(num);
                tails.computeIfAbsent(num, k -> new LinkedList<>()).offer(newSeq);
            }
        }

        // Collect all subsequences
        for (Queue<List<Integer>> queue : tails.values()) {
            result.addAll(queue);
        }

        // Filter valid subsequences (length >= 3)
        result.removeIf(seq -> seq.size() < 3);

        return result;
    }

    // Follow-up 2: Minimum length k instead of 3
    public boolean isPossibleMinK(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        Map<Integer, Integer> need = new HashMap<>();

        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        for (int num : nums) {
            if (freq.get(num) == 0)
                continue;

            freq.put(num, freq.get(num) - 1);

            if (need.getOrDefault(num, 0) > 0) {
                need.put(num, need.get(num) - 1);
                need.put(num + 1, need.getOrDefault(num + 1, 0) + 1);
            } else {
                // Check if we can start a new subsequence of length k
                boolean canStart = true;
                for (int i = 1; i < k; i++) {
                    if (freq.getOrDefault(num + i, 0) == 0) {
                        canStart = false;
                        break;
                    }
                }

                if (canStart) {
                    for (int i = 1; i < k; i++) {
                        freq.put(num + i, freq.get(num + i) - 1);
                    }
                    need.put(num + k, need.getOrDefault(num + k, 0) + 1);
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    // Follow-up 3: Handle duplicates efficiently (already handled in main approach)
    public boolean isPossibleDuplicates(int[] nums) {
        return isPossible(nums); // Same implementation works for duplicates
    }

    // Helper: Validate subsequences
    private boolean validateSubsequences(List<List<Integer>> subsequences) {
        for (List<Integer> seq : subsequences) {
            if (seq.size() < 3)
                return false;

            for (int i = 1; i < seq.size(); i++) {
                if (seq.get(i) != seq.get(i - 1) + 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SplitArrayIntoConsecutiveSubsequences solution = new SplitArrayIntoConsecutiveSubsequences();

        // Test case 1: Basic valid case
        int[] nums1 = { 1, 2, 3, 3, 4, 5 };
        System.out.println("Test 1 - Basic valid case:");
        System.out.println("Expected: true, Got: " + solution.isPossible(nums1));
        System.out.println("PQ approach: " + solution.isPossiblePQ(nums1));

        // Test case 2: Invalid case
        int[] nums2 = { 1, 2, 3, 3, 4, 4, 5, 5 };
        System.out.println("\nTest 2 - Invalid case:");
        System.out.println("Expected: true, Got: " + solution.isPossible(nums2));

        // Test case 3: Cannot form valid subsequences
        int[] nums3 = { 1, 2, 3, 4, 4, 5 };
        System.out.println("\nTest 3 - Cannot form valid:");
        System.out.println("Expected: false, Got: " + solution.isPossible(nums3));

        // Test case 4: Multiple subsequences
        int[] nums4 = { 1, 2, 3, 4, 5, 6 };
        System.out.println("\nTest 4 - Single long sequence:");
        System.out.println("Expected: true, Got: " + solution.isPossible(nums4));

        // Test case 5: Gaps in sequence
        int[] nums5 = { 1, 2, 3, 5, 6, 7 };
        System.out.println("\nTest 5 - Gaps in sequence:");
        System.out.println("Expected: true, Got: " + solution.isPossible(nums5));

        // Edge case: Exactly length 3
        int[] nums6 = { 1, 2, 3 };
        System.out.println("\nEdge case - Exactly length 3:");
        System.out.println("Expected: true, Got: " + solution.isPossible(nums6));

        // Edge case: Too short
        int[] nums7 = { 1, 2 };
        System.out.println("\nEdge case - Too short:");
        System.out.println("Expected: false, Got: " + solution.isPossible(nums7));

        // Follow-up 1: Get actual subsequences
        System.out.println("\nFollow-up 1 - Actual subsequences:");
        List<List<Integer>> subsequences = solution.getSubsequences(nums1);
        System.out.println("Subsequences: " + subsequences);
        System.out.println("Valid: " + solution.validateSubsequences(subsequences));

        // Follow-up 2: Minimum length k
        System.out.println("\nFollow-up 2 - Minimum length k=4:");
        int[] nums8 = { 1, 2, 3, 4, 5, 6, 7, 8 };
        System.out.println("With k=4: " + solution.isPossibleMinK(nums8, 4));

        // Follow-up 3: Handle duplicates
        int[] nums9 = { 1, 1, 1, 2, 2, 2, 3, 3, 3 };
        System.out.println("\nFollow-up 3 - Handle duplicates:");
        System.out.println("Expected: true, Got: " + solution.isPossibleDuplicates(nums9));

        // Performance test
        int[] largeNums = new int[10000];
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = (i % 1000) + 1;
        }
        Arrays.sort(largeNums);
        long startTime = System.currentTimeMillis();
        solution.isPossible(largeNums);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (10k elements): " + (endTime - startTime) + "ms");

        // Verification: Show subsequence formation
        System.out.println("\nVerification for [1,2,3,3,4,5]:");
        System.out.println("Possible split: [1,2,3] and [3,4,5]");
        System.out.println("Both have length >= 3 and are consecutive");
    }
}
