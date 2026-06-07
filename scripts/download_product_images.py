#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
下载商品相关的真实图片
使用 Pexels API 下载免费高质量图片
"""

import os
import requests
import time
import random
from pathlib import Path

# Pexels API 配置（免费，无需注册）
# 使用公开的图片资源

# 商品图片映射（商品类型 -> Pexels 搜索 ID 或使用随机图片）
PRODUCT_IMAGES = {
    # 电子产品
    'electronics_computer': 'https://images.pexels.com/photos/7974/pexels-photo.jpg',
    'electronics_camera': 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    'electronics_tablet': 'https://images.pexels.com/photos/1334597/pexels-photo-1334597.jpeg',
    'electronics_headphone': 'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg',
    'electronics_speaker': 'https://images.pexels.com/photos/3587478/pexels-photo-3587478.jpeg',
    
    # 手机数码
    'phone_iphone': 'https://images.pexels.com/photos/699122/pexels-photo-699122.jpeg',
    'phone_android': 'https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg',
    'phone_huawei': 'https://images.pexels.com/photos/47261/pexels-photo-47261.jpeg',
    'phone_xiaomi': 'https://images.pexels.com/photos/1294886/pexels-photo-1294886.jpeg',
    'phone_samsung': 'https://images.pexels.com/photos/1444416/pexels-photo-1444416.jpeg',
    
    # 家居家具
    'furniture_desk': 'https://images.pexels.com/photos/667838/pexels-photo-667838.jpeg',
    'furniture_chair': 'https://images.pexels.com/photos/2762247/pexels-photo-2762247.jpeg',
    'furniture_bookshelf': 'https://images.pexels.com/photos/2177482/pexels-photo-2177482.jpeg',
    'furniture_sofa': 'https://images.pexels.com/photos/1350789/pexels-photo-1350789.jpeg',
    'furniture_bed': 'https://images.pexels.com/photos/164595/pexels-photo-164595.jpeg',
    
    # 服饰鞋包
    'clothing_jacket': 'https://images.pexels.com/photos/1040945/pexels-photo-1040945.jpeg',
    'clothing_bag': 'https://images.pexels.com/photos/1152077/pexels-photo-1152077.jpeg',
    'clothing_shoes': 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg',
    'clothing_dress': 'https://images.pexels.com/photos/985635/pexels-photo-985635.jpeg',
    'clothing_pants': 'https://images.pexels.com/photos/1598507/pexels-photo-1598507.jpeg',
    
    # 母婴用品
    'baby_toys': 'https://images.pexels.com/photos/209948/pexels-photo-209948.jpeg',
    'baby_stroller': 'https://images.pexels.com/photos/6392956/pexels-photo-6392956.jpeg',
    'baby_clothes': 'https://images.pexels.com/photos/1648375/pexels-photo-1648375.jpeg',
    'baby_bottle': 'https://images.pexels.com/photos/3983423/pexels-photo-3983423.jpeg',
    'baby_crib': 'https://images.pexels.com/photos/534228/pexels-photo-534228.jpeg',
    
    # 图书文具
    'books_book': 'https://images.pexels.com/photos/1290141/pexels-photo-1290141.jpeg',
    'books_notebook': 'https://images.pexels.com/photos/3785927/pexels-photo-3785927.jpeg',
    'books_pen': 'https://images.pexels.com/photos/261763/pexels-photo-261763.jpeg',
    'books_textbook': 'https://images.pexels.com/photos/1370298/pexels-photo-1370298.jpeg',
    'books_magazine': 'https://images.pexels.com/photos/1122528/pexels-photo-1122528.jpeg',
    
    # 运动户外
    'sports_bicycle': 'https://images.pexels.com/photos/276517/pexels-photo-276517.jpeg',
    'sports_basketball': 'https://images.pexels.com/photos/358042/pexels-photo-358042.jpeg',
    'sports_football': 'https://images.pexels.com/photos/47730/the-ball-stadion-football-the-pitch-47730.jpeg',
    'sports_tent': 'https://images.pexels.com/photos/1687845/pexels-photo-1687845.jpeg',
    'sports_skateboard': 'https://images.pexels.com/photos/2078099/pexels-photo-2078099.jpeg',
    
    # 其他
    'other_guitar': 'https://images.pexels.com/photos/1407322/pexels-photo-1407322.jpeg',
    'other_art': 'https://images.pexels.com/photos/1269968/pexels-photo-1269968.jpeg',
    'other_watch': 'https://images.pexels.com/photos/190819/pexels-photo-190819.jpeg',
    'other_plant': 'https://images.pexels.com/photos/1084199/pexels-photo-1084199.jpeg',
    'other_camera': 'https://images.pexels.com/photos/51383/photo-camera-subject-photographer-51383.jpeg',
}

def download_image(image_url, filename, output_dir):
    """
    从 Pexels 下载图片
    """
    try:
        print(f'正在下载: {filename}')
        
        # 添加请求头，模拟浏览器
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
        }
        
        response = requests.get(image_url, headers=headers, timeout=30, allow_redirects=True)
        response.raise_for_status()
        
        filepath = os.path.join(output_dir, filename)
        with open(filepath, 'wb') as f:
            f.write(response.content)
        
        # 获取文件大小
        file_size = os.path.getsize(filepath) / 1024  # KB
        print(f'✓ 下载成功: {filename} ({file_size:.1f} KB)')
        return True
        
    except Exception as e:
        print(f'✗ 下载失败 {filename}: {str(e)}')
        return False

def main():
    # 获取 uploads 目录路径
    script_dir = Path(__file__).parent
    uploads_dir = script_dir.parent / 'backend' / 'uploads'
    uploads_dir.mkdir(parents=True, exist_ok=True)
    
    print(f'图片保存目录: {uploads_dir}')
    print(f'共需下载 {len(PRODUCT_IMAGES)} 张图片\n')
    
    success_count = 0
    failed_count = 0
    
    for filename_base, image_url in PRODUCT_IMAGES.items():
        filename = f'{filename_base}.jpg'
        
        if download_image(image_url, filename, uploads_dir):
            success_count += 1
        else:
            failed_count += 1
        
        # 延迟以避免请求过快
        time.sleep(0.5)
    
    print(f'\n下载完成!')
    print(f'成功: {success_count} 张')
    print(f'失败: {failed_count} 张')

if __name__ == '__main__':
    main()
