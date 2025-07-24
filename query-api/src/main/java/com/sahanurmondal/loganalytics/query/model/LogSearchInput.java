package com.sahanurmondal.loganalytics.query.model;

/**
 * Input class for log search parameters in GraphQL queries.
 */
public class LogSearchInput {
    private String fullTextQuery;
    private String serviceName;
    private String logLevel;
    private String fromTime;
    private String toTime;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "timestamp";
    private SortOrder sortOrder = SortOrder.DESC;

    // Default constructor
    public LogSearchInput() {}

    // Getters and setters
    public String getFullTextQuery() {
        return fullTextQuery;
    }

    public void setFullTextQuery(String fullTextQuery) {
        this.fullTextQuery = fullTextQuery;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page != null && page >= 0 ? page : 0;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size != null && size > 0 ? Math.min(size, 1000) : 20;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy : "timestamp";
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder != null ? sortOrder : SortOrder.DESC;
    }

    @Override
    public String toString() {
        return "LogSearchInput{" +
                "fullTextQuery='" + fullTextQuery + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", logLevel='" + logLevel + '\'' +
                ", fromTime='" + fromTime + '\'' +
                ", toTime='" + toTime + '\'' +
                ", page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder=" + sortOrder +
                '}';
    }
}

enum SortOrder {
    ASC, DESC
}
