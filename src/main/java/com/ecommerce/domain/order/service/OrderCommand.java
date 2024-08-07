package com.ecommerce.domain.order.service;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.external.DummyPlatform;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.service.UserService;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class OrderCommand {
    public record Create(long userId, Map<Long, Integer> items) {
        public Order execute(UserService userService, ProductService productService) {
            return new Order()
                    .putUser(userService, userId)
                    .addItems(productService, items);
        }
    }
    public record Search(long orderId) {
    }
    public record Add(long orderId, long productId, int quantity) {
        public Order execute(OrderQueryService orderQueryService, ProductService productService) {
            return orderQueryService.getOrder(orderId)
                    .addItem(productService, productId, quantity);
        }
    }
    public record Payment( long orderId) {
        public Order execute(OrderQueryService orderQueryService, DummyPlatform dummyPlatform) {
            return orderCommandService.getOrder(orderId)
                    .deductStock()
                    .deductPoint()
                    .finish()
                    .send(dummyPlatform);
        }
    }
    public record Cancel(long orderId) {
        public Order execute(OrderQueryService orderQueryService, DummyPlatform dummyPlatform) {
            return orderQueryService.getOrder(orderId)
                    .chargeStock()
                    .chargePoint()
                    .cancel()
                    .send(dummyPlatform);
        }
    }
    public record Delete(long orderId, long productId) {
        public Order execute(OrderQueryService orderQueryService, ProductService productService) {
            return orderQueryService.getOrder(orderId)
                    .deleteItem(productService, productId);
        }
    }
}
