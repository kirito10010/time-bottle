<template>
  <div class="draw-card-container">
    <h2>抽取卡片</h2>
    <p class="description">消耗积分抽取随机卡片</p>
    
    <div class="draw-area">
      <div class="card-preview" :class="{ 'revealed': isRevealed }">
        <div class="card-front">
          <span class="question-mark">?</span>
        </div>
        <div class="card-back" v-if="isRevealed && drawnCard">
          <span class="card-emoji">{{ drawnCard.emoji }}</span>
          <h4>{{ drawnCard.name }}</h4>
          <p class="card-rarity" :class="drawnCard.rarity">{{ drawnCard.rarityText }}</p>
        </div>
      </div>
      
      <div class="draw-buttons">
        <button 
          class="draw-btn single" 
          :disabled="isDrawing || userPoints < 10"
          @click="drawCard(1)"
        >
          <span class="btn-icon">🎴</span>
          <span class="btn-text">
            <span class="btn-title">单抽</span>
            <span class="btn-cost">10 积分</span>
          </span>
        </button>
        <button 
          class="draw-btn multi" 
          :disabled="isDrawing || userPoints < 90"
          @click="drawCard(10)"
        >
          <span class="btn-icon">🎰</span>
          <span class="btn-text">
            <span class="btn-title">十连抽</span>
            <span class="btn-cost">90 积分</span>
          </span>
        </button>
      </div>
      
      <div class="points-info">
        <span class="points-label">当前积分:</span>
        <span class="points-value">{{ userPoints }}</span>
      </div>
    </div>
    
    <div v-if="drawHistory.length > 0" class="history-section">
      <h3>最近抽取</h3>
      <div class="history-grid">
        <div v-for="(card, index) in drawHistory" :key="index" class="history-item">
          <span class="history-emoji">{{ card.emoji }}</span>
          <span class="history-name">{{ card.name }}</span>
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
const isDrawing = ref(false);
const isRevealed = ref(false);
const drawnCard = ref(null);
const drawHistory = ref([]);

const cardPool = [
  { name: '火焰龙', emoji: '🐉', rarity: 'legendary', rarityText: '传说', chance: 0.01 },
  { name: '冰霜凤凰', emoji: '🦅', rarity: 'legendary', rarityText: '传说', chance: 0.01 },
  { name: '雷霆狮', emoji: '🦁', rarity: 'epic', rarityText: '史诗', chance: 0.05 },
  { name: '暗影狼', emoji: '🐺', rarity: 'epic', rarityText: '史诗', chance: 0.05 },
  { name: '水晶鹿', emoji: '🦌', rarity: 'rare', rarityText: '稀有', chance: 0.15 },
  { name: '风暴鹰', emoji: '🦅', rarity: 'rare', rarityText: '稀有', chance: 0.15 },
  { name: '森林熊', emoji: '🐻', rarity: 'common', rarityText: '普通', chance: 0.29 },
  { name: '海洋龟', emoji: '🐢', rarity: 'common', rarityText: '普通', chance: 0.29 },
];

const getRandomCard = () => {
  const rand = Math.random();
  let cumulative = 0;
  for (const card of cardPool) {
    cumulative += card.chance;
    if (rand <= cumulative) {
      return { ...card };
    }
  }
  return { ...cardPool[cardPool.length - 1] };
};

const drawCard = async (count) => {
  const cost = count === 1 ? 10 : 90;
  
  if (userPoints.value < cost) {
    ElMessage.warning('积分不足');
    return;
  }
  
  isDrawing.value = true;
  isRevealed.value = false;
  userPoints.value -= cost;
  
  await new Promise(resolve => setTimeout(resolve, 500));
  
  if (count === 1) {
    drawnCard.value = getRandomCard();
    drawHistory.value.unshift(drawnCard.value);
  } else {
    const cards = [];
    for (let i = 0; i < count; i++) {
      cards.push(getRandomCard());
    }
    drawnCard.value = cards[0];
    drawHistory.value.unshift(...cards);
  }
  
  if (drawHistory.value.length > 20) {
    drawHistory.value = drawHistory.value.slice(0, 20);
  }
  
  isRevealed.value = true;
  isDrawing.value = false;
  
  ElMessage.success(`恭喜获得 ${drawnCard.value.name}！`);
  refreshPoints();
};
</script>

<style scoped>
.draw-card-container {
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

.draw-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px;
  background: linear-gradient(135deg, #1e1b4b 0%, #312e81 100%);
  border-radius: 16px;
  margin-bottom: 24px;
}

.card-preview {
  width: 200px;
  height: 280px;
  border-radius: 16px;
  position: relative;
  transition: transform 0.6s ease;
  transform-style: preserve-3d;
}

.card-preview.revealed {
  transform: rotateY(180deg);
}

.card-front, .card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  backface-visibility: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.card-front {
  background: linear-gradient(135deg, #4338ca 0%, #6366f1 100%);
  border: 3px solid #818cf8;
}

.question-mark {
  font-size: 80px;
  color: rgba(255, 255, 255, 0.8);
}

.card-back {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border: 3px solid #f59e0b;
  transform: rotateY(180deg);
}

.card-back .card-emoji {
  font-size: 60px;
  margin-bottom: 12px;
}

.card-back h4 {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 18px;
}

.card-rarity {
  font-size: 14px;
  font-weight: 600;
  padding: 4px 12px;
  border-radius: 12px;
}

.card-rarity.legendary {
  background: #fef3c7;
  color: #f59e0b;
}

.card-rarity.epic {
  background: #ede9fe;
  color: #8b5cf6;
}

.card-rarity.rare {
  background: #dbeafe;
  color: #3b82f6;
}

.card-rarity.common {
  background: #f3f4f6;
  color: #6b7280;
}

.draw-buttons {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.draw-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.draw-btn.single {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
}

.draw-btn.multi {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  color: white;
}

.draw-btn:hover:not(:disabled) {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

.draw-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-icon {
  font-size: 24px;
}

.btn-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.btn-title {
  font-size: 16px;
  font-weight: 600;
}

.btn-cost {
  font-size: 12px;
  opacity: 0.8;
}

.points-info {
  margin-top: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.points-label {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.points-value {
  color: #fbbf24;
  font-size: 20px;
  font-weight: 700;
}

.history-section {
  background: #ffffff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.history-section h3 {
  margin: 0 0 12px 0;
  color: #2d3748;
  font-size: 16px;
}

.history-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: #f7fafc;
  border-radius: 8px;
  font-size: 12px;
}

.history-emoji {
  font-size: 16px;
}

.history-name {
  color: #4a5568;
}
</style>
