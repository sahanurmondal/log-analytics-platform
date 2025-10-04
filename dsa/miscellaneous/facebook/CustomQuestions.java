package miscellaneous.facebook;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Facebook/Meta Interview Questions (2021-2024)
 * Based on real interview experiences from LeetCode Discuss and Glassdoor
 */
public class CustomQuestions {

    /**
     * Custom Question: Design a News Feed Ranking System
     * 
     * Description:
     * Design a system that ranks posts in a user's news feed based on:
     * - Recency (newer posts have higher priority)
     * - User engagement (likes, comments, shares)
     * - Friend relationship strength
     * - Content type preferences
     * 
     * Company: Facebook/Meta
     * Difficulty: Hard
     * Asked: System design interviews 2023-2024
     */
    class NewsFeedRanking {
        class Post {
            String id;
            String userId;
            long timestamp;
            int likes;
            int comments;
            int shares;
            String contentType;

            Post(String id, String userId, long timestamp, String contentType) {
                this.id = id;
                this.userId = userId;
                this.timestamp = timestamp;
                this.contentType = contentType;
            }
        }

        class FriendshipScore {
            Map<String, Double> friendScores = new HashMap<>();

            public double getScore(String userId, String friendId) {
                return friendScores.getOrDefault(userId + "_" + friendId, 1.0);
            }

            public void updateScore(String userId, String friendId, double score) {
                friendScores.put(userId + "_" + friendId, score);
            }
        }

        private FriendshipScore friendshipScore = new FriendshipScore();

        public double calculatePostScore(Post post, String viewerUserId) {
            double recencyScore = calculateRecencyScore(post.timestamp);
            double engagementScore = calculateEngagementScore(post);
            double friendshipScore = this.friendshipScore.getScore(viewerUserId, post.userId);
            double contentTypeScore = getContentTypePreference(viewerUserId, post.contentType);

            return recencyScore * 0.3 + engagementScore * 0.4 + friendshipScore * 0.2 + contentTypeScore * 0.1;
        }

        private double calculateRecencyScore(long timestamp) {
            long timeDiff = System.currentTimeMillis() - timestamp;
            return Math.exp(-timeDiff / (24 * 60 * 60 * 1000.0)); // Exponential decay over days
        }

        private double calculateEngagementScore(Post post) {
            return Math.log(1 + post.likes * 1.0 + post.comments * 2.0 + post.shares * 3.0);
        }

        private double getContentTypePreference(String userId, String contentType) {
            // Simplified - would normally query user preferences
            Map<String, Double> preferences = Map.of(
                    "photo", 1.0,
                    "video", 0.8,
                    "text", 0.6,
                    "link", 0.4);
            return preferences.getOrDefault(contentType, 0.5);
        }

        public List<Post> rankPosts(List<Post> posts, String viewerUserId) {
            return posts.stream()
                    .sorted((p1, p2) -> Double.compare(
                            calculatePostScore(p2, viewerUserId),
                            calculatePostScore(p1, viewerUserId)))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Custom Question: Design a Friend Suggestion System
     * 
     * Description:
     * Implement "People You May Know" feature that suggests friends based on:
     * - Mutual friends
     * - Common interests
     * - Location proximity
     * - Contact book matching
     * 
     * Company: Facebook/Meta
     * Difficulty: Medium
     * Asked: ML/Backend interviews 2023-2024
     */
    class FriendSuggestion {
        class User {
            String id;
            Set<String> friends;
            Set<String> interests;
            String location;
            Set<String> contacts;

            User(String id) {
                this.id = id;
                this.friends = new HashSet<>();
                this.interests = new HashSet<>();
                this.contacts = new HashSet<>();
            }
        }

        private Map<String, User> users = new HashMap<>();

        public List<String> suggestFriends(String userId, int limit) {
            User user = users.get(userId);
            if (user == null)
                return new ArrayList<>();

            Map<String, Double> candidateScores = new HashMap<>();

            // Score based on mutual friends
            for (String friendId : user.friends) {
                User friend = users.get(friendId);
                if (friend != null) {
                    for (String mutualFriend : friend.friends) {
                        if (!user.friends.contains(mutualFriend) && !mutualFriend.equals(userId)) {
                            candidateScores.put(mutualFriend,
                                    candidateScores.getOrDefault(mutualFriend, 0.0) + 0.4);
                        }
                    }
                }
            }

            // Score based on common interests
            for (String candidateId : candidateScores.keySet()) {
                User candidate = users.get(candidateId);
                if (candidate != null) {
                    long commonInterests = user.interests.stream()
                            .filter(candidate.interests::contains)
                            .count();
                    candidateScores.put(candidateId,
                            candidateScores.get(candidateId) + commonInterests * 0.1);
                }
            }

            // Score based on contact book
            for (String contactId : user.contacts) {
                if (users.containsKey(contactId) && !user.friends.contains(contactId)) {
                    candidateScores.put(contactId,
                            candidateScores.getOrDefault(contactId, 0.0) + 0.5);
                }
            }

            return candidateScores.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Custom Question: Design a Content Moderation System
     * 
     * Description:
     * Build a system that can automatically flag inappropriate content:
     * - Text analysis for hate speech, spam
     * - Image recognition for inappropriate images
     * - User reporting system
     * - Appeal process
     * 
     * Company: Facebook/Meta
     * Difficulty: Hard
     * Asked: Safety/Security team interviews 2023-2024
     */
    class ContentModerationSystem {
        enum ContentType {
            TEXT, IMAGE, VIDEO
        }

        enum ModerationAction {
            APPROVE, FLAG, REMOVE, SHADOW_BAN
        }

        class Content {
            String id;
            String userId;
            ContentType type;
            String data;
            long timestamp;
            int reportCount;
            Set<String> reporters;

            Content(String id, String userId, ContentType type, String data) {
                this.id = id;
                this.userId = userId;
                this.type = type;
                this.data = data;
                this.timestamp = System.currentTimeMillis();
                this.reportCount = 0;
                this.reporters = new HashSet<>();
            }
        }

        private Map<String, Content> contents = new HashMap<>();
        private Set<String> bannedWords = new HashSet<>();
        private Map<String, Integer> userViolations = new HashMap<>();

        public ContentModerationSystem() {
            // Initialize with some banned words
            bannedWords.addAll(Arrays.asList("spam", "hate", "violence"));
        }

        public ModerationAction moderateContent(Content content) {
            double riskScore = calculateRiskScore(content);

            if (riskScore > 0.9) {
                return ModerationAction.REMOVE;
            } else if (riskScore > 0.7) {
                return ModerationAction.FLAG;
            } else if (riskScore > 0.5) {
                return ModerationAction.SHADOW_BAN;
            }

            return ModerationAction.APPROVE;
        }

        private double calculateRiskScore(Content content) {
            double score = 0.0;

            // Text analysis
            if (content.type == ContentType.TEXT) {
                score += analyzeText(content.data);
            }

            // User history
            int violations = userViolations.getOrDefault(content.userId, 0);
            score += violations * 0.1;

            // Report count
            score += content.reportCount * 0.05;

            return Math.min(score, 1.0);
        }

        private double analyzeText(String text) {
            double score = 0.0;
            String lowerText = text.toLowerCase();

            for (String bannedWord : bannedWords) {
                if (lowerText.contains(bannedWord)) {
                    score += 0.3;
                }
            }

            // Check for excessive caps, repeated characters, etc.
            if (text.length() > 0) {
                long capsCount = text.chars().filter(Character::isUpperCase).count();
                if (capsCount > text.length() * 0.7) {
                    score += 0.2;
                }
            }

            return Math.min(score, 1.0);
        }

        public void reportContent(String contentId, String reporterId) {
            Content content = contents.get(contentId);
            if (content != null && !content.reporters.contains(reporterId)) {
                content.reporters.add(reporterId);
                content.reportCount++;

                // Re-moderate if enough reports
                if (content.reportCount >= 5) {
                    ModerationAction action = moderateContent(content);
                    if (action == ModerationAction.REMOVE) {
                        contents.remove(contentId);
                        userViolations.put(content.userId,
                                userViolations.getOrDefault(content.userId, 0) + 1);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test implementations would go here
        System.out.println("Custom Facebook/Meta interview questions implemented");
    }
}
