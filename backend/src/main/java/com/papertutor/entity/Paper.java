package com.papertutor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 论文实体类
 */
@Entity
@Table(name = "papers", indexes = {
    @Index(name = "idx_arxiv", columnList = "arxivId"),
    @Index(name = "idx_doi", columnList = "doi"),
    @Index(name = "idx_category", columnList = "categoryId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "arxiv_id", unique = true, length = 50)
    private String arxivId;

    @Column(name = "doi", unique = true, length = 100)
    private String doi;

    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(name = "abstract", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "authors", columnDefinition = "JSON")
    private String authors;  // JSON 数组存储作者列表

    @Column(name = "publish_date")
    private LocalDate publishDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "parsed_status", nullable = false)
    private ParseStatus parsedStatus = ParseStatus.PENDING;

    @Column(name = "structured_summary", columnDefinition = "JSON")
    private String structuredSummary;  // JSON 存储结构化摘要

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 解析状态枚举
     */
    public enum ParseStatus {
        PENDING,      // 待解析
        PROCESSING,   // 解析中
        COMPLETED,    // 解析完成
        FAILED        // 解析失败
    }

    /**
     * 获取作者列表
     */
    public List<String> getAuthorsList() {
        // TODO: 实现 JSON 解析
        return List.of();
    }
}
