package com.ecommerce.api.order.controller.dto;

import com.ecommerce.api.domain.OrderItem;
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



}
