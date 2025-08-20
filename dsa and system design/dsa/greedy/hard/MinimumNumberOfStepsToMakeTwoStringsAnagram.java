package greedy.hard;

import java.util.*;

/**
 * Minimum Number of Steps to Make Two Strings Anagram
 * 
 * LeetCode Problem: 1347. Minimum Number of Steps to Make Two Strings Anagram
 * URL:
 * https://leetcode.com/problems/minimum-number-of-steps-to-make-two-strings-anagram/
 * 
 * Company Tags: Microsoft, Amazon, Google, Facebook, Apple
 * Difficulty: Hard (Medium on LeetCode but includes complex variations)
 * 
 * Description:
 * Given two strings s and t of equal length, return the minimum number of steps
 * to make t an anagram of s. In one step, you can replace any character in t
 * with another character.
 * 
 * Constraints:
 * - 1 <= s.length == t.length <= 5 * 10^4
 * - s and t consist of lowercase English letters only
 * 
 * Follow-ups:
 * 1. Can you solve with different approaches?
 * 2. What if strings have different lengths?
 * 3. What if we can add/remove characters?
 * 4. What if we want to minimize different types of operations?
 * 5. What about Unicode characters?
 */
public class MinimumNumberOfStepsToMakeTwoStringsAnagram {

    /**
     * Frequency counting approach - count character differences
     * Time: O(n), Space: O(1) - fixed alphabet size
     */
    public int minSteps(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return -1; // Invalid input
        }

        int[] freq = new int[26]; // For lowercase letters a-z

        // Count characters in s (positive) and t (negative)
        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }

        // Count how many characters in t need to be changed
        int steps = 0;
        for (int count : freq) {
            if (count > 0) { // Characters that s has more than t
                steps += count;
            }
        }

        return steps;
    }

    /**
     * HashMap approach - more flexible for different character sets
     * Time: O(n), Space: O(k) where k is unique characters
     */
    public int minStepsHashMap(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return -1;
        }

        Map<Character, Integer> sCount = new HashMap<>();
        Map<Character, Integer> tCount = new HashMap<>();

        // Count characters in both strings
        for (char c : s.toCharArray()) {
            sCount.put(c, sCount.getOrDefault(c, 0) + 1);
        }

        for (char c : t.toCharArray()) {
            tCount.put(c, tCount.getOrDefault(c, 0) + 1);
        }

        int steps = 0;
        // Count characters that need to be added to t
        for (Map.Entry<Character, Integer> entry : sCount.entrySet()) {
            char c = entry.getKey();
            int sFreq = entry.getValue();
            int tFreq = tCount.getOrDefault(c, 0);

            if (sFreq > tFreq) {
                steps += sFreq - tFreq;
            }
        }

        return steps;
    }

    /**
     * Single pass approach - count differences in one pass
     * Time: O(n), Space: O(1)
     */
    public int minStepsSinglePass(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return -1;
        }

        int[] diff = new int[26];

        for (int i = 0; i < s.length(); i++) {
            diff[s.charAt(i) - 'a']++;
            diff[t.charAt(i) - 'a']--;
        }

        int steps = 0;
        for (int d : diff) {
            steps += Math.max(0, d);
        }

        return steps;
    }

    /**
     * Follow-up 1: Two different approaches comparison
     * Approach 1: Count what needs to be added
     * Approach 2: Count what needs to be removed
     */
    public int[] minStepsBothDirections(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return new int[] { -1, -1 };
        }

        int[] freq = new int[26];

        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }

        int toAdd = 0, toRemove = 0;
        for (int count : freq) {
            if (count > 0) {
                toAdd += count;
            } else if (count < 0) {
                toRemove += Math.abs(count);
            }
        }

        return new int[] { toAdd, toRemove }; // Both should be equal
    }

    /**
     * Follow-up 2: Different length strings - minimum operations
     * Time: O(n + m), Space: O(1)
     */
    public int minStepsDifferentLengths(String s, String t) {
        if (s == null || t == null) {
            return -1;
        }

        int[] sFreq = new int[26];
        int[] tFreq = new int[26];

        for (char c : s.toCharArray()) {
            sFreq[c - 'a']++;
        }

        for (char c : t.toCharArray()) {
            tFreq[c - 'a']++;
        }

        int operations = 0;
        for (int i = 0; i < 26; i++) {
            operations += Math.abs(sFreq[i] - tFreq[i]);
        }

        return operations / 2; // Each operation affects both strings
    }

    /**
     * Follow-up 3: Add/Remove characters allowed
     * Returns [replacements, additions, deletions]
     * Time: O(n + m), Space: O(1)
     */
    public int[] minStepsWithAddRemove(String s, String t) {
        if (s == null || t == null) {
            return new int[] { -1, -1, -1 };
        }

        int[] sFreq = new int[26];
        int[] tFreq = new int[26];

        for (char c : s.toCharArray()) {
            sFreq[c - 'a']++;
        }

        for (char c : t.toCharArray()) {
            tFreq[c - 'a']++;
        }

        int replacements = 0, additions = 0, deletions = 0;

        for (int i = 0; i < 26; i++) {
            int diff = sFreq[i] - tFreq[i];
            if (diff > 0) {
                additions += diff; // Need to add to t
            } else if (diff < 0) {
                deletions += Math.abs(diff); // Need to remove from t
            }
        }

        // Optimal strategy: replace min(additions, deletions), then add/remove rest
        replacements = Math.min(additions, deletions);
        additions -= replacements;
        deletions -= replacements;

        return new int[] { replacements, additions, deletions };
    }

    /**
     * Follow-up 4: Weighted operations (different costs for operations)
     * Time: O(n), Space: O(1)
     */
    public int minStepsWeighted(String s, String t, int replaceCost, int addCost, int removeCost) {
        if (s == null || t == null) {
            return -1;
        }

        int[] operations = minStepsWithAddRemove(s, t);
        if (operations[0] == -1)
            return -1;

        int replacements = operations[0];
        int additions = operations[1];
        int deletions = operations[2];

        // Try different strategies and pick minimum cost
        int strategy1 = replacements * replaceCost + additions * addCost + deletions * removeCost;
        int strategy2 = (replacements + additions + deletions) * Math.max(addCost, removeCost);

        return Math.min(strategy1, strategy2);
    }

    /**
     * Follow-up 5: Unicode support with HashMap
     * Time: O(n), Space: O(k) where k is unique characters
     */
    public int minStepsUnicode(String s, String t) {
        if (s == null || t == null || s.length() != t.length()) {
            return -1;
        }

        Map<Character, Integer> freq = new HashMap<>();

        // Count differences
        for (int i = 0; i < s.length(); i++) {
            char sChar = s.charAt(i);
            char tChar = t.charAt(i);

            freq.put(sChar, freq.getOrDefault(sChar, 0) + 1);
            freq.put(tChar, freq.getOrDefault(tChar, 0) - 1);
        }

        int steps = 0;
        for (int count : freq.values()) {
            if (count > 0) {
                steps += count;
            }
        }

        return steps;
    }

    /**
     * Detailed analysis - return character-wise changes needed
     * Time: O(n), Space: O(1)
     */
    public Map<String, Integer> analyzeChanges(String s, String t) {
        Map<String, Integer> analysis = new HashMap<>();

        if (s == null || t == null || s.length() != t.length()) {
            analysis.put("error", -1);
            return analysis;
        }

        int[] freq = new int[26];

        for (int i = 0; i < s.length(); i++) {
            freq[s.charAt(i) - 'a']++;
            freq[t.charAt(i) - 'a']--;
        }

        int totalSteps = 0;
        StringBuilder excess = new StringBuilder();
        StringBuilder deficit = new StringBuilder();

        for (int i = 0; i < 26; i++) {
            char c = (char) ('a' + i);
            if (freq[i] > 0) {
                excess.append(c).append(":").append(freq[i]).append(" ");
                totalSteps += freq[i];
            } else if (freq[i] < 0) {
                deficit.append(c).append(":").append(Math.abs(freq[i])).append(" ");
            }
        }

        analysis.put("total_steps", totalSteps);
        analysis.put("characters_to_add", excess.length());
        analysis.put("characters_to_remove", deficit.length());

        return analysis;
    }

    public static void main(String[] args) {
        MinimumNumberOfStepsToMakeTwoStringsAnagram solution = new MinimumNumberOfStepsToMakeTwoStringsAnagram();

        System.out.println("=== Minimum Steps to Make Anagram Test ===");

        // Test Case 1: Basic examples
        System.out.println("Basic examples:");
        System.out.println("\"bab\" -> \"aba\": " + solution.minSteps("bab", "aba")); // 1
        System.out.println("\"leetcode\" -> \"practice\": " + solution.minSteps("leetcode", "practice")); // 5

        // Test Case 2: Already anagrams
        System.out.println("\nAlready anagrams:");
        System.out.println("\"abc\" -> \"cab\": " + solution.minSteps("abc", "cab")); // 0
        System.out.println("\"listen\" -> \"silent\": " + solution.minSteps("listen", "silent")); // 0

        // Test Case 3: Completely different
        System.out.println("\nCompletely different:");
        System.out.println("\"aaa\" -> \"bbb\": " + solution.minSteps("aaa", "bbb")); // 3
        System.out.println("\"xyz\" -> \"abc\": " + solution.minSteps("xyz", "abc")); // 3

        // Test Case 4: Compare different approaches
        System.out.println("\nCompare approaches:");
        String s1 = "anagram", t1 = "mangaar";
        System.out.println("Array approach: " + solution.minSteps(s1, t1));
        System.out.println("HashMap approach: " + solution.minStepsHashMap(s1, t1));
        System.out.println("Single pass: " + solution.minStepsSinglePass(s1, t1));

        // Test Case 5: Both directions analysis
        int[] bothDirections = solution.minStepsBothDirections(s1, t1);
        System.out.println("Both directions [add, remove]: " + Arrays.toString(bothDirections));

        // Test Case 6: Different length strings
        System.out.println("\nDifferent lengths:");
        System.out.println("\"hello\" vs \"world\": " +
                solution.minStepsDifferentLengths("hello", "world"));
        System.out.println("\"abc\" vs \"abcdef\": " +
                solution.minStepsDifferentLengths("abc", "abcdef"));

        // Test Case 7: Add/Remove operations
        System.out.println("\nWith add/remove operations:");
        int[] operations = solution.minStepsWithAddRemove("hello", "world");
        System.out.println("\"hello\" vs \"world\" [replace, add, remove]: " +
                Arrays.toString(operations));

        operations = solution.minStepsWithAddRemove("abc", "def");
        System.out.println("\"abc\" vs \"def\" [replace, add, remove]: " +
                Arrays.toString(operations));

        // Test Case 8: Weighted operations
        System.out.println("\nWeighted operations (replace:1, add:2, remove:2):");
        System.out.println("\"hello\" vs \"world\": " +
                solution.minStepsWeighted("hello", "world", 1, 2, 2));

        // Test Case 9: Unicode support
        System.out.println("\nUnicode support:");
        System.out.println("\"café\" vs \"face\": " +
                solution.minStepsUnicode("café", "face"));

        // Test Case 10: Detailed analysis
        System.out.println("\nDetailed analysis:");
        Map<String, Integer> analysis = solution.analyzeChanges("leetcode", "practice");
        System.out.println("Analysis for \"leetcode\" -> \"practice\":");
        for (Map.Entry<String, Integer> entry : analysis.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        // Test Case 11: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty strings: " + solution.minSteps("", ""));
        System.out.println("Single character same: " + solution.minSteps("a", "a"));
        System.out.println("Single character different: " + solution.minSteps("a", "b"));

        // Test Case 12: Performance test
        System.out.println("\n=== Performance Test ===");
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        Random random = new Random(42);

        for (int i = 0; i < 50000; i++) {
            sb1.append((char) ('a' + random.nextInt(26)));
            sb2.append((char) ('a' + random.nextInt(26)));
        }

        String largeS = sb1.toString();
        String largeT = sb2.toString();

        long startTime = System.currentTimeMillis();
        int result1 = solution.minSteps(largeS, largeT);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result2 = solution.minStepsHashMap(largeS, largeT);
        long time2 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result3 = solution.minStepsSinglePass(largeS, largeT);
        long time3 = System.currentTimeMillis() - startTime;

        System.out.println("Array approach: " + result1 + " (" + time1 + "ms)");
        System.out.println("HashMap approach: " + result2 + " (" + time2 + "ms)");
        System.out.println("Single pass: " + result3 + " (" + time3 + "ms)");

        // Test Case 13: All same characters
        System.out.println("\nSpecial cases:");
        System.out.println("\"aaaa\" vs \"bbbb\": " + solution.minSteps("aaaa", "bbbb"));
        System.out.println("\"abab\" vs \"baba\": " + solution.minSteps("abab", "baba"));

        // Test Case 14: Maximum difference scenario
        String maxDiff1 = "abcdefghijklm";
        String maxDiff2 = "nopqrstuvwxyz";
        System.out.println("Maximum difference: " + solution.minSteps(maxDiff1, maxDiff2));
    }
}
