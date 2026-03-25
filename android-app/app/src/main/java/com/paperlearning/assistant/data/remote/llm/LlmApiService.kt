package com.paperlearning.assistant.data.remote.llm

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Retrofit interface for LLM API calls.
 * Supports OpenAI-compatible APIs (OpenAI, Azure OpenAI, local models, etc.)
 */
interface LlmApiService {

    /**
     * Create a chat completion request.
     * 
     * @param apiKey API key for authentication
     * @param request The chat completion request body
     * @return Response containing the chat completion result
     */
    @POST("v1/chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") apiKey: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: LlmRequest
    ): Response<LlmResponse>
}
