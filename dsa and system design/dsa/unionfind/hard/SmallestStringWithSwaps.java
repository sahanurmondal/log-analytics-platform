package unionfind.hard;

import java.util.*;

/**
 * LeetCode 1202: Smallest String With Swaps
 * https://leetcode.com/problems/smallest-string-with-swaps/
 *
 * Description:
 * You are given a string s, and an array of pairs of indices in the string
 * pairs
 * where pairs[i] = [a, b] indicates 2 indices(0-indexed) of the string.
 * You can swap the characters at any pair of indices in the given pairs any
 * number of times.
 * Return the lexicographically smallest string that s can be changed to after
 * using the swaps.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - 0 <= pairs.length <= 10^5
 * - 0 <= pairs[i][0], pairs[i][1] < s.length
 * - s only contains lowercase English letters
 */
public class SmallestStringWithSwaps {

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
    }

    public String smallestStringWithSwaps(String s, List<List<Integer>> pairs) {
        int n = s.length();
        UnionFind uf = new UnionFind(n);

        // Union all swappable indices
        for (List<Integer> pair : pairs) {
            uf.union(pair.get(0), pair.get(1));
        }

        // Group indices by their root parent
        Map<Integer, List<Integer>> groups = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            groups.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        char[] result = s.toCharArray();

        // Sort characters in each group
        for (List<Integer> group : groups.values()) {
            List<Character> chars = new ArrayList<>();
            for (int idx : group) {
                chars.add(s.charAt(idx));
            }
            Collections.sort(chars);
            Collections.sort(group);

            for (int i = 0; i < group.size(); i++) {
                result[group.get(i)] = chars.get(i);
            }
        }

        return new String(result);
    }

    public static void main(String[] args) {
        SmallestStringWithSwaps solution = new SmallestStringWithSwaps();

        // Test case 1
        List<List<Integer>> pairs1 = Arrays.asList(
                Arrays.asList(0, 3),
                Arrays.asList(1, 2));
        System.out.println(solution.smallestStringWithSwaps("dcab", pairs1)); // "bacd"

        // Test case 2
        List<List<Integer>> pairs2 = Arrays.asList(
                Arrays.asList(0, 3),
                Arrays.asList(1, 2),
                Arrays.asList(0, 2));
        System.out.println(solution.smallestStringWithSwaps("dcab", pairs2)); // "abcd"
    }
}
