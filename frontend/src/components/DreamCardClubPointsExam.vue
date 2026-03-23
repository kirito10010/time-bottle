<template>
  <div class="points-exam-container">
    <h2>积分考试</h2>
    <p class="description">通过答题获取积分奖励</p>
    
    <div v-if="!examStarted" class="exam-intro">
      <div class="intro-card">
        <div class="intro-icon">📝</div>
        <h3>每日知识问答</h3>
        <p>回答5道题目，每题正确可获得2积分</p>
        <p class="intro-tip">💡 每天可参加一次，题目随机生成</p>
        <button class="start-btn" @click="startExam">开始答题</button>
      </div>
      
      <div class="stats-row">
        <div class="stat-card">
          <span class="stat-value">{{ todayScore }}</span>
          <span class="stat-label">今日得分</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ totalEarned }}</span>
          <span class="stat-label">累计获得积分</span>
        </div>
        <div class="stat-card">
          <span class="stat-value">{{ correctRate }}%</span>
          <span class="stat-label">正确率</span>
        </div>
      </div>
    </div>
    
    <div v-else-if="!examFinished" class="exam-area">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: `${(currentIndex + 1) / questions.length * 100}%` }"></div>
      </div>
      <p class="question-counter">第 {{ currentIndex + 1 }} / {{ questions.length }} 题</p>
      
      <div class="question-card">
        <h4>{{ currentQuestion.question }}</h4>
        <div class="options">
          <button 
            v-for="(option, index) in currentQuestion.options" 
            :key="index"
            class="option-btn"
            :class="{ 
              'selected': selectedAnswer === index,
              'correct': showAnswer && index === currentQuestion.correctIndex,
              'wrong': showAnswer && selectedAnswer === index && index !== currentQuestion.correctIndex
            }"
            :disabled="showAnswer"
            @click="selectAnswer(index)"
          >
            {{ String.fromCharCode(65 + index) }}. {{ option }}
          </button>
        </div>
        
        <div v-if="showAnswer" class="answer-feedback">
          <p :class="{ 'correct-text': isCorrect, 'wrong-text': !isCorrect }">
            {{ isCorrect ? '✓ 回答正确！+2积分' : '✗ 回答错误' }}
          </p>
          <button class="next-btn" @click="nextQuestion">
            {{ currentIndex < questions.length - 1 ? '下一题' : '查看结果' }}
          </button>
        </div>
      </div>
    </div>
    
    <div v-else class="exam-result">
      <div class="result-card">
        <div class="result-icon">{{ score >= 8 ? '🎉' : score >= 4 ? '👍' : '💪' }}</div>
        <h3>考试完成！</h3>
        <p class="result-score">获得 <span>{{ score }}</span> 积分</p>
        <p class="result-detail">答对 {{ correctCount }} / {{ questions.length }} 题</p>
        <button class="retry-btn" @click="resetExam">再来一次</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';

const examStarted = ref(false);
const examFinished = ref(false);
const currentIndex = ref(0);
const selectedAnswer = ref(null);
const showAnswer = ref(false);
const score = ref(0);
const correctCount = ref(0);
const todayScore = ref(0);
const totalEarned = ref(156);
const correctRate = ref(78);

const questions = ref([
  {
    question: 'Vue 3 中用于创建响应式数据的 API 是什么？',
    options: ['reactive', 'responsive', 'reflexive', 'reflective'],
    correctIndex: 0
  },
  {
    question: 'JavaScript 中，以下哪个方法用于数组遍历？',
    options: ['forEach', 'forLoop', 'iterate', 'traverse'],
    correctIndex: 0
  },
  {
    question: 'CSS 中，flex-direction: column 会使子元素如何排列？',
    options: ['水平排列', '垂直排列', '网格排列', '随机排列'],
    correctIndex: 1
  },
  {
    question: 'HTTP 状态码 404 表示什么？',
    options: ['服务器错误', '请求成功', '资源未找到', '权限不足'],
    correctIndex: 2
  },
  {
    question: 'Git 中，用于查看提交历史的命令是？',
    options: ['git status', 'git log', 'git diff', 'git show'],
    correctIndex: 1
  }
]);

const currentQuestion = computed(() => questions.value[currentIndex.value]);
const isCorrect = computed(() => selectedAnswer.value === currentQuestion.value.correctIndex);

const startExam = () => {
  examStarted.value = true;
  examFinished.value = false;
  currentIndex.value = 0;
  score.value = 0;
  correctCount.value = 0;
};

const selectAnswer = (index) => {
  if (showAnswer.value) return;
  selectedAnswer.value = index;
  showAnswer.value = true;
  
  if (isCorrect.value) {
    score.value += 2;
    correctCount.value++;
  }
};

const nextQuestion = () => {
  if (currentIndex.value < questions.value.length - 1) {
    currentIndex.value++;
    selectedAnswer.value = null;
    showAnswer.value = false;
  } else {
    examFinished.value = true;
    todayScore.value += score.value;
    totalEarned.value += score.value;
    ElMessage.success(`恭喜完成考试！获得 ${score.value} 积分`);
  }
};

const resetExam = () => {
  examStarted.value = false;
  examFinished.value = false;
  currentIndex.value = 0;
  selectedAnswer.value = null;
  showAnswer.value = false;
  score.value = 0;
  correctCount.value = 0;
};
</script>

<style scoped>
.points-exam-container {
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

.stats-row {
  display: flex;
  gap: 16px;
}

.stat-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-value {
  display: block;
  font-size: 28px;
  font-weight: 700;
  color: #2563eb;
}

.stat-label {
  font-size: 14px;
  color: #64748b;
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

.option-btn.correct {
  border-color: #10b981;
  background: #d1fae5;
  color: #065f46;
}

.option-btn.wrong {
  border-color: #ef4444;
  background: #fee2e2;
  color: #991b1b;
}

.option-btn:disabled {
  cursor: default;
}

.answer-feedback {
  margin-top: 20px;
  text-align: center;
}

.correct-text {
  color: #10b981;
  font-weight: 600;
  font-size: 16px;
  margin: 0 0 16px 0;
}

.wrong-text {
  color: #ef4444;
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
  padding: 40px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  max-width: 400px;
}

.result-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.result-card h3 {
  margin: 0 0 16px 0;
  color: #1a202c;
  font-size: 24px;
}

.result-score {
  font-size: 18px;
  color: #64748b;
  margin: 0 0 8px 0;
}

.result-score span {
  font-size: 32px;
  font-weight: 700;
  color: #2563eb;
}

.result-detail {
  color: #94a3b8;
  margin: 0 0 24px 0;
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
</style>
