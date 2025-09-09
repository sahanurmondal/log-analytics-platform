package stacks.medium;

import java.util.*;

/**
 * LeetCode 20: Valid Parentheses
 * https://leetcode.com/problems/valid-parentheses/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, Bloomberg
 * Frequency: Very High (Asked in 700+ interviews)
 *
 * Description:
 * Given a string s containing just the characters '(', ')', '{', '}', '[' and
 * ']',
 * determine if the input string is valid.
 * 
 * An input string is valid if:
 * 1. Open brackets must be closed by the same type of brackets.
 * 2. Open brackets must be closed in the correct order.
 * 3. Every close bracket has a corresponding open bracket of the same type.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - s consists of parentheses only '()[]{}'.
 * 
 * Follow-up Questions:
 * 1. What if there are other characters mixed in?
 * 2. Can you handle nested structures with different weights?
 * 3. What if you need to return the positions of invalid brackets?
 * 4. How would you handle custom bracket pairs?
 */
public class ValidParentheses {

    // Approach 1: Stack with HashMap - O(n) time, O(n) space
    public boolean isValid(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;
        }

        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '[');

        for (char c : s.toCharArray()) {
            if (pairs.containsKey(c)) {
                // Closing bracket
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            } else {
                // Opening bracket
                stack.push(c);
            }
        }

        return stack.isEmpty();
    }

    // Approach 2: Stack without HashMap - O(n) time, O(n) space
    public boolean isValidOptimized(String s) {
        if (s == null || s.length() % 2 != 0) {
            return false;
        }

        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                case '{':
                case '[':
                    stack.push(c);
                    break;
                case ')':
                    if (stack.isEmpty() || stack.pop() != '(')
                        return false;
                    break;
                case '}':
                    if (stack.isEmpty() || stack.pop() != '{')
                        return false;
                    break;
                case ']':
                    if (stack.isEmpty() || stack.pop() != '[')
                        return false;
                    break;
                default:
                    return false; // Invalid character
            }
        }

        return stack.isEmpty();
    }

    // Approach 3: Counter-based (for single type of brackets) - O(n) time, O(1)
    // space
    public boolean isValidSingleType(String s, char open, char close) {
        int count = 0;

        for (char c : s.toCharArray()) {
            if (c == open) {
                count++;
            } else if (c == close) {
                count--;
                if (count < 0) {
                    return false; // More closing than opening
                }
            }
        }

        return count == 0;
    }

    // Approach 4: String replacement (inefficient but interesting) - O(n²) time,
    // O(n) space
    public boolean isValidReplacement(String s) {
        while (s.contains("()") || s.contains("{}") || s.contains("[]")) {
            s = s.replace("()", "").replace("{}", "").replace("[]", "");
        }
        return s.isEmpty();
    }

    // Follow-up 1: Valid parentheses with other characters
    public boolean isValidWithOtherChars(String s) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '[');

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else if (pairs.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
            }
            // Ignore other characters
        }

        return stack.isEmpty();
    }

    // Follow-up 2: Weighted brackets (nested structures have different costs)
    public boolean isValidWeighted(String s, Map<Character, Integer> weights) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '[');

        int totalWeight = 0;

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
                totalWeight += weights.getOrDefault(c, 1);
            } else if (pairs.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != pairs.get(c)) {
                    return false;
                }
                totalWeight -= weights.getOrDefault(pairs.get(c), 1);
            }
        }

        return stack.isEmpty() && totalWeight == 0;
    }

    // Follow-up 3: Return positions of invalid brackets
    public List<Integer> findInvalidPositions(String s) {
        List<Integer> result = new ArrayList<>();
        Stack<Integer> stack = new Stack<>(); // Store positions of opening brackets
        Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '[');

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '(' || c == '{' || c == '[') {
                stack.push(i);
            } else if (pairs.containsKey(c)) {
                if (stack.isEmpty()) {
                    result.add(i); // Unmatched closing bracket
                } else {
                    char opening = s.charAt(stack.peek());
                    if (opening == pairs.get(c)) {
                        stack.pop(); // Valid pair
                    } else {
                        result.add(i); // Mismatched closing bracket
                    }
                }
            }
        }

        // Add positions of unmatched opening brackets
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        Collections.sort(result);
        return result;
    }

    // Follow-up 4: Custom bracket pairs
    public boolean isValidCustom(String s, Map<Character, Character> customPairs) {
        Stack<Character> stack = new Stack<>();
        Set<Character> openingBrackets = new HashSet<>(customPairs.values());

        for (char c : s.toCharArray()) {
            if (openingBrackets.contains(c)) {
                stack.push(c);
            } else if (customPairs.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != customPairs.get(c)) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }

    // Advanced: Balanced brackets with minimum operations
    public int minOperationsToBalance(String s) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> pairs = Map.of(
                ')', '(',
                '}', '{',
                ']', '[');

        int operations = 0;

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);
            } else if (pairs.containsKey(c)) {
                if (stack.isEmpty()) {
                    operations++; // Need to insert opening bracket
                    stack.push(pairs.get(c));
                } else if (stack.peek() == pairs.get(c)) {
                    stack.pop(); // Valid pair
                } else {
                    operations++; // Need to change one bracket
                    stack.pop();
                }
            }
        }

        // Each unmatched opening bracket needs a closing bracket
        operations += stack.size();

        return operations;
    }

    // Advanced: Generate all valid parentheses of length n (LeetCode 22)
    public List<String> generateParenthesis(int n) {
        List<String> result = new ArrayList<>();
        generateHelper(result, "", 0, 0, n);
        return result;
    }

    private void generateHelper(List<String> result, String current, int open, int close, int max) {
        if (current.length() == max * 2) {
            result.add(current);
            return;
        }

        if (open < max) {
            generateHelper(result, current + "(", open + 1, close, max);
        }
        if (close < open) {
            generateHelper(result, current + ")", open, close + 1, max);
        }
    }

    // Helper: Check if character is opening bracket
    private boolean isOpeningBracket(char c) {
        return c == '(' || c == '{' || c == '[';
    }

    // Helper: Check if character is closing bracket
    private boolean isClosingBracket(char c) {
        return c == ')' || c == '}' || c == ']';
    }

    // Helper: Get matching bracket
    private char getMatchingBracket(char c) {
        switch (c) {
            case '(':
                return ')';
            case '{':
                return '}';
            case '[':
                return ']';
            case ')':
                return '(';
            case '}':
                return '{';
            case ']':
                return '[';
            default:
                return '\0';
        }
    }

    // Helper: Performance comparison
    public Map<String, Long> comparePerformance(String s) {
        Map<String, Long> results = new HashMap<>();

        // Test stack with HashMap
        long start = System.nanoTime();
        isValid(s);
        results.put("Stack+HashMap", System.nanoTime() - start);

        // Test optimized stack
        start = System.nanoTime();
        isValidOptimized(s);
        results.put("OptimizedStack", System.nanoTime() - start);

        // Test replacement (only for short strings)
        if (s.length() < 100) {
            start = System.nanoTime();
            isValidReplacement(s);
            results.put("Replacement", System.nanoTime() - start);
        }

        return results;
    }

    public static void main(String[] args) {
        ValidParentheses solution = new ValidParentheses();

        // Test Case 1: Valid parentheses
        System.out.println("=== Test Case 1: Valid Parentheses ===");
        String[] validCases = {
                "()",
                "()[]{}",
                "{[()]}",
                "((()))",
                "{[()]()[{}]}"
        };

        for (String test : validCases) {
            boolean result = solution.isValid(test);
            System.out.println("\"" + test + "\" -> " + result);
        }

        // Test Case 2: Invalid parentheses
        System.out.println("\n=== Test Case 2: Invalid Parentheses ===");
        String[] invalidCases = {
                "(",
                ")",
                "([)]",
                "(((",
                ")))",
                "{[}]",
                "(])"
        };

        for (String test : invalidCases) {
            boolean result = solution.isValid(test);
            System.out.println("\"" + test + "\" -> " + result);
        }

        // Test Case 3: Compare approaches
        System.out.println("\n=== Test Case 3: Compare Approaches ===");
        String testStr = "{[()()()]}(){}";

        boolean result1 = solution.isValid(testStr);
        boolean result2 = solution.isValidOptimized(testStr);
        boolean result3 = solution.isValidReplacement(testStr);

        System.out.println("Input: \"" + testStr + "\"");
        System.out.println("HashMap approach: " + result1);
        System.out.println("Optimized approach: " + result2);
        System.out.println("Replacement approach: " + result3);
        System.out.println("All consistent: " + (result1 == result2 && result2 == result3));

        // Follow-up 1: With other characters
        System.out.println("\n=== Follow-up 1: With Other Characters ===");
        String mixedStr = "a(b[c]d)e{f}g";
        boolean mixedResult = solution.isValidWithOtherChars(mixedStr);
        System.out.println("\"" + mixedStr + "\" -> " + mixedResult);

        // Follow-up 2: Weighted brackets
        System.out.println("\n=== Follow-up 2: Weighted Brackets ===");
        Map<Character, Integer> weights = Map.of(
                '(', 1, '{', 2, '[', 3);
        String weightedStr = "{[()]}";
        boolean weightedResult = solution.isValidWeighted(weightedStr, weights);
        System.out.println("\"" + weightedStr + "\" with weights -> " + weightedResult);

        // Follow-up 3: Invalid positions
        System.out.println("\n=== Follow-up 3: Invalid Positions ===");
        String invalidStr = "([)]";
        List<Integer> invalidPos = solution.findInvalidPositions(invalidStr);
        System.out.println("\"" + invalidStr + "\" invalid positions: " + invalidPos);

        String invalidStr2 = "((())";
        List<Integer> invalidPos2 = solution.findInvalidPositions(invalidStr2);
        System.out.println("\"" + invalidStr2 + "\" invalid positions: " + invalidPos2);

        // Follow-up 4: Custom brackets
        System.out.println("\n=== Follow-up 4: Custom Brackets ===");
        Map<Character, Character> customPairs = Map.of(
                '>', '<',
                '|', '|' // Same opening and closing
        );
        String customStr = "<|text|>";
        boolean customResult = solution.isValidCustom(customStr, customPairs);
        System.out.println("\"" + customStr + "\" with custom pairs -> " + customResult);

        // Advanced: Minimum operations
        System.out.println("\n=== Advanced: Minimum Operations ===");
        String[] operationTests = {
                "(((",
                ")))",
                "([)]",
                "(())",
                ""
        };

        for (String test : operationTests) {
            int ops = solution.minOperationsToBalance(test);
            System.out.println("\"" + test + "\" needs " + ops + " operations");
        }

        // Advanced: Generate valid parentheses
        System.out.println("\n=== Advanced: Generate Valid Parentheses ===");
        List<String> generated = solution.generateParenthesis(3);
        System.out.println("Valid parentheses of length 6: " + generated);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        StringBuilder largeTest = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeTest.append("(){}[]");
        }

        Map<String, Long> performance = solution.comparePerformance(largeTest.toString());
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1_000_000.0 + " ms"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Empty string
        System.out.println("Empty string: " + solution.isValid(""));

        // Single character
        System.out.println("Single '(': " + solution.isValid("("));
        System.out.println("Single ')': " + solution.isValid(")"));

        // Very long valid string
        String longValid = "()".repeat(1000);
        System.out.println("Long valid (2000 chars): " + solution.isValid(longValid));

        // All opening brackets
        String allOpening = "((({{{[[[";
        System.out.println("All opening: " + solution.isValid(allOpening));

        // All closing brackets
        String allClosing = "]]]}})))";
        System.out.println("All closing: " + solution.isValid(allClosing));

        // Single type validation
        System.out.println("\n=== Single Type Validation ===");
        System.out.println("Single type '()()()': " + solution.isValidSingleType("()()()", '(', ')'));
        System.out.println("Single type '(()': " + solution.isValidSingleType("(()", '(', ')'));
        System.out.println("Single type '())': " + solution.isValidSingleType("())", '(', ')'));
    }
}
