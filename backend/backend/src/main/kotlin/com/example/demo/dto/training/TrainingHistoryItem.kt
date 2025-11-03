package com.example.demo.dto.training

data class TrainingHistoryItem(
    val trainingId: Int,
    val trainingName: String,
    val trainingDate: String,
    val trainedFlag: Int,
    val trainingCount: Int,
    val caloriesConsumed: Double
)
