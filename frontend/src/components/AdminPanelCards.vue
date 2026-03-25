<template>
  <div class="admin-cards-container">
    <div class="header-container">
      <h2>集卡管理</h2>
    </div>
    
    <div class="tabs">
      <button :class="{ active: activeTab === 'cards' }" @click="activeTab = 'cards'">卡片管理</button>
      <button :class="{ active: activeTab === 'packs' }" @click="activeTab = 'packs'">卡包管理</button>
    </div>
    
    <div v-if="activeTab === 'cards'" class="cards-section">
      <div class="section-header">
        <h3>卡片列表 (共 {{ cards.length }} 张)</h3>
        <button class="btn-add" @click="openAddModal">+ 添加卡片</button>
      </div>
      
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="cards.length === 0" class="empty-state">
        <p>暂无卡片，点击上方按钮添加</p>
      </div>
      
      <div v-else class="cards-grid">
        <div v-for="card in cards" :key="card.id" class="card-item">
          <div class="card-preview" :class="getRarityClass(card.type)">
            <img v-if="card.imageUrl" :src="card.imageUrl" :alt="card.name" class="card-image">
            <span v-else class="card-emoji">🎴</span>
          </div>
          <div class="card-details">
            <h4>{{ card.name }}</h4>
            <p class="card-series">{{ card.seriesName }}</p>
            <p class="card-rarity" :class="getRarityClass(card.rarityLevel)">{{ card.type }}</p>
            <p class="card-url">{{ truncateUrl(card.imageUrl) }}</p>
          </div>
          <div class="card-actions">
            <button class="btn-edit" @click="openEditModal(card)">编辑</button>
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
    
    <!-- 添加/编辑卡片弹窗 -->
    <Teleport to="body">
      <div v-if="showCardModal" class="modal-overlay" 
        @mousedown="handleOverlayMouseDown" 
        @mouseup="(e) => handleOverlayMouseUp(e, closeCardModal)">
        <div class="modal modal-form" @click.stop>
          <form @submit.prevent="saveCard">
            <div class="modal-header">
              <h3>{{ isEditing ? '编辑卡片' : '添加卡片' }}</h3>
              <div class="modal-header-actions">
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  {{ saving ? '保存中...' : '保存' }}
                </button>
              </div>
            </div>
            
            <div class="modal-body">
              <div class="form-column">
                <div class="form-row">
                  <div class="form-group">
                    <label>系列名称 <span class="required">*</span></label>
                    <input type="text" v-model="cardForm.seriesName" placeholder="请输入系列名称" required>
                  </div>
                  
                  <div class="form-group">
                    <label>卡片名称 <span class="required">*</span></label>
                    <input type="text" v-model="cardForm.name" placeholder="请输入卡片名称" required>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>卡片类型 <span class="required">*</span></label>
                    <input type="text" v-model="cardForm.type" placeholder="请输入卡片类型" required>
                  </div>
                  
                  <div class="form-group">
                    <label>稀有度级别 <span class="required">*</span></label>
                    <select v-model="cardForm.rarityLevel" required>
                      <option :value="1">1 - 普通 (40%)</option>
                      <option :value="2">2 - 稀有 (30%)</option>
                      <option :value="3">3 - 超稀有 (15%)</option>
                      <option :value="4">4 - 史诗 (10%)</option>
                      <option :value="5">5 - 传说 (5%)</option>
                    </select>
                  </div>
                </div>
                
                <div class="form-group">
                  <label>图片URL <span class="required">*</span></label>
                  <input type="url" v-model="cardForm.imageUrl" placeholder="请输入图片完整URL" required>
                </div>
              </div>
              
              <div class="preview-column">
                <div class="preview-container">
                  <p class="preview-title">图片预览</p>
                  <div class="preview-image-wrapper">
                    <img v-if="cardForm.imageUrl" :src="cardForm.imageUrl" alt="预览" @error="imageLoadError = true">
                    <div v-else class="preview-placeholder">
                      <span class="placeholder-icon">🖼️</span>
                      <span class="placeholder-text">输入URL后显示预览</span>
                    </div>
                  </div>
                  <p v-if="imageLoadError" class="error-text">图片加载失败，请检查URL</p>
                </div>
              </div>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const activeTab = ref('cards');
const showCardModal = ref(false);
const showAddPack = ref(false);
const isEditing = ref(false);
const loading = ref(false);
const saving = ref(false);
const imageLoadError = ref(false);

const cards = ref([]);
const editingCardId = ref(null);

const cardForm = ref({
  seriesName: '',
  name: '',
  type: '',
  rarityLevel: 3,
  imageUrl: ''
});

const packs = ref([
  { id: 1, name: '普通卡包', emoji: '📦', description: '包含3张随机普通卡片', price: 50 },
  { id: 2, name: '稀有卡包', emoji: '🎁', description: '包含1张稀有卡片', price: 100 },
  { id: 3, name: '史诗卡包', emoji: '💎', description: '包含1张史诗卡片', price: 200 },
  { id: 4, name: '传说卡包', emoji: '👑', description: '包含1张传说卡片', price: 500 },
]);

let overlayMouseDownTarget = null;

const handleOverlayMouseDown = (e) => {
  overlayMouseDownTarget = e.target;
};

const handleOverlayMouseUp = (e, closeCallback) => {
  if (overlayMouseDownTarget === e.target && e.target.classList.contains('modal-overlay')) {
    closeCallback();
  }
  overlayMouseDownTarget = null;
};

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

const truncateUrl = (url) => {
  if (!url) return '';
  if (url.length > 30) {
    return url.substring(0, 30) + '...';
  }
  return url;
};

const fetchCards = async () => {
  loading.value = true;
  try {
    const response = await fetch('http://localhost:8080/api/cards');
    if (response.ok) {
      cards.value = await response.json();
    } else {
      ElMessage.error('获取卡片列表失败');
    }
  } catch (error) {
    console.error('获取卡片失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const openAddModal = () => {
  isEditing.value = false;
  editingCardId.value = null;
  cardForm.value = { name: '', type: '', imageUrl: '' };
  imageLoadError.value = false;
  showCardModal.value = true;
};

const openEditModal = (card) => {
  isEditing.value = true;
  editingCardId.value = card.id;
  cardForm.value = {
    seriesName: card.seriesName || '',
    name: card.name,
    type: card.type,
    rarityLevel: card.rarityLevel || 3,
    imageUrl: card.imageUrl
  };
  imageLoadError.value = false;
  showCardModal.value = true;
};

const closeCardModal = () => {
  showCardModal.value = false;
  cardForm.value = { name: '', type: '', imageUrl: '' };
  editingCardId.value = null;
  imageLoadError.value = false;
};

const saveCard = async () => {
  if (!cardForm.value.seriesName || !cardForm.value.name || !cardForm.value.type || !cardForm.value.rarityLevel || !cardForm.value.imageUrl) {
    ElMessage.warning('请填写所有必填项');
    return;
  }
  
  saving.value = true;
  try {
    const url = isEditing.value 
      ? `http://localhost:8080/api/cards/${editingCardId.value}`
      : 'http://localhost:8080/api/cards';
    const method = isEditing.value ? 'PUT' : 'POST';
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(cardForm.value)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success(isEditing.value ? '更新成功' : '添加成功');
      closeCardModal();
      fetchCards();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存卡片失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    saving.value = false;
  }
};

const deleteCard = (card) => {
  ElMessageBox.confirm(`确定要删除卡片 "${card.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`http://localhost:8080/api/cards/${card.id}`, {
        method: 'DELETE'
      });
      if (response.ok) {
        ElMessage.success('删除成功');
        fetchCards();
      } else {
        const data = await response.json();
        ElMessage.error(data.message || '删除失败');
      }
    } catch (error) {
      ElMessage.error('网络错误，请稍后重试');
    }
  }).catch(() => {});
};

const editPack = (pack) => {
  ElMessage.info(`编辑卡包: ${pack.name}`);
};

const deletePack = (pack) => {
  ElMessageBox.confirm(`确定要删除卡包 "${pack.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    packs.value = packs.value.filter(p => p.id !== pack.id);
    ElMessage.success('删除成功');
  }).catch(() => {});
};

onMounted(() => {
  fetchCards();
});
</script>

<style scoped>
.admin-cards-container {
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

.loading, .empty-state {
  text-align: center;
  padding: 40px;
  color: #64748b;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
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
  width: 70px;
  height: 70px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  overflow: hidden;
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

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-emoji {
  font-size: 32px;
}

.card-details {
  flex: 1;
  min-width: 0;
}

.card-details h4 {
  margin: 0 0 4px 0;
  color: #1a202c;
  font-size: 14px;
}

.card-rarity {
  font-size: 12px;
  font-weight: 500;
  margin: 0 0 4px 0;
}

.card-rarity.legendary { color: #d97706; }
.card-rarity.epic { color: #7c3aed; }
.card-rarity.rare { color: #2563eb; }
.card-rarity.common { color: #6b7280; }

.card-url {
  font-size: 11px;
  color: #94a3b8;
  margin: 0;
  word-break: break-all;
}

.card-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.btn-edit, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
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

/* Modal Styles */
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

.modal {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  height: 80vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.modal-form {
  width: 80vw;
  height: 80vh;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
  background: #f8fafc;
}

.modal-header h3 {
  margin: 0;
  color: #1a202c;
  font-size: 18px;
  font-weight: 600;
}

.modal-header-actions {
  display: flex;
  gap: 8px;
}

.modal form {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.modal-body {
  display: flex;
  gap: 24px;
  padding: 24px;
  overflow: hidden;
}

.form-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  overflow-y: auto;
  padding-right: 8px;
}

.preview-column {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.preview-container {
  background: #f8fafc;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}

.preview-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  text-align: center;
}

.preview-image-wrapper {
  aspect-ratio: 358 / 494;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.preview-image-wrapper img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #94a3b8;
}

.placeholder-icon {
  font-size: 48px;
}

.placeholder-text {
  font-size: 13px;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-row .form-group.full-width {
  flex: none;
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #334155;
  font-size: 14px;
}

.required {
  color: #dc2626;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-tip {
  font-size: 12px;
  color: #64748b;
  margin-top: 6px;
}

.error-text {
  color: #dc2626;
  font-size: 12px;
  margin-top: 8px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-primary {
  background: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background: #2563eb;
}

.btn-primary:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f1f5f9;
  color: #64748b;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.image-preview p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #64748b;
}

.image-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 8px;
}

.error-text {
  color: #dc2626 !important;
  margin-top: 8px !important;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.btn-cancel, .btn-save {
  padding: 10px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel {
  background: #f1f5f9;
  color: #64748b;
}

.btn-save {
  background: #3b82f6;
  color: white;
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Packs Section */
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
