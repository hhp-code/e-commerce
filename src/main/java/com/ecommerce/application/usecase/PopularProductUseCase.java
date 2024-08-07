package com.ecommerce.application.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PopularProductUseCase {
    private final OrderService orderService;

    public PopularProductUseCase(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<Product> getPopularProducts() {
        int durationDays = 3;
        List<Order> finishedOrderWithDays = orderService.getFinishedOrderWithDays(durationDays);
        Map<Product, Long> sellingMap = new ConcurrentHashMap<>();
        for(Order order: finishedOrderWithDays){
            order.getOrderItems().forEach((product, quantity) -> {
                if(sellingMap.containsKey(product)){
                    sellingMap.put(product, sellingMap.get(product) + quantity);
                } else {
                    sellingMap.put(product, (long) quantity);
                }
            });
        }
        return sellingMap.entrySet().stream()
                .sorted(Map.Entry.<Product, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }
}
