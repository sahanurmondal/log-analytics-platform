package grid.hard;

import java.util.*;

/**
 * LeetCode 488: Zuma Game
 * https://leetcode.com/problems/zuma-game/
 *
 * Description:
 * You are playing a variation of the game Zuma.
 * In this variation of Zuma, there is a single row of colored balls on a board,
 * where each ball can be colored red 'R', yellow 'Y', blue 'B', green 'G', or
 * white 'W'.
 * You also have several colored balls in your hand.
 * Your goal is to clear all the balls from the board. On each turn:
 * - Pick any ball from your hand and insert it at any position on the board.
 * - If there is a group of 3 or more consecutive balls of the same color,
 * remove these balls from the board.
 * - If there are no more balls on the board, you have won the game.
 * - Repeat this process until you either win or cannot make any more moves.
 * Given a string board representing the initial state of the balls on the board
 * and a string hand representing the balls in your hand,
 * return the minimum number of balls you have to insert to clear all the balls
 * from the board. If you cannot clear all the balls, return -1.
 *
 * Constraints:
 * - 1 <= board.length <= 16
 * - 1 <= hand.length <= 5
 * - board and hand consist of the characters 'R', 'Y', 'B', 'G', and 'W'
 * - The initial state of the board will not have any groups of 3 or more
 * consecutive balls of the same color
 */
public class ZumaGame {

    public int findMinStep(String board, String hand) {
        int[] handCount = new int[5];
        for (char c : hand.toCharArray()) {
            handCount[charToIndex(c)]++;
        }

        return dfs(board, handCount, new HashMap<>());
    }

    private int dfs(String board, int[] hand, Map<String, Integer> memo) {
        if (board.isEmpty())
            return 0;

        String key = board + "," + Arrays.toString(hand);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int result = Integer.MAX_VALUE;

        for (int i = 0; i <= board.length(); i++) {
            for (int j = 0; j < 5; j++) {
                if (hand[j] > 0) {
                    char ball = indexToChar(j);
                    hand[j]--;

                    String newBoard = board.substring(0, i) + ball + board.substring(i);
                    String cleanedBoard = removeConsecutive(newBoard);

                    int steps = dfs(cleanedBoard, hand, memo);
                    if (steps != -1) {
                        result = Math.min(result, steps + 1);
                    }

                    hand[j]++;
                }
            }
        }

        result = result == Integer.MAX_VALUE ? -1 : result;
        memo.put(key, result);
        return result;
    }

    private String removeConsecutive(String board) {
        boolean removed = true;
        while (removed) {
            removed = false;
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < board.length()) {
                char curr = board.charAt(i);
                int count = 1;

                while (i + count < board.length() && board.charAt(i + count) == curr) {
                    count++;
                }

                if (count >= 3) {
                    removed = true;
                    i += count;
                } else {
                    for (int j = 0; j < count; j++) {
                        sb.append(curr);
                    }
                    i += count;
                }
            }

            board = sb.toString();
        }

        return board;
    }

    private int charToIndex(char c) {
        return "RYBGW".indexOf(c);
    }

    private char indexToChar(int i) {
        return "RYBGW".charAt(i);
    }

    public static void main(String[] args) {
        ZumaGame solution = new ZumaGame();

        System.out.println(solution.findMinStep("WWRRBBWW", "WRBRW")); // 2
        System.out.println(solution.findMinStep("G", "GGGGG")); // 2
        System.out.println(solution.findMinStep("RBYYBBRRB", "YRBGB")); // -1
    }
}
