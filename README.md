# 🍶 拾光瓶 (Time Bottle)

[![Vue](https://img.shields.io/badge/Vue-3.5.25-4FC08D?logo=vue.js)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-6DB33F?logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

一个现代化的个人财务管理与绩效追踪系统，采用前后端分离架构。

## ✨ 功能特性

### 📊 财务台账
- 账单管理：增删改查、批量删除
- 多条件筛选：日期、类型、分类、账户、金额范围、备注
- 分类管理：收入/支出分类，支持自定义
- 数据可视化：饼图、柱状图、热力图
- 数据导入导出：CSV/Excel 格式

### 📈 绩效记录
- 每日绩效记录：工作量、绩效工时
- 加班记录管理：加班时长、项目关联
- 月度薪资计算：自动计算周期、绩效薪资、加班薪资
- 工资配置：自定义薪资参数

### 📋 绩效看板
- Excel 数据导入
- 多维度图表分析
- 个人绩效排名
- 项目绩效对比
- 绩效分布情况

### 🔐 用户系统
- 用户注册/登录
- BCrypt 密码加密
- 密码找回（邮箱验证码）
- 用户信息修改
- 头像上传

## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.0 | 核心框架 |
| Spring Data JPA | - | ORM 框架 |
| Spring Security | - | 安全框架 |
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
| Element Plus | 2.13.2 | UI 组件库 |
| ECharts | 6.0.0 | 图表可视化 |
| xlsx | - | Excel 解析 |

## 📁 项目结构

```
time-bottle/
├── backend/                    # 后端项目
│   ├── src/main/java/com/timebottle/backend/
│   │   ├── config/             # 配置层
│   │   ├── controller/         # 控制器层
│   │   ├── entity/             # 实体层
│   │   ├── repository/         # 数据访问层
│   │   └── service/            # 业务逻辑层
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # Vue 组件
│   │   ├── router/             # 路由配置
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
│
└── README.md
```

## 🚀 快速开始

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

# 2. 导入数据表（执行建表 SQL）

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
- 后端 API 服务：http://localhost:8080

## 📸 项目截图

### 介绍页面
![介绍页面](docs/screenshots/introduction.png)

### 财务台账
![财务台账](docs/screenshots/financial-ledger.png)

### 绩效记录
![绩效记录](docs/screenshots/performance-record.png)

### 绩效看板
![绩效看板](docs/screenshots/performance-dashboard.png)

## 🔧 配置说明

### 后端配置 (application.properties)

```properties
# 服务器端口
server.port=8080

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/time_bottle?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password

# JPA 配置
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

## 📝 开发者备注

### 薪资计算公式
- 绩效总和 = 总绩效人天 - 考勤天数（底量扣除） - 加班小时数 × 0.125
- 绩效薪资 = 绩效总和 × 170元 × 94%
- 加班薪资 = 加班时长 × 17元
- 实发薪资 = 应发总额 - 扣款总额

### 工资周期
- 每月 26 日至次月 25 日为一个工资周期

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 📄 许可证

本项目基于 [MIT](LICENSE) 许可证开源。

## 📧 联系方式

- 邮箱：a3025863517@163.com
- GitHub：[https://github.com/kirito10010/time-bottle](https://github.com/kirito10010/time-bottle)

---

⭐ 如果这个项目对你有帮助，请给一个 Star！
