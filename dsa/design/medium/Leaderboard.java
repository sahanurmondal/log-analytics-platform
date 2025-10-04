package design.medium;

import java.util.*;

/**
 * LeetCode 1244: Design a Leaderboard
 * https://leetcode.com/problems/design-a-leaderboard/
 *
 * Description: Design a Leaderboard class, which has three functions:
 * addScore(playerId, score): Update the leaderboard by adding score to the
 * given player's score.
 * top(K): Return the score sum of the top K players.
 * reset(playerId): Reset the score of the player with the given id to 0.
 * 
 * Constraints:
 * - 1 <= playerId, K <= 10000
 * - 0 <= score <= 100
 * - At most 1000 calls will be made to each function
 *
 * Follow-up:
 * - Can you solve it efficiently?
 * 
 * Time Complexity: O(log n) for addScore/reset, O(K log n) for top
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon
 */
public class Leaderboard {

    private Map<Integer, Integer> scores;
    private TreeMap<Integer, Integer> sortedScores;

    public Leaderboard() {
        scores = new HashMap<>();
        sortedScores = new TreeMap<>(Collections.reverseOrder());
    }

    public void addScore(int playerId, int score) {
        int prevScore = scores.getOrDefault(playerId, 0);
        int newScore = prevScore + score;

        scores.put(playerId, newScore);

        // Update sorted scores
        if (prevScore != 0) {
            sortedScores.put(prevScore, sortedScores.get(prevScore) - 1);
            if (sortedScores.get(prevScore) == 0) {
                sortedScores.remove(prevScore);
            }
        }

        sortedScores.put(newScore, sortedScores.getOrDefault(newScore, 0) + 1);
    }

    public int top(int K) {
        int sum = 0;
        int count = 0;

        for (Map.Entry<Integer, Integer> entry : sortedScores.entrySet()) {
            int score = entry.getKey();
            int freq = entry.getValue();

            int take = Math.min(K - count, freq);
            sum += score * take;
            count += take;

            if (count == K)
                break;
        }

        return sum;
    }

    public void reset(int playerId) {
        int prevScore = scores.get(playerId);
        scores.put(playerId, 0);

        sortedScores.put(prevScore, sortedScores.get(prevScore) - 1);
        if (sortedScores.get(prevScore) == 0) {
            sortedScores.remove(prevScore);
        }

        sortedScores.put(0, sortedScores.getOrDefault(0, 0) + 1);
    }

    public static void main(String[] args) {
        Leaderboard leaderboard = new Leaderboard();
        leaderboard.addScore(1, 73);
        leaderboard.addScore(2, 56);
        leaderboard.addScore(3, 39);
        leaderboard.addScore(4, 51);
        leaderboard.addScore(5, 4);
        System.out.println(leaderboard.top(1)); // Expected: 73
        leaderboard.reset(1);
        leaderboard.reset(2);
        leaderboard.addScore(2, 51);
        leaderboard.addScore(3, 13);
        System.out.println(leaderboard.top(3)); // Expected: 141
    }
}
