package miscellaneous.pinterest;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design Pinterest Pin and Board System
 * 
 * Description:
 * Design a visual discovery platform that supports:
 * - Pin creation and management
 * - Board organization
 * - Visual search and recommendations
 * - Following system
 * - Engagement analytics
 * 
 * Company: Pinterest
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignPinSystem {

    class Pin {
        String id;
        String imageUrl;
        String title;
        String description;
        String userId;
        String boardId;
        List<String> tags;
        int repins;
        int likes;
        long timestamp;

        Pin(String imageUrl, String title, String description, String userId, String boardId) {
            this.id = UUID.randomUUID().toString();
            this.imageUrl = imageUrl;
            this.title = title;
            this.description = description;
            this.userId = userId;
            this.boardId = boardId;
            this.tags = new ArrayList<>();
            this.repins = 0;
            this.likes = 0;
            this.timestamp = System.currentTimeMillis();
        }
    }

    class Board {
        String id;
        String name;
        String description;
        String userId;
        List<String> pinIds;
        boolean isPrivate;

        Board(String name, String description, String userId) {
            this.id = UUID.randomUUID().toString();
            this.name = name;
            this.description = description;
            this.userId = userId;
            this.pinIds = new ArrayList<>();
            this.isPrivate = false;
        }
    }

    class VisualSearchEngine {
        public List<Pin> searchSimilarPins(String pinId, int limit) {
            Pin targetPin = pins.get(pinId);
            if (targetPin == null)
                return new ArrayList<>();

            return pins.values().stream()
                    .filter(p -> !p.id.equals(pinId))
                    .filter(p -> hasVisualSimilarity(p, targetPin))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        private boolean hasVisualSimilarity(Pin pin1, Pin pin2) {
            // Simplified visual similarity check
            return pin1.tags.stream().anyMatch(tag -> pin2.tags.contains(tag));
        }
    }

    class RecommendationEngine {
        public List<Pin> getPersonalizedPins(String userId, int limit) {
            // Get user's interests from their boards and pins
            Set<String> userInterests = getUserInterests(userId);

            return pins.values().stream()
                    .filter(p -> !p.userId.equals(userId))
                    .filter(p -> p.tags.stream().anyMatch(userInterests::contains))
                    .sorted((p1, p2) -> Integer.compare(p2.repins + p2.likes, p1.repins + p1.likes))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        private Set<String> getUserInterests(String userId) {
            Set<String> interests = new HashSet<>();

            // Analyze user's boards and pins
            for (Board board : boards.values()) {
                if (board.userId.equals(userId)) {
                    for (String pinId : board.pinIds) {
                        Pin pin = pins.get(pinId);
                        if (pin != null) {
                            interests.addAll(pin.tags);
                        }
                    }
                }
            }

            return interests;
        }
    }

    private Map<String, Pin> pins = new HashMap<>();
    private Map<String, Board> boards = new HashMap<>();
    private VisualSearchEngine visualSearchEngine = new VisualSearchEngine();
    private RecommendationEngine recommendationEngine = new RecommendationEngine();

    public String createPin(String imageUrl, String title, String description, String userId, String boardId) {
        Pin pin = new Pin(imageUrl, title, description, userId, boardId);
        pins.put(pin.id, pin);

        // Add to board
        Board board = boards.get(boardId);
        if (board != null) {
            board.pinIds.add(pin.id);
        }

        return pin.id;
    }

    public String createBoard(String name, String description, String userId) {
        Board board = new Board(name, description, userId);
        boards.put(board.id, board);
        return board.id;
    }

    public void repinToBoard(String pinId, String boardId) {
        Pin originalPin = pins.get(pinId);
        Board targetBoard = boards.get(boardId);

        if (originalPin != null && targetBoard != null) {
            // Create new pin (repin)
            Pin repin = new Pin(originalPin.imageUrl, originalPin.title,
                    originalPin.description, targetBoard.userId, boardId);
            pins.put(repin.id, repin);
            targetBoard.pinIds.add(repin.id);

            // Increment repin count
            originalPin.repins++;
        }
    }

    public List<Pin> searchSimilarPins(String pinId, int limit) {
        return visualSearchEngine.searchSimilarPins(pinId, limit);
    }

    public List<Pin> getPersonalizedFeed(String userId, int limit) {
        return recommendationEngine.getPersonalizedPins(userId, limit);
    }

    public static void main(String[] args) {
        DesignPinSystem pinterest = new DesignPinSystem();

        // Create board
        String boardId = pinterest.createBoard("Travel", "Travel inspiration", "user1");

        // Create pin
        String pinId = pinterest.createPin("https://example.com/image.jpg",
                "Beautiful Beach", "Sunset at the beach", "user1", boardId);

        // Repin to another board
        String boardId2 = pinterest.createBoard("Nature", "Nature photos", "user2");
        pinterest.repinToBoard(pinId, boardId2);

        System.out.println("Pinterest system setup completed");
    }
}
