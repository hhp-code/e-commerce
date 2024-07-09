package com.ecommerce.api.product.controller;

import com.ecommerce.api.product.service.ProductService;
import com.ecommerce.api.product.controller.dto.ProductMapper;
import com.ecommerce.api.product.controller.dto.ProductDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public ProductDto.ProductListResponse getProducts() {
        return ProductMapper.toProductListResponse(
                productService.getProducts()
        );
    }


    @GetMapping("/{productId}")
    @Operation(summary = "상품 상세 조회", description = "상품을 조회합니다.")
    public ProductDto.ProductResponse getProduct(@PathVariable Long productId) {
        productService.getProduct(productId);
        return new ProductDto.ProductResponse(productId, "wow", BigDecimal.valueOf(100), 50);
    }
    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "인기 상품을 조회합니다.")
    public ProductDto.ProductListResponse getPopularProducts() {
        return ProductMapper.toProductListResponse(
                productService.getPopularProducts()
        );
    }




}

