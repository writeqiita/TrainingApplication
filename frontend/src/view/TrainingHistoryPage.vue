<template>
  <div class="history-container">
    <h1>トレーニング履歴</h1>

    <div v-if="loading">
      <p>トレーニング履歴を読み込み中...</p>
    </div>

    <div v-else-if="error">
      <p class="error-message">{{ error }}</p>
    </div>

    <div v-else>
      <div v-if="Object.keys(groupedHistory).length === 0" class="no-data">
        <p>トレーニング履歴がありません</p>
      </div>

      <div v-else class="training-history">
        <!-- 全体の合計 -->
        <div class="summary">
          <h3>全体の合計</h3>
          <p>トレーニング実施回数: {{ totalTrainings }}回</p>
          <p>消費カロリー: {{ totalCalories.toFixed(1) }}kcal</p>
        </div>

        <!-- 日付ごとの履歴 -->
        <div
          v-for="(trainings, date) in groupedHistory"
          :key="date"
          class="training-day-group"
        >
          <h2 class="date-header">{{ date }}</h2>

          <!-- 日付ごとの合計 -->
          <div class="daily-summary">
            <p>トレーニング実施回数: {{ dailyReps[date] }}回</p>
            <p>消費カロリー: {{ dailyCalories[date].toFixed(1) }}kcal</p>
          </div>

          <!-- 各トレーニング -->
          <div
            v-for="training in trainings"
            :key="`${training.trainingId}-${training.trainingDate}`"
            class="training-item"
          >
            <div class="training-header">
              <h3>{{ training.trainingName }}</h3>
              <span class="status" :class="training.trainedFlag === 1 ? 'completed' : 'pending'">
                {{ training.trainedFlag === 1 ? '実施済み' : '未実施' }}
              </span>
            </div>

            <div class="training-details">
              <p><strong>回数:</strong> {{ training.trainingCount }}回</p>
              <p><strong>消費カロリー:</strong> {{ training.caloriesConsumed.toFixed(1) }}kcal</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="button-group">
      <button @click="goBack" class="primary-btn">ホームに戻る</button>
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
const trainingHistory = ref([])
const loading = ref(false)
const error = ref('')

// 総実施回数
const totalTrainings = computed(() =>
  trainingHistory.value
    .filter(t => t.trainedFlag === 1)
    .reduce((sum, t) => sum + (t.trainingCount || 0), 0)
)

// 総消費カロリー
const totalCalories = computed(() =>
  trainingHistory.value
    .filter(t => t.trainedFlag === 1)
    .reduce((sum, t) => sum + (t.caloriesConsumed || 0), 0)
)

// 日付ごとにグループ化
const groupedHistory = computed(() => {
  const groups = {}
  trainingHistory.value.forEach(t => {
    const dateKey = formatDate(t.trainingDate)
    if (!groups[dateKey]) groups[dateKey] = []
    groups[dateKey].push(t)
  })
  return groups
})

// 各日ごとの合計カロリー
const dailyCalories = computed(() => {
  const totals = {}
  for (const [date, trainings] of Object.entries(groupedHistory.value)) {
    totals[date] = trainings
      .filter(t => t.trainedFlag === 1)
      .reduce((sum, t) => sum + (t.caloriesConsumed || 0), 0)
  }
  return totals
})

// 各日ごとの実施回数
const dailyReps = computed(() => {
  const reps = {}
  for (const [date, trainings] of Object.entries(groupedHistory.value)) {
    reps[date] = trainings
      .filter(t => t.trainedFlag === 1)
      .reduce((sum, t) => sum + (t.trainingCount || 0), 0)
  }
  return reps
})

// 日付フォーマット
const formatDate = dateStr => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('ja-JP', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// データ取得
const fetchTrainingHistory = async () => {
  loading.value = true
  error.value = ''
  try {
    if (!userId) {
      error.value = 'ログイン情報が見つかりません。再ログインしてください。'
      return
    }
    const response = await axios.get(`/api/training-history/user/${userId}/all`)
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
onMounted(() => {
  username.value = sessionStorage.getItem('username') || 'ゲスト'
  fetchTrainingHistory()
})

const goBack = () => router.back()
</script>

<style>
.history-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.date-header {
  margin-top: 30px;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 2px solid #4caf50;
  padding-bottom: 5px;
}

.training-item {
  background-color: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-top: 10px;
}

.training-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.training-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.daily-summary {
  text-align: right;
  margin-top: 10px;
  font-weight: bold;
  color: #2e7d32;
}

.summary {
  background-color: #e3f2fd;
  border: 1px solid #bbdefb;
  border-radius: 8px;
  padding: 20px;
  margin-top: 30px;
  text-align: center;
}

.status.completed {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.status.pending {
  background-color: #fff3e0;
  color: #f57c00;
}
</style>
