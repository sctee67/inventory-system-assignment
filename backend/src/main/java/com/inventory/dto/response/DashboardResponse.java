package com.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponse {
    private long totalProducts;
    private long inStockCount;
    private long lowStockCount;
    private long outOfStockCount;
    private BigDecimal totalInventoryValue;
    private List<ProductResponse> lowStockItems;
}
