<template>
  <div class="admin-cards-container">
    <h2>集卡管理</h2>
    <p class="description">管理卡片和卡包配置</p>
    
    <div class="tabs">
      <button :class="{ active: activeTab === 'cards' }" @click="activeTab = 'cards'">卡片管理</button>
      <button :class="{ active: activeTab === 'packs' }" @click="activeTab = 'packs'">卡包管理</button>
    </div>
    
    <div v-if="activeTab === 'cards'" class="cards-section">
      <div class="section-header">
        <h3>卡片列表</h3>
        <button class="btn-add" @click="showAddCard = true">+ 添加卡片</button>
      </div>
      
      <div class="cards-grid">
        <div v-for="card in cards" :key="card.id" class="card-item">
          <div class="card-preview" :class="card.rarity">
            <span class="card-emoji">{{ card.emoji }}</span>
          </div>
          <div class="card-details">
            <h4>{{ card.name }}</h4>
            <p class="card-rarity" :class="card.rarity">{{ card.rarityText }}</p>
            <p class="card-chance">抽取概率: {{ (card.chance * 100).toFixed(1) }}%</p>
          </div>
          <div class="card-actions">
            <button class="btn-edit" @click="editCard(card)">编辑</button>
            <button class="btn-delete" @click="deleteCard(card)">删除</button>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="packs-section">
      <div class="section-header">
        <h3>卡包列表</h3>
        <button class="btn-add" @click="showAddPack = true">+ 添加卡包</button>
      </div>
      
      <div class="packs-list">
        <div v-for="pack in packs" :key="pack.id" class="pack-item">
          <div class="pack-icon">{{ pack.emoji }}</div>
          <div class="pack-details">
            <h4>{{ pack.name }}</h4>
            <p>{{ pack.description }}</p>
            <p class="pack-price">价格: {{ pack.price }} 积分</p>
          </div>
          <div class="pack-actions">
            <button class="btn-edit" @click="editPack(pack)">编辑</button>
            <button class="btn-delete" @click="deletePack(pack)">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const activeTab = ref('cards');
const showAddCard = ref(false);
const showAddPack = ref(false);

const cards = ref([
  { id: 1, name: '火焰龙', emoji: '🐉', rarity: 'legendary', rarityText: '传说', chance: 0.01 },
  { id: 2, name: '冰霜凤凰', emoji: '🦅', rarity: 'legendary', rarityText: '传说', chance: 0.01 },
  { id: 3, name: '雷霆狮', emoji: '🦁', rarity: 'epic', rarityText: '史诗', chance: 0.05 },
  { id: 4, name: '暗影狼', emoji: '🐺', rarity: 'epic', rarityText: '史诗', chance: 0.05 },
  { id: 5, name: '水晶鹿', emoji: '🦌', rarity: 'rare', rarityText: '稀有', chance: 0.15 },
  { id: 6, name: '森林熊', emoji: '🐻', rarity: 'common', rarityText: '普通', chance: 0.29 },
]);

const packs = ref([
  { id: 1, name: '普通卡包', emoji: '📦', description: '包含3张随机普通卡片', price: 50 },
  { id: 2, name: '稀有卡包', emoji: '🎁', description: '包含1张稀有卡片', price: 100 },
  { id: 3, name: '史诗卡包', emoji: '💎', description: '包含1张史诗卡片', price: 200 },
  { id: 4, name: '传说卡包', emoji: '👑', description: '包含1张传说卡片', price: 500 },
]);

const editCard = (card) => {
  ElMessage.info(`编辑卡片: ${card.name}`);
};

const deleteCard = (card) => {
  ElMessageBox.confirm(`确定要删除卡片 ${card.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    cards.value = cards.value.filter(c => c.id !== card.id);
    ElMessage.success('删除成功');
  }).catch(() => {});
};

const editPack = (pack) => {
  ElMessage.info(`编辑卡包: ${pack.name}`);
};

const deletePack = (pack) => {
  ElMessageBox.confirm(`确定要删除卡包 ${pack.name} 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    packs.value = packs.value.filter(p => p.id !== pack.id);
    ElMessage.success('删除成功');
  }).catch(() => {});
};
</script>

<style scoped>
.admin-cards-container {
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

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
}

.tabs button {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tabs button.active {
  background: #3b82f6;
  color: white;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  color: #334155;
}

.btn-add {
  padding: 8px 16px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s ease;
}

.btn-add:hover {
  background: #059669;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.card-item {
  display: flex;
  align-items: center;
  gap: 12px;
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.card-preview {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.card-preview.legendary {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
}

.card-preview.epic {
  background: linear-gradient(135deg, #ede9fe 0%, #ddd6fe 100%);
}

.card-preview.rare {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
}

.card-preview.common {
  background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%);
}

.card-emoji {
  font-size: 28px;
}

.card-details {
  flex: 1;
}

.card-details h4 {
  margin: 0 0 4px 0;
  color: #1a202c;
  font-size: 14px;
}

.card-rarity {
  font-size: 12px;
  font-weight: 500;
  margin: 0 0 2px 0;
}

.card-rarity.legendary { color: #d97706; }
.card-rarity.epic { color: #7c3aed; }
.card-rarity.rare { color: #2563eb; }
.card-rarity.common { color: #6b7280; }

.card-chance {
  font-size: 11px;
  color: #94a3b8;
  margin: 0;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.btn-edit, .btn-delete {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 11px;
  cursor: pointer;
}

.btn-edit {
  background: #3b82f6;
  color: white;
}

.btn-delete {
  background: #fee2e2;
  color: #dc2626;
}

.packs-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pack-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.pack-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.pack-details {
  flex: 1;
}

.pack-details h4 {
  margin: 0 0 4px 0;
  color: #1a202c;
}

.pack-details p {
  margin: 0 0 4px 0;
  font-size: 13px;
  color: #64748b;
}

.pack-price {
  color: #2563eb !important;
  font-weight: 500;
}

.pack-actions {
  display: flex;
  gap: 8px;
}
</style>
