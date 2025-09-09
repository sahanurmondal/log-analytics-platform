package strings.medium;

import java.util.*;

/**
 * LeetCode 3: Longest Substring Without Repeating Characters
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, ByteDance, LinkedIn
 * Frequency: Very High (Asked in 900+ interviews)
 *
 * Description:
 * Given a string s, find the length of the longest substring without repeating
 * characters.
 *
 * Constraints:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of English letters, digits, symbols and spaces.
 * 
 * Follow-up Questions:
 * 1. Can you return the actual substring instead of just length?
 * 2. What if you need to find all such substrings?
 * 3. Can you solve for exactly k distinct characters?
 * 4. What if characters have different weights/priorities?
 */
public class LongestSubstringWithoutRepeatingCharacters {

    // Approach 1: Sliding Window with HashSet - O(n) time, O(min(m,n)) space
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        Set<Character> window = new HashSet<>();
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // Shrink window until no duplicate
            while (window.contains(current)) {
                window.remove(s.charAt(left));
                left++;
            }

            window.add(current);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 2: Optimized Sliding Window with HashMap - O(n) time, O(min(m,n))
    // space
    public int lengthOfLongestSubstringOptimized(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // If character is repeated, move left pointer
            if (charIndex.containsKey(current)) {
                left = Math.max(left, charIndex.get(current) + 1);
            }

            charIndex.put(current, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 3: ASCII Array (for limited character set) - O(n) time, O(1) space
    public int lengthOfLongestSubstringASCII(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int[] lastIndex = new int[128]; // ASCII characters
        Arrays.fill(lastIndex, -1);

        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            if (lastIndex[current] >= left) {
                left = lastIndex[current] + 1;
            }

            lastIndex[current] = right;
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 4: Brute Force (for comparison) - O(n³) time, O(min(m,n)) space
    public int lengthOfLongestSubstringBruteForce(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int maxLength = 0;

        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                if (hasUniqueCharacters(s, i, j)) {
                    maxLength = Math.max(maxLength, j - i + 1);
                }
            }
        }

        return maxLength;
    }

    private boolean hasUniqueCharacters(String s, int start, int end) {
        Set<Character> chars = new HashSet<>();
        for (int i = start; i <= end; i++) {
            if (chars.contains(s.charAt(i))) {
                return false;
            }
            chars.add(s.charAt(i));
        }
        return true;
    }

    // Follow-up 1: Return the actual substring
    public String longestSubstringWithoutRepeating(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }

        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0, maxLength = 0, bestStart = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            if (charIndex.containsKey(current)) {
                left = Math.max(left, charIndex.get(current) + 1);
            }

            charIndex.put(current, right);

            if (right - left + 1 > maxLength) {
                maxLength = right - left + 1;
                bestStart = left;
            }
        }

        return s.substring(bestStart, bestStart + maxLength);
    }

    // Follow-up 2: Find all longest substrings
    public List<String> allLongestSubstrings(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.length() == 0) {
            return result;
        }

        int maxLength = lengthOfLongestSubstring(s);
        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            if (charIndex.containsKey(current)) {
                left = Math.max(left, charIndex.get(current) + 1);
            }

            charIndex.put(current, right);

            if (right - left + 1 == maxLength) {
                String substring = s.substring(left, right + 1);
                if (!result.contains(substring)) {
                    result.add(substring);
                }
            }
        }

        return result;
    }

    // Follow-up 3: Longest substring with exactly k distinct characters
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return 0;
        }

        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);
            charCount.put(current, charCount.getOrDefault(current, 0) + 1);

            // Shrink window if more than k distinct characters
            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }

            // Update max length when exactly k distinct characters
            if (charCount.size() == k) {
                maxLength = Math.max(maxLength, right - left + 1);
            }
        }

        return maxLength;
    }

    // Follow-up 4: Weighted characters (priority-based)
    public int lengthOfLongestSubstringWeighted(String s, Map<Character, Integer> weights) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        Map<Character, Integer> window = new HashMap<>();
        int left = 0, maxWeight = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);

            // If character already in window, shrink from left
            while (window.containsKey(current)) {
                char leftChar = s.charAt(left);
                window.remove(leftChar);
                left++;
            }

            window.put(current, right);

            // Calculate total weight of current window
            int currentWeight = 0;
            for (char c : window.keySet()) {
                currentWeight += weights.getOrDefault(c, 1);
            }

            maxWeight = Math.max(maxWeight, currentWeight);
        }

        return maxWeight;
    }

    // Advanced: Longest substring with at most k distinct characters
    public int lengthOfLongestSubstringAtMostK(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return 0;
        }

        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char current = s.charAt(right);
            charCount.put(current, charCount.getOrDefault(current, 0) + 1);

            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Advanced: Longest repeating character replacement (LeetCode 424)
    public int characterReplacement(String s, int k) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int[] count = new int[26];
        int left = 0, maxCount = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);

            // If window size - max frequency > k, shrink window
            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Helper: Get character frequency in string
    public Map<Character, Integer> getCharFrequency(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    // Helper: Check if string has all unique characters
    public boolean hasAllUniqueChars(String s) {
        Set<Character> chars = new HashSet<>();
        for (char c : s.toCharArray()) {
            if (chars.contains(c)) {
                return false;
            }
            chars.add(c);
        }
        return true;
    }

    // Helper: Performance comparison
    public Map<String, Long> comparePerformance(String s) {
        Map<String, Long> results = new HashMap<>();

        // Test sliding window with HashSet
        long start = System.nanoTime();
        lengthOfLongestSubstring(s);
        results.put("SlidingWindow", System.nanoTime() - start);

        // Test optimized sliding window
        start = System.nanoTime();
        lengthOfLongestSubstringOptimized(s);
        results.put("OptimizedWindow", System.nanoTime() - start);

        // Test ASCII array (if applicable)
        if (s.chars().allMatch(c -> c < 128)) {
            start = System.nanoTime();
            lengthOfLongestSubstringASCII(s);
            results.put("ASCIIArray", System.nanoTime() - start);
        }

        // Test brute force (only for short strings)
        if (s.length() < 100) {
            start = System.nanoTime();
            lengthOfLongestSubstringBruteForce(s);
            results.put("BruteForce", System.nanoTime() - start);
        }

        return results;
    }

    public static void main(String[] args) {
        LongestSubstringWithoutRepeatingCharacters solution = new LongestSubstringWithoutRepeatingCharacters();

        // Test Case 1: Standard examples
        System.out.println("=== Test Case 1: Standard Examples ===");
        String[] testCases = {
                "abcabcbb", // Expected: 3 ("abc")
                "bbbbb", // Expected: 1 ("b")
                "pwwkew", // Expected: 3 ("wke")
                "", // Expected: 0
                "abcdef", // Expected: 6 ("abcdef")
                "aab", // Expected: 2 ("ab")
                "dvdf" // Expected: 3 ("vdf")
        };

        for (String test : testCases) {
            int result = solution.lengthOfLongestSubstring(test);
            System.out.println("\"" + test + "\" -> Length: " + result);
        }

        // Test Case 2: Compare approaches
        System.out.println("\n=== Test Case 2: Compare Approaches ===");
        String testStr = "abcabcbb";

        int result1 = solution.lengthOfLongestSubstring(testStr);
        int result2 = solution.lengthOfLongestSubstringOptimized(testStr);
        int result3 = solution.lengthOfLongestSubstringASCII(testStr);
        int result4 = solution.lengthOfLongestSubstringBruteForce(testStr);

        System.out.println("Input: \"" + testStr + "\"");
        System.out.println("Sliding Window: " + result1);
        System.out.println("Optimized Window: " + result2);
        System.out.println("ASCII Array: " + result3);
        System.out.println("Brute Force: " + result4);
        System.out.println("All consistent: " +
                (result1 == result2 && result2 == result3 && result3 == result4));

        // Follow-up 1: Return actual substring
        System.out.println("\n=== Follow-up 1: Return Actual Substring ===");
        for (String test : Arrays.asList("abcabcbb", "pwwkew", "bbbbb")) {
            String longest = solution.longestSubstringWithoutRepeating(test);
            System.out.println("\"" + test + "\" -> Longest: \"" + longest + "\"");
        }

        // Follow-up 2: All longest substrings
        System.out.println("\n=== Follow-up 2: All Longest Substrings ===");
        String multiTest = "abcabcbb";
        List<String> allLongest = solution.allLongestSubstrings(multiTest);
        System.out.println("\"" + multiTest + "\" -> All longest: " + allLongest);

        // Follow-up 3: Exactly k distinct characters
        System.out.println("\n=== Follow-up 3: Exactly K Distinct Characters ===");
        String kTest = "eceba";
        for (int k = 1; k <= 4; k++) {
            int lengthK = solution.lengthOfLongestSubstringKDistinct(kTest, k);
            System.out.println("\"" + kTest + "\" with exactly " + k + " distinct: " + lengthK);
        }

        // Follow-up 4: Weighted characters
        System.out.println("\n=== Follow-up 4: Weighted Characters ===");
        Map<Character, Integer> weights = Map.of(
                'a', 1, 'b', 2, 'c', 3, 'd', 4, 'e', 5);
        String weightTest = "abcde";
        int weightedResult = solution.lengthOfLongestSubstringWeighted(weightTest, weights);
        System.out.println("\"" + weightTest + "\" weighted result: " + weightedResult);

        // Advanced: At most k distinct
        System.out.println("\n=== Advanced: At Most K Distinct ===");
        String atMostTest = "eceba";
        for (int k = 1; k <= 4; k++) {
            int atMostK = solution.lengthOfLongestSubstringAtMostK(atMostTest, k);
            System.out.println("\"" + atMostTest + "\" with at most " + k + " distinct: " + atMostK);
        }

        // Advanced: Character replacement
        System.out.println("\n=== Advanced: Character Replacement ===");
        String replaceTest = "AABABBA";
        int replaceResult = solution.characterReplacement(replaceTest, 1);
        System.out.println("\"" + replaceTest + "\" with 1 replacement: " + replaceResult);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        StringBuilder largeTest = new StringBuilder();
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            largeTest.append((char) ('a' + random.nextInt(26)));
        }

        Map<String, Long> performance = solution.comparePerformance(largeTest.toString());
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1_000_000.0 + " ms"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Single character
        System.out.println("Single char 'a': " + solution.lengthOfLongestSubstring("a"));

        // All same characters
        System.out.println("All same 'aaaa': " + solution.lengthOfLongestSubstring("aaaa"));

        // Already unique
        System.out.println("Already unique 'abcdef': " + solution.lengthOfLongestSubstring("abcdef"));

        // Special characters
        String special = "!@#$%^&*()";
        System.out.println("Special chars: " + solution.lengthOfLongestSubstring(special));

        // Mixed case and numbers
        String mixed = "Aa1Bb2Cc3";
        System.out.println("Mixed case/numbers: " + solution.lengthOfLongestSubstring(mixed));

        // Unicode characters
        String unicode = "αβγαβγ";
        System.out.println("Unicode: " + solution.lengthOfLongestSubstring(unicode));

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");
        String helperTest = "abcabc";
        Map<Character, Integer> freq = solution.getCharFrequency(helperTest);
        System.out.println("Frequency of \"" + helperTest + "\": " + freq);
        System.out.println("Has all unique chars: " + solution.hasAllUniqueChars(helperTest));
        System.out.println("Has all unique chars 'abc': " + solution.hasAllUniqueChars("abc"));
    }
}
