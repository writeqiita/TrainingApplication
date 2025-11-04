package com.example.demo.repository

import com.example.demo.entity.Training
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TrainingRepository : JpaRepository<Training, Int> {
    fun findByTrainingPartIn(trainingParts: List<Int> ): List<Training>
    fun findByTrainingNameContaining(trainingName: String): List<Training>
}