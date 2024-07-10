package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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


    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> cartItems;

    public Cart() {

    }

    public Cart(User user) {
        this.user = user;
        this.cartItems = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusDays(7);
    }

    public void addCartItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
    }

    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
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