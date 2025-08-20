package heap.hard;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * LeetCode 502: IPO
 * https://leetcode.com/problems/ipo/
 * 
 * Companies: Amazon, Microsoft, Google
 * Frequency: Medium
 *
 * Description:
 * Suppose LeetCode will start its IPO soon. In order to sell a good price of
 * its shares to Venture Capital, LeetCode would like to work on some projects
 * to increase its capital before the IPO.
 * You are given `k` projects, `w` initial capital, an array of profits
 * `profits`, and an array of capital `capital`.
 * To start a project `i`, you must have a capital of at least `capital[i]` to
 * start it. After finishing the project, you will obtain `profits[i]` and add
 * it to your total capital.
 * Pick at most `k` distinct projects to maximize your final capital.
 *
 * Constraints:
 * - 1 <= k <= 10^5
 * - 0 <= w <= 10^9
 * - n == profits.length == capital.length
 * - 1 <= n <= 10^5
 * - 0 <= profits[i] <= 10^4
 * - 0 <= capital[i] <= 10^9
 * 
 * Follow-up Questions:
 * 1. Why are two heaps (or one heap and sorting) a good approach here?
 * 2. What is the greedy strategy being used?
 * 3. How would you handle the case where you can do the same project multiple
 * times?
 */
public class IPO {

    // Approach 1: Two Heaps (or Sort + One Heap) - O(n log n + k log n) time, O(n)
    // space
    public int findMaximizedCapital(int k, int w, int[] profits, int[] capital) {
        int n = profits.length;
        int[][] projects = new int[n][2];
        for (int i = 0; i < n; i++) {
            projects[i][0] = capital[i];
            projects[i][1] = profits[i];
        }

        // Sort projects by their capital requirement
        Arrays.sort(projects, (a, b) -> a[0] - b[0]);

        // Max-heap to store profits of affordable projects
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        int projectIndex = 0;
        // Perform at most k projects
        for (int i = 0; i < k; i++) {
            // Add all affordable projects to the max-heap
            while (projectIndex < n && projects[projectIndex][0] <= w) {
                maxHeap.offer(projects[projectIndex][1]);
                projectIndex++;
            }

            // If no projects are affordable, break
            if (maxHeap.isEmpty()) {
                break;
            }

            // Greedily pick the most profitable project
            w += maxHeap.poll();
        }

        return w;
    }

    public static void main(String[] args) {
        IPO solution = new IPO();

        // Test case 1
        int k1 = 2, w1 = 0;
        int[] profits1 = { 1, 2, 3 }, capital1 = { 0, 1, 1 };
        System.out.println("Max Capital 1: " + solution.findMaximizedCapital(k1, w1, profits1, capital1)); // 4

        // Test case 2
        int k2 = 3, w2 = 0;
        int[] profits2 = { 1, 2, 3 }, capital2 = { 0, 1, 2 };
        System.out.println("Max Capital 2: " + solution.findMaximizedCapital(k2, w2, profits2, capital2)); // 6

        // Test case 3: Not enough capital to start any project
        int k3 = 1, w3 = 0;
        int[] profits3 = { 1, 2, 3 }, capital3 = { 1, 1, 2 };
        System.out.println("Max Capital 3: " + solution.findMaximizedCapital(k3, w3, profits3, capital3)); // 0

        // Test case 4: k is larger than number of projects
        int k4 = 10, w4 = 0;
        int[] profits4 = { 1, 2, 3 }, capital4 = { 0, 1, 1 };
        System.out.println("Max Capital 4: " + solution.findMaximizedCapital(k4, w4, profits4, capital4)); // 6
    }
}
