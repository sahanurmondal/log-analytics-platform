package com.sahanurmondal.loganalytics.query.model;

import java.util.List;

/**
 * Log statistics aggregation response.
 */
public class LogStats {
    private Long totalLogs;
    private Long errorCount;
    private Long warningCount;
    private List<ServiceLogCount> logsByService;
    private List<LevelLogCount> logsByLevel;
    private TimeRange timeRange;

    // Default constructor
    public LogStats() {}

    // Getters and setters
    public Long getTotalLogs() {
        return totalLogs;
    }

    public void setTotalLogs(Long totalLogs) {
        this.totalLogs = totalLogs;
    }

    public Long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Long errorCount) {
        this.errorCount = errorCount;
    }

    public Long getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Long warningCount) {
        this.warningCount = warningCount;
    }

    public List<ServiceLogCount> getLogsByService() {
        return logsByService;
    }

    public void setLogsByService(List<ServiceLogCount> logsByService) {
        this.logsByService = logsByService;
    }

    public List<LevelLogCount> getLogsByLevel() {
        return logsByLevel;
    }

    public void setLogsByLevel(List<LevelLogCount> logsByLevel) {
        this.logsByLevel = logsByLevel;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }
}

/**
 * Log count grouped by service name.
 */
class ServiceLogCount {
    private String serviceName;
    private Long count;

    public ServiceLogCount() {}

    public ServiceLogCount(String serviceName, Long count) {
        this.serviceName = serviceName;
        this.count = count;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

/**
 * Log count grouped by log level.
 */
class LevelLogCount {
    private String logLevel;
    private Long count;

    public LevelLogCount() {}

    public LevelLogCount(String logLevel, Long count) {
        this.logLevel = logLevel;
        this.count = count;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}

/**
 * Time range specification.
 */
class TimeRange {
    private String fromTime;
    private String toTime;

    public TimeRange() {}

    public TimeRange(String fromTime, String toTime) {
        this.fromTime = fromTime;
        this.toTime = toTime;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }
}
