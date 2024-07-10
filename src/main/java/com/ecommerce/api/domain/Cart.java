package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastUpdated;
    private LocalDateTime expirationDate;



    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> cartItems;


    @OneToOne
    private User user;

    public Cart() {
        this.cartItems = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusDays(7);
    }

    public void addCartItem(CartItem item) {
        cartItems.add(item);
        updateLastUpdated();
    }

    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        updateLastUpdated();
    }

    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

    public Optional<CartItem> getCartItem(Product product) {
        return cartItems.stream()
                .filter(item -> item.getProduct() != null && item.getProduct().equals(product))
                .findFirst();
    }

}