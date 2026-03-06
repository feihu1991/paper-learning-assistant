import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { RootState, AppDispatch } from '../store/store'
import { logout } from '../store/slices/userSlice'
import './MainLayout.css'

interface MainLayoutProps {
  children: React.ReactNode
}

const MainLayout = ({ children }: MainLayoutProps) => {
  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useDispatch<AppDispatch>()
  const { user, isAuthenticated } = useSelector((state: RootState) => state.user)
  const [isMenuOpen, setIsMenuOpen] = useState(false)

  const handleLogout = () => {
    dispatch(logout())
    navigate('/')
    setIsMenuOpen(false)
  }

  const isActive = (path: string) => location.pathname === path

  return (
    <div className="main-layout">
      <header className="navbar">
        <div className="navbar-container">
          <Link to="/" className="navbar-brand">
            📚 Paper Learning
          </Link>

          {/* 桌面端导航 */}
          <nav className="navbar-nav">
            <Link 
              to="/" 
              className={`nav-link ${isActive('/') ? 'active' : ''}`}
            >
              首页
            </Link>
            
            {isAuthenticated && (
              <>
                <Link 
                  to="/favorites" 
                  className={`nav-link ${isActive('/favorites') ? 'active' : ''}`}
                >
                  收藏列表
                </Link>
                <Link 
                  to="/progress" 
                  className={`nav-link ${isActive('/progress') ? 'active' : ''}`}
                >
                  学习进度
                </Link>
              </>
            )}
          </nav>

          {/* 用户菜单 */}
          <div className="navbar-user">
            {isAuthenticated ? (
              <div className="user-menu">
                <span className="user-name">你好, {user?.username}</span>
                <button 
                  onClick={handleLogout}
                  className="btn btn-outline btn-sm"
                >
                  登出
                </button>
              </div>
            ) : (
              <div className="auth-buttons">
                <Link 
                  to="/auth?mode=login" 
                  className={`btn btn-outline btn-sm ${isActive('/auth') && isActive('/auth?mode=login') ? '' : ''}`}
                >
                  登录
                </Link>
                <Link 
                  to="/auth?mode=register" 
                  className="btn btn-primary btn-sm"
                >
                  注册
                </Link>
              </div>
            )}
          </div>

          {/* 移动端菜单按钮 */}
          <button 
            className="mobile-menu-btn"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            ☰
          </button>
        </div>

        {/* 移动端导航菜单 */}
        {isMenuOpen && (
          <nav className="navbar-nav-mobile">
            <Link 
              to="/" 
              className={`nav-link ${isActive('/') ? 'active' : ''}`}
              onClick={() => setIsMenuOpen(false)}
            >
              首页
            </Link>
            
            {isAuthenticated ? (
              <>
                <Link 
                  to="/favorites" 
                  className={`nav-link ${isActive('/favorites') ? 'active' : ''}`}
                  onClick={() => setIsMenuOpen(false)}
                >
                  收藏列表
                </Link>
                <Link 
                  to="/progress" 
                  className={`nav-link ${isActive('/progress') ? 'active' : ''}`}
                  onClick={() => setIsMenuOpen(false)}
                >
                  学习进度
                </Link>
                <button onClick={handleLogout} className="nav-link btn-logout">
                  登出
                </button>
              </>
            ) : (
              <>
                <Link 
                  to="/auth?mode=login" 
                  className="nav-link"
                  onClick={() => setIsMenuOpen(false)}
                >
                  登录
                </Link>
                <Link 
                  to="/auth?mode=register" 
                  className="nav-link"
                  onClick={() => setIsMenuOpen(false)}
                >
                  注册
                </Link>
              </>
            )}
          </nav>
        )}
      </header>

      <main className="main-content">
        {children}
      </main>

      <footer className="footer">
        <p>© 2024 Paper Learning. All rights reserved.</p>
      </footer>
    </div>
  )
}

export default MainLayout
