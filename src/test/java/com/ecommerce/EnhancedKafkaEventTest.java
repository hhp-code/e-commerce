package com.ecommerce;

import com.ecommerce.domain.order.event.OrderCancelEvent;
import com.ecommerce.domain.order.event.OrderCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.group-id=test-group"
})
@EmbeddedKafka(partitions = 3, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class EnhancedKafkaEventTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ORDER_CREATE_TOPIC = "order-create";
    private static final String ORDER_CANCEL_TOPIC = "order-cancel";
    private static final int TIMEOUT_SECONDS = 30;

    @Test
    public void testMultipleEventTypes() throws Exception {
        KafkaConsumer<String, String> consumer = createTestConsumer();
        consumer.subscribe(Arrays.asList(ORDER_CREATE_TOPIC, ORDER_CANCEL_TOPIC));

        // 여러 이벤트 발행
        OrderCreateEvent createEvent = new OrderCreateEvent(1L, Collections.emptyList());
        OrderCancelEvent cancelEvent = new OrderCancelEvent(1L);

        kafkaTemplate.send(ORDER_CREATE_TOPIC, objectMapper.writeValueAsString(createEvent));
        kafkaTemplate.send(ORDER_CANCEL_TOPIC, objectMapper.writeValueAsString(cancelEvent));

        CountDownLatch latch = new CountDownLatch(2);
        consumeAndVerifyMessages(consumer, latch, 2);

        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS), "Both events should be received");
    }

    @Test
    public void testInvalidMessageFormat() {
        String invalidJson = "{ invalid json }";
        assertThrows(Exception.class, () -> kafkaTemplate.send(ORDER_CREATE_TOPIC, invalidJson).get());
    }

    @Test
    public void testHighVolumeMessageProcessing() throws Exception {
        int messageCount = 1000;
        CountDownLatch latch = new CountDownLatch(messageCount);
        KafkaConsumer<String, String> consumer = createTestConsumer();
        consumer.subscribe(Collections.singletonList(ORDER_CREATE_TOPIC));

        // 대량의 메시지 발행
        for (int i = 0; i < messageCount; i++) {
            OrderCreateEvent event = new OrderCreateEvent((long) i, Collections.emptyList());
            kafkaTemplate.send(ORDER_CREATE_TOPIC, objectMapper.writeValueAsString(event));
        }

        new Thread(() -> consumeAndVerifyMessages(consumer, latch, messageCount)).start();

        assertTrue(latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS),
                "All " + messageCount + " messages should be processed");
    }

    private void consumeAndVerifyMessages(KafkaConsumer<String, String> consumer, CountDownLatch latch, int expectedCount) {
        int receivedCount = 0;
        while (receivedCount < expectedCount) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Received: " + record.value());
                latch.countDown();
                receivedCount++;
            }
        }
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