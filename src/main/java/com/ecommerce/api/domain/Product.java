package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Entity
public class Product {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private BigDecimal price;
    private Integer availableStock;
    private Integer reservedStock;
    @Setter
    private LocalDateTime lastUpdated;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    public Product() {
        this.lastUpdated = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Product(String name, BigDecimal price, Integer availableStock) {
        this();
        this.name = name;
        this.price = price;
        this.availableStock = availableStock;
        this.reservedStock = 0;
    }


    public int getAvailableStock() {
        return availableStock;
    }

    public Instant getLastUpdated() {
        return lastUpdated.toInstant(ZoneOffset.UTC);
    }

    public void setId(long l) {
        this.id = l;
    }

    public void setAvailableStock(int i) {
        this.availableStock = i;
    }

    public void setReservedStock(int i) {
        this.reservedStock = i;
    }

    public void setDeleted(boolean b) {
        this.isDeleted = b;
    }

}