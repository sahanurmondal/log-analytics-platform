package recursion.medium;

/**
 * LeetCode 40: Combination Sum II
 * https://leetcode.com/problems/combination-sum-ii/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Find all unique combinations in candidates where the candidate numbers sum to
 * target.
 *
 * Constraints:
 * - 1 <= candidates.length <= 100
 * - 1 <= candidates[i] <= 50
 * - 1 <= target <= 30
 *
 * Follow-ups:
 * 1. Can you count the number of combinations?
 * 2. Can you handle duplicates efficiently?
 * 3. Can you optimize for large input?
 */
public class CombinationSumII {
    public java.util.List<java.util.List<Integer>> combinationSum2(int[] candidates, int target) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        java.util.Arrays.sort(candidates);
        dfs(candidates, target, 0, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfs(int[] candidates, int target, int start, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        if (target == 0) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = start; i < candidates.length; i++) {
            if (i > start && candidates[i] == candidates[i - 1])
                continue;
            if (candidates[i] > target)
                break;
            path.add(candidates[i]);
            dfs(candidates, target - candidates[i], i + 1, path, res);
            path.remove(path.size() - 1);
        }
    }

    // Follow-up 1: Count number of combinations
    public int countCombinationSum2(int[] candidates, int target) {
        return combinationSum2(candidates, target).size();
    }

    // Follow-up 2: Handle duplicates efficiently (already handled above)
    // Follow-up 3: Optimize for large input (not needed for constraints)

    public static void main(String[] args) {
        CombinationSumII solution = new CombinationSumII();
        System.out.println(solution.combinationSum2(new int[] { 10, 1, 2, 7, 6, 1, 5 }, 8)); // [[1,1,6],[1,2,5],[1,7],[2,6]]
        System.out.println(solution.countCombinationSum2(new int[] { 10, 1, 2, 7, 6, 1, 5 }, 8)); // 4
    }
}
