package lld;

import java.util.*;
import java.io.*;

/**
 * LLD #83: Game State Persistence Layer (Snapshot & Restore)
 * 
 * Design Patterns:
 * 1. Memento Pattern - State snapshots
 * 2. Strategy Pattern - Different serialization strategies
 * 3. Template Method - Save/load workflow
 * 4. Prototype Pattern - Deep cloning of game state
 * 
 * Supports multiple games (Chess, TicTacToe, Snake, etc.)
 */

// Generic game state interface
interface GameState extends Serializable {
    String getGameType();
    Map<String, Object> getStateData();
    void restoreFromData(Map<String, Object> data);
    GameState clone();
}

// Example game states
class ChessGameState implements GameState {
    private static final long serialVersionUID = 1L;
    private String[][] board;
    private String currentPlayer;
    private List<String> moveHistory;
    private boolean whiteCanCastleKingSide;
    private boolean whiteCanCastleQueenSide;
    
    public ChessGameState(String[][] board, String currentPlayer, List<String> moveHistory) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.moveHistory = new ArrayList<>(moveHistory);
    }
    
    @Override
    public String getGameType() {
        return "Chess";
    }
    
    @Override
    public Map<String, Object> getStateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("board", board);
        data.put("currentPlayer", currentPlayer);
        data.put("moveHistory", moveHistory);
        data.put("whiteCanCastleKingSide", whiteCanCastleKingSide);
        data.put("whiteCanCastleQueenSide", whiteCanCastleQueenSide);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void restoreFromData(Map<String, Object> data) {
        this.board = (String[][]) data.get("board");
        this.currentPlayer = (String) data.get("currentPlayer");
        this.moveHistory = (List<String>) data.get("moveHistory");
        this.whiteCanCastleKingSide = (Boolean) data.getOrDefault("whiteCanCastleKingSide", false);
        this.whiteCanCastleQueenSide = (Boolean) data.getOrDefault("whiteCanCastleQueenSide", false);
    }
    
    @Override
    public GameState clone() {
        String[][] boardCopy = new String[board.length][];
        for (int i = 0; i < board.length; i++) {
            boardCopy[i] = board[i].clone();
        }
        return new ChessGameState(boardCopy, currentPlayer, new ArrayList<>(moveHistory));
    }
}

class SnakeGameState implements GameState {
    private static final long serialVersionUID = 1L;
    private List<int[]> snakeBody;
    private int[] foodPosition;
    private String direction;
    private int score;
    private int level;
    
    public SnakeGameState(List<int[]> snakeBody, int[] foodPosition, String direction, int score) {
        this.snakeBody = snakeBody;
        this.foodPosition = foodPosition;
        this.direction = direction;
        this.score = score;
    }
    
    @Override
    public String getGameType() {
        return "Snake";
    }
    
    @Override
    public Map<String, Object> getStateData() {
        Map<String, Object> data = new HashMap<>();
        data.put("snakeBody", snakeBody);
        data.put("foodPosition", foodPosition);
        data.put("direction", direction);
        data.put("score", score);
        data.put("level", level);
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void restoreFromData(Map<String, Object> data) {
        this.snakeBody = (List<int[]>) data.get("snakeBody");
        this.foodPosition = (int[]) data.get("foodPosition");
        this.direction = (String) data.get("direction");
        this.score = (Integer) data.get("score");
        this.level = (Integer) data.getOrDefault("level", 1);
    }
    
    @Override
    public GameState clone() {
        List<int[]> bodyClone = new ArrayList<>();
        for (int[] segment : snakeBody) {
            bodyClone.add(segment.clone());
        }
        return new SnakeGameState(bodyClone, foodPosition.clone(), direction, score);
    }
}

// Strategy Pattern - Serialization strategies
interface SerializationStrategy {
    byte[] serialize(GameState state) throws IOException;
    GameState deserialize(byte[] data) throws IOException, ClassNotFoundException;
}

class JavaSerializationStrategy implements SerializationStrategy {
    @Override
    public byte[] serialize(GameState state) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(state);
        oos.close();
        return bos.toByteArray();
    }
    
    @Override
    public GameState deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (GameState) ois.readObject();
    }
}

class JSONSerializationStrategy implements SerializationStrategy {
    @Override
    public byte[] serialize(GameState state) throws IOException {
        // Simplified JSON serialization
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("gameType", state.getGameType());
        wrapper.put("stateData", state.getStateData());
        wrapper.put("timestamp", System.currentTimeMillis());
        
        String json = convertToJSON(wrapper);
        return json.getBytes();
    }
    
    @Override
    public GameState deserialize(byte[] data) throws IOException {
        String json = new String(data);
        // Simplified JSON deserialization (in practice, use Jackson/Gson)
        throw new UnsupportedOperationException("JSON deserialization not fully implemented");
    }
    
    private String convertToJSON(Map<String, Object> data) {
        // Simplified - use proper JSON library in production
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}

// Snapshot metadata
class GameSnapshot {
    private String snapshotId;
    private String gameType;
    private long timestamp;
    private byte[] serializedData;
    private String description;
    private Map<String, String> metadata;
    
    public GameSnapshot(String snapshotId, String gameType, byte[] data, String description) {
        this.snapshotId = snapshotId;
        this.gameType = gameType;
        this.serializedData = data;
        this.description = description;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    public String getSnapshotId() { return snapshotId; }
    public String getGameType() { return gameType; }
    public long getTimestamp() { return timestamp; }
    public byte[] getSerializedData() { return serializedData; }
    public String getDescription() { return description; }
    public Map<String, String> getMetadata() { return metadata; }
}

// Storage interface
interface SnapshotStorage {
    void saveSnapshot(GameSnapshot snapshot) throws IOException;
    GameSnapshot loadSnapshot(String snapshotId) throws IOException;
    List<GameSnapshot> listSnapshots(String gameType);
    void deleteSnapshot(String snapshotId) throws IOException;
}

class InMemoryStorage implements SnapshotStorage {
    private Map<String, GameSnapshot> snapshots = new HashMap<>();
    
    @Override
    public void saveSnapshot(GameSnapshot snapshot) {
        snapshots.put(snapshot.getSnapshotId(), snapshot);
    }
    
    @Override
    public GameSnapshot loadSnapshot(String snapshotId) {
        return snapshots.get(snapshotId);
    }
    
    @Override
    public List<GameSnapshot> listSnapshots(String gameType) {
        List<GameSnapshot> result = new ArrayList<>();
        for (GameSnapshot snapshot : snapshots.values()) {
            if (snapshot.getGameType().equals(gameType)) {
                result.add(snapshot);
            }
        }
        result.sort(Comparator.comparingLong(GameSnapshot::getTimestamp).reversed());
        return result;
    }
    
    @Override
    public void deleteSnapshot(String snapshotId) {
        snapshots.remove(snapshotId);
    }
}

public class GameStatePersistence {
    private SerializationStrategy serializationStrategy;
    private SnapshotStorage storage;
    private Map<String, GameState> activeGames;
    
    public GameStatePersistence(SerializationStrategy strategy, SnapshotStorage storage) {
        this.serializationStrategy = strategy;
        this.storage = storage;
        this.activeGames = new HashMap<>();
    }
    
    // MAIN ALGORITHM: Create snapshot of current game state
    public String saveGameState(String gameId, GameState state, String description) throws IOException {
        String snapshotId = generateSnapshotId(gameId);
        
        // Serialize game state
        byte[] serializedData = serializationStrategy.serialize(state);
        
        // Create snapshot
        GameSnapshot snapshot = new GameSnapshot(snapshotId, state.getGameType(), serializedData, description);
        snapshot.getMetadata().put("gameId", gameId);
        snapshot.getMetadata().put("size", String.valueOf(serializedData.length));
        
        // Store snapshot
        storage.saveSnapshot(snapshot);
        
        return snapshotId;
    }
    
    // MAIN ALGORITHM: Restore game state from snapshot
    public GameState loadGameState(String snapshotId) throws IOException, ClassNotFoundException {
        GameSnapshot snapshot = storage.loadSnapshot(snapshotId);
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot not found: " + snapshotId);
        }
        
        // Deserialize game state
        GameState state = serializationStrategy.deserialize(snapshot.getSerializedData());
        
        return state;
    }
    
    // Quick save/load for active games
    public String quickSave(String gameId, GameState state) throws IOException {
        activeGames.put(gameId, state.clone());
        return saveGameState(gameId, state, "Quick save");
    }
    
    public GameState quickLoad(String gameId) throws IOException, ClassNotFoundException {
        GameState cached = activeGames.get(gameId);
        if (cached != null) {
            return cached.clone();
        }
        
        // Find latest snapshot for this game
        List<GameSnapshot> snapshots = storage.listSnapshots("*");
        for (GameSnapshot snapshot : snapshots) {
            if (gameId.equals(snapshot.getMetadata().get("gameId"))) {
                return loadGameState(snapshot.getSnapshotId());
            }
        }
        
        return null;
    }
    
    // Auto-save functionality
    public void enableAutoSave(String gameId, GameState state, long intervalMs) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    saveGameState(gameId, state, "Auto-save");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, intervalMs, intervalMs);
    }
    
    public List<GameSnapshot> listSavedGames(String gameType) {
        return storage.listSnapshots(gameType);
    }
    
    public void deleteSnapshot(String snapshotId) throws IOException {
        storage.deleteSnapshot(snapshotId);
    }
    
    private String generateSnapshotId(String gameId) {
        return gameId + "_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    public static void main(String[] args) throws Exception {
        // Create persistence layer
        SerializationStrategy strategy = new JavaSerializationStrategy();
        SnapshotStorage storage = new InMemoryStorage();
        GameStatePersistence persistence = new GameStatePersistence(strategy, storage);
        
        // Create a chess game state
        String[][] board = new String[8][8];
        board[0][0] = "R"; board[0][4] = "K";
        List<String> moves = Arrays.asList("e4", "e5", "Nf3");
        ChessGameState chessState = new ChessGameState(board, "White", moves);
        
        // Save game
        System.out.println("Saving chess game...");
        String snapshotId = persistence.saveGameState("chess-game-1", chessState, "Mid-game position");
        System.out.println("Saved with ID: " + snapshotId);
        
        // Load game
        System.out.println("\nLoading chess game...");
        GameState loaded = persistence.loadGameState(snapshotId);
        System.out.println("Loaded game type: " + loaded.getGameType());
        System.out.println("State data: " + loaded.getStateData());
        
        // List saved games
        System.out.println("\n=== Saved Games ===");
        List<GameSnapshot> snapshots = persistence.listSavedGames("Chess");
        for (GameSnapshot snap : snapshots) {
            System.out.println("ID: " + snap.getSnapshotId());
            System.out.println("Description: " + snap.getDescription());
            System.out.println("Timestamp: " + new Date(snap.getTimestamp()));
            System.out.println("---");
        }
    }
}

/*
 * INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: How do you handle versioning of game states?
 * A: Include version number in serialized data. Use adapter pattern:
 *    - Store version with each snapshot
 *    - Create converters for old versions → new versions
 *    - Maintain backward compatibility for several versions
 *    - Deprecate very old versions with migration tools
 * 
 * Q2: How would you implement cloud save/sync?
 * A: Use cloud storage (S3, Firebase):
 *    - Upload snapshots to cloud
 *    - Sync across devices using unique game/user ID
 *    - Handle conflicts (last-write-wins or merge)
 *    - Use delta sync for efficiency (only changed data)
 * 
 * Q3: What's the trade-off between full snapshots vs delta snapshots?
 * A: Full: Simple, fast restore, more storage
 *    Delta: Less storage, slower restore (need to replay), complex
 *    Hybrid: Periodic full snapshots + intermediate deltas
 * 
 * Q4: How to handle corrupt save files?
 * A: Multi-layered approach:
 *    - Checksum validation (MD5/SHA)
 *    - Keep multiple save slots
 *    - Automatic backup before overwrite
 *    - Graceful degradation (partial restore)
 *    - User notification with recovery options
 * 
 * Q5: How would you compress game states?
 * A: Multiple approaches:
 *    - GZIP compression on serialized data
 *    - Custom encoding for repetitive data
 *    - Delta encoding from base state
 *    - Use binary formats instead of JSON
 *    Typical: 50-90% size reduction
 * 
 * Q6: How to implement save slots (multiple saves per game)?
 * A: Organize storage by game ID + slot number:
 *    - Directory structure: /saves/gameId/slot1/, slot2/, etc.
 *    - Metadata file per slot (timestamp, description, preview)
 *    - UI shows all slots with thumbnails
 * 
 * Q7: How would you generate save file previews/thumbnails?
 * A: Store additional metadata:
 *    - Screenshot (small compressed image)
 *    - Key stats (level, score, time played)
 *    - Mini-map or board state visualization
 *    - Generated at save time, displayed in load menu
 * 
 * Q8: How to handle save migration during game updates?
 * A: Migration pipeline:
 *    - Detect save file version
 *    - Apply transformation chain (v1→v2→v3)
 *    - Validate migrated data
 *    - Keep backup of original
 *    - Log migration for debugging
 * 
 * Q9: How would you implement auto-save without impacting gameplay?
 * A: Asynchronous saving:
 *    - Clone game state (Prototype pattern)
 *    - Serialize in background thread
 *    - Write to disk asynchronously
 *    - Don't block game loop
 *    - Use double buffering for state
 * 
 * Q10: How to secure save files from tampering?
 * A: Security measures:
 *    - Encrypt save files (AES)
 *    - Digital signatures (HMAC)
 *    - Obfuscation of data
 *    - Server-side validation for multiplayer
 *    - Store critical data server-side
 *    Trade-off: Performance vs security
 */
