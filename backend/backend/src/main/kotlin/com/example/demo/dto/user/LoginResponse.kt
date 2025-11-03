package com.example.demo.dto.user

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val userId: Int? = null,
    val username: String? = null,
    val weight: Double? = null,
    val admin: Int? = null
)
