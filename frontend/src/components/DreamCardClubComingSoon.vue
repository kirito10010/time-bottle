<script setup>
</script>

<template>
  <div class="coming-soon-container">
    <div class="coming-soon-content">
      <div class="icon-wrapper">
        <span class="icon">🚧</span>
      </div>
      <h2>功能开发中</h2>
      <p class="description">该功能正在紧张开发中，敬请期待...</p>
      <div class="features-preview">
        <div class="feature-item">
          <span class="feature-icon">✨</span>
          <span>更多精彩功能</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🎮</span>
          <span>全新游戏体验</span>
        </div>
        <div class="feature-item">
          <span class="feature-icon">🎁</span>
          <span>丰富奖励机制</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.coming-soon-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 40px 20px;
}

.coming-soon-content {
  text-align: center;
  max-width: 400px;
}

.icon-wrapper {
  margin-bottom: 24px;
}

.icon {
  font-size: 80px;
  display: inline-block;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 12px 0;
}

.description {
  font-size: 16px;
  color: #718096;
  margin: 0 0 32px 0;
  line-height: 1.6;
}

.features-preview {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f4fd 100%);
  border-radius: 12px;
  font-size: 15px;
  color: #4a5568;
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateX(5px);
  background: linear-gradient(135deg, #e0efff 0%, #d0e8ff 100%);
}

.feature-icon {
  font-size: 20px;
}
</style>
