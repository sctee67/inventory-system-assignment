package com.inventory.service;

import com.inventory.dto.request.ProductRequest;
import com.inventory.dto.response.PagedResponse;
import com.inventory.dto.response.ProductResponse;
import com.inventory.entity.Product;
import com.inventory.entity.User;
import com.inventory.entity.enums.StockStatus;
import com.inventory.exception.DuplicateResourceException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductResponse createProduct(ProductRequest request, MultipartFile image, User user) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setSku(request.getSku());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.setUser(user);

        product.updateStatus();

        if (image != null && !image.isEmpty()) {
            product.setImageUrl(fileStorageService.storeFile(image));
        }

        productRepository.save(product);
        log.info("Product created: {} (SKU: {})", product.getName(), product.getSku());
        return mapToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (productRepository.existsBySkuAndIdNot(request.getSku(), id)) {
            throw new DuplicateResourceException("Product with SKU '" + request.getSku() + "' already exists");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setSku(request.getSku());
        product.setQuantity(request.getQuantity());
        product.setPrice(request.getPrice());
        product.updateStatus();

        if (image != null && !image.isEmpty()) {
            fileStorageService.deleteFile(product.getImageUrl());
            product.setImageUrl(fileStorageService.storeFile(image));
        }

        productRepository.save(product);
        log.info("Product updated: {} (id: {})", product.getName(), product.getId());
        return mapToResponse(product);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        fileStorageService.deleteFile(product.getImageUrl());
        productRepository.delete(product);
        log.info("Product deleted: {} (id: {})", product.getName(), id);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    public PagedResponse<ProductResponse> getProducts(String search, String category, String status,
                                                       String sortBy, String sortDir, int page, int size) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Specification<Product> spec = buildSpecification(search, category, status);
        Page<Product> productPage = productRepository.findAll(spec, pageRequest);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<ProductResponse>builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories();
    }

    private Specification<Product> buildSpecification(String search, String category, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate skuLike = cb.like(cb.lower(root.get("sku")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                predicates.add(cb.or(nameLike, skuLike, descLike));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), StockStatus.valueOf(status)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
