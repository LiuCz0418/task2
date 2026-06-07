-- =============================================
-- 更新商品图片为本地上传的图片
-- 解决外部图片 403 无法加载的问题
-- =============================================

USE es;

-- 电子产品分类商品
UPDATE products SET image_url = '/uploads/electronics_computer.jpg' WHERE id = 1; -- iPhone
UPDATE products SET image_url = '/uploads/electronics_headphone.jpg' WHERE id = 2; -- AirPods
UPDATE products SET image_url = '/uploads/electronics_tablet.jpg' WHERE id = 3; -- iPad
UPDATE products SET image_url = '/uploads/electronics_computer.jpg' WHERE id = 4; -- MacBook
UPDATE products SET image_url = '/uploads/electronics_camera.jpg' WHERE id = 5; -- 索尼相机

-- 手机数码分类商品
UPDATE products SET image_url = '/uploads/phone_huawei.jpg' WHERE id = 6; -- 华为
UPDATE products SET image_url = '/uploads/phone_xiaomi.jpg' WHERE id = 7; -- 小米
UPDATE products SET image_url = '/uploads/phone_android.jpg' WHERE id = 8; -- OPPO
UPDATE products SET image_url = '/uploads/phone_samsung.jpg' WHERE id = 9; -- 三星
UPDATE products SET image_url = '/uploads/phone_iphone.jpg' WHERE id = 10; -- vivo

-- 家居家具分类商品
UPDATE products SET image_url = '/uploads/furniture_desk.jpg' WHERE id = 11; -- 书桌
UPDATE products SET image_url = '/uploads/furniture_chair.jpg' WHERE id = 12; -- 椅子
UPDATE products SET image_url = '/uploads/furniture_bookshelf.jpg' WHERE id = 13; -- 吸尘器
UPDATE products SET image_url = '/uploads/furniture_sofa.jpg' WHERE id = 14; -- 空气净化器
UPDATE products SET image_url = '/uploads/furniture_bed.jpg' WHERE id = 15; -- 空调

-- 服饰鞋包分类商品
UPDATE products SET image_url = '/uploads/clothing_jacket.jpg' WHERE id = 16; -- 羽绒服
UPDATE products SET image_url = '/uploads/clothing_bag.jpg' WHERE id = 17; -- 包包
UPDATE products SET image_url = '/uploads/clothing_jacket.jpg' WHERE id = 18; -- 外套
UPDATE products SET image_url = '/uploads/clothing_bag.jpg' WHERE id = 19; -- 背包
UPDATE products SET image_url = '/uploads/clothing_shoes.jpg' WHERE id = 20; -- 夹克

-- 母婴用品分类商品
UPDATE products SET image_url = '/uploads/baby_stroller.jpg' WHERE id = 21; -- 婴儿车
UPDATE products SET image_url = '/uploads/baby_toys.jpg' WHERE id = 22; -- 玩具
UPDATE products SET image_url = '/uploads/baby_clothes.jpg' WHERE id = 23; -- 儿童服装
UPDATE products SET image_url = '/uploads/baby_bottle.jpg' WHERE id = 24; -- 奶瓶
UPDATE products SET image_url = '/uploads/baby_crib.jpg' WHERE id = 25; -- 童鞋

-- 图书文具分类商品
UPDATE products SET image_url = '/uploads/books_book.jpg' WHERE id = 26; -- 书籍
UPDATE products SET image_url = '/uploads/books_notebook.jpg' WHERE id = 27; -- 笔记本
UPDATE products SET image_url = '/uploads/books_textbook.jpg' WHERE id = 28; -- 小说
UPDATE products SET image_url = '/uploads/books_pen.jpg' WHERE id = 29; -- 文具套装
UPDATE products SET image_url = '/uploads/books_magazine.jpg' WHERE id = 30; -- 教材

-- 运动户外分类商品
UPDATE products SET image_url = '/uploads/sports_bicycle.jpg' WHERE id = 31; -- 自行车
UPDATE products SET image_url = '/uploads/sports_basketball.jpg' WHERE id = 32; -- 篮球
UPDATE products SET image_url = '/uploads/sports_skateboard.jpg' WHERE id = 33; -- 滑板
UPDATE products SET image_url = '/uploads/sports_football.jpg' WHERE id = 34; -- 足球
UPDATE products SET image_url = '/uploads/sports_tent.jpg' WHERE id = 35; -- 帐篷

-- 其他分类商品
UPDATE products SET image_url = '/uploads/other_guitar.jpg' WHERE id = 36; -- 吉他
UPDATE products SET image_url = '/uploads/other_art.jpg' WHERE id = 37; -- 艺术品
UPDATE products SET image_url = '/uploads/other_camera.jpg' WHERE id = 38; -- 乐器
UPDATE products SET image_url = '/uploads/other_watch.jpg' WHERE id = 39; -- 装饰画
UPDATE products SET image_url = '/uploads/other_plant.jpg' WHERE id = 40; -- 钢琴

-- 显示更新结果
SELECT id, name, image_url, category_id FROM products ORDER BY id;
