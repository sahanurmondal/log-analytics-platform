# Staff Engineer Preparation Compendium

Time Horizon: 4 Months | Focus: Architecture Depth, Scale & Reliability, Leadership & Influence, Interview Excellence

Each section includes: Purpose, When to Use, Structured Template, 1–2 Filled Exemplars, Common Pitfalls, Metrics / Success Indicators, Expansion Ideas.

---
## 1. Architecture Decision Records (ADRs)
**Purpose**: Permanently capture significant engineering decisions with rationale and trade-offs; enable future revisit.
**When to Use**: Any non-trivial change impacting architecture, cost, risk, operability, or team process.

**Structured Template**:
- Title / ID
- Status (Proposed | Accepted | Superseded | Deprecated)
- Date / Owners
- Context (Forces, Constraints, Drivers)
- Decision
- Alternatives Considered (≥3 with Pros/Cons)
- Impact (Code, Operations, Org)
- Risk & Mitigations
- Security / Privacy / Compliance Notes
- Observability Implications
- Rollback / Reconsider Triggers
- Follow-up Tasks (Dated)

**Exemplar 1** (Short):
- Title: ADR-012: Introduce API Gateway Layer
- Status: Accepted
- Context: Increasing client variants (web, mobile, partner); inconsistent auth and rate limiting scattered.
- Decision: Adopt single gateway (Envoy) with centralized authN/Z, throttling, request normalization.
- Alternatives: (1) Keep per-service filters (Pros: no new infra; Cons: duplication), (2) Library-based cross-cutting (Pros: uniform code; Cons: language lock-in), (3) Gateway (Chosen; Pros: central policy; Cons: new SPOF risk).
- Impact: Add infra tier, update DNS, standardize tokens (JWT). Latency +3–5ms median.
- Risk: Gateway outage impact → Mitigation active-active + health failover.
- Observability: Unified access logs, per-route latency SLOs.
- Rollback Trigger: >15ms P95 added latency sustained 7 days.
- Follow-up: Rate limit policy DSL (due 2025-09-10).

**Exemplar 2** (Deep):
- Title: ADR-019: Migrate from Single-Region Postgres to Multi-Region Replicated Architecture
- Status: Proposed
- Context: SLA requires <1% annual downtime; current RTO 2h; latency complaints from APAC.
- Decision: Use Postgres logical replication + read replicas per region; writes centralized; plan eventual multi-primary review.
- Alternatives: (1) Sharding by user (complex rebalancing), (2) Managed global DB (vendor lock-in), (3) Multi-region read scaling (chosen).
- Impact: Read latency -40% APAC; write latency +5% global (cross-region hop). Adds replication lag ~150ms.
- Risk: Replica lag causing stale reads → Mitigate with read-after-write tokens.
- Security/Privacy: Cross-border data residency gating EU data (subset pinned to Frankfurt cluster).
- Observability: Lag metric alarms (>500ms 5m).
- Rollback: Lag >1s sustained or replication errors >0.1% events.
- Follow-up: Evaluate multi-primary Q1 next year.

**Common Pitfalls**:
- Vague context (unclear why). Missing alternatives. No revisit triggers.
**Metrics**:
- ADR Coverage: (# material decisions documented) / (decisions) ≥ 90%
- Revisit Rate: % ADRs revisited on trigger within SLA (≥80%).
**Expansion Ideas**: Link ADRs to incident root causes; generate quarterly ADR digest.
**Example Resources**:
- https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions (Nygard ADR article)
- https://github.com/joelparkerhenderson/architecture_decision_record (ADR templates)
- https://adr.github.io/ (adr-tools usage)
- https://martinfowler.com/ (Trade-off & evolutionary architecture essays)
- https://aws.amazon.com/architecture/well-architected/ (Well-Architected pillars reference)

---
## 2. Capacity & Cost Modeling
**Purpose**: Forecast scaling limits, infra cost, and cost-to-serve; inform trade-offs & budget.
**When to Use**: New architecture, volume growth planning, cost optimization reviews.

**Template**:
- Inputs Table (Traffic, DAU, Peak RPS, Read/Write %, Avg Payload, Cache Hit %, Latency Targets)
- Tier Resource Model (App, Cache, DB, Queue, Storage, CDN)
- Scaling Formula per Tier (e.g., Instances = PeakCPU / TargetUtil)
- Cost Table (Unit price × quantity)
- Breakpoints (When upgrade needed)
- Headroom Policy (e.g., 30% spare at peak)
- Optimization Opportunities (Top 3 with savings estimate)

**Exemplar**:
- Inputs: DAU 5M; Peak RPS 25k; RW 80/20; Payload 1.5KB; Cache Hit 85%.
- Derived DB Write RPS: 25k * 0.2 = 5k.
- App CPU: 25k * 1.2ms cpu = 30k ms/s = 30 cores; With 60% target util → 50 cores → 13 × 4-core nodes.
- Cost Snapshot: App $6k/mo, Redis $2k, DB $7k, CDN $1.5k, Total $16.5k.
- Breakpoint: At 40k RPS DB CPU ≥80%; plan partitioning.
- Optimization: (1) Increase cache hit to 90% saves ~750 RPS DB; (2) Compress payload reduces egress $400/mo; (3) Adopt spot instances save 20% app cost.

**Pitfalls**: Using average not P95; ignoring replication overhead; static cost without seasonal peak.
**Metrics**: Forecast error <15% monthly; Cost per 1k requests; Infra utilization (60–70% target).
**Expansion**: Add scenario sensitivity; Monte Carlo traffic variance.
**Example Resources**:
- https://sre.google/sre-book/capacity-planning/ (Google SRE capacity planning)
- https://aws.amazon.com/blogs/architecture/ (Cost/perf architecture blogs)
- https://cloud.google.com/architecture/framework/cost-optimization (Cloud cost optimization)
- https://netflixtechblog.com/ (Capacity & efficiency posts)
- https://cloudflare.com/blog/ (Performance & scaling case studies)

---
## 3. SLO / Reliability Playbook
**Purpose**: Define reliability targets, protect error budgets, codify alerting & remediation.
**When to Use**: Service onboarding, quarterly reliability review, incident pattern changes.

**Template**:
- Service Overview
- User Journeys & Critical Indicators
- SLO Table (Availability, Latency P95, Freshness, Durability)
- Error Budget Policy (Consumption rules)
- Alert Taxonomy (Immediate, Degraded, Watch)
- Burn Rate Alarms (e.g., 2h fast, 24h slow)
- Remediation Workflow
- Incident Roles & Escalation Ladder
- Postmortem Trigger Criteria

**Exemplar**:
- API Availability SLO: 99.9% monthly; Error Budget 43m.
- Burn Rate Rule: If burn rate >14 over 1h → page; >2 over 6h → investigate.
- Latency P95 SLO: <250ms; If >300ms 30m sustained → page.

**Pitfalls**: Monitoring SLI mismatch; alert storms; not tracking budget usage in backlog prioritization.
**Metrics**: Budget usage vs plan; MTTR; % action items closed within SLA.
**Expansion**: Add probabilistic SLOs; composite SLO across dependencies.
**Example Resources**:
- https://sre.google/sre-book/service-level-objectives/ (SLO fundamentals)
- https://nobl9.com/resources (Practical SLO guides)
- https://grafana.com/blog/tags/slo/ (Implementation stories)
- https://landing.google.com/sre/resources/practicesandprocesses/ (SRE practices)
- https://honeycomb.io/blog (Observability & error budget narratives)

---
## 4. Migration Strategy Catalog
**Purpose**: Provide repeatable migration playbooks reducing risk & downtime.
**When to Use**: Architecture shifts (DB, service extraction, infra provider change).

**Template (Per Migration)**:
- Name & Goal
- Drivers / Success Criteria
- Constraints / Guardrails
- Phases (Assess, Prepare, Dual, Cutover, Decommission)
- Data Strategy (Backfill, CDC, Validation)
- Traffic Shaping (Canary %, Feature Flags)
- Risk Matrix (Likelihood × Impact)
- Rollback Plan
- Metrics (Lag, Error Rate, Performance)

**Exemplar (Monolith → Service)**:
- Phases: Extract domain boundaries; Introduce anti-corruption layer; Dual-write; Shadow reads; Gradual cut traffic.
- Data: Backfill historical orders; CDC streaming for new events; Consistency validator job.
- Risk: Divergence (Mitigate: idempotent events). Latency spike (Mitigate: warm cache priming).

**Pitfalls**: Big bang; ignoring write skew; missing idempotency.
**Metrics**: Cutover error rate <0.2%; Dual-write divergence <0.01%; Backfill throughput vs schedule.
**Expansion**: Automated diff dashboards; synthetic shadow traffic harness.
**Example Resources**:
- https://martinfowler.com/articles/patterns-of-distributed-systems/ (Patterns incl. migration aides)
- https://martinfowler.com/bliki/StranglerFigApplication.html (Strangler pattern)
- https://aws.amazon.com/architecture/ (Migration whitepapers)
- https://vercel.com/blog/ (Zero-downtime deployment stories)
- https://shopify.engineering (Large-scale refactor & migration posts)

---
## 5. Threat & Privacy Toolkit
**Purpose**: Systematically assess security threats & privacy risks early.
**When to Use**: New feature with PII, architectural change, compliance audits.

**Template**:
- Data Classification Matrix (Public/Internal/Restricted/PII/Sensitive)
- STRIDE Table per Trust Boundary
- Threat Entries: Vector, Impact, Likelihood, Mitigation, Residual Risk
- Privacy Considerations (Minimization, Retention, Consent, Access Controls)
- Encryption (At Rest, In Transit, Key Management Rotation Policy)
- Audit Logging Schema
- Abuse Prevention (Rate, Behavioral anomaly detection)

**Exemplar**:
- Threat: Replay of JWT tokens from stolen device. Mitigation: short-lived tokens + refresh + device binding.
- Privacy: Pseudonymize user_id in analytics; retention trimmed to 13 months.

**Pitfalls**: Over-engineering low-risk; ignoring abuse vectors; stale key rotation.
**Metrics**: Time to patch critical vuln; % endpoints with mTLS; Key rotation interval compliance.
**Expansion**: Add threat scoring automation; integrate DLP scanning.
**Example Resources**:
- https://owasp.org/www-project-top-ten/ (OWASP Top 10)
- https://owasp.org/www-community/Threat_Modeling (Threat modeling guidance)
- https://cheatsheetseries.owasp.org/ (Security cheat sheets)
- https://cloudsecurityalliance.org/ (Cloud security patterns)
- https://gdpr.eu/ (GDPR overview)

---
## 6. Performance Engineering Case Studies
**Purpose**: Build diagnostic intuition via real bottleneck narratives.
**When to Use**: Regression analysis, capacity planning, interview storytelling.

**Template (Per Case)**:
- Symptom & KPI Deviation
- Baseline vs Current Metrics
- Hypotheses List
- Instrumentation Plan (Metrics / Traces / Profiles)
- Findings (Flame Graph, GC Log snippet)
- Fix Applied
- Result Metrics Delta
- Lessons / Preventative Guardrail

**Exemplar**:
- Symptom: P95 latency 220ms→420ms after deploy.
- Hypothesis: N+1 query; GC pause; thread pool saturation.
- Instrumentation: Added DB query timing, thread pool queue size gauge.
- Finding: Thread pool queue length spikes; root cause: blocking external call added in request path.
- Fix: Async bulkhead with timeout + cache fallback.
- Result: P95 240ms; queue stable.

**Pitfalls**: Premature optimization; skipping baseline snapshot.
**Metrics**: Mean time to isolate cause; % cases with automated guardrails added.
**Expansion**: Add chaos/perf combined tests; scenario tagging taxonomy.
**Example Resources**:
- https://mechanical-sympathy.blogspot.com/ (Low-latency & perf)
- https://github.com/jvm-profiling-tools/honest-profiler (Profiling tool)
- https://netflixtechblog.com/ (Perf & optimization case studies)
- https://engineering.linkedin.com/blog (Performance & scalability posts)
- https://aws.amazon.com/builders-library/ (Latency & perf articles)

---
## 7. Distributed Systems Advanced Primer
**Purpose**: Deepen understanding for high-scale design interviews & real-world resilience.
**When to Use**: Architecture reviews; designing consensus, replication, ordering.

**Sections**:
- Consensus (Raft vs Multi-Paxos vs EPaxos vs Zab) comparison
- Time (Physical vs Logical vs Hybrid; NTP drift impacts)
- Conflict Resolution (CRDT types, LWW pitfalls, vector clocks)
- Idempotency Strategies (Tokens, version checks, upserts)
- Delivery Semantics (At-most, At-least, Effectively-once myth)
- Backpressure Patterns (Token bucket, leaky bucket, credit-based)
- Data Partitioning & Rebalancing
- Hot Key Mitigation (Sharding, randomization, two-level caches)

**Mini Exemplar**: Conflict-free counter CRDT (G-Counter) merge: element-wise max + sum.

**Pitfalls**: Over-complicating with consensus when monotonic buckets suffice; ignoring clock skew.
**Metrics**: Incident rate due to ordering / duplication; replication lag distribution.
**Expansion**: Add design exercises: global counter, chat ordering, multi-region write path.
**Example Resources**:
- https://raft.github.io/ (Raft paper & visualization)
- https://aws.amazon.com/builders-library/ (Distributed system patterns)
- https://jepsen.io/analyses (Consistency anomaly analyses)
- https://aphyr.com/tags/jepsen (Partition testing lessons)
- https://muratbuffalo.blogspot.com/ (Research paper summaries)

---
## 8. Incident Postmortem Library
**Purpose**: Institutionalize learning; reduce recurrence probability.
**When to Use**: Sev1/2 incidents; repeated pattern lower severity.

**Template**:
- Summary (1 line customer impact)
- Timeline (UTC; detection → mitigation → resolution)
- Impact Metrics (Duration, Affected % requests)
- Root Cause (5-Whys / Causal Tree)
- Contributing Factors (Technical / Process / Org)
- Detection Gaps
- What Went Well
- Corrective Actions (Short / Long Term w/ owners & due dates)
- Prevent Recurrence Checklist

**Exemplar**:
- Incident: Cache stampede causing DB CPU 95% for 18m; 8% request latency breach.
- Root Cause: Simultaneous key expiry → thundering herd.
- Fix: Added single-flight + jittered TTL.

**Pitfalls**: Blameful tone; vague actions; missing deadlines.
**Metrics**: Action item closure rate; MTTR trend; recurrence rate.
**Expansion**: Pattern taxonomy (config error, capacity mis-estimate, code deploy, dependency outage).
**Example Resources**:
- https://sre.google/sre-book/postmortem-culture/ (Postmortem culture)
- https://github.com/dastergon/awesome-sre#post-mortems (Public incident repo list)
- https://blog.pragmaticengineer.com/ (Incident & reliability essays)
- https://aws.amazon.com/builders-library/ (Operational lessons)
- https://landing.google.com/sre/resources/ (SRE resources)

---
## 9. Influence & Stakeholder Toolkit
**Purpose**: Drive alignment and decisions across teams & exec layer.
**When to Use**: Cross-org proposals, escalations, roadmap negotiations.

**Template Components**:
- Stakeholder Map (Influence vs Interest grid)
- Narrative Outline (Problem → Impact → Options → Recommendation → Ask)
- Objection Handling Table (Objection / Reframe / Evidence)
- Escalation Ladder & Timing Triggers

**Exemplar**:
- Proposal: Introduce centralized schema registry.
- Options: Status quo, per-team libs, managed registry.
- Objection: Adds latency → Reframe: amortized vs inconsistent schema risk.

**Pitfalls**: Data-free assertions; skipping pre-alignment 1:1s.
**Metrics**: Proposal acceptance rate; decision latency; # escalations avoided via pre-alignment.
**Expansion**: Influence retro doc per major decision.
**Example Resources**:
- https://randsinrepose.com/ (Leadership & influence)
- https://larahogan.me/blog/ (Stakeholder management)
- https://leaddev.com/ (Staff+ leadership articles)
- https://martinfowler.com/articles/ (Architecture & communication)
- https://hbr.org/ (Executive influence frameworks)

---
## 10. Behavioral STAR Story Bank
**Purpose**: Ready library of high-signal leadership & impact stories.
**When to Use**: Behavioral interviews, performance reviews, mentoring.

**Template**:
- Category
- Situation (Context + Challenge + Scale)
- Task (Objective / Constraints)
- Action (Multi-threaded steps, influencing tactics)
- Result (Quantified outcomes + Secondary benefits)
- Reflection (Lesson / Reusable play)
- Competencies (Leadership Principles / Values)

**Exemplar 1 (Operational Excellence)**:
- Situation: 20% oncall pages noise; dev burnout.
- Action: Built alert taxonomy, SLO gating, error budget review cadence.
- Result: Pages -65%, MTTR -30%, retention improved.

**Exemplar 2 (Strategic Bet)**:
- Situation: Latency complaints APAC; revenue churn risk.
- Action: Led multi-region read replica project.
- Result: P95 -40%; churn risk mitigated; upsell +4%.

**Pitfalls**: Missing quant metrics; overlong chronology.
**Metrics**: # curated stories (target ≥25) with quant impact.
**Expansion**: Map stories to evaluation rubrics; add negative/learned stories.
**Example Resources**:
- https://thepragmaticengineer.com/ (Career impact narratives)
- https://leaddev.com/ (Leadership case studies)
- https://stackoverflow.blog/ (Team & collaboration stories)
- https://www.amazon.jobs/en/principles (Leadership principles reference)
- https://hbr.org/topic/leadership (Leadership behaviors)

---
## 11. Roadmap & Vision Document
**Purpose**: Communicate multi-quarter technical direction linking strategy & execution.
**When to Use**: Annual / semi-annual planning, exec review.

**Template**:
- Vision Statement (North Star)
- Strategic Pillars (3–5)
- Current State Gaps
- Pillar Initiatives (Objective, KPI, Timeline, Dependencies)
- Risk Register
- Resource / Capacity Allocation Table
- De-scope Principles
- KPI Dashboard Mock

**Exemplar**:
- Pillar: Reliability → Goal: 99.95% 12-month rolling availability; Initiatives: SLO adoption, chaos program, multi-region.

**Pitfalls**: Feature list vs outcome; ignoring capacity reality.
**Metrics**: Initiative on-time %; KPI delta vs baseline.
**Expansion**: Add scenario planning (best/base/worst).
**Example Resources**:
- https://gitlab.com/gitlab-org/gitlab/-/blob/master/doc/ direction (Public strategy examples)
- https://productstrategy.co/ (Strategy frameworks)
- https://a16z.com/ (Market & strategic tech essays)
- https://martinfowler.com/bliki/EvolutionaryArchitecture.html (Evolution & vision)
- https://stratechery.com/ (Macro strategy context)

---
## 12. Design Review Checklist (Staff Lens)
**Purpose**: Ensure robust, evolvable designs; surface hidden risks early.
**When to Use**: Pre-implementation review; major refactors.

**Sections & Sample Probing Questions**:
- Clarity: Is problem + success metric explicit?
- Correctness: Edge cases enumerated? Failure modes?
- Reliability: Degradation strategy documented?
- Scalability: Growth factor beyond 10× capacity addressed?
- Evolvability: Clear extension seams? Dependency boundaries?
- Security/Privacy: Data classification handled?
- Observability: SLIs & instrumentation plan?
- Cost: Run vs build cost analysis?
- Risk: Top 3 unknowns & experiments?

**Pitfalls**: Reviewing solution absent explicit constraints; rubber stamp sessions.
**Metrics**: Review action item cycle time; defect escape rate.
**Expansion**: Automate pre-review checklist form.
**Example Resources**:
- https://martinfowler.com/architecture/ (Design principles)
- https://aws.amazon.com/architecture/well-architected/ (Pillars for review)
- https://google.github.io/eng-practices/review/ (Code review guidance)
- https://increment.com/software-architecture/ (Architecture articles)
- https://microsoft.github.io/code-with-engineering-playbook/ (Design practices)

---
## 13. Production Readiness Gate
**Purpose**: Guardrail to launch only operationally prepared services/features.
**When to Use**: Pre-launch / major change freeze.

**Template**:
- Functional Test Coverage Summary
- SLOs & Alerts Configured
- Runbooks & Oncall Training Completed
- Load Test Report (Peak + Stress + Soak)
- Deployment Strategy (Canary/Blue-Green)
- Rollback Procedure Verified
- Security Review Sign-off
- Data Migration Plan & Dry Run Results
- Observability Dashboard Links
- Compliance (PII, retention) checklist

**Pitfalls**: Box-ticking without artifact evidence; skipping rollback drill.
**Metrics**: Post-launch incidents per launch; rollback speed.
**Expansion**: Add automated PR readiness scoring.
**Example Resources**:
- https://sre.google/workbook/ (Operational readiness)
- https://aws.amazon.com/builders-library/ (Readiness patterns)
- https://grafana.com/blog/ (Dashboards & observability setup)
- https://launchdarkly.com/blog/ (Release & feature flag practices)
- https://gremlin.com/blog/ (Resilience & chaos experiments)

---
## 14. Multi-Region / DR Playbook
**Purpose**: Achieve defined RTO/RPO; structured failover & failback.
**When to Use**: Region outage simulation, compliance, resilience reviews.

**Template**:
- Critical Services Inventory & Tier
- RTO/RPO Matrix
- Replication Strategy (DB, Object Store, Cache)
- Failover Triggers & Decision Authority
- Failover Runbook (Checklist with timing)
- Data Consistency Validation Steps
- Failback Plan
- Test Schedule & Last Drill Results

**Exemplar**:
- RTO 15m; RPO 5m for Checkout.
- Drill: Simulated region isolation; failover completed 11m; replication lag 80ms.

**Pitfalls**: Missing failback; untested manual DNS changes.
**Metrics**: Drill success rate; Actual vs target RTO.
**Expansion**: Automated chaos region kill experiments.
**Example Resources**:
- https://aws.amazon.com/architecture/ (Multi-region whitepapers)
- https://cloud.google.com/architecture/framework/resiliency (Resiliency patterns)
- https://azure.microsoft.com/en-us/blog/topics/architecture/ (HA & DR posts)
- https://netflixtechblog.com/ (Chaos & multi-region operations)
- https://gremlin.com/ (Chaos engineering drills)

---
## 15. Mentorship & Leveling Matrix
**Purpose**: Guide growth; articulate Staff expectations; plan delegation.
**When to Use**: Performance cycles, mentoring sessions, career planning.

**Matrix Axes**:
- System Scope
- Technical Depth
- Operational Excellence
- Execution Predictability
- Influence / Cross-Team Impact
- Talent Development

**Template Entry**:
- Level Descriptor
- Observable Behaviors
- Evidence Examples
- Growth Recommendations

**Exemplar (Staff vs Senior)**:
- Staff Influence: Proactively aligns 3+ teams on platform strategy; Senior: Optimizes within team.

**Pitfalls**: Vague descriptors; bias toward heroic effort.
**Metrics**: Promotion readiness clarity scores; mentorship pairing coverage.
**Expansion**: Add rubric scoring sheet.
**Example Resources**:
- https://staffeng.com/ (Staff+ role interviews)
- https://leaddev.com/ (Mentoring & leveling articles)
- https://randsinrepose.com/ (Career frameworks)
- https://github.com/ladyleet/ladder (Engineering ladder examples)
- https://opensource.google/documentation/eng-practices/ (Practice guidelines)

---
## 16. Observability Maturity Ladder
**Purpose**: Structured path from basic metrics to predictive insights.
**When to Use**: Quarterly platform health review; tool investment decisions.

**Levels**:
1. Basic: Uptime + coarse logs
2. Instrumented: RED + USE metrics, tracing enabled
3. Proactive: SLO dashboards, anomaly detection, structured logs
4. Predictive: ML-based forecasts, adaptive sampling, auto-remediation hooks

**Template (Per Level)**:
- Capabilities
- Required Tooling
- Policies / Standards
- Gaps to Next Level

**Exemplar** (Advancing 2→3): Add burn rate alerts, trace sampling adjustments, structured log schema adoption.

**Pitfalls**: Metric cardinality explosion; tracing cost runaway.
**Metrics**: MTTR trend; Alert precision (% actionable); Cardinality budget adherence.
**Expansion**: Add cost efficiency dimension.
**Example Resources**:
- https://opentelemetry.io/docs/ (Instrumentation standards)
- https://grafana.com/ (Visualization & metrics)
- https://honeycomb.io/blog (High-cardinality observability)
- https://lightstep.com/blog/ (Tracing practices)
- https://signoz.io/blog/ (Open-source observability)

---
## 17. AI / ML Integration Patterns
**Purpose**: Standardize safe, reliable ML feature integration.
**When to Use**: Adding personalization, recommendations, anomaly detection, LLM augmentation.

**Patterns**:
- Retrieval-Augmented Generation (RAG)
- Feature Store & Online/Offline Parity
- Model Version Canary & Shadow Deployment
- Drift Detection (Data, Performance)
- Evaluation Harness (Offline metrics vs Online A/B)
- Safety Filtering & PII Redaction

**Template (Per Pattern)**:
- Context & Use Cases
- Architecture Sketch (Components)
- Data Lifecycle (Ingest → Feature → Inference → Feedback)
- Risks & Mitigations
- Metrics (Latency, Accuracy, Drift, Cost / QPS)

**Exemplar (Shadow Deploy)**:
- Duplicate inference path; log predictions; compare vs production model; no user impact; promote after stability & uplift > baseline.

**Pitfalls**: Training-serving skew; ungoverned prompt changes; missing rollback of model versions.
**Metrics**: Offline→Online delta; Drift detection latency; Model rollback frequency.
**Expansion**: Reinforcement learning loop; prompt version registry.
**Example Resources**:
- https://mlsys.org/ (Systems for ML research)
- https://arxiv.org/ (Model versioning & deployment papers)
- https://huggingface.co/docs (Model & inference patterns)
- https://featurestore.org/ (Feature store concepts)
- https://landing.google.com/sre/resources/practicesandprocesses/ (ML reliability practices)

---
## 18. Compliance & Audit Toolkit
**Purpose**: Ensure regulatory adherence & auditable trail for sensitive operations.
**When to Use**: Handling PII/financial data, audits, SOC2/GDPR prep.

**Template**:
- Data Inventory & Classification
- Access Control Matrix (Role → Resources → Justification)
- Retention & Deletion Schedule
- Audit Log Spec (Who, What, When, Where, Why, Correlation ID)
- Consent & Preference Model
- Change Management Evidence Checklist

**Exemplar**:
- Audit Log Field Set: event_id, actor_id, actor_role, action, resource_type, resource_id, ip, user_agent, timestamp_iso, trace_id, result, reason_code.

**Pitfalls**: Logging secrets; unindexed logs; missing correlation IDs.
**Metrics**: % sensitive tables with access reviews; Deletion SLA compliance; Audit finding count.
**Expansion**: Automated policy-as-code for access reviews.
**Example Resources**:
- https://cloudsecurityalliance.org/ (Cloud compliance frameworks)
- https://owasp.org/www-project/asvs/ (App security verification)
- https://gdpr.eu/ (GDPR reference)
- https://www.isaca.org/resources (Audit & governance)
- https://pcisecuritystandards.org/ (PCI DSS if payments)

---
## 19. Interview Simulation Matrix
**Purpose**: Structured rehearsal; coverage across Staff competencies.
**When to Use**: Weekly mocks; final month prep; gap targeting.

**Matrix Dimensions**:
- Panel Types: Coding, System Design, Architecture Deep Dive, Behavioral, Leadership, Cross-Functional, Bar-Raiser
- For Each: Goal, Sample Questions, Evaluation Signals, High-Signal Follow-ups, Common Failure Modes.

**Exemplar (Architecture Deep Dive)**:
- Goal: Depth in decisions, risk trade-offs.
- Signals: Clear mental model, failure modes, cost/perf trade-offs, evolution plan.
- Failure Mode: Premature solutioning, no metrics.

**Pitfalls**: Practicing only design breadth; ignoring behavioral follow-ups.
**Metrics**: Mock pass rate trend; Panel coverage heatmap.
**Expansion**: Add video self-review rubric.
**Example Resources**:
- https://interviewing.io/ (Interview practice insights)
- https://staffeng.com/guides/interview/ (Staff interview experiences)
- https://leetcode.com/discuss/interview-question-system-design/ (Design Q bank)
- https://github.com/donnemartin/system-design-primer (System design study)
- https://thepragmaticengineer.com/ (Interview strategy articles)

---
## 20. Decision Economics Framework
**Purpose**: Rationalize build vs buy vs OSS vs partner decisions.
**When to Use**: Platform investment proposals, vendor evaluations.

**Template**:
- Problem Definition & Outcome Target
- Options Table (Option, Time-to-Value, TCO 1/3 Year, Strategic Differentiation Score, Risk Profile, Exit Cost)
- Weighted Scoring Matrix (Criteria × Weight × Score)
- Sensitivity Analysis (Traffic, Cost variability)
- Recommendation & Assumptions
- Re-evaluation Trigger (Metric threshold/date)

**Exemplar**:
- Options: Build feature flag service vs Buy SaaS vs Extend existing config.
- Weighted Result: Buy (score 8.2) vs Build (6.9) vs Extend (5.3). Chosen Buy; Revisit if monthly cost >$15k or SLA breach 2x.

**Pitfalls**: Inflated differentiation narrative; ignoring exit cost.
**Metrics**: Realized vs projected TCO variance; Decision cycle time.
**Expansion**: Portfolio view of strategic vs commodity allocations.
**Example Resources**:
- https://martinfowler.com/articles/buynotbuild.html (Buy vs build thinking)
- https://aws.amazon.com/economics/ (Cloud economics)
- https://queue.acm.org/ (Architecture & trade-off essays)
- https://blog.pragmaticengineer.com/ (Cost & platform strategy)
- https://a16z.com/ (Strategic technology investment theses)

---
## Integrated 4-Month Cadence
**Month 1 (Foundations)**: ADR baseline, 4 SLOs, security & threat model, design review checklist adoption, decision economics pilot.
**Month 2 (Scale & Reliability)**: Capacity model v1, migration blueprint set (3), DR drill, observability level-up, 5 performance cases.
**Month 3 (Leadership & Influence)**: Vision doc v1, influence toolkit roll-out, 15 STAR stories curated, mentorship matrix published, 2 postmortems simulated.
**Month 4 (Advanced & Readiness)**: Distributed systems deep dives, AI patterns doc, compliance toolkit gaps closed, interview matrix full, refine artifacts & run full mock loops.

**Weekly Rhythm**:
- Mon: Artifact creation/refinement
- Tue: Technical depth drill (perf/GC/DS)
- Wed: Mock design + retro
- Thu: Behavioral & influence practice
- Fri: Incident or migration teardown + metrics audit

**KPIs**:
- 12+ ADRs; 3 migration walkthroughs; 4 postmortem sims; ≥2 mock design panels/month; ≥25 STAR stories; ≥10 perf cases.

---
## Usage Guide
1. Start with ADR & SLO templates → institutionalize decision & reliability baseline.
2. Parallel build STAR bank weekly (avoid last-minute scramble).
3. Every new significant architecture: fill Decision Economics + ADR simultaneously.
4. Monthly: Review capacity & SLO burn trends; adjust roadmap pillar risks.
5. Last 6 weeks: Increase mock frequency; run at least 2 multi-interview simulation days.

## Expansion Backlog
- Add Automated Template Generators (scripts) for ADR & Postmortem.
- Introduce Metrics Scorecard dashboard.
- Link each STAR story to competency map & evidence tag cloud.
- Build prompt library for each pattern with variant difficulty.

---
## Quick Index
1 ADRs | 2 Capacity | 3 SLO | 4 Migration | 5 Threat/Privacy | 6 Performance | 7 Distributed Primer | 8 Postmortems | 9 Influence | 10 STAR Bank | 11 Roadmap | 12 Design Review | 13 Prod Readiness | 14 DR Playbook | 15 Mentorship | 16 Observability | 17 AI Patterns | 18 Compliance | 19 Interview Matrix | 20 Decision Economics

---
Prepared for iterative enrichment—select any section to expand to deep dive annexes (e.g., add full CRDT lab, GC tuning compendium) as next step.
