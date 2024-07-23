package com.ecommerce.api.controller.domain.order.dto;

import com.ecommerce.api.exception.domain.OrderException;
import com.ecommerce.domain.product.Product;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@UtilityClass
public class OrderDto {

    public record OrderCreateRequest(long customerId, Map<Long,Integer> items) {
        public void validate() {
            if (items.size() > 10) {
                throw new OrderException.ControllerException("주문 수량은 최대 10개까지 가능합니다.");
            }
        }

    }

    public record OrderListRequest(long customerId) {
        public void validate() {
            if (customerId <= 0) {
                throw new OrderException.ControllerException("주문자의 ID가 잘못되었습니다.");
            }
        }
    }

    public record OrderResponse(Long id,
                                LocalDateTime orderDate,
                                BigDecimal regularPrice,
                                BigDecimal salePrice,
                                BigDecimal sellingPrice,
                                String status,
                                Boolean isDeleted,
                                LocalDateTime deletedAt,
                                Map<Long,Integer> items) {
    }

    public record OrderListResponse(List<OrderResponse> orders) {
    }


    public record OrderAddItemRequest(long productId, int quantity) {
        public void validate() {
            if (quantity <= 0) {
                throw new OrderException.ControllerException("상품 수량은 0보다 커야 합니다.");
            }
        }
    }
    public record OrderDeleteItemRequest(long productId) {
        public void validate() {
            if (productId <= 0) {
                throw new OrderException.ControllerException("상품 ID가 잘못되었습니다.");
            }
        }
    }

    public record OrderPayRequest (long userId, long orderId) {
        public void validate() {
            if (orderId <= 0) {
                throw new OrderException.ControllerException("주문 ID가 잘못되었습니다.");
            }
        }
    }
    public record OrderCancelRequest(long userId, long orderId) {
        public void validate() {
            if (orderId <= 0) {
                throw new OrderException.ControllerException("주문 ID가 잘못되었습니다.");
            }
        }
    }
    public record PopularListResponse(List<Product> products) {
    }
}
