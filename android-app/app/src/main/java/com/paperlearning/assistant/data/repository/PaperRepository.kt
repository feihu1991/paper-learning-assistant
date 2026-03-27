package com.paperlearning.assistant.data.repository

import com.paperlearning.assistant.data.local.dao.PaperDao
import com.paperlearning.assistant.data.model.PaperEntity
import com.paperlearning.assistant.data.model.ParseStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaperRepository @Inject constructor(
    private val paperDao: PaperDao
) {
    suspend fun insertPaper(paper: PaperEntity): Long {
        return paperDao.insert(paper)
    }

    suspend fun updatePaper(paper: PaperEntity) {
        paperDao.update(paper)
    }

    suspend fun deletePaper(paper: PaperEntity) {
        paperDao.delete(paper)
    }

    suspend fun getPaperById(id: Long): PaperEntity? {
        return paperDao.getById(id)
    }

    fun getAllPapers(): Flow<List<PaperEntity>> {
        return paperDao.getAll()
    }

    fun getPapersByStatus(status: ParseStatus): Flow<List<PaperEntity>> {
        return paperDao.getByStatus(status.ordinal)
    }

    fun searchPapers(query: String): Flow<List<PaperEntity>> {
        return paperDao.search(query)
    }

    suspend fun updateParseStatus(paperId: Long, status: ParseStatus) {
        val paper = getPaperById(paperId)
        paper?.let {
            updatePaper(it.copy(parsedStatus = status))
        }
    }
}
