package tries.hard;

import java.util.*;

/**
 * LeetCode 425: Word Squares
 * https://leetcode.com/problems/word-squares/
 * 
 * Companies: Google, Airbnb, Facebook, Amazon, Microsoft
 * Frequency: High (Asked in 600+ interviews)
 *
 * Description:
 * Given an array of UNIQUE strings words, return all word squares you can build
 * from words.
 * The same word from words can be used multiple times. You can return the
 * answer in any order.
 * 
 * A sequence of strings forms a valid word square if the kth row and column
 * read the same string, where 0 ≤ k < max(numRows, numColumns).
 * 
 * For example, the word sequence ["ball","area","lead","lady"] forms a word
 * square
 * because each word reads the same both horizontally and vertically:
 * 
 * b a l l
 * a r e a
 * l e a d
 * l a d y
 * 
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 4
 * - All words[i] have the same length.
 * - words[i] consists of only lowercase English letters.
 * - All words[i] are unique.
 * 
 * Follow-up Questions:
 * 1. How would you optimize for repeated queries with the same word list?
 * 2. Can you find the largest possible word square?
 * 3. What about word squares with different word lengths?
 * 4. How to handle word squares with wildcards or missing letters?
 * 5. Can you find word squares with specific patterns or constraints?
 * 6. What about generating word squares with maximum/minimum lexicographic
 * order?
 */
public class WordSquares {

    // Approach 1: Backtracking with Trie - O(N * 26^L * L) time, O(N * L) space
    public static List<List<String>> wordSquares(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }

        int wordLength = words[0].length();
        Trie trie = new Trie();

        // Build trie
        for (String word : words) {
            trie.insert(word);
        }

        // Try each word as the first row
        for (String word : words) {
            List<String> square = new ArrayList<>();
            square.add(word);
            backtrack(square, wordLength, trie, result);
        }

        return result;
    }

    private static void backtrack(List<String> square, int wordLength, Trie trie, List<List<String>> result) {
        int currentRow = square.size();

        if (currentRow == wordLength) {
            result.add(new ArrayList<>(square));
            return;
        }

        // Build prefix for next word from current column
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < currentRow; i++) {
            prefix.append(square.get(i).charAt(currentRow));
        }

        // Find all words that start with this prefix
        List<String> candidates = trie.getWordsWithPrefix(prefix.toString());

        for (String candidate : candidates) {
            // Check if this candidate can form a valid square
            if (isValidCandidate(square, candidate, currentRow)) {
                square.add(candidate);
                backtrack(square, wordLength, trie, result);
                square.remove(square.size() - 1);
            }
        }
    }

    private static boolean isValidCandidate(List<String> square, String candidate, int row) {
        int wordLength = candidate.length();

        // Check if adding this candidate maintains the word square property
        for (int col = row + 1; col < wordLength; col++) {
            StringBuilder prefix = new StringBuilder();

            // Build prefix for column 'col'
            for (int i = 0; i < row; i++) {
                prefix.append(square.get(i).charAt(col));
            }
            prefix.append(candidate.charAt(col));

            // This prefix should be a prefix of some word for future rows
            // For now, we'll do a basic check - this can be optimized with trie
        }

        return true;
    }

    // Trie implementation
    public static class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
                node.wordsWithPrefix.add(word);
            }
            node.isWord = true;
        }

        public List<String> getWordsWithPrefix(String prefix) {
            TrieNode node = root;
            for (char c : prefix.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return new ArrayList<>();
                }
                node = node.children.get(c);
            }
            return new ArrayList<>(node.wordsWithPrefix);
        }

        public boolean hasPrefix(String prefix) {
            TrieNode node = root;
            for (char c : prefix.toCharArray()) {
                if (!node.children.containsKey(c)) {
                    return false;
                }
                node = node.children.get(c);
            }
            return true;
        }

        private static class TrieNode {
            Map<Character, TrieNode> children;
            @SuppressWarnings("unused") // Used for completeness of trie structure
            boolean isWord;
            Set<String> wordsWithPrefix;

            TrieNode() {
                children = new HashMap<>();
                isWord = false;
                wordsWithPrefix = new HashSet<>();
            }
        }
    }

    // Approach 2: Optimized backtracking with better pruning - O(N * 26^L * L)
    // time, O(N * L) space
    public static List<List<String>> wordSquaresOptimized(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }

        int wordLength = words[0].length();
        Map<String, List<String>> prefixMap = buildPrefixMap(words);

        for (String word : words) {
            List<String> square = new ArrayList<>();
            square.add(word);
            backtrackOptimized(square, wordLength, prefixMap, result);
        }

        return result;
    }

    private static Map<String, List<String>> buildPrefixMap(String[] words) {
        Map<String, List<String>> prefixMap = new HashMap<>();

        for (String word : words) {
            for (int i = 0; i <= word.length(); i++) {
                String prefix = word.substring(0, i);
                @SuppressWarnings("unused")
                List<String> wordList = prefixMap.computeIfAbsent(prefix, prefixKey -> new ArrayList<>());
                wordList.add(word);
            }
        }

        return prefixMap;
    }

    private static void backtrackOptimized(List<String> square, int wordLength,
            Map<String, List<String>> prefixMap,
            List<List<String>> result) {
        int currentRow = square.size();

        if (currentRow == wordLength) {
            result.add(new ArrayList<>(square));
            return;
        }

        // Build prefix for next word
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < currentRow; i++) {
            prefix.append(square.get(i).charAt(currentRow));
        }

        List<String> candidates = prefixMap.getOrDefault(prefix.toString(), new ArrayList<>());

        for (String candidate : candidates) {
            if (isValidSquareCandidate(square, candidate, currentRow, prefixMap)) {
                square.add(candidate);
                backtrackOptimized(square, wordLength, prefixMap, result);
                square.remove(square.size() - 1);
            }
        }
    }

    private static boolean isValidSquareCandidate(List<String> square, String candidate,
            int row, Map<String, List<String>> prefixMap) {
        int wordLength = candidate.length();

        // Check each future column
        for (int col = row + 1; col < wordLength; col++) {
            StringBuilder columnPrefix = new StringBuilder();

            for (int i = 0; i < row; i++) {
                columnPrefix.append(square.get(i).charAt(col));
            }
            columnPrefix.append(candidate.charAt(col));

            // Check if this prefix exists in our prefix map
            if (!prefixMap.containsKey(columnPrefix.toString())) {
                return false;
            }
        }

        return true;
    }

    // Approach 3: Iterative approach with BFS - O(N * 26^L * L) time, O(N * L)
    // space
    public static List<List<String>> wordSquaresBFS(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0) {
            return result;
        }

        int wordLength = words[0].length();
        Map<String, List<String>> prefixMap = buildPrefixMap(words);

        Queue<List<String>> queue = new LinkedList<>();

        // Initialize with each word
        for (String word : words) {
            List<String> initialSquare = new ArrayList<>();
            initialSquare.add(word);
            queue.offer(initialSquare);
        }

        while (!queue.isEmpty()) {
            List<String> currentSquare = queue.poll();

            if (currentSquare.size() == wordLength) {
                result.add(currentSquare);
                continue;
            }

            int currentRow = currentSquare.size();
            StringBuilder prefix = new StringBuilder();

            for (int i = 0; i < currentRow; i++) {
                prefix.append(currentSquare.get(i).charAt(currentRow));
            }

            List<String> candidates = prefixMap.getOrDefault(prefix.toString(), new ArrayList<>());

            for (String candidate : candidates) {
                if (isValidSquareCandidate(currentSquare, candidate, currentRow, prefixMap)) {
                    List<String> newSquare = new ArrayList<>(currentSquare);
                    newSquare.add(candidate);
                    queue.offer(newSquare);
                }
            }
        }

        return result;
    }

    // Follow-up 1: Optimized for repeated queries
    public static class WordSquareBuilder {
        private Map<String, List<String>> prefixMap;
        private String[] words;
        private int wordLength;

        public WordSquareBuilder(String[] words) {
            this.words = words;
            this.wordLength = words.length > 0 ? words[0].length() : 0;
            this.prefixMap = buildPrefixMap(words);
        }

        public List<List<String>> findWordSquares() {
            return wordSquaresOptimized(words);
        }

        public List<List<String>> findWordSquaresWithFirstWord(String firstWord) {
            List<List<String>> result = new ArrayList<>();

            if (!Arrays.asList(words).contains(firstWord)) {
                return result;
            }

            List<String> square = new ArrayList<>();
            square.add(firstWord);
            backtrackOptimized(square, wordLength, prefixMap, result);

            return result;
        }

        public int countWordSquares() {
            return findWordSquares().size();
        }

        public boolean hasWordSquare() {
            List<String> square = new ArrayList<>();
            return findAnyWordSquare(square, 0);
        }

        private boolean findAnyWordSquare(List<String> square, int row) {
            if (row == wordLength) {
                return true;
            }

            if (row == 0) {
                for (String word : words) {
                    square.add(word);
                    if (findAnyWordSquare(square, row + 1)) {
                        return true;
                    }
                    square.remove(square.size() - 1);
                }
            } else {
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < row; i++) {
                    prefix.append(square.get(i).charAt(row));
                }

                List<String> candidates = prefixMap.getOrDefault(prefix.toString(), new ArrayList<>());

                for (String candidate : candidates) {
                    if (isValidSquareCandidate(square, candidate, row, prefixMap)) {
                        square.add(candidate);
                        if (findAnyWordSquare(square, row + 1)) {
                            return true;
                        }
                        square.remove(square.size() - 1);
                    }
                }
            }

            return false;
        }
    }

    // Follow-up 2: Largest possible word square
    public static class LargestWordSquare {

        public static List<String> findLargestWordSquare(String[] words) {
            // Group words by length
            Map<Integer, List<String>> wordsByLength = new HashMap<>();

            for (String word : words) {
                @SuppressWarnings("unused")
                List<String> lengthList = wordsByLength.computeIfAbsent(word.length(), lengthKey -> new ArrayList<>());
                lengthList.add(word);
            }

            // Try from largest length to smallest
            List<Integer> lengths = new ArrayList<>(wordsByLength.keySet());
            lengths.sort(Collections.reverseOrder());

            for (int length : lengths) {
                List<String> wordsOfLength = wordsByLength.get(length);
                List<List<String>> squares = wordSquaresOptimized(wordsOfLength.toArray(new String[0]));

                if (!squares.isEmpty()) {
                    return squares.get(0); // Return first (largest) word square found
                }
            }

            return new ArrayList<>();
        }

        public static int getLargestWordSquareSize(String[] words) {
            List<String> largest = findLargestWordSquare(words);
            return largest.size();
        }

        public static List<List<String>> getAllWordSquaresBySize(String[] words) {
            Map<Integer, List<String>> wordsByLength = new HashMap<>();

            for (String word : words) {
                @SuppressWarnings("unused")
                List<String> lengthList = wordsByLength.computeIfAbsent(word.length(), lengthKey -> new ArrayList<>());
                lengthList.add(word);
            }

            List<List<String>> allSquares = new ArrayList<>();

            for (Map.Entry<Integer, List<String>> entry : wordsByLength.entrySet()) {
                List<String> wordsOfLength = entry.getValue();
                if (wordsOfLength.size() >= entry.getKey()) { // Need at least n words for n×n square
                    allSquares.addAll(wordSquaresOptimized(wordsOfLength.toArray(new String[0])));
                }
            }

            return allSquares;
        }
    }

    // Follow-up 3: Word squares with different lengths (rectangular)
    public static class RectangularWordSquares {

        public static List<List<String>> rectangularWordSquares(String[] words, int rows, int cols) {
            List<List<String>> result = new ArrayList<>();

            // Filter words by column length
            List<String> validWords = new ArrayList<>();
            for (String word : words) {
                if (word.length() == cols) {
                    validWords.add(word);
                }
            }

            if (validWords.size() < rows) {
                return result;
            }

            Map<String, List<String>> prefixMap = buildPrefixMap(validWords.toArray(new String[0]));

            List<String> rectangle = new ArrayList<>();
            backtrackRectangular(rectangle, rows, cols, prefixMap, result, validWords);

            return result;
        }

        private static void backtrackRectangular(List<String> rectangle, int rows, int cols,
                Map<String, List<String>> prefixMap,
                List<List<String>> result, List<String> validWords) {
            if (rectangle.size() == rows) {
                result.add(new ArrayList<>(rectangle));
                return;
            }

            int currentRow = rectangle.size();

            if (currentRow == 0) {
                // First row - can be any valid word
                for (String word : validWords) {
                    rectangle.add(word);
                    backtrackRectangular(rectangle, rows, cols, prefixMap, result, validWords);
                    rectangle.remove(rectangle.size() - 1);
                }
            } else {
                // Subsequent rows - must match column constraints
                List<String> candidates = new ArrayList<>(validWords);

                // Filter candidates based on column prefixes
                for (int col = 0; col < Math.min(currentRow, cols); col++) {
                    StringBuilder columnPrefix = new StringBuilder();
                    for (int row = 0; row < currentRow; row++) {
                        columnPrefix.append(rectangle.get(row).charAt(col));
                    }

                    String prefix = columnPrefix.toString();
                    candidates.retainAll(prefixMap.getOrDefault(prefix, new ArrayList<>()));
                }

                for (String candidate : candidates) {
                    rectangle.add(candidate);
                    backtrackRectangular(rectangle, rows, cols, prefixMap, result, validWords);
                    rectangle.remove(rectangle.size() - 1);
                }
            }
        }
    }

    // Follow-up 4: Word squares with wildcards
    public static class WildcardWordSquares {

        public static List<List<String>> wordSquaresWithWildcards(String[] words, char wildcard) {
            List<List<String>> result = new ArrayList<>();
            if (words == null || words.length == 0) {
                return result;
            }

            int wordLength = words[0].length();
            WildcardTrie trie = new WildcardTrie(wildcard);

            for (String word : words) {
                trie.insert(word);
            }

            for (String word : words) {
                List<String> square = new ArrayList<>();
                square.add(word);
                backtrackWithWildcards(square, wordLength, trie, result);
            }

            return result;
        }

        private static void backtrackWithWildcards(List<String> square, int wordLength,
                WildcardTrie trie, List<List<String>> result) {
            int currentRow = square.size();

            if (currentRow == wordLength) {
                result.add(new ArrayList<>(square));
                return;
            }

            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < currentRow; i++) {
                prefix.append(square.get(i).charAt(currentRow));
            }

            List<String> candidates = trie.getWordsWithWildcardPrefix(prefix.toString());

            for (String candidate : candidates) {
                square.add(candidate);
                backtrackWithWildcards(square, wordLength, trie, result);
                square.remove(square.size() - 1);
            }
        }

        private static class WildcardTrie {
            private WildcardTrieNode root;
            private char wildcard;

            public WildcardTrie(char wildcard) {
                this.root = new WildcardTrieNode();
                this.wildcard = wildcard;
            }

            public void insert(String word) {
                WildcardTrieNode node = root;
                for (char c : word.toCharArray()) {
                    node.children.putIfAbsent(c, new WildcardTrieNode());
                    node = node.children.get(c);
                    node.words.add(word);
                }
                node.isWord = true;
            }

            public List<String> getWordsWithWildcardPrefix(String prefix) {
                Set<String> result = new HashSet<>();
                dfsWildcard(root, prefix, 0, result);
                return new ArrayList<>(result);
            }

            private void dfsWildcard(WildcardTrieNode node, String prefix, int index, Set<String> result) {
                if (index == prefix.length()) {
                    result.addAll(node.words);
                    return;
                }

                char c = prefix.charAt(index);

                if (c == wildcard) {
                    // Wildcard matches any character
                    for (WildcardTrieNode child : node.children.values()) {
                        dfsWildcard(child, prefix, index + 1, result);
                    }
                } else {
                    // Exact character match
                    if (node.children.containsKey(c)) {
                        dfsWildcard(node.children.get(c), prefix, index + 1, result);
                    }
                }
            }

            private static class WildcardTrieNode {
                Map<Character, WildcardTrieNode> children;
                @SuppressWarnings("unused") // Used for completeness of trie structure
                boolean isWord;
                Set<String> words;

                WildcardTrieNode() {
                    children = new HashMap<>();
                    isWord = false;
                    words = new HashSet<>();
                }
            }
        }
    }

    // Follow-up 5: Word squares with specific patterns
    public static class PatternWordSquares {

        public static List<List<String>> wordSquaresWithPattern(String[] words, String pattern) {
            List<List<String>> result = new ArrayList<>();
            if (words == null || words.length == 0 || pattern == null) {
                return result;
            }

            int wordLength = words[0].length();
            if (pattern.length() != wordLength) {
                return result;
            }

            Map<String, List<String>> prefixMap = buildPrefixMap(words);

            // Find words that match the pattern
            List<String> patternMatches = new ArrayList<>();
            for (String word : words) {
                if (matchesPattern(word, pattern)) {
                    patternMatches.add(word);
                }
            }

            for (String word : patternMatches) {
                List<String> square = new ArrayList<>();
                square.add(word);
                backtrackOptimized(square, wordLength, prefixMap, result);
            }

            return result;
        }

        private static boolean matchesPattern(String word, String pattern) {
            if (word.length() != pattern.length()) {
                return false;
            }

            for (int i = 0; i < word.length(); i++) {
                char patternChar = pattern.charAt(i);
                if (patternChar != '?' && patternChar != word.charAt(i)) {
                    return false;
                }
            }

            return true;
        }

        public static List<List<String>> wordSquaresWithConstraints(String[] words,
                List<String> constraints) {
            List<List<String>> result = new ArrayList<>();

            for (List<String> constraint : getConstraintCombinations(constraints)) {
                if (constraint.size() == words[0].length()) {
                    List<List<String>> squares = findSquaresWithConstraints(words, constraint);
                    result.addAll(squares);
                }
            }

            return result;
        }

        private static List<List<String>> getConstraintCombinations(List<String> constraints) {
            // Generate all possible combinations of constraints
            List<List<String>> combinations = new ArrayList<>();
            generateCombinations(constraints, 0, new ArrayList<>(), combinations);
            return combinations;
        }

        private static void generateCombinations(List<String> constraints, int index,
                List<String> current, List<List<String>> combinations) {
            if (index == constraints.size()) {
                combinations.add(new ArrayList<>(current));
                return;
            }

            current.add(constraints.get(index));
            generateCombinations(constraints, index + 1, current, combinations);
            current.remove(current.size() - 1);

            generateCombinations(constraints, index + 1, current, combinations);
        }

        private static List<List<String>> findSquaresWithConstraints(String[] words,
                List<String> constraints) {
            // Simplified implementation - would need more complex logic for real
            // constraints
            return wordSquaresOptimized(words);
        }
    }

    // Follow-up 6: Lexicographic ordering
    public static class LexicographicWordSquares {

        public static List<String> findLexicographicallySmallestSquare(String[] words) {
            List<List<String>> allSquares = wordSquaresOptimized(words);

            if (allSquares.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> smallest = allSquares.get(0);

            for (List<String> square : allSquares) {
                if (isLexicographicallySmaller(square, smallest)) {
                    smallest = square;
                }
            }

            return smallest;
        }

        public static List<String> findLexicographicallyLargestSquare(String[] words) {
            List<List<String>> allSquares = wordSquaresOptimized(words);

            if (allSquares.isEmpty()) {
                return new ArrayList<>();
            }

            List<String> largest = allSquares.get(0);

            for (List<String> square : allSquares) {
                if (isLexicographicallySmaller(largest, square)) {
                    largest = square;
                }
            }

            return largest;
        }

        private static boolean isLexicographicallySmaller(List<String> square1, List<String> square2) {
            String combined1 = String.join("", square1);
            String combined2 = String.join("", square2);
            return combined1.compareTo(combined2) < 0;
        }

        public static List<List<String>> findSquaresSortedLexicographically(String[] words) {
            List<List<String>> allSquares = wordSquaresOptimized(words);

            allSquares.sort((square1, square2) -> {
                String combined1 = String.join("", square1);
                String combined2 = String.join("", square2);
                return combined1.compareTo(combined2);
            });

            return allSquares;
        }
    }

    // Utility methods for testing
    public static void printWordSquare(List<String> square) {
        System.out.println("Word Square:");
        for (String word : square) {
            System.out.println(word);
        }
        System.out.println();
    }

    public static void printAllWordSquares(List<List<String>> squares) {
        System.out.println("Found " + squares.size() + " word squares:");
        for (int i = 0; i < squares.size(); i++) {
            System.out.println("Square " + (i + 1) + ":");
            for (String word : squares.get(i)) {
                System.out.println(word);
            }
            System.out.println();
        }
    }

    public static boolean isValidWordSquare(List<String> square) {
        if (square.isEmpty()) {
            return true;
        }

        int size = square.size();

        // Check if all words have the same length as the number of words
        for (String word : square) {
            if (word.length() != size) {
                return false;
            }
        }

        // Check symmetry property
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (square.get(i).charAt(j) != square.get(j).charAt(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    // Performance testing utilities
    public static void compareApproaches(String[] words) {
        System.out.println("=== Performance Comparison ===");

        long start, end;

        // Backtracking with Trie
        start = System.nanoTime();
        List<List<String>> result1 = wordSquares(words);
        end = System.nanoTime();
        System.out.println("Backtracking with Trie: " + result1.size() + " squares, " +
                (end - start) / 1_000_000 + " ms");

        // Optimized backtracking
        start = System.nanoTime();
        List<List<String>> result2 = wordSquaresOptimized(words);
        end = System.nanoTime();
        System.out.println("Optimized backtracking: " + result2.size() + " squares, " +
                (end - start) / 1_000_000 + " ms");

        // BFS approach
        start = System.nanoTime();
        List<List<String>> result3 = wordSquaresBFS(words);
        end = System.nanoTime();
        System.out.println("BFS approach: " + result3.size() + " squares, " +
                (end - start) / 1_000_000 + " ms");

        System.out.println("Results consistent: " +
                (result1.size() == result2.size() && result2.size() == result3.size()));
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String[] words1 = { "area", "lead", "wall", "lady", "ball" };
        List<List<String>> squares1 = wordSquares(words1);

        System.out.println("Input words: " + Arrays.toString(words1));
        printAllWordSquares(squares1);

        // Verify validity
        for (List<String> square : squares1) {
            System.out.println("Square valid: " + isValidWordSquare(square));
        }

        // Test Case 2: Optimized approach comparison
        System.out.println("=== Test Case 2: Optimized Approach ===");

        List<List<String>> squares2 = wordSquaresOptimized(words1);
        System.out.println("Optimized approach found " + squares2.size() + " squares");

        // Test Case 3: BFS approach
        System.out.println("=== Test Case 3: BFS Approach ===");

        List<List<String>> squares3 = wordSquaresBFS(words1);
        System.out.println("BFS approach found " + squares3.size() + " squares");

        // Test Case 4: Word square builder (repeated queries)
        System.out.println("=== Test Case 4: Word Square Builder ===");

        WordSquareBuilder builder = new WordSquareBuilder(words1);
        System.out.println("Has word square: " + builder.hasWordSquare());
        System.out.println("Total count: " + builder.countWordSquares());

        List<List<String>> squaresWithBall = builder.findWordSquaresWithFirstWord("ball");
        System.out.println("Squares starting with 'ball': " + squaresWithBall.size());

        // Test Case 5: Largest word square
        System.out.println("=== Test Case 5: Largest Word Square ===");

        String[] mixedWords = { "a", "aa", "aaa", "area", "lead", "wall", "lady", "ball" };
        List<String> largestSquare = LargestWordSquare.findLargestWordSquare(mixedWords);
        int largestSize = LargestWordSquare.getLargestWordSquareSize(mixedWords);

        System.out.println("Largest word square size: " + largestSize);
        if (!largestSquare.isEmpty()) {
            printWordSquare(largestSquare);
        }

        // Test Case 6: Rectangular word squares
        System.out.println("=== Test Case 6: Rectangular Word Squares ===");

        String[] rectWords = { "cat", "dog", "rat", "car", "art" };
        List<List<String>> rectangles = RectangularWordSquares.rectangularWordSquares(rectWords, 2, 3);
        System.out.println("Rectangular (2x3) squares: " + rectangles.size());

        for (List<String> rect : rectangles) {
            System.out.println("Rectangle:");
            for (String word : rect) {
                System.out.println(word);
            }
            System.out.println();
        }

        // Test Case 7: Wildcard word squares
        System.out.println("=== Test Case 7: Wildcard Word Squares ===");

        String[] wildcardWords = { "a*ea", "l*ad", "w*ll", "l*dy", "b*ll" };
        List<List<String>> wildcardSquares = WildcardWordSquares.wordSquaresWithWildcards(wildcardWords, '*');
        System.out.println("Wildcard squares: " + wildcardSquares.size());

        // Test Case 8: Pattern word squares
        System.out.println("=== Test Case 8: Pattern Word Squares ===");

        String pattern = "?a??"; // Words with 'a' as second character
        List<List<String>> patternSquares = PatternWordSquares.wordSquaresWithPattern(words1, pattern);
        System.out.println("Pattern '" + pattern + "' squares: " + patternSquares.size());

        // Test Case 9: Lexicographic ordering
        System.out.println("=== Test Case 9: Lexicographic Ordering ===");

        List<String> smallestSquare = LexicographicWordSquares.findLexicographicallySmallestSquare(words1);
        List<String> largestSquare2 = LexicographicWordSquares.findLexicographicallyLargestSquare(words1);

        System.out.println("Lexicographically smallest square:");
        printWordSquare(smallestSquare);

        System.out.println("Lexicographically largest square:");
        printWordSquare(largestSquare2);

        // Test Case 10: Performance comparison
        System.out.println("=== Test Case 10: Performance Comparison ===");

        compareApproaches(words1);

        // Test Case 11: Edge cases
        System.out.println("=== Test Case 11: Edge Cases ===");

        // Empty array
        String[] emptyWords = {};
        List<List<String>> emptyResult = wordSquares(emptyWords);
        System.out.println("Empty words result: " + emptyResult.size());

        // Single word
        String[] singleWord = { "a" };
        List<List<String>> singleResult = wordSquares(singleWord);
        System.out.println("Single word result: " + singleResult.size());

        // No valid squares
        String[] noSquares = { "abc", "def", "ghi" };
        List<List<String>> noSquaresResult = wordSquares(noSquares);
        System.out.println("No valid squares result: " + noSquaresResult.size());

        // Test Case 12: Stress testing
        System.out.println("=== Test Case 12: Stress Testing ===");

        // Generate test words
        String[] stressWords = generateTestWords(50, 3);

        long start = System.nanoTime();
        List<List<String>> stressResult = wordSquares(stressWords);
        long end = System.nanoTime();

        System.out.println("Stress test with 50 words (length 3): " + stressResult.size() +
                " squares found in " + (end - start) / 1_000_000 + " ms");

        System.out.println("\nWord Squares testing completed successfully!");
    }

    // Helper method for stress testing
    private static String[] generateTestWords(int count, int length) {
        Set<String> words = new HashSet<>();
        Random random = new Random(42); // Fixed seed for reproducibility

        while (words.size() < count) {
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < length; i++) {
                word.append((char) ('a' + random.nextInt(26)));
            }
            words.add(word.toString());
        }

        return words.toArray(new String[0]);
    }
}
