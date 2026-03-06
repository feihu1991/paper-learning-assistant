import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { User, Mail, Calendar, BookOpen, Clock, Award, Save, X, Loader2, Building, FileText } from 'lucide-react'
import { RootState, AppDispatch } from '../../store/store'
import { fetchCurrentUser, fetchUserProfile, updateUserProfile } from '../../store/slices/userSlice'
import { fetchLearningProgress, fetchProgressStats } from '../../store/slices/progressSlice'
import './UserProfile.css'
import './UserProfile.css'

interface ProfileFormData {
  username: string
  email: string
  bio: string
  institution: string
}

const UserProfile = () => {
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  
  const { user, isAuthenticated, loading, profile, profileLoading, profileError } = useSelector((state: RootState) => state.user)
  const { progressList, stats } = useSelector((state: RootState) => state.progress)

  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState<ProfileFormData>({
    username: '',
    email: '',
    bio: '',
    institution: ''
  })
  const [saveSuccess, setSaveSuccess] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: '/profile' } } })
      return
    }
    
    dispatch(fetchCurrentUser())
    dispatch(fetchUserProfile())
    dispatch(fetchLearningProgress())
    dispatch(fetchProgressStats())
  }, [dispatch, isAuthenticated, navigate])

  useEffect(() => {
    if (profile) {
      setFormData({
        username: profile.username || user?.username || '',
        email: profile.email || user?.email || '',
        bio: profile.bio || '',
        institution: profile.institution || ''
      })
    } else if (user) {
      setFormData({
        username: user.username,
        email: user.email,
        bio: '',
        institution: ''
      })
    }
  }, [profile, user])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSaveSuccess(false)
    
    await dispatch(updateUserProfile(formData))
    setSaveSuccess(true)
    setIsEditing(false)
    
    setTimeout(() => setSaveSuccess(false), 3000)
  }

  const handleCancel = () => {
    if (profile) {
      setFormData({
        username: profile.username || user?.username || '',
        email: profile.email || user?.email || '',
        bio: profile.bio || '',
        institution: profile.institution || ''
      })
    }
    setIsEditing(false)
  }

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
        {!isEditing && (
          <button 
            className="edit-profile-btn"
            onClick={() => setIsEditing(true)}
          >
            编辑资料
          </button>
        )}
      </div>

      {isEditing && (
        <div className="profile-edit-form">
          <h2>编辑个人资料</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="username">
                <User size={16} />
                用户名
              </label>
              <input
                type="text"
                id="username"
                name="username"
                value={formData.username}
                onChange={handleInputChange}
                placeholder="请输入用户名"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="email">
                <Mail size={16} />
                邮箱
              </label>
              <input
                type="email"
                id="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="请输入邮箱"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="institution">
                <Building size={16} />
                机构/学校
              </label>
              <input
                type="text"
                id="institution"
                name="institution"
                value={formData.institution}
                onChange={handleInputChange}
                placeholder="请输入机构或学校名称"
              />
            </div>
            
            <div className="form-group">
              <label htmlFor="bio">
                <FileText size={16} />
                个人简介
              </label>
              <textarea
                id="bio"
                name="bio"
                value={formData.bio}
                onChange={handleInputChange}
                placeholder="请介绍一下自己..."
                rows={4}
              />
            </div>

            {profileError && (
              <div className="form-error">{profileError}</div>
            )}

            {saveSuccess && (
              <div className="form-success">保存成功！</div>
            )}

            <div className="form-actions">
              <button 
                type="submit" 
                className="save-btn"
                disabled={profileLoading}
              >
                {profileLoading ? (
                  <>
                    <Loader2 size={18} className="spin" />
                    保存中...
                  </>
                ) : (
                  <>
                    <Save size={18} />
                    保存
                  </>
                )}
              </button>
              <button 
                type="button" 
                className="cancel-btn"
                onClick={handleCancel}
                disabled={profileLoading}
              >
                <X size={18} />
                取消
              </button>
            </div>
          </form>
        </div>
      )}

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
