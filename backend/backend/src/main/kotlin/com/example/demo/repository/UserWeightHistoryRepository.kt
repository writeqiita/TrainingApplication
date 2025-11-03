package com.example.demo.repository

import com.example.demo.entity.UserWeightHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserWeightHistoryRepository : JpaRepository<UserWeightHistory, Int> {
    fun findByUserIdOrderByDateDesc(userId: Int): List<UserWeightHistory>
}
