package com.ecommerce.domain.product;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Getter
public class Product {
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer stock;

    public Product(String name, BigDecimal price, Integer stock) {
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public Product deductStock(int quantity) {
        if(stock < quantity) {
            log.error("Stock is not enough. Stock: {}, Quantity: {}", stock, quantity);
            throw new IllegalArgumentException("Stock is not enough");
        }
        stock -= quantity;
        log.info("Stock deducted. New stock: {}", stock);
        return this;
    }

    public Product chargeStock(int quantity) {
        if(quantity <= 0) {
            log.error("Invalid quantity: {}", quantity);
            throw new IllegalArgumentException("Invalid quantity");
        }
        stock += quantity;
        log.info("Stock charged. New stock: {}", stock);
        return this;
    }

    public Long getId() {
        return productId;
    }

    public LocalDateTime getLastUpdated() {
        return LocalDateTime.now();
    }

    public boolean isDeleted() {
        return false;
    }
}