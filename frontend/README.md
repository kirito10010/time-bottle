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
2. **梦幻集卡社** - 我的图鉴、积分商城、抽取卡片、积分考试、卡牌交换
3. **管理员后台** - 用户管理、集卡管理、题目管理、反馈管理
4. **用户系统** - 登录注册、密码找回、个人资料编辑

### 核心特性

- Vue 3 Composition API (`<script setup>` 语法)
- 响应式布局设计
- 毛玻璃效果 UI
- 卡片发光动画效果
- 图片懒加载优化
- 侧边栏状态持久化
- JWT Token 认证

---

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.25 | 前端框架（Composition API） |
| Vite | 7.3.1 | 构建工具 |
| Vue Router | 5.0.3 | 路由管理 |
| Element Plus | 2.13.2 | UI 组件库（消息提示、配置提供者） |
| ECharts | 6.0.0 | 图表可视化（财务台账、绩效看板） |
| flatpickr | 4.6.13 | 日期选择器 |
| xlsx | 0.18.5 | Excel 解析（数据导入导出） |

### 开发依赖

| 依赖 | 版本 | 用途 |
|------|------|------|
| @vitejs/plugin-vue | 6.0.2 | Vite Vue 插件 |

---

## 项目结构

```
frontend/
├── public/                              # 静态资源目录
│   ├── time-bottle.png                  # 网站 Logo
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
│   │   │
│   │   ├── DataControlConsoleFinancialLedger.vue   # 财务台账
│   │   ├── DataControlConsolePerformanceRecord.vue # 绩效记录
│   │   ├── DataControlConsolePerformanceDashboard.vue # 绩效看板
│   │   │
│   │   ├── DreamCardClubMyAlbum.vue     # 我的图鉴
│   │   ├── DreamCardClubPointsExam.vue  # 积分考试
│   │   ├── DreamCardClubPointsMall.vue  # 积分商城
│   │   ├── DreamCardClubDrawCard.vue    # 抽取卡片
│   │   ├── DreamCardClubCardExchange.vue # 卡牌交换
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

构建产物输出到 `dist/` 目录。

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
| `/admin-users` | AdminUsers | AdminPanelUsers.vue | 管理员 |
| `/admin-cards` | AdminCards | AdminPanelCards.vue | 管理员 |
| `/admin-questions` | AdminQuestions | AdminPanelQuestions.vue | 管理员 |
| `/admin-feedback` | AdminFeedback | AdminPanelFeedback.vue | 管理员 |

### 路由守卫

```javascript
router.beforeEach((to) => {
  const savedUser = localStorage.getItem('user');
  const isLoggedIn = !!savedUser;
  
  // 需要登录但未登录，重定向到登录页
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
  
  // 已登录用户访问登录/注册页，重定向到首页
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    return '/financial-ledger';
  }
  
  return true;
});
```

### 路由 Meta 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `public` | Boolean | 是否为公开页面（无需登录） |
| `requiresAuth` | Boolean | 是否需要登录 |
| `requiresAdmin` | Boolean | 是否需要管理员权限 |

---

## 组件详解

### 1. App.vue - 根组件

根组件负责整体布局和全局状态管理。

#### 核心功能

1. **布局切换**
   - 公开页面（登录、注册、介绍页）：全屏显示
   - 登录后页面：侧边栏 + 主内容区

2. **侧边栏状态持久化**
   ```javascript
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

3. **菜单展开状态持久化**
   ```javascript
   const saveMenuState = () => {
     localStorage.setItem('expandedMenus', JSON.stringify(expandedMenus.value));
   };
   
   const loadMenuState = () => {
     const saved = localStorage.getItem('expandedMenus');
     if (saved) {
       expandedMenus.value = { ...expandedMenus.value, ...JSON.parse(saved) };
     }
   };
   ```

4. **积分刷新功能（provide/inject）**
   ```javascript
   const refreshPoints = () => {
     fetchPointsStatus();
   };
   
   provide('refreshPoints', refreshPoints);
   ```
   
   子组件使用：
   ```javascript
   const refreshPoints = inject('refreshPoints', () => {});
   ```

5. **头像 URL 处理**
   ```javascript
   const getAvatarUrl = (avatar) => {
     if (!avatar) return '/default-avatar.svg';
     if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar;
     if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') return '/default-avatar.svg';
     if (avatar.includes('\\') || avatar.includes('/')) {
       const usersimgIndex = avatar.indexOf('Usersimg');
       if (usersimgIndex !== -1) {
         return '/' + avatar.substring(usersimgIndex).replace(/\\/g, '/');
       }
     }
     return '/Usersimg/' + avatar;
   };
   ```

#### 用户信息结构

```javascript
const userInfo = ref({
  username: '用户名',
  avatar: '',
  nickname: '未命名',
  id: null,
  role: '0'  // '0'-普通用户, '1'-VIP, '2'-管理员
});
```

#### localStorage 存储内容

| Key | 类型 | 说明 |
|-----|------|------|
| `user` | JSON String | 用户信息对象 |
| `token` | String | JWT Token |
| `sidebarVisible` | String ('true'/'false') | 侧边栏显示状态 |
| `expandedMenus` | JSON String | 菜单展开状态 |

---

### 2. DreamCardClubMyAlbum.vue - 我的图鉴

#### 核心功能

1. **卡片展示**
   - 分页展示（每页24张）
   - 已获得/未获得状态区分
   - 稀有度发光效果

2. **搜索筛选**
   ```javascript
   const filterCards = () => {
     let filtered = [...allCards.value];
     
     // 关键词搜索（卡片名称或系列名称）
     if (searchKeyword.value.trim()) {
       const keyword = searchKeyword.value.toLowerCase().trim();
       filtered = filtered.filter(card => 
         card.name.toLowerCase().includes(keyword) || 
         (card.seriesName && card.seriesName.toLowerCase().includes(keyword))
       );
     }
     
     // 系列筛选
     if (selectedSeries.value) {
       filtered = filtered.filter(card => card.seriesName === selectedSeries.value);
     }
     
     // 稀有度筛选
     if (selectedRarity.value) {
       filtered = filtered.filter(card => card.rarityLevel === parseInt(selectedRarity.value));
     }
     
     cards.value = filtered;
   };
   ```

3. **图片懒加载**
   ```html
   <img 
     :src="card.imageUrl" 
     :alt="card.name" 
     class="photo-image"
     loading="lazy"
     decoding="async"
   />
   ```

4. **卡片放大预览（Teleport + Transition）**
   ```html
   <Teleport to="body">
     <Transition name="modal">
       <div v-if="showCardModal" class="card-modal-overlay" @click="closeCardModal">
         <!-- 弹窗内容 -->
       </div>
     </Transition>
   </Teleport>
   ```

5. **稀有度样式映射**
   ```javascript
   const getRarityClass = (rarityLevel) => {
     switch (rarityLevel) {
       case 5: return 'legendary';  // 传说 - 金色
       case 4: return 'epic';       // 史诗 - 紫色
       case 3: return 'rare';       // 稀有 - 蓝色
       case 2: return 'uncommon';   // 精良 - 绿色
       default: return 'common';    // 普通 - 灰色
     }
   };
   ```

#### 性能优化

```css
.photo-frame {
  will-change: transform;
  transform: translateZ(0);
  contain: layout style paint;
}

.photo-grid {
  will-change: transform;
}
```

#### API 调用

```javascript
const fetchAlbumData = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/cards/album', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const data = await response.json();
  // data: { cards, totalCards, ownedCards, completionRate }
};
```

---

### 3. DreamCardClubPointsMall.vue - 积分商城

#### 核心功能

1. **三个标签页**
   - 商城（市场）
   - 我的上架
   - 上架卡片

2. **筛选功能**
   ```javascript
   const fetchConsignments = async () => {
     const params = new URLSearchParams();
     params.append('page', currentPage.value);
     params.append('size', 12);
     if (searchKeyword.value) params.append('keyword', searchKeyword.value);
     if (selectedSeries.value) params.append('series', selectedSeries.value);
     if (selectedRarity.value) params.append('rarity', selectedRarity.value);
     
     const response = await fetch(`${API_BASE}?${params.toString()}`);
   };
   ```

3. **购买卡片**
   ```javascript
   const buyCard = async () => {
     const response = await fetch(`${API_BASE}/buy/${selectedItem.value.id}`, {
       method: 'POST',
       headers: {
         'Content-Type': 'application/json',
         'Authorization': `Bearer ${token}`
       },
       body: JSON.stringify({ quantity: buyQuantity.value })
     });
   };
   ```

4. **上架卡片**
   ```javascript
   const listCard = async () => {
     const response = await fetch(`${API_BASE}/list`, {
       method: 'POST',
       headers: {
         'Content-Type': 'application/json',
         'Authorization': `Bearer ${token}`
       },
       body: JSON.stringify({
         cardId: selectedCard.value.cardId,
         unitPrice: sellPrice.value,
         quantity: sellQuantity.value
       })
     });
   };
   ```

5. **弹窗点击外部关闭**
   ```javascript
   const handleOverlayMouseDown = (e) => {
     mouseDownOnOverlay.value = e.target.classList.contains('modal-overlay');
   };
   
   const handleOverlayMouseUp = (e, closeModal) => {
     if (mouseDownOnOverlay.value && e.target.classList.contains('modal-overlay')) {
       closeModal();
     }
     mouseDownOnOverlay.value = false;
   };
   ```

---

### 4. DreamCardClubDrawCard.vue - 抽取卡片

#### 核心功能

1. **抽卡概率展示**
   ```html
   <div class="probability-info">
     <span class="prob-item legendary">传说 1%</span>
     <span class="prob-item epic">史诗 2%</span>
     <span class="prob-item rare">稀有 7%</span>
     <span class="prob-item uncommon">精良 20%</span>
     <span class="prob-item common">普通 70%</span>
   </div>
   ```

2. **单抽/十连抽**
   ```javascript
   const drawCard = async (count) => {
     const cost = count === 1 ? 10 : 90;  // 十连抽9折
     
     if (userPoints.value < cost) {
       ElMessage.warning('积分不足');
       return;
     }
     
     const response = await fetch('/api/cards/draw', {
       method: 'POST',
       headers: {
         'Content-Type': 'application/json',
         'Authorization': `Bearer ${token}`
       },
       body: JSON.stringify({ count })
     });
   };
   ```

3. **卡片翻转动画**
   ```javascript
   // 十连抽时依次翻转
   const flipCardsSequentially = (count) => {
     flippedCards.value = new Array(count).fill(false);
     for (let i = 0; i < count; i++) {
       setTimeout(() => {
         flippedCards.value[i] = true;
       }, 300 + i * 180);
     }
   };
   ```

4. **CSS 3D 翻转**
   ```css
   .card-preview {
     transform-style: preserve-3d;
     transition: transform 0.6s ease;
   }
   
   .card-preview.revealed {
     transform: rotateY(180deg);
   }
   
   .card-front, .card-back {
     backface-visibility: hidden;
   }
   
   .card-back {
     transform: rotateY(180deg);
   }
   ```

---

### 5. AdminPanelUsers.vue - 用户管理

#### 核心功能

1. **获取用户列表**
   ```javascript
   const fetchUsers = async () => {
     const token = localStorage.getItem('token');
     const response = await fetch('/api/admin/users', {
       headers: { 'Authorization': `Bearer ${token}` }
     });
   };
   ```

2. **编辑用户**
   ```javascript
   const updateUser = async (userId, userData) => {
     const response = await fetch(`/api/admin/users/${userId}`, {
       method: 'PUT',
       headers: {
         'Content-Type': 'application/json',
         'Authorization': `Bearer ${token}`
       },
       body: JSON.stringify(userData)
     });
   };
   ```

#### 重要提示

- 所有管理员接口必须在请求头携带 `Authorization: Bearer {token}`
- 用户角色：`'0'`-普通用户，`'1'`-VIP，`'2'`-管理员
- 用户状态：`'0'`-禁用，`'1'`-正常

---

## API 接口调用

### 通用请求模式

```javascript
// GET 请求
const fetchData = async () => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/endpoint', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const data = await response.json();
};

// POST 请求
const postData = async (body) => {
  const token = localStorage.getItem('token');
  const response = await fetch('/api/endpoint', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(body)
  });
  const data = await response.json();
};
```

### 认证接口 `/api/auth`

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| POST | `/register` | 用户注册 | `{username, password, email}` |
| POST | `/login` | 用户登录 | `{username, password}` |
| PUT | `/profile` | 修改用户信息 | FormData (nickname, password, avatar) |

### 积分接口 `/api/points`

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/sign-in` | 每日签到（+5积分） |
| GET | `/status` | 获取签到状态和积分 |
| GET | `/balance` | 获取积分余额 |

### 卡片接口 `/api/cards`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/album` | 获取用户图鉴数据 |
| POST | `/draw` | 抽卡（body: `{count}`） |

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

### 管理员接口 `/api/admin/users`

> **重要**：所有管理员接口需要 `Authorization: Bearer {token}` 头，且用户角色必须为 `'2'`

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | `/` | 获取用户列表 |
| GET | `/{id}` | 获取单个用户 |
| PUT | `/{id}` | 更新用户信息 |
| DELETE | `/{id}` | 删除用户 |

---

## 状态管理

本项目不使用 Vuex/Pinia，采用以下方式管理状态：

### 1. localStorage 持久化

```javascript
// 存储用户信息
localStorage.setItem('user', JSON.stringify(userInfo));

// 读取用户信息
const user = JSON.parse(localStorage.getItem('user'));

// 存储Token
localStorage.setItem('token', token);

// 读取Token
const token = localStorage.getItem('token');
```

### 2. provide/inject 跨组件通信

```javascript
// App.vue - 提供方法
const refreshPoints = () => {
  fetchPointsStatus();
};
provide('refreshPoints', refreshPoints);

// 子组件 - 注入方法
const refreshPoints = inject('refreshPoints', () => {});

// 使用
refreshPoints();  // 刷新积分
```

### 3. 响应式状态

```javascript
// 组件内部响应式状态
const userPoints = ref(0);
const isLoggedIn = ref(false);
const consignments = ref([]);
```

---

## 样式规范

### 1. CSS 变量

项目未使用 CSS 变量，直接在组件中使用具体颜色值。

### 2. 颜色规范

| 用途 | 颜色值 | 说明 |
|------|--------|------|
| 主色调 | `#667eea` → `#764ba2` | 渐变紫色 |
| 传说 | `#ffd700` / `#fbbf24` | 金色 |
| 史诗 | `#a855f7` / `#8b5cf6` | 紫色 |
| 稀有 | `#3b82f6` / `#60a5fa` | 蓝色 |
| 精良 | `#22c55e` / `#10b981` | 绿色 |
| 普通 | `#9ca3af` / `#6b7280` | 灰色 |

### 3. 毛玻璃效果

```css
.glass-effect {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}
```

### 4. 卡片发光动画

```css
@keyframes glow-legendary {
  from {
    box-shadow: 0 0 15px rgba(255, 215, 0, 0.6), 0 0 30px rgba(255, 215, 0, 0.4);
  }
  to {
    box-shadow: 0 0 25px rgba(255, 215, 0, 0.9), 0 0 50px rgba(255, 215, 0, 0.6);
  }
}
```

### 5. 过渡动画

```css
/* Vue Transition 组件 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
```

---

## 性能优化

### 1. 图片懒加载

```html
<img 
  :src="card.imageUrl" 
  loading="lazy"
  decoding="async"
/>
```

### 2. CSS 性能优化

```css
.optimized-element {
  /* 提示浏览器将要变化的属性 */
  will-change: transform, opacity;
  
  /* 启用 GPU 加速 */
  transform: translateZ(0);
  
  /* 限制重绘范围 */
  contain: layout style paint;
}
```

### 3. 分页渲染

```javascript
// 只渲染当前页的卡片，不是全部渲染
const currentPageCards = computed(() => {
  const start = currentPage.value * cardsPerPage;
  return cards.value.slice(start, start + cardsPerPage);
});
```

### 4. Teleport 弹窗

```html
<!-- 将弹窗渲染到 body，避免父组件样式影响 -->
<Teleport to="body">
  <div v-if="showModal" class="modal">...</div>
</Teleport>
```

---

## 开发指南

### 1. 组件开发规范

- 使用 `<script setup>` 语法
- 使用 Composition API
- 样式使用 `<style scoped>`

```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue';
import { ElMessage } from 'element-plus';

// 响应式状态
const data = ref([]);

// 计算属性
const computedValue = computed(() => data.value.length);

// 方法
const fetchData = async () => {
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

### 2. API 调用规范

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
      ElMessage.error('请求失败');
    }
  } catch (error) {
    console.error('请求错误:', error);
    ElMessage.error('网络错误');
  }
};
```

### 3. 添加新页面步骤

1. 在 `src/components/` 创建组件文件
2. 在 `src/router/index.js` 添加路由配置
3. 在 `App.vue` 侧边栏添加菜单项（如需要）

### 4. 添加新 API 步骤

1. 确认后端接口已实现
2. 在组件中调用 API
3. 处理响应数据
4. 更新 UI

---

## 部署指南

### 1. 构建生产版本

```bash
cd frontend
npm run build
```

### 2. 部署到 Nginx

将 `dist/` 目录内容复制到 Nginx 的 `html/` 目录：

```bash
# Windows
Copy-Item -Recurse -Force frontend/dist/* nginx-1.28.3/html/

# Linux
cp -r frontend/dist/* /usr/share/nginx/html/
```

### 3. Nginx 配置

```nginx
server {
    listen 80;
    server_name localhost;
    
    root html;
    index index.html;
    
    # 前端路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API 代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    # 用户头像代理
    location /Usersimg {
        proxy_pass http://localhost:8080;
    }
    
    # 默认头像代理
    location /default-avatar.svg {
        proxy_pass http://localhost:8080;
    }
}
```

### 4. Vite 开发代理配置

```javascript
// vite.config.js
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/Usersimg': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/default-avatar.svg': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
```

---

## 常见问题

### 1. 登录后刷新页面丢失状态

**原因**：localStorage 中的数据未正确读取。

**解决**：在 `App.vue` 的 `onMounted` 中调用 `checkAuth()` 恢复状态。

### 2. 头像无法显示

**原因**：头像 URL 处理逻辑问题。

**解决**：使用 `getAvatarUrl()` 函数统一处理：
- 默认头像：`/default-avatar.svg`
- 用户上传：`/Usersimg/{filename}`
- 外部链接：直接使用

### 3. 管理员接口 403 错误

**原因**：未携带 Token 或用户不是管理员。

**解决**：
1. 确保请求头携带 `Authorization: Bearer {token}`
2. 确保用户 `role` 为 `'2'`

### 4. 卡片动画卡顿

**原因**：CSS 动画过多导致性能问题。

**解决**：添加性能优化 CSS：
```css
.card {
  will-change: transform;
  transform: translateZ(0);
  contain: layout style paint;
}
```

### 5. 侧边栏状态不持久

**原因**：未正确保存到 localStorage。

**解决**：在 `toggleSidebar` 中调用 `localStorage.setItem()`。

### 6. 图片加载慢

**原因**：未使用懒加载。

**解决**：添加懒加载属性：
```html
<img loading="lazy" decoding="async" />
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

详细文档请参阅后端项目 README。

---

## 数据库表结构

数据库共 15 张表，详见 `../123.txt` 建表 SQL。

主要表：
- `users` - 用户表
- `anime_card` - 卡片表
- `user_card` - 用户卡片收集表
- `consignments` - 积分商城表
- `points_log` - 积分流水表
- `questions` - 题库表

---

## License

MIT License
