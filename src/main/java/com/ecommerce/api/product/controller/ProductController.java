package com.ecommerce.api.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
@Tag(name = "product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 조회합니다.")
    public List<ProductResponse> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Arrays.asList(
                new ProductResponse(1L, "ProductResponse 1", BigDecimal.valueOf(100), 50),
                new ProductResponse(2L, "ProductResponse 2", BigDecimal.valueOf(200), 30)
        );
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 조회", description = "상품을 조회합니다.")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return new ProductResponse(productId, "wow", BigDecimal.valueOf(100), 50);
    }

    @PostMapping
    @Operation(summary = "상품 생성", description = "상품을 생성합니다.")
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return new ProductResponse(1L, request.name(), request.price(), request.quantity());
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    public ProductResponse updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        return new ProductResponse(productId, request.name(), request.price(), request.quantity());
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.noContent().build();
    }

}

