package design.medium;

import java.util.*;

/**
 * LeetCode 1912: Design a Video Sharing Platform
 * https://leetcode.com/problems/design-a-video-sharing-platform/
 *
 * Description: Design a video sharing platform that supports uploading,
 * viewing, liking, disliking videos.
 * 
 * Constraints:
 * - 1 <= video.length <= 10^5
 * - 0 <= startTime < endTime <= video.length
 * - At most 10^5 calls will be made in total
 *
 * Follow-up:
 * - How to handle concurrent access?
 * - Video recommendation system?
 * 
 * Time Complexity: O(1) for most operations, O(log n) for sorted operations
 * Space Complexity: O(videos + views)
 * 
 * Company Tags: Google, Facebook, Netflix
 */
public class DesignVideoSharingPlatform {

    class Video {
        int videoId;
        String content;
        int likes;
        int dislikes;
        int views;

        Video(int videoId, String content) {
            this.videoId = videoId;
            this.content = content;
            this.likes = 0;
            this.dislikes = 0;
            this.views = 0;
        }
    }

    private Map<Integer, Video> videos;
    private PriorityQueue<Integer> removedIds;
    private int nextId;

    public DesignVideoSharingPlatform() {
        videos = new HashMap<>();
        removedIds = new PriorityQueue<>();
        nextId = 0;
    }

    public int upload(String video) {
        int videoId;
        if (!removedIds.isEmpty()) {
            videoId = removedIds.poll();
        } else {
            videoId = nextId++;
        }

        videos.put(videoId, new Video(videoId, video));
        return videoId;
    }

    public void remove(int videoId) {
        if (videos.containsKey(videoId)) {
            videos.remove(videoId);
            removedIds.offer(videoId);
        }
    }

    public String watch(int videoId, int startTime, int endTime) {
        Video video = videos.get(videoId);
        if (video == null) {
            return "-1";
        }

        video.views++;

        String content = video.content;
        int start = Math.max(0, Math.min(startTime, content.length()));
        int end = Math.max(start, Math.min(endTime, content.length()));

        return content.substring(start, end);
    }

    public void like(int videoId) {
        Video video = videos.get(videoId);
        if (video != null) {
            video.likes++;
        }
    }

    public void dislike(int videoId) {
        Video video = videos.get(videoId);
        if (video != null) {
            video.dislikes++;
        }
    }

    public int[] getLikesAndDislikes(int videoId) {
        Video video = videos.get(videoId);
        if (video == null) {
            return new int[] { -1 };
        }
        return new int[] { video.likes, video.dislikes };
    }

    public int getViews(int videoId) {
        Video video = videos.get(videoId);
        return video == null ? -1 : video.views;
    }

    // Additional features
    public List<Integer> getTrendingVideos(int k) {
        PriorityQueue<Video> trending = new PriorityQueue<>((a, b) -> {
            // Sort by engagement score (likes + views - dislikes)
            int scoreA = a.likes + a.views - a.dislikes;
            int scoreB = b.likes + b.views - b.dislikes;
            if (scoreA != scoreB) {
                return scoreB - scoreA;
            }
            return a.videoId - b.videoId; // Tie-breaker: smaller ID first
        });

        trending.addAll(videos.values());

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < k && !trending.isEmpty(); i++) {
            result.add(trending.poll().videoId);
        }

        return result;
    }

    public List<Integer> searchVideos(String query) {
        List<Integer> results = new ArrayList<>();

        for (Video video : videos.values()) {
            if (video.content.toLowerCase().contains(query.toLowerCase())) {
                results.add(video.videoId);
            }
        }

        // Sort by relevance (views + likes)
        results.sort((a, b) -> {
            Video videoA = videos.get(a);
            Video videoB = videos.get(b);
            int scoreA = videoA.views + videoA.likes;
            int scoreB = videoB.views + videoB.likes;
            return scoreB - scoreA;
        });

        return results;
    }

    public Map<String, Object> getVideoStats(int videoId) {
        Video video = videos.get(videoId);
        if (video == null) {
            return null;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("videoId", video.videoId);
        stats.put("likes", video.likes);
        stats.put("dislikes", video.dislikes);
        stats.put("views", video.views);
        stats.put("length", video.content.length());
        stats.put("engagement", video.likes + video.views - video.dislikes);

        return stats;
    }

    public static void main(String[] args) {
        DesignVideoSharingPlatform platform = new DesignVideoSharingPlatform();

        // Upload videos
        int video1 = platform.upload("abcdef");
        int video2 = platform.upload("ghijkl");
        System.out.println("Uploaded video1: " + video1); // Expected: 0
        System.out.println("Uploaded video2: " + video2); // Expected: 1

        // Watch videos
        System.out.println("Watch video1[1,3]: " + platform.watch(video1, 1, 3)); // Expected: "bc"
        System.out.println("Watch video2[0,6]: " + platform.watch(video2, 0, 6)); // Expected: "ghijkl"

        // Like and dislike
        platform.like(video1);
        platform.like(video1);
        platform.dislike(video1);
        platform.like(video2);

        // Get stats
        System.out.println("Video1 likes/dislikes: " + Arrays.toString(platform.getLikesAndDislikes(video1))); // Expected:
                                                                                                               // [2, 1]
        System.out.println("Video1 views: " + platform.getViews(video1)); // Expected: 1

        // Remove and test
        platform.remove(video1);
        System.out.println("Watch removed video1: " + platform.watch(video1, 0, 1)); // Expected: "-1"

        // Upload new video (should reuse ID 0)
        int video3 = platform.upload("newvideo");
        System.out.println("New video ID: " + video3); // Expected: 0

        // Test additional features
        platform.upload("trending content");
        platform.upload("popular video");

        for (int i = 0; i < 5; i++) {
            platform.like(video3);
            platform.watch(video3, 0, 5);
        }

        System.out.println("Trending videos: " + platform.getTrendingVideos(3));
        System.out.println("Search 'video': " + platform.searchVideos("video"));
        System.out.println("Video3 stats: " + platform.getVideoStats(video3));
    }
}
