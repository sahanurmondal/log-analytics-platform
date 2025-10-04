package bitmanipulation.medium;

/**
 * LeetCode 477: Total Hamming Distance
 * https://leetcode.com/problems/total-hamming-distance/
 *
 * Description: The Hamming distance between two integers is the number of
 * positions at which the corresponding bits are different.
 * Given an integer array nums, return the sum of Hamming distances between all
 * the pairs of integers in nums.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - 0 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(n) time per bit?
 * - What about avoiding O(n^2) brute force?
 * 
 * Time Complexity: O(n * 32)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook
 */
public class TotalHammingDistance {

    // Main optimized solution - Count bits at each position
    public int totalHammingDistance(int[] nums) {
        int totalDistance = 0;
        int n = nums.length;

        // Check each bit position
        for (int i = 0; i < 32; i++) {
            int countOnes = 0;

            // Count how many numbers have 1 at position i
            for (int num : nums) {
                countOnes += (num >> i) & 1;
            }

            // Numbers with 0 at this position
            int countZeros = n - countOnes;

            // Each pair (one with 0, one with 1) contributes 1 to Hamming distance
            totalDistance += countOnes * countZeros;
        }

        return totalDistance;
    }

    // Brute force solution - For comparison (O(n^2))
    public int totalHammingDistanceBruteForce(int[] nums) {
        int totalDistance = 0;

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                totalDistance += Integer.bitCount(nums[i] ^ nums[j]);
            }
        }

        return totalDistance;
    }

    public static void main(String[] args) {
        TotalHammingDistance solution = new TotalHammingDistance();

        System.out.println(solution.totalHammingDistance(new int[] { 4, 14, 2 })); // Expected: 6
        System.out.println(solution.totalHammingDistance(new int[] { 4, 14, 4 })); // Expected: 4
        System.out.println(solution.totalHammingDistanceBruteForce(new int[] { 4, 14, 2 })); // Expected: 6
    }
}
