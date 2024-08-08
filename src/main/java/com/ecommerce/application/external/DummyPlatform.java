package com.ecommerce.application.external;

import com.ecommerce.domain.order.event.PayAfterEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DummyPlatform {
    @Transactional
    public boolean send(PayAfterEvent order){
        if(order.eventId() != null){
            log.info("주문이 완료되었습니다. 주문번호: {}", order.getId());
            return true;
        }
        return false;
    }
}
