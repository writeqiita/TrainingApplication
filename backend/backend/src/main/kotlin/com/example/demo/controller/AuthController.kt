package com.example.demo.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import com.example.demo.entity.User
import com.example.demo.repository.UserRepository
import com.example.demo.dto.user.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val user = userRepository.findByUserName(request.username)
        return if (user != null && user.password == request.password) {
            ResponseEntity.ok(
                LoginResponse(
                    success = true,
                    message = "Login successful",
                    userId = user.userId,
                    username = user.userName,
                    weight = user.weight,
                    admin = user.admin
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse(false, "Invalid credentials"))
        }
    }

    @PostMapping("/register")
    fun register(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        if (userRepository.findByUserName(request.username) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(LoginResponse(false, "すでにユーザー名が使用されています"))
        }

        val user = User(
            userName = request.username,
            password = request.password,
            createTime = LocalDateTime.now(),
            createUser = request.username
        )

        userRepository.save(user)

        return ResponseEntity.ok(
            LoginResponse(
                success = true,
                message = "Registration successful",
                userId = user.userId,
                username = user.userName
            )
        )
    }
}
