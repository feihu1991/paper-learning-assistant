package com.paperlearning.assistant.data.remote.llm

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Unified LLM API client that manages connections to multiple LLM providers.
 * Supports any OpenAI-compatible API endpoint.
 */
@Singleton
class LlmClient @Inject constructor() {

    companion object {
        private const val TAG = "LlmClient"
    }

    private var currentApiService: LlmApiService? = null
    private var currentBaseUrl: String? = null

    /**
     * Get or create an API service for the given endpoint.
     * Caches the service to avoid recreating Retrofit instances.
     *
     * @param baseUrl The base URL of the LLM API endpoint
     * @return The LLM API service instance
     */
    private fun getApiService(baseUrl: String): LlmApiService {
        if (currentApiService == null || currentBaseUrl != baseUrl) {
            Log.d(TAG, "Creating new Retrofit instance for: $baseUrl")
            currentApiService = createApiService(baseUrl)
            currentBaseUrl = baseUrl
        }
        return currentApiService!!
    }

    /**
     * Create a new Retrofit API service for the given endpoint.
     *
     * @param baseUrl The base URL of the LLM API endpoint
     * @return A new LLM API service instance
     */
    private fun createApiService(baseUrl: String): LlmApiService {
        return Retrofit.Builder()
            .baseUrl(ensureTrailingSlash(baseUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LlmApiService::class.java)
    }

    /**
     * Ensure the base URL ends with a trailing slash.
     */
    private fun ensureTrailingSlash(url: String): String {
        return if (url.endsWith("/")) url else "$url/"
    }

    /**
     * Send a chat completion request to the specified LLM endpoint.
     *
     * @param baseUrl The base URL of the LLM API endpoint
     * @param apiKey The API key for authentication (format: "Bearer YOUR_KEY")
     * @param request The chat completion request
     * @return The API response, or null if the request fails
     */
    suspend fun chatCompletion(
        baseUrl: String,
        apiKey: String,
        request: LlmRequest
    ): LlmResponse? {
        return try {
            val apiService = getApiService(baseUrl)
            val authHeader = if (apiKey.startsWith("Bearer ")) apiKey else "Bearer $apiKey"
            val response = apiService.createChatCompletion(authHeader, "application/json", request)

            if (response.isSuccessful) {
                Log.d(TAG, "LLM API call succeeded: ${response.code()}")
                response.body()
            } else {
                Log.e(TAG, "LLM API call failed: ${response.code()} ${response.message()}")
                Log.d(TAG, "Response error body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "LLM API network error", e)
            null
        }
    }

    /**
     * Send a simple chat message and get the response.
     * Convenience method for basic single-turn conversations.
     *
     * @param baseUrl The base URL of the LLM API endpoint
     * @param apiKey The API key for authentication
     * @param model The model to use
     * @param userMessage The user's message
     * @param systemPrompt Optional system prompt
     * @return The assistant's response text, or null if the request fails
     */
    suspend fun simpleChat(
        baseUrl: String,
        apiKey: String,
        model: String,
        userMessage: String,
        systemPrompt: String? = null
    ): String? {
        val request = LlmRequestBuilder(model)
            .apply {
                systemPrompt?.let { addSystemMessage(it) }
                addUserMessage(userMessage)
            }
            .build()

        val response = chatCompletion(baseUrl, apiKey, request)
        return response?.getAssistantResponse()
    }

    /**
     * Validate an API endpoint and key by making a test request.
     *
     * @param baseUrl The base URL to test
     * @param apiKey The API key to test
     * @param model The model to use for testing
     * @return True if the endpoint and key are valid, false otherwise
     */
    suspend fun validateEndpoint(
        baseUrl: String,
        apiKey: String,
        model: String = "gpt-3.5-turbo"
    ): Boolean {
        return try {
            val request = LlmRequestBuilder(model)
                .addUserMessage("Hello")
                .build()

            val response = chatCompletion(baseUrl, apiKey, request)
            response != null && !response.hasError()
        } catch (e: Exception) {
            Log.e(TAG, "Endpoint validation failed", e)
            false
        }
    }

    /**
     * Clear the cached API service.
     * Call this when switching endpoints.
     */
    fun clearCache() {
        currentApiService = null
        currentBaseUrl = null
        Log.d(TAG, "API service cache cleared")
    }
}
