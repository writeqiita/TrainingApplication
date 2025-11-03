package com.example.demo.dto.training

import com.example.demo.dto.training.TrainingItem

data class TrainingHistoryRequest(
    val userId: Int,
    val trainings: List<TrainingItem>
)
