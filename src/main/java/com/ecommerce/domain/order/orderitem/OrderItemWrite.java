package com.ecommerce.domain.order.orderitem;

import com.ecommerce.domain.product.Product;
import lombok.Getter;

public record OrderItemWrite(Product product, int quantity) {

}