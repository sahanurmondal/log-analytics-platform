package backtracking.easy;

import java.util.*;

/**
 * LeetCode 784: Letter Case Permutation
 * https://leetcode.com/problems/letter-case-permutation/
 *
 * Description: Given a string s, you can transform every letter individually to
 * be lowercase or uppercase to create another string.
 * Return a list of all possible strings we could create. Return the output in
 * any order.
 * 
 * Constraints:
 * - 1 <= s.length <= 12
 * - s consists of lowercase English letters, uppercase English letters, and
 * digits
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What about using bit manipulation?
 * 
 * Time Complexity: O(N * 2^L) where L is number of letters
 * Space Complexity: O(N * 2^L)
 * 
 * Company Tags: Google, Facebook
 */
public class LetterCasePermutation {

    public List<String> letterCasePermutation(String s) {
        List<String> result = new ArrayList<>();
        backtrack(s, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String s, int index, StringBuilder current, List<String> result) {
        if (index == s.length()) {
            result.add(current.toString());
            return;
        }

        char c = s.charAt(index);
        if (Character.isLetter(c)) {
            // Try lowercase
            current.append(Character.toLowerCase(c));
            backtrack(s, index + 1, current, result);
            current.deleteCharAt(current.length() - 1);

            // Try uppercase
            current.append(Character.toUpperCase(c));
            backtrack(s, index + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        } else {
            // Digit - keep as is
            current.append(c);
            backtrack(s, index + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // Alternative solution - Iterative
    public List<String> letterCasePermutationIterative(String s) {
        List<String> result = new ArrayList<>();
        result.add(s);

        for (int i = 0; i < s.length(); i++) {
            if (Character.isLetter(s.charAt(i))) {
                int size = result.size();
                for (int j = 0; j < size; j++) {
                    String str = result.get(j);
                    char[] chars = str.toCharArray();
                    chars[i] = Character.isLowerCase(chars[i]) ? Character.toUpperCase(chars[i])
                            : Character.toLowerCase(chars[i]);
                    result.add(new String(chars));
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        LetterCasePermutation solution = new LetterCasePermutation();

        System.out.println(solution.letterCasePermutation("a1b2")); // Expected: ["a1b2","a1B2","A1b2","A1B2"]
        System.out.println(solution.letterCasePermutation("3z4")); // Expected: ["3z4","3Z4"]
        System.out.println(solution.letterCasePermutation("12345")); // Expected: ["12345"]
        System.out.println(solution.letterCasePermutation("C")); // Expected: ["c","C"]
    }
}
