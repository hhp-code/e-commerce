package com.ecommerce.interfaces.controller.domain.product.dto;

import com.ecommerce.domain.product.ProductWrite;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;

import java.util.List;

public class ProductMapper {
    public static ProductDto.ProductListResponse toProductListResponse(List<ProductWrite> products) {
        return new ProductDto.ProductListResponse(products);
    }
    public static ProductDto.ProductResponse toProductResponse(ProductWrite product) {
        return new ProductDto.ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    public static OrderDto.PopularListResponse toPopulartListResponse(List<ProductWrite> popularProducts) {
        return new OrderDto.PopularListResponse(popularProducts);
    }
}
