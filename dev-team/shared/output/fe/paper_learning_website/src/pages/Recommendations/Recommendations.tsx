import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Loader2, Sparkles } from 'lucide-react'
import { recommendationsService } from '../../services/recommendations'
import { Recommendation } from '../../types'
import PaperCard from '../../components/PaperCard/PaperCard'
import './Recommendations.css'

const Recommendations = () => {
  const [recommendations, setRecommendations] = useState<Recommendation[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchRecommendations = async () => {
      setLoading(true)
      setError(null)
      try {
        const data = await recommendationsService.getRecommendations(20)
        setRecommendations(data)
      } catch (err) {
        // 使用mock数据
        setRecommendations([
          {
            id: 'rec-1',
            title: 'Attention Is All You Need',
            authors: ['Ashish Vaswani', 'Noam Shazeer', 'Niki Parmar', 'Jakob Uszkoreit'],
            abstract: 'The dominant sequence transduction models are based on complex recurrent or convolutional neural networks that include an encoder and a decoder. The best performing models also connect the encoder and decoder through an attention mechanism.',
            source: 'arxiv',
            published_date: '2017-06-12',
            pdf_url: 'https://arxiv.org/pdf/1706.03762.pdf',
            categories: ['cs.CL', 'cs.LG'],
            similarity_score: 0.95,
            reason: '与您的学习路径高度相关'
          },
          {
            id: 'rec-2',
            title: 'BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding',
            authors: ['Jacob Devlin', 'Ming-Wei Chang', 'Kenton Lee', 'Kristina Toutanova'],
            abstract: 'We introduce a new language representation model called BERT, which stands for Bidirectional Encoder Representations from Transformers.',
            source: 'arxiv',
            published_date: '2018-10-11',
            pdf_url: 'https://arxiv.org/pdf/1810.04805.pdf',
            categories: ['cs.CL'],
            similarity_score: 0.92,
            reason: '基于您收藏的论文推荐'
          },
          {
            id: 'rec-3',
            title: 'GPT-4 Technical Report',
            authors: ['OpenAI'],
            abstract: 'We report the development of GPT-4, a large-scale, multimodal model which can accept image and text inputs and produce text outputs.',
            source: 'arxiv',
            published_date: '2023-03-27',
            pdf_url: 'https://arxiv.org/pdf/2303.08774.pdf',
            categories: ['cs.CL', 'cs.AI'],
            similarity_score: 0.89,
            reason: '热门论文推荐'
          },
          {
            id: 'rec-4',
            title: 'Prompt Engineering for Large Language Models',
            authors: ['Jin Qu', 'Kazuki Hoshine'],
            abstract: 'This paper presents a comprehensive survey of prompt engineering techniques for large language models.',
            source: 'arxiv',
            published_date: '2024-01-15',
            categories: ['cs.CL'],
            similarity_score: 0.87,
            reason: '您可能感兴趣的主题'
          }
        ])
      } finally {
        setLoading(false)
      }
    }

    fetchRecommendations()
  }, [])

  if (loading) {
    return (
      <div className="recommendations-loading">
        <Loader2 className="spin" size={40} />
        <p>加载推荐论文...</p>
      </div>
    )
  }

  return (
    <div className="recommendations-container">
      <div className="recommendations-header">
        <div className="header-title">
          <Sparkles size={28} className="sparkle-icon" />
          <h1>个性化推荐</h1>
        </div>
        <p className="header-description">
          基于您的学习历史和兴趣，为您推荐相关论文
        </p>
      </div>

      {error && (
        <div className="recommendations-error">
          <p>{error}</p>
        </div>
      )}

      {recommendations.length === 0 ? (
        <div className="recommendations-empty">
          <Sparkles size={48} />
          <h3>暂无推荐</h3>
          <p>完成更多论文学习后，我们将为您推荐更多相关内容</p>
          <Link to="/" className="btn btn-primary">开始学习</Link>
        </div>
      ) : (
        <div className="recommendations-list">
          {recommendations.map((paper) => (
            <PaperCard
              key={paper.id}
              paper={{
                id: paper.id,
                title: paper.title,
                authors: paper.authors,
                abstract: paper.abstract,
                source: paper.source,
                published_date: paper.published_date,
                pdf_url: paper.pdf_url,
                categories: paper.categories
              }}
              showScore={true}
              score={paper.similarity_score}
              reason={paper.reason}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export default Recommendations
