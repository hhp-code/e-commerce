package com.ecommerce.application.saga;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class OrderStateMachine {
    private OrderState currentState;
    private Long orderId;

    public void initiate(Long orderId) {
        this.orderId = orderId;
        this.currentState = OrderState.CREATED;
    }

    public void transitionTo(OrderState newState) {
        this.currentState = newState;
    }

}