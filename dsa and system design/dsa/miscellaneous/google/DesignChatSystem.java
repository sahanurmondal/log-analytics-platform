package miscellaneous.google;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Question: Design a Real-time Chat System
 * 
 * Description:
 * Design a chat system that supports:
 * - One-on-one messaging
 * - Group chats
 * - Message delivery status (sent, delivered, read)
 * - Online/offline status
 * - Message history with pagination
 * 
 * Company: Google
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignChatSystem {

    enum MessageStatus {
        SENT, DELIVERED, READ
    }

    enum UserStatus {
        ONLINE, OFFLINE, AWAY
    }

    class Message {
        String id;
        String senderId;
        String chatId;
        String content;
        long timestamp;
        MessageStatus status;

        Message(String id, String senderId, String chatId, String content) {
            this.id = id;
            this.senderId = senderId;
            this.chatId = chatId;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.status = MessageStatus.SENT;
        }
    }

    class Chat {
        String id;
        Set<String> participants;
        List<Message> messages;
        boolean isGroupChat;
        String name;

        Chat(String id, Set<String> participants, boolean isGroupChat) {
            this.id = id;
            this.participants = new HashSet<>(participants);
            this.messages = new ArrayList<>();
            this.isGroupChat = isGroupChat;
        }
    }

    class User {
        String id;
        String name;
        UserStatus status;
        long lastSeen;

        User(String id, String name) {
            this.id = id;
            this.name = name;
            this.status = UserStatus.OFFLINE;
            this.lastSeen = System.currentTimeMillis();
        }
    }

    private Map<String, User> users = new ConcurrentHashMap<>();
    private Map<String, Chat> chats = new ConcurrentHashMap<>();
    private Map<String, List<String>> userChats = new ConcurrentHashMap<>();
    private Map<String, Set<String>> onlineUsers = new ConcurrentHashMap<>();

    public String createChat(Set<String> participants, boolean isGroupChat) {
        String chatId = UUID.randomUUID().toString();
        Chat chat = new Chat(chatId, participants, isGroupChat);
        chats.put(chatId, chat);

        for (String userId : participants) {
            userChats.computeIfAbsent(userId, k -> new ArrayList<>()).add(chatId);
        }

        return chatId;
    }

    public void sendMessage(String senderId, String chatId, String content) {
        Chat chat = chats.get(chatId);
        if (chat == null || !chat.participants.contains(senderId)) {
            return;
        }

        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, senderId, chatId, content);
        chat.messages.add(message);

        // Notify online participants
        for (String participantId : chat.participants) {
            if (!participantId.equals(senderId) && users.get(participantId).status == UserStatus.ONLINE) {
                notifyUser(participantId, message);
            }
        }
    }

    public void markMessageAsRead(String userId, String chatId, String messageId) {
        Chat chat = chats.get(chatId);
        if (chat == null || !chat.participants.contains(userId)) {
            return;
        }

        chat.messages.stream()
                .filter(m -> m.id.equals(messageId))
                .findFirst()
                .ifPresent(m -> {
                    if (!m.senderId.equals(userId)) {
                        m.status = MessageStatus.READ;
                    }
                });
    }

    public List<Message> getMessages(String userId, String chatId, int page, int pageSize) {
        Chat chat = chats.get(chatId);
        if (chat == null || !chat.participants.contains(userId)) {
            return new ArrayList<>();
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, chat.messages.size());

        if (start >= chat.messages.size()) {
            return new ArrayList<>();
        }

        return chat.messages.subList(start, end);
    }

    public void setUserStatus(String userId, UserStatus status) {
        User user = users.get(userId);
        if (user != null) {
            user.status = status;
            user.lastSeen = System.currentTimeMillis();
        }
    }

    private void notifyUser(String userId, Message message) {
        // Implementation would send real-time notification
        System.out.println("Notifying user " + userId + " of new message: " + message.content);
    }

    public static void main(String[] args) {
        DesignChatSystem chatSystem = new DesignChatSystem();

        // Create users
        chatSystem.users.put("user1", chatSystem.new User("user1", "Alice"));
        chatSystem.users.put("user2", chatSystem.new User("user2", "Bob"));

        // Create chat
        Set<String> participants = Set.of("user1", "user2");
        String chatId = chatSystem.createChat(participants, false);

        // Send message
        chatSystem.sendMessage("user1", chatId, "Hello Bob!");

        // Get messages
        List<Message> messages = chatSystem.getMessages("user2", chatId, 0, 10);
        System.out.println("Messages: " + messages.size());
    }
}
