package com.ecommerce.infra.product.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_name", columnList = "name"),
})
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime lastUpdated;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    public ProductEntity(Long id, BigDecimal price, String name, Integer stock) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.stock = stock;
    }

    public ProductEntity() {

    }


    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}