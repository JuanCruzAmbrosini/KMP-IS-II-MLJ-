package com.ecommerce.cart.model;

import java.math.BigDecimal;

public record CartItem(Long id, String name, BigDecimal price, int quantity) {
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
