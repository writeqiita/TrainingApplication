<template>
  <div class="register-container">
    <h2>新規ユーザー登録</h2>
    <form @submit.prevent="handleRegister">
      <div class="input-group">
        <label>ユーザー名</label>
        <input v-model="username" required />
      </div>
      <div class="input-group">
        <label>パスワード</label>
        <input type="password" v-model="password" required />
      </div>
      <button type="submit" class="btn-register">登録</button>
    </form>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

const handleRegister = async () => {
  try {
    const res = await axios.post('/api/auth/register', {
      username: username.value,
      password: password.value
    })
    if (res.data.success) {
      router.push('/')  // ログイン画面へ遷移
    } else {
      errorMessage.value = res.data.message
    }
  } catch (e) {
    // サーバーが返したメッセージがある場合はそれを表示
    if (e.response && e.response.data && e.response.data.message) {
      errorMessage.value = e.response.data.message
    } else {
      errorMessage.value = '登録に失敗しました。'
    }
  }
}
</script>

<style>
.register-container {
  max-width: 300px;
  margin: 0 auto;
  padding: 30px;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
  background-color: #fff;
}

.input-group {
  margin-bottom: 15px;
  text-align: left;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.input-group input {
  width: 100%;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
  color: white;
  font-size: 16px;
}

.btn-register {
  background-color: #4CAF50;
}

.btn-register:hover {
  background-color: #45a049;
  transform: scale(1.03);
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
