<template>
    <div class="proposal-result-container">
      <h1>トレーニング提案内容</h1>
  
      <div v-if="loading">
        <p>トレーニング提案を生成中...</p>
      </div>
  
      <div v-else-if="error">
        <p class="error-message">{{ error }}</p>
      </div>
  
      <div v-else-if="trainingProposals.length > 0">
        <h2>提案されたトレーニング</h2>
        <div class="training-list">
          <div v-for="(training, index) in trainingProposals" :key="index" class="training-item">
            <h3>{{ training.trainingName }}</h3>
            <p><strong>回数:</strong> {{ training.reps }}回</p>
            <p><strong>消費カロリー:</strong> {{ training.calories.toFixed(1) }}kcal</p>
          </div>
        </div>
  
        <div class="total-calories">
          <h3>合計消費カロリー: {{ totalCalories.toFixed(1) }}kcal</h3>
        </div>
      </div>
  
      <div v-else>
        <p>トレーニング提案が見つかりませんでした。</p>
      </div>
  
      <div class="button-group">
        <button @click="approveTraining" class="approve-btn" :disabled="isApproving">
          {{ isApproving ? '登録中...' : '承認' }}
        </button>
  
        <button @click="requestNewProposal" class="repropose-btn" :disabled="isApproving">
          再提案
        </button>
  
        <button @click="goBack" class="back-btn" :disabled="isApproving">
          戻る
        </button>
      </div>
  
      <!-- 成功メッセージ用モーダル -->
      <div v-if="successModalMessage" class="modal-overlay">
        <div class="modal">
          <p>{{ successModalMessage }}</p>
          <button class="approve-btn" @click="goHomeFromModal">OK</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted, nextTick } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  const router = useRouter()
  
  const trainingProposals = ref([])
  const loading = ref(false)
  const error = ref('')
  const isApproving = ref(false)
  const successModalMessage = ref('')
  const userId = sessionStorage.getItem('userId')

  // 合計カロリー
  const totalCalories = computed(() => trainingProposals.value.reduce((sum, t) => sum + t.calories, 0))
  
  const fetchTrainingProposals = async (parts, calories) => {
    loading.value = true
    error.value = ''

    try {
      const res = await axios.post('/api/training/propose', { userId, parts, calories })
      if (res.data && res.data.trainings) trainingProposals.value = res.data.trainings
      else error.value = 'トレーニング提案の取得に失敗しました'
    } catch (err) {
      console.error(err)
      error.value = 'トレーニング提案取得中にエラーが発生しました'
    } finally {
      loading.value = false
    }
  }
  
  onMounted(() => {
    const query = router.currentRoute.value.query
    const parts = query.parts ? query.parts.split(',').filter(p => p.trim() !== '') : []
    const calories = parseInt(query.calories) || 300
    fetchTrainingProposals(parts, calories)
  })
  
  const approveTraining = async () => {
    if (isApproving.value) return
    isApproving.value = true
    await nextTick()
    error.value = ''
    try {
      if (!userId) {
        error.value = 'ログイン情報が取得できません'
        isApproving.value = false
        return
      }
  
      const res = await axios.post('/api/training-history/register', {
        userId,
        trainings: trainingProposals.value
      })
  
      if (res.data.success) {
        successModalMessage.value = res.data.message || 'トレーニングを登録しました'
      } else {
        error.value = res.data.message || '登録に失敗しました'
      }
    } catch (err) {
      console.error(err)
      error.value = 'トレーニング履歴の登録に失敗しました'
    } finally {
      isApproving.value = false
    }
  }
  
  // モーダルのOKボタンでホームへ
  const goHomeFromModal = () => {
    successModalMessage.value = ''
    router.push('/home')
  }
  
  const requestNewProposal = () => {
    error.value = ''
    const query = router.currentRoute.value.query
    const parts = query.parts ? query.parts.split(',').filter(p => p.trim() !== '') : []
    const calories = parseInt(query.calories) || 10
    fetchTrainingProposals(parts, calories)
  }
  
  const goBack = () => router.push('/training-proposal-input')
  </script>
  
  <style>
  .proposal-result-container {
      max-width: 800px;
      margin: 0 auto;
      padding: 20px;
  }
  
  .training-list {
      margin: 20px 0;
  }
  
  .training-item {
      background-color: #f5f5f5;
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 15px;
      margin-bottom: 15px;
  }
  
  .training-item h3 {
      margin: 0 0 10px 0;
      color: #333;
  }
  
  .training-item p {
      margin: 5px 0;
      color: #666;
  }
  
  .total-calories {
      background-color: #e8f5e8;
      border: 2px solid #4caf50;
      border-radius: 8px;
      padding: 15px;
      margin: 20px 0;
      text-align: center;
  }
  
  .total-calories h3 {
      margin: 0;
      color: #2e7d32;
  }
  
  .button-group {
      display: flex;
      gap: 15px;
      justify-content: center;
      margin-top: 30px;
  }
  
  .approve-btn {
      background-color: #4caf50;
      color: white;
      border: none;
      padding: 12px 24px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 16px;
      font-weight: bold;
  }
  
  .approve-btn:hover {
      background-color: #45a049;
  }
  
  .repropose-btn {
      background-color: #ff9800;
      color: white;
      border: none;
      padding: 12px 24px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 16px;
  }
  
  .repropose-btn:hover {
      background-color: #e68900;
  }
  
  .back-btn {
      background-color: #757575;
      color: white;
      border: none;
      padding: 12px 24px;
      border-radius: 6px;
      cursor: pointer;
      font-size: 16px;
  }
  
  .back-btn:hover {
      background-color: #616161;
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
  
  button:disabled {
      opacity: 0.6;
      cursor: not-allowed;
  }
  
  .modal-overlay {
    position: fixed;
    top:0; left:0;
    width:100%; height:100%;
    background-color: rgba(0,0,0,0.3);
    display:flex;
    align-items:center;
    justify-content:center;
    z-index:9999;
  }
  .modal {
    background-color:white;
    padding:20px 30px;
    border-radius:12px;
    text-align:center;
    box-shadow:0 4px 12px rgba(0,0,0,0.3);
  }
  .modal button {
    margin-top:15px;
    padding:12px 24px; /* 元ボタンサイズと同じ */
    background-color:#4caf50; /* 承認ボタン色 */
    border:none;
    border-radius:6px;
    color:white;
    cursor:pointer;
    font-size:16px;
    font-weight:bold;
  }
  .modal button:hover { background-color:#45a049; }
  </style>
  