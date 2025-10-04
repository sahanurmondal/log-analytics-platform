package design.medium;

import java.util.*;

/**
 * Design Chat Room System
 *
 * Description: Design a chat room system that supports:
 * - Multiple chat rooms
 * - User join/leave rooms
 * - Send/receive messages
 * - Message history
 * - User presence
 * 
 * Constraints:
 * - Support up to 1000 users per room
 * - Message history limited to 1000 messages per room
 * - User names are unique
 *
 * Follow-up:
 * - How to handle message delivery guarantees?
 * - Real-time notifications?
 * 
 * Time Complexity: O(1) for most operations, O(n) for broadcasting
 * Space Complexity: O(rooms * users + messages)
 * 
 * Company Tags: Google, Facebook, Slack
 */
public class DesignChatRoom {

    enum UserStatus {
        ONLINE, AWAY, OFFLINE
    }

    class User {
        String username;
        UserStatus status;
        long lastActivity;
        Set<String> joinedRooms;

        User(String username) {
            this.username = username;
            this.status = UserStatus.ONLINE;
            this.lastActivity = System.currentTimeMillis();
            this.joinedRooms = new HashSet<>();
        }

        void updateActivity() {
            this.lastActivity = System.currentTimeMillis();
            if (this.status == UserStatus.AWAY) {
                this.status = UserStatus.ONLINE;
            }
        }
    }

    class Message {
        String messageId;
        String sender;
        String content;
        long timestamp;
        String roomId;

        Message(String sender, String content, String roomId) {
            this.messageId = UUID.randomUUID().toString();
            this.sender = sender;
            this.content = content;
            this.roomId = roomId;
            this.timestamp = System.currentTimeMillis();
        }
    }

    class ChatRoom {
        String roomId;
        String roomName;
        Set<String> members;
        List<Message> messageHistory;
        int maxMembers;
        int maxMessages;

        ChatRoom(String roomId, String roomName) {
            this.roomId = roomId;
            this.roomName = roomName;
            this.members = new HashSet<>();
            this.messageHistory = new ArrayList<>();
            this.maxMembers = 1000;
            this.maxMessages = 1000;
        }

        boolean addMember(String username) {
            if (members.size() >= maxMembers) {
                return false;
            }
            return members.add(username);
        }

        void addMessage(Message message) {
            messageHistory.add(message);
            if (messageHistory.size() > maxMessages) {
                messageHistory.remove(0); // Remove oldest message
            }
        }
    }

    private Map<String, User> users;
    private Map<String, ChatRoom> rooms;
    private Map<String, Set<String>> userNotifications; // username -> pending notifications

    public DesignChatRoom() {
        users = new HashMap<>();
        rooms = new HashMap<>();
        userNotifications = new HashMap<>();
    }

    public boolean registerUser(String username) {
        if (users.containsKey(username)) {
            return false;
        }

        users.put(username, new User(username));
        userNotifications.put(username, new HashSet<>());
        return true;
    }

    public String createRoom(String roomName) {
        String roomId = "room_" + UUID.randomUUID().toString().substring(0, 8);
        rooms.put(roomId, new ChatRoom(roomId, roomName));
        return roomId;
    }

    public boolean joinRoom(String username, String roomId) {
        User user = users.get(username);
        ChatRoom room = rooms.get(roomId);

        if (user == null || room == null) {
            return false;
        }

        if (room.addMember(username)) {
            user.joinedRooms.add(roomId);
            user.updateActivity();

            // Notify other room members
            broadcastSystemMessage(roomId, username + " joined the room", username);
            return true;
        }

        return false;
    }

    public boolean leaveRoom(String username, String roomId) {
        User user = users.get(username);
        ChatRoom room = rooms.get(roomId);

        if (user == null || room == null) {
            return false;
        }

        if (room.members.remove(username)) {
            user.joinedRooms.remove(roomId);
            user.updateActivity();

            // Notify other room members
            broadcastSystemMessage(roomId, username + " left the room", username);
            return true;
        }

        return false;
    }

    public boolean sendMessage(String username, String roomId, String content) {
        User user = users.get(username);
        ChatRoom room = rooms.get(roomId);

        if (user == null || room == null || !room.members.contains(username)) {
            return false;
        }

        Message message = new Message(username, content, roomId);
        room.addMessage(message);
        user.updateActivity();

        // Notify all room members except sender
        for (String member : room.members) {
            if (!member.equals(username)) {
                userNotifications.get(member).add(message.messageId);
            }
        }

        return true;
    }

    private void broadcastSystemMessage(String roomId, String content, String excludeUser) {
        ChatRoom room = rooms.get(roomId);
        if (room == null)
            return;

        Message systemMessage = new Message("SYSTEM", content, roomId);
        room.addMessage(systemMessage);

        // Notify all room members except excluded user
        for (String member : room.members) {
            if (!member.equals(excludeUser)) {
                userNotifications.get(member).add(systemMessage.messageId);
            }
        }
    }

    public List<Message> getRoomHistory(String username, String roomId, int limit) {
        User user = users.get(username);
        ChatRoom room = rooms.get(roomId);

        if (user == null || room == null || !room.members.contains(username)) {
            return new ArrayList<>();
        }

        user.updateActivity();
        List<Message> history = room.messageHistory;
        int start = Math.max(0, history.size() - limit);

        return new ArrayList<>(history.subList(start, history.size()));
    }

    public List<String> getRoomMembers(String username, String roomId) {
        User user = users.get(username);
        ChatRoom room = rooms.get(roomId);

        if (user == null || room == null || !room.members.contains(username)) {
            return new ArrayList<>();
        }

        return new ArrayList<>(room.members);
    }

    public List<String> getUserRooms(String username) {
        User user = users.get(username);
        if (user == null) {
            return new ArrayList<>();
        }

        user.updateActivity();
        return new ArrayList<>(user.joinedRooms);
    }

    public void setUserStatus(String username, UserStatus status) {
        User user = users.get(username);
        if (user != null) {
            user.status = status;
            user.updateActivity();
        }
    }

    public UserStatus getUserStatus(String username) {
        User user = users.get(username);
        if (user == null) {
            return null;
        }

        // Auto-update status based on activity
        long timeSinceActivity = System.currentTimeMillis() - user.lastActivity;
        if (timeSinceActivity > 300000 && user.status == UserStatus.ONLINE) { // 5 minutes
            user.status = UserStatus.AWAY;
        } else if (timeSinceActivity > 1800000) { // 30 minutes
            user.status = UserStatus.OFFLINE;
        }

        return user.status;
    }

    public int getUnreadCount(String username) {
        Set<String> notifications = userNotifications.get(username);
        return notifications == null ? 0 : notifications.size();
    }

    public void markAsRead(String username, String messageId) {
        Set<String> notifications = userNotifications.get(username);
        if (notifications != null) {
            notifications.remove(messageId);
        }
    }

    public void clearNotifications(String username) {
        Set<String> notifications = userNotifications.get(username);
        if (notifications != null) {
            notifications.clear();
        }
    }

    public Map<String, Object> getRoomInfo(String roomId) {
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("roomId", room.roomId);
        info.put("roomName", room.roomName);
        info.put("memberCount", room.members.size());
        info.put("messageCount", room.messageHistory.size());

        return info;
    }

    public static void main(String[] args) {
        DesignChatRoom chatSystem = new DesignChatRoom();

        // Register users
        chatSystem.registerUser("alice");
        chatSystem.registerUser("bob");
        chatSystem.registerUser("charlie");

        // Create room
        String roomId = chatSystem.createRoom("General Discussion");
        System.out.println("Created room: " + roomId);

        // Users join room
        chatSystem.joinRoom("alice", roomId);
        chatSystem.joinRoom("bob", roomId);

        // Send messages
        chatSystem.sendMessage("alice", roomId, "Hello everyone!");
        chatSystem.sendMessage("bob", roomId, "Hi Alice!");

        // Charlie joins and sends message
        chatSystem.joinRoom("charlie", roomId);
        chatSystem.sendMessage("charlie", roomId, "Hello all!");

        // Get room history
        List<Message> history = chatSystem.getRoomHistory("alice", roomId, 10);
        System.out.println("\nRoom history:");
        for (Message msg : history) {
            System.out.println(msg.sender + ": " + msg.content);
        }

        // Check notifications
        System.out.println("\nBob's unread count: " + chatSystem.getUnreadCount("bob"));

        // Get room info
        System.out.println("\nRoom info: " + chatSystem.getRoomInfo(roomId));

        // Check user status
        System.out.println("Alice status: " + chatSystem.getUserStatus("alice"));

        // Set user away
        chatSystem.setUserStatus("alice", UserStatus.AWAY);
        System.out.println("Alice status after setting away: " + chatSystem.getUserStatus("alice"));
    }
}
