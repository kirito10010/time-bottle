<template>
  <div class="admin-users-container">
    <h2>用户管理</h2>
    <p class="description">管理系统用户账户</p>
    
    <div class="toolbar">
      <div class="search-box">
        <input type="text" v-model="searchText" placeholder="搜索用户名或邮箱..." @input="filterUsers">
      </div>
      <div class="filter-group">
        <select v-model="filterRole" @change="filterUsers">
          <option value="">全部角色</option>
          <option value="0">普通用户</option>
          <option value="1">VIP用户</option>
          <option value="2">管理员</option>
        </select>
        <select v-model="filterStatus" @change="filterUsers">
          <option value="">全部状态</option>
          <option value="1">正常</option>
          <option value="0">禁用</option>
        </select>
      </div>
    </div>
    
    <div class="users-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>邮箱</th>
            <th>昵称</th>
            <th>角色</th>
            <th>积分</th>
            <th>状态</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>{{ user.nickname }}</td>
            <td>
              <span class="role-badge" :class="user.roleClass">{{ user.roleText }}</span>
            </td>
            <td>{{ user.points }}</td>
            <td>
              <span class="status-badge" :class="user.status === '1' ? 'active' : 'disabled'">
                {{ user.status === '1' ? '正常' : '禁用' }}
              </span>
            </td>
            <td>{{ user.createdAt }}</td>
            <td class="actions">
              <button class="btn-edit" @click="editUser(user)">编辑</button>
              <button 
                class="btn-toggle" 
                :class="user.status === '1' ? 'btn-disable' : 'btn-enable'"
                @click="toggleUserStatus(user)"
              >
                {{ user.status === '1' ? '禁用' : '启用' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination">
      <button :disabled="currentPage === 1" @click="currentPage--">上一页</button>
      <span>第 {{ currentPage }} 页</span>
      <button @click="currentPage++">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchText = ref('');
const filterRole = ref('');
const filterStatus = ref('');
const currentPage = ref(1);

const users = ref([
  { id: 1, username: 'admin', email: 'admin@example.com', nickname: '管理员', role: '2', roleText: '管理员', roleClass: 'admin', points: 9999, status: '1', createdAt: '2024-01-01' },
  { id: 2, username: 'user1', email: 'user1@example.com', nickname: '小明', role: '0', roleText: '普通用户', roleClass: 'normal', points: 156, status: '1', createdAt: '2024-02-15' },
  { id: 3, username: 'vip1', email: 'vip1@example.com', nickname: 'VIP用户', role: '1', roleText: 'VIP用户', roleClass: 'vip', points: 520, status: '1', createdAt: '2024-03-01' },
  { id: 4, username: 'user2', email: 'user2@example.com', nickname: '小红', role: '0', roleText: '普通用户', roleClass: 'normal', points: 88, status: '0', createdAt: '2024-03-10' },
]);

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchSearch = !searchText.value || 
      user.username.includes(searchText.value) || 
      user.email.includes(searchText.value);
    const matchRole = !filterRole.value || user.role === filterRole.value;
    const matchStatus = !filterStatus.value || user.status === filterStatus.value;
    return matchSearch && matchRole && matchStatus;
  });
});

const filterUsers = () => {
  currentPage.value = 1;
};

const editUser = (user) => {
  ElMessage.info(`编辑用户: ${user.username}`);
};

const toggleUserStatus = (user) => {
  const action = user.status === '1' ? '禁用' : '启用';
  ElMessageBox.confirm(`确定要${action}用户 ${user.username} 吗？`, '确认操作', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    user.status = user.status === '1' ? '0' : '1';
    ElMessage.success(`已${action}用户 ${user.username}`);
  }).catch(() => {});
};
</script>

<style scoped>
.admin-users-container {
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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
  flex-wrap: wrap;
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

.users-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

th {
  background: #f8fafc;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
}

td {
  font-size: 14px;
  color: #334155;
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

.btn-edit, .btn-toggle {
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

.pagination span {
  color: #64748b;
}
</style>
