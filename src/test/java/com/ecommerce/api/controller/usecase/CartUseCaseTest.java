package com.ecommerce.api.controller.usecase;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderItem;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CartUseCaseTest {
    private static final Long USER_ID = 1L;
    private static final Long ORDER_ID = 2L;
    private static final Long PRODUCT_ID = 3L;
    @Mock
    private OrderService orderService;


    @Mock
    private ProductService productService;

    @InjectMocks
    private CartUseCase cartUseCase;
    @Nested
    @DisplayName("장바구니 아이템 추가 테스트")
    class AddOrderItemToOrderTests {
        @Test
        @DisplayName("기존 주문에 장바구니 아이템을 추가한다")
        void addCartItemToOrder_ShouldAddItemToExistingOrder_WhenValidCommandProvided() {
            //given
            Product mockProduct = createMockProduct();
            Order mockOrder = createMockOrder();
            OrderCommand.Add addCommand = new OrderCommand.Add(USER_ID, PRODUCT_ID, 1);

            when(orderService.getOrCreateOrder(addCommand)).thenReturn(mockOrder);
            when(productService.getProduct(PRODUCT_ID)).thenReturn(mockProduct);
            when(orderService.saveAndGet(any(Order.class))).thenReturn(mockOrder);

            //when
            Order result = cartUseCase.addCartItemToOrder(addCommand);

            //then
            assertNotNull(result);
            assertEquals(mockOrder, result);
        }
    }
    private User createMockUser() {
        return new User(USER_ID, "testUser", BigDecimal.valueOf(1000));
    }

    private Product createMockProduct() {
        return new Product(1L,"test", BigDecimal.TWO, 1000);
    }

    private OrderItem createMockCartItem() {
        return new OrderItem(createMockProduct(), 1);
    }

    private Order createMockOrder() {
        Order order = new Order(ORDER_ID, createMockUser(), List.of(createMockCartItem()));
        order.start();
        return order;
    }
}