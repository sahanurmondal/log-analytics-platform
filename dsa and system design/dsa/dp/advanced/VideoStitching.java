package dp.advanced;

import java.util.*;

/**
 * LeetCode 1024: Video Stitching
 * https://leetcode.com/problems/video-stitching/
 *
 * Description:
 * You are given a series of video clips from a sporting event that lasted time
 * seconds.
 * These video clips can be overlapping with each other and have varying
 * lengths.
 * Each video clip clips[i] = [starti, endi] indicates that the ith clip started
 * at starti and ended at endi.
 * We can cut these clips into segments freely.
 * Return the minimum number of clips needed so that we can cut and rearrange
 * the clips to cover the entire sporting event ([0, time]).
 * If the task is impossible, return -1.
 *
 * Constraints:
 * - 1 <= clips.length <= 100
 * - 0 <= starti <= endi <= 100
 * - 1 <= time <= 100
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 * - What if we need to find the actual clips used?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class VideoStitching {

    // Approach 1: DP - O(time * clips.length) time, O(time) space
    public int videoStitching(int[][] clips, int time) {
        int[] dp = new int[time + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= time; i++) {
            for (int[] clip : clips) {
                int start = clip[0], end = clip[1];

                if (start <= i && i <= end && dp[start] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[start] + 1);
                }
            }
        }

        return dp[time] == Integer.MAX_VALUE ? -1 : dp[time];
    }

    // Approach 2: Greedy - O(n log n) time, O(1) space
    public int videoStitchingGreedy(int[][] clips, int time) {
        Arrays.sort(clips, (a, b) -> a[0] - b[0]);

        int result = 0;
        int currentEnd = 0;
        int farthest = 0;
        int i = 0;

        while (currentEnd < time) {
            while (i < clips.length && clips[i][0] <= currentEnd) {
                farthest = Math.max(farthest, clips[i][1]);
                i++;
            }

            if (farthest <= currentEnd) {
                return -1;
            }

            currentEnd = farthest;
            result++;
        }

        return result;
    }

    // Approach 3: Interval DP - O(time^2) time, O(time) space
    public int videoStitchingIntervalDP(int[][] clips, int time) {
        int[] maxReach = new int[time + 1];

        for (int[] clip : clips) {
            int start = clip[0], end = clip[1];
            for (int i = start; i <= Math.min(time, end); i++) {
                maxReach[i] = Math.max(maxReach[i], end);
            }
        }

        int[] dp = new int[time + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 0; i < time; i++) {
            if (dp[i] == Integer.MAX_VALUE)
                continue;

            int farthest = maxReach[i];
            for (int j = i + 1; j <= Math.min(time, farthest); j++) {
                dp[j] = Math.min(dp[j], dp[i] + 1);
            }
        }

        return dp[time] == Integer.MAX_VALUE ? -1 : dp[time];
    }

    // Approach 4: BFS - O(clips.length * time) time, O(time) space
    public int videoStitchingBFS(int[][] clips, int time) {
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[time + 1];

        queue.offer(0);
        visited[0] = true;
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int pos = queue.poll();

                if (pos >= time)
                    return steps;

                for (int[] clip : clips) {
                    int start = clip[0], end = clip[1];

                    if (start <= pos && pos < end) {
                        for (int next = pos + 1; next <= Math.min(time, end); next++) {
                            if (!visited[next]) {
                                visited[next] = true;
                                queue.offer(next);
                            }
                        }
                    }
                }
            }

            steps++;
        }

        return -1;
    }

    // Approach 5: Get Actual Clips Used - O(n log n) time, O(n) space
    public List<int[]> getClipsUsed(int[][] clips, int time) {
        Arrays.sort(clips, (a, b) -> a[0] - b[0]);

        List<int[]> result = new ArrayList<>();
        int currentEnd = 0;
        int farthest = 0;
        int i = 0;
        int[] bestClip = null;

        while (currentEnd < time) {
            farthest = currentEnd;
            bestClip = null;

            while (i < clips.length && clips[i][0] <= currentEnd) {
                if (clips[i][1] > farthest) {
                    farthest = clips[i][1];
                    bestClip = clips[i];
                }
                i++;
            }

            if (farthest <= currentEnd) {
                return new ArrayList<>();
            }

            result.add(bestClip);
            currentEnd = farthest;
        }

        return result;
    }

    public static void main(String[] args) {
        VideoStitching solution = new VideoStitching();

        System.out.println("=== Video Stitching Test Cases ===");

        // Test Case 1: Example from problem
        int[][] clips1 = { { 0, 2 }, { 4, 6 }, { 8, 10 }, { 1, 9 }, { 1, 5 }, { 5, 9 } };
        int time1 = 10;
        System.out.println("Test 1 - Clips: " + Arrays.deepToString(clips1) + ", time: " + time1);
        System.out.println("DP: " + solution.videoStitching(clips1, time1));
        System.out.println("Greedy: " + solution.videoStitchingGreedy(clips1, time1));
        System.out.println("Interval DP: " + solution.videoStitchingIntervalDP(clips1, time1));
        System.out.println("BFS: " + solution.videoStitchingBFS(clips1, time1));

        List<int[]> usedClips1 = solution.getClipsUsed(clips1, time1);
        System.out.println("Clips used:");
        for (int[] clip : usedClips1) {
            System.out.println("  " + Arrays.toString(clip));
        }
        System.out.println("Expected: 3\n");

        // Test Case 2: Impossible case
        int[][] clips2 = { { 0, 1 }, { 1, 2 } };
        int time2 = 5;
        System.out.println("Test 2 - Clips: " + Arrays.deepToString(clips2) + ", time: " + time2);
        System.out.println("Greedy: " + solution.videoStitchingGreedy(clips2, time2));
        System.out.println("Expected: -1\n");

        performanceTest();
    }

    private static void performanceTest() {
        VideoStitching solution = new VideoStitching();

        int[][] clips = new int[100][];
        for (int i = 0; i < 100; i++) {
            int start = (int) (Math.random() * 50);
            int end = start + (int) (Math.random() * 30) + 1;
            clips[i] = new int[] { start, Math.min(end, 100) };
        }
        int time = 100;

        System.out.println("=== Performance Test (Clips: " + clips.length + ", time: " + time + ") ===");

        long start = System.nanoTime();
        int result1 = solution.videoStitching(clips, time);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.videoStitchingGreedy(clips, time);
        end = System.nanoTime();
        System.out.println("Greedy: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
