# Migration Guide: Monolith → Microservices

## Overview

This guide walks you through splitting the existing monolithic Spring Boot application (`tn.esprit.projetintegre`) into three microservices:

1. **User Service** (Phase 2) — Auth, Users, Wallets, Wishlists
2. **Product Service** (Phase 1) — Products, Categories, Reviews
3. **Order Service** (Phase 1) — Orders, Cart, Shipping, Inventory, Warehouses

---

## Phase 1: Product Service Migration

### Step 1: Copy Entity Classes

Copy these files from the monolith to the Product Service:

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/entities/
# TO:   product-service/src/main/java/tn/esprit/productservice/entities/

Product.java
Category.java
Review.java
```

**Required changes in each entity:**

```java
// 1. Change package declaration
package tn.esprit.productservice.entities;  // was: tn.esprit.projetintegre.entities

// 2. Remove relationships to entities that live in other services
// In Product.java:
//   REMOVE: @ManyToOne User seller;  
//   REPLACE WITH: Long sellerId;  // External reference

// 3. Keep internal relationships intact
//   KEEP: @ManyToOne Category category;
//   KEEP: @OneToMany List<Review> reviews;
```

**Product.java changes:**
```java
// BEFORE (monolith):
@ManyToOne
@JoinColumn(name = "seller_id")
private User seller;

// AFTER (microservice):
@Column(name = "seller_id")
private Long sellerId;  // References user_db.users.id
```

**Review.java changes:**
```java
// BEFORE:
@ManyToOne
private User user;

// AFTER:
@Column(name = "user_id")
private Long userId;  // References user_db.users.id
```

### Step 2: Copy Repository Interfaces

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/repositories/
# TO:   product-service/src/main/java/tn/esprit/productservice/repositories/

ProductRepository.java
CategoryRepository.java
ReviewRepository.java   (if exists, otherwise create)
```

**Changes:**
```java
package tn.esprit.productservice.repositories;  // Update package

import tn.esprit.productservice.entities.Product;  // Update imports
```

### Step 3: Copy Service Classes

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/services/
# TO:   product-service/src/main/java/tn/esprit/productservice/services/

ProductService.java
CategoryService.java
```

**Required changes:**

```java
package tn.esprit.productservice.services;

// 1. Update all imports
import tn.esprit.productservice.entities.*;
import tn.esprit.productservice.repositories.*;
import tn.esprit.productservice.events.ProductEventPublisher;

// 2. Replace User references with userId
// BEFORE:
public Product createProduct(ProductRequest req, User seller) {
    product.setSeller(seller);
}
// AFTER:
public Product createProduct(ProductRequest req, Long sellerId) {
    product.setSellerId(sellerId);  // userId from X-User-Id header
}

// 3. Add event publishing
@Autowired
private ProductEventPublisher eventPublisher;

public ProductResponse createProduct(ProductRequest req, Long sellerId) {
    Product product = /* existing save logic */;
    
    // NEW: Publish Kafka event
    eventPublisher.publishProductCreated(ProductEvent.builder()
        .productId(product.getId())
        .productName(product.getName())
        .price(product.getPrice())
        .stockQuantity(product.getStockQuantity())
        .timestamp(LocalDateTime.now())
        .build());
    
    return mapper.toResponse(product);
}

// 4. Add Redis caching annotations
@Cacheable(value = "products", key = "'page:' + #page + ':size:' + #size")
public Page<ProductResponse> getAllProducts(int page, int size) { ... }

@CacheEvict(value = {"products", "product-detail"}, allEntries = true)
public ProductResponse updateProduct(Long id, ProductRequest req) { ... }
```

### Step 4: Copy Controllers

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/controllers/
# TO:   product-service/src/main/java/tn/esprit/productservice/controllers/

ProductController.java
CategoryController.java
```

**Required changes:**

```java
package tn.esprit.productservice.controllers;

// 1. Replace @AuthenticationPrincipal with header extraction
// BEFORE:
@PostMapping
public ResponseEntity<ProductResponse> createProduct(
    @RequestBody ProductRequest req,
    @AuthenticationPrincipal UserDetails user) {
    return ResponseEntity.ok(productService.create(req, user));
}

// AFTER:
@PostMapping
public ResponseEntity<ProductResponse> createProduct(
    @RequestBody ProductRequest req,
    @RequestHeader("X-User-Id") Long userId,
    @RequestHeader(value = "X-User-Role", required = false) String role) {
    return ResponseEntity.ok(productService.create(req, userId));
}
```

### Step 5: Copy DTOs and Mapper

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/dto/
# TO:   product-service/src/main/java/tn/esprit/productservice/dto/

request/ProductRequest.java
request/CategoryRequest.java
response/ProductResponse.java
response/CategoryResponse.java

# FROM: backend/src/main/java/tn/esprit/projetintegre/mapper/
# TO:   product-service/src/main/java/tn/esprit/productservice/mapper/

DtoMapper.java  (copy only product-related methods)
```

### Step 6: Copy Exception Handling

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/exception/
# TO:   product-service/src/main/java/tn/esprit/productservice/exception/

ResourceNotFoundException.java
GlobalExceptionHandler.java
```

### Step 7: Copy Enums

```
# Copy only product-related enums
ProductStatus.java (if exists)
```

---

## Phase 1: Order Service Migration

### Step 1: Copy Entity Classes

```
# FROM: backend/src/main/java/tn/esprit/projetintegre/entities/
# TO:   order-service/src/main/java/tn/esprit/orderservice/entities/

Order.java
OrderItem.java
Cart.java
CartItem.java
ShippingAddress.java
Inventory.java        (if exists)
Warehouse.java        (if exists)
Rental.java           (if exists)
```

**Required changes:**

```java
// Order.java:
// BEFORE:
@ManyToOne
private User user;
// AFTER:
@Column(name = "user_id")
private Long userId;

// OrderItem.java:
// BEFORE:
@ManyToOne
private Product product;
// AFTER:
@Column(name = "product_id")
private Long productId;
@Column(name = "product_name")
private String productName;  // Denormalized for display
@Column(name = "unit_price")
private BigDecimal unitPrice; // Snapshot at order time

// CartItem.java:
// BEFORE:
@ManyToOne
private Product product;
// AFTER:
@Column(name = "product_id")
private Long productId;
private String productName;
private BigDecimal unitPrice;
```

### Step 2: Copy Repositories

```
OrderRepository.java
CartRepository.java
CartItemRepository.java
ShippingAddressRepository.java
InventoryRepository.java
WarehouseRepository.java
```

### Step 3: Copy Services

```
OrderService.java
CartService.java
ShippingAddressService.java
InventoryService.java
WarehouseService.java
RentalService.java
```

**Key changes:**

```java
// Replace direct Product queries with inter-service calls:
// BEFORE:
Product product = productRepository.findById(productId).orElseThrow();

// AFTER:
@Autowired
private WebClient productServiceWebClient;

public ProductDTO getProduct(Long productId) {
    return productServiceWebClient.get()
        .uri("/api/products/{id}", productId)
        .retrieve()
        .bodyToMono(ProductDTO.class)
        .block();
}

// Add event publishing after order creation:
@Autowired
private OrderEventPublisher eventPublisher;

public OrderResponse createOrder(OrderRequest req, Long userId) {
    Order order = /* existing save logic */;
    
    eventPublisher.publishOrderCreated(OrderEvent.builder()
        .orderId(order.getId())
        .orderNumber(order.getOrderNumber())
        .userId(userId)
        .totalAmount(order.getTotalAmount())
        .items(mapItems(order.getOrderItems()))
        .timestamp(LocalDateTime.now())
        .build());
    
    return mapper.toResponse(order);
}
```

### Step 4: Copy Controllers

```
OrderController.java
CartController.java
ShippingAddressController.java
InventoryController.java
WarehouseController.java
RentalController.java
```

### Step 5: Copy DTOs, Mapper, Exceptions, Enums

Same pattern as Product Service — copy only order-related files.

---

## Database Migration Strategy

### Option A: Schema Split (Recommended for Phase 1)

1. **Export existing data** from the monolith database:
   ```sql
   -- Product data
   mysqldump -u root -p ecommerce_db products categories reviews \
       product_images product_tags > product_data.sql
   
   -- Order data
   mysqldump -u root -p ecommerce_db orders order_items carts cart_items \
       shipping_addresses inventory warehouses > order_data.sql
   ```

2. **Import into separate databases**:
   ```sql
   mysql -u productservice -p product_db < product_data.sql
   mysql -u orderservice -p order_db < order_data.sql
   ```

3. **Update foreign key references**:
   ```sql
   -- In product_db: Replace user FK with plain column
   ALTER TABLE products DROP FOREIGN KEY fk_product_seller;
   ALTER TABLE products ADD COLUMN seller_id BIGINT;
   UPDATE products p SET p.seller_id = (SELECT id FROM ecommerce_db.users WHERE ...);
   
   -- In order_db: Denormalize product info into order_items
   ALTER TABLE order_items ADD COLUMN product_name VARCHAR(255);
   ALTER TABLE order_items ADD COLUMN unit_price DECIMAL(10,2);
   UPDATE order_items oi SET 
       oi.product_name = (SELECT name FROM ecommerce_db.products WHERE id = oi.product_id),
       oi.unit_price = (SELECT price FROM ecommerce_db.products WHERE id = oi.product_id);
   ```

### Option B: Fresh Start (for new deployments)

Let Hibernate `ddl-auto: update` create schemas from entities. Only migrate data if needed.

---

## Code Refactoring Checklist

### Product Service ✅
- [ ] Copy `Product`, `Category`, `Review` entities
- [ ] Update package declarations (`tn.esprit.productservice`)
- [ ] Replace `User` entity references with `Long sellerId`/`Long userId`
- [ ] Copy `ProductRepository`, `CategoryRepository`
- [ ] Copy `ProductService`, `CategoryService`
- [ ] Add `ProductEventPublisher` calls in service methods
- [ ] Add `@Cacheable`/`@CacheEvict` annotations
- [ ] Copy `ProductController`, `CategoryController`
- [ ] Replace `@AuthenticationPrincipal` with `@RequestHeader("X-User-Id")`
- [ ] Copy product-related DTOs (request + response)
- [ ] Copy product-related mapper methods
- [ ] Copy `GlobalExceptionHandler`
- [ ] Copy product-related enums
- [ ] Remove Spring Security dependency (gateway handles auth)
- [ ] Update `application.yml` with correct database/port
- [ ] Test all CRUD endpoints

### Order Service ✅
- [ ] Copy `Order`, `OrderItem`, `Cart`, `CartItem`, `ShippingAddress` entities
- [ ] Copy `Inventory`, `Warehouse`, `Rental` entities
- [ ] Update package declarations (`tn.esprit.orderservice`)
- [ ] Replace `User` references with `Long userId`
- [ ] Replace `Product` references with `Long productId` + denormalized fields
- [ ] Copy all order-related repositories
- [ ] Copy all order-related services
- [ ] Replace direct Product queries with WebClient calls
- [ ] Add `OrderEventPublisher` calls
- [ ] Add Redis caching where appropriate
- [ ] Copy all order-related controllers
- [ ] Replace `@AuthenticationPrincipal` with `@RequestHeader("X-User-Id")`
- [ ] Copy order-related DTOs and mapper methods
- [ ] Copy `GlobalExceptionHandler`
- [ ] Copy order-related enums
- [ ] Remove Spring Security dependency
- [ ] Update `application.yml`
- [ ] Test all CRUD endpoints

### API Gateway ✅
- [ ] JWT filter validates tokens correctly
- [ ] Routes forward to correct services
- [ ] CORS allows Angular frontend
- [ ] Public endpoints (auth, product browsing) skip JWT
- [ ] User context headers (X-User-Id, X-User-Role) propagated

### Infrastructure ✅
- [ ] Docker Compose starts all services
- [ ] Kafka topics created
- [ ] Redis accessible from all services
- [ ] n8n workflow imported and active
- [ ] All health checks passing

---

## Common Pitfalls

1. **Circular imports**: Make sure entities don't reference classes from other services
2. **Lazy loading**: With `open-in-view: false`, ensure eager fetching where needed
3. **Transaction boundaries**: Transactions are now per-service; use Saga for cross-service
4. **Missing data**: Denormalize frequently-needed data (product name in order_items)
5. **Security**: Don't forget to remove `@PreAuthorize` — Gateway handles auth now
6. **Port conflicts**: Each service runs on a different port (8081, 8082, 8083)
