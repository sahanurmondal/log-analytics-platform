package com.sahanurmondal.loganalytics.ingestor.controller;

import com.sahanurmondal.loganalytics.model.LogEvent;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * REST controller for log ingestion.
 * 
 * Provides endpoints for accepting log events and publishing them to Kafka
 * for downstream processing by the log processor service.
 */
@RestController
@RequestMapping("/api/v1/logs")
@CrossOrigin(origins = "*")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;
    private final String topicName;
    private final Counter ingestedLogsCounter;
    private final Counter failedLogsCounter;

    public LogController(KafkaTemplate<String, LogEvent> kafkaTemplate,
                        @Value("${app.kafka.topic.raw-logs}") String topicName,
                        MeterRegistry meterRegistry) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
        this.ingestedLogsCounter = Counter.builder("logs.ingested.total")
                .description("Total number of logs successfully ingested")
                .register(meterRegistry);
        this.failedLogsCounter = Counter.builder("logs.failed.total")
                .description("Total number of logs that failed to be ingested")
                .register(meterRegistry);
    }

    /**
     * Ingests a batch of log events.
     * 
     * @param logEvents List of log events to ingest
     * @return Response indicating success or failure
     */
    @PostMapping
    public ResponseEntity<IngestResponse> ingestLogs(@Valid @RequestBody List<LogEvent> logEvents) {
        logger.info("Received {} log events for ingestion", logEvents.size());

        if (logEvents.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new IngestResponse(false, "No log events provided", 0, 0));
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Process each log event asynchronously
        List<CompletableFuture<SendResult<String, LogEvent>>> futures = logEvents.stream()
                .map(logEvent -> {
                    // Use service name as partition key for better distribution
                    String key = logEvent.serviceName();
                    return kafkaTemplate.send(topicName, key, logEvent)
                            .whenComplete((result, ex) -> {
                                if (ex == null) {
                                    successCount.incrementAndGet();
                                    ingestedLogsCounter.increment();
                                    logger.debug("Successfully sent log event to topic {} partition {} offset {}",
                                            result.getRecordMetadata().topic(),
                                            result.getRecordMetadata().partition(),
                                            result.getRecordMetadata().offset());
                                } else {
                                    failureCount.incrementAndGet();
                                    failedLogsCounter.increment();
                                    logger.error("Failed to send log event for service {}: {}",
                                            logEvent.serviceName(), ex.getMessage());
                                }
                            });
                })
                .toList();

        // Wait for all operations to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        int finalSuccessCount = successCount.get();
        int finalFailureCount = failureCount.get();

        logger.info("Ingestion completed. Success: {}, Failures: {}", finalSuccessCount, finalFailureCount);

        boolean success = finalFailureCount == 0;
        String message = success ? "All log events ingested successfully" :
                String.format("Partial success: %d succeeded, %d failed", finalSuccessCount, finalFailureCount);

        return ResponseEntity.ok(new IngestResponse(success, message, finalSuccessCount, finalFailureCount));
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Log Ingestor is healthy");
    }

    /**
     * Response model for log ingestion operations.
     */
    public record IngestResponse(
            boolean success,
            String message,
            int successCount,
            int failureCount
    ) {}
}
