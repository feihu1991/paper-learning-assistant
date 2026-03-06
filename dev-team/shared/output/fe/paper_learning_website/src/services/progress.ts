import api from './api'
import { LearningProgress, ProgressStats } from '../types'

export const progressService = {
  // 获取学习进度列表
  async getLearningProgress(): Promise<LearningProgress[]> {
    return api.get<LearningProgress[]>('/progress')
  },

  // 获取学习统计数据
  async getProgressStats(): Promise<ProgressStats> {
    return api.get<ProgressStats>('/progress/stats')
  },

  // 更新学习进度
  async updateProgress(paperId: string, data: {
    status?: 'not_started' | 'in_progress' | 'completed'
    progress?: number
    completed_sections?: string[]
  }): Promise<LearningProgress> {
    return api.put<LearningProgress>(`/progress/${paperId}`, data)
  },

  // 获取单篇论文的学习进度
  async getPaperProgress(paperId: string): Promise<LearningProgress> {
    return api.get<LearningProgress>(`/progress/${paperId}`)
  }
}
