package backtracking.medium;

import java.util.*;

/**
 * LeetCode 17: Letter Combinations of a Phone Number
 * https://leetcode.com/problems/letter-combinations-of-a-phone-number/
 *
 * Description: Given a string containing digits from 2-9 inclusive, return all
 * possible letter combinations
 * that the number could represent. Return the answer in any order.
 * 
 * Constraints:
 * - 0 <= digits.length <= 4
 * - digits[i] is a digit in the range ['2', '9']
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * - What if we need to find combinations with specific constraints?
 * 
 * Time Complexity: O(3^n * 4^m) where n is digits with 3 letters, m is digits
 * with 4 letters
 * Space Complexity: O(3^n * 4^m)
 * 
 * Algorithm:
 * 1. Backtracking: Build combinations character by character
 * 2. Iterative: Use queue to build combinations level by level
 * 3. DFS with mapping: Map digits to letters and explore all paths
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class LetterCombinationsPhoneNumber {

    private static final String[] MAPPING = {
            "", // 0
            "", // 1
            "abc", // 2
            "def", // 3
            "ghi", // 4
            "jkl", // 5
            "mno", // 6
            "pqrs", // 7
            "tuv", // 8
            "wxyz" // 9
    };

    // Main optimized solution - Backtracking
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty())
            return result;

        backtrack(digits, 0, new StringBuilder(), result);
        return result;
    }

    private void backtrack(String digits, int index, StringBuilder current, List<String> result) {
        if (index == digits.length()) {
            result.add(current.toString());
            return;
        }

        String letters = MAPPING[digits.charAt(index) - '0'];
        for (char letter : letters.toCharArray()) {
            current.append(letter);
            backtrack(digits, index + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }

    // Alternative solution - Iterative with Queue
    public List<String> letterCombinationsIterative(String digits) {
        List<String> result = new ArrayList<>();
        if (digits.isEmpty())
            return result;

        Queue<String> queue = new LinkedList<>();
        queue.offer("");

        for (char digit : digits.toCharArray()) {
            String letters = MAPPING[digit - '0'];
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                String current = queue.poll();
                for (char letter : letters.toCharArray()) {
                    queue.offer(current + letter);
                }
            }
        }

        result.addAll(queue);
        return result;
    }

    // Follow-up optimization - Using ArrayList multiplication
    public List<String> letterCombinationsOptimized(String digits) {
        if (digits.isEmpty())
            return new ArrayList<>();

        List<String> result = new ArrayList<>();
        result.add("");

        for (char digit : digits.toCharArray()) {
            String letters = MAPPING[digit - '0'];
            List<String> temp = new ArrayList<>();

            for (String combination : result) {
                for (char letter : letters.toCharArray()) {
                    temp.add(combination + letter);
                }
            }
            result = temp;
        }

        return result;
    }

    public static void main(String[] args) {
        LetterCombinationsPhoneNumber solution = new LetterCombinationsPhoneNumber();

        // Test Case 1: Normal case
        System.out.println(solution.letterCombinations("23")); // Expected:
                                                               // ["ad","ae","af","bd","be","bf","cd","ce","cf"]

        // Test Case 2: Empty string
        System.out.println(solution.letterCombinations("")); // Expected: []

        // Test Case 3: Single digit
        System.out.println(solution.letterCombinations("2")); // Expected: ["a","b","c"]

        // Test Case 4: Digit with 4 letters
        System.out.println(solution.letterCombinations("7")); // Expected: ["p","q","r","s"]

        // Test Case 5: Three digits
        System.out.println(solution.letterCombinations("234")); // Expected: 27 combinations

        // Test Case 6: Maximum constraint
        System.out.println(solution.letterCombinations("2345").size()); // Expected: 81

        // Test Case 7: All digits with 4 letters
        System.out.println(solution.letterCombinations("79")); // Expected: 16 combinations

        // Test Case 8: Mixed 3 and 4 letter digits
        System.out.println(solution.letterCombinations("27")); // Expected: 12 combinations

        // Test Case 9: Test iterative approach
        System.out.println(solution.letterCombinationsIterative("23").equals(solution.letterCombinations("23"))); // Expected:
                                                                                                                  // true

        // Test Case 10: Maximum length
        System.out.println(solution.letterCombinations("2222").size()); // Expected: 81
    }
}
