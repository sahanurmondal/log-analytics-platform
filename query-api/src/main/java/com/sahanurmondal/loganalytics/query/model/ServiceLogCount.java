package com.sahanurmondal.loganalytics.query.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLogCount {
    private String serviceName;
    private Long count;
}
