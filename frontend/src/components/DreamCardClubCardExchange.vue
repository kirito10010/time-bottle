<template>
  <div class="card-exchange-container">
    <div class="header-container">
      <h2>卡牌交换</h2>
    </div>
    
    <div class="tabs">
      <button 
        @click="activeTab = 'gift'" 
        :class="{ active: activeTab === 'gift' }"
      >赠送卡牌</button>
      <button 
        @click="activeTab = 'exchange'"
        :class="{ active: activeTab === 'exchange' }"
      >卡牌交换</button>
      <button 
        @click="activeTab = 'records'"
        :class="{ active: activeTab === 'records' }"
      >记录</button>
    </div>
    
    <div v-if="activeTab === 'gift'" class="tab-content">
      <div class="section">
        <h3>赠送卡牌</h3>
        
        <div class="gift-layout">
          <div class="gift-side">
            <div class="side-header">
              <label>选择卡片</label>
              <div class="search-row">
                <input 
                  v-model="searchGiftCardKeyword" 
                  placeholder="输入卡片名称搜索..."
                >
                <button class="btn-search" @click="searchGiftCardKeyword = ''">清除</button>
              </div>
            </div>
            <div class="card-selector" v-if="filteredGiftCards.length > 0">
              <div 
                v-for="card in filteredGiftCards" 
                :key="card.userCardId" 
                class="card-item"
                :class="{ selected: selectedGiftCard && selectedGiftCard.userCardId === card.userCardId }"
                @click="selectGiftCard(card)"
              >
                <img :src="card.cardImageUrl" alt="卡片" class="card-image">
                <div class="card-info">
                  <span class="card-name">{{ card.cardName }}</span>
                  <span class="card-quantity">x{{ card.quantity }}</span>
                </div>
              </div>
            </div>
            <div v-else-if="myCards.length > 0 && filteredGiftCards.length === 0" class="empty-tip">没有找到匹配的卡片</div>
            <div v-else class="empty-tip">暂无可用卡片</div>
            
            <div class="quantity-section" v-if="selectedGiftCard">
              <label>赠送数量</label>
              <input type="number" v-model.number="giftQuantity" min="1" :max="selectedGiftCard?.quantity || 1">
            </div>
          </div>
          
          <div class="gift-divider">
            <span class="gift-icon">🎁</span>
          </div>
          
          <div class="gift-side">
            <div class="side-header">
              <label>选择接收用户</label>
              <div class="search-row">
                <input 
                  v-model="searchUserKeyword" 
                  placeholder="输入用户昵称搜索..." 
                  @keyup.enter="searchUsers"
                >
                <button class="btn-search" @click="searchUsers">搜索</button>
              </div>
            </div>
            
            <div class="user-selector" v-if="filteredUsers.length > 0">
              <div 
                v-for="user in filteredUsers" 
                :key="user.id" 
                class="user-item"
                :class="{ selected: selectedReceiver && selectedReceiver.id === user.id }"
                @click="selectReceiver(user)"
              >
                <img :src="getAvatarUrl(user.avatar)" alt="头像" class="user-avatar">
                <div class="user-info-text">
                  <span class="user-name">{{ user.nickname || user.username }}</span>
                  <span class="user-id">ID: {{ user.id }}</span>
                </div>
              </div>
            </div>
            <div v-else class="empty-tip">暂无用户</div>
            
            <div class="gift-preview" v-if="selectedGiftCard && selectedReceiver">
              <div class="preview-title">赠送预览</div>
              <div class="preview-content">
                <div class="preview-card">
                  <img :src="selectedGiftCard.cardImageUrl" alt="卡片" class="preview-card-image">
                  <div class="preview-card-info">
                    <span class="preview-card-name">{{ selectedGiftCard.cardName }}</span>
                    <span class="preview-card-quantity">x{{ giftQuantity }}</span>
                  </div>
                </div>
                <div class="preview-arrow">→</div>
                <div class="preview-user">
                  <img :src="getAvatarUrl(selectedReceiver.avatar)" alt="头像" class="preview-user-avatar">
                  <span class="preview-user-name">{{ selectedReceiver.nickname || selectedReceiver.username }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button class="btn btn-primary" @click="sendGift" :disabled="!selectedGiftCard || !selectedReceiver">
            确认赠送
          </button>
        </div>
      </div>
    </div>
    
    <div v-else-if="activeTab === 'exchange'" class="tab-content">
      <div class="section">
        <h3>卡牌交换</h3>
        
        <div class="exchange-layout">
          <div class="exchange-side">
            <div class="side-header">
              <label>选择你要送出的卡片</label>
              <div class="search-row">
                <input 
                  v-model="searchMyCardKeyword" 
                  placeholder="输入卡片名称搜索..."
                >
                <button class="btn-search" @click="searchMyCardKeyword = ''">清除</button>
              </div>
            </div>
            <div class="card-selector" v-if="filteredMyCards.length > 0">
              <div 
                v-for="card in filteredMyCards" 
                :key="card.userCardId" 
                class="card-item"
                :class="{ selected: selectedSendCard && selectedSendCard.userCardId === card.userCardId }"
                @click="selectSendCard(card)"
              >
                <img :src="card.cardImageUrl" alt="卡片" class="card-image">
                <div class="card-info">
                  <span class="card-name">{{ card.cardName }}</span>
                  <span class="card-quantity">x{{ card.quantity }}</span>
                </div>
              </div>
            </div>
            <div v-else-if="myCards.length > 0 && filteredMyCards.length === 0" class="empty-tip">没有找到匹配的卡片</div>
            <div v-else class="empty-tip">暂无可用卡片</div>
            
            <div class="quantity-section" v-if="selectedSendCard">
              <label>送出数量</label>
              <input type="number" v-model.number="sendQuantity" min="1" :max="selectedSendCard?.quantity || 1">
            </div>
          </div>
          
          <div class="exchange-divider">
            <span class="exchange-icon">⇄</span>
          </div>
          
          <div class="exchange-side">
            <div class="side-header">
              <label>选择你想要的卡片</label>
              <div class="search-row">
                <input 
                  v-model="searchCardKeyword" 
                  placeholder="输入卡片名称搜索..."
                  @keyup.enter="searchCards"
                >
                <button class="btn-search" @click="searchCards">搜索</button>
              </div>
            </div>
            
            <div class="card-selector" v-if="allCards.length > 0">
              <div 
                v-for="card in allCards" 
                :key="card.cardId" 
                class="card-item"
                :class="{ selected: selectedWantCard && selectedWantCard.cardId === card.cardId }"
                @click="selectWantCard(card)"
              >
                <img :src="card.cardImageUrl" alt="卡片" class="card-image">
                <div class="card-info">
                  <span class="card-name">{{ card.cardName }}</span>
                  <span class="card-id">ID: {{ card.cardId }}</span>
                </div>
              </div>
            </div>
            <div v-else-if="hasSearchedCards" class="empty-tip">没有找到匹配的卡片</div>
            <div v-else class="empty-tip">请搜索卡片</div>
            
            <div class="quantity-section" v-if="selectedWantCard">
              <label>收到数量</label>
              <input type="number" v-model.number="wantQuantity" min="1">
            </div>
            
            <div class="user-section" v-if="selectedWantCard">
              <button class="btn btn-secondary btn-full" @click="searchUsersWithCard">查找拥有该卡片的用户</button>
              
              <div class="user-list" v-if="availableUsers.length > 0">
                <label>选择交换对象</label>
                <div class="user-search-results">
                  <div 
                    v-for="user in availableUsers" 
                    :key="user.id" 
                    class="user-item"
                    :class="{ selected: selectedExchangeUser && selectedExchangeUser.id === user.id }"
                    @click="selectExchangeUser(user)"
                  >
                    <img :src="getAvatarUrl(user.avatar)" alt="头像" class="user-avatar">
                    <div class="user-info-text">
                      <span class="user-name">{{ user.nickname || user.username }}</span>
                      <span class="user-card-count">持有: x{{ user.quantity }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else-if="hasSearchedUsers" class="empty-tip">没有找到拥有该卡片的用户</div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button class="btn btn-primary" @click="sendExchangeRequest" :disabled="!selectedSendCard || !selectedWantCard || !selectedExchangeUser">
            发送交换申请
          </button>
        </div>
      </div>
    </div>
    
    <div v-else-if="activeTab === 'records'" class="tab-content">
      <div class="records-grid">
        <div class="section">
          <h3>收到的赠送</h3>
          <div v-if="receivedGifts.length === 0" class="empty-tip">暂无收到的赠送记录</div>
          <div v-else class="records-list">
            <div v-for="gift in receivedGifts" :key="gift.id" class="record-item">
              <img :src="gift.cardImageUrl" alt="卡片" class="record-card-image">
              <div class="record-details">
                <p class="record-title">{{ gift.cardName }} x{{ gift.quantity }}</p>
                <p class="record-meta">来自: {{ gift.senderName }} | {{ formatDate(gift.createdAt) }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <div class="section">
          <h3>待处理的交换申请</h3>
          <div v-if="pendingExchanges.length === 0" class="empty-tip">暂无待处理的交换申请</div>
          <div v-else class="records-list">
            <div v-for="exchange in pendingExchanges" :key="exchange.id" class="exchange-item">
              <div class="exchange-info">
                <div class="exchange-card">
                  <img :src="exchange.senderCardImageUrl" alt="卡片" class="exchange-card-image">
                  <p>{{ exchange.senderCardName }} x{{ exchange.senderCardQuantity }}</p>
                  <span class="card-label">对方提供</span>
                </div>
                <span class="exchange-arrow">⇄</span>
                <div class="exchange-card">
                  <img :src="exchange.receiverCardImageUrl" alt="卡片" class="exchange-card-image">
                  <p>{{ exchange.receiverCardName }} x{{ exchange.receiverCardQuantity }}</p>
                  <span class="card-label">你提供</span>
                </div>
              </div>
              <div class="exchange-meta">
                <span>来自: {{ exchange.senderName }}</span>
                <span>{{ formatDate(exchange.createdAt) }}</span>
              </div>
              <div class="exchange-actions">
                <button class="btn btn-success" @click="acceptExchange(exchange)">同意</button>
                <button class="btn btn-danger" @click="rejectExchange(exchange)">拒绝</button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="section">
          <h3>我发送的交换申请</h3>
          <div v-if="myExchangeRequests.length === 0" class="empty-tip">暂无发送的交换申请</div>
          <div v-else class="records-list">
            <div v-for="exchange in myExchangeRequests" :key="exchange.id" class="exchange-item">
              <div class="exchange-info">
                <div class="exchange-card">
                  <img :src="exchange.senderCardImageUrl" alt="卡片" class="exchange-card-image">
                  <p>{{ exchange.senderCardName }} x{{ exchange.senderCardQuantity }}</p>
                  <span class="card-label">你提供</span>
                </div>
                <span class="exchange-arrow">⇄</span>
                <div class="exchange-card">
                  <img :src="exchange.receiverCardImageUrl" alt="卡片" class="exchange-card-image">
                  <p>{{ exchange.receiverCardName }} x{{ exchange.receiverCardQuantity }}</p>
                  <span class="card-label">对方提供</span>
                </div>
              </div>
              <div class="exchange-meta">
                <span>发给: {{ exchange.receiverName }}</span>
                <span :class="getStatusClass(exchange.status)">{{ getStatusText(exchange.status) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

const API_BASE = '/api/card-exchange';

const activeTab = ref('gift');
const myCards = ref([]);
const selectedGiftCard = ref(null);
const selectedReceiver = ref(null);
const selectedSendCard = ref(null);
const selectedWantCard = ref(null);
const selectedExchangeUser = ref(null);
const searchUserKeyword = ref('');
const searchResults = ref([]);
const giftQuantity = ref(1);
const sendQuantity = ref(1);
const wantQuantity = ref(1);
const receivedGifts = ref([]);
const pendingExchanges = ref([]);
const searchMyCardKeyword = ref('');
const searchGiftCardKeyword = ref('');

const filteredMyCards = computed(() => {
  if (!searchMyCardKeyword.value.trim()) {
    return myCards.value;
  }
  const keyword = searchMyCardKeyword.value.toLowerCase().trim();
  return myCards.value.filter(card => 
    card.cardName.toLowerCase().includes(keyword)
  );
});

const filteredGiftCards = computed(() => {
  if (!searchGiftCardKeyword.value.trim()) {
    return myCards.value;
  }
  const keyword = searchGiftCardKeyword.value.toLowerCase().trim();
  return myCards.value.filter(card => 
    card.cardName.toLowerCase().includes(keyword)
  );
});

const filteredUsers = computed(() => {
  if (!searchUserKeyword.value.trim()) {
    return allUsers.value;
  }
  return searchResults.value;
});

const myExchangeRequests = ref([]);
const availableUsers = ref([]);
const hasSearchedUsers = ref(false);
const searchCardKeyword = ref('');
const allCards = ref([]);
const hasSearchedCards = ref(false);
const cardCarousel = ref(null);
const carouselPosition = ref(0);
const allUsers = ref([]);

const getToken = () => localStorage.getItem('token') || '';

const getAvatarUrl = (avatar) => {
  if (!avatar) return '/default-avatar.svg';
  if (avatar === 'default-avatar.png') return '/default-avatar.svg';
  if (avatar.startsWith('http')) return avatar;
  return `/Usersimg/${avatar}`;
};

const formatDate = (date) => {
  if (!date) return '-';
  const d = new Date(date);
  return d.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getStatusClass = (status) => {
  switch (status) {
    case 'pending': return 'status-pending';
    case 'completed': return 'status-completed';
    case 'rejected': return 'status-rejected';
    default: return '';
  }
};

const getStatusText = (status) => {
  switch (status) {
    case 'pending': return '待处理';
    case 'completed': return '已完成';
    case 'rejected': return '已拒绝';
    default: return status;
  }
};

const fetchMyCards = async () => {
  try {
    const response = await fetch(`${API_BASE}/my-cards`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    myCards.value = data.cards || [];
  } catch (error) {
    console.error('获取卡片失败:', error);
  }
};

const searchUsers = async () => {
  if (!searchUserKeyword.value.trim()) {
    searchResults.value = [];
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/search-users?keyword=${encodeURIComponent(searchUserKeyword.value)}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    searchResults.value = data.users || [];
  } catch (error) {
    console.error('搜索用户失败:', error);
  }
};

const fetchAllUsers = async () => {
  try {
    const response = await fetch(`${API_BASE}/all-users`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    allUsers.value = data.users || [];
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

const selectGiftCard = (card) => {
  selectedGiftCard.value = card;
  giftQuantity.value = 1;
};

const selectReceiver = (user) => {
  selectedReceiver.value = user;
};

const selectSendCard = (card) => {
  selectedSendCard.value = card;
  sendQuantity.value = 1;
};

const selectWantCard = (card) => {
  selectedWantCard.value = card;
  wantQuantity.value = 1;
  availableUsers.value = [];
  selectedExchangeUser.value = null;
  hasSearchedUsers.value = false;
};

const selectExchangeUser = (user) => {
  selectedExchangeUser.value = user;
};

const searchCards = async () => {
  try {
    const params = new URLSearchParams();
    if (searchCardKeyword.value.trim()) {
      params.append('keyword', searchCardKeyword.value.trim());
    }
    
    const response = await fetch(`${API_BASE}/search-cards?${params.toString()}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    allCards.value = data.cards || [];
    hasSearchedCards.value = true;
    carouselPosition.value = 0;
  } catch (error) {
    console.error('搜索卡片失败:', error);
    ElMessage.error('搜索卡片失败');
  }
};

const scrollCards = (direction) => {
  if (!cardCarousel.value) return;
  
  const scrollAmount = 240;
  if (direction === 'prev') {
    cardCarousel.value.scrollBy({ left: -scrollAmount, behavior: 'smooth' });
    carouselPosition.value = Math.max(0, carouselPosition.value - 1);
  } else {
    cardCarousel.value.scrollBy({ left: scrollAmount, behavior: 'smooth' });
    carouselPosition.value++;
  }
};

const searchUsersWithCard = async () => {
  if (!selectedWantCard.value) {
    ElMessage.warning('请先选择想要的卡片');
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/find-users-with-card?cardId=${selectedWantCard.value.cardId}&minQuantity=${wantQuantity.value || 1}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    availableUsers.value = data.users || [];
    hasSearchedUsers.value = true;
    selectedExchangeUser.value = null;
    
    if (availableUsers.value.length === 0) {
      ElMessage.info('没有找到拥有该卡片的用户');
    }
  } catch (error) {
    console.error('搜索用户失败:', error);
    ElMessage.error('搜索失败');
  }
};

const sendGift = async () => {
  if (!selectedGiftCard.value || !selectedReceiver.value) {
    ElMessage.warning('请选择卡片和接收者');
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/gift`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        receiverId: selectedReceiver.value.id,
        cardId: selectedGiftCard.value.cardId,
        quantity: giftQuantity.value
      })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('赠送成功！');
      selectedGiftCard.value = null;
      selectedReceiver.value = null;
      giftQuantity.value = 1;
      searchResults.value = [];
      fetchMyCards();
    } else {
      ElMessage.error(data.message || '赠送失败');
    }
  } catch (error) {
    console.error('赠送失败:', error);
    ElMessage.error('网络错误');
  }
};

const sendExchangeRequest = async () => {
  if (!selectedSendCard.value || !selectedWantCard.value || !selectedExchangeUser.value) {
    ElMessage.warning('请选择卡片和交换对象');
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/exchange`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        receiverId: selectedExchangeUser.value.id,
        senderCardId: selectedSendCard.value.cardId,
        senderCardQuantity: sendQuantity.value,
        receiverCardId: selectedWantCard.value.cardId,
        receiverCardQuantity: wantQuantity.value
      })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('交换申请已发送');
      selectedSendCard.value = null;
      selectedWantCard.value = null;
      selectedExchangeUser.value = null;
      sendQuantity.value = 1;
      wantQuantity.value = 1;
      availableUsers.value = [];
      hasSearchedUsers.value = false;
      fetchMyCards();
    } else {
      ElMessage.error(data.message || '发送失败');
    }
  } catch (error) {
    console.error('发送交换申请失败:', error);
    ElMessage.error('网络错误');
  }
};

const acceptExchange = async (exchange) => {
  try {
    const response = await fetch(`${API_BASE}/exchange/${exchange.id}/accept`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('交换成功');
      fetchPendingExchanges();
      fetchMyCards();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('接受交换失败:', error);
    ElMessage.error('网络错误');
  }
};

const rejectExchange = async (exchange) => {
  try {
    const response = await fetch(`${API_BASE}/exchange/${exchange.id}/reject`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('已拒绝交换');
      fetchPendingExchanges();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('拒绝交换失败:', error);
    ElMessage.error('网络错误');
  }
};

const fetchReceivedGifts = async () => {
  try {
    const response = await fetch(`${API_BASE}/gifts/received`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    receivedGifts.value = data.gifts || [];
  } catch (error) {
    console.error('获取赠送记录失败:', error);
  }
};

const fetchPendingExchanges = async () => {
  try {
    const response = await fetch(`${API_BASE}/exchanges/pending`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    pendingExchanges.value = data.exchanges || [];
  } catch (error) {
    console.error('获取交换申请失败:', error);
  }
};

const fetchMyExchangeRequests = async () => {
  try {
    const response = await fetch(`${API_BASE}/exchanges/my-requests`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    const data = await response.json();
    myExchangeRequests.value = data.exchanges || [];
  } catch (error) {
    console.error('获取我的交换申请失败:', error);
  }
};

onMounted(() => {
  fetchMyCards();
  fetchAllUsers();
  fetchReceivedGifts();
  fetchPendingExchanges();
  fetchMyExchangeRequests();
  searchCards();
});
</script>

<style scoped>
.card-exchange-container {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.header-container {
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

.tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 20px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #64748b;
  transition: all 0.2s;
}

.tabs button:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.tabs button.active {
  background: #3b82f6;
  border-color: #3b82f6;
  color: white;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.gift-layout {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.gift-side {
  flex: 1;
  min-width: 280px;
}

.gift-side .side-header {
  margin-bottom: 12px;
}

.gift-side .side-header label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.gift-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  flex-shrink: 0;
}

.gift-icon {
  font-size: 28px;
}

.user-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 280px;
  overflow-y: auto;
}

.user-selector .user-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.user-selector .user-item:hover {
  border-color: #3b82f6;
  transform: translateY(-2px);
}

.user-selector .user-item.selected {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.user-selector .user-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.user-selector .user-info-text {
  flex: 1;
}

.user-selector .user-name {
  display: block;
  font-size: 13px;
  color: #1a202c;
  font-weight: 500;
}

.user-selector .user-id {
  display: block;
  font-size: 10px;
  color: #94a3b8;
}

.gift-preview {
  margin-top: 16px;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.preview-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
  opacity: 0.9;
}

.preview-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.preview-card {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
}

.preview-card-image {
  width: 40px;
  height: 53px;
  object-fit: cover;
  border-radius: 4px;
}

.preview-card-info {
  flex: 1;
}

.preview-card-name {
  display: block;
  font-size: 13px;
  font-weight: 600;
}

.preview-card-quantity {
  display: block;
  font-size: 11px;
  opacity: 0.8;
  margin-top: 2px;
}

.preview-arrow {
  font-size: 20px;
  opacity: 0.8;
}

.preview-user {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  padding: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
}

.preview-user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.preview-user-name {
  font-size: 13px;
  font-weight: 600;
}

.exchange-layout {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.exchange-side {
  flex: 1;
  min-width: 280px;
}

.exchange-side .side-header {
  margin-bottom: 12px;
}

.exchange-side .side-header label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.exchange-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  flex-shrink: 0;
}

.exchange-icon {
  font-size: 28px;
  color: #3b82f6;
  font-weight: bold;
}

.quantity-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.quantity-section label {
  display: block;
  margin-bottom: 6px;
  font-size: 13px;
  color: #64748b;
}

.quantity-section input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 14px;
}

.user-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e2e8f0;
}

.user-section .btn-full {
  width: 100%;
  margin-bottom: 12px;
}

.user-list label {
  display: block;
  margin-bottom: 8px;
  font-size: 13px;
  color: #64748b;
}

.records-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.records-grid .section {
  margin-bottom: 0;
}

.records-grid .records-list {
  max-height: 400px;
}

.section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #1a202c;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
}

.form-group input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

.search-row {
  display: flex;
  gap: 8px;
  width: 100%;
  align-items: center;
}

.search-row input {
  flex: 0 1 180px;
  min-width: 120px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  font-size: 13px;
  transition: all 0.3s ease;
  background: #f8fafc;
}

.search-row input:focus {
  outline: none;
  border-color: #3b82f6;
  background: white;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.search-row input::placeholder {
  color: #94a3b8;
}

.btn-search {
  padding: 8px 16px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.btn-search:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.btn-search:active {
  transform: translateY(0);
}

.card-selector {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 10px;
  max-height: 320px;
  overflow-y: auto;
  padding: 4px;
}

.card-item {
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  padding: 6px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.card-item:hover {
  border-color: #3b82f6;
  transform: translateY(-2px);
}

.card-item.selected {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

.card-image {
  width: 100%;
  aspect-ratio: 3/4;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}

.card-info {
  margin-top: 6px;
  text-align: center;
}

.card-name {
  display: block;
  font-size: 11px;
  color: #1a202c;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

.card-quantity {
  display: block;
  font-size: 10px;
  color: #64748b;
  margin-top: 2px;
}

.user-search-results {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: white;
}

.user-item:hover {
  border-color: #3b82f6;
}

.user-item.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  font-size: 14px;
  color: #1a202c;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #2563eb;
}

.btn-success {
  background: #10b981;
  color: white;
}

.btn-danger {
  background: #ef4444;
  color: white;
}

.empty-tip {
  color: #94a3b8;
  font-size: 14px;
  text-align: center;
  padding: 20px;
}

.records-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 200px;
  overflow-y: auto;
}

.record-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8fafc;
  border-radius: 8px;
}

.record-card-image {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.record-details {
  flex: 1;
}

.record-title {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: #1a202c;
}

.record-meta {
  margin: 0;
  font-size: 12px;
  color: #64748b;
}

.exchange-item {
  padding: 16px;
  background: #f8fafc;
  border-radius: 8px;
}

.exchange-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 12px;
}

.exchange-card {
  text-align: center;
}

.exchange-card-image {
  width: 80px;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 8px;
}

.exchange-card p {
  margin: 0;
  font-size: 12px;
  color: #1a202c;
}

.card-label {
  display: block;
  font-size: 11px;
  color: #64748b;
  margin-top: 4px;
}

.exchange-arrow {
  font-size: 24px;
  color: #3b82f6;
}

.exchange-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #64748b;
  margin-bottom: 12px;
}

.exchange-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.status-pending {
  color: #f59e0b;
}

.status-completed {
  color: #10b981;
}

.status-rejected {
  color: #ef4444;
}

.hint-text {
  font-size: 12px;
  color: #94a3b8;
  margin: 4px 0 0 0;
}

.user-info-text {
  display: flex;
  flex-direction: column;
}

.user-card-count {
  font-size: 11px;
  color: #64748b;
}

.card-carousel-container {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
}

.card-carousel {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  scroll-behavior: smooth;
  padding: 8px 4px;
  flex: 1;
  scrollbar-width: none;
}

.card-carousel::-webkit-scrollbar {
  display: none;
}

.carousel-item {
  flex-shrink: 0;
}

.carousel-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #e2e8f0;
  background: white;
  border-radius: 50%;
  cursor: pointer;
  font-size: 18px;
  color: #64748b;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.carousel-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.carousel-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.card-id {
  display: block;
  font-size: 10px;
  color: #94a3b8;
  margin-top: 2px;
}

.btn-secondary {
  background: #64748b;
  color: white;
}

.btn-secondary:hover {
  background: #475569;
}
</style>
