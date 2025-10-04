package unionfind.hard;

/**
 * LeetCode 990: Satisfiability of Equality Equations
 * https://leetcode.com/problems/satisfiability-of-equality-equations/
 *
 * Description:
 * You are given an array of strings equations that represent relationships
 * between variables
 * where each string equations[i] is of length 4 and takes one of two different
 * forms:
 * "xi==yi" or "xi!=yi".
 * Here, xi and yi are lowercase letters (not necessarily different) that
 * represent one-letter variable names.
 * Return true if it is possible to assign integers to variable names so as to
 * satisfy all the given equations,
 * or false otherwise.
 *
 * Constraints:
 * - 1 <= equations.length <= 500
 * - equations[i].length == 4
 * - equations[i][0] is a lowercase letter
 * - equations[i][1] is either '=' or '!'
 * - equations[i][2] is '='
 * - equations[i][3] is a lowercase letter
 */
public class SatisfiabilityEquations {

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

    public boolean equationsPossible(String[] equations) {
        UnionFind uf = new UnionFind(26);

        // Process equality equations first
        for (String eq : equations) {
            if (eq.charAt(1) == '=') {
                int x = eq.charAt(0) - 'a';
                int y = eq.charAt(3) - 'a';
                uf.union(x, y);
            }
        }

        // Check inequality equations
        for (String eq : equations) {
            if (eq.charAt(1) == '!') {
                int x = eq.charAt(0) - 'a';
                int y = eq.charAt(3) - 'a';
                if (uf.connected(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SatisfiabilityEquations solution = new SatisfiabilityEquations();

        // Test case 1
        String[] equations1 = { "a==b", "b!=a" };
        System.out.println(solution.equationsPossible(equations1)); // false

        // Test case 2
        String[] equations2 = { "b==a", "a==b" };
        System.out.println(solution.equationsPossible(equations2)); // true

        // Test case 3
        String[] equations3 = { "a==b", "b==c", "a==c" };
        System.out.println(solution.equationsPossible(equations3)); // true
    }
}
