<template>
  <div class="login-container">
    <h1>トレーニングアプリ</h1>
    <form @submit.prevent="handleLogin">
      <div class="input-group">
        <label>ユーザーID</label>
        <input v-model="username" required />
      </div>
      <div class="input-group">
        <label>パスワード</label>
        <input type="password" v-model="password" required />
      </div>
      <!-- ログインボタン -->
      <button type="submit" class="btn-login">ログイン</button>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <!-- 新規ユーザー登録ボタン -->
      <button type="button" class="btn-register" @click="goToRegister">新規ユーザー登録</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const router = useRouter()

// 新規ユーザー登録画面へ遷移
const goToRegister = () => {
  router.push('/register')
}

const handleLogin = async () => {
  try {
    const res = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value
    })
    if (res.data.success) {
      // ログイン情報をセッションに保存
      sessionStorage.setItem('userId', res.data.userId)
      sessionStorage.setItem('username', res.data.username)
      sessionStorage.setItem('weight', res.data.weight)
      sessionStorage.setItem('admin', res.data.admin)
      // ホーム画面に遷移
      router.push('/home')
    } else {
      errorMessage.value = res.data.message
    }
  } catch (err) {
    errorMessage.value = 'ユーザーIDまたはパスワードが間違っています'
  }
}
</script>

<style>
.login-container {
  max-width: 300px;
  margin: 0 auto;
  padding: 30px;
  border: 1px solid #ddd;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
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

.btn-login {
  background-color: #4CAF50; /* 緑 */
}

.btn-login:hover {
  background-color: #45a049;
  transform: scale(1.03);
}

.btn-register {
  background-color: #2196F3; /* 青 */
}

.btn-register:hover {
  background-color: #1976D2;
  transform: scale(1.03);
}

.error-message {
  color: red;
  margin-top: 10px;
}
</style>
