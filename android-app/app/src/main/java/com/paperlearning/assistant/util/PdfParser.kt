package com.paperlearning.assistant.util

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileOutputStream

/**
 * PDF 解析工具类
 * 负责从 PDF 文件中提取元数据（标题、作者、摘要）并管理文件存储
 */
class PdfParser {

    /**
     * PDF 元数据
     */
    data class PdfMetadata(
        val title: String?,
        val authors: List<String>?,
        val abstract: String?,
        val fullText: String?
    )

    /**
     * 从 PDF 提取元数据
     * 使用启发式方法从 PDF 内容中识别标题、作者和摘要
     * 
     * @param context Android 上下文
     * @param uri PDF 文件的 URI
     * @return 提取的元数据
     */
    fun extractMetadata(context: Context, uri: Uri): PdfMetadata {
        val text = extractTextFromPdf(context, uri)
        return parseMetadataFromText(text)
    }

    /**
     * 从 PDF 提取文本内容
     */
    private fun extractTextFromPdf(context: Context, uri: Uri): String {
        val sb = StringBuilder()
        
        try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val renderer = PdfRenderer(pfd)
                
                // 提取前 5 页的内容（通常包含标题、作者、摘要）
                val maxPages = minOf(renderer.pageCount, 5)
                
                for (pageIndex in 0 until maxPages) {
                    renderer.openPage(pageIndex).use { page ->
                        // 创建位图来渲染页面
                        val bitmap = android.graphics.Bitmap.createBitmap(
                            page.width,
                            page.height,
                            android.graphics.Bitmap.Config.ARGB_8888
                        )
                        
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        
                        // 使用 OCR 或简单的像素分析来提取文本
                        // 注意：Android 的 PdfRenderer 不直接支持文本提取
                        // 这里我们使用简化的方法，实际项目中应使用 PDFBox 或 iText
                        bitmap.recycle()
                    }
                }
                
                renderer.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // 临时实现：返回空文本
        // TODO: 集成 PDF 文本提取库（如 Apache PDFBox Android 版）
        return sb.toString()
    }

    /**
     * 从提取的文本中解析元数据
     * 使用启发式规则识别标题、作者和摘要
     */
    private fun parseMetadataFromText(text: String): PdfMetadata {
        if (text.isBlank()) {
            return PdfMetadata(null, null, null, null)
        }

        val lines = text.split("\n").map { it.trim() }.filter { it.isNotBlank() }
        
        var title: String? = null
        var authors: List<String>? = null
        var abstractText: String? = null
        
        // 启发式规则：第一行通常是标题
        if (lines.isNotEmpty()) {
            title = lines.first().take(200) // 限制标题长度
        }
        
        // 查找作者行（通常包含逗号或"and"）
        for (i in 1 until minOf(lines.size, 5)) {
            val line = lines[i]
            if (line.contains(",") || line.contains(" and ", ignoreCase = true)) {
                authors = line.split(",", " and ")
                    .map { it.trim() }
                    .filter { it.length > 2 && it.length < 50 }
                break
            }
        }
        
        // 查找摘要
        val abstractIndex = lines.indexOfFirst { 
            it.contains("abstract", ignoreCase = true) || 
            it.contains("摘要", ignoreCase = true)
        }
        
        if (abstractIndex != -1 && abstractIndex + 1 < lines.size) {
            // 从摘要标记后开始收集文本
            val abstractLines = mutableListOf<String>()
            var currentIndex = abstractIndex + 1
            
            // 收集直到遇到关键词（如 Introduction, 1., 等）
            while (currentIndex < lines.size) {
                val line = lines[currentIndex]
                if (line.matches(Regex("^(Introduction|\\d+\\.|I\\.|Background)"))) {
                    break
                }
                abstractLines.add(line)
                currentIndex++
                
                // 限制摘要长度
                if (abstractLines.joinToString(" ").length > 500) {
                    break
                }
            }
            
            abstractText = abstractLines.joinToString(" ")
        }
        
        return PdfMetadata(title, authors, abstractText, text)
    }

    /**
     * 复制 PDF 文件到内部存储
     * 
     * @param context Android 上下文
     * @param uri 源文件 URI
     * @param paperId 论文 ID（用于生成文件名）
     * @return 复制后的文件路径
     */
    fun copyToInternalStorage(context: Context, uri: Uri, paperId: Long): String {
        val fileName = "paper_${paperId}.pdf"
        val file = File(context.filesDir, fileName)
        
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        
        return file.absolutePath
    }

    /**
     * 从文件路径读取 PDF 文本（用于已存储的 PDF）
     */
    fun extractTextFromFile(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) {
            return ""
        }
        
        // TODO: 实现文件路径的文本提取
        // 目前返回空字符串，实际应使用 PDF 库
        return ""
    }
}
