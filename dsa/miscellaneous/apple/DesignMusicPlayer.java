package miscellaneous.apple;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design a Music Streaming Player
 * 
 * Description:
 * Design a music player that supports:
 * - Play, pause, skip, previous functionality
 * - Playlist management
 * - Shuffle and repeat modes
 * - Smart recommendations based on listening history
 * - Offline downloads
 * 
 * Company: Apple
 * Difficulty: Medium
 * Asked: iOS/Backend interviews 2023-2024
 */
public class DesignMusicPlayer {

    enum PlayerState {
        PLAYING, PAUSED, STOPPED
    }

    enum RepeatMode {
        NONE, SINGLE, ALL
    }

    class Song {
        String id;
        String title;
        String artist;
        String album;
        int duration; // in seconds
        String genre;
        boolean isDownloaded;

        Song(String id, String title, String artist, String album, int duration, String genre) {
            this.id = id;
            this.title = title;
            this.artist = artist;
            this.album = album;
            this.duration = duration;
            this.genre = genre;
            this.isDownloaded = false;
        }
    }

    class Playlist {
        String id;
        String name;
        List<Song> songs;
        String userId;
        boolean isPublic;

        Playlist(String id, String name, String userId) {
            this.id = id;
            this.name = name;
            this.userId = userId;
            this.songs = new ArrayList<>();
            this.isPublic = false;
        }
    }

    class PlaybackSession {
        String userId;
        Song currentSong;
        long startTime;
        long endTime;
        boolean completed;

        PlaybackSession(String userId, Song song) {
            this.userId = userId;
            this.currentSong = song;
            this.startTime = System.currentTimeMillis();
            this.completed = false;
        }
    }

    class MusicPlayer {
        private PlayerState state;
        private Song currentSong;
        private Playlist currentPlaylist;
        private int currentIndex;
        private boolean shuffleMode;
        private RepeatMode repeatMode;
        private List<Integer> shuffleOrder;
        private int currentPosition; // in seconds

        public MusicPlayer() {
            this.state = PlayerState.STOPPED;
            this.shuffleMode = false;
            this.repeatMode = RepeatMode.NONE;
            this.currentPosition = 0;
        }

        public void play(Playlist playlist, int songIndex) {
            this.currentPlaylist = playlist;
            this.currentIndex = songIndex;
            this.currentSong = playlist.songs.get(songIndex);
            this.state = PlayerState.PLAYING;

            if (shuffleMode) {
                generateShuffleOrder();
            }

            recordPlaybackSession();
        }

        public void pause() {
            if (state == PlayerState.PLAYING) {
                state = PlayerState.PAUSED;
            }
        }

        public void resume() {
            if (state == PlayerState.PAUSED) {
                state = PlayerState.PLAYING;
            }
        }

        public void next() {
            if (currentPlaylist == null || currentPlaylist.songs.isEmpty()) {
                return;
            }

            int nextIndex = getNextSongIndex();
            if (nextIndex != -1) {
                currentIndex = nextIndex;
                currentSong = currentPlaylist.songs.get(currentIndex);
                currentPosition = 0;
                recordPlaybackSession();
            }
        }

        public void previous() {
            if (currentPlaylist == null || currentPlaylist.songs.isEmpty()) {
                return;
            }

            int prevIndex = getPreviousSongIndex();
            if (prevIndex != -1) {
                currentIndex = prevIndex;
                currentSong = currentPlaylist.songs.get(currentIndex);
                currentPosition = 0;
                recordPlaybackSession();
            }
        }

        public void toggleShuffle() {
            shuffleMode = !shuffleMode;
            if (shuffleMode) {
                generateShuffleOrder();
            }
        }

        public void setRepeatMode(RepeatMode mode) {
            this.repeatMode = mode;
        }

        private int getNextSongIndex() {
            if (shuffleMode) {
                int currentShuffleIndex = shuffleOrder.indexOf(currentIndex);
                if (currentShuffleIndex < shuffleOrder.size() - 1) {
                    return shuffleOrder.get(currentShuffleIndex + 1);
                } else if (repeatMode == RepeatMode.ALL) {
                    return shuffleOrder.get(0);
                }
            } else {
                if (repeatMode == RepeatMode.SINGLE) {
                    return currentIndex;
                } else if (currentIndex < currentPlaylist.songs.size() - 1) {
                    return currentIndex + 1;
                } else if (repeatMode == RepeatMode.ALL) {
                    return 0;
                }
            }
            return -1;
        }

        private int getPreviousSongIndex() {
            if (shuffleMode) {
                int currentShuffleIndex = shuffleOrder.indexOf(currentIndex);
                if (currentShuffleIndex > 0) {
                    return shuffleOrder.get(currentShuffleIndex - 1);
                } else if (repeatMode == RepeatMode.ALL) {
                    return shuffleOrder.get(shuffleOrder.size() - 1);
                }
            } else {
                if (repeatMode == RepeatMode.SINGLE) {
                    return currentIndex;
                } else if (currentIndex > 0) {
                    return currentIndex - 1;
                } else if (repeatMode == RepeatMode.ALL) {
                    return currentPlaylist.songs.size() - 1;
                }
            }
            return -1;
        }

        private void generateShuffleOrder() {
            shuffleOrder = new ArrayList<>();
            for (int i = 0; i < currentPlaylist.songs.size(); i++) {
                shuffleOrder.add(i);
            }
            Collections.shuffle(shuffleOrder);
        }

        private void recordPlaybackSession() {
            // Record for analytics and recommendations
            playbackHistory.add(new PlaybackSession("currentUser", currentSong));
        }
    }

    class RecommendationEngine {
        public List<Song> getRecommendations(String userId, int limit) {
            Map<String, Integer> genreCount = new HashMap<>();
            Map<String, Integer> artistCount = new HashMap<>();

            // Analyze listening history
            for (PlaybackSession session : playbackHistory) {
                if (session.userId.equals(userId)) {
                    Song song = session.currentSong;
                    genreCount.put(song.genre, genreCount.getOrDefault(song.genre, 0) + 1);
                    artistCount.put(song.artist, artistCount.getOrDefault(song.artist, 0) + 1);
                }
            }

            // Find top genres and artists
            String topGenre = genreCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("");

            // Return songs from similar genres/artists
            return allSongs.stream()
                    .filter(song -> song.genre.equals(topGenre))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    private List<Song> allSongs = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();
    private List<PlaybackSession> playbackHistory = new ArrayList<>();
    private MusicPlayer player = new MusicPlayer();
    private RecommendationEngine recommendationEngine = new RecommendationEngine();

    public Playlist createPlaylist(String name, String userId) {
        String playlistId = UUID.randomUUID().toString();
        Playlist playlist = new Playlist(playlistId, name, userId);
        playlists.add(playlist);
        return playlist;
    }

    public void addSongToPlaylist(String playlistId, Song song) {
        playlists.stream()
                .filter(p -> p.id.equals(playlistId))
                .findFirst()
                .ifPresent(p -> p.songs.add(song));
    }

    public void downloadSong(String songId) {
        allSongs.stream()
                .filter(s -> s.id.equals(songId))
                .findFirst()
                .ifPresent(s -> s.isDownloaded = true);
    }

    public static void main(String[] args) {
        DesignMusicPlayer musicSystem = new DesignMusicPlayer();

        // Create songs
        Song song1 = musicSystem.new Song("1", "Song 1", "Artist 1", "Album 1", 180, "Rock");
        Song song2 = musicSystem.new Song("2", "Song 2", "Artist 2", "Album 2", 200, "Pop");

        musicSystem.allSongs.addAll(Arrays.asList(song1, song2));

        // Create playlist
        Playlist playlist = musicSystem.createPlaylist("My Playlist", "user1");
        musicSystem.addSongToPlaylist(playlist.id, song1);
        musicSystem.addSongToPlaylist(playlist.id, song2);

        // Play music
        musicSystem.player.play(playlist, 0);
        System.out.println("Playing: " + song1.title);

        // Get recommendations
        List<Song> recommendations = musicSystem.recommendationEngine.getRecommendations("user1", 5);
        System.out.println("Recommendations: " + recommendations.size());
    }
}
