<template>
  <div class="points-mall-container">
    <div class="header-container">
      <h2>积分商城</h2>
      <div class="points-info">
        <span class="points-icon">💎</span>
        <span class="points-value">{{ userPoints.toLocaleString() }} 积分</span>
      </div>
    </div>

    <div class="tabs">
      <button :class="{ active: activeTab === 'market' }" @click="switchTab('market')">商城</button>
      <button :class="{ active: activeTab === 'my-listings' }" @click="switchTab('my-listings')">我的上架</button>
      <button :class="{ active: activeTab === 'sell' }" @click="switchTab('sell')">上架卡片</button>
    </div>

    <div class="content-area">
      <div v-if="activeTab === 'market'" class="market-tab">
        <div class="search-bar">
          <input type="text" v-model="searchKeyword" placeholder="搜索卡片名称或系列..." @keyup.enter="searchConsignments">
          <button class="btn-search" @click="searchConsignments">搜索</button>
        </div>

        <div class="items-grid" v-if="consignments.length > 0">
          <div v-for="item in consignments" :key="item.id" class="item-card">
            <div class="card-image-wrapper" :class="getRarityClass(item.card?.rarityLevel)">
              <img :src="item.card?.imageUrl" :alt="item.card?.name" class="card-image">
            </div>
            <div class="item-info">
              <h4 class="card-name">{{ item.card?.name }}</h4>
              <p class="card-series">{{ item.card?.seriesName }}</p>
              <div class="card-meta">
                <span class="card-type" :class="getRarityClass(item.card?.rarityLevel)">{{ item.card?.type }}</span>
                <span class="quantity">库存: {{ item.quantity }}</span>
              </div>
              <div class="seller-info">
                <img :src="getAvatarUrl(item.seller?.avatar)" class="seller-avatar">
                <span class="seller-name">{{ item.seller?.nickname }}</span>
              </div>
              <div class="price-row">
                <span class="price">{{ item.unitPrice }} 积分</span>
                <button class="btn-buy" @click="openBuyModal(item)" :disabled="item.quantity <= 0">购买</button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <span class="empty-icon">📦</span>
          <p>暂无商品</p>
        </div>

        <div class="pagination" v-if="totalPages > 1">
          <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">上一页</button>
          <span class="page-info">{{ currentPage + 1 }} / {{ totalPages }}</span>
          <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">下一页</button>
        </div>
      </div>

      <div v-if="activeTab === 'my-listings'" class="my-listings-tab">
        <div class="items-grid" v-if="myConsignments.length > 0">
          <div v-for="item in myConsignments" :key="item.id" class="item-card my-item">
            <div class="card-image-wrapper" :class="getRarityClass(item.card?.rarityLevel)">
              <img :src="item.card?.imageUrl" :alt="item.card?.name" class="card-image">
            </div>
            <div class="item-info">
              <h4 class="card-name">{{ item.card?.name }}</h4>
              <p class="card-series">{{ item.card?.seriesName }}</p>
              <div class="card-meta">
                <span class="card-type" :class="getRarityClass(item.card?.rarityLevel)">{{ item.card?.type }}</span>
                <span class="quantity">库存: {{ item.quantity }}</span>
              </div>
              <div class="price-row">
                <span class="price">{{ item.unitPrice }} 积分</span>
                <button class="btn-delist" @click="delistCard(item.id)">下架</button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <span class="empty-icon">📭</span>
          <p>您还没有上架任何卡片</p>
        </div>
      </div>

      <div v-if="activeTab === 'sell'" class="sell-tab">
        <div class="items-grid" v-if="sellableCards.length > 0">
          <div v-for="card in sellableCards" :key="card.cardId" class="item-card sell-item">
            <div class="card-image-wrapper" :class="getRarityClass(card.rarityLevel)">
              <img :src="card.imageUrl" :alt="card.name" class="card-image">
            </div>
            <div class="item-info">
              <h4 class="card-name">{{ card.name }}</h4>
              <p class="card-series">{{ card.seriesName }}</p>
              <div class="card-meta">
                <span class="card-type" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</span>
                <span class="quantity">拥有: {{ card.quantity }}</span>
              </div>
              <button class="btn-sell" @click="openSellModal(card)">上架</button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <span class="empty-icon">🎴</span>
          <p>您没有可上架的卡片</p>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showBuyModal" class="modal-overlay" @click.self="closeBuyModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>购买卡片</h3>
            <button class="close-btn" @click="closeBuyModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="buy-card-preview" v-if="selectedItem">
              <img :src="selectedItem.card?.imageUrl" :alt="selectedItem.card?.name">
              <div class="buy-card-info">
                <h4>{{ selectedItem.card?.name }}</h4>
                <p>{{ selectedItem.card?.seriesName }}</p>
                <p class="unit-price">单价: {{ selectedItem.unitPrice }} 积分</p>
              </div>
            </div>
            <div class="form-group">
              <label>购买数量</label>
              <div class="quantity-input">
                <button @click="buyQuantity = Math.max(1, buyQuantity - 1)">-</button>
                <input type="number" v-model.number="buyQuantity" min="1" :max="selectedItem?.quantity">
                <button @click="buyQuantity = Math.min(selectedItem?.quantity || 1, buyQuantity + 1)">+</button>
              </div>
            </div>
            <div class="total-cost">
              总计: <span class="cost-value">{{ (selectedItem?.unitPrice || 0) * buyQuantity }}</span> 积分
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="closeBuyModal">取消</button>
            <button class="btn-confirm" @click="buyCard">确认购买</button>
          </div>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="showSellModal" class="modal-overlay" @click.self="closeSellModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>上架卡片</h3>
            <button class="close-btn" @click="closeSellModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="sell-card-preview" v-if="selectedCard">
              <img :src="selectedCard.imageUrl" :alt="selectedCard.name">
              <div class="sell-card-info">
                <h4>{{ selectedCard.name }}</h4>
                <p>{{ selectedCard.seriesName }}</p>
                <p class="owned-quantity">拥有: {{ selectedCard.quantity }} 张</p>
              </div>
            </div>
            <div class="form-group">
              <label>上架数量</label>
              <div class="quantity-input">
                <button @click="sellQuantity = Math.max(1, sellQuantity - 1)">-</button>
                <input type="number" v-model.number="sellQuantity" min="1" :max="selectedCard?.quantity">
                <button @click="sellQuantity = Math.min(selectedCard?.quantity || 1, sellQuantity + 1)">+</button>
              </div>
            </div>
            <div class="form-group">
              <label>单价 (积分)</label>
              <input type="number" v-model.number="sellPrice" min="1" class="price-input">
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="closeSellModal">取消</button>
            <button class="btn-confirm" @click="listCard">确认上架</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { ElMessage } from 'element-plus';

const API_BASE = 'http://localhost:8080/api/consignments';
const refreshPoints = inject('refreshPoints', () => {});

const userPoints = ref(0);
const activeTab = ref('market');
const searchKeyword = ref('');

const consignments = ref([]);
const myConsignments = ref([]);
const sellableCards = ref([]);

const currentPage = ref(0);
const totalPages = ref(0);

const showBuyModal = ref(false);
const showSellModal = ref(false);
const selectedItem = ref(null);
const selectedCard = ref(null);
const buyQuantity = ref(1);
const sellQuantity = ref(1);
const sellPrice = ref(10);

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

const getAvatarUrl = (avatar) => {
  if (!avatar) return '/default-avatar.svg';
  if (avatar.startsWith('http')) return avatar;
  return `http://localhost:8080/Usersimg/${avatar}`;
};

const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'market') {
    fetchConsignments();
  } else if (tab === 'my-listings') {
    fetchMyConsignments();
  } else if (tab === 'sell') {
    fetchSellableCards();
  }
};

const fetchUserPoints = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;
  
  try {
    const response = await fetch('http://localhost:8080/api/points/status', {
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

const fetchConsignments = async () => {
  try {
    const params = new URLSearchParams();
    params.append('page', currentPage.value);
    params.append('size', 12);
    if (searchKeyword.value) params.append('keyword', searchKeyword.value);

    const response = await fetch(`${API_BASE}?${params.toString()}`);
    const data = await response.json();
    
    consignments.value = data.items || [];
    currentPage.value = data.currentPage || 0;
    totalPages.value = data.totalPages || 0;
  } catch (error) {
    console.error('获取商品列表失败:', error);
  }
};

const searchConsignments = () => {
  currentPage.value = 0;
  fetchConsignments();
};

const changePage = (page) => {
  currentPage.value = page;
  fetchConsignments();
};

const fetchMyConsignments = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const response = await fetch(`${API_BASE}/my`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await response.json();
    myConsignments.value = data.items || [];
  } catch (error) {
    console.error('获取我的上架失败:', error);
  }
};

const fetchSellableCards = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const response = await fetch(`${API_BASE}/sellable`, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await response.json();
    sellableCards.value = data.items || [];
  } catch (error) {
    console.error('获取可上架卡片失败:', error);
  }
};

const openBuyModal = (item) => {
  selectedItem.value = item;
  buyQuantity.value = 1;
  showBuyModal.value = true;
};

const closeBuyModal = () => {
  showBuyModal.value = false;
  selectedItem.value = null;
};

const openSellModal = (card) => {
  selectedCard.value = card;
  sellQuantity.value = 1;
  sellPrice.value = 10;
  showSellModal.value = true;
};

const closeSellModal = () => {
  showSellModal.value = false;
  selectedCard.value = null;
};

const buyCard = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/buy/${selectedItem.value.id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ quantity: buyQuantity.value })
    });

    const data = await response.json();

    if (response.ok && data.success) {
      ElMessage.success('购买成功！');
      closeBuyModal();
      fetchConsignments();
      fetchUserPoints();
      refreshPoints();
    } else {
      ElMessage.error(data.message || '购买失败');
    }
  } catch (error) {
    console.error('购买失败:', error);
    ElMessage.error('网络错误');
  }
};

const listCard = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }

  if (sellPrice.value <= 0) {
    ElMessage.warning('请输入有效的价格');
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/list`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        cardId: selectedCard.value.cardId,
        unitPrice: sellPrice.value,
        quantity: sellQuantity.value
      })
    });

    const data = await response.json();

    if (response.ok && data.success) {
      ElMessage.success('上架成功！');
      closeSellModal();
      fetchSellableCards();
    } else {
      ElMessage.error(data.message || '上架失败');
    }
  } catch (error) {
    console.error('上架失败:', error);
    ElMessage.error('网络错误');
  }
};

const delistCard = async (consignmentId) => {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const response = await fetch(`${API_BASE}/delist/${consignmentId}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${token}` }
    });

    const data = await response.json();

    if (response.ok && data.success) {
      ElMessage.success('下架成功！');
      fetchMyConsignments();
      fetchSellableCards();
    } else {
      ElMessage.error(data.message || '下架失败');
    }
  } catch (error) {
    console.error('下架失败:', error);
    ElMessage.error('网络错误');
  }
};

onMounted(() => {
  fetchUserPoints();
  fetchConsignments();
});
</script>

<style scoped>
.points-mall-container {
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
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.points-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
}

.points-icon {
  font-size: 18px;
}

.points-value {
  font-size: 14px;
  font-weight: 600;
}

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tabs button:hover {
  background: #e2e8f0;
}

.tabs button.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.content-area {
  flex: 1;
  overflow: auto;
  min-height: 0;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-bar input {
  flex: 1;
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
}

.btn-search {
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.item-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.card-image-wrapper {
  position: relative;
  aspect-ratio: 3/4;
  overflow: hidden;
}

.card-image-wrapper.legendary { box-shadow: inset 0 0 20px rgba(255, 215, 0, 0.3); }
.card-image-wrapper.epic { box-shadow: inset 0 0 20px rgba(168, 85, 247, 0.3); }
.card-image-wrapper.rare { box-shadow: inset 0 0 20px rgba(59, 130, 246, 0.3); }
.card-image-wrapper.uncommon { box-shadow: inset 0 0 20px rgba(34, 197, 94, 0.3); }

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  padding: 12px;
}

.card-name {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-series {
  font-size: 12px;
  color: #64748b;
  margin: 0 0 8px;
}

.card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-type {
  font-size: 10px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.card-type.legendary { background: #fef3c7; color: #b45309; }
.card-type.epic { background: #ede9fe; color: #7c3aed; }
.card-type.rare { background: #dbeafe; color: #2563eb; }
.card-type.uncommon { background: #d1fae5; color: #059669; }
.card-type.common { background: #f1f5f9; color: #64748b; }

.quantity {
  font-size: 11px;
  color: #94a3b8;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.seller-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  object-fit: cover;
}

.seller-name {
  font-size: 11px;
  color: #64748b;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  font-size: 16px;
  font-weight: 700;
  color: #667eea;
}

.btn-buy {
  padding: 6px 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.btn-buy:hover:not(:disabled) {
  opacity: 0.9;
}

.btn-buy:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.btn-delist {
  padding: 6px 14px;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.btn-delist:hover {
  background: #dc2626;
  color: white;
}

.btn-sell {
  width: 100%;
  padding: 8px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
}

.btn-sell:hover {
  opacity: 0.9;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #94a3b8;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #64748b;
  font-size: 14px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 400px;
  max-width: 90vw;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #64748b;
}

.modal-body {
  padding: 20px;
}

.buy-card-preview, .sell-card-preview {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.buy-card-preview img, .sell-card-preview img {
  width: 80px;
  height: 106px;
  object-fit: cover;
  border-radius: 6px;
}

.buy-card-info h4, .sell-card-info h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #1e293b;
}

.buy-card-info p, .sell-card-info p {
  margin: 0 0 4px;
  font-size: 12px;
  color: #64748b;
}

.unit-price, .owned-quantity {
  font-weight: 600;
  color: #667eea !important;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #475569;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quantity-input input {
  width: 80px;
  text-align: center;
  padding: 8px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

.quantity-input button {
  width: 32px;
  height: 32px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  cursor: pointer;
  font-size: 16px;
}

.price-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
}

.total-cost {
  text-align: right;
  font-size: 14px;
  color: #475569;
}

.cost-value {
  font-size: 20px;
  font-weight: 700;
  color: #667eea;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel {
  padding: 10px 20px;
  background: #f1f5f9;
  color: #64748b;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-confirm {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
</style>
