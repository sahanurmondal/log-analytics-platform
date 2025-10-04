package strings.medium;

import java.util.*;

/**
 * LeetCode 14: Longest Common Prefix
 * https://leetcode.com/problems/longest-common-prefix/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Write a function to find the longest common prefix string
 * amongst an array of strings.
 *
 * Constraints:
 * - 1 <= strs.length <= 200
 * - 0 <= strs[i].length <= 200
 * - strs[i] consists of only lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you use binary search?
 * 2. Can you find longest common suffix?
 * 3. Can you handle case-insensitive comparison?
 */
public class LongestCommonPrefix {

    // Approach 1: Vertical scanning (O(S) time, where S is sum of all string
    // lengths)
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        for (int i = 0; i < strs[0].length(); i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < strs.length; j++) {
                if (i >= strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];
    }

    // Follow-up 1: Binary search approach
    public String longestCommonPrefixBinarySearch(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        int minLen = Arrays.stream(strs).mapToInt(String::length).min().orElse(0);
        int low = 0, high = minLen;

        while (low < high) {
            int mid = (low + high + 1) / 2;
            if (isCommonPrefix(strs, mid))
                low = mid;
            else
                high = mid - 1;
        }
        return strs[0].substring(0, low);
    }

    // Follow-up 2: Longest common suffix
    public String longestCommonSuffix(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        String first = strs[0];
        for (int i = first.length() - 1; i >= 0; i--) {
            char c = first.charAt(i);
            for (int j = 1; j < strs.length; j++) {
                int idx = strs[j].length() - (first.length() - i);
                if (idx < 0 || strs[j].charAt(idx) != c) {
                    return first.substring(i + 1);
                }
            }
        }
        return first;
    }

    // Follow-up 3: Case-insensitive comparison
    public String longestCommonPrefixIgnoreCase(String[] strs) {
        if (strs == null || strs.length == 0)
            return "";
        String[] lowerStrs = Arrays.stream(strs).map(String::toLowerCase).toArray(String[]::new);
        return longestCommonPrefix(lowerStrs);
    }

    // Helper: Check if length is valid common prefix
    private boolean isCommonPrefix(String[] strs, int len) {
        String prefix = strs[0].substring(0, len);
        for (int i = 1; i < strs.length; i++) {
            if (!strs[i].startsWith(prefix))
                return false;
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestCommonPrefix solution = new LongestCommonPrefix();

        // Test case 1: Basic case
        String[] strs1 = { "flower", "flow", "flight" };
        System.out.println("Test 1 - strs: " + Arrays.toString(strs1) + " Expected: fl");
        System.out.println("Result: " + solution.longestCommonPrefix(strs1));
        System.out.println("Binary search: " + solution.longestCommonPrefixBinarySearch(strs1));

        // Test case 2: Longest common suffix
        System.out.println("\nTest 2 - Longest common suffix:");
        System.out.println("Result: " + solution.longestCommonSuffix(strs1));

        // Test case 3: Case-insensitive
        String[] strs2 = { "Flower", "FLOW", "flight" };
        System.out.println("\nTest 3 - Case-insensitive:");
        System.out.println("Result: " + solution.longestCommonPrefixIgnoreCase(strs2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: '" + solution.longestCommonPrefix(new String[] {}) + "'");
        System.out.println("Single string: '" + solution.longestCommonPrefix(new String[] { "hello" }) + "'");
        System.out.println(
                "No common prefix: '" + solution.longestCommonPrefix(new String[] { "dog", "racecar", "car" }) + "'");
        System.out.println(
                "Empty string in array: '" + solution.longestCommonPrefix(new String[] { "abc", "", "ab" }) + "'");

        // Stress test
        System.out.println("\nStress test:");
        String[] largeStrs = new String[1000];
        for (int i = 0; i < 1000; i++) {
            largeStrs[i] = "commonprefix" + i;
        }
        long start = System.nanoTime();
        String result = solution.longestCommonPrefixBinarySearch(largeStrs);
        long end = System.nanoTime();
        System.out.println("Result: '" + result + "' (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
