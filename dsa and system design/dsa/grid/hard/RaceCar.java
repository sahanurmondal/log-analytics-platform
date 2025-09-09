package grid.hard;

import java.util.*;

/**
 * LeetCode 818: Race Car
 * https://leetcode.com/problems/race-car/
 *
 * Description:
 * Your car starts at position 0 and speed +1 on an infinite number line.
 * Your car can go into negative positions. Your car drives automatically
 * according to a sequence of instructions 'A' (accelerate) and 'R' (reverse).
 * When you get an instruction 'A', your car does the following:
 * - position += speed
 * - speed *= 2
 * When you get an instruction 'R', your car does the following:
 * - If your speed is positive then speed = -1
 * - Otherwise speed = 1
 * Your position stays the same.
 * Given a target position target, return the length of the shortest sequence of
 * instructions to get there.
 *
 * Constraints:
 * - 1 <= target <= 10^4
 */
public class RaceCar {

    public int racecar(int target) {
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(new int[] { 0, 1 }); // {position, speed}
        visited.add("0,1");

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int pos = curr[0];
                int speed = curr[1];

                if (pos == target)
                    return steps;

                // Try accelerate
                int newPos = pos + speed;
                int newSpeed = speed * 2;
                String key = newPos + "," + newSpeed;

                if (Math.abs(newPos) <= 2 * target && !visited.contains(key)) {
                    visited.add(key);
                    queue.offer(new int[] { newPos, newSpeed });
                }

                // Try reverse
                int reverseSpeed = speed > 0 ? -1 : 1;
                String reverseKey = pos + "," + reverseSpeed;

                if (!visited.contains(reverseKey)) {
                    visited.add(reverseKey);
                    queue.offer(new int[] { pos, reverseSpeed });
                }
            }

            steps++;
        }

        return -1;
    }

    public static void main(String[] args) {
        RaceCar solution = new RaceCar();

        System.out.println(solution.racecar(3)); // 2
        System.out.println(solution.racecar(6)); // 5
    }
}
