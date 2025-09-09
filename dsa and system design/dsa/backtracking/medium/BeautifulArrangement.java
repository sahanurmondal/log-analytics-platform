package backtracking.medium;

/**
 * LeetCode 526: Beautiful Arrangement
 * https://leetcode.com/problems/beautiful-arrangement/
 *
 * Description: Suppose you have n integers labeled 1 through n. A permutation
 * of those n integers perm (1-indexed)
 * is considered a beautiful arrangement if for every i (1 <= i <= n), either:
 * perm[i] is divisible by i, or
 * i is divisible by perm[i].
 * Given an integer n, return the number of the beautiful arrangements that you
 * can construct.
 * 
 * Constraints:
 * - 1 <= n <= 15
 *
 * Follow-up:
 * - Can you optimize with pruning?
 * 
 * Time Complexity: O(k) where k is number of valid permutations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class BeautifulArrangement {

    public int countArrangement(int n) {
        boolean[] visited = new boolean[n + 1];
        return backtrack(n, 1, visited);
    }

    private int backtrack(int n, int pos, boolean[] visited) {
        if (pos > n)
            return 1;

        int count = 0;
        for (int i = 1; i <= n; i++) {
            if (!visited[i] && (pos % i == 0 || i % pos == 0)) {
                visited[i] = true;
                count += backtrack(n, pos + 1, visited);
                visited[i] = false;
            }
        }

        return count;
    }

    // Alternative solution - Start from end (optimization)
    public int countArrangementOptimized(int n) {
        boolean[] visited = new boolean[n + 1];
        return backtrackFromEnd(n, n, visited);
    }

    private int backtrackFromEnd(int n, int pos, boolean[] visited) {
        if (pos == 0)
            return 1;

        int count = 0;
        for (int i = 1; i <= n; i++) {
            if (!visited[i] && (pos % i == 0 || i % pos == 0)) {
                visited[i] = true;
                count += backtrackFromEnd(n, pos - 1, visited);
                visited[i] = false;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        BeautifulArrangement solution = new BeautifulArrangement();

        System.out.println(solution.countArrangement(2)); // Expected: 2
        System.out.println(solution.countArrangement(1)); // Expected: 1
        System.out.println(solution.countArrangement(3)); // Expected: 3
        System.out.println(solution.countArrangement(4)); // Expected: 8
        System.out.println(solution.countArrangement(15)); // Expected: 24679
    }
}
