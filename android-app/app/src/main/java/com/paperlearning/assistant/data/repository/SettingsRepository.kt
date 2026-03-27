package com.paperlearning.assistant.data.repository

import com.paperlearning.assistant.data.local.dao.LlmConfigDao
import com.paperlearning.assistant.data.model.LlmConfigEntity
import com.paperlearning.assistant.data.security.ApiKeyStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing LLM configuration and secure API key storage.
 * API keys are stored encrypted via ApiKeyStore (EncryptedSharedPreferences).
 * The config metadata (name, endpoint, model) is stored in Room as usual.
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val llmConfigDao: LlmConfigDao,
    private val apiKeyStore: ApiKeyStore
) {
    /**
     * Save a new LLM configuration and store its API key securely.
     *
     * @param config The LLM config (apiKey field stores plaintext temporarily)
     * @param apiKey The plaintext API key to encrypt and store separately
     */
    suspend fun saveLlmConfig(config: LlmConfigEntity, apiKey: String) {
        val configId = llmConfigDao.insert(config)
        apiKeyStore.saveApiKey(configId, apiKey)
    }

    /**
     * Update an existing LLM configuration and its API key.
     *
     * @param config The updated LLM config
     * @param apiKey The new plaintext API key (or null to keep existing)
     */
    suspend fun updateLlmConfig(config: LlmConfigEntity, apiKey: String?) {
        llmConfigDao.update(config)
        apiKey?.let { apiKeyStore.saveApiKey(config.id, it) }
    }

    /**
     * Get the currently active LLM configuration with its API key decrypted.
     *
     * @return The active config with plaintext API key, or null if none set
     */
    suspend fun getActiveLlmConfig(): LlmConfigEntity? {
        val config = llmConfigDao.getActive() ?: return null
        val decryptedApiKey = apiKeyStore.getApiKey(config.id)
        return if (decryptedApiKey != null) {
            config.copy(apiKey = decryptedApiKey)
        } else {
            config
        }
    }

    fun getPresetLlmConfigs(): Flow<List<LlmConfigEntity>> {
        return llmConfigDao.getPresets()
    }

    fun getAllLlmConfigs(): Flow<List<LlmConfigEntity>> {
        return llmConfigDao.getAll()
    }

    /**
     * Set a configuration as active. The API key is preserved in secure storage.
     *
     * @param configId The ID of the config to activate
     */
    suspend fun setActiveLlmConfig(configId: Long) {
        val allConfigs = llmConfigDao.getAll().first()
        allConfigs.forEach { config ->
            llmConfigDao.update(config.copy(isActive = config.id == configId))
        }
    }

    /**
     * Delete an LLM configuration and its stored API key.
     *
     * @param config The config to delete
     */
    suspend fun deleteLlmConfig(config: LlmConfigEntity) {
        if (!config.isPreset) {
            if (config.isActive) {
                llmConfigDao.getActive()?.let {
                    val presets = llmConfigDao.getPresets().first()
                    presets.firstOrNull()?.let { default ->
                        llmConfigDao.update(default.copy(isActive = true))
                    }
                }
            }
            apiKeyStore.deleteApiKey(config.id)
            llmConfigDao.update(config)
        }
    }

    suspend fun hasActiveConfig(): Boolean {
        return getActiveLlmConfig() != null
    }
}
