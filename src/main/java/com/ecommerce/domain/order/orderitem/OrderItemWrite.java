package com.ecommerce.domain.order.orderitem;

import com.ecommerce.domain.product.Product;

public record OrderItemWrite(Product product, int quantity) {

}