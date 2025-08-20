package recursion.hard;

/**
 * LeetCode 753: Cracking the Safe
 * https://leetcode.com/problems/cracking-the-safe/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Return any string that satisfies the conditions for cracking the safe.
 *
 * Constraints:
 * - 1 <= n <= 4
 * - 1 <= k <= 10
 *
 * Follow-ups:
 * 1. Can you generate all possible combinations?
 * 2. Can you optimize for large n, k?
 * 3. Can you find the shortest sequence?
 */
public class CrackingTheSafe {
    public String crackSafe(int n, int k) {
        StringBuilder sb = new StringBuilder();
        java.util.Set<String> seen = new java.util.HashSet<>();
        String start = "0".repeat(n - 1);
        dfs(start, k, seen, sb, n);
        sb.append(start);
        return sb.toString();
    }

    private void dfs(String node, int k, java.util.Set<String> seen, StringBuilder sb, int n) {
        for (int i = 0; i < k; i++) {
            String next = node + i;
            if (!seen.contains(next)) {
                seen.add(next);
                dfs(next.substring(1), k, seen, sb, n);
                sb.append(i);
            }
        }
    }

    // Follow-up 1: Generate all possible combinations
    public java.util.List<String> generateAllCombinations(int n, int k) {
        java.util.List<String> res = new java.util.ArrayList<>();
        dfsGen("", n, k, res);
        return res;
    }

    private void dfsGen(String curr, int n, int k, java.util.List<String> res) {
        if (curr.length() == n) {
            res.add(curr);
            return;
        }
        for (int i = 0; i < k; i++)
            dfsGen(curr + i, n, k, res);
    }

    // Follow-up 2: Optimize for large n, k (already optimal)
    // Follow-up 3: Find shortest sequence (already handled by de Bruijn sequence)

    public static void main(String[] args) {
        CrackingTheSafe solution = new CrackingTheSafe();
        System.out.println(solution.crackSafe(2, 2)); // "00110"
        System.out.println(solution.generateAllCombinations(2, 2)); // ["00","01","10","11"]
    }
}
