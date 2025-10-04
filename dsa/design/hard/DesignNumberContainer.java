package design.hard;

import java.util.*;

/**
 * LeetCode 2349: Design a Number Container System
 * https://leetcode.com/problems/design-a-number-container-system/
 *
 * Description: Design a number container system that can do the following:
 * - Insert or Replace a number at the given index in the system.
 * - Return the smallest index for the given number in the system.
 * 
 * Constraints:
 * - 1 <= index, number <= 10^9
 * - At most 10^5 calls will be made in total to change and find
 *
 * Follow-up:
 * - Can you optimize for frequent find operations?
 * 
 * Time Complexity: O(log n) for change, O(log n) for find
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class DesignNumberContainer {

    private Map<Integer, Integer> indexToNumber; // index -> number
    private Map<Integer, TreeSet<Integer>> numberToIndices; // number -> sorted indices

    public DesignNumberContainer() {
        indexToNumber = new HashMap<>();
        numberToIndices = new HashMap<>();
    }

    public void change(int index, int number) {
        // Remove old mapping if exists
        if (indexToNumber.containsKey(index)) {
            int oldNumber = indexToNumber.get(index);
            TreeSet<Integer> indices = numberToIndices.get(oldNumber);
            indices.remove(index);
            if (indices.isEmpty()) {
                numberToIndices.remove(oldNumber);
            }
        }

        // Add new mapping
        indexToNumber.put(index, number);
        numberToIndices.computeIfAbsent(number, k -> new TreeSet<>()).add(index);
    }

    public int find(int number) {
        TreeSet<Integer> indices = numberToIndices.get(number);
        return indices == null || indices.isEmpty() ? -1 : indices.first();
    }

    public static void main(String[] args) {
        DesignNumberContainer nc = new DesignNumberContainer();
        System.out.println(nc.find(10)); // Expected: -1
        nc.change(2, 10);
        nc.change(1, 10);
        nc.change(3, 10);
        nc.change(5, 10);
        System.out.println(nc.find(10)); // Expected: 1
        nc.change(1, 20);
        System.out.println(nc.find(10)); // Expected: 2
        System.out.println(nc.find(20)); // Expected: 1
    }
}
