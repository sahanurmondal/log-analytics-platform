package grid.hard;

import java.util.*;

/**
 * LeetCode 212: Word Search II
 * https://leetcode.com/problems/word-search-ii/
 *
 * Description:
 * Given an m x n board of characters and a list of strings words, return all
 * words on the board.
 * Each word must be constructed from letters of sequentially adjacent cells,
 * where adjacent cells are
 * horizontally or vertically neighboring. The same letter cell may not be used
 * more than once in a word.
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
 */
public class WordSearchII {

    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
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

    public List<String> findWords(char[][] board, String[] words) {
        List<String> result = new ArrayList<>();
        TrieNode root = buildTrie(words);

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                dfs(board, i, j, root, result);
            }
        }

        return result;
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
            node.word = null; // avoid duplicate
        }

        board[i][j] = '#'; // mark as visited

        dfs(board, i + 1, j, node, result);
        dfs(board, i - 1, j, node, result);
        dfs(board, i, j + 1, node, result);
        dfs(board, i, j - 1, node, result);

        board[i][j] = c; // restore
    }

    public static void main(String[] args) {
        WordSearchII solution = new WordSearchII();

        char[][] board = {
                { 'o', 'a', 'a', 'n' },
                { 'e', 't', 'a', 'e' },
                { 'i', 'h', 'k', 'r' },
                { 'i', 'f', 'l', 'v' }
        };
        String[] words = { "oath", "pea", "eat", "rain" };

        System.out.println(solution.findWords(board, words)); // ["eat","oath"]
    }
}
