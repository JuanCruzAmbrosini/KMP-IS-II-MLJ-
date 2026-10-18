package com.ecommerce.cart.controller;

import com.ecommerce.cart.model.CartItem;
import com.ecommerce.cart.model.OrderSummary;
import com.ecommerce.cart.service.AbstractCartCheckoutTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final AbstractCartCheckoutTemplate standardCheckout;
    private final AbstractCartCheckoutTemplate expressCheckout;

    public CartController(
            @Qualifier("standardCheckout") AbstractCartCheckoutTemplate standardCheckout,
            @Qualifier("expressCheckout") AbstractCartCheckoutTemplate expressCheckout) {
        this.standardCheckout = standardCheckout;
        this.expressCheckout = expressCheckout;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderSummary> checkout(
            @RequestParam(defaultValue = "standard") String shippingType,
            @RequestBody List<CartItem> items) {
        
        AbstractCartCheckoutTemplate processor = "express".equalsIgnoreCase(shippingType) 
                ? expressCheckout 
                : standardCheckout;

        return ResponseEntity.ok(processor.processCheckout(items));
    }
}
