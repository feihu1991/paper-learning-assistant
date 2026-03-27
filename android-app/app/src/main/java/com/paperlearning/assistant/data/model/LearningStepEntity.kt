package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_steps")
data class LearningStepEntity(
    @PrimaryKey val id: Long = 0,
    val paperId: Long,
    val stepOrder: Int,
    val stepType: StepType,
    val title: String,
    val content: String,
    val mediaPath: String?,
    val estimatedMinutes: Int,
    // UI-only: indicates whether this step has been completed by the user
    // This field is populated by the UI layer based on user progress data
    val isCompleted: Boolean = false
)
