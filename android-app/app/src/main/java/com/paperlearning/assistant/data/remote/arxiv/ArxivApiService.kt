package com.paperlearning.assistant.data.remote.arxiv

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * arXiv API 服务接口
 * 用于搜索和获取论文元数据
 */
interface ArxivApiService {
    
    /**
     * 搜索论文
     * @param searchQuery 搜索关键词
     * @param maxResults 最大结果数
     * @param start 起始位置
     * @param sortBy 排序字段 (submittedDate, relevance, lastUpdatedDate)
     * @param sortOrder 排序顺序 (ascending, descending)
     */
    @GET("api/query")
    suspend fun searchPapers(
        @Query("search_query") searchQuery: String,
        @Query("max_results") maxResults: Int = 20,
        @Query("start") start: Int = 0,
        @Query("sortBy") sortBy: String = "submittedDate",
        @Query("sortOrder") sortOrder: String = "descending"
    ): ArxivResponse
    
    /**
     * 根据 ID 获取单篇论文
     */
    @GET("api/query")
    suspend fun getPaperById(
        @Query("id_list") idList: String
    ): ArxivResponse
}
