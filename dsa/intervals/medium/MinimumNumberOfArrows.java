package intervals.medium;

import java.util.*;

/**
 * LeetCode 452: Minimum Number of Arrows to Burst Balloons
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, Goldman Sachs
 * Frequency: High (Asked in 250+ interviews)
 *
 * Description:
 * There are some spherical balloons taped onto a flat wall that represents the
 * XY-plane.
 * The balloons are represented as a 2D integer array points where points[i] =
 * [xstart, xend]
 * denotes a balloon whose horizontal diameter stretches from xstart to xend.
 * You do not know
 * the exact y-coordinates of the balloons.
 * 
 * Arrows can be shot up directly vertically (in the positive y-direction) from
 * different
 * points along the x-axis. A balloon with xstart and xend is burst by an arrow
 * shot at x
 * if xstart <= x <= xend. There is no limit to the number of balloons an arrow
 * can burst.
 * 
 * Given the array points, return the minimum number of arrows needed to burst
 * all balloons.
 *
 * Constraints:
 * - 1 <= points.length <= 10^5
 * - points[i].length == 2
 * - -2^31 <= xstart < xend <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. What if arrows have limited range and can only burst balloons within that
 * range?
 * 2. Can you return the actual positions where arrows should be shot?
 * 3. What if balloons have different priorities/values?
 * 4. How would you handle 2D balloons (circles)?
 */
public class MinimumNumberOfArrows {

    // Approach 1: Greedy by End Points - O(n log n) time, O(1) space
    public int findMinArrowShots(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        // Sort by end points
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrows = 1;
        int arrowPosition = points[0][1];

        for (int i = 1; i < points.length; i++) {
            // If current balloon starts after arrow position, need new arrow
            if (points[i][0] > arrowPosition) {
                arrows++;
                arrowPosition = points[i][1];
            }
        }

        return arrows;
    }

    // Approach 2: Greedy by Start Points - O(n log n) time, O(1) space
    public int findMinArrowShotsStartSort(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        // Sort by start points
        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));

        int arrows = 1;
        int arrowEnd = points[0][1];

        for (int i = 1; i < points.length; i++) {
            if (points[i][0] <= arrowEnd) {
                // Overlapping - update arrow end to minimum of current range
                arrowEnd = Math.min(arrowEnd, points[i][1]);
            } else {
                // No overlap - need new arrow
                arrows++;
                arrowEnd = points[i][1];
            }
        }

        return arrows;
    }

    // Approach 3: Interval Merging Approach - O(n log n) time, O(n) space
    public int findMinArrowShotsMerge(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        // Sort by start points
        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        merged.add(points[0]);

        for (int i = 1; i < points.length; i++) {
            int[] current = points[i];
            int[] last = merged.get(merged.size() - 1);

            if (current[0] <= last[1]) {
                // Overlapping - merge by taking intersection
                last[0] = Math.max(last[0], current[0]);
                last[1] = Math.min(last[1], current[1]);
            } else {
                // Non-overlapping - add new interval
                merged.add(current);
            }
        }

        return merged.size();
    }

    // Follow-up 1: Limited range arrows
    public int findMinArrowShotsLimitedRange(int[][] points, int arrowRange) {
        if (points == null || points.length == 0) {
            return 0;
        }

        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));

        int arrows = 0;
        int i = 0;

        while (i < points.length) {
            arrows++;
            int arrowStart = points[i][0];
            int arrowEnd = arrowStart + arrowRange;

            // Burst all balloons within arrow range
            while (i < points.length && points[i][0] <= arrowEnd && points[i][1] >= arrowStart) {
                // Update effective range to intersection
                arrowStart = Math.max(arrowStart, points[i][0]);
                arrowEnd = Math.min(arrowEnd, points[i][1]);
                i++;
            }
        }

        return arrows;
    }

    // Follow-up 2: Return arrow positions
    public List<Integer> findArrowPositions(int[][] points) {
        List<Integer> positions = new ArrayList<>();

        if (points == null || points.length == 0) {
            return positions;
        }

        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrowPosition = points[0][1];
        positions.add(arrowPosition);

        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > arrowPosition) {
                arrowPosition = points[i][1];
                positions.add(arrowPosition);
            }
        }

        return positions;
    }

    // Follow-up 3: Balloons with priorities/values
    public int findMinArrowShotsWithPriority(int[][] points, int[] values, int targetValue) {
        if (points == null || points.length == 0) {
            return 0;
        }

        // Create balloon objects with values
        List<Balloon> balloons = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            balloons.add(new Balloon(points[i][0], points[i][1], values[i]));
        }

        // Sort by value density (value per unit length) descending, then by end point
        balloons.sort((a, b) -> {
            double densityA = (double) a.value / (a.end - a.start + 1);
            double densityB = (double) b.value / (b.end - b.start + 1);
            if (Math.abs(densityA - densityB) > 1e-9) {
                return Double.compare(densityB, densityA);
            }
            return Integer.compare(a.end, b.end);
        });

        int arrows = 0;
        int totalValue = 0;
        boolean[] burst = new boolean[balloons.size()];

        while (totalValue < targetValue) {
            arrows++;

            // Find best position for next arrow
            int bestPosition = findBestArrowPosition(balloons, burst);

            // Burst all balloons that can be hit by this arrow
            for (int i = 0; i < balloons.size(); i++) {
                if (!burst[i] && balloons.get(i).start <= bestPosition && bestPosition <= balloons.get(i).end) {
                    burst[i] = true;
                    totalValue += balloons.get(i).value;
                }
            }
        }

        return arrows;
    }

    private int findBestArrowPosition(List<Balloon> balloons, boolean[] burst) {
        Map<Integer, Integer> positionValue = new HashMap<>();

        for (int i = 0; i < balloons.size(); i++) {
            if (burst[i])
                continue;

            Balloon balloon = balloons.get(i);
            for (int pos = balloon.start; pos <= balloon.end; pos++) {
                positionValue.put(pos, positionValue.getOrDefault(pos, 0) + balloon.value);
            }
        }

        return positionValue.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    static class Balloon {
        int start, end, value;

        Balloon(int start, int end, int value) {
            this.start = start;
            this.end = end;
            this.value = value;
        }
    }

    // Follow-up 4: 2D Balloons (circles)
    public int findMinArrowShots2D(int[][] circles, int arrowWidth) {
        // circles[i] = [x, y, radius]
        if (circles == null || circles.length == 0) {
            return 0;
        }

        // Convert circles to intervals based on x-coordinate projection
        List<Circle> balloons = new ArrayList<>();
        for (int[] circle : circles) {
            int x = circle[0], y = circle[1], r = circle[2];
            balloons.add(new Circle(x, y, r));
        }

        // Sort by x-coordinate
        balloons.sort((a, b) -> Integer.compare(a.x, b.x));

        int arrows = 0;
        boolean[] burst = new boolean[balloons.size()];

        for (int i = 0; i < balloons.size(); i++) {
            if (burst[i])
                continue;

            arrows++;
            Circle current = balloons.get(i);

            // Try to find best arrow position to hit maximum balloons
            int arrowX = current.x;

            // Burst all balloons within arrow width
            for (int j = i; j < balloons.size(); j++) {
                if (Math.abs(balloons.get(j).x - arrowX) <= arrowWidth &&
                        canBurstCircle(balloons.get(j), arrowX, arrowWidth)) {
                    burst[j] = true;
                }
            }
        }

        return arrows;
    }

    private boolean canBurstCircle(Circle circle, int arrowX, int arrowWidth) {
        // Check if arrow at arrowX with width can burst the circle
        int minX = arrowX - arrowWidth / 2;
        int maxX = arrowX + arrowWidth / 2;

        // Check if arrow range overlaps with circle's x-range
        int circleMinX = circle.x - circle.radius;
        int circleMaxX = circle.x + circle.radius;

        return !(maxX < circleMinX || minX > circleMaxX);
    }

    static class Circle {
        int x, y, radius;

        Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    // Helper: Validate input and analyze statistics
    public Map<String, Object> analyzeInput(int[][] points) {
        Map<String, Object> analysis = new HashMap<>();

        if (points == null || points.length == 0) {
            analysis.put("empty", true);
            return analysis;
        }

        analysis.put("balloonCount", points.length);

        int totalWidth = 0;
        int maxWidth = 0;
        int minWidth = Integer.MAX_VALUE;
        int minStart = Integer.MAX_VALUE;
        int maxEnd = Integer.MIN_VALUE;

        for (int[] point : points) {
            int width = point[1] - point[0] + 1;
            totalWidth += width;
            maxWidth = Math.max(maxWidth, width);
            minWidth = Math.min(minWidth, width);
            minStart = Math.min(minStart, point[0]);
            maxEnd = Math.max(maxEnd, point[1]);
        }

        analysis.put("averageWidth", (double) totalWidth / points.length);
        analysis.put("maxWidth", maxWidth);
        analysis.put("minWidth", minWidth);
        analysis.put("totalRange", maxEnd - minStart + 1);
        analysis.put("minStart", minStart);
        analysis.put("maxEnd", maxEnd);

        // Count overlaps
        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));
        int overlaps = 0;
        for (int i = 1; i < points.length; i++) {
            if (points[i][0] <= points[i - 1][1]) {
                overlaps++;
            }
        }
        analysis.put("overlappingPairs", overlaps);

        return analysis;
    }

    // Helper: Visualize balloon positions
    public String visualizeBalloons(int[][] points, int maxWidth) {
        if (points == null || points.length == 0)
            return "";

        int minStart = Arrays.stream(points).mapToInt(p -> p[0]).min().orElse(0);
        int maxEnd = Arrays.stream(points).mapToInt(p -> p[1]).max().orElse(0);

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < points.length && i < 10; i++) { // Limit to 10 balloons for visualization
            sb.append("Balloon ").append(i + 1).append(": ");

            int start = points[i][0] - minStart;
            int end = points[i][1] - minStart;

            for (int j = 0; j <= Math.min(maxEnd - minStart, maxWidth); j++) {
                if (j >= start && j <= end) {
                    sb.append("■");
                } else {
                    sb.append("·");
                }
            }
            sb.append(" [").append(points[i][0]).append(",").append(points[i][1]).append("]\n");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        MinimumNumberOfArrows solution = new MinimumNumberOfArrows();

        // Test Case 1: Standard case
        int[][] points1 = { { 10, 16 }, { 2, 8 }, { 1, 6 }, { 7, 12 } };

        int result1 = solution.findMinArrowShots(points1);
        System.out.println("Test 1 - Min arrows needed: " + result1); // Expected: 2

        // Compare different approaches
        int result1Start = solution.findMinArrowShotsStartSort(points1.clone());
        int result1Merge = solution.findMinArrowShotsMerge(points1.clone());

        System.out.println("Start sort approach: " + result1Start);
        System.out.println("Merge approach: " + result1Merge);
        System.out.println("All approaches consistent: " +
                (result1 == result1Start && result1Start == result1Merge));

        // Test Case 2: No overlaps
        int[][] points2 = { { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } };
        int result2 = solution.findMinArrowShots(points2);
        System.out.println("Test 2 - No overlaps: " + result2); // Expected: 4

        // Test Case 3: All overlapping
        int[][] points3 = { { 1, 10 }, { 2, 9 }, { 3, 8 }, { 4, 7 } };
        int result3 = solution.findMinArrowShots(points3);
        System.out.println("Test 3 - All overlapping: " + result3); // Expected: 1

        // Test Case 4: Edge case with same points
        int[][] points4 = { { 1, 1 }, { 2, 2 }, { 3, 3 } };
        int result4 = solution.findMinArrowShots(points4);
        System.out.println("Test 4 - Same start/end points: " + result4); // Expected: 3

        // Test Case 5: Large numbers (edge of constraints)
        int[][] points5 = { { -2147483646, -2147483645 }, { 2147483646, 2147483647 } };
        int result5 = solution.findMinArrowShots(points5);
        System.out.println("Test 5 - Large numbers: " + result5); // Expected: 2

        // Follow-up 1: Limited range arrows
        System.out.println("\n=== Follow-up 1: Limited Range Arrows ===");
        int limitedResult = solution.findMinArrowShotsLimitedRange(points1, 5);
        System.out.println("With arrow range 5: " + limitedResult);

        // Follow-up 2: Arrow positions
        System.out.println("\n=== Follow-up 2: Arrow Positions ===");
        List<Integer> positions = solution.findArrowPositions(points1);
        System.out.println("Arrow positions: " + positions);

        // Verify positions work
        boolean allBurst = true;
        for (int[] balloon : points1) {
            boolean canBurst = false;
            for (int pos : positions) {
                if (pos >= balloon[0] && pos <= balloon[1]) {
                    canBurst = true;
                    break;
                }
            }
            if (!canBurst) {
                allBurst = false;
                break;
            }
        }
        System.out.println("All balloons can be burst by found positions: " + allBurst);

        // Follow-up 3: Priority-based
        System.out.println("\n=== Follow-up 3: Priority-based Arrows ===");
        int[] values = { 5, 10, 3, 8 };
        int priorityResult = solution.findMinArrowShotsWithPriority(points1, values, 20);
        System.out.println("Arrows needed for target value 20: " + priorityResult);

        // Follow-up 4: 2D Balloons
        System.out.println("\n=== Follow-up 4: 2D Balloons ===");
        int[][] circles = { { 0, 0, 3 }, { 2, 2, 2 }, { 4, 4, 1 } };
        int arrows2D = solution.findMinArrowShots2D(circles, 2);
        System.out.println("2D arrows needed: " + arrows2D);

        // Analysis and visualization
        System.out.println("\n=== Input Analysis ===");
        Map<String, Object> analysis = solution.analyzeInput(points1);
        analysis.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n=== Visualization ===");
        String visualization = solution.visualizeBalloons(points1, 50);
        System.out.println(visualization);

        // Performance testing
        System.out.println("=== Performance Testing ===");

        // Generate large test case
        int[][] largePoints = new int[10000][2];
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(1000000);
            int end = start + random.nextInt(1000) + 1;
            largePoints[i] = new int[] { start, end };
        }

        long start = System.nanoTime();
        int endSortResult = solution.findMinArrowShots(largePoints.clone());
        long endSortTime = System.nanoTime() - start;

        start = System.nanoTime();
        int startSortResult = solution.findMinArrowShotsStartSort(largePoints.clone());
        long startSortTime = System.nanoTime() - start;

        start = System.nanoTime();
        int mergeResult = solution.findMinArrowShotsMerge(largePoints.clone());
        long mergeTime = System.nanoTime() - start;

        System.out.println("End sort: " + endSortResult + " (" + endSortTime / 1_000_000.0 + " ms)");
        System.out.println("Start sort: " + startSortResult + " (" + startSortTime / 1_000_000.0 + " ms)");
        System.out.println("Merge: " + mergeResult + " (" + mergeTime / 1_000_000.0 + " ms)");
        System.out.println("All results consistent: " +
                (endSortResult == startSortResult && startSortResult == mergeResult));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Single balloon
        int[][] single = { { 1, 5 } };
        System.out.println("Single balloon: " + solution.findMinArrowShots(single));

        // Touching balloons
        int[][] touching = { { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("Touching balloons: " + solution.findMinArrowShots(touching));

        // Nested balloons
        int[][] nested = { { 1, 10 }, { 2, 9 }, { 3, 8 }, { 4, 7 }, { 5, 6 } };
        System.out.println("Nested balloons: " + solution.findMinArrowShots(nested));

        // Very large range with small balloons
        int[][] sparseRange = { { 1, 2 }, { 1000000, 1000001 } };
        System.out.println("Sparse range: " + solution.findMinArrowShots(sparseRange));
    }
}
