package backtracking.easy;

import java.util.*;

/**
 * LeetCode 401: Binary Watch
 * https://leetcode.com/problems/binary-watch/
 *
 * Description: A binary watch has 4 LEDs on the top to represent hours (0-11),
 * and 6 LEDs on the bottom to represent minutes (0-59).
 * Given an integer turnedOn which represents the number of LEDs that are
 * currently on, return all possible times the watch could represent.
 * 
 * Constraints:
 * - 0 <= turnedOn <= 10
 *
 * Follow-up:
 * - Can you solve it using bit manipulation?
 * 
 * Time Complexity: O(1) - fixed number of combinations
 * Space Complexity: O(1)
 * 
 * Company Tags: Google
 */
public class BinaryWatch {

    public List<String> readBinaryWatch(int turnedOn) {
        List<String> result = new ArrayList<>();

        for (int h = 0; h < 12; h++) {
            for (int m = 0; m < 60; m++) {
                if (Integer.bitCount(h) + Integer.bitCount(m) == turnedOn) {
                    result.add(String.format("%d:%02d", h, m));
                }
            }
        }

        return result;
    }

    // Alternative solution - Backtracking
    public List<String> readBinaryWatchBacktrack(int turnedOn) {
        List<String> result = new ArrayList<>();
        int[] hours = { 8, 4, 2, 1 };
        int[] minutes = { 32, 16, 8, 4, 2, 1 };

        backtrack(turnedOn, 0, 0, 0, 0, hours, minutes, result);
        return result;
    }

    private void backtrack(int turnedOn, int pos, int h, int m, int used,
            int[] hours, int[] minutes, List<String> result) {
        if (h >= 12 || m >= 60)
            return;
        if (used == turnedOn) {
            result.add(String.format("%d:%02d", h, m));
            return;
        }
        if (pos >= 10)
            return;

        // Don't turn on current LED
        backtrack(turnedOn, pos + 1, h, m, used, hours, minutes, result);

        // Turn on current LED
        if (pos < 4) {
            backtrack(turnedOn, pos + 1, h + hours[pos], m, used + 1, hours, minutes, result);
        } else {
            backtrack(turnedOn, pos + 1, h, m + minutes[pos - 4], used + 1, hours, minutes, result);
        }
    }

    public static void main(String[] args) {
        BinaryWatch solution = new BinaryWatch();

        System.out.println(solution.readBinaryWatch(1)); // Expected:
                                                         // ["0:01","0:02","0:04","0:08","0:16","0:32","1:00","2:00","4:00","8:00"]
        System.out.println(solution.readBinaryWatch(9)); // Expected: []
        System.out.println(solution.readBinaryWatch(0)); // Expected: ["0:00"]
    }
}
