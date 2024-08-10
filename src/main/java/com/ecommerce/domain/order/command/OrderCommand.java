package com.ecommerce.domain.order.command;

import com.ecommerce.domain.order.orderitem.OrderItemWrite;
import com.ecommerce.domain.order.OrderWrite;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.service.UserService;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class OrderCommand {
    public record Create(long userId, List<OrderItemWrite> items) {
        public OrderWrite execute(UserService userService) {
            return new OrderWrite()
                    .putUser(userService, userId)
                    .addItems(items);
        }
    }

    public record Add(long orderId, long productId, int quantity) {
        public OrderWrite execute(OrderWrite order, ProductService productService) {
            Product product = productService.getProduct(productId);
            OrderItemWrite item = new OrderItemWrite(product, quantity);
            return order.addItem(item);
        }
    }
    public record Payment(long orderId) {
        public OrderWrite execute(OrderWrite order) {
            return order
                    .deductStock()
                    .deductPoint()
                    .finish();
        }
    }
    public record Cancel(long orderId) {
        public OrderWrite execute(OrderWrite order) {
            return order
                    .chargeStock()
                    .chargePoint()
                    .cancel();
        }
    }
    public record Delete(long orderId, long productId) {
        public OrderWrite execute(OrderWrite order) {
            return order
                    .deleteItem(productId);
        }
    }
}
