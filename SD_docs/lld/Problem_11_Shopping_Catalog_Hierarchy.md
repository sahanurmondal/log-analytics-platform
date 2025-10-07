# Problem 11: Shopping Catalog with Hierarchical Categories (Composite Pattern + Tree Traversal)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a product catalog system with nested category hierarchies, supporting attributes inheritance, search across levels, and efficient navigation.

**Assumptions / Scope:**
- Infinite-depth category tree (Electronics → Phones → Smartphones → iPhone)
- Products inherit category attributes (Brand, Price Range, Features)
- Support breadcrumb navigation
- Search within category and all subcategories
- Category-level promotions (20% off all Electronics)
- Scale: 100K categories, 10M products, 1K requests/sec
- Out of scope: Reviews, recommendations, real-time inventory

**Non-Functional Goals:**
- Search response in < 50ms
- Category tree traversal < 10ms
- Support 1K concurrent searches
- 99.9% availability
- Efficient attribute inheritance

### 2. Core Requirements

**Functional:**
- Create/update/delete categories with parent-child relationships
- Add/remove products to categories
- Define category-specific attributes (e.g., "Screen Size" for TVs)
- Inherit attributes down category tree
- Search products within category and descendants
- Generate breadcrumb path (Home → Electronics → Phones)
- Apply category-level filters (price range, brand, features)
- Support category-wide promotions
- Move categories (reparent)
- Get category ancestors/descendants

**Non-Functional:**
- **Performance**: < 50ms search, < 10ms navigation
- **Scalability**: 10M products, 100K categories
- **Consistency**: Eventual consistency for search index
- **Flexibility**: Dynamic attributes per category
- **Observability**: Track search depth, filter usage

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Composite Pattern for Category Tree**
- **Problem**: Represent tree where leaves (products) and nodes (categories) have different behaviors
- **Solution**: Composite pattern with CatalogComponent abstraction
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Composite pattern for category hierarchy
 */
abstract class CatalogComponent {
    protected UUID id;
    protected String name;
    protected CatalogComponent parent;
    
    // Common operations
    public abstract void add(CatalogComponent component);
    public abstract void remove(CatalogComponent component);
    public abstract List<CatalogComponent> getChildren();
    public abstract boolean isComposite();
    
    // Attribute operations
    public abstract Map<String, AttributeDefinition> getAttributes();
    public abstract List<Product> getAllProducts();
    
    // Navigation
    public List<CatalogComponent> getBreadcrumb() {
        List<CatalogComponent> path = new ArrayList<>();
        CatalogComponent current = this;
        
        while (current != null) {
            path.add(0, current); // Prepend
            current = current.parent;
        }
        
        return path;
    }
}

/**
 * Category (Composite)
 */
class Category extends CatalogComponent {
    private List<CatalogComponent> children = new ArrayList<>();
    private Map<String, AttributeDefinition> attributes = new HashMap<>();
    private String description;
    private CategoryMetadata metadata;
    
    public Category(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * INTERVIEW CRITICAL: Add child with cycle detection
     */
    @Override
    public void add(CatalogComponent component) {
        // Prevent cycles
        if (isAncestor(component)) {
            throw new CyclicHierarchyException(
                "Cannot add ancestor as child"
            );
        }
        
        children.add(component);
        component.parent = this;
        
        // Propagate attribute changes
        notifyAttributeChange();
    }
    
    @Override
    public void remove(CatalogComponent component) {
        children.remove(component);
        component.parent = null;
    }
    
    @Override
    public List<CatalogComponent> getChildren() {
        return new ArrayList<>(children);
    }
    
    @Override
    public boolean isComposite() {
        return true;
    }
    
    /**
     * INTERVIEW CRITICAL: Get all attributes including inherited
     */
    @Override
    public Map<String, AttributeDefinition> getAttributes() {
        Map<String, AttributeDefinition> allAttributes = new HashMap<>();
        
        // Inherit from parent first
        if (parent != null) {
            allAttributes.putAll(parent.getAttributes());
        }
        
        // Override with own attributes
        allAttributes.putAll(attributes);
        
        return allAttributes;
    }
    
    /**
     * INTERVIEW CRITICAL: Get all products in subtree (DFS)
     */
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        for (CatalogComponent child : children) {
            if (child.isComposite()) {
                products.addAll(child.getAllProducts());
            } else {
                products.add((Product) child);
            }
        }
        
        return products;
    }
    
    /**
     * Check if component is ancestor (prevents cycles)
     */
    private boolean isAncestor(CatalogComponent component) {
        CatalogComponent current = this.parent;
        
        while (current != null) {
            if (current.equals(component)) {
                return true;
            }
            current = current.parent;
        }
        
        return false;
    }
    
    /**
     * Define category-specific attribute
     */
    public void defineAttribute(String name, AttributeType type, boolean required) {
        AttributeDefinition attr = AttributeDefinition.builder()
            .name(name)
            .type(type)
            .required(required)
            .build();
        
        attributes.put(name, attr);
        
        // Notify descendants
        notifyAttributeChange();
    }
    
    /**
     * Propagate attribute changes to descendants
     */
    private void notifyAttributeChange() {
        for (CatalogComponent child : children) {
            if (child.isComposite()) {
                ((Category) child).notifyAttributeChange();
            } else {
                ((Product) child).refreshAttributes();
            }
        }
    }
}

/**
 * Product (Leaf)
 */
class Product extends CatalogComponent {
    private String sku;
    private String description;
    private BigDecimal price;
    private Map<String, Object> attributeValues = new HashMap<>();
    private ProductStatus status;
    
    public Product(UUID id, String name, String sku) {
        this.id = id;
        this.name = name;
        this.sku = sku;
    }
    
    @Override
    public void add(CatalogComponent component) {
        throw new UnsupportedOperationException("Cannot add to product");
    }
    
    @Override
    public void remove(CatalogComponent component) {
        throw new UnsupportedOperationException("Cannot remove from product");
    }
    
    @Override
    public List<CatalogComponent> getChildren() {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isComposite() {
        return false;
    }
    
    /**
     * Get attributes from parent category
     */
    @Override
    public Map<String, AttributeDefinition> getAttributes() {
        if (parent != null) {
            return parent.getAttributes();
        }
        return Collections.emptyMap();
    }
    
    @Override
    public List<Product> getAllProducts() {
        return List.of(this);
    }
    
    /**
     * Set attribute value with validation
     */
    public void setAttributeValue(String name, Object value) {
        Map<String, AttributeDefinition> attrs = getAttributes();
        AttributeDefinition def = attrs.get(name);
        
        if (def == null) {
            throw new AttributeNotFoundException("Attribute not defined: " + name);
        }
        
        if (!def.getType().isValid(value)) {
            throw new InvalidAttributeValueException(
                "Invalid value for " + name + ": " + value
            );
        }
        
        attributeValues.put(name, value);
    }
    
    /**
     * Refresh attributes when category changes
     */
    void refreshAttributes() {
        Map<String, AttributeDefinition> currentAttrs = getAttributes();
        
        // Remove obsolete attributes
        attributeValues.keySet().removeIf(key -> !currentAttrs.containsKey(key));
        
        // Validate required attributes
        for (AttributeDefinition attr : currentAttrs.values()) {
            if (attr.isRequired() && !attributeValues.containsKey(attr.getName())) {
                // Log warning or trigger validation
                logger.warn("Product {} missing required attribute: {}", 
                          sku, attr.getName());
            }
        }
    }
}
```

**Challenge 2: Efficient Category Search (Path Materialization)**
- **Problem**: Search products in category and all descendants without recursive DB queries
- **Solution**: Materialize ancestor paths for indexed queries
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Category search with path materialization
 */
class CatalogSearchService {
    
    /**
     * Search products within category hierarchy
     */
    public SearchResult searchProducts(UUID categoryId, SearchCriteria criteria) {
        // Get category and all descendants using materialized path
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        String path = category.getMaterializedPath();
        
        // Query products with path prefix
        // path = "/1/5/12" → matches "/1/5/12", "/1/5/12/18", "/1/5/12/25", etc.
        List<Product> products = productRepo.findByCategoryPathStartingWith(
            path,
            criteria.toSpecification()
        );
        
        // Apply filters
        List<Product> filtered = applyFilters(products, criteria);
        
        // Apply sorting
        List<Product> sorted = applySorting(filtered, criteria.getSortBy());
        
        // Paginate
        Page<Product> page = paginate(sorted, criteria.getPageRequest());
        
        // Build facets
        Map<String, List<FacetValue>> facets = buildFacets(products, criteria);
        
        return SearchResult.builder()
            .products(page.getContent())
            .totalCount(products.size())
            .facets(facets)
            .build();
    }
    
    /**
     * Materialize category path on save
     */
    @EventListener
    public void onCategorySaved(CategorySavedEvent event) {
        Category category = event.getCategory();
        
        // Build path: /ancestorId/.../parentId/currentId
        String path = buildMaterializedPath(category);
        category.setMaterializedPath(path);
        
        categoryRepo.save(category);
        
        // Update all descendants
        updateDescendantPaths(category);
    }
    
    /**
     * Build materialized path
     */
    private String buildMaterializedPath(Category category) {
        List<CatalogComponent> breadcrumb = category.getBreadcrumb();
        
        return breadcrumb.stream()
            .map(c -> c.getId().toString())
            .collect(Collectors.joining("/", "/", ""));
    }
    
    /**
     * Recursively update descendant paths
     */
    private void updateDescendantPaths(Category category) {
        for (CatalogComponent child : category.getChildren()) {
            if (child.isComposite()) {
                Category childCategory = (Category) child;
                String newPath = buildMaterializedPath(childCategory);
                childCategory.setMaterializedPath(newPath);
                categoryRepo.save(childCategory);
                
                updateDescendantPaths(childCategory);
            }
        }
    }
    
    /**
     * Apply attribute filters
     */
    private List<Product> applyFilters(List<Product> products, 
                                       SearchCriteria criteria) {
        return products.stream()
            .filter(product -> matchesFilters(product, criteria.getFilters()))
            .collect(Collectors.toList());
    }
    
    /**
     * Check if product matches all filters
     */
    private boolean matchesFilters(Product product, 
                                   Map<String, FilterValue> filters) {
        for (Map.Entry<String, FilterValue> entry : filters.entrySet()) {
            String attrName = entry.getKey();
            FilterValue filter = entry.getValue();
            
            Object productValue = product.getAttributeValues().get(attrName);
            
            if (!filter.matches(productValue)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Build facets for filter sidebar
     */
    private Map<String, List<FacetValue>> buildFacets(List<Product> products,
                                                      SearchCriteria criteria) {
        Map<String, List<FacetValue>> facets = new HashMap<>();
        
        // Get category attributes
        Category category = categoryRepo.findById(criteria.getCategoryId())
            .orElseThrow();
        Map<String, AttributeDefinition> attributes = category.getAttributes();
        
        // Build facet for each attribute
        for (AttributeDefinition attr : attributes.values()) {
            if (attr.isFacetable()) {
                List<FacetValue> values = buildFacet(products, attr);
                facets.put(attr.getName(), values);
            }
        }
        
        return facets;
    }
    
    /**
     * Build facet values with counts
     */
    private List<FacetValue> buildFacet(List<Product> products, 
                                        AttributeDefinition attr) {
        Map<Object, Long> valueCounts = products.stream()
            .map(p -> p.getAttributeValues().get(attr.getName()))
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));
        
        return valueCounts.entrySet().stream()
            .map(e -> new FacetValue(e.getKey().toString(), e.getValue()))
            .sorted(Comparator.comparingLong(FacetValue::getCount).reversed())
            .collect(Collectors.toList());
    }
}
```

**Challenge 3: Category Move (Reparenting)**
- **Problem**: Move entire subtree to new parent without breaking references
- **Solution**: Transaction with path recalculation
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Move category to new parent
 */
class CategoryMoveService {
    
    @Transactional
    public void moveCategory(UUID categoryId, UUID newParentId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        Category newParent = categoryRepo.findById(newParentId).orElseThrow();
        
        // Validate move
        validateMove(category, newParent);
        
        // Remove from old parent
        if (category.getParent() != null) {
            category.getParent().remove(category);
            categoryRepo.save((Category) category.getParent());
        }
        
        // Add to new parent
        newParent.add(category);
        categoryRepo.save(newParent);
        
        // Recalculate paths for entire subtree
        recalculatePaths(category);
        
        // Publish event for search index update
        eventPublisher.publishEvent(new CategoryMovedEvent(
            categoryId,
            newParentId
        ));
    }
    
    /**
     * Validate move operation
     */
    private void validateMove(Category category, Category newParent) {
        // Cannot move to self
        if (category.equals(newParent)) {
            throw new InvalidMoveException("Cannot move to self");
        }
        
        // Cannot move to descendant (creates cycle)
        if (isDescendant(category, newParent)) {
            throw new InvalidMoveException("Cannot move to descendant");
        }
        
        // Check permissions
        if (!hasPermission(category, "move")) {
            throw new UnauthorizedException("No permission to move category");
        }
    }
    
    /**
     * Check if target is descendant of source
     */
    private boolean isDescendant(Category source, Category target) {
        CatalogComponent current = target.getParent();
        
        while (current != null) {
            if (current.equals(source)) {
                return true;
            }
            current = current.getParent();
        }
        
        return false;
    }
    
    /**
     * Recalculate paths for subtree (BFS)
     */
    private void recalculatePaths(Category root) {
        Queue<Category> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            Category current = queue.poll();
            
            // Update path
            String newPath = buildMaterializedPath(current);
            current.setMaterializedPath(newPath);
            categoryRepo.save(current);
            
            // Enqueue children
            for (CatalogComponent child : current.getChildren()) {
                if (child.isComposite()) {
                    queue.offer((Category) child);
                }
            }
        }
    }
}
```

**Challenge 4: Category-Level Promotions**
- **Problem**: Apply discount to all products in category and descendants
- **Solution**: Visitor pattern to traverse tree
- **Algorithm**:
```java
/**
 * INTERVIEW CRITICAL: Apply promotion using Visitor pattern
 */
interface CatalogVisitor {
    void visit(Category category);
    void visit(Product product);
}

class PromotionApplier implements CatalogVisitor {
    private final Promotion promotion;
    private final List<Product> affectedProducts = new ArrayList<>();
    
    public PromotionApplier(Promotion promotion) {
        this.promotion = promotion;
    }
    
    @Override
    public void visit(Category category) {
        // Traverse children
        for (CatalogComponent child : category.getChildren()) {
            child.accept(this);
        }
    }
    
    @Override
    public void visit(Product product) {
        // Apply promotion
        if (promotion.isApplicable(product)) {
            BigDecimal discount = promotion.calculateDiscount(product.getPrice());
            product.setPromotionalPrice(product.getPrice().subtract(discount));
            affectedProducts.add(product);
        }
    }
    
    public List<Product> getAffectedProducts() {
        return affectedProducts;
    }
}

// Usage
Category electronics = categoryRepo.findByName("Electronics");
Promotion salePromotion = new Promotion("20% off Electronics", 0.20);

PromotionApplier applier = new PromotionApplier(salePromotion);
electronics.accept(applier);

List<Product> affected = applier.getAffectedProducts();
productRepo.saveAll(affected);
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Composite** | Category tree (leaf = product, composite = category) | Uniform treatment of nodes/leaves |
| **Visitor** | Traverse tree to apply promotions | Decouple operations from structure |
| **Specification** | Product search filters | Composable query criteria |
| **Strategy** | Search sorting (price, name, relevance) | Swap sorting algorithm |
| **Observer** | Notify on category changes | Update search index |
| **Repository** | Data access | Abstract persistence |
| **Path Materialization** | Efficient subtree queries | Avoid recursive DB calls |

### 5. Domain Model & Class Structure

```
                 CatalogComponent (Abstract)
                        ▲
                        │
           ┌────────────┴────────────┐
           │                         │
        Category                  Product
      (Composite)                 (Leaf)
    - children                  - sku
    - attributes                - price
    - add()                     - attributeValues
    - remove()
    - getAllProducts()
```

### 6. Detailed Sequence Diagrams

**Sequence: Search Products in Category**
```
Client  SearchSvc  CategoryRepo  ProductRepo  FacetBuilder
  │        │           │             │             │
  ├─search─>│          │             │             │
  │        ├─findById──>│            │             │
  │        │<─category─┤             │             │
  │        ├─findByPathPrefix────────>│            │
  │        │<─products─────────────────┤            │
  │        ├─applyFilters───┐          │            │
  │        │<───────────────┘          │            │
  │        ├─buildFacets───────────────────────────>│
  │        │<─facets──────────────────────────────┤
  │<─result┤           │             │             │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Attribute definition
public class AttributeDefinition {
    private String name;
    private AttributeType type; // STRING, NUMBER, BOOLEAN, ENUM
    private boolean required;
    private boolean facetable; // Show in filter sidebar
    private List<String> allowedValues; // For ENUM type
}

// Materialized path
public class Category extends CatalogComponent {
    @Column(name = "materialized_path", length = 4000)
    private String materializedPath; // "/1/5/12"
    
    // Index for efficient subtree queries
}

// Search criteria
public class SearchCriteria {
    private UUID categoryId;
    private Map<String, FilterValue> filters;
    private SortBy sortBy; // PRICE_ASC, PRICE_DESC, NAME, RELEVANCE
    private PageRequest pageRequest;
}

// Filter value
interface FilterValue {
    boolean matches(Object value);
}

class RangeFilter implements FilterValue {
    private Comparable min;
    private Comparable max;
    
    @Override
    public boolean matches(Object value) {
        if (!(value instanceof Comparable)) return false;
        Comparable comp = (Comparable) value;
        return (min == null || comp.compareTo(min) >= 0) &&
               (max == null || comp.compareTo(max) <= 0);
    }
}

class ExactFilter implements FilterValue {
    private Object expectedValue;
    
    @Override
    public boolean matches(Object value) {
        return Objects.equals(value, expectedValue);
    }
}

class InFilter implements FilterValue {
    private Set<Object> allowedValues;
    
    @Override
    public boolean matches(Object value) {
        return allowedValues.contains(value);
    }
}

// Repository queries
public interface ProductRepository {
    @Query("SELECT * FROM products " +
           "WHERE category_path LIKE :pathPrefix || '%'")
    List<Product> findByCategoryPathStartingWith(
        @Param("pathPrefix") String pathPrefix,
        Specification<Product> spec
    );
}
```

### 8. Thread Safety & Concurrency

**Category Modification:**
- Pessimistic lock on category during move
- Transaction ensures atomicity
- Path recalculation in single transaction

**Concurrent Searches:**
- Read-only operations (no locks needed)
- Eventual consistency for search index
- Stale reads acceptable

**Product Updates:**
- Optimistic locking (@Version)
- Retry on concurrent modification
- Queue updates to search index

### 9. Top Interview Questions & Answers

**Q1: Why Composite pattern?**
**A:**
```
Composite allows treating categories and products uniformly:
- getAllProducts() works on both
- Visitor traversal works on entire tree
- Simplifies client code (no type checking)

Alternative (rejected): Separate Category/Product hierarchies
→ Requires duplicate navigation logic
```

**Q2: Why materialize paths?**
**A:**
```sql
-- Without materialized path (recursive CTE)
WITH RECURSIVE subtree AS (
    SELECT id FROM categories WHERE id = ?
    UNION ALL
    SELECT c.id FROM categories c
    JOIN subtree s ON c.parent_id = s.id
)
SELECT * FROM products WHERE category_id IN (SELECT id FROM subtree);
-- Multiple roundtrips, slow

-- With materialized path
SELECT * FROM products WHERE category_path LIKE '/1/5/12%';
-- Single query, uses index, fast
```

**Q3: How to handle deep hierarchies (> 100 levels)?**
**A:**
```java
// Limit depth during category creation
if (getBreadcrumb().size() >= MAX_DEPTH) {
    throw new MaxDepthExceededException("Max depth: " + MAX_DEPTH);
}

// For pathological cases, use nested set model
// (left, right) instead of materialized path
```

**Q4: Database schema?**
**A:**
```sql
CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id UUID REFERENCES categories(id),
    materialized_path VARCHAR(4000),
    description TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_categories_path 
    ON categories(materialized_path);
CREATE INDEX idx_categories_parent 
    ON categories(parent_id);

CREATE TABLE category_attributes (
    id UUID PRIMARY KEY,
    category_id UUID REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    required BOOLEAN DEFAULT FALSE,
    facetable BOOLEAN DEFAULT FALSE,
    UNIQUE(category_id, name)
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    category_id UUID REFERENCES categories(id),
    category_path VARCHAR(4000),
    price DECIMAL(10, 2),
    status VARCHAR(20)
);

CREATE INDEX idx_products_category_path 
    ON products(category_path);
CREATE INDEX idx_products_sku 
    ON products(sku);

CREATE TABLE product_attribute_values (
    product_id UUID REFERENCES products(id),
    attribute_name VARCHAR(100),
    attribute_value TEXT,
    PRIMARY KEY (product_id, attribute_name)
);

CREATE INDEX idx_product_attrs_name_value 
    ON product_attribute_values(attribute_name, attribute_value);
```

**Q5: How to search across all categories?**
**A:**
```java
// Search from root category
Category root = categoryRepo.findRoot();
SearchResult result = searchService.searchProducts(
    root.getId(),
    criteria
);

// Or search with empty path filter
List<Product> products = productRepo.findAll(
    criteria.toSpecification()
);
```

**Q6: How to handle attribute conflicts during move?**
**A:**
```java
@Transactional
public void moveCategory(UUID categoryId, UUID newParentId) {
    Category category = categoryRepo.findById(categoryId).orElseThrow();
    Category newParent = categoryRepo.findById(newParentId).orElseThrow();
    
    // Check attribute compatibility
    Map<String, AttributeDefinition> parentAttrs = newParent.getAttributes();
    Map<String, AttributeDefinition> categoryAttrs = category.getAttributes();
    
    for (String attrName : categoryAttrs.keySet()) {
        AttributeDefinition parentDef = parentAttrs.get(attrName);
        AttributeDefinition categoryDef = categoryAttrs.get(attrName);
        
        if (parentDef != null && !parentDef.isCompatible(categoryDef)) {
            throw new AttributeConflictException(
                "Attribute " + attrName + " conflicts with parent"
            );
        }
    }
    
    // Proceed with move...
}
```

**Q7: Performance optimization for large catalogs?**
**A:**
```java
// 1. Cache hot categories (Electronics, Clothing)
@Cacheable(value = "categories", key = "#id")
public Category findById(UUID id) {
    return categoryRepo.findById(id).orElseThrow();
}

// 2. Lazy load children
@OneToMany(fetch = FetchType.LAZY)
private List<CatalogComponent> children;

// 3. Search index (Elasticsearch)
@Document(indexName = "products")
public class ProductDocument {
    private String id;
    private String name;
    private String categoryPath;
    private Map<String, Object> attributes;
}

// 4. Denormalize breadcrumb
@Column(name = "breadcrumb")
private String breadcrumb; // "Home > Electronics > Phones"
```

**Q8: How to test hierarchy operations?**
**A:**
```java
@Test
public void testCategoryMove() {
    // Setup
    Category root = new Category(UUID.randomUUID(), "Root");
    Category electronics = new Category(UUID.randomUUID(), "Electronics");
    Category clothing = new Category(UUID.randomUUID(), "Clothing");
    Category phones = new Category(UUID.randomUUID(), "Phones");
    
    root.add(electronics);
    root.add(clothing);
    electronics.add(phones);
    
    // Move phones to clothing
    moveService.moveCategory(phones.getId(), clothing.getId());
    
    // Verify
    assertEquals(clothing, phones.getParent());
    assertTrue(clothing.getChildren().contains(phones));
    assertFalse(electronics.getChildren().contains(phones));
    
    // Verify path updated
    assertEquals("/root/clothing/phones", phones.getMaterializedPath());
}
```

**Q9: How to handle category deletion?**
**A:**
```java
@Transactional
public void deleteCategory(UUID categoryId, DeletionStrategy strategy) {
    Category category = categoryRepo.findById(categoryId).orElseThrow();
    
    switch (strategy) {
        case CASCADE:
            // Delete category and all descendants
            deleteRecursive(category);
            break;
            
        case REPARENT:
            // Move children to parent
            Category parent = (Category) category.getParent();
            for (CatalogComponent child : category.getChildren()) {
                parent.add(child);
            }
            categoryRepo.delete(category);
            break;
            
        case FAIL_IF_NOT_EMPTY:
            if (!category.getChildren().isEmpty()) {
                throw new CategoryNotEmptyException();
            }
            categoryRepo.delete(category);
            break;
    }
}
```

**Q10: What metrics to track?**
**A:**
```
KPIs:
1. Search latency (p50, p95, p99)
2. Category depth distribution
3. Products per category (min, max, avg)
4. Facet usage (which filters used most)
5. Search result relevance score

Alerts:
- Search latency > 100ms
- Category tree depth > 20 levels
- Category with 0 products (orphan)
```

### 10. Extensions & Variations

1. **Multi-Taxonomy**: Product in multiple categories (fashion + seasonal)
2. **Virtual Categories**: Dynamic categories (e.g., "On Sale")
3. **Personalized Navigation**: Show categories based on user history
4. **A/B Testing**: Test different category structures
5. **Merchandising Rules**: Pin products to top of category

### 11. Testing Strategy

**Unit Tests:**
- Composite operations (add, remove, move)
- Attribute inheritance
- Path materialization
- Cycle detection

**Integration Tests:**
- Search with filters
- Category move with path recalculation
- Concurrent category updates

**Performance Tests:**
- Search 10M products < 50ms
- Navigate 100K category tree < 10ms
- 1K concurrent searches

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Recursive DB queries for subtree
✅ **Do**: Materialize paths

❌ **Avoid**: Storing inherited attributes on products
✅ **Do**: Compute at runtime from category

❌ **Avoid**: Tight coupling between category and product
✅ **Do**: Composite pattern with abstraction

❌ **Avoid**: Synchronous search index update
✅ **Do**: Async event-driven indexing

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Add category | O(1) | O(1) | Update parent reference |
| Move category | O(D) | O(D) | D = depth, recalculate paths |
| Get breadcrumb | O(D) | O(D) | Traverse to root |
| Get all products | O(N) | O(N) | N = products in subtree |
| Search | O(M + log M) | O(M) | M = matching products, sort |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Composite Pattern** | 30% | Correct abstraction, uniform interface |
| **Path Materialization** | 25% | Efficient subtree queries |
| **Attribute Inheritance** | 20% | Dynamic computation, propagation |
| **Move Operation** | 15% | Cycle detection, path recalculation |
| **Real-world Awareness** | 10% | Facets, filters, search index |

**Red Flags:**
- Recursive DB queries for subtree
- No cycle detection on add/move
- Storing computed attributes redundantly
- Synchronous blocking operations
- No consideration for search performance

---
