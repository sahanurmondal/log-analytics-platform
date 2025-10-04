package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 76: Minimum Window Substring
 * https://leetcode.com/problems/minimum-window-substring/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Uber, LinkedIn
 * Frequency: Very High (Asked in 300+ interviews)
 *
 * Description: Given two strings s and t, return the minimum window substring
 * of s
 * such that every character in t (including duplicates) is included in the
 * window.
 * If there is no such window, return the empty string "".
 *
 * Constraints:
 * - 1 <= s.length, t.length <= 10^5
 * - s and t consist of uppercase and lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you find all minimum windows of the same length?
 * 2. Can you handle case-insensitive matching?
 * 3. Can you extend to find windows with at most k missing characters?
 * 4. Can you find the minimum window that contains at least m characters from
 * t?
 */
public class MinimumWindowCoveringAllCharacters {

    // Approach 1: Sliding window with hash map - O(|s| + |t|) time, O(|s| + |t|)
    // space
    public String minWindow(String s, String t) {
        if (s.length() < t.length())
            return "";

        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        int required = targetMap.size();
        int formed = 0;
        Map<Character, Integer> windowMap = new HashMap<>();

        int left = 0, right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        while (right < s.length()) {
            char rightChar = s.charAt(right);
            windowMap.put(rightChar, windowMap.getOrDefault(rightChar, 0) + 1);

            if (targetMap.containsKey(rightChar) &&
                    windowMap.get(rightChar).intValue() == targetMap.get(rightChar).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowMap.put(leftChar, windowMap.get(leftChar) - 1);
                if (targetMap.containsKey(leftChar) &&
                        windowMap.get(leftChar).intValue() < targetMap.get(leftChar).intValue()) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // Approach 2: Optimized sliding window - O(|s| + |t|) time, O(|s| + |t|) space
    public String minWindowOptimized(String s, String t) {
        if (s.length() < t.length())
            return "";

        int[] targetCount = new int[128];
        int[] windowCount = new int[128];

        for (char c : t.toCharArray()) {
            targetCount[c]++;
        }

        int required = 0;
        for (int count : targetCount) {
            if (count > 0)
                required++;
        }

        int left = 0, right = 0;
        int formed = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        while (right < s.length()) {
            char rightChar = s.charAt(right);
            windowCount[rightChar]++;

            if (targetCount[rightChar] > 0 && windowCount[rightChar] == targetCount[rightChar]) {
                formed++;
            }

            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowCount[leftChar]--;
                if (targetCount[leftChar] > 0 && windowCount[leftChar] < targetCount[leftChar]) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // Approach 3: Filtered sliding window - O(|s| + |t|) time, O(|s| + |t|) space
    public String minWindowFiltered(String s, String t) {
        if (s.length() < t.length())
            return "";

        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        // Create filtered list of (index, character) for characters in t
        List<int[]> filteredS = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (targetMap.containsKey(c)) {
                filteredS.add(new int[] { i, c });
            }
        }

        int required = targetMap.size();
        int formed = 0;
        Map<Character, Integer> windowMap = new HashMap<>();

        int left = 0, right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        while (right < filteredS.size()) {
            char rightChar = (char) filteredS.get(right)[1];
            windowMap.put(rightChar, windowMap.getOrDefault(rightChar, 0) + 1);

            if (windowMap.get(rightChar).intValue() == targetMap.get(rightChar).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                int start = filteredS.get(left)[0];
                int end = filteredS.get(right)[0];

                if (end - start + 1 < minLen) {
                    minLen = end - start + 1;
                    minStart = start;
                }

                char leftChar = (char) filteredS.get(left)[1];
                windowMap.put(leftChar, windowMap.get(leftChar) - 1);
                if (windowMap.get(leftChar).intValue() < targetMap.get(leftChar).intValue()) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // Follow-up 1: Find all minimum windows of the same length
    public List<String> findAllMinWindows(String s, String t) {
        List<String> result = new ArrayList<>();
        if (s.length() < t.length())
            return result;

        String minWindow = minWindow(s, t);
        if (minWindow.isEmpty())
            return result;

        int minLen = minWindow.length();
        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        for (int i = 0; i <= s.length() - minLen; i++) {
            String candidate = s.substring(i, i + minLen);
            if (isValidWindow(candidate, targetMap)) {
                result.add(candidate);
            }
        }

        return result;
    }

    // Follow-up 2: Case-insensitive matching
    public String minWindowIgnoreCase(String s, String t) {
        return minWindow(s.toLowerCase(), t.toLowerCase());
    }

    // Follow-up 3: Find windows with at most k missing characters
    public String minWindowWithKMissing(String s, String t, int k) {
        if (s.length() < t.length() - k)
            return "";

        Map<Character, Integer> targetMap = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetMap.put(c, targetMap.getOrDefault(c, 0) + 1);
        }

        int required = targetMap.size();
        int formed = 0;
        Map<Character, Integer> windowMap = new HashMap<>();

        int left = 0, right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        while (right < s.length()) {
            char rightChar = s.charAt(right);
            windowMap.put(rightChar, windowMap.getOrDefault(rightChar, 0) + 1);

            if (targetMap.containsKey(rightChar) &&
                    windowMap.get(rightChar).intValue() == targetMap.get(rightChar).intValue()) {
                formed++;
            }

            while (left <= right && formed >= required - k) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowMap.put(leftChar, windowMap.get(leftChar) - 1);
                if (targetMap.containsKey(leftChar) &&
                        windowMap.get(leftChar).intValue() < targetMap.get(leftChar).intValue()) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // Follow-up 4: Find minimum window with at least m characters from t
    public String minWindowWithAtLeastM(String s, String t, int m) {
        if (m <= 0)
            return "";

        Set<Character> targetChars = new HashSet<>();
        for (char c : t.toCharArray()) {
            targetChars.add(c);
        }

        int left = 0, right = 0;
        int count = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;

        while (right < s.length()) {
            if (targetChars.contains(s.charAt(right))) {
                count++;
            }

            while (count >= m) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                if (targetChars.contains(s.charAt(left))) {
                    count--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    // Helper method: Check if a window contains all characters from target
    private boolean isValidWindow(String window, Map<Character, Integer> targetMap) {
        Map<Character, Integer> windowMap = new HashMap<>();
        for (char c : window.toCharArray()) {
            windowMap.put(c, windowMap.getOrDefault(c, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : targetMap.entrySet()) {
            if (windowMap.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    // Helper method: Get window information for debugging
    public String getWindowInfo(String s, String t) {
        String result = minWindow(s, t);
        if (result.isEmpty()) {
            return "No valid window found";
        }

        int start = s.indexOf(result);
        return String.format("Window: \"%s\" at position [%d, %d] with length %d",
                result, start, start + result.length() - 1, result.length());
    }

    // Helper method: Count character frequencies
    public Map<Character, Integer> getCharFrequency(String str) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : str.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }
        return freq;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumWindowCoveringAllCharacters solution = new MinimumWindowCoveringAllCharacters();

        // Test case 1: Basic case
        String s1 = "ADOBECODEBANC", t1 = "ABC";
        System.out.println("Test 1 - S: " + s1 + ", T: " + t1 + " Expected: BANC");
        System.out.println("Approach 1: \"" + solution.minWindow(s1, t1) + "\"");
        System.out.println("Approach 2: \"" + solution.minWindowOptimized(s1, t1) + "\"");
        System.out.println("Approach 3: \"" + solution.minWindowFiltered(s1, t1) + "\"");
        System.out.println("Info: " + solution.getWindowInfo(s1, t1));

        // Test case 2: No valid window
        String s2 = "a", t2 = "aa";
        System.out.println("\nTest 2 - S: " + s2 + ", T: " + t2 + " Expected: \"\" (no valid window)");
        System.out.println("Result: \"" + solution.minWindow(s2, t2) + "\"");

        // Test case 3: Entire string is minimum window
        String s3 = "a", t3 = "a";
        System.out.println("\nTest 3 - S: " + s3 + ", T: " + t3 + " Expected: a (entire string)");
        System.out.println("Result: \"" + solution.minWindow(s3, t3) + "\"");

        // Test case 4: Multiple same characters in target
        String s4 = "ADOBECODEBANC", t4 = "AABC";
        System.out.println("\nTest 4 - S: " + s4 + ", T: " + t4 + " Expected: ADOBEC (multiple A's needed)");
        System.out.println("Result: \"" + solution.minWindow(s4, t4) + "\"");

        // Test case 5: Target longer than source
        String s5 = "AB", t5 = "ABC";
        System.out.println("\nTest 5 - S: " + s5 + ", T: " + t5 + " Expected: \"\" (target longer)");
        System.out.println("Result: \"" + solution.minWindow(s5, t5) + "\"");

        // Test case 6: Case sensitive
        String s6 = "AaBbCc", t6 = "abc";
        System.out.println("\nTest 6 - S: " + s6 + ", T: " + t6 + " Expected: bCc (case sensitive)");
        System.out.println("Result: \"" + solution.minWindow(s6, t6) + "\"");

        // Test case 7: Large input with repeating pattern
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("ABCD");
        }
        sb.append("XYZ");
        String s7 = sb.toString();
        String t7 = "XYZ";
        System.out.println("\nTest 7 - Large input with pattern: " + s7.length() + " chars, T: " + t7);
        long startTime = System.nanoTime();
        String result7 = solution.minWindow(s7, t7);
        long endTime = System.nanoTime();
        System.out.println("Result: \"" + result7 + "\" (Time: " + (endTime - startTime) / 1_000_000 + " ms)");

        // Test case 8: All characters in target are the same
        String s8 = "aaaaaaaaab", t8 = "aaaa";
        System.out.println("\nTest 8 - S: " + s8 + ", T: " + t8 + " Expected: aaaa (repeated chars)");
        System.out.println("Result: \"" + solution.minWindow(s8, t8) + "\"");

        // Test Follow-ups
        System.out.println("\nFollow-up tests:");

        // All minimum windows
        List<String> allMinWindows = solution.findAllMinWindows("ADOBECODEBANCBANC", "ABC");
        System.out.println("All min windows for 'ADOBECODEBANCBANC' with 'ABC': " + allMinWindows);

        // Case insensitive
        System.out.println("Case insensitive 'AaBbCc' with 'ABC': \"" +
                solution.minWindowIgnoreCase("AaBbCc", "ABC") + "\"");

        // With k missing
        System.out.println("Window with 1 missing char 'ADOBEC' with 'ABCD': \"" +
                solution.minWindowWithKMissing("ADOBEC", "ABCD", 1) + "\"");

        // At least m characters
        System.out.println("At least 2 chars from 'ABC' in 'ADOBECODEBANC': \"" +
                solution.minWindowWithAtLeastM("ADOBECODEBANC", "ABC", 2) + "\"");

        // Edge case: Empty strings
        System.out.println("\nEdge cases:");
        System.out.println("Empty S: \"" + solution.minWindow("", "A") + "\"");
        System.out.println("Empty T: \"" + solution.minWindow("A", "") + "\"");

        // Edge case: Single character strings
        System.out.println("Single char match: \"" + solution.minWindow("A", "A") + "\"");
        System.out.println("Single char no match: \"" + solution.minWindow("A", "B") + "\"");

        // Edge case: Very long strings
        StringBuilder longS = new StringBuilder();
        StringBuilder longT = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longS.append((char) ('A' + (i % 26)));
        }
        for (int i = 0; i < 26; i++) {
            longT.append((char) ('A' + i));
        }

        System.out.println("\nEdge case - Very long strings:");
        System.out.println("S length: " + longS.length() + ", T length: " + longT.length());
        startTime = System.nanoTime();
        String longResult = solution.minWindowOptimized(longS.toString(), longT.toString());
        endTime = System.nanoTime();
        System.out.println("Result length: " + longResult.length() +
                " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");

        // Stress test: Random strings
        System.out.println("\nStress test - Random strings:");
        Random random = new Random(42);
        for (int len : new int[] { 100, 1000, 5000 }) {
            StringBuilder randomS = new StringBuilder();
            StringBuilder randomT = new StringBuilder();

            for (int i = 0; i < len; i++) {
                randomS.append((char) ('A' + random.nextInt(26)));
            }
            for (int i = 0; i < Math.min(10, len); i++) {
                randomT.append((char) ('A' + random.nextInt(26)));
            }

            startTime = System.nanoTime();
            String randomResult = solution.minWindow(randomS.toString(), randomT.toString());
            endTime = System.nanoTime();

            System.out.println("Length " + len + ": Window length " + randomResult.length() +
                    " (Time: " + (endTime - startTime) / 1_000_000 + " ms)");
        }

        // Character frequency analysis
        System.out.println("\nCharacter frequency analysis:");
        Map<Character, Integer> freq1 = solution.getCharFrequency(s1);
        Map<Character, Integer> freq2 = solution.getCharFrequency(t1);
        System.out.println("S frequencies: " + freq1);
        System.out.println("T frequencies: " + freq2);
    }
}
