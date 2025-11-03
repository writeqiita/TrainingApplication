package com.example.demo.dto.training

data class UpdateTrainingStatusRequest(
    val trainingId: Int,
    val userId: Int,
    val trainingDate: String,
    val trainedFlag: Int
)
