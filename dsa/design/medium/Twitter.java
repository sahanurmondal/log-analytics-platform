package design.medium;

import java.util.*;

/**
 * LeetCode 355: Design Twitter
 * https://leetcode.com/problems/design-twitter/
 *
 * Description: Design a simplified version of Twitter where users can post
 * tweets, follow/unfollow another user, and see the 10 most recent tweets in
 * the user's news feed.
 * 
 * Constraints:
 * - 1 <= userId, followerId, followeeId <= 500
 * - 0 <= tweetId <= 10^4
 * - All the tweets have unique IDs
 * - At most 3 * 10^4 calls will be made to postTweet, getNewsFeed, follow, and
 * unfollow
 *
 * Follow-up:
 * - Can you implement the functions with the best time complexity for each?
 * 
 * Time Complexity: O(k log k) for getNewsFeed where k is total tweets, O(1) for
 * others
 * Space Complexity: O(n + m) where n is users, m is tweets
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class Twitter {

    class Tweet {
        int tweetId;
        int timestamp;
        Tweet next;

        Tweet(int tweetId, int timestamp) {
            this.tweetId = tweetId;
            this.timestamp = timestamp;
        }
    }

    private Map<Integer, Set<Integer>> followMap;
    private Map<Integer, Tweet> tweetMap;
    private int timestamp;

    public Twitter() {
        followMap = new HashMap<>();
        tweetMap = new HashMap<>();
        timestamp = 0;
    }

    public void postTweet(int userId, int tweetId) {
        Tweet tweet = new Tweet(tweetId, timestamp++);
        tweet.next = tweetMap.get(userId);
        tweetMap.put(userId, tweet);
    }

    public List<Integer> getNewsFeed(int userId) {
        PriorityQueue<Tweet> maxHeap = new PriorityQueue<>((a, b) -> b.timestamp - a.timestamp);

        // Add user's own tweets
        if (tweetMap.containsKey(userId)) {
            maxHeap.offer(tweetMap.get(userId));
        }

        // Add followees' tweets
        Set<Integer> followees = followMap.get(userId);
        if (followees != null) {
            for (int followeeId : followees) {
                if (tweetMap.containsKey(followeeId)) {
                    maxHeap.offer(tweetMap.get(followeeId));
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 10 && !maxHeap.isEmpty(); i++) {
            Tweet tweet = maxHeap.poll();
            result.add(tweet.tweetId);

            if (tweet.next != null) {
                maxHeap.offer(tweet.next);
            }
        }

        return result;
    }

    public void follow(int followerId, int followeeId) {
        if (followerId == followeeId)
            return;

        followMap.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        Set<Integer> followees = followMap.get(followerId);
        if (followees != null) {
            followees.remove(followeeId);
        }
    }

    public static void main(String[] args) {
        Twitter twitter = new Twitter();
        twitter.postTweet(1, 5);
        System.out.println(twitter.getNewsFeed(1)); // Expected: [5]
        twitter.follow(1, 2);
        twitter.postTweet(2, 6);
        System.out.println(twitter.getNewsFeed(1)); // Expected: [6, 5]
        twitter.unfollow(1, 2);
        System.out.println(twitter.getNewsFeed(1)); // Expected: [5]
    }
}
