package com.ecommerce.api.product.controller.dto;

import com.ecommerce.api.domain.Product;

import java.util.List;

public class ProductMapper {
    public static ProductDto.ProductListResponse toProductListResponse(List<Product> products) {
        return new ProductDto.ProductListResponse(products);
    }

}
