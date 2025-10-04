package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Basic Calculator
 * 
 * Description:
 * Implement a basic calculator to evaluate a simple expression string.
 * The expression string may contain +, -, (, ), and non-negative integers.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class BasicCalculator {

    public int calculate(String s) {
        Stack<Integer> stack = new Stack<>();
        int result = 0;
        int number = 0;
        int sign = 1;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                number = number * 10 + (c - '0');
            }

            if (c == '+' || c == '-' || c == ')' || i == s.length() - 1) {
                result += sign * number;
                number = 0;
                sign = (c == '+') ? 1 : -1;
            }

            if (c == '(') {
                stack.push(result);
                stack.push(sign);
                result = 0;
                sign = 1;
            }

            if (c == ')') {
                result = result * stack.pop() + stack.pop();
            }
        }

        return result;
    }

    // Extended version with *, /
    public int calculateAdvanced(String s) {
        Stack<Integer> stack = new Stack<>();
        int num = 0;
        char operator = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                num = num * 10 + (c - '0');
            }

            if (c == '+' || c == '-' || c == '*' || c == '/' || i == s.length() - 1) {
                if (operator == '+') {
                    stack.push(num);
                } else if (operator == '-') {
                    stack.push(-num);
                } else if (operator == '*') {
                    stack.push(stack.pop() * num);
                } else if (operator == '/') {
                    stack.push(stack.pop() / num);
                }

                operator = c;
                num = 0;
            }
        }

        int result = 0;
        while (!stack.isEmpty()) {
            result += stack.pop();
        }

        return result;
    }

    public static void main(String[] args) {
        BasicCalculator solution = new BasicCalculator();

        System.out.println(solution.calculate("1 + 1")); // 2
        System.out.println(solution.calculate(" 2-1 + 2 ")); // 3
        System.out.println(solution.calculate("(1+(4+5+2)-3)+(6+8)")); // 23

        System.out.println(solution.calculateAdvanced("3+2*2")); // 7
        System.out.println(solution.calculateAdvanced(" 3/2 ")); // 1
        System.out.println(solution.calculateAdvanced(" 3+5 / 2 ")); // 5
    }
}
