package design.medium;

import java.util.*;

/**
 * LeetCode 1670: Design Front Middle Back Queue
 * https://leetcode.com/problems/design-front-middle-back-queue/
 *
 * Description: Design a queue that supports push and pop operations in the
 * front, middle, and back.
 * 
 * Constraints:
 * - 1 <= val <= 10^9
 * - At most 1000 calls will be made to pushFront, pushMiddle, pushBack,
 * popFront, popMiddle, and popBack
 *
 * Follow-up:
 * - Can you solve it efficiently using deques?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class FrontMiddleBackQueue {

    private Deque<Integer> left;
    private Deque<Integer> right;

    public FrontMiddleBackQueue() {
        left = new ArrayDeque<>();
        right = new ArrayDeque<>();
    }

    private void balance() {
        // Maintain invariant: size(left) <= size(right) <= size(left) + 1
        if (left.size() > right.size()) {
            right.offerFirst(left.pollLast());
        } else if (right.size() > left.size() + 1) {
            left.offerLast(right.pollFirst());
        }
    }

    public void pushFront(int val) {
        left.offerFirst(val);
        balance();
    }

    public void pushMiddle(int val) {
        if (left.size() == right.size()) {
            right.offerFirst(val);
        } else {
            left.offerLast(val);
        }
    }

    public void pushBack(int val) {
        right.offerLast(val);
        balance();
    }

    public int popFront() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int result;
        if (!left.isEmpty()) {
            result = left.pollFirst();
        } else {
            result = right.pollFirst();
        }

        balance();
        return result;
    }

    public int popMiddle() {
        if (left.isEmpty() && right.isEmpty()) {
            return -1;
        }

        int result;
        if (left.size() == right.size()) {
            result = left.pollLast();
        } else {
            result = right.pollFirst();
        }

        balance();
        return result;
    }

    public int popBack() {
        if (right.isEmpty()) {
            return -1;
        }

        int result = right.pollLast();
        balance();
        return result;
    }

    public static void main(String[] args) {
        FrontMiddleBackQueue q = new FrontMiddleBackQueue();
        q.pushFront(1);
        q.pushBack(2);
        q.pushMiddle(3);
        q.pushMiddle(4);
        System.out.println(q.popFront()); // Expected: 1
        System.out.println(q.popMiddle()); // Expected: 3
        System.out.println(q.popMiddle()); // Expected: 4
        System.out.println(q.popBack()); // Expected: 2
        System.out.println(q.popFront()); // Expected: -1
    }
}
