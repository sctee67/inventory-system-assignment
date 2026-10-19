package com.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private String imageUrl;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
