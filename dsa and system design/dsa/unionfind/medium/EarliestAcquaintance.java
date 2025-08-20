package unionfind.medium;

import java.util.*;

/**
 * LeetCode 1101: The Earliest Moment When Everyone Become Friends
 * https://leetcode.com/problems/the-earliest-moment-when-everyone-become-friends/
 *
 * Description:
 * There are n people in a social group labeled from 0 to n - 1. You are given
 * an array logs
 * where logs[i] = [timestampi, xi, yi] indicates that xi and yi will be friends
 * at the time timestampi.
 * Friendship is symmetric. Return the earliest time for which every person
 * became acquainted with every other person.
 * If there is no such earliest time, return -1.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - 1 <= logs.length <= 10^4
 * - logs[i].length == 3
 * - 0 <= timestampi <= 10^9
 * - 0 <= xi, yi <= n - 1
 * - xi != yi
 * - All the values timestampi are unique
 */
public class EarliestAcquaintance {

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

        public boolean union(int x, int y) {
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
                return true;
            }
            return false;
        }

        public int getComponents() {
            return components;
        }
    }

    public int earliestAcq(int[][] logs, int n) {
        Arrays.sort(logs, (a, b) -> Integer.compare(a[0], b[0]));

        UnionFind uf = new UnionFind(n);

        for (int[] log : logs) {
            int timestamp = log[0];
            int x = log[1];
            int y = log[2];

            if (uf.union(x, y)) {
                if (uf.getComponents() == 1) {
                    return timestamp;
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        EarliestAcquaintance solution = new EarliestAcquaintance();

        // Test case 1
        int[][] logs1 = { { 20190101, 0, 1 }, { 20190104, 3, 4 }, { 20190107, 2, 3 }, { 20190211, 1, 5 },
                { 20190224, 2, 4 }, { 20190301, 0, 3 }, { 20190312, 1, 2 }, { 20190322, 4, 5 } };
        System.out.println(solution.earliestAcq(logs1, 6)); // 20190301

        // Test case 2
        int[][] logs2 = { { 0, 2, 0 }, { 1, 0, 1 }, { 3, 0, 3 }, { 4, 1, 2 }, { 7, 3, 1 } };
        System.out.println(solution.earliestAcq(logs2, 4)); // 3
    }
}
