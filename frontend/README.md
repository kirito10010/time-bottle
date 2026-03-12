# 拾光瓶 (Time Bottle)

> 记录生活的每一刻

一个全栈个人财务管理应用，采用前后端分离架构，帮助用户记录和管理日常收支，提供直观的数据可视化分析。

---

## 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [API 接口文档](#api-接口文档)
- [功能模块详解](#功能模块详解)
- [快速开始](#快速开始)
- [开发指南](#开发指南)
- [部署说明](#部署说明)

---

## 项目概述

### 项目信息

| 属性 | 内容 |
|------|------|
| **项目名称** | 拾光瓶 (Time Bottle) |
| **版本** | 1.0.0 |
| **架构模式** | 前后端分离 |
| **后端框架** | Spring Boot 3.2.0 |
| **前端框架** | Vue 3.5.x |
| **编程语言** | Java 21 / JavaScript |
| **数据库** | MySQL 8.x |
| **作者** | lijin |

### 核心功能

- 用户认证（注册、登录、退出）
- 用户信息管理（昵称、密码、头像）
- 财务台账（账单管理、分类管理）
- 数据可视化（饼图、柱状图、热力图）
- 数据导入导出（CSV/Excel）
- 绩效记录（占位）
- 绩效看板（占位）

---

## 技术栈

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| Spring Data JPA | - | ORM 框架 |
| Spring Security | - | 安全框架 |
| MySQL Connector | 8.x | 数据库驱动 |
| Lombok | - | 代码简化 |
| BCrypt | - | 密码加密 |

### 前端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.x | 前端框架 |
| Vite | 7.x | 构建工具 |
| Vue Router | 5.x | 路由管理 |
| Element Plus | 2.x | UI 组件库 |
| ECharts | 6.x | 图表可视化 |

---

## 项目结构

### 整体目录结构

```
time-bottle/
├── backend/                              # 后端项目
│   ├── src/main/java/com/timebottle/backend/
│   │   ├── config/                       # 配置层
│   │   │   ├── SecurityConfig.java       # Spring Security 配置
│   │   │   └── WebConfig.java            # Web MVC 配置
│   │   ├── controller/                   # 控制器层
│   │   │   ├── AuthController.java       # 认证接口
│   │   │   ├── BillController.java       # 账单接口
│   │   │   └── CategoryController.java   # 分类接口
│   │   ├── entity/                       # 实体层
│   │   │   ├── User.java                 # 用户实体
│   │   │   ├── Bill.java                 # 账单实体
│   │   │   └── BillCategory.java         # 分类实体
│   │   ├── repository/                   # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   ├── BillRepository.java
│   │   │   └── CategoryRepository.java
│   │   ├── service/                      # 业务逻辑层
│   │   │   ├── UserService.java
│   │   │   ├── BillService.java
│   │   │   └── CategoryService.java
│   │   └── BackendApplication.java       # 启动类
│   ├── src/main/resources/
│   │   └── application.properties        # 应用配置
│   └── pom.xml                           # Maven 配置
│
└── frontend/                             # 前端项目
    ├── src/
    │   ├── assets/                       # 静态资源
    │   │   └── images/login-bg.png       # 登录背景图
    │   ├── components/                   # Vue 组件
    │   │   ├── Login.vue                 # 登录组件
    │   │   ├── Register.vue              # 注册组件
    │   │   ├── UserProfileEdit.vue       # 用户信息编辑
    │   │   ├── DataControlConsoleFinancialLedger.vue  # 财务台账（核心）
    │   │   ├── DataControlConsolePerformanceRecord.vue # 绩效记录（占位）
    │   │   └── DataControlConsolePerformanceDashboard.vue # 绩效看板（占位）
    │   ├── router/
    │   │   └── index.js                  # 路由配置
    │   ├── App.vue                       # 根组件
    │   ├── main.js                       # 入口文件
    │   └── style.css                     # 全局样式
    ├── public/
    │   └── time-bottle.png               # 网站图标
    ├── package.json                      # NPM 配置
    └── vite.config.js                    # Vite 配置
```

### 后端分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                        │
│  AuthController | BillController | CategoryController        │
│                    (处理 HTTP 请求，参数校验)                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service Layer                          │
│     UserService | BillService | CategoryService             │
│                    (业务逻辑处理，事务管理)                    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Repository Layer                         │
│   UserRepository | BillRepository | CategoryRepository       │
│                    (数据访问，JPA 接口)                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Entity Layer                           │
│              User | Bill | BillCategory                      │
│                    (实体映射，数据库表对应)                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 数据库设计

### ER 关系图

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     users       │       │     bills       │       │ bill_categories │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │←──┐   │ id (PK)         │   ┌──→│ id (PK)         │
│ username        │   │   │ user_id (FK)    │───┘   │ user_id (FK)    │
│ password_hash   │   │   │ category_id(FK) │───────│ name            │
│ email           │   │   │ type            │       │ type            │
│ role            │   │   │ account         │       │ is_default      │
│ points          │   │   │ amount          │       │ sort            │
│ status          │   │   │ remark          │       │ is_deleted      │
│ avatar          │   │   │ bill_date       │       └─────────────────┘
│ nickname        │   │   │ bill_time       │
│ last_login_at   │   │   │ is_deleted      │
│ created_at      │   │   │ created_at      │
│ updated_at      │   │   │ updated_at      │
└─────────────────┘   │   └─────────────────┘
                      │
                      └─── 1:N 关系
```

### 表关系说明

| 关系 | 类型 | 说明 |
|------|------|------|
| users → bills | 1:N | 一个用户可以有多条账单记录 |
| users → bill_categories | 1:N | 一个用户可以有多个自定义分类 |
| bill_categories → bills | 1:N | 一个分类可以对应多条账单 |

### 数据库建表 SQL

#### 用户表 (users)

```sql
CREATE TABLE `users` ( 
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户唯一ID，自增主键', 
    `username` VARCHAR(50) NOT NULL COMMENT '登录账号，唯一', 
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希值（使用bcrypt加密）', 
    `email` VARCHAR(100) NOT NULL COMMENT '电子邮箱，唯一', 
    `role` ENUM('0', '1', '2') NOT NULL DEFAULT '0' COMMENT '用户角色：0-普通用户，1-VIP用户，2-管理员', 
    `points` INT NOT NULL DEFAULT '0' COMMENT '用户积分', 
    `status` ENUM('0', '1') NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用，1-正常', 
    `avatar` VARCHAR(255) DEFAULT 'default-avatar.png' COMMENT '头像文件名', 
    `nickname` VARCHAR(50) DEFAULT '未命名' COMMENT '昵称', 
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间', 
    `created_at` DATETIME NOT NULL COMMENT '注册时间', 
    `updated_at` DATETIME NOT NULL COMMENT '用户信息最后更新时间', 
    PRIMARY KEY (`id`), 
    UNIQUE KEY `uniq_username` (`username`), 
    UNIQUE KEY `uniq_email` (`email`), 
    KEY `idx_status` (`status`), 
    KEY `idx_role` (`role`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';
```

#### 分类表 (bill_categories)

```sql
CREATE TABLE `bill_categories` ( 
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID', 
    `user_id` INT UNSIGNED NOT NULL COMMENT '所属用户ID（0=系统默认分类）', 
    `name` VARCHAR(30) NOT NULL COMMENT '分类名称（如餐饮、工资、房租）', 
    `type` ENUM('0', '1') NOT NULL COMMENT '分类类型：0=支出，1=收入', 
    `is_default` ENUM('0', '1') DEFAULT '0' COMMENT '是否系统默认：0=用户自定义，1=系统默认', 
    `sort` TINYINT UNSIGNED DEFAULT 0 COMMENT '排序值（数字越小越靠前）', 
    `is_deleted` ENUM('0', '1') DEFAULT '0' COMMENT '软删除：0=正常，1=删除', 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
    PRIMARY KEY (`id`), 
    KEY `idx_user_type` (`user_id`, `type`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收支分类表';
```

#### 账单表 (bills)

```sql
CREATE TABLE `bills` ( 
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '账单ID', 
    `user_id` INT UNSIGNED NOT NULL COMMENT '所属用户ID', 
    `category_id` INT UNSIGNED NOT NULL COMMENT '分类ID', 
    `type` ENUM('0', '1') NOT NULL COMMENT '收支类型：0=支出，1=收入', 
    `account` ENUM('现金','微信','支付宝','银行卡','其他') NOT NULL DEFAULT '现金' COMMENT '支付/收款账户', 
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额（保留2位小数）', 
    `remark` VARCHAR(150) DEFAULT '' COMMENT '账单备注', 
    `bill_date` DATE NOT NULL COMMENT '账单发生日期', 
    `bill_time` TIME DEFAULT NULL COMMENT '账单发生时间', 
    `is_deleted` ENUM('0', '1') DEFAULT '0' COMMENT '软删除：0=正常，1=删除', 
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
    PRIMARY KEY (`id`), 
    KEY `idx_user_date` (`user_id`, `bill_date`), 
    KEY `idx_category_id` (`category_id`), 
    KEY `idx_account` (`account`) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单记录表';
```

### 字段枚举值说明

| 字段 | 枚举值 | 说明 |
|------|--------|------|
| users.role | '0' | 普通用户 |
| users.role | '1' | VIP用户 |
| users.role | '2' | 管理员 |
| users.status | '0' | 账号禁用 |
| users.status | '1' | 账号正常 |
| bills.type / bill_categories.type | '0' | 支出 |
| bills.type / bill_categories.type | '1' | 收入 |
| bills.account | '现金' / '微信' / '支付宝' / '银行卡' / '其他' | 支付账户类型 |
| is_deleted / is_default | '0' | 否 |
| is_deleted / is_default | '1' | 是 |

---

## API 接口文档

### 基础信息

- **后端地址**: `http://localhost:8080`
- **数据格式**: JSON
- **认证方式**: 无（当前版本未实现 Token 认证）

### 认证接口

#### 用户注册

```
POST /api/auth/register
```

**请求参数**:
```json
{
  "username": "test",
  "password": "123456",
  "email": "test@example.com"
}
```

**响应示例**:
```json
{
  "message": "注册成功",
  "user": {
    "id": 1,
    "username": "test",
    "email": "test@example.com",
    "nickname": "未命名",
    "avatar": "default-avatar.svg",
    "role": "0"
  }
}
```

#### 用户登录

```
POST /api/auth/login
```

**请求参数**:
```json
{
  "username": "test",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "message": "登录成功",
  "user": {
    "id": 1,
    "username": "test",
    "avatar": "default-avatar.svg",
    "nickname": "测试用户",
    "role": "0"
  }
}
```

#### 修改用户信息

```
PUT /api/auth/profile
Content-Type: multipart/form-data
```

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名 |
| nickname | String | 否 | 昵称 |
| password | String | 否 | 新密码（留空不修改） |
| avatar | File | 否 | 头像文件（最大2MB） |

**响应示例**:
```json
{
  "message": "修改成功",
  "user": {
    "id": 1,
    "username": "test",
    "avatar": "uploads/avatars/xxx.png",
    "nickname": "新昵称"
  }
}
```

### 分类接口

#### 获取所有分类

```
GET /api/categories?userId={userId}
```

**响应示例**:
```json
{
  "categories": [
    {
      "id": 1,
      "userId": 0,
      "name": "餐饮",
      "type": "0",
      "isDefault": "1",
      "sort": 0,
      "isDeleted": "0"
    }
  ]
}
```

#### 按类型获取分类

```
GET /api/categories/type/{type}?userId={userId}
```

| 参数 | 说明 |
|------|------|
| type | 0=支出分类，1=收入分类 |

#### 创建分类

```
POST /api/categories
```

**请求参数**:
```json
{
  "name": "交通",
  "type": "0",
  "userId": 1,
  "sort": 0,
  "isDefault": false
}
```

#### 更新分类

```
PUT /api/categories/{id}
```

**请求参数**:
```json
{
  "name": "交通出行",
  "type": "0",
  "sort": 1,
  "userRole": "0"
}
```

#### 删除分类

```
DELETE /api/categories/{id}?userRole={userRole}
```

**注意**: 如果分类下存在账单记录，则无法删除。

### 账单接口

#### 获取账单列表（支持筛选）

```
GET /api/bills?page=1&pageSize=10&userId={userId}
```

**支持的筛选参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| date | String | 账单日期 (YYYY-MM-DD) |
| type | String | 收支类型 (0/1) |
| categoryId | Integer | 分类ID |
| account | String | 账户类型 |
| minAmount | Double | 最小金额 |
| maxAmount | Double | 最大金额 |
| remark | String | 备注关键词 |
| page | Integer | 页码 |
| pageSize | Integer | 每页数量 |
| userId | Integer | 用户ID |

**响应示例**:
```json
{
  "bills": [
    {
      "id": 1,
      "userId": 1,
      "categoryId": 1,
      "type": "0",
      "account": "微信",
      "amount": 35.50,
      "remark": "午餐",
      "billDate": "2026-03-10",
      "billTime": "12:30"
    }
  ],
  "total": 100,
  "page": 1,
  "pageSize": 10
}
```

#### 获取所有账单

```
GET /api/bills/all?page=1&pageSize=1000&userId={userId}
```

#### 创建账单

```
POST /api/bills
```

**请求参数**:
```json
{
  "userId": 1,
  "category_id": 1,
  "type": 0,
  "account": "微信",
  "amount": 35.50,
  "bill_date": "2026-03-10",
  "bill_time": "12:30",
  "remark": "午餐"
}
```

#### 更新账单

```
PUT /api/bills/{id}
```

**请求参数**:
```json
{
  "category_id": 2,
  "type": 0,
  "account": "现金",
  "amount": 40.00,
  "bill_date": "2026-03-10",
  "bill_time": "12:00",
  "remark": "午餐-修改"
}
```

#### 删除账单

```
DELETE /api/bills/{id}
```

---

## 功能模块详解

### 1. 用户认证模块

#### 前端组件
- `Login.vue` - 登录表单
- `Register.vue` - 注册表单

#### 实现逻辑
1. 用户输入用户名和密码
2. 前端调用 `/api/auth/login` 接口
3. 后端验证用户名和密码（BCrypt）
4. 验证成功后返回用户信息
5. 前端将用户信息存储到 `localStorage`
6. `App.vue` 监听登录状态，更新 UI

#### 状态管理
```javascript
// App.vue 中的登录状态管理
const isLoggedIn = ref(false);
const userInfo = ref({ username: '用户名', avatar: '', nickname: '未命名' });

// 从 localStorage 恢复登录状态
onMounted(() => {
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    isLoggedIn.value = true;
    userInfo.value = JSON.parse(savedUser);
  }
});
```

### 2. 财务台账模块

#### 前端组件
- `DataControlConsoleFinancialLedger.vue` - 核心组件（约4000行代码）

#### 功能子模块

##### 2.1 统计卡片
- 显示总收入、总支出、结余
- 根据当前筛选条件实时计算

##### 2.2 财务趋势图表
- 时间范围选择：本周 / 本月 / 自定义
- ECharts 折线图展示收支趋势
- 支持月份选择器和日期范围选择

##### 2.3 账单管理
- 多条件筛选（日期、类型、分类、账户、金额范围、备注）
- 分页显示
- 添加 / 编辑 / 删除账单
- 使用 Element Plus 的日期和时间选择器

##### 2.4 分类管理
- 支出分类和收入分类分开显示
- 支持添加 / 编辑 / 删除自定义分类
- 系统默认分类受保护（仅管理员可操作）
- 支持排序值设置

##### 2.5 数据可视化
- **饼图**：按月显示各分类占比
- **柱状图**：月度收支对比（支持最近12个月或指定年份）
- **热力图**：年度收支分布（按月份和星期统计）

##### 2.6 导入导出
- **导出**：支持 CSV 和 Excel 格式
- **导入**：支持 CSV 格式（Excel 导入开发中）

### 3. 用户信息管理

#### 前端组件
- `UserProfileEdit.vue` - 用户信息编辑弹窗

#### 功能
- 修改昵称
- 修改密码（留空不修改）
- 上传头像（支持 jpg/png/gif/webp，最大 2MB）

### 4. 绩效模块（占位）

#### 前端组件
- `DataControlConsolePerformanceRecord.vue` - 绩效记录
- `DataControlConsolePerformanceDashboard.vue` - 绩效看板

当前状态：显示"功能开发中"占位页面。

---

## 快速开始

### 环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 21+ |
| Node.js | 18+ |
| MySQL | 8.x |
| Maven | 3.8+ |

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE time_bottle DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行建表 SQL（见 [数据库设计](#数据库设计) 章节）

3. 插入系统默认分类（可选）：
```sql
INSERT INTO bill_categories (user_id, name, type, is_default, sort) VALUES
(0, '餐饮', '0', '1', 0),
(0, '交通', '0', '1', 1),
(0, '购物', '0', '1', 2),
(0, '娱乐', '0', '1', 3),
(0, '工资', '1', '1', 0),
(0, '奖金', '1', '1', 1),
(0, '理财', '1', '1', 2);
```

### 后端启动

```bash
# 进入后端目录
cd backend

# 配置数据库连接（修改 application.properties）
# spring.datasource.url=jdbc:mysql://localhost:3306/time_bottle
# spring.datasource.username=root
# spring.datasource.password=your_password

# 启动后端服务
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务运行在 `http://localhost:5173`

### 访问应用

打开浏览器访问 `http://localhost:5173`，即可看到登录页面。

---

## 开发指南

### 后端开发

#### 添加新的 API 接口

1. 在 `entity/` 下创建实体类
2. 在 `repository/` 下创建 Repository 接口
3. 在 `service/` 下创建 Service 类
4. 在 `controller/` 下创建 Controller 类
5. 在 `SecurityConfig.java` 中添加路径白名单（如需要）

#### 实体类示例

```java
@Entity
@Table(name = "table_name")
public class NewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### 前端开发

#### 添加新页面

1. 在 `src/components/` 下创建 Vue 组件
2. 在 `src/router/index.js` 中添加路由配置
3. 在 `App.vue` 的侧边栏导航中添加菜单项

#### 路由配置示例

```javascript
const routes = [
  {
    path: '/new-page',
    name: 'NewPage',
    component: () => import('./components/NewPage.vue')
  }
];
```

#### API 调用示例

```javascript
const fetchData = async () => {
  const response = await fetch('http://localhost:8080/api/endpoint', {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' }
  });
  const data = await response.json();
  return data;
};
```

### 代码规范

- 后端使用 Lombok 简化代码
- 前端使用 Vue 3 Composition API
- 数据库字段使用下划线命名
- Java 类使用驼峰命名

---

## 部署说明

### 后端打包

```bash
cd backend
mvn clean package -DskipTests
```

生成的 JAR 文件位于 `target/backend-0.0.1-SNAPSHOT.jar`

### 前端打包

```bash
cd frontend
npm run build
```

生成的静态文件位于 `dist/` 目录

### 生产环境配置

1. 修改后端 `application.properties`：
```properties
spring.datasource.url=jdbc:mysql://prod-db-host:3306/time_bottle
spring.datasource.username=prod_user
spring.datasource.password=prod_password
```

2. 修改前端 API 地址（建议使用环境变量）

3. 配置 Nginx 反向代理

---

## 页面路由

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 财务台账 | 首页，显示收支统计 |
| `/financial-ledger` | 财务台账 | 收支管理主页面 |
| `/performance-record` | 绩效记录 | 待开发 |
| `/performance-dashboard` | 绩效看板 | 待开发 |

---

## 项目特色

- 现代化技术栈（Spring Boot 3 + Vue 3 + Java 21）
- 前后端分离架构，便于维护和扩展
- 精美 UI 设计（毛玻璃效果、渐变色、动画）
- 完善的数据可视化（ECharts 多图表类型）
- 数据导入导出（CSV/Excel）
- 软删除机制（数据可恢复）
- 响应式设计（支持移动端）
- 系统默认分类（新用户开箱即用）

---

## 待开发功能

- [ ] 绩效记录模块
- [ ] 绩效看板模块
- [ ] JWT Token 认证
- [ ] 数据统计报表
- [ ] 预算管理
- [ ] 账户管理
- [ ] 多币种支持
- [ ] 数据备份恢复
- [ ] Excel 导入功能
- [ ] 移动端适配优化

---

## 常见问题

### Q: 后端启动报错 "Communications link failure"
A: 检查 MySQL 服务是否启动，数据库连接配置是否正确。

### Q: 前端无法调用后端 API
A: 检查后端是否启动在 8080 端口，CORS 配置是否正确。

### Q: 头像上传失败
A: 检查 `uploads/avatars` 目录是否存在且有写入权限。

### Q: 分类删除失败
A: 检查该分类下是否存在账单记录，存在则无法删除。

---

## 更新日志

### v1.0.0 (2026-03-01)

#### 新增功能
- 用户注册、登录、退出功能
- 用户信息修改（昵称、密码、头像）
- 财务台账模块
  - 账单增删改查
  - 分类管理（收入/支出分类）
  - 数据可视化（饼图、柱状图、热力图）
  - 数据导入导出（CSV/Excel）
  - 多条件筛选查询
- 绩效记录模块（占位）
- 绩效看板模块（占位）

#### UI 优化
- 侧边栏毛玻璃效果
- 渐变色动态背景
- 平滑动画过渡
- 响应式布局
- Element Plus Message 消息提示
