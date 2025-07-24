package com.sahanurmondal.loganalytics.query.model;

import java.time.Instant;
import java.util.Map;

/**
 * DTO representing an indexed log event for GraphQL responses.
 */
public class IndexedLogEventDto {
    private String id;
    private String timestamp;
    private String serviceName;
    private String logLevel;
    private String message;
    private Map<String, Object> metadata;
    private String messageHash;
    private Integer messageLength;
    private Boolean isError;
    private Boolean isWarning;

    // Default constructor
    public IndexedLogEventDto() {}

    public IndexedLogEventDto(String id, Instant timestamp, String serviceName, 
                             String logLevel, String message, Map<String, Object> metadata,
                             String messageHash, Integer messageLength, 
                             Boolean isError, Boolean isWarning) {
        this.id = id;
        this.timestamp = timestamp != null ? timestamp.toString() : null;
        this.serviceName = serviceName;
        this.logLevel = logLevel;
        this.message = message;
        this.metadata = metadata;
        this.messageHash = messageHash;
        this.messageLength = messageLength;
        this.isError = isError;
        this.isWarning = isWarning;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }

    public Integer getMessageLength() {
        return messageLength;
    }

    public void setMessageLength(Integer messageLength) {
        this.messageLength = messageLength;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public Boolean getIsWarning() {
        return isWarning;
    }

    public void setIsWarning(Boolean isWarning) {
        this.isWarning = isWarning;
    }

    @Override
    public String toString() {
        return "IndexedLogEventDto{" +
                "id='" + id + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", message='" + message + '\'' +
                ", isError=" + isError +
                ", isWarning=" + isWarning +
                '}';
    }
}
