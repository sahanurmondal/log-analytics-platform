package com.sahanurmondal.loganalytics.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for the Log Processor service.
 * 
 * This service consumes log events from Kafka, enriches them with additional
 * metadata, and indexes them into Elasticsearch for fast searching.
 */
@SpringBootApplication
@EnableKafka
@EnableElasticsearchRepositories
public class LogProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogProcessorApplication.class, args);
    }
}
