package searching.medium;

/**
 * LeetCode 875: Koko Eating Bananas
 * https://leetcode.com/problems/koko-eating-bananas/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Given piles of bananas and an integer h, find the minimum eating speed k such
 * that Koko can eat all bananas within h hours.
 *
 * Constraints:
 * - 1 <= piles.length <= 10^4
 * - 1 <= piles[i] <= 10^9
 * - piles.length <= h <= 10^9
 *
 * Follow-ups:
 * 1. Can you return the actual schedule?
 * 2. What if Koko can eat at variable speeds?
 * 3. Can you handle multiple monkeys?
 */
public class KokoEatingBananas {
    public int minEatingSpeed(int[] piles, int h) {
        int left = 1, right = 0;
        for (int p : piles)
            right = Math.max(right, p);
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canEat(piles, h, mid))
                right = mid;
            else
                left = mid + 1;
        }
        return left;
    }

    private boolean canEat(int[] piles, int h, int k) {
        int hours = 0;
        for (int p : piles)
            hours += (p + k - 1) / k;
        return hours <= h;
    }

    // Follow-up 1: Return actual schedule (hours per pile)
    public int[] eatingSchedule(int[] piles, int k) {
        int[] schedule = new int[piles.length];
        for (int i = 0; i < piles.length; i++)
            schedule[i] = (piles[i] + k - 1) / k;
        return schedule;
    }

    // Follow-up 2: Variable speeds (array of speeds per hour)
    public int minHoursVariableSpeed(int[] piles, int[] speeds) {
        int hours = 0, idx = 0;
        for (int p : piles) {
            int k = speeds[idx % speeds.length];
            hours += (p + k - 1) / k;
            idx++;
        }
        return hours;
    }

    // Follow-up 3: Multiple monkeys (divide piles among monkeys)
    public int minEatingSpeedMultipleMonkeys(int[] piles, int h, int monkeys) {
        int left = 1, right = 0;
        for (int p : piles)
            right = Math.max(right, p);
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (canEatMultipleMonkeys(piles, h, mid, monkeys))
                right = mid;
            else
                left = mid + 1;
        }
        return left;
    }

    private boolean canEatMultipleMonkeys(int[] piles, int h, int k, int monkeys) {
        int hours = 0;
        for (int p : piles)
            hours += (p + k - 1) / k;
        return hours <= h * monkeys;
    }

    public static void main(String[] args) {
        KokoEatingBananas solution = new KokoEatingBananas();
        // Basic case
        int[] piles1 = { 3, 6, 7, 11 };
        System.out.println("Basic: " + solution.minEatingSpeed(piles1, 8)); // 4
        // Edge: Single pile
        int[] piles2 = { 30 };
        System.out.println("Single pile: " + solution.minEatingSpeed(piles2, 5)); // 6
        // Edge: All piles same
        int[] piles3 = { 5, 5, 5, 5 };
        System.out.println("All same: " + solution.minEatingSpeed(piles3, 4)); // 5
        // Edge: h == piles.length
        System.out.println("h == piles.length: " + solution.minEatingSpeed(piles1, 4)); // 11
        // Follow-up 1: Actual schedule
        System.out.println("Schedule: " + java.util.Arrays.toString(solution.eatingSchedule(piles1, 4))); // [1,2,2,3]
        // Follow-up 2: Variable speeds
        int[] speeds = { 2, 3 };
        System.out.println("Variable speeds: " + solution.minHoursVariableSpeed(piles1, speeds)); // 7
        // Follow-up 3: Multiple monkeys
        System.out.println("Multiple monkeys: " + solution.minEatingSpeedMultipleMonkeys(piles1, 4, 2)); // 6
    }
}
