package stacks.medium;

import java.util.*;

/**
 * LeetCode 682: Baseball Game
 * https://leetcode.com/problems/baseball-game/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a list of strings representing operations, return the sum of scores after performing all operations.
 *
 * Constraints:
 * - 1 <= ops.length <= 1000
 * - ops[i] is an integer, "C", "D", or "+"
 * - For operation "+", there will always be at least 2 previous scores
 * - For operations "C" and "D", there will always be at least 1 previous score
 *
 * Operations:
 * - Integer (one round's score): Directly represents the number of points
 * - "+" (one round's score): Sum of the last two valid scores
 * - "D" (one round's score): Double the last valid score
 * - "C" (cancel): Remove the last valid score
 *
 * Follow-up:
 * - Can you handle additional operations like multiplication?
 * - Can you extend to support undo/redo?
 */
public class BaseballGame {

    // Approach 1: Stack simulation
    public int calPoints(String[] ops) {
        Stack<Integer> stack = new Stack<>();
        for (String op : ops) {
            if (op.equals("C")) stack.pop();
            else if (op.equals("D")) stack.push(stack.peek() * 2);
            else if (op.equals("+")) stack.push(stack.peek() + stack.get(stack.size() - 2));
            else stack.push(Integer.parseInt(op));
        }
        int sum = 0;
        for (int score : stack) sum += score;
        return sum;
    }

    // Follow-up 1: Max score
    public int maxScore(String[] ops) {
        Stack<Integer> stack = new Stack<>();
        int max = Integer.MIN_VALUE;
        for (String op : ops) {
            if (op.equals("C")) stack.pop();
            else if (op.equals("D")) stack.push(stack.peek() * 2);
            else if (op.equals("+")) stack.push(stack.peek() + stack.get(stack.size() - 2));
            else stack.push(Integer.parseInt(op));
            if (!stack.isEmpty()) max = Math.max(max, stack.peek());
        }
        return max;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        BaseballGame solution = new BaseballGame();

        // Test case 1: Basic case
        String[] ops1 = {"5","2","C","D","+"};
        System.out.println("Test 1 - ops: " + Arrays.toString(ops1) + " Expected: 30");
        System.out.println("Result: " + solution.calPoints(ops1));

        // Test case 2: Max score
        System.out.println("\nTest 2 - Max score:");
        System.out.println(solution.maxScore(ops1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty ops: " + solution.calPoints(new String[]{}));
        System.out.println("Single op: " + solution.calPoints(new String[]{"10"}));
    }
}
