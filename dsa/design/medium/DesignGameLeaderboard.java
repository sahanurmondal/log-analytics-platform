package design.medium;

import java.util.*;

/**
 * Design Game Leaderboard System
 *
 * Description: Design a game leaderboard that supports:
 * - Player score updates
 * - Real-time rankings
 * - Multiple game modes
 * - Historical tracking
 * - Tournament support
 * 
 * Constraints:
 * - Support millions of players
 * - Fast ranking queries
 * - Score consistency
 *
 * Follow-up:
 * - How to handle tie-breaking?
 * - Seasonal leaderboards?
 * 
 * Time Complexity: O(log n) for updates, O(k) for top-k queries
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Epic Games
 */
public class DesignGameLeaderboard {

    enum GameMode {
        CLASSIC, RANKED, TOURNAMENT, SEASONAL
    }

    class Player {
        String playerId;
        String playerName;
        Map<GameMode, Integer> scores;
        Map<GameMode, Integer> gamesPlayed;
        long lastUpdated;

        Player(String playerId, String playerName) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.scores = new HashMap<>();
            this.gamesPlayed = new HashMap<>();
            this.lastUpdated = System.currentTimeMillis();

            // Initialize scores for all game modes
            for (GameMode mode : GameMode.values()) {
                scores.put(mode, 0);
                gamesPlayed.put(mode, 0);
            }
        }

        void updateScore(GameMode mode, int score) {
            scores.put(mode, score);
            gamesPlayed.put(mode, gamesPlayed.get(mode) + 1);
            lastUpdated = System.currentTimeMillis();
        }

        int getScore(GameMode mode) {
            return scores.getOrDefault(mode, 0);
        }
    }

    class LeaderboardEntry implements Comparable<LeaderboardEntry> {
        String playerId;
        String playerName;
        int score;
        int gamesPlayed;
        long lastUpdated;

        LeaderboardEntry(Player player, GameMode mode) {
            this.playerId = player.playerId;
            this.playerName = player.playerName;
            this.score = player.getScore(mode);
            this.gamesPlayed = player.gamesPlayed.get(mode);
            this.lastUpdated = player.lastUpdated;
        }

        @Override
        public int compareTo(LeaderboardEntry other) {
            if (this.score != other.score) {
                return other.score - this.score; // Higher score first
            }
            if (this.gamesPlayed != other.gamesPlayed) {
                return this.gamesPlayed - other.gamesPlayed; // Fewer games first (higher skill)
            }
            return Long.compare(this.lastUpdated, other.lastUpdated); // Earlier achiever first
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof LeaderboardEntry))
                return false;
            LeaderboardEntry other = (LeaderboardEntry) obj;
            return playerId.equals(other.playerId);
        }

        @Override
        public int hashCode() {
            return playerId.hashCode();
        }
    }

    private Map<String, Player> players;
    private Map<GameMode, TreeSet<LeaderboardEntry>> leaderboards;
    private Map<GameMode, Map<String, LeaderboardEntry>> playerEntries;

    public DesignGameLeaderboard() {
        players = new HashMap<>();
        leaderboards = new HashMap<>();
        playerEntries = new HashMap<>();

        // Initialize leaderboards for each game mode
        for (GameMode mode : GameMode.values()) {
            leaderboards.put(mode, new TreeSet<>());
            playerEntries.put(mode, new HashMap<>());
        }
    }

    public void registerPlayer(String playerId, String playerName) {
        if (!players.containsKey(playerId)) {
            Player player = new Player(playerId, playerName);
            players.put(playerId, player);

            // Add to all leaderboards with initial score
            for (GameMode mode : GameMode.values()) {
                LeaderboardEntry entry = new LeaderboardEntry(player, mode);
                leaderboards.get(mode).add(entry);
                playerEntries.get(mode).put(playerId, entry);
            }
        }
    }

    public void updatePlayerScore(String playerId, GameMode mode, int newScore) {
        Player player = players.get(playerId);
        if (player == null) {
            return; // Player not registered
        }

        // Remove old entry from leaderboard
        LeaderboardEntry oldEntry = playerEntries.get(mode).get(playerId);
        if (oldEntry != null) {
            leaderboards.get(mode).remove(oldEntry);
        }

        // Update player score
        player.updateScore(mode, newScore);

        // Add new entry to leaderboard
        LeaderboardEntry newEntry = new LeaderboardEntry(player, mode);
        leaderboards.get(mode).add(newEntry);
        playerEntries.get(mode).put(playerId, newEntry);
    }

    public int getPlayerRank(String playerId, GameMode mode) {
        LeaderboardEntry playerEntry = playerEntries.get(mode).get(playerId);
        if (playerEntry == null) {
            return -1; // Player not found
        }

        TreeSet<LeaderboardEntry> leaderboard = leaderboards.get(mode);
        int rank = 1;

        for (LeaderboardEntry entry : leaderboard) {
            if (entry.equals(playerEntry)) {
                return rank;
            }
            rank++;
        }

        return -1; // Should never reach here
    }

    public List<LeaderboardEntry> getTopPlayers(GameMode mode, int count) {
        TreeSet<LeaderboardEntry> leaderboard = leaderboards.get(mode);
        List<LeaderboardEntry> topPlayers = new ArrayList<>();

        int added = 0;
        for (LeaderboardEntry entry : leaderboard) {
            if (added >= count)
                break;
            topPlayers.add(entry);
            added++;
        }

        return topPlayers;
    }

    public List<LeaderboardEntry> getPlayersAroundRank(String playerId, GameMode mode, int range) {
        int playerRank = getPlayerRank(playerId, mode);
        if (playerRank == -1) {
            return new ArrayList<>();
        }

        int startRank = Math.max(1, playerRank - range);
        int endRank = playerRank + range;

        TreeSet<LeaderboardEntry> leaderboard = leaderboards.get(mode);
        List<LeaderboardEntry> result = new ArrayList<>();

        int currentRank = 1;
        for (LeaderboardEntry entry : leaderboard) {
            if (currentRank >= startRank && currentRank <= endRank) {
                result.add(entry);
            }
            if (currentRank > endRank)
                break;
            currentRank++;
        }

        return result;
    }

    public int getPlayerScore(String playerId, GameMode mode) {
        Player player = players.get(playerId);
        return player != null ? player.getScore(mode) : 0;
    }

    public Map<GameMode, Integer> getAllPlayerScores(String playerId) {
        Player player = players.get(playerId);
        return player != null ? new HashMap<>(player.scores) : new HashMap<>();
    }

    public void resetLeaderboard(GameMode mode) {
        leaderboards.get(mode).clear();
        playerEntries.get(mode).clear();

        // Reset all player scores for this mode
        for (Player player : players.values()) {
            player.scores.put(mode, 0);
            player.gamesPlayed.put(mode, 0);

            // Re-add to leaderboard
            LeaderboardEntry entry = new LeaderboardEntry(player, mode);
            leaderboards.get(mode).add(entry);
            playerEntries.get(mode).put(player.playerId, entry);
        }
    }

    public Map<String, Object> getLeaderboardStats(GameMode mode) {
        Map<String, Object> stats = new HashMap<>();
        TreeSet<LeaderboardEntry> leaderboard = leaderboards.get(mode);

        stats.put("totalPlayers", leaderboard.size());
        stats.put("gameMode", mode.name());

        if (!leaderboard.isEmpty()) {
            LeaderboardEntry top = leaderboard.first();
            LeaderboardEntry bottom = leaderboard.last();

            stats.put("highestScore", top.score);
            stats.put("lowestScore", bottom.score);
            stats.put("topPlayer", top.playerName);

            // Calculate average score
            double averageScore = leaderboard.stream()
                    .mapToInt(entry -> entry.score)
                    .average()
                    .orElse(0.0);
            stats.put("averageScore", averageScore);
        }

        return stats;
    }

    public List<LeaderboardEntry> getPlayersByScoreRange(GameMode mode, int minScore, int maxScore) {
        TreeSet<LeaderboardEntry> leaderboard = leaderboards.get(mode);
        List<LeaderboardEntry> result = new ArrayList<>();

        for (LeaderboardEntry entry : leaderboard) {
            if (entry.score >= minScore && entry.score <= maxScore) {
                result.add(entry);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        DesignGameLeaderboard leaderboard = new DesignGameLeaderboard();

        // Register players
        leaderboard.registerPlayer("player1", "Alice");
        leaderboard.registerPlayer("player2", "Bob");
        leaderboard.registerPlayer("player3", "Charlie");
        leaderboard.registerPlayer("player4", "Diana");
        leaderboard.registerPlayer("player5", "Eve");

        // Update scores for CLASSIC mode
        leaderboard.updatePlayerScore("player1", GameMode.CLASSIC, 1500);
        leaderboard.updatePlayerScore("player2", GameMode.CLASSIC, 2000);
        leaderboard.updatePlayerScore("player3", GameMode.CLASSIC, 1200);
        leaderboard.updatePlayerScore("player4", GameMode.CLASSIC, 1800);
        leaderboard.updatePlayerScore("player5", GameMode.CLASSIC, 1600);

        // Get top players
        System.out.println("Top 3 players in CLASSIC mode:");
        List<LeaderboardEntry> topPlayers = leaderboard.getTopPlayers(GameMode.CLASSIC, 3);
        for (int i = 0; i < topPlayers.size(); i++) {
            LeaderboardEntry entry = topPlayers.get(i);
            System.out.println((i + 1) + ". " + entry.playerName + " - " + entry.score + " points");
        }

        // Get player rank
        int aliceRank = leaderboard.getPlayerRank("player1", GameMode.CLASSIC);
        System.out.println("\nAlice's rank: " + aliceRank);

        // Get players around Alice's rank
        System.out.println("\nPlayers around Alice's rank (±1):");
        List<LeaderboardEntry> nearbyPlayers = leaderboard.getPlayersAroundRank("player1", GameMode.CLASSIC, 1);
        for (int i = 0; i < nearbyPlayers.size(); i++) {
            LeaderboardEntry entry = nearbyPlayers.get(i);
            int rank = leaderboard.getPlayerRank(entry.playerId, GameMode.CLASSIC);
            System.out.println(rank + ". " + entry.playerName + " - " + entry.score + " points");
        }

        // Update Bob's score
        leaderboard.updatePlayerScore("player2", GameMode.CLASSIC, 2500);
        System.out.println("\nAfter Bob's score update:");

        topPlayers = leaderboard.getTopPlayers(GameMode.CLASSIC, 3);
        for (int i = 0; i < topPlayers.size(); i++) {
            LeaderboardEntry entry = topPlayers.get(i);
            System.out.println((i + 1) + ". " + entry.playerName + " - " + entry.score + " points");
        }

        // Show leaderboard stats
        System.out.println("\nLeaderboard statistics:");
        System.out.println(leaderboard.getLeaderboardStats(GameMode.CLASSIC));

        // Test different game mode
        leaderboard.updatePlayerScore("player1", GameMode.RANKED, 800);
        leaderboard.updatePlayerScore("player2", GameMode.RANKED, 1200);

        System.out.println("\nTop players in RANKED mode:");
        List<LeaderboardEntry> rankedTop = leaderboard.getTopPlayers(GameMode.RANKED, 2);
        for (int i = 0; i < rankedTop.size(); i++) {
            LeaderboardEntry entry = rankedTop.get(i);
            System.out.println((i + 1) + ". " + entry.playerName + " - " + entry.score + " points");
        }
    }
}
