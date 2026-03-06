import api from './api'
import { Favorite } from '../types'

export const favoritesService = {
  // 获取用户收藏列表
  async getFavorites(): Promise<Favorite[]> {
    return api.get<Favorite[]>('/favorites')
  },

  // 添加收藏
  async addFavorite(paperId: string): Promise<Favorite> {
    return api.post<Favorite>('/favorites', { paper_id: paperId })
  },

  // 移除收藏
  async removeFavorite(favoriteId: string): Promise<void> {
    return api.delete(`/favorites/${favoriteId}`)
  },

  // 检查是否已收藏
  async checkFavorite(paperId: string): Promise<boolean> {
    const result = await api.get<{ is_favorited: boolean }>(`/favorites/check/${paperId}`)
    return result.is_favorited
  }
}
