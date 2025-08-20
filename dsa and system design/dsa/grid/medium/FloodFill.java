package grid.medium;

/**
 * LeetCode 733: Flood Fill
 * https://leetcode.com/problems/flood-fill/
 *
 * Description:
 * An image is represented by an m x n integer grid image where image[i][j]
 * represents the pixel value of the image.
 * You are also given three integers sr, sc, and color. You should perform a
 * flood fill on the image starting from the pixel image[sr][sc].
 * To perform a flood fill, consider the starting pixel, plus any pixels
 * connected 4-directionally to the starting pixel of the same color as the
 * starting pixel,
 * plus any pixels connected 4-directionally to those pixels (also with the same
 * color), and so on.
 * Replace the color of all of the aforementioned pixels with color.
 * Return the modified image after performing the flood fill.
 *
 * Constraints:
 * - m == image.length
 * - n == image[i].length
 * - 1 <= m, n <= 50
 * - 0 <= image[i][j], color < 2^16
 * - 0 <= sr < m
 * - 0 <= sc < n
 */
public class FloodFill {

    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        if (image[sr][sc] == color)
            return image;

        int originalColor = image[sr][sc];
        dfs(image, sr, sc, originalColor, color);
        return image;
    }

    private void dfs(int[][] image, int r, int c, int originalColor, int newColor) {
        if (r < 0 || r >= image.length || c < 0 || c >= image[0].length ||
                image[r][c] != originalColor) {
            return;
        }

        image[r][c] = newColor;

        dfs(image, r + 1, c, originalColor, newColor);
        dfs(image, r - 1, c, originalColor, newColor);
        dfs(image, r, c + 1, originalColor, newColor);
        dfs(image, r, c - 1, originalColor, newColor);
    }

    public static void main(String[] args) {
        FloodFill solution = new FloodFill();

        int[][] image = { { 1, 1, 1 }, { 1, 1, 0 }, { 1, 0, 1 } };
        int[][] result = solution.floodFill(image, 1, 1, 2);

        for (int[] row : result) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }
}
