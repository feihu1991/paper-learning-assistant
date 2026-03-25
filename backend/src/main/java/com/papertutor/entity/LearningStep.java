package com.papertutor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 学习步骤实体类
 */
@Entity
@Table(name = "learning_steps", indexes = {
    @Index(name = "idx_paper_order", columnList = "paper_id, step_order")
})
@Data
@NoArgsConstructor
public class LearningStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private Paper paper;

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false, length = 20)
    private StepType stepType;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", length = 10)
    private MediaType mediaType = MediaType.NONE;

    @Column(name = "media_path", length = 500)
    private String mediaPath;

    @Column(name = "quiz_question", columnDefinition = "JSON")
    private String quizQuestion;  // JSON 存储测验问题

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes = 5;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * 步骤类型枚举
     */
    public enum StepType {
        CONCEPT,      // 概念讲解
        METHOD,       // 方法详解
        FORMULA,      // 公式推导
        EXPERIMENT,   // 实验分析
        CONCLUSION,   // 总结回顾
        QUIZ          // 小测验
    }

    /**
     * 媒体类型枚举
     */
    public enum MediaType {
        NONE,
        IMAGE,
        GIF,
        VIDEO
    }
}
