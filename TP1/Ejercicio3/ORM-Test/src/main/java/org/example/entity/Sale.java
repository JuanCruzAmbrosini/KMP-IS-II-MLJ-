package org.example.entity;

import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Entity
@Getter
@Setter
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private double total;
    @OneToMany(mappedBy = "sale")
    private List<SaleDetail> saleDetails;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
