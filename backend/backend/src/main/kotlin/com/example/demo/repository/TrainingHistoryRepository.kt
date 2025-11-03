package com.example.demo.repository

import com.example.demo.entity.TrainingHistory
import com.example.demo.entity.TrainingHistoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TrainingHistoryRepository : JpaRepository<TrainingHistory, TrainingHistoryId> {
    
    // ユーザーIDで検索
    fun findByIdUserId(userId: Int): List<TrainingHistory>
    
    // ユーザーIDと実施フラグで検索
    fun findByIdUserIdAndTrainedFlag(userId: Int, trainedFlag: Int): List<TrainingHistory>
    
    // ユーザーIDと日付範囲で検索
    fun findByIdUserIdAndIdTrainingDateBetween(
        userId: Int, 
        startDate: LocalDateTime, 
        endDate: LocalDateTime
    ): List<TrainingHistory>

    // ユーザーID、日付、トレーニングIDで検索
    fun findByIdUserIdAndIdTrainingDateAndIdTrainingId(
        userId: Int,
        trainingDate: LocalDateTime,
        trainingId: Int
    ): TrainingHistory?
    
    // トレーニングIDで検索
    fun findByIdTrainingId(trainingId: Int): List<TrainingHistory>
    
    // ユーザーの総消費カロリーを計算
    @Query("SELECT COALESCE(SUM(t.caloriesConsumed), 0) FROM TrainingHistory t WHERE t.id.userId = :userId AND t.trainedFlag = 1")
    fun getTotalCaloriesConsumedByUserId(@Param("userId") userId: Int): Double
    
    // ユーザーの実施済みトレーニング数を取得
    @Query("SELECT COUNT(t) FROM TrainingHistory t WHERE t.id.userId = :userId AND t.trainedFlag = 1")
    fun getCompletedTrainingCountByUserId(@Param("userId") userId: Int): Int
}