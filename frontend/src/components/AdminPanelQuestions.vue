<template>
  <div class="admin-questions-container">
    <div class="header-container">
      <h2>题目管理</h2>
    </div>
    
    <div class="toolbar">
      <div class="search-box">
        <input type="text" v-model="searchText" placeholder="搜索题目内容或选项..." @keyup.enter="fetchQuestions">
        <button class="btn-search" @click="fetchQuestions">搜索</button>
      </div>
      <div class="filter-group">
        <button class="btn-add" @click="openAddModal">+ 添加题目</button>
        <button class="btn-batch-delete" @click="batchDeleteQuestions" :disabled="selectedQuestions.length === 0">
          批量删除 ({{ selectedQuestions.length }})
        </button>
      </div>
    </div>
    
    <div class="questions-table">
      <table>
        <thead>
          <tr>
            <th class="checkbox-col">
              <input type="checkbox" v-model="selectAll" @change="toggleSelectAll">
            </th>
            <th>ID</th>
            <th class="question-col">题目内容</th>
            <th>选项A</th>
            <th>选项B</th>
            <th>选项C</th>
            <th>选项D</th>
            <th>正确答案</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="question in questions" :key="question.id">
            <td class="checkbox-col">
              <input type="checkbox" :value="question.id" v-model="selectedQuestions">
            </td>
            <td>{{ question.id }}</td>
            <td class="question-col" :title="question.questionText">{{ truncateText(question.questionText, 30) }}</td>
            <td class="option-col" :title="question.optionA">{{ truncateText(question.optionA, 10) }}</td>
            <td class="option-col" :title="question.optionB">{{ truncateText(question.optionB, 10) }}</td>
            <td class="option-col" :title="question.optionC">{{ truncateText(question.optionC, 10) }}</td>
            <td class="option-col" :title="question.optionD">{{ truncateText(question.optionD, 10) }}</td>
            <td>
              <span class="answer-badge" :class="'answer-' + question.correctOption.toLowerCase()">
                {{ question.correctOption }}
              </span>
            </td>
            <td>{{ formatDate(question.createdAt) }}</td>
            <td class="actions">
              <button class="btn-edit" @click="openEditModal(question)">编辑</button>
              <button class="btn-delete" @click="deleteQuestion(question)">删除</button>
            </td>
          </tr>
          <tr v-if="questions.length === 0">
            <td colspan="10" class="empty-row">暂无数据</td>
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
      <div v-if="showModal" class="modal-overlay" @mousedown="handleOverlayMouseDown" @mouseup="handleOverlayMouseUp($event, closeModal)">
        <div class="modal modal-form" @mousedown.stop @mouseup.stop>
          <form @submit.prevent="saveQuestion">
            <div class="modal-header">
              <h3>{{ isEditing ? '编辑题目' : '添加题目' }}</h3>
              <div class="modal-header-actions">
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  {{ saving ? '保存中...' : '保存' }}
                </button>
              </div>
            </div>
            
            <div class="modal-body">
              <div class="form-column">
                <div class="form-group">
                  <label>题目内容 <span class="required">*</span></label>
                  <textarea v-model="questionForm.questionText" placeholder="请输入题目内容" rows="3" required></textarea>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>选项A <span class="required">*</span></label>
                    <input type="text" v-model="questionForm.optionA" placeholder="请输入选项A" required>
                  </div>
                  <div class="form-group">
                    <label>选项B <span class="required">*</span></label>
                    <input type="text" v-model="questionForm.optionB" placeholder="请输入选项B" required>
                  </div>
                </div>
                
                <div class="form-row">
                  <div class="form-group">
                    <label>选项C <span class="required">*</span></label>
                    <input type="text" v-model="questionForm.optionC" placeholder="请输入选项C" required>
                  </div>
                  <div class="form-group">
                    <label>选项D <span class="required">*</span></label>
                    <input type="text" v-model="questionForm.optionD" placeholder="请输入选项D" required>
                  </div>
                </div>
                
                <div class="form-group">
                  <label>正确答案 <span class="required">*</span></label>
                  <div class="answer-options">
                    <label class="answer-option" :class="{ selected: questionForm.correctOption === 'A' }">
                      <input type="radio" value="A" v-model="questionForm.correctOption">
                      <span class="answer-label">A</span>
                    </label>
                    <label class="answer-option" :class="{ selected: questionForm.correctOption === 'B' }">
                      <input type="radio" value="B" v-model="questionForm.correctOption">
                      <span class="answer-label">B</span>
                    </label>
                    <label class="answer-option" :class="{ selected: questionForm.correctOption === 'C' }">
                      <input type="radio" value="C" v-model="questionForm.correctOption">
                      <span class="answer-label">C</span>
                    </label>
                    <label class="answer-option" :class="{ selected: questionForm.correctOption === 'D' }">
                      <input type="radio" value="D" v-model="questionForm.correctOption">
                      <span class="answer-label">D</span>
                    </label>
                  </div>
                </div>
              </div>
              
              <div class="preview-column">
                <div class="preview-container">
                  <p class="preview-title">题目预览</p>
                  <div class="preview-content">
                    <div class="preview-question">
                      {{ questionForm.questionText || '题目内容将显示在这里' }}
                    </div>
                    <div class="preview-options">
                      <div class="preview-option" :class="{ correct: questionForm.correctOption === 'A' }">
                        <span class="option-letter">A</span>
                        <span class="option-text">{{ questionForm.optionA || '选项A' }}</span>
                      </div>
                      <div class="preview-option" :class="{ correct: questionForm.correctOption === 'B' }">
                        <span class="option-letter">B</span>
                        <span class="option-text">{{ questionForm.optionB || '选项B' }}</span>
                      </div>
                      <div class="preview-option" :class="{ correct: questionForm.correctOption === 'C' }">
                        <span class="option-letter">C</span>
                        <span class="option-text">{{ questionForm.optionC || '选项C' }}</span>
                      </div>
                      <div class="preview-option" :class="{ correct: questionForm.correctOption === 'D' }">
                        <span class="option-letter">D</span>
                        <span class="option-text">{{ questionForm.optionD || '选项D' }}</span>
                      </div>
                    </div>
                  </div>
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
import { ref, computed, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const API_BASE = 'http://localhost:8080/api/admin/questions';

const searchText = ref('');
const currentPage = ref(0);
const pageSize = ref(10);
const totalPages = ref(0);
const totalElements = ref(0);

const questions = ref([]);
const selectedQuestions = ref([]);
const selectAll = ref(false);

const showModal = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const editingQuestionId = ref(null);

const questionForm = ref({
  questionText: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  correctOption: 'A'
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

const truncateText = (text, maxLength) => {
  if (!text) return '';
  if (text.length > maxLength) {
    return text.substring(0, maxLength) + '...';
  }
  return text;
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

const fetchQuestions = async () => {
  try {
    const params = new URLSearchParams();
    params.append('page', currentPage.value);
    params.append('size', pageSize.value);
    if (searchText.value) params.append('keyword', searchText.value);

    const response = await fetch(`${API_BASE}?${params.toString()}`);
    const data = await response.json();
    
    questions.value = data.questions || [];
    currentPage.value = data.currentPage || 0;
    totalPages.value = data.totalPages || 0;
    totalElements.value = data.totalElements || 0;
    selectedQuestions.value = [];
    selectAll.value = false;
  } catch (error) {
    ElMessage.error('获取题目列表失败');
    console.error(error);
  }
};

const changePage = (page) => {
  currentPage.value = page;
  fetchQuestions();
};

const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedQuestions.value = questions.value.map(q => q.id);
  } else {
    selectedQuestions.value = [];
  }
};

watch(selectedQuestions, (newVal) => {
  selectAll.value = newVal.length === questions.value.length && questions.value.length > 0;
});

const openAddModal = () => {
  isEditing.value = false;
  editingQuestionId.value = null;
  questionForm.value = {
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctOption: 'A'
  };
  showModal.value = true;
};

const openEditModal = (question) => {
  isEditing.value = true;
  editingQuestionId.value = question.id;
  questionForm.value = {
    questionText: question.questionText,
    optionA: question.optionA,
    optionB: question.optionB,
    optionC: question.optionC,
    optionD: question.optionD,
    correctOption: question.correctOption
  };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  questionForm.value = {
    questionText: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctOption: 'A'
  };
  editingQuestionId.value = null;
};

const saveQuestion = async () => {
  if (!questionForm.value.questionText || !questionForm.value.optionA || 
      !questionForm.value.optionB || !questionForm.value.optionC || 
      !questionForm.value.optionD || !questionForm.value.correctOption) {
    ElMessage.warning('请填写所有必填项');
    return;
  }
  
  saving.value = true;
  try {
    const url = isEditing.value 
      ? `${API_BASE}/${editingQuestionId.value}`
      : API_BASE;
    const method = isEditing.value ? 'PUT' : 'POST';
    
    const response = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(questionForm.value)
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success(isEditing.value ? '更新成功' : '添加成功');
      closeModal();
      fetchQuestions();
    } else {
      ElMessage.error(data.message || '操作失败');
    }
  } catch (error) {
    console.error('保存题目失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  } finally {
    saving.value = false;
  }
};

const deleteQuestion = async (question) => {
  try {
    await ElMessageBox.confirm(`确定要删除该题目吗？此操作不可恢复！`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    });
    
    const response = await fetch(`${API_BASE}/${question.id}`, {
      method: 'DELETE'
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('删除成功');
      fetchQuestions();
    } else {
      ElMessage.error(data.message || '删除失败');
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const batchDeleteQuestions = async () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请先选择要删除的题目');
    return;
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedQuestions.value.length} 个题目吗？此操作不可恢复！`, '确认批量删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    });
    
    const response = await fetch(`${API_BASE}/batch`, {
      method: 'DELETE',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids: selectedQuestions.value })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('批量删除成功');
      fetchQuestions();
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
  fetchQuestions();
});
</script>

<style scoped>
.admin-questions-container {
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

.btn-add {
  padding: 10px 20px;
  background: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
}

.btn-add:hover {
  background: #059669;
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

.questions-table {
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
  table-layout: fixed;
}

th, td {
  padding: 10px 12px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

th {
  background: #f8fafc;
  font-weight: 600;
  color: #475569;
  font-size: 13px;
  position: sticky;
  top: 0;
  z-index: 1;
  white-space: nowrap;
}

td {
  font-size: 13px;
  color: #334155;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.question-col {
  max-width: 200px;
}

.option-col {
  max-width: 100px;
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

.answer-badge {
  display: inline-block;
  width: 28px;
  height: 28px;
  line-height: 28px;
  text-align: center;
  border-radius: 6px;
  font-weight: 600;
  font-size: 14px;
}

.answer-a { background: #fee2e2; color: #dc2626; }
.answer-b { background: #fef3c7; color: #d97706; }
.answer-c { background: #dbeafe; color: #2563eb; }
.answer-d { background: #d1fae5; color: #059669; }

.actions {
  display: flex;
  gap: 8px;
}

.btn-edit, .btn-delete {
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

.modal {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
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
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.modal-body {
  display: flex;
  gap: 24px;
  padding: 24px;
  overflow: hidden;
  flex: 1;
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
  width: 320px;
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
  flex: 1;
}

.preview-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #334155;
  text-align: center;
}

.preview-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.preview-question {
  background: white;
  padding: 16px;
  border-radius: 8px;
  font-size: 15px;
  color: #1a202c;
  line-height: 1.6;
  border: 1px solid #e2e8f0;
}

.preview-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preview-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: white;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  transition: all 0.2s ease;
}

.preview-option.correct {
  background: #d1fae5;
  border-color: #10b981;
}

.option-letter {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  border-radius: 4px;
  font-weight: 600;
  font-size: 12px;
  color: #64748b;
}

.preview-option.correct .option-letter {
  background: #10b981;
  color: white;
}

.option-text {
  flex: 1;
  font-size: 14px;
  color: #334155;
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 16px;
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
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group textarea {
  resize: vertical;
  min-height: 80px;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.answer-options {
  display: flex;
  gap: 12px;
}

.answer-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.answer-option input {
  display: none;
}

.answer-label {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
}

.answer-option:hover {
  border-color: #3b82f6;
}

.answer-option.selected {
  background: #dbeafe;
  border-color: #3b82f6;
}

.answer-option.selected .answer-label {
  color: #2563eb;
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
</style>
