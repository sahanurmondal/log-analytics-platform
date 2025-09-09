package miscellaneous.spotify;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Spotify Playlist and Discovery System
 * 
 * Description:
 * Design a music discovery system that supports:
 * - Playlist creation and management
 * - Music discovery and recommendations
 * - Social features (sharing, following)
 * - Listening analytics
 * - Smart radio generation
 * 
 * Company: Spotify
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignPlaylistSystem {

    class Song {
        String id;
        String title;
        String artist;
        String genre;
        int duration; // in seconds
        int playCount;
        double rating;

        Song(String id, String title, String artist, String genre, int duration) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            this.genre = genre;
            this.duration = duration;
            this.playCount = 0;
            this.rating = 0.0;
        }
    }

    class Playlist {
        String id;
        String name;
        List<Song> songs;
        String userId;

        Playlist(String id, String name, String userId) {
            this.id = id;
            this.name = name;
            this.userId = userId;
            this.songs = new ArrayList<>();
        }
    }

    class User {
        String id;
        String name;
        List<String> followedArtists;
        List<String> followedPlaylists;

        User(String id, String name) {
            this.id = id;
            this.name = name;
            this.followedArtists = new ArrayList<>();
            this.followedPlaylists = new ArrayList<>();
        }
    }

    class ListeningHistory {
        String userId;
        String songId;
        long timestamp;

        ListeningHistory(String userId, String songId) {
            this.userId = userId;
            this.songId = songId;
            this.timestamp = System.currentTimeMillis();
        }
    }

    class MusicDiscoveryEngine {
        public List<Song> discoverWeekly(String userId, int limit) {
            Map<String, Double> genrePreferences = analyzeGenrePreferences(userId);
            Map<String, Double> artistPreferences = analyzeArtistPreferences(userId);

            return allSongs.values().stream()
                    .filter(song -> !hasUserListened(userId, song.id))
                    .sorted((s1, s2) -> Double.compare(
                            calculateDiscoveryScore(s2, genrePreferences, artistPreferences),
                            calculateDiscoveryScore(s1, genrePreferences, artistPreferences)))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        private double calculateDiscoveryScore(Song song, Map<String, Double> genrePrefs,
                Map<String, Double> artistPrefs) {
            double genreScore = genrePrefs.getOrDefault(song.genre, 0.0);
            double artistScore = artistPrefs.getOrDefault(song.artist, 0.0);
            double popularityScore = song.playCount / 1000000.0; // normalize play count

            return genreScore * 0.4 + artistScore * 0.3 + popularityScore * 0.3;
        }
    }

    /**
     * Additional Feature: Smart Radio Generation
     * Generates radio stations based on seed songs/artists
     */
    class SmartRadioGenerator {
        public List<Song> generateRadio(String seedSongId, int limit) {
            Song seedSong = allSongs.get(seedSongId);
            if (seedSong == null)
                return new ArrayList<>();

            return allSongs.values().stream()
                    .filter(song -> !song.id.equals(seedSongId))
                    .sorted((s1, s2) -> Double.compare(
                            calculateSimilarity(s2, seedSong),
                            calculateSimilarity(s1, seedSong)))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        private double calculateSimilarity(Song song1, Song song2) {
            double genreMatch = song1.genre.equals(song2.genre) ? 0.5 : 0.0;
            double artistMatch = song1.artist.equals(song2.artist) ? 0.3 : 0.0;
            double popularityMatch = Math.min(song1.playCount, song2.playCount) /
                    (double) Math.max(song1.playCount, song2.playCount) * 0.2;

            return genreMatch + artistMatch + popularityMatch;
        }
    }

    private Map<String, Song> allSongs = new HashMap<>();
    private Map<String, Playlist> playlists = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private List<ListeningHistory> listeningHistories = new ArrayList<>();
    private MusicDiscoveryEngine discoveryEngine = new MusicDiscoveryEngine();
    private SmartRadioGenerator radioGenerator = new SmartRadioGenerator();

    public void createSong(String id, String title, String artist, String genre, int duration) {
        Song song = new Song(id, title, artist, genre, duration);
        allSongs.put(id, song);
    }

    public void createPlaylist(String id, String name, String userId) {
        Playlist playlist = new Playlist(id, name, userId);
        playlists.put(id, playlist);
    }

    public void addSongToPlaylist(String playlistId, String songId) {
        Playlist playlist = playlists.get(playlistId);
        Song song = allSongs.get(songId);
        if (playlist != null && song != null) {
            playlist.songs.add(song);
        }
    }

    public void followArtist(String userId, String artistId) {
        User user = users.get(userId);
        if (user != null && !user.followedArtists.contains(artistId)) {
            user.followedArtists.add(artistId);
        }
    }

    public void followPlaylist(String userId, String playlistId) {
        User user = users.get(userId);
        if (user != null && !user.followedPlaylists.contains(playlistId)) {
            user.followedPlaylists.add(playlistId);
        }
    }

    public List<Song> getRecommendedSongs(String userId, int limit) {
        return discoveryEngine.discoverWeekly(userId, limit);
    }

    public void listenToSong(String userId, String songId) {
        ListeningHistory history = new ListeningHistory(userId, songId);
        listeningHistories.add(history);

        Song song = allSongs.get(songId);
        if (song != null) {
            song.playCount++;
        }
    }

    public List<Song> generateRadio(String seedSongId, int limit) {
        return radioGenerator.generateRadio(seedSongId, limit);
    }

    private Map<String, Double> analyzeGenrePreferences(String userId) {
        Map<String, Double> genrePrefs = new HashMap<>();
        Map<String, Integer> genreCounts = new HashMap<>();

        for (ListeningHistory history : listeningHistories) {
            if (history.userId.equals(userId)) {
                Song song = allSongs.get(history.songId);
                if (song != null) {
                    genreCounts.put(song.genre, genreCounts.getOrDefault(song.genre, 0) + 1);
                }
            }
        }

        int totalListens = genreCounts.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : genreCounts.entrySet()) {
            genrePrefs.put(entry.getKey(), entry.getValue() / (double) totalListens);
        }

        return genrePrefs;
    }

    private Map<String, Double> analyzeArtistPreferences(String userId) {
        Map<String, Double> artistPrefs = new HashMap<>();
        Map<String, Integer> artistCounts = new HashMap<>();

        for (ListeningHistory history : listeningHistories) {
            if (history.userId.equals(userId)) {
                Song song = allSongs.get(history.songId);
                if (song != null) {
                    artistCounts.put(song.artist, artistCounts.getOrDefault(song.artist, 0) + 1);
                }
            }
        }

        int totalListens = artistCounts.values().stream().mapToInt(Integer::intValue).sum();
        for (Map.Entry<String, Integer> entry : artistCounts.entrySet()) {
            artistPrefs.put(entry.getKey(), entry.getValue() / (double) totalListens);
        }

        return artistPrefs;
    }

    private boolean hasUserListened(String userId, String songId) {
        return listeningHistories.stream()
                .anyMatch(history -> history.userId.equals(userId) && history.songId.equals(songId));
    }

    public static void main(String[] args) {
        DesignPlaylistSystem spotify = new DesignPlaylistSystem();

        // Create sample songs
        spotify.createSong("1", "Song A", "Artist 1", "Pop", 210);
        spotify.createSong("2", "Song B", "Artist 2", "Rock", 180);
        spotify.createSong("3", "Song C", "Artist 1", "Jazz", 240);
        spotify.createSong("4", "Song D", "Artist 3", "Pop", 200);
        spotify.createSong("5", "Song E", "Artist 2", "Rock", 220);

        // Create a playlist
        spotify.createPlaylist("101", "My Favorite Songs", "user1");

        // Add songs to the playlist
        spotify.addSongToPlaylist("101", "1");
        spotify.addSongToPlaylist("101", "2");

        // Follow an artist
        spotify.followArtist("user1", "Artist 1");

        // Follow a playlist
        spotify.followPlaylist("user1", "101");

        // Listen to a song
        spotify.listenToSong("user1", "1");

        // Get recommended songs
        List<Song> recommendations = spotify.getRecommendedSongs("user1", 3);
        System.out.println("Recommended Songs: " + recommendations.size());
    }
}