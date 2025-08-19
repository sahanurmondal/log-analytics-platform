package com.sahanurmondal.loganalytics.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Log statistics aggregation response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogStats {
    private Long totalLogs;
    private Long errorCount;
    private Long warningCount;
    private List<ServiceLogCount> logsByService;
    private List<LevelLogCount> logsByLevel;
    private TimeRange timeRange;
}
