"""
二手商品交易平台 - 示例图片生成工具

该脚本用于生成项目所需的示例图片资源。
使用免费的占位图服务和开源图片，不爬取任何商业网站。

使用方法：
    python generate_images.py

依赖：
    pip install requests pillow
"""

import os
import requests
from PIL import Image, ImageDraw, ImageFont
import random

# 配置
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IMAGES_DIR = os.path.join(BASE_DIR, '..', 'frontend', 'static', 'images')

# 颜色配置
COLORS = [
    '#FF6B35', '#F7C59F', '#2E4057', '#048A81', 
    '#54C6EB', '#8EE3EF', '#7C77B9', '#1D3354'
]

def ensure_dir(path):
    """确保目录存在"""
    if not os.path.exists(path):
        os.makedirs(path)

def generate_placeholder_image(width, height, text, filename, bg_color=None):
    """生成占位图片"""
    if bg_color is None:
        bg_color = random.choice(COLORS)
    
    # 创建图片
    img = Image.new('RGB', (width, height), bg_color)
    draw = ImageDraw.Draw(img)
    
    # 添加文字
    try:
        font = ImageFont.truetype("arial.ttf", min(width, height) // 8)
    except:
        font = ImageFont.load_default()
    
    # 计算文字位置（居中）
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    x = (width - text_width) // 2
    y = (height - text_height) // 2
    
    # 绘制文字
    draw.text((x, y), text, fill='white', font=font)
    
    # 保存图片
    img.save(filename, 'PNG')
    print(f"Generated: {filename}")

def generate_category_icons():
    """生成分类图标"""
    category_dir = os.path.join(IMAGES_DIR, 'category')
    ensure_dir(category_dir)
    
    categories = [
        ('electronics', '电子'),
        ('phone', '手机'),
        ('furniture', '家具'),
        ('clothing', '服饰'),
        ('baby', '母婴'),
        ('books', '图书'),
        ('sports', '运动'),
        ('other', '其他')
    ]
    
    for name, label in categories:
        filename = os.path.join(category_dir, f'{name}.png')
        generate_placeholder_image(100, 100, label, filename)

def generate_tabbar_icons():
    """生成 TabBar 图标"""
    tabbar_dir = os.path.join(IMAGES_DIR, 'tabbar')
    ensure_dir(tabbar_dir)
    
    tabs = [
        ('home', '首页'),
        ('publish', '发布'),
        ('message', '消息'),
        ('user', '我的')
    ]
    
    for name, label in tabs:
        # 普通状态
        filename = os.path.join(tabbar_dir, f'{name}.png')
        generate_placeholder_image(48, 48, label[0], filename, '#999999')
        
        # 选中状态
        filename_active = os.path.join(tabbar_dir, f'{name}-active.png')
        generate_placeholder_image(48, 48, label[0], filename_active, '#FF6B35')

def generate_avatar():
    """生成默认头像"""
    avatar_dir = os.path.join(IMAGES_DIR, 'avatar')
    ensure_dir(avatar_dir)
    
    filename = os.path.join(avatar_dir, 'default.png')
    generate_placeholder_image(200, 200, '用户', filename, '#CCCCCC')

def generate_product_images():
    """生成示例商品图片"""
    products_dir = os.path.join(IMAGES_DIR, 'products')
    ensure_dir(products_dir)
    
    products = [
        ('iphone13', 'iPhone 13'),
        ('macbook', 'MacBook'),
        ('desk', '书桌'),
        ('nike', 'Nike'),
        ('placeholder', '商品图')
    ]
    
    for name, label in products:
        filename = os.path.join(products_dir, f'{name}.jpg')
        # 生成 PNG 然后转换为 JPG
        img = Image.new('RGB', (400, 400), random.choice(COLORS))
        draw = ImageDraw.Draw(img)
        
        try:
            font = ImageFont.truetype("arial.ttf", 40)
        except:
            font = ImageFont.load_default()
        
        bbox = draw.textbbox((0, 0), label, font=font)
        text_width = bbox[2] - bbox[0]
        text_height = bbox[3] - bbox[1]
        x = (400 - text_width) // 2
        y = (400 - text_height) // 2
        
        draw.text((x, y), label, fill='white', font=font)
        img.save(filename, 'JPEG', quality=85)
        print(f"Generated: {filename}")

def generate_placeholder():
    """生成通用占位图"""
    filename = os.path.join(IMAGES_DIR, 'placeholder.png')
    ensure_dir(IMAGES_DIR)
    generate_placeholder_image(400, 400, '图片', filename, '#EEEEEE')

def main():
    """主函数"""
    print("开始生成示例图片资源...")
    print(f"输出目录: {IMAGES_DIR}")
    print("-" * 40)
    
    generate_placeholder()
    generate_category_icons()
    generate_tabbar_icons()
    generate_avatar()
    generate_product_images()
    
    print("-" * 40)
    print("图片资源生成完成!")
    print(f"请检查目录: {IMAGES_DIR}")

if __name__ == '__main__':
    main()
