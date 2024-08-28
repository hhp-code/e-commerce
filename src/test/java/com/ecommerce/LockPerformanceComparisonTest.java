package com.ecommerce;

import com.ecommerce.application.OrderFacade;
import com.ecommerce.application.usecase.PaymentUseCase;
import com.ecommerce.config.RedisLockManager;
import com.ecommerce.domain.order.service.OrderCommand;
import com.ecommerce.domain.order.service.OrderInfo;
import com.ecommerce.domain.product.Product;
import com.ecommerce.domain.product.service.ProductService;
import com.ecommerce.domain.user.User;
import com.ecommerce.domain.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("cleanser")
public class LockPerformanceComparisonTest {

    @Autowired
    private RedisLockManager redisLockManager;

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Autowired
    private PaymentUseCase paymentUseCase;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private static final int ITERATIONS = 100;
    private static final int THREAD_COUNT = 10;

    List<OrderCommand.Payment> paymentCommands = new ArrayList<>();
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        for(int i = 0; i < 100; i++) {
            User user = userService.saveUser(new User("TestUser" + i, BigDecimal.valueOf(10000000)));
            Product product = productService.saveAndGet(new Product("TestProduct" + i, BigDecimal.valueOf(1), 10));
            OrderCommand.Create createOrderCommand = new OrderCommand.Create(user.getId(), Map.of(product.getId(), 1));
            OrderInfo.Summary order = orderFacade.createOrder(createOrderCommand);
            paymentCommands.add(new OrderCommand.Payment(order.userId(), order.orderId()));
        }

    }


    @Test
    public void comparePerformance() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);

        List<Future<List<Long>>> quantumResults = new ArrayList<>();
        List<Future<List<Long>>> reentrantResults = new ArrayList<>();

        // QuantumLockManager 테스트
        for (int i = 0; i < THREAD_COUNT; i++) {
            quantumResults.add(executorService.submit(this::testQuantumLock));
        }

        // ReentrantLock 테스트
        for (int i = 0; i < THREAD_COUNT; i++) {
            reentrantResults.add(executorService.submit(this::testReentrantLock));
        }

        List<Long> quantumRTTs = new ArrayList<>();
        List<Long> reentrantRTTs = new ArrayList<>();

        for (Future<List<Long>> future : quantumResults) {
            quantumRTTs.addAll(future.get());
        }

        for (Future<List<Long>> future : reentrantResults) {
            reentrantRTTs.addAll(future.get());
        }

        double avgQuantumRTT = calculateAverage(quantumRTTs);
        double avgReentrantRTT = calculateAverage(reentrantRTTs);

        System.out.println("Average RedisLock RTT: " + avgQuantumRTT + " ms");
        System.out.println("Average ReentrantLock RTT: " + avgReentrantRTT + " ms");

        assertThat(avgQuantumRTT).isPositive();
        assertThat(avgReentrantRTT).isPositive();

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    private List<Long> testQuantumLock() {
        List<Long> rtts = new ArrayList<>();
        String resourceId = "testResource-" + Thread.currentThread().getId();

        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            try {
                redisLockManager.executeWithLock(resourceId, Duration.ofSeconds(10), () -> {
                    simulateWork();
                    return null;
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long end = System.nanoTime();
            rtts.add((end - start) / 1_000_000); // 나노초를 밀리초로 변환
        }

        return rtts;
    }

    private List<Long> testReentrantLock() {
        List<Long> rtts = new ArrayList<>();

        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            reentrantLock.lock();
            try {
                simulateWork();
            } finally {
                reentrantLock.unlock();
            }
            long end = System.nanoTime();
            rtts.add((end - start) / 1_000_000); // 나노초를 밀리초로 변환
        }

        return rtts;
    }

    private void simulateWork() {
        for(OrderCommand.Payment payment : paymentCommands) {
            paymentUseCase.payOrder(payment);
        }
    }

    private double calculateAverage(List<Long> numbers) {
        return numbers.stream().mapToLong(Long::longValue).average().orElse(0.0);
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.execute();
    }
}