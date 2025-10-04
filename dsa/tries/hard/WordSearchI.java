package tries.hard;

/**
 * LeetCode 79: Word Search
 * https://leetcode.com/problems/word-search/
 *
 * Description:
 * Given an m x n grid of characters board and a string word, return true if
 * word exists in the grid.
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
 * - Can you optimize using trie for multiple word searches?
 * - Can you extend to find all possible paths?
 * - Can you handle cyclic paths?
 */
public class WordSearchI {
    public boolean exist(char[][] board, String word) {
        int m = board.length, n = board[0].length;
        boolean[][] visited = new boolean[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (dfs(board, word, i, j, 0, visited))
                    return true;
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int i, int j, int idx, boolean[][] visited) {
        if (idx == word.length())
            return true;
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || visited[i][j]
                || board[i][j] != word.charAt(idx))
            return false;
        visited[i][j] = true;
        boolean found = dfs(board, word, i + 1, j, idx + 1, visited) ||
                dfs(board, word, i - 1, j, idx + 1, visited) ||
                dfs(board, word, i, j + 1, idx + 1, visited) ||
                dfs(board, word, i, j - 1, idx + 1, visited);
        visited[i][j] = false;
        return found;
    }

    public static void main(String[] args) {
        WordSearchI solution = new WordSearchI();

        char[][] board1 = {
                { 'A', 'B', 'C', 'E' },
                { 'S', 'F', 'C', 'S' },
                { 'A', 'D', 'E', 'E' }
        };
        System.out.println(solution.exist(board1, "ABCCED")); // true
        System.out.println(solution.exist(board1, "SEE")); // true
        System.out.println(solution.exist(board1, "ABCB")); // false

        // Edge Case: Single cell
        char[][] board2 = { { 'A' } };
        System.out.println(solution.exist(board2, "A")); // true
        System.out.println(solution.exist(board2, "B")); // false

        // Edge Case: Word longer than possible
        char[][] board3 = { { 'A', 'B' }, { 'C', 'D' } };
        System.out.println(solution.exist(board3, "ABCDEF")); // false

        // Edge Case: Repeated letters
        char[][] board4 = { { 'A', 'A', 'A' }, { 'A', 'A', 'A' } };
        System.out.println(solution.exist(board4, "AAAA")); // true

        // Edge Case: Case sensitivity
        char[][] board5 = { { 'a', 'b' }, { 'c', 'd' } };
        System.out.println(solution.exist(board5, "ABCD")); // false
    }
}
