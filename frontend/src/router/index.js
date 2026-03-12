import { createRouter, createWebHistory } from 'vue-router';
import DataControlConsoleFinancialLedger from '../components/DataControlConsoleFinancialLedger.vue';
import DataControlConsolePerformanceRecord from '../components/DataControlConsolePerformanceRecord.vue';
import DataControlConsolePerformanceDashboard from '../components/DataControlConsolePerformanceDashboard.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: DataControlConsoleFinancialLedger
  },
  {
    path: '/financial-ledger',
    name: 'FinancialLedger',
    component: DataControlConsoleFinancialLedger
  },
  {
    path: '/performance-record',
    name: 'PerformanceRecord',
    component: DataControlConsolePerformanceRecord
  },
  {
    path: '/performance-dashboard',
    name: 'PerformanceDashboard',
    component: DataControlConsolePerformanceDashboard
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;