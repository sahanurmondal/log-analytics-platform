package grid.hard;

import java.util.*;

/**
 * LeetCode 1345: Jump Game IV
 * https://leetcode.com/problems/jump-game-iv/
 *
 * Description:
 * Given an array of integers arr, you are initially positioned at the first
 * index of the array.
 * In one step you can jump from index i to index:
 * - i + 1 where: i + 1 < arr.length.
 * - i - 1 where: i - 1 >= 0.
 * - j where: arr[i] == arr[j] and i != j.
 * Return the minimum number of steps to reach the last index of the array.
 *
 * Constraints:
 * - 1 <= arr.length <= 5 * 10^4
 * - -10^8 <= arr[i] <= 10^8
 */
public class JumpGameIV {

    public int minJumps(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return 0;

        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.computeIfAbsent(arr[i], k -> new ArrayList<>()).add(i);
        }

        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[n];

        queue.offer(0);
        visited[0] = true;
        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            steps++;

            for (int i = 0; i < size; i++) {
                int curr = queue.poll();

                // Try all possible jumps
                List<Integer> nextIndices = new ArrayList<>();
                if (curr + 1 < n)
                    nextIndices.add(curr + 1);
                if (curr - 1 >= 0)
                    nextIndices.add(curr - 1);

                // Add teleportation indices
                List<Integer> sameValues = graph.get(arr[curr]);
                if (sameValues != null) {
                    nextIndices.addAll(sameValues);
                    graph.remove(arr[curr]); // Optimization: remove to avoid revisiting
                }

                for (int next : nextIndices) {
                    if (next == n - 1)
                        return steps;

                    if (!visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        JumpGameIV solution = new JumpGameIV();

        int[] arr1 = { 100, -23, -23, 404, 100, 23, 23, 23, 3, 404 };
        System.out.println(solution.minJumps(arr1)); // 3

        int[] arr2 = { 7 };
        System.out.println(solution.minJumps(arr2)); // 0
    }
}
