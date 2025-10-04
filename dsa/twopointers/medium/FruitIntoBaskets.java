package twopointers.medium;

/**
 * LeetCode 904: Fruit Into Baskets
 * https://leetcode.com/problems/fruit-into-baskets/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given an array representing trees with fruit types, return the length of the
 * longest subarray with at most two types.
 *
 * Constraints:
 * - 1 <= fruits.length <= 10^5
 * - 0 <= fruits[i] < fruits.length
 *
 * Follow-ups:
 * 1. Can you generalize to k types?
 * 2. Can you return the actual subarray?
 * 3. Can you handle dynamic updates?
 */
public class FruitIntoBaskets {
    public int totalFruit(int[] fruits) {
        int left = 0, maxLen = 0;
        java.util.Map<Integer, Integer> count = new java.util.HashMap<>();
        for (int right = 0; right < fruits.length; right++) {
            count.put(fruits[right], count.getOrDefault(fruits[right], 0) + 1);
            while (count.size() > 2) {
                count.put(fruits[left], count.get(fruits[left]) - 1);
                if (count.get(fruits[left]) == 0)
                    count.remove(fruits[left]);
                left++;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 1: Generalize to k types
    public int totalFruitKTypes(int[] fruits, int k) {
        int left = 0, maxLen = 0;
        java.util.Map<Integer, Integer> count = new java.util.HashMap<>();
        for (int right = 0; right < fruits.length; right++) {
            count.put(fruits[right], count.getOrDefault(fruits[right], 0) + 1);
            while (count.size() > k) {
                count.put(fruits[left], count.get(fruits[left]) - 1);
                if (count.get(fruits[left]) == 0)
                    count.remove(fruits[left]);
                left++;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 2: Return actual subarray
    public int[] longestSubarray(int[] fruits) {
        int left = 0, maxLen = 0, start = 0;
        java.util.Map<Integer, Integer> count = new java.util.HashMap<>();
        for (int right = 0; right < fruits.length; right++) {
            count.put(fruits[right], count.getOrDefault(fruits[right], 0) + 1);
            while (count.size() > 2) {
                count.put(fruits[left], count.get(fruits[left]) - 1);
                if (count.get(fruits[left]) == 0)
                    count.remove(fruits[left]);
                left++;
            }
            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }
        }
        int[] res = new int[maxLen];
        System.arraycopy(fruits, start, res, 0, maxLen);
        return res;
    }

    // Follow-up 3: Dynamic updates (not implemented)

    public static void main(String[] args) {
        FruitIntoBaskets solution = new FruitIntoBaskets();
        // Basic case
        int[] fruits1 = { 1, 2, 1 };
        System.out.println("Basic: " + solution.totalFruit(fruits1)); // 3

        // Edge: All same type
        int[] fruits2 = { 2, 2, 2, 2 };
        System.out.println("All same: " + solution.totalFruit(fruits2)); // 4

        // Edge: More than two types
        int[] fruits3 = { 1, 2, 3, 2, 2 };
        System.out.println("More than two types: " + solution.totalFruit(fruits3)); // 4

        // Edge: Only one tree
        int[] fruits4 = { 0 };
        System.out.println("Single tree: " + solution.totalFruit(fruits4)); // 1

        // Follow-up: k types
        int[] fruits5 = { 1, 2, 3, 2, 2, 4, 5 };
        System.out.println("K=3 types: " + solution.totalFruitKTypes(fruits5, 3)); // 6

        // Follow-up: actual subarray
        int[] subarray = solution.longestSubarray(fruits3);
        System.out.println("Actual subarray: " + java.util.Arrays.toString(subarray)); // [2,3,2,2]
    }
}
