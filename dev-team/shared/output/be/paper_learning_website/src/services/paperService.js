const axios = require('axios');
const cache = require('../utils/cache');
const Paper = require('../models/Paper');

class PaperService {
  constructor() {
    this.arxivClient = axios.create({
      baseURL: 'http://export.arxiv.org/api/query',
      timeout: 10000
    });
    
    this.cache = cache;
  }
  
  async searchPapers(query, source = 'all', page = 1, limit = 10) {
    const cacheKey = `search:${query}:${source}:${page}:${limit}`;
    
    // 检查缓存
    const cached = await this.cache.get(cacheKey);
    if (cached) {
      return cached;
    }
    
    let results = [];
    
    // 根据来源搜索
    if (source === 'all' || source === 'arxiv') {
      const arxivResults = await this.searchArxiv(query, page, limit);
      results = results.concat(arxivResults);
    }
    
    // 可以添加其他数据源的搜索逻辑
    
    // 缓存结果
    await this.cache.set(cacheKey, results, 3600); // 缓存1小时
    
    return {
      query,
      source,
      page,
      limit,
      total: results.length,
      results
    };
  }
  
  async searchArxiv(query, page = 1, limit = 10) {
    try {
      const start = (page - 1) * limit;
      const response = await this.arxivClient.get('', {
        params: {
          search_query: `all:${query}`,
          start,
          max_results: limit,
          sortBy: 'relevance',
          sortOrder: 'descending'
        }
      });
      
      // 解析arXiv XML响应
      const papers = this.parseArxivResponse(response.data);
      return papers;
    } catch (error) {
      console.error('arXiv搜索错误:', error.message);
      return [];
    }
  }
  
  parseArxivResponse(xmlData) {
    // 简化的XML解析逻辑
    // 实际实现需要使用xml2js等库
    const papers = [];
    
    // 这里应该是实际的XML解析逻辑
    // 为了示例，返回模拟数据
    papers.push({
      id: 'arxiv:2301.12345',
      title: 'A Survey of Large Language Models',
      authors: ['John Doe', 'Jane Smith'],
      abstract: 'This paper provides a comprehensive survey of large language models...',
      source: 'arxiv',
      published_date: '2023-01-01',
      pdf_url: 'https://arxiv.org/pdf/2301.12345.pdf',
      categories: ['cs.CL', 'cs.AI']
    });
    
    return papers;
  }
  
  async getPaperDetails(id) {
    const cacheKey = `paper:${id}`;
    
    // 检查缓存
    const cached = await this.cache.get(cacheKey);
    if (cached) {
      return cached;
    }
    
    // 根据ID获取论文详情
    let paper = null;
    
    if (id.startsWith('arxiv:')) {
      paper = await this.getArxivPaperDetails(id.replace('arxiv:', ''));
    }
    
    // 缓存结果
    if (paper) {
      await this.cache.set(cacheKey, paper, 7200); // 缓存2小时
    }
    
    return paper;
  }
  
  async getArxivPaperDetails(arxivId) {
    try {
      const response = await this.arxivClient.get('', {
        params: {
          id_list: arxivId
        }
      });
      
      // 解析单个论文的XML响应
      const papers = this.parseArxivResponse(response.data);
      return papers[0] || null;
    } catch (error) {
      console.error('获取arXiv论文详情错误:', error.message);
      return null;
    }
  }
  
  async getRelatedPapers(paperId, limit = 5) {
    // 简化的相关论文逻辑
    // 实际实现可能需要基于关键词、作者、引用等
    
    const cacheKey = `related:${paperId}:${limit}`;
    const cached = await this.cache.get(cacheKey);
    if (cached) {
      return cached;
    }
    
    // 模拟相关论文
    const relatedPapers = [
      {
        id: 'arxiv:2302.12345',
        title: 'Advanced Techniques in Natural Language Processing',
        authors: ['Alice Johnson'],
        source: 'arxiv'
      },
      {
        id: 'arxiv:2303.12345',
        title: 'Machine Learning for Text Understanding',
        authors: ['Bob Wilson'],
        source: 'arxiv'
      }
    ].slice(0, limit);
    
    await this.cache.set(cacheKey, relatedPapers, 1800); // 缓存30分钟
    
    return relatedPapers;
  }
  
  async favoritePaper(paperId, userId) {
    // 这里应该将收藏关系保存到数据库
    // 简化实现
    return {
      success: true,
      message: '论文已收藏',
      paperId,
      userId,
      timestamp: new Date().toISOString()
    };
  }
}

module.exports = new PaperService();