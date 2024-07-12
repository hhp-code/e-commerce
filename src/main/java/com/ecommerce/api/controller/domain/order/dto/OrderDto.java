package com.ecommerce.api.controller.domain.order.dto;

import com.ecommerce.domain.order.OrderItem;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class OrderDto {

    public record OrderCreateRequest(long customerId, List<OrderItem> items) {
        public void validate() {
            if (items.size() > 10) {
                throw new IllegalArgumentException("주문 수량은 최대 10개까지 가능합니다.");
            }
        }

    }

    public record OrderListRequest(long customerId) {
        public void validate() {
            if (customerId <= 0) throw new IllegalArgumentException("주문자의 ID가 잘못되었습니다.");
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
                                List<OrderItem> items) {
    }

    public record OrderListResponse(List<OrderResponse> orders) {
    }


    public record OrderAddItemRequest(long productId, int quantity) {
        public void validate() {
            if (quantity <= 0) {
                throw new IllegalArgumentException("상품 수량은 0보다 커야 합니다.");
            }
        }
    }

    public record OrderPayRequest (long orderId,  BigDecimal amount) {
        public void validate() {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("결제 금액은 0 과같거나 커야 합니다.");
            }
        }
    }
}
