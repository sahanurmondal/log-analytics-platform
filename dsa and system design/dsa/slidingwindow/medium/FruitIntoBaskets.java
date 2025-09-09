package slidingwindow.medium;

/**
 * LeetCode 904: Fruit Into Baskets
 * https://leetcode.com/problems/fruit-into-baskets/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given an array fruits where fruits[i] is the type of fruit,
 * return the length of the longest subarray with at most 2 distinct types.
 *
 * Constraints:
 * - 1 <= fruits.length <= 10^5
 * - 0 <= fruits[i] < fruits.length
 *
 * Follow-up Questions:
 * 1. What if you need at most k distinct types?
 * 2. How to return the actual subarray?
 * 3. How to solve for very large arrays efficiently?
 */
public class FruitIntoBaskets {
    // Approach 1: Sliding Window with HashMap - O(n) time, O(k) space
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

    // Approach 2: Sliding Window with Two Pointers - O(n) time, O(1) space
    public int totalFruitTwoPointers(int[] fruits) {
        int left = 0, maxLen = 0;
        int type1 = -1, type2 = -1, lastType = -1, lastTypeCount = 0;
        for (int right = 0; right < fruits.length; right++) {
            int fruit = fruits[right];
            if (fruit == type1 || fruit == type2) {
                maxLen = Math.max(maxLen, right - left + 1);
            } else {
                left = right - lastTypeCount;
                type1 = lastType;
                type2 = fruit;
                maxLen = Math.max(maxLen, right - left + 1);
            }
            if (fruit == lastType)
                lastTypeCount++;
            else {
                lastType = fruit;
                lastTypeCount = 1;
            }
        }
        return maxLen;
    }

    // Follow-up 1: At most k distinct types
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

    // Comprehensive test cases
    public static void main(String[] args) {
        FruitIntoBaskets sol = new FruitIntoBaskets();
        // Test 1: Basic
        System.out.println("Test 1: Expected 3 -> " + sol.totalFruit(new int[] { 1, 2, 1 }));
        // Test 2: Two types
        System.out.println("Test 2: Expected 4 -> " + sol.totalFruit(new int[] { 0, 1, 2, 2 }));
        // Test 3: More than two types
        System.out.println("Test 3: Expected 5 -> " + sol.totalFruit(new int[] { 1, 2, 3, 2, 2 }));
        // Test 4: All same type
        System.out.println("Test 4: Expected 6 -> " + sol.totalFruit(new int[] { 2, 2, 2, 2, 2, 2 }));
        // Test 5: Empty array
        System.out.println("Test 5: Expected 0 -> " + sol.totalFruit(new int[] {}));
        // Test 6: Two pointers approach
        System.out.println("Test 6: Expected 3 -> " + sol.totalFruitTwoPointers(new int[] { 1, 2, 1 }));
        // Test 7: At most k types
        System.out.println("Test 7: Expected 4 -> " + sol.totalFruitKTypes(new int[] { 0, 1, 2, 2 }, 2));
        // Test 8: k = 1
        System.out.println("Test 8: Expected 2 -> " + sol.totalFruitKTypes(new int[] { 1, 1, 2 }, 1));
        // Test 9: k = 3
        System.out.println("Test 9: Expected 5 -> " + sol.totalFruitKTypes(new int[] { 1, 2, 3, 2, 2 }, 3));
        // Test 10: Edge case, single element
        System.out.println("Test 10: Expected 1 -> " + sol.totalFruit(new int[] { 5 }));
        // Test 11: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = i % 2;
        System.out.println("Test 11: Large input -> " + sol.totalFruit(large));
        // Test 12: All distinct
        System.out.println("Test 12: Expected 2 -> " + sol.totalFruit(new int[] { 1, 2, 3, 4, 5 }));
        // Test 13: Alternating types
        System.out.println("Test 13: Expected 2 -> " + sol.totalFruit(new int[] { 1, 2, 1, 2, 1, 2 }));
        // Test 14: k = fruits.length
        System.out.println("Test 14: Expected 6 -> " + sol.totalFruitKTypes(new int[] { 1, 2, 3, 4, 5, 6 }, 6));
        // Test 15: k = 0
        System.out.println("Test 15: Expected 0 -> " + sol.totalFruitKTypes(new int[] { 1, 2, 3 }, 0));
    }
}
