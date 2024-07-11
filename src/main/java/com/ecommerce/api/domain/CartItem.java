package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private Integer quantity;

    private LocalDateTime addedDate;

    @Getter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    @Getter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.addedDate = LocalDateTime.now();
    }



}