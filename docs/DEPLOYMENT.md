# Deployment Guide

## Prerequisites

- **Docker** ≥ 20.10
- **Docker Compose** ≥ 2.0
- **Java 17** (for local development)
- **Maven 3.8+** (for building services)
- **Git** (for cloning the repo)

---

## Step-by-Step Deployment

### Step 1: Clone and Navigate

```bash
cd /path/to/ecommerce_microservices
```

### Step 2: Start Infrastructure (Database, Redis, Kafka)

Start infrastructure services first:

```bash
# Start databases, Redis, Kafka, Zookeeper, n8n
docker-compose up -d user-db product-db order-db redis zookeeper kafka n8n

# Wait for services to be healthy
docker-compose ps

# Create Kafka topics
docker-compose up kafka-init
```

### Step 3: Verify Infrastructure

```bash
# Check MySQL databases
docker exec -it ecommerce-product-db mysql -u productservice -pproductservice123 -e "SHOW DATABASES;"
docker exec -it ecommerce-order-db mysql -u orderservice -porderservice123 -e "SHOW DATABASES;"

# Check Redis
docker exec -it ecommerce-redis redis-cli ping
# Expected: PONG

# Check Kafka topics
docker exec -it ecommerce-kafka kafka-topics --list --bootstrap-server localhost:9092
# Expected: product.created, product.updated, order.created, etc.

# Check n8n
curl http://localhost:5678/healthz
```

### Step 4: Build Microservices

```bash
# Build Product Service
cd product-service
./mvnw clean package -DskipTests
cd ..

# Build Order Service
cd order-service
./mvnw clean package -DskipTests
cd ..

# Build API Gateway
cd api-gateway
./mvnw clean package -DskipTests
cd ..
```

### Step 5: Run Services Locally (Development)

```bash
# Terminal 1: Product Service
cd product-service
./mvnw spring-boot:run

# Terminal 2: Order Service
cd order-service
./mvnw spring-boot:run

# Terminal 3: API Gateway
cd api-gateway
./mvnw spring-boot:run
```

### Step 5 (Alternative): Run Everything with Docker

```bash
# Build and start all services
docker-compose up -d --build

# Check all services are running
docker-compose ps

# View logs
docker-compose logs -f api-gateway product-service order-service
```

### Step 6: Verify Deployment

```bash
# API Gateway health
curl http://localhost:8080/actuator/health

# Product Service health (via gateway)
curl http://localhost:8080/api/products/actuator/health

# Order Service health (direct)
curl http://localhost:8083/actuator/health

# List gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

### Step 7: Import n8n Workflows

1. Open http://localhost:5678
2. Login with `admin` / `admin123`
3. Import `n8n/order-confirmation-workflow.json`
4. Configure SMTP credentials
5. Activate the workflow

---

## Service Ports

| Service | Port | Health Check URL |
|---------|------|------------------|
| API Gateway | 8080 | http://localhost:8080/actuator/health |
| User Service | 8081 | http://localhost:8081/actuator/health |
| Product Service | 8082 | http://localhost:8082/actuator/health |
| Order Service | 8083 | http://localhost:8083/actuator/health |
| MySQL (user_db) | 3306 | - |
| MySQL (product_db) | 3307 | - |
| MySQL (order_db) | 3308 | - |
| Redis | 6379 | redis-cli ping |
| Kafka | 9092 | - |
| Zookeeper | 2181 | - |
| n8n | 5678 | http://localhost:5678/healthz |

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | (set in compose) | JWT signing secret (min 256 bits) |
| `DB_HOST` | `localhost` | Database host |
| `DB_PORT` | varies | Database port |
| `DB_NAME` | varies | Database name |
| `DB_USERNAME` | varies | Database user |
| `DB_PASSWORD` | varies | Database password |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `REDIS_HOST` | `localhost` | Redis host |
| `REDIS_PORT` | `6379` | Redis port |
| `PRODUCT_SERVICE_URL` | `http://localhost:8082` | Product Service base URL |

---

## Troubleshooting

### Service won't start
```bash
# Check logs
docker-compose logs <service-name>

# Restart a specific service
docker-compose restart <service-name>
```

### Database connection refused
```bash
# Verify DB is healthy
docker-compose ps user-db product-db order-db

# Check if port is already in use
lsof -i :3306
lsof -i :3307
lsof -i :3308
```

### Kafka connection issues
```bash
# Check Kafka logs
docker-compose logs kafka

# Verify topics
docker exec -it ecommerce-kafka kafka-topics --list --bootstrap-server localhost:9092
```

### Clean restart
```bash
# Stop everything and remove volumes
docker-compose down -v

# Fresh start
docker-compose up -d --build
```
