package com.ecommerce.domain.order.orderitem;

import com.ecommerce.domain.product.Product;
import lombok.Getter;

@Getter
public record OrderItemWrite(Product product, int quantity) {

}