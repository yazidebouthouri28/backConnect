# API Testing Guide

## Prerequisites

All services should be running. Test via the API Gateway (port 8080).

---

## Authentication

### Register a new user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "role": "BUYER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Save the returned token:**
```bash
export TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

---

## Product Service (port 8082 / via Gateway 8080)

### List Products (Public - no auth required)
```bash
curl http://localhost:8080/api/products
```

### Get Product by ID
```bash
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/products/1
```

### Create Product (Seller only)
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Headphones",
    "description": "High-quality Bluetooth headphones",
    "price": 49.99,
    "sku": "WH-001",
    "stockQuantity": 100,
    "categoryId": 1,
    "images": ["https://upload.wikimedia.org/wikipedia/commons/thumb/0/00/S%C5%82uchawki_referencyjne_K-701_firmy_AKG.jpg/250px-S%C5%82uchawki_referencyjne_K-701_firmy_AKG.jpg
    "tags": ["electronics", "audio"]
  }'
```

### Update Product
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Headphones Pro",
    "price": 59.99
  }'
```

### Delete Product
```bash
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $TOKEN"
```

### List Categories
```bash
curl http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN"
```

### Create Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'
```

---

## Order Service (port 8083 / via Gateway 8080)

### Add to Cart
```bash
curl -X POST http://localhost:8080/api/cart/items \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

### View Cart
```bash
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer $TOKEN"
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shippingAddressId": 1,
    "paymentMethod": "CREDIT_CARD"
  }'
```

### List Orders
```bash
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

### Get Order by ID
```bash
curl http://localhost:8080/api/orders/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Update Order Status (Admin)
```bash
curl -X PUT http://localhost:8080/api/orders/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{ "status": "SHIPPED" }'
```

### Add Shipping Address
```bash
curl -X POST http://localhost:8080/api/shipping-addresses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "addressLine1": "123 Main Street",
    "city": "Tunis",
    "state": "Tunis",
    "postalCode": "1000",
    "country": "Tunisia",
    "phone": "+216 12 345 678",
    "isDefault": true
  }'
```

### Check Inventory
```bash
curl http://localhost:8080/api/inventory/product/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Health Check Endpoints

```bash
# Gateway
curl http://localhost:8080/actuator/health

# Product Service (direct)
curl http://localhost:8082/actuator/health

# Order Service (direct)
curl http://localhost:8083/actuator/health

# Gateway routes
curl http://localhost:8080/actuator/gateway/routes | jq .
```

---

## Postman Collection Tips

1. Create environment variables:
   - `BASE_URL`: `http://localhost:8080`
   - `TOKEN`: (set after login)

2. Add a Pre-request Script to login test:
   ```javascript
   // Auto-save token after login
   if (pm.response.code === 200) {
       var token = pm.response.json().token;
       pm.environment.set("TOKEN", token);
   }
   ```

3. Add Authorization header to all requests:
   - Type: Bearer Token
   - Token: `{{TOKEN}}`

---

## Kafka Event Testing

### Produce a test event
```bash
docker exec -it ecommerce-kafka kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic product.created \
  --property "parse.key=true" \
  --property "key.separator=:"
```
Then type:
```
123:{"eventType":"CREATED","productId":123,"productName":"Test Product","price":29.99,"stockQuantity":50,"timestamp":"2026-03-20T12:00:00"}
```

### Consume events
```bash
docker exec -it ecommerce-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic order.created \
  --from-beginning
```

---

## Redis Testing

```bash
# Connect to Redis
docker exec -it ecommerce-redis redis-cli

# Check cached keys
KEYS *

# Check specific cache
GET products:page:0:size:20

# Monitor real-time commands
MONITOR

# Clear all caches
FLUSHALL
```
