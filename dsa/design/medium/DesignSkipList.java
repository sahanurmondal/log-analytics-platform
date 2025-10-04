package design.medium;

import java.util.*;

/**
 * LeetCode 1206: Design Skiplist
 * https://leetcode.com/problems/design-skiplist/
 *
 * Description: Design a Skiplist without using any built-in libraries.
 * A skiplist is a data structure that takes O(log(n)) time to add, erase and
 * search.
 * 
 * Constraints:
 * - 0 <= num, target <= 2 * 10^4
 * - At most 5 * 10^4 calls will be made to search, add, and erase
 *
 * Follow-up:
 * - Can you come up with O(log n) time complexity for all three operations?
 * 
 * Time Complexity: O(log n) expected for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignSkipList {

    class SkipListNode {
        int val;
        SkipListNode[] forward;

        SkipListNode(int val, int level) {
            this.val = val;
            this.forward = new SkipListNode[level + 1];
        }
    }

    private static final int MAX_LEVEL = 16;
    private SkipListNode head;
    private Random random;
    private int level;

    public DesignSkipList() {
        head = new SkipListNode(-1, MAX_LEVEL);
        random = new Random();
        level = 0;
    }

    private int randomLevel() {
        int lvl = 0;
        while (lvl < MAX_LEVEL && random.nextDouble() < 0.5) {
            lvl++;
        }
        return lvl;
    }

    public boolean search(int target) {
        SkipListNode current = head;

        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].val < target) {
                current = current.forward[i];
            }
        }

        current = current.forward[0];
        return current != null && current.val == target;
    }

    public void add(int num) {
        SkipListNode[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode current = head;

        // Find position to insert
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].val < num) {
                current = current.forward[i];
            }
            update[i] = current;
        }

        int newLevel = randomLevel();
        if (newLevel > level) {
            for (int i = level + 1; i <= newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }

        SkipListNode newNode = new SkipListNode(num, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
    }

    public boolean erase(int num) {
        SkipListNode[] update = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode current = head;

        // Find position to delete
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null && current.forward[i].val < num) {
                current = current.forward[i];
            }
            update[i] = current;
        }

        current = current.forward[0];
        if (current == null || current.val != num) {
            return false;
        }

        // Remove node from all levels
        for (int i = 0; i <= level; i++) {
            if (update[i].forward[i] != current) {
                break;
            }
            update[i].forward[i] = current.forward[i];
        }

        // Update level
        while (level > 0 && head.forward[level] == null) {
            level--;
        }

        return true;
    }

    public static void main(String[] args) {
        DesignSkipList skiplist = new DesignSkipList();
        skiplist.add(1);
        skiplist.add(2);
        skiplist.add(3);
        System.out.println(skiplist.search(0)); // Expected: false
        skiplist.add(4);
        System.out.println(skiplist.search(1)); // Expected: true
        System.out.println(skiplist.erase(0)); // Expected: false
        System.out.println(skiplist.erase(1)); // Expected: true
        System.out.println(skiplist.search(1)); // Expected: false
    }
}
