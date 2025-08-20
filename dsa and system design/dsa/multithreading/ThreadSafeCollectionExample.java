package multithreading;

import java.util.*;
import java.util.concurrent.*;

/**
 * Q19: Thread-safe collections and concurrent data structures
 * Demonstrates different thread-safe collection types and when to use them
 */
public class ThreadSafeCollectionExample {

    public static void main(String[] args) throws InterruptedException {
        int numThreads = 5;
        int itemsPerThread = 1000;

        // Compare different collection types
        compareCollections(numThreads, itemsPerThread);

        // ConcurrentHashMap specific features
        demonstrateConcurrentHashMap();

        // CopyOnWriteArrayList example
        demonstrateCopyOnWriteArrayList();

        // BlockingQueue implementations
        demonstrateBlockingQueues();
    }

    private static void compareCollections(int numThreads, int itemsPerThread) throws InterruptedException {
        System.out.println("=== Collection Performance Comparison ===");

        // Regular HashMap (not thread-safe)
        Map<Integer, String> hashMap = new HashMap<>();

        // Synchronized Map
        Map<Integer, String> synchronizedMap = Collections.synchronizedMap(new HashMap<>());

        // ConcurrentHashMap
        Map<Integer, String> concurrentMap = new ConcurrentHashMap<>();

        // Test with different map types
        System.out.println("Testing HashMap (not thread-safe):");
        try {
            testMap(hashMap, numThreads, itemsPerThread);
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }

        System.out.println("\nTesting synchronized HashMap:");
        testMap(synchronizedMap, numThreads, itemsPerThread);

        System.out.println("\nTesting ConcurrentHashMap:");
        testMap(concurrentMap, numThreads, itemsPerThread);
    }

    private static void testMap(Map<Integer, String> map, int numThreads, int itemsPerThread)
            throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        int key = threadNum * itemsPerThread + j;
                        map.put(key, "Value-" + key);
                    }

                    for (int j = 0; j < itemsPerThread; j++) {
                        int key = threadNum * itemsPerThread + j;
                        map.get(key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();

        System.out.println("Time taken: " + (endTime - startTime) + " ms");
        System.out.println("Map size: " + map.size());

        executor.shutdown();
    }

    private static void demonstrateConcurrentHashMap() {
        System.out.println("\n=== ConcurrentHashMap Special Features ===");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        map.put("apple", 10);
        map.put("banana", 20);
        map.put("orange", 30);
        map.put("grape", 40);
        map.put("kiwi", 50);

        // Atomic operations
        map.computeIfAbsent("melon", k -> 60);
        map.computeIfPresent("apple", (k, v) -> v + 5);

        System.out.println("After compute operations: " + map);

        // Atomic update
        Integer oldValue = map.putIfAbsent("banana", 25);
        System.out.println("Old value for banana: " + oldValue);

        // Concurrent iteration (fail-safe)
        map.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
            // Safe to modify during iteration
            if (k.equals("kiwi")) {
                map.put("papaya", 70);
            }
        });

        System.out.println("After iteration with modification: " + map);

        // Bulk operations
        map.replaceAll((k, v) -> v * 2);
        System.out.println("After replaceAll: " + map);

        // Reduction operations
        int sum = map.reduce(2,
                (k, v) -> v,
                (v1, v2) -> v1 + v2);
        System.out.println("Sum of all values: " + sum);
    }

    private static void demonstrateCopyOnWriteArrayList() throws InterruptedException {
        System.out.println("\n=== CopyOnWriteArrayList Example ===");

        // Regular ArrayList - not thread-safe
        List<String> regularList = new ArrayList<>();

        // Synchronized List
        List<String> synchronizedList = Collections.synchronizedList(new ArrayList<>());

        // CopyOnWriteArrayList - thread-safe for iterations
        List<String> cowList = new CopyOnWriteArrayList<>();

        // Populate the lists
        for (int i = 0; i < 100; i++) {
            String item = "Item-" + i;
            regularList.add(item);
            synchronizedList.add(item);
            cowList.add(item);
        }

        // Test concurrent modification
        System.out.println("Testing concurrent modification:");

        // With CopyOnWriteArrayList
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        executor.submit(() -> {
            // This thread iterates
            try {
                latch.await();
                for (String item : cowList) {
                    System.out.print(".");
                    Thread.sleep(1); // Slow iteration
                }
                System.out.println("\nCopyOnWriteArrayList iteration completed successfully");
            } catch (Exception e) {
                System.out.println("Error in CopyOnWriteArrayList: " + e);
            }
        });

        executor.submit(() -> {
            try {
                latch.countDown();
                Thread.sleep(10); // Let iteration start

                // This thread modifies
                for (int i = 0; i < 50; i++) {
                    cowList.add("New-" + i);
                    Thread.sleep(2);
                }
                System.out.println("CopyOnWriteArrayList modifications completed");
            } catch (Exception e) {
                System.out.println("Error modifying CopyOnWriteArrayList: " + e);
            }
        });

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("Final CopyOnWriteArrayList size: " + cowList.size());

        // Try the same with ArrayList (would throw ConcurrentModificationException)
        System.out.println("\nWith regular ArrayList, this would throw ConcurrentModificationException");
    }

    private static void demonstrateBlockingQueues() throws InterruptedException {
        System.out.println("\n=== BlockingQueue Implementations ===");

        // ArrayBlockingQueue - bounded queue with fixed capacity
        BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(3);

        // LinkedBlockingQueue - optionally bounded queue
        BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(10);

        // PriorityBlockingQueue - unbounded priority queue
        BlockingQueue<String> priorityQueue = new PriorityBlockingQueue<>();

        // DelayQueue - delay before elements can be taken
        DelayQueue<DelayedElement> delayQueue = new DelayQueue<>();

        // SynchronousQueue - each put must wait for a take
        BlockingQueue<String> syncQueue = new SynchronousQueue<>();

        System.out.println("ArrayBlockingQueue example:");

        // Add elements
        arrayQueue.add("Item 1");
        arrayQueue.add("Item 2");
        arrayQueue.offer("Item 3");

        // This would throw an IllegalStateException since queue is full
        // arrayQueue.add("Item 4");

        // This will just return false since queue is full
        boolean offerResult = arrayQueue.offer("Item 4");
        System.out.println("Offer result when queue is full: " + offerResult);

        // Blocking operation - waits if necessary
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(() -> {
            try {
                System.out.println("Trying to put element in full queue...");
                arrayQueue.put("Item 4"); // Will block until space is available
                System.out.println("Put successful after space became available");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Give some time for the thread to block
        Thread.sleep(500);

        // Remove an item to unblock the put
        System.out.println("Taking item from queue: " + arrayQueue.take());

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Final queue size: " + arrayQueue.size());
    }

    static class DelayedElement implements Delayed {
        private final long delayTime;
        private final String data;
        private final long expireTime;

        public DelayedElement(String data, long delayInMillis) {
            this.data = data;
            this.delayTime = delayInMillis;
            this.expireTime = System.currentTimeMillis() + delayInMillis;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = expireTime - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if (this.expireTime < ((DelayedElement) o).expireTime)
                return -1;
            if (this.expireTime > ((DelayedElement) o).expireTime)
                return 1;
            return 0;
        }

        @Override
        public String toString() {
            return "{" + data + ", delay=" + delayTime + "ms}";
        }
    }
}
