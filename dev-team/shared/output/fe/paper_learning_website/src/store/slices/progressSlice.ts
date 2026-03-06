import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import { LearningProgress, ProgressStats } from '../../types'
import { progressService } from '../../services/progress'

interface ProgressState {
  progressList: LearningProgress[]
  stats: ProgressStats | null
  loading: boolean
  error: string | null
}

const initialState: ProgressState = {
  progressList: [],
  stats: null,
  loading: false,
  error: null
}

export const fetchLearningProgress = createAsyncThunk(
  'progress/fetchLearningProgress',
  async (_, { rejectWithValue }) => {
    try {
      const progressList = await progressService.getLearningProgress()
      return progressList
    } catch (error: any) {
      return rejectWithValue(error.message || '获取学习进度失败')
    }
  }
)

export const fetchProgressStats = createAsyncThunk(
  'progress/fetchProgressStats',
  async (_, { rejectWithValue }) => {
    try {
      const stats = await progressService.getProgressStats()
      return stats
    } catch (error: any) {
      return rejectWithValue(error.message || '获取学习统计失败')
    }
  }
)

export const updateLearningProgress = createAsyncThunk(
  'progress/updateLearningProgress',
  async ({ paperId, data }: { paperId: string; data: any }, { rejectWithValue }) => {
    try {
      const progress = await progressService.updateProgress(paperId, data)
      return progress
    } catch (error: any) {
      return rejectWithValue(error.message || '更新学习进度失败')
    }
  }
)

const progressSlice = createSlice({
  name: 'progress',
  initialState,
  reducers: {
    clearProgressError: (state) => {
      state.error = null
    }
  },
  extraReducers: (builder) => {
    builder
      // 获取学习进度列表
      .addCase(fetchLearningProgress.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchLearningProgress.fulfilled, (state, action) => {
        state.loading = false
        state.progressList = action.payload
      })
      .addCase(fetchLearningProgress.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload as string
      })
      // 获取学习统计
      .addCase(fetchProgressStats.pending, (state) => {
        state.loading = true
      })
      .addCase(fetchProgressStats.fulfilled, (state, action) => {
        state.loading = false
        state.stats = action.payload
      })
      .addCase(fetchProgressStats.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload as string
      })
      // 更新学习进度
      .addCase(updateLearningProgress.fulfilled, (state, action) => {
        const index = state.progressList.findIndex(p => p.paper_id === action.payload.paper_id)
        if (index !== -1) {
          state.progressList[index] = action.payload
        } else {
          state.progressList.push(action.payload)
        }
      })
  }
})

export const { clearProgressError } = progressSlice.actions
export default progressSlice.reducer
