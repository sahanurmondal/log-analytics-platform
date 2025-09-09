package queues.medium;

/**
 * LeetCode 346: Moving Average from Data Stream
 * https://leetcode.com/problems/moving-average-from-data-stream/
 *
 * Description:
 * Given a stream of integers and a window size, calculate the moving average of
 * all integers in the sliding window.
 *
 * Constraints:
 * - 1 <= size <= 1000
 * - -10^5 <= val <= 10^5
 * - At most 10^4 calls will be made to next
 *
 * Follow-up:
 * - Can you implement it without storing all values?
 * - Can you extend to support weighted moving average?
 */
import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 346: Moving Average from Data Stream
 * https://leetcode.com/problems/moving-average-from-data-stream/
 *
 * Description:
 * Given a stream of integers and a window size, calculate the moving average of
 * all integers in the sliding window.
 *
 * Constraints:
 * - 1 <= size <= 1000
 * - -10^5 <= val <= 10^5
 * - At most 10^4 calls will be made to next
 *
 * Follow-up:
 * - Can you implement it without storing all values?
 * - Can you extend to support weighted moving average?
 */
public class MovingAverageFromDataStream {
    private Queue<Integer> queue;
    private int maxSize;
    private double sum;

    public MovingAverageFromDataStream(int size) {
        this.queue = new LinkedList<>();
        this.maxSize = size;
        this.sum = 0.0;
    }

    public double next(int val) {
        queue.offer(val);
        sum += val;

        if (queue.size() > maxSize) {
            sum -= queue.poll();
        }

        return sum / queue.size();
    }

    public static void main(String[] args) {
        MovingAverageFromDataStream movingAverage = new MovingAverageFromDataStream(3);
        System.out.println(movingAverage.next(1)); // 1.0
        System.out.println(movingAverage.next(10)); // 5.5
        System.out.println(movingAverage.next(3)); // 4.666...
        System.out.println(movingAverage.next(5)); // 6.0
        // Edge Case: Window size 1
        MovingAverageFromDataStream ma1 = new MovingAverageFromDataStream(1);
        System.out.println(ma1.next(42)); // 42.0
        System.out.println(ma1.next(100)); // 100.0
        // Edge Case: Negative numbers
        System.out.println(movingAverage.next(-5)); // 1.0
    }
}
