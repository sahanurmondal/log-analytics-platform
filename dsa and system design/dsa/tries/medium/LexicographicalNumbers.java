package tries.medium;

import java.util.List;
import java.util.ArrayList;

/**
 * LeetCode 386: Lexicographical Numbers
 * https://leetcode.com/problems/lexicographical-numbers/
 *
 * Description:
 * Given an integer n, return all the numbers in the range [1, n] sorted in
 * lexicographical order.
 *
 * Constraints:
 * - 1 <= n <= 5 * 10^4
 *
 * Follow-up:
 * - Can you solve it using a trie-like approach?
 * - Can you do it iteratively without recursion?
 * - Can you optimize memory usage?
 */
public class LexicographicalNumbers {
    public List<Integer> lexicalOrder(int n) {
        List<Integer> result = new ArrayList<>();
        dfs(n, 0, result);
        return result;
    }

    private void dfs(int n, int curr, List<Integer> result) {
        if (curr > n) return;
        if (curr > 0) result.add(curr);
        for (int i = 0; i <= 9; i++) {
            if (curr * 10 + i > 0 && curr * 10 + i <= n) {
                dfs(n, curr * 10 + i, result);
            }
        }
    }

    public static void main(String[] args) {
        LexicographicalNumbers solution = new LexicographicalNumbers();

        System.out.println(solution.lexicalOrder(13));
        // [1,10,11,12,13,2,3,4,5,6,7,8,9]

        System.out.println(solution.lexicalOrder(2));
        // [1,2]

        // Edge Case: Single digit
        System.out.println(solution.lexicalOrder(9));
        // [1,2,3,4,5,6,7,8,9]

        // Edge Case: Power of 10
        System.out.println(solution.lexicalOrder(100));
        // [1,10,100,11,12,...,19,2,20,21,...] (first few elements)

        // Edge Case: Large n
        System.out.println(solution.lexicalOrder(1000).subList(0, 10));
        // [1,10,100,1000,1001,1002,1003,1004,1005,1006]

        // Edge Case: n = 1
        System.out.println(solution.lexicalOrder(1)); // [1]

        // Edge Case: Two digits
        System.out.println(solution.lexicalOrder(25));
        // [1,10,11,12,13,14,15,16,17,18,19,2,20,21,22,23,24,25,3,4,5,6,7,8,9]
    }
}
