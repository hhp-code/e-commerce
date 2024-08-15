package com.ecommerce.event;

import com.ecommerce.application.CommandHandler;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.user.command.UserCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=test-group"
})
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class KafkaEventTest {

    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int TIMEOUT_SECONDS = 30;

    @Test
    public void testAllEventsPublishAndConsume() throws Exception {
        waitForConsumerGroupToStabilize();

        CountDownLatch latch = new CountDownLatch(6); // 6개의 이벤트를 기대함
        Set<String> receivedEvents = Collections.synchronizedSet(new HashSet<>());

        KafkaConsumer<String, String> consumer = createTestConsumer();
        consumer.subscribe(Arrays.asList("order-create", "order-pay-after", "order-cancel", "order-item-add", "order-item-delete", "point-charge"));

        // 별도 스레드에서 메시지 수신 대기
        new Thread(() -> {
            while (latch.getCount() > 0) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        System.out.println("Received event on topic " + record.topic() + ": " + record.value());
                        receivedEvents.add(record.topic());
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 테스트 커맨드 실행
        commandHandler.handle(new OrderCommand.Create(1L, Collections.emptyList()));
        commandHandler.handle(new OrderCommand.Payment(1L));
        commandHandler.handle(new OrderCommand.Cancel(1L));
        commandHandler.handle(new OrderCommand.Add(1L, 1L, 1));
        commandHandler.handle(new OrderCommand.Delete(1L, 1L));
        commandHandler.handle(new UserCommand.Charge(1L, BigDecimal.valueOf(1000)));

        boolean allMessagesReceived = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(allMessagesReceived, "All events should be received within " + TIMEOUT_SECONDS + " seconds");

        // 수신된 이벤트와 수신되지 않은 이벤트 표시
        System.out.println("Received events:");
        receivedEvents.forEach(event -> System.out.println("- " + event));

        System.out.println("Missing events:");
        Arrays.asList("order-create", "order-pay-after", "order-cancel", "order-item-add", "order-item-delete", "point-charge")
                .stream()
                .filter(event -> !receivedEvents.contains(event))
                .forEach(event -> System.out.println("- " + event));

        consumer.close();
    }
    private void waitForConsumerGroupToStabilize() throws InterruptedException {
        Thread.sleep(10000);
    }

    private KafkaConsumer<String, String> createTestConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return new KafkaConsumer<>(props);
    }
}