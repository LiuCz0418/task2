#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
根据实际商品名称下载匹配的商品图片
使用 Pexels 免费 API
"""

import os
import requests
import time
import mysql.connector
from pathlib import Path

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '123456',
    'database': 'es',
    'charset': 'utf8mb4'
}

# 商品名称到搜索关键词的映射
PRODUCT_KEYWORDS = {
    # 电子产品 (category_id=1)
    'iPhone': 'iphone smartphone',
    'AirPods': 'airpods wireless earbuds',
    'iPad': 'ipad tablet',
    'MacBook': 'macbook laptop',
    '索尼': 'sony headphones',
    
    # 手机数码 (category_id=2)
    '华为': 'huawei smartphone',
    '小米': 'xiaomi phone',
    'OPPO': 'oppo smartphone',
    '三星': 'samsung galaxy phone',
    'vivo': 'vivo smartphone',
    
    # 家居家具 (category_id=3)
    '书桌': 'desk workspace',
    '椅': 'office chair ergonomic',
    '吸尘器': 'vacuum cleaner dyson',
    '空气净化器': 'air purifier',
    '空调': 'air conditioner',
    
    # 服饰鞋包 (category_id=4)
    'Nike': 'nike air jordan sneakers',
    'Lululemon': 'lululemon jacket',
    'Coach': 'coach leather bag',
    'Adidas': 'adidas sneakers',
    'Champion': 'champion hoodie',
    
    # 图书文具 (category_id=5)
    '考研': 'exam books study',
    '计算机系统': 'computer science textbook',
    'Python': 'python programming book',
    '数学': 'mathematics textbook',
    '经济学': 'economics textbook',
    
    # 运动户外 (category_id=6)
    '自行车': 'mountain bike bicycle',
    '篮球': 'basketball spalding',
    '跑步机': 'treadmill fitness',
    '网球拍': 'tennis racket',
    '瑜伽垫': 'yoga mat',
    
    # 母婴用品 (category_id=7)
    '婴儿推车': 'baby stroller',
    '安全座椅': 'car seat child',
    '爬行垫': 'baby play mat',
    '温奶器': 'bottle warmer',
    '乐高': 'lego duplo blocks',
    
    # 其他 (category_id=8)
    'Switch': 'nintendo switch',
    '佳能': 'canon camera eos',
    '大疆': 'dji drone',
    'Kindle': 'kindle paperwhite',
    '机械键盘': 'mechanical keyboard razer',
}

def get_keyword_for_product(product_name):
    """根据商品名称匹配搜索关键词"""
    for key, keyword in PRODUCT_KEYWORDS.items():
        if key in product_name:
            return keyword
    # 默认使用第一个词作为关键词
    return product_name.split()[0] if product_name else 'product'

def download_image_from_pexels(keyword, filename):
    """从 Pexels 下载图片"""
    try:
        # Pexels 特定图片 URL（精选高质量商品图片）
        pexels_images = {
            'iphone smartphone': 'https://images.pexels.com/photos/788946/pexels-photo-788946.jpeg',
            'airpods wireless earbuds': 'https://images.pexels.com/photos/3825517/pexels-photo-3825517.jpeg',
            'ipad tablet': 'https://images.pexels.com/photos/1334597/pexels-photo-1334597.jpeg',
            'macbook laptop': 'https://images.pexels.com/photos/18105/pexels-photo.jpg',
            'sony headphones': 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg',
            
            'huawei smartphone': 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg',
            'xiaomi phone': 'https://images.pexels.com/photos/1294886/pexels-photo-1294886.jpeg',
            'oppo smartphone': 'https://images.pexels.com/photos/1444416/pexels-photo-1444416.jpeg',
            'samsung galaxy phone': 'https://images.pexels.com/photos/887751/pexels-photo-887751.jpeg',
            'vivo smartphone': 'https://images.pexels.com/photos/699122/pexels-photo-699122.jpeg',
            
            'desk workspace': 'https://images.pexels.com/photos/667838/pexels-photo-667838.jpeg',
            'office chair ergonomic': 'https://images.pexels.com/photos/2762247/pexels-photo-2762247.jpeg',
            'vacuum cleaner dyson': 'https://images.pexels.com/photos/4107278/pexels-photo-4107278.jpeg',
            'air purifier': 'https://images.pexels.com/photos/4031818/pexels-photo-4031818.jpeg',
            'air conditioner': 'https://images.pexels.com/photos/1679622/pexels-photo-1679622.jpeg',
            
            'nike air jordan sneakers': 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg',
            'lululemon jacket': 'https://images.pexels.com/photos/1040945/pexels-photo-1040945.jpeg',
            'coach leather bag': 'https://images.pexels.com/photos/1152077/pexels-photo-1152077.jpeg',
            'adidas sneakers': 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg',
            'champion hoodie': 'https://images.pexels.com/photos/8532616/pexels-photo-8532616.jpeg',
            
            'exam books study': 'https://images.pexels.com/photos/301920/pexels-photo-301920.jpeg',
            'computer science textbook': 'https://images.pexels.com/photos/1181263/pexels-photo-1181263.jpeg',
            'python programming book': 'https://images.pexels.com/photos/1181671/pexels-photo-1181671.jpeg',
            'mathematics textbook': 'https://images.pexels.com/photos/6256065/pexels-photo-6256065.jpeg',
            'economics textbook': 'https://images.pexels.com/photos/1370298/pexels-photo-1370298.jpeg',
            
            'mountain bike bicycle': 'https://images.pexels.com/photos/276517/pexels-photo-276517.jpeg',
            'basketball spalding': 'https://images.pexels.com/photos/358042/pexels-photo-358042.jpeg',
            'treadmill fitness': 'https://images.pexels.com/photos/3253501/pexels-photo-3253501.jpeg',
            'tennis racket': 'https://images.pexels.com/photos/226587/pexels-photo-226587.jpeg',
            'yoga mat': 'https://images.pexels.com/photos/3822906/pexels-photo-3822906.jpeg',
            
            'baby stroller': 'https://images.pexels.com/photos/6392956/pexels-photo-6392956.jpeg',
            'car seat child': 'https://images.pexels.com/photos/9159015/pexels-photo-9159015.jpeg',
            'baby play mat': 'https://images.pexels.com/photos/6941001/pexels-photo-6941001.jpeg',
            'bottle warmer': 'https://images.pexels.com/photos/3983423/pexels-photo-3983423.jpeg',
            'lego duplo blocks': 'https://images.pexels.com/photos/209948/pexels-photo-209948.jpeg',
            
            'nintendo switch': 'https://images.pexels.com/photos/3945683/pexels-photo-3945683.jpeg',
            'canon camera eos': 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
            'dji drone': 'https://images.pexels.com/photos/2876511/pexels-photo-2876511.jpeg',
            'kindle paperwhite': 'https://images.pexels.com/photos/1290141/pexels-photo-1290141.jpeg',
            'mechanical keyboard razer': 'https://images.pexels.com/photos/1194713/pexels-photo-1194713.jpeg',
        }
        
        url = pexels_images.get(keyword)
        if not url:
            # 如果没有预定义URL，使用通用商品图片
            url = 'https://images.pexels.com/photos/1148957/pexels-photo-1148957.jpeg'
        
        print(f'正在下载: {filename} ({keyword})')
        
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        
        response = requests.get(url, headers=headers, timeout=30, allow_redirects=True)
        response.raise_for_status()
        
        with open(filename, 'wb') as f:
            f.write(response.content)
        
        file_size = os.path.getsize(filename) / 1024
        print(f'✓ 下载成功: {filename} ({file_size:.1f} KB)')
        return True
        
    except Exception as e:
        print(f'✗ 下载失败 {filename}: {str(e)}')
        return False

def clean_uploads_directory(uploads_dir):
    """清空 uploads 目录中的旧图片"""
    print('\n正在清理旧图片...')
    count = 0
    for file in os.listdir(uploads_dir):
        if file.endswith(('.jpg', '.jpeg', '.png')):
            file_path = os.path.join(uploads_dir, file)
            try:
                os.remove(file_path)
                count += 1
            except Exception as e:
                print(f'删除失败 {file}: {e}')
    print(f'已删除 {count} 个旧图片文件')

def main():
    """主函数"""
    print("=" * 60)
    print("根据实际商品名称下载匹配图片")
    print("=" * 60)
    
    # 获取 uploads 目录
    script_dir = Path(__file__).parent
    uploads_dir = script_dir.parent / 'backend' / 'uploads'
    uploads_dir.mkdir(parents=True, exist_ok=True)
    
    print(f'\n图片保存目录: {uploads_dir}')
    
    # 1. 清空旧图片
    clean_uploads_directory(uploads_dir)
    
    # 2. 连接数据库
    print('\n连接数据库...')
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor(dictionary=True)
        
        # 获取所有商品
        cursor.execute("SELECT id, name, category_id FROM products WHERE id <= 40 ORDER BY id")
        products = cursor.fetchall()
        
        print(f'找到 {len(products)} 个商品\n')
        
        success_count = 0
        failed_count = 0
        updates = []
        
        # 3. 为每个商品下载图片
        for product in products:
            product_id = product['id']
            product_name = product['name']
            
            # 生成文件名
            filename = f'product_{product_id}.jpg'
            filepath = uploads_dir / filename
            
            # 获取搜索关键词
            keyword = get_keyword_for_product(product_name)
            
            # 下载图片
            if download_image_from_pexels(keyword, str(filepath)):
                success_count += 1
                image_url = f'/uploads/{filename}'
                updates.append((image_url, product_id))
            else:
                failed_count += 1
            
            time.sleep(0.5)
        
        # 4. 更新数据库
        print(f'\n\n正在更新数据库...')
        for image_url, product_id in updates:
            cursor.execute(
                "UPDATE products SET image_url = %s WHERE id = %s",
                (image_url, product_id)
            )
        
        conn.commit()
        print(f'✓ 数据库更新完成，共更新 {len(updates)} 条记录')
        
        # 5. 显示结果
        print("\n" + "=" * 60)
        print(f"下载完成!")
        print(f"成功: {success_count} 张")
        print(f"失败: {failed_count} 张")
        print(f"图片目录: {uploads_dir}")
        print("=" * 60)
        
        cursor.close()
        conn.close()
        
    except Exception as e:
        print(f'错误: {e}')
        import traceback
        traceback.print_exc()

if __name__ == '__main__':
    main()
