package com.paperlearning.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "papers")
data class PaperEntity(
    @PrimaryKey val id: Long = 0,
    val arxivId: String?,
    val title: String,
    val authors: String,  // JSON array string
    @ColumnInfo(name = "paper_abstract")
    val paperAbstract: String,
    val pdfPath: String,
    val parsedStatus: ParseStatus = ParseStatus.NOT_PARSED,
    val createdAt: Long = System.currentTimeMillis()
)
