package com.example.demo.controller

import com.example.demo.dto.training.*
import com.example.demo.service.TrainingHistoryService
import com.example.demo.repository.TrainingRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/training-history")
class TrainingHistoryController(
    private val trainingHistoryService: TrainingHistoryService,
    private val trainingHistoryRepository: TrainingRepository,
) {

    @PostMapping("/register")
    fun registerTrainingHistory(@RequestBody request: TrainingHistoryRequest): ResponseEntity<TrainingHistoryResponse> {
        return try {
            trainingHistoryService.registerTrainingHistory(request.userId, request.trainings)
            ResponseEntity.ok(TrainingHistoryResponse(true, "トレーニング履歴を登録しました"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(TrainingHistoryResponse(false, "トレーニング履歴の登録に失敗しました: ${e.message}"))
        }
    }

    @GetMapping("/user/{userId}/date/{date}")
    fun getTrainingHistoryByUserAndDate(
        @PathVariable userId: Int,
        @PathVariable date: String
    ): ResponseEntity<Map<String, Any>> {
        val parsedDate = java.time.LocalDate.parse(date)
        val list = trainingHistoryService.getUserTrainingHistoryByDate(userId, parsedDate)
        return ResponseEntity.ok(mapOf("trainings" to list))
    }

    @GetMapping("/user/{userId}/all")
    fun getAllTrainingHistoryByUser(@PathVariable userId: Int): ResponseEntity<Map<String, Any>> {
        val list = trainingHistoryService.getAllByUserId(userId)
        return ResponseEntity.ok(mapOf("trainings" to list))
    }

    @PutMapping("/update-status")
    fun updateTrainingStatus(@RequestBody request: UpdateTrainingStatusRequest): ResponseEntity<TrainingHistoryResponse> {
        return try {
            trainingHistoryService.updateTrainingStatus(
                request.trainingId,
                request.userId,
                request.trainingDate,
                request.trainedFlag
            )
            ResponseEntity.ok(TrainingHistoryResponse(true, "トレーニングステータスを更新しました"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(TrainingHistoryResponse(false, "トレーニングステータスの更新に失敗しました: ${e.message}"))
        }
    }

    @DeleteMapping("/delete")
    fun deleteTraining(@RequestBody request: DeleteTrainingRequest): ResponseEntity<Map<String, Any>> {
        return try {
            val deleted = trainingHistoryService.deleteTraining(request.trainingId, request.userId, request.trainingDate)
            if (deleted) {
                ResponseEntity.ok(mapOf("success" to true, "message" to "削除に成功しました"))
            } else {
                ResponseEntity.ok(mapOf("success" to false, "message" to "対象が見つかりません"))
            }
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("success" to false, "message" to (e.message ?: "不明なエラーが発生しました"))
            )
        }
    }

    @PutMapping("/update")
    fun updateTrainingHistory(@RequestBody request: UpdateTrainingHistoryRequest): ResponseEntity<Map<String, Any>> {
        return try {
            trainingHistoryService.updateTrainingHistory(
                userId = request.userId,
                oldTrainingId = request.oldTrainingId,
                trainingDate = request.trainingDate,
                newTrainingId = request.newTrainingId,
                count = request.count,
                calories = request.calories
            )
            ResponseEntity.ok(mapOf("success" to true, "message" to "トレーニング履歴を更新しました"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                mapOf("success" to false, "message" to (e.message ?: "更新に失敗しました"))
            )
        }
    }

    @GetMapping("/user/{userId}/date/{date}/training/{trainingId}")
    fun getTrainingHistory(
        @PathVariable userId: Int,
        @PathVariable date: String,
        @PathVariable trainingId: Int
    ): ResponseEntity<TrainingHistoryItem> {
        val dateFormat = java.time.LocalDateTime.parse(date)
        val history = trainingHistoryService.findByUserDateTraining(userId, dateFormat, trainingId)
        return if (history != null) {
            ResponseEntity.ok(history)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
