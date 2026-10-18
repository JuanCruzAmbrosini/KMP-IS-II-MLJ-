package com.ecommerce.cart.service;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.OrderSummary;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public abstract class AbstractCartCheckoutTemplate {

    // MÉTODO PLANTILLA: Estructura invariable del algoritmo de compra
    public final OrderSummary processCheckout(List<CartItem> items) {
        validateCart(items);
        
        BigDecimal subtotal = calculateSubtotal(items);
        BigDecimal shipping = calculateShipping(items);
        BigDecimal tax = calculateTax(subtotal);
        
        BigDecimal total = subtotal.add(shipping).add(tax);
        
        executePayment(total);
        
        if (requiresSpecialPackaging()) { // Hook
            notifyWarehouseForSpecialPackaging();
        }
        
        return new OrderSummary(
            getOrderTypeName(),
            items,
            subtotal.setScale(2, RoundingMode.HALF_UP),
            shipping.setScale(2, RoundingMode.HALF_UP),
            tax.setScale(2, RoundingMode.HALF_UP),
            total.setScale(2, RoundingMode.HALF_UP),
            "COMPLETED"
        );
    }

    // Paso común invariante
    private void validateCart(List<CartItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("El carrito no puede estar vacío");
        }
    }

    // Paso común invariante
    private BigDecimal calculateSubtotal(List<CartItem> items) {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Pasos abstractos específicos que deben implementar las subclases
    protected abstract String getOrderTypeName();
    protected abstract BigDecimal calculateShipping(List<CartItem> items);
    protected abstract BigDecimal calculateTax(BigDecimal subtotal);
    protected abstract void executePayment(BigDecimal total);

    // Hook: comportamiento por defecto sobrescribible
    protected boolean requiresSpecialPackaging() {
        return false;
    }

    protected void notifyWarehouseForSpecialPackaging() {
        // Notificación de empaque reforzado
    }
}
