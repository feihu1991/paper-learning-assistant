package com.paperlearning.assistant.data.repository

import com.paperlearning.assistant.data.remote.arxiv.ArxivApiService
import com.paperlearning.assistant.data.remote.arxiv.ArxivResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ArXiv 论文搜索 Repository
 * 封装 ArxivApiService，提供结构化的论文搜索结果
 */
@Singleton
class ArxivRepository @Inject constructor(
    private val arxivApiService: ArxivApiService
) {
    /**
     * 搜索 ArXiv 论文
     *
     * @param query 搜索关键词
     * @param maxResults 最大结果数，默认 20
     * @return 论文列表
     */
    suspend fun search(query: String, maxResults: Int = 20): List<ArxivSearchResult> {
        val response = arxivApiService.searchPapers(
            searchQuery = query,
            maxResults = maxResults
        )
        return response.toArxivPapers()
    }

    /**
     * 根据 ArXiv ID 获取论文详情
     *
     * @param arxivId ArXiv 论文 ID（如 2301.00001）
     * @return 论文详情，不存在则返回 null
     */
    suspend fun getById(arxivId: String): ArxivSearchResult? {
        val response = arxivApiService.getPaperById(arxivId)
        return response.toArxivPapers().firstOrNull()
    }

    /**
     * 将 ArxivResponse 解析为搜索结果列表
     */
    private fun ArxivResponse.toArxivPapers(): List<ArxivSearchResult> {
        return this.feed.entries.map { entry ->
            ArxivSearchResult(
                arxivId = entry.id.substringAfterLast("/"),
                title = entry.title.replace("\n", " ").trim(),
                authors = entry.authors.map { it.name },
                abstract = entry.summary.replace("\n", " ").trim(),
                published = entry.published,
                pdfUrl = "${entry.id}.pdf"
            )
        }
    }
}

/**
 * ArXiv 论文搜索结果
 */
data class ArxivSearchResult(
    val arxivId: String,
    val title: String,
    val authors: List<String>,
    val abstract: String,
    val published: String,
    val pdfUrl: String
)
