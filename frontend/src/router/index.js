import { createRouter, createWebHistory } from 'vue-router';
import Introduction from '../components/Introduction.vue';
import Login from '../components/Login.vue';
import Register from '../components/Register.vue';
import DataControlConsoleFinancialLedger from '../components/DataControlConsoleFinancialLedger.vue';
import DataControlConsolePerformanceRecord from '../components/DataControlConsolePerformanceRecord.vue';
import DataControlConsolePerformanceDashboard from '../components/DataControlConsolePerformanceDashboard.vue';
import DreamCardClubMyAlbum from '../components/DreamCardClubMyAlbum.vue';
import DreamCardClubPointsExam from '../components/DreamCardClubPointsExam.vue';
import DreamCardClubPointsMall from '../components/DreamCardClubPointsMall.vue';
import DreamCardClubDrawCard from '../components/DreamCardClubDrawCard.vue';
import AdminPanelUsers from '../components/AdminPanelUsers.vue';
import AdminPanelCards from '../components/AdminPanelCards.vue';
import AdminPanelFeedback from '../components/AdminPanelFeedback.vue';

const routes = [
  {
    path: '/',
    name: 'Introduction',
    component: Introduction,
    meta: { public: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { public: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { public: true }
  },
  {
    path: '/financial-ledger',
    name: 'FinancialLedger',
    component: DataControlConsoleFinancialLedger,
    meta: { requiresAuth: true }
  },
  {
    path: '/performance-record',
    name: 'PerformanceRecord',
    component: DataControlConsolePerformanceRecord,
    meta: { requiresAuth: true }
  },
  {
    path: '/performance-dashboard',
    name: 'PerformanceDashboard',
    component: DataControlConsolePerformanceDashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/my-album',
    name: 'MyAlbum',
    component: DreamCardClubMyAlbum,
    meta: { requiresAuth: true }
  },
  {
    path: '/points-exam',
    name: 'PointsExam',
    component: DreamCardClubPointsExam,
    meta: { requiresAuth: true }
  },
  {
    path: '/points-mall',
    name: 'PointsMall',
    component: DreamCardClubPointsMall,
    meta: { requiresAuth: true }
  },
  {
    path: '/draw-card',
    name: 'DrawCard',
    component: DreamCardClubDrawCard,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-users',
    name: 'AdminUsers',
    component: AdminPanelUsers,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-cards',
    name: 'AdminCards',
    component: AdminPanelCards,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin-feedback',
    name: 'AdminFeedback',
    component: AdminPanelFeedback,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  const savedUser = localStorage.getItem('user');
  const isLoggedIn = !!savedUser;
  
  if (to.meta.requiresAuth && !isLoggedIn) {
    return '/login';
  }
  
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    return '/financial-ledger';
  }
  
  return true;
});

export default router;
