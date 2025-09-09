package sorting.medium;

import java.util.*;

/**
 * LeetCode 973: K Closest Points to Origin
 * https://leetcode.com/problems/k-closest-points-to-origin/
 *
 * Description:
 * Given an array of points where points[i] = [x_i, y_i] represents a point on
 * the X-Y plane and an integer k, return the k closest points to the origin (0,
 * 0).
 *
 * Constraints:
 * - 1 <= k <= points.length <= 10^4
 * - -10^4 <= x_i, y_i <= 10^4
 *
 * Follow-up:
 * - Can you solve it using quickselect in O(n) average time?
 * - Can you solve it using a heap?
 * - Can you avoid computing the actual square root?
 */
public class KClosestPointsToOrigin {
        public int[][] kClosest(int[][] points, int k) {
                // Use max heap to keep track of k closest points
                PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
                                (a, b) -> Integer.compare(b[0] * b[0] + b[1] * b[1], a[0] * a[0] + a[1] * a[1]));

                for (int[] point : points) {
                        maxHeap.offer(point);
                        if (maxHeap.size() > k) {
                                maxHeap.poll();
                        }
                }

                int[][] result = new int[k][];
                for (int i = 0; i < k; i++) {
                        result[i] = maxHeap.poll();
                }

                return result;
        }

        public static void main(String[] args) {
                KClosestPointsToOrigin solution = new KClosestPointsToOrigin();

                System.out.println(java.util.Arrays
                                .deepToString(solution.kClosest(new int[][] { { 1, 3 }, { -2, 2 } }, 1)));
                // [[-2,2]]

                System.out.println(
                                java.util.Arrays.deepToString(
                                                solution.kClosest(new int[][] { { 3, 3 }, { 5, -1 }, { -2, 4 } }, 2)));
                // [[3,3],[-2,4]] or [[-2,4],[3,3]]

                // Edge Case: k = 1
                System.out.println(
                                java.util.Arrays.deepToString(
                                                solution.kClosest(new int[][] { { 0, 1 }, { 1, 0 }, { 2, 0 } }, 1)));
                // [[0,1]] or [[1,0]]

                // Edge Case: k = all points
                System.out.println(java.util.Arrays
                                .deepToString(solution.kClosest(new int[][] { { 1, 1 }, { 2, 2 } }, 2)));
                // [[1,1],[2,2]]

                // Edge Case: Points at origin
                System.out.println(
                                java.util.Arrays.deepToString(
                                                solution.kClosest(new int[][] { { 0, 0 }, { 1, 1 }, { 2, 2 } }, 1)));
                // [[0,0]]

                // Edge Case: Same distances
                System.out.println(java.util.Arrays
                                .deepToString(solution.kClosest(
                                                new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }, 2)));
                // Any 2 points (all have same distance 1)
        }
}
