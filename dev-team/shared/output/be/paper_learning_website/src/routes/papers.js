const express = require('express');
const router = express.Router();
const paperController = require('../controllers/paperController');

// 搜索论文
router.post('/search', paperController.searchPapers);

// 获取论文详情
router.get('/:id', paperController.getPaperDetails);

// 获取相关论文
router.get('/:id/related', paperController.getRelatedPapers);

// 保存论文到用户收藏
router.post('/:id/favorite', paperController.favoritePaper);

module.exports = router;