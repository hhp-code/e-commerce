package com.ecommerce.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Product {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;
    @Getter
    private BigDecimal price;
    private Integer availableStock;
    private Integer reservedStock;
    @Getter
    private LocalDateTime lastUpdated;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;


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
    public Product(long id, String name, BigDecimal price, Integer availableStock) {
        this();
        this.id = id;
        this.name = name;
        this.price = price;
        this.availableStock = availableStock;
        this.reservedStock = 0;
    }


    public int getAvailableStock() {
        return availableStock;
    }

    public boolean isDeleted() {
        return isDeleted;
    }
}