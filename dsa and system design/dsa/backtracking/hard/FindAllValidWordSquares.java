package backtracking.hard;

import java.util.*;

/**
 * LeetCode 425: Word Squares
 * https://leetcode.com/problems/word-squares/
 * 
 * Company Tags: Google, Airbnb
 * Difficulty: Hard
 * 
 * Given an array of unique strings words, return all the word squares you can
 * build from words.
 * The same word from words can be used multiple times. You can return the
 * answer in any order.
 * 
 * A sequence of strings forms a valid word square if the kth row and column
 * read the same string,
 * where 0 <= k < max(numRows, numColumns).
 * 
 * Example 1:
 * Input: words = ["area","lead","wall","lady","ball"]
 * Output: [["wall","area","lead","lady"],["ball","area","lead","lady"]]
 * 
 * Constraints:
 * - 1 <= words.length <= 1000
 * - 1 <= words[i].length <= 5
 * - All words[i] have the same length.
 * - words[i] consists of only lowercase English letters.
 * - All words[i] are unique.
 */
public class FindAllValidWordSquares {

    /**
     * Approach 1: Backtracking with Trie
     * Time: O(N * 26^L) where N is number of words, L is word length
     * Space: O(N * L) for trie + O(L^2) for recursion
     */
    public List<List<String>> wordSquares(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0)
            return result;

        int len = words[0].length();
        Trie trie = new Trie();

        // Build trie
        for (String word : words) {
            trie.insert(word);
        }

        List<String> square = new ArrayList<>();
        backtrack(result, square, 0, len, trie);
        return result;
    }

    private void backtrack(List<List<String>> result, List<String> square,
            int row, int len, Trie trie) {
        if (row == len) {
            result.add(new ArrayList<>(square));
            return;
        }

        // Get prefix for current row based on previous rows
        StringBuilder prefix = new StringBuilder();
        for (int col = 0; col < len; col++) {
            if (row < square.size() && col < square.get(row).length()) {
                continue; // Skip if we already have this position
            }
            if (col < square.size()) {
                prefix.append(square.get(col).charAt(row));
            }
        }

        // Find all words with the required prefix
        List<String> candidates = trie.getWordsWithPrefix(getPrefix(square, row));

        for (String candidate : candidates) {
            if (isValidPlacement(square, candidate, row)) {
                square.add(candidate);
                backtrack(result, square, row + 1, len, trie);
                square.remove(square.size() - 1);
            }
        }
    }

    private String getPrefix(List<String> square, int row) {
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < square.size(); i++) {
            if (row < square.get(i).length()) {
                prefix.append(square.get(i).charAt(row));
            }
        }
        return prefix.toString();
    }

    private boolean isValidPlacement(List<String> square, String word, int row) {
        for (int col = 0; col < word.length(); col++) {
            if (col < square.size()) {
                if (row < square.get(col).length() &&
                        square.get(col).charAt(row) != word.charAt(col)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Approach 2: Backtracking with HashMap (Optimized)
     * Time: O(N * 26^L)
     * Space: O(N * L)
     */
    public List<List<String>> wordSquaresOptimized(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0)
            return result;

        int len = words[0].length();
        Map<String, List<String>> prefixMap = new HashMap<>();

        // Build prefix map
        for (String word : words) {
            for (int i = 0; i <= word.length(); i++) {
                String prefix = word.substring(0, i);
                prefixMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(word);
            }
        }

        List<String> square = new ArrayList<>();
        backtrackOptimized(result, square, 0, len, prefixMap);
        return result;
    }

    private void backtrackOptimized(List<List<String>> result, List<String> square,
            int row, int len, Map<String, List<String>> prefixMap) {
        if (row == len) {
            result.add(new ArrayList<>(square));
            return;
        }

        String prefix = getPrefix(square, row);
        if (!prefixMap.containsKey(prefix))
            return;

        for (String word : prefixMap.get(prefix)) {
            square.add(word);
            backtrackOptimized(result, square, row + 1, len, prefixMap);
            square.remove(square.size() - 1);
        }
    }

    /**
     * Approach 3: Iterative with Queue
     * Time: O(N * 26^L)
     * Space: O(N * L)
     */
    public List<List<String>> wordSquaresIterative(String[] words) {
        List<List<String>> result = new ArrayList<>();
        if (words == null || words.length == 0)
            return result;

        int len = words[0].length();
        Map<String, List<String>> prefixMap = buildPrefixMap(words);

        Queue<List<String>> queue = new LinkedList<>();
        for (String word : words) {
            List<String> initial = new ArrayList<>();
            initial.add(word);
            queue.offer(initial);
        }

        while (!queue.isEmpty()) {
            List<String> current = queue.poll();

            if (current.size() == len) {
                result.add(new ArrayList<>(current));
                continue;
            }

            String prefix = getPrefix(current, current.size());
            if (prefixMap.containsKey(prefix)) {
                for (String word : prefixMap.get(prefix)) {
                    List<String> next = new ArrayList<>(current);
                    next.add(word);
                    queue.offer(next);
                }
            }
        }

        return result;
    }

    /**
     * Approach 4: Dynamic Programming with Memoization
     * Time: O(N * 26^L)
     * Space: O(N * L + memoization)
     */
    public List<List<String>> wordSquaresMemo(String[] words) {
        Map<String, List<List<String>>> memo = new HashMap<>();
        Map<String, List<String>> prefixMap = buildPrefixMap(words);

        return wordSquaresMemoHelper("", words[0].length(), prefixMap, memo);
    }

    private List<List<String>> wordSquaresMemoHelper(String state, int len,
            Map<String, List<String>> prefixMap,
            Map<String, List<List<String>>> memo) {
        if (memo.containsKey(state)) {
            return memo.get(state);
        }

        List<List<String>> result = new ArrayList<>();
        String[] stateWords = state.split(",");

        if (stateWords.length == len && !stateWords[0].isEmpty()) {
            result.add(Arrays.asList(stateWords));
            memo.put(state, result);
            return result;
        }

        int row = stateWords[0].isEmpty() ? 0 : stateWords.length;
        String prefix = getPrefix(Arrays.asList(stateWords), row);

        if (prefixMap.containsKey(prefix)) {
            for (String word : prefixMap.get(prefix)) {
                String newState = state.isEmpty() ? word : state + "," + word;
                List<List<String>> subResults = wordSquaresMemoHelper(newState, len, prefixMap, memo);
                result.addAll(subResults);
            }
        }

        memo.put(state, result);
        return result;
    }

    private Map<String, List<String>> buildPrefixMap(String[] words) {
        Map<String, List<String>> prefixMap = new HashMap<>();
        for (String word : words) {
            for (int i = 0; i <= word.length(); i++) {
                String prefix = word.substring(0, i);
                prefixMap.computeIfAbsent(prefix, k -> new ArrayList<>()).add(word);
            }
        }
        return prefixMap;
    }

    static class Trie {
        TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        public void insert(String word) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                if (node.children[c - 'a'] == null) {
                    node.children[c - 'a'] = new TrieNode();
                }
                node = node.children[c - 'a'];
                node.words.add(word);
            }
        }

        public List<String> getWordsWithPrefix(String prefix) {
            TrieNode node = root;
            for (char c : prefix.toCharArray()) {
                if (node.children[c - 'a'] == null) {
                    return new ArrayList<>();
                }
                node = node.children[c - 'a'];
            }
            return node.words;
        }
    }

    static class TrieNode {
        TrieNode[] children;
        List<String> words;

        public TrieNode() {
            children = new TrieNode[26];
            words = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        FindAllValidWordSquares solution = new FindAllValidWordSquares();

        // Test Case 1: Basic example
        System.out.println("Test Case 1: Basic example");
        String[] words1 = { "area", "lead", "wall", "lady", "ball" };
        List<List<String>> result1 = solution.wordSquares(words1);
        System.out.println("Result: " + result1);
        System.out.println("Expected: [[wall,area,lead,lady],[ball,area,lead,lady]]");

        // Test Case 2: Empty words
        System.out.println("\nTest Case 2: Empty words");
        System.out.println(solution.wordSquares(new String[] {})); // []

        // Test Case 3: Words with no squares
        System.out.println("\nTest Case 3: No valid squares");
        String[] words3 = { "abc", "def", "ghi" };
        System.out.println(solution.wordSquares(words3)); // []

        // Test Case 4: Single character words
        System.out.println("\nTest Case 4: Single character");
        String[] words4 = { "a", "b" };
        List<List<String>> result4 = solution.wordSquares(words4);
        System.out.println("Result: " + result4);

        // Test Case 5: All same words
        System.out.println("\nTest Case 5: All same words");
        String[] words5 = { "aaa", "aaa", "aaa" };
        List<List<String>> result5 = solution.wordSquares(words5);
        System.out.println("Result count: " + result5.size());

        // Test Case 6: Compare approaches
        System.out.println("\nTest Case 6: Compare approaches");
        long start = System.nanoTime();
        List<List<String>> opt = solution.wordSquaresOptimized(words1);
        long optTime = System.nanoTime() - start;

        start = System.nanoTime();
        List<List<String>> iter = solution.wordSquaresIterative(words1);
        long iterTime = System.nanoTime() - start;

        System.out.println("Optimized time: " + optTime / 1_000_000.0 + " ms");
        System.out.println("Iterative time: " + iterTime / 1_000_000.0 + " ms");
        System.out.println("Results match: " + (opt.size() == iter.size()));

        // Test Case 7: Complex example
        System.out.println("\nTest Case 7: Complex example");
        String[] words7 = { "abat", "baba", "atan", "atal" };
        List<List<String>> result7 = solution.wordSquares(words7);
        System.out.println("Result: " + result7);

        // Test Case 8: Large input
        System.out.println("\nTest Case 8: Performance test");
        String[] words8 = { "area", "lead", "wall", "lady", "ball", "mars", "area", "read", "seal" };
        start = System.nanoTime();
        List<List<String>> result8 = solution.wordSquares(words8);
        long end = System.nanoTime();
        System.out.println("Large input count: " + result8.size());
        System.out.println("Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test Cases 9-15: Additional edge cases and validation
        System.out.println("\nTest Case 9: Palindromic words");
        String[] words9 = { "abba", "baba", "abab", "baab" };
        List<List<String>> result9 = solution.wordSquares(words9);
        System.out.println("Palindromic result count: " + result9.size());

        System.out.println("\nTest Case 10: Memory test");
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        solution.wordSquaresMemo(words1);
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used: " + (afterMemory - beforeMemory) / 1024.0 + " KB");

        // Validation tests
        for (int i = 11; i <= 15; i++) {
            System.out.println("Test Case " + i + ": Validation test " + (i - 10));
            // Various edge cases and stress tests
            if (i == 11) {
                // Test empty result validation
                String[] empty = { "xyz", "abc" };
                System.out.println("Empty result: " + solution.wordSquares(empty).isEmpty());
            } else if (i == 12) {
                // Test result correctness
                boolean valid = validateSquares(result1);
                System.out.println("Valid squares: " + valid);
            } else if (i == 13) {
                // Test uniqueness
                Set<List<String>> unique = new HashSet<>(result1);
                System.out.println("All unique: " + (unique.size() == result1.size()));
            } else if (i == 14) {
                // Test symmetry
                boolean symmetric = checkSymmetry(result1);
                System.out.println("Squares are symmetric: " + symmetric);
            } else {
                // Stress test
                String[] stress = generateTestWords(100);
                start = System.nanoTime();
                List<List<String>> stressResult = solution.wordSquares(stress);
                end = System.nanoTime();
                System.out.println("Stress test completed in: " + (end - start) / 1_000_000.0 + " ms");
            }
        }
    }

    private static boolean validateSquares(List<List<String>> squares) {
        for (List<String> square : squares) {
            if (!isValidSquare(square))
                return false;
        }
        return true;
    }

    private static boolean isValidSquare(List<String> square) {
        int n = square.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (square.get(i).charAt(j) != square.get(j).charAt(i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean checkSymmetry(List<List<String>> squares) {
        return squares.stream().allMatch(FindAllValidWordSquares::isValidSquare);
    }

    private static String[] generateTestWords(int count) {
        String[] words = new String[Math.min(count, 26)];
        for (int i = 0; i < words.length; i++) {
            words[i] = String.valueOf((char) ('a' + i)).repeat(4);
        }
        return words;
    }
}
