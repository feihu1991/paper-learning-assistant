import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { Provider } from 'react-redux'
import { store } from './store/store'
import MainLayout from './layouts/MainLayout'
import Home from './pages/Home/Home'
import PaperDetail from './pages/PaperDetail/PaperDetail'
import UserProfile from './pages/UserProfile/UserProfile'
import Auth from './pages/Auth/Auth'
import Favorites from './pages/Favorites/Favorites'
import Progress from './pages/Progress/Progress'
import Recommendations from './pages/Recommendations/Recommendations'
import './App.css'

function App() {
  return (
    <Provider store={store}>
      <Router>
        <MainLayout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/paper/:id" element={<PaperDetail />} />
            <Route path="/profile" element={<UserProfile />} />
            <Route path="/auth" element={<Auth />} />
            <Route path="/favorites" element={<Favorites />} />
            <Route path="/progress" element={<Progress />} />
            <Route path="/recommendations" element={<Recommendations />} />
          </Routes>
        </MainLayout>
      </Router>
    </Provider>
  )
}

export default App
