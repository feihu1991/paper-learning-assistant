package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: UserProgressEntity)

    @Update
    suspend fun update(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE paperId = :paperId")
    suspend fun getByPaperId(paperId: Long): UserProgressEntity?

    @Query("SELECT * FROM user_progress ORDER BY startedAt DESC")
    fun getAll(): Flow<List<UserProgressEntity>>
}
