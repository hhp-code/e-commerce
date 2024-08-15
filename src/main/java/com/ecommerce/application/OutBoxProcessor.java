package com.ecommerce.application;

import com.ecommerce.domain.event.EventBus;
import com.ecommerce.domain.outbox.OutboxMessage;
import com.ecommerce.domain.outbox.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@EnableScheduling
public class OutBoxProcessor {
    private final OutboxRepository outboxRepository;
    private final EventBus eventBus;
    private static final int MAX_RETRY_COUNT = 5;

    public OutBoxProcessor(OutboxRepository outboxRepository, EventBus eventBus) {
        this.outboxRepository = outboxRepository;
        this.eventBus = eventBus;
    }

    @Scheduled(fixedRate = 5000)
    public void processOutbox() {
        List<OutboxMessage> messages = outboxRepository.findUnprocessedMessages();
        CompletableFuture.allOf(
                messages.stream()
                        .map(this::processMessage)
                        .toArray(CompletableFuture[]::new)
        ).join();
    }

    @Transactional
    public CompletableFuture<Void> processMessage(OutboxMessage message) {
        return CompletableFuture.runAsync(() -> {
            try {
                eventBus.publish(message.getEventType(), message.getPayload());
                outboxRepository.delete(message.getId());
                log.info("Successfully processed and deleted message: {}", message.getId());
            } catch (Exception e) {
                log.error("Failed to process message: {}", message.getId(), e);
                handlePublishFailure(message);
            }
        });
    }

    private void handlePublishFailure(OutboxMessage message) {
        if (message.getRetryCount() < MAX_RETRY_COUNT) {
            message.incrementRetryCount();
            message.setNextRetryTime(calculateNextRetryTime(message.getRetryCount()));
            outboxRepository.save(message);
            log.info("Scheduled retry for message: {}. Retry count: {}", message.getId(), message.getRetryCount());
        } else {
            moveToDeadLetterQueue(message);
        }
    }

    private LocalDateTime calculateNextRetryTime(int retryCount) {
        return LocalDateTime.now().plusMinutes((long) Math.pow(2, retryCount));
    }

    private void moveToDeadLetterQueue(OutboxMessage message) {
        log.warn("Moving message to dead letter queue: {}", message.getId());
        sendAlertToOperations(message);
    }

    private void sendAlertToOperations(OutboxMessage message) {
        log.warn("Alert: Message {} failed to process after {} attempts", message.getId(), MAX_RETRY_COUNT);
    }
}