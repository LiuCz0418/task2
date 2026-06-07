-- 使用更稳定的图片源替换无法显示的图片
USE es;

-- 使用 Lorem Picsum (稳定的随机图片服务) + 更可靠的 Unsplash 直链
-- 电子产品（分类1）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2022/09/25/22/25/iphone-7479306_640.jpg' WHERE id = 1; -- iPhone
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2021/01/14/01/34/airpods-pro-5916277_640.jpg' WHERE id = 2; -- AirPods
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2020/04/17/12/28/imac-5054238_640.jpg' WHERE id = 3; -- iPad
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2020/10/21/18/07/laptop-5673901_640.jpg' WHERE id = 4; -- MacBook
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2018/09/17/14/27/headphones-3683983_640.jpg' WHERE id = 5; -- 索尼耳机

-- 手机通讯（分类2）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2019/11/22/22/24/smartphone-4646573_640.jpg' WHERE id = 6; -- 华为
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/11/29/05/08/phone-1867584_640.jpg' WHERE id = 7; -- 小米
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/01/22/12/07/imac-1999636_640.png' WHERE id = 8; -- OPPO
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2015/02/02/15/28/samsung-620836_640.jpg' WHERE id = 9; -- 三星
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2014/08/05/10/27/iphone-410311_640.jpg' WHERE id = 10; -- vivo

-- 家具家电（分类3）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/03/28/12/10/chairs-2181947_640.jpg' WHERE id = 11; -- 书桌
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/08/02/01/01/living-room-2569325_640.jpg' WHERE id = 12; -- 椅子
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2020/12/08/03/30/vacuum-cleaner-5813696_640.jpg' WHERE id = 13; -- 吸尘器
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2019/05/29/12/41/fan-4236631_640.jpg' WHERE id = 14; -- 空气净化器
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/09/09/18/25/air-conditioning-2732498_640.jpg' WHERE id = 15; -- 空调

-- 服饰鞋包（分类4）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/11/19/18/06/feet-1840619_640.jpg' WHERE id = 16; -- Nike
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/08/01/08/29/woman-2563491_640.jpg' WHERE id = 17; -- 外套
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/11/29/01/34/bag-1866758_640.jpg' WHERE id = 18; -- 包
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/06/03/17/35/shoes-1433925_640.jpg' WHERE id = 19; -- 运动鞋
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/08/01/11/48/woman-2564660_640.jpg' WHERE id = 20; -- 卫衣

-- 图书教材（分类5）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2015/11/19/21/10/glasses-1052010_640.jpg' WHERE id = 21; -- 考研书
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/03/26/22/21/books-1281581_640.jpg' WHERE id = 22; -- 计算机书
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2015/07/17/22/43/student-849825_640.jpg' WHERE id = 23; -- Python书
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/07/15/19/42/manipulation-2507499_640.jpg' WHERE id = 24; -- 数学书
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/09/10/17/18/book-1659717_640.jpg' WHERE id = 25; -- 经济学

-- 运动户外（分类6）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/05/06/18/26/bicycle-2290411_640.jpg' WHERE id = 26; -- 自行车
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/08/04/12/28/basketball-2580376_640.jpg' WHERE id = 27; -- 篮球
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/08/07/14/02/man-2604149_640.jpg' WHERE id = 28; -- 跑步机
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2020/11/27/18/59/tennis-5782695_640.jpg' WHERE id = 29; -- 网球拍
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/04/03/16/32/yoga-2198264_640.jpg' WHERE id = 30; -- 瑜伽

-- 母婴用品（分类7）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2019/11/04/21/12/baby-4601899_640.jpg' WHERE id = 31; -- 婴儿推车
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/11/10/00/56/baby-2935722_640.jpg' WHERE id = 32; -- 安全座椅
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/11/23/07/47/baby-2972221_640.jpg' WHERE id = 33; -- 爬行垫/婴儿
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2019/02/06/16/32/baby-3979817_640.jpg' WHERE id = 34; -- 奶瓶/温奶器
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2018/04/26/12/14/lego-3351932_640.jpg' WHERE id = 35; -- 乐高

-- 其他（分类8）
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2018/01/31/07/36/nintendo-3120560_640.jpg' WHERE id = 36; -- Switch
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2014/09/06/21/24/camera-437498_640.jpg' WHERE id = 37; -- 相机
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2017/05/22/15/01/drone-2334978_640.jpg' WHERE id = 38; -- 无人机
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2016/02/19/10/14/e-book-reader-1209040_640.jpg' WHERE id = 39; -- Kindle
UPDATE products SET image_url = 'https://cdn.pixabay.com/photo/2015/05/26/23/52/technology-785742_640.jpg' WHERE id = 40; -- 键盘

SELECT '图片更新完成！使用Pixabay稳定图源' AS result;
