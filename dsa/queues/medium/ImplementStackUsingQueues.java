package queues.medium;

/**
 * LeetCode 225: Implement Stack using Queues
 * https://leetcode.com/problems/implement-stack-using-queues/
 *
 * Description:
 * Implement a last in first out (LIFO) stack using only two queues.
 *
 * Constraints:
 * - 1 <= x <= 9
 * - At most 100 calls will be made to push, pop, top, and empty
 * - All the calls to pop and top are valid
 *
 * Follow-up:
 * - Can you implement the stack using only one queue?
 * - Can you optimize the push or pop operation?
 */
public class ImplementStackUsingQueues {
    // Two queues for main solution
    private java.util.Queue<Integer> q1;
    private java.util.Queue<Integer> q2;
    // For follow-up: one queue
    private java.util.Queue<Integer> singleQ;

    public ImplementStackUsingQueues() {
        q1 = new java.util.LinkedList<>();
        q2 = new java.util.LinkedList<>();
        singleQ = new java.util.LinkedList<>();
    }

    // Main: push O(1), pop O(n)
    public void push(int x) {
        q1.offer(x);
        // Follow-up: one queue, push O(n)
        singleQ.offer(x);
        int size = singleQ.size();
        while (size-- > 1) {
            singleQ.offer(singleQ.poll());
        }
    }

    // Main: pop O(n)
    public int pop() {
        if (q1.isEmpty())
            return -1;
        while (q1.size() > 1) {
            q2.offer(q1.poll());
        }
        int val = q1.poll();
        java.util.Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;
        // Follow-up: one queue
        return val;
    }

    public int top() {
        if (q1.isEmpty())
            return -1;
        while (q1.size() > 1) {
            q2.offer(q1.poll());
        }
        int val = q1.peek();
        q2.offer(q1.poll());
        java.util.Queue<Integer> temp = q1;
        q1 = q2;
        q2 = temp;
        return val;
    }

    public boolean empty() {
        return q1.isEmpty();
    }

    public static void main(String[] args) {
        ImplementStackUsingQueues stack = new ImplementStackUsingQueues();
        stack.push(1);
        stack.push(2);
        System.out.println(stack.top()); // 2
        System.out.println(stack.pop()); // 2
        System.out.println(stack.empty()); // false
        // Edge Case: Empty stack operations
        stack.pop();
        System.out.println(stack.empty()); // true
        // Edge Case: Multiple operations
        stack.push(3);
        stack.push(4);
        stack.push(5);
        System.out.println(stack.pop()); // 5
        System.out.println(stack.top()); // 4
        // Follow-up: One queue implementation
        System.out.println("One queue implementation:");
        ImplementStackUsingQueues oneQStack = new ImplementStackUsingQueues();
        oneQStack.singleQ.offer(1);
        oneQStack.singleQ.offer(2);
        int size = oneQStack.singleQ.size();
        while (size-- > 1) {
            oneQStack.singleQ.offer(oneQStack.singleQ.poll());
        }
        System.out.println(oneQStack.singleQ.peek()); // 2
        System.out.println(oneQStack.singleQ.poll()); // 2
        System.out.println(oneQStack.singleQ.isEmpty()); // false
    }
}
