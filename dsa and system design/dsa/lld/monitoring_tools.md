# Monitoring & Observability Tools: MAANG Interview Prep Guide

## 1. Architectures & Core Concepts

### Prometheus
- **Architecture:** Pull-based metrics collection, time-series DB, exporters, Alertmanager.
- **Core Concepts:** Metrics, labels, PromQL, exporters, scraping, alerting.

### Grafana
- **Architecture:** Visualization layer, connects to Prometheus, Elasticsearch, etc.
- **Core Concepts:** Dashboards, panels, alerts, data sources.

### ELK/EFK Stack (Elasticsearch, Logstash/Fluentd, Kibana)
- **Architecture:** Log ingestion (Logstash/Fluentd), storage (Elasticsearch), visualization (Kibana).
- **Core Concepts:** Log parsing, indexing, search, dashboards.

### Jaeger
- **Architecture:** Distributed tracing, spans, collectors, storage, UI.
- **Core Concepts:** Traces, spans, context propagation, sampling.

### Datadog/New Relic
- **Architecture:** SaaS-based, agents collect metrics/logs/traces, dashboards, alerting.
- **Core Concepts:** APM, infrastructure monitoring, log management, SLOs.

## 2. Interview Questions & Answers
- **Q:** How does Prometheus collect and store metrics?
  - *A:* Prometheus scrapes metrics from instrumented targets at intervals, stores them in a time-series DB, and supports querying via PromQL.
- **Q:** What is the difference between logs, metrics, and traces?
  - *A:* Metrics are numeric time-series, logs are unstructured events, traces show request flows across services. All three provide observability.
- **Q:** How do you set up alerting in Prometheus and Grafana?
  - *A:* Use Prometheus Alertmanager for rule-based alerts; Grafana can send alerts based on dashboard thresholds.
- **Q:** How does distributed tracing help in microservices?
  - *A:* Tracing (Jaeger, Datadog) visualizes request paths, identifies bottlenecks, and helps debug latency or failures across services.
- **Q:** How do you monitor SLOs and error budgets?
  - *A:* Define SLIs (e.g., latency < 200ms), set SLOs (e.g., 99.9% of requests), and use tools to track error budgets and trigger alerts when exceeded.

## 3. Important Commands/Queries
- **PromQL:** `rate(http_requests_total[5m])`, `avg_over_time(cpu_usage[1h])`
- **Grafana:** Create dashboards, set alerts, connect data sources.
- **Elasticsearch:** `GET /logs/_search`, `match`, `range` queries.
- **Jaeger:** Search traces by service, operation, or trace ID.

## 4. Key Metrics for Fault Tolerance
- **Latency:** High latency signals slow services; use Prometheus, Datadog, New Relic.
- **Error Rate:** Track 4xx/5xx errors; alert on spikes.
- **Saturation:** Resource usage (CPU, memory, disk, network); alert on high utilization.
- **SLOs/SLIs/Error Budgets:** Track reliability targets and alert when error budgets are exhausted.

**Why Needed:**
- Early detection of outages, performance issues, and resource exhaustion.

**Tools:**
- **Prometheus + Grafana:** Metrics, dashboards, alerting.
- **ELK/EFK:** Log search and analysis.
- **Jaeger:** Distributed tracing.
- **Datadog/New Relic:** Unified monitoring and APM. 