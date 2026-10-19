package com.inventory.service;

import com.inventory.dto.response.DashboardResponse;
import com.inventory.dto.response.ProductResponse;
import com.inventory.entity.Product;
import com.inventory.entity.enums.StockStatus;
import com.inventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;

    public DashboardResponse getStats() {
        long total = productRepository.count();
        long inStock = productRepository.countByStatus(StockStatus.IN_STOCK);
        long lowStock = productRepository.countByStatus(StockStatus.LOW_STOCK);
        long outOfStock = productRepository.countByStatus(StockStatus.OUT_OF_STOCK);

        List<ProductResponse> lowStockItems = productRepository
                .findByStatusOrderByQuantityAsc(StockStatus.LOW_STOCK, PageRequest.of(0, 10))
                .stream()
                .map(this::mapToResponse)
                .toList();

        return DashboardResponse.builder()
                .totalProducts(total)
                .inStockCount(inStock)
                .lowStockCount(lowStock)
                .outOfStockCount(outOfStock)
                .totalInventoryValue(productRepository.getTotalInventoryValue())
                .lowStockItems(lowStockItems)
                .build();
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .sku(product.getSku())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus().name())
                .createdBy(product.getUser().getUsername())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
