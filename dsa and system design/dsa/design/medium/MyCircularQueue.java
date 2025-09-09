package design.medium;

/**
 * LeetCode 622: Design Circular Queue
 * https://leetcode.com/problems/design-circular-queue/
 *
 * Description: Design your implementation of the circular queue.
 * 
 * Constraints:
 * - 1 <= k <= 1000
 * - 0 <= value <= 1000
 * - At most 3000 calls will be made to enQueue, deQueue, Front, Rear, isEmpty,
 * and isFull
 *
 * Follow-up:
 * - Can you solve it without using the built-in queue?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(k)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class MyCircularQueue {

    private int[] queue;
    private int head;
    private int tail;
    private int size;
    private int capacity;

    public MyCircularQueue(int k) {
        capacity = k;
        queue = new int[k];
        head = -1;
        tail = -1;
        size = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) {
            return false;
        }

        if (isEmpty()) {
            head = 0;
        }

        tail = (tail + 1) % capacity;
        queue[tail] = value;
        size++;

        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) {
            return false;
        }

        if (size == 1) {
            head = -1;
            tail = -1;
        } else {
            head = (head + 1) % capacity;
        }

        size--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : queue[head];
    }

    public int Rear() {
        return isEmpty() ? -1 : queue[tail];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public static void main(String[] args) {
        MyCircularQueue circularQueue = new MyCircularQueue(3);
        System.out.println(circularQueue.enQueue(1)); // Expected: true
        System.out.println(circularQueue.enQueue(2)); // Expected: true
        System.out.println(circularQueue.enQueue(3)); // Expected: true
        System.out.println(circularQueue.enQueue(4)); // Expected: false
        System.out.println(circularQueue.Rear()); // Expected: 3
        System.out.println(circularQueue.isFull()); // Expected: true
        System.out.println(circularQueue.deQueue()); // Expected: true
        System.out.println(circularQueue.enQueue(4)); // Expected: true
        System.out.println(circularQueue.Rear()); // Expected: 4
    }
}
