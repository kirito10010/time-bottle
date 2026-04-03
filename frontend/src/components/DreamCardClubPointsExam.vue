<template>
  <div class="points-exam-container">
    <div class="header-container">
      <h2>获取积分</h2>
    </div>
    
    <div class="cards-wrapper">
      <div class="intro-card exam-card" @click="openExamModal">
        <div class="intro-icon">📝</div>
        <h3>知识问答</h3>
        <p>回答5道题目，答对3题及以上可获得积分奖励</p>
        <p class="intro-tip">💡 对3题得10积分，对4题得30积分，对5题得60积分</p>
        <button class="start-btn" @click.stop="openExamModal">开始答题</button>
      </div>
      
      <div class="intro-card streak-card" @click="openStreakModal">
        <div class="intro-icon">🔥</div>
        <h3>连续答题挑战</h3>
        <p>随机刷题，连续答对直到答错结束</p>
        <p class="intro-tip">💡 每小时结算一次，排行榜前5名可获得积分奖励</p>
        <div class="reward-preview">
          <div class="reward-item"><span class="rank gold">🥇</span><span>+360</span></div>
          <div class="reward-item"><span class="rank silver">🥈</span><span>+270</span></div>
          <div class="reward-item"><span class="rank bronze">🥉</span><span>+180</span></div>
          <div class="reward-item"><span class="rank">4</span><span>+90</span></div>
          <div class="reward-item"><span class="rank">5</span><span>+1</span></div>
        </div>
        <div class="my-streak-info" v-if="myMaxStreak > 0">
          <span>🏆 本小时最高：{{ myMaxStreak }} 题</span>
        </div>
        <button class="start-btn streak-start" @click.stop="openStreakModal">开始挑战</button>
      </div>
    </div>

    <div class="ranking-section">
      <h3>📊 本小时排行榜</h3>
      <div class="countdown">
        距离下一轮结算还有 <span class="time">{{ countdownText }}</span>
      </div>
      <div v-if="ranking.length === 0" class="no-data">暂无记录，快来挑战吧！</div>
      <div v-else class="ranking-list">
        <div v-for="item in ranking" :key="item.uid" class="ranking-item" :class="{ 'is-me': item.uid === myUid }">
          <span class="rank-badge" :class="getRankClass(item.rank)">{{ item.rank <= 3 ? ['🥇','🥈','🥉'][item.rank-1] : item.rank }}</span>
          <img :src="getAvatarUrl(item.avatar)" class="rank-avatar" alt="头像">
          <span class="rank-name">{{ item.username }}</span>
          <span class="rank-streak">{{ item.maxStreak }}题</span>
        </div>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showExamModal" class="modal-overlay" @click.self="closeExamModal">
        <div class="modal-content exam-modal">
          <button class="modal-close" @click="closeExamModal">×</button>
          
          <div v-if="!examStarted" class="modal-intro">
            <div class="modal-icon">📝</div>
            <h3>知识问答</h3>
            <p>回答5道题目，答对3题及以上可获得积分奖励</p>
            <p class="intro-tip">💡 对3题得10积分，对4题得30积分，对5题得60积分</p>
            <button class="start-btn" @click="startExam">开始答题</button>
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
            <div class="result-summary">
              <div class="result-icon">{{ pointsEarned >= 30 ? '🎉' : pointsEarned >= 10 ? '👍' : '💪' }}</div>
              <h3>考试完成！</h3>
              <p class="result-score">获得 <span>{{ pointsEarned }}</span> 积分</p>
              <p class="result-detail">答对 {{ correctCount }} / {{ questions.length }} 题</p>
              <p class="result-tip" v-if="pointsEarned === 0">再接再厉，答对3题即可获得积分奖励！</p>
              <div class="result-actions">
                <button class="retry-btn" @click="resetExam">再来一次</button>
                <button class="back-btn" @click="closeExamModal">关闭</button>
              </div>
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
    </Teleport>

    <Teleport to="body">
      <div v-if="showStreakModal" class="modal-overlay" @click.self="closeStreakModal">
        <div class="modal-content streak-modal">
          <button class="modal-close" @click="closeStreakModal">×</button>
          
          <div v-if="!gameStarted" class="modal-intro">
            <div class="modal-icon">🔥</div>
            <h3>连续答题挑战</h3>
            <p>随机刷题，连续答对直到答错结束</p>
            <p class="intro-tip">💡 每小时结算一次，排行榜前5名可获得积分奖励</p>
            <div class="reward-preview">
              <div class="reward-item"><span class="rank gold">🥇</span><span>第一名</span><span class="points">+360积分</span></div>
              <div class="reward-item"><span class="rank silver">🥈</span><span>第二名</span><span class="points">+270积分</span></div>
              <div class="reward-item"><span class="rank bronze">🥉</span><span>第三名</span><span class="points">+180积分</span></div>
              <div class="reward-item"><span class="rank">4</span><span>第四名</span><span class="points">+90积分</span></div>
              <div class="reward-item"><span class="rank">5</span><span>第五名</span><span class="points">+1积分</span></div>
            </div>
            <div class="my-streak-info" v-if="myMaxStreak > 0">
              <span>🏆 本小时我的最高连对：{{ myMaxStreak }} 题</span>
            </div>
            <button class="start-btn streak-start" @click="startGame">开始挑战</button>
          </div>

          <div v-else class="game-area">
            <div class="game-header">
              <div class="streak-counter">
                <span class="streak-icon">🔥</span>
                <span class="streak-number">{{ currentStreak }}</span>
                <span class="streak-label">连对</span>
              </div>
              <div class="best-record">
                本小时最高：<span class="best-number">{{ myMaxStreak }}</span> 题
              </div>
            </div>

            <div class="question-card streak-question">
              <h4>{{ streakQuestion.questionText }}</h4>
              <div class="options">
                <button
                  class="option-btn"
                  :class="{ 'selected': streakSelectedAnswer === 'A', 'correct-show': streakAnswered && streakCorrectOption === 'A', 'wrong-show': streakAnswered && streakSelectedAnswer === 'A' && streakCorrectOption !== 'A' }"
                  :disabled="streakAnswered"
                  @click="selectStreakAnswer('A')"
                >A. {{ streakQuestion.optionA }}</button>
                <button
                  class="option-btn"
                  :class="{ 'selected': streakSelectedAnswer === 'B', 'correct-show': streakAnswered && streakCorrectOption === 'B', 'wrong-show': streakAnswered && streakSelectedAnswer === 'B' && streakCorrectOption !== 'B' }"
                  :disabled="streakAnswered"
                  @click="selectStreakAnswer('B')"
                >B. {{ streakQuestion.optionB }}</button>
                <button
                  class="option-btn"
                  :class="{ 'selected': streakSelectedAnswer === 'C', 'correct-show': streakAnswered && streakCorrectOption === 'C', 'wrong-show': streakAnswered && streakSelectedAnswer === 'C' && streakCorrectOption !== 'C' }"
                  :disabled="streakAnswered"
                  @click="selectStreakAnswer('C')"
                >C. {{ streakQuestion.optionC }}</button>
                <button
                  class="option-btn"
                  :class="{ 'selected': streakSelectedAnswer === 'D', 'correct-show': streakAnswered && streakCorrectOption === 'D', 'wrong-show': streakAnswered && streakSelectedAnswer === 'D' && streakCorrectOption !== 'D' }"
                  :disabled="streakAnswered"
                  @click="selectStreakAnswer('D')"
                >D. {{ streakQuestion.optionD }}</button>
              </div>

              <div v-if="streakAnswered && !streakIsCorrect" class="game-over">
                <div class="game-over-icon">💥</div>
                <h3>挑战结束！</h3>
                <p>本次连对 <span class="final-streak">{{ currentStreak }}</span> 题</p>
                <p class="game-over-tip" v-if="currentStreak > 0 && currentStreak >= myMaxStreak">
                  🎉 刷新了本小时最高记录！
                </p>
                <div class="game-over-actions">
                  <button class="retry-btn" @click="startGame">再来一次</button>
                  <button class="back-btn" @click="closeStreakModal">关闭</button>
                </div>
              </div>

              <div v-if="streakAnswered && streakIsCorrect" class="correct-feedback">
                <p class="correct-text">✅ 回答正确！继续挑战</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, inject, onMounted, onUnmounted } from 'vue';
import { ElMessage } from 'element-plus';

const API_BASE = '/api/exam';
const STREAK_API = '/api/streak';
const refreshPoints = inject('refreshPoints');

const showExamModal = ref(false);
const showStreakModal = ref(false);

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

const openExamModal = () => {
  showExamModal.value = true;
};

const closeExamModal = () => {
  showExamModal.value = false;
  resetExam();
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

const gameStarted = ref(false);
const streakQuestion = ref({});
const streakSelectedAnswer = ref(null);
const streakAnswered = ref(false);
const streakIsCorrect = ref(false);
const streakCorrectOption = ref(null);
const currentStreak = ref(0);
const myMaxStreak = ref(0);
const ranking = ref([]);
const myUid = ref(null);
const countdownText = ref('');

let countdownTimer = null;

const getAvatarUrl = (avatar) => {
  if (!avatar) return '/default-avatar.svg';
  if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') return '/default-avatar.svg';
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) return avatar;
  return `/Usersimg/${avatar}`;
};

const getRankClass = (rank) => {
  if (rank === 1) return 'gold';
  if (rank === 2) return 'silver';
  if (rank === 3) return 'bronze';
  return '';
};

const fetchRanking = async () => {
  try {
    const response = await fetch(`${STREAK_API}/ranking`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (response.ok) {
      const data = await response.json();
      ranking.value = data.ranking || [];
      myMaxStreak.value = data.myMaxStreak || 0;
      const user = JSON.parse(localStorage.getItem('user') || '{}');
      myUid.value = user.id || null;
    }
  } catch (error) {
    console.error('获取排行榜失败:', error);
  }
};

const updateCountdown = () => {
  const now = new Date();
  const nextHour = new Date(now);
  nextHour.setHours(nextHour.getHours() + 1, 0, 0, 0);
  const diff = nextHour - now;
  const minutes = Math.floor(diff / 60000);
  const seconds = Math.floor((diff % 60000) / 1000);
  countdownText.value = `${minutes}分${seconds.toString().padStart(2, '0')}秒`;
};

const openStreakModal = () => {
  showStreakModal.value = true;
};

const closeStreakModal = () => {
  showStreakModal.value = false;
  gameStarted.value = false;
  fetchRanking();
};

const startGame = async () => {
  currentStreak.value = 0;
  streakSelectedAnswer.value = null;
  streakAnswered.value = false;
  streakIsCorrect.value = false;
  streakCorrectOption.value = null;
  gameStarted.value = true;
  await fetchNextStreakQuestion();
};

const fetchNextStreakQuestion = async () => {
  try {
    const response = await fetch(`${STREAK_API}/question`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (response.ok) {
      const data = await response.json();
      streakQuestion.value = data.question || {};
      streakSelectedAnswer.value = null;
      streakAnswered.value = false;
      streakIsCorrect.value = false;
      streakCorrectOption.value = null;
    } else {
      ElMessage.error('获取题目失败');
      gameStarted.value = false;
    }
  } catch (error) {
    console.error('获取题目失败:', error);
    ElMessage.error('网络错误');
    gameStarted.value = false;
  }
};

const selectStreakAnswer = async (option) => {
  if (streakAnswered.value) return;
  streakSelectedAnswer.value = option;
  streakAnswered.value = true;

  try {
    const response = await fetch(`${STREAK_API}/answer`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        questionId: streakQuestion.value.id,
        answer: option,
        currentStreak: currentStreak.value
      })
    });

    const data = await response.json();
    streakIsCorrect.value = data.correct;
    streakCorrectOption.value = data.correctOption;
    currentStreak.value = data.currentStreak;

    if (data.correct) {
      if (data.currentStreak > myMaxStreak.value) {
        myMaxStreak.value = data.currentStreak;
      }
      setTimeout(() => {
        fetchNextStreakQuestion();
      }, 600);
    }
  } catch (error) {
    console.error('提交答案失败:', error);
    ElMessage.error('网络错误');
    streakAnswered.value = false;
  }
};

onMounted(() => {
  fetchRanking();
  updateCountdown();
  countdownTimer = setInterval(() => {
    updateCountdown();
  }, 1000);
});

onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
});
</script>

<style scoped>
.points-exam-container {
  padding: 20px;
  overflow-y: auto;
  max-height: calc(100vh - 120px);
}

.points-exam-container::-webkit-scrollbar {
  width: 6px;
}

.points-exam-container::-webkit-scrollbar-track {
  background: transparent;
}

.points-exam-container::-webkit-scrollbar-thumb {
  background: transparent;
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.header-container h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.cards-wrapper {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.intro-card {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 16px;
  padding: 28px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.intro-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(37, 99, 235, 0.15);
  border-color: #3b82f6;
}

.intro-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.intro-card h3 {
  margin: 0 0 8px 0;
  color: #1a202c;
  font-size: 22px;
}

.intro-card p {
  color: #64748b;
  margin: 0 0 8px 0;
  font-size: 14px;
}

.intro-tip {
  font-size: 13px !important;
  color: #3b82f6 !important;
  margin-top: 12px !important;
}

.start-btn {
  margin-top: 20px;
  padding: 10px 36px;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.start-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.3);
}

.streak-card {
  background: linear-gradient(135deg, #fff7ed 0%, #fef3c7 100%);
  border: 1px solid rgba(251, 191, 36, 0.3);
}

.streak-card:hover {
  border-color: #f59e0b;
  box-shadow: 0 12px 32px rgba(245, 158, 11, 0.15);
}

.streak-card .intro-tip {
  color: #d97706 !important;
}

.streak-start {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.streak-start:hover {
  box-shadow: 0 6px 20px rgba(245, 158, 11, 0.3);
}

.reward-preview {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 16px;
  flex-wrap: wrap;
}

.reward-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 6px;
  font-size: 12px;
  color: #475569;
}

.reward-item .rank {
  font-weight: 700;
  font-size: 13px;
  width: 18px;
  text-align: center;
}

.reward-item .rank.gold { color: #f59e0b; }
.reward-item .rank.silver { color: #94a3b8; }
.reward-item .rank.bronze { color: #d97706; }

.reward-item .points {
  color: #2563eb;
  font-weight: 600;
}

.my-streak-info {
  margin-top: 12px;
  padding: 8px 16px;
  background: rgba(37, 99, 235, 0.1);
  border-radius: 8px;
  color: #2563eb;
  font-weight: 600;
  font-size: 14px;
  display: inline-block;
}

.ranking-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.ranking-section h3 {
  margin: 0 0 12px 0;
  color: #1a202c;
  font-size: 18px;
}

.countdown {
  text-align: center;
  color: #64748b;
  font-size: 14px;
  margin-bottom: 16px;
  padding: 8px;
  background: #f8fafc;
  border-radius: 8px;
}

.countdown .time {
  color: #ef4444;
  font-weight: 700;
  font-size: 16px;
}

.no-data {
  text-align: center;
  color: #94a3b8;
  padding: 32px 0;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
}

.ranking-item.is-me {
  background: #eff6ff;
  border-color: #93c5fd;
}

.ranking-item.is-me .rank-name {
  color: #2563eb;
  font-weight: 600;
}

.rank-badge {
  font-size: 16px;
  width: 28px;
  text-align: center;
}

.rank-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e2e8f0;
}

.rank-name {
  flex: 1;
  font-size: 14px;
  color: #334155;
  font-weight: 500;
}

.rank-streak {
  font-size: 14px;
  font-weight: 700;
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
  padding: 2px 10px;
  border-radius: 12px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.modal-content {
  width: 80vw;
  height: 80vh;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 36px;
  height: 36px;
  border: none;
  background: #f1f5f9;
  border-radius: 50%;
  font-size: 24px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  background: #e2e8f0;
  color: #1a202c;
}

.modal-intro {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.modal-icon {
  font-size: 64px;
  margin-bottom: 16px;
}

.modal-intro h3 {
  margin: 0 0 12px 0;
  font-size: 28px;
  color: #1a202c;
}

.modal-intro p {
  color: #64748b;
  margin: 0 0 8px 0;
  font-size: 16px;
}

.modal-intro .intro-tip {
  font-size: 14px !important;
  margin-top: 16px !important;
}

.modal-intro .start-btn {
  margin-top: 24px;
  padding: 14px 48px;
  font-size: 16px;
}

.modal-intro .reward-preview {
  margin-top: 20px;
}

.modal-intro .reward-item {
  padding: 6px 12px;
  font-size: 13px;
}

.modal-intro .my-streak-info {
  margin-top: 16px;
  font-size: 15px;
}

.exam-modal .modal-intro {
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
}

.streak-modal .modal-intro {
  background: linear-gradient(135deg, #fff7ed 0%, #fef3c7 100%);
}

.exam-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-bar {
  width: 100%;
  max-width: 600px;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
  margin-bottom: 10px;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #2563eb 0%, #3b82f6 100%);
  transition: width 0.3s ease;
}
.question-counter {
  text-align: center;
  color: #64748b;
  margin-bottom: 10px;
  font-size: 14px;
}
.question-card {
  background: #f8fafc;
  border-radius: 12px;
  padding: 20px;
  width: 100%;
  max-width: 600px;
  border: 1px solid #e2e8f0;
}
.question-card h4 {
  margin: 0 0 16px 0;
  color: #1a202c;
  font-size: 16px;
  line-height: 1.5;
}
.options {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.option-btn {
  padding: 12px 16px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 10px;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
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
  margin-top: 24px;
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
  flex: 1;
  display: flex;
  gap: 32px;
  padding: 32px;
  overflow: hidden;
}

.result-summary {
  flex: 0 0 220px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-right: 2px solid #e2e8f0;
  padding-right: 32px;
}

.result-icon {
  font-size: 56px;
  margin-bottom: 12px;
}

.result-summary h3 {
  margin: 0 0 12px 0;
  color: #1a202c;
  font-size: 22px;
}

.result-score {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 6px 0;
}

.result-score span {
  font-size: 32px;
  font-weight: 700;
  color: #2563eb;
}

.result-detail {
  color: #94a3b8;
  margin: 0 0 8px 0;
  font-size: 14px;
}

.result-tip {
  color: #ef4444;
  font-size: 13px;
  margin: 0 0 20px 0;
}

.result-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  width: 100%;
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

.back-btn {
  padding: 10px 32px;
  background: #f1f5f9;
  color: #475569;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: #e2e8f0;
}

.result-details {
  flex: 1;
  min-width: 0;
  text-align: left;
  overflow-y: auto;
}

.result-details h4 {
  font-size: 16px;
  color: #1a202c;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e2e8f0;
}

.detail-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  padding: 16px;
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

.game-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.game-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #fff7ed 0%, #fef3c7 100%);
  border-radius: 10px;
  border: 1px solid rgba(251, 191, 36, 0.2);
  width: 100%;
  max-width: 600px;
}
.streak-counter {
  display: flex;
  align-items: center;
  gap: 6px;
}
.streak-icon {
  font-size: 28px;
}
.streak-number {
  font-size: 28px;
  font-weight: 800;
  color: #f59e0b;
}
.streak-label {
  font-size: 14px;
  color: #92400e;
  font-weight: 600;
}
.best-record {
  font-size: 13px;
  color: #64748b;
}
.best-number {
  font-weight: 700;
  color: #2563eb;
}
.streak-question {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
}
.option-btn.correct-show {
  border-color: #10b981;
  background: #f0fdf4;
  color: #065f46;
}
.option-btn.wrong-show {
  border-color: #ef4444;
  background: #fef2f2;
  color: #991b1b;
}
.game-over {
  margin-top: 16px;
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-radius: 10px;
  border: 1px solid rgba(239, 68, 68, 0.2);
  width: 100%;
}
.game-over-icon {
  font-size: 36px;
  margin-bottom: 6px;
}
.game-over h3 {
  margin: 0 0 6px 0;
  color: #991b1b;
  font-size: 18px;
}
.game-over p {
  margin: 0;
  color: #64748b;
  font-size: 14px;
}
.final-streak {
  font-size: 22px;
  font-weight: 800;
  color: #f59e0b;
}
.game-over-tip {
  color: #2563eb !important;
  font-weight: 600;
  margin-top: 6px !important;
  font-size: 13px;
}
.game-over-actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  justify-content: center;
}
.correct-feedback {
  margin-top: 12px;
  text-align: center;
}
.correct-text {
  font-size: 14px;
  font-weight: 600;
  color: #059669;
  animation: fadeInUp 0.3s ease;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 900px) {
  .cards-wrapper {
    grid-template-columns: 1fr;
  }
  
  .exam-result {
    flex-direction: column;
  }
  
  .result-summary {
    flex: none;
    border-right: none;
    border-bottom: 2px solid #e2e8f0;
    padding-right: 0;
    padding-bottom: 24px;
  }
}
</style>
