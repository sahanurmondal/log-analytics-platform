# System Design Interview Answer Generation Prompt

## Optimal Model Recommendation
**Recommended Model: GPT-4 or Claude-3.5-Sonnet**
- These models excel at system design reasoning, architectural thinking, and detailed technical explanations
- They can generate ASCII diagrams and understand complex trade-offs
- Better at following structured prompts and maintaining consistency

## Master Prompt Template

```
You are a Senior Software Architect and System Design Expert with 15+ years of experience at FAANG companies. Generate a comprehensive interview-style answer for the following system design question.

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
GET /api/v1/[endpoint]/{id}
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
- Use technical depth appropriate for {difficulty} level
- Include specific numbers and calculations
- Draw ASCII diagrams for complex components
- Mention {company}-specific considerations if relevant
- Provide alternative approaches with trade-offs
- Use industry best practices and real-world examples
- Structure as if explaining to an interviewer in 45-60 minutes
- Include decision matrices for complex choices

## Output Format:
Generate a complete markdown document that covers all sections above with appropriate technical depth for a {difficulty} level system design interview.
```

## Usage Instructions:
1. Replace placeholders with actual question data
2. Use with GPT-4 or Claude-3.5-Sonnet for best results
3. For complex questions, break into multiple prompts focusing on specific sections
4. Always validate technical accuracy and update with latest industry practices