package com.example.demo.dto.training

data class DeleteTrainingRequest(
    val trainingId: Int,
    val userId: Int,
    val trainingDate: String
)
