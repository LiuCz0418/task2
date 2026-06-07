-- =============================================
-- 二手交易平台 v2 数据库升级脚本
-- 新增：物流追踪与多维度评价支持
-- 执行前请确保已运行 schema.sql
-- =============================================

USE es;

-- =============================================
-- 扩展评价表：新增多维度评分字段
-- 使用 ALTER TABLE 不破坏现有数据
-- =============================================
-- 检查并添加 service_score 列
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reviews' AND COLUMN_NAME = 'service_score');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `reviews` ADD COLUMN `service_score` INT DEFAULT NULL COMMENT \'服务质量评分(1-5)\'',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 检查并添加 desc_score 列
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reviews' AND COLUMN_NAME = 'desc_score');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `reviews` ADD COLUMN `desc_score` INT DEFAULT NULL COMMENT \'描述准确性评分(1-5)\'',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 检查并添加 ship_score 列
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'reviews' AND COLUMN_NAME = 'ship_score');
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `reviews` ADD COLUMN `ship_score` INT DEFAULT NULL COMMENT \'发货速度评分(1-5)\'',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =============================================
-- 更新现有用户信用分（初始化为有意义的分值）
-- =============================================
UPDATE `users` SET `credit_score` = 98 WHERE `id` = 1;
UPDATE `users` SET `credit_score` = 87 WHERE `id` = 2;
UPDATE `users` SET `credit_score` = 92 WHERE `id` = 3;
UPDATE `users` SET `credit_score` = 76 WHERE `id` = 4;
UPDATE `users` SET `credit_score` = 85 WHERE `id` = 5;
UPDATE `users` SET `credit_score` = 94 WHERE `id` = 6;
UPDATE `users` SET `credit_score` = 68 WHERE `id` = 7;
UPDATE `users` SET `credit_score` = 81 WHERE `id` = 8;
UPDATE `users` SET `credit_score` = 90 WHERE `id` = 9;
UPDATE `users` SET `credit_score` = 73 WHERE `id` = 10;

-- 对10以上的用户随机分配信用分（60-100区间）
UPDATE `users` SET `credit_score` = FLOOR(60 + RAND() * 40) WHERE `id` > 10;

-- =============================================
-- 插入初始评价数据（用于展示）
-- 仅在评价表为空或数据较少时插入
-- =============================================

-- 为商品2插入评价（卖家id=1的商品）
INSERT INTO `reviews` (`product_id`, `order_id`, `user_id`, `rating`, `comment`, `service_score`, `desc_score`, `ship_score`, `created_at`)
SELECT 2, NULL, 2, 5, '卖家服务非常好，商品与描述完全一致，发货速度很快！', 5, 5, 5, NOW() - INTERVAL 10 DAY
WHERE NOT EXISTS (SELECT 1 FROM `reviews` WHERE `product_id` = 2 AND `user_id` = 2 LIMIT 1);

INSERT INTO `reviews` (`product_id`, `order_id`, `user_id`, `rating`, `comment`, `service_score`, `desc_score`, `ship_score`, `created_at`)
SELECT 1, NULL, 2, 4, '商品成色不错，卖家很耐心，就是发货略慢了一点。', 5, 4, 3, NOW() - INTERVAL 7 DAY
WHERE NOT EXISTS (SELECT 1 FROM `reviews` WHERE `product_id` = 1 AND `user_id` = 2 LIMIT 1);

-- 为商品3插入评价（卖家id=2的商品）
INSERT INTO `reviews` (`product_id`, `order_id`, `user_id`, `rating`, `comment`, `service_score`, `desc_score`, `ship_score`, `created_at`)
SELECT 3, NULL, 1, 5, '家具质量很好，卖家打包很仔细，整体很满意！', 5, 5, 4, NOW() - INTERVAL 5 DAY
WHERE NOT EXISTS (SELECT 1 FROM `reviews` WHERE `product_id` = 3 AND `user_id` = 1 LIMIT 1);

INSERT INTO `reviews` (`product_id`, `order_id`, `user_id`, `rating`, `comment`, `service_score`, `desc_score`, `ship_score`, `created_at`)
SELECT 3, NULL, 3, 4, '商品基本符合描述，卖家沟通顺畅，推荐！', 4, 4, 5, NOW() - INTERVAL 3 DAY
WHERE NOT EXISTS (SELECT 1 FROM `reviews` WHERE `product_id` = 3 AND `user_id` = 3 LIMIT 1);

-- 为商品4插入评价（卖家id=2的商品）
INSERT INTO `reviews` (`product_id`, `order_id`, `user_id`, `rating`, `comment`, `service_score`, `desc_score`, `ship_score`, `created_at`)
SELECT 4, NULL, 1, 5, '鞋子成色超好，卖家描述非常准确，快递很快！', 5, 5, 5, NOW() - INTERVAL 2 DAY
WHERE NOT EXISTS (SELECT 1 FROM `reviews` WHERE `product_id` = 4 AND `user_id` = 1 LIMIT 1);
