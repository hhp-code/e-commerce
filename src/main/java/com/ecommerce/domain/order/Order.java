package com.ecommerce.domain.order;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_date_product_quantity",
                columnList = "orderDate, product_id, quantity")
})
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


    @Getter
    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Getter
    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems;

    @Getter
    private LocalDateTime deletedAt;

    public Order() {
    }

    public Order(User user, List<OrderItem> orderItems) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new ArrayList<>(orderItems);
        this.isDeleted = false;
        calculatePrices();
    }
    public Order(long orderId, User user, List<OrderItem> orderItems) {
        this.id = orderId;
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new ArrayList<>(orderItems);
        this.isDeleted = false;
        calculatePrices();
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
        this.regularPrice = calculateRegularPrice();
        this.sellingPrice = calculateSellingPrice();
        this.salePrice = calculateSalePrice();
    }

    private BigDecimal calculateRegularPrice() {
        return orderItems.stream()
                .map(this::calculateItemPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemPrice(OrderItem item) {
        return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
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

    private BigDecimal calculateSalePrice() {
        return regularPrice.subtract(sellingPrice);
    }

    

    public void addCartItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
    }


    public void finish() {
        this.status = OrderStatus.ORDERED;
    }

    public void start() {
        this.status = OrderStatus.PREPARED;
    }

    public String getStatus() {
        return status.name();
    }

    public BigDecimal getTotalAmount() {
        return sellingPrice;
    }

    public boolean isFinished() {
        return status == OrderStatus.ORDERED;
    }

}

