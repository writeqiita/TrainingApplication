<template>
    <div class="home-container">
      <h1>ユーザー情報更新</h1>
  
      <div v-if="loading">
        <p>データを読み込み中...</p>
      </div>
  
      <div v-else-if="error">
        <p class="error-message">{{ error }}</p>
      </div>
  
      <div v-else>
        <!-- 現在体重の入力フォーム -->
        <div class="weight-input-section">
          <h2>現在の体重を登録</h2>
          <form @submit.prevent="updateWeight">
            <label for="weight">体重 (kg):</label>
            <input
              type="number"
              id="weight"
              v-model="currentWeight"
              step="0.1"
              min="0"
              required
            />
            <button type="submit" class="primary-btn" :disabled="updating">
              {{ updating ? '更新中...' : '登録' }}
            </button>
          </form>
        </div>
  
        <!-- 体重履歴一覧 -->
        <div class="weight-history-section">
          <h2>体重履歴</h2>
          <div v-if="weightHistory.length === 0" class="no-data">
            <p>まだ体重の記録がありません。</p>
          </div>
          <div v-else class="training-history">
            <div
              v-for="(record, index) in sortedWeightHistory"
              :key="index"
              class="training-item"
            >
              <div class="training-header">
                <h3>{{ formatDate(record.date) }}</h3>
                <span class="training-date">{{ record.weight.toFixed(1) }} kg</span>
              </div>
            </div>
          </div>
        </div>
  
        <!-- 戻るボタン -->
        <div class="button-group">
          <button @click="goHome" class="secondary-btn">ホームへ戻る</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  const router = useRouter()
  const username = ref('')
  const userId = sessionStorage.getItem('userId')
  const currentWeight = ref('')
  const weightHistory = ref([])
  const loading = ref(false)
  const updating = ref(false)
  const error = ref('')
  
  // ページマウント時にユーザー情報を取得
  onMounted(async () => {
    username.value = sessionStorage.getItem('username') || 'ゲスト'
    
    if (!userId) {
      error.value = 'ログイン情報が見つかりません。'
      return
    }
  
    await fetchWeightHistory()
  })
  
  // 体重履歴を取得
  const fetchWeightHistory = async () => {
    loading.value = true
    error.value = ''
    try {
      const response = await axios.get(`/api/users/${userId}/weight-history`)
      weightHistory.value = response.data.weights || []
    } catch (err) {
      console.error('体重履歴取得エラー:', err)
      error.value = '体重履歴の取得に失敗しました。'
    } finally {
      loading.value = false
    }
  }
  
  // 日付フォーマット
  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('ja-JP', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }
  
  // ソート済み体重履歴（新しい順）
  const sortedWeightHistory = computed(() => {
    return [...weightHistory.value].sort((a, b) => new Date(b.date) - new Date(a.date))
  })
  
  // 体重更新
  const updateWeight = async () => {
    if (currentWeight.value < 1 || currentWeight.value > 1000) {
        alert('体重は1〜1000の範囲で入力してください。')
        return
    }

    updating.value = true
    try {
      const response = await axios.post(`/api/users/${userId}/update-weight`, {
        weight: parseFloat(currentWeight.value)
      })
      if (response.data.success) {
        alert('体重を更新しました！')
        currentWeight.value = ''
        await fetchWeightHistory()
      } else {
        alert('更新に失敗しました。')
      }
    } catch (err) {
      console.error('体重更新エラー:', err)
      alert('サーバーエラーにより更新できませんでした。')
    } finally {
      updating.value = false
    }
  }
  
  // ホームへ戻る
  const goHome = () => {
    router.push('/home')
  }
  </script>
  
  <style>
  /* ホーム画面と同じスタイルを流用 */
  .home-container {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .error-message {
    color: #d32f2f;
    background-color: #ffebee;
    border: 1px solid #ffcdd2;
    border-radius: 4px;
    padding: 12px;
    margin: 20px 0;
    text-align: center;
  }
  
  .weight-input-section {
    background-color: #e3f2fd;
    border: 1px solid #bbdefb;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
  }
  
  .weight-input-section form {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .weight-input-section input {
    width: 100px;
    padding: 6px;
    font-size: 16px;
  }
  
  .weight-history-section {
    margin-top: 20px;
  }
  
  .training-item {
    background-color: #f9f9f9;
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 10px;
  }
  
  .training-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .training-header h3 {
    margin: 0;
    color: #333;
  }
  
  .training-date {
    color: #1976d2;
    font-weight: bold;
  }
  
  .no-data {
    text-align: center;
    color: #666;
    padding: 30px;
    background-color: #f5f5f5;
    border-radius: 8px;
  }
  
  .button-group {
    display: flex;
    justify-content: center;
    margin-top: 30px;
  }
  
  .primary-btn {
    background-color: #4caf50;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 16px;
    font-weight: bold;
  }
  
  .primary-btn:hover {
    background-color: #45a049;
  }
  
  .secondary-btn {
    background-color: #757575;
    color: white;
    border: none;
    padding: 12px 24px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 16px;
  }
  
  .secondary-btn:hover {
    background-color: #616161;
  }
  </style>
  