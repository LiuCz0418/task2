/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : es

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 14/04/2026
 Description: 完整的数据库脚本，包含所有表结构、字段更新和初始数据
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 创建数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `es` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `es`;

-- ----------------------------
-- Table structure for users
-- 用户表（包含身份证号、角色等新增字段）
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密存储）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份证号',
  `credit_score` int DEFAULT 100 COMMENT '信用分（0-100）',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username` (`username`) USING BTREE,
  INDEX `idx_phone` (`phone_number`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for categories
-- 商品分类表
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for products
-- 商品表（包含审核状态、拒绝原因等新增字段）
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `original_price` decimal(10, 2) DEFAULT NULL COMMENT '原价',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主图URL',
  `category_id` int DEFAULT NULL COMMENT '分类ID',
  `seller_id` int NOT NULL COMMENT '卖家ID',
  `condition_level` tinyint DEFAULT 9 COMMENT '成色等级（1-10）',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发布地点',
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `status` tinyint DEFAULT 3 COMMENT '状态：0-下架，1-在售，2-已售，3-待审核，4-审核拒绝',
  `reject_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核拒绝原因',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_seller` (`seller_id`) USING BTREE,
  INDEX `idx_category` (`category_id`) USING BTREE,
  INDEX `idx_status` (`status`) USING BTREE,
  INDEX `idx_created` (`created_at`) USING BTREE,
  CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_products_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for product_images
-- 商品图片表（支持多图）
-- ----------------------------
DROP TABLE IF EXISTS `product_images`;
CREATE TABLE `product_images` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图片URL',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product` (`product_id`) USING BTREE,
  CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '商品图片表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for orders
-- 订单表
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单编号',
  `product_id` int NOT NULL COMMENT '商品ID',
  `buyer_id` int NOT NULL COMMENT '买家ID',
  `seller_id` int NOT NULL COMMENT '卖家ID',
  `price` decimal(10, 2) NOT NULL COMMENT '成交价格',
  `status` enum('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'COMPLETED', 'CANCELLED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING' COMMENT '订单状态',
  `shipping_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货地址',
  `shipping_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人姓名',
  `shipping_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '收货人电话',
  `tracking_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流单号',
  `tracking_company` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '物流公司',
  `payment_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `receive_time` timestamp NULL DEFAULT NULL COMMENT '收货时间',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_order_no` (`order_no`) USING BTREE,
  INDEX `idx_buyer` (`buyer_id`) USING BTREE,
  INDEX `idx_seller` (`seller_id`) USING BTREE,
  INDEX `idx_status` (`status`) USING BTREE,
  CONSTRAINT `fk_orders_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orders_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orders_seller` FOREIGN KEY (`seller_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for reviews
-- 评价表（包含多维度评分、审核状态等新增字段）
-- ----------------------------
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE `reviews` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `order_id` int DEFAULT NULL COMMENT '订单ID',
  `user_id` int NOT NULL COMMENT '评价用户ID',
  `rating` int NOT NULL COMMENT '评分（1-5星）',
  `comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '评价图片（JSON数组）',
  `service_score` int DEFAULT NULL COMMENT '服务质量评分（1-5）',
  `desc_score` int DEFAULT NULL COMMENT '描述准确性评分（1-5）',
  `ship_score` int DEFAULT NULL COMMENT '发货速度评分（1-5）',
  `audit_status` tinyint NOT NULL DEFAULT 1 COMMENT '审核状态：0-待审核，1-已通过，2-已拒绝',
  `audit_remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员审核备注（拒绝原因）',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product` (`product_id`) USING BTREE,
  INDEX `idx_user` (`user_id`) USING BTREE,
  INDEX `idx_reviews_audit_status` (`audit_status`, `created_at`) USING BTREE,
  CONSTRAINT `fk_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_rating` CHECK (`rating` BETWEEN 1 AND 5)
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '评价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for messages
-- 消息表（聊天记录）
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` int NOT NULL COMMENT '发送者ID',
  `receiver_id` int NOT NULL COMMENT '接收者ID',
  `product_id` int DEFAULT NULL COMMENT '关联商品ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `message_type` tinyint DEFAULT 1 COMMENT '消息类型：1-文字，2-图片，3-商品卡片',
  `is_read` tinyint DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sender` (`sender_id`) USING BTREE,
  INDEX `idx_receiver` (`receiver_id`) USING BTREE,
  INDEX `idx_conversation` (`sender_id`, `receiver_id`) USING BTREE,
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_messages_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for favorites
-- 收藏表
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` int NOT NULL COMMENT '用户ID',
  `product_id` int NOT NULL COMMENT '商品ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`),
  INDEX `idx_user_id` (`user_id`) USING BTREE,
  INDEX `idx_product_id` (`product_id`) USING BTREE,
  CONSTRAINT `fk_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_favorites_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- 初始化分类数据
-- ----------------------------
INSERT INTO `categories` (`id`, `name`, `icon`, `sort_order`, `created_at`) VALUES
(1, '电子产品', '/static/images/category/electronics.png', 1, NOW()),
(2, '手机数码', '/static/images/category/phone.png', 2, NOW()),
(3, '家居家具', '/static/images/category/furniture.png', 3, NOW()),
(4, '服饰鞋包', '/static/images/category/clothing.png', 4, NOW()),
(5, '图书文具', '/static/images/category/books.png', 5, NOW()),
(6, '运动户外', '/static/images/category/sports.png', 6, NOW()),
(7, '母婴用品', '/static/images/category/baby.png', 7, NOW()),
(8, '其他', '/static/images/category/other.png', 8, NOW());

-- ----------------------------
-- Records of users
-- 初始化管理员账户（密码为 123456）
-- ----------------------------
INSERT INTO `users` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `phone_number`, `id_card`, `credit_score`, `status`, `role`, `created_at`, `updated_at`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKBg0PnmEQ4L6VLvlZ9wJlVLK4Hi', '管理员', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin', 'admin@es.com', '13800000000', NULL, 100, 1, 'ADMIN', NOW(), NOW());

-- ----------------------------
-- 测试用户数据（可选）
-- 密码均为 123456
-- ----------------------------
INSERT INTO `users` (`id`, `username`, `password`, `nickname`, `avatar`, `email`, `phone_number`, `id_card`, `credit_score`, `status`, `role`, `created_at`, `updated_at`) VALUES
(2, 'test_user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKBg0PnmEQ4L6VLvlZ9wJlVLK4Hi', '测试用户1', 'https://api.dicebear.com/7.x/avataaars/svg?seed=testuser1', 'user1@test.com', '13800138001', NULL, 100, 1, 'USER', NOW(), NOW()),
(3, 'test_user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKBg0PnmEQ4L6VLvlZ9wJlVLK4Hi', '测试用户2', 'https://api.dicebear.com/7.x/avataaars/svg?seed=testuser2', 'user2@test.com', '13800138002', NULL, 95, 1, 'USER', NOW(), NOW());

-- ----------------------------
-- 测试商品数据（可选）
-- ----------------------------
INSERT INTO `products` (`id`, `name`, `description`, `price`, `original_price`, `image_url`, `category_id`, `seller_id`, `condition_level`, `location`, `view_count`, `status`, `reject_reason`, `created_at`, `updated_at`) VALUES
(1, 'iPhone 13 Pro 256G', '自用一年，成色很新，无磕碰，电池健康度92%', 4999.00, 8999.00, '/static/images/products/iphone13.jpg', 2, 1, 9, '北京市朝阳区', 128, 1, NULL, NOW(), NOW()),
(2, 'MacBook Pro 2021', 'M1芯片，16G内存，512G固态，带原装充电器', 8999.00, 14999.00, '/static/images/products/macbook.jpg', 1, 1, 8, '北京市海淀区', 256, 1, NULL, NOW(), NOW()),
(3, '宜家书桌', '九成新书桌，1.2米宽，带抽屉，自提', 299.00, 599.00, '/static/images/products/desk.jpg', 3, 2, 9, '上海市浦东新区', 64, 1, NULL, NOW(), NOW()),
(4, 'Nike Air Max运动鞋', '42码，穿过几次，基本全新', 399.00, 899.00, '/static/images/products/nike.jpg', 4, 2, 9, '广州市天河区', 89, 1, NULL, NOW(), NOW());

-- ----------------------------
-- 测试评价数据（可选）
-- ----------------------------
INSERT INTO `reviews` (`id`, `product_id`, `order_id`, `user_id`, `rating`, `comment`, `images`, `service_score`, `desc_score`, `ship_score`, `audit_status`, `audit_remark`, `created_at`) VALUES
(1, 2, NULL, 2, 5, '卖家服务非常好，商品与描述完全一致，发货速度很快！', NULL, 5, 5, 5, 1, NULL, NOW()),
(2, 1, NULL, 2, 4, '商品成色不错，卖家很耐心，就是发货略慢了一点。', NULL, 5, 4, 3, 1, NULL, NOW()),
(3, 3, NULL, 1, 5, '家具质量很好，卖家打包很仔细，整体很满意！', NULL, 5, 5, 4, 1, NULL, NOW());

SET FOREIGN_KEY_CHECKS = 1;

-- ======================================================
-- 数据库升级脚本说明
-- ======================================================
-- 
-- 如果您已有旧版本的数据库，请按以下顺序执行升级脚本：
-- 
-- 1. 添加身份证号字段：
--    ALTER TABLE `users` ADD COLUMN `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号' AFTER `phone_number`;
-- 
-- 2. 添加用户角色字段：
--    ALTER TABLE `users` ADD COLUMN `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN' AFTER `status`;
-- 
-- 3. 添加商品审核字段：
--    ALTER TABLE `products` ADD COLUMN `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '审核拒绝原因' AFTER `status`;
--    -- 将默认状态改为待审核
--    ALTER TABLE `products` MODIFY `status` TINYINT DEFAULT 3 COMMENT '状态：0-下架，1-在售，2-已售，3-待审核，4-审核拒绝';
-- 
-- 4. 添加多维度评分字段：
--    ALTER TABLE `reviews` ADD COLUMN `service_score` INT DEFAULT NULL COMMENT '服务质量评分(1-5)';
--    ALTER TABLE `reviews` ADD COLUMN `desc_score` INT DEFAULT NULL COMMENT '描述准确性评分(1-5)';
--    ALTER TABLE `reviews` ADD COLUMN `ship_score` INT DEFAULT NULL COMMENT '发货速度评分(1-5)';
-- 
-- 5. 添加评价审核字段：
--    ALTER TABLE `reviews` ADD COLUMN `audit_status` TINYINT NOT NULL DEFAULT 1 COMMENT '审核状态: 0=待审核, 1=已通过, 2=已拒绝';
--    ALTER TABLE `reviews` ADD COLUMN `audit_remark` VARCHAR(255) DEFAULT NULL COMMENT '管理员审核备注（拒绝原因）';
--    CREATE INDEX `idx_reviews_audit_status` ON `reviews` (`audit_status`, `created_at`);
-- 
-- 6. 创建管理员账户：
--    UPDATE `users` SET `role` = 'ADMIN' WHERE `id` = 1;
-- 
-- ======================================================
