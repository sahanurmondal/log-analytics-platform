package dp.hard;

import java.util.*;

/**
 * LeetCode 140: Word Break II
 * https://leetcode.com/problems/word-break-ii/
 *
 * Description:
 * Given a string s and a dictionary of strings wordDict, add spaces in s to
 * construct a sentence
 * where each word is a valid dictionary word. Return all such possible
 * sentences in any order.
 * Note that the same word in the dictionary may be reused multiple times in the
 * segmentation.
 *
 * Constraints:
 * - 1 <= s.length <= 20
 * - 1 <= wordDict.length <= 1000
 * - 1 <= wordDict[i].length <= 10
 * - s and wordDict[i] consist of only lowercase English letters.
 * - All the strings of wordDict are unique.
 *
 * Follow-up:
 * - What if the string is very long?
 * - Can you optimize for repeated calls?
 * 
 * Company Tags: Google, Uber, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class WordBreakII {

    // Approach 1: Backtracking with Memoization - O(2^n) time, O(2^n) space
    public List<String> wordBreakMemo(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, List<String>> memo = new HashMap<>();
        return wordBreakHelper(s, wordSet, memo);
    }

    private List<String> wordBreakHelper(String s, Set<String> wordSet, Map<String, List<String>> memo) {
        if (memo.containsKey(s)) {
            return memo.get(s);
        }

        List<String> result = new ArrayList<>();

        if (s.length() == 0) {
            result.add("");
            return result;
        }

        for (int i = 1; i <= s.length(); i++) {
            String prefix = s.substring(0, i);

            if (wordSet.contains(prefix)) {
                List<String> suffixBreaks = wordBreakHelper(s.substring(i), wordSet, memo);

                for (String suffix : suffixBreaks) {
                    if (suffix.isEmpty()) {
                        result.add(prefix);
                    } else {
                        result.add(prefix + " " + suffix);
                    }
                }
            }
        }

        memo.put(s, result);
        return result;
    }

    // Approach 2: DP + Backtracking - O(n^3 + 2^n) time, O(n^2 + 2^n) space
    public List<String> wordBreakDP(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int n = s.length();

        // First check if word break is possible
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;

        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        if (!dp[n])
            return new ArrayList<>();

        // Build all possible sentences
        return buildSentences(s, wordSet, 0);
    }

    private List<String> buildSentences(String s, Set<String> wordSet, int start) {
        List<String> result = new ArrayList<>();

        if (start == s.length()) {
            result.add("");
            return result;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);

            if (wordSet.contains(word)) {
                List<String> suffixSentences = buildSentences(s, wordSet, end);

                for (String suffix : suffixSentences) {
                    if (suffix.isEmpty()) {
                        result.add(word);
                    } else {
                        result.add(word + " " + suffix);
                    }
                }
            }
        }

        return result;
    }

    // Approach 3: Trie + Backtracking - O(2^n) time, O(2^n) space
    public List<String> wordBreakTrie(String s, List<String> wordDict) {
        TrieNode root = buildTrie(wordDict);
        List<String> result = new ArrayList<>();
        backtrackWithTrie(s, 0, root, new ArrayList<>(), result);
        return result;
    }

    private void backtrackWithTrie(String s, int start, TrieNode root, List<String> path, List<String> result) {
        if (start == s.length()) {
            result.add(String.join(" ", path));
            return;
        }

        TrieNode node = root;
        for (int end = start; end < s.length(); end++) {
            char c = s.charAt(end);

            if (!node.children.containsKey(c))
                break;

            node = node.children.get(c);

            if (node.isWord) {
                path.add(s.substring(start, end + 1));
                backtrackWithTrie(s, end + 1, root, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    private TrieNode buildTrie(List<String> words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                node.children.putIfAbsent(c, new TrieNode());
                node = node.children.get(c);
            }
            node.isWord = true;
        }

        return root;
    }

    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
    }

    // Approach 4: BFS with Path Tracking - O(2^n) time, O(2^n) space
    public List<String> wordBreakBFS(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        List<String> result = new ArrayList<>();

        Queue<List<String>> queue = new LinkedList<>();
        queue.offer(new ArrayList<>());

        int level = 0;
        while (!queue.isEmpty() && level < s.length()) {
            int size = queue.size();
            Set<Integer> visited = new HashSet<>();

            for (int i = 0; i < size; i++) {
                List<String> path = queue.poll();

                for (int end = level + 1; end <= s.length(); end++) {
                    String word = s.substring(level, end);

                    if (wordSet.contains(word)) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(word);

                        if (end == s.length()) {
                            result.add(String.join(" ", newPath));
                        } else if (!visited.contains(end)) {
                            queue.offer(newPath);
                            visited.add(end);
                        }
                    }
                }
            }
            level++;
        }

        return result;
    }

    // Approach 5: DFS with Pruning - O(2^n) time, O(2^n) space
    public List<String> wordBreakDFS(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);

        // Precompute if break is possible from each position
        boolean[] canBreak = new boolean[s.length() + 1];
        canBreak[s.length()] = true;

        for (int i = s.length() - 1; i >= 0; i--) {
            for (int j = i + 1; j <= s.length(); j++) {
                if (canBreak[j] && wordSet.contains(s.substring(i, j))) {
                    canBreak[i] = true;
                    break;
                }
            }
        }

        List<String> result = new ArrayList<>();
        if (canBreak[0]) {
            dfsHelper(s, 0, wordSet, canBreak, new ArrayList<>(), result);
        }

        return result;
    }

    private void dfsHelper(String s, int start, Set<String> wordSet, boolean[] canBreak,
            List<String> path, List<String> result) {
        if (start == s.length()) {
            result.add(String.join(" ", path));
            return;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);

            if (wordSet.contains(word) && canBreak[end]) {
                path.add(word);
                dfsHelper(s, end, wordSet, canBreak, path, result);
                path.remove(path.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        WordBreakII solution = new WordBreakII();

        System.out.println("=== Word Break II Test Cases ===");

        // Test Case 1: Basic example
        String s1 = "catsanddog";
        List<String> wordDict1 = Arrays.asList("cat", "cats", "and", "sand", "dog");
        System.out.println("Test 1 - s: \"" + s1 + "\"");
        System.out.println("wordDict: " + wordDict1);
        System.out.println("Memoization: " + solution.wordBreakMemo(s1, wordDict1));
        System.out.println("DP: " + solution.wordBreakDP(s1, wordDict1));
        System.out.println("Trie: " + solution.wordBreakTrie(s1, wordDict1));
        System.out.println("Expected: [\"cats and dog\", \"cat sand dog\"]\n");

        // Test Case 2: Multiple solutions
        String s2 = "pineapplepenapple";
        List<String> wordDict2 = Arrays.asList("apple", "pen", "applepen", "pine", "pineapple");
        System.out.println("Test 2 - s: \"" + s2 + "\"");
        System.out.println("wordDict: " + wordDict2);
        System.out.println("Memoization: " + solution.wordBreakMemo(s2, wordDict2));
        System.out.println("Expected: [\"pine apple pen apple\", \"pineapple pen apple\", \"pine applepen apple\"]\n");

        // Test Case 3: No solution
        String s3 = "catsandog";
        List<String> wordDict3 = Arrays.asList("cats", "dog", "sand", "and", "cat");
        System.out.println("Test 3 - s: \"" + s3 + "\"");
        System.out.println("wordDict: " + wordDict3);
        System.out.println("Memoization: " + solution.wordBreakMemo(s3, wordDict3));
        System.out.println("Expected: []\n");

        performanceTest();
    }

    private static void performanceTest() {
        WordBreakII solution = new WordBreakII();

        String testString = "aaaaaaaaaa";
        List<String> testDict = Arrays.asList("a", "aa", "aaa", "aaaa", "aaaaa");

        System.out.println("=== Performance Test ===");
        System.out.println("String: \"" + testString + "\"");
        System.out.println("Dict size: " + testDict.size());

        long start = System.nanoTime();
        List<String> result1 = solution.wordBreakMemo(testString, testDict);
        long end = System.nanoTime();
        System.out.println(
                "Memoization: " + result1.size() + " solutions - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        List<String> result2 = solution.wordBreakTrie(testString, testDict);
        end = System.nanoTime();
        System.out.println("Trie: " + result2.size() + " solutions - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        List<String> result3 = solution.wordBreakDFS(testString, testDict);
        end = System.nanoTime();
        System.out.println(
                "DFS with Pruning: " + result3.size() + " solutions - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
