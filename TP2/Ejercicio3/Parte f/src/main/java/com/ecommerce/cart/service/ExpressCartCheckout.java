package com.ecommerce.cart.service;

import com.ecommerce.cart.model.CartItem;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service("expressCheckout")
public class ExpressCartCheckout extends AbstractCartCheckoutTemplate {

    @Override
    protected String getOrderTypeName() {
        return "EXPRESS_PRIORITY";
    }

    @Override
    protected BigDecimal calculateShipping(List<CartItem> items) {
        // Tarifa base $15.00 + $1.50 por cada ítem adicional
        return new BigDecimal("15.00").add(new BigDecimal("1.50").multiply(BigDecimal.valueOf(items.size())));
    }

    @Override
    protected BigDecimal calculateTax(BigDecimal subtotal) {
        // Tasa reducida o régimen prioritario
        return subtotal.multiply(new BigDecimal("0.21"));
    }

    @Override
    protected void executePayment(BigDecimal total) {
        // Autorización y captura inmediata en pasarela
    }

    @Override
    protected boolean requiresSpecialPackaging() {
        return true; // Envío urgente requiere embalaje prioritario
    }
}
