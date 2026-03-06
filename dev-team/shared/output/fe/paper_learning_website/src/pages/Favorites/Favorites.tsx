import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { Link, useNavigate } from 'react-router-dom'
import { Heart, Trash2, BookOpen, Loader2, Search } from 'lucide-react'
import { fetchFavorites, removeFavorite } from '../../store/slices/favoritesSlice'
import { RootState, AppDispatch } from '../../store/store'
import './Favorites.css'

const Favorites = () => {
  const dispatch = useDispatch<AppDispatch>()
  const navigate = useNavigate()
  const { favorites, loading, error } = useSelector(
    (state: RootState) => state.favorites
  )
  const { isAuthenticated } = useSelector((state: RootState) => state.user)
  
  const [searchQuery, setSearchQuery] = useState('')

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/auth', { state: { from: { pathname: '/favorites' } } })
      return
    }
    dispatch(fetchFavorites())
  }, [dispatch, isAuthenticated, navigate])

  const handleRemoveFavorite = (favoriteId: string) => {
    if (window.confirm('确定要移除这篇论文的收藏吗？')) {
      dispatch(removeFavorite(favoriteId))
    }
  }

  const filteredFavorites = favorites.filter(favorite =>
    favorite.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
    favorite.authors.some(author => 
      author.toLowerCase().includes(searchQuery.toLowerCase())
    )
  )

  if (loading) {
    return (
      <div className="favorites-loading">
        <Loader2 className="spin" size={40} />
        <p>加载中...</p>
      </div>
    )
  }

  return (
    <div className="favorites-page">
      <div className="favorites-header">
        <div className="favorites-title">
          <Heart className="heart-icon" size={28} />
          <h1>我的收藏</h1>
          <span className="favorites-count">{favorites.length} 篇论文</span>
        </div>
        
        <div className="favorites-search">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            placeholder="搜索收藏的论文..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>
      </div>

      {error && (
        <div className="favorites-error">
          {error}
        </div>
      )}

      {filteredFavorites.length === 0 ? (
        <div className="favorites-empty">
          <div className="empty-icon">
            <Heart size={64} />
          </div>
          <h2>{searchQuery ? '没有找到匹配的收藏' : '还没有收藏任何论文'}</h2>
          <p>
            {searchQuery 
              ? '尝试使用其他关键词搜索' 
              : '浏览论文时点击收藏按钮，将喜欢的论文保存到这里'}
          </p>
          {!searchQuery && (
            <Link to="/" className="browse-btn">
              <BookOpen size={20} />
              浏览论文
            </Link>
          )}
        </div>
      ) : (
        <div className="favorites-grid">
          {filteredFavorites.map((favorite) => (
            <div key={favorite.id} className="favorite-card">
              <div className="favorite-content">
                <Link to={`/paper/${favorite.paper_id}`} className="favorite-title">
                  {favorite.title}
                </Link>
                <p className="favorite-authors">
                  {favorite.authors.join(', ')}
                </p>
                <p className="favorite-abstract">
                  {favorite.abstract.length > 150
                    ? `${favorite.abstract.substring(0, 150)}...`
                    : favorite.abstract}
                </p>
                <div className="favorite-meta">
                  <span className="favorite-source">{favorite.source}</span>
                  <span className="favorite-date">
                    {new Date(favorite.published_date).toLocaleDateString('zh-CN')}
                  </span>
                </div>
              </div>
              <div className="favorite-actions">
                <Link to={`/paper/${favorite.paper_id}`} className="action-btn study-btn">
                  <BookOpen size={18} />
                  学习
                </Link>
                <button 
                  className="action-btn remove-btn"
                  onClick={() => handleRemoveFavorite(favorite.id)}
                >
                  <Trash2 size={18} />
                  移除
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Favorites
