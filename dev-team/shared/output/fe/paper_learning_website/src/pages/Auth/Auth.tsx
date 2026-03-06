import { useState, useEffect } from 'react'
import { useNavigate, useLocation, Link } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import { Eye, EyeOff, LogIn, UserPlus, Loader2 } from 'lucide-react'
import { login, register, clearError } from '../../store/slices/userSlice'
import { RootState, AppDispatch } from '../../store/store'
import './Auth.css'

type AuthMode = 'login' | 'register'

const Auth = () => {
  const [mode, setMode] = useState<AuthMode>('login')
  const [showPassword, setShowPassword] = useState(false)
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: ''
  })

  const navigate = useNavigate()
  const location = useLocation()
  const dispatch = useDispatch<AppDispatch>()
  
  const { loading, error, isAuthenticated } = useSelector(
    (state: RootState) => state.user
  )

  const from = (location.state as any)?.from?.pathname || '/'

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, from])

  useEffect(() => {
    return () => {
      dispatch(clearError())
    }
  }, [dispatch])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (mode === 'login') {
      dispatch(login({ username: formData.username, password: formData.password }))
    } else {
      dispatch(register({
        username: formData.username,
        email: formData.email,
        password: formData.password
      }))
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const toggleMode = () => {
    setMode(mode === 'login' ? 'register' : 'login')
    setFormData({ username: '', email: '', password: '' })
    dispatch(clearError())
  }

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-card">
          <div className="auth-header">
            <h1>{mode === 'login' ? '登录' : '注册'}</h1>
            <p>
              {mode === 'login' 
                ? '欢迎回来！请登录您的账户' 
                : '创建新账户，开始您的论文学习之旅'}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label htmlFor="username">用户名</label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
                placeholder="请输入用户名"
                required
                autoComplete="username"
              />
            </div>

            {mode === 'register' && (
              <div className="form-group">
                <label htmlFor="email">邮箱</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  placeholder="请输入邮箱"
                  required
                  autoComplete="email"
                />
              </div>
            )}

            <div className="form-group">
              <label htmlFor="password">密码</label>
              <div className="password-input-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleInputChange}
                  placeholder="请输入密码"
                  required
                  autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                </button>
              </div>
            </div>

            {error && (
              <div className="auth-error">
                {error}
              </div>
            )}

            <button
              type="submit"
              className="auth-submit-btn"
              disabled={loading}
            >
              {loading ? (
                <>
                  <Loader2 className="spin" size={20} />
                  {mode === 'login' ? '登录中...' : '注册中...'}
                </>
              ) : (
                <>
                  {mode === 'login' ? <LogIn size={20} /> : <UserPlus size={20} />}
                  {mode === 'login' ? '登录' : '注册'}
                </>
              )}
            </button>
          </form>

          <div className="auth-footer">
            <p>
              {mode === 'login' ? '还没有账户？' : '已有账户？'}
              <button
                type="button"
                className="auth-switch-btn"
                onClick={toggleMode}
              >
                {mode === 'login' ? '立即注册' : '立即登录'}
              </button>
            </p>
          </div>

          <div className="auth-back">
            <Link to="/">返回首页</Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Auth
