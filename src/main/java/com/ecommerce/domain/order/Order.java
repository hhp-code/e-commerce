package com.ecommerce.domain.order;

import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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
    @ElementCollection
    private Map<Product, Integer> orderItems;

    @Getter
    private LocalDateTime deletedAt;

    public Order() {
    }
    public Order(User user) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new HashMap<>();
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
    }

    public Order(User user, Map<Product, Integer> orderItems) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new HashMap<>(orderItems);
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
        calculatePrices();
    }
    public Order(long orderId, User user, Map<Product,Integer> orderItems) {
        this.id = orderId;
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new HashMap<>(orderItems);
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
        calculatePrices();
    }


    public void applyCoupon(Coupon coupon) {
        if (coupon != null && coupon.isValid() && coupon.getQuantity() >= 0) {
            this.coupon = coupon;
            calculateDiscount();
            coupon.use();

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

    void calculatePrices() {
        this.regularPrice = calculateRegularPrice();
        this.sellingPrice = calculateSellingPrice();
        this.salePrice = calculateSalePrice();
    }

    private BigDecimal calculateRegularPrice() {
        return orderItems.entrySet().stream()
                .map(item -> calculateItemPrice(item.getKey(), item.getValue()))
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

    private BigDecimal calculateSalePrice() {
        return regularPrice.subtract(sellingPrice);
    }



    public void addOrderItem(Product product, Integer quantity) {
        if (this.orderItems == null) {
            this.orderItems = new HashMap<>();
        }
        this.orderItems.put(product, quantity);
    }


    public Order finish() {
        this.orderStatus = OrderStatus.ORDERED;
        return this;
    }

    public String getOrderStatus() {
        return orderStatus.name();
    }

    public BigDecimal getTotalAmount() {
        return sellingPrice;
    }

    public boolean isFinished() {
        return orderStatus == OrderStatus.ORDERED;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }

    public boolean isCanceled() {
        return orderStatus == OrderStatus.CANCELLED;
    }

    public void deleteOrderItem(Product product) {
        this.orderItems.remove(product);
    }
}

