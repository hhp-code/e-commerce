package com.ecommerce.domain.order;

import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.order.service.OrderCommandService;
import com.ecommerce.application.external.DummyPlatform;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_status", columnList = "order_status")
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
        if(this.sellingPrice == null) {
            calculatePrices();
        }
        return sellingPrice;
    }

    public boolean isFinished() {
        return orderStatus == OrderStatus.ORDERED;
    }

    public Order cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
        return this;
    }

    public boolean isCanceled() {
        return orderStatus == OrderStatus.CANCELLED;
    }

    public void deleteOrderItem(Product product) {
        this.orderItems.remove(product);
    }

    public Order deductStock() {
        for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            product.deductStock( quantity);
        }
        return this;
    }

    public Order deductPoint() {
        this.user.deductPoint( getTotalAmount());
        return this;
    }

    public Order send(DummyPlatform dummyPlatform) {
        dummyPlatform.send(this);
        return this;
    }

    public Order saveAndGet(OrderCommandService orderCommandService) {
        return orderCommandService.saveOrder(this);
    }

    public Order chargeStock() {
        for (Map.Entry<Product, Integer> entry : this.orderItems.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            product.chargeStock(quantity);
        }
        return this;

    }

    public Order chargePoint() {
        this.user.chargePoint(getTotalAmount());
        return this;
    }


    public Order addItem(ProductService productService, Long productId, int quantity) {
        Product product = productService.getProduct(productId);
        if (product.getStock() < quantity) {
            throw new OrderException.ServiceException("상품의 재고가 부족합니다.");
        }
        this.addOrderItem(product, quantity);
        return this;
    }

    public Order deleteItem(ProductService productService, long orderId) {
        Product product = productService.getProduct(orderId);
        this.deleteOrderItem(product);
        return this;
    }

    public Order putUser(UserService userService, long userId) {
        this.user = userService.getUser(userId);
        return this;
    }

    public Order addItems(ProductService productService, Map<Long, Integer> items) {
        items.forEach((productId, quantity) -> {
            Product product = productService.getProduct(productId);
            validateOrderItem(product, quantity);
            this.addOrderItem(product, quantity);
        });
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
}

