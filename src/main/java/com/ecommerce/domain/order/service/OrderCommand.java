package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class OrderCommand {
    public record Create(long userId, Map<Long, Integer> items) {
        public Order execute(User user, Map<Product, Integer> items) {
            return new Order(user)
                    .addItems(items);
        }
    }

    public record Add(long orderId, long productId, int quantity) {
        public Order execute(Order order, ProductService productService) {
            return order
                    .addItem(productService, productId, quantity);
        }
    }
    public record Payment(long userId, long orderId) {
        public Order execute(Order order) {
            return order
                    .deductStock()
                    .deductPoint()
                    .finish();
        }
    }
    public record Cancel(long userId, long orderId) {
        public Order execute(Order order) {
            return order
                    .chargeStock()
                    .chargePoint()
                    .cancel();
        }
    }
    public record Delete(long orderId, long productId) {
        public Order execute(Order order, ProductService productService) {
            return order
                    .deleteItem(productService, productId);
        }
    }
}
