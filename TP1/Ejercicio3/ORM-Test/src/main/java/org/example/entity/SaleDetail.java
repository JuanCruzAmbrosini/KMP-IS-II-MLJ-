package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SaleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private double subtotal;
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
