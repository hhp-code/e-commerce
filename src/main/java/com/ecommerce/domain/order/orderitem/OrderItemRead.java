package com.ecommerce.domain.order.orderitem;

import lombok.Getter;

@Getter
public record OrderItemRead(long productId, int quantity) { }
