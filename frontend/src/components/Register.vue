<template>
  <div class="register-container">
    <div class="register-card">
      <div class="brand-section">
        <img src="/time-bottle.png" alt="Time Bottle" class="brand-logo">
        <h1 class="brand-title">拾光瓶</h1>
        <p class="brand-subtitle">记录生活的每一刻</p>
      </div>
      
      <div class="form-section">
        <h2 class="form-title">用户注册</h2>
        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-group">
            <label for="username" class="form-label">用户名</label>
            <div class="input-container">
              <span class="input-icon">👤</span>
              <input 
                type="text" 
                id="username" 
                v-model="username" 
                class="form-input" 
                placeholder="请输入用户名" 
                required
              >
            </div>
          </div>
          
          <div class="form-group">
            <label for="email" class="form-label">邮箱</label>
            <div class="input-container">
              <span class="input-icon">📧</span>
              <input 
                type="email" 
                id="email" 
                v-model="email" 
                class="form-input" 
                placeholder="请输入邮箱" 
                required
              >
            </div>
          </div>
          
          <div class="form-group">
            <label for="password" class="form-label">密码</label>
            <div class="input-container">
              <span class="input-icon">🔒</span>
              <input 
                type="password" 
                id="password" 
                v-model="password" 
                class="form-input" 
                placeholder="请输入密码" 
                required
              >
            </div>
          </div>
          
          <button 
            type="submit" 
            class="register-button" 
            :disabled="isLoading"
          >
            <span v-if="!isLoading">注册</span>
            <span v-else class="loading-spinner">⏳</span>
          </button>
          
          <div class="form-footer">
            <span>已有账号？</span>
            <button 
              type="button" 
              class="switch-button" 
              @click="$emit('switchToLogin')"
            >
              立即登录
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const emit = defineEmits(['switchToLogin']);

const username = ref('');
const email = ref('');
const password = ref('');
const isLoading = ref(false);

const handleRegister = async () => {
  isLoading.value = true;
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/register', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ 
        username: username.value, 
        password: password.value, 
        email: email.value 
      })
    });

    const data = await response.json();

    if (response.ok) {
      ElMessage.success(data.message || '注册成功，正在跳转到登录页面...');
      setTimeout(() => {
        emit('switchToLogin');
      }, 2000);
    } else {
      ElMessage.error(data.message || '注册失败，请检查输入信息');
    }
  } catch (err) {
    ElMessage.error('网络错误，请稍后重试');
    console.error('注册错误:', err);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  padding: 20px;
  position: relative;
  z-index: 2;
}

.register-card {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 450px;
  overflow: hidden;
  animation: fadeIn 0.5s ease;
  position: relative;
  z-index: 3;
}

.brand-section {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  padding: 40px 30px;
  text-align: center;
  color: #1f2937;
}

.brand-logo {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
  border-radius: 50%;
  background: white;
  padding: 10px;
}

.brand-title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 8px;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.form-section {
  padding: 40px 30px;
}

.form-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  text-align: center;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8px;
}

.input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 12px;
  font-size: 16px;
  color: #9ca3af;
}

.form-input {
  width: 100%;
  padding: 12px 16px 12px 40px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: #f9fafb;
}

.form-input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
  background: white;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: #1f2937;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

.form-footer {
  text-align: center;
  font-size: 14px;
  color: #6b7280;
}

.switch-button {
  background: none;
  border: none;
  color: #4f46e5;
  font-weight: 600;
  cursor: pointer;
  padding: 0 4px;
  transition: color 0.3s ease;
}

.switch-button:hover {
  color: #4338ca;
  text-decoration: underline;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 480px) {
  .register-card {
    max-width: 100%;
  }
  
  .brand-section {
    padding: 30px 20px;
  }
  
  .form-section {
    padding: 30px 20px;
  }
  
  .brand-logo {
    width: 60px;
    height: 60px;
  }
  
  .brand-title {
    font-size: 20px;
  }
  
  .form-title {
    font-size: 18px;
  }
}
</style>
