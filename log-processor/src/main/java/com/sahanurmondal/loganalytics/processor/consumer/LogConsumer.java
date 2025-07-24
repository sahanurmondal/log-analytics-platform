package com.sahanurmondal.loganalytics.processor.consumer;

import com.sahanurmondal.loganalytics.model.LogEvent;
import com.sahanurmondal.loganalytics.processor.model.IndexedLogEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Kafka consumer that processes log events and indexes them into Elasticsearch.
 * 
 * This service consumes from the raw-logs topic, enriches the log events
 * with additional metadata, and performs bulk indexing for efficiency.
 */
@Service
public class LogConsumer {

    private static final Logger logger = LoggerFactory.getLogger(LogConsumer.class);

    private final ElasticsearchOperations elasticsearchOperations;
    private final Counter processedLogsCounter;
    private final Counter failedLogsCounter;
    private final Timer processingTimer;

    public LogConsumer(ElasticsearchOperations elasticsearchOperations, MeterRegistry meterRegistry) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.processedLogsCounter = Counter.builder("logs.processed.total")
                .description("Total number of logs successfully processed")
                .register(meterRegistry);
        this.failedLogsCounter = Counter.builder("logs.processing.failed.total")
                .description("Total number of logs that failed processing")
                .register(meterRegistry);
        this.processingTimer = Timer.builder("logs.processing.duration")
                .description("Time taken to process log events")
                .register(meterRegistry);
    }

    /**
     * Consumes log events from Kafka and indexes them into Elasticsearch.
     * 
     * @param logEvents List of log events from Kafka
     * @param partition Kafka partition
     * @param offset Kafka offset
     * @param acknowledgment Kafka acknowledgment for manual commit
     */
    @KafkaListener(
            topics = "${app.kafka.topic.raw-logs}",
            groupId = "${app.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeLogEvents(@Payload List<LogEvent> logEvents,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                @Header(KafkaHeaders.OFFSET) long offset,
                                Acknowledgment acknowledgment) {
        
        Timer.Sample sample = Timer.start();
        
        try {
            logger.info("Received {} log events from partition {} at offset {}", 
                       logEvents.size(), partition, offset);

            if (logEvents.isEmpty()) {
                logger.warn("Received empty batch of log events");
                acknowledgment.acknowledge();
                return;
            }

            // Transform LogEvent to IndexedLogEvent
            List<IndexedLogEvent> indexedEvents = logEvents.stream()
                    .map(this::transformToIndexedLogEvent)
                    .toList();

            // Bulk index to Elasticsearch
            try {
                List<IndexedObjectInformation> indexedInfo = elasticsearchOperations.save(indexedEvents);
                
                logger.info("Successfully indexed {} log events to Elasticsearch", indexedInfo.size());
                processedLogsCounter.increment(indexedInfo.size());

                // Acknowledge the message only after successful processing
                acknowledgment.acknowledge();
                
            } catch (Exception e) {
                logger.error("Failed to index log events to Elasticsearch: {}", e.getMessage(), e);
                failedLogsCounter.increment(logEvents.size());
                
                // For now, we acknowledge even failed messages to avoid infinite retries
                // In production, you might want to implement dead letter queue
                acknowledgment.acknowledge();
            }

        } catch (Exception e) {
            logger.error("Unexpected error processing log events: {}", e.getMessage(), e);
            failedLogsCounter.increment(logEvents.size());
            acknowledgment.acknowledge();
            
        } finally {
            sample.stop(processingTimer);
        }
    }

    /**
     * Transforms a LogEvent into an IndexedLogEvent with additional metadata.
     * 
     * @param logEvent The original log event
     * @return Enhanced log event ready for indexing
     */
    private IndexedLogEvent transformToIndexedLogEvent(LogEvent logEvent) {
        try {
            Instant timestamp = Instant.now();
            
            IndexedLogEvent indexedEvent = new IndexedLogEvent(
                    logEvent.serviceName(),
                    logEvent.logLevel(),
                    logEvent.message(),
                    logEvent.metadata(),
                    timestamp
            );
            
            // Generate a unique ID for the document
            indexedEvent.setId(generateDocumentId(logEvent, timestamp));
            
            return indexedEvent;
            
        } catch (Exception e) {
            logger.error("Error transforming log event: {}", e.getMessage(), e);
            
            // Create a minimal indexed event in case of transformation error
            IndexedLogEvent fallbackEvent = new IndexedLogEvent();
            fallbackEvent.setId(UUID.randomUUID().toString());
            fallbackEvent.setServiceName(logEvent.serviceName());
            fallbackEvent.setLogLevel(logEvent.logLevel());
            fallbackEvent.setMessage("Error processing: " + logEvent.message());
            fallbackEvent.setTimestamp(Instant.now());
            
            return fallbackEvent;
        }
    }

    /**
     * Generates a unique document ID for the Elasticsearch document.
     * 
     * @param logEvent The log event
     * @param timestamp The timestamp
     * @return A unique document ID
     */
    private String generateDocumentId(LogEvent logEvent, Instant timestamp) {
        // Combine service name, timestamp, and a random UUID for uniqueness
        return String.format("%s-%d-%s", 
                logEvent.serviceName().toLowerCase(),
                timestamp.toEpochMilli(),
                UUID.randomUUID().toString().substring(0, 8));
    }
}
