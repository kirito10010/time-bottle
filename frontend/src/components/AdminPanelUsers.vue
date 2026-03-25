<template>
  <div class="admin-users-container">
    <div class="header-container">
      <h2>用户管理</h2>
    </div>
    
    <div class="toolbar">
      <div class="search-box">
        <input type="text" v-model="searchText" placeholder="搜索用户名、邮箱或昵称..." @keyup.enter="fetchUsers">
        <button class="btn-search" @click="fetchUsers">搜索</button>
      </div>
      <div class="filter-group">
        <select v-model="filterRole" @change="fetchUsers">
          <option value="">全部角色</option>
          <option value="0">普通用户</option>
          <option value="1">VIP用户</option>
          <option value="2">管理员</option>
        </select>
        <select v-model="filterStatus" @change="fetchUsers">
          <option value="">全部状态</option>
          <option value="1">正常</option>
          <option value="0">禁用</option>
        </select>
        <button class="btn-batch-delete" @click="batchDeleteUsers" :disabled="selectedUsers.length === 0">
          批量删除 ({{ selectedUsers.length }})
        </button>
      </div>
    </div>
    
    <div class="users-table">
      <table>
        <thead>
          <tr>
            <th class="checkbox-col">
              <input type="checkbox" v-model="selectAll" @change="toggleSelectAll">
            </th>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>昵称</th>
            <th>角色</th>
            <th>积分</th>
            <th>状态</th>
            <th>最后登录</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="checkbox-col">
              <input type="checkbox" :value="user.id" v-model="selectedUsers">
            </td>
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.nickname || '未命名' }}</td>
            <td>
              <span class="role-badge" :class="getRoleClass(user.role)">{{ getRoleText(user.role) }}</span>
            </td>
            <td>{{ user.points }}</td>
            <td>
              <span class="status-badge" :class="user.status === '1' ? 'active' : 'disabled'">
                {{ user.status === '1' ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ formatDate(user.lastLoginAt) }}</td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td class="actions">
              <button class="btn-edit" @click="openEditModal(user)">编辑</button>
              <button 
                class="btn-toggle" 
                :class="user.status === '1' ? 'btn-disable' : 'btn-enable'"
                @click="toggleUserStatus(user)"
              >
                {{ user.status === '1' ? '禁用' : '启用' }}
              </button>
              <button class="btn-delete" @click="deleteUser(user)">删除</button>
            </td>
          </tr>
          <tr v-if="users.length === 0">
            <td colspan="11" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination">
      <button :disabled="currentPage === 0" @click="changePage(currentPage - 1)">上一页</button>
      <div class="page-numbers">
        <button 
          v-for="page in displayedPages" 
          :key="page" 
          :class="{ active: page === currentPage }"
          @click="changePage(page)"
        >
          {{ page + 1 }}
        </button>
      </div>
      <button :disabled="currentPage >= totalPages - 1" @click="changePage(currentPage + 1)">下一页</button>
      <span class="page-info">共 {{ totalElements }} 条记录</span>
    </div>

    <Teleport to="body">
      <div v-if="showEditModal" class="modal-overlay" @mousedown="handleOverlayMouseDown" @mouseup="handleOverlayMouseUp($event, closeEditModal)">
        <div class="modal-content" @mousedown.stop @mouseup.stop>
          <div class="modal-header">
            <h3>编辑用户</h3>
            <button class="close-btn" @click="closeEditModal">&times;</button>
          </div>
          <form @submit.prevent="saveUser">
            <div class="modal-body">
              <div class="form-group">
                <label>用户名</label>
                <input type="text" :value="editingUser.username" disabled>
              </div>
              <div class="form-group">
                <label>邮箱</label>
                <input type="email" :value="editingUser.email" disabled>
              </div>
              <div class="form-group">
                <label>昵称</label>
                <input type="text" v-model="editForm.nickname">
              </div>
              <div class="form-group">
                <label>角色</label>
                <select v-model="editForm.role">
                  <option value="0">普通用户</option>
                  <option value="1">VIP用户</option>
                  <option value="2">管理员</option>
                </select>
              </div>
              <div class="form-group">
                <label>状态</label>
                <select v-model="editForm.status">
                  <option value="1">正常</option>
                  <option value="0">禁用</option>
                </select>
              </div>
              <div class="form-group">
                <label>积分</label>
                <input type="number" v-model.number="editForm.points" min="0">
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-cancel" @click="closeEditModal">取消</button>
              <button type="submit" class="btn-save">保存</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const API_BASE = 'http://localhost:8080/api/admin/users';

const searchText = ref('');
const filterRole = ref('');
const filterStatus = ref('');
const currentPage = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);
const totalElements = ref(0);

const users = ref([]);
const selectedUsers = ref([]);
const selectAll = ref(false);

const showEditModal = ref(false);
const editingUser = ref({});
const editForm = ref({
  nickname: '',
  role: '0',
  status: '1',
  points: 0
});

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

const displayedPages = computed(() => {
  const pages = [];
  const start = Math.max(0, currentPage.value - 2);
  const end = Math.min(totalPages.value - 1, currentPage.value + 2);
  for (let i = start; i <= end; i++) {
    pages.push(i);
  }
  return pages;
});

const fetchUsers = async () => {
  try {
    const params = new URLSearchParams();
    params.append('page', currentPage.value);
    params.append('size', pageSize.value);
    if (searchText.value) params.append('keyword', searchText.value);
    if (filterRole.value) params.append('role', filterRole.value);
    if (filterStatus.value) params.append('status', filterStatus.value);

    const response = await fetch(`${API_BASE}?${params.toString()}`);
    const data = await response.json();
    
    users.value = data.users || [];
    currentPage.value = data.currentPage || 0;
    totalPages.value = data.totalPages || 0;
    totalElements.value = data.totalElements || 0;
    selectedUsers.value = [];
    selectAll.value = false;
  } catch (error) {
    ElMessage.error('获取用户列表失败');
    console.error(error);
  }
};

const changePage = (page) => {
  currentPage.value = page;
  fetchUsers();
};

const getRoleText = (role) => {
  const roles = { '0': '普通用户', '1': 'VIP用户', '2': '管理员' };
  return roles[role] || '未知';
};

const getRoleClass = (role) => {
  const classes = { '0': 'normal', '1': 'vip', '2': 'admin' };
  return classes[role] || '';
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

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedUsers.value = users.value.map(u => u.id);
  } else {
    selectedUsers.value = [];
  }
};

watch(selectedUsers, (newVal) => {
  selectAll.value = newVal.length === users.value.length && users.value.length > 0;
});

const openEditModal = (user) => {
  editingUser.value = { ...user };
  editForm.value = {
    nickname: user.nickname || '',
    role: user.role,
    status: user.status,
    points: user.points
  };
  showEditModal.value = true;
};

const closeEditModal = () => {
  showEditModal.value = false;
  editingUser.value = {};
};

const saveUser = async () => {
  try {
    const response = await fetch(`${API_BASE}/${editingUser.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(editForm.value)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('保存成功');
      closeEditModal();
      fetchUsers();
    } else {
      ElMessage.error(data.message || '保存失败');
    }
  } catch (error) {
    ElMessage.error('保存失败');
    console.error(error);
  }
};

const toggleUserStatus = async (user) => {
  const action = user.status === '1' ? '禁用' : '启用';
  
  try {
    await ElMessageBox.confirm(`确定要${action}用户 ${user.username} 吗？`, '确认操作', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const newStatus = user.status === '1' ? '0' : '1';
    const response = await fetch(`${API_BASE}/${user.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ status: newStatus })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success(`已${action}用户 ${user.username}`);
      fetchUsers();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('操作失败');
    }
  }
};

const deleteUser = async (user) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户 ${user.username} 吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    });
    
    const response = await fetch(`${API_BASE}/${user.id}`, {
      method: 'DELETE'
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('删除成功');
      fetchUsers();
    } else {
      ElMessage.error(data.message || '删除失败');
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeleteUsers = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请先选择要删除的用户');
    return;
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedUsers.value.length} 个用户吗？此操作不可恢复！`, '确认批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    });
    
    const response = await fetch(`${API_BASE}/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedUsers.value })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('批量删除成功');
      fetchUsers();
    } else {
      ElMessage.error(data.message || '批量删除失败');
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
};

onMounted(() => {
  fetchUsers();
});
</script>

<style scoped>
.admin-users-container {
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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
}

.search-box {
  display: flex;
  gap: 8px;
}

.search-box input {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  width: 280px;
  font-size: 14px;
}

.search-box input:focus {
  outline: none;
  border-color: #3b82f6;
}

.btn-search {
  padding: 10px 20px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.btn-search:hover {
  background: #2563eb;
}

.filter-group {
  display: flex;
  gap: 12px;
}

.filter-group select {
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.btn-batch-delete {
  padding: 10px 20px;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.btn-batch-delete:hover:not(:disabled) {
  background: #dc2626;
}

.btn-batch-delete:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.users-table {
  background: white;
  border-radius: 12px;
  overflow: auto;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  flex: 1;
  min-height: 0;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

th {
  background: #f8fafc;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  position: sticky;
  top: 0;
  z-index: 1;
}

td {
  font-size: 14px;
  color: #334155;
}

.checkbox-col {
  width: 40px;
  text-align: center;
}

.checkbox-col input {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.empty-row {
  text-align: center;
  color: #94a3b8;
  padding: 40px;
}

.role-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.role-badge.admin {
  background: #fef3c7;
  color: #d97706;
}

.role-badge.vip {
  background: #ede9fe;
  color: #7c3aed;
}

.role-badge.normal {
  background: #e0f2fe;
  color: #0284c7;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.active {
  background: #d1fae5;
  color: #059669;
}

.status-badge.disabled {
  background: #fee2e2;
  color: #dc2626;
}

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-toggle, .btn-delete {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-edit {
  background: #3b82f6;
  color: white;
}

.btn-edit:hover {
  background: #2563eb;
}

.btn-disable {
  background: #fee2e2;
  color: #dc2626;
}

.btn-enable {
  background: #d1fae5;
  color: #059669;
}

.btn-delete {
  background: #fee2e2;
  color: #dc2626;
}

.btn-delete:hover {
  background: #dc2626;
  color: white;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 20px;
  flex-wrap: wrap;
}

.pagination button {
  padding: 8px 16px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination button:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-numbers button {
  min-width: 36px;
  padding: 8px 12px;
}

.page-numbers button.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
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
  width: 480px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: auto;
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
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #334155;
}

.modal-body {
  padding: 20px;
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

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
}

.form-group input:disabled {
  background: #f8fafc;
  color: #94a3b8;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid #e2e8f0;
}

.btn-cancel,
.btn-save {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
}

.btn-cancel {
  background: #f1f5f9;
  color: #64748b;
}

.btn-cancel:hover {
  background: #e2e8f0;
}

.btn-save {
  background: #3b82f6;
  color: white;
}

.btn-save:hover {
  background: #2563eb;
}
</style>
