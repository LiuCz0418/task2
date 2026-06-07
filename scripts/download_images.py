"""
二手商品交易平台 - 商品图片资源下载工具

从免费图片源下载商品图片，丰富系统资源展示。
使用 Unsplash、Picsum 等免费可商用图片源。

使用方法：
    pip install requests pillow
    python download_images.py
"""

import os
import requests
import time
import random
from PIL import Image, ImageDraw, ImageFont
from io import BytesIO

# 配置
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IMAGES_DIR = os.path.join(BASE_DIR, '..', 'frontend', 'static', 'images')
BACKEND_UPLOADS = os.path.join(BASE_DIR, '..', 'backend', 'uploads')

# 请求头
HEADERS = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36'
}

# 商品分类及对应的搜索关键词
PRODUCT_CATEGORIES = {
    'electronics': {
        'keywords': ['laptop', 'computer', 'tablet', 'headphones', 'camera'],
        'count': 5
    },
    'phone': {
        'keywords': ['smartphone', 'mobile phone', 'iphone'],
        'count': 5
    },
    'furniture': {
        'keywords': ['desk', 'chair', 'bookshelf', 'sofa', 'table'],
        'count': 5
    },
    'clothing': {
        'keywords': ['jacket', 'shoes', 'sneakers', 'bag', 'watch'],
        'count': 5
    },
    'books': {
        'keywords': ['books', 'notebook', 'stationery'],
        'count': 3
    },
    'sports': {
        'keywords': ['bicycle', 'fitness', 'yoga', 'basketball'],
        'count': 4
    },
    'baby': {
        'keywords': ['toys', 'baby toys', 'children'],
        'count': 3
    },
    'other': {
        'keywords': ['guitar', 'plant', 'kitchen', 'art'],
        'count': 4
    }
}

def ensure_dir(path):
    """确保目录存在"""
    if not os.path.exists(path):
        os.makedirs(path)
        print(f"Created directory: {path}")

def download_from_picsum(filename, width=400, height=400):
    """从 Picsum 下载随机图片"""
    try:
        url = f"https://picsum.photos/{width}/{height}"
        response = requests.get(url, headers=HEADERS, timeout=30)
        if response.status_code == 200:
            img = Image.open(BytesIO(response.content))
            img = img.convert('RGB')
            img.save(filename, 'JPEG', quality=85)
            print(f"Downloaded: {filename}")
            return True
    except Exception as e:
        print(f"Error downloading from Picsum: {e}")
    return False

def download_from_unsplash_source(keyword, filename, width=400, height=400):
    """从 Unsplash Source 下载图片（免费API）"""
    try:
        # Unsplash Source API - 免费无需认证
        url = f"https://source.unsplash.com/{width}x{height}/?{keyword}"
        response = requests.get(url, headers=HEADERS, timeout=30, allow_redirects=True)
        if response.status_code == 200:
            img = Image.open(BytesIO(response.content))
            img = img.convert('RGB')
            img.save(filename, 'JPEG', quality=85)
            print(f"Downloaded [{keyword}]: {filename}")
            return True
    except Exception as e:
        print(f"Error downloading {keyword}: {e}")
    return False

def download_from_lorem_picsum(keyword, filename, width=400, height=400):
    """从 Lorem Picsum 下载图片"""
    try:
        # 使用随机种子确保每次获取不同图片
        seed = random.randint(1, 1000)
        url = f"https://picsum.photos/seed/{keyword}_{seed}/{width}/{height}"
        response = requests.get(url, headers=HEADERS, timeout=30)
        if response.status_code == 200:
            img = Image.open(BytesIO(response.content))
            img = img.convert('RGB')
            img.save(filename, 'JPEG', quality=85)
            print(f"Downloaded: {filename}")
            return True
    except Exception as e:
        print(f"Error: {e}")
    return False

def generate_gradient_image(text, filename, width=400, height=400):
    """生成渐变背景的占位图"""
    from PIL import ImageDraw, ImageFont
    
    # 随机选择颜色组合
    color_pairs = [
        ((255, 107, 53), (255, 143, 90)),    # 橙色系
        ((54, 198, 235), (142, 227, 239)),   # 蓝色系
        ((124, 119, 185), (159, 154, 220)),  # 紫色系
        ((4, 138, 129), (30, 180, 170)),     # 青色系
        ((233, 100, 121), (255, 150, 170)),  # 粉色系
        ((46, 64, 87), (80, 100, 130)),      # 深蓝系
    ]
    
    c1, c2 = random.choice(color_pairs)
    
    # 创建渐变图片
    img = Image.new('RGB', (width, height))
    for y in range(height):
        ratio = y / height
        r = int(c1[0] * (1 - ratio) + c2[0] * ratio)
        g = int(c1[1] * (1 - ratio) + c2[1] * ratio)
        b = int(c1[2] * (1 - ratio) + c2[2] * ratio)
        for x in range(width):
            img.putpixel((x, y), (r, g, b))
    
    # 添加文字
    draw = ImageDraw.Draw(img)
    try:
        font = ImageFont.truetype("arial.ttf", 36)
    except:
        font = ImageFont.load_default()
    
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = (width - text_width) // 2
    y = (height - text_height) // 2
    
    # 绘制阴影和文字
    draw.text((x+2, y+2), text, fill=(0, 0, 0, 128), font=font)
    draw.text((x, y), text, fill='white', font=font)
    
    img.save(filename, 'JPEG', quality=90)
    print(f"Generated: {filename}")
    return True

def download_product_images():
    """下载商品图片"""
    products_dir = os.path.join(IMAGES_DIR, 'products')
    ensure_dir(products_dir)
    
    total_downloaded = 0
    
    for category, config in PRODUCT_CATEGORIES.items():
        print(f"\n--- Downloading {category} images ---")
        category_dir = os.path.join(products_dir, category)
        ensure_dir(category_dir)
        
        for i, keyword in enumerate(config['keywords'][:config['count']]):
            filename = os.path.join(category_dir, f"{keyword.replace(' ', '_')}_{i+1}.jpg")
            
            # 尝试从 Unsplash Source 下载
            success = download_from_unsplash_source(keyword, filename)
            
            if not success:
                # 备用：从 Picsum 下载
                success = download_from_lorem_picsum(keyword, filename)
            
            if not success:
                # 最后备用：生成渐变图
                generate_gradient_image(keyword.title(), filename)
            
            total_downloaded += 1
            
            # 避免请求过快
            time.sleep(1)
    
    return total_downloaded

def download_category_icons():
    """下载/生成分类图标"""
    category_dir = os.path.join(IMAGES_DIR, 'category')
    ensure_dir(category_dir)
    
    categories = [
        ('electronics', 'laptop', '电子'),
        ('phone', 'smartphone', '手机'),
        ('furniture', 'chair', '家具'),
        ('clothing', 'fashion', '服饰'),
        ('baby', 'toys', '母婴'),
        ('books', 'books', '图书'),
        ('sports', 'fitness', '运动'),
        ('other', 'gift', '其他')
    ]
    
    for name, keyword, label in categories:
        filename = os.path.join(category_dir, f'{name}.png')
        
        # 尝试下载图标风格的图片
        success = download_from_unsplash_source(keyword, filename, 100, 100)
        
        if not success:
            generate_gradient_image(label, filename, 100, 100)
        
        time.sleep(0.5)

def download_avatar_images():
    """下载头像图片"""
    avatar_dir = os.path.join(IMAGES_DIR, 'avatar')
    ensure_dir(avatar_dir)
    
    # 默认头像
    filename = os.path.join(avatar_dir, 'default.png')
    success = download_from_unsplash_source('portrait', filename, 200, 200)
    if not success:
        generate_gradient_image('用户', filename, 200, 200)
    
    # 额外的示例头像
    for i in range(1, 6):
        filename = os.path.join(avatar_dir, f'avatar_{i}.png')
        success = download_from_unsplash_source('face portrait', filename, 200, 200)
        if not success:
            generate_gradient_image(f'用户{i}', filename, 200, 200)
        time.sleep(0.5)

def download_banner_images():
    """下载首页轮播图"""
    banner_dir = os.path.join(IMAGES_DIR, 'banner')
    ensure_dir(banner_dir)
    
    banners = ['shopping', 'sale', 'deal', 'marketplace']
    
    for i, keyword in enumerate(banners, 1):
        filename = os.path.join(banner_dir, f'banner_{i}.jpg')
        success = download_from_unsplash_source(keyword, filename, 750, 300)
        if not success:
            generate_gradient_image(f'Banner {i}', filename, 750, 300)
        time.sleep(1)

def create_tabbar_icons():
    """创建 TabBar 图标"""
    tabbar_dir = os.path.join(IMAGES_DIR, 'tabbar')
    ensure_dir(tabbar_dir)
    
    icons = {
        'home': '首',
        'publish': '+',
        'message': '消',
        'user': '我'
    }
    
    for name, char in icons.items():
        for state, color in [('', '#999999'), ('-active', '#FF6B35')]:
            filename = os.path.join(tabbar_dir, f'{name}{state}.png')
            
            img = Image.new('RGBA', (48, 48), (255, 255, 255, 0))
            draw = ImageDraw.Draw(img)
            
            # 绘制圆形背景
            draw.ellipse([4, 4, 44, 44], fill=color)
            
            # 添加文字
            try:
                font = ImageFont.truetype("arial.ttf", 24)
            except:
                font = ImageFont.load_default()
            
            bbox = draw.textbbox((0, 0), char, font=font)
            text_width = bbox[2] - bbox[0]
            text_height = bbox[3] - bbox[1]
            x = (48 - text_width) // 2
            y = (48 - text_height) // 2 - 2
            
            draw.text((x, y), char, fill='white', font=font)
            img.save(filename, 'PNG')
            print(f"Created: {filename}")

def create_placeholder():
    """创建通用占位图"""
    ensure_dir(IMAGES_DIR)
    filename = os.path.join(IMAGES_DIR, 'placeholder.png')
    generate_gradient_image('图片', filename, 400, 400)

def copy_to_backend_uploads():
    """复制部分图片到后端上传目录"""
    ensure_dir(BACKEND_UPLOADS)
    
    products_dir = os.path.join(IMAGES_DIR, 'products')
    if os.path.exists(products_dir):
        import shutil
        for category in os.listdir(products_dir):
            category_path = os.path.join(products_dir, category)
            if os.path.isdir(category_path):
                for img_file in os.listdir(category_path)[:2]:
                    src = os.path.join(category_path, img_file)
                    dst = os.path.join(BACKEND_UPLOADS, f"{category}_{img_file}")
                    if os.path.isfile(src):
                        shutil.copy2(src, dst)
                        print(f"Copied to uploads: {dst}")

def main():
    """主函数"""
    print("=" * 50)
    print("二手商品交易平台 - 图片资源下载工具")
    print("=" * 50)
    print(f"输出目录: {IMAGES_DIR}")
    print("-" * 50)
    
    # 1. 创建占位图
    print("\n[1/6] 创建占位图...")
    create_placeholder()
    
    # 2. 创建 TabBar 图标
    print("\n[2/6] 创建 TabBar 图标...")
    create_tabbar_icons()
    
    # 3. 下载分类图标
    print("\n[3/6] 下载分类图标...")
    download_category_icons()
    
    # 4. 下载头像图片
    print("\n[4/6] 下载头像图片...")
    download_avatar_images()
    
    # 5. 下载商品图片
    print("\n[5/6] 下载商品图片...")
    count = download_product_images()
    
    # 6. 下载轮播图
    print("\n[6/6] 下载轮播图...")
    download_banner_images()
    
    # 复制到后端目录
    print("\n[Extra] 复制图片到后端上传目录...")
    copy_to_backend_uploads()
    
    print("\n" + "=" * 50)
    print(f"下载完成! 共获取 {count}+ 张图片")
    print(f"图片目录: {IMAGES_DIR}")
    print(f"后端上传目录: {BACKEND_UPLOADS}")
    print("=" * 50)

if __name__ == '__main__':
    main()
