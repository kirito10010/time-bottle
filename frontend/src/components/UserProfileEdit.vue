<template>
  <div class="user-profile-edit-overlay" @click="close">
    <div class="user-profile-edit" @click.stop>
      <h3>修改个人信息</h3>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="avatar">头像</label>
          <div class="avatar-upload">
            <img :src="getAvatarUrl(avatar)" alt="当前头像" class="current-avatar">
            <input type="file" id="avatar" @change="handleAvatarChange" accept="image/*">
          </div>
        </div>
        
        <div class="form-group">
          <label for="nickname">昵称</label>
          <input type="text" id="nickname" v-model="nickname" required>
        </div>
        
        <div class="form-group">
          <label for="password">新密码（留空不修改）</label>
          <input type="password" id="password" v-model="password">
        </div>
        
        <div class="form-actions">
          <button type="button" class="cancel-button" @click="close">取消</button>
          <button type="submit" class="save-button">保存</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  userInfo: {
    type: Object,
    required: true
  }
});

const emit = defineEmits(['close', 'update']);

const nickname = ref(props.userInfo.nickname || props.userInfo.username);
const password = ref('');
const avatar = ref(props.userInfo.avatar);
const avatarFile = ref(null);

const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return 'http://localhost:8080/default-avatar.svg';
  }
  if (avatar.startsWith('http://') || avatar.startsWith('https://') || avatar.startsWith('data:image/')) {
    return avatar;
  }
  if (avatar === 'default-avatar.svg' || avatar === 'default-avatar.png') {
    return 'http://localhost:8080/default-avatar.svg';
  }
  return 'http://localhost:8080/Usersimg/' + avatar;
};

const handleAvatarChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    avatarFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      avatar.value = e.target.result;
    };
    reader.readAsDataURL(file);
  }
};

const handleSubmit = async () => {
  try {
    const formData = new FormData();
    formData.append('username', props.userInfo.username);
    formData.append('nickname', nickname.value);
    if (password.value) {
      formData.append('password', password.value);
    }
    if (avatarFile.value) {
      formData.append('avatar', avatarFile.value);
    }
    
    const response = await fetch('http://localhost:8080/api/auth/profile', {
      method: 'PUT',
      body: formData
    });
    
    const data = await response.json();
    
    if (response.ok) {
      ElMessage.success('修改成功');
      emit('update', data.user);
      setTimeout(() => {
        emit('close');
      }, 1500);
    } else {
      ElMessage.error(data.message || '修改失败');
    }
  } catch (err) {
    ElMessage.error('网络错误，请稍后重试');
    console.error('修改信息错误:', err);
  }
};

const close = () => {
  emit('close');
};
</script>

<style scoped>
.user-profile-edit-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.user-profile-edit {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  padding: 30px;
  border-radius: 16px;
  box-shadow: 
    0 8px 32px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 1px solid rgba(255, 255, 255, 0.8);
  width: 100%;
  max-width: 420px;
  animation: fadeIn 0.3s ease;
}

.user-profile-edit h3 {
  margin-top: 0;
  margin-bottom: 24px;
  text-align: center;
  color: #2d3748;
  font-size: 18px;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(224, 230, 237, 0.5);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4a5568;
  font-size: 14px;
}

.form-group input[type="text"],
.form-group input[type="password"] {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid rgba(224, 230, 237, 0.8);
  border-radius: 10px;
  font-size: 14px;
  transition: all 0.2s ease;
  outline: none;
  background: rgba(255, 255, 255, 0.8);
}

.form-group input[type="text"]:focus,
.form-group input[type="password"]:focus {
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.1);
}

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.current-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #4299e1;
  box-shadow: 0 2px 8px rgba(66, 153, 225, 0.2);
  transition: transform 0.2s ease;
}

.current-avatar:hover {
  transform: scale(1.05);
}

.avatar-upload input[type="file"] {
  flex: 1;
  padding: 8px;
  border: 1px dashed rgba(203, 213, 224, 0.8);
  border-radius: 10px;
  background: rgba(247, 250, 252, 0.8);
  transition: all 0.2s ease;
}

.avatar-upload input[type="file"]:hover {
  border-color: #4299e1;
  background: rgba(237, 242, 247, 0.8);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid rgba(224, 230, 237, 0.5);
}

.cancel-button,
.save-button {
  padding: 10px 24px;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  outline: none;
}

.cancel-button {
  background: rgba(247, 250, 252, 0.8);
  color: #4a5568;
  border: 1px solid rgba(224, 230, 237, 0.8);
}

.cancel-button:hover {
  background: rgba(237, 242, 247, 0.8);
  border-color: rgba(203, 213, 224, 0.8);
  transform: translateY(-1px);
}

.save-button {
  background: linear-gradient(135deg, #4299e1 0%, #667eea 100%);
  color: #ffffff;
}

.save-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(66, 153, 225, 0.3);
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
</style>
