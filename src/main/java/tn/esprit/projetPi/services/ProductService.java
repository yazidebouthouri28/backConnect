package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.ProductDTO;
import tn.esprit.projetPi.entities.Product;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getActiveProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getFeaturedProducts() {
        return productRepository.findByIsFeaturedTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toDTO(product);
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = toEntity(dto);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        if (product.getIsActive() == null) product.setIsActive(true);
        if (product.getInStock() == null) product.setInStock(true);
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    public ProductDTO updateProduct(String id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        updateEntityFromDTO(existing, dto);
        existing.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(existing);
        return toDTO(saved);
    }

    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getRentalProducts() {
        return productRepository.findByRentalAvailableTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        return productRepository.findLowStockProducts(threshold).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setShortDescription(product.getShortDescription());
        dto.setPrice(product.getPrice());
        dto.setRentalPrice(product.getRentalPrice());
        dto.setCompareAtPrice(product.getCompareAtPrice());
        dto.setSku(product.getSku());
        dto.setTags(product.getTags());
        dto.setImages(product.getImages());
        dto.setIsActive(product.getIsActive());
        dto.setIsFeatured(product.getIsFeatured());
        dto.setInStock(product.getInStock());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setLowStockThreshold(product.getLowStockThreshold());
        dto.setRentalAvailable(product.getRentalAvailable());
        dto.setLoyaltyPoints(product.getLoyaltyPoints());
        dto.setRating(product.getRating());
        dto.setReviews(product.getReviews());
        dto.setSeoTitle(product.getSeoTitle());
        dto.setSeoDescription(product.getSeoDescription());
        dto.setDiscount(product.getDiscount());
        dto.setSpecs(product.getSpecs());
        return dto;
    }

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        updateEntityFromDTO(product, dto);
        return product;
    }

    private void updateEntityFromDTO(Product product, ProductDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setShortDescription(dto.getShortDescription());
        product.setPrice(dto.getPrice());
        product.setRentalPrice(dto.getRentalPrice());
        product.setCompareAtPrice(dto.getCompareAtPrice());
        product.setSku(dto.getSku());
        product.setTags(dto.getTags());
        product.setImages(dto.getImages());
        product.setIsActive(dto.getIsActive());
        product.setIsFeatured(dto.getIsFeatured());
        product.setInStock(dto.getInStock());
        product.setStockQuantity(dto.getStockQuantity());
        product.setLowStockThreshold(dto.getLowStockThreshold());
        product.setRentalAvailable(dto.getRentalAvailable());
        product.setLoyaltyPoints(dto.getLoyaltyPoints());
        product.setRating(dto.getRating());
        product.setReviews(dto.getReviews());
        product.setSeoTitle(dto.getSeoTitle());
        product.setSeoDescription(dto.getSeoDescription());
        product.setDiscount(dto.getDiscount());
        product.setSpecs(dto.getSpecs());
    }
}
