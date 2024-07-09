package com.ecommerce.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private BigDecimal price;
    private Integer availableStock;
    private Integer reservedStock;
    private LocalDateTime lastUpdated;
    private boolean isDeleted;
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isDeleted() {
        return isDeleted;
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

    public void setName(String sampleProduct) {
        this.name = sampleProduct;
    }

    public void setPrice(BigDecimal bigDecimal) {
        this.price = bigDecimal;
    }

    public void setAvailableStock(int i) {
        this.availableStock = i;
    }

    public void setReservedStock(int i) {
        this.reservedStock = i;
    }

    public void setLastUpdated(LocalDateTime now) {
        this.lastUpdated = now;
    }

    public void setDeleted(boolean b) {
        this.isDeleted = b;
    }
}