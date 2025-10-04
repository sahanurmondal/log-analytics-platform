package grid.hard;

import java.util.*;

/**
 * LeetCode 1553: Minimum Number of Days to Eat N Oranges
 * https://leetcode.com/problems/minimum-number-of-days-to-eat-n-oranges/
 *
 * Description:
 * There are n oranges in the kitchen and you decided to eat some of these
 * oranges every day as follows:
 * - Eat one orange.
 * - If the number of remaining oranges n is divisible by 2, you can eat n/2
 * oranges.
 * - If the number of remaining oranges n is divisible by 3, you can eat 2 *
 * (n/3) oranges.
 * Return the minimum number of days to eat n oranges.
 *
 * Constraints:
 * - 1 <= n <= 2 * 10^9
 */
public class MinimumMovesToReachTarget {

    private Map<Integer, Integer> memo = new HashMap<>();

    public int minDays(int n) {
        if (n == 1)
            return 1;
        if (memo.containsKey(n))
            return memo.get(n);

        int result = 1 + Math.min(n % 2 + minDays(n / 2), n % 3 + minDays(n / 3));
        memo.put(n, result);
        return result;
    }

    // Alternative BFS approach for smaller values
    public int minDaysBFS(int n) {
        if (n == 1)
            return 1;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(n);
        visited.add(n);

        int days = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            days++;

            for (int i = 0; i < size; i++) {
                int curr = queue.poll();

                if (curr == 1)
                    return days;

                // Option 1: eat one orange
                if (curr - 1 >= 1 && !visited.contains(curr - 1)) {
                    visited.add(curr - 1);
                    queue.offer(curr - 1);
                }

                // Option 2: if divisible by 2
                if (curr % 2 == 0 && !visited.contains(curr / 2)) {
                    visited.add(curr / 2);
                    queue.offer(curr / 2);
                }

                // Option 3: if divisible by 3
                if (curr % 3 == 0 && !visited.contains(curr / 3)) {
                    visited.add(curr / 3);
                    queue.offer(curr / 3);
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        MinimumMovesToReachTarget solution = new MinimumMovesToReachTarget();

        System.out.println(solution.minDays(10)); // 4
        System.out.println(solution.minDays(6)); // 3
        System.out.println(solution.minDays(1)); // 1
    }
}
