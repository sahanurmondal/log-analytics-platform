package strings.medium;

import java.util.*;

/**
 * LeetCode 424: Longest Repeating Character Replacement
 * https://leetcode.com/problems/longest-repeating-character-replacement/
 * 
 * Companies: Facebook, Amazon, Google, Microsoft, Apple, Bloomberg
 * Frequency: Very High (Asked in 1000+ interviews)
 *
 * Description:
 * You are given a string s and an integer k. You can choose any character of
 * the string and change it to any other uppercase English letter.
 * You can perform this operation at most k times.
 * Return the length of the longest substring containing the same letter you can
 * get after performing the above operations.
 * 
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of only uppercase English letters
 * - 0 <= k <= s.length
 * 
 * Follow-up Questions:
 * 1. How would you handle lowercase letters or mixed case?
 * 2. Can you solve for multiple characters to replace simultaneously?
 * 3. What if we want the k-th longest repeating character replacement?
 * 4. How to handle replacement with specific character constraints?
 * 5. Can you find all possible replacements of length >= target?
 * 6. What about finding the minimum operations needed for a target length?
 */
public class LongestRepeatingCharacterReplacement {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(1) space
    public static int characterReplacement(String s, int k) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0;
        int maxCount = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);

            // Update max count of any character in current window
            maxCount = Math.max(maxCount, count.get(rightChar));

            // If window size - maxCount > k, shrink window
            while (right - left + 1 - maxCount > k) {
                char leftChar = s.charAt(left);
                count.put(leftChar, count.get(leftChar) - 1);
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 2: Sliding Window with Array - O(n) time, O(1) space
    public static int characterReplacementArray(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxCount = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            count[s.charAt(right) - 'A']++;
            maxCount = Math.max(maxCount, count[s.charAt(right) - 'A']);

            while (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 3: Optimized Sliding Window - O(n) time, O(1) space
    public static int characterReplacementOptimized(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxCount = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(right) - 'A']);

            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left++) - 'A']--;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 4: Brute Force with Character Analysis - O(n²) time, O(1) space
    public static int characterReplacementBruteForce(String s, int k) {
        int maxLength = 0;

        for (int i = 0; i < s.length(); i++) {
            int[] count = new int[26];
            int maxCount = 0;

            for (int j = i; j < s.length(); j++) {
                count[s.charAt(j) - 'A']++;
                maxCount = Math.max(maxCount, count[s.charAt(j) - 'A']);

                int currentLength = j - i + 1;
                if (currentLength - maxCount <= k) {
                    maxLength = Math.max(maxLength, currentLength);
                } else {
                    break;
                }
            }
        }

        return maxLength;
    }

    // Approach 5: Character-specific sliding window - O(26*n) time, O(1) space
    public static int characterReplacementByChar(String s, int k) {
        int maxLength = 0;

        // Try for each character A-Z
        for (char c = 'A'; c <= 'Z'; c++) {
            int left = 0;
            int replacements = 0;

            for (int right = 0; right < s.length(); right++) {
                if (s.charAt(right) != c) {
                    replacements++;
                }

                while (replacements > k) {
                    if (s.charAt(left) != c) {
                        replacements--;
                    }
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }
        }

        return maxLength;
    }

    // Follow-up 1: Handle mixed case letters
    public static class MixedCaseHandling {

        public static int characterReplacementMixedCase(String s, int k) {
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = Character.toUpperCase(s.charAt(right));
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = Character.toUpperCase(s.charAt(left));
                    count.put(leftChar, count.get(leftChar) - 1);
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }

        public static int characterReplacementCaseSensitive(String s, int k) {
            // Count both uppercase and lowercase separately
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }

        public static int characterReplacementIgnoreCase(String s, int k) {
            return characterReplacementMixedCase(s.toUpperCase(), k);
        }
    }

    // Follow-up 2: Multiple characters replacement
    public static class MultipleCharacterReplacement {

        public static int longestSubstringWithKReplacements(String s, int k, Set<Character> targetChars) {
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int targetCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);

                if (targetChars.contains(rightChar)) {
                    targetCount++;
                }

                while (right - left + 1 - targetCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    if (targetChars.contains(leftChar)) {
                        targetCount--;
                    }
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }

        public static int longestSubstringTwoCharacters(String s, int k, char char1, char char2) {
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int targetCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);

                if (rightChar == char1 || rightChar == char2) {
                    targetCount++;
                }

                while (right - left + 1 - targetCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    if (leftChar == char1 || leftChar == char2) {
                        targetCount--;
                    }
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }
    }

    // Follow-up 3: K-th longest repeating character replacement
    public static class KthLongestReplacement {

        public static List<Integer> allPossibleLengths(String s, int k) {
            Set<Integer> lengthSet = new HashSet<>();

            // Try all possible substrings
            for (int i = 0; i < s.length(); i++) {
                Map<Character, Integer> count = new HashMap<>();
                int maxCount = 0;

                for (int j = i; j < s.length(); j++) {
                    char c = s.charAt(j);
                    count.put(c, count.getOrDefault(c, 0) + 1);
                    maxCount = Math.max(maxCount, count.get(c));

                    int currentLength = j - i + 1;
                    if (currentLength - maxCount <= k) {
                        lengthSet.add(currentLength);
                    }
                }
            }

            List<Integer> result = new ArrayList<>(lengthSet);
            Collections.sort(result, Collections.reverseOrder());
            return result;
        }

        public static int kthLongestReplacement(String s, int k, int kth) {
            List<Integer> lengths = allPossibleLengths(s, k);
            return kth <= lengths.size() ? lengths.get(kth - 1) : -1;
        }

        public static List<Integer> topKLongestReplacements(String s, int k, int topK) {
            List<Integer> lengths = allPossibleLengths(s, k);
            return lengths.subList(0, Math.min(topK, lengths.size()));
        }
    }

    // Follow-up 4: Replacement with specific constraints
    public static class ConstrainedReplacement {

        public static int characterReplacementWithConstraint(String s, int k, char forbiddenChar) {
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);

                // Skip if trying to replace with forbidden character
                if (rightChar == forbiddenChar) {
                    // Reset window
                    left = right + 1;
                    count.clear();
                    maxCount = 0;
                    continue;
                }

                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }

        public static int characterReplacementOnlyAllowed(String s, int k, Set<Character> allowedChars) {
            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);

                if (!allowedChars.contains(rightChar)) {
                    // Skip this character, reset window
                    left = right + 1;
                    count.clear();
                    maxCount = 0;
                    continue;
                }

                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    left++;
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }
    }

    // Follow-up 5: Find all possible replacements
    public static class AllPossibleReplacements {

        public static class ReplacementResult {
            int startIndex;
            int endIndex;
            char targetChar;
            int replacements;

            public ReplacementResult(int start, int end, char target, int replacements) {
                this.startIndex = start;
                this.endIndex = end;
                this.targetChar = target;
                this.replacements = replacements;
            }

            @Override
            public String toString() {
                return String.format("[%d,%d] -> '%c' (replacements: %d, length: %d)",
                        startIndex, endIndex, targetChar, replacements, endIndex - startIndex + 1);
            }
        }

        public static List<ReplacementResult> findAllReplacements(String s, int k, int minLength) {
            List<ReplacementResult> results = new ArrayList<>();

            for (int i = 0; i < s.length(); i++) {
                for (char targetChar = 'A'; targetChar <= 'Z'; targetChar++) {
                    int replacements = 0;

                    for (int j = i; j < s.length(); j++) {
                        if (s.charAt(j) != targetChar) {
                            replacements++;
                        }

                        if (replacements <= k && j - i + 1 >= minLength) {
                            results.add(new ReplacementResult(i, j, targetChar, replacements));
                        } else if (replacements > k) {
                            break;
                        }
                    }
                }
            }

            return results;
        }

        public static List<ReplacementResult> findOptimalReplacements(String s, int k, int targetLength) {
            List<ReplacementResult> results = new ArrayList<>();

            for (int i = 0; i <= s.length() - targetLength; i++) {
                Map<Character, Integer> count = new HashMap<>();

                // Count characters in window
                for (int j = i; j < i + targetLength; j++) {
                    char c = s.charAt(j);
                    count.put(c, count.getOrDefault(c, 0) + 1);
                }

                // Find best character to keep
                char bestChar = 'A';
                int maxCount = 0;
                for (Map.Entry<Character, Integer> entry : count.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        bestChar = entry.getKey();
                    }
                }

                int replacements = targetLength - maxCount;
                if (replacements <= k) {
                    results.add(new ReplacementResult(i, i + targetLength - 1, bestChar, replacements));
                }
            }

            return results;
        }
    }

    // Follow-up 6: Minimum operations for target length
    public static class MinimumOperations {

        public static int minOperationsForLength(String s, int targetLength) {
            if (targetLength > s.length()) {
                return -1;
            }

            int minOperations = Integer.MAX_VALUE;

            for (int i = 0; i <= s.length() - targetLength; i++) {
                Map<Character, Integer> count = new HashMap<>();

                for (int j = i; j < i + targetLength; j++) {
                    char c = s.charAt(j);
                    count.put(c, count.getOrDefault(c, 0) + 1);
                }

                int maxCount = count.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                int operations = targetLength - maxCount;
                minOperations = Math.min(minOperations, operations);
            }

            return minOperations;
        }

        public static Map<Integer, Integer> minOperationsForAllLengths(String s) {
            Map<Integer, Integer> result = new HashMap<>();

            for (int len = 1; len <= s.length(); len++) {
                result.put(len, minOperationsForLength(s, len));
            }

            return result;
        }

        public static int maxLengthWithOperations(String s, int maxOperations) {
            int maxLength = 0;

            for (int len = 1; len <= s.length(); len++) {
                int minOps = minOperationsForLength(s, len);
                if (minOps <= maxOperations) {
                    maxLength = len;
                }
            }

            return maxLength;
        }
    }

    // Performance comparison utility
    public static void compareApproaches(String s, int k) {
        System.out.println("=== Performance Comparison ===");
        System.out.println("String length: " + s.length() + ", k: " + k);

        long start, end;
        int iterations = 10000;

        // Sliding Window HashMap
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            characterReplacement(s, k);
        }
        end = System.nanoTime();
        System.out.println("HashMap approach: " + (end - start) / 1_000_000 + " ms");

        // Sliding Window Array
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            characterReplacementArray(s, k);
        }
        end = System.nanoTime();
        System.out.println("Array approach: " + (end - start) / 1_000_000 + " ms");

        // Optimized approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            characterReplacementOptimized(s, k);
        }
        end = System.nanoTime();
        System.out.println("Optimized approach: " + (end - start) / 1_000_000 + " ms");

        // Character-specific approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            characterReplacementByChar(s, k);
        }
        end = System.nanoTime();
        System.out.println("Character-specific approach: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String[] testStrings = { "ABAB", "AABABBA", "ABCDE", "AAAA", "ABCDABCD" };
        int[] testK = { 2, 1, 1, 0, 2 };

        for (int i = 0; i < testStrings.length; i++) {
            String s = testStrings[i];
            int k = testK[i];

            int result1 = characterReplacement(s, k);
            int result2 = characterReplacementArray(s, k);
            int result3 = characterReplacementOptimized(s, k);
            int result4 = characterReplacementByChar(s, k);

            System.out.printf("s=\"%s\", k=%d%n", s, k);
            System.out.printf("  HashMap: %d, Array: %d, Optimized: %d, ByChar: %d%n",
                    result1, result2, result3, result4);

            if (!(result1 == result2 && result2 == result3 && result3 == result4)) {
                System.out.println("  WARNING: Inconsistent results!");
            }
        }

        // Test Case 2: Mixed case handling
        System.out.println("\n=== Test Case 2: Mixed Case Handling ===");

        String mixedCase = "AaBbCcDd";
        int k = 2;

        int caseSensitive = MixedCaseHandling.characterReplacementCaseSensitive(mixedCase, k);
        int ignoreCase = MixedCaseHandling.characterReplacementIgnoreCase(mixedCase, k);
        int mixedCaseResult = MixedCaseHandling.characterReplacementMixedCase(mixedCase, k);

        System.out.printf("String: \"%s\", k=%d%n", mixedCase, k);
        System.out.printf("Case sensitive: %d%n", caseSensitive);
        System.out.printf("Ignore case: %d%n", ignoreCase);
        System.out.printf("Mixed case: %d%n", mixedCaseResult);

        // Test Case 3: Multiple character replacement
        System.out.println("\n=== Test Case 3: Multiple Character Replacement ===");

        String multiChar = "ABCABCABC";
        Set<Character> targetChars = Set.of('A', 'B');

        int multiResult = MultipleCharacterReplacement.longestSubstringWithKReplacements(multiChar, 2, targetChars);
        int twoCharResult = MultipleCharacterReplacement.longestSubstringTwoCharacters(multiChar, 2, 'A', 'B');

        System.out.printf("String: \"%s\", k=2, target chars: %s%n", multiChar, targetChars);
        System.out.printf("Multiple chars result: %d%n", multiResult);
        System.out.printf("Two chars result: %d%n", twoCharResult);

        // Test Case 4: K-th longest replacement
        System.out.println("\n=== Test Case 4: K-th Longest Replacement ===");

        String kthTest = "AABABBA";
        List<Integer> allLengths = KthLongestReplacement.allPossibleLengths(kthTest, 1);
        int kthLongest = KthLongestReplacement.kthLongestReplacement(kthTest, 1, 3);
        List<Integer> topK = KthLongestReplacement.topKLongestReplacements(kthTest, 1, 5);

        System.out.printf("String: \"%s\", k=1%n", kthTest);
        System.out.printf("All possible lengths: %s%n", allLengths);
        System.out.printf("3rd longest: %d%n", kthLongest);
        System.out.printf("Top 5 lengths: %s%n", topK);

        // Test Case 5: Constrained replacement
        System.out.println("\n=== Test Case 5: Constrained Replacement ===");

        String constrainedTest = "ABCABC";
        char forbidden = 'C';
        Set<Character> allowed = Set.of('A', 'B', 'D');

        int constrainedResult = ConstrainedReplacement.characterReplacementWithConstraint(constrainedTest, 2,
                forbidden);
        int allowedResult = ConstrainedReplacement.characterReplacementOnlyAllowed(constrainedTest, 2, allowed);

        System.out.printf("String: \"%s\", k=2%n", constrainedTest);
        System.out.printf("Forbidden char '%c': %d%n", forbidden, constrainedResult);
        System.out.printf("Only allowed %s: %d%n", allowed, allowedResult);

        // Test Case 6: All possible replacements
        System.out.println("\n=== Test Case 6: All Possible Replacements ===");

        String allTest = "ABAB";
        List<AllPossibleReplacements.ReplacementResult> allReplacements = AllPossibleReplacements
                .findAllReplacements(allTest, 1, 3);
        List<AllPossibleReplacements.ReplacementResult> optimalReplacements = AllPossibleReplacements
                .findOptimalReplacements(allTest, 1, 4);

        System.out.printf("String: \"%s\", k=1, minLength=3%n", allTest);
        System.out.println("All replacements:");
        allReplacements.forEach(System.out::println);

        System.out.printf("Optimal replacements for length 4:%n");
        optimalReplacements.forEach(System.out::println);

        // Test Case 7: Minimum operations
        System.out.println("\n=== Test Case 7: Minimum Operations ===");

        String minOpsTest = "AABABBA";
        int minOps = MinimumOperations.minOperationsForLength(minOpsTest, 5);
        Map<Integer, Integer> allMinOps = MinimumOperations.minOperationsForAllLengths(minOpsTest);
        int maxLen = MinimumOperations.maxLengthWithOperations(minOpsTest, 2);

        System.out.printf("String: \"%s\"%n", minOpsTest);
        System.out.printf("Min operations for length 5: %d%n", minOps);
        System.out.printf("Min operations for all lengths: %s%n", allMinOps);
        System.out.printf("Max length with 2 operations: %d%n", maxLen);

        // Test Case 8: Edge cases
        System.out.println("\n=== Test Case 8: Edge Cases ===");

        String[] edgeCases = { "A", "", "AAAA", "ABCDEFGHIJKLMNOPQRSTUVWXYZ" };
        int[] edgeK = { 0, 0, 5, 10 };

        for (int i = 0; i < edgeCases.length; i++) {
            if (edgeCases[i].isEmpty())
                continue;

            String s = edgeCases[i];
            int kValue = edgeK[i];

            int result = characterReplacement(s, kValue);
            System.out.printf("s=\"%s\", k=%d, result=%d%n",
                    s.length() > 20 ? s.substring(0, 20) + "..." : s, kValue, result);
        }

        // Test Case 9: Performance comparison
        System.out.println("\n=== Test Case 9: Performance Comparison ===");

        String perfTest = "ABCDEFGHIJ".repeat(100);
        compareApproaches(perfTest, 50);

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        Random random = new Random(42);
        int testCases = 1000;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            StringBuilder sb = new StringBuilder();
            int length = random.nextInt(50) + 1;

            for (int i = 0; i < length; i++) {
                sb.append((char) ('A' + random.nextInt(5))); // A-E
            }

            String s = sb.toString();
            int kValue = random.nextInt(length + 1);

            int result1 = characterReplacement(s, kValue);
            int result2 = characterReplacementArray(s, kValue);
            int result3 = characterReplacementOptimized(s, kValue);

            if (result1 == result2 && result2 == result3) {
                passed++;
            }
        }

        System.out.printf("Stress test: %d/%d passed%n", passed, testCases);

        System.out.println("\nLongest Repeating Character Replacement testing completed successfully!");
    }
}
