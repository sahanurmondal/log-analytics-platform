package com.sahanurmondal.loganalytics.query.model;

/**
 * Input class for log statistics parameters in GraphQL queries.
 */
public class LogStatsInput {
    private String serviceName;
    private String fromTime;
    private String toTime;

    // Default constructor
    public LogStatsInput() {}

    // Getters and setters
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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

    @Override
    public String toString() {
        return "LogStatsInput{" +
                "serviceName='" + serviceName + '\'' +
                ", fromTime='" + fromTime + '\'' +
                ", toTime='" + toTime + '\'' +
                '}';
    }
}
