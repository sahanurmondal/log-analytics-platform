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
 * If the pile has less than k bananas, she eats all of them instead and will
 * not eat any more bananas during this hour.
 * Koko likes to eat slowly but still wants to finish eating all the bananas
 * before the guards come back.
 * Return the minimum integer k such that she can eat all the bananas within h
 * hours.
 *
 * Companies: Google, Facebook, Amazon, Microsoft, Bloomberg, Adobe, Uber,
 * DoorDash
 * Difficulty: Medium
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= piles.length <= 10^4
 * - piles[i] <= 10^9
 * - 1 <= h <= 10^9
 *
 * Follow-ups:
 * - What if Koko can eat from multiple piles in one hour?
 * - How to minimize the maximum eating time per pile?
 * - Can you solve this with ternary search?
 */
public class KokoEatingBananas {

    // Binary Search on Answer - O(n * log(max)) time, O(1) space
    public int minEatingSpeed(int[] piles, int h) {
        if (piles == null || piles.length == 0 || h <= 0) {
            return 0;
        }

        int left = 1; // Minimum possible speed
        int right = getMaxPile(piles); // Maximum possible speed

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canEatAllBananas(piles, h, mid)) {
                right = mid; // Try slower speed
            } else {
                left = mid + 1; // Need faster speed
            }
        }

        return left;
    }

    private boolean canEatAllBananas(int[] piles, int h, int speed) {
        long hoursNeeded = 0;

        for (int pile : piles) {
            // Math.ceil(pile / speed) = (pile + speed - 1) / speed
            hoursNeeded += (pile + speed - 1) / speed;

            if (hoursNeeded > h) {
                return false; // Early termination
            }
        }

        return hoursNeeded <= h;
    }

    private int getMaxPile(int[] piles) {
        int max = piles[0];
        for (int pile : piles) {
            max = Math.max(max, pile);
        }
        return max;
    }

    // Alternative implementation with explicit result tracking
    public int minEatingSpeedAlt(int[] piles, int h) {
        if (piles == null || piles.length == 0 || h <= 0) {
            return 0;
        }

        int left = 1;
        int right = getMaxPile(piles);
        int result = right;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (canEatAllBananas(piles, h, mid)) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Optimized bounds calculation
    public int minEatingSpeedOptimized(int[] piles, int h) {
        if (piles == null || piles.length == 0 || h <= 0) {
            return 0;
        }

        long totalBananas = getTotalBananas(piles);
        int maxPile = getMaxPile(piles);

        // Better lower bound: ceiling of (total bananas / h)
        int left = Math.max(1, (int) ((totalBananas + h - 1) / h));
        int right = maxPile;

        // If we have more hours than piles, minimum speed is 1
        if (h >= totalBananas) {
            return 1;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canEatAllBananas(piles, h, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private long getTotalBananas(int[] piles) {
        long total = 0;
        for (int pile : piles) {
            total += pile;
        }
        return total;
    }

    // Calculate exact hours needed for given speed
    public long calculateHoursNeeded(int[] piles, int speed) {
        if (speed <= 0) {
            return Long.MAX_VALUE;
        }

        long hours = 0;
        for (int pile : piles) {
            hours += (pile + speed - 1) / speed;
        }

        return hours;
    }

    // Find maximum speed needed (when h = piles.length)
    public int maxSpeedNeeded(int[] piles) {
        return getMaxPile(piles);
    }

    // Find minimum speed needed (when h is very large)
    public int minSpeedPossible(int[] piles) {
        return 1;
    }

    // Get eating schedule for given speed
    public int[][] getEatingSchedule(int[] piles, int speed) {
        java.util.List<java.util.List<Integer>> schedule = new java.util.ArrayList<>();

        for (int i = 0; i < piles.length; i++) {
            int pile = piles[i];
            int hoursForThisPile = (pile + speed - 1) / speed;

            for (int hour = 0; hour < hoursForThisPile; hour++) {
                java.util.List<Integer> hourData = new java.util.ArrayList<>();
                hourData.add(i); // Pile index

                int bananasThisHour = Math.min(speed, pile - hour * speed);
                if (bananasThisHour > 0) {
                    hourData.add(bananasThisHour); // Bananas eaten this hour
                    schedule.add(hourData);
                }
            }
        }

        // Convert to 2D array
        int[][] result = new int[schedule.size()][2];
        for (int i = 0; i < schedule.size(); i++) {
            result[i][0] = schedule.get(i).get(0); // Pile index
            result[i][1] = schedule.get(i).get(1); // Bananas eaten
        }

        return result;
    }

    // Greedy approach for comparison - O(n * max) time
    public int minEatingSpeedGreedy(int[] piles, int h) {
        int maxPile = getMaxPile(piles);

        for (int speed = 1; speed <= maxPile; speed++) {
            if (canEatAllBananas(piles, h, speed)) {
                return speed;
            }
        }

        return maxPile;
    }

    // Ternary search approach
    public int minEatingSpeedTernary(int[] piles, int h) {
        if (piles == null || piles.length == 0 || h <= 0) {
            return 0;
        }

        int left = 1;
        int right = getMaxPile(piles);

        while (right - left > 2) {
            int mid1 = left + (right - left) / 3;
            int mid2 = right - (right - left) / 3;

            long hours1 = calculateHoursNeeded(piles, mid1);
            long hours2 = calculateHoursNeeded(piles, mid2);

            if (hours1 <= h && hours2 <= h) {
                // Both speeds work, try smaller speeds
                right = mid2;
            } else if (hours1 > h && hours2 > h) {
                // Both speeds don't work, try larger speeds
                left = mid1;
            } else if (hours1 <= h) {
                // mid1 works but mid2 doesn't
                right = mid2;
            } else {
                // mid2 works but mid1 doesn't
                left = mid1;
            }
        }

        // Check remaining candidates
        for (int speed = left; speed <= right; speed++) {
            if (canEatAllBananas(piles, h, speed)) {
                return speed;
            }
        }

        return right;
    }

    // Find speed range that works
    public int[] getSpeedRange(int[] piles, int h) {
        int minSpeed = minEatingSpeed(piles, h);
        int maxSpeed = getMaxPile(piles);

        return new int[] { minSpeed, maxSpeed };
    }

    // Validate solution
    public boolean validateSolution(int[] piles, int h, int speed) {
        // Check if speed can finish within h hours
        if (!canEatAllBananas(piles, h, speed)) {
            return false;
        }

        // Check if smaller speed fails (if speed > 1)
        if (speed > 1 && canEatAllBananas(piles, h, speed - 1)) {
            return false;
        }

        return true;
    }

    // Handle edge cases
    public int minEatingSpeedRobust(int[] piles, int h) {
        if (piles == null || piles.length == 0) {
            return 0;
        }

        if (h <= 0) {
            return -1; // Invalid input
        }

        if (h >= getTotalBananas(piles)) {
            return 1; // Can eat 1 banana per hour
        }

        if (h < piles.length) {
            return -1; // Impossible - not enough hours
        }

        return minEatingSpeed(piles, h);
    }

    // Find minimum hours needed for given speed
    public long minHoursNeeded(int[] piles, int speed) {
        return calculateHoursNeeded(piles, speed);
    }

    // Find all valid speeds within range
    public java.util.List<Integer> findAllValidSpeeds(int[] piles, int h, int maxSpeed) {
        java.util.List<Integer> validSpeeds = new java.util.ArrayList<>();

        for (int speed = 1; speed <= maxSpeed; speed++) {
            if (canEatAllBananas(piles, h, speed)) {
                validSpeeds.add(speed);
            }
        }

        return validSpeeds;
    }

    // Calculate efficiency (bananas per hour used)
    public double calculateEfficiency(int[] piles, int h, int speed) {
        long hoursNeeded = calculateHoursNeeded(piles, speed);
        if (hoursNeeded > h) {
            return 0.0; // Invalid
        }

        long totalBananas = getTotalBananas(piles);
        return (double) totalBananas / hoursNeeded;
    }

    public static void main(String[] args) {
        KokoEatingBananas solution = new KokoEatingBananas();

        // Test Case 1: [3,6,7,11], h = 8
        int[] piles1 = { 3, 6, 7, 11 };
        System.out.println(solution.minEatingSpeed(piles1, 8)); // Expected: 4

        // Test Case 2: [30,11,23,4,20], h = 5
        int[] piles2 = { 30, 11, 23, 4, 20 };
        System.out.println(solution.minEatingSpeed(piles2, 5)); // Expected: 30

        // Test Case 3: [30,11,23,4,20], h = 6
        System.out.println(solution.minEatingSpeed(piles2, 6)); // Expected: 23

        // Test Case 4: Single pile
        int[] piles4 = { 10 };
        System.out.println(solution.minEatingSpeed(piles4, 3)); // Expected: 4

        // Test Case 5: Minimum hours
        int[] piles5 = { 1, 2, 3 };
        System.out.println(solution.minEatingSpeed(piles5, 3)); // Expected: 3

        // Test alternative implementations
        System.out.println("Alternative: " + solution.minEatingSpeedAlt(piles1, 8)); // Expected: 4
        System.out.println("Optimized: " + solution.minEatingSpeedOptimized(piles1, 8)); // Expected: 4
        System.out.println("Ternary: " + solution.minEatingSpeedTernary(piles1, 8)); // Expected: 4

        // Test hours calculation
        System.out.println("Hours needed (speed 4): " + solution.calculateHoursNeeded(piles1, 4)); // Expected: 8
        System.out.println("Hours needed (speed 3): " + solution.calculateHoursNeeded(piles1, 3)); // Expected: 10

        // Test eating schedule
        int[][] schedule = solution.getEatingSchedule(piles1, 4);
        System.out.println("Eating schedule (speed 4):");
        for (int i = 0; i < schedule.length; i++) {
            System.out.println("Hour " + (i + 1) + ": pile " + schedule[i][0] + ", eat " + schedule[i][1] + " bananas");
        }

        // Test speed range
        int[] range = solution.getSpeedRange(piles1, 8);
        System.out.println("Speed range: [" + range[0] + ", " + range[1] + "]"); // Expected: [4, 11]

        // Test validation
        int result = solution.minEatingSpeed(piles1, 8);
        System.out.println("Solution valid: " + solution.validateSolution(piles1, 8, result)); // Expected: true

        // Test robust version
        System.out.println("Robust: " + solution.minEatingSpeedRobust(piles1, 8)); // Expected: 4

        // Test greedy approach (small input only)
        System.out.println("Greedy: " + solution.minEatingSpeedGreedy(piles4, 3)); // Expected: 4

        // Test efficiency calculation
        double efficiency = solution.calculateEfficiency(piles1, 8, 4);
        System.out.println("Efficiency (speed 4): " + efficiency);

        // Test all valid speeds
        java.util.List<Integer> validSpeeds = solution.findAllValidSpeeds(piles1, 8, 15);
        System.out.println("Valid speeds: " + validSpeeds);

        // Edge cases
        int[] edge1 = { 1000000000 };
        System.out.println("Very large pile: " + solution.minEatingSpeed(edge1, 2)); // Expected: 500000000

        int[] edge2 = { 1, 1, 1, 1 };
        System.out.println("All small piles: " + solution.minEatingSpeed(edge2, 10)); // Expected: 1

        // Performance test
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = (i + 1) * 100;
        }

        long startTime = System.currentTimeMillis();
        int largeResult = solution.minEatingSpeed(large, 2000);
        long binaryTime = System.currentTimeMillis() - startTime;

        System.out.println("Large test result: " + largeResult + " (time: " + binaryTime + "ms)");

        // Compare different approaches performance
        int[] testPiles = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.minEatingSpeed(testPiles, 15);
        }
        long binarySearchTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.minEatingSpeedGreedy(testPiles, 15);
        }
        long greedyTime = System.currentTimeMillis() - startTime;

        System.out.println("Binary search time (1000 runs): " + binarySearchTime + "ms");
        System.out.println("Greedy time (1000 runs): " + greedyTime + "ms");

        // Test with different hour constraints
        int[] testHours = { piles1.length, piles1.length + 1, piles1.length + 5, piles1.length + 10, 100 };
        for (int h : testHours) {
            int speed = solution.minEatingSpeed(piles1, h);
            long hours = solution.calculateHoursNeeded(piles1, speed);
            System.out.println("Hours: " + h + ", Min speed: " + speed + ", Actual hours: " + hours);
        }

        // Stress test with various pile configurations
        int[][] testPiles2 = {
                { 1, 1, 1, 1, 1 },
                { 1, 2, 3, 4, 5 },
                { 5, 4, 3, 2, 1 },
                { 1, 100, 1, 100, 1 },
                { 50, 50, 50, 50 }
        };

        for (int[] piles : testPiles2) {
            int speed = solution.minEatingSpeed(piles, piles.length + 2);
            System.out.println("Piles: " + java.util.Arrays.toString(piles) +
                    ", Hours: " + (piles.length + 2) + ", Min speed: " + speed);
        }

        // Test boundary conditions
        int[] boundary1 = { 1 };
        System.out.println("Single pile, single hour: " + solution.minEatingSpeed(boundary1, 1)); // Expected: 1

        int[] boundary2 = { 2, 2 };
        System.out.println("Two piles, two hours: " + solution.minEatingSpeed(boundary2, 2)); // Expected: 2

        int[] boundary3 = { 1, 1000000 };
        System.out.println("Mixed sizes: " + solution.minEatingSpeed(boundary3, 3)); // Expected: 500000
    }
}
