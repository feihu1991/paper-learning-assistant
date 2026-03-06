import { configureStore } from '@reduxjs/toolkit'
import userReducer from './slices/userSlice'
import favoritesReducer from './slices/favoritesSlice'
import progressReducer from './slices/progressSlice'

export const store = configureStore({
  reducer: {
    user: userReducer,
    favorites: favoritesReducer,
    progress: progressReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch