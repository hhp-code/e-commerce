package com.ecommerce.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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


    private boolean isDeleted;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Getter
    @OneToMany(mappedBy = "order")
    private List<CartItem> cartItems;

    @Getter
    private LocalDateTime deletedAt;

    public Order() {
    }

    public Order(User user, List<CartItem> cartItems) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.cartItems = cartItems;
        this.isDeleted = false;
        calculatePrices();
    }
    public Order(long orderId, User user, List<CartItem> cartItems) {
        this.id = orderId;
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.cartItems = new ArrayList<>(cartItems);
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
        BigDecimal regularPrice = BigDecimal.ZERO;
        BigDecimal sellingPrice = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
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


    public Boolean getIsDeleted() {
        return isDeleted;
    }


    public void addCartItem(CartItem cartItem) {
        if (this.cartItems == null) {
            this.cartItems = new ArrayList<>();
        }
        this.cartItems.add(cartItem);
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
}

