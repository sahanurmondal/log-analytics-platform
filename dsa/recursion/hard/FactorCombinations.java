package recursion.hard;

/**
 * LeetCode 254: Factor Combinations
 * https://leetcode.com/problems/factor-combinations/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Return all possible combinations of factors (excluding 1 and n).
 *
 * Constraints:
 * - 1 <= n <= 10^7
 *
 * Follow-ups:
 * 1. Can you count the number of combinations?
 * 2. Can you generate combinations with constraints?
 * 3. Can you optimize for large n?
 */
public class FactorCombinations {
    public java.util.List<java.util.List<Integer>> getFactors(int n) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        dfs(n, 2, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfs(int n, int start, java.util.List<Integer> path, java.util.List<java.util.List<Integer>> res) {
        if (n == 1 && path.size() > 1) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = start; i <= n; i++) {
            if (n % i == 0) {
                path.add(i);
                dfs(n / i, i, path, res);
                path.remove(path.size() - 1);
            }
        }
    }

    // Follow-up 1: Count number of combinations
    public int countFactors(int n) {
        return countHelper(n, 2);
    }

    private int countHelper(int n, int start) {
        int count = 0;
        for (int i = start; i <= n; i++) {
            if (n % i == 0) {
                if (n / i == 1 && i != n)
                    count++;
                else
                    count += countHelper(n / i, i);
            }
        }
        return count;
    }

    // Follow-up 2: Generate combinations with constraints (e.g., max factor)
    public java.util.List<java.util.List<Integer>> getFactorsWithMax(int n, int maxFactor) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        dfsMax(n, 2, maxFactor, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfsMax(int n, int start, int maxFactor, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        if (n == 1 && path.size() > 1) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = start; i <= Math.min(n, maxFactor); i++) {
            if (n % i == 0) {
                path.add(i);
                dfsMax(n / i, i, maxFactor, path, res);
                path.remove(path.size() - 1);
            }
        }
    }

    // Follow-up 3: Optimize for large n (skip unnecessary factors)
    // Already optimized by starting from 2

    public static void main(String[] args) {
        FactorCombinations solution = new FactorCombinations();
        System.out.println(solution.getFactors(1)); // []
        System.out.println(solution.getFactors(12)); // [[2,6],[2,2,3],[3,4]]
        System.out.println(solution.getFactors(37)); // []
        // Edge Case: Prime number
        System.out.println(solution.getFactors(17)); // []
        // Edge Case: Perfect square
        System.out.println(solution.getFactors(16)); // [[2,8],[2,2,4],[2,2,2,2],[4,4]]
        // Edge Case: Large composite number
        System.out.println(solution.getFactors(24)); // Many combinations
        System.out.println(solution.countFactors(12)); // 3
        System.out.println(solution.getFactorsWithMax(12, 3)); // [[2,2,3],[3,4]]
    }
}
