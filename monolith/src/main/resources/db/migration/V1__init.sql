CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    parent_id BIGINT REFERENCES category(id),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INTEGER NOT NULL,
    brand VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    category_id BIGINT REFERENCES category(id),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE cart (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE cart_item (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES cart(id),
    product_id BIGINT REFERENCES product(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    status VARCHAR(255) NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES product(id),
    quantity INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    transaction_ref VARCHAR(255),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE shipping_info (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE REFERENCES orders(id),
    address VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    tracking_number VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP
);

CREATE TABLE review (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    rating INTEGER NOT NULL,
    comment VARCHAR(255),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    modified_at TIMESTAMP,
    UNIQUE(user_id, product_id)
);
