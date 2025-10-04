package math.hard;

/**
 * LeetCode 391: Perfect Rectangle
 * https://leetcode.com/problems/perfect-rectangle/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Given N axis-aligned rectangles, determine if they collectively form a
 * perfect rectangle.
 *
 * Constraints:
 * - 1 <= N <= 2 * 10^4
 *
 * Follow-ups:
 * 1. Can you find the area of the union?
 * 2. Can you find the perimeter?
 * 3. Can you handle floating point rectangles?
 */
public class PerfectRectangle {
    public boolean isRectangleCover(int[][] rectangles) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        int area = 0;
        java.util.Set<String> corners = new java.util.HashSet<>();
        for (int[] r : rectangles) {
            minX = Math.min(minX, r[0]);
            minY = Math.min(minY, r[1]);
            maxX = Math.max(maxX, r[2]);
            maxY = Math.max(maxY, r[3]);
            area += (r[2] - r[0]) * (r[3] - r[1]);
            String[] pts = {
                    r[0] + " " + r[1], r[0] + " " + r[3],
                    r[2] + " " + r[1], r[2] + " " + r[3]
            };
            for (String p : pts) {
                if (!corners.add(p))
                    corners.remove(p);
            }
        }
        if (corners.size() != 4 ||
                !corners.contains(minX + " " + minY) ||
                !corners.contains(minX + " " + maxY) ||
                !corners.contains(maxX + " " + minY) ||
                !corners.contains(maxX + " " + maxY))
            return false;
        return area == (maxX - minX) * (maxY - minY);
    }

    // Follow-up 1: Area of union (already computed above)
    public int areaOfUnion(int[][] rectangles) {
        int area = 0;
        for (int[] r : rectangles)
            area += (r[2] - r[0]) * (r[3] - r[1]);
        return area;
    }

    // Follow-up 2: Perimeter of union (sweep line)
    public int perimeterOfUnion(int[][] rectangles) {
        // ...complex sweep line implementation...
        return -1; // Placeholder
    }

    // Follow-up 3: Floating point rectangles
    public boolean isRectangleCoverFloat(double[][] rectangles) {
        // ...similar logic with double precision...
        return false; // Placeholder
    }

    public static void main(String[] args) {
        PerfectRectangle solution = new PerfectRectangle();
        int[][] rects = { { 1, 1, 3, 3 }, { 3, 1, 4, 2 }, { 3, 2, 4, 4 }, { 1, 3, 2, 4 }, { 2, 3, 3, 4 } };
        System.out.println(solution.isRectangleCover(rects)); // true
        System.out.println(solution.areaOfUnion(rects)); // 10
    }
}
