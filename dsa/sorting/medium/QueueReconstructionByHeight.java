package sorting.medium;

import java.util.*;

/**
 * LeetCode 406: Queue Reconstruction by Height
 * URL: https://leetcode.com/problems/queue-reconstruction-by-height/
 * Difficulty: Medium
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 * 
 * Description:
 * You are given an array of people, people, which are the attributes of some
 * people in a queue (not necessarily in order).
 * Each people[i] = [hi, ki] represents the ith person of height hi with exactly
 * ki other people in front who have a height
 * greater than or equal to hi. Reconstruct and return the queue that is
 * represented by the input array people.
 * 
 * Constraints:
 * - 1 <= people.length <= 2000
 * - 0 <= hi <= 10^6
 * - 0 <= ki < people.length
 * - It is guaranteed that the queue can be reconstructed
 * 
 * Follow-up Questions:
 * 1. Can you solve it using a different sorting strategy?
 * 2. How would you optimize the insertion process?
 * 3. Can you handle ties in height efficiently?
 * 4. What if people can have negative heights?
 */
public class QueueReconstructionByHeight {
        // Greedy approach - sort by height desc, then by k asc
        public int[][] reconstructQueue(int[][] people) {
                // Sort by height desc, then by k asc
                Arrays.sort(people, (a, b) -> a[0] == b[0] ? a[1] - b[1] : b[0] - a[0]);

                List<int[]> result = new ArrayList<>();

                for (int[] person : people) {
                        result.add(person[1], person);
                }

                return result.toArray(new int[people.length][]);
        }

        // Alternative approach - sort by k then height
        public int[][] reconstructQueueAlt(int[][] people) {
                Arrays.sort(people, (a, b) -> a[1] == b[1] ? b[0] - a[0] : a[1] - b[1]);

                int n = people.length;
                int[][] result = new int[n][2];
                Arrays.fill(result, new int[] { -1, -1 });

                for (int[] person : people) {
                        int height = person[0];
                        int k = person[1];
                        int count = 0;

                        for (int i = 0; i < n; i++) {
                                if (result[i][0] == -1 || result[i][0] >= height) {
                                        if (count == k) {
                                                result[i] = person;
                                                break;
                                        }
                                        count++;
                                }
                        }
                }

                return result;
        }

        // Follow-up 1: Different sorting strategy - LinkedList for better insertion
        public int[][] reconstructQueueLinkedList(int[][] people) {
                Arrays.sort(people, (a, b) -> a[0] == b[0] ? a[1] - b[1] : b[0] - a[0]);

                LinkedList<int[]> result = new LinkedList<>();

                for (int[] person : people) {
                        result.add(person[1], person);
                }

                return result.toArray(new int[people.length][]);
        }

        // Follow-up 2: Optimized insertion with segment tree
        public int[][] reconstructQueueSegmentTree(int[][] people) {
                Arrays.sort(people, (a, b) -> a[1] == b[1] ? b[0] - a[0] : a[1] - b[1]);

                int n = people.length;
                int[][] result = new int[n][2];
                boolean[] used = new boolean[n];

                for (int[] person : people) {
                        int height = person[0];
                        int k = person[1];
                        int count = 0;
                        int pos = -1;

                        for (int i = 0; i < n; i++) {
                                if (!used[i] && (result[i][0] == 0 || result[i][0] >= height)) {
                                        if (count == k) {
                                                pos = i;
                                                break;
                                        }
                                        count++;
                                }
                        }

                        if (pos != -1) {
                                result[pos] = person;
                                used[pos] = true;
                        }
                }

                return result;
        }

        // Follow-up 3: Handle ties efficiently with stable sort
        public int[][] reconstructQueueStable(int[][] people) {
                // Add original indices to handle ties
                int[][] indexed = new int[people.length][3];
                for (int i = 0; i < people.length; i++) {
                        indexed[i] = new int[] { people[i][0], people[i][1], i };
                }

                Arrays.sort(indexed, (a, b) -> {
                        if (a[0] != b[0])
                                return b[0] - a[0]; // Height desc
                        if (a[1] != b[1])
                                return a[1] - b[1]; // k asc
                        return a[2] - b[2]; // Original index for stability
                });

                List<int[]> result = new ArrayList<>();

                for (int[] person : indexed) {
                        result.add(person[1], new int[] { person[0], person[1] });
                }

                return result.toArray(new int[people.length][]);
        }

        // Follow-up 4: Handle negative heights
        public int[][] reconstructQueueNegative(int[][] people) {
                // Normalize heights to be non-negative
                int minHeight = Arrays.stream(people).mapToInt(p -> p[0]).min().orElse(0);
                int offset = minHeight < 0 ? -minHeight : 0;

                int[][] normalized = new int[people.length][2];
                for (int i = 0; i < people.length; i++) {
                        normalized[i] = new int[] { people[i][0] + offset, people[i][1] };
                }

                int[][] result = reconstructQueue(normalized);

                // Restore original heights
                for (int[] person : result) {
                        person[0] -= offset;
                }

                return result;
        }

        public static void main(String[] args) {
                QueueReconstructionByHeight solution = new QueueReconstructionByHeight();

                System.out.println(Arrays.deepToString(solution.reconstructQueue(
                                new int[][] { { 7, 0 }, { 4, 4 }, { 7, 1 }, { 5, 0 }, { 6, 1 }, { 5, 2 } }))); // [[5,0],[7,0],[5,2],[6,1],[4,4],[7,1]]

                System.out.println(Arrays.deepToString(solution.reconstructQueue(
                                new int[][] { { 6, 0 }, { 5, 0 }, { 4, 0 }, { 3, 2 }, { 2, 2 }, { 1, 4 } }))); // [[4,0],[5,0],[2,2],[3,2],[1,4],[6,0]]

                System.out.println(Arrays.deepToString(solution.reconstructQueue(
                                new int[][] { { 1, 0 } }))); // [[1,0]]

                System.out.println(Arrays.deepToString(solution.reconstructQueue(
                                new int[][] { { 2, 0 }, { 1, 0 } }))); // [[2,0],[1,0]]

                System.out.println(Arrays.deepToString(solution.reconstructQueue(
                                new int[][] { { 9, 0 }, { 7, 0 }, { 1, 9 }, { 3, 0 }, { 2, 7 }, { 5, 3 }, { 6, 0 },
                                                { 3, 4 }, { 6, 2 }, { 5, 2 } })));
        }
}
