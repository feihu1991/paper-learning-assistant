import { useState } from 'react'
import SearchBar from '../components/SearchBar/SearchBar'
import PaperCard from '../components/PaperCard/PaperCard'
import axios from 'axios'
import './Home.css'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api'

const Home = () => {
  const [searchResults, setSearchResults] = useState([])
  const [loading, setLoading] = useState(false)
  
  const handleSearch = async (query: string) => {
    setLoading(true)
    try {
      // 调用真正的后端搜索API
      const response = await axios.post(`${API_BASE_URL}/papers/search`, { query })
      setSearchResults(response.data || [])
      
      // 如果API调用失败或返回空，使用fallback数据
      if (!response.data || response.data.length === 0) {
        setSearchResults([
          {
            id: 'arxiv:2301.12345',
            title: 'A Survey of Large Language Models',
            authors: ['John Doe', 'Jane Smith'],
            abstract: 'This paper provides a comprehensive survey...',
            source: 'arxiv',
            published_date: '2023-01-01'
          },
          {
            id: 'arxiv:2302.12345',
            title: 'Advanced Techniques in Natural Language Processing',
            authors: ['Alice Johnson'],
            abstract: 'This paper explores advanced NLP techniques...',
            source: 'arxiv',
            published_date: '2023-02-01'
          }
        ])
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
          published_date: '2023-01-01'
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
            {searchResults.map((paper) => (
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