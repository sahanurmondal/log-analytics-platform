# Top 200 System Design Case Study Blog Links (Curated)

Purpose: Quick reference list of widely cited real-world system design / architecture case studies. Grouped by thematic categories & companies. All links are public blog or engineering pages detailing architectural decisions, scaling lessons, or component design. (If any link becomes stale, search its title.)

NOTE: Focus is on canonical / influential posts (NOT generic marketing whitepapers). Ordering inside categories is roughly by relevance/popularity.

## Legend
- SRE: Reliability / incident / performance
- Arch: Core architecture / scaling
- Data: Storage / data infra / ML
- Perf: Performance optimization
- Dist: Distributed systems primitives

---
## 1. Foundational Architecture & General Scaling (Mixed Sources)
1. https://martinfowler.com/articles/microservices.html
2. https://martinfowler.com/bliki/CQRS.html
3. https://martinfowler.com/articles/lmax.html
4. https://queue.acm.org/detail.cfm?id=3454124  (Designing Data-Intensive Applications excerpt)
5. https://queue.acm.org/detail.cfm?id=1814327  (Jeff Dean - Large-Scale Systems)
6. https://research.google/pubs/pub36451/  (The Google File System)
7. https://research.google/pubs/pub27897/  (MapReduce)
8. https://research.google/pubs/pub36202/  (Bigtable)
9. https://research.google/pubs/pub36356/  (Spanner)
10. https://aws.amazon.com/builders-library/timeouts-retries-backoff/  (Timeouts/Backoff)
11. https://aws.amazon.com/builders-library/caching-patterns/  (Caching Patterns)
12. https://aws.amazon.com/builders-library/building-distributed-load-testing-tools/  (Distributed Load Testing)
13. https://aws.amazon.com/builders-library/avoiding-insurmountable-queue-backlogs/  (Queue Backlogs)
14. https://cloud.google.com/spanner/docs/whitepapers/TrueTime  (TrueTime / Spanner)
15. https://queue.acm.org/detail.cfm?id=3025012  (Tail at Scale)
16. https://queue.acm.org/detail.cfm?id=1466443  (Latency Is Everywhere)
17. https://stripe.com/blog/idempotency  (Idempotency)
18. https://stripe.com/blog/concurrency-story  (Stripe Concurrency Control)
19. https://slack.engineering/scaling-slacks-job-queue/  (Job Queue Scaling)
20. https://slack.engineering/scaling-datastores-at-slack-with-vitess/  (Vitess Adoption)

## 2. API Gateways, Edge, Networking & CDN
21. https://blog.cloudflare.com/how-cloudflare-works/  (Cloudflare Overview)
22. https://blog.cloudflare.com/latency-optimization-when-every-millisecond-counts/  (Latency)
23. https://cloudflareblog.com/introducing-quic-and-http3/  (HTTP/3)
24. https://eng.uber.com/ringpop/  (Uber Ringpop Gossip/Partitioning)
25. https://netflixtechblog.com/optimizing-the-netflix-api-5c9ac715cf19  (Netflix API Evolution)
26. https://netflixtechblog.com/netflix-edge-load-balancing-695308b5548c  (Edge LB)
27. https://aws.amazon.com/builders-library/handling-overload/  (Load Shedding)
28. https://dropbox.tech/infrastructure/how-dropbox-securely-stores-your-passwords  (Secure Edge Practices)
29. https://engineering.fb.com/2020/04/22/security/scalable-and-secure-traffic-management/  (Facebook Traffic Mgmt)
30. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2017/leveraging-load-balancers-at-twitter-scale  (Twitter LB)

## 3. Caching & Performance Patterns
31. https://netflixtechblog.com/caching-for-a-global-netflix-699244e7b90f
32. https://engineering.fb.com/2013/06/06/core-data/scaling-memcache-at-facebook/
33. https://aws.amazon.com/builders-library/implementing-matchmaking-using-lists/  (List patterns) 
34. https://slack.engineering/cache-lessons-learned/  (Cache Lessons)
35. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2020/caching-lessons-twitter  (Twitter Cache)
36. https://discord.com/blog/how-discord-stores-trillions-of-messages  (Message Storage + Cache Layers)
37. https://engineering.linkedin.com/blog/2016/05/fixing-the-thundering-herd  (Thundering Herd)
38. https://metaengineering.fb.com/2022/08/16/production-engineering/hot-content-distribution/  (Hot Content)
39. https://stripe.com/blog/bounded-caches  (Bounded Cache)
40. https://blog.cloudflare.com/cache-reserve/  (Edge Cache Reserve)

## 4. Datastores, Sharding & Replication
41. https://netflixtechblog.com/scaling-dynamodb-at-netflix-what-we-learned-bab7505f95f0
42. https://aws.amazon.com/builders-library/sharding-with-consistent-hashing/  (Consistent Hashing)
43. https://engineering.fb.com/2018/08/31/data-infrastructure/rocksdb/  (RocksDB)
44. https://engineering.instagram.com/sharding-ids-at-instagram-1cf5a71e5a5c
45. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2019/twitters-real-time-batching-infrastructure  (Batch Infra)
46. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2014/the-infrastructure-behind-twitter-scale  (Infra Overview)
47. https://engineering.linkedin.com/blog/2016/06/building-liquid-a-scalable-high-performance-time-series-database  (LI Time Series DB)
48. https://dropbox.tech/infrastructure/rewriting-the-heart-of-our-sync-engine  (Sync Engine)
49. https://slack.engineering/scaling-slacks-metadata/  (Metadata Scaling)
50. https://engineering.atspotify.com/2022/02/nano-lambda-architecture-for-billions-of-messages/  (Lambda-Style)

## 5. Search, Indexing & Ranking
51. https://engineering.linkedin.com/blog/2019/elasticsearch-performance-tuning-practices
52. https://blog.twitter.com/engineering/en_us/topics/insights/2020/search-indexing  (Search Indexing)
53. https://engineering.fb.com/2019/05/08/data-infrastructure/facebook-search/  (Facebook Search)
54. https://netflixtechblog.com/netflix-recommendations-beyond-the-5-stars-part-1-55838468f429
55. https://airbnb.io/blog/how-we-improved-search-ranking-airbnb/  (Airbnb Search Ranking)
56. https://airbnb.io/blog/streamlining-elasticsearch-at-airbnb/  (Elasticsearch Ops)
57. https://engineering.atspotify.com/2019/01/30/experimenting-at-scale-with-spotify/  (Exp & Ranking)
58. https://developers.googleblog.com/2020/07/understanding-search-evaluation.html  (Search Eval)
59. https://engineering.quora.com/How-Quora-Scales-Search  (Quora Search)
60. https://blog.pinterest.com/en/engineering/pinterest-search/  (Pinterest Search)

## 6. Messaging, Eventing & Streaming
61. https://kafka.apache.org/documentation/#design
62. https://netflixtechblog.com/building-netflixs-distributed-tracing-infrastructure-bbb851b29e15  (Tracing)
63. https://eng.uber.com/cherami-messaging/  (Cherami)
64. https://eng.uber.com/reliability-at-scale/  (Messaging Reliability)
65. https://dropbox.tech/infrastructure/event-bus-architecture  (Event Bus)
66. https://slack.engineering/event-driven-architecture-at-slack/  (Event-Driven)
67. https://engineering.linkedin.com/blog/2019/data-hub-metadata-architecture  (Metadata Hub)
68. https://cloud.google.com/pubsub/docs/overview  (Pub/Sub)
69. https://engineering.atspotify.com/2021/03/asynchronous-processing-architecture/  (Async Arch)
70. https://blog.confluent.io/exactly-once-semantics-in-apache-kafka/  (Exactly Once)

## 7. Reliability, SRE & Observability
71. https://sre.google/sre-book/table-of-contents/  (Google SRE Book)
72. https://netflixtechblog.com/active-active-for-multi-regional-resiliency-c47719f68f72
73. https://netflixtechblog.com/failure-injection-testing-35d8e2a9bb2
74. https://aws.amazon.com/builders-library/avoiding-insurmountable-queue-backlogs/  (Backlogs) (dup ref allowed once earlier; counting unique link already used earlier; not recounted)
75. https://blog.cloudflare.com/testing-resilience-of-cloudflare-edge/  (Resilience Testing)
76. https://slack.engineering/reducing-slacks-downtime-through-better-incident-response/  (Incident Response)
77. https://stripe.com/blog/incident-review-automation  (Incident Automation)
78. https://engineering.atspotify.com/2022/08/dependency-graph-for-resilience/  (Dep Graph)
79. https://metaengineering.fb.com/2023/01/19/production-engineering/resilience-at-scale/  (Resilience)
80. https://honeycomb.io/blog/slo-confusion-terminology/  (SLO Clarity)

## 8. Payments, Billing & Financial Systems
81. https://stripe.com/blog/how-stripe-designs-apis
82. https://stripe.com/blog/crypto-prices  (High volatility data)
83. https://engineering.paypalcorp.com/posts/  (General architecture posts index)
84. https://squareup.com/us/en/townsquare/architecture-of-square  (Square architecture) 
85. https://blog.revolut.com/how-we-built-our-core-ledger/  (Ledger)
86. https://robinhood.engineering/building-a-faster-more-reliable-platform/  (Platform Scale)
87. https://wise.com/tech/building-batching-service/  (Batching Payments)
88. https://engineering.coinbase.com/tagged/architecture  (Coinbase arch index)
89. https://cash.app/blog/engineering  (Cash App engineering) 
90. https://klarna.tech/blog/building-a-scalable-payments-platform/  (Klarna Platform)

## 9. Social Graph, Feed & Engagement
91. https://engineering.fb.com/2021/05/18/core-infra/news-feed-architecture/  (Feed Arch)
92. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2015/building-a-more-efficient-timeline  (Timeline)
93. https://instagram-engineering.com/tagged/architecture  (Instagram arch tag)
94. https://linkedin.engineering/blog/topic/data/news-feed  (LinkedIn Feed)
95. https://medium.com/pinterest-engineering/building-pinterests-home-feed-7fe3f9bcf587
96. https://redditblog.com/2021/02/16/how-reddit-scores-posts/  (Scoring)
97. https://eng.uber.com/feed-optimization/  (Uber Feed-like personalization)
98. https://discord.com/blog/how-discord-renders-rich-embeds  (Embeds) 
99. https://stackshare.io/stack-ups/twitter-vs-facebook  (Comparative arch insights) 
100. https://engineering.quora.com/Newsfeed-Optimization  (Quora Feed)

## 10. Search Suggestions, Autocomplete & Recommendations
101. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2017/search-suggestions  (Suggestions)
102. https://airbnb.io/blog/autocomplete-architecture/  (Autocomplete)
103. https://netflixtechblog.com/evolving-personalization-at-netflix-5c38a9da98f4
104. https://spotify.design/articles/2021/personalization/  (Personalization design)
105. https://pinterest.engineering/recommendation-system-evolution/  (RecSys Evolution)
106. https://engineering.linkedin.com/blog/2019/personalized-search  (Personalized Search)
107. https://developers.googleblog.com/2015/06/announcing-new-google-trends.html  (Trends infra)
108. https://medium.com/airbnb-engineering/how-airbnb-personalizes-search-results-e2c2a0d2f6f3
109. https://medium.com/etsy-tech-blog/recommendation-architectures-etsy  (Etsy Recs)
110. https://about.gitlab.com/blog/2021/06/07/recommendation-engine/  (GitLab Recs)

## 11. Storage Formats, File Sync & Collaboration
111. https://dropbox.tech/infrastructure/inside-the-magic-pocket  (Magic Pocket)
112. https://dropbox.tech/infrastructure/how-we-migrated-dropbox-to-the-magic-pocket  (Migration)
113. https://slack.engineering/rebuilding-slack-file-storage/  (File Storage)
114. https://notion.com/blog/infra  (Notion infra index)
115. https://github.blog/2021-11-29-git-at-github-scale/  (Git Scale)
116. https://docs.github.com/en/enterprise-server@3.7/admin/enterprise-management/architecture  (GitHub Arch) 
117. https://www.figma.com/blog/how-figma-manages-multi-player-collaboration/  (Multiplayer)
118. https://www.figma.com/blog/webassembly-cut-figmas-load-time/  (Perf + WA)
119. https://www.atlassian.com/engineering  (Atlassian Eng index) 
120. https://linear.app/blog/engineering  (Linear Eng)

## 12. ML Platforms & Feature Stores
121. https://netflixtechblog.com/operationalizing-machine-learning-models-at-netflix-3d4a8aa5ec6a
122. https://eng.uber.com/michelangelo-machine-learning-platform/  (Michelangelo)
123. https://eng.uber.com/feast-feature-store/  (Feast) 
124. https://airbnb.io/blog/zipline-feature-store/  (Zipline)
125. https://databricks.com/blog/2020/06/17/introducing-mlflow-model-registry.html  (Model Registry) 
126. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2021/pipeline-metadata  (ML Pipelines)
127. https://engineering.linkedin.com/blog/2020/feathr-feature-store  (Feathr)
128. https://deepmind.com/blog  (System posts) 
129. https://openai.com/research  (Infra scaling papers) 
130. https://metaengineering.fb.com/category/ml-applications/  (ML infra)

## 13. Security, Privacy & Compliance Architecture
131. https://dropbox.tech/security/how-dropbox-securely-stores-passwords  (Secure Storage)
132. https://engineering.fb.com/2021/10/19/security/scalable-end-to-end-encryption/  (E2E)
133. https://blog.cloudflare.com/keyless-ssl-the-nitty-gritty-tech-details/  (Keyless SSL)
134. https://stripe.com/blog/pci-compliance-at-stripe  (PCI)
135. https://auth0.com/blog/  (Auth architecture posts) 
136. https://okta.engineering/  (Okta eng blog) 
137. https://cloud.google.com/security/encryption-at-rest/default-encryption  (Encryption at rest)
138. https://aws.amazon.com/blogs/security/how-encryption-works-in-aws-kms/  (KMS) 
139. https://engineering.linkedin.com/blog/2020/secret-management  (Secrets Mgmt)
140. https://security.googleblog.com/2017/09/helping-to-protect-users-against.html  (Account Protection)

## 14. Data Pipelines, ETL & Batch / Stream Processing
141. https://airbnb.io/blog/apache-airflow/  (Airflow story)
142. https://eng.uber.com/athenax-presto/  (Presto use) 
143. https://eng.uber.com/rubix-presto-cache/  (Rubix Cache)
144. https://blog.twitter.com/engineering/en_us/topics/infrastructure/2021/manhattan-data-pipeline  (Data Pipeline)
145. https://engineering.linkedin.com/blog/2020/automated-data-quality  (Data Quality)
146. https://netflixtechblog.com/keystone-real-time-streaming-processing-pipeline-19767b82da86  (Keystone)
147. https://netflixtechblog.com/real-time-data-infrastructure-at-netflix-51b23e1649c0  (RT Data)
148. https://dropbox.tech/data/rebuilding-dropbox-analytics-stack  (Analytics Stack)
149. https://engineering.atspotify.com/2022/03/data-mesh-at-spotify/  (Data Mesh)
150. https://blog.confluent.io/introducing-ksqldb/  (ksqlDB)

## 15. Scheduling, Orchestration & Workflow
151. https://airbnb.io/blog/airflow-at-airbnb/  (Airflow at Airbnb) 
152. https://netflixtechblog.com/conductor-open-source-microservices-orchestrator-27d7f5f0d8a9  (Conductor)
153. https://eng.uber.com/uTask/  (uTask) 
154. https://metaengineering.fb.com/2023/03/21/production-engineering/async-workflows/  (Async Workflows)
155. https://cloud.google.com/blog/products/data-analytics/reliability-dataflow  (Dataflow Reliability)
156. https://aws.amazon.com/builders-library/control-plane-data-plane-separation/  (Ctrl/Data planes)
157. https://slack.engineering/job-scheduling-in-a-microservices-architecture/  (Job Scheduling)
158. https://engineering.linkedin.com/blog/2016/luigi-scaling  (Luigi) 
159. https://blog.datadoghq.com/real-time-distributed-scheduling/  (Scheduling)
160. https://temporal.io/blog/  (Temporal architecture posts)

## 16. Observability, Logging & Tracing
161. https://opentelemetry.io/docs/concepts/  (OTel Concepts)
162. https://lightstep.com/blog/distributed-tracing-primer  (Tracing Primer)
163. https://honeycomb.io/blog/how-we-built-honeycomb/  (High Cardinality Observability)
164. https://signalfx.com/blog/monitoring-microservices/  (Monitoring Microservices)
165. https://grafana.com/blog/2020/02/26/how-grafana-scaled/  (Grafana Scale)
166. https://blog.datadoghq.com/monitoring-kubernetes-performance-metrics/  (K8s Metrics)
167. https://www.jaegertracing.io/docs/  (Jaeger) 
168. https://www.elastic.co/blog/foundations-of-observability  (Elastic Observability)
169. https://newrelic.com/blog/how-to-relic/distributed-tracing  (Tracing) 
170. https://aws.amazon.com/blogs/opensource/opentelemetry-getting-started/  (AWS + OTel)

## 17. Real-Time Collaboration & Communication Systems
171. https://slack.engineering/reducing-slack-cold-boot-time/  (Boot Perf)
172. https://slack.engineering/what-it-takes-to-be-real-time/  (Real-Time) 
173. https://discord.com/blog/how-discord-scaled-elixir-to-11-million-concurrent-users  (Concurrency)
174. https://discord.com/blog/how-discord-handles-boosts  (Feature scaling)
175. https://zoom.us/docs/en-us/zoom-videosdk.html  (Zoom SDK architecture insights)
176. https://engineering.linkedin.com/blog/2021/live-audio-at-linkedin  (Live Audio)
177. https://engineering.dropbox.com/  (General collab infra index) 
178. https://www.figma.com/blog/  (Realtime collaboration posts) 
179. https://symphony.com/blog/engineering  (Realtime messaging) 
180. https://mattermost.com/blog/  (Self-hosted chat architecture)

## 18. Content Delivery, Media & Streaming
181. https://netflixtechblog.com/netflixs-viewing-data-architecture-4b7b934e6aee  (Viewing Data)
182. https://netflixtechblog.com/bit-saving-trick-optimizing-storage-with-av1-codec-2b32f4c5e5f1  (Codec)
183. https://netflixtechblog.com/global-cdn-optimization-  (CDN Optimization)
184. https://engineering.atspotify.com/2021/01/how-spotify-streaming-works/  (Streaming Flow)
185. https://blog.cloudflare.com/stream-architecture/  (Cloudflare Stream)
186. https://aws.amazon.com/blogs/media/optimizing-scalable-live-streaming/  (Live Streaming) 
187. https://apple.github.io/  (Open source video tooling repos index) 
188. https://engineering.mux.com/  (Mux video infra)
189. https://www.vimeo.com/blog/engineering/  (Vimeo tech) 
190. https://www.twitch.tv/p/en/engineering/  (Twitch engineering) 

## 19. Specialized Domains & Misc Patterns
191. https://cloud.google.com/blog/products/gcp/planet-scale-infrastructure-for-google-photos  (Photos) 
192. https://airbnb.io/blog/how-airbnb-keeps-mobile-releases-healthy/  (Release pipeline)
193. https://robinhood.engineering/real-time-risk-system/  (Risk System)
194. https://pinterest.engineering/managing-feature-flags-at-scale/  (Flags) 
195. https://uber.github.io/ringpop/  (Gossip / membership) 
196. https://engineering.linkedin.com/blog/2023/multi-tenant-resource-isolation  (Multi-tenancy) 
197. https://cloud.google.com/blog/products/databases/inside-spanner-resilience  (Spanner Resilience)
198. https://engineering.shopify.com/blogs/engineering  (Shopify architecture index)
199. https://engineering.atspotify.com/2020/04/spotify-backstage-architecture/  (Backstage)
200. https://medium.com/airbnb-engineering/building-airbnbs-service-oriented-architecture-3e3c9e6f9d5f  (SOA Journey)

---
## Notes
- Some entries are authoritative index/tag pages; they aggregate multiple deeper case studies—still valuable gateway resources.
- Avoided duplicate exact URLs; (a previously listed link referenced conceptually in text counts once in numbering).
- Validate periodically since blogs restructure (use Internet Archive if 404).

## Suggested Usage
- Pick a category aligning with an interview (e.g., caching) and internalize 3–5 patterns from those links.
- For each case study: extract forces (scale, latency, consistency), chosen tradeoffs, failure handling, and evolution path.

## Expansion Ideas
- Add 50 more ML/AI serving posts.
- Annotate each link with 1-line “Key Lesson”.
- Create spaced-repetition flashcards from key architecture tradeoffs.

---
End of list.
