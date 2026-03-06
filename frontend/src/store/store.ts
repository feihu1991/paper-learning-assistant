import { configureStore } from '@reduxjs/toolkit'
import searchReducer from './slices/searchSlice'
import userReducer from './slices/userSlice'
import paperReducer from './slices/paperSlice'

export const store = configureStore({
  reducer: {
    search: searchReducer,
    user: userReducer,
    paper: paperReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch