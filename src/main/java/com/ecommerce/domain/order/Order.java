package com.ecommerce.domain.order;

import com.ecommerce.interfaces.exception.domain.OrderException;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
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


    void calculatePrices() {
        this.regularPrice = calculateRegularPrice();
        this.salePrice = calculateSalePrice();
        this.sellingPrice = this.regularPrice.subtract(this.salePrice);
    }

    private BigDecimal calculateSalePrice() {
        return this.regularPrice.multiply(new BigDecimal("0.1"));
    }

    private BigDecimal calculateRegularPrice() {
        return orderItems.entrySet().stream()
                .map(item -> calculateItemPrice(item.getKey(), item.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemPrice(Product product, Integer price) {
        return product.getPrice().multiply(BigDecimal.valueOf(price));

    }


    public void addOrderItem(Product product, Integer quantity) {
        if (this.orderItems == null) {
            this.orderItems = new HashMap<>();
        }
        this.orderItems.put(product, quantity);
        calculatePrices();
    }


    public Order finish() {
        this.orderStatus = OrderStatus.ORDERED;
        return this;
    }

    public String getOrderStatus() {
        String name = orderStatus.name();
        if(name.equals("ORDERED")) {
            return "주문완료";
        } else if(name.equals("CANCELLED")) {
            return "주문취소";
        } else {
            return "주문준비중";
        }
    }

    public BigDecimal getTotalAmount() {
        if(this.sellingPrice == null) {
            calculatePrices();
        }
        return sellingPrice;
    }

    public Order cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
        return this;
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
        calculatePrices();
        return this;
    }

    public Order deleteItem(ProductService productService, long orderId) {
        Product product = productService.getProduct(orderId);
        this.deleteOrderItem(product);
        calculatePrices();
        return this;
    }

    public Order addItems( Map<Product, Integer> items) {
        items.forEach(this::validateOrderItem);
        this.orderItems.putAll(items);
        calculatePrices();
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

