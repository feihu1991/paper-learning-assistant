import api from './api'
import { Note, CreateNoteRequest, UpdateNoteRequest } from '../types'

export const notesService = {
  // 获取论文的所有笔记
  getNotes: async (paperId: string): Promise<Note[]> => {
    return api.get<Note[]>(`/papers/${paperId}/notes`)
  },

  // 创建笔记
  createNote: async (data: CreateNoteRequest): Promise<Note> => {
    return api.post<Note>('/notes', data)
  },

  // 更新笔记
  updateNote: async (noteId: string, data: UpdateNoteRequest): Promise<Note> => {
    return api.put<Note>(`/notes/${noteId}`, data)
  },

  // 删除笔记
  deleteNote: async (noteId: string): Promise<void> => {
    return api.delete(`/notes/${noteId}`)
  }
}
