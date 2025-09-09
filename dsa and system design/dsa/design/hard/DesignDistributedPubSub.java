package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * Variation: Design Distributed Publish-Subscribe System
 *
 * Description:
 * Design a distributed publish-subscribe system supporting subscribe,
 * unsubscribe, and publish.
 *
 * Constraints:
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for message ordering?
 * - Can you support wildcard subscriptions?
 * 
 * Time Complexity: O(1) for subscribe/unsubscribe, O(n) for publish where n is
 * subscribers
 * Space Complexity: O(topics * subscribers)
 * 
 * Company Tags: Apache Kafka, RabbitMQ, Amazon SNS, Google Pub/Sub
 */
public class DesignDistributedPubSub {

    class Message {
        String topic;
        String content;
        String publisherId;
        long timestamp;
        long sequenceNumber;

        Message(String topic, String content, String publisherId, long sequenceNumber) {
            this.topic = topic;
            this.content = content;
            this.publisherId = publisherId;
            this.timestamp = System.currentTimeMillis();
            this.sequenceNumber = sequenceNumber;
        }

        @Override
        public String toString() {
            return String.format("[%s] %s: %s (seq: %d)", topic, publisherId, content, sequenceNumber);
        }
    }

    class Subscriber {
        String clientId;
        Set<String> subscribedTopics;
        Set<Pattern> wildcardPatterns;
        Queue<Message> messageQueue;
        boolean isActive;
        long lastActivity;

        Subscriber(String clientId) {
            this.clientId = clientId;
            this.subscribedTopics = ConcurrentHashMap.newKeySet();
            this.wildcardPatterns = ConcurrentHashMap.newKeySet();
            this.messageQueue = new ConcurrentLinkedQueue<>();
            this.isActive = true;
            this.lastActivity = System.currentTimeMillis();
        }

        void addMessage(Message message) {
            if (isActive) {
                messageQueue.offer(message);
                lastActivity = System.currentTimeMillis();

                // Limit queue size to prevent memory issues
                if (messageQueue.size() > 1000) {
                    messageQueue.poll(); // Remove oldest message
                }
            }
        }

        boolean isInterestedInTopic(String topic) {
            if (subscribedTopics.contains(topic)) {
                return true;
            }

            // Check wildcard patterns
            for (Pattern pattern : wildcardPatterns) {
                if (pattern.matcher(topic).matches()) {
                    return true;
                }
            }

            return false;
        }
    }

    class Topic {
        String name;
        Set<String> subscribers;
        List<Message> messageHistory;
        long messageSequence;

        Topic(String name) {
            this.name = name;
            this.subscribers = ConcurrentHashMap.newKeySet();
            this.messageHistory = new ArrayList<>();
            this.messageSequence = 0;
        }

        synchronized long getNextSequence() {
            return ++messageSequence;
        }

        void addMessage(Message message) {
            messageHistory.add(message);

            // Keep only recent messages to prevent memory issues
            if (messageHistory.size() > 100) {
                messageHistory.remove(0);
            }
        }
    }

    private final Map<String, Topic> topics;
    private final Map<String, Subscriber> subscribers;
    private final ExecutorService messageDeliveryExecutor;

    public DesignDistributedPubSub() {
        this.topics = new ConcurrentHashMap<>();
        this.subscribers = new ConcurrentHashMap<>();
        this.messageDeliveryExecutor = Executors.newFixedThreadPool(5);
    }

    public void subscribe(String topic, String clientId) {
        if (topic == null || clientId == null || topic.trim().isEmpty() || clientId.trim().isEmpty()) {
            System.err.println("Invalid topic or clientId for subscription");
            return;
        }

        // Create subscriber if doesn't exist
        Subscriber subscriber = subscribers.computeIfAbsent(clientId, Subscriber::new);

        // Handle wildcard subscriptions
        if (topic.contains("*") || topic.contains("?")) {
            String regexPattern = topic.replace("*", ".*").replace("?", ".");
            Pattern pattern = Pattern.compile(regexPattern);
            subscriber.wildcardPatterns.add(pattern);
        } else {
            subscribeToExactTopic(topic, clientId, subscriber);
        }

        System.out.println("Client " + clientId + " subscribed to " + topic);
    }

    private void subscribeToExactTopic(String topic, String clientId, Subscriber subscriber) {
        // Create topic if doesn't exist
        Topic topicObj = topics.computeIfAbsent(topic, Topic::new);

        // Add subscriber to topic
        topicObj.subscribers.add(clientId);
        subscriber.subscribedTopics.add(topic);
    }

    public void unsubscribe(String topic, String clientId) {
        if (topic == null || clientId == null || topic.trim().isEmpty() || clientId.trim().isEmpty()) {
            return;
        }

        Subscriber subscriber = subscribers.get(clientId);
        if (subscriber == null) {
            System.out.println("Client " + clientId + " not found for unsubscription");
            return;
        }

        // Remove from specific topic
        Topic topicObj = topics.get(topic);
        if (topicObj != null) {
            topicObj.subscribers.remove(clientId);

            // Remove topic if no subscribers
            if (topicObj.subscribers.isEmpty()) {
                topics.remove(topic);
            }
        }

        subscriber.subscribedTopics.remove(topic);

        System.out.println("Client " + clientId + " unsubscribed from " + topic);
    }

    public void publish(String topic, String message) {
        if (topic == null || message == null || topic.trim().isEmpty()) {
            System.err.println("Invalid topic or message for publishing");
            return;
        }

        // Create topic if doesn't exist
        Topic topicObj = topics.computeIfAbsent(topic, Topic::new);

        // Create message
        long sequenceNumber = topicObj.getNextSequence();
        Message msg = new Message(topic, message, "system", sequenceNumber);

        // Store message in topic history
        topicObj.addMessage(msg);

        // Deliver to all interested subscribers
        deliverMessage(topic, msg);

        System.out.println("Published to " + topic + ": " + message);
    }

    private void deliverMessage(String topic, Message message) {
        messageDeliveryExecutor.submit(() -> {
            Set<String> interestedSubscribers = new HashSet<>();

            // Add direct subscribers
            Topic topicObj = topics.get(topic);
            if (topicObj != null) {
                interestedSubscribers.addAll(topicObj.subscribers);
            }

            // Add wildcard subscribers
            for (Subscriber subscriber : subscribers.values()) {
                if (subscriber.isInterestedInTopic(topic) && !interestedSubscribers.contains(subscriber.clientId)) {
                    interestedSubscribers.add(subscriber.clientId);
                }
            }

            // Deliver message to all interested subscribers
            for (String subscriberId : interestedSubscribers) {
                Subscriber subscriber = subscribers.get(subscriberId);
                if (subscriber != null) {
                    subscriber.addMessage(message);
                }
            }

            if (!interestedSubscribers.isEmpty()) {
                System.out.println("Message delivered to " + interestedSubscribers.size() + " subscribers");
            }
        });
    }

    public List<Message> getMessages(String clientId) {
        Subscriber subscriber = subscribers.get(clientId);
        if (subscriber == null) {
            return new ArrayList<>();
        }

        List<Message> messages = new ArrayList<>();
        Message msg;
        while ((msg = subscriber.messageQueue.poll()) != null) {
            messages.add(msg);
        }

        return messages;
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTopics", topics.size());
        stats.put("totalSubscribers", subscribers.size());

        int totalSubscriptions = 0;
        for (Topic topic : topics.values()) {
            totalSubscriptions += topic.subscribers.size();
        }
        stats.put("totalSubscriptions", totalSubscriptions);

        return stats;
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedPubSub pubsub = new DesignDistributedPubSub();

        System.out.println("=== Basic Operations ===");
        pubsub.subscribe("sports", "A");
        pubsub.subscribe("news", "B");
        pubsub.subscribe("sports", "C");

        pubsub.publish("sports", "Football match");
        pubsub.publish("news", "Breaking news");

        Thread.sleep(100); // Allow async message delivery

        System.out.println("A's messages: " + pubsub.getMessages("A"));
        System.out.println("B's messages: " + pubsub.getMessages("B"));
        System.out.println("C's messages: " + pubsub.getMessages("C"));

        System.out.println("\n=== Unsubscribe Test ===");
        pubsub.unsubscribe("sports", "A");
        pubsub.publish("sports", "Cricket match");

        Thread.sleep(100);

        System.out.println("A's new messages: " + pubsub.getMessages("A"));
        System.out.println("C's new messages: " + pubsub.getMessages("C"));

        System.out.println("\n=== Wildcard Test ===");
        pubsub.subscribe("sport*", "D"); // Wildcard subscription
        pubsub.publish("sports", "Tennis match");
        pubsub.publish("sporting", "Olympic news");

        Thread.sleep(100);

        System.out.println("D's messages (sport*): " + pubsub.getMessages("D"));

        // Edge Case: Unsubscribe non-subscribed client
        pubsub.unsubscribe("news", "NonExistent");

        System.out.println("\nSystem stats: " + pubsub.getSystemStats());
    }
}
