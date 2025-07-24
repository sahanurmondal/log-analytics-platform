package com.sahanurmondal.loganalytics.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the Log Ingestor service.
 * 
 * This service provides a high-availability REST API for ingesting logs
 * and publishing them to Kafka for downstream processing.
 */
@SpringBootApplication
@EnableKafka
public class LogIngestorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogIngestorApplication.class, args);
    }
}
