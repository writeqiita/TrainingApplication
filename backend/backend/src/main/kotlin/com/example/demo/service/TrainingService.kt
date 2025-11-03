package com.example.demo.service

import com.example.demo.repository.TrainingRepository
import com.example.demo.repository.UserRepository
import kotlin.math.ceil
import kotlin.random.Random
import org.springframework.stereotype.Service
import com.example.demo.dto.training.TrainingItem
import org.slf4j.LoggerFactory

@Service
class TrainingService(
    private val trainingRepository: TrainingRepository,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(TrainingService::class.java)

    fun generateTrainingProposal(
        userId: Int,
        parts: List<String>,
        targetCalories: Int
    ): List<TrainingItem> {

        // ユーザーの体重をDBから取得（なければ60kg）
        val userWeight = userRepository.findByUserId(userId)?.weight ?: 60.0
        logger.info("ユーザーID: $userId, 体重: $userWeight kg, 目標カロリー: $targetCalories")

        // 部位でトレーニングを絞り込み
        val availableTrainings =
            if (parts.isEmpty()) trainingRepository.findAll()
            else {
                val partNumbers = parts.map { partToNumber(it) }
                trainingRepository.findAll().filter { training ->
                    partNumbers.contains(training.trainingPart)
                }
            }

        if (availableTrainings.isEmpty()) {
            logger.warn("指定部位でトレーニングが見つかりません: $parts")
            return emptyList()
        }

        // 最大5種目をランダムに選択
        val selectedTrainings = availableTrainings.shuffled().take(5)
        logger.info("選択されたトレーニング: ${selectedTrainings.map { it.trainingName }}")

        // カロリーを均等に分配
        val caloriesPerTraining = targetCalories.toDouble() / selectedTrainings.size
        logger.info("各トレーニングあたりの目標カロリー: $caloriesPerTraining kcal")

        val proposals = mutableListOf<TrainingItem>()

        for (training in selectedTrainings) {
            val targetCal = caloriesPerTraining

            // 1回あたりの消費カロリーを計算
            val calPerRep = calculateCaloriesPerRep(training.mets, userWeight, training.trainingPace)
            logger.info("トレーニング: ${training.trainingName}, METS: ${training.mets}, ペース: ${training.trainingPace}, 1回あたり消費カロリー: $calPerRep")

            // 必要な回数を切り上げで計算
            val reps = ceil(targetCal / calPerRep).toInt()
            val actualCalories = reps * calPerRep

            logger.info("目標カロリー: $targetCal, 計算回数: $reps, 実際の消費カロリー: $actualCalories")

            proposals.add(
                TrainingItem(
                    trainingId = training.trainingId ?: 0,
                    trainingName = training.trainingName,
                    reps = reps,
                    calories = actualCalories
                )
            )
        }

        return proposals
    }

    private fun partToNumber(part: String): Int {
        return when (part) {
            "腕" -> 0
            "脚" -> 1
            "腹筋" -> 2
            "全身" -> 3
            else -> 3
        }
    }

    private fun calculateCaloriesPerRep(mets: Float, weightKg: Double, pacePerMinute: Int): Double {
        val caloriesPerMinute = mets * (1.0 / 60.0) * weightKg
        return caloriesPerMinute / pacePerMinute
    }

    fun getAllTrainings(): List<TrainingItem> {
        return trainingRepository.findAll().map {
            TrainingItem(
                trainingId = it.trainingId ?: 0,
                trainingName = it.trainingName,
                reps = 0,
                calories = 0.0
            )
        }
    }
}
