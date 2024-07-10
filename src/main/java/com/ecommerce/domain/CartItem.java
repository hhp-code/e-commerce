package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private Integer quantity;

    private LocalDateTime addedDate;

    @Setter
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @Getter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public CartItem() {
    }

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.addedDate = LocalDateTime.now();
    }


    public long getId() {
        return id;
    }
}