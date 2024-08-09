package com.ecommerce.infra.order.entity;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_status", columnList = "order_status")
})
@Getter
public class OrderEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private LocalDateTime orderDate;
    @Getter
    private BigDecimal regularPrice;
    @Getter
    private BigDecimal salePrice;
    @Getter
    private BigDecimal sellingPrice;


    @Getter
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;


    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Getter
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItemEntities = new ArrayList<>();

    @Getter
    private LocalDateTime deletedAt;

    public OrderEntity() {
    }
    public OrderEntity(User user) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItemEntities = new ArrayList<>();
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
    }

    public OrderEntity(User user, List<OrderItemEntity> orderItemEntities) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItemEntities = new ArrayList<>(orderItemEntities);
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
        calculatePrices();
    }

    void calculatePrices() {
        this.regularPrice = calculateRegularPrice();
        this.sellingPrice = calculateSellingPrice();
        this.salePrice = regularPrice.subtract(sellingPrice);
    }

    private BigDecimal calculateRegularPrice() {
        return orderItemEntities.stream()
                .map(orderItemEntity -> calculateItemPrice(orderItemEntity.getProduct(), orderItemEntity.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemPrice(Product product, Integer price) {
        return product.getPrice().multiply(BigDecimal.valueOf(price));

    }

    private BigDecimal calculateSellingPrice() {
        if (coupon == null) {
            return regularPrice;
        }

        BigDecimal discountAmount = calculateDiscountAmount();
        return regularPrice.subtract(discountAmount).max(BigDecimal.ZERO);
    }

    private BigDecimal calculateDiscountAmount() {
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            return regularPrice.multiply(coupon.getDiscountAmount())
                    .divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return coupon.getDiscountAmount();
        }
        return BigDecimal.ZERO;
    }


    public String getOrderStatus() {
        return orderStatus.name();
    }

    public BigDecimal getTotalAmount() {
        if(this.sellingPrice == null) {
            calculatePrices();
        }
        return sellingPrice;
    }


}

