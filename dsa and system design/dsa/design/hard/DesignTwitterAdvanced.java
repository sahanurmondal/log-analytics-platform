package design.hard;

import java.util.*;

/**
 * Advanced Twitter Design with Additional Features
 * Enhanced version of LeetCode 355 with more features
 *
 * Description: Design Twitter with advanced features:
 * - Tweet with media, hashtags, mentions
 * - Trending topics
 * - Tweet statistics
 * - User verification
 * 
 * Constraints:
 * - Enhanced functionality over basic Twitter
 * - Support for multimedia content
 * - Real-time trending analysis
 *
 * Follow-up:
 * - How to scale for millions of users?
 * - Real-time notifications?
 * 
 * Time Complexity: Various based on operation
 * Space Complexity: O(users + tweets + relationships)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class DesignTwitterAdvanced {

    class Tweet {
        int tweetId;
        int userId;
        String content;
        List<String> hashtags;
        List<Integer> mentions;
        List<String> media;
        long timestamp;
        int likes;
        int retweets;

        Tweet(int tweetId, int userId, String content) {
            this.tweetId = tweetId;
            this.userId = userId;
            this.content = content;
            this.hashtags = new ArrayList<>();
            this.mentions = new ArrayList<>();
            this.media = new ArrayList<>();
            this.timestamp = System.currentTimeMillis();
            this.likes = 0;
            this.retweets = 0;

            parseContent();
        }

        private void parseContent() {
            String[] words = content.split("\\s+");
            for (String word : words) {
                if (word.startsWith("#")) {
                    hashtags.add(word.substring(1).toLowerCase());
                } else if (word.startsWith("@")) {
                    try {
                        mentions.add(Integer.parseInt(word.substring(1)));
                    } catch (NumberFormatException e) {
                        // Invalid mention format
                    }
                }
            }
        }
    }

    class User {
        int userId;
        String username;
        boolean verified;
        Set<Integer> followers;
        Set<Integer> following;
        List<Tweet> tweets;

        User(int userId, String username) {
            this.userId = userId;
            this.username = username;
            this.verified = false;
            this.followers = new HashSet<>();
            this.following = new HashSet<>();
            this.tweets = new ArrayList<>();
        }
    }

    private Map<Integer, User> users;
    private Map<Integer, Tweet> tweets;
    private Map<String, Integer> hashtagCount;
    private PriorityQueue<Map.Entry<String, Integer>> trendingTopics;
    private int tweetIdCounter;

    public DesignTwitterAdvanced() {
        users = new HashMap<>();
        tweets = new HashMap<>();
        hashtagCount = new HashMap<>();
        trendingTopics = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        tweetIdCounter = 1;
    }

    public void createUser(int userId, String username) {
        if (!users.containsKey(userId)) {
            users.put(userId, new User(userId, username));
        }
    }

    public void verifyUser(int userId) {
        User user = users.get(userId);
        if (user != null) {
            user.verified = true;
        }
    }

    public int postTweet(int userId, String content) {
        User user = users.get(userId);
        if (user == null) {
            createUser(userId, "User" + userId);
            user = users.get(userId);
        }

        int tweetId = tweetIdCounter++;
        Tweet tweet = new Tweet(tweetId, userId, content);

        tweets.put(tweetId, tweet);
        user.tweets.add(tweet);

        // Update hashtag counts
        for (String hashtag : tweet.hashtags) {
            hashtagCount.put(hashtag, hashtagCount.getOrDefault(hashtag, 0) + 1);
        }

        return tweetId;
    }

    public void follow(int followerId, int followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower != null && followee != null && followerId != followeeId) {
            follower.following.add(followeeId);
            followee.followers.add(followerId);
        }
    }

    public void unfollow(int followerId, int followeeId) {
        User follower = users.get(followerId);
        User followee = users.get(followeeId);

        if (follower != null && followee != null) {
            follower.following.remove(followeeId);
            followee.followers.remove(followerId);
        }
    }

    public List<Integer> getNewsFeed(int userId) {
        User user = users.get(userId);
        if (user == null) {
            return new ArrayList<>();
        }

        PriorityQueue<Tweet> feed = new PriorityQueue<>((a, b) -> Long.compare(b.timestamp, a.timestamp));

        // Add user's own tweets
        feed.addAll(user.tweets);

        // Add followees' tweets
        for (int followeeId : user.following) {
            User followee = users.get(followeeId);
            if (followee != null) {
                feed.addAll(followee.tweets);
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 10 && !feed.isEmpty(); i++) {
            result.add(feed.poll().tweetId);
        }

        return result;
    }

    public void likeTweet(int userId, int tweetId) {
        Tweet tweet = tweets.get(tweetId);
        if (tweet != null) {
            tweet.likes++;
        }
    }

    public void retweetTweet(int userId, int tweetId) {
        Tweet originalTweet = tweets.get(tweetId);
        if (originalTweet != null) {
            originalTweet.retweets++;
            // Create retweet entry
            postTweet(userId, "RT @" + originalTweet.userId + ": " + originalTweet.content);
        }
    }

    public List<String> getTrendingHashtags(int k) {
        PriorityQueue<Map.Entry<String, Integer>> trending = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        trending.addAll(hashtagCount.entrySet());

        List<String> result = new ArrayList<>();
        for (int i = 0; i < k && !trending.isEmpty(); i++) {
            result.add("#" + trending.poll().getKey());
        }

        return result;
    }

    public List<Integer> searchTweets(String hashtag) {
        List<Integer> result = new ArrayList<>();
        String searchTag = hashtag.startsWith("#") ? hashtag.substring(1).toLowerCase() : hashtag.toLowerCase();

        for (Tweet tweet : tweets.values()) {
            if (tweet.hashtags.contains(searchTag)) {
                result.add(tweet.tweetId);
            }
        }

        // Sort by timestamp (newest first)
        result.sort((a, b) -> Long.compare(tweets.get(b).timestamp, tweets.get(a).timestamp));

        return result;
    }

    public Map<String, Object> getTweetStats(int tweetId) {
        Tweet tweet = tweets.get(tweetId);
        if (tweet == null) {
            return null;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("likes", tweet.likes);
        stats.put("retweets", tweet.retweets);
        stats.put("hashtags", tweet.hashtags);
        stats.put("mentions", tweet.mentions);
        stats.put("timestamp", tweet.timestamp);

        return stats;
    }

    public static void main(String[] args) {
        DesignTwitterAdvanced twitter = new DesignTwitterAdvanced();

        // Create users
        twitter.createUser(1, "alice");
        twitter.createUser(2, "bob");
        twitter.createUser(3, "charlie");

        // Verify user
        twitter.verifyUser(1);

        // Post tweets
        int tweet1 = twitter.postTweet(1, "Hello #world! Great day for #coding @2");
        int tweet2 = twitter.postTweet(2, "Learning #algorithms and #datastructures");
        int tweet3 = twitter.postTweet(3, "Beautiful #sunset today #photography");

        // Follow relationships
        twitter.follow(1, 2);
        twitter.follow(1, 3);
        twitter.follow(2, 3);

        // Get news feed
        System.out.println("News feed for user 1: " + twitter.getNewsFeed(1));

        // Like and retweet
        twitter.likeTweet(2, tweet1);
        twitter.retweetTweet(2, tweet1);

        // Get trending hashtags
        System.out.println("Trending hashtags: " + twitter.getTrendingHashtags(3));

        // Search tweets
        System.out.println("Tweets with #world: " + twitter.searchTweets("#world"));

        // Get tweet stats
        System.out.println("Tweet " + tweet1 + " stats: " + twitter.getTweetStats(tweet1));
    }
}
