package com.ecommerce.cart.model;

import java.math.BigDecimal;
import java.util.List;

public record OrderSummary(
    String orderType,
    List<CartItem> items,
    BigDecimal subtotal,
    BigDecimal shippingCost,
    BigDecimal taxAmount,
    BigDecimal total,
    String status
) {}
