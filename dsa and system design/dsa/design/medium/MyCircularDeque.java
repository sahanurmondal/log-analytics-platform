package design.medium;

/**
 * LeetCode 641: Design Circular Deque
 * https://leetcode.com/problems/design-circular-deque/
 *
 * Description: Design your implementation of the circular double-ended queue
 * (deque).
 * 
 * Constraints:
 * - 1 <= k <= 1000
 * - 0 <= value <= 1000
 * - At most 2000 calls will be made to insertFront, insertLast, deleteFront,
 * deleteLast, getFront, getRear, isEmpty, isFull
 *
 * Follow-up:
 * - Can you solve it without using the built-in deque?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(k)
 * 
 * Company Tags: Google, Facebook
 */
public class MyCircularDeque {

    private int[] deque;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public MyCircularDeque(int k) {
        capacity = k;
        deque = new int[k];
        front = 0;
        rear = 0;
        size = 0;
    }

    public boolean insertFront(int value) {
        if (isFull()) {
            return false;
        }

        front = (front - 1 + capacity) % capacity;
        deque[front] = value;
        size++;

        return true;
    }

    public boolean insertLast(int value) {
        if (isFull()) {
            return false;
        }

        deque[rear] = value;
        rear = (rear + 1) % capacity;
        size++;

        return true;
    }

    public boolean deleteFront() {
        if (isEmpty()) {
            return false;
        }

        front = (front + 1) % capacity;
        size--;

        return true;
    }

    public boolean deleteLast() {
        if (isEmpty()) {
            return false;
        }

        rear = (rear - 1 + capacity) % capacity;
        size--;

        return true;
    }

    public int getFront() {
        return isEmpty() ? -1 : deque[front];
    }

    public int getRear() {
        return isEmpty() ? -1 : deque[(rear - 1 + capacity) % capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public static void main(String[] args) {
        MyCircularDeque circularDeque = new MyCircularDeque(3);
        System.out.println(circularDeque.insertLast(1)); // Expected: true
        System.out.println(circularDeque.insertLast(2)); // Expected: true
        System.out.println(circularDeque.insertFront(3)); // Expected: true
        System.out.println(circularDeque.insertFront(4)); // Expected: false
        System.out.println(circularDeque.getRear()); // Expected: 2
        System.out.println(circularDeque.isFull()); // Expected: true
        System.out.println(circularDeque.deleteLast()); // Expected: true
        System.out.println(circularDeque.insertFront(4)); // Expected: true
        System.out.println(circularDeque.getFront()); // Expected: 4
    }
}
