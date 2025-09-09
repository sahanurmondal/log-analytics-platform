package design.medium;

import java.util.*;

/**
 * LeetCode 380: Insert Delete GetRandom O(1)
 * https://leetcode.com/problems/insert-delete-getrandom-o1/
 *
 * Description: Implement the RandomizedSet class with insert, remove, and
 * getRandom methods all in O(1) average time complexity.
 * 
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 2 * 10^5 calls will be made to insert, remove, and getRandom
 * - There will be at least one element in the data structure when getRandom is
 * called
 *
 * Follow-up:
 * - Can you implement the functions without using extra space?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class RandomizedSet {

    private List<Integer> values;
    private Map<Integer, Integer> indices;
    private Random random;

    public RandomizedSet() {
        values = new ArrayList<>();
        indices = new HashMap<>();
        random = new Random();
    }

    public boolean insert(int val) {
        if (indices.containsKey(val)) {
            return false;
        }

        indices.put(val, values.size());
        values.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!indices.containsKey(val)) {
            return false;
        }

        int indexToRemove = indices.get(val);
        int lastElement = values.get(values.size() - 1);

        // Move last element to the position of element to remove
        values.set(indexToRemove, lastElement);
        indices.put(lastElement, indexToRemove);

        // Remove the last element
        values.remove(values.size() - 1);
        indices.remove(val);

        return true;
    }

    public int getRandom() {
        return values.get(random.nextInt(values.size()));
    }

    public static void main(String[] args) {
        RandomizedSet randomizedSet = new RandomizedSet();
        System.out.println(randomizedSet.insert(1)); // Expected: true
        System.out.println(randomizedSet.remove(2)); // Expected: false
        System.out.println(randomizedSet.insert(2)); // Expected: true
        System.out.println(randomizedSet.getRandom()); // Expected: 1 or 2
        System.out.println(randomizedSet.remove(1)); // Expected: true
        System.out.println(randomizedSet.insert(2)); // Expected: false
        System.out.println(randomizedSet.getRandom()); // Expected: 2
    }
}
