package graphs.easy;

/**
 * LeetCode 733: Flood Fill
 * https://leetcode.com/problems/flood-fill/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given an image and a starting pixel, fill the connected area
 * with a new color.
 *
 * Constraints:
 * - 1 <= image.length, image[0].length <= 50
 * 
 * Follow-up Questions:
 * 1. Can you solve with BFS?
 * 2. Can you solve with DFS?
 */
public class FloodFill {
    // Approach 1: DFS - O(mn) time, O(mn) space
    public int[][] floodFillDFS(int[][] image, int sr, int sc, int color) {
        int m = image.length, n = image[0].length, orig = image[sr][sc];
        if (orig == color)
            return image;
        dfs(image, sr, sc, orig, color, m, n);
        return image;
    }

    private void dfs(int[][] img, int i, int j, int orig, int color, int m, int n) {
        if (i < 0 || j < 0 || i >= m || j >= n || img[i][j] != orig)
            return;
        img[i][j] = color;
        dfs(img, i + 1, j, orig, color, m, n);
        dfs(img, i - 1, j, orig, color, m, n);
        dfs(img, i, j + 1, orig, color, m, n);
        dfs(img, i, j - 1, orig, color, m, n);
    }

    // Approach 2: BFS
    public int[][] floodFillBFS(int[][] image, int sr, int sc, int color) {
        int m = image.length, n = image[0].length, orig = image[sr][sc];
        if (orig == color)
            return image;
        java.util.Queue<int[]> q = new java.util.LinkedList<>();
        q.offer(new int[] { sr, sc });
        image[sr][sc] = color;
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            for (int[] d : dirs) {
                int ni = cur[0] + d[0], nj = cur[1] + d[1];
                if (ni >= 0 && nj >= 0 && ni < m && nj < n && image[ni][nj] == orig) {
                    image[ni][nj] = color;
                    q.offer(new int[] { ni, nj });
                }
            }
        }
        return image;
    }

    public static void main(String[] args) {
        FloodFill ff = new FloodFill();
        int[][] img = { { 1, 1, 1 }, { 1, 1, 0 }, { 1, 0, 1 } };
        int[][] res = ff.floodFillDFS(img, 1, 1, 2);
        for (int[] row : res)
            System.out.print(java.util.Arrays.toString(row) + " ");
        System.out.println();

        int[][] img2 = { { 0, 0, 0 }, { 0, 0, 0 } };
        res = ff.floodFillBFS(img2, 0, 0, 2);
        for (int[] row : res)
            System.out.print(java.util.Arrays.toString(row) + " ");
        System.out.println();

        // No change if color is same
        int[][] img3 = { { 1, 1 }, { 1, 1 } };
        res = ff.floodFillDFS(img3, 0, 0, 1);
        System.out.println(res[0][0] == 1);

        // Single pixel
        int[][] img4 = { { 5 } };
        res = ff.floodFillBFS(img4, 0, 0, 7);
        System.out.println(res[0][0] == 7);
    }
}
