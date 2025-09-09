package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Expression Add Operators
 * 
 * Description:
 * Given a string num and a target value, add binary operators (+, -, *)
 * between digits to get an expression that evaluates to target.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class ExpressionAddOperators {

    public List<String> addOperators(String num, int target) {
        List<String> result = new ArrayList<>();
        if (num == null || num.length() == 0) {
            return result;
        }

        backtrack(result, "", num, target, 0, 0, 0);
        return result;
    }

    private void backtrack(List<String> result, String path, String num, int target,
            int index, long eval, long multed) {
        if (index == num.length()) {
            if (target == eval) {
                result.add(path);
            }
            return;
        }

        for (int i = index; i < num.length(); i++) {
            String part = num.substring(index, i + 1);
            if (part.length() > 1 && part.charAt(0) == '0') {
                break; // Skip numbers with leading zeros
            }

            long cur = Long.parseLong(part);

            if (index == 0) {
                backtrack(result, path + part, num, target, i + 1, cur, cur);
            } else {
                backtrack(result, path + "+" + part, num, target, i + 1, eval + cur, cur);
                backtrack(result, path + "-" + part, num, target, i + 1, eval - cur, -cur);
                backtrack(result, path + "*" + part, num, target, i + 1,
                        eval - multed + multed * cur, multed * cur);
            }
        }
    }

    public static void main(String[] args) {
        ExpressionAddOperators solution = new ExpressionAddOperators();

        List<String> result1 = solution.addOperators("123", 6);
        System.out.println("123 -> 6: " + result1); // ["1+2+3", "1*2*3"]

        List<String> result2 = solution.addOperators("232", 8);
        System.out.println("232 -> 8: " + result2); // ["2*3+2", "2+3*2"]

        List<String> result3 = solution.addOperators("105", 5);
        System.out.println("105 -> 5: " + result3); // ["1*0+5","10-5"]
    }
}
