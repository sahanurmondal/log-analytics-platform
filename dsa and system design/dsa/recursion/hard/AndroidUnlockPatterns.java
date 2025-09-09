package recursion.hard;

/**
 * LeetCode 351: Android Unlock Patterns
 * https://leetcode.com/problems/android-unlock-patterns/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Given m and n, return the number of Android unlock patterns of length between
 * m and n.
 *
 * Constraints:
 * - 1 <= m <= n <= 9
 *
 * Follow-ups:
 * 1. Can you generate all patterns?
 * 2. Can you handle custom grids?
 * 3. Can you optimize for large m, n?
 */
public class AndroidUnlockPatterns {
    private int[][] skip = new int[10][10];
    private boolean[] visited = new boolean[10];

    public int numberOfPatterns(int m, int n) {
        skip[1][3] = skip[3][1] = 2;
        skip[1][7] = skip[7][1] = 4;
        skip[3][9] = skip[9][3] = 6;
        skip[7][9] = skip[9][7] = 8;
        skip[1][9] = skip[9][1] = skip[2][8] = skip[8][2] = skip[3][7] = skip[7][3] = skip[4][6] = skip[6][4] = 5;
        int res = 0;
        for (int len = m; len <= n; len++) {
            res += dfs(1, len - 1) * 4;
            res += dfs(2, len - 1) * 4;
            res += dfs(5, len - 1);
        }
        return res;
    }

    private int dfs(int curr, int remain) {
        if (remain == 0)
            return 1;
        visited[curr] = true;
        int res = 0;
        for (int i = 1; i <= 9; i++) {
            if (!visited[i] && (skip[curr][i] == 0 || visited[skip[curr][i]]))
                res += dfs(i, remain - 1);
        }
        visited[curr] = false;
        return res;
    }

    // Follow-up 1: Generate all patterns
    public java.util.List<java.util.List<Integer>> generatePatterns(int m, int n) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        for (int len = m; len <= n; len++) {
            for (int i = 1; i <= 9; i++) {
                dfsGen(i, len - 1, new java.util.ArrayList<>(), res);
            }
        }
        return res;
    }

    private void dfsGen(int curr, int remain, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        path.add(curr);
        visited[curr] = true;
        if (remain == 0)
            res.add(new java.util.ArrayList<>(path));
        else {
            for (int i = 1; i <= 9; i++) {
                if (!visited[i] && (skip[curr][i] == 0 || visited[skip[curr][i]]))
                    dfsGen(i, remain - 1, path, res);
            }
        }
        visited[curr] = false;
        path.remove(path.size() - 1);
    }

    // Follow-up 2: Custom grid (not implemented)
    // Follow-up 3: Optimize for large m, n (not needed for n <= 9)

    public static void main(String[] args) {
        AndroidUnlockPatterns solution = new AndroidUnlockPatterns();
        System.out.println(solution.numberOfPatterns(1, 1)); // 9
        System.out.println(solution.numberOfPatterns(1, 2)); // 65
        System.out.println(solution.numberOfPatterns(2, 3)); // 544
        // Edge Case: Maximum pattern length
        System.out.println(solution.numberOfPatterns(1, 9)); // All possible patterns
        // Edge Case: Fixed length
        System.out.println(solution.numberOfPatterns(4, 4)); // Patterns of exactly length 4
        System.out.println(solution.generatePatterns(1, 2).size()); // 65
    }
}
