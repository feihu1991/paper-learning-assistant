package com.paperlearning.assistant.data.repository

import com.paperlearning.assistant.data.local.dao.LearningStepDao
import com.paperlearning.assistant.data.local.dao.UserProgressDao
import com.paperlearning.assistant.data.model.LearningMode
import com.paperlearning.assistant.data.model.LearningStepEntity
import com.paperlearning.assistant.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningRepository @Inject constructor(
    private val learningStepDao: LearningStepDao,
    private val userProgressDao: UserProgressDao
) {
    suspend fun insertLearningSteps(steps: List<LearningStepEntity>) {
        learningStepDao.insert(steps)
    }

    fun getLearningStepsByPaperId(paperId: Long): Flow<List<LearningStepEntity>> {
        return learningStepDao.getByPaperId(paperId)
    }

    suspend fun deleteLearningStepsByPaperId(paperId: Long) {
        learningStepDao.deleteByPaperId(paperId)
    }

    suspend fun saveUserProgress(progress: UserProgressEntity) {
        userProgressDao.insert(progress)
    }

    suspend fun updateUserProgress(progress: UserProgressEntity) {
        userProgressDao.update(progress)
    }

    suspend fun getUserProgressByPaperId(paperId: Long): UserProgressEntity? {
        return userProgressDao.getByPaperId(paperId)
    }

    fun getAllUserProgress(): Flow<List<UserProgressEntity>> {
        return userProgressDao.getAll()
    }

    suspend fun startLearningSession(
        paperId: Long,
        learningMode: LearningMode,
        totalSteps: Int
    ) {
        val progress = UserProgressEntity(
            paperId = paperId,
            currentStep = 0,
            completedSteps = "[]",
            learningMode = learningMode
        )
        saveUserProgress(progress)
    }

    suspend fun updateCurrentStep(paperId: Long, currentStep: Int) {
        val progress = getUserProgressByPaperId(paperId)
        progress?.let {
            updateUserProgress(it.copy(currentStep = currentStep))
        }
    }

    suspend fun markStepAsCompleted(paperId: Long, stepIndex: Int) {
        val progress = getUserProgressByPaperId(paperId)
        progress?.let {
            val completedSteps = it.completedSteps
                .trim('[', ']')
                .split(",")
                .filter { it.isNotBlank() }
                .map { it.toInt() }
                .toMutableList()
            
            if (stepIndex !in completedSteps) {
                completedSteps.add(stepIndex)
            }
            
            updateUserProgress(
                it.copy(
                    completedSteps = completedSteps.toString(),
                    completedAt = it.completedAt
                )
            )
        }
    }
}
