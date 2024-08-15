package com.ecommerce.domain.product;

import com.ecommerce.infra.product.entity.ProductEntity;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDomainMapper {
    public static ProductWrite toWriteModel(ProductEntity product) {
        return new ProductWrite(
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }

    public static List<ProductWrite> toWriteModels(List<ProductEntity> popularProducts) {
        return popularProducts.stream()
                .map(ProductDomainMapper::toWriteModel)
                .collect(Collectors.toList());
    }

    public static ProductEntity toEntity(ProductWrite testProduct) {
        return new ProductEntity(
                testProduct.getId(),
                testProduct.getPrice(),
                testProduct.getName(),
                testProduct.getStock()
        );
    }
}
