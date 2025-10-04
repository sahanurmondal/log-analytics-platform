package recursion.hard;

/**
 * LeetCode 996: Number of Squareful Arrays
 * https://leetcode.com/problems/number-of-squareful-arrays/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Given an array, return the number of permutations where every pair of
 * adjacent elements sum to a perfect square.
 *
 * Constraints:
 * - 1 <= A.length <= 12
 * - 1 <= A[i] <= 10^9
 *
 * Follow-ups:
 * 1. Can you generate all squareful arrays?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle duplicates efficiently?
 */
public class NumberOfSquarefulArrays {
    public int numSquarefulPerms(int[] A) {
        java.util.Arrays.sort(A);
        boolean[] used = new boolean[A.length];
        return dfs(A, used, -1, 0);
    }

    private int dfs(int[] A, boolean[] used, int prev, int count) {
        if (count == A.length)
            return 1;
        int res = 0;
        for (int i = 0; i < A.length; i++) {
            if (used[i] || (i > 0 && A[i] == A[i - 1] && !used[i - 1]))
                continue;
            if (prev == -1 || isSquare(A[i] + prev)) {
                used[i] = true;
                res += dfs(A, used, A[i], count + 1);
                used[i] = false;
            }
        }
        return res;
    }

    private boolean isSquare(int x) {
        int r = (int) Math.sqrt(x);
        return r * r == x;
    }

    // Follow-up 1: Generate all squareful arrays
    public java.util.List<java.util.List<Integer>> generateSquarefulArrays(int[] A) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        java.util.Arrays.sort(A);
        dfsGen(A, new boolean[A.length], -1, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfsGen(int[] A, boolean[] used, int prev, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        if (path.size() == A.length) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = 0; i < A.length; i++) {
            if (used[i] || (i > 0 && A[i] == A[i - 1] && !used[i - 1]))
                continue;
            if (prev == -1 || isSquare(A[i] + prev)) {
                used[i] = true;
                path.add(A[i]);
                dfsGen(A, used, A[i], path, res);
                path.remove(path.size() - 1);
                used[i] = false;
            }
        }
    }

    // Follow-up 2: Optimize for large arrays (not needed for n <= 12)
    // Follow-up 3: Handle duplicates efficiently (already handled above)

    public static void main(String[] args) {
        NumberOfSquarefulArrays solution = new NumberOfSquarefulArrays();
        System.out.println(solution.numSquarefulPerms(new int[] { 1, 17, 8 })); // 2
        System.out.println(solution.generateSquarefulArrays(new int[] { 1, 17, 8 })); // [[1,8,17],[17,8,1]]
    }
}
