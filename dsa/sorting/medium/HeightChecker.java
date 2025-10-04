package sorting.medium;

/**
 * LeetCode 1051: Height Checker
 * https://leetcode.com/problems/height-checker/
 *
 * Description:
 * A school is trying to take an annual photo of all the students. The students
 * are asked to stand in a single file line in non-decreasing order by height.
 * Let this ordering be represented by the integer array expected where
 * expected[i] is the expected height of the ith student in line.
 *
 * Constraints:
 * - 1 <= heights.length <= 100
 * - 1 <= heights[i] <= 100
 *
 * Follow-up:
 * - Can you solve it using counting sort since heights are bounded?
 * - Can you optimize for the case where most students are in correct positions?
 * - Can you solve it in a single pass?
 */
public class HeightChecker {
    public int heightChecker(int[] heights) {
        // Using counting sort since heights are bounded [1, 100]
        int[] count = new int[101];

        // Count frequency of each height
        for (int height : heights) {
            count[height]++;
        }

        int result = 0;
        int j = 1;

        // Compare with expected sorted order
        for (int i = 0; i < heights.length; i++) {
            // Find next smallest height
            while (count[j] == 0) {
                j++;
            }

            // If current height doesn't match expected position
            if (heights[i] != j) {
                result++;
            }

            count[j]--;
        }

        return result;
    }

    public static void main(String[] args) {
        HeightChecker solution = new HeightChecker();

        System.out.println(solution.heightChecker(new int[] { 1, 1, 4, 2, 1, 3 })); // 3
        System.out.println(solution.heightChecker(new int[] { 5, 1, 2, 3, 4 })); // 5
        System.out.println(solution.heightChecker(new int[] { 1, 2, 3, 4, 5 })); // 0

        // Edge Case: All same height
        System.out.println(solution.heightChecker(new int[] { 1, 1, 1, 1 })); // 0

        // Edge Case: Reverse sorted
        System.out.println(solution.heightChecker(new int[] { 5, 4, 3, 2, 1 })); // 4

        // Edge Case: Single student
        System.out.println(solution.heightChecker(new int[] { 1 })); // 0

        // Edge Case: Two students
        System.out.println(solution.heightChecker(new int[] { 2, 1 })); // 2

        // Edge Case: Almost sorted
        System.out.println(solution.heightChecker(new int[] { 1, 2, 4, 3, 5 })); // 2
    }
}
