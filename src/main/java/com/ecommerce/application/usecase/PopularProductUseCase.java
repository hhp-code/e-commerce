package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.orderitem.OrderItemRead;
import com.ecommerce.domain.order.OrderRead;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.product.ProductWrite;
import com.ecommerce.domain.product.service.ProductService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PopularProductUseCase {
    private final OrderService orderService;
    private final ProductService productService;

    public PopularProductUseCase(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    public List<ProductWrite> getPopularProducts() {
        int durationDays = 3;
        List<OrderRead> finishedOrderEntityWithDays = orderService.getFinishedOrderWithDays(durationDays);
        Map<ProductWrite, Integer> sellingMap = new ConcurrentHashMap<>();
        for (OrderRead orderRead : finishedOrderEntityWithDays) {
            for (OrderItemRead orderLine : orderRead.getItems()) {
                long productId = orderLine.productId();
                ProductWrite product = productService.getProduct(productId);
                int quantity = orderLine.quantity();
                sellingMap.put(product, sellingMap.getOrDefault(product, 0) + quantity);
            }
        }
        return sellingMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map(Map.Entry::getKey)
                .toList();
    }
}
