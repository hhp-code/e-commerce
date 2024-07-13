package com.ecommerce.external;

import com.ecommerce.domain.order.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummyPlatform {
    public boolean send(Order order){
        if(order.isFinished()){
            log.info("주문이 완료되었습니다. 주문번호: {}", order.getId());
            return true;
        }
        return false;
    }
}
