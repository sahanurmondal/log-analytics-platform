package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 397: Integer Replacement
 * https://leetcode.com/problems/integer-replacement/
 *
 * Description: Given a positive integer n, you can apply one of the following
 * operations:
 * 1. If n is even, replace n with n / 2.
 * 2. If n is odd, replace n with either n + 1 or n - 1.
 * Return the minimum number of operations needed for n to become 1.
 * 
 * Constraints:
 * - 1 <= n <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using bit manipulation?
 * - What about memoization?
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(log n) for memoization
 * 
 * Company Tags: Google
 */
public class IntegerReplacement {

    // Main optimized solution - Bit manipulation
    public int integerReplacement(int n) {
        if (n == Integer.MAX_VALUE)
            return 32; // Special case

        int count = 0;
        while (n != 1) {
            if ((n & 1) == 0) {
                n >>>= 1; // Even: divide by 2
            } else if (n == 3 || Integer.bitCount(n + 1) > Integer.bitCount(n - 1)) {
                n--; // Choose n-1 if it results in fewer 1s
            } else {
                n++; // Choose n+1 if it results in fewer 1s
            }
            count++;
        }
        return count;
    }

    // Alternative solution - Memoization
    private Map<Integer, Integer> memo = new HashMap<>();

    public int integerReplacementMemo(int n) {
        if (n == 1)
            return 0;
        if (memo.containsKey(n))
            return memo.get(n);

        int result;
        if (n % 2 == 0) {
            result = 1 + integerReplacementMemo(n / 2);
        } else {
            result = 1 + Math.min(integerReplacementMemo(n + 1), integerReplacementMemo(n - 1));
        }

        memo.put(n, result);
        return result;
    }

    // Alternative solution - BFS
    public int integerReplacementBFS(int n) {
        if (n == 1)
            return 0;

        Queue<Long> queue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        queue.offer((long) n);
        visited.add((long) n);

        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                long current = queue.poll();
                if (current == 1)
                    return steps;

                if (current % 2 == 0) {
                    long next = current / 2;
                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.offer(next);
                    }
                } else {
                    long next1 = current + 1;
                    long next2 = current - 1;
                    if (!visited.contains(next1)) {
                        visited.add(next1);
                        queue.offer(next1);
                    }
                    if (!visited.contains(next2)) {
                        visited.add(next2);
                        queue.offer(next2);
                    }
                }
            }
            steps++;
        }
        return steps;
    }

    public static void main(String[] args) {
        IntegerReplacement solution = new IntegerReplacement();

        System.out.println(solution.integerReplacement(8)); // Expected: 3
        System.out.println(solution.integerReplacement(7)); // Expected: 4
        System.out.println(solution.integerReplacement(4)); // Expected: 2
        System.out.println(solution.integerReplacementMemo(8)); // Expected: 3
        System.out.println(solution.integerReplacementBFS(7)); // Expected: 4
    }
}
