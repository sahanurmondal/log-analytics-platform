package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Task Scheduler with Cooling Time
 * 
 * Description:
 * Given a characters array tasks representing tasks to be executed, and a
 * cooling
 * time n, return the minimum time needed to execute all tasks.
 * 
 * Companies: Facebook, Amazon, Google
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class TaskScheduler {

    public int leastInterval(char[] tasks, int n) {
        Map<Character, Integer> taskCount = new HashMap<>();
        for (char task : tasks) {
            taskCount.put(task, taskCount.getOrDefault(task, 0) + 1);
        }

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        maxHeap.addAll(taskCount.values());

        int time = 0;
        Queue<int[]> queue = new LinkedList<>(); // [count, time when available]

        while (!maxHeap.isEmpty() || !queue.isEmpty()) {
            time++;

            if (!maxHeap.isEmpty()) {
                int count = maxHeap.poll() - 1;
                if (count > 0) {
                    queue.offer(new int[] { count, time + n });
                }
            }

            if (!queue.isEmpty() && queue.peek()[1] == time) {
                maxHeap.offer(queue.poll()[0]);
            }
        }

        return time;
    }

    public static void main(String[] args) {
        TaskScheduler solution = new TaskScheduler();

        char[] tasks1 = { 'A', 'A', 'A', 'B', 'B', 'B' };
        System.out.println(solution.leastInterval(tasks1, 2)); // 8

        char[] tasks2 = { 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G' };
        System.out.println(solution.leastInterval(tasks2, 2)); // 16
    }
}
