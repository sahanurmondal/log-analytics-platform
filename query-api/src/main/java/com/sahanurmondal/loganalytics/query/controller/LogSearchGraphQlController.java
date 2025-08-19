package com.sahanurmondal.loganalytics.query.controller;

import com.sahanurmondal.loganalytics.query.model.IndexedLogEventDto;
import com.sahanurmondal.loganalytics.query.model.LogSearchInput;
import com.sahanurmondal.loganalytics.query.model.LogSearchResponse;
import com.sahanurmondal.loganalytics.query.model.LogStats;
import com.sahanurmondal.loganalytics.query.model.LogStatsInput;
import com.sahanurmondal.loganalytics.query.model.SortOrder;
import com.sahanurmondal.loganalytics.query.model.TimeRange;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

/**
 * GraphQL controller for searching and querying indexed logs.
 *
 * Provides GraphQL endpoints for flexible log querying with advanced
 * filtering, pagination, and aggregation capabilities.
 */
@Controller
public class LogSearchGraphQlController {

    private static final Logger logger = LoggerFactory.getLogger(LogSearchGraphQlController.class);

    private final ElasticsearchOperations elasticsearchOperations;
    private final Counter searchRequestsCounter;
    private final Timer searchTimer;

    public LogSearchGraphQlController(ElasticsearchOperations elasticsearchOperations, MeterRegistry meterRegistry) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.searchRequestsCounter = Counter.builder("graphql.search.requests.total")
                .description("Total number of GraphQL search requests")
                .register(meterRegistry);
        this.searchTimer = Timer.builder("graphql.search.duration")
                .description("Time taken to execute GraphQL search queries")
                .register(meterRegistry);
    }

    /**
     * Searches logs based on the provided input criteria.
     *
     * @param input Search input containing filters and pagination parameters
     * @return Search response with results and pagination information
     */
    @QueryMapping
    public LogSearchResponse searchLogs(@Argument LogSearchInput input) {
        Timer.Sample sample = Timer.start();
        searchRequestsCounter.increment();

        try {
            logger.info("GraphQL search request: {}", input);

            // Validate and adjust parameters
            int page = Math.max(input.getPage(), 0);
            int size = Math.min(Math.max(input.getSize(), 1), 1000);

            // Build search criteria
            Criteria criteria = buildSearchCriteria(input);

            // Create query with pagination and sorting
            Query query = new CriteriaQuery(criteria)
                    .setPageable(createPageable(page, size, input.getSortBy(), input.getSortOrder()));

            // Execute search
            SearchHits<Map> searchHits = elasticsearchOperations.search(query, Map.class);

            // Transform results
            List<IndexedLogEventDto> results = searchHits.getSearchHits().stream()
                    .map(this::mapToIndexedLogEventDto)
                    .toList();

            LogSearchResponse response = new LogSearchResponse(results, searchHits.getTotalHits(), page, size);

            logger.info("GraphQL search completed. Found {} results out of {} total",
                    results.size(), searchHits.getTotalHits());

            return response;

        } catch (Exception e) {
            logger.error("Error executing GraphQL search query: {}", e.getMessage(), e);
            return new LogSearchResponse(List.of(), 0L, input.getPage(), input.getSize());
        } finally {
            sample.stop(searchTimer);
        }
    }

    /**
     * Gets a specific log event by its ID.
     *
     * @param id The log event ID
     * @return The log event or null if not found
     */
    @QueryMapping
    public IndexedLogEventDto logById(@Argument String id) {
        try {
            logger.debug("Fetching log by ID: {}", id);

            Map<String, Object> result = elasticsearchOperations.get(id, Map.class);

            if (result != null) {
                return mapToIndexedLogEventDto(id, result);
            }

            logger.warn("Log not found with ID: {}", id);
            return null;

        } catch (Exception e) {
            logger.error("Error fetching log by ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Gets aggregated statistics for logs.
     *
     * @param input Statistics input with optional filters
     * @return Log statistics
     */
    @QueryMapping
    public LogStats logStats(@Argument LogStatsInput input) {
        try {
            logger.info("Getting log statistics: {}", input);

            // Default to last 24 hours if no time range specified
            Instant defaultTo = Instant.now();
            Instant actualFrom = parseTimestamp(input != null ? input.getFromTime() : null,
                    defaultTo.minus(24, ChronoUnit.HOURS));
            Instant actualTo = parseTimestamp(input != null ? input.getToTime() : null, defaultTo);

            // Build base criteria
            Criteria criteria = new Criteria();
            criteria = criteria.and("timestamp").between(actualFrom, actualTo);

            if (input != null && StringUtils.hasText(input.getServiceName())) {
                criteria = criteria.and("serviceName").is(input.getServiceName());
            }

            // Get total count
            Query totalQuery = new CriteriaQuery(criteria);
            long totalLogs = elasticsearchOperations.count(totalQuery, Map.class);

            // Get error count
            Criteria errorCriteria = criteria.and("isError").is(true);
            Query errorQuery = new CriteriaQuery(errorCriteria);
            long errorCount = elasticsearchOperations.count(errorQuery, Map.class);

            // Get warning count
            Criteria warningCriteria = criteria.and("isWarning").is(true);
            Query warningQuery = new CriteriaQuery(warningCriteria);
            long warningCount = elasticsearchOperations.count(warningQuery, Map.class);

            // Create LogStats response (simplified version without aggregations for now)
            LogStats stats = new LogStats();
            stats.setTotalLogs(totalLogs);
            stats.setErrorCount(errorCount);
            stats.setWarningCount(warningCount);
            stats.setLogsByService(List.of()); // TODO: Implement aggregations
            stats.setLogsByLevel(List.of()); // TODO: Implement aggregations

            TimeRange timeRange = new TimeRange();
            timeRange.setFromTime(actualFrom.toString());
            timeRange.setToTime(actualTo.toString());
            stats.setTimeRange(timeRange);

            return stats;

        } catch (Exception e) {
            logger.error("Error getting log statistics: {}", e.getMessage(), e);
            return new LogStats(); // Return empty stats on error
        }
    }

    private Criteria buildSearchCriteria(LogSearchInput input) {
        Criteria criteria = new Criteria();

        // Full-text search on message field
        if (StringUtils.hasText(input.getFullTextQuery())) {
            criteria = criteria.and("message").contains(input.getFullTextQuery());
        }

        // Filter by service name
        if (StringUtils.hasText(input.getServiceName())) {
            criteria = criteria.and("serviceName").is(input.getServiceName());
        }

        // Filter by log level
        if (StringUtils.hasText(input.getLogLevel())) {
            criteria = criteria.and("logLevel").is(input.getLogLevel().toUpperCase());
        }

        // Time range filter
        Instant fromTime = parseTimestamp(input.getFromTime(), null);
        Instant toTime = parseTimestamp(input.getToTime(), null);

        if (fromTime != null && toTime != null) {
            criteria = criteria.and("timestamp").between(fromTime, toTime);
        } else if (fromTime != null) {
            criteria = criteria.and("timestamp").greaterThanEqual(fromTime);
        } else if (toTime != null) {
            criteria = criteria.and("timestamp").lessThanEqual(toTime);
        }

        return criteria;
    }

    private Pageable createPageable(int page, int size, String sortBy, SortOrder sortOrder) {
        Sort.Direction direction = sortOrder == SortOrder.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }

    @SuppressWarnings("rawtypes")
    private IndexedLogEventDto mapToIndexedLogEventDto(SearchHit<Map> hit) {
        // noinspection unchecked
        return mapToIndexedLogEventDto(hit.getId(), hit.getContent());
    }

    @SuppressWarnings("unchecked")
    private IndexedLogEventDto mapToIndexedLogEventDto(String id, Map<String, Object> source) {
        return new IndexedLogEventDto(
                id,
                parseTimestamp(source.get("timestamp")),
                (String) source.get("serviceName"),
                (String) source.get("logLevel"),
                (String) source.get("message"),
                (Map<String, Object>) source.get("metadata"),
                (String) source.get("messageHash"),
                (Integer) source.get("messageLength"),
                (Boolean) source.get("isError"),
                (Boolean) source.get("isWarning"));
    }

    private Instant parseTimestamp(Object timestamp) {
        if (timestamp instanceof String) {
            try {
                return Instant.parse((String) timestamp);
            } catch (Exception e) {
                logger.warn("Failed to parse timestamp: {}", timestamp);
                return Instant.now();
            }
        } else if (timestamp instanceof Long) {
            return Instant.ofEpochMilli((Long) timestamp);
        }
        return Instant.now();
    }

    private Instant parseTimestamp(String timestamp, Instant defaultValue) {
        if (StringUtils.hasText(timestamp)) {
            try {
                return Instant.parse(timestamp);
            } catch (Exception e) {
                logger.warn("Failed to parse timestamp: {}", timestamp);
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
