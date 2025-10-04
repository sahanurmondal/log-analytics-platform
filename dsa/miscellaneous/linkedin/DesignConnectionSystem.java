package miscellaneous.linkedin;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design LinkedIn Connection System
 * 
 * Description:
 * Design a professional networking system that supports:
 * - User profiles and connections
 * - Connection recommendations
 * - Skill endorsements
 * - Job recommendations
 * - Professional network analysis
 * 
 * Company: LinkedIn
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignConnectionSystem {

    enum ConnectionStatus {
        PENDING, CONNECTED, REJECTED
    }

    class User {
        String id;
        String name;
        String title;
        String company;
        String location;
        List<String> skills;
        Set<String> connections;
        Map<String, Integer> endorsements;
        String industry;
        int experience;

        User(String id, String name, String title, String company) {
            this.id = id;
            this.name = name;
            this.title = title;
            this.company = company;
            this.skills = new ArrayList<>();
            this.connections = new HashSet<>();
            this.endorsements = new HashMap<>();
            this.experience = 0;
        }
    }

    class ConnectionRequest {
        String id;
        String fromUserId;
        String toUserId;
        String message;
        ConnectionStatus status;
        long timestamp;

        ConnectionRequest(String fromUserId, String toUserId, String message) {
            this.id = UUID.randomUUID().toString();
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.message = message;
            this.status = ConnectionStatus.PENDING;
            this.timestamp = System.currentTimeMillis();
        }
    }

    class ConnectionRecommendationEngine {
        public List<String> getRecommendations(String userId, int limit) {
            User user = users.get(userId);
            if (user == null)
                return new ArrayList<>();

            Map<String, Double> candidateScores = new HashMap<>();

            // Score based on mutual connections
            for (String connectionId : user.connections) {
                User connection = users.get(connectionId);
                if (connection != null) {
                    for (String mutualConnection : connection.connections) {
                        if (!user.connections.contains(mutualConnection) &&
                                !mutualConnection.equals(userId)) {
                            candidateScores.put(mutualConnection,
                                    candidateScores.getOrDefault(mutualConnection, 0.0) + 0.5);
                        }
                    }
                }
            }

            // Score based on same company
            for (User candidate : users.values()) {
                if (!candidate.id.equals(userId) && !user.connections.contains(candidate.id)) {
                    if (user.company.equals(candidate.company)) {
                        candidateScores.put(candidate.id,
                                candidateScores.getOrDefault(candidate.id, 0.0) + 0.3);
                    }
                }
            }

            // Score based on similar skills
            for (User candidate : users.values()) {
                if (!candidate.id.equals(userId) && !user.connections.contains(candidate.id)) {
                    long commonSkills = user.skills.stream()
                            .filter(candidate.skills::contains)
                            .count();

                    if (commonSkills > 0) {
                        candidateScores.put(candidate.id,
                                candidateScores.getOrDefault(candidate.id, 0.0) + commonSkills * 0.1);
                    }
                }
            }

            return candidateScores.entrySet().stream()
                    .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }
    }

    class NetworkAnalyzer {
        public int getDegreesOfSeparation(String userId1, String userId2) {
            if (userId1.equals(userId2))
                return 0;

            Set<String> visited = new HashSet<>();
            Queue<String> queue = new LinkedList<>();
            Map<String, Integer> distances = new HashMap<>();

            queue.offer(userId1);
            visited.add(userId1);
            distances.put(userId1, 0);

            while (!queue.isEmpty()) {
                String currentUser = queue.poll();
                int currentDistance = distances.get(currentUser);

                if (currentUser.equals(userId2)) {
                    return currentDistance;
                }

                User user = users.get(currentUser);
                if (user != null) {
                    for (String connectionId : user.connections) {
                        if (!visited.contains(connectionId)) {
                            visited.add(connectionId);
                            distances.put(connectionId, currentDistance + 1);
                            queue.offer(connectionId);
                        }
                    }
                }
            }

            return -1; // Not connected
        }

        public List<String> getInfluencers(String industry, int limit) {
            return users.values().stream()
                    .filter(u -> u.industry.equals(industry))
                    .sorted((u1, u2) -> Integer.compare(u2.connections.size(), u1.connections.size()))
                    .limit(limit)
                    .map(u -> u.id)
                    .collect(Collectors.toList());
        }
    }

    class SkillEndorsementSystem {
        public void endorseSkill(String endorserId, String endorseeId, String skill) {
            User endorsee = users.get(endorseeId);
            if (endorsee != null && endorsee.skills.contains(skill)) {
                String endorsementKey = endorseeId + ":" + skill;
                endorsements.computeIfAbsent(endorsementKey, k -> new HashSet<>()).add(endorserId);
            }
        }

        public int getEndorsementCount(String userId, String skill) {
            String endorsementKey = userId + ":" + skill;
            return endorsements.getOrDefault(endorsementKey, new HashSet<>()).size();
        }

        public List<String> getTopSkills(String userId, int limit) {
            User user = users.get(userId);
            if (user == null)
                return new ArrayList<>();

            return user.skills.stream()
                    .sorted((s1, s2) -> Integer.compare(
                            getEndorsementCount(userId, s2),
                            getEndorsementCount(userId, s1)))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
    }

    private Map<String, User> users = new HashMap<>();
    private Map<String, ConnectionRequest> connectionRequests = new HashMap<>();
    private Map<String, Set<String>> endorsements = new HashMap<>();
    private ConnectionRecommendationEngine recommendationEngine = new ConnectionRecommendationEngine();
    private NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer();
    private SkillEndorsementSystem endorsementSystem = new SkillEndorsementSystem();

    public String sendConnectionRequest(String fromUserId, String toUserId, String message) {
        User fromUser = users.get(fromUserId);
        User toUser = users.get(toUserId);

        if (fromUser == null || toUser == null || fromUserId.equals(toUserId)) {
            return null;
        }

        if (fromUser.connections.contains(toUserId)) {
            return null; // Already connected
        }

        ConnectionRequest request = new ConnectionRequest(fromUserId, toUserId, message);
        connectionRequests.put(request.id, request);

        return request.id;
    }

    public boolean acceptConnectionRequest(String requestId) {
        ConnectionRequest request = connectionRequests.get(requestId);
        if (request == null || request.status != ConnectionStatus.PENDING) {
            return false;
        }

        User fromUser = users.get(request.fromUserId);
        User toUser = users.get(request.toUserId);

        if (fromUser != null && toUser != null) {
            fromUser.connections.add(request.toUserId);
            toUser.connections.add(request.fromUserId);
            request.status = ConnectionStatus.CONNECTED;
            return true;
        }

        return false;
    }

    public boolean rejectConnectionRequest(String requestId) {
        ConnectionRequest request = connectionRequests.get(requestId);
        if (request == null || request.status != ConnectionStatus.PENDING) {
            return false;
        }

        request.status = ConnectionStatus.REJECTED;
        return true;
    }

    public List<String> getConnectionRecommendations(String userId, int limit) {
        return recommendationEngine.getRecommendations(userId, limit);
    }

    public int getDegreesOfSeparation(String userId1, String userId2) {
        return networkAnalyzer.getDegreesOfSeparation(userId1, userId2);
    }

    public void endorseSkill(String endorserId, String endorseeId, String skill) {
        endorsementSystem.endorseSkill(endorserId, endorseeId, skill);
    }

    public static void main(String[] args) {
        DesignConnectionSystem linkedin = new DesignConnectionSystem();

        // Create users
        User user1 = linkedin.new User("user1", "Alice Johnson", "Software Engineer", "Google");
        user1.skills.addAll(Arrays.asList("Java", "Python", "Machine Learning"));
        user1.industry = "Technology";
        linkedin.users.put(user1.id, user1);

        User user2 = linkedin.new User("user2", "Bob Smith", "Data Scientist", "Facebook");
        user2.skills.addAll(Arrays.asList("Python", "Machine Learning", "Statistics"));
        user2.industry = "Technology";
        linkedin.users.put(user2.id, user2);

        // Send connection request
        String requestId = linkedin.sendConnectionRequest("user1", "user2", "Let's connect!");
        System.out.println("Connection request sent: " + requestId);

        // Accept connection
        boolean accepted = linkedin.acceptConnectionRequest(requestId);
        System.out.println("Connection accepted: " + accepted);

        // Endorse skill
        linkedin.endorseSkill("user1", "user2", "Machine Learning");

        // Get recommendations
        List<String> recommendations = linkedin.getConnectionRecommendations("user1", 5);
        System.out.println("Recommendations: " + recommendations.size());
    }
}
