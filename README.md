# 二手商品交易平台

一个基于 Spring Boot + UniApp 开发的二手商品交易平台，支持微信小程序。

## 技术栈

- **后端**: Spring Boot 2.7.18 + Spring Data JPA + Spring Security + JWT + MySQL 8.0
- **前端**: UniApp (Vue 3) + Vite，支持编译为微信小程序
- **数据库**: MySQL 8.0

## 环境要求

- **JDK**: 11 或更高版本
- **Maven**: 3.6+（用于构建后端）
- **Node.js**: 16+（用于构建前端）
- **MySQL**: 8.0
- **微信开发者工具**（用于预览小程序）

## 快速开始

### 1. 安装环境

#### Windows 用户：

1. **安装 JDK 11+**
   - 下载地址：https://adoptium.net/
   - 安装后配置环境变量 `JAVA_HOME`

2. **安装 Maven**
   - 下载地址：https://maven.apache.org/download.cgi
   - 解压后配置环境变量 `MAVEN_HOME`

3. **安装 Node.js**
   - 下载地址：https://nodejs.org/ （推荐 LTS 版本）
   - 安装时勾选 "Add to PATH"

4. **安装 MySQL 8.0**
   - 下载地址：https://dev.mysql.com/downloads/mysql/
   - 安装时设置 root 密码为 `123456`（或修改配置文件）

5. **安装微信开发者工具**
   - 下载地址：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

### 2. 初始化数据库

打开 MySQL 客户端（如 MySQL Workbench 或命令行），执行：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS es DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE es;
```

然后导入完整的数据库脚本：

```bash
# 导入完整的数据库结构和初始数据
mysql -u root -p es < database/es.sql
```

或者按顺序执行以下脚本：

```bash
# 1. 基础表结构
mysql -u root -p es < database/schema.sql

# 2. 多维度评分和信用分初始化
mysql -u root -p es < database/update_v2.sql

# 3. 身份证号字段
mysql -u root -p es < database/add_id_card.sql

# 4. 评价审核机制
mysql -u root -p es < database/update_review_audit.sql

# 5. 测试数据（可选）
mysql -u root -p es < database/seed_data.sql
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev:mp-weixin
```

然后用微信开发者工具打开 `frontend/dist/dev/mp-weixin` 目录

### 5. 创建管理员账户

首次使用需要创建管理员账户，在 MySQL 中执行：

```sql
-- 将普通用户升级为管理员
UPDATE users SET role = 'ADMIN' WHERE id = 1;
```

## 功能特性

### 基础功能

- [x] 用户注册/登录
- [x] 商品发布/编辑/删除
- [x] 商品分类浏览
- [x] 商品搜索
- [x] 订单管理（创建、支付、发货、收货）
- [x] 即时通讯（买家与卖家聊天）
- [x] 商品评价
- [x] 个人中心
- [x] 商品收藏

### 审核机制（新增）

#### 用户注册审核
- 新用户注册后需要管理员审核通过才能登录
- 支持查看用户身份证号进行实名认证
- 管理员可批准或拒绝注册申请

#### 商品发布审核
- 用户发布的商品默认为"待审核"状态
- 管理员审核通过后商品才会上架展示
- 审核拒绝时可填写拒绝原因

#### 评价信用分审核
- 差评（1-2星）会触发信用分扣分，需管理员审核
- 管理员审核通过后才会扣除卖家信用分
- 审核拒绝则不扣分，保护卖家权益

### 多维度评价系统

评价支持多维度评分：
- **综合评分**：1-5星整体评价
- **服务质量**：卖家服务态度评分
- **描述准确性**：商品与描述是否一致
- **发货速度**：发货时效评分

### 信用分系统

- 用户初始信用分 100 分
- 差评审核通过后扣除卖家信用分
- 信用分影响用户信誉展示

## 数据库表结构

### 用户表 (users)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 用户ID（主键） |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（加密存储） |
| nickname | VARCHAR(50) | 昵称 |
| avatar | VARCHAR(255) | 头像URL |
| email | VARCHAR(100) | 邮箱 |
| phone_number | VARCHAR(20) | 手机号 |
| id_card | VARCHAR(18) | 身份证号（新增） |
| credit_score | INT | 信用分（0-100） |
| status | TINYINT | 状态：0-禁用，1-正常 |
| role | VARCHAR(20) | 角色：USER/ADMIN（新增） |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 商品表 (products)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 商品ID（主键） |
| name | VARCHAR(100) | 商品名称 |
| description | TEXT | 商品描述 |
| price | DECIMAL(10,2) | 商品价格 |
| original_price | DECIMAL(10,2) | 原价 |
| image_url | VARCHAR(255) | 主图URL |
| category_id | INT | 分类ID |
| seller_id | INT | 卖家ID |
| condition_level | TINYINT | 成色等级（1-10） |
| location | VARCHAR(100) | 发布地点 |
| view_count | INT | 浏览次数 |
| status | TINYINT | 状态：0-下架，1-在售，2-已售，3-待审核，4-审核拒绝 |
| reject_reason | VARCHAR(255) | 拒绝原因（新增） |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 评价表 (reviews)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 评价ID（主键） |
| product_id | INT | 商品ID |
| order_id | INT | 订单ID |
| user_id | INT | 评价用户ID |
| rating | INT | 评分（1-5星） |
| comment | TEXT | 评价内容 |
| images | TEXT | 评价图片（JSON数组） |
| service_score | INT | 服务质量评分（新增） |
| desc_score | INT | 描述准确性评分（新增） |
| ship_score | INT | 发货速度评分（新增） |
| audit_status | TINYINT | 审核状态：0-待审核，1-已通过，2-已拒绝（新增） |
| audit_remark | VARCHAR(255) | 审核备注（新增） |
| created_at | TIMESTAMP | 创建时间 |

### 订单表 (orders)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 订单ID（主键） |
| order_no | VARCHAR(32) | 订单编号 |
| product_id | INT | 商品ID |
| buyer_id | INT | 买家ID |
| seller_id | INT | 卖家ID |
| price | DECIMAL(10,2) | 成交价格 |
| status | ENUM | 订单状态：PENDING/PAID/SHIPPED/DELIVERED/COMPLETED/CANCELLED |
| shipping_address | VARCHAR(255) | 收货地址 |
| shipping_name | VARCHAR(50) | 收货人姓名 |
| shipping_phone | VARCHAR(20) | 收货人电话 |
| tracking_number | VARCHAR(50) | 物流单号 |
| tracking_company | VARCHAR(50) | 物流公司 |
| payment_time | TIMESTAMP | 支付时间 |
| ship_time | TIMESTAMP | 发货时间 |
| receive_time | TIMESTAMP | 收货时间 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

### 其他表

- **categories**: 商品分类表
- **product_images**: 商品图片表（支持多图）
- **messages**: 消息表（聊天记录）
- **favorites**: 收藏表

## 配置说明

### 后端配置 (backend/src/main/resources/application.yml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/es?useSSL=false&serverTimezone=UTC&characterEncoding=utf8
    username: root
    password: 123456  # 修改为你的数据库密码

# 文件上传路径
upload:
  path: C:/Users/xzc/Desktop/wx/es/backend/uploads  # 修改为你的路径
```

### 前端配置 (frontend/src/utils/request.js)

```javascript
// API 基础地址
const BASE_URL = 'http://localhost:8080'
// 如果使用手机预览，需要改为电脑的局域网 IP，如：
// const BASE_URL = 'http://192.168.1.100:8080'
```

## API 接口

### 用户相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `GET /api/users/{id}` - 获取用户信息
- `PUT /api/users/{id}` - 更新用户信息

### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情
- `POST /api/products` - 发布商品
- `PUT /api/products/{id}` - 更新商品
- `DELETE /api/products/{id}` - 删除商品

### 订单相关
- `GET /api/orders` - 获取订单列表
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}/pay` - 支付订单
- `PUT /api/orders/{id}/ship` - 发货
- `PUT /api/orders/{id}/receive` - 确认收货

### 评价相关
- `GET /api/reviews/product/{productId}` - 获取商品评价
- `POST /api/reviews` - 提交评价

### 管理员接口
- `GET /api/admin/users/pending` - 获取待审核用户列表
- `POST /api/admin/users/{id}/audit` - 审核用户注册
- `GET /api/admin/products/pending` - 获取待审核商品列表
- `POST /api/admin/products/{id}/review` - 审核商品
- `GET /api/admin/reviews/pending` - 获取待审核差评列表
- `POST /api/admin/reviews/{id}/audit` - 审核差评

## 常见问题

### Q: 后端启动失败，提示数据库连接错误？
A: 请检查：
1. MySQL 服务是否已启动
2. `application.yml` 中的数据库用户名和密码是否正确
3. 数据库 `es` 是否已创建

### Q: 微信小程序无法连接后端？
A: 请检查：
1. 手机和电脑是否在同一 WiFi 下
2. 在小程序"我的"->"设置"中配置正确的服务器 IP 地址
3. 电脑防火墙是否允许 8080 端口

### Q: 真机调试时提示网络连接失败？
A: 真机调试时需要配置服务器 IP：
1. 在电脑上打开命令提示符，执行 `ipconfig` 查看本机 IP（通常是 192.168.x.x）
2. 在小程序中进入"我的"->"设置"
3. 输入电脑的 IP 地址，点击保存
4. 重新编译小程序（Ctrl+R 或点击编译按钮）
5. 确保手机和电脑连接同一个 WiFi

### Q: 图片加载失败？
A: 请检查：
1. `application.yml` 中的 `upload.path` 路径是否存在
2. 该路径是否有写入权限
3. 如果是外部图片 403 错误，执行 `mysql -u root -p es < database/fix_images_local.sql` 将图片改为本地图片

### Q: 如何访问管理员审核页面？
A:
1. 首先将用户升级为管理员：`UPDATE users SET role = 'ADMIN' WHERE id = 1;`
2. 使用管理员账户登录小程序
3. 在"我的"页面会显示"审核管理"入口

## 项目结构

```
task2/
├── backend/                    # 后端项目
│   ├── src/main/java/com/es/secondhand/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 服务层
│   │   ├── repository/        # 数据访问层
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   └── config/            # 配置类
│   └── src/main/resources/
│       └── application.yml    # 配置文件
├── frontend/                   # 前端项目（UniApp）
│   ├── src/
│   │   ├── pages/             # 页面
│   │   ├── components/        # 组件
│   │   ├── utils/             # 工具函数
│   │   └── static/            # 静态资源
│   └── package.json
├── database/                   # 数据库脚本
│   ├── es.sql                 # 完整数据库脚本
│   ├── schema.sql             # 表结构
│   ├── seed_data.sql          # 测试数据
│   ├── add_id_card.sql        # 身份证号字段
│   └── update_review_audit.sql # 评价审核字段
└── README.md
```

## 开发团队

如有问题，欢迎提交 Issue 或联系开发者。
