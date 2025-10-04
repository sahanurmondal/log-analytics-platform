package math.medium;

/**
 * LeetCode 398: Random Pick Index
 * https://leetcode.com/problems/random-pick-index/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Given an array of integers with possible duplicates, implement a method to
 * randomly pick an index of a given target.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -10^9 <= nums[i] <= 10^9
 *
 * Follow-ups:
 * 1. Can you optimize for multiple queries?
 * 2. Can you handle updates to the array?
 * 3. Can you pick k random indices?
 */
public class RandomPickIndex {
    private int[] nums;
    private java.util.Random rand = new java.util.Random();

    public RandomPickIndex(int[] nums) {
        this.nums = nums;
    }

    public int pick(int target) {
        int res = -1, count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                count++;
                if (rand.nextInt(count) == 0)
                    res = i;
            }
        }
        return res;
    }

    // Follow-up 1: Optimize for multiple queries (precompute map)
    private java.util.Map<Integer, java.util.List<Integer>> map = null;

    public void preprocess() {
        map = new java.util.HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.computeIfAbsent(nums[i], k -> new java.util.ArrayList<>()).add(i);
        }
    }

    public int pickFast(int target) {
        if (map == null)
            preprocess();
        java.util.List<Integer> indices = map.get(target);
        if (indices == null || indices.isEmpty())
            return -1;
        return indices.get(rand.nextInt(indices.size()));
    }

    // Follow-up 2: Handle updates to the array
    public void update(int index, int value) {
        nums[index] = value;
        map = null; // Invalidate cache
    }

    // Follow-up 3: Pick k random indices
    public int[] pickK(int target, int k) {
        java.util.List<Integer> indices = new java.util.ArrayList<>();
        for (int i = 0; i < nums.length; i++)
            if (nums[i] == target)
                indices.add(i);
        java.util.Collections.shuffle(indices);
        return indices.stream().limit(k).mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        RandomPickIndex solution = new RandomPickIndex(new int[] { 1, 2, 3, 3, 3 });
        System.out.println(solution.pick(3)); // random index of 3
        solution.preprocess();
        System.out.println(solution.pickFast(3)); // fast random index of 3
        solution.update(2, 4);
        System.out.println(solution.pick(3)); // random index of 3 after update
        System.out.println(java.util.Arrays.toString(solution.pickK(3, 2))); // two random indices of 3
    }
}
