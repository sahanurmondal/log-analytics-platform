package arrays.hard;

import java.util.*;

/**
 * LeetCode 212: Word Search II
 * https://leetcode.com/problems/word-search-ii/
 *
 * Description:
 * Given an m x n board of characters and a list of strings words, return all
 * words on the board.
 * Each word must be constructed from letters of sequentially adjacent cells.
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 12
 * - board[i][j] is a lowercase English letter
 * - 1 <= words.length <= 3 * 10^4
 * - 1 <= words[i].length <= 10
 * - words[i] consists of lowercase English letters
 * - All the strings of words are unique
 *
 * Follow-up:
 * - Can you optimize using Trie?
 * 
 * Time Complexity: O(m * n * 4^l) where l is max word length
 * Space Complexity: O(total characters in words)
 */
public class WordSearchII {

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();

        // Build Trie
        TrieNode root = buildTrie(words);

        // DFS from each cell
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                dfs(board, i, j, root, result);
            }
        }

        return result;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();

        for (String word : words) {
            TrieNode node = root;
            for (char c : word.toCharArray()) {
                int index = c - 'a';
                if (node.children[index] == null) {
                    node.children[index] = new TrieNode();
                }
                node = node.children[index];
            }
            node.word = word;
        }

        return root;
    }

    private void dfs(char[][] board, int i, int j, TrieNode node, List<String> result) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length)
            return;

        char c = board[i][j];
        if (c == '#' || node.children[c - 'a'] == null)
            return;

        node = node.children[c - 'a'];
        if (node.word != null) {
            result.add(node.word);
            node.word = null; // Avoid duplicates
        }

        board[i][j] = '#'; // Mark as visited

        // Explore 4 directions
        dfs(board, i - 1, j, node, result);
        dfs(board, i + 1, j, node, result);
        dfs(board, i, j - 1, node, result);
        dfs(board, i, j + 1, node, result);

        board[i][j] = c; // Backtrack
    }

    public static void main(String[] args) {
        WordSearchII solution = new WordSearchII();

        // Test Case 1: Normal case
        char[][] board1 = { { 'o', 'a', 'a', 'n' }, { 'e', 't', 'a', 'e' }, { 'i', 'h', 'k', 'r' },
                { 'i', 'f', 'l', 'v' } };
        String[] words1 = { "oath", "pea", "eat", "rain" };
        System.out.println(solution.findWords(board1, words1)); // Expected: ["eat","oath"]

        // Test Case 2: Edge case - no words found
        char[][] board2 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words2 = { "abcb" };
        System.out.println(solution.findWords(board2, words2)); // Expected: []

        // Test Case 3: Single cell
        char[][] board3 = { { 'a' } };
        String[] words3 = { "a" };
        System.out.println(solution.findWords(board3, words3)); // Expected: ["a"]

        // Test Case 4: All words found
        char[][] board4 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words4 = { "ab", "cd", "ac", "bd" };
        System.out.println(solution.findWords(board4, words4)); // Expected: ["ab","cd"]

        // Test Case 5: Overlapping paths
        char[][] board5 = { { 'a', 'a' } };
        String[] words5 = { "aaa" };
        System.out.println(solution.findWords(board5, words5)); // Expected: []

        // Test Case 6: Complex board
        char[][] board6 = { { 'o', 'a', 'b', 'n' }, { 'o', 't', 'a', 'e' }, { 'a', 'h', 'k', 'r' },
                { 'a', 'f', 'l', 'v' } };
        String[] words6 = { "oa", "oaa" };
        System.out.println(solution.findWords(board6, words6)); // Expected: ["oa","oaa"]

        // Test Case 7: Single character words
        char[][] board7 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words7 = { "a", "b", "c", "d", "e" };
        System.out.println(solution.findWords(board7, words7)); // Expected: ["a","b","c","d"]

        // Test Case 8: Long words
        char[][] board8 = { { 'a', 'b', 'c' }, { 'a', 'e', 'd' }, { 'a', 'f', 'g' } };
        String[] words8 = { "abcdefg", "gfedcba" };
        System.out.println(solution.findWords(board8, words8)); // Expected: ["abcdefg"]

        // Test Case 9: Repeated letters
        char[][] board9 = { { 'a', 'a', 'a' }, { 'a', 'a', 'a' }, { 'a', 'a', 'a' } };
        String[] words9 = { "aaaaaaaaa" };
        System.out.println(solution.findWords(board9, words9)); // Expected: ["aaaaaaaaa"]

        // Test Case 10: No valid paths
        char[][] board10 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words10 = { "ac", "ca", "bb", "dd" };
        System.out.println(solution.findWords(board10, words10)); // Expected: []
    }
}