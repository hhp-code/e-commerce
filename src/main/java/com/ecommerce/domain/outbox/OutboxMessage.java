package com.ecommerce.domain.outbox;


import lombok.Getter;

import java.time.LocalDateTime;

public class OutboxMessage {
    @Getter
    private String id;
    private String aggregateType;
    private String aggregateId;
    @Getter
    private String eventType;
    @Getter
    private String payload;
    private LocalDateTime createdAt;
    @Getter
    private int retryCount;

    public OutboxMessage(String id, String aggregateType, String aggregateId, String eventType, String payload, LocalDateTime now, int retryCount) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = now;
        this.retryCount = retryCount;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    public void setNextRetryTime(LocalDateTime localDateTime) {
        // 다음 재시도 시간 설정

    }
}