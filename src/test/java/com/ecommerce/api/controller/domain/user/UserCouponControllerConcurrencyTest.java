package com.ecommerce.api.controller.domain.user;

import com.ecommerce.api.scheduler.CouponQueueManager;
import com.ecommerce.domain.coupon.Coupon;
import com.ecommerce.domain.coupon.DiscountType;
import com.ecommerce.domain.coupon.service.CouponService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserCouponControllerConcurrencyTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponQueueManager couponQueueManager;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp(){
        couponService.deleteAll();
        userService.deleteAll();
        Coupon coupon = new Coupon(1L,"SUMMER2024", BigDecimal.valueOf(1000), DiscountType.FIXED_AMOUNT, 1000
        , LocalDateTime.now(),LocalDateTime.now().plusDays(7),true);
        Coupon coupon2 = new Coupon(2L,"WINTER2024", BigDecimal.valueOf(5000), DiscountType.PERCENTAGE, 500
                , LocalDateTime.now(),LocalDateTime.now().plusDays(7),true);
        couponService.save(coupon);
        couponService.save(coupon2);
        for(int i=0; i<1000; i++){
            userService.saveUser(new User("TestUser"+i, BigDecimal.ZERO));
        }

    }

    /*
      목적: 동시에 많은 수의 쿠폰 발급 요청을 처리할 수 있는지 확인
      시나리오:
      1. 여러 스레드에서 동시에 1000개의 쿠폰 발급 요청을 보냄
      2. 쿠폰 수량이 정확히 감소했는지 확인
     */

    @Test
    @DisplayName("대량 동시 요청 처리 테스트 쿠폰 -1000개")
    void batchTest() throws InterruptedException {
        final long COUPON_ID = 1L;
        int THREAD_COUNT = 1000;
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            final long userId = i+1;
            executorService.submit(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/users/{userId}/coupons", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(String.valueOf(COUPON_ID)))
                            .andExpect(request().asyncStarted())
                            .andReturn();

                    mockMvc.perform(asyncDispatch(result))
                            .andExpect(status().isOk());

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 예외 처리
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        assertEquals(0, couponService.getRemainingQuantity(COUPON_ID), "쿠폰이 모두 소진되어야 합니다.");
    }



    @Test
    @DisplayName("타이밍 관련 테스트")
    void timingTest() throws Exception {
        final long COUPON_ID = 2L;
        CountDownLatch processingLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(2);

        // 첫 번째 요청
        new Thread(() -> {
            try {
                processingLatch.await();
                MvcResult result = mockMvc.perform(post("/api/users/{userId}/coupons", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(COUPON_ID)))
                        .andExpect(request().asyncStarted())
                        .andReturn();

                mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                // 예외 처리
            } finally {
                completionLatch.countDown();
            }
        }).start();

        // 두 번째 요청
        new Thread(() -> {
            try {
                processingLatch.countDown();
                Thread.sleep(100);
                MvcResult result = mockMvc.perform(post("/api/users/{userId}/coupons", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.valueOf(COUPON_ID)))
                        .andExpect(request().asyncStarted())
                        .andReturn();

                mockMvc.perform(asyncDispatch(result))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                // 예외 처리
            } finally {
                completionLatch.countDown();
            }
        }).start();

        completionLatch.await(30, TimeUnit.SECONDS);

        assertEquals(498, couponService.getRemainingQuantity(COUPON_ID), "두 요청 모두 처리되어야 합니다.");
        assertTrue(couponQueueManager.getCouponQueue().isEmpty(), "처리 완료 후 큐가 비어있어야 합니다.");
    }

}