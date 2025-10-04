package stacks.medium;

import java.util.*;

/**
 * LeetCode 895: Maximum Frequency Stack
 * https://leetcode.com/problems/maximum-frequency-stack/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Implement FreqStack, a stack-like data structure that supports
 * push and pop operations. The pop operation removes and returns the most
 * frequent element in the stack.
 *
 * Constraints:
 * - 1 <= val <= 10^9
 * - At most 2 * 10^4 calls to push/pop
 * 
 * Follow-up Questions:
 * 1. Can you support peek?
 * 2. Can you optimize for large number of operations?
 * 3. Can you handle ties by recency?
 */
public class FreqStack {

    private Map<Integer, Integer> freq = new HashMap<>();
    private Map<Integer, Stack<Integer>> group = new HashMap<>();
    private int maxFreq = 0;

    public void push(int x) {
        int f = freq.getOrDefault(x, 0) + 1;
        freq.put(x, f);
        maxFreq = Math.max(maxFreq, f);
        group.computeIfAbsent(f, z -> new Stack<>()).push(x);
    }

    public int pop() {
        int x = group.get(maxFreq).pop();
        freq.put(x, freq.get(x) - 1);
        if (group.get(maxFreq).isEmpty())
            maxFreq--;
        return x;
    }

    // Follow-up 1: Support peek
    public int peek() {
        return group.get(maxFreq).peek();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FreqStack solution = new FreqStack();

        // Test case 1: Basic case
        solution.push(5);
        solution.push(7);
        solution.push(5);
        solution.push(7);
        solution.push(4);
        solution.push(5);
        System.out.println("Test 1 - Pop sequence Expected: 5,7,5,4");
        System.out.println(solution.pop());
        System.out.println(solution.pop());
        System.out.println(solution.pop());
        System.out.println(solution.pop());

        // Test case 2: Peek
        solution.push(7);
        System.out.println("\nTest 2 - Peek:");
        System.out.println(solution.peek());

        // Edge cases
        System.out.println("\nEdge cases:");
        FreqStack emptyStack = new FreqStack();
        emptyStack.push(1);
        System.out.println("Peek single: " + emptyStack.peek());
    }
}
