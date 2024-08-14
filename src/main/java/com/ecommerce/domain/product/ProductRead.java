package com.ecommerce.domain.product;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ProductRead {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private LocalDateTime lastUpdated;
    private boolean isDeleted;


}