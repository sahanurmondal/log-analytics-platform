package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Message Queue System
 *
 * Description: Design a message queue that supports:
 * - Multiple topics and partitions
 * - Producer and consumer groups
 * - Message persistence and ordering
 * - Dead letter queues
 * - Message acknowledgment
 * 
 * Constraints:
 * - Handle high throughput
 * - Ensure message durability
 * - Support multiple consumers
 *
 * Follow-up:
 * - How to handle consumer failures?
 * - Message deduplication?
 * 
 * Time Complexity: O(1) for produce/consume, O(log n) for seeking
 * Space Complexity: O(messages)
 * 
 * Company Tags: Google, Amazon, Facebook, Apache Kafka
 */
public class DesignMessageQueue {

    enum MessageStatus {
        PENDING, ACKNOWLEDGED, FAILED, DEAD_LETTER
    }

    class Message {
        String messageId;
        String topic;
        int partition;
        String key;
        Object payload;
        Map<String, String> headers;
        long timestamp;
        long offset;
        MessageStatus status;
        int retryCount;
        int maxRetries;

        Message(String topic, int partition, String key, Object payload) {
            this.messageId = UUID.randomUUID().toString();
            this.topic = topic;
            this.partition = partition;
            this.key = key;
            this.payload = payload;
            this.headers = new HashMap<>();
            this.timestamp = System.currentTimeMillis();
            this.status = MessageStatus.PENDING;
            this.retryCount = 0;
            this.maxRetries = 3;
        }
    }

    class TopicPartition {
        String topic;
        int partition;
        List<Message> messages;
        AtomicLong nextOffset;
        Map<String, Long> consumerOffsets; // consumerId -> offset

        TopicPartition(String topic, int partition) {
            this.topic = topic;
            this.partition = partition;
            this.messages = new ArrayList<>();
            this.nextOffset = new AtomicLong(0);
            this.consumerOffsets = new ConcurrentHashMap<>();
        }

        synchronized long addMessage(Message message) {
            message.offset = nextOffset.getAndIncrement();
            messages.add(message);
            return message.offset;
        }

        synchronized List<Message> getMessages(String consumerId, int maxMessages) {
            long offset = consumerOffsets.getOrDefault(consumerId, 0L);
            List<Message> result = new ArrayList<>();

            for (int i = (int) offset; i < messages.size() && result.size() < maxMessages; i++) {
                Message message = messages.get(i);
                if (message.status == MessageStatus.PENDING) {
                    result.add(message);
                }
            }

            return result;
        }

        synchronized void commitOffset(String consumerId, long offset) {
            consumerOffsets.put(consumerId, offset + 1);
        }
    }

    interface MessageConsumer {
        void onMessage(Message message);

        void onError(Message message, Exception error);
    }

    class ConsumerGroup {
        String groupId;
        Set<String> consumers;
        Map<String, MessageConsumer> consumerCallbacks;
        Map<TopicPartition, String> partitionAssignments; // partition -> consumerId

        ConsumerGroup(String groupId) {
            this.groupId = groupId;
            this.consumers = new HashSet<>();
            this.consumerCallbacks = new ConcurrentHashMap<>();
            this.partitionAssignments = new ConcurrentHashMap<>();
        }

        void addConsumer(String consumerId, MessageConsumer callback) {
            consumers.add(consumerId);
            consumerCallbacks.put(consumerId, callback);
            rebalancePartitions();
        }

        void removeConsumer(String consumerId) {
            consumers.remove(consumerId);
            consumerCallbacks.remove(consumerId);
            rebalancePartitions();
        }

        private void rebalancePartitions() {
            // Simple round-robin assignment
            List<String> consumerList = new ArrayList<>(consumers);
            if (consumerList.isEmpty())
                return;

            int consumerIndex = 0;
            for (TopicPartition partition : partitions.values()) {
                String assignedConsumer = consumerList.get(consumerIndex % consumerList.size());
                partitionAssignments.put(partition, assignedConsumer);
                consumerIndex++;
            }
        }
    }

    private Map<String, TopicPartition> partitions; // topic:partition -> TopicPartition
    private Map<String, ConsumerGroup> consumerGroups;
    private Map<String, List<Message>> deadLetterQueues; // topic -> messages
    private ScheduledExecutorService scheduler;
    private ExecutorService messageProcessor;

    public DesignMessageQueue() {
        partitions = new ConcurrentHashMap<>();
        consumerGroups = new ConcurrentHashMap<>();
        deadLetterQueues = new ConcurrentHashMap<>();
        scheduler = Executors.newScheduledThreadPool(2);
        messageProcessor = Executors.newFixedThreadPool(10);

        // Start background tasks
        startMessageProcessor();
        startDeadLetterProcessor();
    }

    public void createTopic(String topic, int numPartitions) {
        for (int i = 0; i < numPartitions; i++) {
            String partitionKey = topic + ":" + i;
            partitions.put(partitionKey, new TopicPartition(topic, i));
        }
        deadLetterQueues.put(topic, new ArrayList<>());
    }

    public long produce(String topic, String key, Object payload, Map<String, String> headers) {
        int partition = getPartitionForKey(topic, key);
        String partitionKey = topic + ":" + partition;

        TopicPartition topicPartition = partitions.get(partitionKey);
        if (topicPartition == null) {
            throw new IllegalArgumentException("Topic partition not found: " + partitionKey);
        }

        Message message = new Message(topic, partition, key, payload);
        if (headers != null) {
            message.headers.putAll(headers);
        }

        return topicPartition.addMessage(message);
    }

    private int getPartitionForKey(String topic, String key) {
        if (key == null) {
            return 0; // Default partition
        }

        // Count partitions for topic
        int partitionCount = (int) partitions.keySet().stream()
                .filter(k -> k.startsWith(topic + ":"))
                .count();

        return Math.abs(key.hashCode()) % partitionCount;
    }

    public void subscribe(String groupId, String consumerId, List<String> topics, MessageConsumer callback) {
        ConsumerGroup group = consumerGroups.computeIfAbsent(groupId, ConsumerGroup::new);
        group.addConsumer(consumerId, callback);
    }

    public void unsubscribe(String groupId, String consumerId) {
        ConsumerGroup group = consumerGroups.get(groupId);
        if (group != null) {
            group.removeConsumer(consumerId);
        }
    }

    public void acknowledgeMessage(String groupId, String consumerId, Message message) {
        message.status = MessageStatus.ACKNOWLEDGED;

        String partitionKey = message.topic + ":" + message.partition;
        TopicPartition partition = partitions.get(partitionKey);
        if (partition != null) {
            partition.commitOffset(groupId + ":" + consumerId, message.offset);
        }
    }

    public void rejectMessage(String groupId, String consumerId, Message message) {
        message.status = MessageStatus.FAILED;
        message.retryCount++;

        if (message.retryCount >= message.maxRetries) {
            moveToDeadLetterQueue(message);
        } else {
            // Retry after delay
            scheduler.schedule(() -> {
                message.status = MessageStatus.PENDING;
            }, 1000L * message.retryCount, TimeUnit.MILLISECONDS);
        }
    }

    private void moveToDeadLetterQueue(Message message) {
        message.status = MessageStatus.DEAD_LETTER;
        List<Message> dlq = deadLetterQueues.get(message.topic);
        if (dlq != null) {
            synchronized (dlq) {
                dlq.add(message);
            }
        }
    }

    private void startMessageProcessor() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (ConsumerGroup group : consumerGroups.values()) {
                for (Map.Entry<TopicPartition, String> assignment : group.partitionAssignments.entrySet()) {
                    TopicPartition partition = assignment.getKey();
                    String consumerId = assignment.getValue();
                    MessageConsumer callback = group.consumerCallbacks.get(consumerId);

                    if (callback != null) {
                        List<Message> messages = partition.getMessages(group.groupId + ":" + consumerId, 10);

                        for (Message message : messages) {
                            messageProcessor.submit(() -> {
                                try {
                                    callback.onMessage(message);
                                } catch (Exception e) {
                                    try {
                                        callback.onError(message, e);
                                    } catch (Exception ex) {
                                        rejectMessage(group.groupId, consumerId, message);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    private void startDeadLetterProcessor() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (Map.Entry<String, List<Message>> entry : deadLetterQueues.entrySet()) {
                String topic = entry.getKey();
                List<Message> dlq = entry.getValue();

                synchronized (dlq) {
                    if (!dlq.isEmpty()) {
                        System.out.println("Dead letter queue for topic " + topic + " has " + dlq.size() + " messages");
                    }
                }
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    public Map<String, Object> getTopicStats(String topic) {
        Map<String, Object> stats = new HashMap<>();

        List<TopicPartition> topicPartitions = partitions.values().stream()
                .filter(p -> p.topic.equals(topic))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        int totalMessages = topicPartitions.stream()
                .mapToInt(p -> p.messages.size())
                .sum();

        long totalOffset = topicPartitions.stream()
                .mapToLong(p -> p.nextOffset.get())
                .sum();

        stats.put("topic", topic);
        stats.put("partitions", topicPartitions.size());
        stats.put("totalMessages", totalMessages);
        stats.put("totalOffset", totalOffset);

        List<Message> dlq = deadLetterQueues.get(topic);
        stats.put("deadLetterMessages", dlq != null ? dlq.size() : 0);

        return stats;
    }

    public void shutdown() {
        scheduler.shutdown();
        messageProcessor.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignMessageQueue mq = new DesignMessageQueue();

        // Create topic
        mq.createTopic("user-events", 3);

        // Create consumer
        MessageConsumer consumer = new MessageConsumer() {
            @Override
            public void onMessage(Message message) {
                System.out.println("Consumed: " + message.messageId + " - " + message.payload);
                // Simulate processing
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onError(Message message, Exception error) {
                System.err.println("Error processing message " + message.messageId + ": " + error.getMessage());
            }
        };

        // Subscribe consumer
        mq.subscribe("user-group", "consumer1", Arrays.asList("user-events"), consumer);

        // Produce messages
        for (int i = 0; i < 10; i++) {
            Map<String, String> headers = new HashMap<>();
            headers.put("source", "web-app");

            long offset = mq.produce("user-events", "user" + (i % 3),
                    "User " + i + " logged in", headers);
            System.out.println("Produced message at offset: " + offset);
        }

        // Wait for processing
        Thread.sleep(2000);

        // Show topic stats
        System.out.println("\nTopic stats: " + mq.getTopicStats("user-events"));

        // Shutdown
        mq.shutdown();
    }
}
