package tn.esprit.projetPi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private String id;
    private String userId;
    private List<CartItemDTO> items;
    private BigDecimal totalAmount;
    private Integer itemCount;
}
