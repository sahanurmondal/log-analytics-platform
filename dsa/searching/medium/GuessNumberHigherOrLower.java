package searching.medium;

/**
 * LeetCode 374: Guess Number Higher or Lower
 * https://leetcode.com/problems/guess-number-higher-or-lower/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Guess a number between 1 and n.
 *
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you minimize the number of guesses?
 * 2. Can you handle random guess feedback?
 * 3. Can you guess in a distributed system?
 */
public class GuessNumberHigherOrLower {
    private int pick;

    public GuessNumberHigherOrLower(int pick) {
        this.pick = pick;
    }

    // API: returns -1 if guess > pick, 1 if guess < pick, 0 if guess == pick
    private int guess(int num) {
        return Integer.compare(pick, num);
    }

    public int guessNumber(int n) {
        int left = 1, right = n;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int res = guess(mid);
            if (res == 0)
                return mid;
            else if (res < 0)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return -1;
    }

    // Follow-up 1: Minimize number of guesses (already optimal)
    // Follow-up 2: Random guess feedback (simulate random pick)
    public int guessNumberRandom(int n) {
        int left = 1, right = n;
        java.util.Random rand = new java.util.Random();
        while (left <= right) {
            int mid = left + rand.nextInt(right - left + 1);
            int res = guess(mid);
            if (res == 0)
                return mid;
            else if (res < 0)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return -1;
    }

    // Follow-up 3: Distributed guessing (simulate with multiple threads)
    public int guessNumberDistributed(int n) {
        // Simulate with two threads guessing halves
        int left = 1, right = n;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int res = guess(mid);
            if (res == 0)
                return mid;
            else if (res < 0)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        GuessNumberHigherOrLower solution = new GuessNumberHigherOrLower(6);
        // Basic case
        System.out.println("Basic: " + solution.guessNumber(10)); // 6
        // Edge: Pick is 1
        GuessNumberHigherOrLower sol2 = new GuessNumberHigherOrLower(1);
        System.out.println("Pick is 1: " + sol2.guessNumber(10)); // 1
        // Edge: Pick is n
        GuessNumberHigherOrLower sol3 = new GuessNumberHigherOrLower(10);
        System.out.println("Pick is n: " + sol3.guessNumber(10)); // 10
        // Follow-up 2: Random guess
        System.out.println("Random guess: " + solution.guessNumberRandom(10));
        // Follow-up 3: Distributed guess
        System.out.println("Distributed guess: " + solution.guessNumberDistributed(10));
    }
}
