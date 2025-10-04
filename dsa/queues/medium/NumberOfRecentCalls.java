package queues.medium;

/**
 * LeetCode 933: Number of Recent Calls
 * https://leetcode.com/problems/number-of-recent-calls/
 *
 * Description:
 * You have a RecentCounter class which counts the number of recent requests
 * within a certain time frame.
 *
 * Constraints:
 * - 1 <= t <= 10^9
 * - Each test case will call ping with strictly increasing values of t
 * - At most 10^4 calls will be made to ping
 *
 * Follow-up:
 * - Can you implement it without using a queue?
 * - Can you extend to support different time windows?
 */
import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 933: Number of Recent Calls
 * https://leetcode.com/problems/number-of-recent-calls/
 *
 * Description:
 * You have a RecentCounter class which counts the number of recent requests
 * within a certain time frame.
 *
 * Constraints:
 * - 1 <= t <= 10^9
 * - Each test case will call ping with strictly increasing values of t
 * - At most 10^4 calls will be made to ping
 *
 * Follow-up:
 * - Can you implement it without using a queue?
 * - Can you extend to support different time windows?
 */
public class NumberOfRecentCalls {
    private Queue<Integer> queue;

    public NumberOfRecentCalls() {
        this.queue = new LinkedList<>();
    }

    public int ping(int t) {
        queue.offer(t);

        // Remove all requests that are older than 3000ms
        while (!queue.isEmpty() && queue.peek() < t - 3000) {
            queue.poll();
        }

        return queue.size();
    }

    public static void main(String[] args) {
        NumberOfRecentCalls recentCounter = new NumberOfRecentCalls();
        System.out.println(recentCounter.ping(1)); // 1
        System.out.println(recentCounter.ping(100)); // 2
        System.out.println(recentCounter.ping(3001)); // 3
        System.out.println(recentCounter.ping(3002)); // 3
        // Edge Case: Large gaps
        System.out.println(recentCounter.ping(6002)); // 1
        // Edge Case: Multiple calls in sequence
        System.out.println(recentCounter.ping(6003)); // 2
        System.out.println(recentCounter.ping(6004)); // 3
    }
}
