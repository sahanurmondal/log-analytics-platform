package searching.hard;

import java.util.*;

/**
 * LeetCode 774: Minimize Max Distance to Gas Station
 * https://leetcode.com/problems/minimize-max-distance-to-gas-station/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Add k gas stations to minimize the maximum distance between
 * consecutive gas stations.
 *
 * Constraints:
 * - 2 <= stations.length <= 2000
 * - 0 <= stations[i] <= 10^8
 * - 1 <= k <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you return the positions of new gas stations?
 * 2. What if stations have different priorities?
 * 3. Can you handle real-time additions?
 */
public class MinimizeMaxDistanceToGasStation {

    // Approach 1: Binary search on answer - O(n log(max_dist / precision)) time,
    // O(1) space
    public double minmaxGasDist(int[] stations, int k) {
        double left = 0, right = 0;

        // Find maximum distance
        for (int i = 1; i < stations.length; i++) {
            right = Math.max(right, stations[i] - stations[i - 1]);
        }

        while (right - left > 1e-6) {
            double mid = left + (right - left) / 2;
            if (canAchieveMaxDist(stations, k, mid)) {
                right = mid;
            } else {
                left = mid;
            }
        }
        return right;
    }

    private boolean canAchieveMaxDist(int[] stations, int k, double maxDist) {
        int needed = 0;
        for (int i = 1; i < stations.length; i++) {
            double dist = stations[i] - stations[i - 1];
            needed += (int) Math.ceil(dist / maxDist) - 1;
            if (needed > k)
                return false;
        }
        return true;
    }

    // Approach 2: Priority queue (heap) - O(n log n + k log n) time, O(n) space
    public double minmaxGasDistHeap(int[] stations, int k) {
        PriorityQueue<double[]> heap = new PriorityQueue<>((a, b) -> Double.compare(b[0], a[0])); // Max heap by gap

        // Initialize heap with all gaps
        for (int i = 1; i < stations.length; i++) {
            double dist = stations[i] - stations[i - 1];
            heap.offer(new double[] { dist, dist, 1 }); // {current_gap, original_dist, count}
        }

        // Add k gas stations
        for (int i = 0; i < k; i++) {
            double[] largest = heap.poll();
            double originalDist = largest[1];
            int count = (int) largest[2];

            count++;
            double newGap = originalDist / count;
            heap.offer(new double[] { newGap, originalDist, count });
        }

        return heap.peek()[0];
    }

    // Approach 3: Greedy with precise calculation - O(n * k) time, O(n) space
    public double minmaxGasDistGreedy(int[] stations, int k) {
        int n = stations.length;
        int[] count = new int[n - 1]; // Number of stations added to each segment

        for (int i = 0; i < k; i++) {
            double maxGap = 0;
            int maxIdx = -1;

            for (int j = 0; j < n - 1; j++) {
                double originalDist = stations[j + 1] - stations[j];
                double currentGap = originalDist / (count[j] + 1);

                if (currentGap > maxGap) {
                    maxGap = currentGap;
                    maxIdx = j;
                }
            }

            if (maxIdx != -1) {
                count[maxIdx]++;
            }
        }

        double result = 0;
        for (int i = 0; i < n - 1; i++) {
            double gap = (double) (stations[i + 1] - stations[i]) / (count[i] + 1);
            result = Math.max(result, gap);
        }

        return result;
    }

    // Follow-up 1: Return positions of new gas stations
    public List<Double> getNewStationPositions(int[] stations, int k) {
        List<Double> positions = new ArrayList<>();
        int n = stations.length;
        int[] count = new int[n - 1];

        // First, determine how many stations to add to each segment
        for (int i = 0; i < k; i++) {
            double maxGap = 0;
            int maxIdx = -1;

            for (int j = 0; j < n - 1; j++) {
                double originalDist = stations[j + 1] - stations[j];
                double currentGap = originalDist / (count[j] + 1);

                if (currentGap > maxGap) {
                    maxGap = currentGap;
                    maxIdx = j;
                }
            }

            if (maxIdx != -1) {
                count[maxIdx]++;
            }
        }

        // Generate actual positions
        for (int i = 0; i < n - 1; i++) {
            if (count[i] > 0) {
                double segmentLength = stations[i + 1] - stations[i];
                double gap = segmentLength / (count[i] + 1);

                for (int j = 1; j <= count[i]; j++) {
                    positions.add(stations[i] + j * gap);
                }
            }
        }

        Collections.sort(positions);
        return positions;
    }

    // Follow-up 2: Weighted priorities for segments
    public double minmaxGasDistWeighted(int[] stations, int[] weights, int k) {
        PriorityQueue<double[]> heap = new PriorityQueue<>((a, b) -> Double.compare(b[0] * b[3], a[0] * a[3])); // Max
                                                                                                                // heap
                                                                                                                // by
                                                                                                                // weighted
                                                                                                                // gap

        for (int i = 1; i < stations.length; i++) {
            double dist = stations[i] - stations[i - 1];
            double weight = weights[i - 1];
            heap.offer(new double[] { dist, dist, 1, weight });
        }

        for (int i = 0; i < k; i++) {
            double[] largest = heap.poll();
            double originalDist = largest[1];
            int count = (int) largest[2];
            double weight = largest[3];

            count++;
            double newGap = originalDist / count;
            heap.offer(new double[] { newGap, originalDist, count, weight });
        }

        return heap.peek()[0];
    }

    // Follow-up 3: Real-time addition simulation
    public List<Double> simulateRealTimeAddition(int[] stations, int k) {
        List<Double> maxDistances = new ArrayList<>();
        int[] count = new int[stations.length - 1];

        for (int added = 0; added < k; added++) {
            double maxGap = 0;
            int maxIdx = -1;

            for (int j = 0; j < stations.length - 1; j++) {
                double originalDist = stations[j + 1] - stations[j];
                double currentGap = originalDist / (count[j] + 1);

                if (currentGap > maxGap) {
                    maxGap = currentGap;
                    maxIdx = j;
                }
            }

            if (maxIdx != -1) {
                count[maxIdx]++;
            }

            // Calculate current max distance
            double currentMax = 0;
            for (int i = 0; i < stations.length - 1; i++) {
                double gap = (double) (stations[i + 1] - stations[i]) / (count[i] + 1);
                currentMax = Math.max(currentMax, gap);
            }
            maxDistances.add(currentMax);
        }

        return maxDistances;
    }

    public static void main(String[] args) {
        MinimizeMaxDistanceToGasStation solution = new MinimizeMaxDistanceToGasStation();

        // Test case 1: Basic case
        int[] stations1 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println("Test 1 - Basic case (k=9):");
        double result1 = solution.minmaxGasDist(stations1, 9);
        System.out.printf("Expected: 0.5, Got: %.6f\n", result1);
        System.out.printf("Heap approach: %.6f\n", solution.minmaxGasDistHeap(stations1, 9));
        System.out.printf("Greedy approach: %.6f\n", solution.minmaxGasDistGreedy(stations1, 9));

        // Test case 2: Uneven spacing
        int[] stations2 = { 23, 24, 36, 39, 46, 56, 57, 65, 84, 98 };
        System.out.println("\nTest 2 - Uneven spacing (k=1):");
        double result2 = solution.minmaxGasDist(stations2, 1);
        System.out.printf("Got: %.6f\n", result2);

        // Test case 3: Large gaps
        int[] stations3 = { 1, 10, 20 };
        System.out.println("\nTest 3 - Large gaps (k=4):");
        double result3 = solution.minmaxGasDist(stations3, 4);
        System.out.printf("Got: %.6f\n", result3);

        // Test case 4: No stations needed (k=0)
        System.out.println("\nTest 4 - No additional stations (k=0):");
        double result4 = solution.minmaxGasDist(stations1, 0);
        System.out.printf("Expected: 1.0, Got: %.6f\n", result4);

        // Edge case: Two stations only
        int[] stations5 = { 1, 11 };
        System.out.println("\nEdge case - Two stations (k=5):");
        double result5 = solution.minmaxGasDist(stations5, 5);
        System.out.printf("Expected: ~1.67, Got: %.6f\n", result5);

        // Follow-up 1: Get positions of new stations
        System.out.println("\nFollow-up 1 - New station positions:");
        List<Double> positions = solution.getNewStationPositions(new int[] { 1, 5, 10 }, 2);
        System.out.println("Positions: " + positions);

        // Follow-up 2: Weighted priorities
        int[] weights = { 1, 2, 1, 1 };
        System.out.println("\nFollow-up 2 - Weighted priorities:");
        double weightedResult = solution.minmaxGasDistWeighted(new int[] { 1, 3, 6, 10, 15 }, weights, 2);
        System.out.printf("Weighted result: %.6f\n", weightedResult);

        // Follow-up 3: Real-time simulation
        System.out.println("\nFollow-up 3 - Real-time simulation:");
        List<Double> simulation = solution.simulateRealTimeAddition(new int[] { 1, 5, 10 }, 3);
        System.out.println("Max distances after each addition: " + simulation);

        // Performance test
        int[] largeStations = new int[2000];
        for (int i = 0; i < largeStations.length; i++) {
            largeStations[i] = i * 100;
        }
        long startTime = System.currentTimeMillis();
        solution.minmaxGasDist(largeStations, 1000);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (2000 stations, k=1000): " + (endTime - startTime) + "ms");
    }
}
