# 200 Advanced Spring Boot Tricky Real-World Questions (with Answers & Examples)

Format per item:
Q: Tricky question
Context: Real-world situation
Common Pitfall: Typical wrong assumption
Answer: Concise resolution
Example: Short illustrative snippet (Java / YAML)
Key Takeaway: Memorable principle
---

### 1. Why does a @Bean method sometimes run twice during startup (leading to double initialization)?
Context: Using @Configuration(proxyBeanMethods = false) vs default with CGLIB.
Common Pitfall: Placing @Bean method inside a non-@Configuration class (e.g., @Component) causing direct invocation.
Answer: Only @Configuration classes use CGLIB to intercept @Bean calls; plain @Component + @Bean leads to each call creating a new instance.
Example:
```java
@Component // NOT @Configuration
class ExtraBeans { @Bean ServiceX x(){ return new ServiceX(); } }
```
Key Takeaway: Use @Configuration for @Bean factories; else, each invocation returns a new instance.

### 2. Why does @Transactional on a private method do nothing?
Context: Service has private helper annotated with @Transactional.
Common Pitfall: Expecting proxy to intercept private/internal calls.
Answer: Spring AOP proxies only intercept external public method calls; internal/private method calls bypass proxy.
Example:
```java
@Transactional private void internal(){ /* no tx actually */ }
```
Key Takeaway: Transactional boundaries must be on public methods invoked from outside the proxy.

### 3. Why is my @Transactional(readOnly=true) method still issuing UPDATE for JPA dirty fields?
Context: Method marked readOnly but entity modifications flushed.
Common Pitfall: Assuming readOnly forbids flush automatically.
Answer: readOnly=true optimizes some queries; JPA provider may still detect dirty state unless you avoid modifications or set FlushMode.MANUAL.
Example:
```java
@PersistenceContext EntityManager em;
em.setFlushMode(FlushModeType.COMMIT);
```
Key Takeaway: readOnly isn’t a write guard; it’s a hint—avoid mutating managed entities.

### 4. Why does @Scheduled method run twice in a clustered deployment?
Context: Two pods both executing scheduled job.
Common Pitfall: Expecting distributed coordination automatically.
Answer: @Scheduled is local; for single execution use leader election or distributed scheduler (ShedLock, Quartz DB store).
Example:
```xml
<dependency> com.github.kagkarlsson:db-scheduler </dependency>
```
Key Takeaway: @Scheduled is per-instance; add coordination for cluster uniqueness.

### 5. Why does enabling devtools cause application restarts on static resource access?
Context: Frequent restarts on editing front-end assets.
Common Pitfall: Packaging static directory inside monitored classpath triggers restart.
Answer: Devtools watches classpath; large static changes cause restart; move heavy assets outside classpath or disable restart for path.
Example:
```properties
spring.devtools.restart.exclude=static/**
```
Key Takeaway: Exclude volatile directories from restart triggers.

### 6. Why is a custom Converter<String, LocalDate> not invoked in JSON binding?
Context: Expecting global Converter to affect Jackson mapping.
Common Pitfall: Confusing Spring ConversionService with Jackson ObjectMapper.
Answer: Jackson uses its own serializers; register a Module or @JsonFormat instead.
Example:
```java
@Bean Module javaTime(){ return new JavaTimeModule(); }
```
Key Takeaway: Spring conversion != Jackson serialization.

### 7. Why does @ConfigurationProperties bean not bind values from environment?
Context: Fields remain null.
Common Pitfall: Missing @EnableConfigurationProperties or @Component scanning for the class.
Answer: Annotate with @ConfigurationProperties + @Component (or register via @EnableConfigurationProperties) & ensure setters or constructor binding.
Example:
```java
@ConfigurationProperties(prefix="app.feature")
public record FeatureProps(boolean enabled, int timeout) {}
```
Key Takeaway: Register properties class + use bindable structure (record/ setters).

### 8. Why does @RequestScope cause memory leak or high GC?
Context: Heavy objects in request scope referenced beyond request.
Common Pitfall: Capturing request-scoped bean in a singleton & storing.
Answer: Avoid retaining proxies or resolved request bean beyond scope; inject ObjectProvider to fetch per-request.
Example:
```java
@Autowired ObjectProvider<UserContext> userCtx;
```
Key Takeaway: Do not cache scoped proxies outside their lifecycle.

### 9. Why are two DataSource beans created unexpectedly?
Context: Added custom DataSource plus spring-boot-starter-data-jpa.
Common Pitfall: Boot auto-config still creates primary because properties present.
Answer: Exclude DataSourceAutoConfiguration or mark custom bean @Primary.
Example:
```java
@SpringBootApplication(exclude=DataSourceAutoConfiguration.class)
```
Key Takeaway: Override or exclude auto-config to avoid duplicate beans.

### 10. Why does Flyway migration run before dynamic secrets are available?
Context: Using Vault to fetch DB creds.
Common Pitfall: Flyway executes at context refresh earlier than secret retrieval.
Answer: Order via flyway.enabled=false then trigger after secret init, or implement FlywayMigrationStrategy.
Example:
```java
@Bean FlywayMigrationStrategy strategy(){ return Flyway::migrate; }
```
Key Takeaway: Control migration timing when credentials are dynamic.

### 11. Why does an actuator endpoint show 401 when security disabled for others?
Context: /actuator/health is public, /actuator/info 401.
Common Pitfall: Not configuring management.endpoints.web.exposure/include or security matcher.
Answer: Expose endpoints & configure permitAll for patterns.
Example:
```properties
management.endpoints.web.exposure.include=health,info
```
Key Takeaway: Exposure and security config both required.

### 12. Why is @WebMvcTest loading unwanted beans (slowing tests)?
Context: Additional services appear.
Common Pitfall: Putting @ComponentScan on test or including slice customizations pulling more beans.
Answer: Keep slice lean; avoid scanning common config or using @Import heavy configs.
Key Takeaway: Maintain isolation in test slices.

### 13. Why does @DataJpaTest fail due to missing embedded DB driver?
Context: No H2 dependency; using Postgres in prod.
Common Pitfall: Expecting Boot to magically provide embedded DB.
Answer: Add test runtime DB (H2) or configure Testcontainers DataSource.
Example:
```java
@Testcontainers static classBase { @Container PostgreSQLContainer<?> db=... }
```
Key Takeaway: Provide explicit test database dependency.

### 14. Why do lazy JPA relationships trigger N+1 even with @EntityGraph?
Context: Using repository method but still many selects.
Common Pitfall: Using findAll() then accessing relationships not part of entity graph.
Answer: Define custom query with fetch join or entity graph specifying attributes.
Example:
```java
@EntityGraph(attributePaths={"items"}) List<Order> findByStatus(...)
```
Key Takeaway: Fetch strategy must match access pattern; default findAll not optimized.

### 15. Why does caching not work on methods inside same class?
Context: @Cacheable method called by another method of same bean.
Common Pitfall: Self-invocation bypasses proxy.
Answer: Extract to separate bean or use AopContext/AspectJ mode.
Example:
```java
@EnableCaching(proxyTargetClass=true)
```
Key Takeaway: Proxy interception requires external call.

### 16. Why is a Feign client not picking up custom RequestInterceptor?
Context: Interceptor bean defined but not applied.
Common Pitfall: Defining interceptor in component scan outside Feign context package or missing @EnableFeignClients.
Answer: Ensure @EnableFeignClients scans package where interceptor bean is; or specify configuration attribute.
Key Takeaway: Feign client configuration isolation via configuration classes.

### 17. Why does @Transactional ignore rollback on checked exception?
Context: Throwing custom checked exception expecting rollback.
Common Pitfall: Assuming rollback on any exception.
Answer: By default rollback on runtime; specify rollbackFor for checked ones.
Example:
```java
@Transactional(rollbackFor=MyCheckedException.class)
```
Key Takeaway: Configure rollbackFor for checked exceptions.

### 18. Why is RedisCacheConfig ignoring custom TTL per cache entry?
Context: Setting @Cacheable unless TTL differs.
Common Pitfall: Expect dynamic TTL without customizing CacheManager.
Answer: Use CacheManager customizing per-cache configuration or use CachePut with manual ops.
Example:
```java
RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(5));
```
Key Takeaway: TTL is at cache config unless manually controlled.

### 19. Why does WebClient block thread unexpectedly?
Context: Mixing .block() inside reactive controller.
Common Pitfall: Blocking call on event-loop thread causing warnings.
Answer: Offload blocking calls to bounded elastic scheduler or avoid block.
Example:
```java
Mono.fromCallable(this::blocking).subscribeOn(Schedulers.boundedElastic())
```
Key Takeaway: Never block Netty event-loop; delegate to proper scheduler.

### 20. Why do @ControllerAdvice handlers not catch exceptions from WebFlux functional endpoints?
Context: Using RouterFunction.
Common Pitfall: Expecting annotation advice to apply.
Answer: For functional style, use .onError or implement ErrorWebExceptionHandler.
Key Takeaway: Annotation-based advice works for annotated controllers, not functional chain.

### 21. Why does enabling HTTP/2 not show multiplexing?
Context: Using Tomcat + JDK8.
Common Pitfall: HTTP/2 over cleartext (h2c) not automatically; JDK8 missing ALPN.
Answer: Need TLS + ALPN or use Jetty/Undertow; upgrade to JDK11+.
Key Takeaway: HTTP/2 requires ALPN; environment must support it.

### 22. Why do my @Async methods still run sequentially?
Context: Executor misconfigured returning single-thread pool.
Common Pitfall: Not defining a multi-thread TaskExecutor or forgetting @EnableAsync.
Answer: Provide an AsyncConfigurer or bean named taskExecutor with desired pool size.
Example:
```java
@Bean Executor taskExecutor(){ return Executors.newFixedThreadPool(8); }
```
Key Takeaway: Configure pool + enable annotation.

### 23. Why does @Async method not propagate SecurityContext?
Context: Security details lost in async thread.
Common Pitfall: Expecting automatic propagation.
Answer: Wrap executor with DelegatingSecurityContextAsyncTaskExecutor.
Example:
```java
new DelegatingSecurityContextAsyncTaskExecutor(original)
```
Key Takeaway: Explicitly propagate context for async.

### 24. Why is a global @ExceptionHandler overshadowed by default error page?
Context: HTML error body returned.
Common Pitfall: Missing @ResponseBody or using @Controller instead of @RestController.
Answer: Annotate handler with @ResponseBody or place in @RestControllerAdvice.
Key Takeaway: Response body advice needed for JSON error shape.

### 25. Why does customizing Jackson ObjectMapper not affect message converters?
Context: Defining new ObjectMapper bean but Boot still uses default.
Common Pitfall: Creating bean named differently or after converters initialized.
Answer: Provide a primary ObjectMapper bean or Jackson2ObjectMapperBuilderCustomizer.
Example:
```java
@Bean @Primary ObjectMapper mapper(){ return new ObjectMapper().findAndRegisterModules(); }
```
Key Takeaway: Override primary ObjectMapper early in context.

### 26. Why does @ConditionalOnProperty fail when property comes from environment variable with underscore?
Context: ENV VAR APP_FEATURE_ENABLED=true.
Common Pitfall: Using wrong relaxed binding name.
Answer: Map environment variable to property (app.feature.enabled) via relaxed binding; confirm property is loaded before condition evaluation.
Key Takeaway: Property naming & early evaluation order matter.

### 27. Why does @ConfigurationProperties validation not trigger?
Context: Using @Validated but no errors.
Common Pitfall: Missing JSR-303 provider or using primitive fields defaulting silently.
Answer: Include validation starter & use wrapper types; add constraint annotations.
Example:
```java
@Min(1) Integer poolSize;
```
Key Takeaway: Need validation dependency + constraints + non-primitives.

### 28. Why do metrics not appear after adding micrometer and actuator?
Context: /actuator/metrics empty.
Common Pitfall: Using management.metrics.export.* without enabling binder or missing dependency.
Answer: Add appropriate micrometer-registry & ensure endpoints exposed.
Example:
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
```
Key Takeaway: Registry + exposure required for visibility.

### 29. Why is a @Retryable method not retrying on Feign exception?
Context: FeignClient throws FeignException; no retry.
Common Pitfall: Missing @EnableRetry or exception not matching include.
Answer: Enable retry + specify include; ensure bean proxied (public method).
Example:
```java
@Retryable(include=FeignException.class, maxAttempts=3)
```
Key Takeaway: Enable retry & ensure exception matches specification.

### 30. Why does spring.main.lazy-initialization degrade actuator readiness?
Context: Lazy init on, readiness reports UP though some beans not validated.
Common Pitfall: Believing readiness ensures beans loaded.
Answer: Lazy delays bean creation until first use; readiness won't catch failures until runtime.
Key Takeaway: Lazy init trades startup speed for deferred failure detection.

### 31. Why do multiple @Order(Ordered.HIGHEST_PRECEDENCE) filters still execute unpredictably?
Context: Same order value given.
Common Pitfall: Expect strict order with identical order numbers.
Answer: When equal order, registration order determines; assign distinct precedence values.
Key Takeaway: Unique ordering ensures determinism.

### 32. Why is application failing to start after adding spring-boot-starter-security unexpectedly?
Context: 401 on all endpoints; missing login form.
Common Pitfall: Not adding security configuration, default denies everything.
Answer: Provide SecurityFilterChain bean customizing authorization.
Example:
```java
@Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
  return http.authorizeHttpRequests(a->a.requestMatchers("/public/**").permitAll().anyRequest().authenticated())
    .httpBasic().and().build(); }
```
Key Takeaway: Starter adds default deny-all; configure paths explicitly.

### 33. Why does @GeneratedValue(strategy=IDENTITY) cause batch insert inefficiency?
Context: Batch insertion of large lists slow.
Common Pitfall: Using IDENTITY prevents JDBC batching due to immediate key retrieval.
Answer: Switch to TABLE or SEQUENCE (with allocationSize) to allow batching.
Key Takeaway: IDENTITY breaks batching; prefer hi/lo or sequence.

### 34. Why is Hibernate second-level cache ignored despite configuration?
Context: Entities not cached.
Common Pitfall: Missing @Cache annotation or proper region factory dependency.
Answer: Add caching provider and annotate entities with @Cache(usage=...) (if provider supports).
Key Takeaway: Both provider + entity-level enablement required.

### 35. Why do ephemeral container restarts lose in-memory state despite caching abstraction?
Context: Using ConcurrentHashMapCacheManager.
Common Pitfall: Assuming durability of in-memory store in k8s.
Answer: Use external distributed cache (Redis) for cross-restart retention.
Key Takeaway: In-memory caches die with pod; externalize if persistence needed.

### 36. Why does Spring Cloud Config refresh not update @Value fields?
Context: /actuator/refresh invoked.
Common Pitfall: Expect @Value to dynamically refresh without @RefreshScope.
Answer: Use @RefreshScope on bean or switch to @ConfigurationProperties.
Key Takeaway: @Value fields are static once bound unless bean refresh-scoped.

### 37. Why does @PostConstruct run before transactional proxies applied?
Context: Accessing @Transactional method inside @PostConstruct failing transactional semantics.
Common Pitfall: Calling self-invoked method expecting TX active.
Answer: Proxy not yet applied; move logic to ApplicationRunner or separate bean.
Key Takeaway: Avoid self-invocation in lifecycle callbacks expecting AOP.

### 38. Why is OpenAPI/Swagger not showing pageable parameter properly?
Context: Using Page<T> in controller.
Common Pitfall: Missing springdoc-openapi data rest or not using @ParameterObject.
Answer: Annotate Pageable argument appropriately.
Example:
```java
public Page<User> list(@ParameterObject Pageable pageable)
```
Key Takeaway: Provide proper annotations for parameter model expansion.

### 39. Why does enabling reactive and servlet starters cause ambiguous Web stack?
Context: Added spring-boot-starter-web + spring-boot-starter-webflux.
Common Pitfall: Expect both to run simultaneously.
Answer: Boot chooses Spring MVC by default when both on classpath; reactive runs only on WebFlux alone or functional endpoints with config.
Key Takeaway: Avoid mixing reactive & MVC starters unless intentional fallback needed.

### 40. Why does @Modifying @Query not flush updates immediately?
Context: Expect data visible outside transaction.
Common Pitfall: Observing stale reads before commit.
Answer: Visibility only after commit; flush does not commit; for immediate read by others need separate transaction.
Key Takeaway: Commit boundary defines visibility, not flush alone.

### 41. Why does a lazy @Autowired cause NullPointerException in @PostConstruct?
Context: Field marked @Lazy.
Common Pitfall: Accessing before first proxy invocation.
Answer: Proxy injected but underlying target not initialized until call; ensure initialization order differently.
Key Takeaway: Avoid early logic requiring lazy bean fully initialized.

### 42. Why does custom HealthIndicator not show in aggregated health?
Context: Implemented HealthIndicator<?> but missing.
Common Pitfall: Name not ending with HealthIndicator or bean not registered.
Answer: Implement HealthIndicator and ensure bean registration; check management endpoint exposure.
Key Takeaway: Bean + exposure required for aggregator presence.

### 43. Why does @Transactional(propagation=REQUIRES_NEW) inside same transaction not open new TX?
Context: Self-invocation again.
Common Pitfall: Method called internally bypass proxy.
Answer: External call required for new transaction; restructure or inject self proxy.
Example:
```java
@Autowired private MyService self; self.requiresNew();
```
Key Takeaway: AOP proxies required for propagation semantics.

### 44. Why is startup time large after enabling spring.factories auto-configs not used?
Context: Many unused auto-configuration classes.
Common Pitfall: Not analyzing condition matches.
Answer: Use `--debug` to view condition evaluation; exclude unnecessary configs.
Key Takeaway: Trim auto-config for faster startup.

### 45. Why does RestTemplate timeout not apply?
Context: Set connectionRequestTimeout but not read.
Common Pitfall: Using SimpleClientHttpRequestFactory; set only connect timeout.
Answer: Use HttpComponentsClientHttpRequestFactory and set both connect & read timeouts.
Key Takeaway: Use appropriate factory supporting desired timeouts.

### 46. Why do duplicate logback configurations apply?
Context: logback.xml & logback-spring.xml both present.
Common Pitfall: Including multiple config files.
Answer: Keep only one; use logback-spring for profile conditions.
Key Takeaway: Single logging config to avoid duplication.

### 47. Why does property overriding via command line not reflect in @ConfigurationProperties bean bound early?
Context: Overridden after context started.
Common Pitfall: Changing property file post-start expecting live update.
Answer: Properties resolved at bind time; need refresh mechanism or environment reload.
Key Takeaway: Static binding unless dynamic refresh implemented.

### 48. Why are profiling-specific beans loaded outside target profile?
Context: Mistyped profile names.
Common Pitfall: Using @Profile("dev,qa") expecting OR semantics; means profile named exactly "dev,qa".
Answer: Use @Profile({"dev","qa"}).
Key Takeaway: Strings array for multiple profiles, not comma inside one string.

### 49. Why does @JsonIgnore on field not hide value in response?
Context: Lombok + Jackson.
Common Pitfall: Accessors overshadow field annotation; need annotate getter.
Answer: Place @JsonIgnore on accessor or use @JsonProperty(access = WRITE_ONLY).
Key Takeaway: Annotate access path used by serialization.

### 50. Why is Boot not picking up custom banner.txt in jar?
Context: banner.txt under src/main/resources/config.
Common Pitfall: Wrong path; Boot loads root or classpath:banner.txt.
Answer: Place at classpath root or configure banner.location.
Key Takeaway: Correct resource path or property required.

### 51. Why is distributed tracing header lost across async boundaries?
Context: Using Sleuth / Micrometer tracing with @Async.
Common Pitfall: Not enabling context propagation.
Answer: Use TaskExecutor bean instrumented by tracing library or DelegatingTracingExecutor.
Key Takeaway: Use instrumented executors to preserve trace context.

### 52. Why does customizing server.port via EnvironmentPostProcessor not work?
Context: Changing after web server initialized.
Common Pitfall: Late mutation insufficient.
Answer: Modify property before context refresh in postProcessor; ensure on spring.factories registration.
Key Takeaway: Early phase required for core server properties.

### 53. Why does binding list properties produce empty list?
Context: application.yml with `app.items: a,b,c` but list empty.
Common Pitfall: Using comma list with YAML expecting array.
Answer: Use proper YAML list syntax or relaxed binding `app.items: a,b,c` works for @Value but not for @ConfigurationProperties list if mis-typed.
Key Takeaway: Correct YAML list or comma-delimited based on binder expectations.

### 54. Why do tests intermittently fail due to leftover data between @DataJpaTest methods?
Context: Using @Transactional tests expecting rollback.
Common Pitfall: Using @Commit or altering transaction inadvertently.
Answer: Ensure default rollback true; avoid manual commit inside test.
Key Takeaway: Keep test modifications inside transactional boundary.

### 55. Why do integration tests hang on context close?
Context: Non-daemon threads created (schedulers).
Common Pitfall: Forgetting to shutdown custom ExecutorService.
Answer: Manage lifecycle via @PreDestroy or use TaskScheduler beans managed by Spring.
Key Takeaway: Register thread pools as beans for automatic graceful shutdown.

### 56. Why does JPA optimistic locking not trigger on stale update?
Context: Missing @Version field.
Common Pitfall: Expecting version check without annotation.
Answer: Add @Version attribute to entity.
Key Takeaway: Version column required for optimistic locking.

### 57. Why is memory usage high with large JSON responses using Jackson streaming incorrectly?
Context: Reading entire body into memory.
Common Pitfall: Using ObjectMapper.readValue on huge arrays.
Answer: Use streaming API / MappingIterator.
Example:
```java
try (var p = mapper.getFactory().createParser(src)) { ... }
```
Key Takeaway: Stream large payloads to avoid full materialization.

### 58. Why does concurrency test show lost updates despite @Transactional?
Context: Race conditions updating same row.
Common Pitfall: Assuming @Transactional ensures serialization.
Answer: Use optimistic/pessimistic locking, or database constraints.
Key Takeaway: Transaction does not imply isolation beyond default READ_COMMITTED.

### 59. Why does migrating to Java 17 break reflective access in native image build?
Context: GraalVM native build fails on reflection.
Common Pitfall: Missing reflective configuration.
Answer: Use Spring AOT / `native-image-agent` to generate reflect-config.json.
Key Takeaway: Native images need explicit reflection metadata.

### 60. Why does actuator /shutdown not appear?
Context: Expecting endpoint for remote shutdown.
Common Pitfall: Not enabling it; disabled by default.
Answer: Set `management.endpoint.shutdown.enabled=true` and expose.
Key Takeaway: Explicit enable + exposure required for sensitive endpoints.

### 61. Why does WebFlux return 415 for multipart upload?
Context: Missing decoder.
Common Pitfall: Not including spring-boot-starter-webflux with multipart config.
Answer: Add `spring.codec.multipart.max-in-memory-size` etc and ensure correct content type.
Key Takeaway: Ensure necessary codecs & content type.

### 62. Why do property placeholders in logback-spring.xml not resolve?
Context: Using ${app.name} early.
Common Pitfall: Property not yet loaded or missing springProperty scope.
Answer: Use `<springProperty>` or ensure property available via application.properties.
Key Takeaway: Logging config requires spring-aware syntax.

### 63. Why does injecting prototype bean into singleton not create new instances each call?
Context: Prototype expected each use.
Common Pitfall: Direct field injection caches instance.
Answer: Use ObjectProvider or method lookup injection.
Example:
```java
@Autowired ObjectProvider<Widget> widgetProvider; widgetProvider.getObject();
```
Key Takeaway: Prototype retrieval must be on demand.

### 64. Why is CORS preflight failing despite global config?
Context: Using WebFlux + MVC config confusion.
Common Pitfall: Configuring only MVC for reactive stack.
Answer: Use WebFlux CORS mapping or gateway filters appropriately.
Key Takeaway: Use correct stack-specific CORS configuration.

### 65. Why does spring.jpa.show-sql hide parameter values?
Context: Want to see actual bound parameters.
Common Pitfall: Using show-sql expecting parameters; only shows placeholders.
Answer: Enable logging for `org.hibernate.type.descriptor.sql` or use `format_sql` & `logging.level.org.hibernate.SQL=debug`.
Key Takeaway: Parameter logging requires specific logger categories.

### 66. Why are actuator metrics duplicated when using both micrometer-registry-prometheus and manually creating a PrometheusMeterRegistry?
Context: Double registration.
Common Pitfall: Creating second registry bean.
Answer: Allow Boot auto-registry only; remove manual bean.
Key Takeaway: Avoid duplicate registry instantiation.

### 67. Why does Resilience4j circuit breaker not wrap Feign calls?
Context: Missing annotations or config.
Common Pitfall: Expect auto integration without dependency.
Answer: Add resilience4j-spring-boot2 & annotate method with @CircuitBreaker(name="client") or use Feign decorator.
Key Takeaway: Explicit binding of resilience to call site required.

### 68. Why are scheduled tasks delayed under heavy CPU load?
Context: Single-threaded scheduler overloaded.
Common Pitfall: Using default single thread TaskScheduler.
Answer: Provide ThreadPoolTaskScheduler with pool size >1.
Key Takeaway: Scale scheduler threads to workload.

### 69. Why is Liquibase changelog not applied in test while Flyway works?
Context: Using Flyway starter only.
Common Pitfall: Having Liquibase files but Flyway active.
Answer: Choose one migration tool or enable Liquibase; Boot disables Liquibase if Flyway present.
Key Takeaway: Only one migration tool active by default; configure differently to use both.

### 70. Why does binding to server.address=0.0.0.0 still not accessible externally in container?
Context: Running docker, port not open.
Common Pitfall: Not publishing port with docker run -p.
Answer: Expose container port; env config correct but host mapping missing.
Key Takeaway: Container networking requires host port mapping.

### 71. Why does a reactive repository call inside @Transactional annotate cause blocking?
Context: Using R2DBC vs JPA mixing.
Common Pitfall: Mixing imperative JPA transaction with reactive pipeline.
Answer: Use R2dbcTransactionManager for reactive; avoid bridging sync JPA.
Key Takeaway: Don’t mix reactive and blocking transaction managers.

### 72. Why does context refresh time spike with many conditional beans?
Context: Hundreds of @Conditional beans.
Common Pitfall: Complex condition evaluation overhead.
Answer: Consolidate conditions, reduce scanning packages.
Key Takeaway: Limit conditional complexity for faster startup.

### 73. Why is Spring Security ignoring custom PasswordEncoder bean?
Context: Using default delegating encoder.
Common Pitfall: Creating bean after security auto-config.
Answer: Define PasswordEncoder bean early; ensure no conflicting bean names.
Key Takeaway: Provide single PasswordEncoder bean prior to security config.

### 74. Why does test using @SpringBootTest(webEnvironment=RANDOM_PORT) not pick test profile?
Context: Expecting application-test.properties.
Common Pitfall: Missing @ActiveProfiles("test").
Answer: Add @ActiveProfiles or set spring.profiles.active in test properties.
Key Takeaway: Activate profile explicitly for tests.

### 75. Why is Feign client ignoring compression?
Context: Gzip expected.
Common Pitfall: Not enabling compression properties.
Answer: Set `feign.compression.request.enabled=true` & response enabled with min size.
Key Takeaway: Enable compression separately for request/response.

### 76. Why does request body read twice cause IllegalStateException?
Context: Logging filter reading InputStream then controller reading again.
Common Pitfall: Consuming stream without caching wrapper.
Answer: Wrap request with ContentCachingRequestWrapper.
Key Takeaway: Cache body if multiple reads needed.

### 77. Why does customizing Undertow thread counts not reflect?
Context: Config properties not applied.
Common Pitfall: Using Tomcat starter; Undertow not active.
Answer: Switch dependency to undertow starter.
Key Takeaway: Only active server's properties apply.

### 78. Why is @ResponseStatus on exception ignored with @ControllerAdvice using ResponseEntityExceptionHandler?
Context: Status different from expectation.
Common Pitfall: Overridden handle methods supersede annotations.
Answer: Ensure not overriding or explicitly set status in handler.
Key Takeaway: Handler precedence can override annotation-based status.

### 79. Why does spring-boot-starter-validation not validate nested lists in @RequestBody?
Context: List<Dto> not validated.
Common Pitfall: Missing @Valid on list elements.
Answer: Use @Valid on collection field or method param.
Example:
```java
public void save(@RequestBody @Valid List<ItemDto> items)
```
Key Takeaway: @Valid needed at collection boundary.

### 80. Why does @CacheEvict(allEntries=true) not clear after transactional failure?
Context: Cache cleared but transaction rolled back.
Common Pitfall: Eviction occurs before rollback; losing cached data.
Answer: Use beforeInvocation=false (default) ensures eviction only after successful invocation; ensure rollback semantics align.
Key Takeaway: Evictions occur post-success by default; align with transaction outcome.

### 81. Why does an integration test fail with random port already in use?
Context: Parallel tests.
Common Pitfall: Using fixed server.port in tests.
Answer: Use RANDOM_PORT and avoid static port or enforce dynamic assignment.
Key Takeaway: Randomize ports in parallel test scenarios.

### 82. Why is @EnableJpaAuditing not setting createdDate?
Context: Field remains null.
Common Pitfall: Missing @EntityListeners(AuditingEntityListener.class) or annotation @CreatedDate.
Answer: Add auditing annotations & ensure auditing enabled.
Key Takeaway: Auditing requires both enabling + field annotations.

### 83. Why does customizing ObjectMapper feature disable Boot defaults (e.g., JavaTimeModule)?
Context: New ObjectMapper ignoring modules.
Common Pitfall: Not calling findAndRegisterModules or customizing builder.
Answer: Use Jackson2ObjectMapperBuilderCustomizer to modify defaults.
Key Takeaway: Customize builder not replacing entire mapper unless re-register modules.

### 84. Why does WebClient DNS caching cause stale endpoint usage?
Context: Kubernetes Pod IP changed.
Common Pitfall: Rely on default JDK DNS TTL infinite.
Answer: Configure networkaddress.cache.ttl or use reactor-netty name resolver customizing TTL.
Key Takeaway: Tune DNS TTL for dynamic infrastructure.

### 85. Why does request tracing show broken spans when using custom ExecutorService?
Context: Manual thread pool.
Common Pitfall: Not wrapping tasks with tracing context.
Answer: Use ContextPropagatingExecutor or instrumentation library.
Key Takeaway: Propagate trace context explicitly with custom executors.

### 86. Why do Kotlin data classes not bind properly with @ConfigurationProperties?
Context: Null values or binding errors.
Common Pitfall: Missing constructor binding annotation or using val with no default.
Answer: Use @ConstructorBinding (Boot 2.x) or record style in Java, ensure non-nullable fields have values.
Key Takeaway: Provide constructor binding or defaults for Kotlin immutability.

### 87. Why is reactive chain unsubscribed prematurely in controller returning Mono<Void>?
Context: Side effects missing.
Common Pitfall: Not returning the publisher from last operation; side effect lost.
Answer: Chain operations and return the final Mono.
Key Takeaway: Only returned publisher is subscribed.

### 88. Why does Boot not pick testcontainers service properties automatically for DataSource?
Context: Container started but AutoConfig uses default local DB.
Common Pitfall: Not setting spring.datasource.* from container.
Answer: Use @DynamicPropertySource to register container properties.
Key Takeaway: Dynamically propagate container connection props.

### 89. Why is @Value default expression not used when property missing?
Context: Using `@Value("${missing:default}")` but got exception.
Common Pitfall: Typo in placeholder or referencing nested missing property without default.
Answer: Ensure all nested placeholders have defaults.
Key Takeaway: Provide defaults for each placeholder segment.

### 90. Why does enabling GZip cause minimal size responses not compressed?
Context: Expect all responses compressed.
Common Pitfall: Not adjusting min-response-size.
Answer: Configure `server.compression.min-response-size`.
Key Takeaway: Compression threshold determines eligibility.

### 91. Why does customizing mapping for /error produce recursion?
Context: Overriding ErrorController mapping at /error.
Common Pitfall: Throwing exception again inside error mapping.
Answer: Avoid generating error again; produce stable fallback response.
Key Takeaway: Error path must not re-throw causing loop.

### 92. Why does replacing default DataSource break Spring Batch metadata initialization?
Context: Batch tables missing.
Common Pitfall: Not enabling batch schema initialization.
Answer: Set `spring.batch.jdbc.initialize-schema=always` or provide schema.
Key Takeaway: Batch metadata requires schema initialization or manual provisioning.

### 93. Why does Boot devtools disable template caching but not static resource caching?
Context: Expect immediate static updates.
Common Pitfall: Browser caching interfering.
Answer: Static resource caching controlled by `spring.web.resources.cache` and HTTP headers.
Key Takeaway: Disable browser caching separately for rapid iteration.

### 94. Why do environment-specific property overrides not apply when using config tree (K8s) with same priority?
Context: Two sources have same key.
Common Pitfall: Assuming order deterministic.
Answer: PropertySource order determines precedence; adjust order or naming.
Key Takeaway: Control PropertySource precedence for overrides.

### 95. Why is @Lookup method returning null?
Context: Abstract method on component not proxied.
Common Pitfall: Non-abstract or final method prevents CGLIB override.
Answer: Ensure method is abstract (or not final) & bean is proxied.
Key Takeaway: @Lookup relies on method override by CGLIB.

### 96. Why does using MapStruct with componentModel=spring create circular dependency issues?
Context: Mappers referencing services referencing mappers.
Common Pitfall: Injecting heavy services into mappers.
Answer: Keep mappers pure; avoid injecting service layers; create dedicated decorator if needed.
Key Takeaway: Mappers should be stateless converters.

### 97. Why do application events not arrive in @EventListener asynchronously despite @Async on method?
Context: Expect async event handling.
Common Pitfall: Missing @EnableAsync.
Answer: Add @EnableAsync and configure executor.
Key Takeaway: @Async requires enabling infrastructure.

### 98. Why is binding of Map<String,String> losing certain keys with periods?
Context: Keys like "a.b".
Common Pitfall: Period interprets nested properties.
Answer: Escape or use bracket notation: `map["a.b"]=value`.
Key Takeaway: Dots indicate nesting; escape for literal keys.

### 99. Why does migrating to Spring Boot 3 break javax.* imports?
Context: Compilation errors.
Common Pitfall: Not updating to jakarta.* namespace.
Answer: Replace javax with jakarta packages & update dependencies.
Key Takeaway: Boot 3 requires Jakarta EE 9 namespace.

### 100. Why does customizing Tomcat connector (port) not apply when running in native image?
Context: Changing port programmatically.
Common Pitfall: Modifying after context refresh.
Answer: Customize via WebServerFactoryCustomizer bean before start.
Key Takeaway: Early customization hook required.

### 101. Why does caching with SpEL condition referencing method param fail with NullPointerException?
Context: Null param used in SpEL.
Common Pitfall: SpEL expression not guarding null.
Answer: Use safe navigation or condition param!=null.
Example:
```java
@Cacheable(condition="#id != null")
```
Key Takeaway: Guard nulls in SpEL expressions.

### 102. Why does migrating to Spring Boot 3 break security configuration using WebSecurityConfigurerAdapter?
Context: Class removed.
Common Pitfall: Not adopting component-based SecurityFilterChain.
Answer: Replace adapter with bean returning SecurityFilterChain.
Key Takeaway: New security config style mandatory in Boot 3.

### 103. Why is WebTestClient not seeing controller advice responses?
Context: Using standaloneSetup.
Common Pitfall: Not registering controller advice in mock server.
Answer: Configure with `bindToController().controllerAdvice(...)`.
Key Takeaway: Register advice in test binding.

### 104. Why does reactive timeout not cancel downstream DB query?
Context: R2DBC query continues.
Common Pitfall: Timeout applied at layer above DB driver not honoring cancellation.
Answer: Ensure driver supports cancellation; apply timeout operator earlier.
Key Takeaway: Cancellability depends on driver support.

### 105. Why is resource chain not fingerprinting static assets?
Context: Cache busting not working.
Common Pitfall: Missing `spring.web.resources.chain.strategy.content.enabled=true`.
Answer: Enable content strategy and chain.
Key Takeaway: Activate resource chain strategies for hashing.

### 106. Why do multiple OpenFeign retry mechanisms conflict?
Context: Feign + Resilience4j + custom retryer.
Common Pitfall: Double retries increasing latency.
Answer: Disable Feign built-in Retryer or unify with resilience.
Key Takeaway: Avoid layered redundant retries.

### 107. Why does Hibernate generate cross join queries unexpectedly?
Context: Fetch multiple LAZY associations.
Common Pitfall: Accessing collections causing N+1 or cross join due to fetch mode.
Answer: Use fetch join or entity graph to optimize; avoid EAGER on many associations.
Key Takeaway: Plan fetch strategy to prevent cartesian explosion.

### 108. Why is memory leak detected with large number of ClassLoader entries after many restarts (devtools)?
Context: Frequent restarts leak metaspace.
Common Pitfall: Holding static references to application classes in third-party libs.
Answer: Avoid static caches referencing context classes or disable devtools restart.
Key Takeaway: Static references block class unloading.

### 109. Why does asynchronous logging (Logback) reorder log lines across threads?
Context: Using AsyncAppender.
Common Pitfall: Expect strict chronological ordering.
Answer: Async decouples emission; ordering only guaranteed per thread.
Key Takeaway: Async logging trades order for throughput.

### 110. Why does ResponseEntity<byte[]> cause high memory for large file responses?
Context: Byte array built fully.
Common Pitfall: Materializing file in memory.
Answer: Use StreamingResponseBody or InputStreamResource.
Key Takeaway: Stream large responses to avoid buffer overhead.

### 111. Why does enabling Hibernate batch fetch size still yield many selects?
Context: Setting `hibernate.default_batch_fetch_size`.
Common Pitfall: Not using LAZY ManyToOne/OneToOne which batch can optimize.
Answer: Apply @BatchSize or use fetch graph; ensure Accessing triggers batch.
Key Takeaway: Batch size effective for specific association patterns.

### 112. Why is bean not post-processed by custom BeanPostProcessor?
Context: Processor logs missing.
Common Pitfall: Processor declared after bean instantiation or conditional excludes.
Answer: Ensure post-processor is registered early (no lazy) and not conditional false.
Key Takeaway: Post-processors must load before target beans.

### 113. Why does path variable decoding differ between Tomcat & Undertow?
Context: Encoded slashes preserved differently.
Common Pitfall: Assuming uniform behavior.
Answer: Configure server-specific decoding settings.
Key Takeaway: Servlet containers vary in path normalization.

### 114. Why does HikariCP not show leak detection warnings?
Context: Expect warnings.
Common Pitfall: Not setting leakDetectionThreshold.
Answer: Configure threshold in ms.
Key Takeaway: Leak detection disabled unless threshold set.

### 115. Why does @TransactionalEventListener not fire for events published in same transaction when phase AFTER_COMMIT?
Context: Observing absence.
Common Pitfall: Transaction rolled back or not actually transactional.
Answer: Ensure transaction commits successfully; use BEFORE_COMMIT for earlier.
Key Takeaway: AFTER_COMMIT only triggers on successful commit boundaries.

### 116. Why does customizing Jackson for Kotlin nulls still fail on missing fields?
Context: Missing field mapping to non-null property.
Common Pitfall: Not enabling Kotlin module.
Answer: Add `jackson-module-kotlin` and ensure proper configuration `fail-on-null-for-primitives`.
Key Takeaway: Kotlin integration module required for accurate null handling.

### 117. Why is reactive controller returning 406 Not Acceptable?
Context: No acceptable media type.
Common Pitfall: Missing produces or Accept header mismatch.
Answer: Add proper `produces = MediaType.APPLICATION_JSON_VALUE`.
Key Takeaway: Content negotiation drives selection.

### 118. Why does customizing BeanFactoryPostProcessor not affect beans annotated with @ConfigurationProperties?
Context: Attempt property override.
Common Pitfall: Modification after binding occurs.
Answer: Use EnvironmentPostProcessor or PropertySource early.
Key Takeaway: Choose correct phase for property mutation.

### 119. Why does enabling CSRF break PATCH/DELETE requests in tests?
Context: 403 errors.
Common Pitfall: Forgetting csrf() tokens.
Answer: Provide csrf token in test or disable for stateless API.
Key Takeaway: Provide or disable CSRF when using non-browser clients.

### 120. Why is Boot slow on cold start in Docker with many JAR layers?
Context: Layered fat jar.
Common Pitfall: Single large layer invalidated on each change.
Answer: Use layered jar feature to separate dependencies.
Key Takeaway: Layering improves incremental image rebuild.

### 121. Why does @ConstructorBinding not work after Boot 3 migration?
Context: Annotation removed.
Common Pitfall: Still using @ConstructorBinding.
Answer: Constructor binding is default for single constructor classes; remove annotation.
Key Takeaway: Boot 3 simplifies constructor binding usage.

### 122. Why is custom ValidationMessageSource ignored?
Context: Using messages.properties.
Common Pitfall: Not naming bean messageSource.
Answer: Provide bean named messageSource & configure LocalValidatorFactoryBean.
Key Takeaway: Standard bean naming triggers validation message usage.

### 123. Why do @RestClientTest tests load full application context?
Context: Using incorrect annotations.
Common Pitfall: Using @SpringBootTest instead of slice.
Answer: Use @RestClientTest(YourClient.class) for isolating.
Key Takeaway: Choose slice annotation for minimal context.

### 124. Why does Boot ignore spring.config.import for configserver when not prefixed with optional:?
Context: Startup failure if server down.
Common Pitfall: Not marking optional.
Answer: Use `spring.config.import=optional:configserver:` for tolerant startup.
Key Takeaway: Optional prefix allows fallback.

### 125. Why does repeated @Bean close/open cause channel leak for RabbitMQ?
Context: Recreating ConnectionFactory on refresh.
Common Pitfall: Not configuring singletons.
Answer: Ensure connection factory is singleton; avoid dynamic recreation.
Key Takeaway: Manage messaging resources as singletons.

### 126. Why is reusing WebClient across tenants leaking headers?
Context: Mutable default headers.
Common Pitfall: Modifying builder after build.
Answer: Use builder.clone() per tenant or build immutable clients.
Key Takeaway: Avoid mutating shared client state.

### 127. Why does scaling sessions with Spring Session & Redis produce stale session attributes?
Context: Race conditions multi write.
Common Pitfall: Large session objects mutated concurrently.
Answer: Minimize session size & use delta updates.
Key Takeaway: Keep sessions small & atomic to avoid race overwrites.

### 128. Why does migrating to record-based DTOs break Jackson polymorphism?
Context: Type info missing.
Common Pitfall: Not adding @JsonTypeInfo.
Answer: Annotate base type with type info.
Key Takeaway: Polymorphic deserialization requires explicit metadata.

### 129. Why does ResponseStatusException not map to custom error body?
Context: Expect custom error JSON.
Common Pitfall: Not customizing ErrorAttributes.
Answer: Implement ErrorAttributes bean.
Key Takeaway: Override default error attribute mapping for custom bodies.

### 130. Why does @Sql test annotation not run script before schema init?
Context: Failing due missing table.
Common Pitfall: Execution phase mismatch.
Answer: Use @Sql before test method or ensure schema creation earlier.
Key Takeaway: Order scripts relative to schema generation.

### 131. Why is Reactor context not propagated across flatMap with blocking bridging?
Context: Using `publishOn` with blocking call.
Common Pitfall: Losing context switching threads.
Answer: Use `contextWrite` and avoid blocking; use `deferContextual`.
Key Takeaway: Reactor context tied to asynchronous chain; blocking breaks chain.

### 132. Why does Micrometer timer produce unrealistic high durations under test?
Context: Using virtual time or simulated delays.
Common Pitfall: Not resetting registry between tests.
Answer: Clear metrics or new SimpleMeterRegistry per test.
Key Takeaway: Isolate metric registries in tests.

### 133. Why is WebSocket handshake failing behind reverse proxy?
Context: 400 upgrade required.
Common Pitfall: Missing forwarded headers config.
Answer: Configure server.forward-headers-strategy=native & proxy pass upgrade headers.
Key Takeaway: Preserve upgrade headers through proxy.

### 134. Why does property deprecation warning appear for property I never set?
Context: Boot logs deprecation.
Common Pitfall: Transitive dependency sets default.
Answer: Inspect `--debug` condition evaluation report.
Key Takeaway: Transitive autoconfig may set deprecated properties.

### 135. Why do nested transactions not rollback inner changes only?
Context: Using PROPAGATION_NESTED expecting partial rollback.
Common Pitfall: Database not supporting savepoints.
Answer: Use a DataSource supporting savepoints (e.g., JDBC driver) & ensure nested propagation.
Key Takeaway: Nested relies on savepoints; driver support required.

### 136. Why does enabling HTTP trace leak sensitive authorization headers?
Context: Actuator httptrace endpoint.
Common Pitfall: Exposing headers unfiltered.
Answer: Customize HttpTraceRepository filtering headers.
Key Takeaway: Sanitize traces before exposure.

### 137. Why are context events delivered out of expected order?
Context: Listening to ApplicationPreparedEvent vs ApplicationStartedEvent.
Common Pitfall: Using wrong event types for timing.
Answer: Use correct lifecycle event for needed phase.
Key Takeaway: Choose lifecycle event intentionally.

### 138. Why is @EnableBinding (Spring Cloud Stream old) failing after upgrade?
Context: Migrated to new functional style.
Common Pitfall: Relying on deprecated model.
Answer: Use functional bean definitions + spring.cloud.stream.function.definition.
Key Takeaway: Functional style replaced annotation binding.

### 139. Why does customizing thread pool not impact @Scheduled tasks?
Context: Provided executor bean.
Common Pitfall: Not implementing SchedulingConfigurer or using TaskScheduler.
Answer: Provide TaskScheduler bean named taskScheduler.
Key Takeaway: Dedicated scheduler bean needed for @Scheduled override.

### 140. Why does JPA entity equality break in sets after detach/merge?
Context: equals/hashCode using all fields.
Common Pitfall: Using mutable fields in equality.
Answer: Use immutable business key or ID (after assignment) for equality.
Key Takeaway: Stable identity fields for entity equality.

### 141. Why do custom interceptors not trigger for error responses in WebClient?
Context: Expect logging.
Common Pitfall: Not handling onStatus.
Answer: Add `.filter((req, next)-> next.exchange(req).flatMap(res->{ /* inspect */ return Mono.just(res); }))`.
Key Takeaway: Use filters and onStatus for error path instrumentation.

### 142. Why do GraphQL controllers not pick up validation annotations?
Context: @Valid ignored.
Common Pitfall: Missing proper wiring of Validator for GraphQL instrumentation.
Answer: Add GraphQL handler interceptors performing validation.
Key Takeaway: Manual validation integration for GraphQL mapping.

### 143. Why is large batch delete via JPA slow?
Context: Iterative entity removal triggers cascades.
Common Pitfall: Using entity manager remove in loop.
Answer: Use bulk JPQL delete or native query.
Key Takeaway: Bulk operations > per-entity removal for performance.

### 144. Why is memory leak observed in WebFlux due to not consuming body?
Context: Dropping responses early.
Common Pitfall: Ignoring body without releasing data buffers.
Answer: Consume or cancel subscription appropriately.
Key Takeaway: Always consume or cancel to release buffers.

### 145. Why does using ObjectMapper directly inside reactive pipeline block event loop?
Context: CPU heavy serialization.
Common Pitfall: Not offloading CPU tasks.
Answer: Use `publishOn(Schedulers.boundedElastic())` for blocking/CPU serialization heavy tasks.
Key Takeaway: Offload CPU-bound work off event loop.

### 146. Why does enabling debug logging degrade throughput drastically?
Context: High QPS service with DEBUG logs.
Common Pitfall: Logging synchronous I/O.
Answer: Use async logging or reduce debug scope.
Key Takeaway: Logging overhead significant at DEBUG under load.

### 147. Why is gRPC server not starting within Boot application?
Context: Only HTTP server up.
Common Pitfall: Missing Netty gRPC server bean start.
Answer: Define lifecycle bean starting gRPC server after context.
Key Takeaway: Manage non-Boot servers explicitly.

### 148. Why does injecting Environment in static context fail?
Context: Using static field injection.
Common Pitfall: Static injection unsupported.
Answer: Use @PostConstruct set static ref or avoid static.
Key Takeaway: Dependency injection works on instances.

### 149. Why does RestController returning ResponseEntity<Flux<T>> stream not flush progressively?
Context: Buffering entire stream.
Common Pitfall: Not using proper media type (text/event-stream or application/stream+json).
Answer: Set appropriate content type.
Key Takeaway: Streaming media type required for progressive flush.

### 150. Why does Spring Batch parallel step execution cause deadlocks on same DB tables?
Context: Multiple steps writing same table.
Common Pitfall: No partitioning or locking strategy.
Answer: Partition data, segregate write sets or serialize steps.
Key Takeaway: Avoid overlapping write contention in parallel steps.

### 151. Why does large YAML property file cause slow startup?
Context: Thousands of properties.
Common Pitfall: Parsing overhead & binding complexity.
Answer: Split config sources & environment overrides.
Key Takeaway: Keep config lean; externalize large sets.

### 152. Why does enabling management.port on separate port break health checks in k8s?
Context: Liveness pointing to main port /actuator/health.
Common Pitfall: Not updating probe path/port.
Answer: Point probe to management port or unify endpoints.
Key Takeaway: Adjust health probes when separating ports.

### 153. Why is customizing Undertow buffer size not reducing latency?
Context: Changed buffer-size property.
Common Pitfall: Bottleneck elsewhere (DB).
Answer: Profile end-to-end; buffer size not primary latency factor.
Key Takeaway: Optimize after profiling real bottleneck.

### 154. Why does GraphQL subscription hang after first event?
Context: Publisher completes.
Common Pitfall: Returning Mono instead of Flux.
Answer: Provide hot Flux or bridge to publisher.
Key Takeaway: Subscriptions require continuous Flux source.

### 155. Why does Resilience4j rate limiter starve burst traffic?
Context: Config with steady limit.
Common Pitfall: No warmup or burst tokens.
Answer: Adjust limit refresh period & permits for bursts.
Key Takeaway: Configure limiter for traffic pattern.

### 156. Why is @JsonView ignored in projection with ResponseEntity<?>?
Context: Returning entity.
Common Pitfall: Not specifying view on controller method produces or ResponseBodyAdvice.
Answer: Annotate method with @JsonView.
Key Takeaway: Controller method must declare view.

### 157. Why does customizing allowed origins with wildcard + credentials fail CORS?
Context: Access-Control-Allow-Origin=* with credentials.
Common Pitfall: Wildcard not permitted with credentials.
Answer: Return exact origin when credentials used.
Key Takeaway: Wildcard incompatible with credentialed requests.

### 158. Why is reactive stream backpressure ignored when bridging to blocking repository?
Context: Using blocking inside map.
Common Pitfall: Blocking call negates backpressure signals.
Answer: Use boundedElastic scheduler + limit concurrency.
Key Takeaway: Blocking sections must enforce concurrency control.

### 159. Why does Boot configuration processing ignore unknown property without failure?
Context: Typos slip through.
Common Pitfall: Not enabling fail-on-unknown.
Answer: Set `spring.jackson.deserialization.fail-on-unknown-properties=true` or custom binder validation.
Key Takeaway: Enable strict binding to catch typos.

### 160. Why is retry + timeout combination causing longer overall latency than expected?
Context: Timeout inside retry loop resets timer each attempt.
Common Pitfall: Not using overall deadline.
Answer: Implement total timeout (deadline) besides per-attempt.
Key Takeaway: Combining retries/timeouts requires global budget enforcement.

### 161. Why does layered jar extraction slow ephemeral startup in containers?
Context: Boot extracting layers.
Common Pitfall: Using exploded mode unnecessarily.
Answer: Use `java -jar` directly with optimized layers or explore CDS.
Key Takeaway: Avoid unnecessary extraction overhead.

### 162. Why does customizing Hibernate physical naming strategy break table discovery?
Context: Missing tables.
Common Pitfall: Strategy altering names differently than schema.
Answer: Align naming strategy with existing schema naming.
Key Takeaway: Naming strategy must reflect actual schema conventions.

### 163. Why are AOT native hints not applied at runtime jar mode?
Context: Hints in runtime not impacting reflection.
Common Pitfall: Expecting native hints to affect JVM mode.
Answer: AOT hints for native image only; runtime needs standard config.
Key Takeaway: Separate configuration for native vs JVM.

### 164. Why does reusing ByteBuf in reactive Netty produce corrupted responses?
Context: Manual buffer reuse.
Common Pitfall: Reference counting misuse.
Answer: Let framework manage; avoid manual retention.
Key Takeaway: Netty buffer lifecycle delicate—avoid manual reuse.

### 165. Why does enabling distributed tracing increase latency significantly?
Context: High sampling rate.
Common Pitfall: 100% sampling in production.
Answer: Reduce sampling rate & export asynchronously.
Key Takeaway: Sampling trade-off between observability & latency.

### 166. Why does customizing BeanFactory for dependency resolution break auto wiring order?
Context: Custom AutowireCandidateResolver.
Common Pitfall: Overriding logic incorrectly.
Answer: Extend existing resolver rather than replace.
Key Takeaway: Decorate core strategies carefully.

### 167. Why is dynamic bean registration not visible to @Autowired list injection?
Context: Registering beans after injection.
Common Pitfall: Late registration after context refresh.
Answer: Register before refresh or refresh context.
Key Takeaway: Bean discovery happens at refresh time.

### 168. Why does Tomcat access log show request time shorter than controller metrics?
Context: Timing mismatch.
Common Pitfall: Access log logs network duration, not internal processing including async continuations.
Answer: Use application metrics for server-side processing time.
Key Takeaway: Different timing scopes measure distinct phases.

### 169. Why are conditional beans misfiring with custom environment profile logic?
Context: Custom EnvironmentPostProcessor.
Common Pitfall: Altering profiles after conditions evaluated.
Answer: Set profiles before context refresh triggers condition evaluation.
Key Takeaway: Profile mutations must happen early.

### 170. Why does using @Transactional on tests slow them dramatically?
Context: Large dataset load each rollback.
Common Pitfall: Always using rollback when not needed.
Answer: Use non-transactional tests where read-only or isolate heavy setups.
Key Takeaway: Reserve transactional tests for state-dependent logic.

### 171. Why does migrating to Boot 3 break CORS config using WebMvcConfigurerAdapter?
Context: Class removed.
Common Pitfall: Not adopting interfaces.
Answer: Implement WebMvcConfigurer directly.
Key Takeaway: Use interface-based configuration; adapter deprecated.

### 172. Why does customizing ObjectMapper to fail on unknown properties break patch endpoints? 
Context: PATCH has partial object.
Common Pitfall: Strict deserialization for patch.
Answer: Use different ObjectMapper (or @JsonIgnoreProperties) for patch endpoints.
Key Takeaway: Use per-use-case ObjectMapper configuration.

### 173. Why is GraphQL DataLoader not batching effectively?
Context: Batching disabled.
Common Pitfall: Creating new DataLoader per field invocation instead of per request.
Answer: Register DataLoaderRegistry per request scope.
Key Takeaway: One DataLoader per request to aggregate keys.

### 174. Why does migrating to Virtual Threads (Project Loom) not show expected throughput gains in Boot app?
Context: Blocking external DB.
Common Pitfall: Underlying driver not using Loom-friendly I/O.
Answer: Use drivers supporting asynchronous or ensure workload is thread-block dominated.
Key Takeaway: Loom benefits depend on blocking style & driver compatibility.

### 175. Why does enabling StrictMode validations raise unexpected bean definition override errors?
Context: Multiple beans same name.
Common Pitfall: Previously silent override.
Answer: Rename or qualify conflicting beans.
Key Takeaway: Unique bean names for deterministic wiring.

### 176. Why is graceful shutdown not waiting for WebFlux in-flight requests?
Context: Immediate termination.
Common Pitfall: Not enabling graceful shutdown property.
Answer: Set `spring.lifecycle.timeout-per-shutdown-phase` & server shutdown property.
Key Takeaway: Configure graceful shutdown explicitly.

### 177. Why does property placeholder in @Scheduled cron expression not resolve?
Context: `@Scheduled(cron="${my.cron}")` failing.
Common Pitfall: Bean not proxied with property placeholder.
Answer: Ensure property loaded; use static constant or SpEL.
Key Takeaway: Cron placeholders allowed if property present at parse time.

### 178. Why are metrics missing for custom executor?
Context: Executor not instrumented.
Common Pitfall: Not registering with MeterBinder.
Answer: Use ExecutorServiceMetrics.monitor.
Key Takeaway: Wrap executors for metrics collection.

### 179. Why does reloading log configuration not change logging level dynamically?
Context: Expect live update.
Common Pitfall: Static config file without scan.
Answer: Use Spring Boot actuator loggers endpoint or enable logback scan.
Key Takeaway: Use actuator for dynamic level changes.

### 180. Why does integration test randomly fail due to ResourceAccessException (connection refused)?
Context: Test hitting started server.
Common Pitfall: Not waiting for server readiness; race condition.
Answer: Use WebTestClient injection or @LocalServerPort; rely on managed startup.
Key Takeaway: Use Boot-managed client injection for server tests.

### 181. Why are duplicate metrics with different tags increasing cardinality unexpectedly?
Context: Tag values dynamic (UUID).
Common Pitfall: Using high-cardinality tags like userId.
Answer: Limit tags to bounded cardinality set.
Key Takeaway: Avoid dynamic unbounded tag values.

### 182. Why does customizing DataSource failing under native image?
Context: Reflection use in driver.
Common Pitfall: Not including reflection config.
Answer: Add native hints for driver classes or use supported drivers.
Key Takeaway: Native mode requires explicit driver reflection metadata.

### 183. Why is @EnableAutoConfiguration on library starter shadowed by app exclusions?
Context: Library not configuring beans.
Common Pitfall: App excludes broad auto-config packages.
Answer: Narrow exclusion scope.
Key Takeaway: Careful with broad excludes impacting library starters.

### 184. Why does reactive retry cause duplicate downstream side-effects?
Context: Non-idempotent downstream call.
Common Pitfall: Retrying stateful operations.
Answer: Ensure idempotency or use retryWhen with predicate guarding.
Key Takeaway: Retry only safe, idempotent operations.

### 185. Why does customizing thread context classloader inside reactor chain break class resolution?
Context: Setting custom CL.
Common Pitfall: Overwriting without restoring.
Answer: Restore original after segment.
Key Takeaway: Preserve original classloader boundaries.

### 186. Why is @Document (Spring Data Mongo) entity not saving indexes automatically?
Context: Expect auto index creation.
Common Pitfall: Not enabling auto index creation (spring.data.mongodb.auto-index-creation=true).
Answer: Enable property or create indexes manually.
Key Takeaway: Explicitly enable index auto-creation in production carefully.

### 187. Why does large number of conditional beans slow native image build?
Context: AOT analysis overhead.
Common Pitfall: Unnecessary conditions complexity.
Answer: Simplify conditions & leverage hints.
Key Takeaway: AOT prefers straightforward config graphs.

### 188. Why is memory usage high with many WebClient instances?
Context: Creating new builder each request.
Common Pitfall: Not reusing.
Answer: Reuse WebClient; keep idempotent for concurrency.
Key Takeaway: WebClient is intended to be reused.

### 189. Why does customizing Reactor Netty HTTP client pooling not reduce connection churn?
Context: Frequent connects.
Common Pitfall: Not setting `ConnectionProvider`.
Answer: Provide custom ConnectionProvider bean.
Key Takeaway: Configure connection pool provider for reuse.

### 190. Why are application metrics missing after moving to Boot 3 + Micrometer 1.11?
Context: Renamed metric names.
Common Pitfall: Dashboards expecting old names.
Answer: Update dashboards or define legacy naming via MeterFilter.
Key Takeaway: Metric schema changes require dashboard updates.

### 191. Why does upgrading to Hibernate 6 break custom dialect usage?
Context: API changes.
Common Pitfall: Old dialect base class usage.
Answer: Adjust to new Dialect constructors & register functions.
Key Takeaway: Adapt dialect implementations to new version APIs.

### 192. Why is GraphQL input validation failing silently?
Context: Constraints ignored.
Common Pitfall: Missing schema directive integration.
Answer: Integrate validation via instrumentation or manual checks.
Key Takeaway: GraphQL requires explicit validation wiring.

### 193. Why does enabling gzip on server not compress SSE responses?
Context: text/event-stream.
Common Pitfall: Expect compression by default.
Answer: Add mime type to compressable types.
Key Takeaway: Configure additional mime types for compression.

### 194. Why do ephemeral pods lose distributed lock state unexpectedly?
Context: In-memory lock.
Common Pitfall: Not using external lock store.
Answer: Use Redis/Zookeeper for distributed locks.
Key Takeaway: External durable store for distributed locks.

### 195. Why does customizing log pattern not show traceId even with tracing enabled?
Context: Missing MDC keys.
Common Pitfall: Using unsupported pattern or tracer not populating MDC.
Answer: Include %X{traceId} & ensure tracer integration.
Key Takeaway: Logging pattern must reference correct MDC keys.

### 196. Why does application fail to deploy as WAR on external Tomcat after working as jar?
Context: Missing initializer.
Common Pitfall: Not extending SpringBootServletInitializer.
Answer: Provide initializer subclass.
Key Takeaway: WAR packaging requires servlet initializer.

### 197. Why does customizing graceful shutdown delay not apply on older Boot version?
Context: Using property introduced later.
Common Pitfall: Setting property unsupported by version.
Answer: Upgrade Boot or implement custom SmartLifecycle.
Key Takeaway: Verify version supports configuration property.

### 198. Why are metrics for R2DBC missing vs JDBC ones present?
Context: Expect same instrumentation.
Common Pitfall: Not adding r2dbc-micrometer integration.
Answer: Add instrumentation or manual MeterBinder.
Key Takeaway: Add explicit instrumentation per driver.

### 199. Why does environment variable overriding nested property with list not work?
Context: List of servers.
Common Pitfall: Incorrect naming: APP_SERVERS_0_HOST.
Answer: Use indexed environment variable naming aligning with relaxed binding.
Key Takeaway: Follow correct naming for indexed elements.

### 200. Why does Boot application hang on shutdown waiting for non-daemon threads created by third-party library?
Context: Threads not stopping.
Common Pitfall: Not registering shutdown hook for library.
Answer: Implement SmartLifecycle or DisposableBean to close library resources.
Key Takeaway: Ensure all non-daemon threads stopped in shutdown phase.

---
## Category Index
- Transactions & Persistence: 2,3,14,17,33,40,47,56,58,88,111,135,140,143,150,170
- Configuration & Properties: 7,26,27,36,44,47,48,53,62,94,118,121,124,134,151,162,169,199
- Caching & Performance: 15,18,57,65,90,97,111,120,145,146,160,188
- Reactive & WebFlux: 19,20,39,61,71,84,87,104,144,149,154,158,165,174,189,198
- Security & Context: 23,32,44,73,102,119,157
- Auto-Config & AOP: 1,6,10,12,22,25,37,41,43,52,63,67,83,95,112,166,167
- Scheduling & Async: 4,22,68,76,139,176,177
- Observability & Metrics: 9,11,28,29,59,66,81,91,109,114,132,136,165,178,181,190,195
- Serialization & API: 6,24,30,49,57,75,100,110,116,128,156,172,192
- Testing: 12,13,54,55,74,80,88,103,130,132,180
- Cloud / Deployment: 5,31,60,69,70,82,86,93,105,120,152,153,161,176,183,193,194,196,197,200
- Messaging & Integration: 4,29,67,125,141,147,184
- GraphQL: 142,154,156,173,192

## Quick Pattern References
Pattern | Representative Questions
------- | -----------------------
Outbox / Eventing | 4,29,115,184
AOP Proxy Limitations | 2,15,41,43
Idempotency / Retry | 1,29,48,184
Conditional Config | 26,44,52,94
Reactive Backpressure | 19,158,189
Graceful Shutdown | 64,176,200
Caching Strategies | 15,18,65,145
Observability Hardening | 28,66,109,165

## Usage Suggestions
- Convert each Q into mock interview drill: ask for deeper failure scenarios.
- Cross-reference with official docs for evolving features (Boot 3+ changes).
- Group by weak areas and create flashcards focusing on Pitfall + Takeaway.

## Next Steps
Request expansions (e.g., deep dive on top 20), diagramming problem areas, or generating multiple-choice quiz from this set.
