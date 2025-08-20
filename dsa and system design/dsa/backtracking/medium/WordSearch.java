package backtracking.medium;

/**
 * LeetCode 79: Word Search
 * https://leetcode.com/problems/word-search/
 *
 * Description: Given an m x n grid of characters board and a string word,
 * return true if word exists in the grid.
 * The word can be constructed from letters of sequentially adjacent cells.
 * 
 * Constraints:
 * - m == board.length
 * - n = board[i].length
 * - 1 <= m, n <= 6
 * - 1 <= word.length <= 15
 * - board and word consists of only lowercase and uppercase English letters
 *
 * Follow-up:
 * - What if we need to find all words (Word Search II)?
 * - Can you optimize using Trie?
 * 
 * Time Complexity: O(N * 4^L) where N = cells, L = word length
 * Space Complexity: O(L) for recursion depth
 * 
 * Algorithm:
 * 1. DFS Backtracking: Try each cell as starting point
 * 2. Mark visited cells and backtrack
 * 3. Trie optimization for multiple words
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class WordSearch {

    // Main optimized solution - DFS Backtracking
    public boolean exist(char[][] board, String word) {
        if (board == null || board.length == 0 || word == null)
            return false;

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (dfs(board, word, i, j, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int i, int j, int index) {
        if (index == word.length())
            return true;

        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length ||
                board[i][j] != word.charAt(index)) {
            return false;
        }

        char temp = board[i][j];
        board[i][j] = '#'; // Mark as visited

        boolean found = dfs(board, word, i + 1, j, index + 1) ||
                dfs(board, word, i - 1, j, index + 1) ||
                dfs(board, word, i, j + 1, index + 1) ||
                dfs(board, word, i, j - 1, index + 1);

        board[i][j] = temp; // Backtrack
        return found;
    }

    // Alternative solution - Using visited array
    public boolean existWithVisited(char[][] board, String word) {
        boolean[][] visited = new boolean[board.length][board[0].length];

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (dfsWithVisited(board, word, i, j, 0, visited)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsWithVisited(char[][] board, String word, int i, int j, int index, boolean[][] visited) {
        if (index == word.length())
            return true;

        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length ||
                visited[i][j] || board[i][j] != word.charAt(index)) {
            return false;
        }

        visited[i][j] = true;

        boolean found = dfsWithVisited(board, word, i + 1, j, index + 1, visited) ||
                dfsWithVisited(board, word, i - 1, j, index + 1, visited) ||
                dfsWithVisited(board, word, i, j + 1, index + 1, visited) ||
                dfsWithVisited(board, word, i, j - 1, index + 1, visited);

        visited[i][j] = false; // Backtrack
        return found;
    }

    // Follow-up optimization - Early termination
    public boolean existOptimized(char[][] board, String word) {
        if (board == null || board.length == 0 || word == null)
            return false;

        // Count characters in board and word
        int[] boardCount = new int[128];
        int[] wordCount = new int[128];

        for (char[] row : board) {
            for (char c : row) {
                boardCount[c]++;
            }
        }

        for (char c : word.toCharArray()) {
            wordCount[c]++;
            if (wordCount[c] > boardCount[c])
                return false;
        }

        return exist(board, word);
    }

    public static void main(String[] args) {
        WordSearch solution = new WordSearch();

        // Test Case 1: Normal case
        char[][] board1 = { { 'A', 'B', 'C', 'E' }, { 'S', 'F', 'C', 'S' }, { 'A', 'D', 'E', 'E' } };
        System.out.println(solution.exist(board1, "ABCCED")); // Expected: true

        // Test Case 2: Word not found
        char[][] board2 = { { 'A', 'B', 'C', 'E' }, { 'S', 'F', 'C', 'S' }, { 'A', 'D', 'E', 'E' } };
        System.out.println(solution.exist(board2, "SEE")); // Expected: true

        // Test Case 3: Word not found
        char[][] board3 = { { 'A', 'B', 'C', 'E' }, { 'S', 'F', 'C', 'S' }, { 'A', 'D', 'E', 'E' } };
        System.out.println(solution.exist(board3, "ABCB")); // Expected: false

        // Test Case 4: Single cell
        char[][] board4 = { { 'A' } };
        System.out.println(solution.exist(board4, "A")); // Expected: true

        // Test Case 5: Single cell - not found
        char[][] board5 = { { 'A' } };
        System.out.println(solution.exist(board5, "B")); // Expected: false

        // Test Case 6: Single character word
        char[][] board6 = { { 'A', 'B' }, { 'C', 'D' } };
        System.out.println(solution.exist(board6, "A")); // Expected: true

        // Test Case 7: Full path
        char[][] board7 = { { 'A', 'B' }, { 'C', 'D' } };
        System.out.println(solution.exist(board7, "ABDC")); // Expected: true

        // Test Case 8: Zigzag pattern
        char[][] board8 = { { 'A', 'B', 'C' }, { 'D', 'E', 'F' }, { 'G', 'H', 'I' } };
        System.out.println(solution.exist(board8, "ABCFIED")); // Expected: true

        // Test Case 9: Test with visited array approach
        char[][] board9 = { { 'A', 'B', 'C', 'E' }, { 'S', 'F', 'C', 'S' }, { 'A', 'D', 'E', 'E' } };
        System.out.println(solution.existWithVisited(board9, "ABCCED")); // Expected: true

        // Test Case 10: Long word
        char[][] board10 = { { 'A', 'A', 'A', 'A', 'A' }, { 'A', 'A', 'A', 'A', 'A' }, { 'A', 'A', 'A', 'A', 'A' } };
        System.out.println(solution.exist(board10, "AAAAAAAAAAAAAAB")); // Expected: false
    }
}
