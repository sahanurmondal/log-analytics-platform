package recursion.hard;

/**
 * LeetCode 301: Remove Invalid Parentheses
 * https://leetcode.com/problems/remove-invalid-parentheses/
 *
 * Companies: Google, Facebook
 * Frequency: High
 *
 * Description:
 * Remove the minimum number of invalid parentheses to make the input string
 * valid.
 *
 * Constraints:
 * - 1 <= s.length <= 30
 *
 * Follow-ups:
 * 1. Can you return all possible results?
 * 2. Can you optimize for large strings?
 * 3. Can you handle other types of brackets?
 */
public class RemoveInvalidParentheses {
    public java.util.List<String> removeInvalidParentheses(String s) {
        java.util.List<String> res = new java.util.ArrayList<>();
        dfs(s, 0, 0, new char[] { '(', ')' }, res);
        return res;
    }

    private void dfs(String s, int last_i, int last_j, char[] par, java.util.List<String> res) {
        for (int stack = 0, i = last_i; i < s.length(); ++i) {
            if (s.charAt(i) == par[0])
                stack++;
            if (s.charAt(i) == par[1])
                stack--;
            if (stack >= 0)
                continue;
            for (int j = last_j; j <= i; ++j) {
                if (s.charAt(j) == par[1] && (j == last_j || s.charAt(j - 1) != par[1]))
                    dfs(s.substring(0, j) + s.substring(j + 1), i, j, par, res);
            }
            return;
        }
        String reversed = new StringBuilder(s).reverse().toString();
        if (par[0] == '(')
            dfs(reversed, 0, 0, new char[] { ')', '(' }, res);
        else
            res.add(reversed);
    }

    // Follow-up 1: Return all possible results (already handled above)
    // Follow-up 2: Optimize for large strings (not needed for n <= 30)
    // Follow-up 3: Handle other types of brackets
    public java.util.List<String> removeInvalidBrackets(String s, char open, char close) {
        java.util.List<String> res = new java.util.ArrayList<>();
        dfs(s, 0, 0, new char[] { open, close }, res);
        return res;
    }

    public static void main(String[] args) {
        RemoveInvalidParentheses solution = new RemoveInvalidParentheses();
        System.out.println(solution.removeInvalidParentheses("()())()")); // ["()()()","(())()"]
        System.out.println(solution.removeInvalidBrackets("{a{b}}}", '{', '}')); // ["{a{b}}"]
    }
}
