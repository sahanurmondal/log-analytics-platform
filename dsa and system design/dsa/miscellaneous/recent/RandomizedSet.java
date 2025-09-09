package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Insert Delete GetRandom O(1)
 * 
 * Description:
 * Design a data structure that supports insert, delete, and getRandom
 * operations
 * all in O(1) average time complexity.
 * 
 * Companies: Facebook, Amazon, Google
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class RandomizedSet {

    private List<Integer> nums;
    private Map<Integer, Integer> indices;
    private Random random;

    public RandomizedSet() {
        nums = new ArrayList<>();
        indices = new HashMap<>();
        random = new Random();
    }

    public boolean insert(int val) {
        if (indices.containsKey(val)) {
            return false;
        }

        indices.put(val, nums.size());
        nums.add(val);
        return true;
    }

    public boolean remove(int val) {
        if (!indices.containsKey(val)) {
            return false;
        }

        int index = indices.get(val);
        int lastElement = nums.get(nums.size() - 1);

        nums.set(index, lastElement);
        indices.put(lastElement, index);

        nums.remove(nums.size() - 1);
        indices.remove(val);

        return true;
    }

    public int getRandom() {
        return nums.get(random.nextInt(nums.size()));
    }

    public static void main(String[] args) {
        RandomizedSet randomSet = new RandomizedSet();

        System.out.println(randomSet.insert(1)); // true
        System.out.println(randomSet.remove(2)); // false
        System.out.println(randomSet.insert(2)); // true
        System.out.println(randomSet.getRandom()); // 1 or 2
        System.out.println(randomSet.remove(1)); // true
        System.out.println(randomSet.insert(2)); // false
        System.out.println(randomSet.getRandom()); // 2
    }
}
