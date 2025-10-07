package lld;

import java.util.*;
import java.util.concurrent.*;

/**
 * LLD #85: Game Session State Manager
 * 
 * Design Patterns:
 * 1. State Pattern - Session states (Active, Paused, Timeout, Ended)
 * 2. Strategy Pattern - Different timeout strategies
 * 3. Observer Pattern - Session event notifications
 * 4. Singleton Pattern - SessionManager instance
 * 
 * Manages pause/resume, timeouts, and session lifecycle
 */

enum SessionState { CREATED, ACTIVE, PAUSED, TIMEOUT, ENDED, ABANDONED }

class GameSession {
    private String sessionId;
    private String userId;
    private String gameType;
    private SessionState state;
    private long createdAt;
    private long lastActiveAt;
    private long pausedAt;
    private long totalPausedDuration;
    private long totalPlayDuration;
    private Map<String, Object> sessionData;
    
    public GameSession(String sessionId, String userId, String gameType) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.gameType = gameType;
        this.state = SessionState.CREATED;
        this.createdAt = System.currentTimeMillis();
        this.lastActiveAt = createdAt;
        this.sessionData = new ConcurrentHashMap<>();
        this.totalPausedDuration = 0;
        this.totalPlayDuration = 0;
    }
    
    // Getters and setters
    public String getSessionId() { return sessionId; }
    public String getUserId() { return userId; }
    public String getGameType() { return gameType; }
    public SessionState getState() { return state; }
    public void setState(SessionState state) { this.state = state; }
    public long getCreatedAt() { return createdAt; }
    public long getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(long time) { this.lastActiveAt = time; }
    public long getPausedAt() { return pausedAt; }
    public void setPausedAt(long time) { this.pausedAt = time; }
    public long getTotalPausedDuration() { return totalPausedDuration; }
    public void addPausedDuration(long duration) { this.totalPausedDuration += duration; }
    public long getTotalPlayDuration() { return totalPlayDuration; }
    public void setTotalPlayDuration(long duration) { this.totalPlayDuration = duration; }
    public Map<String, Object> getSessionData() { return sessionData; }
    
    public long getActiveDuration() {
        long total = System.currentTimeMillis() - createdAt;
        return total - totalPausedDuration;
    }
}

// Observer Pattern
interface SessionObserver {
    void onSessionStarted(GameSession session);
    void onSessionPaused(GameSession session);
    void onSessionResumed(GameSession session);
    void onSessionTimeout(GameSession session);
    void onSessionEnded(GameSession session);
}

// Strategy Pattern - Timeout strategies
interface TimeoutStrategy {
    long getInactivityTimeout();
    long getPauseTimeout();
    boolean shouldTimeout(GameSession session);
}

class StandardTimeoutStrategy implements TimeoutStrategy {
    private static final long INACTIVITY_TIMEOUT = 5 * 60 * 1000; // 5 minutes
    private static final long PAUSE_TIMEOUT = 30 * 60 * 1000; // 30 minutes
    
    @Override
    public long getInactivityTimeout() {
        return INACTIVITY_TIMEOUT;
    }
    
    @Override
    public long getPauseTimeout() {
        return PAUSE_TIMEOUT;
    }
    
    @Override
    public boolean shouldTimeout(GameSession session) {
        long now = System.currentTimeMillis();
        
        if (session.getState() == SessionState.ACTIVE) {
            return (now - session.getLastActiveAt()) > INACTIVITY_TIMEOUT;
        } else if (session.getState() == SessionState.PAUSED) {
            return (now - session.getPausedAt()) > PAUSE_TIMEOUT;
        }
        
        return false;
    }
}

class MultiplayerTimeoutStrategy implements TimeoutStrategy {
    private static final long INACTIVITY_TIMEOUT = 60 * 1000; // 1 minute (stricter)
    private static final long PAUSE_TIMEOUT = 2 * 60 * 1000; // 2 minutes
    
    @Override
    public long getInactivityTimeout() {
        return INACTIVITY_TIMEOUT;
    }
    
    @Override
    public long getPauseTimeout() {
        return PAUSE_TIMEOUT;
    }
    
    @Override
    public boolean shouldTimeout(GameSession session) {
        long now = System.currentTimeMillis();
        
        if (session.getState() == SessionState.ACTIVE) {
            return (now - session.getLastActiveAt()) > INACTIVITY_TIMEOUT;
        } else if (session.getState() == SessionState.PAUSED) {
            return (now - session.getPausedAt()) > PAUSE_TIMEOUT;
        }
        
        return false;
    }
}

public class GameSessionStateManager {
    private static GameSessionStateManager instance;
    
    private Map<String, GameSession> sessions;
    private Map<String, TimeoutStrategy> strategyMap;
    private List<SessionObserver> observers;
    private ScheduledExecutorService timeoutChecker;
    private ScheduledExecutorService statsCollector;
    
    private static final long TIMEOUT_CHECK_INTERVAL = 30 * 1000; // 30 seconds
    
    private GameSessionStateManager() {
        this.sessions = new ConcurrentHashMap<>();
        this.strategyMap = new HashMap<>();
        this.observers = new CopyOnWriteArrayList<>();
        
        // Setup default strategies
        strategyMap.put("default", new StandardTimeoutStrategy());
        strategyMap.put("multiplayer", new MultiplayerTimeoutStrategy());
        
        // Start timeout checker
        this.timeoutChecker = Executors.newScheduledThreadPool(1);
        startTimeoutChecker();
    }
    
    public static synchronized GameSessionStateManager getInstance() {
        if (instance == null) {
            instance = new GameSessionStateManager();
        }
        return instance;
    }
    
    // MAIN ALGORITHM: Create and start session
    public GameSession createSession(String userId, String gameType) {
        String sessionId = generateSessionId(userId);
        GameSession session = new GameSession(sessionId, userId, gameType);
        sessions.put(sessionId, session);
        return session;
    }
    
    public void startSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session != null && session.getState() == SessionState.CREATED) {
            session.setState(SessionState.ACTIVE);
            session.setLastActiveAt(System.currentTimeMillis());
            notifySessionStarted(session);
        }
    }
    
    // MAIN ALGORITHM: Pause session
    public boolean pauseSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null || session.getState() != SessionState.ACTIVE) {
            return false;
        }
        
        session.setState(SessionState.PAUSED);
        session.setPausedAt(System.currentTimeMillis());
        notifySessionPaused(session);
        return true;
    }
    
    // MAIN ALGORITHM: Resume session
    public boolean resumeSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null || session.getState() != SessionState.PAUSED) {
            return false;
        }
        
        // Calculate paused duration
        long pausedDuration = System.currentTimeMillis() - session.getPausedAt();
        session.addPausedDuration(pausedDuration);
        
        // Check if pause timeout occurred
        TimeoutStrategy strategy = getStrategy(session.getGameType());
        if (pausedDuration > strategy.getPauseTimeout()) {
            timeoutSession(session);
            return false;
        }
        
        session.setState(SessionState.ACTIVE);
        session.setLastActiveAt(System.currentTimeMillis());
        notifySessionResumed(session);
        return true;
    }
    
    // MAIN ALGORITHM: End session
    public void endSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) return;
        
        if (session.getState() == SessionState.ACTIVE || 
            session.getState() == SessionState.PAUSED) {
            
            // Calculate total play duration
            long duration = session.getActiveDuration();
            session.setTotalPlayDuration(duration);
            
            session.setState(SessionState.ENDED);
            notifySessionEnded(session);
        }
        
        // Keep session for a while for stats, then remove
        scheduleSessionCleanup(sessionId, 60000); // 1 minute
    }
    
    // Update activity timestamp
    public void recordActivity(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session != null && session.getState() == SessionState.ACTIVE) {
            session.setLastActiveAt(System.currentTimeMillis());
        }
    }
    
    // Store arbitrary session data
    public void updateSessionData(String sessionId, String key, Object value) {
        GameSession session = sessions.get(sessionId);
        if (session != null) {
            session.getSessionData().put(key, value);
        }
    }
    
    public Object getSessionData(String sessionId, String key) {
        GameSession session = sessions.get(sessionId);
        return session != null ? session.getSessionData().get(key) : null;
    }
    
    // Timeout handling
    private void startTimeoutChecker() {
        timeoutChecker.scheduleAtFixedRate(() -> {
            checkTimeouts();
        }, TIMEOUT_CHECK_INTERVAL, TIMEOUT_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    private void checkTimeouts() {
        long now = System.currentTimeMillis();
        
        for (GameSession session : sessions.values()) {
            if (session.getState() == SessionState.ACTIVE || 
                session.getState() == SessionState.PAUSED) {
                
                TimeoutStrategy strategy = getStrategy(session.getGameType());
                
                if (strategy.shouldTimeout(session)) {
                    timeoutSession(session);
                }
            }
        }
    }
    
    private void timeoutSession(GameSession session) {
        session.setState(SessionState.TIMEOUT);
        notifySessionTimeout(session);
        
        // Auto-end after timeout
        scheduleSessionCleanup(session.getSessionId(), 300000); // 5 minutes
    }
    
    private void scheduleSessionCleanup(String sessionId, long delayMs) {
        timeoutChecker.schedule(() -> {
            sessions.remove(sessionId);
        }, delayMs, TimeUnit.MILLISECONDS);
    }
    
    // Strategy management
    public void registerStrategy(String gameType, TimeoutStrategy strategy) {
        strategyMap.put(gameType, strategy);
    }
    
    private TimeoutStrategy getStrategy(String gameType) {
        return strategyMap.getOrDefault(gameType, strategyMap.get("default"));
    }
    
    // Observer management
    public void addObserver(SessionObserver observer) {
        observers.add(observer);
    }
    
    private void notifySessionStarted(GameSession session) {
        for (SessionObserver obs : observers) {
            obs.onSessionStarted(session);
        }
    }
    
    private void notifySessionPaused(GameSession session) {
        for (SessionObserver obs : observers) {
            obs.onSessionPaused(session);
        }
    }
    
    private void notifySessionResumed(GameSession session) {
        for (SessionObserver obs : observers) {
            obs.onSessionResumed(session);
        }
    }
    
    private void notifySessionTimeout(GameSession session) {
        for (SessionObserver obs : observers) {
            obs.onSessionTimeout(session);
        }
    }
    
    private void notifySessionEnded(GameSession session) {
        for (SessionObserver obs : observers) {
            obs.onSessionEnded(session);
        }
    }
    
    // Utility methods
    private String generateSessionId(String userId) {
        return userId + "_" + System.currentTimeMillis() + "_" + 
               UUID.randomUUID().toString().substring(0, 8);
    }
    
    public GameSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    public List<GameSession> getActiveSessions() {
        return sessions.values().stream()
                .filter(s -> s.getState() == SessionState.ACTIVE)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
                .filter(s -> s.getState() == SessionState.ACTIVE)
                .count();
    }
    
    public void shutdown() {
        timeoutChecker.shutdown();
        if (statsCollector != null) {
            statsCollector.shutdown();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        GameSessionStateManager manager = GameSessionStateManager.getInstance();
        
        // Add observer
        manager.addObserver(new SessionObserver() {
            public void onSessionStarted(GameSession session) {
                System.out.println("✓ Session started: " + session.getSessionId());
            }
            
            public void onSessionPaused(GameSession session) {
                System.out.println("⏸ Session paused: " + session.getSessionId());
            }
            
            public void onSessionResumed(GameSession session) {
                System.out.println("▶ Session resumed: " + session.getSessionId());
            }
            
            public void onSessionTimeout(GameSession session) {
                System.out.println("⏱ Session timeout: " + session.getSessionId());
            }
            
            public void onSessionEnded(GameSession session) {
                System.out.println("■ Session ended: " + session.getSessionId() + 
                                   " (Duration: " + (session.getTotalPlayDuration() / 1000) + "s)");
            }
        });
        
        // Create and start session
        System.out.println("=== Game Session Manager Demo ===\n");
        
        GameSession session = manager.createSession("user123", "chess");
        manager.startSession(session.getSessionId());
        
        // Simulate gameplay
        Thread.sleep(2000);
        manager.recordActivity(session.getSessionId());
        manager.updateSessionData(session.getSessionId(), "score", 100);
        
        // Pause session
        Thread.sleep(1000);
        manager.pauseSession(session.getSessionId());
        
        // Resume after pause
        Thread.sleep(2000);
        manager.resumeSession(session.getSessionId());
        
        // More activity
        Thread.sleep(1000);
        manager.recordActivity(session.getSessionId());
        
        // End session
        Thread.sleep(1000);
        manager.endSession(session.getSessionId());
        
        System.out.println("\nActive sessions: " + manager.getActiveSessionCount());
        
        Thread.sleep(2000);
        manager.shutdown();
    }
}

/*
 * INTERVIEW QUESTIONS & ANSWERS:
 * 
 * Q1: How do you detect idle/inactive sessions?
 * A: Track lastActiveAt timestamp. Use scheduled task to check periodically.
 *    If (currentTime - lastActiveAt) > timeout → mark as idle/timeout.
 *    Update lastActiveAt on any user interaction (moves, clicks, etc).
 * 
 * Q2: How would you handle reconnection after timeout?
 * A: Grace period approach:
 *    - Don't immediately delete timed-out session
 *    - Keep in TIMEOUT state for 5-10 minutes
 *    - Allow reconnection with session restoration
 *    - If grace period expires → permanently end session
 * 
 * Q3: How to implement fair timeout for multiplayer games?
 * A: Stricter timeouts for multiplayer:
 *    - Shorter inactivity window (30-60 seconds)
 *    - Notify other players of pause/timeout
 *    - Allow vote to continue or forfeit
 *    - Penalize frequent abandoners (reputation system)
 * 
 * Q4: How would you persist sessions across server restarts?
 * A: Session persistence:
 *    - Serialize session state to Redis/database
 *    - Store: session ID, user ID, state, timestamps, game data
 *    - On restart: reload active sessions
 *    - Resume from last known state
 *    - Consider sessions > N hours old as expired
 * 
 * Q5: How to handle pause in real-time multiplayer games?
 * A: Complex in multiplayer:
 *    - May not allow pause in competitive games
 *    - If allowed: pause for all players
 *    - Vote to pause system
 *    - Time bank (limited pause time per player)
 *    - Pause only at specific moments (between rounds)
 * 
 * Q6: How would you implement session migration across servers?
 * A: Distributed session management:
 *    - Store session in centralized cache (Redis)
 *    - Stateless game servers
 *    - Session sticky routing or any-server access
 *    - Serialize game state with session
 *    - Use distributed locks for concurrent access
 * 
 * Q7: What metrics should you track for sessions?
 * A: Key metrics:
 *    - Average session duration
 *    - Pause frequency and duration
 *    - Timeout/abandonment rate
 *    - Reconnection success rate
 *    - Active sessions over time (concurrency)
 *    - Session end reasons (normal vs timeout vs crash)
 * 
 * Q8: How to implement progressive timeout warnings?
 * A: Warning system:
 *    - At 80% of timeout: send first warning
 *    - At 90%: send urgent warning
 *    - At 95%: final warning
 *    - Give user chance to extend/resume
 *    - Log warning responses for analysis
 * 
 * Q9: How would you handle time zones for global games?
 * A: Always use UTC:
 *    - Store all timestamps in UTC
 *    - Convert to local time for display only
 *    - Avoid time zone arithmetic on server
 *    - Use ISO 8601 format for timestamps
 *    - Handle daylight saving transitions
 * 
 * Q10: How to optimize for millions of concurrent sessions?
 * A: Scale strategies:
 *    - Partition sessions across multiple managers
 *    - Use Redis for distributed session storage
 *    - Lazy timeout checking (check only when accessed)
 *    - Background job for batch cleanup
 *    - TTL-based automatic expiration
 *    - Separate active/inactive session stores
 */
