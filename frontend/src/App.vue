<script setup>
import { ref, onMounted, onUnmounted, watch, provide, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import UserProfileEdit from './components/UserProfileEdit.vue';
import { ElConfigProvider, ElMessage, ElMessageBox } from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';

const router = useRouter();
const route = useRoute();

const isLoggedIn = ref(false);
const showTokenExpiredModal = ref(false);

const userInfo = ref({
  username: '用户名',
  avatar: '',
  nickname: '未命名',
  id: null,
  role: '0'
});

const userPoints = ref(0);
const hasSignedIn = ref(false);
const isSigningIn = ref(false);

const isAdmin = computed(() => userInfo.value.role === '2');

const expandedMenus = ref({
  dataControl: true,
  dreamCardClub: false,
  adminPanel: false
});

const sidebarVisible = ref(true);
const showUserMenu = ref(false);
const showUserProfileEdit = ref(false);

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
  }).catch(() => {
    showTokenExpiredModal.value = false;
    logout();
  });
};

const authFetch = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  const headers = {
    ...options.headers
  };
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  if (options.body && typeof options.body === 'string' && !headers['Content-Type']) {
    headers['Content-Type'] = 'application/json';
  }
  
  const response = await fetch(url, {
    ...options,
    headers
  });
  
  if (response.status === 401) {
    handleTokenExpired();
    throw new Error('Unauthorized');
  }
  
  return response;
};

provide('authFetch', authFetch);
provide('handleTokenExpired', handleTokenExpired);

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

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value;
};

const toggleMenu = (menuKey) => {
  expandedMenus.value[menuKey] = !expandedMenus.value[menuKey];
  saveMenuState();
};

const saveMenuState = () => {
  localStorage.setItem('expandedMenus', JSON.stringify(expandedMenus.value));
};

const loadMenuState = () => {
  const saved = localStorage.getItem('expandedMenus');
  if (saved) {
    try {
      const parsed = JSON.parse(saved);
      expandedMenus.value = { ...expandedMenus.value, ...parsed };
    } catch (e) {
      console.error('Failed to parse menu state:', e);
    }
  }
};

const logout = () => {
  isLoggedIn.value = false;
  localStorage.removeItem('user');
  localStorage.removeItem('token');
  userInfo.value = {
    username: '用户名',
    avatar: '',
    nickname: '未命名',
    id: null,
    role: '0'
  };
  userPoints.value = 0;
  hasSignedIn.value = false;
  router.push('/');
};

const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return '/default-avatar.svg';
  }
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar;
  }
  if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') {
    return '/default-avatar.svg';
  }
  if (avatar.includes('\\') || avatar.includes('/')) {
    const usersimgIndex = avatar.indexOf('Usersimg');
    if (usersimgIndex !== -1) {
      const relativePath = avatar.substring(usersimgIndex);
      return '/' + relativePath.replace(/\\/g, '/');
    }
  }
  return '/Usersimg/' + avatar;
};

const openUserProfileEdit = () => {
  showUserProfileEdit.value = true;
  showUserMenu.value = false;
};

const closeUserProfileEdit = () => {
  showUserProfileEdit.value = false;
};

const updateUserInfo = (updatedUser) => {
  userInfo.value = updatedUser;
  localStorage.setItem('user', JSON.stringify(updatedUser));
};

const handleClickOutside = (event) => {
  const userProfile = event.target.closest('.user-profile');
  if (!userProfile) {
    showUserMenu.value = false;
  }
};

const checkAuth = () => {
  const savedUser = localStorage.getItem('user');
  if (savedUser) {
    isLoggedIn.value = true;
    userInfo.value = JSON.parse(savedUser);
    if (userInfo.value.id) {
      fetchPointsStatus();
    }
  } else {
    isLoggedIn.value = false;
  }
};

const fetchPointsStatus = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    const response = await authFetch('/api/points/status');
    if (response && response.ok) {
      const data = await response.json();
      userPoints.value = data.points || 0;
      hasSignedIn.value = data.hasSignedIn || false;
    }
  } catch (error) {
    if (error.message !== 'Unauthorized') {
      console.error('获取积分状态失败:', error);
    }
  }
};

const refreshPoints = () => {
  fetchPointsStatus();
};

provide('refreshPoints', refreshPoints);

const handleSignIn = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  if (hasSignedIn.value) {
    ElMessage.warning('今日已签到，请明天再来！');
    return;
  }
  
  isSigningIn.value = true;
  try {
    const response = await fetch('/api/points/sign-in', {
      method: 'POST',
      headers: { 
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    const data = await response.json();
    if (data.success) {
      userPoints.value = data.points;
      hasSignedIn.value = true;
      ElMessage.success(data.message);
    } else {
      ElMessage.warning(data.message);
      if (data.message === '今日已签到，请明天再来！') {
        hasSignedIn.value = true;
      }
    }
  } catch (error) {
    console.error('签到失败:', error);
    ElMessage.error('签到失败，请稍后重试');
  } finally {
    isSigningIn.value = false;
  }
};

const isPublicPage = () => {
  return route.meta.public === true;
};

const isAuthPage = () => {
  return route.path === '/login' || route.path === '/register';
};

watch(() => route.path, () => {
  checkAuth();
});

onMounted(() => {
  loadMenuState();
  loadSidebarState();
  checkAuth();
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <el-config-provider :locale="zhCn">
    <!-- 公开页面（介绍页、登录、注册） -->
    <div v-if="isPublicPage()" class="public-page" :class="{ 'auth-page': isAuthPage() }">
      <div v-if="isAuthPage()" class="auth-background"></div>
      <router-view />
    </div>
    
    <div v-else-if="isLoggedIn" class="app-container" :class="{ 'sidebar-hidden': !sidebarVisible }">
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
              <Transition name="sub-menu">
                <ul v-if="expandedMenus.dataControl" class="sub-menu">
                  <li><router-link to="/financial-ledger"><span class="menu-icon">💰</span>财务台账</router-link></li>
                  <li><router-link to="/performance-record"><span class="menu-icon">📈</span>绩效记录</router-link></li>
                  <li><router-link to="/performance-dashboard"><span class="menu-icon">📋</span>绩效看板</router-link></li>
                </ul>
              </Transition>
            </li>
            <li class="nav-item">
              <div class="nav-item-title" @click="toggleMenu('dreamCardClub')">
                <span class="menu-icon">🎴</span>
                <span>梦幻集卡社</span>
                <span class="expand-icon">{{ expandedMenus.dreamCardClub ? '▼' : '▶' }}</span>
              </div>
              <Transition name="sub-menu">
                <ul v-if="expandedMenus.dreamCardClub" class="sub-menu">
                  <li><router-link to="/my-album"><span class="menu-icon">📚</span>我的图鉴</router-link></li>
                  <li><router-link to="/points-exam"><span class="menu-icon">📝</span>积分考试</router-link></li>
                  <li><router-link to="/points-mall"><span class="menu-icon">🛒</span>积分商城</router-link></li>
                  <li><router-link to="/card-exchange"><span class="menu-icon">🔄</span>卡牌交换</router-link></li>
                  <li><router-link to="/draw-card"><span class="menu-icon">🎰</span>抽取卡片</router-link></li>
                </ul>
              </Transition>
            </li>
            <li class="nav-item" v-if="isAdmin">
              <div class="nav-item-title" @click="toggleMenu('adminPanel')">
                <span class="menu-icon">⚙️</span>
                <span>管理员后台</span>
                <span class="expand-icon">{{ expandedMenus.adminPanel ? '▼' : '▶' }}</span>
              </div>
              <Transition name="sub-menu">
                <ul v-if="expandedMenus.adminPanel" class="sub-menu">
                  <li><router-link to="/admin-users"><span class="menu-icon">👥</span>用户管理</router-link></li>
                  <li><router-link to="/admin-cards"><span class="menu-icon">🃏</span>集卡管理</router-link></li>
                  <li><router-link to="/admin-questions"><span class="menu-icon">📝</span>题目管理</router-link></li>
                  <li><router-link to="/admin-feedback"><span class="menu-icon">💬</span>反馈管理</router-link></li>
                </ul>
              </Transition>
            </li>
          </ul>
        </nav>
      </aside>

      <main class="content">
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
          <div class="header-marquee">
            <div class="marquee-content">
              <span class="marquee-item">💡 每日签到可获得5积分</span>
              <span class="marquee-item">📝 积分考试答对3题得10积分，答对4题得30积分，答对5题得60积分</span>
              <span class="marquee-item">🎰 抽卡可获得稀有卡片，在积分商城出售给其他玩家赚取积分</span>
              <span class="marquee-item">🛒 在积分商城购买其他玩家出售的卡片</span>
              <span class="marquee-item">💡 每日签到可获得5积分</span>
              <span class="marquee-item">📝 积分考试答对3题得10积分，答对4题得30积分，答对5题得60积分</span>
              <span class="marquee-item">🎰 抽卡可获得稀有卡片，在积分商城出售给其他玩家赚取积分</span>
              <span class="marquee-item">🛒 在积分商城购买其他玩家出售的卡片</span>
            </div>
          </div>
          <div class="user-info">
            <div class="user-profile" @click="toggleUserMenu">
              <img :src="getAvatarUrl(userInfo.avatar)" alt="用户头像" class="avatar">
              <span class="username">{{ userInfo.nickname || userInfo.username }}</span>
              <span class="dropdown-icon">▼</span>
              <div v-if="showUserMenu" class="user-dropdown">
                <div class="points-display">
                  <span class="points-label">当前积分</span>
                  <span class="points-value">{{ userPoints }}</span>
                </div>
                <a href="#" @click.stop.prevent="handleSignIn" :class="{ 'signed-in': hasSignedIn }">
                  {{ hasSignedIn ? '✓ 已签到' : '📅 签到领积分' }}
                </a>
                <a href="#" @click.stop="openUserProfileEdit">修改信息</a>
                <a href="#" @click.prevent="logout">退出登录</a>
              </div>
              
              <UserProfileEdit 
                v-if="showUserProfileEdit" 
                :userInfo="userInfo"
                @close="closeUserProfileEdit"
                @update="updateUserInfo"
              />
            </div>
          </div>
        </header>

        <div class="main-content">
          <router-view />
        </div>
      </main>
    </div>
  </el-config-provider>
</template>

<style scoped>
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

.public-page {
  width: 100%;
  min-height: 100vh;
}

.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: url('./assets/images/login-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  z-index: 1;
}

.auth-background::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
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
}

.sub-menu-enter-active,
.sub-menu-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}

.sub-menu-enter-from,
.sub-menu-leave-to {
  opacity: 0;
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
}

.sub-menu-enter-to,
.sub-menu-leave-from {
  opacity: 1;
  max-height: 200px;
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

.header-marquee {
  flex: 1;
  overflow: hidden;
  margin: 0 20px;
}

.marquee-content {
  display: flex;
  animation: marquee 15s linear infinite;
  white-space: nowrap;
}

.marquee-item {
  display: inline-flex;
  align-items: center;
  padding: 8px 24px;
  font-size: 13px;
  color: #4a5568;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 20px;
  margin-right: 40px;
  white-space: nowrap;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-50%);
  }
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
  cursor: pointer;
}

.user-dropdown a:hover {
  background-color: #f7fafc;
  color: #2d3748;
}

.user-dropdown .points-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-bottom: 1px solid #e0e6ed;
}

.user-dropdown .points-label {
  font-size: 13px;
  color: #718096;
}

.user-dropdown .points-value {
  font-size: 18px;
  font-weight: 600;
  color: #2563eb;
}

.user-dropdown a.signed-in {
  color: #10b981;
  cursor: default;
  background-color: #f0fdf4;
}

.user-dropdown a.signed-in:hover {
  background-color: #f0fdf4;
  color: #10b981;
}

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
