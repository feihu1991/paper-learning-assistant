-- Paper Tutor 数据库初始化脚本
-- 创建数据库和基础表结构

CREATE DATABASE IF NOT EXISTS papertutor 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE papertutor;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id),
    INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 论文表
CREATE TABLE IF NOT EXISTS papers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    arxiv_id VARCHAR(50) UNIQUE,
    doi VARCHAR(100) UNIQUE,
    title TEXT NOT NULL,
    abstract TEXT,
    authors JSON,
    publish_date DATE,
    category_id BIGINT,
    pdf_path VARCHAR(500),
    parsed_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    structured_summary JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_arxiv (arxiv_id),
    INDEX idx_doi (doi),
    INDEX idx_category (category_id),
    FULLTEXT INDEX idx_title_abstract (title, abstract)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 论文分析结果表
CREATE TABLE IF NOT EXISTS paper_analyses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    sections JSON,
    figures_list JSON,
    tables_list JSON,
    datasets JSON,
    metrics JSON,
    formulas JSON,
    contributions JSON,
    innovation_score INT,
    difficulty_level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    prerequisite_knowledge JSON,
    related_papers JSON,
    analysis_version VARCHAR(20),
    confidence_score DECIMAL(3,2),
    manual_reviewed BOOLEAN DEFAULT FALSE,
    reviewed_by BIGINT,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES papers(id) ON DELETE CASCADE,
    INDEX idx_paper (paper_id),
    INDEX idx_innovation (innovation_score),
    INDEX idx_difficulty (difficulty_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 学习步骤表
CREATE TABLE IF NOT EXISTS learning_steps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    paper_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    step_type ENUM('CONCEPT', 'METHOD', 'EXPERIMENT', 'CONCLUSION', 'QUIZ', 'FORMULA') NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    media_type ENUM('NONE', 'IMAGE', 'GIF', 'VIDEO') DEFAULT 'NONE',
    media_path VARCHAR(500),
    quiz_question JSON,
    estimated_minutes INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (paper_id) REFERENCES papers(id) ON DELETE CASCADE,
    INDEX idx_paper_order (paper_id, step_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户学习进度表
CREATE TABLE IF NOT EXISTS user_paper_progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    paper_id BIGINT NOT NULL,
    current_step INT DEFAULT 0,
    total_steps INT NOT NULL,
    progress_percentage INT DEFAULT 0,
    completed_step_ids JSON,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (paper_id) REFERENCES papers(id) ON DELETE CASCADE,
    UNIQUE KEY uniq_user_paper (user_id, paper_id),
    INDEX idx_user_progress (user_id, progress_percentage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认分类数据
INSERT INTO categories (name, description) VALUES
('Computer Science', '计算机科学相关论文'),
('Machine Learning', '机器学习领域'),
('Natural Language Processing', '自然语言处理'),
('Computer Vision', '计算机视觉'),
('Physics', '物理学'),
('Mathematics', '数学'),
('Biology', '生物学'),
('Chemistry', '化学');

-- 插入子分类
INSERT INTO categories (name, description, parent_id) VALUES
('Deep Learning', '深度学习', 2),
('Transformer', 'Transformer 架构相关', 3),
('LLM', '大语言模型', 3);
