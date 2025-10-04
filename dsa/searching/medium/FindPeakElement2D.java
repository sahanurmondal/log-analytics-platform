package searching.medium;

/**
 * LeetCode 1901: Find a Peak Element II
 * https://leetcode.com/problems/find-a-peak-element-ii/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a 2D matrix, find a peak element (greater than its neighbors).
 * Return the position [row, col] of any peak.
 *
 * Constraints:
 * - 1 <= matrix.length, matrix[0].length <= 500
 * - 1 <= matrix[i][j] <= 10^5
 *
 * Follow-ups:
 * 1. Can you find all peak elements?
 * 2. Can you handle plateaus?
 * 3. Can you find the topmost/leftmost peak?
 */
public class FindPeakElement2D {
    public int[] findPeakGrid(int[][] mat) {
        int rows = mat.length, cols = mat[0].length;
        int left = 0, right = cols - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int maxRow = 0;
            for (int i = 0; i < rows; i++)
                if (mat[i][mid] > mat[maxRow][mid])
                    maxRow = i;
            boolean leftIsBigger = mid > 0 && mat[maxRow][mid - 1] > mat[maxRow][mid];
            boolean rightIsBigger = mid < cols - 1 && mat[maxRow][mid + 1] > mat[maxRow][mid];
            if (!leftIsBigger && !rightIsBigger)
                return new int[] { maxRow, mid };
            else if (leftIsBigger)
                right = mid - 1;
            else
                left = mid + 1;
        }
        return new int[] { -1, -1 };
    }

    // Follow-up 1: Find all peak elements
    public java.util.List<int[]> findAllPeaks(int[][] mat) {
        java.util.List<int[]> peaks = new java.util.ArrayList<>();
        int rows = mat.length, cols = mat[0].length;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if ((i == 0 || mat[i][j] > mat[i - 1][j]) &&
                        (i == rows - 1 || mat[i][j] > mat[i + 1][j]) &&
                        (j == 0 || mat[i][j] > mat[i][j - 1]) &&
                        (j == cols - 1 || mat[i][j] > mat[i][j + 1]))
                    peaks.add(new int[] { i, j });
        return peaks;
    }

    // Follow-up 2: Handle plateaus (return all plateau peaks)
    public java.util.List<int[]> findPlateauPeaks(int[][] mat) {
        java.util.List<int[]> peaks = new java.util.ArrayList<>();
        int rows = mat.length, cols = mat[0].length;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                boolean up = (i == 0 || mat[i][j] >= mat[i - 1][j]);
                boolean down = (i == rows - 1 || mat[i][j] >= mat[i + 1][j]);
                boolean left = (j == 0 || mat[i][j] >= mat[i][j - 1]);
                boolean right = (j == cols - 1 || mat[i][j] >= mat[i][j + 1]);
                if (up && down && left && right)
                    peaks.add(new int[] { i, j });
            }
        return peaks;
    }

    // Follow-up 3: Topmost peak
    public int[] findTopmostPeak(int[][] mat) {
        java.util.List<int[]> peaks = findAllPeaks(mat);
        int minRow = Integer.MAX_VALUE, idx = -1;
        for (int i = 0; i < peaks.size(); i++) {
            if (peaks.get(i)[0] < minRow) {
                minRow = peaks.get(i)[0];
                idx = i;
            }
        }
        return idx == -1 ? new int[] { -1, -1 } : peaks.get(idx);
    }

    public static void main(String[] args) {
        FindPeakElement2D solution = new FindPeakElement2D();
        // Basic case
        int[][] mat1 = { { 1, 4 }, { 3, 2 } };
        System.out.println("Basic: " + java.util.Arrays.toString(solution.findPeakGrid(mat1))); // [0,1]
        // Edge: Single element
        int[][] mat2 = { { 10 } };
        System.out.println("Single element: " + java.util.Arrays.toString(solution.findPeakGrid(mat2))); // [0,0]
        // Edge: All increasing
        int[][] mat3 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        System.out.println("All increasing: " + java.util.Arrays.toString(solution.findPeakGrid(mat3))); // [2,2]
        // Follow-up 1: All peaks
        System.out.println("All peaks: " + solution.findAllPeaks(mat1));
        // Follow-up 2: Plateau peaks
        int[][] mat4 = { { 1, 2, 2 }, { 2, 2, 2 }, { 2, 2, 1 } };
        System.out.println("Plateau peaks: " + solution.findPlateauPeaks(mat4));
        // Follow-up 3: Topmost peak
        System.out.println("Topmost peak: " + java.util.Arrays.toString(solution.findTopmostPeak(mat3)));
    }
}
