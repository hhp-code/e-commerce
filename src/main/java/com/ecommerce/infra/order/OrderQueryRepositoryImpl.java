package com.ecommerce.infra.order;

import com.ecommerce.domain.order.state.OrderStatus;
import com.ecommerce.infra.order.entity.OrderEntity;
import com.ecommerce.domain.order.query.OrderQueryRepository;
import com.ecommerce.infra.order.entity.QOrderEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
    private final JPAQueryFactory queryFactory;
    private final QOrderEntity order = QOrderEntity.orderEntity;
    public OrderQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<OrderEntity> getById(Long customerId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .leftJoin(order.orderItemEntities).fetchJoin()
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
                .where(order.orderStatus.eq(OrderStatus.STOCK_DEDUCTED)
                        .and(order.orderDate.between(startDate, endDate)))
                .fetch();
    }
}
