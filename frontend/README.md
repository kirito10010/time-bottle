# 拾光瓶 (Time Bottle) - 前端项目

一个现代化的个人财务管理、绩效追踪与集卡游戏系统的前端应用，基于 Vue 3 + Vite 构建。

---

## 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [路由配置](#路由配置)
- [组件详解](#组件详解)
- [API 接口调用](#api-接口调用)
- [状态管理](#状态管理)
- [样式规范](#样式规范)
- [性能优化](#性能优化)
- [开发指南](#开发指南)
- [部署指南](#部署指南)
- [常见问题](#常见问题)

---

## 项目概述

拾光瓶前端是一个单页面应用（SPA），主要包含四大核心功能模块：

1. **数据总控台** - 财务台账、绩效记录、绩效看板
2. **梦幻集卡社** - 我的图鉴、积分考试（含连续答题挑战）、积分商城、抽取卡片（含定向抽卡）、卡牌交换
3. **管理员后台** - 用户管理、集卡管理、题目管理、反馈管理
4. **用户系统** - 登录注册、密码找回、个人资料编辑

### 核心特性

- Vue 3 Composition API (`<script setup>` 语法)
- 响应式布局设计
- 毛玻璃效果 UI
- 卡片发光动画效果
- 3D卡片展示（Three.js）
- 图片懒加载优化
- 侧边栏状态持久化
- JWT Token 认证
- 弹窗 Teleport 渲染
- Token 过期自动处理

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.25 | 前端框架（Composition API） |
| Vite | 7.3.1 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Element Plus | 2.13.2 | UI 组件库（消息提示、日期选择器、配置提供者） |
| ECharts | 6.0.0 | 图表可视化（财务台账、绩效看板） |
| Three.js | latest | 3D效果（卡片展示） |
| flatpickr | 4.6.13 | 日期选择器 |
| xlsx | 0.18.5 | Excel 解析（数据导入导出） |
| terser | latest | 代码压缩（生产构建） |

---

## 项目结构

```
frontend/
├── public/                              # 静态资源目录
│   ├── time-bottle.png                  # 网站 Logo
│   ├── default-avatar.svg               # 默认头像
│   └── vite.svg                         # Vite 默认图标
│
├── src/
│   ├── assets/                          # 资源文件
│   │   ├── images/
│   │   │   ├── login-bg.png             # 登录页背景图
│   │   │   └── saomaguanzhu.PNG         # 二维码图片
│   │   └── vue.svg
│   │
│   ├── components/                      # Vue 组件（17个）
│   │   ├── Login.vue                    # 登录组件
│   │   ├── Register.vue                 # 注册组件
│   │   ├── Introduction.vue             # 项目介绍页
│   │   ├── UserProfileEdit.vue          # 用户信息编辑弹窗
│   │   ├── Card3DViewer.vue             # 3D卡片展示组件
│   │   │
│   │   ├── DataControlConsoleFinancialLedger.vue   # 财务台账
│   │   ├── DataControlConsolePerformanceRecord.vue # 绩效记录
│   │   ├── DataControlConsolePerformanceDashboard.vue # 绩效看板
│   │   │
│   │   ├── DreamCardClubMyAlbum.vue     # 我的图鉴
│   │   ├── DreamCardClubPointsExam.vue  # 积分考试（含连续答题挑战）
│   │   ├── DreamCardClubPointsMall.vue  # 积分商城
│   │   ├── DreamCardClubDrawCard.vue    # 抽取卡片（含定向抽卡、50连抽）
│   │   ├── DreamCardClubCardExchange.vue # 卡牌交换
│   │   ├── DreamCardClubComingSoon.vue  # 待开发页面
│   │   │
│   │   ├── AdminPanelUsers.vue          # 用户管理
│   │   ├── AdminPanelCards.vue          # 集卡管理
│   │   ├── AdminPanelQuestions.vue      # 题目管理
│   │   └── AdminPanelFeedback.vue       # 反馈管理
│   │
│   ├── router/
│   │   └── index.js                     # 路由配置
│   │
│   ├── App.vue                          # 根组件
│   ├── main.js                          # 入口文件
│   └── style.css                        # 全局样式
│
├── index.html                           # HTML 模板
├── package.json                         # NPM 配置
├── vite.config.js                       # Vite 配置
└── README.md                            # 本文档
```

---

## 快速开始

### 环境要求

- Node.js 18+
- npm 9+

### 安装依赖

```bash
cd frontend
npm install
```

### 开发模式

```bash
npm run dev
```

访问 http://localhost:5173

### 生产构建

```bash
npm run build
```

构建产物输出到 `dist/` 目录，代码分割优化：
- `vue-vendor.js` - Vue 核心（约38KB gzip）
- `element-plus.js` - Element Plus（约98KB gzip）
- `echarts.js` - ECharts 图表（约358KB gzip）
- `three.js` - Three.js 3D（约116KB gzip）
- `index.js` - 主代码（约182KB gzip）

### 预览构建结果

```bash
npm run preview
```

---

## 路由配置

### 路由表

| 路由 | 名称 | 组件 | 权限 |
|------|------|------|------|
| `/` | Introduction | Introduction.vue | 公开 |
| `/login` | Login | Login.vue | 公开 |
| `/register` | Register | Register.vue | 公开 |
| `/financial-ledger` | FinancialLedger | DataControlConsoleFinancialLedger.vue | 需登录 |
| `/performance-record` | PerformanceRecord | DataControlConsolePerformanceRecord.vue | 需登录 |
| `/performance-dashboard` | PerformanceDashboard | DataControlConsolePerformanceDashboard.vue | 需登录 |
| `/my-album` | MyAlbum | DreamCardClubMyAlbum.vue | 需登录 |
| `/points-exam` | PointsExam | DreamCardClubPointsExam.vue | 需登录 |
| `/points-mall` | PointsMall | DreamCardClubPointsMall.vue | 需登录 |
| `/draw-card` | DrawCard | DreamCardClubDrawCard.vue | 需登录 |
| `/card-exchange` | CardExchange | DreamCardClubCardExchange.vue | 需登录 |
| `/coming-soon` | ComingSoon | DreamCardClubComingSoon.vue | 需登录 |
| `/admin-users` | AdminUsers | AdminPanelUsers.vue | 管理员 |
| `/admin-cards` | AdminCards | AdminPanelCards.vue | 管理员 |
| `/admin-questions` | AdminQuestions | AdminPanelQuestions.vue | 管理员 |
| `/admin-feedback` | AdminFeedback | AdminPanelFeedback.vue | 管理员 |

### 路由守卫实现

```javascript
// router/index.js
router.beforeEach((to) => {
  const savedUser = localStorage.getItem('user');
  const isLoggedIn = !!savedUser;
  
  // 需要登录但未登录
  if (to.meta.requiresAuth && !isLoggedIn) {
    return '/login';
  }
  
  // 需要管理员权限
  if (to.meta.requiresAdmin) {
    const user = savedUser ? JSON.parse(savedUser) : null;
    if (!user || user.role !== '2') {
      return '/financial-ledger';
    }
  }
  
  // 已登录用户访问登录/注册页
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    return '/financial-ledger';
  }
  
  return true;
});
```

---

## 组件详解

### 1. App.vue - 根组件

根组件负责整体布局和全局状态管理，是整个应用的核心。

#### 核心功能

##### 1. 布局切换
- 公开页面（登录、注册、介绍页）：全屏显示
- 登录后页面：侧边栏 + 主内容区

##### 2. 侧边栏状态持久化
```javascript
const sidebarVisible = ref(true);

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
  localStorage.setItem('sidebarVisible', sidebarVisible.value.toString());
};

const loadSidebarState = () => {
  const saved = localStorage.getItem('sidebarVisible');
  if (saved !== null) {
    sidebarVisible.value = saved === 'true';
  }
};
```

##### 3. 菜单展开状态管理
```javascript
const expandedMenus = ref({
  dataControl: true,
  dreamCardClub: false,
  adminPanel: false
});

const toggleMenu = (menuKey) => {
  expandedMenus.value[menuKey] = !expandedMenus.value[menuKey];
  saveMenuState();
};

const saveMenuState = () => {
  localStorage.setItem('expandedMenus', JSON.stringify(expandedMenus.value));
};
```

##### 4. 积分刷新功能（provide/inject）
```javascript
// 提供方法给子组件
const refreshPoints = () => {
  fetchPointsStatus();
};
provide('refreshPoints', refreshPoints);

// 子组件注入使用
// const refreshPoints = inject('refreshPoints', () => {});
```

##### 5. 认证请求封装
```javascript
const authFetch = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  const headers = {
    ...options.headers
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  
  const response = await fetch(url, { ...options, headers });
  
  // Token 过期处理
  if (response.status === 401) {
    handleTokenExpired();
    throw new Error('Unauthorized');
  }
  
  return response;
};

provide('authFetch', authFetch);
```

##### 6. 头像 URL 处理
```javascript
const getAvatarUrl = (avatar) => {
  if (!avatar) return '/default-avatar.svg';
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar;
  if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') return '/default-avatar.svg';
  if (avatar.includes('\\') || avatar.includes('/')) {
    const usersimgIndex = avatar.indexOf('Usersimg');
    if (usersimgIndex !== -1) {
      const relativePath = avatar.substring(usersimgIndex);
      return '/' + relativePath.replace(/\\/g, '/');
    }
  }
  return '/Usersimg/' + avatar;
};
```

##### 7. Token 过期处理
```javascript
const handleTokenExpired = () => {
  if (showTokenExpiredModal.value) return;
  showTokenExpiredModal.value = true;
  
  ElMessageBox.alert('您的登录已过期，请重新登录', '登录过期', {
    confirmButtonText: '重新登录',
    type: 'warning',
    showClose: false,
    closeOnClickModal: false,
    closeOnPressEscape: false
  }).then(() => {
    showTokenExpiredModal.value = false;
    logout();
  });
};
```

---

### 2. DreamCardClubPointsExam.vue - 积分考试

积分考试页面包含两个核心功能：知识问答和连续答题挑战。

#### 核心功能

##### 1. 知识问答
- 5道题目，答对3题及以上获得积分
- 对3题得10积分，对4题得30积分，对5题得60积分
- 每日限制考试次数

##### 2. 连续答题挑战
- 随机刷题，连续答对直到答错结束
- 每小时结算一次排行榜
- 排行榜前5名获得积分奖励：
  - 第1名：+360积分
  - 第2名：+270积分
  - 第3名：+180积分
  - 第4名：+90积分
  - 第5名：+1积分

##### 3. 弹窗答题模式（Teleport）
```html
<Teleport to="body">
  <div v-if="showExamModal" class="modal-overlay" @click.self="closeExamModal">
    <div class="modal-content exam-modal">
      <!-- 80vw × 80vh 弹窗 -->
    </div>
  </div>
</Teleport>
```

##### 4. 倒计时显示
```javascript
const countdownText = ref('');

const updateCountdown = () => {
  const now = new Date();
  const nextHour = new Date(now);
  nextHour.setHours(nextHour.getHours() + 1, 0, 0, 0);
  const diff = nextHour - now;
  const minutes = Math.floor(diff / 60000);
  const seconds = Math.floor((diff % 60000) / 1000);
  countdownText.value = `${minutes}分${seconds.toString().padStart(2, '0')}秒`;
};

onMounted(() => {
  updateCountdown();
  countdownTimer = setInterval(updateCountdown, 1000);
});
```

##### 5. 答题流程
```javascript
const startExam = async () => {
  const response = await fetch(`${API_BASE}/questions`, {
    headers: { 'Authorization': `Bearer ${getToken()}` }
  });
  const data = await response.json();
  questions.value = data.questions;
  examStarted.value = true;
};

const submitExam = async () => {
  const response = await fetch(`${API_BASE}/submit`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${getToken()}`
    },
    body: JSON.stringify({ answers: userAnswers.value })
  });
  const data = await response.json();
  correctCount.value = data.correctCount;
  pointsEarned.value = data.pointsEarned;
  examFinished.value = true;
};
```

---

### 3. DreamCardClubDrawCard.vue - 抽取卡片

抽卡页面支持普通抽卡和定向抽卡两种模式。

#### 核心功能

##### 1. 抽卡模式切换
```html
<select v-model="isTargetedMode" class="draw-mode-select" @change="handleModeChange">
  <option :value="false">普通抽卡</option>
  <option :value="true">定向抽卡</option>
</select>
<select v-model="selectedSeries" class="series-select" :disabled="!isTargetedMode">
  <option value="">选择系列</option>
  <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
</select>
```

##### 2. 抽卡消耗对比

| 类型 | 普通消耗 | 定向消耗(120%) |
|------|---------|----------------|
| 单抽 | 10 | 12 |
| 十连抽 | 90 | 108 |
| 五十连抽 | 450 | 540 |

##### 3. 抽卡概率公示
```html
<div class="probability-info">
  <span class="prob-item legendary">传说 1%</span>
  <span class="prob-item epic">史诗 2%</span>
  <span class="prob-item rare">稀有 7%</span>
  <span class="prob-item uncommon">精良 20%</span>
  <span class="prob-item common">普通 70%</span>
</div>
```

##### 4. 抽卡请求
```javascript
const drawCard = async (count) => {
  const cost = getDrawCost(count);
  
  if (userPoints.value < cost) {
    ElMessage.warning('积分不足');
    return;
  }
  
  if (isTargetedMode.value && !selectedSeries.value) {
    ElMessage.warning('请先选择系列');
    return;
  }
  
  const url = isTargetedMode.value 
    ? '/api/cards/draw-targeted' 
    : '/api/cards/draw';
  const body = isTargetedMode.value 
    ? { count, seriesName: selectedSeries.value }
    : { count };
  
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(body)
  });
};
```

##### 5. 卡片翻转动画
```javascript
const flipCardsSequentially = (count) => {
  flippedCards.value = new Array(count).fill(false);
  for (let i = 0; i < count; i++) {
    setTimeout(() => {
      flippedCards.value[i] = true;
    }, 300 + i * 180);  // 每张卡片间隔180ms翻转
  }
};
```

##### 6. 积分消耗计算
```javascript
const BASE_COSTS = { 1: 10, 10: 90, 50: 450 };
const TARGETED_MULTIPLIER = 1.2;

const getDrawCost = (count) => {
  const baseCost = BASE_COSTS[count] || count * 10;
  return isTargetedMode.value 
    ? Math.ceil(baseCost * TARGETED_MULTIPLIER) 
    : baseCost;
};
```

---

### 4. DreamCardClubPointsMall.vue - 积分商城

积分商城包含四个标签页，支持卡片交易和回收。

#### 核心功能

##### 1. 四个标签页
```html
<div class="tabs">
  <button :class="{ active: activeTab === 'market' }" @click="switchTab('market')">商城</button>
  <button :class="{ active: activeTab === 'my-listings' }" @click="switchTab('my-listings')">我的上架</button>
  <button :class="{ active: activeTab === 'sell' }" @click="switchTab('sell')">上架卡片</button>
  <button :class="{ active: activeTab === 'recycle' }" @click="switchTab('recycle')">♻️ 回收站</button>
</div>
```

##### 2. 一键回收功能
```javascript
const batchRecycleCards = async () => {
  const response = await fetch('/api/consignments/batch-recycle', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      series: selectedRecycleSeries.value,
      rarity: selectedRecycleRarity.value,
      keepCount: keepCount.value
    })
  });
};
```

##### 3. 回收积分价格表

| 稀有度 | 积分/张 |
|--------|---------|
| 普通 | 2 积分 |
| 精良 | 8 积分 |
| 稀有 | 14 积分 |
| 史诗 | 40 积分 |
| 传说 | 200 积分 |

---

### 5. DreamCardClubMyAlbum.vue - 我的图鉴

图鉴页面展示所有卡片，支持搜索筛选和3D预览。

#### 核心功能

##### 1. 卡片展示
- 分页展示（每页24张）
- 已获得/未获得状态区分
- 稀有度发光效果

##### 2. 搜索筛选
```html
<div class="filter-box">
  <div class="search-bar">
    <input type="text" v-model="searchKeyword" placeholder="搜索卡片名称或系列..." @input="filterCards">
    <button class="btn-search" @click="filterCards">搜索</button>
  </div>
  <select v-model="selectedSeries" @change="filterCards">
    <option value="">全部系列</option>
    <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
  </select>
  <select v-model="selectedRarity" @change="filterCards">
    <option value="">全部稀有度</option>
    <option value="1">普通</option>
    <option value="2">精良</option>
    <option value="3">稀有</option>
    <option value="4">史诗</option>
    <option value="5">传说</option>
  </select>
  <label class="checkbox-wrapper">
    <input type="checkbox" v-model="showOnlyUnowned" @change="filterCards">
    <span class="checkbox-label">未收录</span>
  </label>
</div>
```

##### 3. 分页计算
```javascript
const cardsPerPage = 24;

const totalPages = computed(() => 
  Math.ceil(cards.value.length / cardsPerPage)
);

const currentPageCards = computed(() => {
  const start = currentPage.value * cardsPerPage;
  return cards.value.slice(start, start + cardsPerPage);
});
```

##### 4. 3D卡片预览
```html
<Card3DViewer 
  v-if="show3DViewer && selectedCard" 
  :card="selectedCard" 
  @close="close3DViewer"
/>
```

---

### 6. DreamCardClubCardExchange.vue - 卡牌交换

卡牌交换页面支持赠送卡牌和交换申请。

#### 核心功能

##### 1. 三个标签页
- 赠送卡牌 - 将卡片赠送给其他用户
- 卡牌交换 - 发起交换请求
- 记录 - 查看赠送和交换记录

##### 2. 用户搜索
```javascript
const searchUsers = async () => {
  if (!searchUserKeyword.value.trim()) {
    ElMessage.warning('请输入用户名搜索');
    return;
  }
  
  const response = await fetch(`/api/users/search?keyword=${encodeURIComponent(searchUserKeyword.value)}`, {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  
  if (response.ok) {
    const data = await response.json();
    searchResults.value = data.users || [];
  }
};
```

---

### 7. DreamCardClubComingSoon.vue - 待开发页面

占位页面，显示"功能开发中"提示。

```html
<template>
  <div class="coming-soon-container">
    <div class="coming-soon-content">
      <div class="icon-wrapper">
        <span class="icon">🚧</span>
      </div>
      <h2>功能开发中</h2>
      <p class="description">该功能正在紧张开发中，敬请期待...</p>
    </div>
  </div>
</template>
```

---

### 8. DataControlConsoleFinancialLedger.vue - 财务台账

财务台账页面提供完整的收支管理功能。

#### 核心功能

##### 1. 统计卡片
```html
<div class="stats-section">
  <div class="stat-card income-card">
    <div class="stat-title">总收入</div>
    <div class="stat-value income-value">¥{{ totalIncome.toFixed(2) }}</div>
  </div>
  <div class="stat-card expense-card">
    <div class="stat-title">总支出</div>
    <div class="stat-value expense-value">¥{{ totalExpense.toFixed(2) }}</div>
  </div>
  <div class="stat-card balance-card">
    <div class="stat-title">结余</div>
    <div class="stat-value balance-value">¥{{ balance.toFixed(2) }}</div>
  </div>
</div>
```

##### 2. ECharts 图表
```javascript
const updateChart = () => {
  const chart = echarts.init(chartRef.value);
  
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['收入', '支出'] },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: [
      { name: '收入', type: 'line', data: incomes },
      { name: '支出', type: 'line', data: expenses }
    ]
  };
  
  chart.setOption(option);
};
```

##### 3. 时间范围选择
- 本周
- 本月
- 自定义月份
- 自定义日期范围

---

## API 接口调用

### 认证接口 `/api/auth`

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| POST | `/register` | 用户注册 | `{username, password, email}` |
| POST | `/login` | 用户登录 | `{username, password}` |
| PUT | `/profile` | 修改用户信息 | FormData (nickname, password, avatar) |
| POST | `/forgot-password` | 忘记密码 | `{email}` |
| POST | `/reset-password` | 重置密码 | `{token, newPassword}` |

### 积分接口 `/api/points`

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/sign-in` | 每日签到（+5积分） |
| GET | `/status` | 获取签到状态和积分 |
| GET | `/balance` | 获取积分余额 |

### 卡片接口 `/api/cards`

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| GET | `/album` | 获取用户图鉴数据 | - |
| GET | `/series` | 获取所有系列名称 | - |
| POST | `/draw` | 普通抽卡 | `{count}` |
| POST | `/draw-targeted` | 定向抽卡 | `{count, seriesName}` |

### 积分商城接口 `/api/consignments`

| 方法 | 路径 | 功能 | 参数/请求体 |
|------|------|------|-------------|
| GET | `/` | 获取商城列表 | `keyword, series, rarity, page, size` |
| GET | `/series` | 获取所有系列名称 | - |
| GET | `/my` | 获取我的上架 | - |
| GET | `/sellable` | 获取可上架卡片 | - |
| POST | `/list` | 上架卡片 | `{cardId, unitPrice, quantity}` |
| POST | `/delist/{id}` | 下架卡片 | - |
| POST | `/buy/{id}` | 购买卡片 | `{quantity}` |
| POST | `/recycle` | 回收卡片换积分 | `{cardId, quantity}` |
| POST | `/batch-recycle` | 批量回收卡片 | `{series, rarity, keepCount}` |

### 考试接口 `/api/exam`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/questions` | 获取考试题目 |
| POST | `/submit` | 提交考试答案 |

### 连续答题接口 `/api/streak`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/question` | 获取随机题目 |
| POST | `/answer` | 提交答案 |
| GET | `/ranking` | 获取排行榜 |

### 用户接口 `/api/users`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/search?keyword=xxx` | 搜索用户 |

### 卡牌交换接口 `/api/exchanges`

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/gift` | 赠送卡牌 |
| POST | `/request` | 发起交换请求 |
| GET | `/received` | 获取收到的交换请求 |
| POST | `/accept/{id}` | 接受交换请求 |
| POST | `/reject/{id}` | 拒绝交换请求 |

### 管理员接口 `/api/admin`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/users` | 获取用户列表 |
| PUT | `/users/{id}` | 更新用户信息 |
| DELETE | `/users/{id}` | 删除用户 |
| GET | `/cards` | 获取卡片列表 |
| POST | `/cards` | 添加卡片 |
| PUT | `/cards/{id}` | 更新卡片 |
| DELETE | `/cards/{id}` | 删除卡片 |
| GET | `/questions` | 获取题目列表 |
| POST | `/questions` | 添加题目 |
| PUT | `/questions/{id}` | 更新题目 |
| DELETE | `/questions/{id}` | 删除题目 |

---

## 状态管理

本项目不使用 Vuex/Pinia，采用以下方式管理状态：

### 1. localStorage 持久化

| Key | 类型 | 说明 |
|-----|------|------|
| `user` | JSON String | 用户信息对象 |
| `token` | String | JWT Token |
| `sidebarVisible` | String | 侧边栏显示状态 |
| `expandedMenus` | JSON String | 菜单展开状态 |

### 2. provide/inject 跨组件通信

```javascript
// App.vue - 提供方法
const refreshPoints = () => {
  fetchPointsStatus();
};
provide('refreshPoints', refreshPoints);
provide('authFetch', authFetch);
provide('handleTokenExpired', handleTokenExpired);

// 子组件 - 注入方法
const refreshPoints = inject('refreshPoints', () => {});
const authFetch = inject('authFetch', fetch);
```

---

## 样式规范

### 颜色规范

| 用途 | 颜色值 | 说明 |
|------|--------|------|
| 主色调 | `#667eea` → `#764ba2` | 渐变紫色 |
| 传说 | `#ffd700` | 金色 |
| 史诗 | `#a855f7` | 紫色 |
| 稀有 | `#3b82f6` | 蓝色 |
| 精良 | `#22c55e` | 绿色 |
| 普通 | `#9ca3af` | 灰色 |

### 毛玻璃效果

```css
.glass-effect {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
```

### 卡片发光动画

```css
@keyframes glow-legendary {
  from { box-shadow: 0 0 15px rgba(255, 215, 0, 0.6); }
  to { box-shadow: 0 0 25px rgba(255, 215, 0, 0.9); }
}

.card-back.legendary {
  animation: glow-legendary 2s ease-in-out infinite alternate;
}
```

### 隐藏滚动条

```css
.container::-webkit-scrollbar {
  width: 6px;
}
.container::-webkit-scrollbar-track {
  background: transparent;
}
.container::-webkit-scrollbar-thumb {
  background: transparent;
}
```

### 卡片翻转动画

```css
.card-flip-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transition: transform 0.5s ease;
  transform-style: preserve-3d;
}

.card-flip-item.flipped .card-flip-inner {
  transform: rotateY(180deg);
}

.card-flip-front,
.card-flip-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
}

.card-flip-back {
  transform: rotateY(180deg);
}
```

---

## 性能优化

### 1. Vite 构建优化

```javascript
// vite.config.js
export default defineConfig({
  build: {
    target: 'es2015',
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,    // 移除 console.log
        drop_debugger: true    // 移除 debugger
      }
    },
    chunkSizeWarningLimit: 1000,
    rollupOptions: {
      output: {
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router'],
          'element-plus': ['element-plus'],
          'echarts': ['echarts'],
          'three': ['three']
        }
      }
    }
  }
});
```

### 2. 图片懒加载

```html
<img 
  :src="card.imageUrl" 
  loading="lazy" 
  decoding="async" 
/>
```

### 3. CSS 性能优化

```css
.optimized-element {
  will-change: transform;
  transform: translateZ(0);
  contain: layout style paint;
}
```

### 4. Teleport 弹窗

```html
<!-- 将弹窗渲染到 body，避免被父容器 CSS 影响 -->
<Teleport to="body">
  <div v-if="showModal" class="modal">...</div>
</Teleport>
```

### 5. 滚动条抖动修复

```css
.cards-grid {
  overflow-y: auto;
  padding-right: 6px;  /* 补偿滚动条宽度 */
  box-sizing: border-box;
}
```

---

## 开发指南

### 组件开发规范

```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue';
import { ElMessage } from 'element-plus';

// 状态定义
const data = ref([]);

// 计算属性
const filteredData = computed(() => 
  data.value.filter(item => item.active)
);

// 方法
const fetchData = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }
  // ...
};

// 生命周期
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.component-name {
  /* 样式 */
}
</style>
```

### API 调用规范

```javascript
const fetchData = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }
  
  try {
    const response = await fetch('/api/endpoint', {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    if (response.ok) {
      const data = await response.json();
      // 处理数据
    } else {
      const errorData = await response.json();
      ElMessage.error(errorData.message || '请求失败');
    }
  } catch (error) {
    console.error('请求错误:', error);
    ElMessage.error('网络错误');
  }
};
```

---

## 部署指南

### 1. 构建生产版本

```bash
cd frontend
npm run build
```

### 2. 部署到 Nginx

将 `dist/` 目录内容复制到 Nginx 的 `html/` 目录：

```powershell
Copy-Item -Recurse -Force frontend/dist/* nginx-1.28.3/html/
```

### 3. Nginx 配置

```nginx
worker_processes  1;

events {
    worker_connections  512;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    sendfile        on;
    tcp_nopush      on;
    tcp_nodelay     on;
    keepalive_timeout  30;
    
    # Gzip 压缩
    gzip  on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_min_length 1000;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml image/svg+xml;

    server {
        listen       80;
        server_name  localhost;
        
        root   html;
        index  index.html;
        
        # 前端路由支持
        location / {
            try_files $uri $uri/ /index.html;
        }
        
        # 静态资源缓存（排除/Usersimg路径，避免拦截头像请求）
        location ~* ^/(?!Usersimg/).*\.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 7d;
            add_header Cache-Control "public, immutable";
            try_files $uri =404;
        }
        
        # API 代理
        location /api {
            proxy_pass http://localhost:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_connect_timeout 30s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }
        
        # 用户头像代理（由后端处理静态文件）
        location /Usersimg {
            proxy_pass http://localhost:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            expires 1d;
            add_header Cache-Control "public";
        }
    }
}
```

### 4. 服务器文件结构

```
桌面/
├── backend-0.0.1-SNAPSHOT.jar
├── nginx-1.28.3/
│   ├── conf/nginx.conf
│   └── html/（前端构建产物）
└── uploads/
    └── avatars/
        └── xxx.png（用户上传的头像）
```

### 5. 启动命令

```bash
# 在桌面目录启动后端（确保相对路径正确）
cd C:\Users\你的用户名\Desktop
java -jar backend-0.0.1-SNAPSHOT.jar

# 启动 Nginx
cd nginx-1.28.3
start nginx
```

---

## 常见问题

### 1. 头像无法显示

**原因**：nginx静态资源规则拦截了`/Usersimg`路径。

**解决**：修改nginx配置，使用负向前瞻排除该路径：
```nginx
location ~* ^/(?!Usersimg/).*\.(jpeg|jpg|png)$ {
    # 静态资源规则
}
```

### 2. 弹窗位置不正确

**原因**：弹窗被父容器的`transform`、`filter`等CSS属性影响，`position: fixed`相对于父容器定位。

**解决**：使用`Teleport`将弹窗渲染到`body`：
```html
<Teleport to="body">
  <div v-if="showModal" class="modal-overlay">...</div>
</Teleport>
```

### 3. 抽卡展示抖动

**原因**：滚动条出现/消失导致容器宽度变化。

**解决**：添加padding补偿滚动条宽度：
```css
.cards-grid {
  overflow-y: auto;
  padding-right: 6px;
  box-sizing: border-box;
}
```

### 4. 管理员接口 403 错误

**原因**：未携带 Token 或用户不是管理员。

**解决**：
1. 确保请求头携带 `Authorization: Bearer {token}`
2. 确保用户 `role` 为 `'2'`

### 5. 卡片动画卡顿

**解决**：添加性能优化 CSS：
```css
.card {
  will-change: transform;
  transform: translateZ(0);
  contain: layout style paint;
}
```

### 6. Token 过期处理

**现象**：请求返回 401 状态码。

**解决**：使用 `authFetch` 封装，自动处理 Token 过期：
```javascript
const authFetch = inject('authFetch', fetch);
const response = await authFetch('/api/endpoint');
```

---

## 后端项目

后端项目位于 `../backend/` 目录，技术栈：
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security (BCrypt)
- JWT (jjwt 0.12.3)
- MySQL 8.x
- Java 21
- 定时任务（每小时结算连续答题排行榜）

---

## 数据库表结构

### 用户系统

| 表名 | 说明 |
|------|------|
| `users` | 用户表 |
| `points_log` | 积分流水表 |
| `password_reset_token` | 密码重置验证码表 |

### 集卡游戏

| 表名 | 说明 |
|------|------|
| `anime_card` | 卡片总表 |
| `user_card` | 用户卡片收集表 |
| `consignments` | 积分商城寄售表 |
| `card_gifts` | 卡牌赠送记录表 |
| `card_exchanges` | 卡牌交换申请表 |
| `hourly_streak` | 连续答题小时记录表 |

### 题库系统

| 表名 | 说明 |
|------|------|
| `questions` | 选择题题库表 |

### 卡片稀有度

| rarity_level | 稀有度 | CSS类名 | 抽卡概率 |
|--------------|--------|---------|----------|
| 1 | 普通 | common | 70% |
| 2 | 精良 | uncommon | 20% |
| 3 | 稀有 | rare | 7% |
| 4 | 史诗 | epic | 2% |
| 5 | 传说 | legendary | 1% |

---

## License

MIT License
