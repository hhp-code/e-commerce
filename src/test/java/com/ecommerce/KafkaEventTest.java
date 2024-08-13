package com.ecommerce;

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
import java.util.Collections;
import java.util.Properties;
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
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String TOPIC = "order-create";
    private static final int TIMEOUT_SECONDS = 30;

    @Test
    public void testOrderCreateEventPublishAndConsume() throws Exception {
        waitForConsumerGroupToStabilize();

        CountDownLatch latch = new CountDownLatch(1);

        // 테스트용 컨슈머 설정
        KafkaConsumer<String, String> consumer = createTestConsumer();
        consumer.subscribe(Collections.singletonList(TOPIC));

        // 별도 스레드에서 메시지 수신 대기
        new Thread(() -> {
            while (latch.getCount() > 0) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    try {
                        OrderCreateEvent event = objectMapper.readValue(record.value(), OrderCreateEvent.class);
                        System.out.println("Received event: " + event);
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        // 테스트 이벤트 생성 및 발행
        OrderCreateEvent testEvent = new OrderCreateEvent(1L, Collections.emptyList());
        String eventJson = objectMapper.writeValueAsString(testEvent);
        kafkaTemplate.send(TOPIC, eventJson);

        boolean messageReceived = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        assertTrue(messageReceived, "OrderCreateEvent should be received within " + TIMEOUT_SECONDS + " seconds");

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