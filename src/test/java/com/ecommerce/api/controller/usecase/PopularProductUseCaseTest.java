package com.ecommerce.api.controller.usecase;

import com.ecommerce.config.DatabaseCleanUp;
import com.ecommerce.application.OrderFacade;
import com.ecommerce.application.usecase.PopularProductUseCase;
import com.ecommerce.domain.order.OrderService;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
@Transactional
class PopularProductUseCaseTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @Autowired
    private PopularProductUseCase popularProductUseCase;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderCommandService;


    @Autowired
    private OrderFacade orderFacade;


    @BeforeEach
    void setUp() {
        int userCount = 1000;
        int productCount = 1000;
        int orderCount = 10000;

        createUsers(userCount);
        createProducts(productCount);
        createOrders(orderCount, userCount, productCount);
    }

    private void createUsers(int count) {
        System.out.println("createUsers");
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            users.add(new User("user" + i, BigDecimal.valueOf(100000000)));
        }
        for(User user : users){
            userService.saveUser(user);
        }
        System.out.println("createUsers end");
    }

    private void createProducts(int count) {
        List<Product> products = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            products.add(new Product("product" + i, BigDecimal.valueOf(10 + i), 1000000000));
        }
        for(Product write : products){
            productService.saveAndGet(write);
        }
    }

    private void createOrders(int orderCount, int userCount, int productCount) {
        List<User> users = userService.getAllUsers();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < orderCount; i++) {
            User user = users.get(random.nextInt(userCount));
            Map<Long, Integer> orderItems = new HashMap<>();
            for (int j = 0; j < random.nextInt(productCount)+1; j++) {
                orderItems.put((long)random.nextInt(productCount) + 1, random.nextInt(10)+1);
            }
//            OrderItemWrite orderItemWrite = new OrderItemWrite();
//            OrderCommand.Create command = new OrderCommand.Create(user.getId(), orderItems);
//            OrderInfo.Summary order1 = orderFacade.createOrder(command);
//            long orderId = order1.orderId();
//            OrderCommand.Payment payment = new OrderCommand.Payment(orderId);
//            futures.add(CompletableFuture.runAsync(() -> paymentUseCase.payOrder(payment)));
//            paymentUseCase.payOrder(payment);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();


    }

    @Test
    @DisplayName("100만건에대한 테스트")
    void getPopularProducts() {
        // given
        // when

        List<Product> popularProducts = popularProductUseCase.getPopularProducts();
        // then
        for(Product product : popularProducts){
            System.out.println(product.getName()+ ":" + "재고수량" + product.getStock());
        }
        assertThat(popularProducts).isNotEmpty();
        assertThat(popularProducts.size()).isEqualTo(5);


    }


}