package com.ecommerce.domain.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_name", columnList = "name"),
})
public class Product {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String name;
    @Getter
    private BigDecimal price;
    @Getter
    private Integer stock;
    @Getter
    private LocalDateTime lastUpdated;
    private boolean isDeleted;
    private LocalDateTime deletedAt;


    public Product() {
        this.lastUpdated = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Product(String name, BigDecimal price, Integer stock) {
        this();
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.lastUpdated = LocalDateTime.now();
    }
    public Product(long id, String name, BigDecimal price, Integer stock) {
        this();
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.lastUpdated = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return isDeleted;
    }


    public boolean deductStock(int quantity) {
        if(stock < quantity) {
            return false;
        }
        stock -= quantity;
        this.lastUpdated = LocalDateTime.now();
        return true;
    }
    public boolean chargeStock(int quantity) {
        if(quantity<=0 || stock < quantity) {
            return false;
        }
        stock += quantity;
        this.lastUpdated = LocalDateTime.now();
        return true;
    }

}