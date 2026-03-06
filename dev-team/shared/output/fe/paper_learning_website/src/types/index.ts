// 用户相关类型
export interface User {
  id: string
  username: string
  email: string
  created_at: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  user: User
}

// 收藏相关类型
export interface Favorite {
  id: string
  paper_id: string
  title: string
  authors: string[]
  abstract: string
  source: string
  published_date: string
  created_at: string
}

// 学习进度相关类型
export interface LearningProgress {
  paper_id: string
  title: string
  status: 'not_started' | 'in_progress' | 'completed'
  progress: number // 0-100
  last_accessed: string
  completed_sections: string[]
}

export interface ProgressStats {
  total_papers: number
  completed_papers: number
  in_progress_papers: number
  total_learning_time: number // 分钟
  weekly_progress: {
    date: string
    progress: number
  }[]
}

// 用户资料相关类型
export interface UserProfile {
  id: string
  username: string
  email: string
  bio?: string
  avatar_url?: string
  institution?: string
  created_at: string
}

export interface UpdateProfileRequest {
  username?: string
  email?: string
  bio?: string
  avatar_url?: string
  institution?: string
}

// 导出报告相关类型
export interface ExportReportRequest {
  paperId: string
  format: 'pdf' | 'markdown' | 'html'
  includeAbstract: boolean
  includeSummary: boolean
  includeNotes: boolean
}

export interface ExportReportResponse {
  download_url: string
  filename: string
}
