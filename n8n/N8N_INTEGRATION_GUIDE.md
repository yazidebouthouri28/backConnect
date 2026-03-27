# n8n Integration Guide

## Overview

n8n is used as the workflow automation engine for:
- **Order confirmation emails** — Triggered when `order.created` event fires
- **Delivery confirmation emails** — Triggered when `order.completed` event fires
- **Low stock alerts** — Triggered when `inventory.low-stock` event fires
- **Welcome emails** — Triggered when `user.registered` event fires

---

## Setup

### 1. Access n8n

After running `docker-compose up`, access n8n at:
```
http://localhost:5678
```

Credentials (from docker-compose.yml):
- **Username**: `admin`
- **Password**: `admin123`

### 2. Import Workflow

1. Open n8n UI → **Workflows** → **Import from File**
2. Select `n8n/order-confirmation-workflow.json`
3. Configure SMTP credentials (Settings → Credentials → Add SMTP)
4. Activate the workflow

### 3. Configure SMTP

In n8n, go to **Credentials** → **New** → **SMTP**:

| Field | Value |
|-------|-------|
| Host | `smtp.gmail.com` (or your SMTP provider) |
| Port | `587` |
| User | `your-email@gmail.com` |
| Password | `your-app-password` |
| SSL/TLS | `STARTTLS` |

---

## Integration with Order Service

### Option A: Direct Webhook (Recommended for simplicity)

In your Order Service, call the n8n webhook after creating an order:

```java
// In OrderService.java, after saving the order:
private final WebClient n8nWebClient = WebClient.create("http://n8n:5678");

public OrderResponse createOrder(OrderRequest request) {
    Order order = /* save order */;

    // Trigger n8n workflow via webhook
    n8nWebClient.post()
        .uri("/webhook/order-created")
        .bodyValue(OrderEvent.builder()
            .orderId(order.getId())
            .orderNumber(order.getOrderNumber())
            .userEmail(getUserEmail(order.getUserId()))
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus().name())
            .items(mapOrderItems(order.getOrderItems()))
            .build())
        .retrieve()
        .bodyToMono(Void.class)
        .subscribe();  // Fire and forget

    return mapper.toResponse(order);
}
```

### Option B: Kafka → n8n (via Kafka Trigger node)

1. In n8n, use the **Kafka Trigger** node instead of Webhook
2. Configure it to listen to `order.created` topic
3. Kafka connection: `kafka:29092` (Docker network)

### Option C: Kafka → Custom Bridge → n8n

Create a lightweight Kafka consumer that forwards events to n8n webhooks:

```java
@KafkaListener(topics = "order.created", groupId = "n8n-bridge-group")
public void forwardToN8n(OrderEvent event) {
    webClient.post()
        .uri("http://n8n:5678/webhook/order-created")
        .bodyValue(event)
        .retrieve()
        .bodyToMono(Void.class)
        .subscribe();
}
```

---

## Webhook URLs

| Workflow | URL | Method | Trigger |
|----------|-----|--------|--------|
| Order Confirmation | `POST http://n8n:5678/webhook/order-created` | POST | Order created |
| Delivery Confirmation | `POST http://n8n:5678/webhook/order-completed` | POST | Order delivered |
| Low Stock Alert | `POST http://n8n:5678/webhook/low-stock` | POST | Stock below threshold |

---

## Testing

```bash
# Test the order confirmation webhook
curl -X POST http://localhost:5678/webhook/order-created \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "orderNumber": "ORD-20260320-001",
    "userEmail": "test@example.com",
    "totalAmount": 99.99,
    "status": "PENDING",
    "items": [
      {"productName": "Wireless Headphones", "quantity": 2, "unitPrice": 49.99}
    ]
  }'
```
