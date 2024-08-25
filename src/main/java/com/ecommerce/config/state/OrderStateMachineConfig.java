package com.ecommerce.config.state;

import com.ecommerce.domain.order.state.OrderStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

@Configuration
@EnableStateMachine
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, String> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, String> states) throws Exception {
        states
                .withStates()
                .initial(OrderStatus.CREATED)
                .state(OrderStatus.STOCK_DEDUCTED)
                .end(OrderStatus.POINT_DEDUCTED)
                .end(OrderStatus.COMPLETED)
                .end(OrderStatus.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, String> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.STOCK_DEDUCTED)
                .event("order-create")
                .and()
                .withExternal()
                .source(OrderStatus.STOCK_DEDUCTED).target(OrderStatus.STOCK_DEDUCTED)
                .event("order-item-add")
                .and()
                .withExternal()
                .source(OrderStatus.STOCK_DEDUCTED).target(OrderStatus.STOCK_DEDUCTED)
                .event("order-item-delete")
                .and()
                .withExternal()
                .source(OrderStatus.STOCK_DEDUCTED).target(OrderStatus.POINT_DEDUCTED)
                .event("pay-after")
                .and()
                .withExternal()
                .source(OrderStatus.CREATED).target(OrderStatus.COMPLETED)
                .event("order-cancel")
                .and()
                .withExternal()
                .source(OrderStatus.STOCK_DEDUCTED).target(OrderStatus.COMPLETED)
                .event("order-cancel")
                .and()
                .withExternal()
                .source(OrderStatus.STOCK_DEDUCTED).target(OrderStatus.CANCELLED)
                .event("order-cancel");
    }
}