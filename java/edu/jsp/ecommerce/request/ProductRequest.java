package edu.jsp.ecommerce.request;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank
    private String name;
    private String description;
    @NotNull @Positive
    private BigDecimal price;
    private BigDecimal mrp;
    private Long categoryId;
    private Long brandId;
    private String thumbnailUrl;
    private int stockQuantity;

    private List<String> imageUrls;
}

