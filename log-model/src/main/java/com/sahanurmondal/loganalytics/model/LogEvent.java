package com.sahanurmondal.loganalytics.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a log event in the analytics platform.
 * This is a record that implements Serializable for Kafka message
 * serialization.
 */
public record LogEvent(
        @JsonProperty("serviceName") @NotBlank(message = "Service name cannot be blank") String serviceName,

        @JsonProperty("logLevel") @NotBlank(message = "Log level cannot be blank") String logLevel,

        @JsonProperty("message") @NotBlank(message = "Message cannot be blank") String message,

        @JsonProperty("metadata") @NotNull(message = "Metadata cannot be null") Map<String, String> metadata)
        implements Serializable {

    @JsonCreator
    public LogEvent {
        Objects.requireNonNull(serviceName, "Service name cannot be null");
        Objects.requireNonNull(logLevel, "Log level cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(metadata, "Metadata cannot be null");
    }

    /**
     * Creates a LogEvent with empty metadata.
     */
    public static LogEvent of(String serviceName, String logLevel, String message) {
        return new LogEvent(serviceName, logLevel, message, Map.of());
    }

    /**
     * Creates a LogEvent with the specified metadata.
     */
    public static LogEvent of(String serviceName, String logLevel, String message, Map<String, String> metadata) {
        return new LogEvent(serviceName, logLevel, message, metadata);
    }

    /**
     * Returns a new LogEvent with additional metadata.
     */
    public LogEvent withMetadata(String key, String value) {
        var newMetadata = new java.util.HashMap<>(this.metadata);
        newMetadata.put(key, value);
        return new LogEvent(serviceName, logLevel, message, Map.copyOf(newMetadata));
    }

    /**
     * Returns true if this is an error-level log event.
     */
    public boolean isError() {
        return "ERROR".equalsIgnoreCase(logLevel);
    }

    /**
     * Returns true if this is a warning-level log event.
     */
    public boolean isWarning() {
        return "WARN".equalsIgnoreCase(logLevel) || "WARNING".equalsIgnoreCase(logLevel);
    }

    /**
     * Returns the metadata value for the given key, or null if not present.
     */
    public String getMetadataValue(String key) {
        return metadata.get(key);
    }
}
