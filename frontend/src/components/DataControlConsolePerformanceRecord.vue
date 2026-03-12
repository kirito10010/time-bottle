<template>
  <div class="performance-record">
    <h2>绩效记录</h2>
    <div class="placeholder">
      <p>绩效记录内容</p>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.performance-record {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 
    0 4px 16px rgba(37, 99, 235, 0.06),
    0 1px 2px rgba(0, 0, 0, 0.03),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  padding: 24px;
  transition: all 0.3s ease;
  height: 100%;
  box-sizing: border-box;
}

.performance-record:hover {
  box-shadow: 
    0 8px 24px rgba(37, 99, 235, 0.1),
    0 2px 4px rgba(0, 0, 0, 0.04),
    inset 0 1px 0 rgba(255, 255, 255, 1);
}

.performance-record h2 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 24px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.placeholder {
  height: calc(100% - 80px);
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(248, 250, 252, 0.5);
  border-radius: 12px;
  border: 1px dashed rgba(203, 213, 224, 0.5);
}

.placeholder p {
  font-size: 16px;
  color: #a0aec0;
  margin: 0;
}
</style>
