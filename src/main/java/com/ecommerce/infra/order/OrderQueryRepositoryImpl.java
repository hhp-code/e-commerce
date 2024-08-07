package com.ecommerce.infra.order;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.QOrder;
import com.ecommerce.domain.order.service.repository.OrderQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    public OrderQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<Order> getById(Long customerId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .leftJoin(order.orderItems).fetchJoin()
                .where(order.id.eq(customerId))
                .fetchOne());
    }

    @Override
    public List<Order> getOrders(Long orderId) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .where(order.id.in(orderId))
                .fetch();
    }


    @Override
    public List<Order> getFinishedOrderWithDays(int durationDays) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(durationDays);

        return queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .where(order.orderStatus.eq(OrderStatus.ORDERED)
                        .and(order.orderDate.between(startDate, endDate)))
                .fetch();
    }
}
