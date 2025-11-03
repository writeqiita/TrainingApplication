package com.example.demo.dto.training

data class TrainingProposalRequest(
    val userId: Int,
    val parts: List<String> = emptyList(),
    val calories: Int
)
