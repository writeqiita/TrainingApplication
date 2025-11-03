package com.example.demo.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "TRAINING")
data class Training(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_id")
    val trainingId: Int? = null,

    @Column(name = "training_name", nullable = false, length = 100)
    val trainingName: String,

    @Column(name = "training_part", nullable = false)
    val trainingPart: Int,

    @Column(name = "mets", nullable = false)
    val mets: Float,

    @Column(name = "training_pace", nullable = false)
    val trainingPace: Int,

    @Column(name = "create_time")
    val createTime: LocalDateTime? = null,

    @Column(name = "create_user", length = 50)
    val createUser: String? = null,

    @Column(name = "update_time")
    val updateTime: LocalDateTime? = null,

    @Column(name = "update_user", length = 50)
    val updateUser: String? = null
) {
    // デフォルトコンストラクタ
    constructor() : this(null, "", 0, 0.0f, 0, null, null, null, null)
}