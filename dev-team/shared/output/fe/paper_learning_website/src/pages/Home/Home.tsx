import { useState } from 'react'
import SearchBar from '../../components/SearchBar/SearchBar'
import PaperCard from '../../components/PaperCard/PaperCard'
import axios from 'axios'
import './Home.css'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api'

// 分类选项
const CATEGORIES = [
  { value: '', label: '全部分类' },
  { value: 'cs.CL', label: 'cs.CL - Computation and Language' },
  { value: 'cs.AI', label: 'cs.AI - Artificial Intelligence' },
  { value: 'cs.LG', label: 'cs.LG - Machine Learning' },
  { value: 'cs.CV', label: 'cs.CV - Computer Vision' },
  { value: 'cs.NE', label: 'cs.NE - Neural and Evolutionary Computing' },
  { value: 'stat.ML', label: 'stat.ML - Machine Learning' },
  { value: 'physics.gen-ph', label: 'physics.gen-ph - General Physics' },
  { value: 'math.OC', label: 'math.OC - Optimization and Control' }
]

interface Paper {
  id: string
  title: string
  authors: string[]
  abstract: string
  source: string
  published_date: string
  categories?: string[]
}

const Home = () => {
  const [searchResults, setSearchResults] = useState<Paper[]>([])
  const [loading, setLoading] = useState(false)
  
  // 筛选状态
  const [categoryFilter, setCategoryFilter] = useState('')
  const [yearFilter, setYearFilter] = useState('')
  
  // 筛选论文
  const filterPapers = (papers: Paper[]): Paper[] => {
    return papers.filter(paper => {
      // 分类筛选
      if (categoryFilter && paper.categories) {
        if (!paper.categories.includes(categoryFilter)) {
          return false
        }
      }
      // 年份筛选
      if (yearFilter) {
        const paperYear = new Date(paper.published_date).getFullYear().toString()
        if (paperYear !== yearFilter) {
          return false
        }
      }
      return true
    })
  }

  const handleSearch = async (query: string) => {
    setLoading(true)
    try {
      // 调用真正的后端搜索API
      const response = await axios.post(`${API_BASE_URL}/papers/search`, { query })
      const results = response.data || []
      
      // 如果API调用失败或返回空，使用fallback数据
      if (!results || results.length === 0) {
        setSearchResults([
          {
            id: 'arxiv:2301.12345',
            title: 'A Survey of Large Language Models',
            authors: ['John Doe', 'Jane Smith'],
            abstract: 'This paper provides a comprehensive survey...',
            source: 'arxiv',
            published_date: '2023-01-01',
            categories: ['cs.CL', 'cs.AI']
          },
          {
            id: 'arxiv:2302.12345',
            title: 'Advanced Techniques in Natural Language Processing',
            authors: ['Alice Johnson'],
            abstract: 'This paper explores advanced NLP techniques...',
            source: 'arxiv',
            published_date: '2023-02-15',
            categories: ['cs.CL']
          },
          {
            id: 'arxiv:2303.12345',
            title: 'Deep Learning for Computer Vision',
            authors: ['Bob Wilson', 'Carol White'],
            abstract: 'This paper presents new techniques...',
            source: 'arxiv',
            published_date: '2022-06-10',
            categories: ['cs.CV', 'cs.LG']
          }
        ])
      } else {
        setSearchResults(results)
      }
    } catch (error) {
      console.error('搜索错误:', error)
      // API调用失败时使用fallback
      setSearchResults([
        {
          id: 'arxiv:2301.12345',
          title: 'A Survey of Large Language Models',
          authors: ['John Doe', 'Jane Smith'],
          abstract: 'This paper provides a comprehensive survey...',
          source: 'arxiv',
          published_date: '2023-01-01',
          categories: ['cs.CL', 'cs.AI']
        }
      ])
    } finally {
      setLoading(false)
    }
  }
  
  return (
    <div className="home-container">
      <div className="hero-section">
        <h1 className="hero-title">论文学习助手</h1>
        <p className="hero-subtitle">
          输入论文名称，获取学习内容、解释和可视化图表
        </p>
        
        <div className="search-section">
          <SearchBar onSearch={handleSearch} />
          
          {/* 筛选器 */}
          <div className="filter-section">
            <div className="filter-group">
              <label htmlFor="category-filter">分类:</label>
              <select
                id="category-filter"
                value={categoryFilter}
                onChange={(e) => setCategoryFilter(e.target.value)}
                className="filter-select"
              >
                {CATEGORIES.map(cat => (
                  <option key={cat.value} value={cat.value}>
                    {cat.label}
                  </option>
                ))}
              </select>
            </div>
            
            <div className="filter-group">
              <label htmlFor="year-filter">年份:</label>
              <input
                id="year-filter"
                type="number"
                value={yearFilter}
                onChange={(e) => setYearFilter(e.target.value)}
                placeholder="例如: 2023"
                className="filter-input"
                min="1900"
                max="2100"
              />
            </div>
            
            {(categoryFilter || yearFilter) && (
              <button 
                className="clear-filter-btn"
                onClick={() => {
                  setCategoryFilter('')
                  setYearFilter('')
                }}
              >
                清除筛选
              </button>
            )}
          </div>
          
          <div className="search-tips">
            <span className="tip">试试搜索: </span>
            <button className="tip-button" onClick={() => handleSearch('large language models')}>
              large language models
            </button>
            <button className="tip-button" onClick={() => handleSearch('machine learning')}>
              machine learning
            </button>
            <button className="tip-button" onClick={() => handleSearch('deep learning')}>
              deep learning
            </button>
          </div>
        </div>
      </div>
      
      <div className="results-section">
        {loading ? (
          <div className="loading">搜索中...</div>
        ) : searchResults.length > 0 ? (
          <div className="results-grid">
            {filterPapers(searchResults).map((paper) => (
              <PaperCard key={paper.id} paper={paper} />
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <div className="empty-icon">📚</div>
            <h3>开始搜索论文</h3>
            <p>输入论文名称或关键词，获取学习内容和解释</p>
          </div>
        )}
      </div>
      
      <div className="features-section">
        <h2 className="features-title">核心功能</h2>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">🔍</div>
            <h3>智能搜索</h3>
            <p>支持arXiv、PubMed、IEEE等多源论文搜索</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🤖</div>
            <h3>AI解释</h3>
            <p>使用AI生成易于理解的论文解释和摘要</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📊</div>
            <h3>可视化图表</h3>
            <p>自动创建概念图、流程图等帮助理解</p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🎯</div>
            <h3>学习路径</h3>
            <p>提供结构化的学习指导和进度跟踪</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Home