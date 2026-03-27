package com.paperlearning.assistant.data.local.dao

import androidx.room.*
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(paper: PaperEntity): Long

    @Update
    suspend fun update(paper: PaperEntity)

    @Delete
    suspend fun delete(paper: PaperEntity)

    @Query("SELECT * FROM papers WHERE id = :id")
    suspend fun getById(id: Long): PaperEntity?

    @Query("SELECT * FROM papers ORDER BY createdAt DESC")
    fun getAll(): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE parsedStatus = :status")
    fun getByStatus(status: Int): Flow<List<PaperEntity>>

    @Query("SELECT * FROM papers WHERE title LIKE '%' || :query || '%' OR paperAbstract LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<PaperEntity>>
}
