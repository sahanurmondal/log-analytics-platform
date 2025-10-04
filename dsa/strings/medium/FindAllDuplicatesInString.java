package strings.medium;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LeetCode 442: Find All Duplicates in an Array (Adapted for Strings)
 * Plus multiple string duplicate finding variations
 * 
 * Companies: Microsoft, Google, Amazon, Facebook, Apple, Bloomberg, Adobe
 * Frequency: High (Asked in 700+ interviews)
 *
 * Description:
 * Given a string containing characters, find all characters that appear more
 * than once.
 * Extended to find various types of duplicates in strings including:
 * - Duplicate characters
 * - Duplicate substrings
 * - Duplicate words
 * - Duplicate patterns
 * 
 * Constraints vary by problem variant.
 * 
 * Follow-up Questions:
 * 1. How would you find duplicates with case-insensitive comparison?
 * 2. Can you find duplicate substrings of specific lengths?
 * 3. What about finding overlapping vs non-overlapping duplicates?
 * 4. How to find duplicates with wildcards or patterns?
 * 5. Can you find the longest duplicate substring?
 * 6. What about finding duplicates across multiple strings?
 */
public class FindAllDuplicatesInString {

    // Problem 1: Find all duplicate characters in a string

    // Approach 1: Using HashMap - O(n) time, O(k) space where k is unique
    // characters
    public static List<Character> findDuplicateCharacters(String s) {
        Map<Character, Integer> charCount = new HashMap<>();
        List<Character> duplicates = new ArrayList<>();

        // Count frequencies
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // Find duplicates
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicates.add(entry.getKey());
            }
        }

        return duplicates;
    }

    // Approach 2: Using Set (first occurrence tracking) - O(n) time, O(k) space
    public static List<Character> findDuplicateCharactersSet(String s) {
        Set<Character> seen = new HashSet<>();
        Set<Character> duplicates = new HashSet<>();

        for (char c : s.toCharArray()) {
            if (seen.contains(c)) {
                duplicates.add(c);
            } else {
                seen.add(c);
            }
        }

        return new ArrayList<>(duplicates);
    }

    // Approach 3: Using frequency array (for ASCII) - O(n) time, O(1) space
    public static List<Character> findDuplicateCharactersArray(String s) {
        int[] count = new int[256]; // ASCII characters
        List<Character> duplicates = new ArrayList<>();

        // Count frequencies
        for (char c : s.toCharArray()) {
            count[c]++;
        }

        // Find duplicates
        for (int i = 0; i < count.length; i++) {
            if (count[i] > 1) {
                duplicates.add((char) i);
            }
        }

        return duplicates;
    }

    // Approach 4: Stream-based approach - O(n) time, O(k) space
    public static List<Character> findDuplicateCharactersStream(String s) {
        return s.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Problem 2: Find all duplicate substrings of length k

    // Approach 1: Using HashMap with sliding window - O(n*k) time, O(n*k) space
    public static List<String> findDuplicateSubstrings(String s, int k) {
        if (s.length() < k) {
            return new ArrayList<>();
        }

        Map<String, Integer> substringCount = new HashMap<>();

        // Generate all substrings of length k
        for (int i = 0; i <= s.length() - k; i++) {
            String substring = s.substring(i, i + k);
            substringCount.put(substring, substringCount.getOrDefault(substring, 0) + 1);
        }

        // Find duplicates
        return substringCount.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Approach 2: Using rolling hash for efficiency - O(n) time, O(n) space
    public static List<String> findDuplicateSubstringsRollingHash(String s, int k) {
        if (s.length() < k) {
            return new ArrayList<>();
        }

        Map<Long, List<Integer>> hashToIndices = new HashMap<>();
        Set<String> duplicates = new HashSet<>();

        long base = 256;
        long mod = 1000000007L;
        long basePowK = 1;

        // Calculate base^k % mod
        for (int i = 0; i < k; i++) {
            basePowK = (basePowK * base) % mod;
        }

        long hash = 0;

        // Calculate hash for first k characters
        for (int i = 0; i < k; i++) {
            hash = (hash * base + s.charAt(i)) % mod;
        }

        if (!hashToIndices.containsKey(hash)) {
            hashToIndices.put(hash, new ArrayList<>());
        }
        hashToIndices.get(hash).add(0);

        // Rolling hash for remaining substrings
        for (int i = k; i < s.length(); i++) {
            // Remove leftmost character and add rightmost character
            hash = (hash - (s.charAt(i - k) * basePowK) % mod + mod) % mod;
            hash = (hash * base + s.charAt(i)) % mod;

            int startIndex = i - k + 1;
            List<Integer> indices = hashToIndices.get(hash);

            if (indices != null) {
                // Check for actual substring match (handle hash collisions)
                String currentSubstring = s.substring(startIndex, i + 1);
                for (int prevIndex : indices) {
                    String prevSubstring = s.substring(prevIndex, prevIndex + k);
                    if (currentSubstring.equals(prevSubstring)) {
                        duplicates.add(currentSubstring);
                        break;
                    }
                }
            }

            if (!hashToIndices.containsKey(hash)) {
                hashToIndices.put(hash, new ArrayList<>());
            }
            hashToIndices.get(hash).add(startIndex);
        }

        return new ArrayList<>(duplicates);
    }

    // Problem 3: Find all duplicate words in a string

    // Approach 1: Split and count - O(n) time, O(w) space where w is unique words
    public static List<String> findDuplicateWords(String s) {
        String[] words = s.toLowerCase().split("\\s+");
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            // Remove punctuation
            word = word.replaceAll("[^a-zA-Z]", "");
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordCount.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // Approach 2: Using regex with more sophisticated word boundary detection
    public static List<String> findDuplicateWordsRegex(String s) {
        // Split by word boundaries, keeping only alphabetic words
        String[] words = s.toLowerCase().split("\\W+");
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (!word.isEmpty() && word.matches("[a-z]+")) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        return wordCount.entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .sorted()
                .collect(Collectors.toList());
    }

    // Follow-up 1: Case-insensitive duplicates
    public static class CaseInsensitive {

        public static List<Character> findDuplicateCharactersCaseInsensitive(String s) {
            Map<Character, Integer> charCount = new HashMap<>();

            for (char c : s.toCharArray()) {
                char lowerC = Character.toLowerCase(c);
                charCount.put(lowerC, charCount.getOrDefault(lowerC, 0) + 1);
            }

            return charCount.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        public static List<String> findDuplicateSubstringsCaseInsensitive(String s, int k) {
            return findDuplicateSubstrings(s.toLowerCase(), k);
        }

        public static List<String> findDuplicateWordsCaseInsensitive(String s) {
            return findDuplicateWords(s); // Already handles case insensitivity
        }
    }

    // Follow-up 2: Find duplicate substrings of all possible lengths
    public static class AllLengthDuplicates {

        public static Map<Integer, List<String>> findDuplicateSubstringsAllLengths(String s) {
            Map<Integer, List<String>> result = new HashMap<>();

            for (int len = 1; len <= s.length() / 2; len++) {
                List<String> duplicates = findDuplicateSubstrings(s, len);
                if (!duplicates.isEmpty()) {
                    result.put(len, duplicates);
                }
            }

            return result;
        }

        public static List<String> findAllDuplicateSubstrings(String s) {
            Set<String> allDuplicates = new HashSet<>();

            for (int len = 1; len <= s.length() / 2; len++) {
                List<String> duplicates = findDuplicateSubstrings(s, len);
                allDuplicates.addAll(duplicates);
            }

            return new ArrayList<>(allDuplicates);
        }

        // Find the longest duplicate substring
        public static String findLongestDuplicateSubstring(String s) {
            String longest = "";

            for (int len = s.length() / 2; len >= 1; len--) {
                List<String> duplicates = findDuplicateSubstrings(s, len);
                if (!duplicates.isEmpty()) {
                    return duplicates.get(0); // Return first one found
                }
            }

            return longest;
        }

        // Binary search approach for longest duplicate substring
        public static String findLongestDuplicateSubstringBinarySearch(String s) {
            int left = 1, right = s.length() / 2;
            String result = "";

            while (left <= right) {
                int mid = left + (right - left) / 2;
                List<String> duplicates = findDuplicateSubstringsRollingHash(s, mid);

                if (!duplicates.isEmpty()) {
                    result = duplicates.get(0);
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    // Follow-up 3: Overlapping vs Non-overlapping duplicates
    public static class OverlappingAnalysis {

        public static class DuplicateInfo {
            String substring;
            List<Integer> positions;
            boolean hasOverlapping;

            public DuplicateInfo(String substring, List<Integer> positions) {
                this.substring = substring;
                this.positions = new ArrayList<>(positions);
                this.hasOverlapping = checkOverlapping();
            }

            private boolean checkOverlapping() {
                for (int i = 1; i < positions.size(); i++) {
                    if (positions.get(i) < positions.get(i - 1) + substring.length()) {
                        return true;
                    }
                }
                return false;
            }
        }

        public static List<DuplicateInfo> findDuplicateSubstringsWithOverlapInfo(String s, int k) {
            Map<String, List<Integer>> substringPositions = new HashMap<>();

            for (int i = 0; i <= s.length() - k; i++) {
                String substring = s.substring(i, i + k);
                if (!substringPositions.containsKey(substring)) {
                    substringPositions.put(substring, new ArrayList<>());
                }
                substringPositions.get(substring).add(i);
            }

            return substringPositions.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().size() > 1)
                    .map(entry -> new DuplicateInfo(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
        }

        public static List<String> findNonOverlappingDuplicates(String s, int k) {
            return findDuplicateSubstringsWithOverlapInfo(s, k)
                    .stream()
                    .filter(info -> !info.hasOverlapping)
                    .map(info -> info.substring)
                    .collect(Collectors.toList());
        }

        public static List<String> findOverlappingDuplicates(String s, int k) {
            return findDuplicateSubstringsWithOverlapInfo(s, k)
                    .stream()
                    .filter(info -> info.hasOverlapping)
                    .map(info -> info.substring)
                    .collect(Collectors.toList());
        }
    }

    // Follow-up 4: Duplicates with wildcards and patterns
    public static class PatternMatching {

        // Find duplicates allowing single character wildcards (represented by '?')
        public static List<String> findDuplicatesWithWildcard(String s, int k) {
            Map<String, List<Integer>> patternGroups = new HashMap<>();

            for (int i = 0; i <= s.length() - k; i++) {
                String substring = s.substring(i, i + k);

                // Generate all possible wildcard patterns
                Set<String> patterns = generateWildcardPatterns(substring);

                for (String pattern : patterns) {
                    if (!patternGroups.containsKey(pattern)) {
                        patternGroups.put(pattern, new ArrayList<>());
                    }
                    patternGroups.get(pattern).add(i);
                }
            }

            Set<String> result = new HashSet<>();
            for (Map.Entry<String, List<Integer>> entry : patternGroups.entrySet()) {
                if (entry.getValue().size() > 1) {
                    // Convert pattern back to actual substrings
                    for (int pos : entry.getValue()) {
                        result.add(s.substring(pos, pos + k));
                    }
                }
            }

            return new ArrayList<>(result);
        }

        private static Set<String> generateWildcardPatterns(String s) {
            Set<String> patterns = new HashSet<>();
            patterns.add(s); // Original string

            // Generate patterns with single character wildcards
            for (int i = 0; i < s.length(); i++) {
                StringBuilder pattern = new StringBuilder(s);
                pattern.setCharAt(i, '?');
                patterns.add(pattern.toString());
            }

            return patterns;
        }

        // Find duplicates with regex-like patterns
        public static List<String> findDuplicatesWithPattern(String s, String pattern) {
            List<String> matches = new ArrayList<>();
            java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher matcher = compiledPattern.matcher(s);

            while (matcher.find()) {
                matches.add(matcher.group());
            }

            // Find duplicates among matches
            return matches.stream()
                    .collect(Collectors.groupingBy(match -> match, Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        // Find duplicates using edit distance tolerance
        public static List<Set<String>> findDuplicatesWithEditDistance(String s, int k, int maxEditDistance) {
            List<String> allSubstrings = new ArrayList<>();

            for (int i = 0; i <= s.length() - k; i++) {
                allSubstrings.add(s.substring(i, i + k));
            }

            // Group similar substrings
            List<Set<String>> groups = new ArrayList<>();
            Set<String> processed = new HashSet<>();

            for (String current : allSubstrings) {
                if (processed.contains(current)) {
                    continue;
                }

                Set<String> group = new HashSet<>();
                group.add(current);
                processed.add(current);

                for (String other : allSubstrings) {
                    if (!processed.contains(other) &&
                            calculateEditDistance(current, other) <= maxEditDistance) {
                        group.add(other);
                        processed.add(other);
                    }
                }

                if (group.size() > 1) {
                    groups.add(group);
                }
            }

            return groups;
        }

        private static int calculateEditDistance(String s1, String s2) {
            int m = s1.length();
            int n = s2.length();

            int[][] dp = new int[m + 1][n + 1];

            for (int i = 0; i <= m; i++) {
                dp[i][0] = i;
            }

            for (int j = 0; j <= n; j++) {
                dp[0][j] = j;
            }

            for (int i = 1; i <= m; i++) {
                for (int j = 1; j <= n; j++) {
                    if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                        dp[i][j] = dp[i - 1][j - 1];
                    } else {
                        dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                                Math.min(dp[i - 1][j], dp[i][j - 1]));
                    }
                }
            }

            return dp[m][n];
        }
    }

    // Follow-up 5: Advanced duplicate finding
    public static class AdvancedDuplicates {

        // Find repeating patterns (not just exact duplicates)
        public static List<String> findRepeatingPatterns(String s) {
            Set<String> patterns = new HashSet<>();

            for (int len = 1; len <= s.length() / 2; len++) {
                for (int start = 0; start <= s.length() - 2 * len; start++) {
                    String pattern = s.substring(start, start + len);
                    String next = s.substring(start + len, start + 2 * len);

                    if (pattern.equals(next)) {
                        patterns.add(pattern);
                    }
                }
            }

            return new ArrayList<>(patterns);
        }

        // Find palindromic duplicates
        public static List<String> findPalindromicDuplicates(String s, int k) {
            List<String> duplicates = findDuplicateSubstrings(s, k);

            return duplicates.stream()
                    .filter(AdvancedDuplicates::isPalindrome)
                    .collect(Collectors.toList());
        }

        private static boolean isPalindrome(String s) {
            int left = 0, right = s.length() - 1;
            while (left < right) {
                if (s.charAt(left) != s.charAt(right)) {
                    return false;
                }
                left++;
                right--;
            }
            return true;
        }

        // Find anagram duplicates
        public static List<List<String>> findAnagramDuplicates(String s, int k) {
            Map<String, List<String>> anagramGroups = new HashMap<>();

            for (int i = 0; i <= s.length() - k; i++) {
                String substring = s.substring(i, i + k);
                String sorted = sortString(substring);

                if (!anagramGroups.containsKey(sorted)) {
                    anagramGroups.put(sorted, new ArrayList<>());
                }
                anagramGroups.get(sorted).add(substring);
            }

            return anagramGroups.values()
                    .stream()
                    .filter(group -> group.size() > 1)
                    .collect(Collectors.toList());
        }

        private static String sortString(String s) {
            char[] chars = s.toCharArray();
            Arrays.sort(chars);
            return new String(chars);
        }

        // Find duplicates with frequency analysis
        public static Map<String, Integer> findDuplicatesWithFrequency(String s, int k) {
            Map<String, Integer> substringCount = new HashMap<>();

            for (int i = 0; i <= s.length() - k; i++) {
                String substring = s.substring(i, i + k);
                substringCount.put(substring, substringCount.getOrDefault(substring, 0) + 1);
            }

            return substringCount.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1)
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, replacement) -> existing,
                            LinkedHashMap::new));
        }
    }

    // Follow-up 6: Cross-string duplicate finding
    public static class CrossStringDuplicates {

        public static Map<String, List<String>> findDuplicatesAcrossStrings(String[] strings, int k) {
            Map<String, Set<String>> substringToSources = new HashMap<>();

            for (int strIndex = 0; strIndex < strings.length; strIndex++) {
                String currentString = strings[strIndex];

                for (int i = 0; i <= currentString.length() - k; i++) {
                    String substring = currentString.substring(i, i + k);

                    if (!substringToSources.containsKey(substring)) {
                        substringToSources.put(substring, new HashSet<>());
                    }
                    substringToSources.get(substring).add("String" + strIndex);
                }
            }

            // Filter for substrings that appear in multiple strings
            Map<String, List<String>> result = new HashMap<>();
            for (Map.Entry<String, Set<String>> entry : substringToSources.entrySet()) {
                if (entry.getValue().size() > 1) {
                    result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
            }

            return result;
        }

        public static List<String> findCommonSubstrings(String[] strings, int k) {
            if (strings.length == 0) {
                return new ArrayList<>();
            }

            // Start with substrings from first string
            Set<String> common = new HashSet<>();
            String firstString = strings[0];

            for (int i = 0; i <= firstString.length() - k; i++) {
                common.add(firstString.substring(i, i + k));
            }

            // Intersect with substrings from other strings
            for (int strIndex = 1; strIndex < strings.length; strIndex++) {
                Set<String> currentSubstrings = new HashSet<>();
                String currentString = strings[strIndex];

                for (int i = 0; i <= currentString.length() - k; i++) {
                    currentSubstrings.add(currentString.substring(i, i + k));
                }

                common.retainAll(currentSubstrings);

                if (common.isEmpty()) {
                    break;
                }
            }

            return new ArrayList<>(common);
        }

        public static String findLongestCommonSubstring(String[] strings) {
            if (strings.length == 0) {
                return "";
            }

            String shortest = Arrays.stream(strings)
                    .min(Comparator.comparing(String::length))
                    .orElse("");

            for (int len = shortest.length(); len >= 1; len--) {
                List<String> commonSubstrings = findCommonSubstrings(strings, len);
                if (!commonSubstrings.isEmpty()) {
                    return commonSubstrings.get(0);
                }
            }

            return "";
        }
    }

    // Utility methods
    public static void printDuplicateAnalysis(String s) {
        System.out.println("=== Duplicate Analysis for: \"" + s + "\" ===");

        // Character duplicates
        List<Character> charDuplicates = findDuplicateCharacters(s);
        System.out.println("Duplicate characters: " + charDuplicates);

        // Substring duplicates of various lengths
        for (int k = 2; k <= Math.min(5, s.length() / 2); k++) {
            List<String> subDuplicates = findDuplicateSubstrings(s, k);
            if (!subDuplicates.isEmpty()) {
                System.out.println("Duplicate substrings (length " + k + "): " + subDuplicates);
            }
        }

        // Word duplicates
        List<String> wordDuplicates = findDuplicateWords(s);
        if (!wordDuplicates.isEmpty()) {
            System.out.println("Duplicate words: " + wordDuplicates);
        }

        // Longest duplicate substring
        String longest = AllLengthDuplicates.findLongestDuplicateSubstring(s);
        if (!longest.isEmpty()) {
            System.out.println("Longest duplicate substring: \"" + longest + "\"");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic character duplicates
        System.out.println("=== Test Case 1: Character Duplicates ===");

        String[] testStrings = {
                "programming", "hello", "aabbcc", "abcdef", "aaaa"
        };

        for (String s : testStrings) {
            List<Character> hashMap = findDuplicateCharacters(s);
            List<Character> set = findDuplicateCharactersSet(s);
            List<Character> array = findDuplicateCharactersArray(s);
            List<Character> stream = findDuplicateCharactersStream(s);

            System.out.printf("String: \"%s\"%n", s);
            System.out.printf("  HashMap: %s%n", hashMap);
            System.out.printf("  Set: %s%n", set);
            System.out.printf("  Array: %s%n", array);
            System.out.printf("  Stream: %s%n", stream);
            System.out.println();
        }

        // Test Case 2: Substring duplicates
        System.out.println("=== Test Case 2: Substring Duplicates ===");

        String testStr = "abcabcabc";
        for (int k = 2; k <= 4; k++) {
            List<String> normal = findDuplicateSubstrings(testStr, k);
            List<String> rolling = findDuplicateSubstringsRollingHash(testStr, k);

            System.out.printf("Length %d - Normal: %s, Rolling Hash: %s%n", k, normal, rolling);
        }

        // Test Case 3: Word duplicates
        System.out.println("\n=== Test Case 3: Word Duplicates ===");

        String[] sentences = {
                "the quick brown fox jumps over the lazy dog",
                "hello world hello universe",
                "Java is great. Java is powerful. Programming in Java is fun.",
                "No duplicates here in this sentence"
        };

        for (String sentence : sentences) {
            List<String> duplicates = findDuplicateWords(sentence);
            System.out.printf("Sentence: \"%s\"%n", sentence);
            System.out.printf("Duplicate words: %s%n%n", duplicates);
        }

        // Test Case 4: Case-insensitive duplicates
        System.out.println("=== Test Case 4: Case-Insensitive Duplicates ===");

        String mixedCase = "AaAaBbBb";
        List<Character> caseSensitive = findDuplicateCharacters(mixedCase);
        List<Character> caseInsensitive = CaseInsensitive.findDuplicateCharactersCaseInsensitive(mixedCase);

        System.out.printf("String: \"%s\"%n", mixedCase);
        System.out.printf("Case sensitive: %s%n", caseSensitive);
        System.out.printf("Case insensitive: %s%n", caseInsensitive);

        // Test Case 5: All length duplicates
        System.out.println("\n=== Test Case 5: All Length Duplicates ===");

        String complexStr = "abcabcdefdef";
        Map<Integer, List<String>> allLengths = AllLengthDuplicates.findDuplicateSubstringsAllLengths(complexStr);
        String longest = AllLengthDuplicates.findLongestDuplicateSubstring(complexStr);
        String longestBinary = AllLengthDuplicates.findLongestDuplicateSubstringBinarySearch(complexStr);

        System.out.printf("String: \"%s\"%n", complexStr);
        System.out.println("All length duplicates: " + allLengths);
        System.out.printf("Longest duplicate: \"%s\"%n", longest);
        System.out.printf("Longest duplicate (binary search): \"%s\"%n", longestBinary);

        // Test Case 6: Overlapping analysis
        System.out.println("\n=== Test Case 6: Overlapping Analysis ===");

        String overlapStr = "aaaa";
        List<OverlappingAnalysis.DuplicateInfo> overlapInfo = OverlappingAnalysis
                .findDuplicateSubstringsWithOverlapInfo(overlapStr, 2);

        System.out.printf("String: \"%s\" (length 2 substrings)%n", overlapStr);
        for (OverlappingAnalysis.DuplicateInfo info : overlapInfo) {
            System.out.printf("  \"%s\" at positions %s, overlapping: %b%n",
                    info.substring, info.positions, info.hasOverlapping);
        }

        List<String> nonOverlapping = OverlappingAnalysis.findNonOverlappingDuplicates(overlapStr, 2);
        List<String> overlapping = OverlappingAnalysis.findOverlappingDuplicates(overlapStr, 2);
        System.out.println("Non-overlapping: " + nonOverlapping);
        System.out.println("Overlapping: " + overlapping);

        // Test Case 7: Pattern matching
        System.out.println("\n=== Test Case 7: Pattern Matching ===");

        String patternStr = "abcabd";
        List<String> wildcardDuplicates = PatternMatching.findDuplicatesWithWildcard(patternStr, 3);
        System.out.printf("String: \"%s\"%n", patternStr);
        System.out.println("Wildcard duplicates (length 3): " + wildcardDuplicates);

        // Edit distance duplicates
        List<Set<String>> editDistanceGroups = PatternMatching.findDuplicatesWithEditDistance("abcabdabc", 3, 1);
        System.out.println("Edit distance groups (max distance 1): " + editDistanceGroups);

        // Test Case 8: Advanced duplicates
        System.out.println("\n=== Test Case 8: Advanced Duplicates ===");

        String advancedStr = "abcabcdefdef";

        List<String> repeatingPatterns = AdvancedDuplicates.findRepeatingPatterns(advancedStr);
        System.out.printf("String: \"%s\"%n", advancedStr);
        System.out.println("Repeating patterns: " + repeatingPatterns);

        List<String> palindromicDuplicates = AdvancedDuplicates.findPalindromicDuplicates("abaabacddc", 3);
        System.out.println("Palindromic duplicates: " + palindromicDuplicates);

        List<List<String>> anagramGroups = AdvancedDuplicates.findAnagramDuplicates("abccbaabc", 3);
        System.out.println("Anagram groups: " + anagramGroups);

        Map<String, Integer> frequencyMap = AdvancedDuplicates.findDuplicatesWithFrequency(advancedStr, 3);
        System.out.println("Frequency analysis: " + frequencyMap);

        // Test Case 9: Cross-string duplicates
        System.out.println("\n=== Test Case 9: Cross-String Duplicates ===");

        String[] multipleStrings = {
                "abcdef", "defghi", "ghijkl", "abcxyz"
        };

        Map<String, List<String>> crossStringDuplicates = CrossStringDuplicates
                .findDuplicatesAcrossStrings(multipleStrings, 3);
        System.out.println("Strings: " + Arrays.toString(multipleStrings));
        System.out.println("Cross-string duplicates: " + crossStringDuplicates);

        List<String> commonSubstrings = CrossStringDuplicates.findCommonSubstrings(multipleStrings, 2);
        System.out.println("Common substrings (length 2): " + commonSubstrings);

        String longestCommon = CrossStringDuplicates.findLongestCommonSubstring(multipleStrings);
        System.out.println("Longest common substring: \"" + longestCommon + "\"");

        // Test Case 10: Comprehensive analysis
        System.out.println("\n=== Test Case 10: Comprehensive Analysis ===");

        String[] analysisStrings = {
                "programming",
                "hello world hello",
                "abcabcdefdef",
                "The quick brown fox jumps over the lazy dog"
        };

        for (String s : analysisStrings) {
            printDuplicateAnalysis(s);
            System.out.println();
        }

        // Test Case 11: Performance comparison
        System.out.println("=== Test Case 11: Performance Comparison ===");

        String perfTestString = "a".repeat(1000) + "b".repeat(1000) + "a".repeat(1000);
        int iterations = 1000;

        long start, end;

        // HashMap approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findDuplicateCharacters(perfTestString);
        }
        end = System.nanoTime();
        System.out.println("HashMap approach: " + (end - start) / 1_000_000 + " ms");

        // Set approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findDuplicateCharactersSet(perfTestString);
        }
        end = System.nanoTime();
        System.out.println("Set approach: " + (end - start) / 1_000_000 + " ms");

        // Array approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            findDuplicateCharactersArray(perfTestString);
        }
        end = System.nanoTime();
        System.out.println("Array approach: " + (end - start) / 1_000_000 + " ms");

        // Rolling hash vs normal substring comparison
        System.out.println("\nSubstring duplicate finding:");

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            findDuplicateSubstrings(perfTestString, 10);
        }
        end = System.nanoTime();
        System.out.println("Normal substring: " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            findDuplicateSubstringsRollingHash(perfTestString, 10);
        }
        end = System.nanoTime();
        System.out.println("Rolling hash: " + (end - start) / 1_000_000 + " ms");

        System.out.println("\nFind All Duplicates in String testing completed successfully!");
    }
}
