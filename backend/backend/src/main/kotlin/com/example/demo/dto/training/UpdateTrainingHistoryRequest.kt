package com.example.demo.dto.training

data class UpdateTrainingHistoryRequest(
    val userId: Int,
    val oldTrainingId: Int,
    val newTrainingId: Int,
    val trainingDate: String,
    val count: Int,
    val calories: Double
)
