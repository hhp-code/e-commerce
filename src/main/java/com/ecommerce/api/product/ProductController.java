package com.ecommerce.api.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public List<ProductResponse> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Arrays.asList(
                new ProductResponse(1L, "ProductResponse 1", BigDecimal.valueOf(100), 50),
                new ProductResponse(2L, "ProductResponse 2", BigDecimal.valueOf(200), 30)
        );
    }

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        return new ProductResponse(productId, "wow", BigDecimal.valueOf(100), 50);
    }

    @PostMapping
    public ProductResponse createProduct(@RequestBody ProductRequest request) {
        return new ProductResponse(1L, request.name(), request.price(), request.quantity());
    }

    @PutMapping("/{productId}")
    public ProductResponse updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        return new ProductResponse(productId, request.name(), request.price(), request.quantity());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.noContent().build();
    }
    record ProductRequest(String name, BigDecimal price, int quantity) {}

    record ProductResponse(Long id, String name, BigDecimal price, int quantity) {}
}
