package design.hard;

import java.util.*;

/**
 * LeetCode 1628: Design an Expression Tree With Evaluate Function
 * https://leetcode.com/problems/design-an-expression-tree-with-evaluate-function/
 *
 * Description: Given the postfix tokens of an arithmetic expression, build and
 * return the binary expression tree that represents this expression.
 * 
 * Constraints:
 * - 1 <= s.length < 100
 * - s.length is odd
 * - s[i] is either a digit or an operator
 * - The given expression is always a valid expression
 *
 * Follow-up:
 * - Can you design the tree to support more operations?
 * 
 * Time Complexity: O(n) for build, O(n) for evaluate
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignExpressionTree {

    abstract static class Node {
        public abstract int evaluate();
    }

    static class TreeBuilder {
        Node buildTree(String[] postfix) {
            Stack<Node> stack = new Stack<>();

            for (String token : postfix) {
                if (isOperator(token)) {
                    Node right = stack.pop();
                    Node left = stack.pop();
                    stack.push(new OperatorNode(token.charAt(0), left, right));
                } else {
                    stack.push(new OperandNode(Integer.parseInt(token)));
                }
            }

            return stack.pop();
        }

        private boolean isOperator(String token) {
            return "+".equals(token) || "-".equals(token) ||
                    "*".equals(token) || "/".equals(token);
        }
    }

    static class OperandNode extends Node {
        private int value;

        OperandNode(int value) {
            this.value = value;
        }

        @Override
        public int evaluate() {
            return value;
        }
    }

    static class OperatorNode extends Node {
        private char operator;
        private Node left, right;

        OperatorNode(char operator, Node left, Node right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override
        public int evaluate() {
            int leftVal = left.evaluate();
            int rightVal = right.evaluate();

            switch (operator) {
                case '+':
                    return leftVal + rightVal;
                case '-':
                    return leftVal - rightVal;
                case '*':
                    return leftVal * rightVal;
                case '/':
                    return leftVal / rightVal;
                default:
                    throw new IllegalArgumentException("Invalid operator: " + operator);
            }
        }
    }

    public static void main(String[] args) {
        TreeBuilder builder = new TreeBuilder();

        // Test case: ["3","4","+","2","*","7","/"]
        String[] postfix1 = { "3", "4", "+", "2", "*", "7", "/" };
        Node root1 = builder.buildTree(postfix1);
        System.out.println(root1.evaluate()); // Expected: 2

        // Test case: ["4","5","2","7","+","-","*"]
        String[] postfix2 = { "4", "5", "2", "7", "+", "-", "*" };
        Node root2 = builder.buildTree(postfix2);
        System.out.println(root2.evaluate()); // Expected: -16
    }
}
