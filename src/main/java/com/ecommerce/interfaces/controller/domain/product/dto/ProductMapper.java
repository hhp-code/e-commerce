package com.ecommerce.interfaces.controller.domain.product.dto;

import com.ecommerce.domain.product.Product;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;

import java.util.List;

public class ProductMapper {
    public static ProductDto.ProductListResponse toProductListResponse(List<Product> products) {
        return new ProductDto.ProductListResponse(products);
    }
    public static ProductDto.ProductResponse toProductResponse(Product product) {
        return new ProductDto.ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getStock());
    }

    public static OrderDto.PopularListResponse toPopulartListResponse(List<Product> popularProducts) {
        return new OrderDto.PopularListResponse(popularProducts);
    }
}
