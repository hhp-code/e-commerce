package com.ecommerce.api.order.controller.dto;

import java.util.List;

public record OrderRequest(long customerId, List<OrderItem> items) {
}
