import { useState } from 'react'
import { Search } from 'lucide-react'
import './SearchBar.css'

interface SearchBarProps {
  onSearch: (query: string) => void
}

const SearchBar = ({ onSearch }: SearchBarProps) => {
  const [query, setQuery] = useState('')
  const [suggestions, setSuggestions] = useState<string[]>([])
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (query.trim()) {
      onSearch(query.trim())
    }
  }
  
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value
    setQuery(value)
    
    // 简单的搜索建议
    if (value.length > 2) {
      const mockSuggestions = [
        `${value} survey`,
        `${value} review`,
        `recent ${value} papers`,
        `${value} applications`
      ]
      setSuggestions(mockSuggestions)
    } else {
      setSuggestions([])
    }
  }
  
  const handleSuggestionClick = (suggestion: string) => {
    setQuery(suggestion)
    onSearch(suggestion)
    setSuggestions([])
  }
  
  return (
    <div className="search-bar-container">
      <form onSubmit={handleSubmit} className="search-form">
        <div className="search-input-wrapper">
          <Search className="search-icon" size={20} />
          <input
            type="text"
            value={query}
            onChange={handleInputChange}
            placeholder="输入论文名称、作者或关键词..."
            className="search-input"
            autoFocus
          />
          <button type="submit" className="search-button">
            搜索
          </button>
        </div>
      </form>
      
      {suggestions.length > 0 && (
        <div className="suggestions-dropdown">
          {suggestions.map((suggestion, index) => (
            <button
              key={index}
              className="suggestion-item"
              onClick={() => handleSuggestionClick(suggestion)}
            >
              {suggestion}
            </button>
          ))}
        </div>
      )}
    </div>
  )
}

export default SearchBar