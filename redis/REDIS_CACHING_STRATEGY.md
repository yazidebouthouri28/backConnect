# Redis Caching Strategy

## Overview

Redis serves three roles in the microservices architecture:
1. **Data Caching** — Reduce database load for frequently accessed data
2. **Session Management** — Store JWT blacklist and user sessions
3. **Rate Limiting** — API Gateway rate limit counters

---

## What to Cache

### Product Service Caches

| Cache Name | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| `products` | `products:page:{page}:size:{size}` | 5 min | Paginated product listings |
| `product-detail` | `product:{id}` | 10 min | Individual product details |
| `categories` | `categories:all` | 1 hour | Category tree (changes rarely) |
| `categories` | `category:{id}` | 1 hour | Single category |
| `featured-products` | `featured:page:{page}` | 15 min | Featured/promoted products |
| `product-search` | `search:{hash(query)}` | 3 min | Search results |

### Order Service Caches

| Cache Name | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| `cart` | `cart:user:{userId}` | 30 min | User's current cart |
| `order-detail` | `order:{id}` | 5 min | Order details |
| `product-price-cache` | `product:{id}:price` | 10 min | Cached product prices for cart |

### API Gateway Caches

| Cache Name | Key Pattern | TTL | Description |
|-----------|-------------|-----|-------------|
| Token blacklist | `blacklist:token:{jti}` | Remaining token TTL | Revoked JWT tokens |
| Rate limit | `ratelimit:{ip}:{endpoint}` | 1 min | Request counters |

---

## Cache Configuration Examples

### Spring Boot `@Cacheable` Usage

```java
// Product Service - Get all products (cached)
@Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size")
public Page<ProductResponse> getAllProducts(int page, int size) {
    return productRepository.findAll(PageRequest.of(page, size))
            .map(mapper::toResponse);
}

// Product Service - Get single product (cached)
@Cacheable(value = "product-detail", key = "#id")
public ProductResponse getProductById(Long id) {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    return mapper.toResponse(product);
}

// Product Service - Cache eviction on update
@CacheEvict(value = {"products", "product-detail", "featured-products"}, allEntries = true)
public ProductResponse updateProduct(Long id, ProductRequest request) {
    // update logic
}

// Categories - long-lived cache
@Cacheable(value = "categories", key = "'all'")
public List<CategoryResponse> getAllCategories() {
    return categoryRepository.findAll().stream()
            .map(mapper::toCategoryResponse)
            .toList();
}
```

### Manual Redis Operations

```java
// Order Service - Cache product price from Product Service
@Autowired
private RedisTemplate<String, Object> redisTemplate;

public BigDecimal getProductPrice(Long productId) {
    String key = "product:" + productId + ":price";
    BigDecimal cached = (BigDecimal) redisTemplate.opsForValue().get(key);
    if (cached != null) return cached;

    // Fetch from Product Service via REST
    BigDecimal price = productServiceClient.getProductPrice(productId);
    redisTemplate.opsForValue().set(key, price, Duration.ofMinutes(10));
    return price;
}
```

---

## TTL Strategy

| Data Type | TTL | Rationale |
|-----------|-----|----------|
| Product listings | 5 min | Balance freshness with performance |
| Product details | 10 min | Individual products change less than lists |
| Categories | 1 hour | Categories rarely change |
| Cart | 30 min | Cart is session-bound, user expects persistence |
| Search results | 3 min | Search should reflect latest products |
| Token blacklist | Token's remaining lifetime | Auto-cleanup when token expires naturally |
| Rate limits | 1 min | Sliding window per minute |

---

## Cache Invalidation Strategy

### Event-Driven Invalidation

When a product is updated, the Kafka event triggers cache invalidation across services:

```
Product Updated → Kafka: product.updated
  → Product Service: @CacheEvict products, product-detail
  → Order Service: Delete product:{id}:price from Redis
```

### Invalidation Patterns

1. **Write-through**: Update DB + invalidate cache in same transaction
2. **Event-based**: Kafka consumer invalidates cache in other services
3. **TTL-based**: Let caches expire naturally for non-critical data

---

## Redis Memory Configuration

In `docker-compose.yml`:
```yaml
redis:
  command: redis-server --appendonly yes --maxmemory 256mb --maxmemory-policy allkeys-lru
```

- `appendonly yes`: Persist data to disk
- `maxmemory 256mb`: Cap memory usage
- `allkeys-lru`: Evict least-recently-used keys when memory is full
