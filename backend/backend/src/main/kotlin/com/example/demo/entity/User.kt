package com.example.demo.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val userId: Int? = null,

    @Column(name = "user_name", nullable = false, unique = true)
    val userName: String,

    @Column(name = "password", nullable = false)
    val password: String,

    @Column(name = "height")
    val height: Double? = null,

    @Column(name = "weight")
    val weight: Double? = null,

    @Column(name = "create_time")
    val createTime: LocalDateTime? = null,

    @Column(name = "create_user")
    val createUser: String? = null,

    @Column(name = "update_time")
    val updateTime: LocalDateTime? = null,

    @Column(name = "update_user")
    val updateUser: String? = null,

    @Column(name = "admin", nullable = false)
    val admin: Int = 0
) {
    // デフォルトコンストラクタ
    constructor() : this(null, "", "", null, null, null, null, null, null, 0)
}
