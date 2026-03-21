<template>
  <div class="login-container">
    <div class="login-card">
      <div class="brand-section">
        <img src="/time-bottle.png" alt="Time Bottle" class="brand-logo">
        <h1 class="brand-title">拾光瓶</h1>
        <p class="brand-subtitle">记录生活的每一刻</p>
      </div>
      
      <div class="form-section">
        <h2 class="form-title">用户登录</h2>
        <form @submit.prevent="handleLogin" class="login-form">
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
            class="login-button" 
            :disabled="isLoading"
          >
            <span v-if="!isLoading">登录</span>
            <span v-else class="loading-spinner">⏳</span>
          </button>
          
          <div class="form-footer">
            <button 
              type="button" 
              class="forgot-password" 
              @click="showForgotDialog = true"
            >
              忘记密码？
            </button>
            <span>还没有账号？</span>
            <button 
              type="button" 
              class="switch-button" 
              @click="router.push('/register')"
            >
              立即注册
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 忘记密码弹窗 -->
    <Teleport to="body">
      <div v-if="showForgotDialog" class="forgot-dialog-overlay" @click="closeForgotDialog">
        <div class="forgot-dialog" @click.stop>
          <div class="forgot-dialog-header">
            <h3>找回密码</h3>
            <button class="close-btn" @click="closeForgotDialog">×</button>
          </div>
          
          <div class="forgot-dialog-content">
            <!-- 步骤1: 输入邮箱 -->
            <div v-if="forgotStep === 1" class="forgot-step">
              <div class="form-group">
                <label>邮箱地址</label>
                <input 
                  type="email" 
                  v-model="forgotEmail" 
                  class="form-input"
                  placeholder="请输入注册时的邮箱"
                >
              </div>
              <button 
                class="forgot-button" 
                @click="sendResetCode"
                :disabled="forgotLoading"
              >
                {{ forgotLoading ? '发送中...' : '发送验证码' }}
              </button>
            </div>

            <!-- 步骤2: 输入验证码和新密码 -->
            <div v-else-if="forgotStep === 2" class="forgot-step">
              <div class="form-group">
                <label>验证码</label>
                <div class="verify-code-input">
                  <input 
                    type="text" 
                    v-model="forgotCode" 
                    class="form-input"
                    placeholder="请输入6位验证码"
                    maxlength="6"
                  >
                  <button 
                    class="resend-btn" 
                    @click="sendResetCode"
                    :disabled="countdown > 0"
                  >
                    {{ countdown > 0 ? `${countdown}秒后重发` : '重新发送' }}
                  </button>
                </div>
              </div>
              <div class="form-group">
                <label>新密码</label>
                <input 
                  type="password" 
                  v-model="forgotNewPassword" 
                  class="form-input"
                  placeholder="请输入新密码（至少6位）"
                >
              </div>
              <div class="form-group">
                <label>确认密码</label>
                <input 
                  type="password" 
                  v-model="forgotConfirmPassword" 
                  class="form-input"
                  placeholder="请再次输入新密码"
                >
              </div>
              <button 
                class="forgot-button" 
                @click="resetPassword"
                :disabled="forgotLoading"
              >
                {{ forgotLoading ? '处理中...' : '确认重置' }}
              </button>
            </div>

            <!-- 步骤3: 成功提示 -->
            <div v-else-if="forgotStep === 3" class="forgot-step success-step">
              <div class="success-icon">✓</div>
              <p>密码重置成功！</p>
              <button 
                class="forgot-button" 
                @click="closeForgotDialog"
              >
                立即登录
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';

const router = useRouter();
const emit = defineEmits(['switchToRegister', 'loginSuccess']);

const username = ref('');
const password = ref('');
const isLoading = ref(false);

const showForgotDialog = ref(false);
const forgotStep = ref(1);
const forgotEmail = ref('');
const forgotCode = ref('');
const forgotNewPassword = ref('');
const forgotConfirmPassword = ref('');
const forgotLoading = ref(false);
const countdown = ref(0);
let countdownTimer = null;

const handleLogin = async () => {
  isLoading.value = true;
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ username: username.value, password: password.value })
    });

    const data = await response.json();

    if (response.ok) {
      localStorage.setItem('user', JSON.stringify(data.user));
      ElMessage.success('登录成功');
      emit('loginSuccess');
      router.push('/financial-ledger');
    } else {
      ElMessage.error(data.message || '登录失败，请检查用户名和密码');
    }
  } catch (err) {
    ElMessage.error('网络错误，请稍后重试');
    console.error('登录错误:', err);
  } finally {
    isLoading.value = false;
  }
};

const closeForgotDialog = () => {
  showForgotDialog.value = false;
  forgotStep.value = 1;
  forgotEmail.value = '';
  forgotCode.value = '';
  forgotNewPassword.value = '';
  forgotConfirmPassword.value = '';
  countdown.value = 0;
  if (countdownTimer) {
    clearInterval(countdownTimer);
  }
};

const startCountdown = () => {
  countdown.value = 60;
  countdownTimer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(countdownTimer);
    }
  }, 1000);
};

const sendResetCode = async () => {
  if (!forgotEmail.value) {
    ElMessage.warning('请输入邮箱地址');
    return;
  }

  forgotLoading.value = true;
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: forgotEmail.value })
    });

    let data;
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      data = await response.json();
    } else {
      const text = await response.text();
      throw new Error(text || '服务器响应异常');
    }

    if (response.ok && data.success) {
      ElMessage.success('验证码已发送到您的邮箱');
      forgotStep.value = 2;
      startCountdown();
    } else {
      ElMessage.error(data.message || '发送失败');
    }
  } catch (err) {
    ElMessage.error('网络错误，请稍后重试');
    console.error('发送验证码错误:', err);
  } finally {
    forgotLoading.value = false;
  }
};

const resetPassword = async () => {
  if (!forgotCode.value) {
    ElMessage.warning('请输入验证码');
    return;
  }
  if (!forgotNewPassword.value || forgotNewPassword.value.length < 6) {
    ElMessage.warning('密码长度不能少于6位');
    return;
  }
  if (forgotNewPassword.value !== forgotConfirmPassword.value) {
    ElMessage.warning('两次输入的密码不一致');
    return;
  }

  forgotLoading.value = true;
  
  try {
    const response = await fetch('http://localhost:8080/api/auth/reset-password', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: forgotEmail.value,
        code: forgotCode.value,
        newPassword: forgotNewPassword.value
      })
    });

    const data = await response.json();

    if (response.ok && data.success) {
      forgotStep.value = 3;
      ElMessage.success('密码重置成功');
    } else {
      ElMessage.error(data.message || '重置失败');
    }
  } catch (err) {
    ElMessage.error('网络错误，请稍后重试');
    console.error('重置密码错误:', err);
  } finally {
    forgotLoading.value = false;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  padding: 20px;
  position: relative;
  z-index: 2;
}

.login-card {
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

.login-button {
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

.login-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.login-button:disabled {
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
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.forgot-password {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #4f46e5;
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
  .login-card {
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

.forgot-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.forgot-dialog {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

.forgot-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.forgot-dialog-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #9ca3af;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #4b5563;
}

.forgot-dialog-content {
  padding: 24px;
}

.forgot-step {
  display: flex;
  flex-direction: column;
}

.forgot-step .form-group {
  margin-bottom: 20px;
}

.forgot-step label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.verify-code-input {
  display: flex;
  gap: 12px;
}

.verify-code-input .form-input {
  flex: 1;
}

.resend-btn {
  white-space: nowrap;
  padding: 10px 16px;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  color: #4b5563;
  cursor: pointer;
  transition: all 0.2s;
}

.resend-btn:hover:not(:disabled) {
  background: #e5e7eb;
}

.resend-btn:disabled {
  color: #9ca3af;
  cursor: not-allowed;
}

.forgot-button {
  width: 100%;
  padding: 14px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.forgot-button:hover:not(:disabled) {
  background: #4338ca;
  transform: translateY(-2px);
}

.forgot-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.success-step {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #10b981;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: white;
  margin: 0 auto 20px;
}

.success-step p {
  font-size: 18px;
  color: #1f2937;
  margin-bottom: 24px;
}
</style>
