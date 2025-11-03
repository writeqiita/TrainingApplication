package com.example.demo.service

import com.example.demo.entity.User
import com.example.demo.repository.UserRepository
import org.springframework.stereotype.Service
import org.hibernate.Transaction
import com.example.demo.repository.UserWeightHistoryRepository
import jakarta.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userWeightHistoryRepository: UserWeightHistoryRepository

) {
    fun findByUserName(userName: String): User? {
        return userRepository.findByUserName(userName)
    }

    fun findByUserId(userId: Int): User? {
        return userRepository.findByUserId(userId)
    }

    @Transactional
    fun saveUserWeight(userId: Int, newWeight: Double) {
        val existUser = userRepository.findByUserId(userId)
        if (existUser != null) {
            val updateUser = existUser.copy(weight = newWeight, updateTime = java.time.LocalDateTime.now(), updateUser = existUser.userName)
            userRepository.save(updateUser)
            userWeightHistoryRepository.save(
                com.example.demo.entity.UserWeightHistory(
                    userId = userId,
                    date = java.time.LocalDateTime.now(),
                    weight = newWeight,
                    createTime = java.time.LocalDateTime.now(),
                    createUser = existUser.userName
                )
            )
        }
    }

    fun getWeightHistory(userId: Int): List<Map<String, Any>> {
        return userWeightHistoryRepository.findByUserIdOrderByDateDesc(userId)
            .map { mapOf("date" to it.date, "weight" to it.weight) }
    }
}