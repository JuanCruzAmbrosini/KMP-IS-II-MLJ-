package com.ecommerce.cart;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.OrderSummary;
import com.ecommerce.cart.service.ExpressCartCheckout;
import com.ecommerce.cart.service.StandardCartCheckout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartCheckoutTest {

    private StandardCartCheckout standardCheckout;
    private ExpressCartCheckout expressCheckout;

    @BeforeEach
    void setUp() {
        standardCheckout = new StandardCartCheckout();
        expressCheckout = new ExpressCartCheckout();
    }

    @Test
    void testStandardCheckout() {
        List<CartItem> items = List.of(
                new CartItem(1L, "Teclado Mecánico RGB", new BigDecimal("75.00"), 1),
                new CartItem(2L, "Mouse Inalámbrico", new BigDecimal("35.50"), 2)
        );

        OrderSummary summary = standardCheckout.processCheckout(items);

        assertEquals("STANDARD_DELIVERY", summary.orderType());
        assertEquals("COMPLETED", summary.status());
        assertEquals(new BigDecimal("146.00"), summary.subtotal());
        assertEquals(new BigDecimal("5.00"), summary.shippingCost());
        assertEquals(new BigDecimal("30.66"), summary.taxAmount());
        assertEquals(new BigDecimal("181.66"), summary.total());
    }

    @Test
    void testExpressCheckout() {
        List<CartItem> items = List.of(
                new CartItem(1L, "Teclado Mecánico RGB", new BigDecimal("75.00"), 1),
                new CartItem(2L, "Mouse Inalámbrico", new BigDecimal("35.50"), 2)
        );

        OrderSummary summary = expressCheckout.processCheckout(items);

        assertEquals("EXPRESS_PRIORITY", summary.orderType());
        assertEquals("COMPLETED", summary.status());
        assertEquals(new BigDecimal("146.00"), summary.subtotal());
        // Shipping: 15.00 + (1.50 * 2) = 18.00
        assertEquals(new BigDecimal("18.00"), summary.shippingCost());
        assertEquals(new BigDecimal("30.66"), summary.taxAmount());
        assertEquals(new BigDecimal("194.66"), summary.total());
    }

    @Test
    void testEmptyCartThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> standardCheckout.processCheckout(List.of()));
    }
}
