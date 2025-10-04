package design.medium;

/**
 * LeetCode 1381: Design a Stack With Increment Operation
 * https://leetcode.com/problems/design-a-stack-with-increment-operation/
 *
 * Description: Design a stack which supports the following operations.
 * 
 * Constraints:
 * - 1 <= maxSize <= 1000
 * - 1 <= x <= 1000
 * - 1 <= k <= 1000
 * - 0 <= val <= 100
 * - At most 1000 calls will be made to each method
 *
 * Follow-up:
 * - Can you make increment operation O(1)?
 * 
 * Time Complexity: O(1) for push/pop/increment
 * Space Complexity: O(maxSize)
 * 
 * Company Tags: Google, Amazon
 */
public class CustomStack {

    private int[] stack;
    private int[] increments;
    private int top;
    private int maxSize;

    public CustomStack(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[maxSize];
        increments = new int[maxSize];
        top = -1;
    }

    public void push(int x) {
        if (top < maxSize - 1) {
            top++;
            stack[top] = x;
        }
    }

    public int pop() {
        if (top == -1) {
            return -1;
        }

        int result = stack[top] + increments[top];

        // Propagate increment to the element below
        if (top > 0) {
            increments[top - 1] += increments[top];
        }

        increments[top] = 0;
        top--;

        return result;
    }

    public void increment(int k, int val) {
        int limit = Math.min(k - 1, top);
        if (limit >= 0) {
            increments[limit] += val;
        }
    }

    // Alternative implementation - Simple approach (O(k) increment)
    static class CustomStackSimple {
        private int[] stack;
        private int top;
        private int maxSize;

        public CustomStackSimple(int maxSize) {
            this.maxSize = maxSize;
            stack = new int[maxSize];
            top = -1;
        }

        public void push(int x) {
            if (top < maxSize - 1) {
                stack[++top] = x;
            }
        }

        public int pop() {
            return top == -1 ? -1 : stack[top--];
        }

        public void increment(int k, int val) {
            int limit = Math.min(k, top + 1);
            for (int i = 0; i < limit; i++) {
                stack[i] += val;
            }
        }
    }

    public static void main(String[] args) {
        CustomStack customStack = new CustomStack(3);
        customStack.push(1);
        customStack.push(2);
        System.out.println(customStack.pop()); // Expected: 2
        customStack.push(2);
        customStack.push(3);
        customStack.push(4);
        customStack.increment(5, 100);
        customStack.increment(2, 100);
        System.out.println(customStack.pop()); // Expected: 103
        System.out.println(customStack.pop()); // Expected: 202
        System.out.println(customStack.pop()); // Expected: 201
        System.out.println(customStack.pop()); // Expected: -1
    }
}
