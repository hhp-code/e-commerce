package com.ecommerce.api.product.controller.dto;

import com.ecommerce.domain.Product;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class ProductDto {
    public record ProductResponse(Long id, String name, BigDecimal price, int quantity) {
    }
    public record ProductListResponse(List<Product> products) {
    }

}
