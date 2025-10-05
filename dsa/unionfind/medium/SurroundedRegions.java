package unionfind.medium;

import unionfind.UnionFind2D;

/**
 * LeetCode 130: Surrounded Regions
 * https://leetcode.com/problems/surrounded-regions/
 *
 * Description:
 * Given an m x n matrix board containing 'X' and 'O', capture all regions that
 * are 4-directionally
 * surrounded by 'X'. A region is captured by flipping all 'O's into 'X's in
 * that surrounded region.
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 200
 * - board[i][j] is 'X' or 'O'.
 *
 * Visual Example:
 * Input:
 * X X X X
 * X O O X
 * X X O X
 * X O X X
 * 
 * Output:
 * X X X X
 * X X X X
 * X X X X
 * X O X X
 * 
 * The 'O' at (3,1) is not surrounded, so it remains 'O'
 *
 * ============================================================================
 * WHY BORDER CELLS NEED SPECIAL TREATMENT:
 * ============================================================================
 *
 * SURROUNDED REGIONS (This Problem):
 * - Border 'O' cells can NEVER be surrounded (they touch the edge)
 * - Any 'O' connected to a border 'O' is also NOT surrounded
 * - We must KEEP these 'O's and only flip the truly surrounded ones
 * - Solution: Connect all border 'O's to a virtual "border node"
 *   → Any 'O' connected to this node = NOT surrounded = keep as 'O'
 *   → Any 'O' NOT connected to this node = surrounded = flip to 'X'
 *
 * NUMBER OF ISLANDS (Different Problem):
 * - ALL islands are counted equally regardless of border position
 * - No special treatment needed - just count all connected components
 * - Border position doesn't affect whether we count an island or not
 *
 * Example showing the difference:
 *
 * Grid:     Number of Islands:     Surrounded Regions:
 * O O X     2 islands              O O X  (keep border O's)
 * X O X     (top and middle)       X O X  (not flip middle O)
 * X X X                            X X X
 *
 * ============================================================================
 *
 * Follow-up:
 * - Can you solve it using DFS/BFS as well?
 * - How would you handle very large boards?
 */
public class SurroundedRegions {

    // Union-Find solution using UnionFind2D
    public void solve(char[][] board) {
        if (board == null || board.length == 0) return;

        int m = board.length;
        int n = board[0].length;
        UnionFind2D uf = new UnionFind2D(m + 1, n + 1); // +1 row for virtual border

        // Use last row as virtual border node
        // This represents "connected to the outside world"
        int borderRow = m;
        int borderCol = 0;

        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        // Activate the virtual border node
        // Any 'O' connected to this node cannot be surrounded
        uf.activate(borderRow, borderCol);

        // Process all 'O' cells
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    uf.activate(i, j);

                    // CRITICAL: Connect border 'O's to the virtual border node
                    // This is what makes border 'O's special!
                    // Border 'O's can never be surrounded because they touch the edge
                    if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                        uf.union(i, j, borderRow, borderCol);
                    }

                    // Connect adjacent 'O's within the grid
                    // This propagates the "connected to border" property
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];

                        if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                                board[newRow][newCol] == 'O') {
                            uf.union(i, j, newRow, newCol);
                        }
                    }
                }
            }
        }

        // Flip 'O's that are NOT connected to border
        // These are the truly surrounded regions
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O' && !uf.connected(i, j, borderRow, borderCol)) {
                    board[i][j] = 'X';  // Flip surrounded 'O' to 'X'
                }
                // Note: 'O's connected to borderNode remain as 'O'
            }
        }
    }

    // DFS solution for comparison
    // This approach marks border-connected 'O's as safe, then flips the rest
    public void solveDFS(char[][] board) {
        if (board == null || board.length == 0) return;

        int m = board.length;
        int n = board[0].length;

        // CRITICAL: Start DFS from all border 'O's
        // Mark them and all connected 'O's as 'S' (safe - cannot be surrounded)
        for (int i = 0; i < m; i++) {
            if (board[i][0] == 'O') dfs(board, i, 0);           // Left border
            if (board[i][n - 1] == 'O') dfs(board, i, n - 1);   // Right border
        }
        for (int j = 0; j < n; j++) {
            if (board[0][j] == 'O') dfs(board, 0, j);           // Top border
            if (board[m - 1][j] == 'O') dfs(board, m - 1, j);   // Bottom border
        }

        // Now process all cells:
        // - Remaining 'O's = surrounded = flip to 'X'
        // - 'S' cells = safe (connected to border) = restore to 'O'
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    board[i][j] = 'X';  // Flip surrounded
                } else if (board[i][j] == 'S') {
                    board[i][j] = 'O';  // Restore border-connected
                }
            }
        }
    }

    private void dfs(char[][] board, int i, int j) {
        int m = board.length, n = board[0].length;
        if (i < 0 || i >= m || j < 0 || j >= n || board[i][j] != 'O') return;

        board[i][j] = 'S'; // Mark as safe (connected to border)

        // Recursively mark all connected 'O's as safe
        dfs(board, i + 1, j);
        dfs(board, i - 1, j);
        dfs(board, i, j + 1);
        dfs(board, i, j - 1);
    }

    public static void main(String[] args) {
        SurroundedRegions solution = new SurroundedRegions();

        // Test case 1: Basic example (UF)
        char[][] board1 = {
                { 'X', 'X', 'X', 'X' },
                { 'X', 'O', 'O', 'X' },
                { 'X', 'X', 'O', 'X' },
                { 'X', 'O', 'X', 'X' }
        };
        solution.solve(board1);
        System.out.println("Test 1 (UF):");
        printBoard(board1);

        // Test case 1: Basic example (DFS)
        char[][] board1DFS = {
                { 'X', 'X', 'X', 'X' },
                { 'X', 'O', 'O', 'X' },
                { 'X', 'X', 'O', 'X' },
                { 'X', 'O', 'X', 'X' }
        };
        solution.solveDFS(board1DFS);
        System.out.println("Test 1 (DFS):");
        printBoard(board1DFS);

        // Test case 2: All X (UF)
        char[][] board2 = {
                { 'X', 'X' },
                { 'X', 'X' }
        };
        solution.solve(board2);
        System.out.println("Test 2 (UF):");
        printBoard(board2);

        // Test case 3: All O (UF) - All on border, none should be flipped
        char[][] board3 = {
                { 'O', 'O' },
                { 'O', 'O' }
        };
        solution.solve(board3);
        System.out.println("Test 3 (UF - All border, keep all O's):");
        printBoard(board3);

        // Test case 3: All O (DFS)
        char[][] board3DFS = {
                { 'O', 'O' },
                { 'O', 'O' }
        };
        solution.solveDFS(board3DFS);
        System.out.println("Test 3 (DFS - All border, keep all O's):");
        printBoard(board3DFS);

        // Test case 4: Border-connected O's (should NOT flip middle O)
        char[][] board4 = {
                { 'O', 'O', 'X' },
                { 'X', 'O', 'X' },
                { 'X', 'X', 'X' }
        };
        solution.solve(board4);
        System.out.println("Test 4 (UF - Border-connected, keep middle O):");
        printBoard(board4);

        char[][] board4DFS = {
                { 'O', 'O', 'X' },
                { 'X', 'O', 'X' },
                { 'X', 'X', 'X' }
        };
        solution.solveDFS(board4DFS);
        System.out.println("Test 4 (DFS - Border-connected, keep middle O):");
        printBoard(board4DFS);
    }

    private static void printBoard(char[][] board) {
        for (char[] row : board) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
