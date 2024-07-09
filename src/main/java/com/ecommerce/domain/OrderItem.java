package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
public class OrderItem {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private Integer quantity;
    @Getter
    private BigDecimal price;

    @Setter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem(int i, BigDecimal bigDecimal) {
        this.quantity = i;
        this.price = bigDecimal;
    }

    public OrderItem() {

    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    enum OrderStatus {
        PENDING, SHIPPED, DELIVERED, CANCELLED
    }

}