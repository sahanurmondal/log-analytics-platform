package recursion.medium;

/**
 * LeetCode 47: Permutations II
 * https://leetcode.com/problems/permutations-ii/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Return all unique permutations of nums.
 *
 * Constraints:
 * - 1 <= nums.length <= 8
 * - -10 <= nums[i] <= 10
 *
 * Follow-ups:
 * 1. Can you count the number of unique permutations?
 * 2. Can you generate permutations with constraints?
 * 3. Can you optimize for large input?
 */
public class PermutationsII {
    public java.util.List<java.util.List<Integer>> permuteUnique(int[] nums) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        java.util.Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        dfs(nums, used, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfs(int[] nums, boolean[] used, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        if (path.size() == nums.length) {
            res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i] || (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]))
                continue;
            used[i] = true;
            path.add(nums[i]);
            dfs(nums, used, path, res);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    // Follow-up 1: Count number of unique permutations
    public int countPermuteUnique(int[] nums) {
        return permuteUnique(nums).size();
    }

    // Follow-up 2: Generate permutations with constraints (e.g., only even numbers)
    public java.util.List<java.util.List<Integer>> permuteEven(int[] nums) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        java.util.Arrays.sort(nums);
        boolean[] used = new boolean[nums.length];
        dfsEven(nums, used, new java.util.ArrayList<>(), res);
        return res;
    }

    private void dfsEven(int[] nums, boolean[] used, java.util.List<Integer> path,
            java.util.List<java.util.List<Integer>> res) {
        if (path.size() == nums.length) {
            if (path.stream().allMatch(x -> x % 2 == 0))
                res.add(new java.util.ArrayList<>(path));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i] || (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]))
                continue;
            used[i] = true;
            path.add(nums[i]);
            dfsEven(nums, used, path, res);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    // Follow-up 3: Optimize for large input (not needed for n <= 8)

    public static void main(String[] args) {
        PermutationsII solution = new PermutationsII();
        System.out.println(solution.permuteUnique(new int[] { 1, 1, 2 })); // [[1,1,2],[1,2,1],[2,1,1]]
        System.out.println(solution.countPermuteUnique(new int[] { 1, 1, 2 })); // 3
        System.out.println(solution.permuteEven(new int[] { 2, 2, 4 })); // [[2,2,4],[2,4,2],[4,2,2]]
    }
}
