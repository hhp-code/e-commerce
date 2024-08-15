package com.ecommerce.domain.order.orderitem;

import com.ecommerce.domain.product.ProductWrite;
import lombok.Getter;

public record OrderItemWrite(ProductWrite product, int quantity) {

}