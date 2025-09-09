package stacks.hard;

import java.util.*;

/**
 * LeetCode 726: Number of Atoms
 * https://leetcode.com/problems/number-of-atoms/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a chemical formula, return the count of each atom.
 *
 * Constraints:
 * - 1 <= formula.length <= 1000
 * - formula consists of uppercase/lowercase letters, digits, and parentheses
 * 
 * Follow-up Questions:
 * 1. Can you handle nested parentheses efficiently?
 * 2. Can you output atoms in lexicographical order?
 * 3. Can you handle invalid formulas?
 */
public class NumberOfAtoms {

    // Approach 1: Stack-based parsing
    public String countOfAtoms(String formula) {
        Stack<Map<String, Integer>> stack = new Stack<>();
        stack.push(new HashMap<>());
        int i = 0, n = formula.length();
        while (i < n) {
            if (formula.charAt(i) == '(') {
                stack.push(new HashMap<>());
                i++;
            } else if (formula.charAt(i) == ')') {
                i++;
                int start = i, mul = 1;
                while (i < n && Character.isDigit(formula.charAt(i)))
                    i++;
                if (i > start)
                    mul = Integer.parseInt(formula.substring(start, i));
                Map<String, Integer> top = stack.pop();
                for (String atom : top.keySet()) {
                    stack.peek().put(atom, stack.peek().getOrDefault(atom, 0) + top.get(atom) * mul);
                }
            } else {
                int start = i;
                i++;
                while (i < n && Character.isLowerCase(formula.charAt(i)))
                    i++;
                String atom = formula.substring(start, i);
                start = i;
                while (i < n && Character.isDigit(formula.charAt(i)))
                    i++;
                int count = start < i ? Integer.parseInt(formula.substring(start, i)) : 1;
                stack.peek().put(atom, stack.peek().getOrDefault(atom, 0) + count);
            }
        }
        Map<String, Integer> map = stack.pop();
        TreeMap<String, Integer> sorted = new TreeMap<>(map);
        StringBuilder sb = new StringBuilder();
        for (String atom : sorted.keySet()) {
            sb.append(atom);
            if (sorted.get(atom) > 1)
                sb.append(sorted.get(atom));
        }
        return sb.toString();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        NumberOfAtoms solution = new NumberOfAtoms();

        // Test case 1: Basic case
        String formula1 = "H2O";
        System.out.println("Test 1 - formula: " + formula1 + " Expected: H2O");
        System.out.println("Result: " + solution.countOfAtoms(formula1));

        // Test case 2: Nested parentheses
        String formula2 = "Mg(OH)2";
        System.out.println("\nTest 2 - Nested parentheses:");
        System.out.println("Result: " + solution.countOfAtoms(formula2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single atom: " + solution.countOfAtoms("O"));
        System.out.println("Empty formula: " + solution.countOfAtoms(""));
    }
}
