<template>
  <div class="admin-feedback-container">
    <h2>反馈管理</h2>
    <p class="description">处理用户反馈和建议</p>
    
    <div class="stats-row">
      <div class="stat-card">
        <span class="stat-value">{{ pendingCount }}</span>
        <span class="stat-label">待处理</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ processingCount }}</span>
        <span class="stat-label">处理中</span>
      </div>
      <div class="stat-card">
        <span class="stat-value">{{ completedCount }}</span>
        <span class="stat-label">已完成</span>
      </div>
    </div>
    
    <div class="filter-bar">
      <select v-model="filterStatus">
        <option value="">全部状态</option>
        <option value="pending">待处理</option>
        <option value="processing">处理中</option>
        <option value="completed">已完成</option>
      </select>
      <select v-model="filterType">
        <option value="">全部类型</option>
        <option value="bug">Bug反馈</option>
        <option value="feature">功能建议</option>
        <option value="other">其他</option>
      </select>
    </div>
    
    <div class="feedback-list">
      <div v-for="feedback in filteredFeedbacks" :key="feedback.id" class="feedback-item">
        <div class="feedback-header">
          <div class="feedback-info">
            <span class="feedback-type" :class="feedback.type">{{ feedback.typeText }}</span>
            <span class="feedback-status" :class="feedback.status">{{ feedback.statusText }}</span>
          </div>
          <span class="feedback-time">{{ feedback.createdAt }}</span>
        </div>
        <div class="feedback-user">
          <span class="user-avatar">{{ feedback.username.charAt(0).toUpperCase() }}</span>
          <span class="user-name">{{ feedback.username }}</span>
        </div>
        <h4 class="feedback-title">{{ feedback.title }}</h4>
        <p class="feedback-content">{{ feedback.content }}</p>
        <div class="feedback-actions">
          <button 
            v-if="feedback.status !== 'processing'" 
            class="btn-process" 
            @click="processFeedback(feedback)"
          >
            处理
          </button>
          <button 
            v-if="feedback.status !== 'completed'" 
            class="btn-complete" 
            @click="completeFeedback(feedback)"
          >
            完成
          </button>
          <button class="btn-reply" @click="replyFeedback(feedback)">回复</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

const filterStatus = ref('');
const filterType = ref('');

const feedbacks = ref([
  { id: 1, username: '小明', type: 'bug', typeText: 'Bug反馈', title: '签到功能异常', content: '点击签到按钮后没有反应，刷新页面后显示已签到，但积分没有增加。', status: 'pending', statusText: '待处理', createdAt: '2024-03-20 14:30' },
  { id: 2, username: '小红', type: 'feature', typeText: '功能建议', title: '希望增加卡片交易功能', content: '建议增加玩家之间交易卡片的功能，这样可以让收集更加有趣。', status: 'processing', statusText: '处理中', createdAt: '2024-03-19 10:15' },
  { id: 3, username: 'VIP用户', type: 'feature', typeText: '功能建议', title: '建议增加卡片图鉴分享', content: '希望能把收集的卡片图鉴分享到社交媒体，展示收藏成果。', status: 'completed', statusText: '已完成', createdAt: '2024-03-18 09:00' },
  { id: 4, username: '测试用户', type: 'other', typeText: '其他', title: '界面优化建议', content: '建议在抽卡界面增加动画效果，让抽卡体验更加刺激。', status: 'pending', statusText: '待处理', createdAt: '2024-03-17 16:45' },
]);

const pendingCount = computed(() => feedbacks.value.filter(f => f.status === 'pending').length);
const processingCount = computed(() => feedbacks.value.filter(f => f.status === 'processing').length);
const completedCount = computed(() => feedbacks.value.filter(f => f.status === 'completed').length);

const filteredFeedbacks = computed(() => {
  return feedbacks.value.filter(f => {
    const matchStatus = !filterStatus.value || f.status === filterStatus.value;
    const matchType = !filterType.value || f.type === filterType.value;
    return matchStatus && matchType;
  });
});

const processFeedback = (feedback) => {
  feedback.status = 'processing';
  feedback.statusText = '处理中';
  ElMessage.success('已将反馈标记为处理中');
};

const completeFeedback = (feedback) => {
  feedback.status = 'completed';
  feedback.statusText = '已完成';
  ElMessage.success('已将反馈标记为完成');
};

const replyFeedback = (feedback) => {
  ElMessage.info(`回复反馈: ${feedback.title}`);
};
</script>

<style scoped>
.admin-feedback-container {
  padding: 20px;
}

h2 {
  margin-bottom: 8px;
  color: #1a202c;
}

.description {
  color: #718096;
  margin-bottom: 24px;
}

.stats-row {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.filter-bar select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.feedback-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feedback-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.feedback-info {
  display: flex;
  gap: 8px;
}

.feedback-type {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.feedback-type.bug {
  background: #fee2e2;
  color: #dc2626;
}

.feedback-type.feature {
  background: #dbeafe;
  color: #2563eb;
}

.feedback-type.other {
  background: #f3f4f6;
  color: #6b7280;
}

.feedback-status {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.feedback-status.pending {
  background: #fef3c7;
  color: #d97706;
}

.feedback-status.processing {
  background: #dbeafe;
  color: #2563eb;
}

.feedback-status.completed {
  background: #d1fae5;
  color: #059669;
}

.feedback-time {
  font-size: 12px;
  color: #94a3b8;
}

.feedback-user {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.user-avatar {
  width: 28px;
  height: 28px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: 600;
}

.user-name {
  font-size: 13px;
  color: #64748b;
}

.feedback-title {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 16px;
}

.feedback-content {
  margin: 0 0 16px 0;
  color: #475569;
  font-size: 14px;
  line-height: 1.6;
}

.feedback-actions {
  display: flex;
  gap: 8px;
}

.btn-process, .btn-complete, .btn-reply {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-process {
  background: #dbeafe;
  color: #2563eb;
}

.btn-complete {
  background: #d1fae5;
  color: #059669;
}

.btn-reply {
  background: #f3f4f6;
  color: #4b5563;
}

.btn-process:hover, .btn-complete:hover, .btn-reply:hover {
  transform: scale(1.05);
}
</style>
