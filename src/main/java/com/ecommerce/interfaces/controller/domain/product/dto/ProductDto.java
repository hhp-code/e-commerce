package com.ecommerce.interfaces.controller.domain.product.dto;

import com.ecommerce.domain.product.ProductWrite;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.util.List;

@UtilityClass
public class ProductDto {
    public record ProductResponse(Long id, String name, BigDecimal price, int quantity) {
    }
    public record ProductListResponse(List<ProductWrite> products) {
    }

}
