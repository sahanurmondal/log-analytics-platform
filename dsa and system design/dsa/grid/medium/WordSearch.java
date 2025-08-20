package grid.medium;

/**
 * LeetCode 79: Word Search
 * https://leetcode.com/problems/word-search/
 *
 * Description:
 * Given an m x n grid of characters board and a string word, return true if
 * word exists in the grid.
 * The word can be constructed from letters of sequentially adjacent cells,
 * where adjacent cells are
 * horizontally or vertically neighboring. The same letter cell may not be used
 * more than once.
 *
 * Constraints:
 * - m == board.length
 * - n = board[i].length
 * - 1 <= m, n <= 6
 * - 1 <= word.length <= 15
 * - board and word consists of only lowercase and uppercase English letters
 */
public class WordSearch {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public boolean exist(char[][] board, String word) {
        if (board == null || board.length == 0 || word == null || word.length() == 0) {
            return false;
        }

        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == word.charAt(0) && dfs(board, word, i, j, 0, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfs(char[][] board, String word, int i, int j, int index, boolean[][] visited) {
        if (index == word.length())
            return true;

        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length ||
                visited[i][j] || board[i][j] != word.charAt(index)) {
            return false;
        }

        visited[i][j] = true;

        for (int[] dir : directions) {
            int ni = i + dir[0];
            int nj = j + dir[1];
            if (dfs(board, word, ni, nj, index + 1, visited)) {
                return true;
            }
        }

        visited[i][j] = false;
        return false;
    }

    public static void main(String[] args) {
        WordSearch solution = new WordSearch();

        char[][] board = {
                { 'A', 'B', 'C', 'E' },
                { 'S', 'F', 'C', 'S' },
                { 'A', 'D', 'E', 'E' }
        };

        System.out.println(solution.exist(board, "ABCCED")); // true
        System.out.println(solution.exist(board, "SEE")); // true
        System.out.println(solution.exist(board, "ABCB")); // false
    }
}
