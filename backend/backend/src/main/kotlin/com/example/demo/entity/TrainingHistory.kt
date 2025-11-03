package com.example.demo.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "TRAINING_HISTORY")
data class TrainingHistory(
    @EmbeddedId
    val id: TrainingHistoryId,

    @Column(name = "trained_flag", nullable = false)
    val trainedFlag: Int,

    @Column(name = "training_count")
    val trainingCount: Int? = null,

    @Column(name = "calories_consumed")
    val caloriesConsumed: Double? = null,

    @Column(name = "create_time")
    val createTime: LocalDateTime? = null,

    @Column(name = "create_user", length = 255)
    val createUser: String? = null,

    @Column(name = "update_time")
    val updateTime: LocalDateTime? = null,

    @Column(name = "update_user", length = 255)
    val updateUser: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", insertable = false, updatable = false)
    val training: Training? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    val user: User? = null
) {
    // デフォルトコンストラクタ
    constructor() : this(
        id = TrainingHistoryId(),
        trainedFlag = 0,
        trainingCount = null,
        caloriesConsumed = null,
        createTime = null,
        createUser = null,
        updateTime = null,
        updateUser = null,
        training = null,
        user = null
    )
}

// 複合主キー用のIDクラス
@Embeddable
data class TrainingHistoryId(
    @Column(name = "training_id")
    val trainingId: Int,
    
    @Column(name = "user_id")
    val userId: Int,
    
    @Column(name = "training_date")
    val trainingDate: LocalDateTime
) {
    // デフォルトコンストラクタ（JPA要件）
    constructor() : this(0, 0, LocalDateTime.now())
}