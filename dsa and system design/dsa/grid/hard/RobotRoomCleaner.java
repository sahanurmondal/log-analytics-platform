package grid.hard;

import java.util.*;

/**
 * LeetCode 489: Robot Room Cleaner
 * https://leetcode.com/problems/robot-room-cleaner/
 *
 * Description:
 * You are controlling a robot that is located somewhere in a room. The room is
 * modeled as an m x n binary grid
 * where 0 represents a wall and 1 represents an empty slot.
 * The robot starts at an unknown location in the room that is guaranteed to be
 * empty, and you do not have access to the grid,
 * but you can move the robot using the given API Robot.
 * You are tasked to use the robot to clean the entire room (i.e., clean every
 * empty cell in the room).
 * The robot with the four given APIs can move forward, turn left or turn right.
 * Each turn is 90 degrees.
 * When the robot tries to move into a wall cell, its sensor detects the
 * obstacle, and it stays on the current cell.
 * Design an algorithm to clean the entire room using the following limited set
 * of APIs:
 * - boolean move()
 * - void turnLeft()
 * - void turnRight()
 * - void clean()
 *
 * Constraints:
 * - m == room.length
 * - n == room[i].length
 * - 1 <= m <= 100
 * - 1 <= n <= 200
 * - room[i][j] is either 0 or 1
 * - room[row][col] == 1
 * - All the empty cells can be visited from the starting position
 */
public class RobotRoomCleaner {

    // Robot interface (for demonstration)
    interface Robot {
        public boolean move();

        public void turnLeft();

        public void turnRight();

        public void clean();
    }

    private int[][] directions = { { -1, 0 }, { 0, 1 }, { 1, 0 }, { 0, -1 } }; // up, right, down, left
    private Set<String> visited = new HashSet<>();

    public void cleanRoom(Robot robot) {
        backtrack(robot, 0, 0, 0);
    }

    private void backtrack(Robot robot, int row, int col, int direction) {
        String key = row + "," + col;
        if (visited.contains(key)) {
            return;
        }

        visited.add(key);
        robot.clean();

        // Try all 4 directions
        for (int i = 0; i < 4; i++) {
            if (robot.move()) {
                int newRow = row + directions[direction][0];
                int newCol = col + directions[direction][1];
                backtrack(robot, newRow, newCol, direction);

                // Go back to previous position
                robot.turnRight();
                robot.turnRight();
                robot.move();
                robot.turnRight();
                robot.turnRight();
            }

            // Turn to next direction
            robot.turnRight();
            direction = (direction + 1) % 4;
        }
    }

    public static void main(String[] args) {
        // This problem requires the Robot interface implementation
        // which is provided by the LeetCode platform
        System.out.println("Robot Room Cleaner - requires Robot interface");
    }
}
