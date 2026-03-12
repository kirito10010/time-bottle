<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import Login from './components/Login.vue';
import Register from './components/Register.vue';
import UserProfileEdit from './components/UserProfileEdit.vue';
import { ElConfigProvider } from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

const router = useRouter();

// 登录状态管理
const isLoggedIn = ref(false);
const currentView = ref('login'); // 'login' or 'register'

// 用户信息
const userInfo = ref({
  username: '用户名',
  avatar: '',
  nickname: '未命名'
});

// 导航栏展开状态管理
const expandedMenus = ref({
  dataControl: true // 默认展开数据总控台
});

// 侧边栏显示状态
const sidebarVisible = ref(true);

// 用户菜单展开状态
const showUserMenu = ref(false);

// 用户信息修改弹窗
const showUserProfileEdit = ref(false);

// 切换侧边栏显示状态
const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
};

// 切换用户菜单展开状态
const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

// 切换菜单展开状态
const toggleMenu = (menuKey) => {
  expandedMenus.value[menuKey] = !expandedMenus.value[menuKey];
};

// 退出登录
const logout = () => {
  isLoggedIn.value = false;
  currentView.value = 'login';
  // 清除localStorage中的用户信息
  localStorage.removeItem('user');
  // 重置用户信息
  userInfo.value = {
    username: '用户名',
    avatar: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=user%20avatar%20portrait&size=32x32',
    nickname: '未命名'
  };
  // 重置路由到根路径
  router.push('/');
};

// 处理登录成功
const handleLoginSuccess = () => {
  isLoggedIn.value = true;
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    userInfo.value = JSON.parse(savedUser);
  }
  router.push('/financial-ledger');
};

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return 'http://localhost:8080/default-avatar.svg';
  }
  // 检查是否已经是完整的URL
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar;
  }
  // 默认头像从静态资源目录获取
  if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') {
    return 'http://localhost:8080/' + avatar;
  }
  // 处理本地文件路径
  if (avatar.includes('\\') || avatar.includes('/')) {
    // 提取Usersimg部分
    const usersimgIndex = avatar.indexOf('Usersimg');
    if (usersimgIndex !== -1) {
      const relativePath = avatar.substring(usersimgIndex);
      return 'http://localhost:8080/' + relativePath.replace(/\\/g, '/');
    }
  }
  // 如果只是文件名，添加Usersimg路径
  return 'http://localhost:8080/Usersimg/' + avatar;
};

// 切换到注册页面
const switchToRegister = () => {
  currentView.value = 'register';
};

// 切换到登录页面
const switchToLogin = () => {
  currentView.value = 'login';
};

// 打开用户信息修改弹窗
const openUserProfileEdit = () => {
  showUserProfileEdit.value = true;
  showUserMenu.value = false; // 关闭用户菜单
};

// 关闭用户信息修改弹窗
const closeUserProfileEdit = () => {
  showUserProfileEdit.value = false;
};

// 更新用户信息
const updateUserInfo = (updatedUser) => {
  userInfo.value = updatedUser;
  // 更新localStorage中的用户信息
  localStorage.setItem('user', JSON.stringify(updatedUser));
};

// 点击外部关闭用户菜单
const handleClickOutside = (event) => {
  const userProfile = event.target.closest('.user-profile');
  if (!userProfile) {
    showUserMenu.value = false;
  }
};

// 生命周期钩子
onMounted(() => {
  // 页面加载时检查localStorage中的用户信息
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    isLoggedIn.value = true;
    userInfo.value = JSON.parse(savedUser);
  }
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <el-config-provider :locale="zhCn">
    <div v-if="!isLoggedIn" class="auth-container">
      <Login 
        v-if="currentView === 'login'" 
        @switchToRegister="switchToRegister"
        @loginSuccess="handleLoginSuccess"
      />
      <Register 
        v-else 
        @switchToLogin="switchToLogin"
      />
    </div>
    <div v-else class="app-container" :class="{ 'sidebar-hidden': !sidebarVisible }">
    <!-- 左侧导航栏 -->
    <aside class="sidebar">
      <header class="sidebar-header">
        <img src="/time-bottle.png" alt="拾光瓶" class="logo">
        <h1>拾光瓶</h1>
      </header>
      <nav class="nav-menu">
        <ul>
          <li class="nav-item">
            <div class="nav-item-title" @click="toggleMenu('dataControl')">
              <span class="menu-icon">📊</span>
              <span>数据总控台</span>
              <span class="expand-icon">{{ expandedMenus.dataControl ? '▼' : '▶' }}</span>
            </div>
            <ul v-if="expandedMenus.dataControl" class="sub-menu">
              <li><router-link to="/financial-ledger"><span class="menu-icon">💰</span>财务台账</router-link></li>
              <li><router-link to="/performance-record"><span class="menu-icon">📈</span>绩效记录</router-link></li>
              <li><router-link to="/performance-dashboard"><span class="menu-icon">📋</span>绩效看板</router-link></li>
            </ul>
          </li>
        </ul>
      </nav>
    </aside>

    <!-- 右侧内容区 -->
    <main class="content">
      <!-- 头部 -->
      <header class="header">
        <div class="header-title">
          <button class="sidebar-toggle" @click="toggleSidebar" :title="sidebarVisible ? '隐藏侧边栏' : '显示侧边栏'">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          </button>
        </div>
        <div class="user-info">
          <div class="user-profile" @click="toggleUserMenu">
            <img :src="getAvatarUrl(userInfo.avatar)" alt="用户头像" class="avatar">
            <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
            <span class="dropdown-icon">▼</span>
            <div v-if="showUserMenu" class="user-dropdown">
              <a href="#" @click.stop="openUserProfileEdit">修改信息</a>
              <a href="#" @click.prevent="logout">退出登录</a>
            </div>
            
            <!-- 用户信息修改弹窗 -->
            <UserProfileEdit 
              v-if="showUserProfileEdit" 
              :userInfo="userInfo"
              @close="closeUserProfileEdit"
              @update="updateUserInfo"
            />
          </div>
        </div>
      </header>

      <!-- 主内容 -->
      <div class="main-content">
        <!-- 这里将显示导航内容 -->
        <router-view />
      </div>
    </main>
  </div>
  </el-config-provider>
</template>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  background-color: #f5f7fa;
}

.auth-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url('./src/assets/images/login-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.auth-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.app-container {
  display: flex;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background: linear-gradient(135deg, #e8f4fd 0%, #f0f7ff 25%, #f8fafc 50%, #f0f7ff 75%, #e8f4fd 100%);
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

/* 侧边导航栏 */
.sidebar {
  position: absolute;
  left: 20px;
  top: 20px;
  bottom: 20px;
  width: 240px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  box-shadow: 
    0 4px 24px rgba(37, 99, 235, 0.08),
    0 1px 2px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  overflow: hidden;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  transform: translateX(0);
  opacity: 1;
  will-change: transform, opacity;
  z-index: 100;
}

.app-container.sidebar-hidden .sidebar {
  animation: slideOut 0.35s ease forwards;
}

.app-container:not(.sidebar-hidden) .sidebar {
  animation: slideIn 0.35s ease forwards;
}

@keyframes slideOut {
  0% {
    transform: translateX(0);
    opacity: 1;
  }
  100% {
    transform: translateX(-120%);
    opacity: 0;
    pointer-events: none;
  }
}

@keyframes slideIn {
  0% {
    transform: translateX(-120%);
    opacity: 0;
  }
  100% {
    transform: translateX(0);
    opacity: 1;
  }
}

.sidebar-header {
  height: 64px;
  padding: 0 20px;
  background: linear-gradient(135deg, #2563eb 0%, #60a5fa 25%, #ffffff 50%, #3b82f6 75%, #1d4ed8 100%);
  background-size: 400% 400%;
  animation: headerGradient 8s ease infinite;
  border-radius: 24px 24px 0 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.sidebar-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent 30%,
    rgba(255, 255, 255, 0.15) 50%,
    transparent 70%
  );
  animation: shimmer 3s ease-in-out infinite;
  pointer-events: none;
}

@keyframes headerGradient {
  0%, 100% { background-position: 0% 50%; }
  25% { background-position: 50% 0%; }
  50% { background-position: 100% 50%; }
  75% { background-position: 50% 100%; }
}

@keyframes shimmer {
  0% { transform: translateX(-100%) rotate(45deg); }
  100% { transform: translateX(100%) rotate(45deg); }
}

.sidebar-header:hover {
  box-shadow: 0 4px 25px rgba(37, 99, 235, 0.5);
}

.sidebar-header:hover .logo {
  transform: rotate(10deg) scale(1.1);
}

.sidebar-header:hover h1 {
  letter-spacing: 4px;
}

.sidebar-header .logo {
  width: 28px;
  height: 28px;
  object-fit: contain;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.sidebar-header h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  letter-spacing: 2px;
  text-shadow: 0 2px 6px rgba(0, 0, 0, 0.4), 0 0 20px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
}

.nav-menu {
  flex: 1;
  padding: 16px 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.nav-menu > ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin: 0;
  margin-bottom: 6px;
}

.nav-item-title {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.25s ease;
  border-radius: 12px;
  margin: 0 12px;
  position: relative;
}

.nav-item-title:hover {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  color: #3182ce;
  transform: translateX(4px);
}

.nav-item-title.active {
  background: linear-gradient(135deg, #ebf8ff 0%, #dbeafe 100%);
  color: #2563eb;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.15);
}

.menu-icon {
  margin-right: 14px;
  font-size: 18px;
  width: 24px;
  text-align: center;
  display: inline-block;
}

.expand-icon {
  margin-left: auto;
  font-size: 10px;
  color: #a0aec0;
  transition: all 0.25s ease;
  background: linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%);
  width: 26px;
  height: 26px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-item-title:hover .expand-icon {
  background: linear-gradient(135deg, #4299e1 0%, #667eea 100%);
  color: #ffffff;
  transform: rotate(90deg);
  box-shadow: 0 2px 6px rgba(66, 153, 225, 0.3);
}

.nav-item-title.active .expand-icon {
  background: linear-gradient(135deg, #4299e1 0%, #667eea 100%);
  color: #ffffff;
  transform: rotate(90deg);
}

.sub-menu {
  list-style: none;
  padding: 8px 0;
  margin: 0;
  transition: all 0.3s ease;
}

.sub-menu li {
  margin: 0;
}

.sub-menu a {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  text-decoration: none;
  color: #718096;
  transition: all 0.25s ease;
  font-size: 14px;
  border-radius: 10px;
  margin: 2px 12px;
  padding-left: 44px;
}

.sub-menu a .menu-icon {
  margin-right: 12px;
  font-size: 16px;
  width: 18px;
}

.sub-menu a:hover {
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  color: #4a5568;
  padding-left: 48px;
  transform: translateX(4px);
}

.sub-menu a.router-link-active {
  background: linear-gradient(135deg, #ebf8ff 0%, #dbeafe 100%);
  color: #2563eb;
  padding-left: 48px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.12);
}

/* 右侧内容区 */
.content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 
    0 4px 24px rgba(37, 99, 235, 0.08),
    0 1px 2px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 255, 255, 0.6);
  margin-left: 260px;
  transition: margin-left 0.35s ease;
}

.app-container.sidebar-hidden .content {
  margin-left: 0;
}

/* 头部 */
.header {
  height: 64px;
  background: rgba(255, 255, 255, 0.5);
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  border-radius: 24px 24px 0 0;
}

.header-title {
  display: flex;
  align-items: center;
}

.sidebar-toggle {
  width: 40px;
  height: 40px;
  border: none;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, background 0.2s ease;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

.sidebar-toggle svg {
  width: 20px;
  height: 20px;
  color: #4a5568;
  transition: transform 0.3s ease;
}

.sidebar-toggle:hover {
  background: linear-gradient(135deg, #4299e1 0%, #667eea 100%);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
  transform: scale(1.05);
}

.sidebar-toggle:hover svg {
  color: #ffffff;
}

.sidebar-toggle:active {
  transform: scale(0.98);
}

.app-container.sidebar-hidden .sidebar-toggle svg {
  transform: rotate(180deg);
}

.user-info {
  position: relative;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #f7fafc;
  border: 1px solid #e0e6ed;
}

.user-profile:hover {
  background-color: #edf2f7;
  border-color: #cbd5e0;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #4299e1;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
}

.dropdown-icon {
  font-size: 12px;
  color: #a0aec0;
  transition: transform 0.2s ease;
}

.user-profile:hover .dropdown-icon {
  color: #718096;
  transform: rotate(180deg);
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background-color: #ffffff;
  border: 1px solid #e0e6ed;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  min-width: 140px;
  z-index: 1000;
  overflow: hidden;
  transition: all 0.2s ease;
}

.user-dropdown a {
  display: block;
  padding: 10px 16px;
  text-decoration: none;
  color: #4a5568;
  font-size: 14px;
  transition: background-color 0.2s ease;
}

.user-dropdown a:hover {
  background-color: #f7fafc;
  color: #2d3748;
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 0 0 24px 24px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

/* 页面模块通用基础样式 */
.main-content > div {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 
    0 4px 16px rgba(37, 99, 235, 0.06),
    0 1px 2px rgba(0, 0, 0, 0.03),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 24px;
  transition: all 0.3s ease;
  height: 100%;
  box-sizing: border-box;
}

.main-content > div:hover {
  box-shadow: 
    0 8px 24px rgba(37, 99, 235, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 1);
}

.main-content > div h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 24px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  outline: none;
  text-decoration: none;
}

.btn-primary {
  background-color: #4299e1;
  color: #ffffff;
}

.btn-primary:hover {
  background-color: #3182ce;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(66, 153, 225, 0.3);
}

.btn-secondary {
  background-color: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background-color: #cbd5e0;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e0e6ed;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.2s ease;
  outline: none;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 160px;
  }
  
  .header {
    padding: 0 20px;
  }
  
  .main-content {
    padding: 20px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-dropdown {
  animation: fadeIn 0.2s ease;
}

/* 隐藏滚动条但保留滚动功能 */
.main-content {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.main-content::-webkit-scrollbar {
  display: none;
}

.nav-menu {
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.nav-menu::-webkit-scrollbar {
  display: none;
}

::-webkit-scrollbar {
  width: 0;
  height: 0;
  display: none;
}

* {
  scrollbar-width: none;
  -ms-overflow-style: none;
}
</style>
