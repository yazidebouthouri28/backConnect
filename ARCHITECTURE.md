# Architecture Documentation

## Table of Contents
1. [Global Architecture Overview](#1-global-architecture-overview)
2. [Authentication Flow](#2-authentication-flow)
3. [Product Operations Flow](#3-product-operations-flow)
4. [Order Creation Flow](#4-order-creation-flow)
5. [Event-Driven Communication](#5-event-driven-communication)
6. [Database Schemas](#6-database-schemas)
7. [Design Decisions](#7-design-decisions)
8. [Patterns & Best Practices](#8-patterns--best-practices)

---

## 1. Global Architecture Overview

```mermaid
flowchart TB
    subgraph Client["Client Layer"]
        Angular["Angular 21 Frontend\n:4200"]
    end

    subgraph Gateway["API Gateway Layer"]
        GW["Spring Cloud Gateway\n:8080\n- JWT Validation\n- Rate Limiting\n- CORS\n- Load Balancing"]
    end

    subgraph Services["Microservices Layer"]
        US["User Service\n:8081\n- Auth / JWT\n- User Profiles\n- Wallets\n- Wishlists"]
        PS["Product Service\n:8082\n- Product Catalog\n- Categories\n- Reviews\n- Inventory Alerts"]
        OS["Order Service\n:8083\n- Order Processing\n- Cart Management\n- Shipping\n- Inventory"]
    end

    subgraph Messaging["Event Bus"]
        Kafka["Apache Kafka\n:9092\nTopics:\n- product.created\n- product.updated\n- order.created\n- order.completed\n- inventory.low-stock"]
    end

    subgraph Cache["Caching Layer"]
        Redis["Redis\n:6379\n- Product Cache\n- Session Cache\n- Rate Limit Counters"]
    end

    subgraph Data["Data Layer"]
        UserDB[("user_db\nMySQL :3306")]
        ProductDB[("product_db\nMySQL :3307")]
        OrderDB[("order_db\nMySQL :3308")]
    end

    subgraph Workflows["Workflow Engine"]
        N8N["n8n\n:5678\n- Order Confirmation Emails\n- Low Stock Alerts\n- Report Generation"]
    end

    Angular -->|"HTTPS"| GW
    GW -->|"Route: /api/auth/**, /api/users/**"| US
    GW -->|"Route: /api/products/**, /api/categories/**"| PS
    GW -->|"Route: /api/orders/**, /api/cart/**"| OS

    US --> UserDB
    PS --> ProductDB
    OS --> OrderDB

    US --> Redis
    PS --> Redis
    OS --> Redis

    PS -->|"Publish Events"| Kafka
    OS -->|"Publish Events"| Kafka
    Kafka -->|"Consume Events"| PS
    Kafka -->|"Consume Events"| OS
    Kafka -->|"Webhook Trigger"| N8N

    style Angular fill:#dd3333,color:#fff
    style GW fill:#2563eb,color:#fff
    style US fill:#7c3aed,color:#fff
    style PS fill:#059669,color:#fff
    style OS fill:#d97706,color:#fff
    style Kafka fill:#231f20,color:#fff
    style Redis fill:#dc382d,color:#fff
    style N8N fill:#ff6d5a,color:#fff
```

---

## 2. Authentication Flow

```mermaid
sequenceDiagram
    participant C as Angular Client
    participant GW as API Gateway :8080
    participant US as User Service :8081
    participant R as Redis
    participant DB as user_db

    Note over C,DB: Registration Flow
    C->>GW: POST /api/auth/register {name, email, password}
    GW->>US: Forward (no auth required)
    US->>DB: Save new User entity
    DB-->>US: User saved
    US->>R: Cache user session
    US-->>GW: 201 {token, refreshToken, user}
    GW-->>C: 201 Created

    Note over C,DB: Login Flow
    C->>GW: POST /api/auth/login {email, password}
    GW->>US: Forward (no auth required)
    US->>DB: Find user by email
    DB-->>US: User entity
    US->>US: Validate password (BCrypt)
    US->>US: Generate JWT token (24h expiry)
    US->>R: Store session + blacklist check
    US-->>GW: 200 {token, user}
    GW-->>C: 200 OK + Set-Cookie

    Note over C,DB: Authenticated Request
    C->>GW: GET /api/products (Authorization: Bearer <token>)
    GW->>GW: Extract JWT from header
    GW->>R: Check token blacklist
    R-->>GW: Token valid
    GW->>GW: Validate JWT signature + expiry
    GW->>GW: Add X-User-Id, X-User-Role headers
    GW->>US: Forward to target service
    US-->>GW: Response
    GW-->>C: Response

    Note over C,DB: Logout Flow
    C->>GW: POST /api/auth/logout (Bearer <token>)
    GW->>US: Forward
    US->>R: Add token to blacklist (TTL = remaining expiry)
    US-->>GW: 200 OK
    GW-->>C: 200 Logged out
```

---

## 3. Product Operations Flow

```mermaid
sequenceDiagram
    participant C as Angular Client
    participant GW as API Gateway
    participant PS as Product Service :8082
    participant R as Redis
    participant DB as product_db
    participant K as Kafka
    participant OS as Order Service :8083

    Note over C,OS: Get Products (with cache)
    C->>GW: GET /api/products?page=0&size=20
    GW->>PS: Forward (authenticated)
    PS->>R: GET products:page:0:size:20
    alt Cache HIT
        R-->>PS: Cached product list
        PS-->>GW: 200 ProductResponse[]
    else Cache MISS
        R-->>PS: null
        PS->>DB: SELECT * FROM products LIMIT 20
        DB-->>PS: Product entities
        PS->>R: SET products:page:0:size:20 (TTL 5min)
        PS-->>GW: 200 ProductResponse[]
    end
    GW-->>C: 200 Product List

    Note over C,OS: Create Product (Seller)
    C->>GW: POST /api/products {name, price, category, ...}
    GW->>PS: Forward (role=SELLER required)
    PS->>DB: INSERT INTO products
    DB-->>PS: Product saved (id=123)
    PS->>R: Invalidate product cache keys
    PS->>K: Publish "product.created" {productId: 123, ...}
    PS-->>GW: 201 ProductResponse
    GW-->>C: 201 Created
    K-->>OS: Consume "product.created"
    OS->>OS: Initialize inventory record

    Note over C,OS: Update Product
    C->>GW: PUT /api/products/123 {price: 29.99}
    GW->>PS: Forward (owner or ADMIN)
    PS->>DB: UPDATE products SET price=29.99 WHERE id=123
    PS->>R: Invalidate cache for product:123
    PS->>K: Publish "product.updated" {productId: 123, changes: {price}}
    PS-->>GW: 200 Updated ProductResponse
    GW-->>C: 200 OK
    K-->>OS: Consume "product.updated"
    OS->>OS: Update local product reference price
```

---

## 4. Order Creation Flow

```mermaid
sequenceDiagram
    participant C as Angular Client
    participant GW as API Gateway
    participant OS as Order Service :8083
    participant PS as Product Service :8082
    participant K as Kafka
    participant DB as order_db
    participant N8N as n8n Workflows
    participant R as Redis

    Note over C,N8N: Add to Cart
    C->>GW: POST /api/cart/items {productId: 123, qty: 2}
    GW->>OS: Forward (authenticated)
    OS->>R: GET product:123:price (cached product info)
    alt Cache HIT
        R-->>OS: Product info
    else Cache MISS
        OS->>PS: GET /internal/products/123 (service-to-service)
        PS-->>OS: ProductDTO {price: 29.99, stock: 50}
        OS->>R: SET product:123:price (TTL 10min)
    end
    OS->>DB: INSERT/UPDATE cart_items
    OS-->>GW: 200 CartResponse
    GW-->>C: 200 Cart Updated

    Note over C,N8N: Place Order
    C->>GW: POST /api/orders {cartId, shippingAddressId, paymentMethod}
    GW->>OS: Forward (authenticated)
    OS->>DB: Load cart items for user
    OS->>PS: POST /internal/products/validate-stock [{id:123, qty:2}]
    PS-->>OS: {valid: true, items: [...]}
    OS->>DB: BEGIN TRANSACTION
    OS->>DB: INSERT INTO orders (orderNumber, totalAmount, status=PENDING)
    OS->>DB: INSERT INTO order_items (for each cart item)
    OS->>DB: DELETE FROM cart_items (clear cart)
    OS->>DB: COMMIT
    OS->>K: Publish "order.created" {orderId, userId, items[], total}
    OS-->>GW: 201 OrderResponse {orderNumber: "ORD-20260320-001"}
    GW-->>C: 201 Order Created

    K-->>PS: Consume "order.created"
    PS->>PS: Decrement stock for each item
    PS->>K: Publish "inventory.low-stock" (if stock < threshold)

    K-->>N8N: Webhook: "order.created"
    N8N->>N8N: Send order confirmation email

    Note over C,N8N: Order Completion
    OS->>DB: UPDATE orders SET status=DELIVERED
    OS->>K: Publish "order.completed" {orderId, userId}
    K-->>N8N: Webhook: "order.completed"
    N8N->>N8N: Send delivery confirmation email
```

---

## 5. Event-Driven Communication

```mermaid
flowchart LR
    subgraph Producers
        PS[Product Service]
        OS[Order Service]
        US[User Service]
    end

    subgraph Kafka["Apache Kafka Broker"]
        T1[product.created]
        T2[product.updated]
        T3[product.deleted]
        T4[order.created]
        T5[order.completed]
        T6[order.cancelled]
        T7[inventory.low-stock]
        T8[user.registered]
        T9[payment.processed]
    end

    subgraph Consumers
        PS2[Product Service]
        OS2[Order Service]
        N8N[n8n Workflows]
    end

    PS -->|publish| T1
    PS -->|publish| T2
    PS -->|publish| T3
    PS -->|publish| T7
    OS -->|publish| T4
    OS -->|publish| T5
    OS -->|publish| T6
    OS -->|publish| T9
    US -->|publish| T8

    T4 -->|consume| PS2
    T1 -->|consume| OS2
    T2 -->|consume| OS2
    T4 -->|webhook| N8N
    T5 -->|webhook| N8N
    T7 -->|webhook| N8N
    T8 -->|webhook| N8N

    style Kafka fill:#231f20,color:#fff
    style PS fill:#059669,color:#fff
    style OS fill:#d97706,color:#fff
    style US fill:#7c3aed,color:#fff
    style PS2 fill:#059669,color:#fff
    style OS2 fill:#d97706,color:#fff
    style N8N fill:#ff6d5a,color:#fff
```

---

## 6. Database Schemas

### 6.1 User Database (user_db)

```mermaid
erDiagram
    users {
        bigint id PK
        varchar name
        varchar username UK
        varchar email UK
        varchar password
        varchar role "ADMIN|BUYER|SELLER"
        boolean is_seller
        boolean is_buyer
        varchar store_name
        int loyalty_points
        varchar level
        varchar phone
        varchar address
        datetime created_at
        datetime updated_at
    }

    wallets {
        bigint id PK
        bigint user_id FK
        decimal balance
        varchar currency
        datetime created_at
        datetime updated_at
    }

    wishlists {
        bigint id PK
        bigint user_id FK
        varchar name
        datetime created_at
    }

    wishlist_items {
        bigint id PK
        bigint wishlist_id FK
        bigint product_id "external ref"
        datetime added_at
    }

    transactions {
        bigint id PK
        bigint wallet_id FK
        decimal amount
        varchar type "CREDIT|DEBIT"
        varchar description
        datetime created_at
    }

    users ||--o| wallets : has
    users ||--o{ wishlists : has
    wishlists ||--o{ wishlist_items : contains
    wallets ||--o{ transactions : records
```

### 6.2 Product Database (product_db)

```mermaid
erDiagram
    products {
        bigint id PK
        varchar name
        text description
        decimal price
        varchar sku UK
        int stock_quantity
        double rating
        boolean is_active
        boolean is_featured
        boolean is_rentable
        decimal rental_price_per_day
        bigint category_id FK
        bigint seller_id "external ref to user_db"
        datetime created_at
        datetime updated_at
    }

    categories {
        bigint id PK
        varchar name UK
        text description
        varchar image_url
        bigint parent_id FK "self-referencing"
        boolean is_active
    }

    reviews {
        bigint id PK
        bigint product_id FK
        bigint user_id "external ref to user_db"
        int rating
        text comment
        datetime created_at
    }

    product_images {
        bigint product_id FK
        varchar image_url
    }

    product_tags {
        bigint product_id FK
        varchar tag
    }

    products ||--o{ reviews : has
    products ||--o{ product_images : has
    products ||--o{ product_tags : has
    categories ||--o{ products : contains
    categories ||--o{ categories : "parent-child"
```

### 6.3 Order Database (order_db)

```mermaid
erDiagram
    orders {
        bigint id PK
        varchar order_number UK
        bigint user_id "external ref to user_db"
        decimal subtotal
        decimal tax_amount
        decimal shipping_cost
        decimal discount_amount
        decimal total_amount
        varchar status "PENDING|CONFIRMED|SHIPPED|DELIVERED|CANCELLED"
        varchar payment_status "UNPAID|PAID|REFUNDED"
        varchar payment_method
        bigint shipping_address_id FK
        datetime order_date
        datetime delivered_at
        datetime created_at
        datetime updated_at
    }

    order_items {
        bigint id PK
        bigint order_id FK
        bigint product_id "external ref to product_db"
        varchar product_name "denormalized"
        decimal unit_price "snapshot at order time"
        int quantity
        decimal total_price
    }

    carts {
        bigint id PK
        bigint user_id "external ref"
        datetime created_at
        datetime updated_at
    }

    cart_items {
        bigint id PK
        bigint cart_id FK
        bigint product_id "external ref"
        varchar product_name "denormalized"
        decimal unit_price
        int quantity
    }

    shipping_addresses {
        bigint id PK
        bigint user_id "external ref"
        varchar full_name
        varchar address_line1
        varchar address_line2
        varchar city
        varchar state
        varchar postal_code
        varchar country
        varchar phone
        boolean is_default
    }

    inventory {
        bigint id PK
        bigint product_id "external ref"
        int available_quantity
        int reserved_quantity
        int reorder_level
        varchar warehouse_id FK
        datetime last_restocked
    }

    warehouses {
        bigint id PK
        varchar name
        varchar location
        varchar address
        int capacity
        boolean is_active
    }

    orders ||--o{ order_items : contains
    orders ||--o| shipping_addresses : "ships to"
    carts ||--o{ cart_items : contains
    warehouses ||--o{ inventory : stores
```

---

## 7. Design Decisions

### 7.1 Why Microservices?

| Concern | Monolith Problem | Microservices Solution |
|---------|-----------------|----------------------|
| **Scalability** | Entire app scales together | Scale Product Service independently during flash sales |
| **Deployment** | Single large deployment | Independent deployments per service |
| **Technology** | Locked to one stack | Each service can evolve independently |
| **Team Ownership** | Shared codebase conflicts | Team per service (Product Team, Order Team) |
| **Fault Isolation** | One bug crashes everything | Product Service failure doesn't affect Order history |
| **Database** | Single shared database = tight coupling | Database per service = loose coupling |

### 7.2 Service Boundaries

We followed **Domain-Driven Design (DDD)** bounded contexts:

- **User Context**: Identity, authentication, profiles, wallets — things that answer "who is this person?"
- **Product Context**: Catalog, categories, reviews — things that answer "what can I buy?"
- **Order Context**: Cart, orders, shipping, inventory — things that answer "what did I buy and where is it?"

### 7.3 Communication Patterns

| Pattern | When | Example |
|---------|------|---------|
| **Synchronous (REST)** | Real-time data needed for request | Order Service validates stock with Product Service |
| **Asynchronous (Kafka)** | Fire-and-forget, eventual consistency | Product created → Order Service initializes inventory |
| **Cache (Redis)** | Frequently read, infrequently changed | Product catalog, user sessions |

### 7.4 Data Consistency Strategy

We use the **Saga Pattern** for distributed transactions:
1. Order Service creates order (PENDING)
2. Publishes `order.created` event
3. Product Service reserves stock
4. If stock unavailable → publishes `order.stock-failed` → Order Service cancels
5. If stock reserved → publishes `stock.reserved` → Order Service confirms

### 7.5 API Gateway Responsibilities

- **Single entry point** for all client requests
- **JWT validation** at the gateway level (services trust internal requests)
- **Route-based forwarding** to appropriate microservice
- **Rate limiting** to prevent abuse
- **CORS handling** centralized
- **Request/Response transformation** (add user context headers)

---

## 8. Patterns & Best Practices

### 8.1 Implemented Patterns

| Pattern | Implementation |
|---------|---------------|
| **API Gateway** | Spring Cloud Gateway with JWT filter |
| **Database per Service** | Separate MySQL instances (user_db, product_db, order_db) |
| **Event-Driven Architecture** | Kafka for async communication |
| **CQRS (light)** | Redis cache for reads, DB for writes |
| **Saga Pattern** | Choreography-based via Kafka events |
| **Circuit Breaker** | Resilience4j for inter-service calls |
| **Service Registry** | Docker Compose DNS (production: Eureka/Consul) |
| **Externalized Config** | application.yml per service + env variables |
| **Health Checks** | Spring Boot Actuator /health endpoints |

### 8.2 Security Best Practices

1. **JWT validated at Gateway** — services don't re-validate
2. **Internal communication** uses service-to-service tokens or network isolation
3. **Secrets management** via environment variables (production: Vault/K8s secrets)
4. **HTTPS everywhere** in production
5. **Rate limiting** at Gateway level
6. **Input validation** at each service boundary

### 8.3 Observability

- **Logging**: Structured JSON logs with correlation IDs
- **Metrics**: Micrometer + Prometheus (future)
- **Tracing**: Spring Cloud Sleuth / Micrometer Tracing (future)
- **Health**: `/actuator/health` on each service

### 8.4 Deployment Best Practices

1. **Docker Compose** for local development
2. **Kubernetes** for production (future)
3. **Blue-Green deployments** for zero-downtime
4. **Database migrations** with Flyway (recommended)
5. **Feature flags** for gradual rollout
