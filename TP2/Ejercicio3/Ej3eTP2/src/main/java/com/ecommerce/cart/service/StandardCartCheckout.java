package com.ecommerce.cart.service;

import com.ecommerce.cart.model.CartItem;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service("standardCheckout")
public class StandardCartCheckout extends AbstractCartCheckoutTemplate {

    @Override
    protected String getOrderTypeName() {
        return "STANDARD_DELIVERY";
    }

    @Override
    protected BigDecimal calculateShipping(List<CartItem> items) {
        // Tarifa fija estándar: $5.00
        return new BigDecimal("5.00");
    }

    @Override
    protected BigDecimal calculateTax(BigDecimal subtotal) {
        // IVA estándar 21%
        return subtotal.multiply(new BigDecimal("0.21"));
    }

    @Override
    protected void executePayment(BigDecimal total) {
        // Lógica de cobro estándar diferido
    }
}
