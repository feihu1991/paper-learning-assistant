import { createSlice, createAsyncThunk } from '@reduxjs/toolkit'
import { Favorite } from '../../types'
import { favoritesService } from '../../services/favorites'

interface FavoritesState {
  favorites: Favorite[]
  loading: boolean
  error: string | null
}

const initialState: FavoritesState = {
  favorites: [],
  loading: false,
  error: null
}

export const fetchFavorites = createAsyncThunk(
  'favorites/fetchFavorites',
  async (_, { rejectWithValue }) => {
    try {
      const favorites = await favoritesService.getFavorites()
      return favorites
    } catch (error: any) {
      return rejectWithValue(error.message || '获取收藏列表失败')
    }
  }
)

export const addFavorite = createAsyncThunk(
  'favorites/addFavorite',
  async (paperId: string, { rejectWithValue }) => {
    try {
      const favorite = await favoritesService.addFavorite(paperId)
      return favorite
    } catch (error: any) {
      return rejectWithValue(error.message || '添加收藏失败')
    }
  }
)

export const removeFavorite = createAsyncThunk(
  'favorites/removeFavorite',
  async (favoriteId: string, { rejectWithValue }) => {
    try {
      await favoritesService.removeFavorite(favoriteId)
      return favoriteId
    } catch (error: any) {
      return rejectWithValue(error.message || '移除收藏失败')
    }
  }
)

const favoritesSlice = createSlice({
  name: 'favorites',
  initialState,
  reducers: {
    clearFavoritesError: (state) => {
      state.error = null
    }
  },
  extraReducers: (builder) => {
    builder
      // 获取收藏列表
      .addCase(fetchFavorites.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(fetchFavorites.fulfilled, (state, action) => {
        state.loading = false
        state.favorites = action.payload
      })
      .addCase(fetchFavorites.rejected, (state, action) => {
        state.loading = false
        state.error = action.payload as string
      })
      // 添加收藏
      .addCase(addFavorite.fulfilled, (state, action) => {
        state.favorites.unshift(action.payload)
      })
      // 移除收藏
      .addCase(removeFavorite.fulfilled, (state, action) => {
        state.favorites = state.favorites.filter(f => f.id !== action.payload)
      })
  }
})

export const { clearFavoritesError } = favoritesSlice.actions
export default favoritesSlice.reducer
