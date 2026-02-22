package tn.esprit.projetPi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchRequest {
    private String query;
    private List<String> categoryIds;
    private List<String> tags;
    private String sellerId;
    
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    private Boolean inStock;
    private Boolean isFeatured;
    private Boolean rentalAvailable;
    
    private Double minRating;
    
    private String sortBy; // price, rating, date, popularity
    private String sortDirection; // asc, desc
    
    private Integer page;
    private Integer size;
}
