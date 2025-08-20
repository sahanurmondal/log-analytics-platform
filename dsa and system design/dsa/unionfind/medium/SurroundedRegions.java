package unionfind.medium;

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
 * Follow-up:
 * - Can you solve it using DFS/BFS as well?
 * - How would you handle very large boards?
 */
public class SurroundedRegions {

    class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
            }
        }

        public boolean connected(int x, int y) {
            return find(x) == find(y);
        }
    }

    public void solve(char[][] board) {
        if (board == null || board.length == 0)
            return;

        int m = board.length;
        int n = board[0].length;
        UnionFind uf = new UnionFind(m * n + 1); // +1 for dummy border node
        int borderNode = m * n;

        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    int id = i * n + j;

                    // Connect border 'O's to border node
                    if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                        uf.union(id, borderNode);
                    }

                    // Connect adjacent 'O's
                    for (int[] dir : directions) {
                        int newRow = i + dir[0];
                        int newCol = j + dir[1];

                        if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                                board[newRow][newCol] == 'O') {
                            int neighborId = newRow * n + newCol;
                            uf.union(id, neighborId);
                        }
                    }
                }
            }
        }

        // Flip 'O's that are not connected to border
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j] == 'O') {
                    int id = i * n + j;
                    if (!uf.connected(id, borderNode)) {
                        board[i][j] = 'X';
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SurroundedRegions solution = new SurroundedRegions();

        // Test case 1: Basic example
        char[][] board1 = {
                { 'X', 'X', 'X', 'X' },
                { 'X', 'O', 'O', 'X' },
                { 'X', 'X', 'O', 'X' },
                { 'X', 'O', 'X', 'X' }
        };
        solution.solve(board1);
        printBoard(board1);

        // Test case 2: All X
        char[][] board2 = {
                { 'X', 'X' },
                { 'X', 'X' }
        };
        solution.solve(board2);
        printBoard(board2);

        // Test case 3: All O
        char[][] board3 = {
                { 'O', 'O' },
                { 'O', 'O' }
        };
        solution.solve(board3);
        printBoard(board3);
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
