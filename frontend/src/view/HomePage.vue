<template>
  <div class="home-container">
    <h1>ホーム画面</h1>
    <p>ようこそ、{{ username }}さん！</p>

    <div v-if="loading">
      <p>トレーニング履歴を読み込み中...</p>
    </div>

    <div v-else-if="error">
      <p class="error-message">{{ error }}</p>
    </div>

    <div v-else>
      <h2>本日行うトレーニング</h2>
      <div v-if="trainingHistory.length === 0" class="no-data">
        <p>まだ本日のトレーニングメニューはありません</p>
        <p>トレーニング提案画面で本日のトレーニングを決めましょう！</p>
      </div>
      <div v-else class="training-history">
        <div v-for="training in sortedTrainingHistory" :key="`${training.trainingId}-${training.trainingDate}`"
          class="training-item" :class="{ 'completed-training': training.trainedFlag === 1 }">
          <div class="training-header">
            <div class="training-title-section">
              <input type="checkbox" :checked="training.trainedFlag === 1"
                @change="updateTrainingStatus(training, $event)" :disabled="updatingStatus" class="training-checkbox" />
              <h3>{{ training.trainingName }}</h3>
            </div>
            <span class="training-date">{{ formatDate(training.trainingDate) }}</span>
          </div>

          <div class="training-details">
            <p><strong>回数:</strong> {{ training.trainingCount }}回</p>
            <p><strong>消費カロリー:</strong> {{ training.caloriesConsumed.toFixed(1) }}kcal</p>
            <span class="status" :class="training.trainedFlag === 1 ? 'completed' : 'pending'">
              {{ training.trainedFlag === 1 ? '実施済み' : '実施前' }}
            </span>
          </div>

          <!-- 削除ボタン -->
          <div class="training-actions">
            <button @click="goEdit(training)" class="delete-btn">編集</button>
            <button @click="deleteTraining(training)" class="delete-btn">
              削除
            </button>
          </div>
        </div>
      </div>

      <div class="summary">
        <h3>本日行ったトレーニング</h3>
        <p>総実施回数: {{ totalTrainings }}回</p>
        <p>総消費カロリー: {{ totalCalories.toFixed(1) }}kcal</p>
      </div>
    </div>

    <div class="button-group">
      <button @click="goToInput" class="primary-btn">トレーニング内容提案</button>
      <button @click="goToHistory" class="primary-btn">トレーニング履歴</button>
      <button v-if="admin === 1" @click="goToCreateTraining" class="primary-btn">新規トレーニング追加</button>
      <button @click="goToEditUser" class="primary-btn">ユーザー情報更新</button>
      <button @click="logout" class="secondary-btn">ログアウト</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const username = ref('')
const userId = sessionStorage.getItem('userId')
const userWeight = ref(null)
const admin = ref(null)
const trainingHistory = ref([])
const loading = ref(false)
const error = ref('')
const updatingStatus = ref(false)
const deleting = ref(false)

// 合計統計
const totalTrainings = computed(() =>
  trainingHistory.value
    .filter(t => t.trainedFlag === 1)
    .reduce((total, t) => total + (t.trainingCount || 0), 0))

const totalCalories = computed(() =>
  trainingHistory.value
    .filter(t => t.trainedFlag === 1)
    .reduce((total, t) => total + (t.caloriesConsumed || 0), 0)
)

// ソート済みトレーニング
const sortedTrainingHistory = computed(() => {
  return [...trainingHistory.value].sort((a, b) => {
    if (a.trainedFlag !== b.trainedFlag) return a.trainedFlag - b.trainedFlag
    return new Date(b.trainingDate) - new Date(a.trainingDate)
  })
})

// 日付フォーマット
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('ja-JP', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 今日の日付
const getToday = () => {
  const today = new Date()
  const yyyy = today.getFullYear()
  const mm = String(today.getMonth() + 1).padStart(2, '0')
  const dd = String(today.getDate()).padStart(2, '0')
  return `${yyyy}-${mm}-${dd}`
}

// トレーニング履歴取得
const fetchTrainingHistory = async () => {
  loading.value = true
  error.value = ''
  try {
    if (!userId) {
      error.value = 'ログイン情報が見つかりません。再ログインしてください。'
      return
    }

    const today = getToday()
    const response = await axios.get(`/api/training-history/user/${userId}/date/${today}`)
    trainingHistory.value = response.data.trainings || []
  } catch (err) {
    console.error('トレーニング履歴取得エラー:', err)
    error.value = 'トレーニング履歴の取得に失敗しました'
    trainingHistory.value = []
  } finally {
    loading.value = false
  }
}

// ページ読み込み時
onMounted(async () => {
  username.value = sessionStorage.getItem('username') || 'ゲスト'
  admin.value = Number(sessionStorage.getItem('admin'))

  if (!userId) {
    error.value = 'ログイン情報が見つかりません。'
    return
  }

  await fetchTrainingHistory()
})

// トレーニング内容入力画面へ遷移
const goToInput = () => router.push('/training-proposal-input')

// トレーニング履歴画面へ遷移
const goToHistory = () => router.push('/training-history')

// 新規トレーニング作成画面へ遷移
const goToCreateTraining = () => router.push('/create-training')

// ユーザー情報更新画面へ遷移
const goToEditUser = () => router.push('/edit-user')

// トレーニング編集画面へ遷移
const goEdit = (training) =>{
  router.push({
    path: '/edit-training',
    query: {
      trainingId: training.trainingId,
      date: training.trainingDate
    }
  })
} 

// ステータス更新
const updateTrainingStatus = async (training, event) => {
  updatingStatus.value = true
  try {
    const newStatus = event.target.checked ? 1 : 0
    const response = await axios.put('/api/training-history/update-status', {
      trainingId: training.trainingId,
      userId: userId,
      trainingDate: training.trainingDate,
      trainedFlag: newStatus
    })
    if (response.data.success) {
      const index = trainingHistory.value.findIndex(
        t => t.trainingId === training.trainingId && t.trainingDate === training.trainingDate
      )
      if (index !== -1) trainingHistory.value[index].trainedFlag = newStatus
      await nextTick()
    } else {
      event.target.checked = !event.target.checked
    }
  } catch (err) {
    console.error('ステータス更新エラー:', err)
    event.target.checked = !event.target.checked
  } finally {
    updatingStatus.value = false
  }
}

// トレーニング削除
const deleteTraining = async (training) => {
  if (deleting.value) return
  const confirmed = window.confirm(`「${training.trainingName}」を削除しますか？`)
  if (!confirmed) return

  deleting.value = true
  try {
    const response = await axios.delete(`/api/training-history/delete`, {
      data: {
        trainingId: training.trainingId,
        userId: userId,
        trainingDate: training.trainingDate
      }
    })

    if (response.data.success) {
      alert('削除に成功しました')
      trainingHistory.value = trainingHistory.value.filter(
        t => !(t.trainingId === training.trainingId && t.trainingDate === training.trainingDate)
      )
    } else {
      alert('削除に失敗しました。')
    }
  } catch (err) {
    console.error('削除エラー:', err)
    alert('サーバーエラーにより削除できませんでした。')
  } finally {
    deleting.value = false
  }
}

// ログアウト
const logout = () => {
  sessionStorage.removeItem('username')
  sessionStorage.removeItem('userId')
  sessionStorage.removeItem('admin')
  sessionStorage.removeItem('weight')
  router.push('/')
}
</script>

<style>
/* 既存の CSS はそのまま */
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

.no-data {
  text-align: center;
  color: #666;
  padding: 40px 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin: 20px 0;
}

.training-history {
  margin: 20px 0;
}

.training-item {
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.training-item.completed-training {
  background-color: #f0f0f0;
  opacity: 0.7;
  color: #666;
}

.training-item.completed-training h3 {
  color: #999;
  text-decoration: line-through;
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.training-title-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.training-checkbox {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.training-checkbox:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.training-header h3 {
  margin: 0;
  color: #333;
}

.training-date {
  color: #666;
  font-size: 14px;
}

.training-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.training-details p {
  margin: 5px 0;
  color: #666;
}

.training-actions {
  text-align: right;
  margin-top: 10px;
}

.delete-btn {
  background-color: #e53935;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  width: auto;
  display: inline-block;
}

.delete-btn:hover {
  background-color: #c62828;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.status.completed {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status.pending {
  background-color: #fff3e0;
  color: #f57c00;
}

.summary {
  background-color: #e3f2fd;
  border: 1px solid #bbdefb;
  border-radius: 8px;
  padding: 20px;
  margin: 20px 0;
  text-align: center;
}

.summary h3 {
  margin: 0 0 15px 0;
  color: #1976d2;
}

.summary p {
  margin: 8px 0;
  font-size: 16px;
  color: #333;
}

.button-group {
  display: flex;
  gap: 15px;
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

.training-actions .delete-btn {
  background-color: #e53935;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  width: auto;
  display: inline-block;
  margin-left: 10px; /* ← ボタン間の間隔 */
}

.delete-btn:first-child {
  margin-left: 0; /* 最初のボタンは左マージン不要 */
}

</style>
