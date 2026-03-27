# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ConnectCamp Backend** — a Spring Boot 3.2.5 REST API for an integrated platform covering e-commerce, events, camping reservations, forums, gamification, and finance. Built as an Esprit school project.

- **Language**: Java 17
- **Package root**: `tn.esprit.projetintegre`
- **Main source**: `backConnect/`
- **Server port**: 8089
- **Database**: MySQL at `localhost:3306/projet_integre` (DDL auto-create enabled)

## Commands

All commands run from `backConnect/`:

```bash
# Build
mvn clean package

# Run
mvn spring-boot:run

# Test all
mvn test

# Run a single test class
mvn test -Dtest=EmergencyAlertServiceTest

# Run a single test method
mvn test -Dtest=EmergencyAlertServiceTest#methodName
```

Access after startup:
- API: `http://localhost:8089`
- Swagger UI: `http://localhost:8089/swagger-ui.html`
- OpenAPI docs: `http://localhost:8089/v3/api-docs`

## Architecture

Standard Spring Boot layered architecture:

```
controllers/   → REST endpoints (~30+ classes)
services/      → Business logic (~52 classes)
repositories/  → Spring Data JPA interfaces
entities/      → JPA entities (~42 classes)
dto/           → Request/Response DTOs
config/        → SecurityConfig, OpenApiConfig, CorsConfig, WebSocketConfig, DataInitializer
security/      → JWT filter, JwtService, CustomUserDetailsService
enums/         → 54+ enum types (Role, OrderStatus, EventStatus, etc.)
exception/     → GlobalExceptionHandler + custom exceptions
aspects/       → LoggingAspect, PerformanceAspect (AOP)
mapper/        → Entity-DTO conversion utilities
```

## Security

JWT-based stateless authentication with roles: `USER`, `ADMIN`, `SELLER`.

- Public routes: `/auth/**`, `/api/public/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- All other routes require a valid Bearer token
- JWT secret and expiration configured in `application.properties`
- CORS allowed origins: `localhost:4200` and `localhost:3000`

## Key Domain Modules

| Module | Controllers / Entities |
|---|---|
| Auth & Users | `AuthController`, `UserController`, `User` entity |
| E-commerce | Products, Categories, Cart, Orders, Reviews, Wishlists, Coupons, Promotions |
| Events & Tickets | `EventController`, `TicketController` |
| Camping | Reservations, Sites, Packages, Services |
| Finance | Wallets, Transactions, Subscriptions, Refunds |
| Gamification | Missions, Achievements |
| Forum | Posts, Comments |
| Notifications | `NotificationController`, WebSocket (STOMP) |

## Validation

Bean Validation (Jakarta) is used throughout DTOs with French error messages. The `GlobalExceptionHandler` converts `MethodArgumentNotValidException` and custom exceptions (`ResourceNotFoundException`, `BusinessException`, `ValidationException`) into structured error responses.

## Testing

Tests use JUnit 5 + Mockito (unit tests with mocked dependencies). Key test classes: `EmergencyAlertServiceTest`, `PackServiceTest`. See `documentation_tests.md` at the repo root for testing methodology.

## File Uploads

Max file size: 25 MB per file, 75 MB per request. Uploads stored in `uploads/` at the repo root.
