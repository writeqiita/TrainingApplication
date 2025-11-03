import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../view/LoginPage.vue' //　ログイン画面
import CreateUserPage from '../view/CreateUserPage.vue'  // 新規登録画面
import HomePage from '../view/HomePage.vue' // ホーム画面
import TrainingProposalInputPage from '../view/TrainingProposalInputPage.vue' // トレーニング提案画面
import TrainingProposalResultPage from '../view/TrainingProposalResultPage.vue' // トレーニング提案結果画面
import TrainingHistory from '../view/TrainingHistoryPage.vue' // トレーニング履歴画面
import CreateTrainingPage from '../view/CreateTrainingPage.vue' // トレーニング追加画面
import EditTrainingPage from '../view/EditTrainingPage.vue' // トレーニング編集画面
import EditUserPage from '../view/EditUserPage.vue' // ユーザー情報更新画面

const routes = [
    {
        // ログイン画面
        path: '/',
        name: 'Login',
        component: LoginPage
    },
    {
        // 新規ユーザー登録画面
        path: '/register',
        name: 'Register',
        component: CreateUserPage
    },
    {
        // ホーム画面
        path: '/home',
        name: 'Home',
        component: HomePage
    },
    {
        // トレーニング提案入力画面
        path: '/training-proposal-input',
        name: 'TrainingProposalInput',
        component: TrainingProposalInputPage
    },
    {
        // トレーニング提案結果画面
        path: '/training-proposal-result',
        name: 'TrainingProposalResult',
        component: TrainingProposalResultPage
    },
    {
        // トレーニング履歴画面
        path: '/training-history',
        name: 'TrainingHistory',
        component: TrainingHistory
    },
    {
        // トレーニング追加画面
        path: '/create-training',
        name: 'CreateTraining',
        component: CreateTrainingPage
    },
    {
        // トレーニング編集画面
        path: '/edit-training',
        name: 'EditTraining',
        component: EditTrainingPage
    },
    {
        // ユーザー情報更新画面
        path: '/edit-user',
        name: 'EditUser',
        component: EditUserPage
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router