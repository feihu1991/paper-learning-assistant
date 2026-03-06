import { Link } from 'react-router-dom'
import { BookOpen, Heart, ExternalLink } from 'lucide-react'
import './PaperCard.css'

interface PaperCardProps {
  paper: {
    id: string
    title: string
    authors: string[]
    abstract: string
    source: string
    published_date: string
  }
}

const PaperCard = ({ paper }: PaperCardProps) => {
  return (
    <div className="paper-card">
      <div className="paper-card-header">
        <span className="paper-source">{paper.source}</span>
        <span className="paper-date">
          {new Date(paper.published_date).toLocaleDateString('zh-CN')}
        </span>
      </div>
      
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
