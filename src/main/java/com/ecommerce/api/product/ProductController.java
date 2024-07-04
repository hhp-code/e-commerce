package com.ecommerce.api.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    public ResponseEntity<List<Product>> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Product> products = Arrays.asList(
                new Product(1L, "Product 1", BigDecimal.valueOf(100), 50),
                new Product(2L, "Product 2", BigDecimal.valueOf(200), 30)
        );
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        Product product = new Product(productId, "wow", BigDecimal.valueOf(100), 50);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = new Product(1L, request.name(), request.price(), request.quantity());
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody ProductRequest request) {
        Product product = new Product(productId, request.name(), request.price(), request.quantity());
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        return ResponseEntity.noContent().build();
    }
    record ProductRequest(String name, BigDecimal price, int quantity) {}

    record Product(Long id, String name, BigDecimal price, int quantity) {}
}
