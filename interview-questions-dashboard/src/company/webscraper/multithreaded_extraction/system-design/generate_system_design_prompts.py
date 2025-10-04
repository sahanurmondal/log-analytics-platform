import json
import os
from datetime import datetime
import re

class SystemDesignAnswerGenerator:
    def __init__(self, questions_file_path, output_directory):
        self.questions_file_path = questions_file_path
        self.output_directory = output_directory
        self.answers_directory = os.path.join(output_directory, 'answers')
        self.questions = []
        
        # Ensure output directories exist
        os.makedirs(output_directory, exist_ok=True)
        os.makedirs(self.answers_directory, exist_ok=True)
        
    def load_questions(self):
        """Load system design questions from JSON file"""
        with open(self.questions_file_path, 'r', encoding='utf-8') as f:
            self.questions = json.load(f)
        print(f"Loaded {len(self.questions)} system design questions")
        
    def generate_domain_specific_followups(self, title, description):
        """Generate domain-specific follow-up questions based on the question content"""
        title_lower = title.lower()
        desc_lower = description.lower()
        
        followups = []
        
        # E-commerce related
        if any(word in title_lower + desc_lower for word in ['ecommerce', 'e-commerce', 'amazon', 'shopping', 'order', 'payment', 'cart']):
            followups.extend([
                "How would you handle inventory management and prevent overselling?",
                "Design the recommendation engine for personalized product suggestions",
                "How would you implement dynamic pricing strategies?",
                "Handle payment processing with multiple payment gateways",
                "Design fraud detection and prevention systems",
                "Implement order fulfillment and tracking systems",
                "How would you handle flash sales and high-traffic events?",
                "Design a robust returns and refunds system"
            ])
        
        # Social media/messaging related
        if any(word in title_lower + desc_lower for word in ['social', 'messenger', 'chat', 'feed', 'post', 'like', 'comment', 'notification']):
            followups.extend([
                "How would you implement real-time messaging with message ordering?",
                "Design a content moderation system for inappropriate content",
                "Implement friend suggestion algorithms",
                "How would you handle viral content and traffic spikes?",
                "Design privacy controls and data access permissions",
                "Implement push notifications across multiple platforms",
                "How would you prevent spam and abuse?",
                "Design content ranking algorithms for news feeds"
            ])
        
        # Video/media streaming
        if any(word in title_lower + desc_lower for word in ['video', 'streaming', 'media', 'youtube', 'netflix', 'content']):
            followups.extend([
                "How would you implement adaptive bitrate streaming?",
                "Design content delivery for global audiences with minimal latency?",
                "Implement content recommendation based on viewing history?",
                "How would you handle live streaming with low latency?",
                "Design copyright detection and content protection?",
                "Implement video transcoding and multiple format support?",
                "How would you monetize with ads without disrupting user experience?",
                "Design offline viewing and download capabilities?"
            ])
        
        # Ride-sharing/location-based
        if any(word in title_lower + desc_lower for word in ['ride', 'uber', 'taxi', 'location', 'map', 'navigation', 'gps']):
            followups.extend([
                "How would you implement efficient driver-rider matching algorithms?",
                "Design dynamic pricing during peak hours and high demand?",
                "Handle real-time location tracking and updates?",
                "Implement optimal route calculation with traffic data?",
                "How would you ensure driver and rider safety?",
                "Design surge pricing and demand prediction?",
                "Handle offline scenarios when connectivity is poor?",
                "Implement rating and feedback systems for quality control?"
            ])
        
        # Financial/payment systems
        if any(word in title_lower + desc_lower for word in ['payment', 'bank', 'financial', 'transaction', 'money', 'wallet', 'crypto']):
            followups.extend([
                "How would you ensure ACID compliance for financial transactions?",
                "Design fraud detection using machine learning?",
                "Implement regulatory compliance (PCI DSS, GDPR, etc.)?",
                "How would you handle currency conversion and exchange rates?",
                "Design audit trails and transaction logging?",
                "Implement risk management and credit scoring?",
                "How would you handle chargebacks and disputes?",
                "Design multi-factor authentication and security measures?"
            ])
        
        # Search engines
        if any(word in title_lower + desc_lower for word in ['search', 'index', 'crawler', 'ranking', 'google']):
            followups.extend([
                "How would you crawl and index billions of web pages?",
                "Design ranking algorithms for search relevance?",
                "Implement real-time search with autocomplete?",
                "How would you handle personalized search results?",
                "Design spam detection and quality scoring?",
                "Implement image and video search capabilities?",
                "How would you cache search results efficiently?",
                "Design voice search and natural language processing?"
            ])
        
        # Gaming/multiplayer
        if any(word in title_lower + desc_lower for word in ['game', 'gaming', 'multiplayer', 'leaderboard', 'match']):
            followups.extend([
                "How would you handle real-time multiplayer game state synchronization?",
                "Design anti-cheat and fraud detection systems?",
                "Implement matchmaking algorithms for balanced gameplay?",
                "How would you handle game saves and progress across devices?",
                "Design in-game purchases and virtual economies?",
                "Implement spectator modes and live streaming integration?",
                "How would you handle tournament and competitive play?",
                "Design social features like guilds and friend systems?"
            ])
        
        # IoT/sensor data
        if any(word in title_lower + desc_lower for word in ['iot', 'sensor', 'device', 'telemetry', 'monitoring']):
            followups.extend([
                "How would you handle millions of IoT devices sending data?",
                "Design data aggregation and real-time analytics?",
                "Implement device management and firmware updates?",
                "How would you handle intermittent connectivity?",
                "Design anomaly detection for sensor data?",
                "Implement edge computing for local processing?",
                "How would you ensure device security and authentication?",
                "Design data retention and archival strategies?"
            ])
        
        # Add generic follow-ups that apply to most systems
        generic_followups = [
            "How would you implement blue-green deployment for zero-downtime updates?",
            "Design disaster recovery with RPO and RTO requirements?",
            "How would you implement feature flags and A/B testing?",
            "Design cost optimization strategies for cloud infrastructure?",
            "Implement comprehensive monitoring and alerting?",
            "How would you handle GDPR compliance and data privacy?",
            "Design automated scaling based on predictive analytics?",
            "Implement chaos engineering for resilience testing?"
        ]
        
        # Combine domain-specific and generic follow-ups, limit to 12 most relevant
        all_followups = followups + generic_followups
        return all_followups[:12]
    
    def generate_concept_links(self, title, description):
        """Generate specific concept links based on the question content"""
        title_lower = title.lower()
        desc_lower = description.lower()
        
        links = {}
        
        # Core system design patterns
        if any(word in title_lower + desc_lower for word in ['event', 'message', 'queue', 'async']):
            links["Event-Driven Architecture"] = "https://martinfowler.com/articles/201701-event-driven.html"
            links["Message Queues"] = "https://aws.amazon.com/message-queue/"
            links["Event Sourcing"] = "https://martinfowler.com/eaaDev/EventSourcing.html"
        
        if any(word in title_lower + desc_lower for word in ['microservice', 'service', 'distributed']):
            links["Microservices Patterns"] = "https://microservices.io/patterns/"
            links["Service Mesh"] = "https://istio.io/latest/docs/concepts/what-is-istio/"
            links["Circuit Breaker"] = "https://martinfowler.com/bliki/CircuitBreaker.html"
        
        if any(word in title_lower + desc_lower for word in ['database', 'sql', 'nosql', 'data']):
            links["Database Per Service"] = "https://microservices.io/patterns/data/database-per-service.html"
            links["CQRS Pattern"] = "https://docs.microsoft.com/en-us/azure/architecture/patterns/cqrs"
            links["Saga Pattern"] = "https://microservices.io/patterns/data/saga.html"
        
        if any(word in title_lower + desc_lower for word in ['cache', 'cdn', 'performance']):
            links["Caching Strategies"] = "https://aws.amazon.com/caching/"
            links["CDN Architecture"] = "https://www.cloudflare.com/learning/cdn/what-is-a-cdn/"
            links["Redis Patterns"] = "https://redis.io/docs/manual/patterns/"
        
        if any(word in title_lower + desc_lower for word in ['scale', 'load', 'balancer']):
            links["Load Balancing"] = "https://aws.amazon.com/what-is/load-balancing/"
            links["Horizontal Scaling"] = "https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html"
            links["Consistent Hashing"] = "https://en.wikipedia.org/wiki/Consistent_hashing"
        
        if any(word in title_lower + desc_lower for word in ['security', 'auth', 'oauth']):
            links["OAuth 2.0"] = "https://oauth.net/2/"
            links["JWT Best Practices"] = "https://auth0.com/blog/a-look-at-the-latest-draft-for-jwt-bcp/"
            links["Zero Trust Architecture"] = "https://www.nist.gov/publications/zero-trust-architecture"
        
        # Add more concept links based on specific domains
        if any(word in title_lower + desc_lower for word in ['stream', 'real-time', 'kafka']):
            links["Stream Processing"] = "https://kafka.apache.org/documentation/streams/"
            links["Real-time Analytics"] = "https://aws.amazon.com/real-time-analytics/"
        
        return links
    
    def generate_question_specific_resources(self, title, description, company):
        """Generate resources specific to the question domain and company"""
        resources = {
            "documentation": [],
            "case_studies": [],
            "tools": [],
            "books": [],
            "courses": []
        }
        
        title_lower = title.lower()
        desc_lower = description.lower()
        company_lower = company.lower()
        
        # Company-specific resources
        if 'amazon' in company_lower:
            resources["documentation"].extend([
                "AWS Well-Architected Framework",
                "AWS Architecture Center",
                "Amazon Builders' Library"
            ])
            resources["case_studies"].extend([
                "Amazon Prime Video's Microservices Journey",
                "Amazon's DynamoDB Paper",
                "AWS Lambda Cold Start Optimization"
            ])
        
        if 'google' in company_lower:
            resources["documentation"].extend([
                "Google Cloud Architecture Framework",
                "Google SRE Books",
                "GCP Best Practices"
            ])
            resources["case_studies"].extend([
                "Google Spanner: TrueTime & External Consistency",
                "MapReduce: Simplified Data Processing",
                "BigTable: A Distributed Storage System"
            ])
        
        if 'meta' in company_lower or 'facebook' in company_lower:
            resources["case_studies"].extend([
                "Facebook's TAO: The power of the graph",
                "Meta's Social Graph Architecture",
                "Instagram's Architecture Evolution"
            ])
        
        # Domain-specific resources
        if any(word in title_lower + desc_lower for word in ['payment', 'financial', 'bank']):
            resources["documentation"].extend([
                "PCI DSS Compliance Guide",
                "Stripe API Documentation",
                "Payment Systems Architecture"
            ])
            resources["case_studies"].extend([
                "PayPal's Risk Management System",
                "Square's Payment Processing Architecture",
                "Stripe's Global Payment Infrastructure"
            ])
        
        if any(word in title_lower + desc_lower for word in ['video', 'streaming', 'media']):
            resources["documentation"].extend([
                "Netflix Tech Blog",
                "YouTube Engineering Blog",
                "Video Encoding Best Practices"
            ])
            resources["case_studies"].extend([
                "Netflix's Microservices Architecture",
                "YouTube's Global Video Infrastructure",
                "Twitch's Live Streaming Architecture"
            ])
        
        if any(word in title_lower + desc_lower for word in ['search', 'elasticsearch', 'index']):
            resources["documentation"].extend([
                "Elasticsearch Documentation",
                "Lucene Architecture Guide",
                "Search Relevance Engineering"
            ])
            resources["case_studies"].extend([
                "Elasticsearch at Scale",
                "Google Search Architecture",
                "LinkedIn's Search Infrastructure"
            ])
        
        # Add general resources
        resources["books"].extend([
            "Designing Data-Intensive Applications - Martin Kleppmann",
            "System Design Interview - Alex Xu",
            "Building Microservices - Sam Newman",
            "Site Reliability Engineering - Google"
        ])
        
        resources["tools"].extend([
            "Draw.io for Architecture Diagrams",
            "Kubernetes for Container Orchestration",
            "Prometheus for Monitoring",
            "Grafana for Visualization"
        ])
        
        return resources
    
    def generate_prompt_for_question(self, question):
        """Generate the complete enhanced prompt for a specific question"""
        
        # Extract question details
        serial_no = question.get('serial_no', '')
        question_number = question.get('question_number', '')
        title = question.get('title', '')
        description = question.get('description', '')
        difficulty = question.get('difficulty', 'Medium')
        company = question.get('company', 'Unknown')
        
        # Generate domain-specific content
        followups = self.generate_domain_specific_followups(title, description)
        concept_links = self.generate_concept_links(title, description)
        resources = self.generate_question_specific_resources(title, description, company)
        
        # Format concept links
        concept_links_formatted = "\n".join([f"  - [{name}]({url})" for name, url in concept_links.items()])
        
        # Format follow-ups with detailed answers
        followups_formatted = ""
        for i, followup in enumerate(followups, 1):
            followups_formatted += f"\n**Q{i}: {followup}**\n"
            followups_formatted += "**Answer:** [Provide detailed explanation addressing scalability, implementation details, trade-offs, and alternative approaches]\n"
        
        # Format resources
        resources_formatted = ""
        for category, items in resources.items():
            if items:
                resources_formatted += f"\n#### {category.title()}:\n"
                for item in items:
                    resources_formatted += f"- {item}\n"
        
        prompt = f"""You are a Principal Software Architect and System Design Expert with 20+ years of experience at FAANG companies and unicorn startups. You have designed systems that handle billions of users and petabytes of data. Generate a comprehensive, interview-ready answer for the following system design question.

## Question Details:
- **Serial No**: {serial_no}
- **Question No**: {question_number}  
- **Title**: {title}
- **Description**: {description}
- **Difficulty**: {difficulty}
- **Company**: {company}

## Required Comprehensive Answer Structure:

### 1. PROBLEM UNDERSTANDING & REQUIREMENTS GATHERING (5-7 minutes)
- **Problem Restatement**: Rephrase the problem in your own words
- **Clarifying Questions**: Ask critical questions an interviewer would expect:
  * Scale estimates (DAU/MAU, peak QPS, data volume, geographic distribution)
  * Read/write patterns and data access patterns
  * Consistency vs availability preferences (CAP theorem trade-offs)
  * Latency requirements and SLA expectations
  * Budget constraints and cost considerations
  * Integration requirements with existing systems
- **Functional Requirements**: Core features the system must support
- **Non-Functional Requirements**: Performance, scalability, reliability, security
- **Success Metrics**: KPIs and measurable outcomes
- **Constraints & Assumptions**: Technical and business limitations

### 2. CAPACITY PLANNING & SCALE ESTIMATION (3-5 minutes)
- **Back-of-envelope calculations** specific to this domain:
  * Storage requirements (data growth over time)
  * Bandwidth calculations (read/write throughput)
  * Memory requirements (caching needs)
  * CPU requirements (processing complexity)
- **Peak load scenarios** and traffic patterns
- **Growth projections** (1 year, 3 years, 5 years)

### 3. HIGH-LEVEL SYSTEM ARCHITECTURE (10-12 minutes)
```
[ASCII DIAGRAM - Comprehensive High-Level Architecture]
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client Apps   │───▶│   CDN/Edge      │───▶│  Load Balancer  │
│  (Web/Mobile)   │    │   Locations     │    │  (Layer 4 & 7)  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                 ┌─────────────────────┼─────────────────────┐
                                 ▼                     ▼                     ▼
                     ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
                     │  API Gateway    │   │  Auth Service   │   │  Rate Limiter   │
                     │  (Validation)   │   │  (JWT/OAuth)    │   │  (Token Bucket) │
                     └─────────────────┘   └─────────────────┘   └─────────────────┘
                                 │
                    ┌────────────┼────────────┬────────────┬────────────┐
                    ▼            ▼            ▼            ▼            ▼
           ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
           │   Service A  │ │   Service B  │ │   Service C  │ │   Service D  │
           │ (Domain X)   │ │ (Domain Y)   │ │ (Domain Z)   │ │ (Analytics)  │
           └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
                    │            │            │            │
                    ▼            ▼            ▼            ▼
           ┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
           │  Database A  │ │  Database B  │ │    Cache     │ │ Message Queue│
           │ (Primary)    │ │ (Read Replica│ │   (Redis)    │ │  (Kafka)     │
           └──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
```

- **Component Responsibilities**: Detailed explanation of each service
- **Data Flow**: Request/response flow with sequence diagrams
- **Technology Stack**: Specific technology choices with justifications
- **Service Boundaries**: Domain-driven design principles
- **Communication Patterns**: Synchronous vs asynchronous interactions

### 4. DETAILED COMPONENT DESIGN (15-20 minutes)

#### 4.1 API Design
```
# Core API Endpoints (RESTful)
POST   /api/v1/{{domain}}/{{resource}}
GET    /api/v1/{{domain}}/{{resource}}/{{{{id}}}}
PUT    /api/v1/{{domain}}/{{resource}}/{{{{id}}}}
DELETE /api/v1/{{domain}}/{{resource}}/{{{{id}}}}
GET    /api/v1/{{domain}}/{{resource}}?page={{{{page}}}}&limit={{{{limit}}}}&filter={{{{filter}}}}

# Request/Response Examples
POST /api/v1/{{domain}}/{{resource}}
Content-Type: application/json
Authorization: Bearer {{{{jwt_token}}}}

{{{{
  "field1": "value1",
  "field2": "value2",
  "metadata": {{{{
    "created_by": "user_id",
    "timestamp": "2024-01-01T00:00:00Z"
  }}}}
}}}}
```

- **API Versioning Strategy**: Semantic versioning and backward compatibility
- **Authentication & Authorization**: JWT, OAuth 2.0, RBAC implementation
- **Input Validation**: Schema validation, sanitization, error handling
- **Rate Limiting**: Token bucket, sliding window algorithms
- **API Documentation**: OpenAPI/Swagger specifications

#### 4.2 Database Design
```sql
-- Core Entity Schema (adjust based on domain)
CREATE TABLE {{primary_entity}} (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    uuid VARCHAR(36) UNIQUE NOT NULL,
    {{domain_specific_fields}},
    status ENUM('active', 'inactive', 'deleted') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    version INT DEFAULT 1,
    
    INDEX idx_{{entity}}_status (status),
    INDEX idx_{{entity}}_created (created_at),
    INDEX idx_{{entity}}_compound ({{field1}}, {{field2}})
);

-- Relationship Tables
CREATE TABLE {{relationship_table}} (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    {{entity1}}_id BIGINT NOT NULL,
    {{entity2}}_id BIGINT NOT NULL,
    relationship_type VARCHAR(50),
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY ({{entity1}}_id) REFERENCES {{entity1}}(id),
    FOREIGN KEY ({{entity2}}_id) REFERENCES {{entity2}}(id),
    UNIQUE KEY uk_{{rel}}_pair ({{entity1}}_id, {{entity2}}_id)
);
```

- **Database Choice**: SQL vs NoSQL justification based on requirements
- **Schema Evolution**: Migration strategies, backward compatibility
- **Indexing Strategy**: Primary, secondary, composite indexes
- **Partitioning/Sharding**: Horizontal partitioning strategies
- **Replication**: Master-slave, master-master configurations
- **Backup & Recovery**: Point-in-time recovery, disaster recovery

#### 4.3 Caching Architecture
```
Cache Hierarchy:
L1: Browser Cache (Static Assets) - TTL: 1 day
L2: CDN Cache (API Responses) - TTL: 1 hour  
L3: Application Cache (Redis) - TTL: Configurable
L4: Database Query Cache - TTL: 15 minutes
```

- **Cache Invalidation**: Write-through, write-back, write-around patterns
- **Cache Warming**: Proactive loading of frequently accessed data
- **Distributed Caching**: Consistent hashing, cache clustering
- **Cache Monitoring**: Hit ratios, eviction rates, memory usage

#### 4.4 Message Queue & Event System
```
Event Flow Architecture:
Producer → Message Broker → Consumer Groups → Dead Letter Queue

Event Schema:
{{
  "event_id": "uuid",
  "event_type": "domain.action.version",
  "timestamp": "ISO-8601",
  "source": "service_name",
  "data": {{}},
  "metadata": {{
    "correlation_id": "uuid",
    "causation_id": "uuid"
  }}
}}
```

- **Message Ordering**: Partition keys, FIFO guarantees
- **Delivery Semantics**: At-least-once, exactly-once processing
- **Error Handling**: Retry mechanisms, dead letter queues
- **Event Sourcing**: Event store design, snapshot strategies

### 5. ADVANCED SCALABILITY PATTERNS (10-12 minutes)

#### 5.1 Horizontal Scaling Strategies
- **Database Sharding**: Shard key selection, rebalancing strategies
- **Service Partitioning**: Domain-driven microservices boundaries
- **Load Balancing**: Consistent hashing, health checks
- **Auto-scaling**: Predictive scaling, metric-based triggers

#### 5.2 Performance Optimization
- **Query Optimization**: Explain plans, index tuning
- **Connection Pooling**: Database connection management
- **Asynchronous Processing**: Background jobs, worker pools
- **Compression**: Data compression, response gzipping

#### 5.3 Global Distribution
- **Multi-region Deployment**: Active-active, active-passive
- **Data Locality**: Geographic data placement
- **Cross-region Replication**: Eventual consistency models
- **Latency Optimization**: Edge computing, regional caches

### 6. RELIABILITY & FAULT TOLERANCE (8-10 minutes)

#### 6.1 Failure Scenarios & Mitigations
- **Single Point of Failure**: Redundancy strategies
- **Cascading Failures**: Circuit breaker implementation
- **Data Center Outages**: Multi-AZ deployment
- **Network Partitions**: Split-brain prevention

#### 6.2 Resilience Patterns
- **Circuit Breaker**: Implementation with exponential backoff
- **Bulkhead Pattern**: Resource isolation strategies
- **Timeout Management**: Configurable timeouts, retry policies
- **Graceful Degradation**: Fallback mechanisms

#### 6.3 Disaster Recovery
- **RPO/RTO Targets**: Recovery point/time objectives
- **Backup Strategies**: Full, incremental, differential backups
- **Failover Procedures**: Automated vs manual failover
- **Testing**: Chaos engineering, disaster recovery drills

### 7. SECURITY ARCHITECTURE (5-7 minutes)

#### 7.1 Authentication & Authorization
- **Multi-factor Authentication**: TOTP, SMS, biometric
- **Single Sign-On**: SAML, OAuth 2.0, OpenID Connect
- **Role-Based Access Control**: Hierarchical permissions
- **API Security**: Rate limiting, input validation, CORS

#### 7.2 Data Protection
- **Encryption**: AES-256 at rest, TLS 1.3 in transit
- **Key Management**: Hardware security modules, key rotation
- **Data Classification**: PII identification, compliance requirements
- **Audit Logging**: Immutable logs, log integrity

#### 7.3 Network Security
- **VPC Architecture**: Subnet isolation, security groups
- **Web Application Firewall**: DDoS protection, bot detection
- **Intrusion Detection**: Anomaly detection, threat intelligence
- **Zero Trust**: Identity verification, least privilege access

### 8. MONITORING & OBSERVABILITY (5-7 minutes)

#### 8.1 Metrics & KPIs
```
Business Metrics:
- {{domain_specific_metrics}}
- User engagement rates
- Revenue per user
- Feature adoption rates

Technical Metrics:
- Response time (p50, p95, p99)
- Throughput (requests/second)
- Error rates (4xx, 5xx)
- Resource utilization (CPU, memory, disk)
```

#### 8.2 Logging & Tracing
- **Structured Logging**: JSON format, correlation IDs
- **Distributed Tracing**: Jaeger, Zipkin implementation
- **Log Aggregation**: ELK stack, Splunk integration
- **Alerting**: PagerDuty, Slack notifications

#### 8.3 Health Monitoring
- **Health Checks**: Liveness, readiness probes
- **Synthetic Monitoring**: End-to-end user journey testing
- **Performance Monitoring**: APM tools, profiling
- **Capacity Planning**: Trend analysis, predictive scaling

### 9. TRADE-OFFS ANALYSIS & DESIGN DECISIONS (8-10 minutes)

#### 9.1 CAP Theorem Implications
- **Consistency vs Availability**: Trade-off justification for this domain
- **Partition Tolerance**: Network failure handling strategies
- **Eventual Consistency**: Acceptable inconsistency windows

#### 9.2 Performance vs Cost
- **Compute Resources**: CPU-intensive vs memory-intensive workloads
- **Storage Options**: SSD vs HDD, hot vs cold storage
- **Network Bandwidth**: CDN usage, data transfer costs
- **Reserved vs On-demand**: Capacity planning strategies

#### 9.3 Complexity vs Maintainability
- **Microservices vs Monolith**: Service granularity decisions
- **Build vs Buy**: Third-party integration strategies
- **Technical Debt**: Refactoring strategies, code quality

### 10. DESIGN PATTERNS & CONCEPTS USED

#### Applied Patterns with Justifications:
{concept_links_formatted}

#### Pattern Implementations:
- **CQRS (Command Query Responsibility Segregation)**:
  * **Use Case**: Separate read/write models for performance
  * **Implementation**: Event sourcing with read projections
  * **Benefits**: Optimized queries, scalable writes
  
- **Event Sourcing**:
  * **Use Case**: Audit trails, temporal queries, replay capability
  * **Implementation**: Event store with snapshots
  * **Benefits**: Complete audit history, debugging capability
  
- **Saga Pattern**:
  * **Use Case**: Distributed transaction management
  * **Implementation**: Choreography vs orchestration
  * **Benefits**: Eventual consistency, failure recovery

### 11. TECHNOLOGY STACK DEEP DIVE

#### Backend Services
- **Programming Language**: {{language}} (justification based on requirements)
- **Framework**: {{framework}} (performance, ecosystem, team expertise)
- **API Gateway**: Kong/AWS API Gateway (rate limiting, authentication)
- **Service Mesh**: Istio/Linkerd (service-to-service communication)

#### Data Layer
- **Primary Database**: PostgreSQL/MongoDB (consistency vs scalability)
- **Caching**: Redis Cluster (high availability, persistence)
- **Search Engine**: Elasticsearch (full-text search, analytics)
- **Message Broker**: Apache Kafka (high throughput, durability)

#### Infrastructure
- **Cloud Provider**: AWS/GCP/Azure (specific services used)
- **Container Orchestration**: Kubernetes (auto-scaling, service discovery)
- **CI/CD**: GitLab CI/Jenkins (automated testing, deployment)
- **Infrastructure as Code**: Terraform (reproducible environments)

### 12. FOLLOW-UP QUESTIONS & DETAILED ANSWERS

{followups_formatted}

### 13. IMPLEMENTATION ROADMAP

#### Phase 1: MVP (0-3 months)
- Core functionality implementation
- Basic scalability measures
- Essential monitoring and alerting
- MVP technology stack

#### Phase 2: Scale-up (3-6 months)
- Performance optimizations
- Advanced caching strategies
- Microservices refactoring
- Enhanced monitoring

#### Phase 3: Enterprise Scale (6-12 months)
- Global distribution
- Advanced security measures
- Machine learning integration
- Full observability stack

### 14. ALTERNATIVE APPROACHES

#### Approach 1: Monolithic Architecture
- **Pros**: Simpler deployment, easier debugging, faster initial development
- **Cons**: Scaling limitations, technology lock-in, team coordination
- **When to Use**: Small teams, simple requirements, rapid prototyping

#### Approach 2: Microservices Architecture
- **Pros**: Independent scaling, technology diversity, team autonomy
- **Cons**: Distributed system complexity, network latency, operational overhead
- **When to Use**: Large teams, complex domains, varied scaling requirements

#### Approach 3: Serverless Architecture
- **Pros**: Zero server management, automatic scaling, pay-per-use
- **Cons**: Vendor lock-in, cold starts, limited runtime environments
- **When to Use**: Event-driven workloads, variable traffic, quick time-to-market

### 15. REAL-WORLD CASE STUDIES & EXAMPLES

#### Similar Systems in Production:
- **{{company}} Implementation**: Key architectural decisions and lessons learned
- **Industry Best Practices**: Patterns from similar systems at scale
- **Common Pitfalls**: Anti-patterns and how to avoid them
- **Success Stories**: Metrics and outcomes from real implementations

### 16. LEARNING RESOURCES & REFERENCES

#### Question-Specific Resources:
{resources_formatted}

#### Industry Papers & Blogs:
- Company engineering blogs (Netflix, Uber, Airbnb, etc.)
- Academic papers on distributed systems
- Cloud provider architecture guides
- Open source project documentation

#### Professional Development:
- AWS/GCP/Azure certification paths
- System design interview preparation
- Distributed systems courses
- Architecture decision records (ADRs)

---

## Answer Requirements:
- **Technical Depth**: Appropriate for {{difficulty}} level at {{company}}
- **Practical Focus**: Include implementation details and real-world constraints
- **Quantitative Analysis**: Specific numbers, calculations, and benchmarks
- **Visual Elements**: ASCII diagrams for complex architecture components
- **Industry Relevance**: Current best practices and emerging technologies
- **Interview Simulation**: Structure as 45-60 minute conversation with an interviewer

## Success Criteria:
Your answer should demonstrate:
1. **Senior-level architectural thinking** with complex trade-off analysis
2. **Deep technical knowledge** of distributed systems and scalability patterns
3. **Practical experience** with real-world constraints and business requirements
4. **Communication skills** suitable for explaining complex concepts to stakeholders
5. **Problem-solving approach** that considers multiple solutions and alternatives

**Generate a comprehensive answer that would impress the most senior system design interviewers and showcase expert-level knowledge in distributed systems architecture.**
"""
        
        return prompt
        """Generate the complete prompt for a specific question"""
        
        # Extract question details
        serial_no = question.get('serial_no', '')
        question_number = question.get('question_number', '')
        title = question.get('title', '')
        description = question.get('description', '')
        difficulty = question.get('difficulty', 'Medium')
        company = question.get('company', 'Unknown')
        
        prompt = f"""You are a Senior Software Architect and System Design Expert with 15+ years of experience at FAANG companies. Generate a comprehensive interview-style answer for the following system design question.

## Question Details:
- **Serial No**: {serial_no}
- **Question No**: {question_number}  
- **Title**: {title}
- **Description**: {description}
- **Difficulty**: {difficulty}
- **Company**: {company}

## Required Answer Structure:

### 1. PROBLEM UNDERSTANDING & CLARIFICATION (5-7 minutes)
- Restate the problem in your own words
- Ask critical clarifying questions an interviewer would expect:
  * Scale (DAU/MAU, peak QPS, data volume)
  * Geographic distribution
  * Read/write patterns
  * Consistency vs availability preferences
  * Budget/latency constraints
- Define functional and non-functional requirements
- Estimate scale numbers (back-of-envelope calculations)

### 2. HIGH-LEVEL SYSTEM ARCHITECTURE (10-12 minutes)
```
[ASCII DIAGRAM - High Level Architecture]
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │───▶│Load Balancer│───▶│  API Gateway │
└─────────────┘    └─────────────┘    └─────────────┘
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    ▼                      ▼                      ▼
            ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
            │   Service A  │    │   Service B  │    │   Service C  │
            └──────────────┘    └──────────────┘    └──────────────┘
                    │                      │                      │
                    ▼                      ▼                      ▼
            ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
            │  Database A  │    │  Database B  │    │    Cache     │
            └──────────────┘    └──────────────┘    └──────────────┘
```

- Major components and their responsibilities
- Data flow between components
- Technology stack recommendations

### 3. DETAILED COMPONENT DESIGN (15-20 minutes)

#### API Design
```
POST /api/v1/[endpoint]
GET /api/v1/[endpoint]/{{id}}
```

#### Database Schema
```sql
-- Core entities with relationships
CREATE TABLE [table_name] (
    id BIGINT PRIMARY KEY,
    -- other fields
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

#### Data Models
- Entity relationship diagrams
- Data storage patterns
- Indexing strategies

### 4. DEEP DIVE & TRADE-OFFS (10-12 minutes)

#### Scalability Patterns
- **Horizontal vs Vertical Scaling**: When to use each
- **Database Sharding**: Sharding key selection, rebalancing
- **Microservices**: Service boundaries, communication patterns
- **Caching Strategy**: Multi-level caching, cache invalidation
- **CDN Usage**: Static vs dynamic content delivery

#### Consistency & Availability Trade-offs
- **CAP Theorem**: Which two properties to prioritize and why
- **Eventual Consistency**: Acceptable inconsistency windows
- **ACID vs BASE**: Transaction requirements analysis
- **Conflict Resolution**: Last-write-wins, vector clocks, CRDTs

#### Performance Optimizations
- **Database Optimizations**: Query optimization, connection pooling
- **Asynchronous Processing**: Message queues, event-driven architecture
- **Load Balancing**: Round-robin, weighted, consistent hashing
- **Rate Limiting**: Token bucket, sliding window algorithms

### 5. SYSTEM DESIGN PATTERNS (5-8 minutes)

#### Applicable Patterns
- **CQRS (Command Query Responsibility Segregation)**: When and why
- **Event Sourcing**: Audit trails, replay capabilities
- **Saga Pattern**: Distributed transaction management
- **Circuit Breaker**: Fault tolerance and failure isolation
- **Bulkhead Pattern**: Resource isolation
- **Strangler Fig**: Legacy system migration

#### Anti-patterns to Avoid
- Common design mistakes
- Premature optimization
- Over-engineering vs under-engineering

### 6. MONITORING & OBSERVABILITY (3-5 minutes)
- **Metrics**: Key performance indicators (KPIs)
- **Logging**: Structured logging, log aggregation
- **Tracing**: Distributed tracing, request correlation
- **Alerting**: SLA/SLO definitions, alert fatigue prevention
- **Health Checks**: Liveness and readiness probes

### 7. SECURITY CONSIDERATIONS (3-5 minutes)
- **Authentication**: JWT, OAuth 2.0, session management
- **Authorization**: RBAC, ABAC, fine-grained permissions
- **Data Protection**: Encryption at rest and in transit
- **Network Security**: VPC, security groups, WAF
- **Input Validation**: SQL injection, XSS prevention

### 8. FOLLOW-UP SCENARIOS & ADVANCED TOPICS (5-10 minutes)

#### Scale-up Scenarios
- "What if traffic increases 10x overnight?"
- "How would you handle Black Friday-level traffic?"
- "Global expansion to multiple regions?"

#### Failure Scenarios
- "What if the primary database goes down?"
- "How do you handle cascading failures?"
- "Network partition between data centers?"

#### Advanced Features
- "How would you add real-time notifications?"
- "Implement personalization/recommendation engine?"
- "Add analytics and reporting capabilities?"

### 9. TECHNOLOGY STACK JUSTIFICATION
- **Database Choice**: SQL vs NoSQL, specific technologies
- **Programming Language**: Performance vs developer productivity
- **Infrastructure**: Cloud provider selection, containerization
- **Message Brokers**: Kafka vs RabbitMQ vs SQS
- **Caching**: Redis vs Memcached vs application-level

### 10. COST OPTIMIZATION
- **Resource Utilization**: Auto-scaling strategies
- **Data Storage**: Hot vs cold storage tiers
- **Network Costs**: CDN usage, data transfer optimization
- **Compute Costs**: Reserved instances, spot instances

## Answer Requirements:
- Use technical depth appropriate for {{difficulty}} level
- Include specific numbers and calculations
- Draw ASCII diagrams for complex components
- Mention {{company}}-specific considerations if relevant
- Provide alternative approaches with trade-offs
- Use industry best practices and real-world examples
- Structure as if explaining to an interviewer in 45-60 minutes
- Include decision matrices for complex choices

## Output Format:
Generate a complete markdown document that covers all sections above with appropriate technical depth for a {{difficulty}} level system design interview.

IMPORTANT: Create practical, interview-ready content that demonstrates deep system design knowledge and architectural thinking. Focus on real-world constraints and trade-offs."""
        
        return prompt
    
    def create_individual_prompt_files(self):
        """Create individual prompt files for each question"""
        prompts_dir = os.path.join(self.output_directory, 'prompts')
        os.makedirs(prompts_dir, exist_ok=True)
        
        for question in self.questions:
            serial_no = question.get('serial_no', '')
            question_number = question.get('question_number', '')
            title = question.get('title', '').replace('/', '_').replace(':', '_')
            
            # Clean title for filename
            clean_title = ''.join(c for c in title if c.isalnum() or c in (' ', '-', '_')).strip()
            clean_title = clean_title.replace(' ', '_')[:100]  # Limit length
            
            filename = f"prompt_{serial_no:03d}_{question_number}_{clean_title}.md"
            filepath = os.path.join(prompts_dir, filename)
            
            prompt = self.generate_prompt_for_question(question)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(prompt)
        
        print(f"Created {len(self.questions)} individual prompt files in {prompts_dir}")
    
    def create_batch_prompt_file(self):
        """Create a single file with all prompts for batch processing"""
        batch_filename = os.path.join(self.output_directory, 'all_system_design_prompts.md')
        
        with open(batch_filename, 'w', encoding='utf-8') as f:
            f.write(f"# System Design Interview Questions - Complete Prompt Collection\\n")
            f.write(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\\n")
            f.write(f"Total Questions: {len(self.questions)}\\n\\n")
            f.write("---\\n\\n")
            
            for i, question in enumerate(self.questions, 1):
                f.write(f"## Question {i}/{len(self.questions)}\\n\\n")
                f.write(self.generate_prompt_for_question(question))
                f.write("\\n\\n" + "="*80 + "\\n\\n")
        
        print(f"Created batch prompt file: {batch_filename}")
    
    def create_question_index(self):
        """Create an index file listing all questions"""
        index_filename = os.path.join(self.output_directory, 'question_index.md')
        
        with open(index_filename, 'w', encoding='utf-8') as f:
            f.write("# System Design Questions Index\\n\\n")
            f.write(f"Total Questions: {len(self.questions)}\\n")
            f.write(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\\n\\n")
            
            # Group by difficulty
            difficulty_groups = {}
            for question in self.questions:
                difficulty = question.get('difficulty', 'Unknown')
                if difficulty not in difficulty_groups:
                    difficulty_groups[difficulty] = []
                difficulty_groups[difficulty].append(question)
            
            f.write("## Questions by Difficulty\\n\\n")
            for difficulty in ['Easy', 'Medium', 'Hard', 'Unknown']:
                if difficulty in difficulty_groups:
                    questions = difficulty_groups[difficulty]
                    f.write(f"### {difficulty} ({len(questions)} questions)\\n\\n")
                    for question in questions:
                        serial_no = question.get('serial_no', '')
                        question_number = question.get('question_number', '')
                        title = question.get('title', '')
                        company = question.get('company', '')
                        f.write(f"- **{serial_no}.** Q{question_number}: {title} ({company})\\n")
                    f.write("\\n")
            
            # Group by company
            f.write("## Questions by Company\\n\\n")
            company_groups = {}
            for question in self.questions:
                company = question.get('company', 'Unknown')
                if company not in company_groups:
                    company_groups[company] = []
                company_groups[company].append(question)
            
            for company in sorted(company_groups.keys()):
                questions = company_groups[company]
                f.write(f"### {company} ({len(questions)} questions)\\n\\n")
                for question in questions:
                    serial_no = question.get('serial_no', '')
                    question_number = question.get('question_number', '')
                    title = question.get('title', '')
                    difficulty = question.get('difficulty', '')
                    f.write(f"- **{serial_no}.** Q{question_number}: {title} ({difficulty})\\n")
                f.write("\\n")
        
        print(f"Created question index: {index_filename}")
    
    def create_usage_guide(self):
        """Create a usage guide for the prompts"""
        guide_filename = os.path.join(self.output_directory, 'usage_guide.md')
        
        content = '''# System Design Answer Generation Guide

## Overview
This directory contains optimized prompts for generating comprehensive system design interview answers using AI models like GPT-4 or Claude-3.5-Sonnet.

## File Structure
```
system-design/
├── usage_guide.md                 # This file
├── question_index.md              # Index of all questions
├── system_design_prompt_template.md # Master prompt template
├── all_system_design_prompts.md   # All prompts in one file
└── prompts/                       # Individual prompt files
    ├── prompt_001_42_Identifying_Problems.md
    ├── prompt_002_46_Design_crypto_exchange.md
    └── ...
```

## Recommended AI Models

### Primary Recommendation: GPT-4 or Claude-3.5-Sonnet
**Why these models?**
- Superior architectural reasoning and system design knowledge
- Better at generating ASCII diagrams and technical documentation
- Strong understanding of scalability patterns and trade-offs
- Consistent adherence to structured prompts
- Deep knowledge of industry best practices

### Alternative Options:
- **Claude-3-Opus**: Good alternative with strong reasoning
- **GPT-4-Turbo**: Faster response times, good technical depth
- **Gemini Ultra**: Google's flagship model with strong technical knowledge

## Usage Instructions

### Method 1: Individual Questions
1. Choose a question from the `prompts/` directory
2. Copy the entire prompt content
3. Paste into your chosen AI model
4. Review and refine the generated answer
5. Save as markdown file for the specific question

### Method 2: Batch Processing
1. Use `all_system_design_prompts.md` for processing multiple questions
2. Split into smaller batches if hitting token limits
3. Process each question section separately

### Method 3: Custom Prompts
1. Use `system_design_prompt_template.md` as base
2. Replace placeholders with specific question data
3. Modify sections based on question complexity

## Quality Guidelines

### For AI Model Interaction:
- Always specify the difficulty level and company context
- Ask for clarification if the generated answer lacks technical depth
- Request ASCII diagrams for complex architectural components
- Validate technical accuracy of the generated content

### Answer Validation Checklist:
- [ ] Problem understanding section addresses core requirements
- [ ] High-level architecture includes ASCII diagrams
- [ ] API design follows REST/GraphQL best practices
- [ ] Database schema is properly normalized
- [ ] Trade-offs discussion covers CAP theorem implications
- [ ] Security considerations are comprehensive
- [ ] Monitoring and observability are addressed
- [ ] Follow-up scenarios are realistic and challenging

## Tips for Better Results

### Prompt Optimization:
1. **Be Specific**: Include exact scale requirements and constraints
2. **Context Matters**: Mention the company's known tech stack when relevant
3. **Iterative Refinement**: Ask for deeper dives on specific sections
4. **Real-world Focus**: Request practical implementation details

### Example Follow-up Prompts:
```
"Can you provide more detailed ASCII diagrams for the database sharding strategy?"
"Expand on the monitoring section with specific metrics and alerting strategies"
"Add a detailed analysis of the circuit breaker pattern implementation"
"Include code examples for the API endpoints"
```

## Company-Specific Considerations

### FAANG Companies (Meta, Amazon, Apple, Netflix, Google):
- Emphasize massive scale (billions of users)
- Focus on global distribution and latency
- Include ML/AI integration opportunities
- Discuss cost optimization at scale

### Financial Services (JPMorgan, Goldman Sachs):
- Prioritize consistency and ACID compliance
- Include comprehensive security measures
- Address regulatory compliance requirements
- Focus on audit trails and data lineage

### Startups/Scale-ups:
- Balance sophistication with simplicity
- Consider MVP vs full-scale architecture
- Include migration strategies
- Focus on developer productivity

## Expected Answer Quality

### Excellent Answer Characteristics:
- **Comprehensive**: Covers all 10 required sections
- **Technical Depth**: Appropriate for the stated difficulty level
- **Practical**: Includes real-world constraints and considerations
- **Visual**: Contains ASCII diagrams and clear explanations
- **Interactive**: Structured as a conversation with an interviewer

### Red Flags to Avoid:
- Generic answers that could apply to any question
- Missing trade-off discussions
- No mention of failure scenarios
- Lack of specific technology recommendations
- Oversimplified architecture for complex problems

## Success Metrics
A high-quality answer should:
1. Take 45-60 minutes to present in an interview setting
2. Demonstrate senior-level architectural thinking
3. Include specific numbers and calculations
4. Address follow-up questions proactively
5. Show understanding of business constraints

## Continuous Improvement
- Update prompts based on latest industry practices
- Incorporate new design patterns and technologies
- Refine based on actual interview feedback
- Add company-specific templates as needed

---

**Note**: These prompts are designed to generate interview-ready content. Always review and customize the generated answers based on your experience and the specific interview context.
'''
        
        with open(guide_filename, 'w', encoding='utf-8') as f:
            f.write(content)
        
        print(f"Created usage guide: {guide_filename}")
    
    def sanitize_filename(self, title):
        """Sanitize title for use as filename"""
        # Remove question number prefix and clean up
        title = re.sub(r'^\d+\.\s*', '', title)
        # Replace special characters with underscores
        title = re.sub(r'[^\w\s-]', '', title)
        # Replace spaces and multiple underscores with single underscore
        title = re.sub(r'[\s_]+', '_', title)
        # Remove leading/trailing underscores and limit length
        title = title.strip('_')[:50]
        return title.lower()
    
    def generate_answer_template(self, question):
        """Generate a comprehensive answer template for a question"""
        title = question['title']
        description = question['description']
        difficulty = question['difficulty']
        company = question['company']
        serial_no = question['serial_no']
        question_no = question['question_number']
        
        # Extract domain from title and description
        domain = self.extract_domain_from_question(title, description)
        
        template = f"""# {title}

## Question Details
- **Serial No**: {serial_no}
- **Question No**: {question_no}
- **Difficulty**: {difficulty}
- **Company**: {company}
- **Domain**: {domain}

## Problem Statement
{description}

## 🏗️ High-Level Architecture

```ascii
[Client Apps] → [Load Balancer] → [API Gateway] → [Microservices]
                                                       ↓
[Cache Layer] ← [Database Layer] ← [Message Queue] ← [Business Logic]
```

## 📋 Requirements Analysis

### Functional Requirements
- [ ] Core functionality 1
- [ ] Core functionality 2
- [ ] Core functionality 3

### Non-Functional Requirements
- **Scalability**: Handle X concurrent users
- **Availability**: 99.9% uptime
- **Consistency**: Eventual consistency model
- **Performance**: Sub-200ms response time
- **Security**: Authentication and authorization

## 🗄️ Database Design

### Primary Entities
```sql
-- Add your schema here based on the specific question
CREATE TABLE main_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Relationships
- Entity relationships specific to this problem

## 🔄 API Design

### RESTful Endpoints
```
GET    /api/v1/resource
POST   /api/v1/resource
PUT    /api/v1/resource/{{id}}
DELETE /api/v1/resource/{{id}}
```

## 🧩 System Components

### 1. **API Gateway**
- Rate limiting
- Authentication
- Request routing

### 2. **Load Balancer**
- Distributes traffic
- Health checks
- Auto-scaling

### 3. **Microservices**
- Service-specific logic
- Independent deployment
- Technology diversity

### 4. **Database Layer**
- Primary database
- Read replicas
- Caching strategy

### 5. **Message Queue**
- Async processing
- Event-driven architecture
- Reliability

## 📊 Data Flow

1. **Request Flow**: Client → LB → API Gateway → Service
2. **Data Write**: Service → Queue → Database
3. **Data Read**: Service → Cache → Database (if cache miss)
4. **Event Processing**: Service → Queue → Event Handlers

## 🔍 Deep Dive: Key Components

### Caching Strategy
- **L1 Cache**: Application-level cache
- **L2 Cache**: Distributed cache (Redis)
- **CDN**: Static content delivery

### Consistency Model
- **Strong Consistency**: Critical operations
- **Eventual Consistency**: Non-critical data
- **Conflict Resolution**: Last-write-wins or vector clocks

### Scalability Patterns
- **Horizontal Scaling**: Add more instances
- **Vertical Scaling**: Increase instance capacity
- **Database Sharding**: Partition data across nodes

## ⚖️ Trade-offs Analysis

| Aspect | Option A | Option B | Recommendation |
|--------|----------|----------|----------------|
| Consistency | Strong | Eventual | Depends on use case |
| Database | SQL | NoSQL | Based on data structure |
| Architecture | Monolith | Microservices | Team size and complexity |

## 🔐 Security Considerations

### Authentication & Authorization
- JWT tokens
- OAuth 2.0 / OpenID Connect
- Role-based access control (RBAC)

### Data Protection
- Encryption at rest and in transit
- Input validation and sanitization
- SQL injection prevention

### Network Security
- API rate limiting
- DDoS protection
- Firewall configuration

## 📈 Monitoring & Observability

### Metrics
- Request latency (P95, P99)
- Error rates
- Throughput (RPS)
- Resource utilization

### Logging
- Structured logging
- Centralized log aggregation
- Log retention policies

### Alerting
- SLA breach alerts
- Error rate spikes
- Resource exhaustion warnings

## 🚀 Deployment Strategy

### Infrastructure
- Containerization (Docker)
- Orchestration (Kubernetes)
- CI/CD pipeline

### Environments
- Development
- Staging
- Production

### Release Strategy
- Blue-green deployment
- Canary releases
- Feature flags

## 📚 Follow-up Questions & Answers

### Q1: How would you handle a 10x increase in traffic?
**Answer**: Implement auto-scaling, add more read replicas, introduce caching layers, and consider database sharding.

### Q2: What if the primary database fails?
**Answer**: Implement database replication with automatic failover, use read replicas for read operations, and maintain regular backups.

### Q3: How would you ensure data consistency across microservices?
**Answer**: Implement distributed transactions using saga pattern, event sourcing, or two-phase commit protocol.

### Q4: What monitoring would you implement?
**Answer**: Application performance monitoring (APM), infrastructure monitoring, business metrics, and distributed tracing.

### Q5: How would you handle data privacy compliance (GDPR)?
**Answer**: Implement data encryption, audit trails, right to deletion, data minimization, and consent management.

## 🔗 Technology Stack & Resources

### Recommended Technologies
- **Frontend**: React, Vue.js, Angular
- **Backend**: Node.js, Java Spring Boot, Python Django
- **Database**: PostgreSQL, MongoDB, Cassandra
- **Cache**: Redis, Memcached
- **Message Queue**: Apache Kafka, RabbitMQ, AWS SQS
- **Search**: Elasticsearch, Apache Solr
- **Monitoring**: Prometheus, Grafana, New Relic

### Learning Resources
- [High Scalability](http://highscalability.com/)
- [System Design Interview](https://github.com/donnemartin/system-design-primer)
- [Distributed Systems Reading List](https://dancres.github.io/Pages/)
- [AWS Architecture Center](https://aws.amazon.com/architecture/)
- [Google Cloud Architecture Framework](https://cloud.google.com/architecture/framework)

### Related Patterns & Concepts
- **Design Patterns**: Singleton, Factory, Observer, Strategy
- **Architectural Patterns**: Microservices, Event-Driven, CQRS
- **Distributed System Concepts**: CAP Theorem, ACID, BASE, Eventual Consistency
- **Scalability Patterns**: Load Balancing, Caching, Database Scaling

## 💡 Key Takeaways

1. **Start Simple**: Begin with a monolith and evolve to microservices
2. **Think Scale**: Design for current needs + 2-3x growth
3. **Measure Everything**: Implement comprehensive monitoring from day one
4. **Security First**: Build security into every layer
5. **Iterate**: Continuously improve based on real-world usage

---

*Generated on {datetime.now().strftime("%Y-%m-%d %H:%M:%S")} for {company} System Design Interview*
"""
        return template
    
    def extract_domain_from_question(self, title, description):
        """Extract the domain/type of system from question content"""
        text = (title + " " + description).lower()
        
        # Domain mapping based on keywords
        if any(keyword in text for keyword in ['chat', 'messaging', 'whatsapp', 'slack', 'discord']):
            return "Real-time Messaging"
        elif any(keyword in text for keyword in ['video', 'youtube', 'netflix', 'streaming', 'media']):
            return "Video/Media Streaming"
        elif any(keyword in text for keyword in ['social', 'facebook', 'instagram', 'twitter', 'feed']):
            return "Social Media Platform"
        elif any(keyword in text for keyword in ['ecommerce', 'amazon', 'shopping', 'marketplace', 'payment']):
            return "E-commerce Platform"
        elif any(keyword in text for keyword in ['ride', 'uber', 'taxi', 'booking', 'location']):
            return "Location-based Service"
        elif any(keyword in text for keyword in ['search', 'google', 'elasticsearch', 'index']):
            return "Search Engine"
        elif any(keyword in text for keyword in ['cache', 'redis', 'memcached', 'cdn']):
            return "Caching System"
        elif any(keyword in text for keyword in ['notification', 'push', 'email', 'sms']):
            return "Notification System"
        elif any(keyword in text for keyword in ['analytics', 'metrics', 'logging', 'monitoring']):
            return "Analytics Platform"
        elif any(keyword in text for keyword in ['file', 'storage', 'dropbox', 'google drive']):
            return "File Storage System"
        elif any(keyword in text for keyword in ['url', 'shortener', 'tinyurl', 'bitly']):
            return "URL Shortener"
        elif any(keyword in text for keyword in ['rate', 'limiter', 'throttling', 'quota']):
            return "Rate Limiting System"
        elif any(keyword in text for keyword in ['recommendation', 'suggest', 'personalization']):
            return "Recommendation Engine"
        elif any(keyword in text for keyword in ['blockchain', 'crypto', 'bitcoin', 'wallet']):
            return "Blockchain/Crypto System"
        elif any(keyword in text for keyword in ['iot', 'sensor', 'device', 'telemetry']):
            return "IoT Platform"
        else:
            return "Distributed System"
    
    def generate_answer_file(self, question):
        """Generate and save answer file for a specific question"""
        try:
            # Generate filename
            safe_title = self.sanitize_filename(question['title'])
            filename = f"q{question['serial_no']:03d}_{safe_title}.md"
            filepath = os.path.join(self.answers_directory, filename)
            
            # Generate answer content
            answer_content = self.generate_answer_template(question)
            
            # Save to file
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(answer_content)
            
            return filename
            
        except Exception as e:
            print(f"Error generating answer for question {question['serial_no']}: {e}")
            return None
    
    def generate_all_answers(self):
        """Generate answer files for all questions"""
        print("Generating comprehensive answer templates...")
        self.load_questions()
        
        generated_files = []
        failed_questions = []
        
        for question in self.questions:
            filename = self.generate_answer_file(question)
            if filename:
                generated_files.append(filename)
                print(f"Generated: {filename}")
            else:
                failed_questions.append(question['serial_no'])
        
        # Create index file for answers
        self.create_answers_index(generated_files)
        
        print(f"\\nAnswer generation completed!")
        print(f"Successfully generated: {len(generated_files)} files")
        print(f"Failed: {len(failed_questions)} files")
        if failed_questions:
            print(f"Failed question numbers: {failed_questions}")
    
    def create_answers_index(self, generated_files):
        """Create an index file listing all generated answers"""
        index_content = f"""# System Design Answers Index

Generated on: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}
Total Questions: {len(self.questions)}
Generated Answers: {len(generated_files)}

## Questions by Domain

"""
        
        # Group questions by domain
        domain_groups = {}
        for question in self.questions:
            domain = self.extract_domain_from_question(question['title'], question['description'])
            if domain not in domain_groups:
                domain_groups[domain] = []
            domain_groups[domain].append(question)
        
        # Add domain sections
        for domain, questions in sorted(domain_groups.items()):
            index_content += f"### {domain} ({len(questions)} questions)\\n\\n"
            for question in sorted(questions, key=lambda x: x['serial_no']):
                safe_title = self.sanitize_filename(question['title'])
                filename = f"q{question['serial_no']:03d}_{safe_title}.md"
                index_content += f"- [{question['title']}](./{filename}) (Serial: {question['serial_no']}, Difficulty: {question['difficulty']}, Company: {question['company']})\\n"
            index_content += "\\n"
        
        # Add quick reference
        index_content += f"""## Quick Reference

### By Difficulty
"""
        
        # Group by difficulty
        difficulty_groups = {}
        for question in self.questions:
            diff = question['difficulty']
            if diff not in difficulty_groups:
                difficulty_groups[diff] = []
            difficulty_groups[diff].append(question)
        
        for difficulty, questions in sorted(difficulty_groups.items()):
            index_content += f"- **{difficulty}**: {len(questions)} questions\\n"
        
        index_content += f"""
### By Company (Top 10)
"""
        
        # Group by company
        company_counts = {}
        for question in self.questions:
            company = question['company']
            company_counts[company] = company_counts.get(company, 0) + 1
        
        top_companies = sorted(company_counts.items(), key=lambda x: x[1], reverse=True)[:10]
        for company, count in top_companies:
            index_content += f"- **{company}**: {count} questions\\n"
        
        # Save index file
        index_filepath = os.path.join(self.answers_directory, 'README.md')
        with open(index_filepath, 'w', encoding='utf-8') as f:
            f.write(index_content)
        
        print(f"Created answers index: {index_filepath}")
    
    def generate_all_prompts(self):
        """Generate all prompt files and documentation"""
        print("Starting system design prompt generation...")
        self.load_questions()
        self.create_individual_prompt_files()
        self.create_batch_prompt_file()
        self.create_question_index()
        self.create_usage_guide()
        print("\\nPrompt generation completed successfully!")
        print(f"Output directory: {self.output_directory}")
        print(f"Total questions processed: {len(self.questions)}")
    
    def generate_everything(self):
        """Generate both prompts and answer templates"""
        print("Starting comprehensive system design content generation...")
        self.load_questions()
        
        # Generate prompts
        print("\\n1. Generating prompts...")
        self.create_individual_prompt_files()
        self.create_batch_prompt_file()
        self.create_question_index()
        self.create_usage_guide()
        
        # Generate answer templates
        print("\\n2. Generating answer templates...")
        self.generate_all_answers()
        
        print("\\n✅ All content generation completed successfully!")
        print(f"📁 Output directory: {self.output_directory}")
        print(f"📁 Answers directory: {self.answers_directory}")
        print(f"📊 Total questions processed: {len(self.questions)}")

def main():
    # File paths
    questions_file = "system_design_questions.json"
    output_directory = "system-design"
    
    # Create generator and run
    generator = SystemDesignAnswerGenerator(questions_file, output_directory)
    
    # Generate everything (prompts + answer templates)
    generator.generate_everything()

if __name__ == "__main__":
    main()