package design.medium;

import java.util.*;

/**
 * LeetCode 379: Design Phone Directory
 * https://leetcode.com/problems/design-phone-directory/
 *
 * Description: Design a phone directory that initially has maxNumbers empty
 * slots that can store numbers.
 * 
 * Constraints:
 * - 1 <= maxNumbers <= 10^4
 * - 0 <= number < maxNumbers
 * - At most 2 * 10^4 calls will be made to get, check, and release
 *
 * Follow-up:
 * - Can you solve it with O(1) for all operations?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class PhoneDirectory {

    private Set<Integer> available;
    private Iterator<Integer> iterator;

    public PhoneDirectory(int maxNumbers) {
        available = new LinkedHashSet<>();
        for (int i = 0; i < maxNumbers; i++) {
            available.add(i);
        }
        iterator = available.iterator();
    }

    public int get() {
        if (available.isEmpty()) {
            return -1;
        }

        int number = iterator.next();
        iterator.remove();

        // Reset iterator if needed
        if (!iterator.hasNext()) {
            iterator = available.iterator();
        }

        return number;
    }

    public boolean check(int number) {
        return available.contains(number);
    }

    public void release(int number) {
        if (available.add(number)) {
            iterator = available.iterator();
        }
    }

    // Alternative implementation using array and pointer
    static class PhoneDirectoryArray {
        private boolean[] used;
        private Queue<Integer> available;

        public PhoneDirectoryArray(int maxNumbers) {
            used = new boolean[maxNumbers];
            available = new LinkedList<>();
            for (int i = 0; i < maxNumbers; i++) {
                available.offer(i);
            }
        }

        public int get() {
            if (available.isEmpty()) {
                return -1;
            }

            int number = available.poll();
            used[number] = true;
            return number;
        }

        public boolean check(int number) {
            return !used[number];
        }

        public void release(int number) {
            if (used[number]) {
                used[number] = false;
                available.offer(number);
            }
        }
    }

    public static void main(String[] args) {
        PhoneDirectory directory = new PhoneDirectory(3);
        System.out.println(directory.get()); // Expected: 0
        System.out.println(directory.get()); // Expected: 1
        System.out.println(directory.check(2)); // Expected: true
        System.out.println(directory.get()); // Expected: 2
        System.out.println(directory.check(2)); // Expected: false
        directory.release(2);
        System.out.println(directory.check(2)); // Expected: true
    }
}
