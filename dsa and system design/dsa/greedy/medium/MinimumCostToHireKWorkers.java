package greedy.medium;

/**
 * LeetCode 857: Minimum Cost to Hire K Workers
 * https://leetcode.com/problems/minimum-cost-to-hire-k-workers/
 *
 * Description:
 * Given quality and wage arrays, return the minimum cost to hire exactly K
 * workers.
 *
 * Constraints:
 * - 1 <= K <= N <= 10^4
 * - 1 <= quality[i], wage[i] <= 10^4
 */
import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * LeetCode 857: Minimum Cost to Hire K Workers
 * https://leetcode.com/problems/minimum-cost-to-hire-k-workers/
 *
 * Description:
 * Given quality and wage arrays, return the minimum cost to hire exactly K
 * workers.
 *
 * Constraints:
 * - 1 <= K <= N <= 10^4
 * - 1 <= quality[i], wage[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using sorting and priority queue?
 * - Can you optimize for different fairness constraints?
 */
public class MinimumCostToHireKWorkers {
    public double mincostToHireWorkers(int[] quality, int[] wage, int K) {
        int n = quality.length;
        Worker[] workers = new Worker[n];

        for (int i = 0; i < n; i++) {
            workers[i] = new Worker(quality[i], wage[i]);
        }

        // Sort by wage/quality ratio
        Arrays.sort(workers, (a, b) -> Double.compare(a.ratio, b.ratio));

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        double minCost = Double.MAX_VALUE;
        int qualitySum = 0;

        for (Worker worker : workers) {
            maxHeap.offer(worker.quality);
            qualitySum += worker.quality;

            if (maxHeap.size() > K) {
                qualitySum -= maxHeap.poll();
            }

            if (maxHeap.size() == K) {
                minCost = Math.min(minCost, qualitySum * worker.ratio);
            }
        }

        return minCost;
    }

    class Worker {
        int quality;
        int wage;
        double ratio;

        Worker(int quality, int wage) {
            this.quality = quality;
            this.wage = wage;
            this.ratio = (double) wage / quality;
        }
    }

    public static void main(String[] args) {
        MinimumCostToHireKWorkers solution = new MinimumCostToHireKWorkers();
        System.out.println(solution.mincostToHireWorkers(new int[] { 10, 20, 5 }, new int[] { 70, 50, 30 }, 2)); // 105.0
        System.out
                .println(solution.mincostToHireWorkers(new int[] { 3, 1, 10, 10, 1 }, new int[] { 4, 8, 2, 2, 7 }, 3)); // 30.666...
        // Edge Case: K == N
        System.out.println(solution.mincostToHireWorkers(new int[] { 1, 2, 3 }, new int[] { 10, 20, 30 }, 3)); // 60.0
        // Edge Case: All same quality and wage
        System.out.println(solution.mincostToHireWorkers(new int[] { 5, 5, 5 }, new int[] { 10, 10, 10 }, 2)); // 20.0
        // Edge Case: Large input
        int[] quality = new int[10000], wage = new int[10000];
        for (int i = 0; i < 10000; i++) {
            quality[i] = 1;
            wage[i] = 1;
        }
        System.out.println(solution.mincostToHireWorkers(quality, wage, 5000)); // 5000.0
    }
}
