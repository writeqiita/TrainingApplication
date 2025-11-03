package com.example.demo.dto.training

data class CreateTrainingRequest(
    val trainingName: String,
    val trainingPart: String,
    val mets: Double,
    val trainingPace: Int,
    val createUser: String
)
