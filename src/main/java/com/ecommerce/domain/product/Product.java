package com.ecommerce.domain.product;

import com.ecommerce.api.exception.domain.ProductException;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    @Getter
    private Integer stock;
    @Getter
    private LocalDateTime lastUpdated;
    private final boolean isDeleted;
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
    public void deductStock(int orderedQuantity) {
        if(this.stock < orderedQuantity) {
            throw new ProductException("재고가 부족합니다.");
        }
        this.stock -= orderedQuantity;
        this.lastUpdated = LocalDateTime.now();
    }
    public void chargeStock(Integer quantity) {
        if(quantity < 0) {
            throw new ProductException("충전 수량은 0보다 커야 합니다.");
        }
        this.stock += quantity;
        this.lastUpdated = LocalDateTime.now();
    }

}