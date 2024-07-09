package com.ecommerce.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
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

    @Enumerated(EnumType.STRING)
    private OrderItem.OrderStatus status;

    private boolean isDeleted;
    @Getter
    private LocalDateTime deletedAt;


    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Getter
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;


    public Order() {
    }

    public void applyCoupon(Coupon coupon) {
        if (coupon != null && coupon.isValid()) {
            this.coupon = coupon;
            calculateDiscount();
        } else {
            throw new IllegalArgumentException("Invalid coupon");
        }
    }

    private void calculateDiscount() {
        if (this.coupon != null) {
            this.salePrice = this.coupon.getDiscountAmount();
            this.sellingPrice = this.sellingPrice.subtract(this.salePrice);
        }
    }

    private void calculatePrices() {
        BigDecimal regularPrice = BigDecimal.ZERO;
        BigDecimal sellingPrice = BigDecimal.ZERO;

        for (OrderItem item : orderItems) {
            BigDecimal itemPrice = item.getProduct().getPrice();
            int quantity = item.getQuantity();

            regularPrice = regularPrice.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }
        this.regularPrice = regularPrice;

        if (coupon != null) {
            if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
                BigDecimal discountAmount = regularPrice.multiply(coupon.getDiscountAmount().divide(BigDecimal.valueOf(100)));
                sellingPrice = regularPrice.subtract(discountAmount);
            } else if (coupon.getDiscountType() == DiscountType.FIXED_AMOUNT) {
                sellingPrice = regularPrice.subtract(coupon.getDiscountAmount());
            }
        } else {
            sellingPrice = regularPrice;
        }
        this.sellingPrice = sellingPrice.max(BigDecimal.ZERO);
        this.salePrice = regularPrice.subtract(sellingPrice);


    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
        calculatePrices();
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
        calculatePrices();
    }


    public String getStatus() {
        return status.name();
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        calculatePrices();
    }
}

