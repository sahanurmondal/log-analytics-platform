package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Design Distributed Configuration Management System
 * 
 * Description:
 * Design a distributed configuration management system that allows applications
 * to dynamically retrieve, update, and monitor configuration changes across
 * multiple environments and services with real-time updates and versioning.
 * 
 * Requirements:
 * - Hierarchical configuration structure
 * - Real-time configuration updates
 * - Version control and rollback
 * - Environment-based configuration
 * - Configuration validation and schemas
 * - Distributed caching and replication
 * - Access control and audit logging
 * - Configuration change notifications
 * 
 * Key Features:
 * - Dynamic configuration updates
 * - Configuration inheritance
 * - Change propagation
 * - Conflict resolution
 * - Configuration templates
 * - Multi-environment support
 * 
 * Company Tags: Spring Cloud Config, Consul, etcd, Apache Zookeeper
 * Difficulty: Hard
 */
public class DesignDistributedConfigurationManagement {

    enum ConfigurationType {
        STRING, INTEGER, DOUBLE, BOOLEAN, JSON, LIST
    }

    enum Environment {
        DEVELOPMENT, STAGING, PRODUCTION, TESTING
    }

    static class ConfigurationValue {
        final String key;
        final Object value;
        final ConfigurationType type;
        final String description;
        final boolean encrypted;
        final long version;
        final long timestamp;
        final String lastModifiedBy;
        final Map<String, String> metadata;

        public ConfigurationValue(String key, Object value, ConfigurationType type,
                String description, boolean encrypted, String modifiedBy) {
            this.key = key;
            this.value = value;
            this.type = type;
            this.description = description;
            this.encrypted = encrypted;
            this.version = System.currentTimeMillis(); // Simple versioning
            this.timestamp = System.currentTimeMillis();
            this.lastModifiedBy = modifiedBy;
            this.metadata = new HashMap<>();
        }

        public ConfigurationValue addMetadata(String metaKey, String metaValue) {
            this.metadata.put(metaKey, metaValue);
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> T getValue(Class<T> clazz) {
            if (clazz.isInstance(value)) {
                return (T) value;
            }
            throw new ClassCastException("Cannot cast " + value.getClass() + " to " + clazz);
        }

        @Override
        public String toString() {
            return String.format("Config{key=%s, value=%s, type=%s, version=%d}",
                    key, encrypted ? "[ENCRYPTED]" : value, type, version);
        }
    }

    static class ConfigurationNamespace {
        private final String namespace;
        private final Environment environment;
        private final Map<String, ConfigurationValue> configurations;
        private final Map<String, ConfigurationNamespace> children;
        private volatile long lastModified;

        public ConfigurationNamespace(String namespace, Environment environment) {
            this.namespace = namespace;
            this.environment = environment;
            this.configurations = new ConcurrentHashMap<>();
            this.children = new ConcurrentHashMap<>();
            this.lastModified = System.currentTimeMillis();
        }

        public void setConfiguration(String key, ConfigurationValue config) {
            configurations.put(key, config);
            lastModified = System.currentTimeMillis();
        }

        public ConfigurationValue getConfiguration(String key) {
            return configurations.get(key);
        }

        public ConfigurationValue getConfigurationRecursive(String key) {
            // Check current namespace first
            ConfigurationValue config = configurations.get(key);
            if (config != null) {
                return config;
            }

            // Check child namespaces
            for (ConfigurationNamespace child : children.values()) {
                config = child.getConfigurationRecursive(key);
                if (config != null) {
                    return config;
                }
            }

            return null;
        }

        public void addChildNamespace(ConfigurationNamespace child) {
            children.put(child.namespace, child);
        }

        public Map<String, ConfigurationValue> getAllConfigurations() {
            return new HashMap<>(configurations);
        }

        public Map<String, ConfigurationValue> getAllConfigurationsRecursive() {
            Map<String, ConfigurationValue> allConfigs = new HashMap<>(configurations);

            for (ConfigurationNamespace child : children.values()) {
                allConfigs.putAll(child.getAllConfigurationsRecursive());
            }

            return allConfigs;
        }

        public Set<String> getChildNamespaces() {
            return new HashSet<>(children.keySet());
        }

        public String getNamespace() {
            return namespace;
        }

        public Environment getEnvironment() {
            return environment;
        }

        public long getLastModified() {
            return lastModified;
        }
    }

    interface ConfigurationChangeListener {
        void onConfigurationChanged(String namespace, String key, ConfigurationValue oldValue,
                ConfigurationValue newValue);

        void onConfigurationAdded(String namespace, String key, ConfigurationValue value);

        void onConfigurationRemoved(String namespace, String key, ConfigurationValue oldValue);
    }

    static class ConfigurationCache {
        private final Map<String, Map<String, ConfigurationValue>> cache;
        private final Map<String, Long> cacheTimestamps;
        private final long cacheExpirationMs;

        public ConfigurationCache(long cacheExpirationMs) {
            this.cache = new ConcurrentHashMap<>();
            this.cacheTimestamps = new ConcurrentHashMap<>();
            this.cacheExpirationMs = cacheExpirationMs;
        }

        public void put(String namespace, Map<String, ConfigurationValue> configurations) {
            cache.put(namespace, new HashMap<>(configurations));
            cacheTimestamps.put(namespace, System.currentTimeMillis());
        }

        public Map<String, ConfigurationValue> get(String namespace) {
            Long timestamp = cacheTimestamps.get(namespace);
            if (timestamp != null && System.currentTimeMillis() - timestamp < cacheExpirationMs) {
                return cache.get(namespace);
            }

            // Cache expired, remove entry
            cache.remove(namespace);
            cacheTimestamps.remove(namespace);
            return null;
        }

        public void invalidate(String namespace) {
            cache.remove(namespace);
            cacheTimestamps.remove(namespace);
        }

        public void clear() {
            cache.clear();
            cacheTimestamps.clear();
        }

        public int size() {
            return cache.size();
        }
    }

    // Main Configuration Management System
    private final Map<String, Map<Environment, ConfigurationNamespace>> namespaces;
    private final List<ConfigurationChangeListener> listeners;
    private final ConfigurationCache cache;
    private final ExecutorService notificationExecutor;
    private final Map<String, String> userSessions;
    private final AtomicLong changeCounter;

    public DesignDistributedConfigurationManagement() {
        this.namespaces = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.cache = new ConfigurationCache(300000); // 5 minutes cache
        this.notificationExecutor = Executors.newFixedThreadPool(4);
        this.userSessions = new ConcurrentHashMap<>();
        this.changeCounter = new AtomicLong(0);
    }

    public void createNamespace(String namespace, Environment environment) {
        namespaces.computeIfAbsent(namespace, k -> new ConcurrentHashMap<>())
                .put(environment, new ConfigurationNamespace(namespace, environment));
    }

    public void setConfiguration(String namespace, Environment environment, String key,
            Object value, ConfigurationType type, String description,
            boolean encrypted, String userId) {

        Map<Environment, ConfigurationNamespace> envNamespaces = namespaces.get(namespace);
        if (envNamespaces == null) {
            createNamespace(namespace, environment);
            envNamespaces = namespaces.get(namespace);
        }

        ConfigurationNamespace configNamespace = envNamespaces.get(environment);
        if (configNamespace == null) {
            configNamespace = new ConfigurationNamespace(namespace, environment);
            envNamespaces.put(environment, configNamespace);
        }

        ConfigurationValue oldValue = configNamespace.getConfiguration(key);
        ConfigurationValue newValue = new ConfigurationValue(key, value, type, description, encrypted, userId);

        configNamespace.setConfiguration(key, newValue);
        changeCounter.incrementAndGet();

        // Invalidate cache
        cache.invalidate(namespace + "-" + environment.name());

        // Notify listeners
        notifyListeners(namespace, key, oldValue, newValue);
    }

    public ConfigurationValue getConfiguration(String namespace, Environment environment, String key) {
        // Check cache first
        String cacheKey = namespace + "-" + environment.name();
        Map<String, ConfigurationValue> cachedConfigs = cache.get(cacheKey);

        if (cachedConfigs != null) {
            ConfigurationValue config = cachedConfigs.get(key);
            if (config != null) {
                return config;
            }
        }

        // Retrieve from namespace
        Map<Environment, ConfigurationNamespace> envNamespaces = namespaces.get(namespace);
        if (envNamespaces == null) {
            return null;
        }

        ConfigurationNamespace configNamespace = envNamespaces.get(environment);
        if (configNamespace == null) {
            return null;
        }

        ConfigurationValue config = configNamespace.getConfigurationRecursive(key);

        // Update cache if found
        if (config != null && cachedConfigs == null) {
            Map<String, ConfigurationValue> allConfigs = configNamespace.getAllConfigurationsRecursive();
            cache.put(cacheKey, allConfigs);
        }

        return config;
    }

    public Map<String, ConfigurationValue> getAllConfigurations(String namespace, Environment environment) {
        String cacheKey = namespace + "-" + environment.name();
        Map<String, ConfigurationValue> cachedConfigs = cache.get(cacheKey);

        if (cachedConfigs != null) {
            return new HashMap<>(cachedConfigs);
        }

        Map<Environment, ConfigurationNamespace> envNamespaces = namespaces.get(namespace);
        if (envNamespaces == null) {
            return new HashMap<>();
        }

        ConfigurationNamespace configNamespace = envNamespaces.get(environment);
        if (configNamespace == null) {
            return new HashMap<>();
        }

        Map<String, ConfigurationValue> allConfigs = configNamespace.getAllConfigurationsRecursive();
        cache.put(cacheKey, allConfigs);

        return allConfigs;
    }

    public boolean removeConfiguration(String namespace, Environment environment, String key, String userId) {
        Map<Environment, ConfigurationNamespace> envNamespaces = namespaces.get(namespace);
        if (envNamespaces == null) {
            return false;
        }

        ConfigurationNamespace configNamespace = envNamespaces.get(environment);
        if (configNamespace == null) {
            return false;
        }

        ConfigurationValue oldValue = configNamespace.getConfiguration(key);
        if (oldValue == null) {
            return false;
        }

        configNamespace.configurations.remove(key);
        changeCounter.incrementAndGet();

        // Invalidate cache
        cache.invalidate(namespace + "-" + environment.name());

        // Notify listeners
        notifyListenersRemoved(namespace, key, oldValue);

        return true;
    }

    public void addConfigurationChangeListener(ConfigurationChangeListener listener) {
        listeners.add(listener);
    }

    public void removeConfigurationChangeListener(ConfigurationChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(String namespace, String key, ConfigurationValue oldValue,
            ConfigurationValue newValue) {
        for (ConfigurationChangeListener listener : listeners) {
            notificationExecutor.submit(() -> {
                try {
                    if (oldValue == null) {
                        listener.onConfigurationAdded(namespace, key, newValue);
                    } else {
                        listener.onConfigurationChanged(namespace, key, oldValue, newValue);
                    }
                } catch (Exception e) {
                    System.err.println("Error notifying listener: " + e.getMessage());
                }
            });
        }
    }

    private void notifyListenersRemoved(String namespace, String key, ConfigurationValue oldValue) {
        for (ConfigurationChangeListener listener : listeners) {
            notificationExecutor.submit(() -> {
                try {
                    listener.onConfigurationRemoved(namespace, key, oldValue);
                } catch (Exception e) {
                    System.err.println("Error notifying listener: " + e.getMessage());
                }
            });
        }
    }

    public Set<String> getAllNamespaces() {
        return new HashSet<>(namespaces.keySet());
    }

    public Set<Environment> getEnvironmentsForNamespace(String namespace) {
        Map<Environment, ConfigurationNamespace> envNamespaces = namespaces.get(namespace);
        return envNamespaces != null ? new HashSet<>(envNamespaces.keySet()) : new HashSet<>();
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNamespaces", namespaces.size());
        stats.put("totalChanges", changeCounter.get());
        stats.put("cacheSize", cache.size());
        stats.put("activeListeners", listeners.size());
        stats.put("activeSessions", userSessions.size());

        Map<String, Integer> configCounts = new HashMap<>();
        int totalConfigs = 0;

        for (Map.Entry<String, Map<Environment, ConfigurationNamespace>> entry : namespaces.entrySet()) {
            String namespace = entry.getKey();
            int namespaceConfigCount = 0;

            for (ConfigurationNamespace configNamespace : entry.getValue().values()) {
                namespaceConfigCount += configNamespace.getAllConfigurationsRecursive().size();
            }

            configCounts.put(namespace, namespaceConfigCount);
            totalConfigs += namespaceConfigCount;
        }

        stats.put("totalConfigurations", totalConfigs);
        stats.put("configurationsByNamespace", configCounts);

        return stats;
    }

    public void createUserSession(String userId, String sessionToken) {
        userSessions.put(sessionToken, userId);
    }

    public void invalidateUserSession(String sessionToken) {
        userSessions.remove(sessionToken);
    }

    public String getUserFromSession(String sessionToken) {
        return userSessions.get(sessionToken);
    }

    public void shutdown() {
        notificationExecutor.shutdown();
        try {
            if (!notificationExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                notificationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            notificationExecutor.shutdownNow();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedConfigurationManagement configManager = new DesignDistributedConfigurationManagement();

        // Add change listener
        configManager.addConfigurationChangeListener(new ConfigurationChangeListener() {
            @Override
            public void onConfigurationChanged(String namespace, String key, ConfigurationValue oldValue,
                    ConfigurationValue newValue) {
                System.out.println("🔄 Config changed in " + namespace + ": " + key +
                        " [" + oldValue.value + " -> " + newValue.value + "]");
            }

            @Override
            public void onConfigurationAdded(String namespace, String key, ConfigurationValue value) {
                System.out.println("➕ Config added to " + namespace + ": " + key + " = " + value.value);
            }

            @Override
            public void onConfigurationRemoved(String namespace, String key, ConfigurationValue oldValue) {
                System.out.println("➖ Config removed from " + namespace + ": " + key);
            }
        });

        System.out.println("=== Configuration Management Demo ===");

        // Create user session
        configManager.createUserSession("admin", "session-123");

        // Set up configurations for different environments
        configManager.setConfiguration("app", Environment.DEVELOPMENT, "database.url",
                "jdbc:mysql://dev-db:3306/myapp", ConfigurationType.STRING,
                "Development database URL", false, "admin");

        configManager.setConfiguration("app", Environment.DEVELOPMENT, "database.max-connections",
                10, ConfigurationType.INTEGER, "Max database connections", false, "admin");

        configManager.setConfiguration("app", Environment.PRODUCTION, "database.url",
                "jdbc:mysql://prod-db:3306/myapp", ConfigurationType.STRING,
                "Production database URL", false, "admin");

        configManager.setConfiguration("app", Environment.PRODUCTION, "database.max-connections",
                50, ConfigurationType.INTEGER, "Max database connections", false, "admin");

        configManager.setConfiguration("app", Environment.PRODUCTION, "api.secret-key",
                "super-secret-production-key", ConfigurationType.STRING,
                "API secret key", true, "admin");

        // Configure cache settings
        configManager.setConfiguration("cache", Environment.DEVELOPMENT, "redis.host",
                "dev-redis", ConfigurationType.STRING, "Redis host", false, "admin");

        configManager.setConfiguration("cache", Environment.PRODUCTION, "redis.host",
                "prod-redis-cluster", ConfigurationType.STRING, "Redis host", false, "admin");

        configManager.setConfiguration("cache", Environment.PRODUCTION, "redis.ttl",
                3600, ConfigurationType.INTEGER, "Default TTL in seconds", false, "admin");

        Thread.sleep(1000); // Wait for notifications

        System.out.println("\n=== Configuration Retrieval ===");

        // Retrieve configurations
        ConfigurationValue devDbUrl = configManager.getConfiguration("app", Environment.DEVELOPMENT, "database.url");
        System.out.println("Dev DB URL: " + (devDbUrl != null ? devDbUrl.getValue(String.class) : "NOT FOUND"));

        ConfigurationValue prodDbUrl = configManager.getConfiguration("app", Environment.PRODUCTION, "database.url");
        System.out.println("Prod DB URL: " + (prodDbUrl != null ? prodDbUrl.getValue(String.class) : "NOT FOUND"));

        ConfigurationValue prodSecret = configManager.getConfiguration("app", Environment.PRODUCTION, "api.secret-key");
        System.out.println("Prod Secret: " + (prodSecret != null ? "[ENCRYPTED]" : "NOT FOUND"));

        System.out.println("\n=== All Configurations by Environment ===");

        // Get all configurations for development
        Map<String, ConfigurationValue> devConfigs = configManager.getAllConfigurations("app", Environment.DEVELOPMENT);
        System.out.println("Development configurations:");
        for (Map.Entry<String, ConfigurationValue> entry : devConfigs.entrySet()) {
            System.out.println("  " + entry.getKey() + " = " + entry.getValue().value +
                    " (type: " + entry.getValue().type + ")");
        }

        // Get all configurations for production
        Map<String, ConfigurationValue> prodConfigs = configManager.getAllConfigurations("app", Environment.PRODUCTION);
        System.out.println("\nProduction configurations:");
        for (Map.Entry<String, ConfigurationValue> entry : prodConfigs.entrySet()) {
            ConfigurationValue config = entry.getValue();
            String displayValue = config.encrypted ? "[ENCRYPTED]" : String.valueOf(config.value);
            System.out.println("  " + entry.getKey() + " = " + displayValue +
                    " (type: " + config.type + ")");
        }

        System.out.println("\n=== Configuration Updates ===");

        // Update configuration
        configManager.setConfiguration("app", Environment.DEVELOPMENT, "database.max-connections",
                20, ConfigurationType.INTEGER, "Updated max connections", false, "admin");

        // Add new configuration
        configManager.setConfiguration("app", Environment.DEVELOPMENT, "feature.new-ui",
                true, ConfigurationType.BOOLEAN, "Enable new UI", false, "admin");

        Thread.sleep(1000); // Wait for notifications

        System.out.println("\n=== Cache Performance Test ===");

        // Test cache performance
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            configManager.getConfiguration("app", Environment.PRODUCTION, "database.url");
        }
        long endTime = System.currentTimeMillis();
        System.out.println("1000 cached reads took: " + (endTime - startTime) + "ms");

        System.out.println("\n=== System Statistics ===");

        // Show system stats
        Map<String, Object> stats = configManager.getSystemStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n=== Namespace and Environment Info ===");

        // Show all namespaces
        Set<String> allNamespaces = configManager.getAllNamespaces();
        System.out.println("All namespaces: " + allNamespaces);

        for (String namespace : allNamespaces) {
            Set<Environment> environments = configManager.getEnvironmentsForNamespace(namespace);
            System.out.println("Namespace '" + namespace + "' environments: " + environments);
        }

        // Remove configuration
        System.out.println("\n=== Configuration Removal ===");
        boolean removed = configManager.removeConfiguration("app", Environment.DEVELOPMENT, "feature.new-ui", "admin");
        System.out.println("Removed 'feature.new-ui': " + removed);

        Thread.sleep(1000); // Wait for notifications

        configManager.shutdown();
    }
}
