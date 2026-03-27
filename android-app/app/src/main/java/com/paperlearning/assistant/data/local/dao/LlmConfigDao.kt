package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.LlmConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LlmConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(config: LlmConfigEntity): Long

    @Update
    suspend fun update(config: LlmConfigEntity)

    @Query("SELECT * FROM llm_configs WHERE isActive = 1")
    suspend fun getActive(): LlmConfigEntity?

    @Query("SELECT * FROM llm_configs WHERE isPreset = 1")
    fun getPresets(): Flow<List<LlmConfigEntity>>

    @Query("SELECT * FROM llm_configs")
    fun getAll(): Flow<List<LlmConfigEntity>>
}
