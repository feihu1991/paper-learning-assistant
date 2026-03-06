import api from './api'

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

export const userService = {
  // 获取用户资料
  async getProfile(): Promise<UserProfile> {
    return api.get<UserProfile>('/user/profile')
  },

  // 更新用户资料
  async updateProfile(data: UpdateProfileRequest): Promise<UserProfile> {
    return api.put<UserProfile>('/user/profile', data)
  }
}

export const exportService = {
  // 导出论文报告
  async exportReport(data: ExportReportRequest): Promise<ExportReportResponse> {
    return api.post<ExportReportResponse>('/export/report', data)
  }
}
