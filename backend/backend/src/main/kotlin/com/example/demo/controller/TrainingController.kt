package com.example.demo.controller

import com.example.demo.dto.training.*
import com.example.demo.entity.Training
import com.example.demo.repository.TrainingRepository
import com.example.demo.service.TrainingService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/training")
class TrainingController(
    private val trainingService: TrainingService,
    private val trainingRepository: TrainingRepository
) {

    // トレーニング提案
    @PostMapping("/propose")
    fun proposeTraining(@RequestBody request: TrainingProposalRequest): ResponseEntity<TrainingProposalResponse> {
        return try {
            val proposals = trainingService.generateTrainingProposal(
                userId = request.userId,
                parts = request.parts,
                targetCalories = request.calories
            )
            val totalCalories = proposals.sumOf { it.calories }

            val response = TrainingProposalResponse(
                trainings = proposals,
                totalCalories = totalCalories
            )
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest().build()
        }
    }

    // 全トレーニング取得
    @GetMapping("/all")
    fun getAllTrainings(): ResponseEntity<List<GetAllTraining>> {
        return try {
            val trainings = trainingRepository.findAll().map { training ->
                GetAllTraining(
                    trainingId = training.trainingId ?: 0,
                    trainingName = training.trainingName,
                    trainingPart = training.trainingPart,
                    mets = training.mets.toDouble(),
                    trainingPace = training.trainingPace
                )
            }
            ResponseEntity.ok(trainings)
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest().build()
        }
    }

    // 新規トレーニング追加
    @PostMapping
    fun createTraining(@RequestBody request: CreateTrainingRequest): ResponseEntity<Any> {
        return try {
            val training = Training(
                trainingName = request.trainingName,
                trainingPart = request.trainingPart.toIntOrNull() ?: 0,
                mets = request.mets.toFloat(),
                trainingPace = request.trainingPace,
                createTime = LocalDateTime.now(),
                createUser = request.createUser
            )
            trainingRepository.save(training)
            ResponseEntity.ok(mapOf("success" to true, "message" to "トレーニングを追加しました"))
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.badRequest()
                .body(mapOf("success" to false, "message" to "トレーニング追加に失敗しました"))
        }
    }
}
