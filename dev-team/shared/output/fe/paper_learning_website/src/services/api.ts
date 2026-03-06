import axios, { AxiosError, AxiosResponse } from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 返回 response.data
api.interceptors.response.use(
  (response: AxiosResponse) => response.data,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // 处理未授权
      localStorage.removeItem('token')
      window.location.href = '/auth'
    }
    return Promise.reject(error)
  }
)

// 扩展 axios 方法的返回类型
const apiExt = {
  get: <T>(url: string, config?: any): Promise<T> => 
    api.get(url, config),
  post: <T>(url: string, data?: any, config?: any): Promise<T> => 
    api.post(url, data, config),
  put: <T>(url: string, data?: any, config?: any): Promise<T> => 
    api.put(url, data, config),
  delete: <T>(url: string, config?: any): Promise<T> => 
    api.delete(url, config),
  patch: <T>(url: string, data?: any, config?: any): Promise<T> => 
    api.patch(url, data, config),
}

export default apiExt
