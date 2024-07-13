package com.ecommerce.domain.order;

import com.ecommerce.domain.product.Product;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private Integer quantity;

    private LocalDateTime addedDate;

    @Getter
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;


    @Getter
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.addedDate = LocalDateTime.now();
    }



}