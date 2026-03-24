<template>
  <div class="points-mall-container">
    <div class="header-container">
      <h2>积分商城</h2>
    </div>
    
    <div class="points-balance">
      <span class="balance-label">当前积分</span>
      <span class="balance-value">{{ userPoints }}</span>
    </div>
    
    <div class="mall-grid">
      <div v-for="item in mallItems" :key="item.id" class="mall-item">
        <div class="item-image">
          <span class="item-emoji">{{ item.emoji }}</span>
        </div>
        <div class="item-info">
          <h4>{{ item.name }}</h4>
          <p class="item-desc">{{ item.description }}</p>
          <div class="item-footer">
            <span class="item-price">
              <span class="price-icon">💎</span>
              {{ item.price }} 积分
            </span>
            <button 
              class="buy-btn" 
              :class="{ 'disabled': userPoints < item.price }"
              :disabled="userPoints < item.price"
              @click="buyItem(item)"
            >
              {{ userPoints >= item.price ? '兑换' : '积分不足' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue';
import { ElMessage } from 'element-plus';

const refreshPoints = inject('refreshPoints', () => {});

const userPoints = ref(100);

const mallItems = ref([
  { id: 1, name: '普通卡包', emoji: '📦', description: '包含3张随机普通卡片', price: 50 },
  { id: 2, name: '稀有卡包', emoji: '🎁', description: '包含1张稀有卡片', price: 100 },
  { id: 3, name: '史诗卡包', emoji: '💎', description: '包含1张史诗卡片', price: 200 },
  { id: 4, name: '传说卡包', emoji: '👑', description: '包含1张传说卡片', price: 500 },
  { id: 5, name: '双倍积分卡', emoji: '⚡', description: '24小时内积分获取翻倍', price: 150 },
  { id: 6, name: '幸运符', emoji: '🍀', description: '下次抽卡必得稀有以上', price: 80 },
]);

const buyItem = (item) => {
  if (userPoints.value >= item.price) {
    userPoints.value -= item.price;
    ElMessage.success(`成功兑换 ${item.name}！`);
    refreshPoints();
  } else {
    ElMessage.warning('积分不足，无法兑换');
  }
};
</script>

<style scoped>
.points-mall-container {
  padding: 20px;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.points-balance {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  border-radius: 24px;
  margin-bottom: 24px;
}

.balance-label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
}

.balance-value {
  color: #ffffff;
  font-size: 24px;
  font-weight: 700;
}

.mall-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.mall-item {
  display: flex;
  gap: 16px;
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.mall-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.item-image {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-emoji {
  font-size: 32px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-info h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
  font-size: 16px;
}

.item-desc {
  font-size: 12px;
  color: #718096;
  margin: 0 0 auto 0;
}

.item-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 12px;
}

.item-price {
  display: flex;
  align-items: center;
  gap: 4px;
  font-weight: 600;
  color: #2563eb;
}

.price-icon {
  font-size: 14px;
}

.buy-btn {
  padding: 6px 16px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.buy-btn:hover:not(.disabled) {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.buy-btn.disabled {
  background: #cbd5e0;
  cursor: not-allowed;
}
</style>
