package com.example.demo.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_weight_history")
class UserWeightHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weight_history_id")
    val weightHistoryId: Int = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Int,

    @Column(nullable = false)
    val date: LocalDateTime,

    @Column(nullable = false)
    val weight: Double,

    @Column(name = "create_time")
    val createTime: LocalDateTime? = null,

    @Column(name = "create_user")
    val createUser: String? = null,

    @Column(name = "update_time")
    val updateTime: LocalDateTime? = null,

    @Column(name = "update_user")
    val updateUser: String? = null
){
    constructor() : this(0, 0, LocalDateTime.now(), 0.0, null, null, null, null)
}
