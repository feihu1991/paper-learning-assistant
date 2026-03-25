package com.paperlearning.assistant.data.repository

import com.paperlearning.assistant.data.local.dao.LlmConfigDao
import com.paperlearning.assistant.data.model.LlmConfigEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val llmConfigDao: LlmConfigDao
) {
    suspend fun saveLlmConfig(config: LlmConfigEntity) {
        llmConfigDao.insert(config)
    }

    suspend fun updateLlmConfig(config: LlmConfigEntity) {
        llmConfigDao.update(config)
    }

    suspend fun getActiveLlmConfig(): LlmConfigEntity? {
        return llmConfigDao.getActive()
    }

    fun getPresetLlmConfigs(): Flow<List<LlmConfigEntity>> {
        return llmConfigDao.getPresets()
    }

    fun getAllLlmConfigs(): Flow<List<LlmConfigEntity>> {
        return llmConfigDao.getAll()
    }

    suspend fun setActiveLlmConfig(configId: Long) {
        val allConfigs = getAllLlmConfigs()
            .firstOrNull()
            ?: return
        
        allConfigs.forEach { config ->
            llmConfigDao.update(
                config.copy(isActive = config.id == configId)
            )
        }
    }

    suspend fun deleteLlmConfig(config: LlmConfigEntity) {
        if (!config.isPreset) {
            val paper = com.paperlearning.assistant.data.local.dao.PaperDao::class
            llmConfigDao.getActive()?.let { active ->
                if (active.id == config.id) {
                    val presets = getPresetLlmConfigs().firstOrNull()
                    presets?.firstOrNull()?.let { default ->
                        llmConfigDao.update(default.copy(isActive = true))
                    }
                }
            }
        }
    }

    suspend fun hasActiveConfig(): Boolean {
        return getActiveLlmConfig() != null
    }
}
