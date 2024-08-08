package com.ecommerce.application.event;

import com.ecommerce.application.external.DummyPlatform;
import com.ecommerce.domain.order.event.PayAfterEvent;
import com.ecommerce.domain.order.service.OrderInfo;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {
    private final DummyPlatform dummyPlatform;

    public PaymentEventListener(DummyPlatform dummyPlatform) {
        this.dummyPlatform = dummyPlatform;
    }
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void paymentSuccessHandler(PayAfterEvent event) {
        dummyPlatform.send(event);
    }
}
