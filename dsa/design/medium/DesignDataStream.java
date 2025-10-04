package design.medium;

/**
 * LeetCode 2526: Find Consecutive Integers from a Data Stream
 * https://leetcode.com/problems/find-consecutive-integers-from-a-data-stream/
 *
 * Description: For a stream of integers, implement a data structure that checks
 * if the last k integers parsed in the stream are equal to value.
 * 
 * Constraints:
 * - 1 <= value, k <= 10^5
 * - 1 <= num <= 10^5
 * - At most 10^5 calls will be made to consec
 *
 * Follow-up:
 * - Can you solve it in O(1) time per call?
 * 
 * Time Complexity: O(1) for consec
 * Space Complexity: O(1)
 * 
 * Company Tags: Google
 */
public class DesignDataStream {

    private int value;
    private int k;
    private int consecutiveCount;

    public DesignDataStream(int value, int k) {
        this.value = value;
        this.k = k;
        this.consecutiveCount = 0;
    }

    public boolean consec(int num) {
        if (num == value) {
            consecutiveCount++;
        } else {
            consecutiveCount = 0;
        }

        return consecutiveCount >= k;
    }

    public static void main(String[] args) {
        DesignDataStream dataStream = new DesignDataStream(4, 3);
        System.out.println(dataStream.consec(4)); // Expected: false
        System.out.println(dataStream.consec(4)); // Expected: false
        System.out.println(dataStream.consec(4)); // Expected: true
        System.out.println(dataStream.consec(3)); // Expected: false
    }
}
