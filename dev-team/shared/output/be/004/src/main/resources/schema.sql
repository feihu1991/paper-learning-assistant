-- 论文学习网站数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS paper_learning CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE paper_learning;

-- 论文表
CREATE TABLE IF NOT EXISTS papers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(500) NOT NULL COMMENT '论文标题',
    abstract TEXT COMMENT '论文摘要',
    authors VARCHAR(1000) COMMENT '作者',
    pdf_url VARCHAR(2000) COMMENT 'PDF链接',
    cover_image LONGBLOB COMMENT '封面图片',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_title (title),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论文表';

-- 论文分析表
CREATE TABLE IF NOT EXISTS paper_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id BIGINT NOT NULL COMMENT '论文ID',
    background TEXT COMMENT '背景介绍',
    coreConcepts JSON COMMENT '核心概念',
    methodology JSON COMMENT '方法论',
    keyInnovations JSON COMMENT '关键创新',
    learningPath JSON COMMENT '学习路径',
    difficulty INT COMMENT '难度等级(1-5)',
    estimated_time INT COMMENT '预估学习时间(分钟)',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_paper_id (paper_id),
    INDEX idx_status (status),
    FOREIGN KEY (paper_id) REFERENCES papers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论文分析表';

-- 分析任务表
CREATE TABLE IF NOT EXISTS analysis_tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paper_id BIGINT NOT NULL COMMENT '论文ID',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/processing/completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_paper_id (paper_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (paper_id) REFERENCES papers(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析任务表';
