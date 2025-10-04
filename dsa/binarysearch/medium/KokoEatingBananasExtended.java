package binarysearch.medium;

/**
 * LeetCode 875: Koko Eating Bananas
 * https://leetcode.com/problems/koko-eating-bananas/
 *
 * Description:
 * Koko loves to eat bananas. There are n piles of bananas, the ith pile has
 * piles[i] bananas.
 * The guards have gone and will come back in h hours.
 * Koko can decide her bananas-per-hour eating speed of k. Each hour, she
 * chooses some pile of bananas and eats k bananas from that pile.
 * If the pile has less than k bananas, she eats all of them for that hour and
 * won't eat any more bananas during that hour.
 * Koko likes to eat slowly but still wants to finish eating all the bananas
 * before the guards come back.
 * Return the minimum integer k such that she can eat all the bananas within h
 * hours.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Uber,
 * Airbnb
 * Difficulty: Medium
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= piles.length <= 10^4
 * - piles.length <= h <= 10^9
 * - 1 <= piles[i] <= 10^9
 *
 * Follow-ups:
 * - What if Koko can eat from multiple piles in one hour?
 * - How would you handle if piles can be refilled?
 * - Can you optimize for very large pile sizes?
 */
public class KokoEatingBananasExtended {

    // Binary Search on answer - O(n * log(max_pile)) time, O(1) space
    public int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = getMaxPile(piles);

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canFinish(piles, mid, h)) {
                right = mid; // Try smaller speed
            } else {
                left = mid + 1; // Need faster speed
            }
        }

        return left;
    }

    private boolean canFinish(int[] piles, int speed, int h) {
        long totalHours = 0;

        for (int pile : piles) {
            totalHours += (pile + speed - 1) / speed; // Ceiling division
            if (totalHours > h) {
                return false; // Early termination
            }
        }

        return totalHours <= h;
    }

    private int getMaxPile(int[] piles) {
        int max = piles[0];
        for (int pile : piles) {
            max = Math.max(max, pile);
        }
        return max;
    }

    // Alternative implementation with explicit ceiling
    public int minEatingSpeedAlt(int[] piles, int h) {
        int left = 1;
        int right = getMaxPile(piles);
        int result = right;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (canFinishAlt(piles, mid, h)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    private boolean canFinishAlt(int[] piles, int speed, int h) {
        long totalHours = 0;

        for (int pile : piles) {
            totalHours += Math.ceil((double) pile / speed);
            if (totalHours > h) {
                return false;
            }
        }

        return totalHours <= h;
    }

    // Optimized version with early bounds checking
    public int minEatingSpeedOptimized(int[] piles, int h) {
        // Calculate total bananas
        long totalBananas = 0;
        int maxPile = 0;

        for (int pile : piles) {
            totalBananas += pile;
            maxPile = Math.max(maxPile, pile);
        }

        // Minimum possible speed (ceiling of average)
        int left = (int) Math.ceil((double) totalBananas / h);
        left = Math.max(left, 1);

        // Maximum possible speed
        int right = maxPile;

        // If we have enough time, minimum speed is 1
        if (h >= totalBananas) {
            return 1;
        }

        // If each pile needs its own hour, we need max pile speed
        if (h == piles.length) {
            return maxPile;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canFinish(piles, mid, h)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    // Version that returns the eating schedule
    public int[] getEatingSchedule(int[] piles, int h) {
        int speed = minEatingSpeed(piles, h);
        java.util.List<Integer> schedule = new java.util.ArrayList<>();

        for (int pile : piles) {
            int hoursNeeded = (pile + speed - 1) / speed;
            for (int i = 0; i < hoursNeeded; i++) {
                schedule.add(Math.min(speed, pile - i * speed));
            }
        }

        return schedule.stream().mapToInt(i -> i).toArray();
    }

    // Linear search for comparison - O(n * max_pile) time
    public int minEatingSpeedLinear(int[] piles, int h) {
        int maxPile = getMaxPile(piles);

        for (int speed = 1; speed <= maxPile; speed++) {
            if (canFinish(piles, speed, h)) {
                return speed;
            }
        }

        return maxPile;
    }

    // Handle edge case where h is very large
    public int minEatingSpeedLargeH(int[] piles, int h) {
        long totalBananas = 0;
        for (int pile : piles) {
            totalBananas += pile;
        }

        if (h >= totalBananas) {
            return 1;
        }

        return minEatingSpeed(piles, h);
    }

    // Version with detailed timing analysis
    public int minEatingSpeedWithAnalysis(int[] piles, int h) {
        System.out.println("Piles: " + java.util.Arrays.toString(piles));
        System.out.println("Hours available: " + h);

        int left = 1;
        int right = getMaxPile(piles);

        System.out.println("Search range: [" + left + ", " + right + "]");

        int iterations = 0;

        while (left < right) {
            iterations++;
            int mid = left + (right - left) / 2;
            long hoursNeeded = calculateHours(piles, mid);

            System.out.println("Iteration " + iterations + ": speed=" + mid + ", hours=" + hoursNeeded);

            if (hoursNeeded <= h) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        System.out.println("Total iterations: " + iterations);
        return left;
    }

    private long calculateHours(int[] piles, int speed) {
        long totalHours = 0;
        for (int pile : piles) {
            totalHours += (pile + speed - 1) / speed;
        }
        return totalHours;
    }

    // Handle floating-point speeds (theoretical extension)
    public double minEatingSpeedFloat(int[] piles, int h, double precision) {
        double left = 1.0;
        double right = (double) getMaxPile(piles);

        while (right - left > precision) {
            double mid = (left + right) / 2.0;

            if (canFinishFloat(piles, mid, h)) {
                right = mid;
            } else {
                left = mid;
            }
        }

        return right;
    }

    private boolean canFinishFloat(int[] piles, double speed, int h) {
        double totalHours = 0;

        for (int pile : piles) {
            totalHours += Math.ceil(pile / speed);
            if (totalHours > h) {
                return false;
            }
        }

        return totalHours <= h;
    }

    public static void main(String[] args) {
        KokoEatingBananasExtended solution = new KokoEatingBananasExtended();

        // Test Case 1: [3,6,7,11], h = 8
        int[] piles1 = { 3, 6, 7, 11 };
        System.out.println(solution.minEatingSpeed(piles1, 8)); // Expected: 4

        // Test Case 2: [30,11,23,4,20], h = 5
        int[] piles2 = { 30, 11, 23, 4, 20 };
        System.out.println(solution.minEatingSpeed(piles2, 5)); // Expected: 30

        // Test Case 3: [30,11,23,4,20], h = 6
        System.out.println(solution.minEatingSpeed(piles2, 6)); // Expected: 23

        // Test Case 4: Single pile
        int[] piles3 = { 312884470 };
        System.out.println(solution.minEatingSpeed(piles3, 312884469)); // Expected: 2

        // Test Case 5: All piles same size
        int[] piles4 = { 5, 5, 5, 5 };
        System.out.println(solution.minEatingSpeed(piles4, 8)); // Expected: 3

        // Test Case 6: Very long time available
        int[] piles5 = { 1, 2, 3 };
        System.out.println(solution.minEatingSpeed(piles5, 100)); // Expected: 1

        // Test alternative implementation
        System.out.println("Alternative: " + solution.minEatingSpeedAlt(piles1, 8)); // Expected: 4

        // Test optimized version
        System.out.println("Optimized: " + solution.minEatingSpeedOptimized(piles1, 8)); // Expected: 4

        // Test linear search (small input only)
        int[] small = { 3, 6, 7 };
        System.out.println("Linear: " + solution.minEatingSpeedLinear(small, 4)); // Expected: 6

        // Test with analysis
        System.out.println("\nDetailed analysis:");
        solution.minEatingSpeedWithAnalysis(piles1, 8);

        // Test eating schedule
        int[] schedule = solution.getEatingSchedule(new int[] { 3, 6 }, 4);
        System.out.println("Eating schedule: " + java.util.Arrays.toString(schedule));

        // Test edge cases
        int[] edge1 = { 1 };
        System.out.println("Single banana: " + solution.minEatingSpeed(edge1, 1)); // Expected: 1

        int[] edge2 = { 1000000000 };
        System.out.println("Large pile: " + solution.minEatingSpeed(edge2, 2)); // Expected: 500000000

        // Test floating-point version
        double floatResult = solution.minEatingSpeedFloat(new int[] { 3, 6, 7, 11 }, 8, 0.1);
        System.out.println("Float result: " + floatResult); // Expected: ~4.0

        // Large test case
        int[] largePiles = new int[10000];
        java.util.Arrays.fill(largePiles, 1000);
        long startTime = System.currentTimeMillis();
        int largeResult = solution.minEatingSpeed(largePiles, 20000);
        long endTime = System.currentTimeMillis();
        System.out.println("Large test result: " + largeResult + " (time: " + (endTime - startTime) + "ms)");

        // Test with very large h
        System.out.println("Large h test: " + solution.minEatingSpeedLargeH(piles1, 1000000)); // Expected: 1
    }
}
