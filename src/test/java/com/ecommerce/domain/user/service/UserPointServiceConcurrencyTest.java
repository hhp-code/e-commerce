package com.ecommerce.domain.user.service;

import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserPointServiceConcurrencyTest {
    @Mock
    UserRepository userRepository;
    @Autowired
    UserPointService userPointService;
    @Autowired
    private UserService userService;

    private User user;
    @BeforeEach
    void setUp() {
        userService.deleteAll();
        user = new User("testUser", BigDecimal.valueOf(100));
        userService.saveUser(user);
    }


    @Test
    public void testConcurrentChargePoint() throws InterruptedException {
        long userId = 1L;
        int threadCount = 10;

        BigDecimal chargeAmount = BigDecimal.valueOf(10);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.chargePoint(userId, chargeAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        assertThat(userService.getUser(userId).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }
    @Test
    public void testConcurrentDeductPoint() throws InterruptedException {
        Long userId = 1L;
        BigDecimal decreaseAmount = new BigDecimal("10");
        int threadCount = 5;



        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    userPointService.deductPoint(1L, decreaseAmount);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        assertThat(userService.getUser(userId).getPoint()).isEqualByComparingTo(BigDecimal.valueOf(50));

    }



}