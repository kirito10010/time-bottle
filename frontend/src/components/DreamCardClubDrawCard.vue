<template>
  <div class="draw-card-container">
    <div class="header-container">
      <h2>抽取卡片</h2>
    </div>
    
    <div class="draw-area">
      <!-- 单抽显示 -->
      <div v-if="!isMultiDraw" class="card-preview" :class="{ 'revealed': isRevealed }">
        <div class="card-front">
          <div class="card-front-inner">
            <span class="question-mark">?</span>
          </div>
        </div>
        <div class="card-back" v-if="isRevealed && drawnCards.length > 0" :class="getRarityClass(drawnCards[0].rarityLevel)">
          <img v-if="drawnCards[0].imageUrl" :src="drawnCards[0].imageUrl" alt="卡片" class="card-image">
          <div class="card-info">
            <span class="card-name">{{ drawnCards[0].name }}</span>
            <span class="card-type" :class="getRarityClass(drawnCards[0].rarityLevel)">{{ drawnCards[0].type }}</span>
          </div>
        </div>
      </div>
      
      <!-- 十连抽显示 -->
      <div v-if="isMultiDraw && drawnCards.length > 1" class="cards-grid">
        <div 
          v-for="(card, index) in drawnCards" 
          :key="index" 
          class="card-flip-item"
          :class="{ 'flipped': flippedCards[index] }"
        >
          <div class="card-flip-inner">
            <div class="card-flip-front">
              <span class="question-mark-small">?</span>
            </div>
            <div class="card-flip-back" :class="getRarityClass(card.rarityLevel)">
              <img v-if="card.imageUrl" :src="card.imageUrl" alt="卡片" class="card-item-image">
              <div class="card-item-info">
                <span class="card-name-small">{{ card.name }}</span>
                <span class="card-type-small" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="draw-controls">
        <button 
          class="draw-btn single" 
          :disabled="isDrawing || userPoints < 10"
          @click="drawCard(1)"
        >
          <span class="btn-title">单抽</span>
          <span class="btn-cost">10 积分</span>
        </button>
        <button 
          class="draw-btn multi" 
          :disabled="isDrawing || userPoints < 90"
          @click="drawCard(10)"
        >
          <span class="btn-title">十连抽</span>
          <span class="btn-cost">90 积分</span>
        </button>
      </div>
      
      <div class="points-bar">
        <span class="points-label">当前积分</span>
        <span class="points-value">{{ userPoints.toLocaleString() }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const refreshPoints = inject('refreshPoints', () => {});

const userPoints = ref(0);
const isDrawing = ref(false);
const isRevealed = ref(false);
const drawnCards = ref([]);
const isMultiDraw = ref(false);
const flippedCards = ref([]);

const getRarityClass = (rarityLevel) => {
  if (!rarityLevel) return 'common';
  switch (rarityLevel) {
    case 5: return 'legendary';
    case 4: return 'epic';
    case 3: return 'rare';
    case 2: return 'uncommon';
    default: return 'common';
  }
};

const flipCardsSequentially = (count) => {
  flippedCards.value = new Array(count).fill(false);
  for (let i = 0; i < count; i++) {
    setTimeout(() => {
      flippedCards.value[i] = true;
    }, i * 180);
  }
};

const fetchUserPoints = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    const response = await fetch('/api/points/status', {
      headers: { Authorization: `Bearer ${token}` }
    });
    if (response.ok) {
      const data = await response.json();
      userPoints.value = data.points || 0;
    }
  } catch (error) {
    console.error('获取积分失败:', error);
  }
};

const drawCard = async (count) => {
  const cost = count === 1 ? 10 : 90;
  
  if (userPoints.value < cost) {
    ElMessage.warning('积分不足');
    return;
  }
  
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }
  
  isDrawing.value = true;
  isRevealed.value = false;
  drawnCards.value = [];
  isMultiDraw.value = count > 1;
  
  try {
    const response = await fetch('http://localhost:8080/api/cards/draw', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ count })
    });
    
    const data = await response.json();
    
    if (response.ok && data.success) {
      const cards = data.cards || [];
      if (cards.length > 0) {
        drawnCards.value = cards;
        userPoints.value -= cost;
        isRevealed.value = true;
        
        if (count === 1) {
          ElMessage.success(`获得 ${cards[0].name}`);
        } else {
          flipCardsSequentially(cards.length);
          ElMessage.success(`十连抽完成！`);
        }
        
        refreshPoints();
      }
    } else {
      ElMessage.error(data.message || '抽卡失败');
    }
  } catch (error) {
    console.error('抽卡失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    isDrawing.value = false;
  }
};

onMounted(() => {
  fetchUserPoints();
});
</script>

<style scoped>
.draw-card-container {
  padding: 20px;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.draw-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 32px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

/* 单抽卡片样式 */
.card-preview {
  width: 180px;
  height: 252px;
  border-radius: 8px;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.5s ease;
}

.card-preview.revealed {
  transform: rotateY(180deg);
}

.card-front, .card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 8px;
  backface-visibility: hidden;
  overflow: hidden;
}

.card-front {
  background: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-front-inner {
  width: 140px;
  height: 196px;
  border: 2px dashed #475569;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.question-mark {
  font-size: 64px;
  color: #64748b;
  font-weight: 300;
  font-style: italic;
}

.card-back {
  transform: rotateY(180deg);
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-back.legendary {
  box-shadow: 
    0 0 20px rgba(255, 215, 0, 0.8),
    0 0 40px rgba(255, 215, 0, 0.5),
    0 0 60px rgba(255, 215, 0, 0.3),
    inset 0 0 20px rgba(255, 215, 0, 0.1);
  animation: glow-legendary 2s ease-in-out infinite alternate;
}

.card-back.epic {
  box-shadow: 
    0 0 15px rgba(168, 85, 247, 0.8),
    0 0 30px rgba(168, 85, 247, 0.5),
    0 0 45px rgba(168, 85, 247, 0.3),
    inset 0 0 15px rgba(168, 85, 247, 0.1);
  animation: glow-epic 2s ease-in-out infinite alternate;
}

.card-back.rare {
  box-shadow: 
    0 0 12px rgba(59, 130, 246, 0.8),
    0 0 24px rgba(59, 130, 246, 0.5),
    0 0 36px rgba(59, 130, 246, 0.3),
    inset 0 0 12px rgba(59, 130, 246, 0.1);
  animation: glow-rare 2s ease-in-out infinite alternate;
}

.card-back.uncommon {
  box-shadow: 
    0 0 10px rgba(34, 197, 94, 0.8),
    0 0 20px rgba(34, 197, 94, 0.5),
    0 0 30px rgba(34, 197, 94, 0.3),
    inset 0 0 10px rgba(34, 197, 94, 0.1);
  animation: glow-uncommon 2s ease-in-out infinite alternate;
}

.card-back.common {
  box-shadow: 
    0 0 8px rgba(156, 163, 175, 0.6),
    0 0 16px rgba(156, 163, 175, 0.3);
}

@keyframes glow-legendary {
  from {
    box-shadow: 
      0 0 20px rgba(255, 215, 0, 0.8),
      0 0 40px rgba(255, 215, 0, 0.5),
      0 0 60px rgba(255, 215, 0, 0.3),
      inset 0 0 20px rgba(255, 215, 0, 0.1);
  }
  to {
    box-shadow: 
      0 0 30px rgba(255, 215, 0, 1),
      0 0 60px rgba(255, 215, 0, 0.7),
      0 0 90px rgba(255, 215, 0, 0.4),
      inset 0 0 30px rgba(255, 215, 0, 0.2);
  }
}

@keyframes glow-epic {
  from {
    box-shadow: 
      0 0 15px rgba(168, 85, 247, 0.8),
      0 0 30px rgba(168, 85, 247, 0.5),
      0 0 45px rgba(168, 85, 247, 0.3),
      inset 0 0 15px rgba(168, 85, 247, 0.1);
  }
  to {
    box-shadow: 
      0 0 25px rgba(168, 85, 247, 1),
      0 0 50px rgba(168, 85, 247, 0.7),
      0 0 75px rgba(168, 85, 247, 0.4),
      inset 0 0 25px rgba(168, 85, 247, 0.2);
  }
}

@keyframes glow-rare {
  from {
    box-shadow: 
      0 0 12px rgba(59, 130, 246, 0.8),
      0 0 24px rgba(59, 130, 246, 0.5),
      0 0 36px rgba(59, 130, 246, 0.3),
      inset 0 0 12px rgba(59, 130, 246, 0.1);
  }
  to {
    box-shadow: 
      0 0 20px rgba(59, 130, 246, 1),
      0 0 40px rgba(59, 130, 246, 0.7),
      0 0 60px rgba(59, 130, 246, 0.4),
      inset 0 0 20px rgba(59, 130, 246, 0.2);
  }
}

@keyframes glow-uncommon {
  from {
    box-shadow: 
      0 0 10px rgba(34, 197, 94, 0.8),
      0 0 20px rgba(34, 197, 94, 0.5),
      0 0 30px rgba(34, 197, 94, 0.3),
      inset 0 0 10px rgba(34, 197, 94, 0.1);
  }
  to {
    box-shadow: 
      0 0 15px rgba(34, 197, 94, 1),
      0 0 30px rgba(34, 197, 94, 0.7),
      0 0 45px rgba(34, 197, 94, 0.4),
      inset 0 0 15px rgba(34, 197, 94, 0.2);
  }
}

.card-image {
  flex: 1;
  width: 100%;
  object-fit: cover;
}

.card-info {
  padding: 12px;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 4px;
  border-top: 1px solid #e2e8f0;
}

.card-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
}

.card-type {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
  text-align: center;
  letter-spacing: 0.5px;
}

.card-type.legendary {
  background: #fef3c7;
  color: #b45309;
}

.card-type.epic {
  background: #ede9fe;
  color: #7c3aed;
}

.card-type.rare {
  background: #dbeafe;
  color: #2563eb;
}

.card-type.common {
  background: #f1f5f9;
  color: #64748b;
}

/* 十连抽卡片网格样式 */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  width: 100%;
  max-width: 600px;
  margin-bottom: 24px;
}

.card-flip-item {
  position: relative;
  aspect-ratio: 3/4;
  perspective: 800px;
}

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
  border-radius: 6px;
  backface-visibility: hidden;
  overflow: hidden;
}

.card-flip-front {
  background: #1e293b;
  display: flex;
  align-items: center;
  justify-content: center;
}

.question-mark-small {
  font-size: 28px;
  color: #475569;
  font-weight: 300;
  font-style: italic;
}

.card-flip-back {
  transform: rotateY(180deg);
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.card-flip-back.legendary {
  box-shadow: 
    0 0 10px rgba(255, 215, 0, 0.8),
    0 0 20px rgba(255, 215, 0, 0.5),
    0 0 30px rgba(255, 215, 0, 0.3);
  animation: glow-legendary-small 2s ease-in-out infinite alternate;
}

.card-flip-back.epic {
  box-shadow: 
    0 0 8px rgba(168, 85, 247, 0.8),
    0 0 16px rgba(168, 85, 247, 0.5),
    0 0 24px rgba(168, 85, 247, 0.3);
  animation: glow-epic-small 2s ease-in-out infinite alternate;
}

.card-flip-back.rare {
  box-shadow: 
    0 0 6px rgba(59, 130, 246, 0.8),
    0 0 12px rgba(59, 130, 246, 0.5),
    0 0 18px rgba(59, 130, 246, 0.3);
  animation: glow-rare-small 2s ease-in-out infinite alternate;
}

.card-flip-back.uncommon {
  box-shadow: 
    0 0 5px rgba(34, 197, 94, 0.8),
    0 0 10px rgba(34, 197, 94, 0.5),
    0 0 15px rgba(34, 197, 94, 0.3);
  animation: glow-uncommon-small 2s ease-in-out infinite alternate;
}

.card-flip-back.common {
  box-shadow: 
    0 0 4px rgba(156, 163, 175, 0.6),
    0 0 8px rgba(156, 163, 175, 0.3);
}

@keyframes glow-legendary-small {
  from {
    box-shadow: 
      0 0 10px rgba(255, 215, 0, 0.8),
      0 0 20px rgba(255, 215, 0, 0.5),
      0 0 30px rgba(255, 215, 0, 0.3);
  }
  to {
    box-shadow: 
      0 0 15px rgba(255, 215, 0, 1),
      0 0 30px rgba(255, 215, 0, 0.7),
      0 0 45px rgba(255, 215, 0, 0.4);
  }
}

@keyframes glow-epic-small {
  from {
    box-shadow: 
      0 0 8px rgba(168, 85, 247, 0.8),
      0 0 16px rgba(168, 85, 247, 0.5),
      0 0 24px rgba(168, 85, 247, 0.3);
  }
  to {
    box-shadow: 
      0 0 12px rgba(168, 85, 247, 1),
      0 0 24px rgba(168, 85, 247, 0.7),
      0 0 36px rgba(168, 85, 247, 0.4);
  }
}

@keyframes glow-rare-small {
  from {
    box-shadow: 
      0 0 6px rgba(59, 130, 246, 0.8),
      0 0 12px rgba(59, 130, 246, 0.5),
      0 0 18px rgba(59, 130, 246, 0.3);
  }
  to {
    box-shadow: 
      0 0 10px rgba(59, 130, 246, 1),
      0 0 20px rgba(59, 130, 246, 0.7),
      0 0 30px rgba(59, 130, 246, 0.4);
  }
}

@keyframes glow-uncommon-small {
  from {
    box-shadow: 
      0 0 5px rgba(34, 197, 94, 0.8),
      0 0 10px rgba(34, 197, 94, 0.5),
      0 0 15px rgba(34, 197, 94, 0.3);
  }
  to {
    box-shadow: 
      0 0 8px rgba(34, 197, 94, 1),
      0 0 16px rgba(34, 197, 94, 0.7),
      0 0 24px rgba(34, 197, 94, 0.4);
  }
}

.card-item-image {
  flex: 1;
  width: 100%;
  object-fit: cover;
}

.card-item-info {
  padding: 6px 4px;
  background: #fff;
  border-top: 1px solid #f1f5f9;
}

.card-name-small {
  display: block;
  font-size: 9px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-type-small {
  display: block;
  font-size: 8px;
  font-weight: 700;
  padding: 1px 4px;
  border-radius: 3px;
  text-align: center;
  letter-spacing: 0.3px;
  margin-top: 2px;
}

.card-type-small.legendary {
  background: #fef3c7;
  color: #b45309;
}

.card-type-small.epic {
  background: #ede9fe;
  color: #7c3aed;
}

.card-type-small.rare {
  background: #dbeafe;
  color: #2563eb;
}

.card-type-small.common {
  background: #f1f5f9;
  color: #64748b;
}

/* 按钮样式 */
.draw-controls {
  display: flex;
  gap: 12px;
  margin-top: 32px;
}

.draw-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 14px 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 100px;
}

.draw-btn.single {
  background: #1e293b;
  color: white;
}

.draw-btn.multi {
  background: #334155;
  color: white;
}

.draw-btn:hover:not(:disabled) {
  background: #334155;
}

.draw-btn.multi:hover:not(:disabled) {
  background: #475569;
}

.draw-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-title {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.btn-cost {
  font-size: 11px;
  opacity: 0.6;
}

/* 积分信息 */
.points-bar {
  margin-top: 24px;
  padding: 12px 24px;
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.points-label {
  color: #64748b;
  font-size: 13px;
}

.points-value {
  color: #1e293b;
  font-size: 18px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
}

/* 响应式 */
@media (max-width: 640px) {
  .draw-area {
    padding: 24px 16px;
  }
  
  .cards-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }
  
  .card-preview {
    width: 140px;
    height: 196px;
  }
  
  .question-mark {
    font-size: 48px;
  }
  
  .card-front-inner {
    width: 100px;
    height: 140px;
  }
  
  .draw-controls {
    flex-direction: column;
    width: 100%;
    max-width: 200px;
  }
  
  .draw-btn {
    width: 100%;
  }
}
</style>
