<template>
  <div class="my-album-container">
    <h2>我的图鉴</h2>
    <p class="description">查看您收集的所有卡片</p>
    
    <div class="album-grid">
      <div v-for="card in cards" :key="card.id" class="card-item" :class="{ 'locked': !card.owned }">
        <div class="card-image">
          <span class="card-emoji">{{ card.emoji }}</span>
        </div>
        <div class="card-info">
          <h4>{{ card.name }}</h4>
          <p class="card-rarity" :class="card.rarity">{{ card.rarityText }}</p>
          <p v-if="card.owned" class="card-count">拥有: {{ card.count }}张</p>
          <p v-else class="card-locked">未解锁</p>
        </div>
      </div>
    </div>
    
    <div class="stats-panel">
      <div class="stat-item">
        <span class="stat-value">{{ totalCards }}</span>
        <span class="stat-label">总卡片数</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ ownedCards }}</span>
        <span class="stat-label">已收集</span>
      </div>
      <div class="stat-item">
        <span class="stat-value">{{ completionRate }}%</span>
        <span class="stat-label">完成度</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const cards = ref([
  { id: 1, name: '火焰龙', emoji: '🐉', rarity: 'legendary', rarityText: '传说', owned: true, count: 2 },
  { id: 2, name: '冰霜凤凰', emoji: '🦅', rarity: 'legendary', rarityText: '传说', owned: false, count: 0 },
  { id: 3, name: '雷霆狮', emoji: '🦁', rarity: 'epic', rarityText: '史诗', owned: true, count: 1 },
  { id: 4, name: '暗影狼', emoji: '🐺', rarity: 'epic', rarityText: '史诗', owned: true, count: 3 },
  { id: 5, name: '水晶鹿', emoji: '🦌', rarity: 'rare', rarityText: '稀有', owned: true, count: 5 },
  { id: 6, name: '风暴鹰', emoji: '🦅', rarity: 'rare', rarityText: '稀有', owned: false, count: 0 },
  { id: 7, name: '森林熊', emoji: '🐻', rarity: 'common', rarityText: '普通', owned: true, count: 10 },
  { id: 8, name: '海洋龟', emoji: '🐢', rarity: 'common', rarityText: '普通', owned: true, count: 8 },
]);

const totalCards = computed(() => cards.value.length);
const ownedCards = computed(() => cards.value.filter(c => c.owned).length);
const completionRate = computed(() => Math.round((ownedCards.value / totalCards.value) * 100));
</script>

<style scoped>
.my-album-container {
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

.album-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.card-item {
  background: linear-gradient(135deg, #ffffff 0%, #f7fafc 100%);
  border-radius: 12px;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border: 2px solid transparent;
}

.card-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.card-item.locked {
  opacity: 0.5;
  filter: grayscale(100%);
}

.card-item.locked:hover {
  transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-image {
  width: 80px;
  height: 80px;
  margin: 0 auto 12px;
  background: linear-gradient(135deg, #e8f4fd 0%, #dbeafe 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-emoji {
  font-size: 40px;
}

.card-info h4 {
  margin: 0 0 4px 0;
  color: #2d3748;
  font-size: 14px;
}

.card-rarity {
  font-size: 12px;
  font-weight: 600;
  margin: 0 0 4px 0;
}

.card-rarity.legendary {
  color: #f59e0b;
}

.card-rarity.epic {
  color: #8b5cf6;
}

.card-rarity.rare {
  color: #3b82f6;
}

.card-rarity.common {
  color: #6b7280;
}

.card-count {
  font-size: 12px;
  color: #10b981;
  margin: 0;
}

.card-locked {
  font-size: 12px;
  color: #ef4444;
  margin: 0;
}

.stats-panel {
  display: flex;
  gap: 24px;
  padding: 20px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
}
</style>
