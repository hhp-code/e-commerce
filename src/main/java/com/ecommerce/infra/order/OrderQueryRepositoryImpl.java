package com.ecommerce.infra.order;

import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.QOrder;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    public OrderQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<OrderEntity> getById(Long customerId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .leftJoin(order.orderItems).fetchJoin()
                .where(order.id.eq(customerId))
                .fetchOne());
    }

    @Override
    public List<OrderEntity> getOrders(Long orderId) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .where(order.id.in(orderId))
                .fetch();
    }


    @Override
    public List<OrderEntity> getFinishedOrderWithDays(int durationDays) {
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
