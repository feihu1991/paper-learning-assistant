import api from './api'
import { Recommendation } from '../types'

export const recommendationsService = {
  // 获取推荐论文列表
  getRecommendations: async (limit?: number): Promise<Recommendation[]> => {
    const params = limit ? `?limit=${limit}` : ''
    return api.get<Recommendation[]>(`/recommendations${params}`)
  },

  // 获取基于论文的推荐
  getRecommendationsForPaper: async (paperId: string): Promise<Recommendation[]> => {
    return api.get<Recommendation[]>(`/papers/${paperId}/recommendations`)
  }
}
