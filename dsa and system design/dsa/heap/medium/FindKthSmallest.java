package heap.medium;

/**
 * Variation: Find Kth Smallest Element in Array
 *
 * Description:
 * Find the kth smallest element in an unsorted array.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 */
import java.util.PriorityQueue;

/**
 * LeetCode 215: Kth Largest Element in an Array (modified for smallest)
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 *
 * Description:
 * Find the kth smallest element in an unsorted array.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using quickselect?
 * - Can you optimize space complexity?
 */
public class FindKthSmallest {
    public int findKthSmallest(int[] nums, int k) {
        // Use max heap to keep track of k smallest elements
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        return maxHeap.peek();
    }

    public static void main(String[] args) {
        FindKthSmallest solution = new FindKthSmallest();
        System.out.println(solution.findKthSmallest(new int[] { 3, 2, 1, 5, 6, 4 }, 2)); // 2
        System.out.println(solution.findKthSmallest(new int[] { 3, 2, 3, 1, 2, 4, 5, 5, 6 }, 4)); // 3
    }
}
