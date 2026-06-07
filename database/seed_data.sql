-- 更丰富的商品测试数据
-- 使用在线图片服务来确保图片能够正常显示

USE es;

-- 清空现有商品数据（保留分类和用户）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM product_images;
DELETE FROM products;
SET FOREIGN_KEY_CHECKS = 1;

-- 重置自增ID
ALTER TABLE products AUTO_INCREMENT = 1;

-- 添加更多测试用户（使用正确的字段名）
INSERT INTO users (username, password, nickname, avatar, phone_number, email, created_at, updated_at)
SELECT 'seller2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '数码达人', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller2', '13800000002', 'seller2@test.com', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'seller2');

INSERT INTO users (username, password, nickname, avatar, phone_number, email, created_at, updated_at)
SELECT 'seller3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '书虫小张', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller3', '13800000003', 'seller3@test.com', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'seller3');

INSERT INTO users (username, password, nickname, avatar, phone_number, email, created_at, updated_at)
SELECT 'seller4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '运动健将', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller4', '13800000004', 'seller4@test.com', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'seller4');

INSERT INTO users (username, password, nickname, avatar, phone_number, email, created_at, updated_at)
SELECT 'seller5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '时尚潮人', 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller5', '13800000005', 'seller5@test.com', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'seller5');

-- 电子产品（分类1）- 使用正确的字段名 name 而不是 title, status用数字
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 1, 'iPhone 14 Pro Max 256G 暗紫色', '自用iPhone 14 Pro Max，购买于2023年3月，99新无任何划痕，电池健康96%，全原装无拆修，配件齐全带原装盒子', 6899.00, 9999.00, 'https://picsum.photos/seed/iphone14/400/400', 1, 156, NOW() - INTERVAL 2 DAY, NOW()),
(1, 1, 'AirPods Pro 2代 带MagSafe充电盒', '全新未拆封AirPods Pro第二代，支持主动降噪，原价1899，现1499转让', 1499.00, 1899.00, 'https://picsum.photos/seed/airpods/400/400', 1, 89, NOW() - INTERVAL 5 DAY, NOW()),
(2, 1, 'iPad Pro 11寸 2022款 WiFi版 128G', '几乎全新iPad Pro，购入半年，主要用来看视频，屏幕完美无划痕，配Apple Pencil一代', 4599.00, 6999.00, 'https://picsum.photos/seed/ipad/400/400', 1, 234, NOW() - INTERVAL 1 DAY, NOW()),
(2, 1, 'MacBook Air M2 午夜色 16G+512G', '2023年款MacBook Air M2，配置升级版16G内存+512G硬盘，电池循环次数仅58次', 8999.00, 12999.00, 'https://picsum.photos/seed/macbook/400/400', 1, 312, NOW() - INTERVAL 3 DAY, NOW()),
(1, 1, '索尼WH-1000XM5 旗舰降噪耳机', '世界顶级降噪耳机，仅使用过几次，几乎全新，原包装配件齐全', 2199.00, 2999.00, 'https://picsum.photos/seed/sony-headphone/400/400', 1, 78, NOW() - INTERVAL 7 DAY, NOW());

-- 手机通讯（分类2）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(3, 2, '华为Mate 60 Pro 雅川青 512G', '华为最新旗舰，稀缺货源，全新未激活，支持卫星通信', 7999.00, 8999.00, 'https://picsum.photos/seed/huawei/400/400', 1, 567, NOW() - INTERVAL 1 DAY, NOW()),
(3, 2, '小米14 Ultra 白色 16+512', '徕卡影像旗舰，购入一个月，95新，送原装保护壳和钢化膜', 5499.00, 6999.00, 'https://picsum.photos/seed/xiaomi/400/400', 1, 189, NOW() - INTERVAL 4 DAY, NOW()),
(4, 2, 'OPPO Find X7 Ultra 大漠银月', '影像旗舰手机，双潜望长焦，购入两个月，9成新', 4899.00, 6499.00, 'https://picsum.photos/seed/oppo/400/400', 1, 145, NOW() - INTERVAL 6 DAY, NOW()),
(4, 2, '三星Galaxy S24 Ultra 钛黑 256G', '安卓机皇，S Pen手写笔，2K屏幕，原装配件齐全', 7299.00, 9699.00, 'https://picsum.photos/seed/samsung/400/400', 1, 223, NOW() - INTERVAL 2 DAY, NOW()),
(5, 2, 'vivo X100 Pro 落日橙', '蔡司影像，天玑9300芯片，拍照神器，轻微使用痕迹', 3899.00, 5299.00, 'https://picsum.photos/seed/vivo/400/400', 1, 98, NOW() - INTERVAL 8 DAY, NOW());

-- 家具家电（分类3）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 3, '宜家马尔姆书桌 白色 140x65cm', '宜家经典书桌，搬家出，自提优先，桌面有轻微使用痕迹', 399.00, 999.00, 'https://picsum.photos/seed/desk/400/400', 1, 67, NOW() - INTERVAL 3 DAY, NOW()),
(2, 3, '人体工学椅 西昊M57 黑色', '久坐神器，原价1500+购入，使用一年，功能正常', 699.00, 1599.00, 'https://picsum.photos/seed/chair/400/400', 1, 123, NOW() - INTERVAL 5 DAY, NOW()),
(3, 3, '戴森V15吸尘器 国行正品', '戴森旗舰吸尘器，购入一年半，吸力依然强劲，配件齐全', 2999.00, 5990.00, 'https://picsum.photos/seed/dyson/400/400', 1, 89, NOW() - INTERVAL 2 DAY, NOW()),
(4, 3, '小米空气净化器4 Pro', '除甲醛神器，搬家闲置，滤芯刚换，效果很好', 599.00, 1499.00, 'https://picsum.photos/seed/airpurifier/400/400', 1, 56, NOW() - INTERVAL 9 DAY, NOW()),
(5, 3, '美的变频空调1.5匹 全新', '装修多买了一台，全新未拆封，有发票可延保', 2199.00, 3299.00, 'https://picsum.photos/seed/airconditioner/400/400', 1, 178, NOW() - INTERVAL 1 DAY, NOW());

-- 服饰鞋包（分类4）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 4, 'Nike Air Jordan 1 黑红脚趾 US10', '经典AJ1配色，穿过3次，鞋底干净，原盒带鞋撑', 1299.00, 1699.00, 'https://picsum.photos/seed/jordan/400/400', 1, 234, NOW() - INTERVAL 2 DAY, NOW()),
(2, 4, 'Lululemon Define外套 黑色 M码', '瑜伽外套，买大了穿了一次，吊牌还在，几乎全新', 599.00, 1080.00, 'https://picsum.photos/seed/lululemon/400/400', 1, 156, NOW() - INTERVAL 4 DAY, NOW()),
(3, 4, 'Coach托特包 黑色皮质 大号', '经典通勤包，购入一年，有轻微使用痕迹，容量超大', 899.00, 2800.00, 'https://picsum.photos/seed/coach/400/400', 1, 89, NOW() - INTERVAL 6 DAY, NOW()),
(4, 4, 'Adidas NMD R1 黑白 US9.5', '休闲跑鞋，穿过几次，9成新，舒适度很高', 399.00, 1099.00, 'https://picsum.photos/seed/nmd/400/400', 1, 67, NOW() - INTERVAL 8 DAY, NOW()),
(5, 4, 'Champion卫衣 灰色刺绣款 L码', '美版Champion，面料厚实，洗过两次不缩水', 199.00, 599.00, 'https://picsum.photos/seed/champion/400/400', 1, 45, NOW() - INTERVAL 10 DAY, NOW());

-- 图书教材（分类5）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 5, '考研英语历年真题详解 2024版', '考研必备，做完了出，有少量笔记标注，不影响使用', 35.00, 89.00, 'https://picsum.photos/seed/book1/400/400', 1, 234, NOW() - INTERVAL 1 DAY, NOW()),
(2, 5, '深入理解计算机系统 第3版', 'CSAPP神书，CS必读经典，九成新无笔记', 89.00, 169.00, 'https://picsum.photos/seed/csapp/400/400', 1, 189, NOW() - INTERVAL 3 DAY, NOW()),
(3, 5, 'Python编程从入门到实践 第2版', '学Python首选，有少量笔记，送电子版代码', 39.00, 89.00, 'https://picsum.photos/seed/python/400/400', 1, 156, NOW() - INTERVAL 5 DAY, NOW()),
(4, 5, '高等数学同济第七版 上下册', '考研数学基础教材，有笔记和答案标注', 25.00, 78.00, 'https://picsum.photos/seed/math/400/400', 1, 312, NOW() - INTERVAL 2 DAY, NOW()),
(5, 5, '经济学原理 曼昆第8版 微观+宏观', '经典经济学入门，两本合售，九成新', 69.00, 178.00, 'https://picsum.photos/seed/economics/400/400', 1, 98, NOW() - INTERVAL 7 DAY, NOW());

-- 运动户外（分类6）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 6, '迪卡侬山地自行车 26寸 21速', '通勤代步神器，骑了半年，变速正常，需要自提', 599.00, 1299.00, 'https://picsum.photos/seed/bike/400/400', 1, 145, NOW() - INTERVAL 4 DAY, NOW()),
(2, 6, '斯伯丁官方比赛篮球 7号', '手感很好，室内外通用，有轻微使用痕迹', 129.00, 299.00, 'https://picsum.photos/seed/basketball/400/400', 1, 78, NOW() - INTERVAL 6 DAY, NOW()),
(3, 6, '李宁跑步机 家用折叠静音款', '疫情期间买的，使用次数不多，功能正常，需自提', 1299.00, 2999.00, 'https://picsum.photos/seed/treadmill/400/400', 1, 67, NOW() - INTERVAL 8 DAY, NOW()),
(4, 6, 'Wilson网球拍 初学者套装', '含2支球拍+3个球+拍包，打过几次，成色很新', 199.00, 399.00, 'https://picsum.photos/seed/tennis/400/400', 1, 45, NOW() - INTERVAL 10 DAY, NOW()),
(5, 6, '迪卡侬瑜伽垫 6mm加厚防滑', '几乎全新，只用过两次，送瑜伽带', 49.00, 129.00, 'https://picsum.photos/seed/yoga/400/400', 1, 34, NOW() - INTERVAL 12 DAY, NOW());

-- 母婴用品（分类7）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 7, 'Babycare婴儿推车 高景观双向', '宝宝大了用不上了，功能完好，可平躺可坐', 899.00, 2599.00, 'https://picsum.photos/seed/stroller/400/400', 1, 123, NOW() - INTERVAL 2 DAY, NOW()),
(2, 7, '好孩子儿童安全座椅 0-7岁', '使用一年，无磕碰，isofix接口，正反向安装', 699.00, 1999.00, 'https://picsum.photos/seed/carseat/400/400', 1, 89, NOW() - INTERVAL 5 DAY, NOW()),
(3, 7, '费雪宝宝爬行垫 XPE材质 2米', '环保无味，可折叠收纳，图案可爱', 129.00, 299.00, 'https://picsum.photos/seed/playmat/400/400', 1, 67, NOW() - INTERVAL 7 DAY, NOW()),
(4, 7, '小白熊温奶器 恒温调奶器', '夜奶神器，24小时恒温，省心省力', 89.00, 259.00, 'https://picsum.photos/seed/warmer/400/400', 1, 45, NOW() - INTERVAL 9 DAY, NOW()),
(5, 7, '乐高得宝系列 大颗粒积木桌', '含100+颗粒积木和游戏桌，开发智力好帮手', 299.00, 699.00, 'https://picsum.photos/seed/lego/400/400', 1, 78, NOW() - INTERVAL 11 DAY, NOW());

-- 其他（分类8）
INSERT INTO products (seller_id, category_id, name, description, price, original_price, image_url, status, view_count, created_at, updated_at) VALUES
(1, 8, 'Switch OLED版 日版 带健身环', '白色款Switch OLED，购入半年，轻度使用，送健身环和3款卡带', 2199.00, 3299.00, 'https://picsum.photos/seed/switch/400/400', 1, 345, NOW() - INTERVAL 1 DAY, NOW()),
(2, 8, '佳能EOS R7 相机套机', '微单新品，购入3个月，拍了2000张照片，原包装齐全', 9999.00, 12999.00, 'https://picsum.photos/seed/canon/400/400', 1, 234, NOW() - INTERVAL 3 DAY, NOW()),
(3, 8, '大疆Mini 3 Pro 带屏遥控器', '航拍无人机，续航46分钟，4K视频，带两块电池', 4999.00, 6788.00, 'https://picsum.photos/seed/dji/400/400', 1, 189, NOW() - INTERVAL 5 DAY, NOW()),
(4, 8, 'Kindle Paperwhite 5 电子书', '32G版本，有保护壳，屏幕完美无划痕', 699.00, 1199.00, 'https://picsum.photos/seed/kindle/400/400', 1, 156, NOW() - INTERVAL 7 DAY, NOW()),
(5, 8, '雷蛇黑寡妇V3机械键盘 绿轴', '游戏键盘，RGB灯效，打字手感极佳', 599.00, 1099.00, 'https://picsum.photos/seed/razer/400/400', 1, 123, NOW() - INTERVAL 9 DAY, NOW());

-- 更新用户头像为在线头像服务
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=testuser' WHERE id = 1;
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller1' WHERE id = 2;
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller2' WHERE id = 3;
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller3' WHERE id = 4;
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller4' WHERE id = 5;
UPDATE users SET avatar = 'https://api.dicebear.com/7.x/avataaars/svg?seed=seller5' WHERE id = 6;

SELECT '商品数据导入完成！共导入 40 件商品' AS result;
