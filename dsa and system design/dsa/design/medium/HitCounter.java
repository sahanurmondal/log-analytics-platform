package design.medium;

import java.util.*;

/**
 * LeetCode 362: Design Hit Counter
 * https://leetcode.com/problems/design-hit-counter/
 *
 * Description: Design a hit counter which counts the number of hits received in
 * the past 5 minutes (i.e., the past 300 seconds).
 * 
 * Constraints:
 * - 1 <= timestamp <= 2 * 10^9
 * - All the calls are being made to the system in chronological order (i.e.,
 * timestamp is monotonically increasing)
 * - At most 300 calls will be made to hit and getHits
 *
 * Follow-up:
 * - What if the number of hits per second could be huge? Does your design
 * scale?
 * - What if calls to getHits() are frequent?
 * 
 * Time Complexity: O(1) for hit, O(1) for getHits (amortized)
 * Space Complexity: O(1) - bounded by 300
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class HitCounter {

    private int[] times;
    private int[] hits;

    public HitCounter() {
        times = new int[300];
        hits = new int[300];
    }

    public void hit(int timestamp) {
        int index = timestamp % 300;
        if (times[index] != timestamp) {
            times[index] = timestamp;
            hits[index] = 1;
        } else {
            hits[index]++;
        }
    }

    public int getHits(int timestamp) {
        int total = 0;
        for (int i = 0; i < 300; i++) {
            if (timestamp - times[i] < 300) {
                total += hits[i];
            }
        }
        return total;
    }

    // Alternative implementation - Queue based
    static class HitCounterQueue {
        private Queue<Integer> hits;

        public HitCounterQueue() {
            hits = new LinkedList<>();
        }

        public void hit(int timestamp) {
            hits.offer(timestamp);
        }

        public int getHits(int timestamp) {
            while (!hits.isEmpty() && timestamp - hits.peek() >= 300) {
                hits.poll();
            }
            return hits.size();
        }
    }

    public static void main(String[] args) {
        HitCounter counter = new HitCounter();
        counter.hit(1);
        counter.hit(2);
        counter.hit(3);
        System.out.println(counter.getHits(4)); // Expected: 3
        counter.hit(300);
        System.out.println(counter.getHits(300)); // Expected: 4
        System.out.println(counter.getHits(301)); // Expected: 3
    }
}
