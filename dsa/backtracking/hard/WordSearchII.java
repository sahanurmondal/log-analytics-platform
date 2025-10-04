package backtracking.hard;

import java.util.*;

/**
 * LeetCode 212: Word Search II
 * https://leetcode.com/problems/word-search-ii/
 *
 * Description: Given an m x n board of characters and a list of strings words,
 * return all words on the board.
 * Each word must be constructed from letters of sequentially adjacent cells.
 * 
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 12
 * - 1 <= words.length <= 3 * 10^4
 *
 * Follow-up:
 * - Can you optimize using Trie?
 * 
 * Time Complexity: O(M * N * 4^L * W) where L is max word length, W is number
 * of words
 * Space Complexity: O(W * L)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
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

        char[][] board1 = { { 'o', 'a', 'a', 'n' }, { 'e', 't', 'a', 'e' }, { 'i', 'h', 'k', 'r' },
                { 'i', 'f', 'l', 'v' } };
        String[] words1 = { "oath", "pea", "eat", "rain" };
        System.out.println(solution.findWords(board1, words1)); // Expected: ["eat","oath"]

        char[][] board2 = { { 'a', 'b' }, { 'c', 'd' } };
        String[] words2 = { "abcb" };
        System.out.println(solution.findWords(board2, words2)); // Expected: []
    }
}
