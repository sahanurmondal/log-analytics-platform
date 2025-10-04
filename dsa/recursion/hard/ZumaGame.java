package recursion.hard;

/**
 * LeetCode 488: Zuma Game
 * https://leetcode.com/problems/zuma-game/
 *
 * Companies: Google
 * Frequency: Medium
 *
 * Description:
 * Given a string board and a string hand, return the minimum number of balls
 * needed to clear the board.
 *
 * Constraints:
 * - 1 <= board.length <= 20
 * - 1 <= hand.length <= 5
 *
 * Follow-ups:
 * 1. Can you return the sequence of moves?
 * 2. Can you optimize for large boards?
 * 3. Can you handle multiple hands?
 */
public class ZumaGame {
    public int findMinStep(String board, String hand) {
        int res = dfs(board, hand);
        return res == Integer.MAX_VALUE ? -1 : res;
    }

    private int dfs(String board, String hand) {
        if (board.length() == 0)
            return 0;
        if (hand.length() == 0)
            return Integer.MAX_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < hand.length(); i++) {
            for (int j = 0; j <= board.length(); j++) {
                String newBoard = insert(board, j, hand.charAt(i));
                newBoard = removeConsecutive(newBoard);
                String newHand = hand.substring(0, i) + hand.substring(i + 1);
                int next = dfs(newBoard, newHand);
                if (next != Integer.MAX_VALUE)
                    min = Math.min(min, 1 + next);
            }
        }
        return min;
    }

    private String insert(String board, int pos, char c) {
        return board.substring(0, pos) + c + board.substring(pos);
    }

    private String removeConsecutive(String board) {
        int i = 0;
        while (i < board.length()) {
            int j = i;
            while (j < board.length() && board.charAt(j) == board.charAt(i))
                j++;
            if (j - i >= 3)
                return removeConsecutive(board.substring(0, i) + board.substring(j));
            i = j;
        }
        return board;
    }

    // Follow-up 1: Return sequence of moves (not implemented)
    // Follow-up 2: Optimize for large boards (not needed for n <= 20)
    // Follow-up 3: Handle multiple hands (not implemented)

    public static void main(String[] args) {
        ZumaGame solution = new ZumaGame();
        System.out.println(solution.findMinStep("WRRBBW", "RB")); // -1
        System.out.println(solution.findMinStep("G", "GGGGG")); // 2
    }
}
