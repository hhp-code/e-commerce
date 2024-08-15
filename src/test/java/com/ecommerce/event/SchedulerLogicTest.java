package com.ecommerce.event;

import com.ecommerce.application.CommandHandler;
import com.ecommerce.application.OutBoxProcessor;
import com.ecommerce.domain.event.EventBus;
import com.ecommerce.domain.order.event.OrderCreateEvent;
import com.ecommerce.domain.outbox.OutboxMessage;
import com.ecommerce.domain.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
public class SchedulerLogicTest {

    @MockBean
    private OutboxRepository outboxRepository;

    @MockBean
    private EventBus eventBus;

    @Autowired
    private OutBoxProcessor outBoxProcessor;

    @Autowired
    private CommandHandler commandHandler;

    @Test
    public void testProcessOutbox() {
        // 테스트용 OutboxMessage 생성
        OrderCreateEvent event = new OrderCreateEvent(1L, new ArrayList<>());
        String payload = event.toString();
        OutboxMessage outboxMessage = new OutboxMessage(
                UUID.randomUUID().toString(),
                event.getClass().getSimpleName(),
                event.getAggregateId(),
                event.getEventType(),
                payload,
                LocalDateTime.now(),
                0
        );
        when(outboxRepository.findUnprocessedMessages()).thenReturn(List.of(outboxMessage));

        // processOutbox 메소드 실행
        outBoxProcessor.processOutbox();

        // eventBus의 publish 메소드가 호출되었는지 확인
        verify(eventBus, times(1)).publish(anyString(), anyString());

        // outboxRepository의 delete 메소드가 호출되었는지 확인
        verify(outboxRepository, times(1)).delete(anyString());
    }
}