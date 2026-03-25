package com.paperlearning.assistant.data.remote.arxiv

import com.google.gson.annotations.SerializedName

/**
 * arXiv API 响应
 */
data class ArxivResponse(
    @SerializedName("feed") val feed: ArxivFeed
)

data class ArxivFeed(
    @SerializedName("entry") val entries: List<ArxivEntry>,
    @SerializedName("totalResults") val totalResults: String,
    @SerializedName("itemsPerPage") val itemsPerPage: String,
    @SerializedName("startIndex") val startIndex: String
)

data class ArxivEntry(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("published") val published: String,
    @SerializedName("updated") val updated: String,
    @SerializedName("author") val authors: List<ArxivAuthor>,
    @SerializedName("category") val categories: List<ArxivCategory>
)

data class ArxivAuthor(
    @SerializedName("name") val name: String
)

data class ArxivCategory(
    @SerializedName("term") val term: String
)
