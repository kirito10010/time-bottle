# 拾光瓶 (Time Bottle)

一个现代化的个人财务管理、绩效追踪与集卡游戏系统，采用前后端分离架构。

---

## 项目概述

拾光瓶是一个综合性的个人管理平台，主要包含四大核心功能模块：

1. **数据总控台** - 财务台账、绩效记录、绩效看板
2. **梦幻集卡社** - 我的图鉴、积分商城、抽取卡片、积分考试、卡牌交换
3. **管理员后台** - 用户管理、集卡管理、题目管理、反馈管理
4. **用户系统** - 登录注册、密码找回、个人资料编辑

---

## 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| Spring Data JPA | - | ORM框架 |
| Spring Security | - | 安全框架（BCrypt密码加密） |
| Spring Mail | - | 邮件服务 |
| JWT (jjwt) | 0.12.3 | Token认证 |
| MySQL | 8.x | 数据库 |
| Lombok | - | 代码简化 |
| Java | 21 | 编程语言 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.25 | 前端框架（Composition API） |
| Vite | 7.3.1 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Element Plus | 2.13.2 | UI组件库 |
| ECharts | 6.0.0 | 图表可视化 |
| flatpickr | 4.6.13 | 日期选择器 |
| xlsx | - | Excel解析 |

---

## 项目结构

```
time-bottle/
├── backend/                              # 后端项目
│   ├── src/main/java/com/timebottle/backend/
│   │   ├── config/                       # 配置层
│   │   │   ├── SecurityConfig.java       # Spring Security配置
│   │   │   └── WebConfig.java            # Web MVC配置
│   │   ├── controller/                   # 控制器层（16个）
│   │   │   ├── AuthController.java       # 认证接口
│   │   │   ├── UserController.java       # 用户接口
│   │   │   ├── BillController.java       # 账单接口
│   │   │   ├── CategoryController.java   # 分类接口
│   │   │   ├── PointsController.java     # 积分系统
│   │   │   ├── CardController.java       # 卡片系统
│   │   │   ├── CardExchangeController.java # 卡牌交换
│   │   │   ├── ConsignmentController.java   # 积分商城
│   │   │   ├── ExamController.java       # 积分考试
│   │   │   ├── QuestionController.java   # 题目管理
│   │   │   ├── PasswordResetController.java # 密码重置
│   │   │   ├── DailyPerformanceController.java    # 绩效记录
│   │   │   ├── MonthlySalaryRecordController.java # 薪资记录
│   │   │   ├── OvertimeRecordController.java      # 加班记录
│   │   │   ├── ProductionProjectConfigController.java # 项目配置
│   │   │   └── AdminController.java      # 管理员接口
│   │   ├── entity/                       # 实体层（15个）
│   │   │   ├── User.java                 # 用户实体
│   │   │   ├── Bill.java                 # 账单实体
│   │   │   ├── BillCategory.java         # 分类实体
│   │   │   ├── PointsLog.java            # 积分流水实体
│   │   │   ├── AnimeCard.java            # 卡片实体
│   │   │   ├── UserCard.java             # 用户卡片实体
│   │   │   ├── CardGift.java             # 卡牌赠送实体
│   │   │   ├── CardExchange.java         # 卡牌交换实体
│   │   │   ├── Consignment.java          # 寄售实体
│   │   │   ├── Question.java             # 题目实体
│   │   │   ├── PasswordResetToken.java   # 密码重置令牌
│   │   │   ├── DailyPerformance.java     # 日绩效实体
│   │   │   ├── OvertimeRecord.java       # 加班记录实体
│   │   │   ├── MonthlySalaryRecord.java  # 月薪记录实体
│   │   │   └── ProductionProjectConfig.java # 项目配置实体
│   │   ├── repository/                   # 数据访问层（15个）
│   │   │   ├── UserRepository.java       # 用户数据访问
│   │   │   ├── BillRepository.java       # 账单数据访问
│   │   │   ├── CategoryRepository.java   # 分类数据访问
│   │   │   ├── PointsLogRepository.java  # 积分流水数据访问
│   │   │   ├── AnimeCardRepository.java  # 卡片数据访问
│   │   │   ├── UserCardRepository.java   # 用户卡片数据访问
│   │   │   ├── CardGiftRepository.java   # 卡牌赠送数据访问
│   │   │   ├── CardExchangeRepository.java # 卡牌交换数据访问
│   │   │   ├── ConsignmentRepository.java # 寄售数据访问
│   │   │   ├── QuestionRepository.java   # 题目数据访问
│   │   │   ├── PasswordResetTokenRepository.java # 密码重置数据访问
│   │   │   ├── DailyPerformanceRepository.java # 日绩效数据访问
│   │   │   ├── OvertimeRecordRepository.java # 加班记录数据访问
│   │   │   ├── MonthlySalaryRecordRepository.java # 月薪记录数据访问
│   │   │   └── ProductionProjectConfigRepository.java # 项目配置数据访问
│   │   ├── service/                      # 业务逻辑层（15个）
│   │   │   ├── UserService.java          # 用户业务逻辑
│   │   │   ├── BillService.java          # 账单业务逻辑
│   │   │   ├── CategoryService.java      # 分类业务逻辑
│   │   │   ├── PointsService.java        # 积分业务逻辑
│   │   │   ├── CardService.java          # 卡片业务逻辑
│   │   │   ├── CardExchangeService.java  # 卡牌交换业务逻辑
│   │   │   ├── ConsignmentService.java   # 寄售业务逻辑
│   │   │   ├── ExamService.java          # 考试业务逻辑
│   │   │   ├── QuestionService.java      # 题目业务逻辑
│   │   │   ├── PasswordResetService.java # 密码重置业务逻辑
│   │   │   ├── EmailService.java         # 邮件业务逻辑
│   │   │   ├── DailyPerformanceService.java # 日绩效业务逻辑
│   │   │   ├── OvertimeRecordService.java # 加班记录业务逻辑
│   │   │   ├── MonthlySalaryRecordService.java # 月薪记录业务逻辑
│   │   │   └── ProductionProjectConfigService.java # 项目配置业务逻辑
│   │   └── util/                         # 工具类
│   │       └── JwtUtil.java              # JWT工具类
│   ├── src/main/resources/
│   │   ├── application.properties        # 应用配置
│   │   └── static/                       # 静态资源
│   │       ├── default-avatar.svg        # 默认头像（SVG格式）
│   │       └── Usersimg/                 # 用户上传头像目录
│   └── pom.xml                           # Maven配置
│
├── frontend/                             # 前端项目
│   ├── src/
│   │   ├── components/                   # Vue组件（17个）
│   │   │   ├── Login.vue                 # 登录组件
│   │   │   ├── Register.vue              # 注册组件
│   │   │   ├── Introduction.vue          # 项目介绍页
│   │   │   ├── UserProfileEdit.vue       # 用户信息编辑
│   │   │   ├── DataControlConsoleFinancialLedger.vue  # 财务台账
│   │   │   ├── DataControlConsolePerformanceRecord.vue # 绩效记录
│   │   │   ├── DataControlConsolePerformanceDashboard.vue # 绩效看板
│   │   │   ├── DreamCardClubMyAlbum.vue  # 我的图鉴
│   │   │   ├── DreamCardClubPointsExam.vue # 积分考试
│   │   │   ├── DreamCardClubPointsMall.vue # 积分商城
│   │   │   ├── DreamCardClubDrawCard.vue # 抽取卡片
│   │   │   ├── DreamCardClubCardExchange.vue # 卡牌交换
│   │   │   ├── AdminPanelUsers.vue       # 用户管理
│   │   │   ├── AdminPanelCards.vue       # 集卡管理
│   │   │   ├── AdminPanelQuestions.vue   # 题目管理
│   │   │   └── AdminPanelFeedback.vue    # 反馈管理
│   │   ├── router/index.js               # 路由配置
│   │   ├── App.vue                       # 根组件
│   │   ├── main.js                       # 入口文件
│   │   └── style.css                     # 全局样式
│   ├── package.json                      # NPM配置
│   └── vite.config.js                    # Vite配置
```

---

## 功能模块详解

### 1. 用户认证模块

#### 功能列表
- 用户注册（用户名、邮箱、密码）
- 用户登录（BCrypt密码验证，返回JWT Token）
- 密码找回（邮箱验证码，有效期5分钟）
- 用户信息修改（昵称、密码、头像上传）
- 头像上传（最大2MB，存储在服务器本地 `./uploads/avatars/` 目录）

#### 认证流程
1. 用户登录成功后，后端生成JWT Token返回给前端
2. 前端将Token存储在localStorage中
3. 后续请求在Header中携带 `Authorization: Bearer {token}`
4. 后端通过JwtUtil解析Token获取用户ID

#### 密码重置流程
1. 用户输入邮箱，点击"发送验证码"
2. 后端生成6位数字验证码，存储到 `password_reset_token` 表
3. 验证码有效期5分钟
4. 用户输入验证码和新密码
5. 后端验证验证码是否正确且未过期
6. 验证通过后更新用户密码，标记验证码已使用

### 2. 积分系统模块

#### 积分获取方式
| 操作 | 积分变动 | 说明 |
|------|---------|------|
| 每日签到 | +5 | 每天限一次 |
| 新增账单 | +10 | 记账奖励 |
| 删除账单 | -10 | 删除扣减 |
| 积分考试 | +10/30/60 | 答对3/4/5题 |
| 抽卡消耗 | -10/-90 | 单抽/十连抽 |

#### 积分流水类型 (points_log.type)
| 类型 | 说明 |
|------|------|
| `sign_in` | 签到奖励 |
| `accounting` | 记账奖励 |
| `draw` | 抽卡扣除 |
| `exam` | 考试奖励 |
| `task` | 任务奖励（预留） |
| `exchange` | 兑换消耗（预留） |
| `expire` | 积分过期（预留） |
| `admin` | 管理员调整（预留） |
| `invite` | 邀请奖励（预留） |
| `activity` | 活动奖励（预留） |

### 3. 财务台账模块

#### 功能列表
- **账单管理**：增删改查、批量删除
- **多条件筛选**：日期、类型、分类、账户、金额范围、备注
- **分类管理**：收入/支出分类，支持自定义分类
- **数据可视化**：
  - 饼图：分类占比
  - 柱状图：月度收支趋势
  - 热力图：消费习惯分析
- **数据导入导出**：CSV/Excel格式

#### 账户类型 (bills.account)
- 现金
- 微信
- 支付宝
- 银行卡
- 其他

#### 软删除机制
- 账单和分类使用软删除（`is_deleted` 字段）
- 删除时设置 `is_deleted = '1'`，数据不会真正删除
- 查询时过滤已删除数据

### 4. 绩效管理模块

#### 每日绩效记录
- 记录工作量、绩效工时
- 关联生产项目配置
- 支持作业/质检两种工序类型
- 自动计算绩效人天（实际工作量 / 额定效率）
- **绩效人天是计算列**：`performance_man_days = actual_workload / quota_efficiency`

#### 加班记录管理
- 记录加班时长（支持小数，如2.5小时）
- 关联项目
- 支持描述备注

#### 月度薪资计算
- **工资周期**：每月26日至次月25日
- **绩效计算**：绩效总和 = 总绩效人天 - 考勤天数（底量扣除） - 加班小时数 × 0.125
- **薪资公式**：
  - 绩效薪资 = 绩效总和 × 170元 × 94%
  - 加班薪资 = 加班时长 × 17元
  - 实发薪资 = 应发总额 - 扣款总额

#### 默认工资配置
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

#### 外键约束
- `daily_performance.project_id` → `production_project_config.id` (ON DELETE RESTRICT)
- `overtime_record.project_id` → `production_project_config.id` (ON DELETE RESTRICT)

### 5. 梦幻集卡社模块

#### 我的图鉴
- 展示用户收集的所有卡片
- 显示收集进度和数量
- 按稀有度分类展示
- 卡片发光效果（不同稀有度不同颜色）

#### 积分商城
- **市场**：浏览所有上架卡片，支持搜索和分页
- **我的上架**：管理自己上架的卡片
- **上架卡片**：选择卡片设置价格和数量上架
- **购买卡片**：使用积分购买其他玩家的卡片
- **交易流程**：积分从买家转移到卖家，卡片从卖家转移到买家

#### 抽取卡片
- **单抽**：消耗10积分
- **十连抽**：消耗90积分（9折优惠）
- **稀有度概率**：
  - 传说(5星)：5%
  - 史诗(4星)：10%
  - 超稀有(3星)：15%
  - 稀有(2星)：30%
  - 普通(1星)：40%
- **翻卡动画**：点击翻转显示卡片
- **发光效果**：
  - 普通(1星)：无发光
  - 稀有(2星)：绿色发光
  - 超稀有(3星)：蓝色发光
  - 史诗(4星)：紫色发光
  - 传说(5星)：金色发光

#### 积分考试
- 随机抽取5道选择题
- 无限次答题机会
- **积分奖励**：
  - 答对3题：+10积分
  - 答对4题：+30积分
  - 答对5题：+60积分
- 答题后即时显示正确答案

#### 卡牌交换
- **赠送卡牌**：选择卡片赠送给其他用户
- **卡牌交换**：
  1. 选择要送出的卡片
  2. 搜索想要的卡片（支持名称搜索）
  3. 卡片以可左右滑动的轮播形式展示
  4. 查找拥有该卡片的用户
  5. 选择交换对象发送申请
- **记录查看**：收到的赠送、待处理的交换申请、我发送的交换申请
- **交换状态**：
  - `pending` - 待处理
  - `completed` - 已完成
  - `rejected` - 已拒绝

### 6. 管理员后台模块

#### 用户管理
- 查看所有用户列表
- 搜索用户（用户名、昵称、邮箱）
- 编辑用户信息（昵称、角色、状态、积分）
- 禁用/启用用户账户
- 批量删除用户

#### 集卡管理
- 管理所有卡片
- 添加新卡片（名称、系列、类型、稀有度、图片URL）
- 编辑卡片信息
- 删除卡片

#### 题目管理
- 管理考试题库
- 添加新题目（题目内容、四个选项、正确答案）
- 编辑题目
- 删除题目
- 搜索题目

#### 反馈管理
- 查看用户反馈
- 处理反馈状态

---

## 前端路由

| 路由 | 页面名称 | 组件 | 权限 |
|------|---------|------|------|
| `/` | 项目介绍 | Introduction | 公开 |
| `/login` | 登录 | Login | 公开 |
| `/register` | 注册 | Register | 公开 |
| `/financial-ledger` | 财务台账 | DataControlConsoleFinancialLedger | 需登录 |
| `/performance-record` | 绩效记录 | DataControlConsolePerformanceRecord | 需登录 |
| `/performance-dashboard` | 绩效看板 | DataControlConsolePerformanceDashboard | 需登录 |
| `/my-album` | 我的图鉴 | DreamCardClubMyAlbum | 需登录 |
| `/points-exam` | 积分考试 | DreamCardClubPointsExam | 需登录 |
| `/points-mall` | 积分商城 | DreamCardClubPointsMall | 需登录 |
| `/draw-card` | 抽取卡片 | DreamCardClubDrawCard | 需登录 |
| `/card-exchange` | 卡牌交换 | DreamCardClubCardExchange | 需登录 |
| `/admin-users` | 用户管理 | AdminPanelUsers | 管理员 |
| `/admin-cards` | 集卡管理 | AdminPanelCards | 管理员 |
| `/admin-questions` | 题目管理 | AdminPanelQuestions | 管理员 |
| `/admin-feedback` | 反馈管理 | AdminPanelFeedback | 管理员 |

---

## API 接口文档

### 认证接口 `/api/auth`

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| POST | `/register` | 用户注册 | `{username, password, email}` |
| POST | `/login` | 用户登录 | `{username, password}` |
| PUT | `/profile` | 修改用户信息 | FormData (nickname, password, avatar) |
| POST | `/forgot-password` | 发送密码重置验证码 | `{email}` |
| POST | `/verify-reset-code` | 验证重置验证码 | `{email, code}` |
| POST | `/reset-password` | 重置密码 | `{email, code, newPassword}` |

### 用户接口 `/api/user`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/info` | 获取当前用户信息 |

### 积分接口 `/api/points`

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/sign-in` | 每日签到（+5积分） |
| GET | `/status` | 获取签到状态和积分 |
| GET | `/logs` | 获取积分流水 |
| GET | `/balance` | 获取积分余额 |

### 卡片接口 `/api/cards`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取所有卡片 |
| GET | `/{id}` | 获取单个卡片 |
| GET | `/type/{type}` | 按类型获取卡片 |
| GET | `/user` | 获取用户拥有的卡片 |
| POST | `/draw` | 抽卡（支持单抽和多抽） |
| POST | `/` | 创建卡片（管理员） |
| PUT | `/{id}` | 更新卡片（管理员） |
| DELETE | `/{id}` | 删除卡片（管理员） |

### 卡牌交换接口 `/api/card-exchange`

| 方法 | 路径 | 功能 | 请求体/参数 |
|------|------|------|-------------|
| GET | `/search-users` | 搜索用户 | `keyword` |
| GET | `/find-users-with-card` | 查找拥有某卡片的用户 | `cardId, minQuantity` |
| GET | `/my-cards` | 获取我的卡片 | - |
| GET | `/search-cards` | 搜索卡片 | `keyword` (可选) |
| POST | `/gift` | 赠送卡片 | `{receiverId, cardId, quantity}` |
| POST | `/exchange` | 发送交换申请 | `{receiverId, senderCardId, senderCardQuantity, receiverCardId, receiverCardQuantity}` |
| POST | `/exchange/{id}/accept` | 接受交换 | - |
| POST | `/exchange/{id}/reject` | 拒绝交换 | - |
| GET | `/gifts/received` | 获取收到的赠送 | - |
| GET | `/exchanges/pending` | 获取待处理的交换申请 | - |
| GET | `/exchanges/my-requests` | 获取我发送的交换申请 | - |

### 积分商城接口 `/api/consignments`

| 方法 | 路径 | 功能 | 请求体/参数 |
|------|------|------|-------------|
| GET | `/` | 获取商城列表 | `keyword, page, size` |
| GET | `/my` | 获取我的上架 | - |
| GET | `/sellable` | 获取可上架卡片 | - |
| POST | `/list` | 上架卡片 | `{cardId, unitPrice, quantity}` |
| POST | `/delist/{id}` | 下架卡片 | - |
| POST | `/buy/{id}` | 购买卡片 | `{quantity}` |

### 积分考试接口 `/api/exam`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/questions` | 获取随机5道题目 |
| POST | `/submit` | 提交答案获取积分 |

### 题目管理接口 `/api/questions`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取题目列表（分页） |
| POST | `/` | 创建题目 |
| PUT | `/{id}` | 更新题目 |
| DELETE | `/{id}` | 删除题目 |

### 账单接口 `/api/bills`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取账单列表（支持多条件筛选） |
| GET | `/all` | 获取所有账单 |
| POST | `/` | 创建账单（+10积分） |
| PUT | `/{id}` | 更新账单 |
| DELETE | `/{id}` | 删除账单（-10积分） |

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

### 管理员接口 `/api/admin`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/users` | 获取用户列表 |
| PUT | `/users/{id}` | 更新用户信息 |
| DELETE | `/users/{id}` | 删除用户 |
| DELETE | `/users/batch` | 批量删除用户 |

---

## 数据库表结构

> **注意**：以下表结构基于 `d:\Project\time-bottle\123.txt` 文件中的建表SQL，共15张表。

### 1. 用户表 (users)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | INT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| username | VARCHAR(50) | - | 用户名，唯一 |
| password_hash | VARCHAR(255) | - | 密码哈希（BCrypt） |
| email | VARCHAR(100) | - | 邮箱，唯一 |
| role | ENUM('0','1','2') | '0' | 角色：0-普通用户，1-VIP，2-管理员 |
| points | INT | 0 | 积分 |
| status | ENUM('0','1') | '1' | 状态：0-禁用，1-正常 |
| avatar | VARCHAR(255) | 'default-avatar.svg' | 头像文件名 |
| nickname | VARCHAR(50) | '未命名' | 昵称 |
| last_login_at | DATETIME | NULL | 最后登录时间 |
| created_at | DATETIME | - | 创建时间 |
| updated_at | DATETIME | - | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uniq_username` (`username`)
- UNIQUE KEY `uniq_email` (`email`)
- KEY `idx_status` (`status`)
- KEY `idx_role` (`role`)

### 2. 积分流水表 (points_log)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| user_id | INT UNSIGNED | - | 用户ID |
| change | INT | - | 积分变动值（正数为获得，负数为消耗） |
| type | VARCHAR(20) | - | 变动类型 |
| remark | VARCHAR(255) | NULL | 备注 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 记录时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_user_id` (`user_id`)

**type 字段可选值**：
- `sign_in` - 签到奖励
- `task` - 任务奖励
- `accounting` - 记账奖励
- `draw` - 抽卡扣除
- `exchange` - 兑换消耗
- `expire` - 积分过期
- `admin` - 管理员调整
- `invite` - 邀请奖励
- `activity` - 活动奖励

### 3. 密码重置令牌表 (password_reset_token)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| email | VARCHAR(100) | - | 邮箱地址 |
| token | VARCHAR(10) | - | 验证码（6位数字） |
| expires_at | DATETIME | - | 过期时间（5分钟） |
| used | BOOLEAN | FALSE | 是否已使用 |
| created_at | DATETIME | - | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_email` (`email`)
- KEY `idx_token` (`token`)

### 4. 收支分类表 (bill_categories)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | INT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| user_id | INT UNSIGNED | - | 所属用户ID（0=系统默认分类） |
| name | VARCHAR(30) | - | 分类名称 |
| type | ENUM('0','1') | - | 分类类型：0=支出，1=收入 |
| is_default | ENUM('0','1') | '0' | 是否系统默认：0=用户自定义，1=系统默认 |
| sort | TINYINT UNSIGNED | 0 | 排序值（数字越小越靠前） |
| is_deleted | ENUM('0','1') | '0' | 软删除：0=正常，1=删除 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_user_type` (`user_id`, `type`)

### 5. 账单记录表 (bills)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| user_id | INT UNSIGNED | - | 所属用户ID |
| category_id | INT UNSIGNED | - | 分类ID |
| type | ENUM('0','1') | - | 收支类型：0=支出，1=收入 |
| account | ENUM('现金','微信','支付宝','银行卡','其他') | '现金' | 支付/收款账户 |
| amount | DECIMAL(10,2) | - | 金额（保留2位小数） |
| remark | VARCHAR(150) | '' | 账单备注 |
| bill_date | DATE | - | 账单发生日期 |
| bill_time | TIME | NULL | 账单发生时间（精确到时分） |
| is_deleted | ENUM('0','1') | '0' | 软删除：0=正常，1=删除 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_user_date` (`user_id`, `bill_date`)
- KEY `idx_category_id` (`category_id`)
- KEY `idx_account` (`account`)

### 6. 生产项目配置表 (production_project_config)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| uid | VARCHAR(50) | - | 用户ID（登录用户唯一标识） |
| project_name | VARCHAR(100) | - | 项目名称 |
| operation_quota | DECIMAL(10,2) | - | 作业定额（单位工作量/人天） |
| quality_quota | DECIMAL(10,2) | - | 质检定额（单位工作量/人天） |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_uid` (`uid`)

### 7. 每日绩效记录表 (daily_performance)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| uid | VARCHAR(50) | - | 用户ID |
| record_date | DATE | - | 记录日期 |
| project_id | BIGINT | - | 关联生产项目ID |
| process_type | ENUM('作业','质检') | - | 工序类型 |
| quota_efficiency | DECIMAL(10,2) | - | 额定效率 |
| actual_workload | DECIMAL(10,2) | - | 实际工作量 |
| performance_man_days | DECIMAL(10,5) | 计算列 | 绩效人天合计（实际工作量/额定效率） |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_uid_record_date` (`uid`, `record_date`)
- KEY `idx_project_id` (`project_id`)

**外键约束**：
- `CONSTRAINT fk_daily_performance_project FOREIGN KEY (project_id) REFERENCES production_project_config (id) ON DELETE RESTRICT`

**计算列**：`performance_man_days = actual_workload / quota_efficiency`

### 8. 加班记录表 (overtime_record)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| uid | VARCHAR(50) | - | 用户ID |
| record_date | DATE | - | 加班日期 |
| overtime_hours | DECIMAL(5,2) | - | 加班小时数（支持小数） |
| project_id | BIGINT | - | 关联生产项目ID |
| description | VARCHAR(500) | NULL | 加班描述 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_uid_record_date` (`uid`, `record_date`)
- KEY `idx_project_id` (`project_id`)

**外键约束**：
- `CONSTRAINT fk_overtime_record_project FOREIGN KEY (project_id) REFERENCES production_project_config (id) ON DELETE RESTRICT`

### 9. 月度薪资记录表 (monthly_salary_record)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| uid | VARCHAR(50) | - | 用户ID |
| month | CHAR(7) | - | 月份（格式：YYYY-MM） |
| period_start_date | DATE | - | 周期开始日期 |
| period_end_date | DATE | - | 周期结束日期 |
| attendance_days | DECIMAL(5,2) | 0.00 | 出勤天数 |
| basic_salary | DECIMAL(12,5) | 0.00000 | 基本薪资 |
| performance_coefficient | DECIMAL(7,5) | 1.00000 | 绩效总和 |
| performance_salary | DECIMAL(12,5) | 0.00000 | 绩效薪资 |
| position_performance | DECIMAL(12,5) | 0.00000 | 岗位绩效 |
| meal_allowance | DECIMAL(12,5) | 0.00000 | 餐补 |
| housing_allowance | DECIMAL(12,5) | 0.00000 | 房补 |
| full_attendance_bonus | DECIMAL(12,5) | 0.00000 | 全勤奖 |
| other_bonus | DECIMAL(12,5) | 0.00000 | 其他奖金 |
| pension_insurance | DECIMAL(12,5) | 0.00000 | 养老保险 |
| medical_insurance | DECIMAL(12,5) | 0.00000 | 医疗保险 |
| unemployment_insurance | DECIMAL(12,5) | 0.00000 | 失业保险 |
| late_deduction | DECIMAL(12,5) | 0.00000 | 迟到扣款 |
| overtime_hours | DECIMAL(5,2) | 0.00 | 加班时长（小时） |
| overtime_salary | DECIMAL(12,5) | 0.00000 | 加班薪资 |
| total_payable | DECIMAL(12,5) | 0.00000 | 应发总额 |
| total_deduction | DECIMAL(12,5) | 0.00000 | 扣除总额 |
| net_salary | DECIMAL(12,5) | 0.00000 | 实发薪资 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_uid_month` (`uid`, `month`) - 确保每月每人一条记录
- KEY `idx_uid` (`uid`)
- KEY `idx_month` (`month`)

### 10. 动漫图鉴卡片表 (anime_card)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| series_name | VARCHAR(100) | - | 作品系列名称 |
| name | VARCHAR(100) | - | 卡片名称 |
| type | VARCHAR(50) | - | 卡片类型（如：SSR、SR、R等） |
| rarity_level | TINYINT | - | 稀有度级别：1-5 |
| image_url | VARCHAR(500) | - | 卡片图片URL |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- INDEX `idx_series` (`series_name`)
- INDEX `idx_rarity` (`rarity_level`)

**CHECK约束**：`CHECK (rarity_level BETWEEN 1 AND 5)`

**稀有度对应抽卡概率**：
- 1-普通：40%
- 2-稀有：30%
- 3-超稀有：15%
- 4-史诗：10%
- 5-传说：5%

### 11. 用户卡片收集表 (user_card)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT | AUTO_INCREMENT | 主键，自增 |
| uid | BIGINT | - | 用户ID |
| card_id | BIGINT | - | 卡片ID |
| quantity | INT | 1 | 拥有数量 |
| obtained_at | DATETIME | CURRENT_TIMESTAMP | 首次获得时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 最后更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- UNIQUE KEY `uk_uid_card` (`uid`, `card_id`) - 同一用户每种卡片仅有一条记录

### 12. 卡牌寄售商城表 (consignments)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| seller_id | INT UNSIGNED | - | 卖家用户ID |
| card_id | BIGINT | - | 卡片ID |
| unit_price | INT | - | 单价（消耗积分） |
| quantity | INT UNSIGNED | - | 当前可售数量 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 上架时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_seller` (`seller_id`)
- KEY `idx_card` (`card_id`)
- KEY `idx_price` (`unit_price`)

### 13. 选择题题库表 (questions)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | INT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| question_text | TEXT | - | 题目内容 |
| option_a | VARCHAR(255) | - | 选项A |
| option_b | VARCHAR(255) | - | 选项B |
| option_c | VARCHAR(255) | - | 选项C |
| option_d | VARCHAR(255) | - | 选项D |
| correct_option | CHAR(1) | - | 正确答案：A/B/C/D |
| created_at | TIMESTAMP | CURRENT_TIMESTAMP | 创建时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_created_at` (`created_at`)

### 14. 卡牌赠送记录表 (card_gifts)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| sender_id | INT UNSIGNED | - | 赠送者用户ID |
| receiver_id | INT UNSIGNED | - | 接收者用户ID |
| card_id | BIGINT | - | 卡片ID |
| quantity | INT UNSIGNED | 1 | 赠送数量 |
| status | ENUM('pending','completed','rejected') | 'pending' | 状态 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_sender` (`sender_id`)
- KEY `idx_receiver` (`receiver_id`)
- KEY `idx_status` (`status`)

### 15. 卡牌交换申请表 (card_exchanges)

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| id | BIGINT UNSIGNED | AUTO_INCREMENT | 主键，自增 |
| sender_id | INT UNSIGNED | - | 发起者用户ID |
| receiver_id | INT UNSIGNED | - | 接收者用户ID |
| sender_card_id | BIGINT | - | 发起者提供的卡片ID |
| sender_card_quantity | INT UNSIGNED | 1 | 发起者提供的卡片数量 |
| receiver_card_id | BIGINT | - | 接收者提供的卡片ID |
| receiver_card_quantity | INT UNSIGNED | 1 | 接收者提供的卡片数量 |
| status | ENUM('pending','completed','rejected') | 'pending' | 状态 |
| created_at | DATETIME | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | ON UPDATE CURRENT_TIMESTAMP | 更新时间 |

**索引**：
- PRIMARY KEY (`id`)
- KEY `idx_sender` (`sender_id`)
- KEY `idx_receiver` (`receiver_id`)
- KEY `idx_status` (`status`)

---

## 数据库表关系图

```
users (用户表)
  ├── points_log (积分流水) - user_id
  ├── bill_categories (分类) - user_id
  ├── bills (账单) - user_id
  ├── user_card (用户卡片) - uid
  ├── consignments (寄售) - seller_id
  ├── card_gifts (赠送记录) - sender_id, receiver_id
  └── card_exchanges (交换申请) - sender_id, receiver_id

production_project_config (项目配置)
  ├── daily_performance (日绩效) - project_id [FK]
  └── overtime_record (加班记录) - project_id [FK]

anime_card (卡片表)
  ├── user_card (用户卡片) - card_id
  ├── consignments (寄售) - card_id
  ├── card_gifts (赠送记录) - card_id
  └── card_exchanges (交换申请) - sender_card_id, receiver_card_id

bill_categories (分类表)
  └── bills (账单) - category_id
```

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
USE time_bottle;
SOURCE d:/Project/time-bottle/123.txt;

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

# JWT配置
jwt.expiration=86400000

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

## 核心业务逻辑

### 抽卡系统
1. 从 `anime_card` 表按稀有度概率随机抽取卡片
2. 抽到的卡片保存到 `user_card` 表
3. 如果用户已有该卡片，数量+1
4. 卡片稀有度对应发光效果：
   - 普通(1星)：无发光
   - 稀有(2星)：绿色发光
   - 超稀有(3星)：蓝色发光
   - 史诗(4星)：紫色发光
   - 传说(5星)：金色发光

### 卡牌交换流程
1. 用户A选择要送出的卡片
2. 搜索想要的卡片
3. 查找拥有该卡片的用户
4. 选择用户B发送交换申请（状态：pending）
5. 用户B同意后，双方卡片互换（状态：completed）
6. 用户B拒绝后，申请状态变为rejected

### 卡牌赠送流程
1. 赠送者选择卡片和接收者
2. 系统验证卡片数量是否足够
3. 直接转移卡片到接收者（接收者已有则数量+1）
4. 记录保存到 card_gifts 表

### 积分商城流程
1. 卖家上架卡片（设置价格和数量）
2. 买家浏览商城
3. 买家购买卡片（积分从买家转移到卖家）
4. 卡片从卖家转移到买家
5. 数量为0时记录自动删除

### 绩效计算逻辑
1. 绩效人天 = 实际工作量 / 额定效率（数据库计算列）
2. 绩效总和 = 总绩效人天 - 考勤天数 - 加班小时数 × 0.125
3. 绩效薪资 = 绩效总和 × 170元 × 94%
4. 加班薪资 = 加班时长 × 17元
5. 实发薪资 = 应发总额 - 扣款总额

---

## 项目特色

1. **现代化技术栈**：Spring Boot 3 + Vue 3 + Java 21
2. **前后端分离**：便于维护和扩展
3. **JWT认证**：无状态Token认证机制
4. **精美UI设计**：毛玻璃效果、渐变色、动画过渡
5. **数据可视化**：ECharts多图表类型
6. **数据导入导出**：支持CSV/Excel格式
7. **软删除机制**：数据可恢复
8. **积分系统**：签到、记账、考试多种获取方式
9. **集卡游戏**：抽卡、图鉴收集、卡牌交换
10. **邮件验证**：支持密码找回功能
11. **卡牌交易**：积分商城支持玩家间交易
12. **响应式设计**：适配不同屏幕尺寸
13. **数据库计算列**：绩效人天自动计算
14. **外键约束**：保证数据完整性

---

## 注意事项

1. **密码加密**：使用BCrypt算法，不可逆
2. **JWT Token**：登录后返回Token，前端存储在localStorage
3. **验证码有效期**：5分钟
4. **头像大小限制**：最大2MB
5. **软删除**：账单和分类使用软删除，数据不会真正删除
6. **CORS配置**：已配置允许 localhost:5173 和 localhost:8080
7. **积分扣减**：删除账单会扣减积分，积分最低为0
8. **默认头像**：默认头像为 `default-avatar.svg`，存储在后端 `src/main/resources/static/` 目录
9. **弹窗关闭**：需要在遮罩层按下并松开才关闭，避免误操作
10. **外键约束**：删除项目配置前需先删除关联的绩效记录和加班记录

---

## 前端组件通信

- 使用 `provide/inject` 实现跨组件通信
- `refreshPoints` 方法在 App.vue 中 provide，子组件可 inject 调用刷新积分
- 用户信息存储在 localStorage 的 `user` 字段
- Token 存储在 localStorage 的 `token` 字段

---

## 后端安全配置

- SecurityConfig 已配置所有 `/api/**` 路径为 permitAll
- CORS 配置允许指定来源的跨域请求
- 密码使用 BCrypt 加密存储
- JWT Token 有效期 24 小时

---

## License

MIT License
