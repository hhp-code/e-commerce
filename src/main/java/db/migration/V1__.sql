CREATE TABLE coupons
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    code            VARCHAR(255) NULL,
    discount_amount DECIMAL NULL,
    discount_type   SMALLINT NULL,
    user_id         BIGINT NULL,
    quantity        INT NULL,
    valid_from      datetime NULL,
    valid_to        datetime NULL,
    is_active       BIT(1) NOT NULL,
    CONSTRAINT pk_coupons PRIMARY KEY (id)
);

CREATE TABLE order_order_items
(
    order_id        BIGINT NOT NULL,
    order_items     INT NULL,
    order_items_key BIGINT NOT NULL,
    CONSTRAINT pk_order_orderitems PRIMARY KEY (order_id, order_items_key)
);

CREATE TABLE orders
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    order_date    datetime NULL,
    regular_price DECIMAL NULL,
    sale_price    DECIMAL NULL,
    selling_price DECIMAL NULL,
    is_deleted    BIT(1) NOT NULL,
    order_status  VARCHAR(255) NULL,
    user_id       BIGINT NULL,
    coupon_id     BIGINT NULL,
    deleted_at    datetime NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

CREATE TABLE products
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    name         VARCHAR(255) NULL,
    price        DECIMAL NULL,
    stock        INT NULL,
    last_updated datetime NULL,
    is_deleted   BIT(1) NOT NULL,
    deleted_at   datetime NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    username   VARCHAR(255) NULL,
    point      DECIMAL NULL,
    is_deleted BIT(1) NOT NULL,
    deleted_at datetime NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE INDEX idx_name ON products (name);

CREATE INDEX idx_order_status ON orders (order_status);

CREATE INDEX idx_username ON users (username);

ALTER TABLE coupons
    ADD CONSTRAINT FK_COUPONS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_COUPON FOREIGN KEY (coupon_id) REFERENCES coupons (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE order_order_items
    ADD CONSTRAINT fk_order_orderitems_on_order FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_order_items
    ADD CONSTRAINT fk_order_orderitems_on_product FOREIGN KEY (order_items_key) REFERENCES products (id);