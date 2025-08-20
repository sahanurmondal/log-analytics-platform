package stacks.medium;

import java.util.*;

/**
 * LeetCode 856: Score of Parentheses
 * https://leetcode.com/problems/score-of-parentheses/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a balanced parentheses string, compute the score based on
 * rules: "()" has score 1, AB has score A+B, and (A) has score 2*A.
 *
 * Constraints:
 * - 2 <= s.length <= 50
 * - s consists of '(' and ')'
 * 
 * Follow-up Questions:
 * 1. Can you handle unbalanced strings?
 * 2. Can you optimize for large strings?
 * 3. Can you return the score for each pair?
 */
public class ScoreOfParentheses {

    // Approach 1: Stack-based score calculation
    public int scoreOfParentheses(String s) {
        Stack<Integer> stack = new Stack<>();
        int score = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(score);
                score = 0;
            } else {
                score = stack.pop() + Math.max(2 * score, 1);
            }
        }
        return score;
    }

    // Follow-up 1: Score for each pair
    public List<Integer> scoreForEachPair(String s) {
        List<Integer> scores = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        int score = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(score);
                score = 0;
            } else {
                int prev = stack.pop();
                int pairScore = Math.max(2 * score, 1);
                scores.add(pairScore);
                score = prev + pairScore;
            }
        }
        return scores;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ScoreOfParentheses solution = new ScoreOfParentheses();

        // Test case 1: Basic case
        String s1 = "()";
        System.out.println("Test 1 - s: " + s1 + " Expected: 1");
        System.out.println("Result: " + solution.scoreOfParentheses(s1));

        // Test case 2: Nested
        String s2 = "(())";
        System.out.println("\nTest 2 - Nested:");
        System.out.println("Result: " + solution.scoreOfParentheses(s2));

        // Test case 3: Multiple pairs
        String s3 = "()()";
        System.out.println("\nTest 3 - Multiple pairs:");
        System.out.println("Result: " + solution.scoreOfParentheses(s3));

        // Test case 4: Score for each pair
        System.out.println("\nTest 4 - Score for each pair:");
        System.out.println(solution.scoreForEachPair(s2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: " + solution.scoreOfParentheses(""));
        System.out.println("Single pair: " + solution.scoreOfParentheses("()"));
    }
}
