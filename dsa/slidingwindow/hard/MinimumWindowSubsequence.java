package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 727: Minimum Window Subsequence
 * https://leetcode.com/problems/minimum-window-subsequence/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft
 * Frequency: High
 *
 * Description: Given strings s and t, return the minimum window substring of s
 * such that every character in t is a subsequence of the window.
 * If there is no such window, return the empty string "".
 *
 * Constraints:
 * - 1 <= s.length <= 2 * 10^4
 * - 1 <= t.length <= 100
 * - s and t consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Find all minimum windows of the same length.
 * 2. Handle multiple target subsequences simultaneously.
 * 3. Find the window with maximum number of target subsequences.
 * 4. Handle wildcard characters in the pattern.
 */
public class MinimumWindowSubsequence {

    // Approach 1: Two pointers with greedy matching - O(s * t) time
    public String minWindow(String s, String t) {
        int sLen = s.length(), tLen = t.length();
        int minLen = Integer.MAX_VALUE;
        String result = "";
        int sIdx = 0;
        while (sIdx < sLen) {
            int tIdx = 0, start = sIdx;
            while (sIdx < sLen && tIdx < tLen) {
                if (s.charAt(sIdx) == t.charAt(tIdx))
                    tIdx++;
                sIdx++;
            }
            if (tIdx != tLen)
                break;
            sIdx--;
            tIdx--;
            while (tIdx >= 0) {
                if (s.charAt(sIdx) == t.charAt(tIdx))
                    tIdx--;
                sIdx--;
            }
            sIdx++;
            if (start - sIdx + 1 < minLen) {
                minLen = start - sIdx + 1;
                result = s.substring(sIdx, start + 1);
            }
            sIdx++;
        }
        return result;
    }

    // Approach 2: Dynamic Programming - O(s * t) time, O(s * t) space
    public String minWindowDP(String s, String t) {
        int sLen = s.length(), tLen = t.length();
        int[][] dp = new int[sLen + 1][tLen + 1];
        for (int i = 0; i <= sLen; i++)
            Arrays.fill(dp[i], -1);
        for (int i = 0; i <= sLen; i++)
            dp[i][0] = i;
        for (int i = 1; i <= sLen; i++) {
            for (int j = 1; j <= Math.min(i, tLen); j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = dp[i - 1][j];
            }
        }
        int minLen = Integer.MAX_VALUE;
        String result = "";
        for (int i = tLen; i <= sLen; i++) {
            if (dp[i][tLen] != -1) {
                int len = i - dp[i][tLen];
                if (len < minLen) {
                    minLen = len;
                    result = s.substring(dp[i][tLen], i);
                }
            }
        }
        return result;
    }

    // Approach 3: Optimized DP with space compression - O(s * t) time, O(t) space
    public String minWindowDPOptimized(String s, String t) {
        int sLen = s.length(), tLen = t.length();
        int[] prev = new int[tLen + 1], curr = new int[tLen + 1];
        Arrays.fill(prev, -1);
        Arrays.fill(curr, -1);
        prev[0] = 0;
        int minLen = Integer.MAX_VALUE;
        String result = "";
        for (int i = 1; i <= sLen; i++) {
            curr[0] = i;
            for (int j = 1; j <= Math.min(i, tLen); j++) {
                if (s.charAt(i - 1) == t.charAt(j - 1))
                    curr[j] = prev[j - 1];
                else
                    curr[j] = prev[j];
            }
            if (curr[tLen] != -1) {
                int len = i - curr[tLen];
                if (len < minLen) {
                    minLen = len;
                    result = s.substring(curr[tLen], i);
                }
            }
            int[] temp = prev;
            prev = curr;
            curr = temp;
            Arrays.fill(curr, -1);
        }
        return result;
    }

    // Follow-up 1: Find all minimum windows of the same length
    public List<String> findAllMinWindows(String s, String t) {
        List<String> result = new ArrayList<>();
        String minWindow = minWindow(s, t);
        if (minWindow.isEmpty())
            return result;
        int minLen = minWindow.length(), sLen = s.length(), tLen = t.length();
        int sIdx = 0;
        while (sIdx < sLen) {
            int tIdx = 0, start = sIdx;
            while (sIdx < sLen && tIdx < tLen) {
                if (s.charAt(sIdx) == t.charAt(tIdx))
                    tIdx++;
                sIdx++;
            }
            if (tIdx != tLen)
                break;
            sIdx--;
            tIdx--;
            while (tIdx >= 0) {
                if (s.charAt(sIdx) == t.charAt(tIdx))
                    tIdx--;
                sIdx--;
            }
            sIdx++;
            if (start - sIdx + 1 == minLen)
                result.add(s.substring(sIdx, start + 1));
            sIdx++;
        }
        return result;
    }

    // Follow-up 2: Handle multiple target subsequences
    public String minWindowMultipleTargets(String s, String[] targets) {
        String result = "";
        int minLen = Integer.MAX_VALUE;
        for (String target : targets) {
            String window = minWindow(s, target);
            if (!window.isEmpty() && window.length() < minLen) {
                minLen = window.length();
                result = window;
            }
        }
        return result;
    }

    // Follow-up 3: Find window with maximum number of target subsequences
    public String maxSubsequenceWindow(String s, String t) {
        int maxCount = 0;
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + t.length(); j <= s.length(); j++) {
                String window = s.substring(i, j);
                int count = countSubsequences(window, t);
                if (count > maxCount) {
                    maxCount = count;
                    result = window;
                }
            }
        }
        return result;
    }

    // Follow-up 4: Handle wildcard characters ('?' matches any, '*' matches any
    // sequence)
    public String minWindowWithWildcard(String s, String t) {
        return minWindowWildcardHelper(s, t, 0, 0, new HashMap<>());
    }

    private String minWindowWildcardHelper(String s, String t, int sIdx, int tIdx, Map<String, String> memo) {
        String key = sIdx + "," + tIdx;
        if (memo.containsKey(key))
            return memo.get(key);
        if (tIdx == t.length()) {
            memo.put(key, "");
            return "";
        }
        if (sIdx == s.length()) {
            memo.put(key, null);
            return null;
        }
        String result = null;
        char tChar = t.charAt(tIdx);
        if (tChar == '*') {
            String option1 = minWindowWildcardHelper(s, t, sIdx, tIdx + 1, memo);
            for (int i = sIdx + 1; i <= s.length(); i++) {
                String option2 = minWindowWildcardHelper(s, t, i, tIdx + 1, memo);
                if (option2 != null) {
                    option2 = s.substring(sIdx, i) + option2;
                    if (option1 == null || option2.length() < option1.length())
                        option1 = option2;
                }
            }
            result = option1;
        } else {
            for (int i = sIdx; i < s.length(); i++) {
                if (tChar == '?' || s.charAt(i) == tChar) {
                    String rest = minWindowWildcardHelper(s, t, i + 1, tIdx + 1, memo);
                    if (rest != null) {
                        String candidate = s.substring(sIdx, i + 1) + rest;
                        if (result == null || candidate.length() < result.length())
                            result = candidate;
                    }
                }
            }
        }
        memo.put(key, result);
        return result;
    }

    // Helper: Count how many times t appears as subsequence in s
    private int countSubsequences(String s, String t) {
        int sLen = s.length(), tLen = t.length();
        int[][] dp = new int[sLen + 1][tLen + 1];
        for (int i = 0; i <= sLen; i++)
            dp[i][0] = 1;
        for (int i = 1; i <= sLen; i++) {
            for (int j = 1; j <= tLen; j++) {
                dp[i][j] = dp[i - 1][j];
                if (s.charAt(i - 1) == t.charAt(j - 1))
                    dp[i][j] += dp[i - 1][j - 1];
            }
        }
        return dp[sLen][tLen];
    }

    // Helper: Is t a subsequence of s
    private boolean isSubsequence(String s, String t) {
        int sIdx = 0, tIdx = 0;
        while (sIdx < s.length() && tIdx < t.length()) {
            if (s.charAt(sIdx) == t.charAt(tIdx))
                tIdx++;
            sIdx++;
        }
        return tIdx == t.length();
    }

    // Helper: Get all subsequence positions
    public List<List<Integer>> getAllSubsequencePositions(String s, String t) {
        List<List<Integer>> result = new ArrayList<>();
        findAllSubsequencePositions(s, t, 0, 0, new ArrayList<>(), result);
        return result;
    }

    private void findAllSubsequencePositions(String s, String t, int sIdx, int tIdx, List<Integer> current,
            List<List<Integer>> result) {
        if (tIdx == t.length()) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (sIdx == s.length())
            return;
        for (int i = sIdx; i < s.length(); i++) {
            if (s.charAt(i) == t.charAt(tIdx)) {
                current.add(i);
                findAllSubsequencePositions(s, t, i + 1, tIdx + 1, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumWindowSubsequence solution = new MinimumWindowSubsequence();

        // Test case 1: Basic case
        String s1 = "abcdebdde", t1 = "bde";
        System.out.println("Test 1 - S: " + s1 + ", T: " + t1 + " Expected: bcde");
        System.out.println("Approach 1: \"" + solution.minWindow(s1, t1) + "\"");
        System.out.println("Approach 2: \"" + solution.minWindowDP(s1, t1) + "\"");
        System.out.println("Approach 3: \"" + solution.minWindowDPOptimized(s1, t1) + "\"");

        // Test case 2: No valid window
        String s2 = "abc", t2 = "def";
        System.out.println("\nTest 2 - S: " + s2 + ", T: " + t2 + " Expected: \"\" (no valid window)");
        System.out.println("Result: \"" + solution.minWindow(s2, t2) + "\"");

        // Test case 3: Entire string is minimum window
        String s3 = "abc", t3 = "abc";
        System.out.println("\nTest 3 - S: " + s3 + ", T: " + t3 + " Expected: abc (entire string)");
        System.out.println("Result: \"" + solution.minWindow(s3, t3) + "\"");

        // Test case 4: Multiple valid windows
        String s4 = "abcdefabcde", t4 = "ace";
        System.out.println("\nTest 4 - S: " + s4 + ", T: " + t4 + " Expected: abcde (multiple windows)");
        System.out.println("Result: \"" + solution.minWindow(s4, t4) + "\"");

        // Test case 5: Single character target
        String s5 = "abcdefg", t5 = "c";
        System.out.println("\nTest 5 - S: " + s5 + ", T: " + t5 + " Expected: c (single char)");
        System.out.println("Result: \"" + solution.minWindow(s5, t5) + "\"");

        // Test case 6: Repeated characters in pattern
        String s6 = "abababab", t6 = "aba";
        System.out.println("\nTest 6 - S: " + s6 + ", T: " + t6 + " Expected: abab (repeated chars)");
        System.out.println("Result: \"" + solution.minWindow(s6, t6) + "\"");

        // Test case 7: Large input
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sb.append((char) ('a' + (i % 26)));
        sb.append("xyz");
        String s7 = sb.toString(), t7 = "xyz";
        System.out.println("\nTest 7 - Large input: " + s7.length() + " chars, T: " + t7);
        long startTime = System.nanoTime();
        String result7 = solution.minWindow(s7, t7);
        long endTime = System.nanoTime();
        System.out.println("Result: \"" + result7 + "\" (Time: " + (endTime - startTime) / 1_000_000 + " ms)");

        // Test case 8: Pattern longer than string
        String s8 = "ab", t8 = "abc";
        System.out.println("\nTest 8 - S: " + s8 + ", T: " + t8 + " Expected: \"\" (pattern longer)");
        System.out.println("Result: \"" + solution.minWindow(s8, t8) + "\"");

        // Test Follow-ups
        System.out.println("\nFollow-up tests:");
        List<String> allMinWindows = solution.findAllMinWindows("abcdebddebcde", "bde");
        System.out.println("All min windows for 'abcdebddebcde' with 'bde': " + allMinWindows);

        String[] targets = { "bde", "ace", "xyz" };
        System.out.println("Min window for multiple targets ['bde', 'ace', 'xyz'] in 'abcdebdde': \"" +
                solution.minWindowMultipleTargets("abcdebdde", targets) + "\"");

        System.out.println("Window with max subsequences 'aba' in 'abababab': \"" +
                solution.maxSubsequenceWindow("abababab", "aba") + "\"");

        System.out.println("Wildcard 'a?c' in 'abcadc': \"" +
                solution.minWindowWithWildcard("abcadc", "a?c") + "\"");

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty S: \"" + solution.minWindow("", "a") + "\"");
        System.out.println("Empty T: \"" + solution.minWindow("a", "") + "\"");
        System.out.println("Single char match: \"" + solution.minWindow("a", "a") + "\"");
        System.out.println("Single char no match: \"" + solution.minWindow("a", "b") + "\"");

        // Subsequence count analysis
        String testS = "rabbbit", testT = "rabbit";
        System.out.println("\nSubsequence analysis:");
        System.out.println("Count of '" + testT + "' in '" + testS + "': " +
                solution.countSubsequences(testS, testT));
        System.out.println("Is subsequence: " + solution.isSubsequence(testS, testT));

        List<List<Integer>> positions = solution.getAllSubsequencePositions("abcabc", "abc");
        System.out.println("All positions of 'abc' in 'abcabc': " + positions);

        // Performance comparison
        System.out.println("\nPerformance comparison:");
        String perfS = sb.toString(), perfT = "abcdefghij";
        startTime = System.nanoTime();
        String result1 = solution.minWindow(perfS, perfT);
        long time1 = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        String result2 = solution.minWindowDP(perfS, perfT);
        long time2 = System.nanoTime() - startTime;
        startTime = System.nanoTime();
        String result3 = solution.minWindowDPOptimized(perfS, perfT);
        long time3 = System.nanoTime() - startTime;
        System.out.println("Two pointers: " + time1 / 1_000_000 + " ms, result: \"" + result1 + "\"");
        System.out.println("DP: " + time2 / 1_000_000 + " ms, result: \"" + result2 + "\"");
        System.out.println("DP optimized: " + time3 / 1_000_000 + " ms, result: \"" + result3 + "\"");

        // Stress test with random strings
        System.out.println("\nStress test:");
        Random random = new Random(42);
        for (int len : new int[] { 10, 100, 1000 }) {
            StringBuilder randomS = new StringBuilder();
            StringBuilder randomT = new StringBuilder();
            for (int i = 0; i < len; i++)
                randomS.append((char) ('a' + random.nextInt(26)));
            for (int i = 0; i < Math.min(5, len); i++)
                randomT.append((char) ('a' + random.nextInt(26)));
            startTime = System.nanoTime();
            String randomResult = solution.minWindow(randomS.toString(), randomT.toString());
            endTime = System.nanoTime();
            System.out.println("Length " + len + ": Window length " + randomResult.length() +
                    " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");
        }
    }
}
