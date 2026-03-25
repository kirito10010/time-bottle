<template>
  <div class="draw-card-container">
    <div class="header-container">
      <h2>抽取卡片</h2>
      <div class="points-badge">
        <span class="points-icon">💎</span>
        <span class="points-value">{{ userPoints.toLocaleString() }}</span>
      </div>
    </div>
    
    <div class="draw-area">
      <div class="probability-info">
        <span class="prob-title">概率公示</span>
        <div class="prob-items">
          <span class="prob-item legendary">传说 5%</span>
          <span class="prob-item epic">史诗 10%</span>
          <span class="prob-item rare">稀有 15%</span>
          <span class="prob-item uncommon">精良 30%</span>
          <span class="prob-item common">普通 40%</span>
        </div>
      </div>
      
      <div class="bg-decoration">
        <div class="star" v-for="n in 20" :key="n" :style="getStarStyle(n)"></div>
        <div class="floating-card" v-for="n in 6" :key="'card'+n" :style="getFloatingCardStyle(n)">🎴</div>
      </div>
      
      <div class="gacha-machine">
        <div class="machine-top">
          <div class="machine-lights">
            <span class="light" v-for="n in 8" :key="n"></span>
          </div>
          <div class="machine-title">GACHA</div>
        </div>
        
        <div class="machine-body">
          <div class="card-stage">
            <div class="stage-glow"></div>
            <div class="stage-particles">
              <span v-for="n in 12" :key="n" class="particle"></span>
            </div>
            
            <div v-if="!isMultiDraw" class="card-preview" :class="{ 'revealed': isRevealed }">
              <div class="card-front">
                <div class="card-front-inner">
                  <div class="mystery-pattern">
                    <span class="pattern-line" v-for="n in 4" :key="n"></span>
                  </div>
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
          </div>
        </div>
        
        <div class="machine-bottom">
          <div class="draw-controls">
            <button 
              class="draw-btn single" 
              :disabled="isDrawing || userPoints < 10"
              @click="drawCard(1)"
            >
              <span class="btn-icon">🎴</span>
              <span class="btn-content">
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
              <span class="btn-content">
                <span class="btn-title">十连抽</span>
                <span class="btn-cost">90 积分</span>
              </span>
            </button>
          </div>
        </div>
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

const getStarStyle = (n) => {
  const size = Math.random() * 3 + 1;
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${Math.random() * 2 + 2}s`
  };
};

const getFloatingCardStyle = (n) => {
  return {
    left: `${10 + (n - 1) * 15}%`,
    animationDelay: `${n * 0.5}s`,
    animationDuration: `${4 + n * 0.5}s`
  };
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
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
  flex-shrink: 0;
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.points-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.points-badge .points-icon {
  font-size: 16px;
  animation: sparkle 1.5s ease-in-out infinite;
}

.points-badge .points-value {
  font-size: 14px;
  font-weight: 600;
}

@keyframes sparkle {
  0%, 100% { transform: scale(1) rotate(0deg); }
  50% { transform: scale(1.15) rotate(10deg); }
}

.draw-area {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 16px;
  overflow: hidden;
  min-height: 0;
}

.probability-info {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 10;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.prob-title {
  display: block;
  color: rgba(255, 255, 255, 0.7);
  font-size: 10px;
  margin-bottom: 6px;
  font-weight: 500;
}

.prob-items {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  max-width: 180px;
}

.prob-item {
  font-size: 9px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 600;
}

.prob-item.legendary { background: rgba(255, 215, 0, 0.2); color: #ffd700; }
.prob-item.epic { background: rgba(168, 85, 247, 0.2); color: #a855f7; }
.prob-item.rare { background: rgba(59, 130, 246, 0.2); color: #3b82f6; }
.prob-item.uncommon { background: rgba(34, 197, 94, 0.2); color: #22c55e; }
.prob-item.common { background: rgba(156, 163, 175, 0.2); color: #9ca3af; }

.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.star {
  position: absolute;
  background: white;
  border-radius: 50%;
  animation: twinkle 2s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.5); }
}

.floating-card {
  position: absolute;
  bottom: -50px;
  font-size: 20px;
  opacity: 0.12;
  animation: floatUp 8s ease-in-out infinite;
}

@keyframes floatUp {
  0% { transform: translateY(0) rotate(0deg); opacity: 0; }
  10% { opacity: 0.12; }
  90% { opacity: 0.12; }
  100% { transform: translateY(-400px) rotate(360deg); opacity: 0; }
}

.gacha-machine {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  flex: 1;
  min-height: 0;
}

.machine-top {
  position: relative;
  width: 100%;
  padding: 10px 0;
  flex-shrink: 0;
}

.machine-lights {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 6px;
}

.light {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ffd700;
  animation: blink 0.8s ease-in-out infinite;
  box-shadow: 0 0 8px #ffd700, 0 0 16px #ffd700;
}

.light:nth-child(2n) {
  animation-delay: 0.4s;
  background: #ff6b6b;
  box-shadow: 0 0 8px #ff6b6b, 0 0 16px #ff6b6b;
}

.light:nth-child(3n) {
  background: #4ecdc4;
  box-shadow: 0 0 8px #4ecdc4, 0 0 16px #4ecdc4;
}

@keyframes blink {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(0.8); }
}

.machine-title {
  text-align: center;
  font-size: 22px;
  font-weight: 900;
  letter-spacing: 6px;
  background: linear-gradient(135deg, #ffd700, #ff6b6b, #ffd700);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: shimmer 2s ease-in-out infinite;
}

@keyframes shimmer {
  0%, 100% { filter: brightness(1); }
  50% { filter: brightness(1.3); }
}

.machine-body {
  width: 100%;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.card-stage {
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 10px;
  min-height: 0;
}

.stage-glow {
  position: absolute;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: pulse 3s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 0.8; }
}

.stage-particles {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.particle {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(255, 215, 0, 0.8);
  border-radius: 50%;
  animation: particleFloat 4s ease-in-out infinite;
}

.particle:nth-child(1) { left: 10%; animation-delay: 0s; }
.particle:nth-child(2) { left: 20%; animation-delay: 0.3s; }
.particle:nth-child(3) { left: 30%; animation-delay: 0.6s; }
.particle:nth-child(4) { left: 40%; animation-delay: 0.9s; }
.particle:nth-child(5) { left: 50%; animation-delay: 1.2s; }
.particle:nth-child(6) { left: 60%; animation-delay: 1.5s; }
.particle:nth-child(7) { left: 70%; animation-delay: 1.8s; }
.particle:nth-child(8) { left: 80%; animation-delay: 2.1s; }
.particle:nth-child(9) { left: 15%; animation-delay: 2.4s; }
.particle:nth-child(10) { left: 25%; animation-delay: 2.7s; }
.particle:nth-child(11) { left: 75%; animation-delay: 3s; }
.particle:nth-child(12) { left: 85%; animation-delay: 3.3s; }

@keyframes particleFloat {
  0% { transform: translateY(80px) scale(0); opacity: 0; }
  50% { opacity: 1; }
  100% { transform: translateY(-80px) scale(1); opacity: 0; }
}

.card-preview {
  width: 140px;
  height: 196px;
  border-radius: 10px;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.6s ease;
  z-index: 2;
}

.card-preview.revealed {
  transform: rotateY(180deg);
}

.card-front, .card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 10px;
  backface-visibility: hidden;
  overflow: hidden;
}

.card-front {
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #4a5568;
  box-shadow: 0 0 20px rgba(102, 126, 234, 0.3);
}

.card-front-inner {
  width: 100px;
  height: 140px;
  border: 2px dashed #667eea;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: rgba(102, 126, 234, 0.1);
}

.mystery-pattern {
  position: absolute;
  width: 100%;
  height: 100%;
}

.pattern-line {
  position: absolute;
  background: linear-gradient(90deg, transparent, rgba(102, 126, 234, 0.3), transparent);
  height: 1px;
  width: 100%;
}

.pattern-line:nth-child(1) { top: 25%; }
.pattern-line:nth-child(2) { top: 50%; }
.pattern-line:nth-child(3) { top: 75%; }
.pattern-line:nth-child(4) { transform: rotate(90deg); }

.question-mark {
  font-size: 48px;
  color: #667eea;
  font-weight: 300;
  font-style: italic;
  text-shadow: 0 0 15px rgba(102, 126, 234, 0.8);
  animation: glow 2s ease-in-out infinite alternate;
}

@keyframes glow {
  from { text-shadow: 0 0 15px rgba(102, 126, 234, 0.8); }
  to { text-shadow: 0 0 30px rgba(102, 126, 234, 1), 0 0 45px rgba(102, 126, 234, 0.5); }
}

.card-back {
  transform: rotateY(180deg);
  background: #fff;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-back.legendary {
  box-shadow: 0 0 15px rgba(255, 215, 0, 0.8), 0 0 30px rgba(255, 215, 0, 0.5);
  animation: glow-legendary 2s ease-in-out infinite alternate;
}

.card-back.epic {
  box-shadow: 0 0 12px rgba(168, 85, 247, 0.8), 0 0 24px rgba(168, 85, 247, 0.5);
  animation: glow-epic 2s ease-in-out infinite alternate;
}

.card-back.rare {
  box-shadow: 0 0 10px rgba(59, 130, 246, 0.8), 0 0 20px rgba(59, 130, 246, 0.5);
  animation: glow-rare 2s ease-in-out infinite alternate;
}

.card-back.uncommon {
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.8), 0 0 16px rgba(34, 197, 94, 0.5);
  animation: glow-uncommon 2s ease-in-out infinite alternate;
}

.card-back.common {
  box-shadow: 0 0 6px rgba(156, 163, 175, 0.6);
}

@keyframes glow-legendary {
  from { box-shadow: 0 0 15px rgba(255, 215, 0, 0.8), 0 0 30px rgba(255, 215, 0, 0.5); }
  to { box-shadow: 0 0 25px rgba(255, 215, 0, 1), 0 0 50px rgba(255, 215, 0, 0.7); }
}

@keyframes glow-epic {
  from { box-shadow: 0 0 12px rgba(168, 85, 247, 0.8), 0 0 24px rgba(168, 85, 247, 0.5); }
  to { box-shadow: 0 0 20px rgba(168, 85, 247, 1), 0 0 40px rgba(168, 85, 247, 0.7); }
}

@keyframes glow-rare {
  from { box-shadow: 0 0 10px rgba(59, 130, 246, 0.8), 0 0 20px rgba(59, 130, 246, 0.5); }
  to { box-shadow: 0 0 16px rgba(59, 130, 246, 1), 0 0 32px rgba(59, 130, 246, 0.7); }
}

@keyframes glow-uncommon {
  from { box-shadow: 0 0 8px rgba(34, 197, 94, 0.8), 0 0 16px rgba(34, 197, 94, 0.5); }
  to { box-shadow: 0 0 12px rgba(34, 197, 94, 1), 0 0 24px rgba(34, 197, 94, 0.7); }
}

.card-image {
  flex: 1;
  width: 100%;
  object-fit: cover;
}

.card-info {
  padding: 8px;
  background: #fff;
  display: flex;
  flex-direction: column;
  gap: 2px;
  border-top: 1px solid #e2e8f0;
}

.card-name {
  font-size: 11px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
}

.card-type {
  font-size: 9px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
  text-align: center;
  letter-spacing: 0.5px;
}

.card-type.legendary { background: #fef3c7; color: #b45309; }
.card-type.epic { background: #ede9fe; color: #7c3aed; }
.card-type.rare { background: #dbeafe; color: #2563eb; }
.card-type.uncommon { background: #d1fae5; color: #059669; }
.card-type.common { background: #f1f5f9; color: #64748b; }

.cards-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 8px;
  width: 45vw;
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
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #4a5568;
}

.question-mark-small {
  font-size: 20px;
  color: #667eea;
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

.card-flip-back.legendary { box-shadow: 0 0 8px rgba(255, 215, 0, 0.8); animation: glow-legendary-small 2s ease-in-out infinite alternate; }
.card-flip-back.epic { box-shadow: 0 0 6px rgba(168, 85, 247, 0.8); animation: glow-epic-small 2s ease-in-out infinite alternate; }
.card-flip-back.rare { box-shadow: 0 0 5px rgba(59, 130, 246, 0.8); animation: glow-rare-small 2s ease-in-out infinite alternate; }
.card-flip-back.uncommon { box-shadow: 0 0 4px rgba(34, 197, 94, 0.8); animation: glow-uncommon-small 2s ease-in-out infinite alternate; }
.card-flip-back.common { box-shadow: 0 0 3px rgba(156, 163, 175, 0.6); }

@keyframes glow-legendary-small { from { box-shadow: 0 0 8px rgba(255, 215, 0, 0.8); } to { box-shadow: 0 0 12px rgba(255, 215, 0, 1); } }
@keyframes glow-epic-small { from { box-shadow: 0 0 6px rgba(168, 85, 247, 0.8); } to { box-shadow: 0 0 10px rgba(168, 85, 247, 1); } }
@keyframes glow-rare-small { from { box-shadow: 0 0 5px rgba(59, 130, 246, 0.8); } to { box-shadow: 0 0 8px rgba(59, 130, 246, 1); } }
@keyframes glow-uncommon-small { from { box-shadow: 0 0 4px rgba(34, 197, 94, 0.8); } to { box-shadow: 0 0 6px rgba(34, 197, 94, 1); } }

.card-item-image {
  flex: 1;
  width: 100%;
  object-fit: cover;
}

.card-item-info {
  padding: 4px 2px;
  background: #fff;
  border-top: 1px solid #f1f5f9;
}

.card-name-small {
  display: block;
  font-size: 8px;
  font-weight: 600;
  color: #1e293b;
  text-align: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-type-small {
  display: block;
  font-size: 7px;
  font-weight: 700;
  padding: 1px 3px;
  border-radius: 3px;
  text-align: center;
  margin-top: 1px;
}

.card-type-small.legendary { background: #fef3c7; color: #b45309; }
.card-type-small.epic { background: #ede9fe; color: #7c3aed; }
.card-type-small.rare { background: #dbeafe; color: #2563eb; }
.card-type-small.uncommon { background: #d1fae5; color: #059669; }
.card-type-small.common { background: #f1f5f9; color: #64748b; }

.machine-bottom {
  width: 100%;
  padding-top: 12px;
  flex-shrink: 0;
}

.draw-controls {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.draw-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  overflow: hidden;
}

.draw-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.draw-btn:hover:not(:disabled)::before {
  left: 100%;
}

.draw-btn.single {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.draw-btn.multi {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(240, 147, 251, 0.4);
}

.draw-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
}

.draw-btn.multi:hover:not(:disabled) {
  box-shadow: 0 6px 20px rgba(240, 147, 251, 0.5);
}

.draw-btn:active:not(:disabled) {
  transform: translateY(-1px);
}

.draw-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.btn-icon {
  font-size: 20px;
}

.btn-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 1px;
}

.btn-title {
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 1px;
}

.btn-cost {
  font-size: 11px;
  opacity: 0.8;
}

.btn-discount {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #ffd700;
  color: #1a1a2e;
  font-size: 9px;
  font-weight: 700;
  padding: 3px 6px;
  border-radius: 6px;
  transform: rotate(15deg);
  box-shadow: 0 2px 6px rgba(255, 215, 0, 0.5);
}

@media (max-width: 640px) {
  .draw-area {
    padding: 16px;
  }
  
  .cards-grid {
    grid-template-columns: repeat(5, 1fr);
    gap: 6px;
    padding: 0 10px;
  }
  
  .card-preview {
    width: 120px;
    height: 168px;
  }
  
  .question-mark {
    font-size: 36px;
  }
  
  .card-front-inner {
    width: 80px;
    height: 112px;
  }
  
  .draw-controls {
    flex-direction: column;
    width: 100%;
    max-width: 180px;
  }
  
  .draw-btn {
    width: 100%;
    justify-content: center;
  }
  
  .machine-title {
    font-size: 18px;
    letter-spacing: 4px;
  }
}
</style>
