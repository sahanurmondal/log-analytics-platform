package com.sahanurmondal.loganalytics.processor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Elasticsearch document representing an indexed log event.
 * 
 * This class extends the basic LogEvent with additional fields
 * required for indexing and searching in Elasticsearch.
 */
@Document(indexName = "logs")
@Setting(settingPath = "elasticsearch-settings.json")
public class IndexedLogEvent {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String serviceName;

    @Field(type = FieldType.Keyword)
    private String logLevel;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String message;

    @Field(type = FieldType.Object, enabled = false)
    private Map<String, String> metadata;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String messageHash;

    @Field(type = FieldType.Integer)
    private Integer messageLength;

    @Field(type = FieldType.Boolean)
    private Boolean isError;

    @Field(type = FieldType.Boolean)
    private Boolean isWarning;

    // Default constructor for Spring Data
    public IndexedLogEvent() {}

    public IndexedLogEvent(String serviceName, String logLevel, String message, 
                          Map<String, String> metadata, Instant timestamp) {
        this.serviceName = serviceName;
        this.logLevel = logLevel;
        this.message = message;
        this.metadata = metadata;
        this.timestamp = timestamp;
        this.messageLength = message != null ? message.length() : 0;
        this.messageHash = message != null ? String.valueOf(message.hashCode()) : null;
        this.isError = "ERROR".equalsIgnoreCase(logLevel);
        this.isWarning = "WARN".equalsIgnoreCase(logLevel) || "WARNING".equalsIgnoreCase(logLevel);
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.isError = "ERROR".equalsIgnoreCase(logLevel);
        this.isWarning = "WARN".equalsIgnoreCase(logLevel) || "WARNING".equalsIgnoreCase(logLevel);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.messageLength = message != null ? message.length() : 0;
        this.messageHash = message != null ? String.valueOf(message.hashCode()) : null;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndexedLogEvent that = (IndexedLogEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "IndexedLogEvent{" +
                "id='" + id + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                ", isError=" + isError +
                ", isWarning=" + isWarning +
                '}';
    }
}
