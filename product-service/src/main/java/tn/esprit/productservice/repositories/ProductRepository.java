package tn.esprit.productservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.productservice.entities.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * FIX: Every query that returns Product(s) now uses LEFT JOIN FETCH p.category
 * so the category proxy is always initialized within the session.
 * Without this, Jackson hits a closed session when trying to serialize category.name.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // ── Already had JOIN FETCH - kept as-is ──────────────────────────────────

    @Query(value = "SELECT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH c.parent " +
            "WHERE p.isActive = true",
            countQuery = "SELECT count(p) FROM Product p WHERE p.isActive = true")
    Page<Product> findAllActiveWithCategory(Pageable pageable);

    // ── FIX: Added JOIN FETCH to all remaining queries ────────────────────────

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.isActive = true",
            countQuery = "SELECT count(p) FROM Product p WHERE p.isActive = true")
    Page<Product> findByIsActiveTrue(Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :categoryId",
            countQuery = "SELECT count(p) FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.sellerId = :sellerId AND p.isActive = true",
            countQuery = "SELECT count(p) FROM Product p WHERE p.sellerId = :sellerId AND p.isActive = true")
    Page<Product> findBySellerId(@Param("sellerId") UUID sellerId, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.isFeatured = true AND p.isActive = true")
    List<Product> findByIsFeaturedTrueAndIsActiveTrue();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.isOnSale = true AND p.isActive = true")
    List<Product> findByIsOnSaleTrueAndIsActiveTrue();

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))",
            countQuery = "SELECT count(p) FROM Product p " +
                    "WHERE p.isActive = true AND " +
                    "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = "SELECT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice",
            countQuery = "SELECT count(p) FROM Product p " +
                    "WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.isActive = true ORDER BY p.salesCount DESC")
    List<Product> findTopSellingProducts(Pageable pageable);

    // ── FIX: findById override with JOIN FETCH ────────────────────────────────
    // The default JpaRepository.findById() does NOT fetch category eagerly.
    // This override ensures getProductById() in the service also gets category loaded.
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category c " +
            "LEFT JOIN FETCH c.parent " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.tags " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithCategory(@Param("id") UUID id);

    Optional<Product> findBySku(String sku);

    boolean existsBySku(String sku);
}