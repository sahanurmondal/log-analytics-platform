package slidingwindow.medium;

/**
 * LeetCode 1208: Get Equal Substrings Within Budget
 * https://leetcode.com/problems/get-equal-substrings-within-budget/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given two strings s and t of the same length and an integer
 * maxCost, return the maximum length of a substring of s that can be changed to
 * t with total cost <= maxCost.
 *
 * Constraints:
 * - 1 <= s.length == t.length <= 10^5
 * - 0 <= maxCost <= 10^6
 * - s and t consist of only lowercase English letters
 *
 * Follow-up Questions:
 * 1. What if s and t have different lengths?
 * 2. How to return the actual substring?
 * 3. How to solve for very large maxCost efficiently?
 */
public class GetEqualSubstringsWithinBudget {
    // Approach 1: Sliding Window - O(n) time, O(1) space
    public int equalSubstring(String s, String t, int maxCost) {
        int left = 0, cost = 0, maxLen = 0;
        for (int right = 0; right < s.length(); right++) {
            cost += Math.abs(s.charAt(right) - t.charAt(right));
            while (cost > maxCost) {
                cost -= Math.abs(s.charAt(left) - t.charAt(left));
                left++;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Approach 2: Prefix Sum + Binary Search - O(n log n) time, O(n) space
    public int equalSubstringPrefixSum(String s, String t, int maxCost) {
        int n = s.length();
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + Math.abs(s.charAt(i) - t.charAt(i));
        }
        int maxLen = 0;
        for (int right = 1; right <= n; right++) {
            int left = java.util.Arrays.binarySearch(prefix, 0, right, prefix[right] - maxCost);
            if (left < 0)
                left = -left - 1;
            maxLen = Math.max(maxLen, right - left);
        }
        return maxLen;
    }

    // Follow-up 1: Return actual substring
    public String getMaxEqualSubstring(String s, String t, int maxCost) {
        int left = 0, cost = 0, maxLen = 0, start = 0;
        for (int right = 0; right < s.length(); right++) {
            cost += Math.abs(s.charAt(right) - t.charAt(right));
            while (cost > maxCost) {
                cost -= Math.abs(s.charAt(left) - t.charAt(left));
                left++;
            }
            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }
        }
        return s.substring(start, start + maxLen);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        GetEqualSubstringsWithinBudget sol = new GetEqualSubstringsWithinBudget();
        // Test 1: Basic
        System.out.println("Test 1: Expected 3 -> " + sol.equalSubstring("abcd", "bcdf", 3));
        // Test 2: All equal
        System.out.println("Test 2: Expected 4 -> " + sol.equalSubstring("abcd", "abcd", 0));
        // Test 3: Large cost
        System.out.println("Test 3: Expected 4 -> " + sol.equalSubstring("abcd", "zzzz", 100));
        // Test 4: Prefix sum approach
        System.out.println("Test 4: Expected 3 -> " + sol.equalSubstringPrefixSum("abcd", "bcdf", 3));
        // Test 5: Get actual substring
        System.out.println("Test 5: Expected 'abc' -> " + sol.getMaxEqualSubstring("abcd", "bcdf", 3));
        // Test 6: Edge case, maxCost = 0
        System.out.println("Test 6: Expected 1 -> " + sol.equalSubstring("abcd", "abce", 0));
        // Test 7: Edge case, s and t are empty
        System.out.println("Test 7: Expected 0 -> " + sol.equalSubstring("", "", 10));
        // Test 8: maxCost < min cost
        System.out.println("Test 8: Expected 0 -> " + sol.equalSubstring("a", "z", 0));
        // Test 9: Large input
        String s9 = "a".repeat(10000), t9 = "b".repeat(10000);
        System.out.println("Test 9: Large input -> " + sol.equalSubstring(s9, t9, 10000));
        // Test 10: All different
        System.out.println("Test 10: Expected 1 -> " + sol.equalSubstring("abc", "def", 2));
        // Test 11: maxCost = s.length
        System.out.println("Test 11: Expected 4 -> " + sol.equalSubstring("abcd", "bcdf", 10));
        // Test 12: maxCost = 1
        System.out.println("Test 12: Expected 1 -> " + sol.equalSubstring("abcd", "bcdf", 1));
        // Test 13: maxCost = s.length * 25
        System.out.println("Test 13: Expected 4 -> " + sol.equalSubstring("abcd", "zzzz", 100));
        // Test 14: s and t are identical
        System.out.println("Test 14: Expected 4 -> " + sol.equalSubstring("abcd", "abcd", 100));
        // Test 15: s and t differ at one position
        System.out.println("Test 15: Expected 3 -> " + sol.equalSubstring("abcde", "abfde", 1));
    }
}
