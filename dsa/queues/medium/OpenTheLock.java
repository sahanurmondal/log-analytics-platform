package queues.medium;

/**
 * LeetCode 752: Open the Lock
 * https://leetcode.com/problems/open-the-lock/
 *
 * Description:
 * You have a lock in front of you with 4 circular wheels. Return the minimum
 * total number of turns required to open the lock, or -1 if it is impossible.
 *
 * Constraints:
 * - 1 <= deadends.length <= 500
 * - deadends[i].length == 4
 * - target.length == 4
 * - target will not be in the list deadends
 * - target and deadends[i] consist of digits only
 *
 * Follow-up:
 * - Can you solve it using bidirectional BFS?
 * - Can you extend to n-digit locks?
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 752: Open the Lock
 * https://leetcode.com/problems/open-the-lock/
 *
 * Description:
 * You have a lock in front of you with 4 circular wheels. Return the minimum
 * total number of turns required to open the lock, or -1 if it is impossible.
 *
 * Constraints:
 * - 1 <= deadends.length <= 500
 * - deadends[i].length == 4
 * - target.length == 4
 * - target will not be in the list deadends
 * - target and deadends[i] consist of digits only
 *
 * Follow-up:
 * - Can you solve it using bidirectional BFS?
 * - Can you extend to n-digit locks?
 */
public class OpenTheLock {
    public int openLock(String[] deadends, String target) {
        Set<String> deadSet = new HashSet<>();
        for (String dead : deadends) {
            deadSet.add(dead);
        }

        if (deadSet.contains("0000"))
            return -1;
        if (target.equals("0000"))
            return 0;

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer("0000");
        visited.add("0000");

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            steps++;

            for (int i = 0; i < size; i++) {
                String curr = queue.poll();

                // Generate all possible next states
                for (int j = 0; j < 4; j++) {
                    char c = curr.charAt(j);

                    // Turn up
                    String next1 = curr.substring(0, j) +
                            (char) ((c - '0' + 1) % 10 + '0') +
                            curr.substring(j + 1);

                    // Turn down
                    String next2 = curr.substring(0, j) +
                            (char) ((c - '0' + 9) % 10 + '0') +
                            curr.substring(j + 1);

                    for (String next : new String[] { next1, next2 }) {
                        if (next.equals(target))
                            return steps;

                        if (!visited.contains(next) && !deadSet.contains(next)) {
                            visited.add(next);
                            queue.offer(next);
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        OpenTheLock solution = new OpenTheLock();
        System.out.println(solution.openLock(new String[] { "0201", "0101", "0102", "1212", "2002" }, "0202")); // 6
        System.out.println(solution.openLock(new String[] { "8888" }, "0009")); // 1
        System.out.println(solution
                .openLock(new String[] { "8887", "8889", "8878", "8898", "8788", "8988", "7888", "9888" }, "8888")); // -1

        // Edge Case: Target is start
        System.out.println(solution.openLock(new String[] { "0201" }, "0000")); // 0

        // Edge Case: Start is deadend
        System.out.println(solution.openLock(new String[] { "0000" }, "8888")); // -1
    }
}
