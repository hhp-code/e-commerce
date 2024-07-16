package com.ecommerce.domain.order.service.external;

import com.ecommerce.domain.order.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class DummyPlatform {
    @Transactional
    public boolean send(Order order){
        if(order.isFinished()){
            log.info("주문이 완료되었습니다. 주문번호: {}", order.getId());
            return true;
        }
        if(order.isCanceled()){
            log.info("주문이 취소되었습니다. 주문번호: {}", order.getId());
            return true;
        }
        return false;
    }
}