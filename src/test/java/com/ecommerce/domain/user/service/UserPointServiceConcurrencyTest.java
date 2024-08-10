package com.ecommerce.domain.user.service;

import com.ecommerce.DatabaseCleanUp;
import com.ecommerce.application.UserFacade;
import com.ecommerce.domain.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
class UserPointServiceConcurrencyTest {
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }



    @Autowired
    UserFacade userPointService;
    @Autowired
    private UserService userService;

    private User charge;
    private User deduct;

    @BeforeEach
    @Transactional
    void setUp() {
        charge = userService.saveUser( new User("testChargeUser", BigDecimal.valueOf(100)));
        deduct = userService.saveUser(new User("testDeductUser", BigDecimal.valueOf(100)));
    }


    @Test
    public void testConcurrentChargePoint() {
        int taskCount = 10;
        long userId = charge.getId();

        BigDecimal chargeAmount = BigDecimal.valueOf(10);

        List<CompletableFuture<Void>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> CompletableFuture.runAsync(() ->
                        userPointService.chargePoint(userId, chargeAmount)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();


        assertThat(userService.getUser(userId).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }
    @Test
    public void testConcurrentDeductPoint() {
        int taskCount = 5;
        long userId = deduct.getId();

        BigDecimal deductAmount = BigDecimal.valueOf(10);

        List<CompletableFuture<Void>> futures = IntStream.range(0, taskCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        userPointService.deductPoint(userId, deductAmount);
                    } catch (Exception e) {
                    }
                }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(userService.getUser(userId).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }
    @Test
    public void testDeadlockScenario() {
        long user1Id = charge.getId();
        long user2Id = deduct.getId();

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            userPointService.chargePoint(user1Id, BigDecimal.TEN);
            userPointService.deductPoint(user2Id, BigDecimal.TEN);
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            userPointService.deductPoint(user1Id, BigDecimal.TEN);
            userPointService.chargePoint(user2Id, BigDecimal.TEN);
        });

        CompletableFuture.allOf(future1, future2).join();
        assertThat(userService.getUser(user1Id).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(userService.getUser(user2Id).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }
}