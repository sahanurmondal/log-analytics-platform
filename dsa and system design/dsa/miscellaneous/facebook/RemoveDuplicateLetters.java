package miscellaneous.facebook;

import java.util.*;

/**
 * LeetCode 316: Remove Duplicate Letters
 * https://leetcode.com/problems/remove-duplicate-letters/
 *
 * Description:
 * Given a string s, remove duplicate letters so that every letter appears once
 * and only once.
 * You must make sure your result is the smallest in lexicographical order among
 * all possible results.
 * 
 * Company: Facebook/Meta
 * Difficulty: Medium
 * Asked: Frequently in 2023-2024
 * 
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - s consists of lowercase English letters
 */
public class RemoveDuplicateLetters {

    public String removeDuplicateLetters(String s) {
        Map<Character, Integer> lastOccurrence = new HashMap<>();
        Set<Character> seen = new HashSet<>();
        Stack<Character> stack = new Stack<>();

        // Record last occurrence of each character
        for (int i = 0; i < s.length(); i++) {
            lastOccurrence.put(s.charAt(i), i);
        }

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (seen.contains(c))
                continue;

            // Remove characters that are lexicographically larger and will appear later
            while (!stack.isEmpty() && stack.peek() > c && lastOccurrence.get(stack.peek()) > i) {
                seen.remove(stack.pop());
            }

            stack.push(c);
            seen.add(c);
        }

        StringBuilder result = new StringBuilder();
        for (char c : stack) {
            result.append(c);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        RemoveDuplicateLetters solution = new RemoveDuplicateLetters();

        System.out.println(solution.removeDuplicateLetters("bcabc")); // "abc"
        System.out.println(solution.removeDuplicateLetters("cbacdcbc")); // "acdb"
        System.out.println(solution.removeDuplicateLetters("ecbacba")); // "eacb"
    }
}
