<template>
    <div class="edit-training-page">
        <h2>トレーニング変更</h2>

        <form @submit.prevent="handleUpdateTraining" class="training-form">
            <!-- トレーニング選択 -->
            <div class="input-group">
                <label>トレーニング名</label>
                <select v-model.number="selectedTrainingId" @change="updateMetsAndPace" required>
                    <option disabled value="">選択してください</option>
                    <option v-for="t in allTrainings" :key="t.trainingId" :value="t.trainingId">
                        {{ t.trainingName }}
                    </option>
                </select>
            </div>

            <!-- 回数 -->
            <div class="input-group">
                <label>実施回数</label>
                <input type="number" v-model.number="trainingCount" min="1" required />
            </div>

            <!-- 消費カロリー -->
            <div class="input-group">
                <label>消費カロリー</label>
                <input type="number" :value="calculatedCalories.toFixed(1)" readonly />
            </div>

            <div class="button-group">
                <button type="submit" class="btn-create">変更</button>
                <button type="button" class="btn-home" @click="goHome">ホームに戻る</button>
            </div>
        </form>

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
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

// URLパラメータから日付とトレーニングIDを取得
const trainingDate = ref(route.query.date || '')
const oldTrainingId = ref(Number(route.query.trainingId) || 0)

// セッションから取得
const userId = sessionStorage.getItem('userId')
const weight = sessionStorage.getItem('weight') || 60

// データ
const allTrainings = ref([])
const selectedTrainingId = ref(oldTrainingId.value)
const trainingCount = ref(1)
const mets = ref(0)
const pace = ref(0)
const modalMessage = ref('')

// 消費カロリー計算
const calculatedCalories = computed(() => {
    if (!pace.value || !mets.value) return 0
    const hours = trainingCount.value / pace.value / 60
    return mets.value * weight * hours
})

// トレーニング一覧取得
const fetchAllTrainings = async () => {
    try {
        const res = await axios.get('/api/training/all')
        allTrainings.value = res.data
    } catch (err) {
        console.error('トレーニング一覧取得エラー', err)
    }
}

// トレーニング履歴取得
const fetchTrainingHistory = async () => {
    if (!trainingDate.value || !oldTrainingId.value) {
        modalMessage.value = '履歴取得に必要な情報が不足しています'
        return
    }
    try {
        const res = await axios.get(`/api/training-history/user/${userId}/date/${trainingDate.value}/training/${oldTrainingId.value}`)
        const history = res.data

        if (!history) {
            modalMessage.value = '該当のトレーニング履歴が見つかりません'
            return
        }
        trainingCount.value = history.trainingCount
        selectedTrainingId.value = history.trainingId
        updateMetsAndPace()
    } catch (err) {
        console.error('履歴取得エラー', err)
        modalMessage.value = '履歴取得に失敗しました'
    }
}

// METS と pace 更新
const updateMetsAndPace = () => {
    const selected = allTrainings.value.find(t => t.trainingId === selectedTrainingId.value)
    if (selected) {
        mets.value = selected.mets
        pace.value = selected.trainingPace
    }
}

// トレーニング履歴更新処理
const handleUpdateTraining = async () => {
    if (trainingCount.value < 1 || trainingCount.value > 100000) {
        alert('回数は1〜100000の範囲で入力してください。')
        return
    }
    try {
        await axios.put('/api/training-history/update', {
            userId,
            oldTrainingId: oldTrainingId.value,
            newTrainingId: selectedTrainingId.value,
            trainingDate: trainingDate.value,
            count: trainingCount.value,
            calories: calculatedCalories.value
        })
        modalMessage.value = 'トレーニング履歴を更新しました'
    } catch (err) {
        console.error(err)
        modalMessage.value = '更新に失敗しました'
    }
}

const closeModal = () => {
    modalMessage.value = ''
    router.push('/home')
}

const goHome = () => router.push('/home')

onMounted(async () => {
    await fetchAllTrainings()
    await fetchTrainingHistory()
})
</script>



<style>
.edit-training-page {
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

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.3);
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
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
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