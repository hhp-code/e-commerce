package com.ecommerce.domain.order;

import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

public class OrderWrite {
    @Getter
    private Long id;
    @Getter
    private LocalDateTime orderDate;
    @Getter
    private BigDecimal regularPrice;
    private BigDecimal salePrice;
    @Getter
    private BigDecimal sellingPrice;


    private boolean isDeleted;

    private OrderStatus orderStatus;


    @Getter
    private User user;

    private Coupon coupon;

    @Getter
    private List<OrderItemWrite> orderItems;

    private LocalDateTime deletedAt;

    public OrderWrite() {
    }
    public OrderWrite(User user) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new ArrayList<>();
        this.isDeleted = false;
        this.orderStatus = OrderStatus.PREPARED;
    }

    public OrderWrite(User user, List<OrderItemWrite> orderItems) {
        this.orderDate = LocalDateTime.now();
        this.user = user;
        this.orderItems = new ArrayList<>(orderItems);
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
        this.salePrice = regularPrice.subtract(sellingPrice);
    }

    private BigDecimal calculateRegularPrice() {
        return orderItems.stream()
                .map(orderItem -> calculateItemPrice(orderItem.product(), orderItem.quantity()))
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


    public void addOrderItem(OrderItemWrite orderItemWrite) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItemWrite);
    }


    public OrderWrite finish() {
        this.orderStatus = OrderStatus.ORDERED;
        return this;
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

    public OrderWrite cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
        return this;
    }

    public void deleteOrderItem(OrderItemWrite orderItemWrite) {
        this.orderItems.remove(orderItemWrite);
    }

    public OrderWrite deductStock() {
        for(OrderItemWrite orderItemWrite : orderItems){
            orderItemWrite.product().deductStock(orderItemWrite.quantity());
        }
        return this;
    }

    public OrderWrite deductPoint() {
        this.user.deductPoint( getTotalAmount());
        return this;
    }



    public OrderWrite chargeStock() {
        for(OrderItemWrite orderItemWrite : orderItems){
            orderItemWrite.product().chargeStock(orderItemWrite.quantity());
        }
        return this;

    }

    public OrderWrite chargePoint() {
        this.user.chargePoint(getTotalAmount());
        return this;
    }


    public OrderWrite addItem(OrderItemWrite orderItemWrite) {
        Product product = orderItemWrite.product();
        validateOrderItem(product, orderItemWrite.quantity());
        this.addOrderItem(orderItemWrite);
        return this;
    }

    public OrderWrite deleteItem(long productId) {
        for(OrderItemWrite orderItemWrite : orderItems){
            if(orderItemWrite.product().getId().equals(productId)){
                this.deleteOrderItem(orderItemWrite);
                return this;
            }
        }
        return this;
    }

    public OrderWrite putUser(UserService userService, long userId) {
        this.user = userService.getUser(userId);
        return this;
    }

    public OrderWrite addItems(List<OrderItemWrite> orderItemWrites) {
        for(OrderItemWrite orderItemWrite : orderItemWrites){
            Product product = orderItemWrite.product();
            validateOrderItem(product, orderItemWrite.quantity());
            this.addOrderItem(orderItemWrite);
        }
        return this;
    }

    private void validateOrderItem(Product product, int quantity) {
        if (quantity <= 0) {
            throw new OrderException("주문 수량은 0보다 커야 합니다.");
        }
        if (product.getStock() < quantity) {
            throw new OrderException("상품의 재고가 부족합니다.");
        }
    }

    public Long getUserId() {
        return user.getId();
    }

    public List<OrderItemWrite> getItems() {
        return orderItems;
    }


}

