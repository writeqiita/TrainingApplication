package com.example.demo.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import com.example.demo.service.UserService

@RestController
@RequestMapping("/api/users")
class UserWeightController(
    private val userService: UserService
) {

    @PostMapping("/{userId}/update-weight")
    fun updateWeight(
        @PathVariable userId: Int,
        @RequestBody request: Map<String, Double>
    ): Map<String, Any> {
        val weight = request["weight"] ?: return mapOf("success" to false)
        userService.saveUserWeight(userId, weight)
        return mapOf("success" to true)
    }

    @GetMapping("/{userId}/weight-history")
    fun getWeightHistory(@PathVariable userId: Int): Map<String, Any> {
        val weights = userService.getWeightHistory(userId)
        return mapOf("weights" to weights)
    }
}
