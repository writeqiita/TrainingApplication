<template>
    <div class="create-training-page">
      <h2>新規トレーニング追加</h2>
      <form @submit.prevent="handleCreateTraining" class="training-form">
        <div class="input-group">
          <label>トレーニング名</label>
          <input v-model="trainingName" required />
        </div>
        <div class="input-group">
          <label>鍛えられる部位</label>
          <select v-model.number="trainingPart" required>
            <option value="">選択してください</option>
            <option :value="0">腕</option>
            <option :value="1">脚</option>
            <option :value="2">腹筋</option>
            <option :value="3">全身</option>
          </select>
        </div>
        <div class="input-group">
          <label>運動強度 (METS)</label>
          <input type="number" v-model.number="mets" step="0.1" min="1" required />
        </div>
        <div class="input-group">
          <label>1分間の実施回数</label>
          <input type="number" v-model.number="pace" min="1" required />
        </div>
  
        <div class="button-group">
          <button type="submit" class="btn-create">追加</button>
          <button type="button" class="btn-home" @click="goHome">ホームに戻る</button>
        </div>
      </form>
  
      <h3>登録済みトレーニング一覧</h3>
      <table class="training-table">
        <thead>
          <tr>
            <th>トレーニング名</th>
            <th>部位</th>
            <th>運動強度(METS)</th>
            <th>1分あたりの実施回数</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="training in trainings" :key="training.trainingId">
            <td>{{ training.trainingName }}</td>
            <td>{{ partMap[training.trainingPart] || training.trainingPart }}</td>
            <td>{{ training.mets.toFixed(1) }}</td>
            <td>{{ training.trainingPace }}</td>
          </tr>
        </tbody>
      </table>
  
      <!-- モーダル通知 -->
      <div v-if="modalMessage" class="modal-overlay">
        <div class="modal">
          <p>{{ modalMessage }}</p>
          <button @click="closeModal">OK</button>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import axios from 'axios'
  
  const router = useRouter()
  const trainingName = ref('')
  const trainingPart = ref('')
  const mets = ref(0)
  const pace = ref(0)
  const trainings = ref([])
  
  // モーダル用
  const modalMessage = ref('')
  
  // 部位番号 → 文字列マッピング
  const partMap = {0: '腕', 1: '脚', 2: '腹筋', 3: '全身'}
  
  const fetchTrainings = async () => {
    try {
      const res = await axios.get('/api/training/all')
      trainings.value = res.data
    } catch (err) {
      console.error('トレーニング一覧取得エラー', err)
    }
  }
  
  const handleCreateTraining = async () => {
    try {
      if(!trainingName.value || trainingPart.value === '' || mets.value < 1 || pace.value < 1) {
        modalMessage.value = '全ての項目を正しく入力してください'
        return
      }
      await axios.post('/api/training', {
        trainingName: trainingName.value,
        trainingPart: trainingPart.value,
        mets: mets.value,
        trainingPace: pace.value,
        createUser: sessionStorage.getItem('username')
      })
      modalMessage.value = 'トレーニングを追加しました'
      trainingName.value = ''
      trainingPart.value = ''
      mets.value = 0
      pace.value = 0
      await fetchTrainings()
    } catch (err) {
      console.error(err)
      modalMessage.value = 'トレーニング追加に失敗しました'
    }
  }
  
  const closeModal = () => {
    modalMessage.value = ''
  }
  
  const goHome = () => router.push('/home')
  
  onMounted(() => {
    const admin = Number(sessionStorage.getItem('admin'))
      if (admin !== 1) {
        alert('このページは管理者専用です')
        router.replace('/home')
        return
      }
    fetchTrainings()
  })
  </script>
  
  <style>
  .create-training-page {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
    text-align: center;
  }
  
  .training-form {
    margin-bottom: 30px;
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
  
  .input-group input,
  .input-group select {
    width: 100%;
    padding: 8px;
    border-radius: 6px;
    border: 1px solid #ccc;
    box-sizing: border-box;
  }
  
  .button-group {
    display: flex;
    gap: 10px;
    justify-content: center;
    margin-top: 10px;
  }
  
  button {
    padding: 10px 20px;
    font-weight: bold;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: 0.3s;
    color: white;
    font-size: 16px;
  }
  
  .btn-create {
    background-color: #4caf50;
  }
  
  .btn-create:hover {
    background-color: #45a049;
    transform: scale(1.03);
  }
  
  .btn-home {
    background-color: #9e9e9e;
  }
  
  .btn-home:hover {
    background-color: #757575;
    transform: scale(1.03);
  }
  
  .training-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 20px;
    text-align: left;
  }
  
  .training-table th,
  .training-table td {
    padding: 8px;
    border-bottom: 1px solid #ddd;
  }
  
  .training-table th {
    background-color: #f2f2f2;
  }
  
  /* モーダル用スタイル */
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9999;
  }
  
  .modal {
    background-color: white;
    padding: 20px 30px;
    border-radius: 12px;
    text-align: center;
    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  }
  
  .modal button {
    margin-top: 15px;
    padding: 8px 20px;
    background-color: #2196f3;
    border: none;
    border-radius: 8px;
    color: white;
    cursor: pointer;
  }
  
  .modal button:hover {
    background-color: #1976d2;
  }
  </style>
  