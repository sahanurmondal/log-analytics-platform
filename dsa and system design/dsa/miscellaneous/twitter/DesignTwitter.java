package miscellaneous.twitter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Twitter Timeline System
 * 
 * Description:
 * Design a Twitter-like social media platform that supports:
 * - Tweet posting and retrieval
 * - User following/unfollowing
 * - Timeline generation (home and user timeline)
 * - Tweet engagement (likes, retweets, replies)
 * - Trending topics
 * 
 * Company: Twitter/X
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignTwitter {

    class Tweet {
        String id;
        String userId;
        String content;
        long timestamp;
        int likes;
        int retweets;
        int replies;
        List<String> hashtags;
        String replyToTweetId;

        Tweet(String userId, String content) {
            this.id = UUID.randomUUID().toString();
            this.userId = userId;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.likes = 0;
            this.retweets = 0;
            this.replies = 0;
            this.hashtags = extractHashtags(content);
        }

        private List<String> extractHashtags(String content) {
            List<String> hashtags = new ArrayList<>();
            String[] words = content.split(" ");
            for (String word : words) {
                if (word.startsWith("#")) {
                    hashtags.add(word.substring(1).toLowerCase());
                }
            }
            return hashtags;
        }
    }

    class User {
        String id;
        String username;
        String displayName;
        Set<String> following;
        Set<String> followers;
        List<String> tweets;

        User(String id, String username, String displayName) {
            this.id = id;
            this.username = username;
            this.displayName = displayName;
            this.following = new HashSet<>();
            this.followers = new HashSet<>();
            this.tweets = new ArrayList<>();
        }
    }

    class TimelineGenerator {
        public List<Tweet> generateHomeTimeline(String userId, int limit) {
            User user = users.get(userId);
            if (user == null)
                return new ArrayList<>();

            PriorityQueue<Tweet> timeline = new PriorityQueue<>(
                    (t1, t2) -> Long.compare(t2.timestamp, t1.timestamp));

            // Add tweets from followed users
            for (String followeeId : user.following) {
                User followee = users.get(followeeId);
                if (followee != null) {
                    for (String tweetId : followee.tweets) {
                        Tweet tweet = tweets.get(tweetId);
                        if (tweet != null) {
                            timeline.offer(tweet);
                        }
                    }
                }
            }

            // Add user's own tweets
            for (String tweetId : user.tweets) {
                Tweet tweet = tweets.get(tweetId);
                if (tweet != null) {
                    timeline.offer(tweet);
                }
            }

            List<Tweet> result = new ArrayList<>();
            while (!timeline.isEmpty() && result.size() < limit) {
                result.add(timeline.poll());
            }

            return result;
        }

        public List<Tweet> generateUserTimeline(String userId, int limit) {
            User user = users.get(userId);
            if (user == null)
                return new ArrayList<>();

            return user.tweets.stream()
                    .map(tweetId -> tweets.get(tweetId))
                    .filter(tweet -> tweet != null) // Replace Objects::nonNull with explicit null check
                    .sorted((t1, t2) -> Long.compare(t2.timestamp, t1.timestamp))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    class TrendingTopics {
        private Map<String, Integer> hashtagCounts = new HashMap<>();
        private long lastUpdate = 0;
        private final long UPDATE_INTERVAL = 60 * 1000; // 1 minute

        public void updateTrends() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate < UPDATE_INTERVAL) {
                return;
            }

            hashtagCounts.clear();

            // Count hashtags from recent tweets (last 24 hours)
            long twentyFourHoursAgo = currentTime - 24 * 60 * 60 * 1000;

            for (Tweet tweet : tweets.values()) {
                if (tweet.timestamp > twentyFourHoursAgo) {
                    for (String hashtag : tweet.hashtags) {
                        hashtagCounts.put(hashtag, hashtagCounts.getOrDefault(hashtag, 0) + 1);
                    }
                }
            }

            lastUpdate = currentTime;
        }

        public List<String> getTrendingTopics(int limit) {
            updateTrends();

            return hashtagCounts.entrySet().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
    }

    private Map<String, User> users = new HashMap<>();
    private Map<String, Tweet> tweets = new HashMap<>();
    private Map<String, Set<String>> userLikes = new HashMap<>();
    private TimelineGenerator timelineGenerator = new TimelineGenerator();
    private TrendingTopics trendingTopics = new TrendingTopics();

    public String postTweet(String userId, String content) {
        User user = users.get(userId);
        if (user == null)
            return null;

        Tweet tweet = new Tweet(userId, content);
        tweets.put(tweet.id, tweet);
        user.tweets.add(tweet.id);

        return tweet.id;
    }

    public void follow(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower != null && followee != null && !followerId.equals(followeeId)) {
            follower.following.add(followeeId);
            followee.followers.add(followerId);
        }
    }

    public void unfollow(String followerId, String followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower != null && followee != null) {
            follower.following.remove(followeeId);
            followee.followers.remove(followerId);
        }
    }

    public void likeTweet(String userId, String tweetId) {
        Tweet tweet = tweets.get(tweetId);
        if (tweet != null) {
            Set<String> likedTweets = userLikes.computeIfAbsent(userId, k -> new HashSet<>());
            if (!likedTweets.contains(tweetId)) {
                likedTweets.add(tweetId);
                tweet.likes++;
            }
        }
    }

    public void unlikeTweet(String userId, String tweetId) {
        Tweet tweet = tweets.get(tweetId);
        if (tweet != null) {
            Set<String> likedTweets = userLikes.get(userId);
            if (likedTweets != null && likedTweets.contains(tweetId)) {
                likedTweets.remove(tweetId);
                tweet.likes--;
            }
        }
    }

    public String retweet(String userId, String tweetId) {
        Tweet originalTweet = tweets.get(tweetId);
        if (originalTweet == null)
            return null;

        String retweetContent = "RT @" + users.get(originalTweet.userId).username + ": " + originalTweet.content;
        String retweetId = postTweet(userId, retweetContent);

        if (retweetId != null) {
            originalTweet.retweets++;
        }

        return retweetId;
    }

    public List<Tweet> getHomeTimeline(String userId, int limit) {
        return timelineGenerator.generateHomeTimeline(userId, limit);
    }

    public List<Tweet> getUserTimeline(String userId, int limit) {
        return timelineGenerator.generateUserTimeline(userId, limit);
    }

    public List<String> getTrendingTopics(int limit) {
        return trendingTopics.getTrendingTopics(limit);
    }

    public static void main(String[] args) {
        DesignTwitter twitter = new DesignTwitter();

        // Create users
        twitter.users.put("user1", twitter.new User("user1", "alice", "Alice"));
        twitter.users.put("user2", twitter.new User("user2", "bob", "Bob"));

        // Post tweets
        String tweetId1 = twitter.postTweet("user1", "Hello world! #firstTweet");
        String tweetId2 = twitter.postTweet("user2", "Great day today! #sunny #happy");

        // Follow
        twitter.follow("user1", "user2");

        // Like tweet
        twitter.likeTweet("user1", tweetId2);

        // Get timeline
        List<Tweet> timeline = twitter.getHomeTimeline("user1", 10);
        System.out.println("Timeline size: " + timeline.size());

        // Get trending topics
        List<String> trending = twitter.getTrendingTopics(5);
        System.out.println("Trending topics: " + trending);
    }
}
