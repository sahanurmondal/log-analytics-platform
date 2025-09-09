package recursion.hard;

import java.util.*;

/**
 * LeetCode 1593: Split a String Into the Max Number of Unique Substrings
 * URL:
 * https://leetcode.com/problems/split-a-string-into-the-max-number-of-unique-substrings/
 * Difficulty: Medium (but in hard folder due to backtracking complexity)
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple
 * Frequency: Medium
 * 
 * Description:
 * Given a string s, return the maximum number of unique substrings that the
 * given string can be split into. You can split string s into any list of
 * non-empty substrings, but all substrings must be unique.
 * 
 * Constraints:
 * - 1 <= s.length <= 16
 * - s contains only lowercase English letters
 * - All substrings in the split must be non-empty and unique
 * 
 * Follow-up Questions:
 * 1. Can you optimize using pruning techniques?
 * 2. How would you solve it iteratively using dynamic programming?
 * 3. How would you handle larger strings efficiently?
 * 4. How would you find all possible splits with maximum length?
 * 5. How would you extend to handle case-insensitive splits?
 */
public class SplitStringIntoMaxNumberOfUniqueSubstrings {
    private int maxSplits = 0;

    // Approach 1: Backtracking with pruning
    public int maxUniqueSplit(String s) {
        maxSplits = 0;
        backtrack(s, 0, new HashSet<>());
        return maxSplits;
    }

    /**
     * Backtracking with pruning
     * Time: O(2^n), Space: O(n)
     */
    private void backtrack(String s, int start, Set<String> used) {
        // Pruning: if current split count + remaining chars <= maxSplits, skip
        if (used.size() + (s.length() - start) <= maxSplits) {
            return;
        }

        if (start == s.length()) {
            maxSplits = Math.max(maxSplits, used.size());
            return;
        }

        // Try all possible substrings starting from current position
        for (int end = start + 1; end <= s.length(); end++) {
            String substring = s.substring(start, end);

            if (!used.contains(substring)) {
                used.add(substring);
                backtrack(s, end, used);
                used.remove(substring);
            }
        }
    }

    // Approach 2: Optimized backtracking with better pruning
    public int maxUniqueSplitOptimized(String s) {
        return backtrackOptimized(s, 0, new HashSet<>(), 0);
    }

    private int backtrackOptimized(String s, int start, Set<String> used, int currentMax) {
        // Pruning: if we can't beat current maximum, return early
        if (used.size() + (s.length() - start) <= currentMax) {
            return currentMax;
        }

        if (start == s.length()) {
            return Math.max(currentMax, used.size());
        }

        int maxResult = currentMax;

        for (int end = start + 1; end <= s.length(); end++) {
            String substring = s.substring(start, end);

            if (!used.contains(substring)) {
                used.add(substring);
                maxResult = Math.max(maxResult,
                        backtrackOptimized(s, end, used, maxResult));
                used.remove(substring);
            }
        }

        return maxResult;
    }

    // Approach 3: Memoization with bitmask (for strings up to 16 chars)
    public int maxUniqueSplitMemo(String s) {
        Map<String, Integer> memo = new HashMap<>();
        return dfsWithMemo(s, 0, new HashSet<>(), memo);
    }

    private int dfsWithMemo(String s, int start, Set<String> used, Map<String, Integer> memo) {
        if (start == s.length()) {
            return 0;
        }

        String key = start + ":" + used.toString();
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int maxResult = 0;

        for (int end = start + 1; end <= s.length(); end++) {
            String substring = s.substring(start, end);

            if (!used.contains(substring)) {
                used.add(substring);
                maxResult = Math.max(maxResult,
                        1 + dfsWithMemo(s, end, used, memo));
                used.remove(substring);
            }
        }

        memo.put(key, maxResult);
        return maxResult;
    }

    // Approach 4: Iterative using bit manipulation (for small strings)
    public int maxUniqueSplitIterative(String s) {
        int n = s.length();
        int maxSplit = 0;

        // Try all possible ways to split the string
        for (int mask = 1; mask < (1 << (n - 1)); mask++) {
            Set<String> splits = new HashSet<>();
            int start = 0;
            boolean valid = true;

            for (int i = 0; i < n - 1; i++) {
                if ((mask & (1 << i)) != 0) {
                    String substring = s.substring(start, i + 1);
                    if (splits.contains(substring)) {
                        valid = false;
                        break;
                    }
                    splits.add(substring);
                    start = i + 1;
                }
            }

            if (valid) {
                String lastSubstring = s.substring(start);
                if (!splits.contains(lastSubstring)) {
                    splits.add(lastSubstring);
                    maxSplit = Math.max(maxSplit, splits.size());
                }
            }
        }

        return maxSplit;
    }

    // Follow-up: Get all splits with maximum length
    public List<List<String>> getAllMaxSplits(String s) {
        List<List<String>> allMaxSplits = new ArrayList<>();
        int maxLen = maxUniqueSplit(s);

        findAllSplitsWithLength(s, 0, new HashSet<>(), new ArrayList<>(),
                maxLen, allMaxSplits);

        return allMaxSplits;
    }

    private void findAllSplitsWithLength(String s, int start, Set<String> used,
            List<String> current, int targetLen,
            List<List<String>> result) {
        if (start == s.length()) {
            if (current.size() == targetLen) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        // Pruning: can't reach target length
        if (current.size() + (s.length() - start) < targetLen) {
            return;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            String substring = s.substring(start, end);

            if (!used.contains(substring)) {
                used.add(substring);
                current.add(substring);

                findAllSplitsWithLength(s, end, used, current, targetLen, result);

                current.remove(current.size() - 1);
                used.remove(substring);
            }
        }
    }

    // Follow-up: Count total number of valid splits
    public int countAllValidSplits(String s) {
        return countSplits(s, 0, new HashSet<>());
    }

    private int countSplits(String s, int start, Set<String> used) {
        if (start == s.length()) {
            return 1;
        }

        int count = 0;

        for (int end = start + 1; end <= s.length(); end++) {
            String substring = s.substring(start, end);

            if (!used.contains(substring)) {
                used.add(substring);
                count += countSplits(s, end, used);
                used.remove(substring);
            }
        }

        return count;
    }

    public static void main(String[] args) {
        SplitStringIntoMaxNumberOfUniqueSubstrings solution = new SplitStringIntoMaxNumberOfUniqueSubstrings();

        // Test Case 1: Basic example
        System.out.println("ababccc: " + solution.maxUniqueSplit("ababccc")); // 5

        // Test Case 2: Simple case
        System.out.println("aba: " + solution.maxUniqueSplit("aba")); // 2

        // Test Case 3: Repeated characters
        System.out.println("aa: " + solution.maxUniqueSplit("aa")); // 1

        // Test Case 4: All unique characters
        System.out.println("abcdef: " + solution.maxUniqueSplit("abcdef")); // 6

        // Test Case 5: All same characters
        System.out.println("aaaa: " + solution.maxUniqueSplit("aaaa")); // 1

        // Test Case 6: Single character
        System.out.println("a: " + solution.maxUniqueSplit("a")); // 1

        // Test Case 7: Optimized approach
        System.out.println("Optimized ababccc: " + solution.maxUniqueSplitOptimized("ababccc")); // 5

        // Test Case 8: Memoization approach
        System.out.println("Memo aba: " + solution.maxUniqueSplitMemo("aba")); // 2

        // Test Case 9: Iterative approach
        System.out.println("Iterative aa: " + solution.maxUniqueSplitIterative("aa")); // 1

        // Test Case 10: Complex pattern
        System.out.println("abcabc: " + solution.maxUniqueSplit("abcabc")); // 4

        // Test Case 11: All max splits
        List<List<String>> allSplits = solution.getAllMaxSplits("aba");
        System.out.println("All max splits for 'aba': " + allSplits);

        // Test Case 12: Count all valid splits
        System.out.println("Count all splits for 'aba': " + solution.countAllValidSplits("aba"));

        // Test Case 13: Edge case with alternating pattern
        System.out.println("abab: " + solution.maxUniqueSplit("abab")); // 3

        // Test Case 14: Palindrome
        System.out.println("abba: " + solution.maxUniqueSplit("abba")); // 3

        // Test Case 15: Long unique string
        System.out.println("abcdefghij: " + solution.maxUniqueSplit("abcdefghij")); // 10
    }
}
