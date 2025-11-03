<template>
    <div class="proposal-input-container">
        <h1>トレーニング提案画面</h1>

        <form @submit.prevent="handleSubmit">
            <div>
                <h3>鍛えたい部位を選んでください（任意・複数選択可）:</h3>
                <div class="body-parts-container">
                    <div v-for="part in bodyParts" :key="part" class="body-part-item">
                        <input type="checkbox" :value="part" v-model="selectedParts" :id="`part-${part}`" />
                        <label :for="`part-${part}`">{{ part }}</label>
                    </div>
                </div>
                <p class="optional-note">※ 部位を指定しない場合は、ランダムでトレーニングが提案されます</p>
            </div>

            <div>
                <h3>消費したいカロリーを教えてください</h3>
                <p class="optional-note">※ 1~1000kcalの範囲内で入力してください</p>
                <p class="optional-note">※ 体重60kgの場合 腕立て伏せ40回で消費カロリー約10kcalです</p>
                <input type="number" v-model.number="targetCalories" min="1" required /> kcal
                
            </div>

            <div class="button-group">
                <button type="submit" class="primary-btn">トレーニング作成</button>
                <button type="button" @click="goHome" class="secondary-btn">ホームに戻る</button>
            </div>
        </form>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const bodyParts = ['腕', '脚', '腹筋', '全身']
const selectedParts = ref([])
const targetCalories = ref()
const userId = sessionStorage.getItem('userId')

onMounted(() => {
    if (!userId) {
        alert('ログイン情報が見つかりません。再ログインしてください。')
        router.push('/')
    }
})

const handleSubmit = () => {
    if (!userId) return

    if (targetCalories.value < 1 || targetCalories.value > 1000) {
        alert('カロリーは1〜1000の範囲で入力してください。')
        return
    }
    const parts = selectedParts.value.length > 0 ? selectedParts.value : []

    router.push({
        path: '/training-proposal-result',
        query: {
            userId: userId.toString(),
            parts: parts.join(','),
            calories: targetCalories.value.toString()
        }
    })
}

const goHome = () => router.push('/home')
</script>

<style>
.proposal-input-container {
    max-width: 600px;
    margin: 0 auto;
    padding: 20px;
}

form div {
    margin-bottom: 20px;
}

.body-parts-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 10px;
    margin: 15px 0;
}

.body-part-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 6px;
    background-color: #f9f9f9;
    transition: background-color 0.2s;
}

.body-part-item:hover {
    background-color: #f0f0f0;
}

.body-part-item input[type="checkbox"] {
    margin: 0;
    transform: scale(1.2);
}

.body-part-item label {
    margin: 0;
    cursor: pointer;
    font-weight: 500;
}

.optional-note {
    font-size: 14px;
    color: #666;
    font-style: italic;
    margin-top: 10px;
}

input[type="number"] {
    width: 120px;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
    margin-left: 10px;
}

button[type="submit"]:hover {
    background-color: #45a049;
}

.button-group {
    display: flex;
    gap: 15px;
    justify-content: flex-start;
    margin-top: 20px;
}
</style>
