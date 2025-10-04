package design.hard;

import java.util.*;

/**
 * LeetCode 381: Insert Delete GetRandom O(1) - Duplicates allowed
 * https://leetcode.com/problems/insert-delete-getrandom-o1-duplicates-allowed/
 *
 * Description: RandomizedCollection is a data structure that contains a
 * collection of numbers, possibly duplicates.
 * 
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 2 * 10^5 calls will be made to insert, remove, and getRandom
 * - There will be at least one element in the data structure when getRandom is
 * called
 *
 * Follow-up:
 * - Can you solve it when duplicates are allowed?
 * 
 * Time Complexity: O(1) average for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class RandomizedCollection {

    private List<Integer> values;
    private Map<Integer, Set<Integer>> indices;
    private Random random;

    public RandomizedCollection() {
        values = new ArrayList<>();
        indices = new HashMap<>();
        random = new Random();
    }

    public boolean insert(int val) {
        boolean isNew = !indices.containsKey(val);

        indices.computeIfAbsent(val, k -> new HashSet<>()).add(values.size());
        values.add(val);

        return isNew;
    }

    public boolean remove(int val) {
        if (!indices.containsKey(val) || indices.get(val).isEmpty()) {
            return false;
        }

        // Get an index of val to remove
        int indexToRemove = indices.get(val).iterator().next();
        indices.get(val).remove(indexToRemove);

        int lastIndex = values.size() - 1;
        int lastElement = values.get(lastIndex);

        // Move last element to the position of element to remove
        values.set(indexToRemove, lastElement);

        // Update indices for the moved element
        indices.get(lastElement).remove(lastIndex);
        if (indexToRemove != lastIndex) {
            indices.get(lastElement).add(indexToRemove);
        }

        values.remove(lastIndex);

        return true;
    }

    public int getRandom() {
        return values.get(random.nextInt(values.size()));
    }

    public static void main(String[] args) {
        RandomizedCollection collection = new RandomizedCollection();
        System.out.println(collection.insert(1)); // Expected: true
        System.out.println(collection.insert(1)); // Expected: false
        System.out.println(collection.insert(2)); // Expected: true
        System.out.println(collection.getRandom()); // Expected: 1 or 2
        System.out.println(collection.remove(1)); // Expected: true
        System.out.println(collection.getRandom()); // Expected: 1 or 2
    }
}
