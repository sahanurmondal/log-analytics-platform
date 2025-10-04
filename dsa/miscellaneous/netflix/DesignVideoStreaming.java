package miscellaneous.netflix;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design a Video Streaming Service
 * 
 * Description:
 * Design a video streaming platform that supports:
 * - Video upload and encoding
 * - Adaptive bitrate streaming
 * - Content recommendation
 * - Watch history and resume functionality
 * - Subtitle support
 * 
 * Company: Netflix
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignVideoStreaming {

    enum VideoQuality {
        HD_1080P, HD_720P, SD_480P, SD_360P
    }

    enum ContentType {
        MOVIE, TV_SHOW, DOCUMENTARY, SERIES
    }

    class Video {
        String id;
        String title;
        String description;
        ContentType type;
        int duration; // in seconds
        Map<VideoQuality, String> streamingUrls;
        List<String> subtitleLanguages;
        String thumbnailUrl;
        double rating;

        Video(String id, String title, ContentType type, int duration) {
            this.id = id;
            this.title = title;
            this.type = type;
            this.duration = duration;
            this.streamingUrls = new HashMap<>();
            this.subtitleLanguages = new ArrayList<>();
            this.rating = 0.0;
        }
    }

    class WatchHistory {
        String userId;
        String videoId;
        int watchPosition; // in seconds
        long watchTime;
        boolean completed;

        WatchHistory(String userId, String videoId, int watchPosition) {
            this.userId = userId;
            this.videoId = videoId;
            this.watchPosition = watchPosition;
            this.watchTime = System.currentTimeMillis();
            this.completed = false;
        }
    }

    class StreamingSession {
        String sessionId;
        String userId;
        String videoId;
        VideoQuality currentQuality;
        long startTime;
        int currentPosition;
        boolean isActive;

        StreamingSession(String userId, String videoId, VideoQuality quality) {
            this.sessionId = UUID.randomUUID().toString();
            this.userId = userId;
            this.videoId = videoId;
            this.currentQuality = quality;
            this.startTime = System.currentTimeMillis();
            this.currentPosition = 0;
            this.isActive = true;
        }
    }

    class VideoEncoder {
        public void encodeVideo(String videoId, byte[] videoData) {
            // Simulate encoding to different qualities
            Video video = videos.get(videoId);
            if (video != null) {
                video.streamingUrls.put(VideoQuality.HD_1080P, "https://cdn.example.com/" + videoId + "_1080p.m3u8");
                video.streamingUrls.put(VideoQuality.HD_720P, "https://cdn.example.com/" + videoId + "_720p.m3u8");
                video.streamingUrls.put(VideoQuality.SD_480P, "https://cdn.example.com/" + videoId + "_480p.m3u8");
                video.streamingUrls.put(VideoQuality.SD_360P, "https://cdn.example.com/" + videoId + "_360p.m3u8");
            }
        }
    }

    class RecommendationEngine {
        public List<Video> getRecommendations(String userId, int limit) {
            Map<ContentType, Integer> typePreferences = new HashMap<>();

            // Analyze watch history
            for (WatchHistory history : watchHistories) {
                if (history.userId.equals(userId)) {
                    Video video = videos.get(history.videoId);
                    if (video != null) {
                        typePreferences.put(video.type, typePreferences.getOrDefault(video.type, 0) + 1);
                    }
                }
            }

            // Find preferred content type
            ContentType preferredType = typePreferences.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(ContentType.MOVIE);

            // Return videos of preferred type with high ratings
            return videos.values().stream()
                    .filter(v -> v.type == preferredType)
                    .sorted((v1, v2) -> Double.compare(v2.rating, v1.rating))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    class AdaptiveBitrateStreaming {
        public VideoQuality selectOptimalQuality(String userId, double networkBandwidth) {
            // Select quality based on network conditions
            if (networkBandwidth > 5.0) { // Mbps
                return VideoQuality.HD_1080P;
            } else if (networkBandwidth > 2.5) {
                return VideoQuality.HD_720P;
            } else if (networkBandwidth > 1.0) {
                return VideoQuality.SD_480P;
            } else {
                return VideoQuality.SD_360P;
            }
        }

        public void adjustQuality(String sessionId, double currentBandwidth) {
            StreamingSession session = streamingSessions.get(sessionId);
            if (session != null && session.isActive) {
                VideoQuality newQuality = selectOptimalQuality(session.userId, currentBandwidth);
                if (newQuality != session.currentQuality) {
                    session.currentQuality = newQuality;
                    // Notify client to switch quality
                    notifyQualityChange(session, newQuality);
                }
            }
        }

        private void notifyQualityChange(StreamingSession session, VideoQuality newQuality) {
            System.out.println("Quality changed to " + newQuality + " for session " + session.sessionId);
        }
    }

    private Map<String, Video> videos = new HashMap<>();
    private List<WatchHistory> watchHistories = new ArrayList<>();
    private Map<String, StreamingSession> streamingSessions = new HashMap<>();
    private VideoEncoder encoder = new VideoEncoder();
    private RecommendationEngine recommendationEngine = new RecommendationEngine();
    private AdaptiveBitrateStreaming adaptiveStreaming = new AdaptiveBitrateStreaming();

    public String startStreaming(String userId, String videoId, double networkBandwidth) {
        Video video = videos.get(videoId);
        if (video == null) {
            return null;
        }

        VideoQuality quality = adaptiveStreaming.selectOptimalQuality(userId, networkBandwidth);
        StreamingSession session = new StreamingSession(userId, videoId, quality);
        streamingSessions.put(session.sessionId, session);

        // Check for resume position
        WatchHistory lastWatch = watchHistories.stream()
                .filter(h -> h.userId.equals(userId) && h.videoId.equals(videoId))
                .max(Comparator.comparingLong(h -> h.watchTime))
                .orElse(null);

        if (lastWatch != null && !lastWatch.completed) {
            session.currentPosition = lastWatch.watchPosition;
        }

        return session.sessionId;
    }

    public void updateWatchPosition(String sessionId, int position) {
        StreamingSession session = streamingSessions.get(sessionId);
        if (session != null && session.isActive) {
            session.currentPosition = position;

            // Update watch history
            WatchHistory history = new WatchHistory(session.userId, session.videoId, position);
            Video video = videos.get(session.videoId);
            if (video != null && position >= video.duration * 0.9) {
                history.completed = true;
            }
            watchHistories.add(history);
        }
    }

    public void stopStreaming(String sessionId) {
        StreamingSession session = streamingSessions.get(sessionId);
        if (session != null) {
            session.isActive = false;
            updateWatchPosition(sessionId, session.currentPosition);
        }
    }

    public String getStreamingUrl(String sessionId) {
        StreamingSession session = streamingSessions.get(sessionId);
        if (session == null || !session.isActive) {
            return null;
        }

        Video video = videos.get(session.videoId);
        if (video == null) {
            return null;
        }

        return video.streamingUrls.get(session.currentQuality);
    }

    public static void main(String[] args) {
        DesignVideoStreaming streamingService = new DesignVideoStreaming();

        // Create a video
        Video video = streamingService.new Video("video1", "Test Movie", ContentType.MOVIE, 7200);
        video.rating = 8.5;
        streamingService.videos.put("video1", video);

        // Encode video
        streamingService.encoder.encodeVideo("video1", new byte[0]);

        // Start streaming
        String sessionId = streamingService.startStreaming("user1", "video1", 3.0);
        System.out.println("Started streaming with session: " + sessionId);

        // Get streaming URL
        String url = streamingService.getStreamingUrl(sessionId);
        System.out.println("Streaming URL: " + url);

        // Update watch position
        streamingService.updateWatchPosition(sessionId, 1800);

        // Get recommendations
        List<Video> recommendations = streamingService.recommendationEngine.getRecommendations("user1", 5);
        System.out.println("Recommendations: " + recommendations.size());
    }
}
