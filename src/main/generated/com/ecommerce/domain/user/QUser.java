package com.ecommerce.domain.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 1753703935L;

    public static final QUser user = new QUser("user");

    public final NumberPath<java.math.BigDecimal> balance = createNumber("balance", java.math.BigDecimal.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    public final ListPath<com.ecommerce.domain.order.Order, com.ecommerce.domain.order.QOrder> orders = this.<com.ecommerce.domain.order.Order, com.ecommerce.domain.order.QOrder>createList("orders", com.ecommerce.domain.order.Order.class, com.ecommerce.domain.order.QOrder.class, PathInits.DIRECT2);

    public final ListPath<com.ecommerce.domain.usercoupon.UserCoupon, com.ecommerce.domain.usercoupon.QUserCoupon> userCoupons = this.<com.ecommerce.domain.usercoupon.UserCoupon, com.ecommerce.domain.usercoupon.QUserCoupon>createList("userCoupons", com.ecommerce.domain.usercoupon.UserCoupon.class, com.ecommerce.domain.usercoupon.QUserCoupon.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

