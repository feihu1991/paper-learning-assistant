import { Link } from 'react-router-dom'
import { BookOpen, Heart, Sparkles } from 'lucide-react'
import './PaperCard.css'

interface PaperCardProps {
  paper: {
    id: string
    title: string
    authors: string[]
    abstract: string
    source: string
    published_date: string
    pdf_url?: string
    categories?: string[]
  }
  showScore?: boolean
  score?: number
  reason?: string
}

const PaperCard = ({ paper, showScore, score, reason }: PaperCardProps) => {
  return (
    <div className="paper-card">
      <div className="paper-card-header">
        <span className="paper-source">{paper.source}</span>
        <span className="paper-date">
          {new Date(paper.published_date).toLocaleDateString('zh-CN')}
        </span>
      </div>
      
      {showScore && (
        <div className="paper-score">
          <Sparkles size={14} className="score-icon" />
          <span className="score-value">{Math.round((score || 0) * 100)}% 匹配</span>
        </div>
      )}
      
      <Link to={`/paper/${paper.id}`} className="paper-title">
        {paper.title}
      </Link>
      
      <p className="paper-authors">
        {paper.authors.join(', ')}
      </p>
      
      <p className="paper-abstract">
        {paper.abstract.length > 200
          ? `${paper.abstract.substring(0, 200)}...`
          : paper.abstract}
      </p>

      {reason && (
        <p className="paper-reason">{reason}</p>
      )}
      
      <div className="paper-card-actions">
        <Link to={`/paper/${paper.id}`} className="action-btn primary">
          <BookOpen size={16} />
          开始学习
        </Link>
        <button className="action-btn secondary">
          <Heart size={16} />
          收藏
        </button>
      </div>
    </div>
  )
}

export default PaperCard
