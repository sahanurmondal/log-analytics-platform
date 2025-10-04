package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 424: Longest Repeating Character Replacement
 * https://leetcode.com/problems/longest-repeating-character-replacement/
 * 
 * Companies: Facebook, Amazon, Microsoft, Google, Apple, Bloomberg
 * Frequency: Very High (Asked in 600+ interviews)
 *
 * Description:
 * You are given a string s and an integer k. You can choose any character of
 * the string
 * and change it to any other uppercase English letter. You can perform this
 * operation
 * at most k times. Return the length of the longest substring containing the
 * same letter
 * you can get after performing the above operations.
 * 
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of only uppercase English letters.
 * - 0 <= k <= s.length
 * 
 * Follow-up Questions:
 * 1. How would you handle lowercase letters or mixed case?
 * 2. Can you find all possible longest substrings?
 * 3. What if you want to minimize the number of operations?
 * 4. How to handle multiple types of characters (numbers, symbols)?
 * 5. Can you solve for multiple queries efficiently?
 * 6. What about finding the character that gives the longest substring?
 */
public class LongestRepeatingCharacterReplacement {

    // Approach 1: Sliding Window with Character Count - O(n) time, O(1) space
    public static int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxCount = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            // Expand window
            count[s.charAt(right) - 'A']++;
            maxCount = Math.max(maxCount, count[s.charAt(right) - 'A']);

            // Check if window is valid
            while (right - left + 1 - maxCount > k) {
                // Shrink window
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 2: Sliding Window with HashMap - More flexible for different
    // character sets
    public static int characterReplacementHashMap(String s, int k) {
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

    // Approach 3: Optimized sliding window (never shrink maxCount)
    public static int characterReplacementOptimized(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxCount = 0;

        for (int right = 0; right < s.length(); right++) {
            count[s.charAt(right) - 'A']++;
            maxCount = Math.max(maxCount, count[s.charAt(right) - 'A']);

            // Only shrink if current window is invalid
            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }
        }

        return s.length() - left;
    }

    // Follow-up 1: Handle mixed case letters
    public static class MixedCaseHandler {

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

        // Case-sensitive version
        public static int characterReplacementCaseSensitive(String s, int k) {
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
    }

    // Follow-up 2: Find all possible longest substrings
    public static class FindAllLongestSubstrings {

        public static List<String> findAllLongestSubstrings(String s, int k) {
            List<String> result = new ArrayList<>();
            int maxLength = characterReplacement(s, k);

            if (maxLength == 0)
                return result;

            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    if (count.get(leftChar) == 0) {
                        count.remove(leftChar);
                    }
                    left++;
                    // Recalculate maxCount
                    maxCount = count.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                }

                if (right - left + 1 == maxLength) {
                    String substring = s.substring(left, right + 1);
                    if (!result.contains(substring)) {
                        result.add(substring);
                    }
                }
            }

            return result;
        }

        // Find all longest substrings with their positions
        public static List<int[]> findAllLongestSubstringPositions(String s, int k) {
            List<int[]> result = new ArrayList<>();
            int maxLength = characterReplacement(s, k);

            if (maxLength == 0)
                return result;

            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);
                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    if (count.get(leftChar) == 0) {
                        count.remove(leftChar);
                    }
                    left++;
                    maxCount = count.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                }

                if (right - left + 1 == maxLength) {
                    result.add(new int[] { left, right });
                }
            }

            return result;
        }
    }

    // Follow-up 3: Minimize number of operations
    public static class MinimizeOperations {

        public static class Result {
            int length;
            int operations;
            char targetChar;

            public Result(int length, int operations, char targetChar) {
                this.length = length;
                this.operations = operations;
                this.targetChar = targetChar;
            }

            @Override
            public String toString() {
                return String.format("Length: %d, Operations: %d, Target: %c",
                        length, operations, targetChar);
            }
        }

        public static Result findLongestWithMinOperations(String s, int k) {
            int bestLength = 0;
            int minOperations = Integer.MAX_VALUE;
            char bestChar = 'A';

            // Try each character as target
            for (char target = 'A'; target <= 'Z'; target++) {
                int[] result = findLongestForChar(s, k, target);
                int length = result[0];
                int operations = result[1];

                if (length > bestLength || (length == bestLength && operations < minOperations)) {
                    bestLength = length;
                    minOperations = operations;
                    bestChar = target;
                }
            }

            return new Result(bestLength, minOperations, bestChar);
        }

        private static int[] findLongestForChar(String s, int k, char target) {
            int left = 0;
            int operations = 0;
            int maxLength = 0;
            int minOpsForMaxLength = Integer.MAX_VALUE;

            for (int right = 0; right < s.length(); right++) {
                if (s.charAt(right) != target) {
                    operations++;
                }

                while (operations > k) {
                    if (s.charAt(left) != target) {
                        operations--;
                    }
                    left++;
                }

                int currentLength = right - left + 1;
                if (currentLength > maxLength) {
                    maxLength = currentLength;
                    minOpsForMaxLength = operations;
                } else if (currentLength == maxLength) {
                    minOpsForMaxLength = Math.min(minOpsForMaxLength, operations);
                }
            }

            return new int[] { maxLength, minOpsForMaxLength };
        }
    }

    // Follow-up 4: Handle multiple character types
    public static class MultipleCharacterTypes {

        public static int characterReplacementAlphanumeric(String s, int k) {
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
                    if (count.get(leftChar) == 0) {
                        count.remove(leftChar);
                    }
                    left++;
                    // Recalculate maxCount when window shrinks
                    maxCount = count.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }

        // Handle only specific character types
        public static int characterReplacementFiltered(String s, int k, String allowedChars) {
            Set<Character> allowed = new HashSet<>();
            for (char c : allowedChars.toCharArray()) {
                allowed.add(c);
            }

            Map<Character, Integer> count = new HashMap<>();
            int left = 0;
            int maxCount = 0;
            int maxLength = 0;

            for (int right = 0; right < s.length(); right++) {
                char rightChar = s.charAt(right);

                if (!allowed.contains(rightChar)) {
                    // Reset window when invalid character found
                    count.clear();
                    left = right + 1;
                    maxCount = 0;
                    continue;
                }

                count.put(rightChar, count.getOrDefault(rightChar, 0) + 1);
                maxCount = Math.max(maxCount, count.get(rightChar));

                while (right - left + 1 - maxCount > k) {
                    char leftChar = s.charAt(left);
                    count.put(leftChar, count.get(leftChar) - 1);
                    if (count.get(leftChar) == 0) {
                        count.remove(leftChar);
                    }
                    left++;
                    maxCount = count.values().stream().mapToInt(Integer::intValue).max().orElse(0);
                }

                maxLength = Math.max(maxLength, right - left + 1);
            }

            return maxLength;
        }
    }

    // Follow-up 5: Multiple queries optimization
    public static class MultipleQueriesOptimizer {
        private String s;
        private Map<Integer, Integer> cache;

        public MultipleQueriesOptimizer(String s) {
            this.s = s;
            this.cache = new HashMap<>();
        }

        public int characterReplacement(int k) {
            if (cache.containsKey(k)) {
                return cache.get(k);
            }

            int result = LongestRepeatingCharacterReplacement.characterReplacement(s, k);
            cache.put(k, result);
            return result;
        }

        public void updateString(String newS) {
            this.s = newS;
            this.cache.clear();
        }

        public Map<Integer, Integer> getAllResults(int maxK) {
            Map<Integer, Integer> results = new HashMap<>();
            for (int k = 0; k <= maxK; k++) {
                results.put(k, characterReplacement(k));
            }
            return results;
        }
    }

    // Follow-up 6: Find best character for longest substring
    public static class FindBestCharacter {

        public static class CharacterResult {
            char character;
            int maxLength;
            int operations;
            String substring;

            public CharacterResult(char character, int maxLength, int operations, String substring) {
                this.character = character;
                this.maxLength = maxLength;
                this.operations = operations;
                this.substring = substring;
            }

            @Override
            public String toString() {
                return String.format("Char: %c, Length: %d, Ops: %d, Substring: %s",
                        character, maxLength, operations, substring);
            }
        }

        public static CharacterResult findBestCharacter(String s, int k) {
            char bestChar = 'A';
            int bestLength = 0;
            int bestOperations = Integer.MAX_VALUE;
            String bestSubstring = "";

            for (char target = 'A'; target <= 'Z'; target++) {
                CharacterResult result = findLongestForCharacter(s, k, target);

                if (result.maxLength > bestLength ||
                        (result.maxLength == bestLength && result.operations < bestOperations)) {
                    bestChar = target;
                    bestLength = result.maxLength;
                    bestOperations = result.operations;
                    bestSubstring = result.substring;
                }
            }

            return new CharacterResult(bestChar, bestLength, bestOperations, bestSubstring);
        }

        private static CharacterResult findLongestForCharacter(String s, int k, char target) {
            int left = 0;
            int operations = 0;
            int maxLength = 0;
            int bestLeft = 0, bestRight = -1;
            int minOperations = Integer.MAX_VALUE;

            for (int right = 0; right < s.length(); right++) {
                if (s.charAt(right) != target) {
                    operations++;
                }

                while (operations > k) {
                    if (s.charAt(left) != target) {
                        operations--;
                    }
                    left++;
                }

                if (right - left + 1 > maxLength) {
                    maxLength = right - left + 1;
                    bestLeft = left;
                    bestRight = right;
                    minOperations = operations;
                } else if (right - left + 1 == maxLength && operations < minOperations) {
                    bestLeft = left;
                    bestRight = right;
                    minOperations = operations;
                }
            }

            String substring = bestRight >= bestLeft ? s.substring(bestLeft, bestRight + 1) : "";
            return new CharacterResult(target, maxLength, minOperations, substring);
        }

        public static List<CharacterResult> getAllCharacterResults(String s, int k) {
            List<CharacterResult> results = new ArrayList<>();

            for (char target = 'A'; target <= 'Z'; target++) {
                CharacterResult result = findLongestForCharacter(s, k, target);
                if (result.maxLength > 0) {
                    results.add(result);
                }
            }

            results.sort((a, b) -> {
                if (a.maxLength != b.maxLength) {
                    return Integer.compare(b.maxLength, a.maxLength);
                }
                return Integer.compare(a.operations, b.operations);
            });

            return results;
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(String s, int k, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("String length: " + s.length() + ", k=" + k + ", Iterations: " + iterations);

            // Array-based approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                characterReplacement(s, k);
            }
            long arrayTime = System.nanoTime() - start;

            // HashMap-based approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                characterReplacementHashMap(s, k);
            }
            long hashMapTime = System.nanoTime() - start;

            // Optimized approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                characterReplacementOptimized(s, k);
            }
            long optimizedTime = System.nanoTime() - start;

            System.out.println("Array-based: " + arrayTime / 1_000_000 + " ms");
            System.out.println("HashMap-based: " + hashMapTime / 1_000_000 + " ms");
            System.out.println("Optimized: " + optimizedTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String s1 = "ABAB";
        int k1 = 2;

        System.out.println("Input: s=\"" + s1 + "\", k=" + k1);
        System.out.println("Array approach: " + characterReplacement(s1, k1));
        System.out.println("HashMap approach: " + characterReplacementHashMap(s1, k1));
        System.out.println("Optimized approach: " + characterReplacementOptimized(s1, k1));

        // Test Case 2: Different examples
        System.out.println("\n=== Test Case 2: Different Examples ===");

        String[] testStrings = { "AABABBA", "ABCDEFFG", "AAAA", "ABCDEFG" };
        int[] testKs = { 1, 2, 0, 3 };

        for (int i = 0; i < testStrings.length; i++) {
            System.out.println("s=\"" + testStrings[i] + "\", k=" + testKs[i] +
                    " -> " + characterReplacement(testStrings[i], testKs[i]));
        }

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        System.out.println("Empty string: " + characterReplacement("", 1));
        System.out.println("Single character: " + characterReplacement("A", 0));
        System.out.println("Single character (k=1): " + characterReplacement("A", 1));
        System.out.println("All same characters: " + characterReplacement("AAAA", 2));
        System.out.println("k=0: " + characterReplacement("ABCDEF", 0));

        // Test Case 4: Mixed case handling
        System.out.println("\n=== Test Case 4: Mixed Case Handling ===");

        String mixedCase = "AaBbCc";
        System.out.println("Mixed case \"" + mixedCase + "\":");
        System.out.println("Case insensitive: " +
                MixedCaseHandler.characterReplacementMixedCase(mixedCase, 2));
        System.out.println("Case sensitive: " +
                MixedCaseHandler.characterReplacementCaseSensitive(mixedCase, 2));

        // Test Case 5: Find all longest substrings
        System.out.println("\n=== Test Case 5: Find All Longest Substrings ===");

        String s5 = "AABABBA";
        int k5 = 1;

        System.out.println("Input: \"" + s5 + "\", k=" + k5);
        List<String> allLongest = FindAllLongestSubstrings.findAllLongestSubstrings(s5, k5);
        System.out.println("All longest substrings: " + allLongest);

        List<int[]> positions = FindAllLongestSubstrings.findAllLongestSubstringPositions(s5, k5);
        System.out.print("Positions: ");
        for (int[] pos : positions) {
            System.out.print("[" + pos[0] + "," + pos[1] + "] ");
        }
        System.out.println();

        // Test Case 6: Minimize operations
        System.out.println("\n=== Test Case 6: Minimize Operations ===");

        String s6 = "ABCDABCD";
        int k6 = 2;

        System.out.println("Input: \"" + s6 + "\", k=" + k6);
        MinimizeOperations.Result minResult = MinimizeOperations.findLongestWithMinOperations(s6, k6);
        System.out.println("Best result: " + minResult);

        // Test Case 7: Multiple character types
        System.out.println("\n=== Test Case 7: Multiple Character Types ===");

        String alphanumeric = "ABC123ABC";
        System.out.println("Alphanumeric \"" + alphanumeric + "\":");
        System.out.println("All characters: " +
                MultipleCharacterTypes.characterReplacementAlphanumeric(alphanumeric, 2));
        System.out.println("Letters only: " +
                MultipleCharacterTypes.characterReplacementFiltered(alphanumeric, 2, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        System.out.println("Numbers only: " +
                MultipleCharacterTypes.characterReplacementFiltered(alphanumeric, 2, "0123456789"));

        // Test Case 8: Multiple queries optimization
        System.out.println("\n=== Test Case 8: Multiple Queries ===");

        String s8 = "AABABBA";
        MultipleQueriesOptimizer optimizer = new MultipleQueriesOptimizer(s8);

        System.out.println("Input: \"" + s8 + "\"");
        for (int k = 0; k <= 3; k++) {
            System.out.println("k=" + k + ": " + optimizer.characterReplacement(k));
        }

        System.out.println("All results (k=0 to 5): " + optimizer.getAllResults(5));

        // Test Case 9: Find best character
        System.out.println("\n=== Test Case 9: Find Best Character ===");

        String s9 = "ABCDABCDAB";
        int k9 = 2;

        System.out.println("Input: \"" + s9 + "\", k=" + k9);
        FindBestCharacter.CharacterResult bestChar = FindBestCharacter.findBestCharacter(s9, k9);
        System.out.println("Best character: " + bestChar);

        System.out.println("\nAll character results:");
        List<FindBestCharacter.CharacterResult> allResults = FindBestCharacter.getAllCharacterResults(s9, k9);
        for (int i = 0; i < Math.min(5, allResults.size()); i++) {
            System.out.println((i + 1) + ". " + allResults.get(i));
        }

        // Test Case 10: Large string performance
        System.out.println("\n=== Test Case 10: Large String Performance ===");

        StringBuilder largeString = new StringBuilder();
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            largeString.append((char) ('A' + random.nextInt(5))); // Use A-E
        }

        String largeS = largeString.toString();
        int largeK = 1000;

        long start = System.currentTimeMillis();
        int result = characterReplacement(largeS, largeK);
        long end = System.currentTimeMillis();

        System.out.println("Large string length: " + largeS.length());
        System.out.println("k: " + largeK);
        System.out.println("Result: " + result);
        System.out.println("Time taken: " + (end - start) + " ms");

        // Test Case 11: Stress test
        System.out.println("\n=== Test Case 11: Stress Test ===");

        int testCases = 100;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int length = random.nextInt(50) + 1;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append((char) ('A' + random.nextInt(4))); // Use A-D
            }
            String testString = sb.toString();
            int testK = random.nextInt(length + 1);

            int result1 = characterReplacement(testString, testK);
            int result2 = characterReplacementHashMap(testString, testK);
            int result3 = characterReplacementOptimized(testString, testK);

            if (result1 == result2 && result2 == result3) {
                passed++;
            } else {
                System.out.println("Mismatch for: " + testString + ", k=" + testK +
                        " -> " + result1 + ", " + result2 + ", " + result3);
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Test Case 12: Boundary conditions
        System.out.println("\n=== Test Case 12: Boundary Conditions ===");

        String maxString = "A".repeat(100000);
        System.out.println("Max length string (all same): " + characterReplacement(maxString, 1000));

        String alternating = "AB".repeat(25000);
        System.out.println("Alternating pattern: " + characterReplacement(alternating, 25000));

        String diverse = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".repeat(1000);
        System.out.println("Diverse pattern: " + characterReplacement(diverse.substring(0, 10000), 100));

        // Performance comparison
        PerformanceComparison.compareApproaches("AABABBA".repeat(1000), 500, 1000);

        System.out.println("\nLongest Repeating Character Replacement testing completed successfully!");
    }
}
