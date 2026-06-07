-- 更新商品图片为与名称匹配的真实图片
USE es;

-- 电子产品（分类1）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&h=400&fit=crop' WHERE id = 1; -- iPhone 14 Pro Max
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1606220588913-b3aacb4d2f46?w=400&h=400&fit=crop' WHERE id = 2; -- AirPods Pro
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop' WHERE id = 3; -- iPad Pro
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&h=400&fit=crop' WHERE id = 4; -- MacBook Air
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=400&h=400&fit=crop' WHERE id = 5; -- 索尼耳机

-- 手机通讯（分类2）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400&h=400&fit=crop' WHERE id = 6; -- 华为Mate 60
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400&h=400&fit=crop' WHERE id = 7; -- 小米14
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=400&h=400&fit=crop' WHERE id = 8; -- OPPO
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=400&h=400&fit=crop' WHERE id = 9; -- 三星Galaxy
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=400&fit=crop' WHERE id = 10; -- vivo

-- 家具家电（分类3）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400&h=400&fit=crop' WHERE id = 11; -- 书桌
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400&h=400&fit=crop' WHERE id = 12; -- 人体工学椅
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1558317374-067fb5f30001?w=400&h=400&fit=crop' WHERE id = 13; -- 戴森吸尘器
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=400&h=400&fit=crop' WHERE id = 14; -- 空气净化器
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1631545806609-44a8673f10fd?w=400&h=400&fit=crop' WHERE id = 15; -- 空调

-- 服饰鞋包（分类4）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1552346154-21d32810aba3?w=400&h=400&fit=crop' WHERE id = 16; -- Nike Jordan
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=400&h=400&fit=crop' WHERE id = 17; -- Lululemon外套
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=400&h=400&fit=crop' WHERE id = 18; -- Coach包
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400&h=400&fit=crop' WHERE id = 19; -- Adidas NMD
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=400&h=400&fit=crop' WHERE id = 20; -- Champion卫衣

-- 图书教材（分类5）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400&h=400&fit=crop' WHERE id = 21; -- 考研英语
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400&h=400&fit=crop' WHERE id = 22; -- CSAPP
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=400&fit=crop' WHERE id = 23; -- Python编程
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1509228468518-180dd4864904?w=400&h=400&fit=crop' WHERE id = 24; -- 高等数学
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1554415707-6e8cfc93fe23?w=400&h=400&fit=crop' WHERE id = 25; -- 经济学原理

-- 运动户外（分类6）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1485965120184-e220f721d03e?w=400&h=400&fit=crop' WHERE id = 26; -- 山地自行车
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1519861531473-9200262188bf?w=400&h=400&fit=crop' WHERE id = 27; -- 篮球
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1540497077202-7c8a3999166f?w=400&h=400&fit=crop' WHERE id = 28; -- 跑步机
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1617083934555-ac7b4d0c8be9?w=400&h=400&fit=crop' WHERE id = 29; -- 网球拍
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=400&h=400&fit=crop' WHERE id = 30; -- 瑜伽垫

-- 母婴用品（分类7）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1522771930-78848d9293e8?w=400&h=400&fit=crop' WHERE id = 31; -- 婴儿推车
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1555252333-9f8e92e65df9?w=400&h=400&fit=crop' WHERE id = 32; -- 儿童安全座椅
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=400&h=400&fit=crop' WHERE id = 33; -- 爬行垫
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1584839404042-8bc86d62c7be?w=400&h=400&fit=crop' WHERE id = 34; -- 温奶器/奶瓶
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=400&h=400&fit=crop' WHERE id = 35; -- 乐高积木

-- 其他（分类8）
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1578303512597-81e6cc155b3e?w=400&h=400&fit=crop' WHERE id = 36; -- Switch
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&h=400&fit=crop' WHERE id = 37; -- 佳能相机
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1473968512647-3e447244af8f?w=400&h=400&fit=crop' WHERE id = 38; -- 大疆无人机
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1592434134753-a70baf7979d5?w=400&h=400&fit=crop' WHERE id = 39; -- Kindle
UPDATE products SET image_url = 'https://images.unsplash.com/photo-1618384887929-16ec33fab9ef?w=400&h=400&fit=crop' WHERE id = 40; -- 机械键盘

SELECT '图片更新完成！' AS result;
