package arrays.easy;

import java.util.*;

/**
 * LeetCode 136: Single Number
 * https://leetcode.com/problems/single-number/
 */
public class SingleNumber {
    // Main solution - XOR approach
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    // Alternative solution - HashMap
    public int singleNumberHashMap(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();

        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return -1;
    }

    // Alternative solution - HashSet
    public int singleNumberHashSet(int[] nums) {
        Set<Integer> set = new HashSet<>();

        for (int num : nums) {
            if (set.contains(num)) {
                set.remove(num);
            } else {
                set.add(num);
            }
        }

        return set.iterator().next();
    }

    public static void main(String[] args) {
        SingleNumber solution = new SingleNumber();
        System.out.println(solution.singleNumber(new int[] { 2, 2, 1 })); // 1
        System.out.println(solution.singleNumber(new int[] { 4, 1, 2, 1, 2 })); // 4
        System.out.println(solution.singleNumber(new int[] { 1 })); // 1
    }
}
