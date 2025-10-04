package grid.hard;

import java.util.*;

/**
 * LeetCode 773: Sliding Puzzle
 * https://leetcode.com/problems/sliding-puzzle/
 *
 * Description:
 * On a 2x3 board, there are five tiles labeled from 1 to 5, and an empty square
 * represented by 0.
 * A move consists of choosing 0 and a 4-directionally adjacent number and
 * swapping it.
 * The state of the board is solved if and only if the board is
 * [[1,2,3],[4,5,0]].
 * Given the puzzle board, return the least number of moves required so that the
 * state of the board is solved.
 * If it is impossible for the state of the board to be solved, return -1.
 *
 * Constraints:
 * - board.length == 2
 * - board[i].length == 3
 * - 0 <= board[i][j] <= 5
 * - Each value board[i][j] is unique
 */
public class SlidingPuzzle {

    public int slidingPuzzle(int[][] board) {
        String target = "123450";
        String start = "";

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 3; j++) {
                start += board[i][j];
            }
        }

        if (start.equals(target))
            return 0;

        // Adjacent positions for each index in 1D representation
        int[][] neighbors = { { 1, 3 }, { 0, 2, 4 }, { 1, 5 }, { 0, 4 }, { 1, 3, 5 }, { 2, 4 } };

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer(start);
        visited.add(start);

        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            moves++;

            for (int i = 0; i < size; i++) {
                String curr = queue.poll();
                int zeroIndex = curr.indexOf('0');

                for (int neighbor : neighbors[zeroIndex]) {
                    String next = swap(curr, zeroIndex, neighbor);

                    if (next.equals(target)) {
                        return moves;
                    }

                    if (!visited.contains(next)) {
                        visited.add(next);
                        queue.offer(next);
                    }
                }
            }
        }

        return -1;
    }

    private String swap(String s, int i, int j) {
        char[] chars = s.toCharArray();
        char temp = chars[i];
        chars[i] = chars[j];
        chars[j] = temp;
        return new String(chars);
    }

    public static void main(String[] args) {
        SlidingPuzzle solution = new SlidingPuzzle();

        int[][] board1 = { { 1, 2, 3 }, { 4, 0, 5 } };
        System.out.println(solution.slidingPuzzle(board1)); // 1

        int[][] board2 = { { 1, 2, 3 }, { 5, 4, 0 } };
        System.out.println(solution.slidingPuzzle(board2)); // -1
    }
}
