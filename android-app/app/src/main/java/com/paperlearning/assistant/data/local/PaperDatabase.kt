package com.paperlearning.assistant.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paperlearning.assistant.data.local.dao.*
import com.paperlearning.assistant.data.model.*

@Database(
    entities = [
        PaperEntity::class,
        LearningStepEntity::class,
        UserProgressEntity::class,
        LlmConfigEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class PaperDatabase : RoomDatabase() {
    abstract fun paperDao(): PaperDao
    abstract fun learningStepDao(): LearningStepDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun llmConfigDao(): LlmConfigDao

    companion object {
        const val DATABASE_NAME = "paper_learning_db"
    }
}
