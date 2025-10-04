package miscellaneous.slack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Slack Messaging System
 * 
 * Description:
 * Design a team messaging platform that supports:
 * - Channels and direct messages
 * - Message threading
 * - File sharing
 * - Mentions and notifications
 * - Search functionality
 * 
 * Company: Slack
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignSlackMessaging {

    enum MessageType {
        TEXT, FILE, SYSTEM
    }

    enum ChannelType {
        PUBLIC, PRIVATE, DIRECT
    }

    class Message {
        String id;
        String content;
        String senderId;
        String channelId;
        MessageType type;
        long timestamp;
        String threadId;
        List<String> mentions;
        String fileUrl;

        Message(String content, String senderId, String channelId, MessageType type) {
            this.id = UUID.randomUUID().toString();
            this.content = content;
            this.senderId = senderId;
            this.channelId = channelId;
            this.type = type;
            this.timestamp = System.currentTimeMillis();
            this.mentions = extractMentions(content);
        }

        private List<String> extractMentions(String content) {
            List<String> mentions = new ArrayList<>();
            String[] words = content.split(" ");
            for (String word : words) {
                if (word.startsWith("@")) {
                    mentions.add(word.substring(1));
                }
            }
            return mentions;
        }
    }

    class Channel {
        String id;
        String name;
        ChannelType type;
        Set<String> members;
        String workspaceId;
        String description;

        Channel(String name, ChannelType type, String workspaceId) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.type = type;
            this.workspaceId = workspaceId;
            this.members = new HashSet<>();
        }
    }

    class Thread {
        String id;
        String parentMessageId;
        List<Message> replies;

        Thread(String parentMessageId) {
            this.id = UUID.randomUUID().toString();
            this.parentMessageId = parentMessageId;
            this.replies = new ArrayList<>();
        }
    }

    class SearchEngine {
        public List<Message> searchMessages(String query, String userId, String channelId) {
            return messages.values().stream()
                    .filter(m -> channelId == null || m.channelId.equals(channelId))
                    .filter(m -> canUserAccessChannel(userId, m.channelId))
                    .filter(m -> m.content.toLowerCase().contains(query.toLowerCase()))
                    .sorted((m1, m2) -> Long.compare(m2.timestamp, m1.timestamp))
                    .collect(Collectors.toList());
        }

        private boolean canUserAccessChannel(String userId, String channelId) {
            Channel channel = channels.get(channelId);
            return channel != null && channel.members.contains(userId);
        }
    }

    class NotificationManager {
        public void sendNotification(Message message) {
            // Send notifications to mentioned users
            for (String mention : message.mentions) {
                if (users.containsKey(mention)) {
                    sendMentionNotification(mention, message);
                }
            }

            // Send channel notifications
            Channel channel = channels.get(message.channelId);
            if (channel != null) {
                for (String memberId : channel.members) {
                    if (!memberId.equals(message.senderId)) {
                        sendChannelNotification(memberId, message);
                    }
                }
            }
        }

        private void sendMentionNotification(String userId, Message message) {
            System.out.println("Mention notification sent to " + userId);
        }

        private void sendChannelNotification(String userId, Message message) {
            System.out.println("Channel notification sent to " + userId);
        }
    }

    static class User {
        String id;
        String name;
        String email;
        String status;

        User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.status = "active";
        }
    }

    private Map<String, Message> messages = new HashMap<>();
    private Map<String, Channel> channels = new HashMap<>();
    private Map<String, Thread> threads = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private SearchEngine searchEngine = new SearchEngine();
    private NotificationManager notificationManager = new NotificationManager();

    public String sendMessage(String content, String senderId, String channelId) {
        Message message = new Message(content, senderId, channelId, MessageType.TEXT);
        messages.put(message.id, message);

        // Send notifications
        notificationManager.sendNotification(message);

        return message.id;
    }

    public String replyToThread(String content, String senderId, String parentMessageId) {
        Message parentMessage = messages.get(parentMessageId);
        if (parentMessage == null)
            return null;

        Thread thread = threads.get(parentMessage.threadId);
        if (thread == null) {
            thread = new Thread(parentMessageId);
            threads.put(thread.id, thread);
            parentMessage.threadId = thread.id;
        }

        Message reply = new Message(content, senderId, parentMessage.channelId, MessageType.TEXT);
        reply.threadId = thread.id;
        messages.put(reply.id, reply);
        thread.replies.add(reply);

        return reply.id;
    }

    public String createChannel(String name, ChannelType type, String workspaceId) {
        Channel channel = new Channel(name, type, workspaceId);
        channels.put(channel.id, channel);
        return channel.id;
    }

    public void joinChannel(String userId, String channelId) {
        Channel channel = channels.get(channelId);
        if (channel != null && channel.type == ChannelType.PUBLIC) {
            channel.members.add(userId);
        }
    }

    public List<Message> getChannelMessages(String channelId, int limit) {
        return messages.values().stream()
                .filter(m -> m.channelId.equals(channelId))
                .sorted((m1, m2) -> Long.compare(m2.timestamp, m1.timestamp))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Message> searchMessages(String query, String userId, String channelId) {
        return searchEngine.searchMessages(query, userId, channelId);
    }

    public static void main(String[] args) {
        DesignSlackMessaging slack = new DesignSlackMessaging();

        // Create users
        slack.users.put("user1", new User("user1", "Alice", "alice@example.com"));
        slack.users.put("user2", new User("user2", "Bob", "bob@example.com"));

        // Create channel
        String channelId = slack.createChannel("general", ChannelType.PUBLIC, "workspace1");

        // Join channel
        slack.joinChannel("user1", channelId);
        slack.joinChannel("user2", channelId);

        // Send message
        String messageId = slack.sendMessage("Hello @user2!", "user1", channelId);
        System.out.println("Message sent: " + messageId);

        // Reply to message
        String replyId = slack.replyToThread("Hi @user1!", "user2", messageId);
        System.out.println("Reply sent: " + replyId);

        // Search messages
        List<Message> searchResults = slack.searchMessages("Hello", "user1", channelId);
        System.out.println("Search results: " + searchResults.size());
    }
}
