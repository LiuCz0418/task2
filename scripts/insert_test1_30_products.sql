USE es;
SET NAMES utf8mb4;

INSERT INTO products
(name, description, price, original_price, image_url, category_id, seller_id, condition_level, location, view_count, status, reject_reason, created_at, updated_at)
VALUES
('采集商品01 无线降噪耳机', '轻度使用的无线降噪耳机，耳罩干净，连接稳定，适合通勤、学习和会议使用。支持蓝牙连接，续航表现正常，随商品附带充电线。', 299.00, 699.00, '/static/images/products/electronics/headphones_4.jpg', 1, 4, 8, '上海市浦东新区', 18, 1, NULL, NOW(), NOW()),
('采集商品02 轻薄办公笔记本', '一台适合日常办公和网课学习的轻薄笔记本，键盘手感正常，屏幕显示清晰，机身有轻微使用痕迹。适合文档处理、网页浏览和轻量开发。', 2680.00, 4999.00, '/static/images/products/electronics/laptop_1.jpg', 1, 4, 8, '南京市玄武区', 27, 1, NULL, NOW(), NOW()),
('采集商品03 平板电脑学习套装', '平板电脑成色较新，适合看网课、阅读资料和轻办公使用。屏幕无明显划痕，电池续航正常，附带保护壳。', 1180.00, 2499.00, '/static/images/products/electronics/tablet_3.jpg', 1, 4, 9, '杭州市西湖区', 31, 1, NULL, NOW(), NOW()),
('采集商品04 数码相机入门套机', '入门级数码相机，适合旅行记录、校园拍摄和短视频素材采集。机身功能正常，镜头干净，附带存储卡和相机包。', 1599.00, 3299.00, '/static/images/products/electronics/camera_5.jpg', 1, 4, 8, '苏州市姑苏区', 22, 1, NULL, NOW(), NOW()),

('采集商品05 全面屏智能手机', '备用智能手机，运行流畅，屏幕显示正常，适合当备用机、老人机或学习机。机身边框有轻微磨痕，不影响正常使用。', 899.00, 1999.00, '/static/images/products/phone/smartphone_1.jpg', 2, 4, 8, '北京市朝阳区', 40, 1, NULL, NOW(), NOW()),
('采集商品06 大屏影音手机', '大屏手机适合追剧、刷视频和日常聊天，外放声音清晰，电池健康度良好。已恢复出厂设置，拿到即可使用。', 699.00, 1599.00, '/static/images/products/phone/mobile_phone_2.jpg', 2, 4, 7, '广州市天河区', 35, 1, NULL, NOW(), NOW()),
('采集商品07 iPhone备用机', '适合作为备用机使用的 iPhone，系统运行稳定，拍照清晰，外观有正常使用痕迹。适合对性能要求不高的日常使用场景。', 1380.00, 3299.00, '/static/images/products/phone/iphone_3.jpg', 2, 4, 8, '深圳市南山区', 52, 1, NULL, NOW(), NOW()),

('采集商品08 实木办公书桌', '实木风格办公书桌，桌面宽敞，适合放置电脑、显示器和学习资料。桌面有轻微划痕，结构稳固，需要自提。', 260.00, 899.00, '/static/images/products/furniture/desk_1.jpg', 3, 4, 7, '成都市高新区', 16, 1, NULL, NOW(), NOW()),
('采集商品09 人体工学电脑椅', '人体工学椅，腰托和扶手状态良好，坐感舒适，适合长时间办公学习。椅面干净，无明显破损。', 380.00, 1199.00, '/static/images/products/furniture/chair_2.jpg', 3, 4, 8, '武汉市洪山区', 28, 1, NULL, NOW(), NOW()),
('采集商品10 小户型布艺沙发', '双人布艺沙发，适合租房和小户型客厅使用，整体干净，靠背回弹正常。搬家转让，建议同城自提。', 520.00, 1680.00, '/static/images/products/furniture/sofa_4.jpg', 3, 4, 7, '重庆市渝北区', 19, 1, NULL, NOW(), NOW()),
('采集商品11 简约收纳书架', '多层收纳书架，可放图书、摆件和办公用品，结构稳定，占地小。边角有轻微磨损，不影响使用。', 180.00, 499.00, '/static/images/products/furniture/bookshelf_3.jpg', 3, 4, 8, '西安市雁塔区', 13, 1, NULL, NOW(), NOW()),

('采集商品12 春秋休闲夹克', '男款休闲夹克，版型简洁，适合春秋通勤穿搭。衣服已清洗，袖口和领口状态良好。', 120.00, 399.00, '/static/images/products/clothing/jacket_1.jpg', 4, 4, 8, '长沙市岳麓区', 17, 1, NULL, NOW(), NOW()),
('采集商品13 低帮运动鞋', '低帮运动鞋，鞋底磨损较轻，日常走路和运动都可以穿。尺码标准，已清洁消毒。', 160.00, 599.00, '/static/images/products/clothing/shoes_2.jpg', 4, 4, 7, '合肥市蜀山区', 33, 1, NULL, NOW(), NOW()),
('采集商品14 城市通勤双肩包', '容量较大的通勤双肩包，可放电脑和书本，隔层设计合理。拉链顺滑，背带无断裂。', 95.00, 299.00, '/static/images/products/clothing/bag_4.jpg', 4, 4, 8, '天津市和平区', 24, 1, NULL, NOW(), NOW()),
('采集商品15 简约石英手表', '简约风格石英手表，适合日常搭配，表盘干净，走时正常。表带有轻微使用痕迹。', 150.00, 520.00, '/static/images/products/clothing/watch_5.jpg', 4, 4, 8, '青岛市市南区', 20, 1, NULL, NOW(), NOW()),

('采集商品16 计算机基础教材', '计算机专业基础教材，适合入门学习和课程复习。书页完整，有少量笔记标注，不影响阅读。', 36.00, 89.00, '/static/images/products/books/books_1.jpg', 5, 4, 7, '郑州市金水区', 21, 1, NULL, NOW(), NOW()),
('采集商品17 A5活页笔记本套装', 'A5 活页笔记本和替芯套装，适合课堂笔记、会议记录和计划管理。封皮完好，纸张剩余较多。', 18.00, 59.00, '/static/images/products/books/notebook_2.jpg', 5, 4, 9, '厦门市思明区', 11, 1, NULL, NOW(), NOW()),
('采集商品18 文具收纳组合', '包含笔筒、便签和常用文具，适合宿舍书桌和办公室收纳。整体干净，功能完整。', 25.00, 79.00, '/static/images/products/books/stationery_3.jpg', 5, 4, 9, '福州市鼓楼区', 14, 1, NULL, NOW(), NOW()),

('采集商品19 城市通勤自行车', '通勤自行车，刹车和变速正常，适合校园、地铁站短途代步。车身有使用痕迹，骑行稳定。', 360.00, 1099.00, '/static/images/products/sports/bicycle_1.jpg', 6, 4, 7, '宁波市鄞州区', 42, 1, NULL, NOW(), NOW()),
('采集商品20 室内外篮球', '标准尺寸篮球，手感不错，适合室内外场地使用。球面有轻微磨损，气密性正常。', 55.00, 169.00, '/static/images/products/sports/basketball_4.jpg', 6, 4, 8, '南昌市红谷滩区', 18, 1, NULL, NOW(), NOW()),
('采集商品21 家用健身器材', '小型家用健身器材，适合日常拉伸和力量训练，占地少，收纳方便。功能正常。', 210.00, 699.00, '/static/images/products/sports/fitness_2.jpg', 6, 4, 8, '昆明市五华区', 26, 1, NULL, NOW(), NOW()),
('采集商品22 加厚防滑瑜伽垫', '加厚防滑瑜伽垫，适合瑜伽、普拉提和居家训练。表面干净，无明显破损。', 39.00, 129.00, '/static/images/products/sports/yoga_3.jpg', 6, 4, 8, '贵阳市观山湖区', 15, 1, NULL, NOW(), NOW()),

('采集商品23 儿童益智玩具', '儿童益智玩具套装，零件齐全，适合亲子互动和动手能力训练。已清洁整理。', 45.00, 159.00, '/static/images/products/baby/toys_1.jpg', 7, 4, 8, '哈尔滨市南岗区', 16, 1, NULL, NOW(), NOW()),
('采集商品24 宝宝玩具收纳套装', '宝宝玩具和收纳组合，颜色明亮，适合低龄儿童玩耍。无尖锐边角，整体成色较新。', 68.00, 199.00, '/static/images/products/baby/baby_toys_2.jpg', 7, 4, 8, '沈阳市和平区', 12, 1, NULL, NOW(), NOW()),
('采集商品25 儿童外出用品', '儿童外出用品组合，适合短途出行和日常携带。整体轻便，收纳方便，功能正常。', 98.00, 299.00, '/static/images/products/baby/children_3.jpg', 7, 4, 7, '太原市小店区', 9, 1, NULL, NOW(), NOW()),

('采集商品26 木吉他入门款', '适合初学者练习的木吉他，琴弦状态正常，音色清亮。琴身有轻微使用痕迹，附带琴包。', 320.00, 899.00, '/static/images/products/other/guitar_1.jpg', 8, 4, 8, '兰州市城关区', 29, 1, NULL, NOW(), NOW()),
('采集商品27 桌面绿植盆栽', '桌面绿植盆栽，适合办公室、宿舍和阳台摆放，维护简单，能提升空间氛围。', 28.00, 69.00, '/static/images/products/other/plant_2.jpg', 8, 4, 9, '海口市美兰区', 8, 1, NULL, NOW(), NOW()),
('采集商品28 厨房小家电', '实用厨房小家电，适合租房和小家庭使用，操作简单，清洁方便。功能测试正常。', 180.00, 499.00, '/static/images/products/other/kitchen_3.jpg', 8, 4, 8, '济南市历下区', 23, 1, NULL, NOW(), NOW()),
('采集商品29 装饰画摆件', '家居装饰画摆件，适合客厅、卧室或书房布置。画面干净，边框完好，风格百搭。', 75.00, 229.00, '/static/images/products/other/art_4.jpg', 8, 4, 8, '石家庄市长安区', 10, 1, NULL, NOW(), NOW()),
('采集商品30 台式电脑主机', '台式电脑主机，适合办公、学习和轻度娱乐使用。运行稳定，接口齐全，机箱有正常使用痕迹。', 1280.00, 2999.00, '/static/images/products/electronics/computer_2.jpg', 1, 4, 7, '无锡市滨湖区', 38, 1, NULL, NOW(), NOW());

INSERT INTO product_images (product_id, image_url, sort_order)
SELECT id, '/static/images/products/electronics/headphones_4.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品01 无线降噪耳机'
UNION ALL SELECT id, '/static/images/products/electronics/computer_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品01 无线降噪耳机'
UNION ALL SELECT id, '/static/images/products/electronics/laptop_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品02 轻薄办公笔记本'
UNION ALL SELECT id, '/static/images/products/electronics/computer_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品02 轻薄办公笔记本'
UNION ALL SELECT id, '/static/images/products/electronics/tablet_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品03 平板电脑学习套装'
UNION ALL SELECT id, '/static/images/products/electronics/laptop_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品03 平板电脑学习套装'
UNION ALL SELECT id, '/static/images/products/electronics/camera_5.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品04 数码相机入门套机'
UNION ALL SELECT id, '/static/images/products/electronics/tablet_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品04 数码相机入门套机'
UNION ALL SELECT id, '/static/images/products/phone/smartphone_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品05 全面屏智能手机'
UNION ALL SELECT id, '/static/images/products/phone/mobile_phone_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品05 全面屏智能手机'
UNION ALL SELECT id, '/static/images/products/phone/mobile_phone_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品06 大屏影音手机'
UNION ALL SELECT id, '/static/images/products/phone/iphone_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品06 大屏影音手机'
UNION ALL SELECT id, '/static/images/products/phone/iphone_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品07 iPhone备用机'
UNION ALL SELECT id, '/static/images/products/phone/smartphone_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品07 iPhone备用机'
UNION ALL SELECT id, '/static/images/products/furniture/desk_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品08 实木办公书桌'
UNION ALL SELECT id, '/static/images/products/furniture/table_5.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品08 实木办公书桌'
UNION ALL SELECT id, '/static/images/products/furniture/chair_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品09 人体工学电脑椅'
UNION ALL SELECT id, '/static/images/products/furniture/desk_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品09 人体工学电脑椅'
UNION ALL SELECT id, '/static/images/products/furniture/sofa_4.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品10 小户型布艺沙发'
UNION ALL SELECT id, '/static/images/products/furniture/chair_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品10 小户型布艺沙发'
UNION ALL SELECT id, '/static/images/products/furniture/bookshelf_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品11 简约收纳书架'
UNION ALL SELECT id, '/static/images/products/furniture/table_5.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品11 简约收纳书架'
UNION ALL SELECT id, '/static/images/products/clothing/jacket_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品12 春秋休闲夹克'
UNION ALL SELECT id, '/static/images/products/clothing/watch_5.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品12 春秋休闲夹克'
UNION ALL SELECT id, '/static/images/products/clothing/shoes_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品13 低帮运动鞋'
UNION ALL SELECT id, '/static/images/products/clothing/sneakers_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品13 低帮运动鞋'
UNION ALL SELECT id, '/static/images/products/clothing/bag_4.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品14 城市通勤双肩包'
UNION ALL SELECT id, '/static/images/products/clothing/jacket_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品14 城市通勤双肩包'
UNION ALL SELECT id, '/static/images/products/clothing/watch_5.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品15 简约石英手表'
UNION ALL SELECT id, '/static/images/products/clothing/bag_4.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品15 简约石英手表'
UNION ALL SELECT id, '/static/images/products/books/books_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品16 计算机基础教材'
UNION ALL SELECT id, '/static/images/products/books/notebook_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品16 计算机基础教材'
UNION ALL SELECT id, '/static/images/products/books/notebook_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品17 A5活页笔记本套装'
UNION ALL SELECT id, '/static/images/products/books/stationery_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品17 A5活页笔记本套装'
UNION ALL SELECT id, '/static/images/products/books/stationery_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品18 文具收纳组合'
UNION ALL SELECT id, '/static/images/products/books/books_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品18 文具收纳组合'
UNION ALL SELECT id, '/static/images/products/sports/bicycle_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品19 城市通勤自行车'
UNION ALL SELECT id, '/static/images/products/sports/fitness_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品19 城市通勤自行车'
UNION ALL SELECT id, '/static/images/products/sports/basketball_4.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品20 室内外篮球'
UNION ALL SELECT id, '/static/images/products/sports/bicycle_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品20 室内外篮球'
UNION ALL SELECT id, '/static/images/products/sports/fitness_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品21 家用健身器材'
UNION ALL SELECT id, '/static/images/products/sports/yoga_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品21 家用健身器材'
UNION ALL SELECT id, '/static/images/products/sports/yoga_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品22 加厚防滑瑜伽垫'
UNION ALL SELECT id, '/static/images/products/sports/fitness_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品22 加厚防滑瑜伽垫'
UNION ALL SELECT id, '/static/images/products/baby/toys_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品23 儿童益智玩具'
UNION ALL SELECT id, '/static/images/products/baby/baby_toys_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品23 儿童益智玩具'
UNION ALL SELECT id, '/static/images/products/baby/baby_toys_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品24 宝宝玩具收纳套装'
UNION ALL SELECT id, '/static/images/products/baby/children_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品24 宝宝玩具收纳套装'
UNION ALL SELECT id, '/static/images/products/baby/children_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品25 儿童外出用品'
UNION ALL SELECT id, '/static/images/products/baby/toys_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品25 儿童外出用品'
UNION ALL SELECT id, '/static/images/products/other/guitar_1.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品26 木吉他入门款'
UNION ALL SELECT id, '/static/images/products/other/art_4.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品26 木吉他入门款'
UNION ALL SELECT id, '/static/images/products/other/plant_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品27 桌面绿植盆栽'
UNION ALL SELECT id, '/static/images/products/other/kitchen_3.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品27 桌面绿植盆栽'
UNION ALL SELECT id, '/static/images/products/other/kitchen_3.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品28 厨房小家电'
UNION ALL SELECT id, '/static/images/products/other/plant_2.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品28 厨房小家电'
UNION ALL SELECT id, '/static/images/products/other/art_4.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品29 装饰画摆件'
UNION ALL SELECT id, '/static/images/products/other/guitar_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品29 装饰画摆件'
UNION ALL SELECT id, '/static/images/products/electronics/computer_2.jpg', 0 FROM products WHERE seller_id = 4 AND name = '采集商品30 台式电脑主机'
UNION ALL SELECT id, '/static/images/products/electronics/laptop_1.jpg', 1 FROM products WHERE seller_id = 4 AND name = '采集商品30 台式电脑主机';
