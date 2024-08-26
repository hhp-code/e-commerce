package com.ecommerce.infra.product;

import com.ecommerce.domain.product.service.repository.ProductRepository;
import com.ecommerce.infra.order.entity.QOrderEntity;
import com.ecommerce.infra.order.entity.QOrderItemEntity;
import com.ecommerce.infra.product.entity.ProductEntity;
import com.ecommerce.infra.product.entity.QProductEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJPARepository productJPARepository;
    private final JPAQueryFactory queryFactory;
    private final QOrderEntity order = QOrderEntity.orderEntity;
    private final QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
    private final QProductEntity product = QProductEntity.productEntity;


    public ProductRepositoryImpl(ProductJPARepository productJPARepository, EntityManager entityManager) {
        this.productJPARepository = productJPARepository;
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<ProductEntity> getPopularProducts() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
//
//        NumberExpression<Integer> quantitySum = new CaseBuilder()
//                .when(order.orderItems.containsKey(product))
//                .then(order.orderItems.get(product).quantity.sum())
//                .otherwise(0);
//
//        return queryFactory
//                .select(product)
//                .from(order)
//                .join(product)
//                .on(order.orderItems.containsKey(product))
//                .where(order.orderDate.after(threeDaysAgo)
//                        .and(order.orderStatus.eq(OrderStatus.ORDERED)))
//                .groupBy(product)
//                .orderBy(quantitySum.desc())
//                .limit(5)
//                .fetch();
        return null;
    }

    @Override
    public List<ProductEntity> getProducts() {
        return queryFactory
                .selectFrom(product)
                .orderBy(product.id.asc())
                .fetch();
    }

    @Override
    public Optional<ProductEntity> getProduct(Long productId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(product)
                .where(product.id.eq(productId))
                .fetchOne());
    }

    @Override
    public Optional<ProductEntity> save(ProductEntity product) {
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
    public void saveAll(List<ProductEntity> products) {
        productJPARepository.saveAll(products);
    }

    @Override
    public List<ProductEntity> getAll() {
        return productJPARepository.findAll();

    }

    @Override
    public Optional<ProductEntity> saveProduct(ProductEntity testProduct) {
        return Optional.of(productJPARepository.save(testProduct));
    }


}