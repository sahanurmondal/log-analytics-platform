package unionfind.medium;

/**
 * LeetCode 547: Number of Provinces
 * https://leetcode.com/problems/number-of-provinces/
 *
 * Description:
 * There are n cities. Some of them are connected, while some are not.
 * If city a is connected directly with city b, and city b is connected directly
 * with city c,
 * then city a is connected indirectly with city c.
 * A province is a group of directly or indirectly connected cities and no other
 * cities outside of the group.
 * You are given an n x n matrix isConnected where isConnected[i][j] = 1 if the
 * ith city and the jth city
 * are directly connected, and isConnected[i][j] = 0 otherwise.
 * Return the total number of provinces.
 *
 * Constraints:
 * - 1 <= n <= 200
 * - n == isConnected.length
 * - n == isConnected[i].length
 * - isConnected[i][j] is 1 or 0
 * - isConnected[i][i] == 1
 * - isConnected[i][j] == isConnected[j][i]
 */
public class NumberOfProvinces {

    class UnionFind {
        private int[] parent;
        private int[] rank;
        private int components;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            components = n;
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
                components--;
            }
        }

        public int getComponents() {
            return components;
        }
    }

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    uf.union(i, j);
                }
            }
        }

        return uf.getComponents();
    }

    public static void main(String[] args) {
        NumberOfProvinces solution = new NumberOfProvinces();

        // Test case 1
        int[][] isConnected1 = { { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 1 } };
        System.out.println(solution.findCircleNum(isConnected1)); // 2

        // Test case 2
        int[][] isConnected2 = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
        System.out.println(solution.findCircleNum(isConnected2)); // 3
    }
}
