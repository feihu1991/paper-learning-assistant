import { configureStore } from '@reduxjs/toolkit';
import { combineReducers } from 'redux';

// Create a simple placeholder reducer
const rootReducer = combineReducers({
  // We'll add actual reducers later
  placeholder: (state = {}) => state,
});

export const store = configureStore({
  reducer: rootReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;