-- =============================================
-- 二手商品交易平台数据库表结构
-- 数据库名: es
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS es DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE es;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `messages`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone_number` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `credit_score` INT DEFAULT 100 COMMENT '信用分（0-100）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`),
    INDEX `idx_phone` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 商品分类表
-- =============================================
CREATE TABLE `categories` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '分类图标',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- =============================================
-- 商品表
-- =============================================
CREATE TABLE `products` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '商品价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
    `image_url` VARCHAR(255) DEFAULT NULL COMMENT '主图URL',
    `category_id` INT DEFAULT NULL COMMENT '分类ID',
    `seller_id` INT NOT NULL COMMENT '卖家ID',
    `condition_level` TINYINT DEFAULT 9 COMMENT '成色等级（1-10）',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '发布地点',
    `view_count` INT DEFAULT 0 COMMENT '浏览次数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-在售，2-已售',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`seller_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_seller` (`seller_id`),
    INDEX `idx_category` (`category_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =============================================
-- 商品图片表（支持多图）
-- =============================================
CREATE TABLE `product_images` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '图片ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `image_url` VARCHAR(255) NOT NULL COMMENT '图片URL',
    `sort_order` INT DEFAULT 0 COMMENT '排序顺序',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    INDEX `idx_product` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片表';

-- =============================================
-- 订单表
-- =============================================
CREATE TABLE `orders` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `buyer_id` INT NOT NULL COMMENT '买家ID',
    `seller_id` INT NOT NULL COMMENT '卖家ID',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '成交价格',
    `status` ENUM('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING' COMMENT '订单状态',
    `shipping_address` VARCHAR(255) DEFAULT NULL COMMENT '收货地址',
    `shipping_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `shipping_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `tracking_number` VARCHAR(50) DEFAULT NULL COMMENT '物流单号',
    `tracking_company` VARCHAR(50) DEFAULT NULL COMMENT '物流公司',
    `payment_time` TIMESTAMP NULL COMMENT '支付时间',
    `ship_time` TIMESTAMP NULL COMMENT '发货时间',
    `receive_time` TIMESTAMP NULL COMMENT '收货时间',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`buyer_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`seller_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_order_no` (`order_no`),
    INDEX `idx_buyer` (`buyer_id`),
    INDEX `idx_seller` (`seller_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 评价表
-- =============================================
CREATE TABLE `reviews` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `order_id` INT DEFAULT NULL COMMENT '订单ID',
    `user_id` INT NOT NULL COMMENT '评价用户ID',
    `rating` INT NOT NULL CHECK (`rating` BETWEEN 1 AND 5) COMMENT '评分（1-5星）',
    `comment` TEXT COMMENT '评价内容',
    `images` TEXT COMMENT '评价图片（JSON数组）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE SET NULL,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    INDEX `idx_product` (`product_id`),
    INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- =============================================
-- 消息表（聊天记录）
-- =============================================
CREATE TABLE `messages` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    `sender_id` INT NOT NULL COMMENT '发送者ID',
    `receiver_id` INT NOT NULL COMMENT '接收者ID',
    `product_id` INT DEFAULT NULL COMMENT '关联商品ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `message_type` TINYINT DEFAULT 1 COMMENT '消息类型：1-文字，2-图片，3-商品卡片',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE SET NULL,
    INDEX `idx_sender` (`sender_id`),
    INDEX `idx_receiver` (`receiver_id`),
    INDEX `idx_conversation` (`sender_id`, `receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- =============================================
-- 收藏表
-- =============================================
CREATE TABLE `favorites` (
    `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =============================================
-- 初始化分类数据
-- =============================================
INSERT INTO `categories` (`name`, `icon`, `sort_order`) VALUES
('电子产品', '/static/images/category/electronics.png', 1),
('手机数码', '/static/images/category/phone.png', 2),
('家居家具', '/static/images/category/furniture.png', 3),
('服饰鞋包', '/static/images/category/clothing.png', 4),
('母婴用品', '/static/images/category/baby.png', 5),
('图书文具', '/static/images/category/books.png', 6),
('运动户外', '/static/images/category/sports.png', 7),
('其他', '/static/images/category/other.png', 8);

-- =============================================
-- 测试数据
-- =============================================
-- 插入测试用户
INSERT INTO `users` (`username`, `password`, `nickname`, `avatar`, `email`, `phone_number`, `credit_score`) VALUES
('test_user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKBg0PnmEQ4L6VLvlZ9wJlVLK4Hi', '测试用户1', '/static/images/avatar/default.png', 'user1@test.com', '13800138001', 100),
('test_user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKBg0PnmEQ4L6VLvlZ9wJlVLK4Hi', '测试用户2', '/static/images/avatar/default.png', 'user2@test.com', '13800138002', 95);

-- 插入测试商品
INSERT INTO `products` (`name`, `description`, `price`, `original_price`, `image_url`, `category_id`, `seller_id`, `condition_level`, `location`, `view_count`, `status`) VALUES
('iPhone 13 Pro 256G', '自用一年，成色很新，无磕碰，电池健康度92%', 4999.00, 8999.00, '/static/images/products/iphone13.jpg', 2, 1, 9, '北京市朝阳区', 128, 1),
('MacBook Pro 2021', 'M1芯片，16G内存，512G固态，带原装充电器', 8999.00, 14999.00, '/static/images/products/macbook.jpg', 1, 1, 8, '北京市海淀区', 256, 1),
('宜家书桌', '九成新书桌，1.2米宽，带抽屉，自提', 299.00, 599.00, '/static/images/products/desk.jpg', 3, 2, 9, '上海市浦东新区', 64, 1),
('Nike Air Max运动鞋', '42码，穿过几次，基本全新', 399.00, 899.00, '/static/images/products/nike.jpg', 4, 2, 9, '广州市天河区', 89, 1);
