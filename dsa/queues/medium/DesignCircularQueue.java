package queues.medium;

/**
 * LeetCode 622: Design Circular Queue
 * https://leetcode.com/problems/design-circular-queue/
 *
 * Description:
 * Design your implementation of the circular queue.
 *
 * Constraints:
 * - 1 <= k <= 1000
 * - 0 <= value <= 1000
 * - At most 3000 calls will be made to enQueue, deQueue, Front, Rear, isEmpty,
 * and isFull
 *
 * ASCII Art:
 * Circular Queue (size=5):
 * 
 * Empty: [_][_][_][_][_]
 * ↑
 * front/rear
 * 
 * Add 10: [10][_][_][_][_]
 * ↑ ↑
 * front rear
 * 
 * Add 20: [10][20][_][_][_]
 * ↑ ↑
 * front rear
 *
 * Follow-up:
 * - Can you implement it without using extra space for count?
 * - Can you extend to support priority elements?
 */
/**
 * LeetCode 622: Design Circular Queue
 * https://leetcode.com/problems/design-circular-queue/
 *
 * Description:
 * Design your implementation of the circular queue.
 *
 * Constraints:
 * - 1 <= k <= 1000
 * - 0 <= value <= 1000
 * - At most 3000 calls will be made to enQueue, deQueue, Front, Rear, isEmpty,
 * and isFull
 *
 * ASCII Art:
 * Circular Queue (size=5):
 * 
 * Empty: [_][_][_][_][_]
 * ↑
 * front/rear
 * 
 * Add 10: [10][_][_][_][_]
 * ↑ ↑
 * front rear
 * 
 * Add 20: [10][20][_][_][_]
 * ↑ ↑
 * front rear
 *
 * Follow-up:
 * - Can you implement it without using extra space for count?
 * - Can you extend to support priority elements?
 */
public class DesignCircularQueue {
    private int[] queue;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public DesignCircularQueue(int k) {
        this.queue = new int[k];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
        this.capacity = k;
    }

    public boolean enQueue(int value) {
        if (isFull())
            return false;
        rear = (rear + 1) % capacity;
        queue[rear] = value;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty())
            return false;
        front = (front + 1) % capacity;
        size--;
        return true;
    }

    public int Front() {
        return isEmpty() ? -1 : queue[front];
    }

    public int Rear() {
        return isEmpty() ? -1 : queue[rear];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public static void main(String[] args) {
        DesignCircularQueue queue = new DesignCircularQueue(3);
        System.out.println(queue.enQueue(1)); // true
        System.out.println(queue.enQueue(2)); // true
        System.out.println(queue.enQueue(3)); // true
        System.out.println(queue.enQueue(4)); // false (queue is full)
        System.out.println(queue.Rear()); // 3
        System.out.println(queue.isFull()); // true
        System.out.println(queue.deQueue()); // true
        System.out.println(queue.enQueue(4)); // true
        System.out.println(queue.Rear()); // 4
    }
}
