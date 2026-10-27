package com.kindsonthegenius.product_app.dto;

import com.kindsonthegenius.product_app.model.Product;

import java.time.LocalDateTime;

public record ProductListResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        CategorySummary category
) {
    public static ProductListResponse from(Product product) {
        CategorySummary category = product.getCategory() == null
                ? null
                : new CategorySummary(
                        product.getCategory().getId(),
                        product.getCategory().getTitle(),
                        product.getCategory().getMetaTitle()
                );
        return new ProductListResponse(product.getId(), product.getTitle(), product.getCreatedAt(), category);
    }

    public record CategorySummary(Long id, String title, String metaTitle) {
    }
}
