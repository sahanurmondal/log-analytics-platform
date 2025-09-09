package binarysearch.easy;

/**
 * LeetCode 374: Guess Number Higher or Lower
 * https://leetcode.com/problems/guess-number-higher-or-lower/
 *
 * Description:
 * We are playing the Guess Game. The game is as follows:
 * I pick a number from 1 to n. You have to guess which number I picked.
 * Every time you guess wrong, I will tell you whether the number I picked is
 * higher or lower than your guess.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Uber, Airbnb
 * Difficulty: Easy
 * Asked: 2023-2024 (Medium Frequency)
 *
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 * - 1 <= pick <= n
 *
 * Follow-ups:
 * - What's the optimal strategy to minimize worst-case guesses?
 * - How would you handle if the API has cost per call?
 * - Can you implement a ternary search version?
 */
public class GuessNumberHigherOrLower {

    private int pick = 6; // Simulated picked number for testing

    // The guess API (given by LeetCode)
    private int guess(int num) {
        if (num == pick)
            return 0;
        else if (num < pick)
            return 1;
        else
            return -1;
    }

    // Binary Search solution - O(log n)
    public int guessNumber(int n) {
        int left = 1, right = n;

        while (left <= right) {
            int mid = left + (right - left) / 2; // Avoid overflow
            int result = guess(mid);

            if (result == 0) {
                return mid; // Found the number
            } else if (result == 1) {
                left = mid + 1; // Guess higher
            } else {
                right = mid - 1; // Guess lower
            }
        }

        return -1; // Should never reach here given constraints
    }

    // Ternary Search approach - Follow-up (less efficient than binary search)
    public int guessNumberTernary(int n) {
        return guessNumberTernaryHelper(1, n);
    }

    private int guessNumberTernaryHelper(int left, int right) {
        if (left > right)
            return -1;

        int mid1 = left + (right - left) / 3;
        int mid2 = right - (right - left) / 3;

        int result1 = guess(mid1);
        if (result1 == 0)
            return mid1;

        int result2 = guess(mid2);
        if (result2 == 0)
            return mid2;

        if (result1 == 1) {
            // Number is higher than mid1
            if (result2 == 1) {
                // Number is higher than mid2
                return guessNumberTernaryHelper(mid2 + 1, right);
            } else {
                // Number is between mid1 and mid2
                return guessNumberTernaryHelper(mid1 + 1, mid2 - 1);
            }
        } else {
            // Number is lower than mid1
            return guessNumberTernaryHelper(left, mid1 - 1);
        }
    }

    public static void main(String[] args) {
        GuessNumberHigherOrLower solution = new GuessNumberHigherOrLower();

        // Test Case 1: Pick = 6, n = 10
        solution.pick = 6;
        System.out.println(solution.guessNumber(10)); // Expected: 6

        // Test Case 2: Pick = 1, n = 1
        solution.pick = 1;
        System.out.println(solution.guessNumber(1)); // Expected: 1

        // Test Case 3: Pick = 1, n = 2
        solution.pick = 1;
        System.out.println(solution.guessNumber(2)); // Expected: 1

        // Test Case 4: Pick = 2, n = 2
        solution.pick = 2;
        System.out.println(solution.guessNumber(2)); // Expected: 2

        // Test Ternary Search
        solution.pick = 6;
        System.out.println(solution.guessNumberTernary(10)); // Expected: 6

        // Edge case: Large n
        solution.pick = 1702766719;
        System.out.println(solution.guessNumber(2126753390)); // Expected: 1702766719
    }
}
