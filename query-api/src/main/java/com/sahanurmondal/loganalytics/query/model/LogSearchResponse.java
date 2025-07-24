package com.sahanurmondal.loganalytics.query.model;

import java.util.List;

/**
 * Response wrapper for log search results with pagination information.
 */
public class LogSearchResponse {
    private List<IndexedLogEventDto> results;
    private Long totalHits;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;

    // Default constructor
    public LogSearchResponse() {}

    public LogSearchResponse(List<IndexedLogEventDto> results, Long totalHits, 
                           Integer currentPage, Integer pageSize) {
        this.results = results;
        this.totalHits = totalHits;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalHits / pageSize);
        this.hasNext = currentPage < totalPages - 1;
        this.hasPrevious = currentPage > 0;
    }

    // Getters and setters
    public List<IndexedLogEventDto> getResults() {
        return results;
    }

    public void setResults(List<IndexedLogEventDto> results) {
        this.results = results;
    }

    public Long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Long totalHits) {
        this.totalHits = totalHits;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
