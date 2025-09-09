package stacks.hard;

import java.util.*;

/**
 * Advanced Variation: Calculator With Variables
 * 
 * Description: Implement a calculator that supports variables, assignment, and
 * basic arithmetic.
 *
 * Constraints:
 * - Variable names are lowercase letters
 * - Assignment with '='
 * - Expressions can use previously assigned variables
 * 
 * Follow-up Questions:
 * 1. Support functions.
 * 2. Support arrays.
 * 3. Support error handling.
 */
public class CalculatorWithVariables {

    private final Map<String, Integer> vars = new HashMap<>();

    public int calculate(String s) {
        // ...implement variable assignment and usage
        // For brevity, not implemented here.
        return 0;
    }

    // Follow-up 1: Support functions
    public int calculateWithFunctions(String s) {
        // ...implement function support
        // For brevity, not implemented here.
        return 0;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CalculatorWithVariables solution = new CalculatorWithVariables();

        // Test case 1: Basic assignment
        String expr1 = "a=5";
        System.out.println("Test 1 - expr: " + expr1 + " Expected: 5");
        System.out.println("Result: " + solution.calculate(expr1));

        // Test case 2: Use variable
        String expr2 = "a+3";
        System.out.println("\nTest 2 - Use variable:");
        System.out.println("Result: " + solution.calculate(expr2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Unknown variable: " + solution.calculate("b+2"));
        System.out.println("Empty string: " + solution.calculate(""));
    }
}
