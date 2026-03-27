package com.paperlearning.assistant.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage for sensitive API keys using EncryptedSharedPreferences.
 * API keys are encrypted at rest using AES-256-GCM before being stored.
 * 
 * @param context Application context for accessing EncryptedSharedPreferences
 */
@Singleton
class ApiKeyStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "paper_learning_secure_prefs"
        private const val KEY_PREFIX = "api_key_"
    }

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /**
     * Save an API key securely for the given config ID.
     * The key is encrypted at rest using EncryptedSharedPreferences.
     *
     * @param configId The LLM config ID to associate with this key
     * @param apiKey The plaintext API key to encrypt and store
     */
    fun saveApiKey(configId: Long, apiKey: String) {
        encryptedPrefs.edit()
            .putString("$KEY_PREFIX$configId", apiKey)
            .apply()
    }

    /**
     * Retrieve the decrypted API key for the given config ID.
     *
     * @param configId The LLM config ID to look up
     * @return The decrypted API key, or null if not found
     */
    fun getApiKey(configId: Long): String? {
        return encryptedPrefs.getString("$KEY_PREFIX$configId", null)
    }

    /**
     * Delete the stored API key for the given config ID.
     *
     * @param configId The LLM config ID whose key should be deleted
     */
    fun deleteApiKey(configId: Long) {
        encryptedPrefs.edit()
            .remove("$KEY_PREFIX$configId")
            .apply()
    }

    /**
     * Check whether an API key exists for the given config ID.
     *
     * @param configId The LLM config ID to check
     * @return True if a key is stored, false otherwise
     */
    fun hasApiKey(configId: Long): Boolean {
        return encryptedPrefs.contains("$KEY_PREFIX$configId")
    }
}
