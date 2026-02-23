package tn.esprit.projetPi.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@RequiredArgsConstructor
public class MongoCollectionInitializer {

    private final MongoTemplate mongoTemplate;

    @Bean
    public CommandLineRunner createCollections() {
        return args -> {

            // Liste complète des collections MongoDB (mise à jour avec tes entités récentes)
            String[] collections = {
                    "products",
                    "rentalProducts",
                    "categories",
                    "subcategories",
                    "inventories",
                    "warehouses",
                    "orders",
                    "orderItems",
                    "users",
                    "cartItems",
                    "rentals",
                    "sellerSettings",
                    "subscriptions",
                    "payment_methods",
                    "invoices",
                    "transactions",
                    "refunds",
                    "loyalty_programs",
                    "points",
                    "certifications",
                    "certification_items",
                    "complaints",
                    "events",
                    "event_comments",
                    "notifications",
                    "organizers",
                    "participants",
                    "reservations",
                    "reservation_history",
                    "services",
                    "sites",
                    "tickets",
                    "ticket_reservations",
                    "timeslots",
                    "virtual_tours",
                    "scenes_360",
                    "chat_rooms",
                    "chat_messages",
                    "forum_articles",
                    "forum_comments",
                    "sponsors",
                    "sponsorships"
            };

            for (String col : collections) {
                try {
                    if (!mongoTemplate.collectionExists(col)) {
                        mongoTemplate.createCollection(col);
                        System.out.println("🟢 Created collection: " + col);
                    } else {
                        System.out.println("⚪ Collection already exists: " + col);
                    }
                } catch (Exception e) {
                    System.err.println("🔴 Error creating collection '" + col + "': " + e.getMessage());
                }
            }

            System.out.println("✅ All collections initialization attempt finished.");
        };
    }
}
