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
      <button :class="{ active: activeTab === 'recycle' }" @click="switchTab('recycle')">♻️ 回收站</button>
    </div>

    <div class="content-area">
      <div v-if="activeTab === 'market'" class="market-tab">
        <div class="filter-row">
          <div class="search-bar">
            <input type="text" v-model="searchKeyword" placeholder="搜索卡片名称或系列..." @keyup.enter="searchConsignments">
            <button class="btn-search" @click="searchConsignments">搜索</button>
          </div>
          <select v-model="selectedSeries" @change="searchConsignments" class="filter-select">
            <option value="">全部系列</option>
            <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
          </select>
          <select v-model="selectedRarity" @change="searchConsignments" class="filter-select">
            <option value="">全部稀有度</option>
            <option value="1">普通</option>
            <option value="2">精良</option>
            <option value="3">稀有</option>
            <option value="4">史诗</option>
            <option value="5">传说</option>
          </select>
        </div>

        <div class="items-grid" v-if="consignments.length > 0">
          <div v-for="item in consignments" :key="item.id" class="item-card">
            <div class="card-image-wrapper" :class="getRarityClass(item.card?.rarityLevel)">
              <img :src="item.card?.imageUrl" :alt="item.card?.name" class="card-image">
            </div>
            <div class="item-info">
              <div class="info-header">
                <h4 class="card-name">{{ item.card?.name }}</h4>
                <span class="card-type" :class="getRarityClass(item.card?.rarityLevel)">{{ item.card?.type }}</span>
              </div>
              <div class="info-row">
                <span class="card-series">{{ item.card?.seriesName }}</span>
                <span class="quantity">库存 {{ item.quantity }}</span>
              </div>
              <div class="info-footer">
                <div class="seller-info">
                  <img :src="getAvatarUrl(item.seller?.avatar)" class="seller-avatar">
                  <span class="seller-name">{{ item.seller?.nickname }}</span>
                </div>
                <div class="price-action">
                  <span class="price">{{ item.unitPrice }} 积分</span>
                  <button class="btn-buy" @click="openBuyModal(item)" :disabled="item.quantity <= 0">购买</button>
                </div>
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
        <div class="filter-row">
          <div class="search-bar">
            <input type="text" v-model="myListingsSearchKeyword" placeholder="搜索卡片名称或系列..." @keyup.enter="searchMyListings">
            <button class="btn-search" @click="searchMyListings">搜索</button>
          </div>
          <select v-model="myListingsSelectedSeries" @change="searchMyListings" class="filter-select">
            <option value="">全部系列</option>
            <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
          </select>
          <select v-model="myListingsSelectedRarity" @change="searchMyListings" class="filter-select">
            <option value="">全部稀有度</option>
            <option value="1">普通</option>
            <option value="2">精良</option>
            <option value="3">稀有</option>
            <option value="4">史诗</option>
            <option value="5">传说</option>
          </select>
        </div>
        
        <div class="items-grid" v-if="myConsignments.length > 0">
          <div v-for="item in myConsignments" :key="item.id" class="item-card my-item">
            <div class="card-image-wrapper" :class="getRarityClass(item.card?.rarityLevel)">
              <img :src="item.card?.imageUrl" :alt="item.card?.name" class="card-image">
            </div>
            <div class="item-info">
              <div class="info-header">
                <h4 class="card-name">{{ item.card?.name }}</h4>
                <span class="card-type" :class="getRarityClass(item.card?.rarityLevel)">{{ item.card?.type }}</span>
              </div>
              <div class="info-row">
                <span class="card-series">{{ item.card?.seriesName }}</span>
                <span class="quantity">库存 {{ item.quantity }}</span>
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
        <div class="filter-row">
          <div class="search-bar">
            <input type="text" v-model="sellSearchKeyword" placeholder="搜索卡片名称或系列..." @keyup.enter="searchSellableCards">
            <button class="btn-search" @click="searchSellableCards">搜索</button>
          </div>
          <select v-model="sellSelectedSeries" @change="searchSellableCards" class="filter-select">
            <option value="">全部系列</option>
            <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
          </select>
          <select v-model="sellSelectedRarity" @change="searchSellableCards" class="filter-select">
            <option value="">全部稀有度</option>
            <option value="1">普通</option>
            <option value="2">精良</option>
            <option value="3">稀有</option>
            <option value="4">史诗</option>
            <option value="5">传说</option>
          </select>
        </div>
        
        <div class="items-grid" v-if="sellableCards.length > 0">
          <div v-for="card in sellableCards" :key="card.cardId" class="item-card sell-item">
            <div class="card-image-wrapper" :class="getRarityClass(card.rarityLevel)">
              <img :src="card.imageUrl" :alt="card.name" class="card-image">
            </div>
            <div class="item-info">
              <div class="info-header">
                <h4 class="card-name">{{ card.name }}</h4>
                <span class="card-type" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</span>
              </div>
              <div class="info-row">
                <span class="card-series">{{ card.seriesName }}</span>
                <span class="quantity">拥有 {{ card.quantity }}</span>
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

      <div v-if="activeTab === 'recycle'" class="recycle-tab">
        <div class="recycle-info">
          <h4>♻️ 卡片回收站</h4>
          <p class="recycle-desc">将不需要的卡片回收换取积分</p>
          <div class="recycle-rates">
            <div class="rate-item">
              <span class="rate-label">普通卡</span>
              <span class="rate-value">2 积分/张</span>
            </div>
            <div class="rate-item">
              <span class="rate-label">精良卡</span>
              <span class="rate-value">8 积分/张</span>
            </div>
            <div class="rate-item">
              <span class="rate-label">稀有卡</span>
              <span class="rate-value">14 积分/张</span>
            </div>
            <div class="rate-item">
              <span class="rate-label">史诗卡</span>
              <span class="rate-value">40 积分/张</span>
            </div>
            <div class="rate-item legendary">
              <span class="rate-label">传说卡</span>
              <span class="rate-value">200 积分/张</span>
            </div>
          </div>
        </div>
        
        <div class="filter-row">
          <div class="search-bar">
            <input type="text" v-model="recycleSearchKeyword" placeholder="搜索卡片名称或系列..." @keyup.enter="searchRecyclableCards">
            <button class="btn-search" @click="searchRecyclableCards">搜索</button>
          </div>
          <select v-model="recycleSelectedSeries" @change="searchRecyclableCards" class="filter-select">
            <option value="">全部系列</option>
            <option v-for="series in seriesList" :key="series" :value="series">{{ series }}</option>
          </select>
          <select v-model="recycleSelectedRarity" @change="searchRecyclableCards" class="filter-select">
            <option value="">全部稀有度</option>
            <option value="1">普通</option>
            <option value="2">精良</option>
            <option value="3">稀有</option>
            <option value="4">史诗</option>
            <option value="5">传说</option>
          </select>
        </div>
        
        <div class="items-grid" v-if="recyclableCards.length > 0">
          <div v-for="card in recyclableCards" :key="card.cardId" class="item-card recycle-item">
            <div class="card-image-wrapper" :class="getRarityClass(card.rarityLevel)">
              <img :src="card.imageUrl" :alt="card.name" class="card-image">
              <div class="quantity-badge">x{{ card.quantity }}</div>
            </div>
            <div class="item-info">
              <div class="info-header">
                <h4 class="card-name">{{ card.name }}</h4>
                <span class="card-type" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</span>
              </div>
              <div class="info-row">
                <span class="card-series">{{ card.seriesName }}</span>
              </div>
              <div class="recycle-info-row">
                <span class="recycle-price">{{ getRecyclePrice(card.rarityLevel) }} 积分/张</span>
              </div>
              <button class="btn-recycle" @click="openRecycleModal(card)">回收</button>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <span class="empty-icon">♻️</span>
          <p>您没有可回收的卡片</p>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showBuyModal" class="modal-overlay" 
        @mousedown="handleOverlayMouseDown" 
        @mouseup="handleOverlayMouseUp($event, closeBuyModal)">
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
      <div v-if="showSellModal" class="modal-overlay" 
        @mousedown="handleOverlayMouseDown" 
        @mouseup="handleOverlayMouseUp($event, closeSellModal)">
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

    <Teleport to="body">
      <div v-if="showRecycleModal" class="modal-overlay" 
        @mousedown="handleOverlayMouseDown" 
        @mouseup="handleOverlayMouseUp($event, closeRecycleModal)">
        <div class="modal-content">
          <div class="modal-header">
            <h3>回收卡片</h3>
            <button class="close-btn" @click="closeRecycleModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="recycle-card-preview" v-if="selectedRecycleCard">
              <img :src="selectedRecycleCard.imageUrl" :alt="selectedRecycleCard.name">
              <div class="recycle-card-info">
                <h4>{{ selectedRecycleCard.name }}</h4>
                <p>{{ selectedRecycleCard.seriesName }}</p>
                <p class="recycle-unit-price">回收价: {{ getRecyclePrice(selectedRecycleCard.rarityLevel) }} 积分/张</p>
                <p class="owned-quantity">拥有: {{ selectedRecycleCard.quantity }} 张</p>
              </div>
            </div>
            <div class="form-group">
              <label>回收数量</label>
              <div class="quantity-input">
                <button @click="recycleQuantity = Math.max(1, recycleQuantity - 1)">-</button>
                <input type="number" v-model.number="recycleQuantity" min="1" :max="selectedRecycleCard?.quantity">
                <button @click="recycleQuantity = Math.min(selectedRecycleCard?.quantity || 1, recycleQuantity + 1)">+</button>
              </div>
            </div>
            <div class="recycle-total">
              <span>总计可获得:</span>
              <span class="total-points">{{ getRecyclePrice(selectedRecycleCard?.rarityLevel) * recycleQuantity }} 积分</span>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="closeRecycleModal">取消</button>
            <button class="btn-confirm recycle-btn" @click="recycleCard">确认回收</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { ElMessage } from 'element-plus';

const API_BASE = '/api/consignments';
const refreshPoints = inject('refreshPoints', () => {});

const userPoints = ref(0);
const activeTab = ref('market');
const searchKeyword = ref('');
const selectedSeries = ref('');
const selectedRarity = ref('');
const seriesList = ref([]);

const myListingsSearchKeyword = ref('');
const myListingsSelectedSeries = ref('');
const myListingsSelectedRarity = ref('');

const sellSearchKeyword = ref('');
const sellSelectedSeries = ref('');
const sellSelectedRarity = ref('');

const recycleSearchKeyword = ref('');
const recycleSelectedSeries = ref('');
const recycleSelectedRarity = ref('');

const consignments = ref([]);
const myConsignments = ref([]);
const sellableCards = ref([]);
const recyclableCards = ref([]);

const currentPage = ref(0);
const totalPages = ref(0);

const showBuyModal = ref(false);
const showSellModal = ref(false);
const showRecycleModal = ref(false);
const selectedItem = ref(null);
const selectedCard = ref(null);
const selectedRecycleCard = ref(null);
const buyQuantity = ref(1);
const sellQuantity = ref(1);
const recycleQuantity = ref(1);
const sellPrice = ref(10);
const mouseDownOnOverlay = ref(false);

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
  if (avatar === 'default-avatar.png') return '/default-avatar.svg';
  if (avatar.startsWith('http')) return avatar;
  return `/Usersimg/${avatar}`;
};

const switchTab = (tab) => {
  activeTab.value = tab;
  if (tab === 'market') {
    fetchConsignments();
  } else if (tab === 'my-listings') {
    fetchMyConsignments();
  } else if (tab === 'sell') {
    fetchSellableCards();
  } else if (tab === 'recycle') {
    fetchRecyclableCards();
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

const fetchConsignments = async () => {
  try {
    const params = new URLSearchParams();
    params.append('page', currentPage.value);
    params.append('size', 12);
    if (searchKeyword.value) params.append('keyword', searchKeyword.value);
    if (selectedSeries.value) params.append('series', selectedSeries.value);
    if (selectedRarity.value) params.append('rarity', selectedRarity.value);

    const response = await fetch(`${API_BASE}?${params.toString()}`);
    const data = await response.json();
    
    consignments.value = data.items || [];
    currentPage.value = data.currentPage || 0;
    totalPages.value = data.totalPages || 0;
    
    if (data.seriesList) {
      seriesList.value = data.seriesList;
    }
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
    const params = new URLSearchParams();
    if (myListingsSearchKeyword.value) params.append('keyword', myListingsSearchKeyword.value);
    if (myListingsSelectedSeries.value) params.append('series', myListingsSelectedSeries.value);
    if (myListingsSelectedRarity.value) params.append('rarity', myListingsSelectedRarity.value);

    const url = params.toString() 
      ? `${API_BASE}/my?${params.toString()}` 
      : `${API_BASE}/my`;
    
    const response = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await response.json();
    myConsignments.value = data.items || [];
    
    if (data.seriesList) {
      seriesList.value = data.seriesList;
    }
  } catch (error) {
    console.error('获取我的上架失败:', error);
  }
};

const fetchSellableCards = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const params = new URLSearchParams();
    if (sellSearchKeyword.value) params.append('keyword', sellSearchKeyword.value);
    if (sellSelectedSeries.value) params.append('series', sellSelectedSeries.value);
    if (sellSelectedRarity.value) params.append('rarity', sellSelectedRarity.value);

    const url = params.toString() 
      ? `${API_BASE}/sellable?${params.toString()}` 
      : `${API_BASE}/sellable`;
    
    const response = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await response.json();
    sellableCards.value = data.items || [];
    
    if (data.seriesList) {
      seriesList.value = data.seriesList;
    }
  } catch (error) {
    console.error('获取可上架卡片失败:', error);
  }
};

const searchMyListings = () => {
  fetchMyConsignments();
};

const searchSellableCards = () => {
  fetchSellableCards();
};

const getRecyclePrice = (rarityLevel) => {
  switch (rarityLevel) {
    case 5: return 200;
    case 4: return 40;
    case 3: return 14;
    case 2: return 8;
    default: return 2;
  }
};

const fetchRecyclableCards = async () => {
  const token = localStorage.getItem('token');
  if (!token) return;

  try {
    const params = new URLSearchParams();
    if (recycleSearchKeyword.value) params.append('keyword', recycleSearchKeyword.value);
    if (recycleSelectedSeries.value) params.append('series', recycleSelectedSeries.value);
    if (recycleSelectedRarity.value) params.append('rarity', recycleSelectedRarity.value);

    const url = params.toString() 
      ? `${API_BASE}/sellable?${params.toString()}` 
      : `${API_BASE}/sellable`;
    
    const response = await fetch(url, {
      headers: { Authorization: `Bearer ${token}` }
    });
    const data = await response.json();
    recyclableCards.value = data.items || [];
    
    if (data.seriesList) {
      seriesList.value = data.seriesList;
    }
  } catch (error) {
    console.error('获取可回收卡片失败:', error);
  }
};

const searchRecyclableCards = () => {
  fetchRecyclableCards();
};

const openRecycleModal = (card) => {
  selectedRecycleCard.value = card;
  recycleQuantity.value = 1;
  showRecycleModal.value = true;
};

const closeRecycleModal = () => {
  showRecycleModal.value = false;
  selectedRecycleCard.value = null;
};

const recycleCard = async () => {
  const token = localStorage.getItem('token');
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }

  try {
    const response = await fetch(`${API_BASE}/recycle`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        cardId: selectedRecycleCard.value.cardId,
        quantity: recycleQuantity.value
      })
    });

    const data = await response.json();

    if (response.ok && data.success) {
      ElMessage.success(`回收成功！获得 ${data.earnedPoints} 积分`);
      closeRecycleModal();
      fetchRecyclableCards();
      fetchUserPoints();
      refreshPoints();
    } else {
      ElMessage.error(data.message || '回收失败');
    }
  } catch (error) {
    console.error('回收失败:', error);
    ElMessage.error('网络错误');
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

const handleOverlayMouseDown = (e) => {
  mouseDownOnOverlay.value = e.target.classList.contains('modal-overlay');
};

const handleOverlayMouseUp = (e, closeModal) => {
  if (mouseDownOnOverlay.value && e.target.classList.contains('modal-overlay')) {
    closeModal();
  }
  mouseDownOnOverlay.value = false;
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

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.search-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-bar input {
  padding: 8px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  background: white;
  min-width: 180px;
  transition: all 0.2s ease;
}

.search-bar input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.btn-search {
  padding: 8px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-search:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.filter-select {
  padding: 8px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  color: #475569;
  background: white;
  cursor: pointer;
  min-width: 140px;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.item-card {
  background: white;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-image-wrapper {
  position: relative;
  width: 100%;
  padding-bottom: 133.33%;
  overflow: hidden;
  background: #f8fafc;
}

.card-image-wrapper.legendary { box-shadow: inset 0 0 20px rgba(255, 215, 0, 0.3); }
.card-image-wrapper.epic { box-shadow: inset 0 0 20px rgba(168, 85, 247, 0.3); }
.card-image-wrapper.rare { box-shadow: inset 0 0 20px rgba(59, 130, 246, 0.3); }
.card-image-wrapper.uncommon { box-shadow: inset 0 0 20px rgba(34, 197, 94, 0.3); }

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center center;
}

.item-info {
  padding: 10px;
}

.info-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}

.card-name {
  font-size: 13px;
  font-weight: 600;
  color: #1e293b;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.card-type {
  font-size: 9px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 3px;
  flex-shrink: 0;
}

.card-type.legendary { background: #fef3c7; color: #b45309; }
.card-type.epic { background: #ede9fe; color: #7c3aed; }
.card-type.rare { background: #dbeafe; color: #2563eb; }
.card-type.uncommon { background: #d1fae5; color: #059669; }
.card-type.common { background: #f1f5f9; color: #64748b; }

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.card-series {
  font-size: 11px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.quantity {
  font-size: 10px;
  color: #94a3b8;
  flex-shrink: 0;
}

.info-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.seller-info {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  min-width: 0;
}

.seller-avatar {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
}

.seller-name {
  font-size: 10px;
  color: #94a3b8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.price-action {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  font-size: 15px;
  font-weight: 700;
  color: #667eea;
}

.btn-buy {
  padding: 5px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 11px;
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
  padding: 5px 12px;
  background: #fee2e2;
  color: #dc2626;
  border: none;
  border-radius: 5px;
  font-size: 11px;
  cursor: pointer;
  flex-shrink: 0;
}

.btn-delist:hover {
  background: #dc2626;
  color: white;
}

.btn-sell {
  width: 100%;
  padding: 6px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 11px;
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

.recycle-tab {
  padding: 20px 0;
}

.recycle-info {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.recycle-info h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #059669;
}

.recycle-desc {
  margin: 0 0 16px 0;
  color: #6b7280;
  font-size: 14px;
}

.recycle-rates {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.rate-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #d1fae5;
}

.rate-item.legendary {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-color: #fbbf24;
}

.rate-label {
  font-size: 13px;
  color: #475569;
  font-weight: 500;
}

.rate-value {
  font-size: 14px;
  color: #059669;
  font-weight: 700;
}

.rate-item.legendary .rate-value {
  color: #b45309;
}

.recycle-item {
  position: relative;
}

.quantity-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 10px;
  backdrop-filter: blur(4px);
}

.recycle-info-row {
  margin-bottom: 8px;
  text-align: center;
}

.recycle-price {
  font-size: 12px;
  color: #059669;
  font-weight: 600;
}

.btn-recycle {
  width: 100%;
  padding: 8px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-recycle:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.recycle-card-preview {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.recycle-card-preview img {
  width: 80px;
  height: 106px;
  object-fit: cover;
  border-radius: 6px;
}

.recycle-card-info h4 {
  margin: 0 0 4px;
  font-size: 14px;
  color: #1e293b;
}

.recycle-card-info p {
  margin: 0 0 4px;
  font-size: 12px;
  color: #64748b;
}

.recycle-unit-price {
  font-weight: 600;
  color: #059669 !important;
}

.recycle-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f0fdf4;
  border-radius: 8px;
  margin-top: 16px;
}

.recycle-total span:first-child {
  font-size: 14px;
  color: #475569;
}

.total-points {
  font-size: 20px;
  font-weight: 700;
  color: #059669;
}

.recycle-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%) !important;
}

.recycle-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}
</style>
