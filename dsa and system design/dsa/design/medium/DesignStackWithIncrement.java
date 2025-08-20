package design.medium;

/**
 * LeetCode 1381: Design a Stack with Increment Operation
 *
 * Description:
 * Design a stack which supports the following operations:
 * - CustomStack(int maxSize) Initializes the object with maxSize
 * - void push(int x) Adds x to the top of the stack if the stack hasn't reached
 * the maxSize
 * - int pop() Pops and returns the top of stack or -1 if the stack is empty
 * - void inc(int k, int val) Increments the bottom k elements of the stack by
 * val
 *
 * Input: maxSize, push(x), pop(), inc(k, val)
 * Output: void (for push/inc), int (for pop)
 *
 * Constraints:
 * - 1 <= maxSize <= 1000
 * - 1 <= x <= 1000
 * - 1 <= k <= 1000
 * - 0 <= val <= 100
 * - At most 1000 calls will be made to each method
 *
 * Solution Approaches:
 * 1. Array + Lazy Propagation (O(1) push/pop/inc, O(maxSize) space)
 * Steps:
 * a. Use array to store stack elements
 * b. Use lazy array to store increment values
 * c. Apply increments during pop operation
 * Time: O(1) for all operations
 * Space: O(maxSize)
 * - Example: push(1), push(2), inc(2,100), pop() → 102, pop() → 101
 * 2. Array + Direct Increment (O(k) inc, O(1) push/pop, O(maxSize) space)
 * Steps:
 * a. Use array to store stack elements
 * b. Directly increment bottom k elements
 * Time: O(1) push/pop, O(k) inc
 * Space: O(maxSize)
 */
public class DesignStackWithIncrement {
    private int[] stack;
    private int[] lazy;
    private int top;
    private int maxSize;

    public DesignStackWithIncrement(int maxSize) {
        this.maxSize = maxSize;
        this.stack = new int[maxSize];
        this.lazy = new int[maxSize];
        this.top = -1;
    }

    public void push(int x) {
        if (top < maxSize - 1) {
            stack[++top] = x;
        }
    }

    public int pop() {
        if (top == -1) {
            return -1;
        }

        int result = stack[top] + lazy[top];

        // Propagate lazy increment to the element below
        if (top > 0) {
            lazy[top - 1] += lazy[top];
        }

        lazy[top] = 0;
        top--;

        return result;
    }

    public void inc(int k, int val) {
        if (top >= 0) {
            int idx = Math.min(k - 1, top);
            lazy[idx] += val;
        }
    }

    public static void main(String[] args) {
        // Edge Case 1: Normal operations
        DesignStackWithIncrement stack1 = new DesignStackWithIncrement(3);
        stack1.push(1);
        stack1.push(2);
        stack1.inc(2, 100);
        System.out.println(stack1.pop()); // 102
        System.out.println(stack1.pop()); // 101

        // Edge Case 2: Push beyond capacity
        DesignStackWithIncrement stack2 = new DesignStackWithIncrement(2);
        stack2.push(1);
        stack2.push(2);
        stack2.push(3); // Should be ignored
        System.out.println(stack2.pop()); // 2

        // Edge Case 3: Pop from empty stack
        DesignStackWithIncrement stack3 = new DesignStackWithIncrement(3);
        System.out.println(stack3.pop()); // -1

        // Edge Case 4: Increment more than stack size
        DesignStackWithIncrement stack4 = new DesignStackWithIncrement(3);
        stack4.push(1);
        stack4.inc(5, 100); // Should increment only 1 element
        System.out.println(stack4.pop()); // 101

        // Edge Case 5: Increment empty stack
        DesignStackWithIncrement stack5 = new DesignStackWithIncrement(3);
        stack5.inc(2, 100); // Should do nothing

        // Edge Case 6: Multiple increments
        DesignStackWithIncrement stack6 = new DesignStackWithIncrement(3);
        stack6.push(1);
        stack6.push(2);
        stack6.inc(2, 100);
        stack6.inc(1, 50);
        System.out.println(stack6.pop()); // 102
        System.out.println(stack6.pop()); // 151

        // Edge Case 7: Capacity 1
        DesignStackWithIncrement stack7 = new DesignStackWithIncrement(1);
        stack7.push(1);
        stack7.inc(1, 100);
        System.out.println(stack7.pop()); // 101

        // Edge Case 8: Large increments
        DesignStackWithIncrement stack8 = new DesignStackWithIncrement(3);
        stack8.push(1);
        stack8.inc(1, 100);
        stack8.inc(1, 100);
        System.out.println(stack8.pop()); // 201

        // Edge Case 9: Mixed operations
        DesignStackWithIncrement stack9 = new DesignStackWithIncrement(4);
        stack9.push(1);
        stack9.push(2);
        stack9.push(3);
        stack9.inc(3, 10);
        System.out.println(stack9.pop()); // 13
        stack9.inc(2, 5);
        System.out.println(stack9.pop()); // 17
        System.out.println(stack9.pop()); // 16

        // Edge Case 10: Zero increment
        DesignStackWithIncrement stack10 = new DesignStackWithIncrement(3);
        stack10.push(1);
        stack10.inc(1, 0);
        System.out.println(stack10.pop()); // 1
    }
}
