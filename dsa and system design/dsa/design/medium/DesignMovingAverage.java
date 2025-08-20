package design.medium;

import java.util.*;

/**
 * LeetCode 346: Moving Average from Data Stream
 * https://leetcode.com/problems/moving-average-from-data-stream/
 *
 * Description: Given a stream of integers and a window size, calculate the
 * moving average of all integers in the sliding window.
 * 
 * Constraints:
 * - 1 <= size <= 1000
 * - -10^5 <= val <= 10^5
 * - At most 10^4 calls will be made to next
 *
 * Follow-up:
 * - Can you solve it in O(1) time per operation?
 * 
 * Time Complexity: O(1) for next
 * Space Complexity: O(size)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class DesignMovingAverage {

    private Queue<Integer> window;
    private int maxSize;
    private double sum;

    public DesignMovingAverage(int size) {
        this.maxSize = size;
        this.window = new LinkedList<>();
        this.sum = 0.0;
    }

    public double next(int val) {
        if (window.size() == maxSize) {
            sum -= window.poll();
        }

        window.offer(val);
        sum += val;

        return sum / window.size();
    }

    // Alternative implementation using circular array
    static class MovingAverageCircular {
        private int[] window;
        private int size;
        private int count;
        private int index;
        private double sum;

        public MovingAverageCircular(int size) {
            this.size = size;
            this.window = new int[size];
            this.count = 0;
            this.index = 0;
            this.sum = 0.0;
        }

        public double next(int val) {
            if (count < size) {
                sum += val;
                window[index] = val;
                count++;
            } else {
                sum = sum - window[index] + val;
                window[index] = val;
            }

            index = (index + 1) % size;
            return sum / count;
        }
    }

    public static void main(String[] args) {
        DesignMovingAverage ma = new DesignMovingAverage(3);
        System.out.println(ma.next(1)); // Expected: 1.0
        System.out.println(ma.next(10)); // Expected: 5.5
        System.out.println(ma.next(3)); // Expected: 4.666666666666667
        System.out.println(ma.next(5)); // Expected: 6.0

        // Test circular implementation
        MovingAverageCircular mac = new MovingAverageCircular(3);
        System.out.println("Circular - " + mac.next(1)); // Expected: 1.0
        System.out.println("Circular - " + mac.next(10)); // Expected: 5.5
        System.out.println("Circular - " + mac.next(3)); // Expected: 4.666666666666667
        System.out.println("Circular - " + mac.next(5)); // Expected: 6.0
    }
}
