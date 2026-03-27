# Comprehensive E-Commerce Monolith Analysis

## Table of Contents
1. [Current Monolithic Architecture Overview](#1-current-monolithic-architecture-overview)
2. [Backend Analysis](#2-backend-analysis)
3. [Frontend Analysis](#3-frontend-analysis)
4. [Database Schema & Relationships](#4-database-schema--relationships)
5. [API Endpoints Inventory](#5-api-endpoints-inventory)
6. [Product-Related Code (→ Product Service)](#6-product-related-code--product-service)
7. [Order-Related Code (→ Order Service)](#7-order-related-code--order-service)
8. [User/Auth-Related Code (→ User Service)](#8-userauthentication-related-code--user-service)
9. [Recommendations for Microservices Decomposition](#9-recommendations-for-microservices-decomposition)

---

## 1. Current Monolithic Architecture Overview

### Technology Stack
| Layer | Technology | Version |
|-------|-----------|---------|
| **Backend Framework** | Spring Boot | 3.2.5 |
| **Language** | Java | 17 |
| **Frontend Framework** | Angular | 21.x |
| **Database** | MySQL | (via `mysql-connector-j`) |
| **ORM** | Hibernate / Spring Data JPA | (via Spring Boot starter) |
| **Authentication** | JWT (jjwt 0.12.3) | Custom implementation |
| **Security** | Spring Security | (via Spring Boot starter) |
| **API Docs** | SpringDoc OpenAPI | 2.3.0 |
| **Build Tool** | Maven | (pom.xml) |
| **Utility** | Lombok | 1.18.30 |

### Architecture Pattern
The application follows a **layered monolithic architecture** with:
- **Controller Layer** → REST API endpoints
- **Service Layer** → Business logic
- **Repository Layer** → Data access (Spring Data JPA)
- **Entity Layer** → JPA entities mapped to MySQL tables
- **DTO Layer** → Request/Response objects for API communication
- **Mapper Layer** → Entity ↔ DTO conversion (manual `DtoMapper`)
- **Security Layer** → JWT-based authentication & authorization
- **Config Layer** → Security, CORS, OpenAPI configuration

### Package Structure
```
tn.esprit.projetintegre/
├── config/                    # Configuration classes
│   ├── DataInitializer.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controllers/               # REST Controllers (14 controllers)
│   ├── AlertController.java
│   ├── AuthController.java
│   ├── CartController.java
│   ├── CategoryController.java
│   ├── InventoryController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   ├── RentalController.java
│   ├── ShippingAddressController.java
│   ├── TransactionController.java
│   ├── UserController.java
│   ├── WalletController.java
│   ├── WarehouseController.java
│   └── WishlistController.java
├── dto/                       # Data Transfer Objects
│   ├── request/               # 13 request DTOs
│   └── response/              # 14 response DTOs
├── entities/                  # JPA Entities (80+ entities)
├── enums/                     # Enum types (40+ enums)
├── exception/                 # Custom exceptions & global handler
├── mapper/                    # DtoMapper (manual mapping)
├── repositories/              # Spring Data JPA repositories (20+)
├── security/                  # JWT & Security components
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
└── services/                  # Business logic services (15 services)
```

### Key Configuration
- **Server Port**: `8089`
- **Database**: `mysql://localhost:3306/ecommerce_db`
- **DDL Strategy**: `update` (auto schema evolution)
- **JWT Secret**: Hardcoded string (256-bit minimum for HS256)
- **JWT Expiration**: 86,400,000ms (24 hours)
- **CORS Origins**: `localhost:4200` (Angular), `localhost:3000`

---

## 2. Backend Analysis

### 2.1 Entities/Models

#### Core E-Commerce Entities (relevant to microservices decomposition)

| Entity | Table | Key Fields | Relationships |
|--------|-------|-----------|---------------|
| **User** | `users` | id, name, username, email, password, role, isSeller, isBuyer, storeName, loyaltyPoints, level | OneToOne: Wallet, Cart; OneToMany: Orders, Reviews, Transactions, Subscriptions |
| **Product** | `products` | id, name, description, price, sku, stockQuantity, rating, isActive, isFeatured, isRentable | ManyToOne: Category, User(seller); OneToMany: Reviews, CartItems; ElementCollections: images, tags |
| **Order** | `orders` | id, orderNumber, subtotal, totalAmount, status, paymentStatus, shipping fields | ManyToOne: User; OneToMany: OrderItems |
| **OrderItem** | `order_items` | id, productName, quantity, unitPrice, totalPrice | ManyToOne: Order, Product |
| **OrderStatusHistory** | `order_status_history` | id, previousStatus, status, comment, changedAt | ManyToOne: Order, User(changedBy) |
| **Cart** | `carts` | id, totalAmount, discountAmount, appliedCouponCode | OneToOne: User; OneToMany: CartItems |
| **CartItem** | `cart_items` | id, quantity, price, selectedVariant/Color/Size | ManyToOne: Cart, Product |
| **Category** | `categories` | id, name, slug, isActive, displayOrder | Self-referential (parent/subcategories); OneToMany: Products |
| **Subcategory** | `subcategories` | id, name, slug, productCount | ManyToOne: Category; ManyToMany: Products |
| **Wishlist** | `wishlists` | id, name, isPublic | ManyToOne: User; ManyToMany: Products |
| **WishlistItem** | `wishlist_items` | id, priority, priceWhenAdded | ManyToOne: Wishlist, Product, ProductVariant |
| **Wallet** | `wallets` | id, balance, totalDeposited, currency | OneToOne: User; OneToMany: Transactions |
| **Transaction** | `transactions` | id, transactionNumber, amount, type, status | ManyToOne: User, Wallet |
| **Inventory** | `inventory` | id, sku, quantity, reservedQuantity, availableQuantity | ManyToOne: Product, ProductVariant, Warehouse |
| **Warehouse** | `warehouses` | id, code, name, address, capacity | OneToMany: Inventory; ManyToOne: User(manager) |
| **StockMovement** | `stock_movements` | id, reference, movementType, quantity | ManyToOne: Product, Warehouse(s), User |
| **StockAlert** | `stock_alerts` | id, alertType, currentStock, threshold | ManyToOne: Product, Warehouse, User |
| **ProductReview** | `product_reviews` | id, rating, comment, isVerifiedPurchase | ManyToOne: Product, User |
| **ProductSpec** | `product_specs` | id, specName, specValue, unit | ManyToOne: Product |
| **ProductVariant** | `product_variants` | id, sku, name, color, size, price, stock | ManyToOne: Product |
| **ShippingAddress** | `shipping_addresses` | id, recipientName, addressLine1, city, country, isDefault | ManyToOne: User |
| **Rental** | `rentals` | id, rentalNumber, startDate, endDate, status, totalAmount | OneToMany: RentalProducts; ManyToOne: User, Site |
| **RentalProduct** | `rental_products` | id, productName, quantity, dailyRate, totalPrice | ManyToOne: Rental, Product |
| **Promotion** | `promotions` | Various promotion fields | Product/Order related |
| **Notification** | `notifications` | id, title, message | ManyToOne: User |
| **SellerSettings** | `seller_settings` | Seller-specific configuration | ManyToOne: User |
| **Alert** | `alerts` | id, title, alertType, severity, status | ManyToOne: Site, User |

#### Non-E-Commerce Entities (ConnectCamp platform specific)
The monolith also contains **many entities unrelated to e-commerce** that are part of the larger ConnectCamp platform:
- Campsite, Site, Event, Ticket, Reservation
- ChatRoom, ChatMessage, Message, ForumArticle, ForumComment
- EmergencyAlert, EvacuationExercise, ProtocoleUrgence
- Certification, Achievement, Mission, Badge
- Sponsor, Sponsorship, Subscription, Abonnement
- And many more (~40+ additional entities)

### 2.2 Repositories

| Repository | Entity | Key Custom Methods |
|-----------|--------|-------------------|
| **ProductRepository** | Product | `findByIsActiveTrue`, `searchProducts`, `findByPriceRange`, `findTopSellingProducts`, `findByCategoryId`, `findBySellerId` |
| **OrderRepository** | Order | `findByOrderNumber`, `findByUserId`, `findByStatus`, `findOrdersBetweenDates`, `getTotalRevenue`, `countByStatus` |
| **UserRepository** | User | `findByUsername`, `findByEmail`, `existsByUsername`, `existsByEmail`, `searchUsers`, `findVerifiedSellers` |
| **CartRepository** | Cart | `findByUserId` (with EntityGraph for items/products) |
| **CartItemRepository** | CartItem | `findByCartIdAndProductId`, `deleteByCartId` |
| **CategoryRepository** | Category | `findBySlug`, `findByIsActiveTrue`, `findByParentIsNull`, `findByParentId` |
| **WishlistRepository** | Wishlist | Basic CRUD + user-based queries |
| **WalletRepository** | Wallet | User-based wallet queries |
| **TransactionRepository** | Transaction | Type/wallet/user-based queries |
| **InventoryRepository** | Inventory | Product/warehouse-based, low stock queries |
| **WarehouseRepository** | Warehouse | Code-based, active warehouse queries |
| **OrderItemRepository** | OrderItem | Order item queries |
| **ShippingAddressRepository** | ShippingAddress | User-based address queries |
| **RentalRepository** | Rental | User/status-based rental queries |
| **ProductReviewRepository** | ProductReview | Product/user-based review queries |

All repositories use **EntityGraph** annotations for eager loading of commonly needed relationships.

### 2.3 Services & Business Logic

| Service | Responsibilities |
|---------|-----------------|
| **AuthService** | User registration (creates User + Cart + Wallet), login, JWT token generation |
| **UserService** | CRUD operations, role management, seller onboarding, suspension, loyalty points/tier management |
| **ProductService** | CRUD, search, filtering, stock updates, view count tracking, soft delete |
| **OrderService** | Order creation from cart, stock deduction, shipping cost calculation (free over 100), tax calculation (19%), status management, payment status, stock restoration on cancel, loyalty points award |
| **CartService** | Cart management, add/remove/update items, clear cart, total calculation |
| **CategoryService** | Category hierarchy management, CRUD, slug-based lookup |
| **WishlistService** | Wishlist CRUD, product add/remove from wishlist |
| **WalletService** | Balance management, fund operations, wallet activation/deactivation |
| **TransactionService** | Transaction queries by type, user, wallet |
| **InventoryService** | Stock management, adjustments, reservations, low stock detection |
| **WarehouseService** | Warehouse CRUD, code-based lookup |
| **ShippingAddressService** | Address CRUD, default address management |
| **AlertService** | Alert CRUD, status-based filtering, resolution |
| **RentalService** | Rental lifecycle management, status updates |

#### Key Business Rules in OrderService
1. **Order creation** converts cart items into order items
2. **Stock validation** checks sufficient stock before order
3. **Stock deduction** happens immediately on order creation
4. **Shipping cost**: Free for orders ≥ 100, otherwise 7 (TND)
5. **Tax**: 19% of subtotal
6. **Loyalty**: 1 point per 10 TND spent
7. **Cancellation** restores product stock
8. **Payment completion** sets order status to CONFIRMED

### 2.4 Security & Authentication

#### JWT Implementation
- **JwtTokenProvider**: Generates/validates JWT tokens using HMAC-SHA with configurable secret
- **JwtAuthenticationFilter**: Extracts JWT from `Authorization: Bearer <token>` header
- **CustomUserDetailsService**: Loads user by username or email, maps Role to Spring Security GrantedAuthority (`ROLE_` prefix)

#### Security Configuration
- **Stateless** session management (no server-side sessions)
- **BCrypt** password encoding
- **Public endpoints**: `/auth/**`, `/api/auth/**`, Swagger UI, GET `/api/products/**`, GET `/api/categories/**`, GET `/api/warehouses/**`
- **Role-based access**: `@PreAuthorize` annotations on controllers
  - `ADMIN` only: User management, all orders, revenue
  - `ADMIN` or `SELLER`: Product CRUD, inventory, warehouse, order status updates
  - `USER`/`ADMIN`: Shipping addresses, rentals
  - Authenticated: Cart, orders, profile

#### CORS Configuration
- Allowed origins: `localhost:4200`, `localhost:3000`
- All common HTTP methods allowed
- Credentials allowed, 1-hour preflight cache

### 2.5 DTOs

#### Request DTOs
| DTO | Used For |
|-----|----------|
| `AuthRequest` | Login (username + password) |
| `RegisterRequest` | User registration |
| `ProductRequest` | Product create/update |
| `OrderRequest` | Order creation |
| `CategoryRequest` | Category CRUD |
| `CartItemRequest` | Cart item operations |
| `InventoryRequest` | Inventory management |
| `WarehouseRequest` | Warehouse CRUD |
| `ShippingAddressRequest` | Address management |
| `WishlistRequest` | Wishlist CRUD |
| `WalletRequest` | Wallet operations |
| `TransactionRequest` | Transaction creation |
| `RentalRequest` / `RentalProductRequest` | Rental management |
| `AlertRequest` | Alert creation |
| `PromotionRequest` | Promotion management |
| `BecomeSellerRequest` | Seller onboarding |
| `UserUpdateRequest` | Profile updates |

#### Response DTOs
| DTO | Key Fields |
|-----|-----------|
| `ProductResponse` | Full product details with category/seller names |
| `OrderResponse` | Order with amounts, status, shipping info |
| `OrderItemResponse` | Item details with product info |
| `CartResponse` | Cart with items, totals, final amount |
| `CartItemResponse` | Item with product thumbnail, subtotal, stock |
| `CategoryResponse` | Category with parent info, product count |
| `UserResponse` | User profile data |
| `WalletResponse` | Balance, currency, status |
| `TransactionResponse` | Transaction details |
| `InventoryResponse` | Stock levels, warehouse info |
| `WarehouseResponse` | Warehouse details |
| `ShippingAddressResponse` | Full address data |
| `WishlistResponse` | Wishlist with product count |
| `AlertResponse` | Alert with reporter/resolver info |
| `RentalResponse` / `RentalProductResponse` | Rental details |

### 2.6 Exception Handling
- **GlobalExceptionHandler** with `@ControllerAdvice`
- Custom exceptions: `ResourceNotFoundException`, `DuplicateResourceException`, `InsufficientStockException`, `BusinessException`, `ForbiddenException`, `UnauthorizedException`, `ValidationException`

---

## 3. Frontend Analysis

### 3.1 Project Structure
```
src/app/
├── admin/                     # Admin dashboard components
│   ├── admin-dashboard/
│   ├── admin-panel/           # Main admin entry point
│   ├── admin-sidebar/
│   ├── marketplace-management/
│   ├── users-management/
│   └── ... (events, campsites, etc.)
├── components/
│   ├── auth/                  # Login/Register (unified)
│   ├── cart/                  # Shopping cart component
│   ├── client/                # Client dashboard (orders, cart, profile)
│   ├── marketplace/           # Product listing
│   │   └── product-detail/    # Product detail page
│   ├── seller/                # Seller dashboard
│   ├── home/                  # Homepage
│   ├── navigation/            # Top nav bar
│   ├── profile/               # User profile
│   └── ... (community, events, map, etc.)
├── guards/                    # Route guards
│   └── auth.guard.ts          # AuthGuard, AdminGuard, SellerGuard, ClientGuard, OrganizerGuard
├── interceptors/
│   ├── auth.interceptor.ts    # JWT token injection
│   └── error.interceptor.ts   # Global error handling
├── models/
│   ├── api.models.ts          # Core interfaces (User, Product, Order, Cart, etc.)
│   ├── ecommerce.model.ts     # E-commerce specific models
│   └── ... (camping models)
├── services/
│   ├── auth.service.ts        # Authentication (login, register, JWT management)
│   ├── product.service.ts     # Product API calls
│   ├── order.service.ts       # Order API calls
│   ├── cart.service.ts        # Cart management (local + backend sync)
│   ├── category.service.ts    # Category API calls
│   ├── inventory.service.ts   # Inventory management
│   ├── warehouse.service.ts   # Warehouse management
│   ├── rental.service.ts      # Rental management
│   ├── wallet.service.ts      # Wallet operations
│   ├── user.service.ts        # User data (currently mock data)
│   └── notification.service.ts
└── environments/
    ├── environment.ts
    └── environment.prod.ts    # apiUrl: http://localhost:8089/api
```

### 3.2 Key Frontend Services

#### AuthService
- Login sends `{ username: credentials.email, password }` to `/auth/login`
- Register sends full registration payload to `/auth/register`
- JWT stored in `localStorage` as `auth_token`
- User data stored as `current_user`
- Role mapping: Backend `USER` → Frontend `CLIENT`
- Token expiration check via JWT payload decode
- SSR-safe localStorage access

#### ProductService
- Full CRUD operations against `/api/products`
- Pagination, search, category filtering, price range filtering
- Featured and top-selling product queries
- Seller-specific product listing

#### OrderService
- Creates orders via `POST /api/orders` with query params (not JSON body)
- User-specific order listing
- Status updates (admin)
- Order cancellation

#### CartService
- **Dual mode**: Local (localStorage) + Backend sync
- Maintains `BehaviorSubject<CartItem[]>` for reactive updates
- Silent backend failures (local cart preserved)
- Post-login cart synchronization (merges local + server carts)

### 3.3 Routing & Guards
- `/marketplace` → Product listing
- `/marketplace/:id` → Product detail
- `/cart` → Cart view (requires auth)
- `/dashboard` → Client dashboard (requires auth)
- `/seller` → Seller dashboard (requires auth)
- `/admin` → Admin panel (requires ADMIN role)
- `/auth`, `/auth/login`, `/auth/register` → Authentication
- `/profile` → User profile

### 3.4 Environment Configuration
```typescript
// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8089/api'  // Note: /api already included
};
```
**Important**: Some services prepend `/api` again, resulting in double `/api/api` paths. The `AuthService` uses `${environment.apiUrl}/auth` (→ `/api/auth`), while other services use `${environment.apiUrl}/api/products` (→ `/api/api/products`). The backend supports both `/auth/**` and `/api/auth/**` paths to handle this inconsistency.

---

## 4. Database Schema & Relationships

### Core E-Commerce Tables

```
┌─────────────┐     ┌──────────────┐     ┌──────────────┐
│   users      │────→│   wallets    │     │  categories  │
│              │────→│   carts      │     │   (self-ref) │
│              │────→│   orders     │     └──────┬───────┘
│              │────→│  wishlists   │            │
│              │────→│  reviews     │     ┌──────┴───────┐
│              │────→│ transactions │     │  products     │
│              │────→│ ship_addrs   │←────│  (seller_id) │
└──────────────┘     └──────────────┘     │  (cat_id)    │
                                          └──────┬───────┘
                                                 │
                     ┌──────────────┐     ┌──────┴───────┐
                     │  order_items │←────│  cart_items   │
                     │  (order_id)  │     │  (cart_id)    │
                     │  (product_id)│     │  (product_id) │
                     └──────┬───────┘     └──────────────┘
                            │
                     ┌──────┴───────┐
                     │   orders     │
                     │  (user_id)   │
                     └──────────────┘

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  warehouses  │────→│  inventory   │←────│  products    │
│              │     │  (product_id)│     └──────────────┘
│              │     │  (wh_id)     │
└──────────────┘     └──────────────┘

┌──────────────┐     ┌──────────────┐
│  rentals     │────→│rental_products│
│  (user_id)   │     │  (rental_id) │
└──────────────┘     │  (product_id)│
                     └──────────────┘
```

### Table Summary

| Table | Primary Relationships | Index Columns |
|-------|----------------------|---------------|
| `users` | PK: id | username (unique), email (unique) |
| `products` | FK: category_id, seller_id | category_id, seller_id, isActive, price, sku |
| `product_images` | FK: product_id | - |
| `product_tags` | FK: product_id | - |
| `product_reviews` | FK: product_id, user_id | - |
| `product_specs` | FK: product_id | product_id, specName |
| `product_variants` | FK: product_id | product_id, sku, isActive |
| `categories` | FK: parent_id (self-ref) | name (unique) |
| `subcategories` | FK: category_id | category_id, slug, isActive |
| `orders` | FK: user_id | orderNumber (unique) |
| `order_items` | FK: order_id, product_id | - |
| `order_status_history` | FK: order_id, changed_by | order_id, status, changedAt |
| `carts` | FK: user_id (unique) | - |
| `cart_items` | FK: cart_id, product_id | - |
| `wallets` | FK: user_id (unique) | - |
| `transactions` | FK: user_id, wallet_id | transactionNumber (unique) |
| `wishlists` | FK: user_id | - |
| `wishlist_items` | FK: wishlist_id, product_id, variant_id | wishlist_id, product_id |
| `wishlist_products` | Join: wishlist_id, product_id | - |
| `inventory` | FK: product_id, variant_id, warehouse_id | product_id, warehouse_id, sku |
| `warehouses` | FK: manager_id | code (unique), isActive |
| `stock_movements` | FK: product_id, warehouse_id, etc. | product_id, warehouse_id, type, date |
| `stock_alerts` | FK: product_id, warehouse_id | product_id, alertType, isResolved |
| `shipping_addresses` | FK: user_id | user_id, isDefault |
| `rentals` | FK: user_id, site_id | rentalNumber, user_id, status, dates |
| `rental_products` | FK: rental_id, product_id | product_id, status, rental_id |

---

## 5. API Endpoints Inventory

### Authentication (`/auth` & `/api/auth`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` or `/api/auth/register` | Public | Register new user |
| POST | `/auth/login` or `/api/auth/login` | Public | Login |
| GET | `/auth/health` or `/api/auth/health` | Public | Health check |

### Products (`/api/products`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/products` | Public | List active products (paginated) |
| GET | `/api/products/{id}` | Public | Get product by ID (increments views) |
| GET | `/api/products/category/{categoryId}` | Public | Products by category |
| GET | `/api/products/seller/{sellerId}` | Public | Products by seller |
| GET | `/api/products/search?keyword=` | Public | Search products |
| GET | `/api/products/price-range?min=&max=` | Public | Filter by price |
| GET | `/api/products/featured` | Public | Featured products |
| GET | `/api/products/top-selling?limit=` | Public | Top selling products |
| POST | `/api/products` | ADMIN/SELLER | Create product |
| PUT | `/api/products/{id}` | ADMIN/SELLER | Update product |
| PATCH | `/api/products/{id}/stock?quantity=` | ADMIN/SELLER | Update stock |
| DELETE | `/api/products/{id}` | ADMIN/SELLER | Soft delete product |

### Orders (`/api/orders`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/orders` | ADMIN/SELLER | All orders |
| GET | `/api/orders/{id}` | Authenticated | Order by ID |
| GET | `/api/orders/number/{orderNumber}` | Authenticated | Order by number |
| GET | `/api/orders/user/{userId}` | Authenticated | User's orders (paginated) |
| GET | `/api/orders/status/{status}` | ADMIN/SELLER | Orders by status |
| POST | `/api/orders?userId=&shipping...&paymentMethod=` | Authenticated | Create order from cart |
| PATCH | `/api/orders/{id}/status?status=` | ADMIN/SELLER | Update order status |
| PATCH | `/api/orders/{id}/payment?status=&transactionId=` | Authenticated | Update payment status |
| GET | `/api/orders/revenue` | ADMIN/SELLER | Total revenue |

### Cart (`/api/cart`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/cart/{userId}` | Authenticated | Get user's cart |
| POST | `/api/cart/{userId}/items?productId=&quantity=` | Authenticated | Add item to cart |
| PUT | `/api/cart/{userId}/items/{itemId}?quantity=` | Authenticated | Update item quantity |
| DELETE | `/api/cart/{userId}/items/{itemId}` | Authenticated | Remove item |
| DELETE | `/api/cart/{userId}/clear` | Authenticated | Clear cart |

### Categories (`/api/categories`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/categories` | Public | All categories |
| GET | `/api/categories/active` | Public | Active categories |
| GET | `/api/categories/root` | Public | Root categories |
| GET | `/api/categories/{id}` | Public | Category by ID |
| GET | `/api/categories/slug/{slug}` | Public | Category by slug |
| GET | `/api/categories/{parentId}/subcategories` | Public | Subcategories |
| POST | `/api/categories` | ADMIN/SELLER | Create category |
| PUT | `/api/categories/{id}` | ADMIN/SELLER | Update category |
| DELETE | `/api/categories/{id}` | ADMIN/SELLER | Delete category |

### Users (`/api/users`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/users` | ADMIN | All users |
| GET | `/api/users/active` | Authenticated | Active users (paginated) |
| GET | `/api/users/{id}` | Authenticated | User by ID |
| GET | `/api/users/search?keyword=` | Authenticated | Search users |
| PUT | `/api/users/{id}` | Authenticated | Update profile |
| PUT | `/api/users/{id}/role?role=` | ADMIN | Update role |
| POST | `/api/users/{id}/become-seller` | Authenticated | Become seller |
| POST | `/api/users/{id}/suspend?reason=` | ADMIN | Suspend user |
| POST | `/api/users/{id}/unsuspend` | ADMIN | Unsuspend user |
| DELETE | `/api/users/{id}` | ADMIN | Soft delete user |

### Wallets (`/api/wallets`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/wallets` | Authenticated | All wallets |
| GET | `/api/wallets/{id}` | Authenticated | Wallet by ID |
| GET | `/api/wallets/user/{userId}` | Authenticated | User's wallet |
| GET | `/api/wallets/user/{userId}/balance` | Authenticated | Wallet balance |
| POST | `/api/wallets/user/{userId}/add-funds?amount=` | Authenticated | Add funds |
| POST | `/api/wallets/user/{userId}/deduct-funds?amount=` | Authenticated | Deduct funds |
| PUT | `/api/wallets/user/{userId}/deactivate` | Authenticated | Deactivate wallet |
| PUT | `/api/wallets/user/{userId}/activate` | Authenticated | Activate wallet |

### Transactions (`/api/transactions`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/transactions` | Authenticated | All transactions (paginated) |
| GET | `/api/transactions/{id}` | Authenticated | Transaction by ID |
| GET | `/api/transactions/number/{number}` | Authenticated | Transaction by number |
| GET | `/api/transactions/user/{userId}` | Authenticated | User's transactions |
| GET | `/api/transactions/wallet/{walletId}` | Authenticated | Wallet's transactions |
| GET | `/api/transactions/type/{type}` | Authenticated | Transactions by type |

### Inventory (`/api/inventory`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/inventory` | Authenticated | All inventory (paginated) |
| GET | `/api/inventory/all` | Authenticated | All inventory (no pagination) |
| GET | `/api/inventory/{id}` | Authenticated | Inventory by ID |
| GET | `/api/inventory/product/{productId}` | Authenticated | Inventory by product |
| GET | `/api/inventory/warehouse/{warehouseId}` | Authenticated | Inventory by warehouse |
| GET | `/api/inventory/low-stock` | Authenticated | Low stock items |
| POST | `/api/inventory` | ADMIN/SELLER | Create inventory |
| PUT | `/api/inventory/{id}` | ADMIN/SELLER | Update inventory |
| POST | `/api/inventory/{id}/adjust` | ADMIN/SELLER | Adjust stock |
| POST | `/api/inventory/{id}/reserve` | ADMIN/SELLER | Reserve stock |
| POST | `/api/inventory/{id}/release` | ADMIN/SELLER | Release reservation |
| DELETE | `/api/inventory/{id}` | ADMIN/SELLER | Delete inventory |

### Warehouses (`/api/warehouses`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/warehouses` | SELLER/ADMIN | All warehouses |
| GET | `/api/warehouses/paginated` | SELLER/ADMIN | Paginated warehouses |
| GET | `/api/warehouses/active` | SELLER/ADMIN | Active warehouses |
| GET | `/api/warehouses/{id}` | SELLER/ADMIN | Warehouse by ID |
| GET | `/api/warehouses/code/{code}` | SELLER/ADMIN | Warehouse by code |
| POST | `/api/warehouses` | ADMIN/SELLER | Create warehouse |
| PUT | `/api/warehouses/{id}` | ADMIN/SELLER | Update warehouse |
| DELETE | `/api/warehouses/{id}` | ADMIN/SELLER | Delete warehouse |

### Shipping Addresses (`/api/shipping-addresses`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/shipping-addresses/user/{userId}` | USER/ADMIN | User's addresses |
| GET | `/api/shipping-addresses/{id}` | USER/ADMIN | Address by ID |
| GET | `/api/shipping-addresses/user/{userId}/default` | USER/ADMIN | Default address |
| POST | `/api/shipping-addresses` | USER/ADMIN | Create address |
| PUT | `/api/shipping-addresses/{id}` | USER/ADMIN | Update address |
| DELETE | `/api/shipping-addresses/{id}` | USER/ADMIN | Delete address |

### Wishlists (`/api/wishlists`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/wishlists` | Authenticated | All wishlists |
| GET | `/api/wishlists/{id}` | Authenticated | Wishlist by ID |
| GET | `/api/wishlists/user/{userId}` | Authenticated | User's wishlists |
| POST | `/api/wishlists?userId=` | Authenticated | Create wishlist |
| PUT | `/api/wishlists/{id}` | Authenticated | Update wishlist |
| POST | `/api/wishlists/{id}/products/{productId}` | Authenticated | Add product |
| DELETE | `/api/wishlists/{id}/products/{productId}` | Authenticated | Remove product |
| DELETE | `/api/wishlists/{id}` | Authenticated | Delete wishlist |

### Rentals (`/api/rentals`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/rentals` | ADMIN/SELLER | All rentals |
| GET | `/api/rentals/{id}` | Authenticated | Rental by ID |
| GET | `/api/rentals/number/{number}` | Authenticated | Rental by number |
| GET | `/api/rentals/user/{userId}` | USER/ADMIN | User's rentals (paginated) |
| POST | `/api/rentals` | USER/ADMIN | Create rental |
| PATCH | `/api/rentals/{id}/status?status=` | SELLER/ADMIN | Update rental status |
| DELETE | `/api/rentals/{id}` | ADMIN/SELLER | Delete rental |

### Alerts (`/api/alerts`)
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/alerts` | Authenticated | All alerts |
| GET | `/api/alerts/paged` | Authenticated | Paginated alerts |
| GET | `/api/alerts/{id}` | Authenticated | Alert by ID |
| GET | `/api/alerts/status/{status}` | Authenticated | Alerts by status |
| POST | `/api/alerts` | Authenticated | Create alert |
| PUT | `/api/alerts/{id}` | Authenticated | Update alert |
| PUT | `/api/alerts/{id}/resolve` | Authenticated | Resolve alert |
| DELETE | `/api/alerts/{id}` | Authenticated | Delete alert |

---

## 6. Product-Related Code (→ Product Service)

### Entities to Extract
- `Product` (core entity with all product fields)
- `Category` (product categorization with hierarchy)
- `Subcategory` (sub-categorization)
- `ProductReview` (product ratings/reviews)
- `ProductSpec` (product specifications)
- `ProductVariant` (product variants: color, size, etc.)
- `Promotion` (product promotions)

### Repositories
- `ProductRepository` — Full search, filter, pagination
- `CategoryRepository` — Category hierarchy
- `SubcategoryRepository` — Subcategory management
- `ProductReviewRepository` — Review queries
- `ProductSpecRepository` — Spec queries
- `ProductVariantRepository` — Variant queries

### Services
- `ProductService` — Core product CRUD and business logic
- `CategoryService` — Category management

### Controllers
- `ProductController` (`/api/products`)
- `CategoryController` (`/api/categories`)

### DTOs
- Request: `ProductRequest`, `CategoryRequest`
- Response: `ProductResponse`, `CategoryResponse`

### Business Rules
- Products are soft-deleted (isActive = false)
- SKU is unique
- Price validation (0.01 to 9,999,999.99)
- View count tracking on product access
- Featured/On-sale/Rentable flags
- Stock quantity tracked at product level AND in inventory table

### Cross-Service Dependencies
- **Needs User info**: Product has `seller_id` (FK to users) — will need UserService API call or denormalized seller data
- **Needed by Order Service**: Order items reference products for stock deduction
- **Needed by Cart Service**: Cart items reference products for pricing
- **Needed by Inventory Service**: Inventory tracks product stock in warehouses

---

## 7. Order-Related Code (→ Order Service)

### Entities to Extract
- `Order` (core order entity)
- `OrderItem` (line items with product snapshot)
- `OrderStatusHistory` (order lifecycle tracking)
- `Cart` (shopping cart — could be own service or part of Order)
- `CartItem` (cart line items)
- `ShippingAddress` (delivery addresses)

### Repositories
- `OrderRepository` — Order queries with date ranges, revenue calculation
- `OrderItemRepository` — Order item queries
- `OrderStatusHistoryRepository` — Status history
- `CartRepository` — Cart access with eager loading
- `CartItemRepository` — Cart item CRUD
- `ShippingAddressRepository` — Address management

### Services
- `OrderService` — Order creation, status management, revenue
- `CartService` — Cart management (add/remove/clear)
- `ShippingAddressService` — Address CRUD

### Controllers
- `OrderController` (`/api/orders`)
- `CartController` (`/api/cart`)
- `ShippingAddressController` (`/api/shipping-addresses`)

### DTOs
- Request: `OrderRequest`, `CartItemRequest`, `ShippingAddressRequest`
- Response: `OrderResponse`, `OrderItemResponse`, `CartResponse`, `CartItemResponse`, `ShippingAddressResponse`

### Business Rules
- Orders created from cart (cart → order conversion)
- Order number format: `ORD-{timestamp}`
- **Shipping**: Free for subtotal ≥ 100, else 7 TND
- **Tax**: 19% of subtotal
- **Stock**: Validated and deducted on order creation; restored on cancellation
- **Payment**: Status update triggers order confirmation
- **Loyalty**: 1 point per 10 TND (awarded on order creation)
- **Status Flow**: PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED (or CANCELLED/REFUNDED/RETURNED)

### Cross-Service Dependencies
- **Needs Product info**: Stock checking, price validation, stock deduction
- **Needs User info**: Order belongs to a user; loyalty points update
- **Needs Cart data**: Order creation reads from cart then clears it
- **Wallet/Transaction**: Payment could involve wallet deduction

---

## 8. User/Authentication-Related Code (→ User Service)

### Entities to Extract
- `User` (core user entity with all profile/seller/loyalty fields)
- `Wallet` (user's wallet)
- `Transaction` (wallet transactions)
- `Wishlist` & `WishlistItem` (user wishlists)
- `SellerSettings` (seller configuration)
- `Notification` (user notifications)

### Repositories
- `UserRepository` — User queries, search, role-based
- `WalletRepository` — Wallet operations
- `TransactionRepository` — Transaction queries
- `WishlistRepository` / `WishlistItemRepository` — Wishlist queries
- `SellerSettingsRepository` — Seller config
- `NotificationRepository` — Notification queries

### Services
- `AuthService` — Registration (User + Cart + Wallet creation), login, JWT
- `UserService` — Profile CRUD, role management, seller onboarding, loyalty
- `WalletService` — Balance management
- `TransactionService` — Transaction queries

### Controllers
- `AuthController` (`/auth` & `/api/auth`)
- `UserController` (`/api/users`)
- `WalletController` (`/api/wallets`)
- `TransactionController` (`/api/transactions`)
- `WishlistController` (`/api/wishlists`)

### Security Components
- `JwtTokenProvider` — Token generation/validation
- `JwtAuthenticationFilter` — Request filtering
- `CustomUserDetailsService` — User loading for Spring Security
- `SecurityConfig` — Full security configuration

### DTOs
- Request: `AuthRequest`, `RegisterRequest`, `UserUpdateRequest`, `BecomeSellerRequest`, `WalletRequest`, `TransactionRequest`, `WishlistRequest`
- Response: `AuthResponse`, `UserResponse`/`UserDTO`, `WalletResponse`, `TransactionResponse`, `WishlistResponse`

### Business Rules
- Unique username and email enforcement
- BCrypt password hashing
- Cart + Wallet auto-created on registration
- Role hierarchy: USER, SELLER, ADMIN (+ BUYER, ORGANIZER, etc.)
- Seller onboarding with store info
- Loyalty tier system: BRONZE (0) → SILVER (1000) → GOLD (5000) → PLATINUM (10000)
- Gamification: XP, levels, missions
- Soft delete (isActive flag)
- Suspension system with reason tracking

---

## 9. Recommendations for Microservices Decomposition

### Proposed Microservices Architecture

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │  (Spring Cloud  │
                    │    Gateway)     │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
     ┌──────┴──────┐  ┌─────┴──────┐  ┌──────┴──────┐
     │   User      │  │  Product   │  │   Order     │
     │  Service    │  │  Service   │  │  Service    │
     │             │  │            │  │             │
     │ - Auth/JWT  │  │ - Products │  │ - Orders    │
     │ - Users     │  │ - Category │  │ - Cart      │
     │ - Wallets   │  │ - Reviews  │  │ - Shipping  │
     │ - Profiles  │  │ - Variants │  │ - Payments  │
     │ - Wishlists │  │ - Specs    │  │ - Inventory │
     └──────┬──────┘  └─────┬──────┘  │ - Warehouse │
            │                │         │ - Rentals   │
            │                │         └──────┬──────┘
     ┌──────┴──────┐  ┌─────┴──────┐  ┌──────┴──────┐
     │  User DB    │  │ Product DB │  │  Order DB   │
     │  (MySQL)    │  │  (MySQL)   │  │  (MySQL)    │
     └─────────────┘  └────────────┘  └─────────────┘
```

### Service Boundaries

#### 1. User Service (Port: 8081)
**Responsibility**: Authentication, user management, wallets, wishlists
- **Entities**: User, Wallet, Transaction, Wishlist, WishlistItem, SellerSettings, Notification
- **Endpoints**: `/api/auth/**`, `/api/users/**`, `/api/wallets/**`, `/api/transactions/**`, `/api/wishlists/**`
- **Database**: `user_service_db`
- **Key concern**: JWT issuing — all other services validate tokens but only User Service issues them

#### 2. Product Service (Port: 8082)
**Responsibility**: Product catalog, categories, reviews, specifications
- **Entities**: Product, Category, Subcategory, ProductReview, ProductSpec, ProductVariant, Promotion
- **Endpoints**: `/api/products/**`, `/api/categories/**`
- **Database**: `product_service_db`
- **Key concern**: Denormalize `sellerName` and `sellerId` into Product (avoid cross-service joins)

#### 3. Order Service (Port: 8083)
**Responsibility**: Order processing, cart, shipping, inventory, warehouse, rentals
- **Entities**: Order, OrderItem, OrderStatusHistory, Cart, CartItem, ShippingAddress, Inventory, Warehouse, StockMovement, StockAlert, Rental, RentalProduct
- **Endpoints**: `/api/orders/**`, `/api/cart/**`, `/api/shipping-addresses/**`, `/api/inventory/**`, `/api/warehouses/**`, `/api/rentals/**`
- **Database**: `order_service_db`
- **Key concern**: Saga pattern for order creation (stock check → reserve → create order → deduct stock); compensating transactions on failure

### Inter-Service Communication

| Source | Target | Data Needed | Pattern |
|--------|--------|-------------|---------|
| Order Service | Product Service | Stock check, price validation | Sync REST (via Feign/RestTemplate) |
| Order Service | User Service | User validation, loyalty points update | Sync REST |
| Order Service | Product Service | Stock deduction/restoration | Sync REST or Async Event |
| Product Service | User Service | Seller info for product display | Cache seller info locally |
| API Gateway | User Service | JWT validation | Token validation filter |

### Shared Concerns

1. **JWT Secret**: Must be shared across all services (via config server or environment)
2. **ApiResponse wrapper**: Standardize across all services
3. **Exception handling**: Common exception types in shared library
4. **Pagination**: Common `PageResponse` DTO

### Migration Strategy

1. **Phase 1**: Extract User Service first (authentication is cross-cutting)
2. **Phase 2**: Extract Product Service (least coupled to order flow)
3. **Phase 3**: Extract Order Service (most complex, depends on both)
4. **Phase 4**: Add API Gateway for routing and JWT validation
5. **Phase 5**: Add service discovery (Eureka) if scaling needed

### Data Migration Considerations

- Each service gets its own MySQL database
- Foreign key relationships between services become **eventual consistency** via IDs
- Product references in OrderItem should store **snapshot data** (name, price, SKU, thumbnail) — already implemented!
- User references stored as `userId` (Long) with optional denormalized name fields

### Frontend Impact

The Angular frontend currently calls `http://localhost:8089/api/*` for everything. After decomposition:
- **Option A**: API Gateway routes all requests (preferred) — frontend only changes the base URL
- **Option B**: Frontend directly calls each service — requires updating `environment.ts` with multiple URLs

The existing service structure in Angular (ProductService, OrderService, CartService, AuthService) already maps cleanly to the proposed microservices, minimizing frontend refactoring.

---

## Summary

The current monolith is well-structured with clear separation of concerns within the codebase. The Controller → Service → Repository pattern, DTOs, and entity relationships provide a solid foundation for microservices extraction. Key challenges will be:

1. **Data consistency** between Order and Product services (stock management)
2. **Authentication propagation** across services (JWT token forwarding)
3. **Transaction management** — currently `@Transactional` spans multiple repositories that will be in different services
4. **Cart-to-Order conversion** — currently within a single transaction, will need saga/choreography pattern

The OrderItem entity already stores product snapshots (name, SKU, thumbnail, price), which is excellent for microservices — it means order history won't break if product data changes.
