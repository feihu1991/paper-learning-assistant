package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "llm_configs")
data class LlmConfigEntity(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val apiEndpoint: String,
    val apiKey: String,
    val model: String,
    val isActive: Boolean = false,
    val isPreset: Boolean = false
)
