package com.example.demo.dto.training

data class GetAllTraining(
    val trainingId: Int,
    val trainingName: String,
    val trainingPart: Int,
    val mets: Double,
    val trainingPace: Int
)
