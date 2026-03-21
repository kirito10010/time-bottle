# 拾光瓶 (Time Bottle)

一个现代化的个人财务管理与绩效追踪系统，采用前后端分离架构。

---

## 项目概述

拾光瓶是一个综合性的个人管理平台，主要包含两大核心功能：

1. **财务台账** - 个人收支管理、账单记录、数据可视化
2. **绩效管理** - 每日绩效记录、加班管理、薪资计算

---

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| Spring Data JPA | - | ORM框架 |
| Spring Security | - | 安全框架（BCrypt密码加密） |
| Spring Mail | - | 邮件服务 |
| MySQL | 8.x | 数据库 |
| Lombok | - | 代码简化 |
| Java | 21 | 编程语言 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.25 | 前端框架 |
| Vite | 7.3.1 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Element Plus | 2.13.2 | UI组件库 |
| ECharts | 6.0.0 | 图表可视化 |
| flatpickr | 4.6.13 | 日期选择器 |

---

## 项目结构

```
time-bottle/
├── backend/                              # 后端项目
│   ├── src/main/java/com/timebottle/backend/
│   │   ├── config/                       # 配置层
│   │   │   ├── SecurityConfig.java       # Spring Security配置
│   │   │   └── WebConfig.java            # Web MVC配置
│   │   ├── controller/                   # 控制器层
│   │   │   ├── AuthController.java       # 认证接口
│   │   │   ├── BillController.java       # 账单接口
│   │   │   ├── CategoryController.java   # 分类接口
│   │   │   ├── UserController.java       # 用户接口
│   │   │   ├── DailyPerformanceController.java    # 绩效记录
│   │   │   ├── MonthlySalaryRecordController.java # 薪资记录
│   │   │   ├── OvertimeRecordController.java      # 加班记录
│   │   │   ├── ProductionProjectConfigController.java # 项目配置
│   │   │   └── PasswordResetController.java       # 密码重置
│   │   ├── entity/                       # 实体层
│   │   ├── repository/                   # 数据访问层
│   │   ├── service/                      # 业务逻辑层
│   │   └── BackendApplication.java       # 启动类
│   ├── src/main/resources/
│   │   └── application.properties        # 应用配置
│   └── pom.xml                           # Maven配置
│
├── frontend/                             # 前端项目
│   ├── src/
│   │   ├── components/                   # Vue组件
│   │   │   ├── Login.vue                 # 登录组件
│   │   │   ├── Register.vue              # 注册组件
│   │   │   ├── UserProfileEdit.vue       # 用户信息编辑
│   │   │   ├── DataControlConsoleFinancialLedger.vue  # 财务台账
│   │   │   ├── DataControlConsolePerformanceRecord.vue # 绩效记录
│   │   │   └── DataControlConsolePerformanceDashboard.vue # 绩效看板
│   │   ├── router/index.js               # 路由配置
│   │   ├── App.vue                       # 根组件
│   │   ├── main.js                       # 入口文件
│   │   └── style.css                     # 全局样式
│   ├── package.json                      # NPM配置
│   └── vite.config.js                    # Vite配置
```

---

## 功能模块

### 1. 用户认证模块
- 用户注册（用户名、邮箱、密码）
- 用户登录（BCrypt密码验证）
- 密码找回（邮箱验证码，有效期5分钟）
- 用户信息修改（昵称、密码、头像上传）
- 头像上传（最大2MB）

### 2. 财务台账模块
- **账单管理**：增删改查、批量删除
- **多条件筛选**：日期、类型、分类、账户、金额范围、备注
- **分类管理**：收入/支出分类，支持自定义分类
- **数据可视化**：
  - 饼图：分类占比
  - 柱状图：月度收支趋势
  - 热力图：消费习惯分析
- **数据导入导出**：CSV/Excel格式

### 3. 绩效管理模块
- **每日绩效记录**：记录工作量、绩效工时
- **加班记录管理**：加班时长、项目关联
- **月度薪资计算**：
  - 自动计算周期（每月26日至次月25日）
  - 绩效总和 = 总绩效 - 底量扣除 - 加班扣除
  - 绩效薪资 = 绩效总和 × 170元 × 94%
  - 加班薪资 = 加班时长 × 17元
- **工资配置**：可自定义各项薪资参数，保存到浏览器

### 4. 前端路由

| 路由 | 页面 | 说明 |
|------|------|------|
| `/` | 财务台账 | 首页，收支管理 |
| `/financial-ledger` | 财务台账 | 收支管理主页面 |
| `/performance-record` | 绩效记录 | 绩效数据录入 |
| `/performance-dashboard` | 绩效看板 | 绩效数据展示 |

---

## API 接口

### 认证接口 `/api/auth`

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/register` | 用户注册 |
| POST | `/login` | 用户登录 |
| PUT | `/profile` | 修改用户信息（支持头像上传） |
| POST | `/forgot-password` | 发送密码重置验证码 |
| POST | `/verify-reset-code` | 验证重置验证码 |
| POST | `/reset-password` | 重置密码 |

### 账单接口 `/api/bills`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取账单列表（支持多条件筛选） |
| GET | `/all` | 获取所有账单 |
| POST | `/` | 创建账单 |
| PUT | `/{id}` | 更新账单 |
| DELETE | `/{id}` | 删除账单 |

**筛选参数**: `date`, `type`, `categoryId`, `account`, `minAmount`, `maxAmount`, `remark`, `userId`, `page`, `pageSize`

### 分类接口 `/api/categories`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取所有分类 |
| GET | `/type/{type}` | 按类型获取分类 |
| POST | `/` | 创建分类 |
| PUT | `/{id}` | 更新分类 |
| DELETE | `/{id}` | 删除分类 |

### 绩效记录接口 `/api/daily-performances`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取绩效记录列表 |
| POST | `/` | 创建绩效记录 |
| PUT | `/{id}` | 更新绩效记录 |
| DELETE | `/{id}` | 删除绩效记录 |
| DELETE | `/batch` | 批量删除 |

### 加班记录接口 `/api/overtime-records`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取加班记录列表 |
| POST | `/` | 创建加班记录 |
| PUT | `/{id}` | 更新加班记录 |
| DELETE | `/{id}` | 删除加班记录 |
| DELETE | `/batch` | 批量删除 |

### 薪资记录接口 `/api/salary-records`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取薪资记录列表 |
| POST | `/` | 创建薪资记录 |
| PUT | `/{id}` | 更新薪资记录 |
| DELETE | `/{id}` | 删除薪资记录 |
| DELETE | `/batch` | 批量删除 |

### 项目配置接口 `/api/project-configs`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取项目配置列表 |
| POST | `/` | 创建项目配置 |
| PUT | `/{id}` | 更新项目配置 |
| DELETE | `/{id}` | 删除项目配置 |
| DELETE | `/batch` | 批量删除 |

---

## 数据库表结构

### 用户表 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password_hash | VARCHAR(255) | 密码哈希（BCrypt） |
| email | VARCHAR(100) | 邮箱，唯一 |
| role | ENUM('0','1','2') | 角色：0-普通用户，1-VIP，2-管理员 |
| points | INT | 积分，默认0 |
| status | ENUM('0','1') | 状态：0-禁用，1-正常 |
| avatar | VARCHAR(255) | 头像文件名，默认 default-avatar.png |
| nickname | VARCHAR(50) | 昵称，默认"未命名" |
| last_login_at | DATETIME | 最后登录时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 账单表 (bills)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT UNSIGNED | 主键，自增 |
| user_id | INT UNSIGNED | 用户ID |
| category_id | INT UNSIGNED | 分类ID |
| type | ENUM('0','1') | 类型：0-支出，1-收入 |
| account | ENUM | 账户：现金/微信/支付宝/银行卡/其他 |
| amount | DECIMAL(10,2) | 金额 |
| remark | VARCHAR(150) | 备注 |
| bill_date | DATE | 账单日期 |
| bill_time | TIME | 账单时间 |
| is_deleted | ENUM('0','1') | 软删除标记，默认0 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 分类表 (bill_categories)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT UNSIGNED | 主键，自增 |
| user_id | INT UNSIGNED | 用户ID（0=系统默认） |
| name | VARCHAR(30) | 分类名称 |
| type | ENUM('0','1') | 类型：0-支出，1-收入 |
| is_default | ENUM('0','1') | 是否系统默认 |
| sort | TINYINT UNSIGNED | 排序值 |
| is_deleted | ENUM('0','1') | 软删除标记，默认0 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 每日绩效表 (daily_performance)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| uid | VARCHAR(50) | 用户标识 |
| record_date | DATE | 记录日期 |
| project_id | BIGINT | 项目ID |
| process_type | ENUM('作业','质检') | 工序类型 |
| quota_efficiency | DECIMAL(10,2) | 额定效率 |
| actual_workload | DECIMAL(10,2) | 实际工作量 |
| performance_man_days | DECIMAL(10,5) | 绩效人天合计（计算列） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 加班记录表 (overtime_record)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| uid | VARCHAR(50) | 用户标识 |
| record_date | DATE | 记录日期 |
| overtime_hours | DECIMAL(5,2) | 加班时长 |
| project_id | BIGINT | 项目ID |
| description | VARCHAR(500) | 描述 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 月度薪资表 (monthly_salary_record)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| uid | VARCHAR(50) | 用户标识 |
| month | CHAR(7) | 月份（YYYY-MM） |
| period_start_date | DATE | 周期开始日期 |
| period_end_date | DATE | 周期结束日期 |
| attendance_days | DECIMAL(5,2) | 出勤天数 |
| basic_salary | DECIMAL(12,5) | 基本工资 |
| performance_coefficient | DECIMAL(7,5) | 绩效总和 |
| performance_salary | DECIMAL(12,5) | 绩效工资 |
| position_performance | DECIMAL(12,5) | 岗位绩效 |
| meal_allowance | DECIMAL(12,5) | 餐补 |
| housing_allowance | DECIMAL(12,5) | 房补 |
| full_attendance_bonus | DECIMAL(12,5) | 全勤奖 |
| other_bonus | DECIMAL(12,5) | 其他奖金 |
| pension_insurance | DECIMAL(12,5) | 养老保险 |
| medical_insurance | DECIMAL(12,5) | 医疗保险 |
| unemployment_insurance | DECIMAL(12,5) | 失业保险 |
| late_deduction | DECIMAL(12,5) | 迟到扣款 |
| overtime_hours | DECIMAL(5,2) | 加班时长 |
| overtime_salary | DECIMAL(12,5) | 加班工资 |
| total_payable | DECIMAL(12,5) | 应发总额 |
| total_deduction | DECIMAL(12,5) | 扣款总额 |
| net_salary | DECIMAL(12,5) | 实发工资 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 项目配置表 (production_project_config)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| uid | VARCHAR(50) | 用户标识 |
| project_name | VARCHAR(100) | 项目名称 |
| operation_quota | DECIMAL(10,2) | 作业定额 |
| quality_quota | DECIMAL(10,2) | 质检定额 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 密码重置令牌表 (password_reset_token)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| email | VARCHAR(100) | 邮箱 |
| token | VARCHAR(10) | 验证码（6位数字） |
| expires_at | DATETIME | 过期时间（5分钟） |
| used | BOOLEAN | 是否已使用 |
| created_at | DATETIME | 创建时间 |

---

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 后端启动

```bash
# 1. 创建数据库
mysql -u root -p
CREATE DATABASE time_bottle CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. 导入数据表（执行建表SQL）

# 3. 修改配置文件
# 编辑 backend/src/main/resources/application.properties
# 修改数据库连接信息和邮件配置

# 4. 启动后端
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
# 1. 安装依赖
cd frontend
npm install

# 2. 启动开发服务器
npm run dev

# 3. 构建生产版本
npm run build
```

### 访问地址
- 前端开发服务器：http://localhost:5173
- 后端API服务：http://localhost:8080

---

## 配置说明

### 后端配置 (application.properties)

```properties
# 服务器端口
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/time_bottle?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password

# JPA配置
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# 文件上传配置
app.upload.avatar-path=./uploads/avatars
app.upload.max-size=2097152
spring.servlet.multipart.max-file-size=2MB

# 邮件配置（用于密码找回）
spring.mail.host=smtp.163.com
spring.mail.port=465
spring.mail.username=your_email@163.com
spring.mail.password=your_email_password
spring.mail.properties.mail.smtp.ssl.enable=true
```

### 前端配置 (vite.config.js)

```javascript
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

## 项目特色

1. **现代化技术栈**：Spring Boot 3 + Vue 3 + Java 21
2. **前后端分离**：便于维护和扩展
3. **精美UI设计**：毛玻璃效果、渐变色、动画过渡
4. **数据可视化**：ECharts多图表类型
5. **数据导入导出**：支持CSV/Excel格式
6. **软删除机制**：数据可恢复
7. **系统默认分类**：新用户开箱即用
8. **邮件验证**：支持密码找回功能
9. **工资配置持久化**：保存到浏览器localStorage

---

## 注意事项

1. **密码加密**：使用BCrypt算法，不可逆
2. **验证码有效期**：5分钟
3. **头像大小限制**：最大2MB
4. **软删除**：账单和分类使用软删除，数据不会真正删除
5. **CORS配置**：开发环境允许所有来源，生产环境需要限制

---

## 开发者备注

### 薪资计算公式
- 绩效总和 = 总绩效人天 - 考勤天数（底量扣除） - 加班小时数 × 0.125
- 绩效薪资 = 绩效总和 × 170元 × 94%
- 加班薪资 = 加班时长 × 17元
- 实发薪资 = 应发总额 - 扣款总额

### 工资周期
- 每月26日至次月25日为一个工资周期
- 选择某月份时，自动计算该月份对应的工资周期

### 默认工资配置
| 项目 | 默认值 |
|------|--------|
| 基本薪资 | 2000 |
| 岗位绩效 | 500 |
| 餐补 | 200 |
| 房补 | 300 |
| 全勤奖 | 300 |
| 其他奖金 | 100 |
| 养老保险 | 360.32 |
| 医疗保险 | 90.08 |
| 失业保险 | 13.51 |
| 迟到扣款 | 0 |

---

## License

MIT License
