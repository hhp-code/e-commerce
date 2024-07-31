package com.ecommerce.domain.order.repository;

import com.ecommerce.domain.order.Order;
import com.ecommerce.domain.order.OrderStatus;
import com.ecommerce.domain.order.QOrder;
import com.ecommerce.domain.order.service.repository.OrderRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJPARepository orderJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;

    public OrderRepositoryImpl(OrderJPARepository orderJPARepository, JPAQueryFactory queryFactory) {
        this.orderJPARepository = orderJPARepository;
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
    public Optional<Order> saveAndGet(Order order) {
        return Optional.of(orderJPARepository.save(order));
    }

    @Override
    public Optional<Order> findByUserIdAndStatus(Long id, OrderStatus orderStatus) {
        return Optional.ofNullable(queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .where(order.user.id.eq(id)
                        .and(order.orderStatus.eq(orderStatus)))
                .fetchOne());
    }

    @Override
    @Transactional
    public void deleteAll() {
        orderJPARepository.deleteAll();
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

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? order.user.id.eq(userId) : null;
    }
}