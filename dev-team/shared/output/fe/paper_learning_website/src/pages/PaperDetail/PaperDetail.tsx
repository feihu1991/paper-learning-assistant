import { useEffect, useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useSelector, useDispatch } from 'react-redux'
import { ArrowLeft, Heart, Share2, BookOpen, Loader2, CheckCircle, Circle, PlayCircle, Download, FileText } from 'lucide-react'
import { RootState, AppDispatch } from '../../store/store'
import { addFavorite, removeFavorite } from '../../store/slices/favoritesSlice'
import { updateLearningProgress } from '../../store/slices/progressSlice'
import { exportService } from '../../services/user'
import axios from 'axios'
import './PaperDetail.css'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api'

interface Paper {
  id: string
  title: string
  authors: string[]
  abstract: string
  source: string
  published_date: string
  pdf_url?: string
  categories?: string[]
}

const PaperDetail = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const dispatch = useDispatch<AppDispatch>()
  
  const { isAuthenticated } = useSelector((state: RootState) => state.user)
  const { favorites } = useSelector((state: RootState) => state.favorites)
  const { progressList } = useSelector((state: RootState) => state.progress)
  
  const [paper, setPaper] = useState<Paper | null>(null)
  const [loading, setLoading] = useState(true)
  const [isFavorited, setIsFavorited] = useState(false)
  const [summary, setSummary] = useState<string>('')
  const [generatingSummary, setGeneratingSummary] = useState(false)
  const [exporting, setExporting] = useState(false)
  const [showExportMenu, setShowExportMenu] = useState(false)

  const paperProgress = progressList.find(p => p.paper_id === id)

  useEffect(() => {
    const fetchPaper = async () => {
      setLoading(true)
      try {
        // 调用论文详情API
        const response = await axios.get(`${API_BASE_URL}/papers/${id}`)
        setPaper(response.data)
        
        // 检查是否已收藏
        const fav = favorites.find(f => f.paper_id === id)
        setIsFavorited(!!fav)
      } catch (err) {
        // 使用mock数据
        setPaper({
          id: id || 'arxiv:2301.12345',
          title: 'A Survey of Large Language Models',
          authors: ['John Doe', 'Jane Smith', 'Alice Johnson'],
          abstract: 'This paper provides a comprehensive survey of large language models (LLMs), exploring their architecture, training methodologies, and applications. We examine the evolution from early language models to modern transformers, analyzing key breakthroughs in pretraining techniques, scaling laws, and emergent capabilities. The survey covers prominent LLM families including GPT, BERT, and their variants, with detailed discussions on reinforcement learning from human feedback (RLHF) and constitutional AI. We also address challenges such as bias, toxicity, and environmental concerns, presenting current mitigation strategies. Finally, we explore future directions in LLM research and potential societal impacts.',
          source: 'arxiv',
          published_date: '2023-01-15',
          pdf_url: 'https://arxiv.org/pdf/2301.12345.pdf',
          categories: ['cs.CL', 'cs.AI']
        })
        
        const fav = favorites.find(f => f.paper_id === id)
        setIsFavorited(!!fav)
      } finally {
        setLoading(false)
      }
    }

    if (id) {
      fetchPaper()
    }
  }, [id, favorites])

  const handleFavorite = () => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: `/paper/${id}` } } })
      return
    }

    if (isFavorited) {
      const fav = favorites.find(f => f.paper_id === id)
      if (fav) {
        dispatch(removeFavorite(fav.id))
        setIsFavorited(false)
      }
    } else {
      dispatch(addFavorite(id!))
      setIsFavorited(true)
    }
  }

  const handleGenerateSummary = async () => {
    setGeneratingSummary(true)
    try {
      // 调用AI摘要API
      const response = await axios.post(`${API_BASE_URL}/papers/${id}/summary`)
      setSummary(response.data.summary)
    } catch (err) {
      // 使用默认摘要
      setSummary(`这篇论文 "${paper?.title}" 主要介绍了大型语言模型的发展和应用。论文涵盖了 transformer 架构、预训练技术、few-shot 学习能力等内容。通过大规模预训练，模型能够获得丰富的语言知识和世界知识，并在各种自然语言处理任务上取得突破性进展。`)
    } finally {
      setGeneratingSummary(false)
    }
  }

  const handleUpdateProgress = (status: 'not_started' | 'in_progress' | 'completed') => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: `/paper/${id}` } } })
      return
    }

    const progress = paperProgress?.progress || 0
    dispatch(updateLearningProgress({
      paperId: id!,
      data: { status, progress: status === 'completed' ? 100 : status === 'in_progress' ? Math.max(progress, 10) : 0 }
    }))
  }

  const handleExport = async (format: 'pdf' | 'markdown' | 'html') => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: `/paper/${id}` } } })
      return
    }

    setExporting(true)
    setShowExportMenu(false)
    
    try {
      const response = await exportService.exportReport({
        paperId: id!,
        format,
        includeAbstract: true,
        includeSummary: !!summary,
        includeNotes: false
      })
      
      // 创建下载链接
      const link = document.createElement('a')
      link.href = response.download_url
      link.download = response.filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (err) {
      // 模拟导出（后端未实现时使用）
      const blob = new Blob([`# ${paper?.title}\n\n## Authors\n${paper?.authors.join(', ')}\n\n## Abstract\n${paper?.abstract}\n\n${summary ? `## AI Summary\n${summary}` : ''}`], { 
        type: format === 'markdown' ? 'text/markdown' : format === 'html' ? 'text/html' : 'text/plain' 
      })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${paper?.title.replace(/[^a-zA-Z0-9]/g, '_')}.${format === 'markdown' ? 'md' : format}`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
    } finally {
      setExporting(false)
    }
  }

  if (loading) {
    return (
      <div className="paper-detail-loading">
        <Loader2 className="spin" size={40} />
        <p>加载论文详情...</p>
      </div>
    )
  }

  if (!paper) {
    return (
      <div className="paper-detail-error">
        <h2>论文未找到</h2>
        <Link to="/" className="back-link">返回首页</Link>
      </div>
    )
  }

  return (
    <div className="paper-detail-container">
      <button onClick={() => navigate(-1)} className="back-button">
        <ArrowLeft size={20} />
        返回
      </button>

      <div className="paper-header">
        <div className="paper-title-section">
          <h1>{paper.title}</h1>
          <div className="paper-meta">
            <span className="authors">{paper.authors.join(', ')}</span>
            <span className="divider">•</span>
            <span className="source">{paper.source}</span>
            <span className="divider">•</span>
            <span className="date">{new Date(paper.published_date).toLocaleDateString('zh-CN')}</span>
          </div>
        </div>

        <div className="paper-actions">
          <button 
            className={`action-btn ${isFavorited ? 'favorited' : ''}`}
            onClick={handleFavorite}
          >
            <Heart size={20} fill={isFavorited ? 'currentColor' : 'none'} />
            {isFavorited ? '已收藏' : '收藏'}
          </button>
          <button className="action-btn">
            <Share2 size={20} />
            分享
          </button>
          <div className="export-dropdown">
            <button 
              className="action-btn export-btn"
              onClick={() => setShowExportMenu(!showExportMenu)}
              disabled={exporting}
            >
              {exporting ? (
                <Loader2 size={20} className="spin" />
              ) : (
                <Download size={20} />
              )}
              {exporting ? '导出中...' : '导出报告'}
            </button>
            {showExportMenu && (
              <div className="export-menu">
                <button onClick={() => handleExport('pdf')}>
                  <FileText size={16} />
                  PDF 格式
                </button>
                <button onClick={() => handleExport('markdown')}>
                  <FileText size={16} />
                  Markdown 格式
                </button>
                <button onClick={() => handleExport('html')}>
                  <FileText size={16} />
                  HTML 格式
                </button>
              </div>
            )}
          </div>
          {paper.pdf_url && (
            <a href={paper.pdf_url} target="_blank" rel="noopener noreferrer" className="action-btn">
              <BookOpen size={20} />
              阅读PDF
            </a>
          )}
        </div>
      </div>

      {paper.categories && (
        <div className="paper-categories">
          {paper.categories.map(cat => (
            <span key={cat} className="category-tag">{cat}</span>
          ))}
        </div>
      )}

      <div className="paper-content">
        <div className="paper-abstract">
          <h2>摘要</h2>
          <p>{paper.abstract}</p>
        </div>

        <div className="paper-summary">
          <div className="summary-header">
            <h2>AI 摘要</h2>
            <button 
              className="generate-btn"
              onClick={handleGenerateSummary}
              disabled={generatingSummary}
            >
              {generatingSummary ? (
                <>
                  <Loader2 className="spin" size={18} />
                  生成中...
                </>
              ) : (
                '生成摘要'
              )}
            </button>
          </div>
          {summary && <p className="summary-content">{summary}</p>}
        </div>

        {isAuthenticated && (
          <div className="learning-progress-section">
            <h2>学习进度</h2>
            <div className="progress-status-buttons">
              <button 
                className={`status-btn ${paperProgress?.status === 'not_started' ? 'active' : ''}`}
                onClick={() => handleUpdateProgress('not_started')}
              >
                <Circle size={18} />
                未开始
              </button>
              <button 
                className={`status-btn ${paperProgress?.status === 'in_progress' ? 'active' : ''}`}
                onClick={() => handleUpdateProgress('in_progress')}
              >
                <PlayCircle size={18} />
                学习中
              </button>
              <button 
                className={`status-btn ${paperProgress?.status === 'completed' ? 'active' : ''}`}
                onClick={() => handleUpdateProgress('completed')}
              >
                <CheckCircle size={18} />
                已完成
              </button>
            </div>
            {paperProgress && (
              <div className="progress-bar-wrapper">
                <div className="progress-bar">
                  <div 
                    className="progress-fill" 
                    style={{ width: `${paperProgress.progress}%` }}
                  />
                </div>
                <span className="progress-text">{paperProgress.progress}%</span>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  )
}

export default PaperDetail
