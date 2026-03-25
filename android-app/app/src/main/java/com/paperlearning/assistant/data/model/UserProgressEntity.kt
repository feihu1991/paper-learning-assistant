package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val currentStep: Int,
    val completedSteps: String,  // JSON array string
    val learningMode: LearningMode,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
