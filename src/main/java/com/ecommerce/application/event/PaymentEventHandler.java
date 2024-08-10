package com.ecommerce.application.event;

import com.ecommerce.application.external.DummyPlatform;
import com.ecommerce.domain.order.event.OrderPayAfterEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventHandler {
    private final DummyPlatform dummyPlatform;

    public PaymentEventHandler(DummyPlatform dummyPlatform) {
        this.dummyPlatform = dummyPlatform;
    }
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(OrderPayAfterEvent event) {
        dummyPlatform.send(event);
    }
}
