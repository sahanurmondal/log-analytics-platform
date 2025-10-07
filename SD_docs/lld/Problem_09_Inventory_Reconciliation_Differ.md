# Problem 9: Inventory Reconciliation Differ (Merkle Trees + Idempotent Sync + Conflict Resolution)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an inventory reconciliation system that detects and resolves discrepancies between multiple data sources (warehouse systems, e-commerce platforms, POS systems) ensuring eventual consistency.

**Assumptions / Scope:**
- Multiple inventory sources with independent updates
- Periodic reconciliation (hourly/daily)
- Handle network partitions and delayed updates
- Support manual and automatic conflict resolution
- Audit trail for all reconciliation actions
- Scale: 1M SKUs, 100 locations, 10K updates/minute
- Out of scope: Real-time streaming reconciliation, blockchain integration

**Non-Functional Goals:**
- Detect discrepancies within 1 hour
- Reconcile 1M records in < 10 minutes
- Handle concurrent reconciliation jobs safely
- 99.9% accuracy in conflict detection
- Idempotent reconciliation operations

### 2. Core Requirements

**Functional:**
- Compare inventory across multiple sources
- Detect discrepancies (quantity mismatches, missing items)
- Classify discrepancy types (shrinkage, data entry error, sync failure)
- Generate reconciliation report with root cause analysis
- Support resolution strategies (master source, latest timestamp, manual review)
- Track reconciliation history with audit trail
- Handle three-way merge conflicts
- Support partial reconciliation (by category, location)
- Provide reconciliation metrics and alerts

**Non-Functional:**
- **Consistency**: Eventual consistency across all sources
- **Performance**: Process 1M records in < 10 min
- **Reliability**: Idempotent operations, retry-safe
- **Observability**: Track discrepancy trends, resolution rates
- **Scalability**: Partition by SKU range for parallel processing

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Efficient Difference Detection (Merkle Trees)**
- **Problem**: Comparing millions of records is expensive (O(N) comparisons)
- **Solution**: Use Merkle trees to quickly identify divergent subtrees
- **Algorithm**:
```java
/**
 * Merkle Tree for efficient inventory comparison
 * 
 * Tree structure:
 *        Root Hash
 *       /         \
 *   Hash(A-M)   Hash(N-Z)
 *    /    \       /    \
 * Hash(A-F) ... (leaf nodes = individual SKUs)
 */
class MerkleTreeReconciliation {
    
    /**
     * INTERVIEW CRITICAL: Build Merkle tree from inventory snapshot
     */
    public MerkleTree buildMerkleTree(List<InventoryRecord> records) {
        // Sort records by SKU for consistent tree structure
        List<InventoryRecord> sorted = records.stream()
            .sorted(Comparator.comparing(InventoryRecord::getSku))
            .collect(Collectors.toList());
        
        // Create leaf nodes (hash of each record)
        List<MerkleNode> leafNodes = sorted.stream()
            .map(record -> new MerkleNode(
                record.getSku(),
                hashRecord(record),
                null, null
            ))
            .collect(Collectors.toList());
        
        // Build tree bottom-up
        return buildTreeRecursive(leafNodes);
    }
    
    /**
     * Hash individual inventory record
     */
    private String hashRecord(InventoryRecord record) {
        String data = String.format("%s:%d:%d:%s",
            record.getSku(),
            record.getQuantity(),
            record.getVersion(),
            record.getLastUpdated().toEpochMilli()
        );
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Build tree recursively by pairing and hashing nodes
     */
    private MerkleTree buildTreeRecursive(List<MerkleNode> nodes) {
        if (nodes.isEmpty()) {
            return new MerkleTree(null);
        }
        
        if (nodes.size() == 1) {
            return new MerkleTree(nodes.get(0));
        }
        
        List<MerkleNode> parentLevel = new ArrayList<>();
        
        // Pair nodes and create parent
        for (int i = 0; i < nodes.size(); i += 2) {
            MerkleNode left = nodes.get(i);
            MerkleNode right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : null;
            
            String combinedHash = hashPair(left.getHash(), 
                                          right != null ? right.getHash() : "");
            
            MerkleNode parent = new MerkleNode(
                null, // Internal node, no SKU
                combinedHash,
                left,
                right
            );
            
            parentLevel.add(parent);
        }
        
        // Recurse up the tree
        return buildTreeRecursive(parentLevel);
    }
    
    private String hashPair(String leftHash, String rightHash) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((leftHash + rightHash).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Compare two Merkle trees to find differences
     */
    public List<String> findDifferences(MerkleTree tree1, MerkleTree tree2) {
        List<String> differentSKUs = new ArrayList<>();
        findDifferencesRecursive(tree1.getRoot(), tree2.getRoot(), differentSKUs);
        return differentSKUs;
    }
    
    private void findDifferencesRecursive(MerkleNode node1, MerkleNode node2, 
                                          List<String> differences) {
        // If hashes match, subtrees are identical
        if (node1 != null && node2 != null && 
            node1.getHash().equals(node2.getHash())) {
            return;
        }
        
        // If one node is null, entire subtree is different
        if (node1 == null || node2 == null) {
            collectAllSKUs(node1 != null ? node1 : node2, differences);
            return;
        }
        
        // If leaf node, record SKU
        if (node1.getSku() != null) {
            differences.add(node1.getSku());
            return;
        }
        
        // Recurse on children
        findDifferencesRecursive(node1.getLeft(), node2.getLeft(), differences);
        findDifferencesRecursive(node1.getRight(), node2.getRight(), differences);
    }
    
    private void collectAllSKUs(MerkleNode node, List<String> skus) {
        if (node == null) return;
        
        if (node.getSku() != null) {
            skus.add(node.getSku());
        }
        
        collectAllSKUs(node.getLeft(), skus);
        collectAllSKUs(node.getRight(), skus);
    }
}

class MerkleNode {
    private final String sku; // Null for internal nodes
    private final String hash;
    private final MerkleNode left;
    private final MerkleNode right;
    
    // Constructor and getters
}

class MerkleTree {
    private final MerkleNode root;
    
    public MerkleTree(MerkleNode root) {
        this.root = root;
    }
    
    public MerkleNode getRoot() {
        return root;
    }
    
    public String getRootHash() {
        return root != null ? root.getHash() : "";
    }
}
```

**Challenge 2: Conflict Resolution with Multiple Sources**
- **Problem**: Three sources have different values for same SKU - which is correct?
- **Solution**: Pluggable resolution strategy with confidence scoring
- **Algorithm**:
```java
/**
 * Conflict resolution with multiple strategies
 */
class ConflictResolutionService {
    
    /**
     * INTERVIEW CRITICAL: Resolve discrepancy across multiple sources
     */
    public ResolutionResult resolveDiscrepancy(Discrepancy discrepancy,
                                               ResolutionStrategy strategy) {
        List<InventoryRecord> conflictingRecords = discrepancy.getRecords();
        
        InventoryRecord resolved = switch (strategy) {
            case MASTER_SOURCE -> resolveMasterSource(conflictingRecords);
            case LATEST_TIMESTAMP -> resolveLatestTimestamp(conflictingRecords);
            case MAJORITY_VOTE -> resolveMajorityVote(conflictingRecords);
            case HIGHEST_QUANTITY -> resolveHighestQuantity(conflictingRecords);
            case MANUAL_REVIEW -> null; // Requires human intervention
        };
        
        if (resolved == null) {
            return ResolutionResult.requiresManualReview(discrepancy);
        }
        
        // Calculate confidence score
        double confidence = calculateConfidence(resolved, conflictingRecords);
        
        // If low confidence, escalate to manual review
        if (confidence < 0.7) {
            return ResolutionResult.requiresManualReview(discrepancy);
        }
        
        return ResolutionResult.success(resolved, confidence, strategy);
    }
    
    /**
     * Strategy 1: Trust designated master source
     */
    private InventoryRecord resolveMasterSource(List<InventoryRecord> records) {
        // Priority: ERP > Warehouse > POS
        return records.stream()
            .min(Comparator.comparing(r -> getSourcePriority(r.getSource())))
            .orElse(null);
    }
    
    private int getSourcePriority(InventorySource source) {
        return switch (source) {
            case ERP -> 1;
            case WAREHOUSE_MANAGEMENT -> 2;
            case POS -> 3;
            case ECOMMERCE -> 4;
        };
    }
    
    /**
     * Strategy 2: Most recent update wins
     */
    private InventoryRecord resolveLatestTimestamp(List<InventoryRecord> records) {
        return records.stream()
            .max(Comparator.comparing(InventoryRecord::getLastUpdated))
            .orElse(null);
    }
    
    /**
     * Strategy 3: Majority consensus
     */
    private InventoryRecord resolveMajorityVote(List<InventoryRecord> records) {
        // Group by quantity
        Map<Integer, List<InventoryRecord>> byQuantity = records.stream()
            .collect(Collectors.groupingBy(InventoryRecord::getQuantity));
        
        // Find most common quantity
        Optional<Map.Entry<Integer, List<InventoryRecord>>> majority = byQuantity.entrySet().stream()
            .max(Comparator.comparingInt(e -> e.getValue().size()));
        
        if (majority.isEmpty()) {
            return null;
        }
        
        // Return most recent record with majority quantity
        return majority.get().getValue().stream()
            .max(Comparator.comparing(InventoryRecord::getLastUpdated))
            .orElse(null);
    }
    
    /**
     * Strategy 4: Highest quantity (conservative approach)
     */
    private InventoryRecord resolveHighestQuantity(List<InventoryRecord> records) {
        return records.stream()
            .max(Comparator.comparingInt(InventoryRecord::getQuantity))
            .orElse(null);
    }
    
    /**
     * INTERVIEW CRITICAL: Calculate confidence score
     */
    private double calculateConfidence(InventoryRecord resolved, 
                                       List<InventoryRecord> allRecords) {
        double confidence = 0.0;
        
        // Factor 1: Source reliability (0.0 - 0.4)
        double sourceScore = getSourceReliability(resolved.getSource()) * 0.4;
        confidence += sourceScore;
        
        // Factor 2: Recency (0.0 - 0.3)
        long ageMinutes = Duration.between(resolved.getLastUpdated(), Instant.now())
            .toMinutes();
        double recencyScore = Math.max(0, 1.0 - (ageMinutes / 1440.0)) * 0.3; // Decay over 24 hours
        confidence += recencyScore;
        
        // Factor 3: Consensus (0.0 - 0.3)
        long agreementCount = allRecords.stream()
            .filter(r -> r.getQuantity() == resolved.getQuantity())
            .count();
        double consensusScore = ((double) agreementCount / allRecords.size()) * 0.3;
        confidence += consensusScore;
        
        return confidence;
    }
    
    private double getSourceReliability(InventorySource source) {
        return switch (source) {
            case ERP -> 0.95;
            case WAREHOUSE_MANAGEMENT -> 0.90;
            case POS -> 0.80;
            case ECOMMERCE -> 0.75;
        };
    }
}

enum ResolutionStrategy {
    MASTER_SOURCE,
    LATEST_TIMESTAMP,
    MAJORITY_VOTE,
    HIGHEST_QUANTITY,
    MANUAL_REVIEW
}
```

**Challenge 3: Idempotent Reconciliation**
- **Problem**: Reconciliation job may run multiple times (retry, parallel execution)
- **Solution**: Idempotency key based on snapshot timestamp and SKU range
- **Algorithm**:
```java
/**
 * Idempotent reconciliation service
 */
class ReconciliationService {
    
    /**
     * INTERVIEW CRITICAL: Idempotent reconciliation execution
     */
    @Transactional
    public ReconciliationResult reconcile(ReconciliationJob job) {
        String idempotencyKey = generateIdempotencyKey(job);
        
        // Check if already processed
        Optional<ReconciliationResult> existing = resultRepo.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            logger.info("Reconciliation already processed: {}", idempotencyKey);
            return existing.get();
        }
        
        // Lock to prevent concurrent execution
        boolean acquired = lockService.tryAcquireLock(idempotencyKey, Duration.ofMinutes(30));
        if (!acquired) {
            throw new ReconciliationInProgressException(idempotencyKey);
        }
        
        try {
            // Execute reconciliation
            ReconciliationResult result = executeReconciliation(job);
            
            // Store result with idempotency key
            result.setIdempotencyKey(idempotencyKey);
            resultRepo.save(result);
            
            return result;
            
        } finally {
            lockService.releaseLock(idempotencyKey);
        }
    }
    
    /**
     * Generate idempotency key from job parameters
     */
    private String generateIdempotencyKey(ReconciliationJob job) {
        // Key = hash of (snapshot_timestamp + sku_range + sources)
        String data = String.format("%s:%s:%s:%s",
            job.getSnapshotTimestamp().toEpochMilli(),
            job.getSkuRangeStart(),
            job.getSkuRangeEnd(),
            job.getSources().stream()
                .map(Enum::name)
                .sorted()
                .collect(Collectors.joining(","))
        );
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Core reconciliation logic
     */
    private ReconciliationResult executeReconciliation(ReconciliationJob job) {
        Instant startTime = Instant.now();
        
        // 1. Fetch snapshots from all sources
        Map<InventorySource, List<InventoryRecord>> snapshots = new HashMap<>();
        for (InventorySource source : job.getSources()) {
            List<InventoryRecord> snapshot = inventoryClient.fetchSnapshot(
                source,
                job.getSnapshotTimestamp(),
                job.getSkuRangeStart(),
                job.getSkuRangeEnd()
            );
            snapshots.put(source, snapshot);
        }
        
        // 2. Build Merkle trees for fast comparison
        Map<InventorySource, MerkleTree> trees = snapshots.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> merkleService.buildMerkleTree(e.getValue())
            ));
        
        // 3. Find discrepancies
        List<Discrepancy> discrepancies = findDiscrepancies(snapshots, trees);
        
        // 4. Resolve conflicts
        List<Resolution> resolutions = new ArrayList<>();
        for (Discrepancy discrepancy : discrepancies) {
            ResolutionResult resolution = conflictResolver.resolveDiscrepancy(
                discrepancy,
                job.getResolutionStrategy()
            );
            resolutions.add(new Resolution(discrepancy, resolution));
        }
        
        // 5. Apply corrections (if auto-resolve enabled)
        if (job.isAutoResolve()) {
            applyCorrections(resolutions);
        }
        
        // 6. Generate report
        ReconciliationReport report = generateReport(discrepancies, resolutions);
        
        Instant endTime = Instant.now();
        
        return ReconciliationResult.builder()
            .jobId(job.getId())
            .startTime(startTime)
            .endTime(endTime)
            .totalRecordsProcessed(calculateTotalRecords(snapshots))
            .discrepanciesFound(discrepancies.size())
            .discrepanciesResolved(countResolved(resolutions))
            .discrepanciesPending(countPending(resolutions))
            .report(report)
            .build();
    }
    
    /**
     * INTERVIEW CRITICAL: Find all discrepancies
     */
    private List<Discrepancy> findDiscrepancies(
            Map<InventorySource, List<InventoryRecord>> snapshots,
            Map<InventorySource, MerkleTree> trees) {
        
        List<Discrepancy> discrepancies = new ArrayList<>();
        
        // Compare each pair of sources
        List<InventorySource> sources = new ArrayList<>(snapshots.keySet());
        
        for (int i = 0; i < sources.size(); i++) {
            for (int j = i + 1; j < sources.size(); j++) {
                InventorySource source1 = sources.get(i);
                InventorySource source2 = sources.get(j);
                
                // Use Merkle trees to find different SKUs
                List<String> differentSKUs = merkleService.findDifferences(
                    trees.get(source1),
                    trees.get(source2)
                );
                
                // For each different SKU, create discrepancy record
                for (String sku : differentSKUs) {
                    InventoryRecord record1 = findRecordBySKU(snapshots.get(source1), sku);
                    InventoryRecord record2 = findRecordBySKU(snapshots.get(source2), sku);
                    
                    // Check if already recorded
                    if (!hasDiscrepancy(discrepancies, sku)) {
                        // Collect all records for this SKU across all sources
                        List<InventoryRecord> allRecords = snapshots.values().stream()
                            .map(list -> findRecordBySKU(list, sku))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                        
                        Discrepancy discrepancy = Discrepancy.builder()
                            .sku(sku)
                            .records(allRecords)
                            .type(classifyDiscrepancy(allRecords))
                            .severity(calculateSeverity(allRecords))
                            .detectedAt(Instant.now())
                            .build();
                        
                        discrepancies.add(discrepancy);
                    }
                }
            }
        }
        
        return discrepancies;
    }
    
    /**
     * Classify discrepancy type for root cause analysis
     */
    private DiscrepancyType classifyDiscrepancy(List<InventoryRecord> records) {
        // Missing in some sources
        int distinctQuantities = (int) records.stream()
            .map(InventoryRecord::getQuantity)
            .distinct()
            .count();
        
        if (distinctQuantities == 1) {
            // Same quantity but different metadata
            return DiscrepancyType.METADATA_MISMATCH;
        }
        
        // Check for zero quantity (potential deletion)
        boolean hasZero = records.stream().anyMatch(r -> r.getQuantity() == 0);
        if (hasZero) {
            return DiscrepancyType.MISSING_RECORD;
        }
        
        // Check variance
        IntSummaryStatistics stats = records.stream()
            .mapToInt(InventoryRecord::getQuantity)
            .summaryStatistics();
        
        int range = stats.getMax() - stats.getMin();
        
        if (range > 100) {
            return DiscrepancyType.MAJOR_QUANTITY_MISMATCH;
        } else if (range > 10) {
            return DiscrepancyType.MODERATE_QUANTITY_MISMATCH;
        } else {
            return DiscrepancyType.MINOR_QUANTITY_MISMATCH;
        }
    }
    
    /**
     * Calculate severity for prioritization
     */
    private DiscrepancySeverity calculateSeverity(List<InventoryRecord> records) {
        IntSummaryStatistics stats = records.stream()
            .mapToInt(InventoryRecord::getQuantity)
            .summaryStatistics();
        
        int range = stats.getMax() - stats.getMin();
        double avgQuantity = stats.getAverage();
        
        // High severity if large absolute difference or large % difference
        if (range > 1000 || (range / avgQuantity > 0.5 && avgQuantity > 0)) {
            return DiscrepancySeverity.HIGH;
        } else if (range > 100 || (range / avgQuantity > 0.2 && avgQuantity > 0)) {
            return DiscrepancySeverity.MEDIUM;
        } else {
            return DiscrepancySeverity.LOW;
        }
    }
    
    /**
     * Apply corrections to source systems
     */
    private void applyCorrections(List<Resolution> resolutions) {
        for (Resolution resolution : resolutions) {
            if (resolution.getResult().isAutoResolved()) {
                InventoryRecord correctValue = resolution.getResult().getResolvedRecord();
                
                // Update each source with incorrect value
                for (InventoryRecord record : resolution.getDiscrepancy().getRecords()) {
                    if (!record.equals(correctValue)) {
                        try {
                            inventoryClient.updateRecord(
                                record.getSource(),
                                correctValue
                            );
                            
                            // Record in audit trail
                            auditService.logCorrection(
                                resolution.getDiscrepancy().getSku(),
                                record,
                                correctValue,
                                resolution.getResult().getStrategy()
                            );
                        } catch (Exception e) {
                            logger.error("Failed to apply correction for SKU: {}", 
                                       resolution.getDiscrepancy().getSku(), e);
                        }
                    }
                }
            }
        }
    }
}

enum DiscrepancyType {
    MISSING_RECORD,
    METADATA_MISMATCH,
    MINOR_QUANTITY_MISMATCH,
    MODERATE_QUANTITY_MISMATCH,
    MAJOR_QUANTITY_MISMATCH
}

enum DiscrepancySeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}
```

**Challenge 4: Efficient Batch Processing**
- **Problem**: Process 1M records without OOM or long-running transactions
- **Solution**: Partition by SKU range with parallel execution
- **Algorithm**:
```java
/**
 * Parallel batch reconciliation
 */
class BatchReconciliationService {
    private final ExecutorService executorService;
    
    /**
     * INTERVIEW CRITICAL: Partition and parallelize reconciliation
     */
    public ReconciliationResult reconcileBatch(ReconciliationJob job) {
        // Partition SKU range into chunks
        List<SKURange> partitions = partitionSKURange(
            job.getSkuRangeStart(),
            job.getSkuRangeEnd(),
            job.getPartitionSize()
        );
        
        logger.info("Created {} partitions for reconciliation", partitions.size());
        
        // Submit parallel jobs
        List<CompletableFuture<ReconciliationResult>> futures = partitions.stream()
            .map(partition -> CompletableFuture.supplyAsync(
                () -> reconcilePartition(job, partition),
                executorService
            ))
            .collect(Collectors.toList());
        
        // Wait for all to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allOf.get(30, TimeUnit.MINUTES); // Timeout after 30 min
        } catch (Exception e) {
            logger.error("Batch reconciliation failed", e);
            throw new ReconciliationException("Batch processing failed", e);
        }
        
        // Aggregate results
        List<ReconciliationResult> partitionResults = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
        
        return aggregateResults(partitionResults);
    }
    
    /**
     * Partition SKU range (lexicographically)
     */
    private List<SKURange> partitionSKURange(String start, String end, int partitionSize) {
        List<SKURange> partitions = new ArrayList<>();
        
        // Get all SKUs in range (from database or cache)
        List<String> allSKUs = inventoryRepo.findSKUsInRange(start, end);
        allSKUs.sort(String::compareTo);
        
        // Partition into chunks
        for (int i = 0; i < allSKUs.size(); i += partitionSize) {
            int endIdx = Math.min(i + partitionSize, allSKUs.size());
            String rangeStart = allSKUs.get(i);
            String rangeEnd = allSKUs.get(endIdx - 1);
            
            partitions.add(new SKURange(rangeStart, rangeEnd));
        }
        
        return partitions;
    }
    
    /**
     * Reconcile single partition
     */
    private ReconciliationResult reconcilePartition(ReconciliationJob job, SKURange range) {
        ReconciliationJob partitionJob = job.copy();
        partitionJob.setSkuRangeStart(range.getStart());
        partitionJob.setSkuRangeEnd(range.getEnd());
        
        return reconciliationService.reconcile(partitionJob);
    }
    
    /**
     * Aggregate partition results
     */
    private ReconciliationResult aggregateResults(List<ReconciliationResult> results) {
        return ReconciliationResult.builder()
            .totalRecordsProcessed(results.stream()
                .mapToLong(ReconciliationResult::getTotalRecordsProcessed)
                .sum())
            .discrepanciesFound(results.stream()
                .mapToLong(ReconciliationResult::getDiscrepanciesFound)
                .sum())
            .discrepanciesResolved(results.stream()
                .mapToLong(ReconciliationResult::getDiscrepanciesResolved)
                .sum())
            .discrepanciesPending(results.stream()
                .mapToLong(ReconciliationResult::getDiscrepanciesPending)
                .sum())
            .startTime(results.stream()
                .map(ReconciliationResult::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(Instant.now()))
            .endTime(results.stream()
                .map(ReconciliationResult::getEndTime)
                .max(Comparator.naturalOrder())
                .orElse(Instant.now()))
            .build();
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Resolution strategies (master, latest, majority) | Swap conflict resolution logic |
| **Template Method** | Reconciliation flow (fetch → compare → resolve → apply) | Common steps with variant strategies |
| **Composite** | Merkle tree structure | Hierarchical hash aggregation |
| **Command** | Correction actions (update, delete, insert) | Encapsulate operations for audit |
| **Repository** | Data access for inventory, results | Abstract persistence |
| **Factory** | Create resolution strategy based on config | Instantiate based on policy |
| **Observer** | Alert on high-severity discrepancies | Decouple notification logic |

### 5. Domain Model & Class Structure

```
┌─────────────────────────┐
│ ReconciliationService   │ (Application Service)
│  - merkleService        │
│  - conflictResolver     │
│  - inventoryClient      │
└────────┬────────────────┘
         │ coordinates
         │
    ┌────┴─────────┬──────────────┬──────────────┐
    ▼              ▼              ▼              ▼
┌────────────┐ ┌──────────┐ ┌──────────┐  ┌──────────┐
│MerkleTree  │ │Discrep.  │ │Resolution│  │ Audit    │
│  (Algo)    │ │(Entity)  │ │(Entity)  │  │(Entity)  │
└────────────┘ └──────────┘ └──────────┘  └──────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Reconciliation Flow**
```
Scheduler  ReconcilSvc  MerkleSvc  ConflictResolver  InventoryClient
   │           │            │             │                 │
   ├─trigger───>│            │             │                 │
   │           ├─fetchSnapshots──────────────────────────>│
   │           │<─snapshots────────────────────────────────┤
   │           ├─buildTrees─>│             │                 │
   │           │<─trees──────┤             │                 │
   │           ├─findDiff────>│             │                 │
   │           │<─diffSKUs───┤             │                 │
   │           ├─resolve────────────────>│                 │
   │           │<─resolutions─────────────┤                 │
   │           ├─applyCorrections─────────────────────────>│
   │           │<─success────────────────────────────────────┤
   │<─report───┤            │             │                 │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// Domain entities
public class InventoryRecord {
    private String sku;
    private int quantity;
    private InventorySource source;
    private Instant lastUpdated;
    private long version;
    private Map<String, String> metadata;
}

public enum InventorySource {
    ERP,
    WAREHOUSE_MANAGEMENT,
    POS,
    ECOMMERCE
}

public class Discrepancy {
    private UUID id;
    private String sku;
    private List<InventoryRecord> records;
    private DiscrepancyType type;
    private DiscrepancySeverity severity;
    private Instant detectedAt;
    private DiscrepancyStatus status; // DETECTED, RESOLVED, PENDING_REVIEW
}

public class ReconciliationJob {
    private UUID id;
    private Instant snapshotTimestamp;
    private String skuRangeStart;
    private String skuRangeEnd;
    private Set<InventorySource> sources;
    private ResolutionStrategy resolutionStrategy;
    private boolean autoResolve;
    private int partitionSize; // For parallel processing
}

public class ReconciliationResult {
    private UUID jobId;
    private String idempotencyKey;
    private Instant startTime;
    private Instant endTime;
    private long totalRecordsProcessed;
    private long discrepanciesFound;
    private long discrepanciesResolved;
    private long discrepanciesPending;
    private ReconciliationReport report;
}
```

### 8. Thread Safety & Concurrency

**Idempotency:**
- Hash-based idempotency key
- Distributed lock prevents parallel execution
- Results cached by idempotency key

**Parallel Processing:**
- Partition by SKU range (no overlap)
- Independent executor threads
- No shared mutable state

**Merkle Tree:**
- Immutable once built
- Thread-safe read operations

### 9. Top Interview Questions & Answers

**Q1: Why Merkle trees instead of direct comparison?**
**A:**
```
Complexity comparison:
- Direct: O(N) where N = total records
- Merkle: O(log N) to identify divergent subtrees + O(D) for differences

For 1M records with 1000 differences:
- Direct: 1M comparisons
- Merkle: ~20 tree levels + 1000 detail fetches = ~1020 operations

Merkle wins when differences << total records
```

**Q2: How do you handle network partitions?**
**A:**
```java
// Use snapshot timestamps to detect stale data
if (Duration.between(snapshot.getTimestamp(), Instant.now())
        .toHours() > 24) {
    throw new StaleSnapshotException("Snapshot too old");
}

// Implement eventual consistency
// Don't force immediate sync - queue for retry
// Use compensation transactions for rollback
```

**Q3: What if all sources have different values?**
**A:**
```java
// Escalate to manual review with context
if (confidence < 0.7) {
    notificationService.alertOps(new ManualReviewRequired(
        sku,
        allRecords,
        "No consensus reached - confidence: " + confidence
    ));
}

// Provide recommendation but require approval
```

**Q4: How to test Merkle tree implementation?**
**A:**
```java
@Test
public void testMerkleTreeDifferenceDetection() {
    List<InventoryRecord> snapshot1 = List.of(
        record("SKU001", 100),
        record("SKU002", 200),
        record("SKU003", 300)
    );
    
    List<InventoryRecord> snapshot2 = List.of(
        record("SKU001", 100),
        record("SKU002", 250), // Different
        record("SKU003", 300)
    );
    
    MerkleTree tree1 = service.buildMerkleTree(snapshot1);
    MerkleTree tree2 = service.buildMerkleTree(snapshot2);
    
    assertNotEquals(tree1.getRootHash(), tree2.getRootHash());
    
    List<String> diffs = service.findDifferences(tree1, tree2);
    assertEquals(1, diffs.size());
    assertEquals("SKU002", diffs.get(0));
}
```

**Q5: What metrics to track?**
**A:**
```
Metrics:
1. Discrepancy rate (per source, per category)
2. Resolution time (detected → resolved)
3. Auto-resolution rate
4. Confidence score distribution
5. Processing time per 1M records
6. Merkle tree comparison speedup

Alerts:
- Discrepancy rate > 5% → Data quality issue
- Auto-resolution < 70% → Review strategy
- Processing time > 15 min → Scale out
```

**Q6: How to partition for parallel processing?**
**A:**
```java
// Option 1: Lexicographic SKU range
Partitions:
- A000-C999
- D000-F999
- ...

// Option 2: Hash-based partitioning
partition = hash(sku) % num_partitions

// Option 3: Category-based
- Electronics
- Clothing
- ...

// Prefer lexicographic for range queries
// Prefer hash-based for even distribution
```

**Q7: Database schema?**
**A:**
```sql
CREATE TABLE inventory_snapshots (
    id UUID PRIMARY KEY,
    source VARCHAR(50) NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    snapshot_timestamp TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    version BIGINT NOT NULL,
    metadata JSONB,
    merkle_leaf_hash VARCHAR(64)
);

CREATE INDEX idx_snapshots_sku_source_time 
    ON inventory_snapshots(sku, source, snapshot_timestamp DESC);

CREATE TABLE discrepancies (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    detected_at TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP,
    resolution_strategy VARCHAR(50)
);

CREATE INDEX idx_discrepancies_status 
    ON discrepancies(status, severity DESC, detected_at);

CREATE TABLE reconciliation_results (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(64) UNIQUE NOT NULL,
    job_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    total_records_processed BIGINT,
    discrepancies_found INT,
    discrepancies_resolved INT,
    discrepancies_pending INT
);

CREATE INDEX idx_results_idempotency 
    ON reconciliation_results(idempotency_key);

CREATE TABLE reconciliation_audit (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    source VARCHAR(50) NOT NULL,
    old_quantity INT,
    new_quantity INT,
    strategy VARCHAR(50),
    confidence DECIMAL(3,2),
    corrected_at TIMESTAMP NOT NULL,
    corrected_by VARCHAR(100)
);

CREATE INDEX idx_audit_sku_time 
    ON reconciliation_audit(sku, corrected_at DESC);
```

**Q8: How to handle high-value items differently?**
**A:**
```java
// Custom severity calculation
if (item.getValue().compareTo(new BigDecimal("10000")) > 0) {
    discrepancy.setSeverity(DiscrepancySeverity.CRITICAL);
    discrepancy.setRequiresManualReview(true);
}

// Stricter confidence threshold
double confidenceThreshold = item.isHighValue() ? 0.9 : 0.7;

// Immediate alerts for high-value discrepancies
if (item.isHighValue() && discrepancy.getSeverity() == CRITICAL) {
    alertService.sendImmediateAlert(discrepancy);
}
```

**Q9: How to optimize Merkle tree for frequent updates?**
**A:**
```java
// Incremental update instead of full rebuild
public void updateMerkleTree(MerkleTree tree, String sku, int newQuantity) {
    // Find leaf node
    MerkleNode leaf = tree.findLeaf(sku);
    
    // Update leaf hash
    String newHash = hashRecord(sku, newQuantity);
    leaf.setHash(newHash);
    
    // Propagate changes up the tree
    MerkleNode current = leaf.getParent();
    while (current != null) {
        String combinedHash = hashPair(
            current.getLeft().getHash(),
            current.getRight() != null ? current.getRight().getHash() : ""
        );
        current.setHash(combinedHash);
        current = current.getParent();
    }
}

// Only O(log N) instead of O(N) for full rebuild
```

**Q10: How to scale to 100M+ SKUs?**
**A:**
```
Horizontal scaling:
1. Shard by SKU hash (consistent hashing)
2. Each shard handles subset of SKUs
3. Parallel reconciliation per shard

Merkle tree optimization:
1. Persist tree structure (don't rebuild each time)
2. Use shallow comparisons (check only top levels first)
3. Lazy load leaf nodes on demand

Incremental reconciliation:
1. Track changed SKUs since last run
2. Only reconcile changed + random sample
3. Full reconciliation weekly, incremental hourly
```

### 10. Extensions & Variations

1. **Real-time Reconciliation**: Use change data capture (CDC) for instant sync
2. **ML-Based Anomaly Detection**: Predict discrepancies before they occur
3. **Blockchain Integration**: Immutable audit trail with distributed consensus
4. **Cost-Based Resolution**: Factor in correction cost (labor, shipping)
5. **Predictive Replenishment**: Use discrepancy patterns to optimize inventory

### 11. Testing Strategy

**Unit Tests:**
- Merkle tree construction and comparison
- Conflict resolution strategies
- Idempotency key generation
- Confidence calculation

**Integration Tests:**
- Full reconciliation flow
- Parallel partition processing
- Idempotent retry handling
- Audit trail completeness

**Performance Tests:**
- 1M records in < 10 min
- Merkle tree speedup vs direct comparison
- Parallel processing scalability

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Direct O(N²) comparison across all sources
✅ **Do**: Merkle trees for O(log N) divergence detection

❌ **Avoid**: Non-idempotent reconciliation
✅ **Do**: Idempotency key with distributed lock

❌ **Avoid**: Single-threaded batch processing
✅ **Do**: Partition by SKU range with parallel execution

❌ **Avoid**: Hardcoded conflict resolution
✅ **Do**: Pluggable strategy pattern

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Build Merkle tree | O(N log N) | O(N) | N = records, sort + build |
| Compare trees | O(log N + D) | O(D) | D = differences |
| Direct comparison | O(N) | O(1) | Baseline |
| Resolve conflict | O(S) | O(1) | S = sources per SKU |
| Batch reconcile | O(N/P * log N) | O(N) | P = partitions (parallel) |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Merkle Trees** | 30% | Understands hierarchical hashing, comparison algorithm |
| **Conflict Resolution** | 25% | Multi-strategy with confidence scoring |
| **Idempotency** | 20% | Prevents duplicate execution, retry-safe |
| **Scalability** | 15% | Partitioning, parallel processing |
| **Real-world Awareness** | 10% | Network partitions, eventual consistency |

**Red Flags:**
- Direct N² comparison
- No idempotency handling
- Hardcoded conflict resolution
- No audit trail
- Ignoring eventual consistency challenges

---
