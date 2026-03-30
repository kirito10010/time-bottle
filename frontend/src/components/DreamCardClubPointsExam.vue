<template>
  <div class="points-exam-container">
    <div class="header-container">
      <h2>积分考试</h2>
    </div>
    
    <div v-if="!examStarted" class="exam-intro">
      <div class="intro-card">
        <div class="intro-icon">📝</div>
        <h3>知识问答</h3>
        <p>回答5道题目，答对3题及以上可获得积分奖励</p>
        <p class="intro-tip">💡 对3题得10积分，对4题得30积分，对5题得60积分</p>
        <button class="start-btn" @click="startExam">开始答题</button>
      </div>
    </div>
    
    <div v-else-if="!examFinished" class="exam-area">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: `${(currentIndex + 1) / questions.length * 100}%` }"></div>
      </div>
      <p class="question-counter">第 {{ currentIndex + 1 }} / {{ questions.length }} 题</p>
      
      <div class="question-card">
        <h4>{{ currentQuestion.questionText }}</h4>
        <div class="options">
          <button 
            class="option-btn"
            :class="{ 'selected': selectedAnswer === 'A' }"
            @click="selectAnswer('A')"
          >
            A. {{ currentQuestion.optionA }}
          </button>
          <button 
            class="option-btn"
            :class="{ 'selected': selectedAnswer === 'B' }"
            @click="selectAnswer('B')"
          >
            B. {{ currentQuestion.optionB }}
          </button>
          <button 
            class="option-btn"
            :class="{ 'selected': selectedAnswer === 'C' }"
            @click="selectAnswer('C')"
          >
            C. {{ currentQuestion.optionC }}
          </button>
          <button 
            class="option-btn"
            :class="{ 'selected': selectedAnswer === 'D' }"
            @click="selectAnswer('D')"
          >
            D. {{ currentQuestion.optionD }}
          </button>
        </div>
        
        <div v-if="selectedAnswer" class="answer-feedback">
          <p class="selected-text">已选择：{{ selectedAnswer }}</p>
          <button class="next-btn" @click="confirmAnswer">
            {{ currentIndex < questions.length - 1 ? '下一题' : '提交答案' }}
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="exam-result">
      <div class="result-card">
        <div class="result-summary">
          <div class="result-icon">{{ pointsEarned >= 30 ? '🎉' : pointsEarned >= 10 ? '👍' : '💪' }}</div>
          <h3>考试完成！</h3>
          <p class="result-score">获得 <span>{{ pointsEarned }}</span> 积分</p>
          <p class="result-detail">答对 {{ correctCount }} / {{ questions.length }} 题</p>
          <p class="result-tip" v-if="pointsEarned === 0">再接再厉，答对3题即可获得积分奖励！</p>
          <button class="retry-btn" @click="resetExam">再来一次</button>
        </div>
        
        <div class="result-details" v-if="examDetails.length > 0">
          <h4>答题详情</h4>
          <div class="detail-list">
            <div 
              v-for="(detail, index) in examDetails" 
              :key="index" 
              class="detail-item"
              :class="{ 'correct': detail.isCorrect, 'wrong': !detail.isCorrect }"
            >
              <div class="detail-header">
                <span class="detail-number">第{{ index + 1 }}题</span>
                <span class="detail-status">{{ detail.isCorrect ? '✓ 正确' : '✗ 错误' }}</span>
              </div>
              <p class="detail-question">{{ detail.questionText }}</p>
              <div class="detail-options">
                <div class="option-row" :class="{ 'user-choice': detail.userAnswer === 'A', 'correct-answer': detail.correctOption === 'A' }">
                  <span class="option-label">A.</span>
                  <span class="option-text">{{ detail.optionA }}</span>
                  <span v-if="detail.userAnswer === 'A'" class="tag user-tag">你的选择</span>
                  <span v-if="detail.correctOption === 'A'" class="tag correct-tag">正确答案</span>
                </div>
                <div class="option-row" :class="{ 'user-choice': detail.userAnswer === 'B', 'correct-answer': detail.correctOption === 'B' }">
                  <span class="option-label">B.</span>
                  <span class="option-text">{{ detail.optionB }}</span>
                  <span v-if="detail.userAnswer === 'B'" class="tag user-tag">你的选择</span>
                  <span v-if="detail.correctOption === 'B'" class="tag correct-tag">正确答案</span>
                </div>
                <div class="option-row" :class="{ 'user-choice': detail.userAnswer === 'C', 'correct-answer': detail.correctOption === 'C' }">
                  <span class="option-label">C.</span>
                  <span class="option-text">{{ detail.optionC }}</span>
                  <span v-if="detail.userAnswer === 'C'" class="tag user-tag">你的选择</span>
                  <span v-if="detail.correctOption === 'C'" class="tag correct-tag">正确答案</span>
                </div>
                <div class="option-row" :class="{ 'user-choice': detail.userAnswer === 'D', 'correct-answer': detail.correctOption === 'D' }">
                  <span class="option-label">D.</span>
                  <span class="option-text">{{ detail.optionD }}</span>
                  <span v-if="detail.userAnswer === 'D'" class="tag user-tag">你的选择</span>
                  <span v-if="detail.correctOption === 'D'" class="tag correct-tag">正确答案</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue';
import { ElMessage } from 'element-plus';

const API_BASE = '/api/exam';
const refreshPoints = inject('refreshPoints');

const examStarted = ref(false);
const examFinished = ref(false);
const currentIndex = ref(0);
const selectedAnswer = ref(null);
const correctCount = ref(0);
const pointsEarned = ref(0);

const questions = ref([]);
const userAnswers = ref([]);
const examDetails = ref([]);

const currentQuestion = computed(() => questions.value[currentIndex.value] || {});

const getToken = () => {
  return localStorage.getItem('token') || '';
};

const startExam = async () => {
  try {
    const response = await fetch(`${API_BASE}/questions`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    
    if (!response.ok) {
      const text = await response.text();
      console.error('开始考试失败:', response.status, text);
      if (response.status === 429) {
        ElMessage.error('今日考试次数已用完，请明天再来');
      } else {
        ElMessage.error('获取题目失败，请检查登录状态');
      }
      return;
    }
    
    const data = await response.json();
    questions.value = data.questions;
    examStarted.value = true;
    examFinished.value = false;
    currentIndex.value = 0;
    correctCount.value = 0;
    pointsEarned.value = 0;
    userAnswers.value = [];
    selectedAnswer.value = null;
  } catch (error) {
    console.error('开始考试失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

const selectAnswer = (option) => {
  selectedAnswer.value = option;
};

const confirmAnswer = () => {
  if (!selectedAnswer.value) {
    ElMessage.warning('请先选择一个答案');
    return;
  }
  
  userAnswers.value.push({
    questionId: currentQuestion.value.id,
    answer: selectedAnswer.value
  });
  
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++;
    selectedAnswer.value = null;
  } else {
    submitExam();
  }
};

const submitExam = async () => {
  try {
    const response = await fetch(`${API_BASE}/submit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        answers: userAnswers.value
      })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      if (data.correctCount === -1) {
        ElMessage.error('今日考试次数已用完，请明天再来');
        resetExam();
        return;
      }
      correctCount.value = data.correctCount;
      pointsEarned.value = data.pointsEarned;
      examDetails.value = data.details || [];
      examFinished.value = true;
      if (data.pointsEarned > 0) {
        ElMessage.success(data.message);
        if (refreshPoints) refreshPoints();
      }
    } else {
      ElMessage.error(data.message || '提交失败');
    }
  } catch (error) {
    console.error('提交考试失败:', error);
    ElMessage.error('网络错误，请稍后重试');
  }
};

const resetExam = () => {
  examStarted.value = false;
  examFinished.value = false;
  currentIndex.value = 0;
  selectedAnswer.value = null;
  correctCount.value = 0;
  pointsEarned.value = 0;
  questions.value = [];
  userAnswers.value = [];
};
</script>

<style scoped>
.points-exam-container {
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

.exam-intro {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.intro-card {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 16px;
  padding: 32px;
  text-align: center;
}

.intro-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.intro-card h3 {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 24px;
}

.intro-card p {
  color: #64748b;
  margin: 0 0 8px 0;
}

.intro-tip {
  font-size: 14px;
  color: #3b82f6 !important;
  margin-top: 16px !important;
}

.start-btn {
  margin-top: 24px;
  padding: 12px 48px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.start-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 24px rgba(37, 99, 235, 0.3);
}

.exam-area {
  max-width: 600px;
  margin: 0 auto;
}

.progress-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  transition: width 0.3s ease;
}

.question-counter {
  text-align: center;
  color: #64748b;
  margin-bottom: 16px;
}

.question-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.question-card h4 {
  margin: 0 0 20px 0;
  color: #1a202c;
  font-size: 18px;
  line-height: 1.6;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-btn {
  padding: 16px 20px;
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 15px;
  color: #4a5568;
}

.option-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  background: #eff6ff;
}

.option-btn.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.option-btn:disabled {
  cursor: default;
}

.answer-feedback {
  margin-top: 20px;
  text-align: center;
}

.selected-text {
  color: #3b82f6;
  font-weight: 600;
  font-size: 16px;
  margin: 0 0 16px 0;
}

.next-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.next-btn:hover {
  transform: scale(1.05);
}

.exam-result {
  display: flex;
  justify-content: center;
}

.result-card {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  max-width: 100%;
  width: 100%;
  display: flex;
  gap: 32px;
}

.result-summary {
  flex: 0 0 200px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-right: 2px solid #e2e8f0;
  padding-right: 32px;
}

.result-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.result-card h3 {
  margin: 0 0 12px 0;
  color: #1a202c;
  font-size: 20px;
}

.result-score {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 6px 0;
}

.result-score span {
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.result-detail {
  color: #94a3b8;
  margin: 0 0 6px 0;
  font-size: 14px;
}

.result-tip {
  color: #ef4444;
  font-size: 13px;
  margin: 0 0 20px 0;
}

.retry-btn {
  padding: 12px 32px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.retry-btn:hover {
  transform: scale(1.05);
}

.result-details {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.result-details h4 {
  font-size: 16px;
  color: #1a202c;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e2e8f0;
}

.detail-list {
  max-height: 500px;
  overflow-y: auto;
  padding-right: 8px;
}

.detail-list::-webkit-scrollbar {
  width: 6px;
}

.detail-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.detail-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.detail-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.detail-item {
  padding: 16px;
  margin-bottom: 12px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  background: #f8fafc;
}

.detail-item.correct {
  border-color: #10b981;
  background: #f0fdf4;
}

.detail-item.wrong {
  border-color: #ef4444;
  background: #fef2f2;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.detail-number {
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.detail-status {
  font-size: 13px;
  font-weight: 600;
}

.detail-item.correct .detail-status {
  color: #10b981;
}

.detail-item.wrong .detail-status {
  color: #ef4444;
}

.detail-question {
  font-size: 14px;
  color: #1a202c;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.detail-options {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.option-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  background: white;
  border: 1px solid #e2e8f0;
  font-size: 13px;
}

.option-row.user-choice {
  background: #fef2f2;
  border-color: #fca5a5;
}

.option-row.correct-answer {
  background: #f0fdf4;
  border-color: #86efac;
}

.option-label {
  font-weight: 600;
  color: #64748b;
  min-width: 20px;
}

.option-text {
  flex: 1;
  color: #1a202c;
}

.tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.user-tag {
  background: #fee2e2;
  color: #dc2626;
}

.correct-tag {
  background: #d1fae5;
  color: #059669;
}

</style>
