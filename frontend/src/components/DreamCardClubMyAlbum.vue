<template>
  <div class="album-container">
    <div class="header-container">
      <h2>我的图鉴</h2>
    </div>
    
    <div class="book-container">
      <div class="book-spine"></div>
      <div class="book-cover">
        <div class="book-inner">
          <div class="book-pages">
            <div 
              v-for="(page, pageIndex) in totalPages" 
              :key="pageIndex"
              class="book-page"
              :class="{ 'active': currentPage === pageIndex }"
            >
              <div class="page-content">
                <div class="photo-grid">
                  <div 
                    v-for="(card, index) in getPageCards(pageIndex)" 
                    :key="card.id"
                    class="photo-frame"
                    :class="[
                      card.owned ? 'owned' : '',
                      getRarityClass(card.rarityLevel)
                    ]"
                  >
                    <div class="frame-border">
                      <span class="frame-corner tl"></span>
                      <span class="frame-corner tr"></span>
                      <span class="frame-corner bl"></span>
                      <span class="frame-corner br"></span>
                    </div>
                    <div class="photo-wrapper">
                      <div class="photo-glow"></div>
                      <img 
                        :src="card.imageUrl" 
                        :alt="card.name" 
                        class="photo-image"
                      />
                      <div v-if="card.owned" class="quantity-badge">
                        <span class="badge-icon">✨</span>
                        x{{ card.quantity }}
                      </div>
                      <div v-if="!card.owned" class="lock-overlay">
                        <span class="lock-icon">?</span>
                      </div>
                    </div>
                    <div class="photo-caption">
                      <div class="caption-decoration"></div>
                      <span class="caption-series">{{ card.seriesName }}</span>
                      <span class="caption-name">{{ card.name }}</span>
                      <span class="caption-type" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="book-controls">
          <div class="page-nav">
            <button 
              class="nav-btn prev"
              :disabled="currentPage === 0"
              @click="prevPage"
            >
              ◀
            </button>
            <span class="page-indicator">
              {{ currentPage + 1 }} / {{ totalPages }}
            </span>
            <button 
              class="nav-btn next"
              :disabled="currentPage === totalPages - 1"
              @click="nextPage"
            >
              ▶
            </button>
          </div>
          <div class="stats-inline">
            <div class="stat-inline-item">
              <span class="stat-value">{{ ownedCards }}</span>
              <span class="stat-label">已收集</span>
            </div>
            <div class="stat-divider-v"></div>
            <div class="stat-inline-item">
              <span class="stat-value">{{ totalCards }}</span>
              <span class="stat-label">总卡片</span>
            </div>
            <div class="stat-divider-v"></div>
            <div class="stat-inline-item">
              <span class="stat-value">{{ completionRate }}%</span>
              <span class="stat-label">完成度</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';

const cards = ref([]);
const totalCards = ref(0);
const ownedCards = ref(0);
const completionRate = ref(0);
const currentPage = ref(0);
const cardsPerPage = 24;

const totalPages = computed(() => {
  return Math.ceil(cards.value.length / cardsPerPage) || 1;
});

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

const getPageCards = (pageIndex) => {
  const start = pageIndex * cardsPerPage;
  return cards.value.slice(start, start + cardsPerPage);
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
};

const nextPage = () => {
  if (currentPage.value < totalPages.value - 1) {
    currentPage.value++;
  }
};

const fetchAlbumData = async () => {
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('http://localhost:8080/api/cards/album', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    if (response.ok) {
      const data = await response.json();
      cards.value = data.cards || [];
      totalCards.value = data.totalCards || 0;
      ownedCards.value = data.ownedCards || 0;
      completionRate.value = data.completionRate || 0;
      currentPage.value = 0;
    }
  } catch (error) {
    console.error('获取图鉴数据失败:', error);
  }
};

onMounted(() => {
  fetchAlbumData();
});
</script>

<style scoped>
.album-container {
  padding: 20px;
  margin: 0 auto;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.description {
  color: #718096;
  margin: 0;
  font-size: 13px;
}

.book-container {
  display: flex;
  gap: 0;
  justify-content: center;
  align-items: stretch;
  width: 100%;
  flex: 1;
  min-height: 0;
  box-sizing: border-box;
}

.book-spine {
  width: 30px;
  flex-shrink: 0;
  background: linear-gradient(to right, #5c3317 0%, #8b4513 30%, #a0522d 50%, #8b4513 70%, #5c3317 100%);
  border-radius: 6px 0 0 6px;
  box-shadow: 
    -4px 0 8px rgba(0, 0, 0, 0.3),
    inset 2px 0 4px rgba(255, 255, 255, 0.1),
    inset -1px 0 3px rgba(0, 0, 0, 0.2);
  position: relative;
}

.book-spine::before {
  content: '图鉴';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotate(-90deg);
  font-size: 14px;
  font-weight: 700;
  color: #fef7ed;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  letter-spacing: 3px;
  white-space: nowrap;
}

.book-cover {
  flex: 1;
  min-width: 0;
  min-height: 0;
  max-height: 100%;
  background: linear-gradient(135deg, #4a2c0f 0%, #654321 25%, #8b4513 50%, #654321 75%, #4a2c0f 100%);
  border-radius: 0 10px 10px 0;
  padding: 16px;
  box-shadow: 
    4px 4px 16px rgba(0, 0, 0, 0.4),
    inset 0 0 40px rgba(0, 0, 0, 0.3);
  position: relative;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}

.book-cover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 85% 15%, rgba(255, 255, 255, 0.08) 0%, transparent 40%),
    radial-gradient(circle at 15% 85%, rgba(0, 0, 0, 0.2) 0%, transparent 40%);
  pointer-events: none;
  border-radius: 0 10px 10px 0;
}

.book-inner {
  position: relative;
  z-index: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
  flex: 1;
  min-height: 0;
}

.book-pages {
  flex: 1;
  min-width: 0;
  min-height: 0;
  overflow-y: auto;
  background: linear-gradient(135deg, #fffef9 0%, #f5f0e6 100%);
  border-radius: 6px;
  padding: 16px;
  box-shadow: 
    inset 0 0 20px rgba(139, 69, 19, 0.1),
    0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
  display: flex;
  flex-direction: column;
}

.book-pages::before {
  content: '';
  position: absolute;
  left: 0;
  top: 10%;
  bottom: 10%;
  width: 2px;
  background: linear-gradient(to bottom, transparent, #d4a574, transparent);
}

.book-page {
  display: none;
  width: 100%;
  box-sizing: border-box;
}

.book-page.active {
  display: flex;
  flex-direction: column;
  animation: fadeIn 0.4s ease;
  width: 100%;
}

.book-page .page-content {
  width: 100%;
  box-sizing: border-box;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateX(30px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateX(0) scale(1);
  }
}

.photo-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;
  justify-content: flex-start;
}

.photo-frame {
  background: linear-gradient(145deg, #ffffff 0%, #f8f4ec 100%);
  padding: 5px;
  box-shadow: 
    0 2px 8px rgba(0, 0, 0, 0.12),
    0 1px 3px rgba(0, 0, 0, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  position: relative;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 3px;
  width: 140px;
  flex-shrink: 0;
}

.photo-frame::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, transparent 50%),
    linear-gradient(315deg, rgba(0, 0, 0, 0.03) 0%, transparent 50%);
  pointer-events: none;
  z-index: 1;
  border-radius: 3px;
}

.photo-frame:hover {
  transform: translateY(-4px) rotate(-1deg) scale(1.02);
  box-shadow: 
    0 8px 20px rgba(0, 0, 0, 0.2),
    0 4px 10px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  z-index: 10;
}

.photo-frame.legendary {
  box-shadow: 
    0 0 15px rgba(255, 215, 0, 0.6),
    0 0 30px rgba(255, 215, 0, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: glow-album-legendary 2s ease-in-out infinite alternate;
}

.photo-frame.epic {
  box-shadow: 
    0 0 12px rgba(168, 85, 247, 0.6),
    0 0 24px rgba(168, 85, 247, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: glow-album-epic 2s ease-in-out infinite alternate;
}

.photo-frame.rare {
  box-shadow: 
    0 0 10px rgba(59, 130, 246, 0.6),
    0 0 20px rgba(59, 130, 246, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: glow-album-rare 2s ease-in-out infinite alternate;
}

.photo-frame.uncommon {
  box-shadow: 
    0 0 8px rgba(34, 197, 94, 0.6),
    0 0 16px rgba(34, 197, 94, 0.4),
    0 2px 8px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
  animation: glow-album-uncommon 2s ease-in-out infinite alternate;
}

.photo-frame.common {
  box-shadow: 
    0 0 6px rgba(156, 163, 175, 0.5),
    0 0 12px rgba(156, 163, 175, 0.3),
    0 2px 8px rgba(0, 0, 0, 0.12),
    inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

@keyframes glow-album-legendary {
  from {
    box-shadow: 
      0 0 15px rgba(255, 215, 0, 0.6),
      0 0 30px rgba(255, 215, 0, 0.4),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
  to {
    box-shadow: 
      0 0 25px rgba(255, 215, 0, 0.9),
      0 0 50px rgba(255, 215, 0, 0.6),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
}

@keyframes glow-album-epic {
  from {
    box-shadow: 
      0 0 12px rgba(168, 85, 247, 0.6),
      0 0 24px rgba(168, 85, 247, 0.4),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
  to {
    box-shadow: 
      0 0 20px rgba(168, 85, 247, 0.9),
      0 0 40px rgba(168, 85, 247, 0.6),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
}

@keyframes glow-album-rare {
  from {
    box-shadow: 
      0 0 10px rgba(59, 130, 246, 0.6),
      0 0 20px rgba(59, 130, 246, 0.4),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
  to {
    box-shadow: 
      0 0 16px rgba(59, 130, 246, 0.9),
      0 0 32px rgba(59, 130, 246, 0.6),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
}

@keyframes glow-album-uncommon {
  from {
    box-shadow: 
      0 0 8px rgba(34, 197, 94, 0.6),
      0 0 16px rgba(34, 197, 94, 0.4),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
  to {
    box-shadow: 
      0 0 12px rgba(34, 197, 94, 0.9),
      0 0 24px rgba(34, 197, 94, 0.6),
      0 2px 8px rgba(0, 0, 0, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.9);
  }
}

.frame-border {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 3;
}

.frame-corner {
  position: absolute;
  width: 14px;
  height: 14px;
  border: 2px solid;
  border-color: transparent;
}

.photo-frame.owned .frame-corner {
  border-color: #d4a574;
}

.frame-corner.tl {
  top: 3px;
  left: 3px;
  border-right: none;
  border-bottom: none;
}

.frame-corner.tr {
  top: 3px;
  right: 3px;
  border-left: none;
  border-bottom: none;
}

.frame-corner.bl {
  bottom: 3px;
  left: 3px;
  border-right: none;
  border-top: none;
}

.frame-corner.br {
  bottom: 3px;
  right: 3px;
  border-left: none;
  border-top: none;
}

.photo-wrapper {
  position: relative;
  aspect-ratio: 3/4;
  overflow: hidden;
  background: linear-gradient(135deg, #f0ebe3 0%, #e8e0d5 100%);
  border-radius: 2px;
}

.photo-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 30%, rgba(255, 255, 255, 0.3) 0%, transparent 60%);
  pointer-events: none;
  z-index: 1;
  opacity: 0.5;
}

.photo-frame.owned .photo-glow {
  opacity: 1;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.3s ease;
}

.photo-frame:not(.owned) .photo-image {
  filter: grayscale(60%) brightness(0.85);
}

.photo-frame.owned .photo-image {
  filter: none;
  transform: scale(1.02);
}

.quantity-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 50%, #d97706 100%);
  color: #451a03;
  font-size: 12px;
  font-weight: 800;
  padding: 4px 10px;
  border-radius: 16px;
  box-shadow: 
    0 3px 10px rgba(217, 119, 6, 0.5),
    0 1px 3px rgba(0, 0, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  z-index: 4;
  display: flex;
  align-items: center;
  gap: 4px;
  border: 2px solid #fef3c7;
}

.badge-icon {
  font-size: 10px;
}

.lock-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(80, 80, 80, 0.7) 0%, rgba(60, 60, 60, 0.8) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 4;
  backdrop-filter: blur(2px);
}

.lock-icon {
  font-size: 44px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 700;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.photo-caption {
  padding: 10px 6px 6px;
  text-align: center;
  background: linear-gradient(180deg, #ffffff 0%, #f8f4ec 100%);
  position: relative;
}

.caption-decoration {
  position: absolute;
  top: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 2px;
  background: linear-gradient(to right, transparent, #d4a574, transparent);
}

.caption-series {
  display: block;
  font-size: 9px;
  color: #9ca3af;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.caption-name {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.caption-name {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: #374151;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.caption-type {
  display: inline-block;
  font-size: 10px;
  font-weight: 800;
  padding: 3px 10px;
  border-radius: 10px;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.caption-type.legendary {
  background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%);
  color: #78350f;
  box-shadow: 0 2px 8px rgba(251, 191, 36, 0.4);
}

.caption-type.epic {
  background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
  color: #4c1d95;
  box-shadow: 0 2px 8px rgba(139, 92, 246, 0.4);
}

.caption-type.rare {
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: #1e3a8a;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
}

.caption-type.uncommon {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  color: #064e3b;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.4);
}

.caption-type.common {
  background: linear-gradient(135deg, #9ca3af 0%, #6b7280 100%);
  color: #1f2937;
  box-shadow: 0 2px 8px rgba(107, 114, 128, 0.3);
}

.book-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 2px solid rgba(139, 69, 19, 0.25);
  position: relative;
  z-index: 1;
  width: 100%;
  box-sizing: border-box;
  flex-shrink: 0;
}

.page-nav {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stats-inline {
  display: flex;
  align-items: center;
  gap: 16px;
  background: linear-gradient(135deg, #fef7ed 0%, #f5e6d3 100%);
  padding: 8px 20px;
  border-radius: 20px;
  border: 2px solid #d4a574;
}

.stat-inline-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stat-inline-item .stat-value {
  font-size: 18px;
  color: #92400e;
}

.stat-inline-item .stat-label {
  font-size: 11px;
  color: #a16207;
}

.stat-divider-v {
  width: 1px;
  height: 24px;
  background: #d4a574;
}

.nav-btn {
  background: linear-gradient(135deg, #8b4513 0%, #a0522d 50%, #8b4513 100%);
  color: #fef7ed;
  border: 3px solid #654321;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-weight: bold;
  box-shadow: 
    0 4px 12px rgba(0, 0, 0, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.nav-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #a0522d 0%, #cd853f 50%, #a0522d 100%);
  transform: scale(1.1);
  box-shadow: 
    0 6px 16px rgba(0, 0, 0, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.nav-btn:active:not(:disabled) {
  transform: scale(0.95);
}

.nav-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
  filter: grayscale(50%);
}

.page-indicator {
  color: #fef7ed;
  font-size: 15px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
  letter-spacing: 2px;
}

@media (max-width: 900px) {
  .book-controls {
    flex-direction: column;
    gap: 16px;
  }
  
  .page-nav {
    order: 2;
  }
  
  .stats-inline {
    order: 1;
  }
  
  .photo-frame {
    width: 120px;
  }
}

@media (max-width: 600px) {
  .photo-frame {
    width: 100px;
  }
  
  .stats-inline {
    padding: 6px 14px;
    gap: 10px;
  }
  
  .stat-inline-item .stat-value {
    font-size: 15px;
  }
  
  .stat-divider-v {
    height: 18px;
  }
}
</style>
