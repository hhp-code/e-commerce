package com.ecommerce.domain.product.repository;

import com.ecommerce.domain.order.QOrder;
import com.ecommerce.domain.product.QProduct;
import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.domain.product.Product;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJPARepository productJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;
    private final QOrder order = QOrder.order;

    public ProductRepositoryImpl(ProductJPARepository productJPARepository, EntityManager entityManager) {
        this.productJPARepository = productJPARepository;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Product> getPopularProducts() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);

        // 1. 커버링 인덱스를 사용하여 인기 상품 ID와 판매량 조회
        List<Tuple> popularProductInfo = queryFactory
                .select(order.orderItems.any().product.id, order.orderItems.any().quantity.sum())
                .from(order)
                .where(order.orderDate.after(threeDaysAgo))
                .groupBy(order.orderItems.any().product.id)
                .orderBy(order.orderItems.any().quantity.sum().desc())
                .limit(5)
                .fetch();

        List<Long> productIds = popularProductInfo.stream()
                .map(tuple -> tuple.get(0, Long.class))
                .collect(Collectors.toList());

        // 2. 조회된 ID를 사용하여 실제 상품 정보 조회
        List<Product> products = queryFactory
                .selectFrom(product)
                .where(product.id.in(productIds))
                .fetch();

        // 3. 판매량에 따라 정렬
        Map<Long, Long> quantityMap = popularProductInfo.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Long.class),
                        tuple -> tuple.get(1, Long.class)
                ));

        products.sort((p1, p2) -> quantityMap.get(p2.getId()).compareTo(quantityMap.get(p1.getId())));

        return products;
    }

    @Override
    public List<Product> getProducts() {
        return queryFactory
                .selectFrom(product)
                .orderBy(product.id.asc())
                .fetch();
    }

    @Override
    public Optional<Product> getProduct(Long productId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(product)
                .where(product.id.eq(productId))
                .fetchOne());
    }

    @Override
    @Transactional
    public Optional<Product> save(Product product) {
        return Optional.of(productJPARepository.save(product));
    }

    @Override
    @Transactional
    public void deleteAll() {
        queryFactory
                .delete(product)
                .execute();
    }

    @Override
    @Transactional
    public int decreaseAvailableStock(Long id, int orderedQuantity) {
        long updatedCount = queryFactory
                .update(product)
                .set(product.availableStock, product.availableStock.subtract(orderedQuantity))
                .where(product.id.eq(id).and(product.availableStock.goe(orderedQuantity)))
                .execute();
        return (int) updatedCount;
    }

    @Override
    public int increaseReservedStock(Long productId, int quantity) {
        long updatedCount = queryFactory
                .update(product)
                .set(product.reservedStock, product.reservedStock.add(quantity))
                .where(product.id.eq(productId).and(product.availableStock.goe(quantity)))
                .execute();
        return (int) updatedCount;
    }

    @Override
    public int decreaseReservedStock(Long id, Integer quantity) {
        long updatedCount = queryFactory
                .update(product)
                .set(product.reservedStock, product.reservedStock.subtract(quantity))
                .where(product.id.eq(id).and(product.reservedStock.goe(quantity)))
                .execute();
        return (int) updatedCount;
    }

    @Override
    public int increaseAvailableStock(Long id, Integer quantity) {
        long updatedCount = queryFactory
                .update(product)
                .set(product.availableStock, product.availableStock.add(quantity))
                .where(product.id.eq(id))
                .execute();
        return (int) updatedCount;
    }


}