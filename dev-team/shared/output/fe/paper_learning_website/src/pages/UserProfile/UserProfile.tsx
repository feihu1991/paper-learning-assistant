import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { User, Mail, Calendar, BookOpen, Clock, Award } from 'lucide-react'
import { RootState, AppDispatch } from '../../store/store'
import { fetchCurrentUser } from '../../store/slices/userSlice'
import { fetchLearningProgress, fetchProgressStats } from '../../store/slices/progressSlice'
import './UserProfile.css'

const UserProfile = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  
  const { user, isAuthenticated, loading } = useSelector((state: RootState) => state.user)
  const { progressList, stats } = useSelector((state: RootState) => state.progress)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: '/profile' } } })
      return
    }
    
    dispatch(fetchCurrentUser())
    dispatch(fetchLearningProgress())
    dispatch(fetchProgressStats())
  }, [dispatch, isAuthenticated, navigate])

  if (loading || !user) {
    return (
      <div className="profile-loading">
        <div className="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    )
  }

  const completedPapers = progressList.filter(p => p.status === 'completed').length
  const inProgressPapers = progressList.filter(p => p.status === 'in_progress').length

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-avatar">
          <User size={48} />
        </div>
        <div className="profile-info">
          <h1>{user.username}</h1>
          <div className="profile-meta">
            <span className="meta-item">
              <Mail size={16} />
              {user.email}
            </span>
            <span className="meta-item">
              <Calendar size={16} />
              加入于 {new Date(user.created_at).toLocaleDateString('zh-CN')}
            </span>
          </div>
        </div>
      </div>

      <div className="profile-stats">
        <div className="stat-card">
          <div className="stat-icon">
            <BookOpen size={24} />
          </div>
          <div className="stat-details">
            <span className="stat-number">{stats?.total_papers || progressList.length}</span>
            <span className="stat-label">学习论文</span>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon completed">
            <Award size={24} />
          </div>
          <div className="stat-details">
            <span className="stat-number">{completedPapers}</span>
            <span className="stat-label">已完成</span>
          </div>
        </div>
        
        <div className="stat-card">
          <div className="stat-icon in-progress">
            <Clock size={24} />
          </div>
          <div className="stat-details">
            <span className="stat-number">{inProgressPapers}</span>
            <span className="stat-label">学习中</span>
          </div>
        </div>
      </div>

      <div className="profile-sections">
        <section className="profile-section">
          <h2>学习概览</h2>
          <div className="section-content">
            <p>
              欢迎来到您的学习空间！您目前正在学习 {progressList.length} 篇论文，
              其中 {completedPapers} 篇已完成。
            </p>
            {stats?.total_learning_time && (
              <p>累计学习时间: {stats.total_learning_time} 分钟</p>
            )}
          </div>
        </section>

        <section className="profile-section">
          <h2>快速链接</h2>
          <div className="quick-links">
            <button 
              className="quick-link-btn"
              onClick={() => navigate('/favorites')}
            >
              📚 我的收藏
            </button>
            <button 
              className="quick-link-btn"
              onClick={() => navigate('/progress')}
            >
              📊 学习进度
            </button>
          </div>
        </section>
      </div>
    </div>
  )
}

export default UserProfile
