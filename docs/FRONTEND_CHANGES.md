# Frontend Changes for Microservices Migration

> **Date:** 2026-03-23  
> **Angular Version:** 21  
> **Frontend Path:** `/home/ubuntu/github_repos/frontend`

---

### Overview

This document details all changes made to the Angular frontend to adapt it from the monolithic backend (port `8089`) to the new microservices architecture accessed through the **API Gateway** (port `8080`).

---

### Files Modified

| # | File | Change Summary |
|---|------|---------------|
| 1 | `src/environments/environment.ts` | `apiUrl` changed from `http://localhost:8089` → `http://localhost:8080` |
| 2 | `src/environments/environment.prod.ts` | `apiUrl` changed from `http://localhost:8089/api` → `http://localhost:8080`; removed `/api` suffix |
| 3 | `src/environments/environment.development.ts` | **New file** — development environment pointing to `http://localhost:8080` |
| 4 | `src/app/services/auth.service.ts` | URL path changed from `/auth` → `/api/auth` to match gateway routing |
| 5 | `src/app/services/wallet.service.ts` | URL path changed from `/wallet` → `/api/wallets` to match gateway routing |
| 6 | `src/app/services/product.service.ts` | `ProductRequest.categoryId` and `sellerId` types changed from `number` → `string` (UUID) |
| 7 | `src/app/services/order.service.ts` | `create()` method `userId` type changed from `number` → `string` (UUID) |
| 8 | `src/app/models/api.models.ts` | `CreateOrderDto.userId` type changed from `number` → `string` (UUID) |
| 9 | `src/app/models/ecommerce.model.ts` | `CreateProductDto.sellerId` type changed from `number` → `string` (UUID) |
| 10 | `src/app/components/admin/admin.component.ts` | Removed hardcoded `http://localhost:8080/api/admin`; now uses `environment.apiUrl + '/api'` |

---

### API Endpoint Mapping (Old → New)

All requests now go through the **API Gateway** at `http://localhost:8080` which routes to the appropriate microservice.

#### Authentication (User Service — port 8081)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/auth/login` | `http://localhost:8080/api/auth/login` | No JWT required |
| `http://localhost:8089/auth/register` | `http://localhost:8080/api/auth/register` | No JWT required |

#### Products (Product Service — port 8082)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/products` | `http://localhost:8080/api/products` | GET public (no auth), POST requires JWT |
| `http://localhost:8089/api/products/{id}` | `http://localhost:8080/api/products/{id}` | UUID instead of Long |
| `http://localhost:8089/api/products/search` | `http://localhost:8080/api/products/search` | Same query params |
| `http://localhost:8089/api/products/featured` | `http://localhost:8080/api/products/featured` | — |
| `http://localhost:8089/api/products/top-selling` | `http://localhost:8080/api/products/top-selling` | — |
| `http://localhost:8089/api/products/category/{id}` | `http://localhost:8080/api/products/category/{id}` | UUID |
| `http://localhost:8089/api/products/seller/{id}` | `http://localhost:8080/api/products/seller/{id}` | UUID |
| `http://localhost:8089/api/products/my-products` | `http://localhost:8080/api/products/my-products` | JWT required |
| `http://localhost:8089/api/products/{id}/stock` | `http://localhost:8080/api/products/{id}/stock` | UUID |
| `http://localhost:8089/api/products/{id}/feature` | `http://localhost:8080/api/products/{id}/feature` | UUID |

#### Categories (Product Service — port 8082)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/categories` | `http://localhost:8080/api/categories` | — |
| `http://localhost:8089/api/categories/{id}` | `http://localhost:8080/api/categories/{id}` | UUID |
| `http://localhost:8089/api/categories/name/{name}` | `http://localhost:8080/api/categories/name/{name}` | — |

#### Reviews (Product Service — port 8082)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/reviews` | `http://localhost:8080/api/reviews` | JWT required |
| `http://localhost:8089/api/reviews/{id}` | `http://localhost:8080/api/reviews/{id}` | UUID |

#### Orders (Order Service — port 8083)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/orders` | `http://localhost:8080/api/orders` | JWT required |
| `http://localhost:8089/api/orders/{id}` | `http://localhost:8080/api/orders/{id}` | UUID |
| `http://localhost:8089/api/orders/user/{userId}` | `http://localhost:8080/api/orders/user/{userId}` | UUID |
| `http://localhost:8089/api/orders/{id}/status` | `http://localhost:8080/api/orders/{id}/status` | UUID |
| `http://localhost:8089/api/orders/{id}/tracking` | `http://localhost:8080/api/orders/{id}/tracking` | UUID |
| `http://localhost:8089/api/orders/seller` | `http://localhost:8080/api/orders/seller` | — |

#### Cart (Order Service — port 8083)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/cart/{userId}` | `http://localhost:8080/api/cart/{userId}` | UUID, JWT required |
| `http://localhost:8089/api/cart/{userId}/items` | `http://localhost:8080/api/cart/{userId}/items` | UUID |
| `http://localhost:8089/api/cart/{userId}/items/{id}` | `http://localhost:8080/api/cart/{userId}/items/{id}` | UUID |
| `http://localhost:8089/api/cart/{userId}/clear` | `http://localhost:8080/api/cart/{userId}/clear` | UUID |

#### Inventory (Order Service — port 8083)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/inventory` | `http://localhost:8080/api/inventory` | JWT required |
| `http://localhost:8089/api/inventory/{id}` | `http://localhost:8080/api/inventory/{id}` | UUID |
| `http://localhost:8089/api/inventory/low-stock` | `http://localhost:8080/api/inventory/low-stock` | — |

#### Warehouses (Order Service — port 8083)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/warehouses` | `http://localhost:8080/api/warehouses` | JWT required |
| `http://localhost:8089/api/warehouses/{id}` | `http://localhost:8080/api/warehouses/{id}` | UUID |

#### Rentals (Order Service — port 8083)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/api/rentals` | `http://localhost:8080/api/rentals` | JWT required |
| `http://localhost:8089/api/rentals/{id}` | `http://localhost:8080/api/rentals/{id}` | UUID |

#### Wallets (User Service — port 8081)

| Old Monolithic Endpoint | New Gateway Endpoint | Notes |
|------------------------|---------------------|-------|
| `http://localhost:8089/wallet` | `http://localhost:8080/api/wallets` | Path changed, JWT required |
| `http://localhost:8089/wallet/add-funds` | `http://localhost:8080/api/wallets/add-funds` | — |
| `http://localhost:8089/wallet/transactions` | `http://localhost:8080/api/wallets/transactions` | — |

---

### Environment Configuration Changes

#### `environment.ts` (Development)
```typescript
// BEFORE (monolithic)
export const environment = {
  apiUrl: 'http://localhost:8089'
};

// AFTER (microservices via API Gateway)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

#### `environment.prod.ts` (Production)
```typescript
// BEFORE (monolithic)
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8089/api'
};

// AFTER (microservices via API Gateway)
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080'
};
```

> **Note:** The `/api` suffix was removed from `environment.prod.ts` because all service files already prepend `/api/` to their endpoint paths. The gateway routes expect `/api/products/**`, `/api/orders/**`, etc.

#### New: `environment.development.ts`
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

---

### UUID vs Long ID Changes

The microservices use **UUID** (string) for all entity IDs instead of **Long** (number).

| Model/Interface | Field | Old Type | New Type |
|----------------|-------|----------|----------|
| `ProductRequest` (product.service.ts) | `categoryId` | `number` | `string` |
| `ProductRequest` (product.service.ts) | `sellerId` | `number` | `string` |
| `CreateOrderDto` (api.models.ts) | `userId` | `number` | `string` |
| `CreateProductDto` (ecommerce.model.ts) | `sellerId` | `number` | `string` |
| `OrderService.create()` | `userId` param | `number` | `string` |

> **Impact:** Most of the frontend already used `string` types for IDs (e.g., `Product.id`, `Order.id`, `Category.id`). Only a few places used `number` for user/seller/category references which have been updated.

---

### Authentication Flow

The authentication flow remains the same with these key points:

1. **Login/Register** → `POST /api/auth/login` or `/api/auth/register` (no JWT required)
2. **Token Storage** → JWT is stored in `localStorage` under `auth_token` key
3. **Auth Interceptor** → `authInterceptor` reads `auth_token` and attaches `Authorization: Bearer <token>` header to all requests
4. **API Gateway JWT Filter** → Validates the JWT, extracts user claims, and adds `X-User-Id`, `X-User-Email`, `X-User-Role`, `X-User-Name` headers to requests forwarded to microservices
5. **Error Handling** → `errorInterceptor` catches `401` responses and redirects to login (except for auth endpoints)
6. **Token Expiry** → `AuthService.isAuthenticated()` checks JWT expiry client-side

```
Frontend → [Bearer Token] → API Gateway → [Validates JWT] → [X-User-* Headers] → Microservice
```

---

### Setup Instructions

#### Prerequisites
- Node.js 18+ and npm
- Angular CLI (`npm install -g @angular/cli`)
- Running microservices infrastructure (see `docs/DEPLOYMENT.md`)

#### Step 1: Install Dependencies
```bash
cd /home/ubuntu/github_repos/frontend
npm install
```

#### Step 2: Verify Environment Configuration
Ensure `src/environments/environment.ts` points to the API Gateway:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

#### Step 3: Start Backend Services
Make sure all backend services are running:
```bash
# Start infrastructure (MySQL, Redis, Kafka)
cd /home/ubuntu/ecommerce_microservices
docker-compose up -d user_db product_db order_db redis kafka zookeeper

# Start microservices
# User Service (port 8081)
# Product Service (port 8082)
# Order Service (port 8083)
# API Gateway (port 8080)
```

#### Step 4: Run the Frontend
```bash
cd /home/ubuntu/github_repos/frontend
ng serve
# OR
npm start
```

The frontend will be available at `http://localhost:4200`.

---

### Testing Instructions

#### Test with the New Backend

##### 1. Health Check
```bash
# API Gateway health
curl http://localhost:8080/actuator/health

# Product Service health (via Gateway)
curl http://localhost:8082/actuator/health

# Order Service health (via Gateway)
curl http://localhost:8083/actuator/health
```

##### 2. Authentication
```bash
# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "CLIENT"
  }'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test@example.com", "password": "password123"}'
# Response: { "token": "eyJ...", "userId": "uuid-string", ... }
```

##### 3. Products (via Gateway)
```bash
# Get all products (public — no auth needed)
curl http://localhost:8080/api/products

# Get product by ID (requires auth)
curl http://localhost:8080/api/products/{uuid} \
  -H "Authorization: Bearer <token>"

# Create product (requires SELLER/ADMIN role)
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "A test product",
    "price": 29.99,
    "stockQuantity": 100,
    "sellerId": "<uuid>",
    "categoryId": "<uuid>"
  }'
```

##### 4. Orders (via Gateway)
```bash
# Create order
curl -X POST "http://localhost:8080/api/orders?userId=<uuid>&shippingAddress=123+Main+St&paymentMethod=CARD" \
  -H "Authorization: Bearer <token>"

# Get user's orders
curl http://localhost:8080/api/orders/user/<uuid> \
  -H "Authorization: Bearer <token>"
```

##### 5. Cart (via Gateway)
```bash
# Add item to cart
curl -X POST "http://localhost:8080/api/cart/<userId>/items?productId=<productUuid>&quantity=2" \
  -H "Authorization: Bearer <token>"

# Get cart
curl http://localhost:8080/api/cart/<userId> \
  -H "Authorization: Bearer <token>"
```

#### Frontend Testing Checklist

| Feature | How to Test | Expected |
|---------|------------|----------|
| Login | Enter credentials on `/auth/login` | JWT stored, redirect to dashboard |
| Register | Fill form on `/auth/register` | Account created, JWT stored |
| Browse Products | Visit marketplace page | Products listed (public, no auth) |
| Product Detail | Click a product | Product details with UUID in URL |
| Add to Cart | Click "Add to Cart" button | Item added, cart count updates |
| View Cart | Navigate to cart page | Cart items displayed with prices |
| Create Order | Complete checkout flow | Order created, confirmation shown |
| Admin Panel | Login as ADMIN, visit `/admin` | Dashboard with management sections |
| Seller Products | Login as SELLER, view "My Products" | Seller's products listed |

---

### Troubleshooting

#### CORS Errors
The API Gateway is configured to allow origins `http://localhost:4200` and `http://localhost:3000`. If you see CORS errors:
- Verify the API Gateway is running on port 8080
- Check that `CorsConfig.java` includes your frontend origin
- Ensure preflight `OPTIONS` requests are not blocked

#### 401 Unauthorized
- Check that the JWT token is valid and not expired
- Verify the JWT secret matches between the User Service (issuer) and the API Gateway (validator)
- Look at the browser's Network tab → request headers for `Authorization: Bearer ...`
- The API Gateway logs at DEBUG level — check for JWT validation errors

#### 404 Not Found
- Verify the gateway route matches the request path
- Check that the target microservice is running
- Gateway routes: `/api/products/**` → port 8082, `/api/orders/**` → port 8083

#### Connection Refused
- Ensure all microservices are running
- Check infrastructure (MySQL, Redis, Kafka) is healthy
- Verify port mappings in `docker-compose.yml`

#### UUID Format Errors
- All IDs in the microservices are UUIDs (e.g., `550e8400-e29b-41d4-a716-446655440000`)
- If you see "Invalid UUID" errors, check that the frontend isn't sending numeric IDs
- Product, Order, Category, Review, and User IDs are all UUID strings

---

### Breaking Changes Summary

1. **API Base URL**: Changed from `http://localhost:8089` to `http://localhost:8080` (API Gateway)
2. **Auth Endpoint Path**: Changed from `/auth/*` to `/api/auth/*`
3. **Wallet Endpoint Path**: Changed from `/wallet/*` to `/api/wallets/*`
4. **ID Types**: All entity IDs are now UUIDs (strings) instead of Long (numbers)
5. **Admin Component**: No longer uses hardcoded URL; uses environment config
6. **Production Environment**: Removed `/api` suffix from `apiUrl` (services add their own `/api/` prefix)

---

### Architecture Diagram

```
┌─────────────────────┐
│   Angular Frontend   │
│   localhost:4200     │
│                     │
│  authInterceptor    │──── Bearer Token ────┐
│  errorInterceptor   │                      │
└─────────────────────┘                      ▼
                                 ┌──────────────────────┐
                                 │    API Gateway        │
                                 │    localhost:8080      │
                                 │                      │
                                 │  JWT Validation       │
                                 │  CORS Handling        │
                                 │  Route Dispatching    │
                                 └──────┬───────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
           ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
           │ User Service  │   │Product Service│   │ Order Service │
           │ localhost:8081│   │localhost:8082 │   │localhost:8083 │
           │               │   │               │   │               │
           │ /api/auth     │   │ /api/products │   │ /api/orders   │
           │ /api/users    │   │ /api/categories│  │ /api/cart     │
           │ /api/wallets  │   │ /api/reviews  │   │ /api/inventory│
           │ /api/wishlists│   │               │   │ /api/warehouses│
           │               │   │               │   │ /api/rentals  │
           └──────────────┘   └──────────────┘   └──────────────┘
```
