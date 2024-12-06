USE shopapp;

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    fullname VARCHAR(100) DEFAULT '',
    username VARCHAR(100) NOT NULL,
    phone_number VARCHAR(10) NOT NULL,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);
ALTER TABLE users ADD COLUMN role_id INT;

CREATE TABLE roles(
    id INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL 
);
INSERT INTO roles (id, name) VALUES
(0, 'ADMIN'),
(1, 'USER');
ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles (id);

CREATE TABLE tokens(
    id int PRIMARY KEY AUTO_INCREMENT,
    token varchar(255) UNIQUE NOT NULL,
    token_type varchar(50) NOT NULL,
    expiration_date DATETIME,
    revoked tinyint(1) NOT NULL,
    expired tinyint(1) NOT NULL,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'social network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email ',
    name VARCHAR(100) NOT NULL COMMENT 'UserNAME',
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name varchar(100) NOT NULL DEFAULT '' COMMENT 'CATEGORY NAME',
);


CREATE TABLE products (
id INT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(350) COMMENT 'PRODUCT NAME',
price FLOAT NOT NULL CHECK (price >= 0),
thumbnail VARCHAR(300) DEFAULT '',
description LONGTEXT,
created_at DATETIME,
updated_at DATETIME,
category_id INT,
FOREIGN KEY (category_id) REFERENCES categories (id)
);

CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    CONSTRAINT fk_product_images_product_id 
        FOREIGN KEY (product_id) 
        REFERENCES products (id) ON DELETE CASCADE,
    image_url VARCHAR(300)
);


CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id int,
    FOREIGN KEY (user_id) REFERENCES users(id),
    fullname VARCHAR(100) DEFAULT '',
    email VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    note VARCHAR(100) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    total_money FLOAT CHECK(total_money >= 0)
);

ALTER TABLE orders ADD COLUMN `shipping_method` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `shipping_address` VARCHAR(200);
ALTER TABLE orders ADD COLUMN `shipping_date` DATE;
ALTER TABLE orders ADD COLUMN `tracking_number` VARCHAR(100);
ALTER TABLE orders ADD COLUMN `payment_method` VARCHAR(100);

ALTER TABLE orders ADD COLUMN active TINYINT(1);

ALTER TABLE orders 
MODIFY COLUMN status ENUM('pending', 'processing', 'shipped', 'delivered', 'cancelled') 
COMMENT 'STATUS OF ORDER';

CREATE TABLE order_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders (id),
    product_id INT,
    FOREIGN KEY (product_id) REFERENCES products (id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(total_money >= 0),
    color VARCHAR(20) DEFAULT ''
);

ALTER TABLE orders
MODIFY COLUMN total_money FLOAT DEFAULT 0,
MODIFY COLUMN shipping_method VARCHAR(100),
MODIFY COLUMN tracking_number VARCHAR(100),
MODIFY COLUMN payment_method VARCHAR(100),
MODIFY COLUMN shipping_address VARCHAR(200) DEFAULT '',
MODIFY COLUMN shipping_date DATE,
MODIFY COLUMN active TINYINT(1) DEFAULT 1;

UPDATE orders
SET total_money = 0,
    shipping_method = '',
    tracking_number = '',
    payment_method = '',
    shipping_address = '',    
    active = 1
WHERE total_money IS NULL
   OR shipping_method IS NULL
   OR tracking_number IS NULL
   OR payment_method IS NULL
   OR shipping_address IS NULL
   OR shipping_date IS NULL
   OR active IS NULL;

SET SQL_SAFE_UPDATES = 0;
