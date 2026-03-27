# E-Commerce Microservices Architecture

## Overview

This package provides everything needed to transform the existing monolithic Spring Boot e-commerce application into a microservices architecture. It includes architecture diagrams, configuration templates, Docker infrastructure, event-driven communication setup, and a step-by-step migration guide.

### Architecture at a Glance

```
┌─────────────────────────────────────────────────────────────┐
│                    Angular 21 Frontend                       │
│                       :4200                                  │
└─────────────────────────┬───────────────────────────────────┘
                          │ HTTPS
┌─────────────────────────▼───────────────────────────────────┐
│              Spring Cloud API Gateway                        │
│              :8080 (JWT + CORS + Routing)                    │
└───────┬──────────────────┬─────────────────┬────────────────┘
        │                  │                 │
┌───────▼──────┐  ┌────────▼───────┐  ┌─────▼────────┐
│ User Service │  │ Product Service│  │ Order Service │
│   :8081      │  │    :8082       │  │    :8083      │
│ (Phase 2)    │  │  (Phase 1)     │  │  (Phase 1)    │
└───────┬──────┘  └────────┬───────┘  └─────┬────────┘
        │                  │                 │
   [user_db]          [product_db]      [order_db]
    MySQL               MySQL            MySQL
        └──────────┬───────┴────────┬────┘
                   │                │
            ┌──────▼──────┐  ┌─────▼─────┐
            │  Apache     │  │   Redis    │
            │  Kafka      │  │   :6379    │
            │  :9092      │  └───────────┘
            └──────┬──────┘
                   │
            ┌──────▼──────┐
            │     n8n     │
            │   :5678     │
            │  (Emails)   │
            └─────────────┘
```

---

## Project Structure

```
ecommerce_microservices/
├── README.md                          # This file
├── ARCHITECTURE.md                    # Diagrams + design decisions
├── docker-compose.yml                 # Full infrastructure
│
├── api-gateway/                       # Spring Cloud Gateway
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/tn/esprit/gateway/
│       │   ├── ApiGatewayApplication.java
│       │   ├── config/CorsConfig.java
│       │   └── filter/JwtAuthenticationFilterFactory.java
│       └── resources/application.yml
│
├── product-service/                   # Product Catalog Microservice
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/tn/esprit/productservice/
│       │   ├── ProductServiceApplication.java
│       │   ├── config/           # Kafka, Redis configs
│       │   ├── controllers/      # ← Copy ProductController here
│       │   ├── dto/request/      # ← Copy ProductRequest here
│       │   ├── dto/response/     # ← Copy ProductResponse here
│       │   ├── entities/         # ← Copy Product, Category, Review here
│       │   ├── enums/            # ← Copy product-related enums
│       │   ├── events/           # Kafka producers + consumers
│       │   ├── exception/        # ← Copy GlobalExceptionHandler
│       │   ├── mapper/           # ← Copy DtoMapper (product methods)
│       │   ├── repositories/     # ← Copy ProductRepository here
│       │   └── services/         # ← Copy ProductService here
│       └── resources/application.yml
│
├── order-service/                     # Order Processing Microservice
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/
│       ├── java/tn/esprit/orderservice/
│       │   ├── OrderServiceApplication.java
│       │   ├── config/           # Kafka, Redis, WebClient configs
│       │   ├── controllers/      # ← Copy OrderController, CartController
│       │   ├── dto/request/      # ← Copy OrderRequest, CartRequest
│       │   ├── dto/response/     # ← Copy OrderResponse, CartResponse
│       │   ├── entities/         # ← Copy Order, Cart, Inventory, etc.
│       │   ├── enums/            # ← Copy order-related enums
│       │   ├── events/           # Kafka producers + consumers
│       │   ├── exception/        # ← Copy GlobalExceptionHandler
│       │   ├── mapper/           # ← Copy DtoMapper (order methods)
│       │   ├── repositories/     # ← Copy OrderRepository, CartRepository
│       │   └── services/         # ← Copy OrderService, CartService
│       └── resources/application.yml
│
├── kafka/                             # Kafka Configuration
│   ├── config/
│   │   ├── kafka-producer-config.md
│   │   └── kafka-consumer-config.md
│   └── schemas/                       # Event JSON Schemas
│       ├── product-created.json
│       ├── product-updated.json
│       ├── order-created.json
│       ├── order-completed.json
│       └── inventory-low-stock.json
│
├── redis/
│   └── REDIS_CACHING_STRATEGY.md      # Caching documentation
│
├── n8n/
│   ├── order-confirmation-workflow.json
│   └── N8N_INTEGRATION_GUIDE.md
│
├── docs/
│   ├── MIGRATION_GUIDE.md             # Step-by-step migration
│   ├── DEPLOYMENT.md                  # Deployment instructions
│   └── TESTING.md                     # API testing examples
│
└── analysis.md                        # Original monolith analysis
```

---

## Quick Start

### 1. Start Infrastructure
```bash
docker-compose up -d user-db product-db order-db redis zookeeper kafka n8n
docker-compose up kafka-init
```

### 2. Migrate Code (see docs/MIGRATION_GUIDE.md)
Copy your existing controllers, services, entities, and repositories into the appropriate microservice directories following the migration guide.

### 3. Build & Run
```bash
# Build all services
cd product-service && ./mvnw clean package -DskipTests && cd ..
cd order-service && ./mvnw clean package -DskipTests && cd ..
cd api-gateway && ./mvnw clean package -DskipTests && cd ..

# Run with Docker
docker-compose up -d --build
```

### 4. Test
```bash
# Health check
curl http://localhost:8080/actuator/health

# Browse products (public)
curl http://localhost:8080/api/products
```

---

## Key Documents

| Document | Description |
|----------|-------------|
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Architecture diagrams, flow charts, design decisions |
| [docs/MIGRATION_GUIDE.md](./docs/MIGRATION_GUIDE.md) | Step-by-step code migration instructions |
| [docs/DEPLOYMENT.md](./docs/DEPLOYMENT.md) | How to deploy everything |
| [docs/TESTING.md](./docs/TESTING.md) | API testing with curl examples |
| [redis/REDIS_CACHING_STRATEGY.md](./redis/REDIS_CACHING_STRATEGY.md) | Caching strategy and configuration |
| [n8n/N8N_INTEGRATION_GUIDE.md](./n8n/N8N_INTEGRATION_GUIDE.md) | Email workflow setup |
| [kafka/config/](./kafka/config/) | Kafka producer/consumer configuration |

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Backend | Spring Boot | 3.2.5 |
| Language | Java | 17 |
| API Gateway | Spring Cloud Gateway | 2023.0.1 |
| Database | MySQL | 8.0 |
| Messaging | Apache Kafka | 7.5.0 (Confluent) |
| Caching | Redis | 7 (Alpine) |
| Workflows | n8n | Latest |
| Containers | Docker + Docker Compose | 3.8 |

---

## Migration Phases

### Phase 1 (Current) — Product & Order Services
- ✅ Product Service (Catalog, Categories, Reviews)
- ✅ Order Service (Orders, Cart, Shipping, Inventory)
- ✅ API Gateway with JWT
- ✅ Kafka event infrastructure
- ✅ Redis caching
- ✅ n8n email workflows

### Phase 2 (Future) — User Service
- User Service (Auth, Profiles, Wallets, Wishlists)
- Service-to-service authentication
- Distributed tracing (Micrometer)
- Kubernetes deployment manifests
