import axios from 'axios';

// API base URL - will be set from environment variable in production
const API_BASE_URL = process.env.VITE_API_URL || 'http://localhost:3000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Paper API
export const paperApi = {
  search: (query: string, page = 1, limit = 10) =>
    api.get('/papers/search', { params: { query, page, limit } }),
  
  getById: (id: string) => api.get(`/papers/${id}`),
  
  getSummary: (id: string) => api.get(`/papers/${id}/summary`),
  
  getVisualization: (id: string, type: string) =>
    api.get(`/papers/${id}/visualization`, { params: { type } }),
};

// User API
export const userApi = {
  login: (email: string, password: string) =>
    api.post('/auth/login', { email, password }),
  
  register: (userData: any) => api.post('/auth/register', userData),
  
  getProfile: () => api.get('/user/profile'),
  
  updateProfile: (profileData: any) => api.put('/user/profile', profileData),
  
  getSavedPapers: () => api.get('/user/saved-papers'),
  
  savePaper: (paperId: string) => api.post('/user/save-paper', { paperId }),
};

// AI Generation API
export const aiApi = {
  generateExplanation: (paperId: string, concept: string) =>
    api.post('/ai/explain', { paperId, concept }),
  
  generateQuiz: (paperId: string) => api.post('/ai/quiz', { paperId }),
  
  generateSummary: (paperId: string, length: 'short' | 'medium' | 'long') =>
    api.post('/ai/summary', { paperId, length }),
};

export default api;