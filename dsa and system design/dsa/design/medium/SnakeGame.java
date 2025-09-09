package design.medium;

import java.util.*;

/**
 * LeetCode 353: Design Snake Game
 * https://leetcode.com/problems/design-snake-game/
 *
 * Description: Design a Snake game that is played on a device with screen size
 * height x width.
 * 
 * Constraints:
 * - 1 <= width, height <= 10^4
 * - 1 <= food.length <= 50
 * - food[i].length == 2
 * - 0 <= row_i < height
 * - 0 <= col_i < width
 * - direction.length == 1
 * - direction in ["U", "D", "L", "R"]
 * - At most 10^4 calls will be made to move
 *
 * Follow-up:
 * - Can you solve it efficiently?
 * 
 * Time Complexity: O(1) per move
 * Space Complexity: O(n) where n is snake length
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SnakeGame {

    private Deque<int[]> snake;
    private Set<String> snakeSet;
    private int[][] food;
    private int foodIndex;
    private int width, height;

    public SnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        this.foodIndex = 0;

        snake = new LinkedList<>();
        snakeSet = new HashSet<>();

        // Initialize snake at (0, 0)
        snake.offer(new int[] { 0, 0 });
        snakeSet.add("0,0");
    }

    public int move(String direction) {
        int[] head = snake.peekFirst();
        int newRow = head[0];
        int newCol = head[1];

        // Calculate new head position
        switch (direction) {
            case "U":
                newRow--;
                break;
            case "D":
                newRow++;
                break;
            case "L":
                newCol--;
                break;
            case "R":
                newCol++;
                break;
        }

        // Check boundary collision
        if (newRow < 0 || newRow >= height || newCol < 0 || newCol >= width) {
            return -1;
        }

        int[] newHead = new int[] { newRow, newCol };
        String newHeadKey = newRow + "," + newCol;

        // Check if food is eaten
        boolean ateFood = false;
        if (foodIndex < food.length &&
                newRow == food[foodIndex][0] && newCol == food[foodIndex][1]) {
            ateFood = true;
            foodIndex++;
        }

        if (!ateFood) {
            // Remove tail
            int[] tail = snake.pollLast();
            snakeSet.remove(tail[0] + "," + tail[1]);
        }

        // Check self collision (after removing tail)
        if (snakeSet.contains(newHeadKey)) {
            return -1;
        }

        // Add new head
        snake.offerFirst(newHead);
        snakeSet.add(newHeadKey);

        return snake.size() - 1; // Score is length - 1
    }

    public static void main(String[] args) {
        SnakeGame game = new SnakeGame(3, 2, new int[][] { { 1, 2 }, { 0, 1 } });
        System.out.println(game.move("R")); // Expected: 0
        System.out.println(game.move("D")); // Expected: 0
        System.out.println(game.move("R")); // Expected: 1
        System.out.println(game.move("U")); // Expected: 1
        System.out.println(game.move("L")); // Expected: 2
        System.out.println(game.move("U")); // Expected: -1
    }
}
