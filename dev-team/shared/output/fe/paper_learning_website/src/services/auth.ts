import api from './api'
import { LoginRequest, RegisterRequest, AuthResponse, User } from '../types'

export const authService = {
  // 登录
  async login(data: LoginRequest): Promise<AuthResponse> {
    return api.post<AuthResponse>('/auth/login', data)
  },

  // 注册
  async register(data: RegisterRequest): Promise<AuthResponse> {
    return api.post<AuthResponse>('/auth/register', data)
  },

  // 登出
  async logout(): Promise<void> {
    return api.post('/auth/logout')
  },

  // 获取当前用户信息
  async getCurrentUser(): Promise<User> {
    return api.get<User>('/auth/me')
  },

  // 验证token
  async verifyToken(): Promise<User> {
    return api.get<User>('/auth/verify')
  }
}
