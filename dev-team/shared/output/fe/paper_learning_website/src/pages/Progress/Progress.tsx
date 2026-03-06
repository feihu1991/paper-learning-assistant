import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { Bar, Doughnut } from 'react-chartjs-2'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js'
import { BookOpen, Clock, TrendingUp, Loader2, CheckCircle, Circle, PlayCircle } from 'lucide-react'
import { fetchLearningProgress, fetchProgressStats } from '../../store/slices/progressSlice'
import { RootState, AppDispatch } from '../../store/store'
import './Progress.css'

// 注册 Chart.js 组件
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend, ArcElement)

const Progress = () => {
  const dispatch = useDispatch<AppDispatch>()
  const navigate = useNavigate()
  const { progressList, stats, loading, error } = useSelector(
    (state: RootState) => state.progress
  )
  const { isAuthenticated } = useSelector((state: RootState) => state.user)
  
  const [activeTab, setActiveTab] = useState<'overview' | 'list'>('overview')

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: '/progress' } } })
      return
    }
    dispatch(fetchLearningProgress())
    dispatch(fetchProgressStats())
  }, [dispatch, isAuthenticated, navigate])

  if (loading) {
    return (
      <div className="progress-loading">
        <Loader2 className="spin" size={40} />
        <p>加载中...</p>
      </div>
    )
  }

  // 模拟数据（当后端API未实现时使用）
  const mockStats = stats || {
    total_papers: progressList.length || 5,
    completed_papers: progressList.filter(p => p.status === 'completed').length || 2,
    in_progress_papers: progressList.filter(p => p.status === 'in_progress').length || 2,
    total_learning_time: 120,
    weekly_progress: [
      { date: '周一', progress: 20 },
      { date: '周二', progress: 35 },
      { date: '周三', progress: 15 },
      { date: '周四', progress: 50 },
      { date: '周五', progress: 40 },
      { date: '周六', progress: 60 },
      { date: '周日', progress: 30 },
    ]
  }

  const mockProgressList = progressList.length > 0 ? progressList : [
    { paper_id: '1', title: 'Large Language Models Survey', status: 'completed', progress: 100, last_accessed: '2024-01-15', completed_sections: ['abstract', 'introduction', 'methods'] },
    { paper_id: '2', title: 'Transformer Architecture', status: 'in_progress', progress: 65, last_accessed: '2024-01-14', completed_sections: ['abstract', 'introduction'] },
    { paper_id: '3', title: 'Attention Mechanisms', status: 'in_progress', progress: 30, last_accessed: '2024-01-13', completed_sections: ['abstract'] },
    { paper_id: '4', title: 'BERT Pre-training', status: 'not_started', progress: 0, last_accessed: '2024-01-10', completed_sections: [] },
    { paper_id: '5', title: 'GPT Applications', status: 'not_started', progress: 0, last_accessed: '2024-01-09', completed_sections: [] },
  ]

  // Chart 配置
  const barChartData = {
    labels: mockStats.weekly_progress.map(w => w.date),
    datasets: [
      {
        label: '学习进度 (%)',
        data: mockStats.weekly_progress.map(w => w.progress),
        backgroundColor: 'rgba(102, 126, 234, 0.8)',
        borderColor: 'rgba(102, 126, 234, 1)',
        borderWidth: 1,
        borderRadius: 6,
      },
    ],
  }

  const barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: {
          callback: (value: any) => `${value}%`,
        },
      },
    },
  }

  const doughnutData = {
    labels: ['已完成', '学习中', '未开始'],
    datasets: [
      {
        data: [
          mockStats.completed_papers,
          mockStats.in_progress_papers,
          mockStats.total_papers - mockStats.completed_papers - mockStats.in_progress_papers,
        ],
        backgroundColor: ['#10b981', '#f59e0b', '#e5e7eb'],
        borderWidth: 0,
      },
    ],
  }

  const doughnutOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'bottom' as const,
      },
    },
    cutout: '65%',
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'completed':
        return <CheckCircle className="status-icon completed" size={20} />
      case 'in_progress':
        return <PlayCircle className="status-icon in-progress" size={20} />
      default:
        return <Circle className="status-icon not-started" size={20} />
    }
  }

  const getStatusText = (status: string) => {
    switch (status) {
      case 'completed':
        return '已完成'
      case 'in_progress':
        return '学习中'
      default:
        return '未开始'
    }
  }

  return (
    <div className="progress-page">
      <div className="progress-header">
        <h1>学习进度</h1>
        <div className="progress-tabs">
          <button
            className={`tab-btn ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            概览
          </button>
          <button
            className={`tab-btn ${activeTab === 'list' ? 'active' : ''}`}
            onClick={() => setActiveTab('list')}
          >
            详细列表
          </button>
        </div>
      </div>

      {error && <div className="progress-error">{error}</div>}

      {/* 统计卡片 */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon blue">
            <BookOpen size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-value">{mockStats.total_papers}</span>
            <span className="stat-label">学习论文数</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon green">
            <CheckCircle size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-value">{mockStats.completed_papers}</span>
            <span className="stat-label">已完成</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon orange">
            <Clock size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-value">{mockStats.total_learning_time}</span>
            <span className="stat-label">学习分钟</span>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon purple">
            <TrendingUp size={24} />
          </div>
          <div className="stat-content">
            <span className="stat-value">
              {Math.round((mockStats.completed_papers / mockStats.total_papers) * 100) || 0}%
            </span>
            <span className="stat-label">完成率</span>
          </div>
        </div>
      </div>

      {activeTab === 'overview' ? (
        <div className="charts-section">
          {/* 进度分布饼图 */}
          <div className="chart-card">
            <h3>学习进度分布</h3>
            <div className="chart-container doughnut">
              <Doughnut data={doughnutData} options={doughnutOptions} />
            </div>
          </div>

          {/* 周学习趋势图 */}
          <div className="chart-card">
            <h3>本周学习趋势</h3>
            <div className="chart-container bar">
              <Bar data={barChartData} options={barChartOptions} />
            </div>
          </div>
        </div>
      ) : (
        <div className="progress-list">
          {mockProgressList.map((item) => (
            <div key={item.paper_id} className="progress-item">
              <div className="progress-item-header">
                {getStatusIcon(item.status)}
                <Link to={`/paper/${item.paper_id}`} className="progress-item-title">
                  {item.title}
                </Link>
              </div>
              <div className="progress-item-content">
                <div className="progress-bar-container">
                  <div className="progress-bar">
                    <div
                      className={`progress-fill ${item.status}`}
                      style={{ width: `${item.progress}%` }}
                    />
                  </div>
                  <span className="progress-percentage">{item.progress}%</span>
                </div>
                <div className="progress-item-meta">
                  <span className={`status-badge ${item.status}`}>
                    {getStatusText(item.status)}
                  </span>
                  <span className="last-accessed">
                    最后学习: {new Date(item.last_accessed).toLocaleDateString('zh-CN')}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Progress
