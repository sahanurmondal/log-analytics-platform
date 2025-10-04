package unionfind.hard;

/**
 * LeetCode 839: Similar String Groups
 * https://leetcode.com/problems/similar-string-groups/
 *
 * Description:
 * Two strings X and Y are similar if we can swap two letters (in different
 * positions) of X, so that it equals Y.
 * Also, two strings X and Y are similar if they are equal.
 * Given a list of strings, group strings into groups of similar strings.
 * Return the number of groups.
 *
 * Constraints:
 * - 1 <= strs.length <= 300
 * - 1 <= strs[i].length <= 300
 * - strs[i] consists of lowercase letters only
 * - All the strings of strs are of the same length
 */
public class SimilarStringGroups {

    class UnionFind {
        private int[] parent;
        private int[] rank;
        private int groups;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            groups = n;
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
                groups--;
            }
        }

        public int getGroups() {
            return groups;
        }
    }

    private boolean isSimilar(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;

        int diffCount = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                diffCount++;
                if (diffCount > 2)
                    return false;
            }
        }

        return diffCount == 0 || diffCount == 2;
    }

    public int numSimilarGroups(String[] strs) {
        int n = strs.length;
        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isSimilar(strs[i], strs[j])) {
                    uf.union(i, j);
                }
            }
        }

        return uf.getGroups();
    }

    public static void main(String[] args) {
        SimilarStringGroups solution = new SimilarStringGroups();

        // Test case 1
        String[] strs1 = { "tars", "rats", "arts", "star" };
        System.out.println(solution.numSimilarGroups(strs1)); // 2

        // Test case 2
        String[] strs2 = { "omv", "ovm" };
        System.out.println(solution.numSimilarGroups(strs2)); // 1
    }
}
