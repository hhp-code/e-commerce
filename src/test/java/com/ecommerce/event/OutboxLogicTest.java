package com.ecommerce.event;

import com.ecommerce.application.CommandHandler;
import com.ecommerce.domain.order.command.OrderCommand;
import com.ecommerce.domain.outbox.OutboxMessage;
import com.ecommerce.domain.outbox.OutboxRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class OutboxLogicTest {

    @MockBean
    private OutboxRepository outboxRepository;

    @Autowired
    private CommandHandler commandHandler;

    @Test
    public void testPublishEvents() {
        // 테스트 커맨드 생성
        OrderCommand.Create command = new OrderCommand.Create(1L, new ArrayList<>());

        // 커맨드 처리
        commandHandler.handle(command);

        // outboxRepository의 save 메소드가 호출되었는지 확인
        verify(outboxRepository, times(1)).save(any(OutboxMessage.class));
    }
}
