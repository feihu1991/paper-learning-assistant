import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { Provider } from 'react-redux'
import { store } from './store/store'
import MainLayout from './layouts/MainLayout'
import Home from './pages/Home/Home'
import PaperDetail from './pages/PaperDetail/PaperDetail'
import UserProfile from './pages/UserProfile/UserProfile'
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
          </Routes>
        </MainLayout>
      </Router>
    </Provider>
  )
}

export default App