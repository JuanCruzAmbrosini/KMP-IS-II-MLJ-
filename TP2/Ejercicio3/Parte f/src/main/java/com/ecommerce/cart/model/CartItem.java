package com.ecommerce.cart.model;

import java.math.BigDecimal;

public record CartItem(
    Long id, 
    String name, 
    BigDecimal price, 
    int quantity,
    Long supplierId,
    String supplierName
) {
    public CartItem(Long id, String name, BigDecimal price, int quantity) {
        this(id, name, price, quantity, null, null);
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
