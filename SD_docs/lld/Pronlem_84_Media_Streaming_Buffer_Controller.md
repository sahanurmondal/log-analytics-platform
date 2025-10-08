/**
* LLD #84: Media Streaming Buffer Controller
*
* Design Patterns:
* 1. Strategy Pattern - Different buffering strategies
* 2. Observer Pattern - Buffer state notifications
* 3. State Pattern - Buffering states (Buffering, Playing, Paused)
*
* Adaptive buffering based on network conditions and playback
  */

enum BufferState { BUFFERING, READY, PLAYING, PAUSED, STALLED }
enum NetworkQuality { EXCELLENT, GOOD, FAIR, POOR }

class MediaSegment {
private int segmentId;
private int qualityLevel; // 0=lowest, higher=better
private byte[] data;
private long duration; // milliseconds
private long size; // bytes

    public MediaSegment(int segmentId, int qualityLevel, long duration, long size) {
        this.segmentId = segmentId;
        this.qualityLevel = qualityLevel;
        this.duration = duration;
        this.size = size;
        this.data = new byte[(int) size];
    }
    
    public int getSegmentId() { return segmentId; }
    public int getQualityLevel() { return qualityLevel; }
    public long getDuration() { return duration; }
    public long getSize() { return size; }
}

// Observer Pattern
interface BufferObserver {
void onBufferStateChanged(BufferState state);
void onBufferLevelChanged(double percentage);
void onQualityChanged(int newQuality);
void onStallDetected();
}

// Strategy Pattern - Buffering strategies
interface BufferingStrategy {
int calculateTargetBufferSize(NetworkQuality networkQuality, int currentBuffer);
int selectQualityLevel(NetworkQuality networkQuality, int bufferLevel);
boolean shouldStartPlayback(int bufferLevel, int targetBuffer);
}

class AdaptiveBufferingStrategy implements BufferingStrategy {
private static final int MIN_BUFFER_MS = 5000;  // 5 seconds
private static final int MAX_BUFFER_MS = 30000; // 30 seconds

    @Override
    public int calculateTargetBufferSize(NetworkQuality networkQuality, int currentBuffer) {
        // MAIN ALGORITHM: Adaptive target buffer calculation
        switch (networkQuality) {
            case EXCELLENT:
                return 10000; // 10s buffer for excellent network
            case GOOD:
                return 15000; // 15s for good
            case FAIR:
                return 20000; // 20s for fair
            case POOR:
                return MAX_BUFFER_MS; // 30s for poor network
            default:
                return 15000;
        }
    }
    
    @Override
    public int selectQualityLevel(NetworkQuality networkQuality, int bufferLevel) {
        // MAIN ALGORITHM: Adaptive quality selection
        // Higher buffer + better network = higher quality
        
        int baseQuality;
        switch (networkQuality) {
            case EXCELLENT: baseQuality = 4; break;
            case GOOD: baseQuality = 3; break;
            case FAIR: baseQuality = 2; break;
            case POOR: baseQuality = 1; break;
            default: baseQuality = 2;
        }
        
        // Adjust based on buffer health
        if (bufferLevel < MIN_BUFFER_MS) {
            return Math.max(0, baseQuality - 2); // Drop quality significantly
        } else if (bufferLevel < MIN_BUFFER_MS * 2) {
            return Math.max(0, baseQuality - 1); // Drop quality slightly
        }
        
        return baseQuality;
    }
    
    @Override
    public boolean shouldStartPlayback(int bufferLevel, int targetBuffer) {
        // Start when we have at least 30% of target buffer
        return bufferLevel >= Math.min(MIN_BUFFER_MS, targetBuffer * 0.3);
    }
}

class NetworkMonitor {
private Queue<Long> downloadTimes; // Recent download times
private Queue<Long> downloadSizes;
private static final int SAMPLE_SIZE = 10;

    public NetworkMonitor() {
        downloadTimes = new LinkedList<>();
        downloadSizes = new LinkedList<>();
    }
    
    public void recordDownload(long timeMs, long bytes) {
        downloadTimes.add(timeMs);
        downloadSizes.add(bytes);
        
        if (downloadTimes.size() > SAMPLE_SIZE) {
            downloadTimes.poll();
            downloadSizes.poll();
        }
    }
    
    // MAIN ALGORITHM: Estimate network quality
    public NetworkQuality estimateQuality() {
        if (downloadTimes.isEmpty()) {
            return NetworkQuality.GOOD;
        }
        
        // Calculate average bandwidth (bytes per second)
        long totalTime = downloadTimes.stream().mapToLong(Long::longValue).sum();
        long totalBytes = downloadSizes.stream().mapToLong(Long::longValue).sum();
        
        if (totalTime == 0) return NetworkQuality.GOOD;
        
        long bandwidthBps = (totalBytes * 1000) / totalTime; // bytes per second
        long bandwidthKbps = (bandwidthBps * 8) / 1000; // kilobits per second
        
        // Classify network quality
        if (bandwidthKbps > 5000) return NetworkQuality.EXCELLENT; // > 5 Mbps
        if (bandwidthKbps > 2000) return NetworkQuality.GOOD;      // > 2 Mbps
        if (bandwidthKbps > 1000) return NetworkQuality.FAIR;      // > 1 Mbps
        return NetworkQuality.POOR;
    }
}

class CircularMediaBuffer {
private Queue<MediaSegment> buffer;
private int maxSegments;
private long totalBufferedDuration; // milliseconds

    public CircularMediaBuffer(int maxSegments) {
        this.buffer = new LinkedList<>();
        this.maxSegments = maxSegments;
        this.totalBufferedDuration = 0;
    }
    
    public synchronized boolean add(MediaSegment segment) {
        if (buffer.size() >= maxSegments) {
            MediaSegment removed = buffer.poll();
            if (removed != null) {
                totalBufferedDuration -= removed.getDuration();
            }
        }
        
        buffer.add(segment);
        totalBufferedDuration += segment.getDuration();
        return true;
    }
    
    public synchronized MediaSegment consume() {
        MediaSegment segment = buffer.poll();
        if (segment != null) {
            totalBufferedDuration -= segment.getDuration();
        }
        return segment;
    }
    
    public synchronized long getBufferedDuration() {
        return totalBufferedDuration;
    }
    
    public synchronized int getSegmentCount() {
        return buffer.size();
    }
    
    public synchronized void clear() {
        buffer.clear();
        totalBufferedDuration = 0;
    }
}

public class MediaStreamingBufferController {
private CircularMediaBuffer buffer;
private BufferingStrategy strategy;
private NetworkMonitor networkMonitor;
private BufferState state;
private int currentQualityLevel;
private int targetBufferMs;
private List<BufferObserver> observers;
private long lastPlaybackTime;
private Timer monitoringTimer;

    private static final int SEGMENT_DURATION_MS = 2000; // 2-second segments
    private static final int STALL_THRESHOLD_MS = 100; // Consider stalled if buffer < 100ms
    
    public MediaStreamingBufferController(int maxBufferSegments) {
        this.buffer = new CircularMediaBuffer(maxBufferSegments);
        this.strategy = new AdaptiveBufferingStrategy();
        this.networkMonitor = new NetworkMonitor();
        this.state = BufferState.BUFFERING;
        this.currentQualityLevel = 2; // Start at medium quality
        this.observers = new ArrayList<>();
        this.targetBufferMs = 15000; // 15 seconds default
    }
    
    // MAIN ALGORITHM: Add segment to buffer with adaptive control
    public void addSegment(MediaSegment segment, long downloadTimeMs) {
        // Record network performance
        networkMonitor.recordDownload(downloadTimeMs, segment.getSize());
        
        // Add to buffer
        buffer.add(segment);
        
        // Update state based on buffer level
        updateBufferState();
        
        // Notify observers
        notifyBufferLevelChanged();
    }
    
    // MAIN ALGORITHM: Consume segment during playback
    public MediaSegment consumeSegment() {
        if (state != BufferState.PLAYING) {
            return null;
        }
        
        MediaSegment segment = buffer.consume();
        
        if (segment == null || buffer.getBufferedDuration() < STALL_THRESHOLD_MS) {
            // Buffer depleted - stall!
            setState(BufferState.STALLED);
            notifyStallDetected();
            return null;
        }
        
        lastPlaybackTime = System.currentTimeMillis();
        updateBufferState();
        notifyBufferLevelChanged();
        
        return segment;
    }
    
    private void updateBufferState() {
        long bufferedMs = buffer.getBufferedDuration();
        NetworkQuality quality = networkMonitor.estimateQuality();
        
        // Update target buffer based on network
        targetBufferMs = strategy.calculateTargetBufferSize(quality, (int) bufferedMs);
        
        // Update quality level
        int newQuality = strategy.selectQualityLevel(quality, (int) bufferedMs);
        if (newQuality != currentQualityLevel) {
            currentQualityLevel = newQuality;
            notifyQualityChanged(newQuality);
        }
        
        // Update playback state
        if (state == BufferState.BUFFERING || state == BufferState.STALLED) {
            if (strategy.shouldStartPlayback((int) bufferedMs, targetBufferMs)) {
                setState(BufferState.READY);
            }
        } else if (state == BufferState.PLAYING) {
            if (bufferedMs < STALL_THRESHOLD_MS) {
                setState(BufferState.STALLED);
            }
        }
    }
    
    public void startPlayback() {
        if (state == BufferState.READY) {
            setState(BufferState.PLAYING);
        }
    }
    
    public void pause() {
        if (state == BufferState.PLAYING) {
            setState(BufferState.PAUSED);
        }
    }
    
    public void resume() {
        if (state == BufferState.PAUSED) {
            long bufferedMs = buffer.getBufferedDuration();
            if (bufferedMs > STALL_THRESHOLD_MS) {
                setState(BufferState.PLAYING);
            } else {
                setState(BufferState.BUFFERING);
            }
        }
    }
    
    public void seek(int positionMs) {
        // Clear buffer and restart buffering
        buffer.clear();
        setState(BufferState.BUFFERING);
        notifyBufferLevelChanged();
    }
    
    private void setState(BufferState newState) {
        if (this.state != newState) {
            this.state = newState;
            notifyStateChanged(newState);
        }
    }
    
    // Observer methods
    public void addObserver(BufferObserver observer) {
        observers.add(observer);
    }
    
    private void notifyStateChanged(BufferState state) {
        for (BufferObserver obs : observers) {
            obs.onBufferStateChanged(state);
        }
    }
    
    private void notifyBufferLevelChanged() {
        double percentage = (buffer.getBufferedDuration() / (double) targetBufferMs) * 100;
        percentage = Math.min(100, percentage);
        for (BufferObserver obs : observers) {
            obs.onBufferLevelChanged(percentage);
        }
    }
    
    private void notifyQualityChanged(int quality) {
        for (BufferObserver obs : observers) {
            obs.onQualityChanged(quality);
        }
    }
    
    private void notifyStallDetected() {
        for (BufferObserver obs : observers) {
            obs.onStallDetected();
        }
    }
    
    // Getters
    public BufferState getState() { return state; }
    public int getCurrentQualityLevel() { return currentQualityLevel; }
    public long getBufferedDuration() { return buffer.getBufferedDuration(); }
    public int getTargetBufferMs() { return targetBufferMs; }
    
    public static void main(String[] args) throws InterruptedException {
        MediaStreamingBufferController controller = new MediaStreamingBufferController(15);
        
        controller.addObserver(new BufferObserver() {
            public void onBufferStateChanged(BufferState state) {
                System.out.println("State changed: " + state);
            }
            
            public void onBufferLevelChanged(double percentage) {
                System.out.printf("Buffer level: %.1f%%\n", percentage);
            }
            
            public void onQualityChanged(int quality) {
                System.out.println("Quality changed to: " + quality);
            }
            
            public void onStallDetected() {
                System.out.println("⚠️ Playback stalled!");
            }
        });
        
        // Simulate buffering segments
        System.out.println("=== Simulating Media Streaming ===\n");
        
        for (int i = 0; i < 10; i++) {
            MediaSegment segment = new MediaSegment(i, 2, 2000, 100000);
            controller.addSegment(segment, 150 + (long) (Math.random() * 100));
            Thread.sleep(200);
        }
        
        // Start playback
        controller.startPlayback();
        
        // Simulate playback consumption
        for (int i = 0; i < 5; i++) {
            Thread.sleep(2000);
            MediaSegment consumed = controller.consumeSegment();
            if (consumed != null) {
                System.out.println("Playing segment: " + consumed.getSegmentId());
            }
        }
    }
}

/*
* INTERVIEW QUESTIONS & ANSWERS:
*
* Q1: How does adaptive bitrate streaming work?
* A: Monitor network bandwidth continuously. If bandwidth drops, switch to lower quality.
*    If bandwidth is good and buffer is healthy, increase quality.
*    Algorithms: Netflix uses DASH, YouTube uses HLS.
*    Key: Balance quality vs buffering/stalls.
*
* Q2: How do you detect and prevent buffer stalls?
* A: Multiple approaches:
*    - Maintain minimum buffer (5-10 seconds)
*    - Predict future bandwidth using moving average
*    - Preemptively lower quality before buffer depletes
*    - Prefetch segments during idle time
*
* Q3: What's the optimal buffer size?
* A: Trade-offs:
*    - Too small: Frequent stalls, bad UX
*    - Too large: High memory usage, slow seek, wasted data
*    Optimal: 10-30 seconds depending on network variability
*    Adaptive: Increase buffer on poor network
*
* Q4: How would you implement live streaming vs VOD?
* A: Live streaming differences:
*    - Lower latency requirement (2-10 seconds vs 30+ for VOD)
*    - Smaller buffer size
*    - Can't seek ahead
*    - Handle stream interruptions gracefully
*    - Sync issues more critical
*
* Q5: How to optimize for mobile devices?
* A: Mobile-specific considerations:
*    - Lower default quality (save data)
*    - Smaller buffer (save memory)
*    - Handle network switches (WiFi ↔ cellular)
*    - Background/foreground transitions
*    - Battery optimization (lower decode complexity)
*
* Q6: How would you implement prefetching?
* A: Predictive prefetching:
*    - Download next segment before current ends
*    - Prefetch higher quality when idle
*    - Use viewing patterns (users watch 80% on average)
*    - Don't prefetch if battery low or cellular data
*
* Q7: How to handle network switches (WiFi to cellular)?
* A: Network transition handling:
*    - Detect network change immediately
*    - Re-evaluate quality level
*    - May need to lower quality instantly
*    - Clear buffer if switching to cellular (data limits)
*    - Notify user of quality change
*
* Q8: What metrics should you track?
* A: Key metrics:
*    - Rebuffer ratio (% time buffering)
*    - Startup time (time to first frame)
*    - Average bitrate
*    - Bitrate switches frequency
*    - Video abandonment rate
*    - Buffer health over time
*
* Q9: How would you implement Picture-in-Picture mode?
* A: PiP considerations:
*    - Continue playback in smaller window
*    - Maintain buffer controller
*    - May lower quality (smaller display)
*    - Handle state transitions carefully
*    - Synchronize with main player state
*
* Q10: How to optimize seek operations?
* A: Fast seeking:
*    - Use keyframe index (seek to I-frames only)
*    - Clear buffer and request new segment
*    - Prefetch around seek position
*    - Show preview thumbnails (trick play)
*    - Minimize rebuffering after seek
       */
