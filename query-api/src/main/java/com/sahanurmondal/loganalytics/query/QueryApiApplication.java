package com.sahanurmondal.loganalytics.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Main application class for the Query API service.
 * 
 * This service provides a REST API for searching and querying
 * indexed logs stored in Elasticsearch.
 */
@SpringBootApplication
@EnableElasticsearchRepositories
public class QueryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueryApiApplication.class, args);
    }
}
