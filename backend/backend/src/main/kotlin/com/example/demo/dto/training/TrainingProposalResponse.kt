package com.example.demo.dto.training
import com.example.demo.dto.training.TrainingItem

data class TrainingProposalResponse(
    val trainings: List<TrainingItem>,
    val totalCalories: Double
)
