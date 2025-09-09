# System Design Reference Guide for Backend Engineers

## Table of Contents
1. [Distributed Systems Fundamentals](#distributed-systems-fundamentals)
2. [Communication Protocols & Patterns](#communication-protocols-and-patterns)
3. [Message Queues & Event Processing](#message-queues-and-event-processing)
4. [Microservices Architecture Patterns](#microservices-architecture-patterns)
5. [Caching Strategies](#caching-strategies)
6. [Database Selection & Design](#database-selection-and-design)
7. [Search Technologies](#search-technologies)
8. [Scheduled & Batch Processing](#scheduled-and-batch-processing)
9. [Scalability Patterns](#scalability-patterns)
10. [Security Considerations](#security-considerations)
11. [Monitoring & Observability](#monitoring-and-observability)
12. [Additional Communication Patterns](#additional-communication-patterns)
13. [Data Consistency & Management](#data-consistency--management)
14. [Database Optimization Techniques](#database-optimization-techniques)
15. [Advanced Reliability Patterns](#advanced-reliability-patterns)
16. [Deployment & Release Strategies](#deployment--release-strategies)

## Distributed Systems Fundamentals

### 1. CAP Theorem

**Concept**: The CAP theorem states that a distributed system can provide at most two of these three guarantees simultaneously:
- **Consistency**: All nodes see the same data at the same time
- **Availability**: Every request receives a response, without guarantee that it contains the most recent data
- **Partition Tolerance**: The system continues to operate despite network partitions

**Java Implementation**: 
```java
// Spring Boot application can be configured to prioritize either CP or AP
// For CP systems (using transactions and synchronous processing):
@EnableTransactionManagement
public class ConsistentApplication {
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

// For AP systems (using eventual consistency):
@EnableAsync
public class AvailableApplication {
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(25);
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }
}
```

**When to Use**:
- CP systems: Financial transactions, inventory management
- AP systems: Social media, content delivery, recommendation systems

**When Not to Use**:
- Don't prioritize consistency when immediate availability is critical
- Don't prioritize availability when data correctness is non-negotiable

**Alternatives**: PACELC theorem extends CAP by considering system behavior during normal operation vs. partition.

### 2. Eventual Consistency

**Concept**: Data replicas will become consistent over time, provided no new updates are made.

**Java Implementation**:
```java
// Spring Data with optimistic locking
@Entity
public class Product {
    @Id
    private Long id;
    
    private String name;
    
    @Version
    private Long version;
    
    // Getters and setters
}

// Repository with custom retry logic for conflict resolution
@Repository
public class EventuallyConsistentRepository {
    @Retryable(
        value = OptimisticLockingFailureException.class,
        maxAttempts = 5,
        backoff = @Backoff(delay = 100)
    )
    public void updateWithRetry(Product product) {
        // Update logic
    }
}
```

**When to Use**:
- High-throughput systems where stale reads are acceptable
- Geographically distributed systems
- Systems where conflicts are rare or easily resolvable

**When Not to Use**:
- Financial systems requiring strict consistency
- Systems with complex interdependencies where stale data causes cascading issues

## Communication Protocols and Patterns

### 1. REST APIs

**Concept**: Representational State Transfer uses HTTP methods to perform CRUD operations on resources.

**Spring Implementation**:
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(productService.save(product));
    }
    
    // PUT, DELETE methods
}
```

**When to Use**:
- Public APIs with diverse clients
- CRUD-focused operations
- When HTTP caching can be leveraged

**When Not to Use**:
- Real-time applications requiring push notifications
- Complex operations that don't map well to HTTP verbs
- When performance is critical and payload size matters

**Alternatives**: GraphQL, gRPC, WebSockets

### 2. GraphQL

**Concept**: Query language for APIs that allows clients to request exactly the data they need.

**Spring Implementation**:
```java
// Using graphql-spring-boot-starter
@Component
public class ProductResolver implements GraphQLQueryResolver {
    @Autowired
    private ProductRepository productRepository;
    
    public Product product(Long id) {
        return productRepository.findById(id).orElse(null);
    }
    
    public List<Product> products(int count, int offset) {
        return productRepository.findAll(PageRequest.of(offset, count)).getContent();
    }
}

// Schema.graphqls
type Product {
    id: ID!
    name: String!
    price: Float!
    category: Category
}

type Query {
    product(id: ID!): Product
    products(count: Int, offset: Int): [Product]!
}
```

**When to Use**:
- Mobile applications with bandwidth constraints
- Complex, deeply nested data structures
- When clients need flexible data querying capabilities
- Multiple frontend teams with different data requirements

**When Not to Use**:
- Simple APIs with fixed data requirements
- When caching is critical (more complex with GraphQL)
- File uploads (requires special handling)

**Alternatives**: REST, gRPC

### 3. WebSockets

**Concept**: Protocol providing full-duplex communication channels over a single TCP connection.

**Spring Implementation**:
```java
// WebSocket configuration
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}

// WebSocket controller
@Controller
public class NotificationController {
    
    @MessageMapping("/send/message")
    @SendTo("/topic/notifications")
    public NotificationMessage sendMessage(UserMessage message) {
        return new NotificationMessage(message.getFrom(), message.getText(), new Date());
    }
}
```

**When to Use**:
- Real-time applications (chat, live dashboards, collaborative editing)
- When server needs to push updates to clients
- Low-latency communication requirements

**When Not to Use**:
- Simple request-response patterns
- When clients might be behind restrictive firewalls
- When horizontal scaling is a priority (stateful connections are harder to scale)

**Alternatives**: Server-Sent Events (SSE), Long polling, gRPC streams

### 4. Server-Sent Events (SSE)

**Concept**: Server-to-client push technology enabling a client to receive automatic updates from a server via HTTP connection.

**Spring Implementation**:
```java
@RestController
public class SSEController {
    
    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter();
        
        // Store emitter in a service for later use
        emitterService.add(emitter);
        
        emitter.onCompletion(() -> emitterService.remove(emitter));
        emitter.onTimeout(() -> emitterService.remove(emitter));
        
        // Send initial event
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connection established"));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
        
        return emitter;
    }
    
    // Method to send events to clients
    public void sendEventToClients(String eventName, Object data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        
        emitterService.getEmitters().forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        
        // Remove dead emitters
        deadEmitters.forEach(emitter -> emitterService.remove(emitter));
    }
}
```

**When to Use**:
- Real-time dashboards, stock tickers
- Notification systems
- When you need server-to-client push but don't need client-to-server communication
- When you need to work with standard HTTP infrastructure

**When Not to Use**:
- Bidirectional communication requirements
- Low latency requirements (WebSockets are better)
- IE browser support is required (without polyfills)

**Alternatives**: WebSockets, Long polling

## Message Queues and Event Processing

### 1. Message Queues (RabbitMQ, ActiveMQ)

**Concept**: Asynchronous service-to-service communication using a queue for message passing.

**Spring Implementation**:
```java
// RabbitMQ Configuration
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue orderQueue() {
        return new Queue("orders", true);
    }
    
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("order-exchange");
    }
    
    @Bean
    public Binding binding(Queue orderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(orderQueue).to(exchange).with("order.created");
    }
}

// Producer
@Service
public class OrderProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendOrder(Order order) {
        rabbitTemplate.convertAndSend("order-exchange", "order.created", order);
    }
}

// Consumer
@Service
public class OrderConsumer {
    @RabbitListener(queues = "orders")
    public void processOrder(Order order) {
        // Process the order
    }
}
```

**When to Use**:
- Decoupling services
- Handling traffic spikes with buffering
- Ensuring message delivery (durability)
- Task distribution among workers

**When Not to Use**:
- When immediate response is required
- For very large messages (use references instead)
- Simple, synchronous workflows

**Alternatives**: Kafka (event streaming), direct HTTP calls, gRPC

### 2. Event Streaming (Kafka)

**Concept**: Distributed streaming platform for building real-time data pipelines and streaming applications.

**Spring Implementation**:
```java
// Kafka Producer Configuration
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// Kafka Consumer Configuration
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

// Producer
@Service
public class EventProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void sendEvent(String event) {
        kafkaTemplate.send("events-topic", event);
    }
}

// Consumer
@Service
public class EventConsumer {
    @KafkaListener(topics = "events-topic", groupId = "my-group")
    public void listen(String message) {
        // Process message
    }
}
```

**When to Use**:
- High-throughput event processing
- Data pipelines requiring processing/transformation
- Event sourcing architecture
- Stream processing and analytics
- Long-term event storage

**When Not to Use**:
- Simple message passing (overkill)
- When strict ordering across partitions is required
- When exactly-once delivery semantics are needed without additional effort

**Alternatives**: RabbitMQ, ActiveMQ, AWS Kinesis

## Microservices Architecture Patterns

### 1. API Gateway

**Concept**: Single entry point for all clients, providing routing, composition, and protocol translation.

**Spring Implementation**:
```java
// Using Spring Cloud Gateway
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product_service", r -> r.path("/products/**")
                .filters(f -> f.rewritePath("/products/(?<segment>.*)", "/${segment}")
                               .addRequestHeader("X-Gateway-Source", "cloud-gateway"))
                .uri("lb://PRODUCT-SERVICE"))
            .route("order_service", r -> r.path("/orders/**")
                .filters(f -> f.rewritePath("/orders/(?<segment>.*)", "/${segment}")
                               .addRequestHeader("X-Gateway-Source", "cloud-gateway"))
                .uri("lb://ORDER-SERVICE"))
            .build();
    }
}
```

**When to Use**:
- Microservices architecture with multiple backend services
- When clients need a single endpoint
- When you need cross-cutting concerns (authentication, logging)
- Protocol translation requirements

**When Not to Use**:
- Simple applications with few services
- When low latency is critical (adds network hop)
- When the gateway could become a single point of failure

**Alternatives**: Direct client-to-service communication, BFF (Backend for Frontend) pattern

### 2. Circuit Breaker

**Concept**: Prevents cascading failures by monitoring for failures and opening the circuit when thresholds are exceeded.

**Spring Implementation**:
```java
// Using Resilience4j
@Configuration
public class CircuitBreakerConfig {
    @Bean
    public Customizer<Resilience4jCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .circuitBreakerConfig(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .slidingWindowSize(10)
                .permittedNumberOfCallsInHalfOpenState(5)
                .build())
            .timeLimiterConfig(TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(3))
                .build())
            .build());
    }
}

// Service with circuit breaker
@Service
public class ProductService {
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;
    
    public Product getProduct(Long id) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("productService");
        
        return circuitBreaker.run(
            () -> productClient.getProduct(id),
            throwable -> getProductFallback(id)
        );
    }
    
    private Product getProductFallback(Long id) {
        return new Product(id, "Fallback Product", 0.0);
    }
}
```

**When to Use**:
- Microservices with dependencies on other services
- When failure in one service should not cascade to others
- When degraded functionality is better than complete failure

**When Not to Use**:
- Simple applications with few dependencies
- Critical operations that cannot accept fallbacks
- When failures should propagate (e.g., in a transaction)

**Alternatives**: Timeouts, Bulkhead pattern, Retry pattern

### 3. Service Discovery

**Concept**: Allows services to find each other without hardcoded locations.

**Spring Implementation**:
```java
// Using Eureka Server
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}

// Client Registration
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }
}

// Consuming a service
@Service
public class ProductClient {
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long id) {
        List<ServiceInstance> instances = discoveryClient.getInstances("PRODUCT-SERVICE");
        
        if (instances != null && !instances.isEmpty()) {
            URI uri = instances.get(0).getUri();
            return restTemplate.getForObject(uri + "/products/" + id, Product.class);
        }
        return null;
    }
}

// Or using Feign Client
@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    Product getProduct(@PathVariable("id") Long id);
}
```

**When to Use**:
- Dynamic environments (cloud, containers)
- Microservices architecture with multiple instances
- When services need to find each other without hardcoded URLs

**When Not to Use**:
- Small, stable environments
- When simplicity is more important than dynamism
- When additional network hops must be avoided

**Alternatives**: DNS-based discovery, Load balancer-based discovery, Configuration-based discovery

### 4. Saga Pattern

**Concept**: Manages failures in distributed transactions by either completing all operations or compensating for partial execution.

**Spring Implementation**:
```java
// Orchestration-based saga using Spring State Machine
@Configuration
@EnableStateMachine
public class OrderSagaStateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {
    
    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states
            .withStates()
            .initial(OrderState.CREATED)
            .state(OrderState.PAYMENT_PENDING)
            .state(OrderState.PAYMENT_COMPLETED)
            .state(OrderState.STOCK_CONFIRMED)
            .state(OrderState.SHIPPED)
            .state(OrderState.DELIVERED)
            .state(OrderState.CANCELLED)
            .end(OrderState.DELIVERED)
            .end(OrderState.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions
            .withExternal()
                .source(OrderState.CREATED)
                .target(OrderState.PAYMENT_PENDING)
                .event(OrderEvent.PROCESS_PAYMENT)
                .action(processPaymentAction())
            .and()
            .withExternal()
                .source(OrderState.PAYMENT_PENDING)
                .target(OrderState.PAYMENT_COMPLETED)
                .event(OrderEvent.PAYMENT_COMPLETED)
            .and()
            .withExternal()
                .source(OrderState.PAYMENT_PENDING)
                .target(OrderState.CANCELLED)
                .event(OrderEvent.PAYMENT_FAILED)
                .action(cancelOrderAction())
            // More transitions
    }
    
    @Bean
    public Action<OrderState, OrderEvent> processPaymentAction() {
        return context -> paymentService.processPayment(context.getExtendedState().get("orderId", Long.class));
    }
    
    @Bean
    public Action<OrderState, OrderEvent> cancelOrderAction() {
        return context -> orderService.cancelOrder(context.getExtendedState().get("orderId", Long.class));
    }
}
```

**When to Use**:
- Distributed transactions across microservices
- When atomicity is required across multiple services
- Long-running business processes with compensating actions

**When Not to Use**:
- Simple transactions within a single service
- When eventual consistency is acceptable
- When transaction volume is very high (complexity overhead)

**Alternatives**: Two-Phase Commit (for tightly coupled systems), eventual consistency

## Caching Strategies

### 1. Application Cache

**Concept**: In-memory caching within the application to reduce database load and improve response time.

**Spring Implementation**:
```java
// Using Spring's caching abstraction
@Configuration
@EnableCaching
public class CachingConfig {
    
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
            new ConcurrentMapCache("products"), 
            new ConcurrentMapCache("customers")
        ));
        return cacheManager;
    }
}

// Or using Caffeine for better performance
@Configuration
@EnableCaching
public class CaffeineCacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("products", "customers");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000));
        return cacheManager;
    }
}

// Using the cache
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        // This will be executed only if the product is not in cache
        return productRepository.findById(id).orElse(null);
    }
    
    @CachePut(value = "products", key = "#product.id")
    public Product save(Product product) {
        return productRepository.save(product);
    }
    
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
```

**When to Use**:
- Frequently accessed, rarely changing data
- Expensive computations or database queries
- Read-heavy workloads

**When Not to Use**:
- Write-heavy workloads (cache invalidation overhead)
- When data freshness is critical
- Limited memory environments
- When caching would add more complexity than benefit

**Alternatives**: Distributed cache, Database query cache, CDN

### 2. Distributed Cache

**Concept**: External cache service shared across multiple application instances.

**Spring Implementation**:
```java
// Using Redis with Spring Data
@Configuration
@EnableRedisRepositories
public class RedisConfig {
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer())
            );
            
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(cacheConfig)
            .build();
    }
}

// Using the cache with the same annotations as with local cache
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        // This will be executed only if the product is not in cache
        return productRepository.findById(id).orElse(null);
    }
}
```

**When to Use**:
- Multiple application instances requiring shared cache
- Session storage across load-balanced servers
- When cache needs to persist beyond application lifecycle
- Large cache requirements exceeding single instance memory

**When Not to Use**:
- Simple applications with single instance
- When latency is extremely critical (network hop)
- Small datasets that fit comfortably in local memory

**Alternatives**: Local cache, Database materialized views, CDN

### 3. Cache-Aside Pattern

**Concept**: Application checks cache first; on miss, fetches from database and updates cache.

**Spring Implementation**:
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private RedisTemplate<String, Product> redisTemplate;
    
    private static final String CACHE_KEY_PREFIX = "product:";
    
    public Product findById(Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        
        // Check cache first
        Product cachedProduct = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (cachedProduct != null) {
            return cachedProduct;
        }
        
        // If not in cache, get from database
        Product product = productRepository.findById(id).orElse(null);
        
        // Update cache if found
        if (product != null) {
            redisTemplate.opsForValue().set(cacheKey, product, Duration.ofHours(1));
        }
        
        return product;
    }
}
```

**When to Use**:
- Read-heavy workloads
- When cache warm-up is not feasible
- When data can be temporarily stale

**When Not to Use**:
- Write-heavy workloads
- When strong consistency is required
- When read patterns are unpredictable

**Alternatives**: Read-Through Cache, Cache-As-SoR (Source of Record)

## Database Selection and Design

### 1. SQL Databases

**Concept**: Relational databases that enforce schemas and support ACID transactions.

**Spring Implementation**:
```java
// Spring Data JPA configuration
@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
public class JpaConfig {
    
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/myapp");
        dataSource.setUsername("postgres");
        dataSource.setPassword("password");
        return dataSource;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.example.domain");
        factory.setDataSource(dataSource());
        
        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        factory.setJpaProperties(jpaProperties);
        
        return factory;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }
}

// Entity
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private BigDecimal price;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Getters and setters
}

// Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice")
    List<Product> findExpensiveProducts(@Param("minPrice") BigDecimal minPrice);
}
```

**When to Use**:
- Structured data with clear relationships
- Complex queries and joins
- ACID transaction requirements
- Reporting and analytics
- Data integrity is crucial

**When Not to Use**:
- Unstructured or semi-structured data
- Extremely high write throughput requirements
- Horizontally scaling beyond certain limits
- When schema flexibility is a top priority

**Alternatives**: NoSQL databases (document, key-value, column, graph)

### 2. NoSQL Databases

**Concept**: Non-relational databases optimized for specific data models and high scalability.

**Spring Implementation (MongoDB)**:
```java
// Spring Data MongoDB configuration
@Configuration
@EnableMongoRepositories
public class MongoConfig {
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "myapp");
    }
}

// Document
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    
    private String name;
    
    private BigDecimal price;
    
    private Map<String, Object> attributes;
    
    private List<Review> reviews;
    
    // Nested document
    @Getter @Setter
    public static class Review {
        private String userId;
        private int rating;
        private String comment;
        private Date date;
    }
    
    // Getters and setters
}

// Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameRegex(String nameRegex);
    
    List<Product> findByPriceGreaterThan(BigDecimal price);
    
    @Query("{ 'attributes.color': ?0 }")
    List<Product> findByColor(String color);
}
```

**When to Use**:
- Semi-structured or unstructured data
- High write throughput requirements
- Horizontal scalability needs
- Schema flexibility and evolution
- Specific data models (documents, graphs, etc.)

**When Not to Use**:
- Complex relationships requiring joins
- Strong ACID transaction requirements
- When data integrity is non-negotiable
- Complex reporting requirements

**Alternatives**: SQL databases, specialized data stores (time-series, etc.)

### 3. Polyglot Persistence

**Concept**: Using different database types for different data storage needs within the same application.

**Spring Implementation**:
```java
@Configuration
@EnableJpaRepositories(basePackages = "com.example.repository.jpa")
@EnableMongoRepositories(basePackages = "com.example.repository.mongo")
@EnableRedisRepositories(basePackages = "com.example.repository.redis")
public class DatabaseConfig {
    // JPA config
    @Bean
    @Primary
    public DataSource sqlDataSource() {
        // Configure SQL datasource
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        // Configure JPA
    }
    
    // MongoDB config
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
    
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "myapp");
    }
    
    // Redis config
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
```

**When to Use**:
- Applications with diverse data storage needs
- When different data types have different access patterns
- When optimizing for both read and write performance
- Microservices architecture where each service chooses its ideal database

**When Not to Use**:
- Simple applications with uniform data needs
- When operational complexity is a concern
- When cross-database transactions are frequent

## Search Technologies

### 1. Elasticsearch

**Concept**: Distributed, RESTful search and analytics engine for full-text search, structured search, and analytics.

**Spring Implementation**:
```java
// Spring Data Elasticsearch configuration
@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig {
    
    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfig = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .build();
        return RestClients.create(clientConfig).rest();
    }
    
    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}

// Document model
@Document(indexName = "products")
public class ProductDocument {
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Keyword)
    private String category;
    
    @Field(type = FieldType.Nested)
    private List<Feature> features;
    
    // Nested type
    @Getter @Setter
    public static class Feature {
        @Field(type = FieldType.Keyword)
        private String name;
        
        @Field(type = FieldType.Text)
        private String value;
    }
}

// Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, String> {
    List<ProductDocument> findByNameContaining(String name);
    
    @Query("{\"bool\": {\"must\": [{\"match\": {\"name\": \"?0\"}}, {\"range\": {\"price\": {\"gte\": \"?1\"}}}]}}")
    List<ProductDocument> findByNameAndMinPrice(String name, BigDecimal minPrice);
}

// Search Service
@Service
public class ProductSearchService {
    
    @Autowired
    private ElasticsearchOperations operations;
    
    public List<ProductDocument> search(String text) {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
            .withQuery(QueryBuilders.multiMatchQuery(text, "name", "description")
                .fuzziness(Fuzziness.AUTO))
            .withSort(SortBuilders.fieldSort("price").order(SortOrder.ASC))
            .build();
        
        return operations.search(query, ProductDocument.class)
            .getSearchHits()
            .stream()
            .map(SearchHit::getContent)
            .collect(Collectors.toList());
    }
}
```

**When to Use**:
- Full-text search requirements
- Log and event data analysis
- When search speed is critical
- Complex search queries with scoring
- Analytics on large datasets

**When Not to Use**:
- Primary data store (typically used alongside a database)
- Frequent updates to documents
- ACID transaction requirements
- Limited infrastructure resources (memory-intensive)

**Alternatives**: Apache Solr, database full-text search, Algolia (managed service)

### 2. Database Full-Text Search

**Concept**: Using database's built-in full-text search capabilities.

**Spring Implementation (PostgreSQL)**:
```java
// Entity with full-text search
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Other fields
}

// Repository with full-text search methods
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT * FROM products " +
                  "WHERE to_tsvector('english', name || ' ' || description) " +
                  "@@ to_tsquery('english', :searchTerm)", 
           nativeQuery = true)
    List<Product> fullTextSearch(@Param("searchTerm") String searchTerm);
    
    @Query(value = "SELECT * FROM products, " +
                  "ts_rank(to_tsvector('english', name || ' ' || description), " +
                  "to_tsquery('english', :searchTerm)) as rank " +
                  "WHERE to_tsvector('english', name || ' ' || description) " +
                  "@@ to_tsquery('english', :searchTerm) " +
                  "ORDER BY rank DESC", 
           nativeQuery = true)
    List<Product> fullTextSearchWithRanking(@Param("searchTerm") String searchTerm);
}
```

**When to Use**:
- When simplicity and reduced infrastructure are priorities
- Moderate search requirements
- Already using the database for primary storage
- Small to medium dataset sizes

**When Not to Use**:
- Advanced search features needed
- Large-scale search operations
- High query load that would impact database performance
- Need for specialized search functionality (geospatial, etc.)

**Alternatives**: Elasticsearch, Apache Solr, Algolia

## Scheduled and Batch Processing

### 1. Scheduled Tasks

**Concept**: Running tasks at specified intervals or times.

**Spring Implementation**:
```java
// Enable scheduling
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // Any custom configuration
}

// Service with scheduled methods
@Service
public class ReportingService {
    
    @Scheduled(fixedRate = 60000) // Run every minute
    public void generatePeriodicReports() {
        // Generate reports
    }
    
    @Scheduled(cron = "0 0 1 * * ?") // Run at 1 AM every day
    public void generateDailyReport() {
        // Generate daily report
    }
    
    @Scheduled(fixedDelay = 30000, initialDelay = 60000) // Start after 1 minute, then every 30 seconds
    public void checkSystemHealth() {
        // Check health
    }
}
```

**When to Use**:
- Recurring tasks within the application
- Simple scheduling needs
- When tasks are tied to the application lifecycle
- Small to medium workloads

**When Not to Use**:
- Distributed scheduling across multiple instances
- Critical tasks that must run exactly once
- CPU-intensive or long-running tasks
- When schedule requires external management

**Alternatives**: Quartz Scheduler, External cron jobs, Workflow engines

### 2. Spring Batch Processing

**Concept**: Framework for batch processing with features for logging/tracking, transaction management, job processing statistics, job restart, and resource management.

**Spring Implementation**:
```java
// Batch configuration
@Configuration
@EnableBatchProcessing
public class BatchConfig {
    
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
    
    @Bean
    public FlatFileItemReader<Customer> reader() {
        return new FlatFileItemReaderBuilder<Customer>()
            .name("customerItemReader")
            .resource(new ClassPathResource("sample-data.csv"))
            .delimited()
            .names(new String[]{"firstName", "lastName", "email"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Customer.class);
            }})
            .build();
    }
    
    @Bean
    public CustomerItemProcessor processor() {
        return new CustomerItemProcessor();
    }
    
    @Bean
    public JdbcBatchItemWriter<Customer> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Customer>()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO customers (first_name, last_name, email) VALUES (:firstName, :lastName, :email)")
            .dataSource(dataSource)
            .build();
    }
    
    @Bean
    public Job importCustomerJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importCustomerJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .flow(step1)
            .end()
            .build();
    }
    
    @Bean
    public Step step1(JdbcBatchItemWriter<Customer> writer) {
        return stepBuilderFactory.get("step1")
            .<Customer, Customer>chunk(10)
            .reader(reader())
            .processor(processor())
            .writer(writer)
            .build();
    }
}

// Processor
public class CustomerItemProcessor implements ItemProcessor<Customer, Customer> {
    @Override
    public Customer process(final Customer customer) {
        final String firstName = customer.getFirstName().toUpperCase();
        final String lastName = customer.getLastName().toUpperCase();
        
        final Customer transformedCustomer = new Customer(firstName, lastName, customer.getEmail());
        
        return transformedCustomer;
    }
}
```

**When to Use**:
- Processing large volumes of data
- Extract-Transform-Load (ETL) operations
- Scheduled data migrations or imports
- Complex processing requiring restart capabilities
- When processing statistics and monitoring are needed

**When Not to Use**:
- Simple, quick tasks
- Real-time processing requirements
- When simplicity is more important than robustness
- Small datasets that don't justify the framework overhead

**Alternatives**: Simple scheduled tasks, Apache Spark, Apache Flink, custom batch processing

## Scalability Patterns

### 1. Load Balancing

**Concept**: Distributing network traffic across multiple servers to ensure high availability and reliability.

**Spring Implementation (Client-Side)**:
```java
// Spring Cloud LoadBalancer with Eureka
@Configuration
public class LoadBalancerConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

// Using the load balanced RestTemplate
@Service
public class ProductClient {
    @Autowired
    private RestTemplate restTemplate;
    
    public Product getProduct(Long id) {
        return restTemplate.getForObject("http://product-service/products/" + id, Product.class);
    }
}

// Or with WebClient
@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

@Service
public class ReactiveProductClient {
    @Autowired
    private WebClient.Builder webClientBuilder;
    
    public Mono<Product> getProduct(Long id) {
        return webClientBuilder.build()
            .get()
            .uri("http://product-service/products/{id}", id)
            .retrieve()
            .bodyToMono(Product.class);
    }
}
```

**When to Use**:
- Multiple instances of the same service
- High availability requirements
- When cost optimization through right-sizing is important
- To handle unpredictable or variable loads

**When Not to Use**:
- Stateful applications without shared state solution
- When vertical scaling is more cost-effective
- Applications with high per-instance overhead

**Alternatives**: Service mesh, API Gateway with custom routing

### 2. Horizontal Scaling

**Concept**: Adding more instances of application to handle increased load.

**Spring Implementation (Kubernetes)**:
```yaml
# Kubernetes Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: my-registry/product-service:latest
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "256Mi"
            cpu: "200m"
          limits:
            memory: "512Mi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10

# Horizontal Pod Autoscaler
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 3
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

**Spring Implementation (Stateless Service)**:
```java
@Configuration
public class StatelessConfig {
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        // Use header-based session for stateless scaling
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}

// Or store session in Redis for shared state
@Configuration
@EnableRedisHttpSession
public class SessionConfig {
    @Bean
    public RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}
```

**When to Use**:
- Traffic exceeds single database capacity
- High availability requirements
- When cost optimization through right-sizing is important
- To handle unpredictable or variable loads

**When Not to Use**:
- Stateful applications without shared state solution
- When vertical scaling is more cost-effective
- Applications with high per-instance overhead

**Alternatives**: Vertical scaling, Function-as-a-Service (FaaS)

## Security Considerations

### 1. HTTPS Implementation

**Concept**: Encrypting HTTP traffic using TLS/SSL.

**Spring Implementation**:
```java
// Spring Boot application.properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat

// HTTP to HTTPS redirect
@Configuration
public class HttpsRedirectConfig {
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    private Connector redirectConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
```

**When to Use**:
- Any production application
- When handling sensitive data
- When user authentication is implemented
- For all external-facing APIs

**When Not to Use**:
- Development environments (though increasingly used there too)
- Internal services in fully trusted networks (debatable)

**Alternatives**: Virtual private networks, API gateways with encryption

### 2. OAuth 2.0 and JWT

**Concept**: Standard authorization framework and token format for secure API access.

**Spring Implementation**:
```java
// Authorization Server (OAuth 2.0)
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
            .withClient("client-id")
            .secret("{noop}client-secret")
            .scopes("read", "write")
            .authorizedGrantTypes("password", "refresh_token")
            .accessTokenValiditySeconds(3600)
            .refreshTokenValiditySeconds(86400);
    }
    
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
            .tokenStore(tokenStore())
            .accessTokenConverter(accessTokenConverter());
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("my-secret-key"); // Use asymmetric key for production
        return converter;
    }
}

// Resource Server
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore());
    }
    
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("my-secret-key");
        return converter;
    }
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .antMatchers("/api/admin/**").hasRole("ADMIN")
            .antMatchers("/api/**").authenticated();
    }
}
```

**When to Use**:
- API authentication and authorization
- Single sign-on requirements
- Microservices with shared authentication
- Mobile/SPA applications requiring secure API access

**When Not to Use**:
- Simple, internal applications
- When overhead of token management is not justified
- Legacy systems with existing authentication mechanisms

**Alternatives**: Basic Auth (for simple cases), API Keys, SAML (for enterprise)

## Monitoring and Observability

### 1. Metrics Collection with Micrometer

**Concept**: Application metrics collection and monitoring for performance and health.

**Spring Implementation**:
```java
// Spring Boot automatically configures Micrometer
// Add dependencies:
// - spring-boot-starter-actuator
// - micrometer-registry-prometheus

// application.properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true

// Custom metrics
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    private final Counter requestCounter;
    private final Timer requestTimer;
    
    public ProductController(MeterRegistry registry) {
        this.requestCounter = Counter.builder("api.requests")
                                    .tag("endpoint", "products")
                                    .description("Number of requests to products API")
                                    .register(registry);
                                    
        this.requestTimer = Timer.builder("api.request.latency")
                                .tag("endpoint", "products")
                                .description("Request latency for products API")
                                .register(registry);
    }
    
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        requestCounter.increment();
        return requestTimer.record(() -> productService.findById(id));
    }
}

// Custom health indicator
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                return Health.up()
                           .withDetail("database", "PostgreSQL")
                           .withDetail("status", "Connection successful")
                           .build();
            }
        } catch (Exception e) {
            return Health.down()
                       .withDetail("error", e.getMessage())
                       .build();
        }
    }
}
```

**When to Use**:
- Production applications requiring monitoring
- Performance-critical applications
- Microservices requiring observability
- When SLAs/SLOs need to be measured and enforced

**When Not to Use**:
- Simple applications where overhead isn't justified
- Development or test environments (sometimes)

**Alternatives**: Custom logging, APM tools (New Relic, Dynatrace), Cloud provider monitoring

### 2. Distributed Tracing

**Concept**: Tracking requests across multiple services to understand system behavior and performance.

**Spring Implementation**:
```java
// Spring Cloud Sleuth with Zipkin
// Add dependencies:
// - spring-cloud-starter-sleuth
// - spring-cloud-sleuth-zipkin

// application.properties
spring.application.name=product-service
spring.sleuth.sampler.probability=1.0
spring.zipkin.base-url=http://localhost:9411

// Service with tracing
@Service
public class ProductService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private Tracer tracer;
    
    public Product getProductWithSupplier(Long id) {
        Span span = tracer.currentSpan().name("getProductWithSupplier");
        
        try (Tracer.SpanInScope ws = tracer.withSpan(span.start())) {
            // Add custom tags
            span.tag("productId", id.toString());
            
            // First get product
            Product product = getProduct(id);
            
            // Then get supplier details
            Supplier supplier = getSupplier(product.getSupplierId());
            
            // Set supplier on product
            product.setSupplier(supplier);
            
            return product;
        } finally {
            span.finish();
        }
    }
    
    private Product getProduct(Long id) {
        // Logic to get product
        return new Product(id, "Product " + id, 100.0);
    }
    
    private Supplier getSupplier(Long id) {
        // This call will automatically propagate trace IDs
        return restTemplate.getForObject("http://supplier-service/suppliers/" + id, Supplier.class);
    }
}
```

**When to Use**:
- Microservices architecture
- Debugging complex request flows
- Performance optimization across services
- When you need to understand dependencies between services

**When Not to Use**:
- Monolithic applications (less beneficial)
- When overhead of tracing impacts performance too much
- Simple systems with few components

**Alternatives**: Logs correlation with request IDs, APM tools, service mesh observability

## Additional Communication Patterns

### 1. Long Polling

**Concept**: Client makes an HTTP request that the server holds open until it has new data or the request times out.

**Spring Implementation**:
```java
@RestController
public class LongPollingController {
    
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    
    @GetMapping("/messages")
    public DeferredResult<ResponseEntity<Message>> getMessages() {
        DeferredResult<ResponseEntity<Message>> deferredResult = new DeferredResult<>(120000L, 
                ResponseEntity.status(HttpStatus.NO_CONTENT).build());
        
        CompletableFuture.runAsync(() -> {
            try {
                Message message = messageQueue.poll(30, TimeUnit.SECONDS);
                if (message != null) {
                    deferredResult.setResult(ResponseEntity.ok(message));
                } else {
                    deferredResult.setResult(ResponseEntity.noContent().build());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
            }
        });
        
        return deferredResult;
    }
    
    @PostMapping("/messages")
    public ResponseEntity<Void> addMessage(@RequestBody Message message) {
        messageQueue.offer(message);
        return ResponseEntity.accepted().build();
    }
}
```

**When to Use**:
- Simple real-time updates where WebSockets are not supported
- When server needs to push data but overhead of WebSockets is not justified
- Legacy browser support is required
- When passing through certain proxies/firewalls that block WebSockets

**When Not to Use**:
- High-frequency updates (connection overhead)
- True bidirectional communication is needed
- Mobile applications (battery impact)
- When many concurrent connections are expected

**Alternatives**: WebSockets, Server-Sent Events, regular polling

### 2. API Versioning

**Concept**: Strategies for evolving APIs without breaking existing clients.

**Spring Implementation**:
```java
// URL Path Versioning
@RestController
@RequestMapping("/api/v1")
public class ProductControllerV1 {
    @GetMapping("/products/{id}")
    public ProductV1 getProductV1(@PathVariable Long id) {
        // Version 1 implementation
    }
}

@RestController
@RequestMapping("/api/v2")
public class ProductControllerV2 {
    @GetMapping("/products/{id}")
    public ProductV2 getProductV2(@PathVariable Long id) {
        // Version 2 implementation
    }
}

// Header-Based Versioning
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id, 
                                      @RequestHeader(value="X-API-Version", defaultValue="1") int version) {
        if (version == 1) {
            return ResponseEntity.ok(getProductV1(id));
        } else if (version == 2) {
            return ResponseEntity.ok(getProductV2(id));
        }
        return ResponseEntity.badRequest().build();
    }
}

// Media Type Versioning
@RestController
@RequestMapping("/api/products")
public class ProductMediaTypeController {
    @GetMapping(value = "/{id}", produces = "application/vnd.company.app-v1+json")
    public ProductV1 getProductV1(@PathVariable Long id) {
        // Version 1 implementation
    }
    
    @GetMapping(value = "/{id}", produces = "application/vnd.company.app-v2+json")
    public ProductV2 getProductV2(@PathVariable Long id) {
        // Version 2 implementation
    }
}
```

**When to Use**:
- When API changes would break existing clients
- Public APIs with diverse clients
- When backward compatibility is required
- Long-lived APIs with expected evolution

**When Not to Use**:
- Internal APIs with synchronized client-server updates
- APIs with extremely simple and stable requirements
- When maintaining multiple versions creates excessive overhead

**Alternatives**: Hypermedia APIs (HATEOAS), forward-compatible design

## Data Consistency & Management

### 1. Idempotency

**Concept**: Making operations safe to retry multiple times without causing additional side effects.

**Java Implementation**:
```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired 
    private IdempotencyRepository idempotencyRepository;
    
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order,
                                           @RequestHeader("Idempotency-Key") String idempotencyKey) {
        // Check if request was already processed
        Optional<ProcessedRequest> processedRequest = 
            idempotencyRepository.findByIdempotencyKey(idempotencyKey);
        
        if (processedRequest.isPresent()) {
            // Return cached response
            Order existingOrder = orderService.findById(processedRequest.get().getResourceId());
            return ResponseEntity.status(HttpStatus.OK)
                .header("X-Idempotent-Replayed", "true")
                .body(existingOrder);
        }
        
        // Process the request
        Order createdOrder = orderService.createOrder(order);
        
        // Store idempotency record
        idempotencyRepository.save(new ProcessedRequest(idempotencyKey, createdOrder.getId()));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }
}

// Idempotency record entity
@Entity
public class ProcessedRequest {
    @Id
    private String idempotencyKey;
    
    private Long resourceId;
    
    private Instant processedAt = Instant.now();
    
    // Constructors, getters, setters
}
```

**When to Use**:
- Payment processing or financial transactions
- Order submissions
- API operations that shouldn't be duplicated
- Network environments with retry logic

**When Not to Use**:
- Read operations (naturally idempotent)
- Operations where retries should count as separate requests
- When immediate failure feedback is required

**Alternatives**: Natural idempotency design, client-side tracking

### 2. Event Sourcing

**Concept**: Storing changes to application state as a sequence of events rather than just the current state.

**Java Implementation**:
```java
// Event
@Getter
public abstract class DomainEvent {
    private final UUID eventId = UUID.randomUUID();
    private final LocalDateTime occurredAt = LocalDateTime.now();
}

// Aggregate
public class ShoppingCart {
    private UUID id;
    private Map<UUID, Integer> items = new HashMap<>();
    private boolean checkedOut = false;
    
    public void apply(ItemAddedEvent event) {
        this.items.put(event.getProductId(), 
                      this.items.getOrDefault(event.getProductId(), 0) + event.getQuantity());
    }
    
    public void apply(ItemRemovedEvent event) {
        if (this.items.containsKey(event.getProductId())) {
            int newQuantity = this.items.get(event.getProductId()) - event.getQuantity();
            if (newQuantity <= 0) {
                this.items.remove(event.getProductId());
            } else {
                this.items.put(event.getProductId(), newQuantity);
            }
        }
    }
    
    public void apply(CartCheckedOutEvent event) {
        this.checkedOut = true;
    }
}

// Event Store
@Service
public class EventStore {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Transactional
    public void save(UUID aggregateId, List<DomainEvent> events) {
        events.forEach(event -> {
            EventRecord record = new EventRecord();
            record.setAggregateId(aggregateId);
            record.setEventType(event.getClass().getSimpleName());
            record.setEventData(serialize(event));
            record.setTimestamp(event.getOccurredAt());
            
            eventRepository.save(record);
        });
    }
    
    public List<DomainEvent> getEvents(UUID aggregateId) {
        return eventRepository.findByAggregateIdOrderByTimestamp(aggregateId)
                             .stream()
                             .map(this::deserialize)
                             .collect(Collectors.toList());
    }
    
    // Serialization methods omitted
}

// Service using Event Sourcing
@Service
public class ShoppingCartService {
    
    @Autowired
    private EventStore eventStore;
    
    public ShoppingCart getCart(UUID cartId) {
        List<DomainEvent> events = eventStore.getEvents(cartId);
        return buildCartFromEvents(events);
    }
    
    @Transactional
    public void addItem(UUID cartId, UUID productId, int quantity) {
        ShoppingCart cart = getCart(cartId);
        
        if (cart.isCheckedOut()) {
            throw new IllegalStateException("Cannot modify a checked-out cart");
        }
        
        ItemAddedEvent event = new ItemAddedEvent(productId, quantity);
        cart.apply(event);
        
        eventStore.save(cartId, Collections.singletonList(event));
    }
    
    private ShoppingCart buildCartFromEvents(List<DomainEvent> events) {
        ShoppingCart cart = new ShoppingCart();
        
        for (DomainEvent event : events) {
            if (event instanceof ItemAddedEvent) {
                cart.apply((ItemAddedEvent) event);
            } else if (event instanceof ItemRemovedEvent) {
                cart.apply((ItemRemovedEvent) event);
            } else if (event instanceof CartCheckedOutEvent) {
                cart.apply((CartCheckedOutEvent) event);
            }
        }
        
        return cart;
    }
}
```

**When to Use**:
- Systems requiring complete audit trails
- Business processes where history is important
- Domains with complex business rules and state transitions
- When the ability to replay events for debugging or rebuilding state is valuable

**When Not to Use**:
- Simple CRUD applications
- Large data volumes with limited history needs
- When query performance is the primary concern
- When event schema evolution would be problematic

**Alternatives**: Traditional state-based persistence, Change Data Capture

### 3. Change Data Capture (CDC)

**Concept**: Tracking changes to a data source and propagating those changes to other systems.

**Spring Implementation**:
```java
// Using Debezium with Spring
@Configuration
public class DebeziumConfig {
    
    @Bean
    public io.debezium.config.Configuration debeziumConfig() {
        return io.debezium.config.Configuration.create()
            .with("name", "postgres-connector")
            .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
            .with("database.hostname", "localhost")
            .with("database.port", "5432")
            .with("database.user", "postgres")
            .with("database.password", "postgres")
            .with("database.dbname", "mydb")
            .with("database.server.name", "dbserver1")
            .with("table.include.list", "public.products")
            .with("plugin.name", "pgoutput")
            .build();
    }
    
    @Bean
    public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine() {
        return DebeziumEngine.create(Json.class)
            .using(debeziumConfig().asProperties())
            .notifying(this::handleChangeEvent)
            .build();
    }
    
    private void handleChangeEvent(ChangeEvent<String, String> event) {
        if (event.value() == null) {
            log.info("Received delete event: {}", event.key());
            return;
        }
        
        try {
            JsonNode valueNode = new ObjectMapper().readTree(event.value());
            JsonNode payload = valueNode.get("payload");
            JsonNode after = payload.get("after");
            JsonNode before = payload.get("before");
            String op = payload.get("op").asText();
            
            switch (op) {
                case "c": // Create
                    handleCreateEvent(after);
                    break;
                case "u": // Update
                    handleUpdateEvent(before, after);
                    break;
                case "d": // Delete
                    handleDeleteEvent(before);
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing change event", e);
        }
    }
    
    private void handleCreateEvent(JsonNode data) {
        ProductEvent event = new ProductEvent(
            EventType.CREATED,
            data.get("id").asLong(),
            data.get("name").asText(),
            data.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    private void handleUpdateEvent(JsonNode before, JsonNode after) {
        ProductEvent event = new ProductEvent(
            EventType.UPDATED,
            after.get("id").asLong(),
            after.get("name").asText(),
            after.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    private void handleDeleteEvent(JsonNode data) {
        ProductEvent event = new ProductEvent(
            EventType.DELETED,
            data.get("id").asLong(),
            data.get("name").asText(),
            data.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    @Bean(destroyMethod = "close")
    public EmbeddedEngine startDebeziumEngine(DebeziumEngine<ChangeEvent<String, String>> engine) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);
        
        return () -> {
            try {
                engine.close();
                executor.shutdownNow();
            } catch (Exception e) {
                log.error("Error closing Debezium engine", e);
            }
        };
    }
}

// Event producer using Spring Cloud Stream
@Component
public class ProductEventProducer {
    
    @Autowired
    private StreamBridge streamBridge;
    
    public void sendEvent(ProductEvent event) {
        streamBridge.send("productEvents-out-0", event);
    }
}

// Event consumer
@Component
public class ProductEventConsumer {
    
    @Autowired
    private ProductIndexService productIndexService;
    
    @Bean
    public Consumer<ProductEvent> processProductEvents() {
        return event -> {
            log.info("Received product event: {}", event);
            
            switch (event.getType()) {
                case CREATED:
                case UPDATED:
                    productIndexService.indexProduct(event);
                    break;
                case DELETED:
                    productIndexService.removeFromIndex(event.getId());
                    break;
            }
        };
    }
}
```

**When to Use**:
- Real-time data integration requirements
- Event-driven architectures
- When data changes need to propagate across systems
- Building data lakes or warehouses with real-time updates
- Implementing CQRS with separate read and write databases

**When Not to Use**:
- Simple applications with a single database
- When batch processing is sufficient
- When the database doesn't support CDC
- When real-time updates aren't required

**Alternatives**: Batch ETL processes, Application-level events, Polling

### 4. Feature Toggles

**Concept**: Using configuration flags to enable or disable features without code deployments.

**Spring Implementation**:
```java
// Simple in-memory implementation
@Service
public class FeatureToggleService {
    
    private final Map<String, Boolean> toggles = new ConcurrentHashMap<>();
    
    public FeatureToggleService() {
        // Initialize default toggles
        toggles.put("FEATURE_NEW_CHECKOUT", false);
        toggles.put("FEATURE_RECOMMENDATIONS", true);
        toggles.put("FEATURE_DARK_MODE", false);
    }
    
    public boolean isEnabled(String featureKey) {
        return toggles.getOrDefault(featureKey, false);
    }
    
    public boolean isEnabled(String featureKey, String userId) {
        // For percentage rollouts or user-targeted features
        if (!toggles.containsKey(featureKey)) return false;
        
        if ("FEATURE_DARK_MODE".equals(featureKey)) {
            // Example of user-targeted toggle (e.g. based on user ID hash)
            return userId.hashCode() % 10 < 3; // 30% of users
        }
        
        return toggles.get(featureKey);
    }
    
    // Admin methods for changing toggle state
    public void setFeatureEnabled(String featureKey, boolean enabled) {
        toggles.put(featureKey, enabled);
    }
}

// Usage in a controller
@RestController
public class CheckoutController {
    
    @Autowired
    private FeatureToggleService featureToggleService;
    
    @Autowired
    private CheckoutService checkoutService;
    
    @Autowired
    private NewCheckoutService newCheckoutService;
    
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestBody CheckoutRequest request) {
        if (featureToggleService.isEnabled("FEATURE_NEW_CHECKOUT")) {
            return ResponseEntity.ok(newCheckoutService.processCheckout(request));
        } else {
            return ResponseEntity.ok(checkoutService.processCheckout(request));
        }
    }
    
    // Canary implementation - must match method name + "Canary"
    public ResponseEntity<OrderResponse> checkoutCanary(@RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(newCheckoutService.processCheckout(request));
    }
    
    // Alternative approach with feature toggle
    @PostMapping("/checkout/alternative")
    public ResponseEntity<OrderResponse> checkoutAlternative(@RequestBody CheckoutRequest request) {
        String clientId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (featureToggleService.isEnabled("FEATURE_NEW_CHECKOUT", clientId)) {
            return ResponseEntity.ok(newCheckoutService.processCheckout(request));
        } else {
            return ResponseEntity.ok(checkoutService.processCheckout(request));
        }
    }
}
```

**When to Use**:
- Gradual feature rollouts
- A/B testing
- Canary releases
- Hiding incomplete features in production
- Emergency feature disabling

**When Not to Use**:
- When toggle logic becomes too complex
- For long-term feature differentiation (better to refactor)
- When toggles are rarely changed (code bloat)
- When toggle state affects data persistence (can cause inconsistencies)

**Alternatives**: Branch deployments, separate services, version-specific routes

## Database Optimization Techniques

### 1. Database Sharding

**Concept**: Partitioning a database across multiple servers to improve scalability and performance.

**Spring Implementation**:
```java
// Configuration for multiple data sources
@Configuration
public class ShardingDataSourceConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard0")
    public DataSourceProperties shard0Properties() {
        return new DataSourceProperties();
    }
    
    @Bean
    public DataSource shard0DataSource() {
        return shard0Properties().initializeDataSourceBuilder().build();
    }
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.shard1")
    public DataSourceProperties shard1Properties() {
        return new DataSourceProperties();
    }
    
    @Bean
    public DataSource shard1DataSource() {
        return shard1Properties().initializeDataSourceBuilder().build();
    }
    
    @Bean
    public DataSource routingDataSource() {
        ShardingDataSource shardingDataSource = new ShardingDataSource();
        
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(0, shard0DataSource());
        dataSources.put(1, shard1DataSource());
        
        shardingDataSource.setTargetDataSources(dataSources);
        shardingDataSource.setDefaultTargetDataSource(shard0DataSource());
        
        return shardingDataSource;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(routingDataSource());
    }
}

// Custom routing data source
public class ShardingDataSource extends AbstractRoutingDataSource {
    
    @Override
    protected Object determineCurrentLookupKey() {
        return ShardingContextHolder.getShardId();
    }
}

// Thread-local context holder
public class ShardingContextHolder {
    private static final ThreadLocal<Integer> shardIdContext = new ThreadLocal<>();
    
    public static void setShardId(Integer shardId) {
        shardIdContext.set(shardId);
    }
    
    public static Integer getShardId() {
        return shardIdContext.get();
    }
    
    public static void clear() {
        shardIdContext.remove();
    }
}

// Service implementation
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getUserById(Long userId) {
        int shardId = calculateShardId(userId);
        try {
            ShardingContextHolder.setShardId(shardId);
            return userRepository.findById(userId).orElse(null);
        } finally {
            ShardingContextHolder.clear();
        }
    }
    
    private int calculateShardId(Long userId) {
        // Simple hash-based sharding
        return Math.abs(userId.hashCode() % 2); // For 2 shards
    }
}
```

**When to Use**:
- Large datasets exceeding single database capacity
- High write throughput requirements
- Geographic distribution of data
- Isolation of data for security or performance

**When Not to Use**:
- Small to medium datasets
- Complex queries spanning multiple shards
- When data consistency across shards is critical
- When joins across shards are common

**Alternatives**: Vertical scaling, NoSQL databases, Database clusters

### 2. Consistent Hashing

**Concept**: Distribution algorithm for data across nodes that minimizes redistribution when nodes are added or removed.

**Spring Implementation**:
```java
public class ConsistentHash<T> {
    
    private final HashFunction hashFunction;
    private final int numberOfReplicas;
    private final SortedMap<Integer, T> circle = new TreeMap<>();
    
    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas, Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numberOfReplicas = numberOfReplicas;
        
        for (T node : nodes) {
            add(node);
        }
    }
    
    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }
    
    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }
    
    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        
        int hash = hashFunction.hash(key);
        
        // If the hash is not in the circle, get first node after it
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        
 
        
        return circle.get(hash);
    }
    
    public interface HashFunction {
        int hash(Object key);
    }
    
    // Simple hash function implementation
    public static class MD5Hash implements HashFunction {
        @Override
        public int hash(Object key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(key.toString().getBytes());
                return ByteBuffer.wrap(digest).getInt();
            } catch (NoSuchAlgorithmException e) {
                return key.hashCode();
            }
        }
    }
}

// Usage in a cache service
@Service
public class DistributedCacheService {
    
    private final ConsistentHash<String> serverMap;
    private final Map<String, RestTemplate> serverClients = new HashMap<>();
    
    public DistributedCacheService() {
        List<String> cacheServers = Arrays.asList(
            "cache-server-1:8080",
            "cache-server-2:8080",
            "cache-server-3:8080"
        );
        
        this.serverMap = new ConsistentHash<>(new ConsistentHash.MD5Hash(), 10, cacheServers);
        
        // Initialize clients
        for (String server : cacheServers) {
            serverClients.put(server, new RestTemplate());
        }
    }
    
    public void put(String key, String value) {
        String server = serverMap.get(key);
        serverClients.get(server).postForObject(
            "http://" + server + "/cache/" + key,
            value,
            Void.class
        );
    }
    
    public String get(String key) {
        String server = serverMap.get(key);
        return serverClients.get(server).getForObject(
            "http://" + server + "/cache/" + key,
            String.class
        );
    }
    
    // Methods for adding/removing servers would also update the consistent hash
}
```

**When to Use**:
- Distributed caching systems
- Content delivery networks
- Distributed storage systems
- Load balancing across servers
- Sharded databases with dynamic scaling

**When Not to Use**:
- When the distribution doesn't need to change frequently
- Small-scale systems where simple sharding works well
- When data locality or affinity is more important than distribution

**Alternatives**: Fixed partitioning, Range partitioning, Modulo-based sharding

## Advanced Reliability Patterns

### 1. Rate Limiting

**Concept**: Controlling the rate of requests to protect services from overload.

**Spring Implementation**:
```java
// Using bucket4j for token bucket algorithm
@Configuration
public class RateLimitConfig {
    
    @Bean
    public Bucket anonymousBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
            .build();
    }
    
    @Bean
    public Bucket authenticatedBucket() {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(500, Refill.intervally(500, Duration.ofMinutes(1))))
            .build();
    }
}

@RestController
public class RateLimitedController {
    
    @Autowired
    private Bucket anonymousBucket;
    
    @Autowired
    private Bucket authenticatedBucket;
    
    @GetMapping("/api/resources")
    public ResponseEntity<?> getResources(
            @RequestHeader(value = "X-API-Key", required = false) String apiKey) {
        
        Bucket bucket = apiKey != null ? authenticatedBucket : anonymousBucket;
        
        if (bucket.tryConsume(1)) {
            // Process the request
            return ResponseEntity.ok(resourceService.getResources());
        } else {
            // Return rate limit exceeded response
            return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                       String.valueOf(bucket.getAvailableTokensInNextSecond()))
                .body("Rate limit exceeded. Try again later.");
        }
    }
}

// Aspect-based rate limiting
@Aspect
@Component
public class RateLimitAspect {
    
    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();
    
    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = extractKey(joinPoint, rateLimit);
        
        Bucket bucket = userBuckets.computeIfAbsent(key, k -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(rateLimit.limit(), 
                         Refill.intervally(rateLimit.limit(), Duration.ofSeconds(rateLimit.duration()))))
                .build()
        );
        
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new RateLimitExceededException("Rate limit exceeded for key: " + key);
        }
    }
    
    private String extractKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        // Implementation depends on the key strategy (IP, user ID, API key, etc.)
        return "default";
    }
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    int limit() default 10;
    int duration() default 60; // seconds
    String key() default ""; // SpEL expression to extract key
}
```

**When to Use**:
- Protecting APIs from abuse
- Ensuring fair resource distribution
- Preventing denial of service attacks
- Enforcing service tiers
- Managing downstream service load

**When Not to Use**:
- Internal services with predictable load
- When complex business logic already controls throughput
- When adding additional latency is problematic
- When global rate limiting across multiple instances is required

**Alternatives**: Throttling, Queueing, Load shedding, Backpressure

### 2. Retry Pattern

**Concept**: Automatically retrying operations that fail due to transient issues.

**Spring Implementation**:
```java
// Using Spring Retry
@Configuration
@EnableRetry
public class RetryConfig {
    
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);
        
        return retryTemplate;
    }
}

@Service
public class PaymentService {
    
    @Autowired
    private RetryTemplate retryTemplate;
    
    @Autowired
    private PaymentGatewayClient client;
    
    public PaymentResponse processPayment(PaymentRequest request) {
        return retryTemplate.execute(context -> {
            try {
                return client.processPayment(request);
            } catch (Exception e) {
                if (e instanceof ConnectionException) {
                    log.warn("Connection issue with payment gateway. Retrying... Attempt: {}", 
                            context.getRetryCount());
                    throw e; // Retry
                }
                throw new NonRetryableException("Non-retryable error", e);
            }
        });
    }
    
    // Method with annotation-based retry
    @Retryable(
        value = {ConnectionException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public PaymentResponse processPaymentWithAnnotation(PaymentRequest request) {
        return client.processPayment(request);
    }
    
    @Recover
    public PaymentResponse recoverFromFailure(ConnectionException e, PaymentRequest request) {
        // Fallback logic when retry exhausted
        log.error("All retries failed for payment processing", e);
        return new PaymentResponse(PaymentStatus.FAILED, "Gateway connection issue");
    }
}
```

**When to Use**:
- Network-related failures
- Transient service unavailability
- Race conditions that might resolve with retry
- Distributed systems with temporary inconsistencies

**When Not to Use**:
- Non-idempotent operations without idempotency keys
- When failure is likely permanent
- Operations that have side effects on retry
- When immediate failure feedback is required

**Alternatives**: Circuit breaker, Fallback mechanisms, Compensating transactions

### 3. Circuit Breaker Pattern

**Concept**: Preventing system overload by temporarily blocking calls to failing services.

**Spring Implementation**:
```java
// Using Resilience4j Circuit Breaker
@Configuration
public class CircuitBreakerConfiguration {
    
    @Bean
    public CircuitBreaker paymentServiceCircuitBreaker() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofMillis(10000))
            .permittedNumberOfCallsInHalfOpenState(5)
            .slidingWindowSize(10)
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .recordExceptions(IOException.class, TimeoutException.class)
            .build();
            
        return CircuitBreaker.of("paymentService", circuitBreakerConfig);
    }
    
    @Bean
    public TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
            .timeoutDuration(Duration.ofSeconds(2))
            .build();
    }
}

@Service
public class PaymentServiceWithCircuitBreaker {
    
    private final CircuitBreaker circuitBreaker;
    private final PaymentGatewayClient client;
    
    public PaymentServiceWithCircuitBreaker(
            CircuitBreaker paymentServiceCircuitBreaker, 
            PaymentGatewayClient client) {
        this.circuitBreaker = paymentServiceCircuitBreaker;
        this.client = client;
    }
    
    public PaymentResponse processPayment(PaymentRequest request) {
        return circuitBreaker.executeSupplier(() -> client.processPayment(request));
    }
    
    // With fallback
    public PaymentResponse processPaymentWithFallback(PaymentRequest request) {
        Supplier<PaymentResponse> paymentSupplier = () -> client.processPayment(request);
        
        Function<Throwable, PaymentResponse> fallbackFunction = throwable -> {
            log.error("Circuit breaker fallback for payment processing", throwable);
            return new PaymentResponse(PaymentStatus.PENDING, "Payment processing delayed");
        };
        
        return circuitBreaker.decorateSupplier(paymentSupplier)
                           .recover(fallbackFunction)
                           .get();
    }
}

// With Spring AOP
@Service
public class PaymentService {
    
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        return client.processPayment(request);
    }
    
    public PaymentResponse paymentFallback(PaymentRequest request, Exception e) {
        log.error("Circuit breaker fallback for payment processing", e);
        return new PaymentResponse(PaymentStatus.PENDING, "Payment processing delayed");
    }
}
```

**When to Use**:
- Preventing cascading failures in distributed systems
- Protecting systems from repeated calls to failing services
- Implementing graceful degradation
- When recovery needs to be automatic

**When Not to Use**:
- Single-instance applications
- Critical operations without fallback options
- When failures should be immediately visible to users
- Simple systems with direct dependencies

**Alternatives**: Timeouts, Bulkhead pattern, Load shedding

### 4. Bulkhead Pattern

**Concept**: Isolating resources to prevent one failing component from consuming all resources.

**Spring Implementation**:
```java
// Using Resilience4j Bulkhead
@Configuration
public class BulkheadConfig {
    
    @Bean
    public Bulkhead orderProcessingBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(20)
            .maxWaitDuration(Duration.ofMillis(500))
            .build();
            
        return Bulkhead.of("orderProcessing", config);
    }
    
    @Bean
    public Bulkhead paymentProcessingBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(10)
            .maxWaitDuration(Duration.ofMillis(1000))
            .build();
            
        return Bulkhead.of("paymentProcessing", config);
    }
}

@Service
public class OrderService {
    
    private final Bulkhead orderBulkhead;
    private final OrderProcessor orderProcessor;
    
    public OrderService(Bulkhead orderProcessingBulkhead, OrderProcessor orderProcessor) {
        this.orderBulkhead = orderProcessingBulkhead;
        this.orderProcessor = orderProcessor;
    }
    
    public OrderResult processOrder(Order order) {
        return orderBulkhead.executeSupplier(() -> orderProcessor.process(order));
    }
    
    // With fallback
    public OrderResult processOrderWithFallback(Order order) {
        Supplier<OrderResult> orderSupplier = () -> orderProcessor.process(order);
        
        Function<Throwable, OrderResult> fallbackFunction = throwable -> {
            if (throwable instanceof BulkheadFullException) {
                log.warn("Order processing bulkhead full, queueing order: {}", order.getId());
                orderQueueService.enqueue(order);
                return new OrderResult(OrderStatus.QUEUED, "Order queued for processing");
            }
            log.error("Error processing order", throwable);
            return new OrderResult(OrderStatus.ERROR, "Order processing failed");
        };
        
        return Try.ofSupplier(Bulkhead.decorateSupplier(orderBulkhead, orderSupplier))
                 .recover(fallbackFunction)
                 .get();
    }
}

// Using Spring AOP
@Service
public class PaymentService {
    
    @Bulkhead(name = "paymentProcessing", fallbackMethod = "paymentFallback")
    public PaymentResult processPayment(Payment payment) {
        return paymentProcessor.process(payment);
    }
    
    public PaymentResult paymentFallback(Payment payment, BulkheadFullException e) {
        log.warn("Payment processing bulkhead full, queueing payment: {}", payment.getId());
        paymentQueueService.enqueue(payment);
        return new PaymentResult(PaymentStatus.QUEUED, "Payment queued for processing");
    }
}
```

**When to Use**:
- Protecting shared resources in a system
- Preventing a single client from consuming all resources
- Isolating critical from non-critical operations
- When different operations have different resource needs

**When Not to Use**:
- Simple applications with few services
- When the operational overhead isn't justified
- When resources are already well-segregated at infrastructure level

**Alternatives**: Thread pool isolation, Connection pool limits, Semaphores

## Deployment & Release Strategies

### 1. Blue-Green Deployment

**Concept**: Maintaining two identical production environments for zero-downtime deployments.

**Spring Implementation**:
```java
// While not directly a Spring feature, here's how you might implement 
// support for blue-green in your Spring application:

@Configuration
public class DeploymentConfig {
    
    @Value("${app.deployment.environment:blue}")
    private String deploymentEnvironment;
    
    @Value("${app.feature.version:v1}")
    private String featureVersion;
    
    @Bean
    public DeploymentContext deploymentContext() {
        return new DeploymentContext(deploymentEnvironment, featureVersion);
    }
}

// Context holder for environment-specific logic
public class DeploymentContext {
    private final String environment;
    private final String featureVersion;
    
    public DeploymentContext(String environment, String featureVersion) {
        this.environment = environment;
        this.featureVersion = featureVersion;
    }
    
    public boolean isBlueEnvironment() {
        return "blue".equals(environment);
    }
    
    public boolean isGreenEnvironment() {
        return "green".equals(environment);
    }
    
    public String getFeatureVersion() {
        return featureVersion;
    }
}

// Database migration support
@Component
public class BlueGreenDatabaseMigration {
    
    @Autowired
    private DeploymentContext deploymentContext;
    
    @EventListener(ApplicationReadyEvent.class)
    public void migrateDatabase() {
        if (deploymentContext.isGreenEnvironment()) {
            // For green deployment, ensure backward compatibility with blue
            log.info("Running migrations for green environment");
            // Add new tables/columns without removing old ones
        } else {
            log.info("Running migrations for blue environment");
            // Standard migrations
        }
    }
}

// Feature version header for API responses
@ControllerAdvice
public class DeploymentEnvironmentAdvice {
    
    @Autowired
    private DeploymentContext deploymentContext;
    
    @ModelAttribute
    public void addDeploymentAttributes(HttpServletResponse response) {
        response.addHeader("X-Environment", deploymentContext.isBlueEnvironment() ? "blue" : "green");
        response.addHeader("X-Feature-Version", deploymentContext.getFeatureVersion());
    }
}
```

**When to Use**:
- Zero-downtime deployment requirements
- Critical systems that can't afford downtime
- When quick rollback capability is essential
- Complex applications where in-place updates are risky

**When Not to Use**:
- Simple applications where downtime is acceptable
- Resource-constrained environments (doubles resource needs)
- When database schema changes require complex coordination
- When infrastructure automation is limited

**Alternatives**: Canary deployments, Rolling updates, Feature toggles

### 2. Canary Deployment

**Concept**: Gradually rolling out changes to a small subset of users before full deployment.

**Spring Implementation**:
```java
// Custom annotation for marking canary endpoints
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Canary {
    String feature() default "";
    int percentage() default 10; // Default to 10% of traffic
}

// Aspect to handle canary routing
@Aspect
@Component
public class CanaryAspect {
    
    @Autowired
    private CanaryRoutingService routingService;
    
    @Around("@annotation(canary)")
    public Object routeToCanary(ProceedingJoinPoint joinPoint, Canary canary) throws Throwable {
        String feature = canary.feature();
        int percentage = canary.percentage();
        
        // Check if request should be routed to canary
        if (routingService.shouldRouteToCanary(feature, percentage)) {
            // Use the canary implementation
            return routingService.invokeCanaryImplementation(joinPoint, feature);
        }
        
        // Use the stable implementation
        return joinPoint.proceed();
    }
}

@Service
public class CanaryRoutingService {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    private final Random random = new Random();
    
    public boolean shouldRouteToCanary(String feature, int percentage) {
        // Simple percentage-based routing
        return random.nextInt(100) < percentage;
        
        // Could also use consistent hashing for user-based routing
        // Or feature flag system integration
    }
    
    public Object invokeCanaryImplementation(ProceedingJoinPoint joinPoint, String feature) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object target = joinPoint.getTarget();
        
        String canaryMethodName = method.getName() + "Canary";
        
        try {
            Method canaryMethod = target.getClass().getMethod(
                canaryMethodName,
                method.getParameterTypes()
            );
            
            return canaryMethod.invoke(target, joinPoint.getArgs());
        } catch (NoSuchMethodException e) {
            // Fallback to original method if canary implementation not found
            return joinPoint.proceed();
        }
    }
}

// Usage in a controller
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ProductServiceV2 productServiceV2; // Canary version
    
    @GetMapping("/{id}")
    @Canary(feature = "product-v2", percentage = 20)
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
    
    // Canary implementation - must match method name + "Canary"
    public ResponseEntity<Product> getProductCanary(@PathVariable Long id) {
        return ResponseEntity.ok(productServiceV2.findById(id));
    }
    
    // Alternative approach with feature toggle
    @GetMapping("/{id}/details")
    public ResponseEntity<ProductDetails> getProductDetails(@PathVariable Long id) {
        String clientId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        if (canaryRoutingService.isInCanaryGroup(clientId, "product-details-v2", 15)) {
            return ResponseEntity.ok(productServiceV2.getProductDetails(id));
        } else {
            return ResponseEntity.ok(productService.getProductDetails(id));
        }
    }
}
```

**When to Use**:
- Testing new features with real users
- When deployment risks need to be minimized
- Performance testing in production
- When full A/B testing infrastructure is overkill

**When Not to Use**:
- Features that can't be partially deployed
- When monitoring is insufficient to detect issues
- When fast rollout is needed
- When user experience must be consistent

**Alternatives**: Feature toggles, A/B testing, Blue-green deployment

### 3. Backend for Frontend (BFF) Pattern

**Concept**: Creating specialized API gateways for specific frontend applications.

**Spring Implementation**:
```java
// Mobile BFF
@RestController
@RequestMapping("/mobile-api")
public class MobileBffController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    // Optimized for mobile - returns essential data only
    @GetMapping("/products/{id}")
    public MobileProductDto getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return MobileProductDto.fromProduct(product);
    }
    
    // Aggregated endpoint - reduces round trips for mobile
    @GetMapping("/user-dashboard")
    public MobileDashboardDto getDashboard(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username);
        List<Order> recentOrders = orderService.getRecentOrders(username, 3);
        List<Product> recommendations = productService.getRecommendations(username, 5);
        
        return new MobileDashboardDto(
            MobileUserDto.fromUser(user),
            recentOrders.stream().map(MobileOrderDto::fromOrder).collect(Collectors.toList()),
            recommendations.stream().map(MobileProductDto::fromProduct).collect(Collectors.toList())
        );
    }
}

// Web BFF
@RestController
@RequestMapping("/web-api")
public class WebBffController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Detailed response with full product details for web
    @GetMapping("/products/{id}")
    public WebProductDto getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        List<Review> reviews = productService.getReviews(id);
        List<Product> relatedProducts = productService.getRelatedProducts(id);
        
        return WebProductDto.builder()
            .product(product)
            .reviews(reviews)
            .relatedProducts(relatedProducts)
            .build();
    }
    
    // Pagination suited for web display
    @GetMapping("/categories/{id}/products")
    public Page<WebProductDto> getProductsByCategory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size) {
        
        Category category = categoryService.findById(id);
        Page<Product> products = productService.findByCategory(category, PageRequest.of(page, size));
        
        return products.map(WebProductDto::fromProduct);
    }
}
```

**When to Use**:
- Different frontend clients with distinct needs (mobile, web, IoT)
- When you need to optimize API responses for specific platforms
- Aggregating multiple backend services for a frontend
- When backend services are too granular for direct client use

**When Not to Use**:
- Simple applications with a single frontend
- When all clients have similar data requirements
- When the added complexity of multiple API layers isn't justified
- When backend services already provide optimized interfaces

**Alternatives**: Generic API gateway, GraphQL, Client-side composition

### 4. Distributed Configuration Management

**Concept**: Centralizing and dynamically updating configuration across distributed services.

**Spring Implementation**:
```java
// Using Spring Cloud Config
// Config Server
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

// application.yml for Config Server
server:
  port: 8888
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/myorg/config-repo
          search-paths: '{application}'
          default-label: main

// Config Client
@SpringBootApplication
public class ConfigClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }
}

// bootstrap.yml for Config Client
spring:
  application:
    name: product-service
  cloud:
    config:
      uri: http://config-server:8888
      fail-fast: true

// Using @ConfigurationProperties
@Configuration
@ConfigurationProperties("product.service")
@RefreshScope
public class ProductServiceConfig {
    
    private int cacheTimeoutSeconds = 300;
    private int pageSize = 20;
    private boolean enableRecommendations = true;
    private List<String> supportedCurrencies = new ArrayList<>();
    
    // Getters and setters
}

// Using @Value
@Service
@RefreshScope
public class ProductService {
    
    @Value("${product.service.max-items-per-order:10}")
    private int maxItemsPerOrder;
    
    @Value("${product.service.min-order-amount:0}")
    private BigDecimal minOrderAmount;
    
    // Service methods using these values
}

// Controller for refreshing configuration
@RestController
public class ConfigController {
    
    @Autowired
    private ContextRefresher contextRefresher;
    
    @PostMapping("/actuator/refresh")
    public ResponseEntity<Map<String, Object>> refresh() {
        Set<String> refreshed = contextRefresher.refresh();
        Map<String, Object> result = new HashMap<>();
        result.put("refreshed", refreshed);
        return ResponseEntity.ok(result);
    }
}
```

**When to Use**:
- Microservice architectures with many services
- When configuration needs to change dynamically
- Environments with different configuration needs (dev, test, prod)
- When coordinated configuration changes are required

**When Not to Use**:
- Simple applications with minimal configuration
- When configuration rarely changes
- When the overhead of external configuration service isn't justified
- When configuration needs to be strictly tied to deployment

**Alternatives**: Environment variables, local configuration files, database-stored configuration

### 5. Service Mesh

**Concept**: Infrastructure layer for handling service-to-service communication.

**Spring Implementation**:
```java
// While service mesh is typically implemented at the infrastructure level
// (Istio, Linkerd, etc.), here's how you might integrate with it in Spring:

@Configuration
public class ServiceMeshIntegrationConfig {
    
    @Bean
    public MeterRegistry meterRegistry() {
        // Configure to expose metrics to service mesh
        CompositeMeterRegistry registry = new CompositeMeterRegistry();
        registry.add(new SimpleMeterRegistry());
        registry.add(new PrometheusMeterRegistry(PrometheusConfig.DEFAULT));
        return registry;
    }
    
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .filter((request, next) -> {
                // Add service mesh trace headers
                ClientRequest newRequest = ClientRequest.from(request)
                    .header("x-request-id", getRequestId())
                    .header("x-b3-traceid", getTraceId())
                    .header("x-b3-spanid", getSpanId())
                    .build();
                return next.exchange(newRequest);
            });
    }
    
    private String getRequestId() {
        // Get from current context or generate
        return MDC.get("requestId");
    }
    
    private String getTraceId() {
        return MDC.get("traceId");
    }
    
    private String getSpanId() {
        return MDC.get("spanId");
    }
}

// Health check endpoints for service mesh
@RestController
public class ServiceMeshHealthController {
    
    @GetMapping("/actuator/health/liveness")
    public Health getLivenessStatus() {
        // Basic health check for liveness probe
        return Health.up().build();
    }
    
    @GetMapping("/actuator/health/readiness")
    public Health getReadinessStatus() {
        // More comprehensive check for readiness probe
        return healthAggregator.getReadinessHealth();
    }
}

// Structured logging for service mesh
@Component
public class ServiceMeshLogConfiguration {
    
    @PostConstruct
    public void configureLogging() {
        MDCAdapter mdcAdapter = MDC.getMDCAdapter();
        
        // Default service information
        mdcAdapter.put("service", "product-service");
        mdcAdapter.put("version", "1.0.0");
    }
    
    @Bean
    public Filter requestContextFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request, 
                    HttpServletResponse response, 
                    FilterChain filterChain) throws ServletException, IOException {
                
                String requestId = request.getHeader("x-request-id");
                if (requestId == null) {
                    requestId = UUID.randomUUID().toString();
                }
                
                String traceId = request.getHeader("x-b3-traceid");
                String spanId = request.getHeader("x-b3-spanid");
                
                try {
                    MDC.put("requestId", requestId);
                    if (traceId != null) MDC.put("traceId", traceId);
                    if (spanId != null) MDC.put("spanId", spanId);
                    
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove("requestId");
                    MDC.remove("traceId");
                    MDC.remove("spanId");
                }
            }
        };
    }
}
```

**When to Use**:
- Large microservice architectures
- When you need consistent service-to-service communication patterns
- When observability across services is critical
- When security between services needs to be standardized

**When Not to Use**:
- Small applications with few services
- When the operational overhead isn't justified
- When teams need fine-grained control over communication patterns
- When network performance is absolutely critical

**Alternatives**: Client libraries, API gateway, Custom observability solutions

### 6. Data Streaming and Change Data Capture (CDC)

**Concept**: Streaming database changes as events for real-time processing and integration.

**Spring Implementation**:
```java
// Using Debezium with Spring Cloud Stream
@Configuration
public class DebeziumConfig {
    
    @Bean
    public io.debezium.config.Configuration debeziumConfig() {
        return io.debezium.config.Configuration.create()
            .with("name", "products-connector")
            .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
            .with("database.hostname", "postgres")
            .with("database.port", "5432")
            .with("database.user", "debezium")
            .with("database.password", "dbz")
            .with("database.dbname", "inventory")
            .with("database.server.name", "dbserver1")
            .with("table.include.list", "public.products")
            .with("plugin.name", "pgoutput")
            .build();
    }
    
    @Bean
    public DebeziumEngine<ChangeEvent<String, String>> debeziumEngine() {
        return DebeziumEngine.create(Json.class)
            .using(debeziumConfig().asProperties())
            .notifying(this::handleChangeEvent)
            .build();
    }
    
    private void handleChangeEvent(ChangeEvent<String, String> event) {
        if (event.value() == null) {
            log.info("Received delete event: {}", event.key());
            return;
        }
        
        try {
            JsonNode valueNode = new ObjectMapper().readTree(event.value());
            JsonNode payload = valueNode.get("payload");
            JsonNode after = payload.get("after");
            JsonNode before = payload.get("before");
            String op = payload.get("op").asText();
            
            switch (op) {
                case "c": // Create
                    handleCreateEvent(after);
                    break;
                case "u": // Update
                    handleUpdateEvent(before, after);
                    break;
                case "d": // Delete
                    handleDeleteEvent(before);
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing change event", e);
        }
    }
    
    private void handleCreateEvent(JsonNode data) {
        ProductEvent event = new ProductEvent(
            EventType.CREATED,
            data.get("id").asLong(),
            data.get("name").asText(),
            data.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    private void handleUpdateEvent(JsonNode before, JsonNode after) {
        ProductEvent event = new ProductEvent(
            EventType.UPDATED,
            after.get("id").asLong(),
            after.get("name").asText(),
            after.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    private void handleDeleteEvent(JsonNode data) {
        ProductEvent event = new ProductEvent(
            EventType.DELETED,
            data.get("id").asLong(),
            data.get("name").asText(),
            data.get("price").decimalValue()
        );
        productEventProducer.sendEvent(event);
    }
    
    @Bean(destroyMethod = "close")
    public EmbeddedEngine startDebeziumEngine(DebeziumEngine<ChangeEvent<String, String>> engine) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);
        
        return () -> {
            try {
                engine.close();
                executor.shutdownNow();
            } catch (Exception e) {
                log.error("Error closing Debezium engine", e);
            }
        };
    }
}

// Event producer using Spring Cloud Stream
@Component
public class ProductEventProducer {
    
    @Autowired
    private StreamBridge streamBridge;
    
    public void sendEvent(ProductEvent event) {
        streamBridge.send("productEvents-out-0", event);
    }
}

// Event consumer
@Component
public class ProductEventConsumer {
    
    @Autowired
    private ProductIndexService productIndexService;
    
    @Bean
    public Consumer<ProductEvent> processProductEvents() {
        return event -> {
            log.info("Received product event: {}", event);
            
            switch (event.getType()) {
                case CREATED:
                case UPDATED:
                    productIndexService.indexProduct(event);
                    break;
                case DELETED:
                    productIndexService.removeFromIndex(event.getId());
                    break;
            }
        };
    }
}
```

**When to Use**:
- Real-time data integration requirements
- Event-driven architectures
- When data changes need to propagate across systems
- Building data lakes or warehouses with real-time updates
- Implementing CQRS with separate read and write databases

**When Not to Use**:
- Simple applications with a single database
- When batch processing is sufficient
- When the database doesn't support CDC
- When real-time updates aren't required

**Alternatives**: Batch ETL processes, Application-level events, Polling
