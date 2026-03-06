const paperService = require('../services/paperService');

class PaperController {
  async searchPapers(req, res, next) {
    try {
      const { query, source = 'all', page = 1, limit = 10 } = req.body;
      
      if (!query || query.trim().length === 0) {
        return res.status(400).json({ error: '搜索查询不能为空' });
      }
      
      const results = await paperService.searchPapers(query, source, page, limit);
      res.json(results);
    } catch (error) {
      next(error);
    }
  }
  
  async getPaperDetails(req, res, next) {
    try {
      const { id } = req.params;
      const paper = await paperService.getPaperDetails(id);
      
      if (!paper) {
        return res.status(404).json({ error: '论文未找到' });
      }
      
      res.json(paper);
    } catch (error) {
      next(error);
    }
  }
  
  async getRelatedPapers(req, res, next) {
    try {
      const { id } = req.params;
      const { limit = 5 } = req.query;
      
      const relatedPapers = await paperService.getRelatedPapers(id, limit);
      res.json(relatedPapers);
    } catch (error) {
      next(error);
    }
  }
  
  async favoritePaper(req, res, next) {
    try {
      const { id } = req.params;
      const userId = req.user?.id; // 从认证中间件获取
      
      if (!userId) {
        return res.status(401).json({ error: '需要登录' });
      }
      
      const result = await paperService.favoritePaper(id, userId);
      res.json(result);
    } catch (error) {
      next(error);
    }
  }
}

module.exports = new PaperController();