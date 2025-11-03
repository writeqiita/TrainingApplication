package com.example.demo.service
import com.example.demo.entity.TrainingHistory
import com.example.demo.entity.TrainingHistoryId
import com.example.demo.repository.TrainingHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import jakarta.transaction.Transaction
import com.example.demo.dto.training.*

@Service
class TrainingHistoryService(
    private val trainingHistoryRepository: TrainingHistoryRepository
) {

    /** トレーニング履歴登録（ユーザーID + トレーニング内容） */
    @Transactional
    fun registerTrainingHistory(userId: Int, trainings: List<TrainingItem>) {
        val currentTime = LocalDateTime.now()

        trainings.forEach { training ->
            val trainingHistory = TrainingHistory(
                id = TrainingHistoryId(
                    trainingId = training.trainingId,
                    userId = userId,
                    trainingDate = currentTime
                ),
                trainedFlag = 0, // 未実施
                trainingCount = training.reps,
                caloriesConsumed = training.calories,
                createTime = currentTime,
                createUser = userId.toString(),
                updateTime = currentTime,
                updateUser = userId.toString()
            )

            trainingHistoryRepository.save(trainingHistory)
        }
    }

    /** 特定ユーザーの当日トレーニング履歴を取得 */
    fun getUserTrainingHistoryByDate(userId: Int, date: LocalDate): List<TrainingHistoryItem> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()

        val histories = trainingHistoryRepository.findByIdUserIdAndIdTrainingDateBetween(
            userId, startOfDay, endOfDay
        )

        return histories.map { history ->
            TrainingHistoryItem(
                trainingId = history.id.trainingId,
                trainingName = history.training?.trainingName ?: "不明なトレーニング",
                trainingDate = history.id.trainingDate.toString(),
                trainedFlag = history.trainedFlag,
                trainingCount = history.trainingCount ?: 0,
                caloriesConsumed = history.caloriesConsumed ?: 0.0
            )
        }.sortedByDescending { it.trainingDate }
    }

    // 特定ユーザーの全トレーニング履歴（実施済み）を取得
    fun getAllByUserId(userId: Int): List<TrainingHistoryItem> {
        val histories = trainingHistoryRepository.findByIdUserIdAndTrainedFlag(userId, 1) // 全履歴取得
        return histories.map { history ->
            TrainingHistoryItem(
                trainingId = history.id.trainingId,
                trainingName = history.training?.trainingName ?: "不明なトレーニング",
                trainingDate = history.id.trainingDate.toString(),
                trainedFlag = history.trainedFlag,
                trainingCount = history.trainingCount ?: 0,
                caloriesConsumed = history.caloriesConsumed ?: 0.0
            )
        }.sortedByDescending { it.trainingDate } // 日付降順
    }


    /** トレーニングの実施状態を更新 */
    @Transactional
    fun updateTrainingStatus(trainingId: Int, userId: Int, trainingDate: String, trainedFlag: Int) {
        val trainingDateObj = LocalDateTime.parse(trainingDate)
        val trainingHistoryId = TrainingHistoryId(trainingId, userId, trainingDateObj)

        val existingHistory = trainingHistoryRepository.findById(trainingHistoryId)
        if (existingHistory.isPresent) {
            val history = existingHistory.get()
            val updatedHistory = history.copy(
                trainedFlag = trainedFlag,
                updateTime = LocalDateTime.now(),
                updateUser = userId.toString()
            )
            trainingHistoryRepository.save(updatedHistory)
        } else {
            throw IllegalArgumentException("指定されたトレーニング履歴が見つかりません")
        }
    }
    /** トレーニング履歴を削除 */
    @Transactional
    fun deleteTraining(trainingId: Int, userId: Int, trainingDate: String): Boolean {
        val trainingDateObj = try {
            LocalDateTime.parse(trainingDate)
        } catch (e: Exception) {
            throw IllegalArgumentException("trainingDate の形式が不正です: ${e.message}")
        }

        val id = TrainingHistoryId(trainingId = trainingId, userId = userId, trainingDate = trainingDateObj)

        return if (trainingHistoryRepository.existsById(id)) {
            trainingHistoryRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @Transactional
    fun updateTrainingHistory(
        userId: Int,
        oldTrainingId: Int,
        trainingDate: String,
        newTrainingId: Int,
        count: Int,
        calories: Double
    ) {
        val trainingDateObj = LocalDateTime.parse(trainingDate)

        // 対象の履歴を取得
        val historyId = TrainingHistoryId(
            trainingId = oldTrainingId,
            userId = userId,
            trainingDate = trainingDateObj
        )
        val history = trainingHistoryRepository.findById(historyId)
            .orElseThrow { IllegalArgumentException("指定されたトレーニング履歴が見つかりません") }

        // 元々の行を削除
        trainingHistoryRepository.delete(history)
        
        // トレーニングID、回数、カロリーを更新
        val updatedHistory = history.copy(
            id = history.id.copy(trainingId = newTrainingId),
            trainingCount = count,
            caloriesConsumed = calories,
            updateTime = LocalDateTime.now(),
            updateUser = userId.toString()
        )

        trainingHistoryRepository.save(updatedHistory)
    }

    @Transactional
    fun findByUserDateTraining(userId: Int, date: LocalDateTime, trainingId: Int): TrainingHistoryItem? {
        val entity = trainingHistoryRepository.findByIdUserIdAndIdTrainingDateAndIdTrainingId(userId, date, trainingId)
        return entity?.let {
            TrainingHistoryItem(
                trainingId = it.id.trainingId,
                trainingName = it.training?.trainingName ?: "不明なトレーニング",
                trainingDate = it.id.trainingDate.toString(),
                trainedFlag = it.trainedFlag,
                trainingCount = it.trainingCount ?: 0,
                caloriesConsumed = it.caloriesConsumed ?: 0.0
            )
        }
    }
}
