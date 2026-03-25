package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.LearningStepEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningStepDao {
    @Insert
    suspend fun insert(steps: List<LearningStepEntity>)

    @Query("SELECT * FROM learning_steps WHERE paperId = :paperId ORDER BY stepOrder")
    fun getByPaperId(paperId: Long): Flow<List<LearningStepEntity>>

    @Query("DELETE FROM learning_steps WHERE paperId = :paperId")
    suspend fun deleteByPaperId(paperId: Long)
}
