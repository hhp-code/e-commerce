package com.ecommerce.api.controller.domain.order;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.interfaces.controller.domain.order.dto.OrderDto;
import com.ecommerce.domain.order.command.OrderCommandService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
@AutoConfigureMockMvc
public class OrderEntityControllerConcurrencyTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderCommandService orderCommandService;

    @Autowired
    private ProductService productService;

    private Product testProduct;

    List<OrderDto.OrderPayRequest> orderPayRequest = new ArrayList<>();
    @BeforeEach
    @Transactional
    void setUp() {
        testProduct = productService.saveAndGet(new Product("testProduct", BigDecimal.valueOf(1), 100));
        for (int i = 0; i < 1000; i++) {
            userService.saveUser(new User("TestUser" + i, BigDecimal.valueOf(10)));
        }

        List<User> testUsers = userService.getAllUsers();
        for(User user : testUsers) {
            Map<Product, Integer> items = Map.of(testProduct, 1);
            OrderEntity orderEntity = new OrderEntity(user, items);
            orderCommandService.saveOrder(orderEntity);
            orderPayRequest.add(new OrderDto.OrderPayRequest(user.getId()));
        }


    }

    @Test
    @DisplayName("100명의 인원이 동시에 결제요청을 했을경우")
    void testConcurrencyPayments() throws Exception {
        int taskCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        CountDownLatch latch = new CountDownLatch(taskCount);
        for (OrderDto.OrderPayRequest order : orderPayRequest) {
            executorService.execute(() -> {
                try{
                    mockMvc.perform(MockMvcRequestBuilders.post("/api/orders/payments")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(order)))
                            .andReturn();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);

        Thread.sleep(1000);
        assertThat(0).isEqualTo(productService.getProduct(testProduct.getId()).getStock());
        executorService.shutdown();
    }
}
