package com.example.demo.repository

import com.example.demo.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Int> {
    fun findByUserName(userName: String): User?
    fun findByUserId(userId: Int): User?
}