const express = require('express');
const cors = require('cors');
const axios = require('axios');

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(cors());
app.use(express.json());

// 搜索论文
app.post('/api/papers/search', async (req, res) => {
  const { query } = req.body;
  
  try {
    // 调用 arXiv API
    const arxivResponse = await axios.get(
      `http://export.arxiv.org/api/query?search_query=all:${encodeURIComponent(query)}&max_results=10`
    );
    
    // 解析 XML 响应
    const parseString = require('xml2js').parseString;
    parseString(arxivResponse.data, (err, result) => {
      if (err) {
        // 如果解析失败，返回模拟数据
        return res.json(getMockPapers(query));
      }
      
      try {
        const entries = result.feed.entry || [];
        const papers = entries.map(entry => ({
          id: entry.id?.[0] || '',
          title: entry.title?.[0]?.replace(/\n/g, ' ').trim() || '',
          authors: entry.author?.map(a => a.name?.[0]).filter(Boolean) || [],
          abstract: entry.summary?.[0]?.replace(/\n/g, ' ').trim() || '',
          published: entry.published?.[0] || '',
          source: 'arxiv'
        }));
        
        res.json(papers.length > 0 ? papers : getMockPapers(query));
      } catch (e) {
        res.json(getMockPapers(query));
      }
    });
  } catch (error) {
    console.error('搜索错误:', error.message);
    // 返回模拟数据
    res.json(getMockPapers(query));
  }
});

// 获取论文详情
app.get('/api/papers/:id', async (req, res) => {
  const { id } = req.params;
  res.json(getMockPaperDetail(id));
});

// AI分析论文 - 调用MiniMax API
app.post('/api/papers/analyze', async (req, res) => {
  const { paperId, title, abstract } = req.body;
  
  const apiKey = process.env.MINIMAX_API_KEY;
  
  if (!apiKey) {
    return res.json({
      paperId,
      summary: 'AI分析功能需要配置MINIMAX_API_KEY环境变量',
      keyPoints: ['请配置MiniMax API Key'],
      difficulty: '未知',
      prerequisites: [],
      error: 'Missing MINIMAX_API_KEY'
    });
  }
  
  try {
    // MiniMax API 格式 - 增强版prompt，更适合初学者
    const enhancedPrompt = `你是一位耐心的AI导师，专门帮助初学者学习论文。请对以下论文进行详细分析，用通俗易懂的语言解释复杂的概念。

论文信息：
标题：${title || 'N/A'}
摘要：${abstract || 'N/A'}
Paper ID: ${paperId}

请用JSON格式返回以下内容（要详细，适合零基础初学者）：

{
  "title": "论文标题",
  "background": "研究背景：用通俗易懂的语言解释为什么这个研究重要，解决了什么问题",
  "coreConcepts": [
    {
      "name": "概念名称",
      "explanation": "用生活化的例子解释这个概念",
      "analogy": "一个简单的类比"
    }
  ],
  "methodology": {
    "overview": "方法概述",
    "steps": ["步骤1", "步骤2", "步骤3"],
    "details": "详细解释每个步骤"
  },
  "keyInnovations": [
    {
      "innovation": "创新点描述",
      "whyImportant": "为什么这个创新重要",
      "howItWorks": "它是如何工作的"
    }
  ],
  "results": {
    "mainFindings": ["主要发现1", "主要发现2"],
    "performance": "性能描述"
  },
  "learningPath": [
    {
      "step": "第1步",
      "content": "学习内容",
      "time": "预计时间",
      "resources": "推荐资源"
    }
  ],
  "discussion": [
    {
      "question": "思考题",
      "answer": "参考答案"
    }
  ],
  "difficulty": "入门/中等/高级",
  "estimatedTime": "预计学习时间",
  "prerequisites": ["前提知识"],
  "summary": "一句话总结"
}`;

    const response = await axios.post(
      'https://api.minimaxi.com/v1/text/chatcompletion_v2',
      {
        model: 'MiniMax-M2.5',
        max_tokens: 4096,
        messages: [
          {
            role: 'user',
            content: enhancedPrompt
          }
        ]
      },
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${apiKey}`
        },
        timeout: 120000
      }
    );
    
    console.log('MiniMax response:', JSON.stringify(response.data, null, 2));
    
    const content = response.data?.choices?.[0]?.message?.content || '';
    
    // 尝试解析JSON
    try {
      const jsonMatch = content.match(/\{[\s\S]*\}/);
      if (jsonMatch) {
        const analysis = JSON.parse(jsonMatch[0]);
        return res.json({
          paperId,
          ...analysis,
          model: 'MiniMax-M2.5'
        });
      }
    } catch (e) {
      // JSON解析失败，返回原始内容
    }
    
    res.json({
      paperId,
      summary: content.substring(0, 200),
      keyPoints: [content.substring(0, 100)],
      difficulty: '中等',
      prerequisites: [],
      raw: content,
      model: 'MiniMax-M2.5'
    });
    
  } catch (error) {
    console.error('AI分析错误:', error.message);
    console.error('详细错误:', error.response?.data || error);
    res.json({
      paperId,
      summary: 'AI分析失败: ' + error.message,
      keyPoints: ['API调用失败'],
      difficulty: '未知',
      prerequisites: [],
      error: error.message,
      details: error.response?.data
    });
  }
});

// 模拟数据
function getMockPapers(query) {
  return [
    {
      id: 'arxiv:1706.03762',
      title: 'Attention Is All You Need',
      authors: ['Ashish Vaswani', 'Noam Shazeer', 'Niki Parmar'],
      abstract: 'The dominant sequence transduction models are based on complex recurrent or convolutional neural networks that include an encoder and a decoder. The best performing models also connect the encoder and decoder through an attention mechanism.',
      published: '2017-06-12',
      source: 'arxiv'
    },
    {
      id: 'arxiv:1810.04805',
      title: 'BERT: Pre-training of Deep Bidirectional Transformers',
      authors: ['Jacob Devlin', 'Ming-Wei Chang', 'Kenton Lee'],
      abstract: 'We introduce a new language representation model called BERT, which stands for Bidirectional Encoder Representations from Transformers.',
      published: '2018-10-11',
      source: 'arxiv'
    }
  ];
}

function getMockPaperDetail(id) {
  return {
    id,
    title: 'Attention Is All You Need',
    authors: ['Ashish Vaswani', 'Noam Shazeer', 'Niki Parmar'],
    abstract: 'The dominant sequence transduction models are based on complex recurrent or convolutional neural networks.',
    published: '2017-06-12',
    source: 'arxiv',
    categories: ['cs.CL', 'cs.LG'],
    pdfUrl: 'https://arxiv.org/pdf/1706.03762.pdf'
  };
}

// 健康检查
app.get('/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

// ========== 用户认证 API ==========
const users = []; // 内存存储
const jwt = require('jsonwebtoken');
const JWT_SECRET = 'paper-learning-secret-key-2026';

// 用户注册
app.post('/api/auth/register', (req, res) => {
  const { username, password, email } = req.body;
  
  if (users.find(u => u.username === username)) {
    return res.json({ error: '用户名已存在' });
  }
  
  const user = {
    id: users.length + 1,
    username,
    password, // 简化：实际应该加密
    email,
    createdAt: new Date().toISOString()
  };
  
  users.push(user);
  
  const token = jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: { id: user.id, username: user.username, email: user.email } });
});

// 用户登录
app.post('/api/auth/login', (req, res) => {
  const { username, password } = req.body;
  
  const user = users.find(u => u.username === username && u.password === password);
  
  if (!user) {
    return res.json({ error: '用户名或密码错误' });
  }
  
  const token = jwt.sign({ id: user.id, username: user.username }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ token, user: { id: user.id, username: user.username, email: user.email } });
});

// ========== 收藏 API ==========
const favorites = []; // 内存存储

// 获取收藏列表
app.get('/api/favorites', (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.json([]);
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    const userFavorites = favorites.filter(f => f.userId === decoded.id);
    res.json(userFavorites);
  } catch (e) {
    res.json([]);
  }
});

// 添加收藏
app.post('/api/favorites/add', (req, res) => {
  const { paperId, title, authors, abstract } = req.body;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    // 检查是否已收藏
    if (favorites.find(f => f.userId === decoded.id && f.paperId === paperId)) {
      return res.json({ error: '已经收藏过了' });
    }
    
    const favorite = {
      id: favorites.length + 1,
      userId: decoded.id,
      paperId,
      title,
      authors: authors || [],
      abstract: abstract || '',
      createdAt: new Date().toISOString()
    };
    
    favorites.push(favorite);
    res.json(favorite);
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// 删除收藏
app.delete('/api/favorites/:id', (req, res) => {
  const { id } = req.params;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const index = favorites.findIndex(f => f.id === parseInt(id) && f.userId === decoded.id);
    if (index === -1) {
      return res.json({ error: '收藏不存在' });
    }
    
    favorites.splice(index, 1);
    res.json({ success: true });
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// ========== 学习进度 API ==========
const learningProgress = []; // 内存存储

// 获取学习进度
app.get('/api/progress', (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.json([]);
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    const userProgress = learningProgress.filter(p => p.userId === decoded.id);
    res.json(userProgress);
  } catch (e) {
    res.json([]);
  }
});

// 更新学习进度
app.post('/api/progress/update', (req, res) => {
  const { paperId, paperTitle, progress, status } = req.body;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    // 查找现有进度
    let existing = learningProgress.find(p => p.userId === decoded.id && p.paperId === paperId);
    
    if (existing) {
      existing.progress = progress;
      existing.status = status;
      existing.updatedAt = new Date().toISOString();
    } else {
      learningProgress.push({
        id: learningProgress.length + 1,
        userId: decoded.id,
        paperId,
        paperTitle,
        progress,
        status,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      });
    }
    
    res.json({ success: true });
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// ========== 用户Profile API ==========
// 获取用户信息
app.get('/api/user/profile', (req, res) => {
  const authHeader = req.headers.authorization;
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    // 简化：从users数组获取
    const user = users.find(u => u.id === decoded.id);
    if (user) {
      res.json({ id: user.id, username: user.username, email: user.email });
    } else {
      res.json({ error: '用户不存在' });
    }
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// 更新用户信息
app.put('/api/user/profile', (req, res) => {
  const { email, bio } = req.body;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const user = users.find(u => u.id === decoded.id);
    if (user) {
      if (email) user.email = email;
      res.json({ success: true, user: { id: user.id, username: user.username, email: user.email } });
    } else {
      res.json({ error: '用户不存在' });
    }
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// ========== 学习笔记 API (v1.3) ==========
const notes = []; // 内存存储

// 添加笔记 - POST /api/notes/add
app.post('/api/notes/add', (req, res) => {
  const { paperId, paperTitle, content, tags } = req.body;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const note = {
      id: notes.length + 1,
      userId: decoded.id,
      paperId,
      paperTitle: paperTitle || '',
      content,
      tags: tags || [],
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    };
    
    notes.push(note);
    res.json(note);
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// 获取笔记 - GET /api/notes?paperId=xxx
app.get('/api/notes', (req, res) => {
  const { paperId } = req.query;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    let userNotes = notes.filter(n => n.userId === decoded.id);
    
    // 如果指定了paperId，则过滤
    if (paperId) {
      userNotes = userNotes.filter(n => n.paperId === paperId);
    }
    
    res.json(userNotes);
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// 更新笔记 - PUT /api/notes/:id
app.put('/api/notes/:id', (req, res) => {
  const { id } = req.params;
  const { content, tags } = req.body;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const note = notes.find(n => n.id === parseInt(id) && n.userId === decoded.id);
    
    if (!note) {
      return res.json({ error: '笔记不存在' });
    }
    
    if (content !== undefined) note.content = content;
    if (tags !== undefined) note.tags = tags;
    note.updatedAt = new Date().toISOString();
    
    res.json(note);
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// 删除笔记 - DELETE /api/notes/:id
app.delete('/api/notes/:id', (req, res) => {
  const { id } = req.params;
  const authHeader = req.headers.authorization;
  
  if (!authHeader) {
    return res.json({ error: '请先登录' });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    const index = notes.findIndex(n => n.id === parseInt(id) && n.userId === decoded.id);
    
    if (index === -1) {
      return res.json({ error: '笔记不存在' });
    }
    
    notes.splice(index, 1);
    res.json({ success: true });
  } catch (e) {
    res.json({ error: '无效的token' });
  }
});

// ========== 论文推荐 API (v1.3) ==========
// GET /api/recommendations - 基于用户学习历史推荐论文

// 模拟论文数据库（用于推荐）
const paperDatabase = [
  { id: 'arxiv:1706.03762', title: 'Attention Is All You Need', category: 'NLP', tags: ['transformer', 'attention'] },
  { id: 'arxiv:1810.04805', title: 'BERT: Pre-training of Deep Bidirectional Transformers', category: 'NLP', tags: ['BERT', 'transformer'] },
  { id: 'arxiv:1907.11692', title: 'RoBERTa: A Robustly Optimized BERT Pretraining Approach', category: 'NLP', tags: ['BERT', 'RoBERTa'] },
  { id: 'arxiv:1409.0473', title: 'Neural Machine Translation by Jointly Learning to Align and Translate', category: 'NLP', tags: ['seq2seq', 'attention'] },
  { id: 'arxiv:1508.04025', title: 'Effective Approaches to Attention-based Neural Machine Translation', category: 'NLP', tags: ['NMT', 'attention'] },
  { id: 'arxiv:1712.05884', title: 'A Neural Probabilistic Language Model', category: 'NLP', tags: ['language-model', 'embedding'] },
  { id: 'arxiv:1802.05365', title: 'DCN+: Mixed Objective and Deep Residual Coattention for Question Answering', category: 'NLP', tags: ['QA', 'attention'] },
  { id: 'arxiv:1801.01290', title: 'Ask the Right Questions: Active Question Reformulation with Reinforcement Learning', category: 'NLP', tags: ['QA', 'RL'] },
  { id: 'arxiv:1706.03741', title: 'Layer Normalization', category: 'Deep Learning', tags: ['normalization', 'transformer'] },
  { id: 'arxiv:1607.06450', title: 'Layer Normalization', category: 'Deep Learning', tags: ['normalization', 'training'] },
  { id: 'arxiv:1512.03385', title: 'Deep Residual Learning for Image Recognition', category: 'CV', tags: ['ResNet', 'image'] },
  { id: 'arxiv:1409.1556', title: 'Very Deep Convolutional Networks for Large-Scale Image Recognition', category: 'CV', tags: ['VGG', 'image'] },
  { id: 'arxiv:1611.05431', title: 'Inception-v4, Inception-ResNet and the Impact of Residual Connections on Learning', category: 'CV', tags: ['Inception', 'residual'] },
  { id: 'arxiv:1802.02611', title: 'Convolutional Neural Networks for Sentence Classification', category: 'NLP', tags: ['CNN', 'text-classification'] },
  { id: 'arxiv:1412.6980', title: 'Adam: A Method for Stochastic Optimization', category: 'Deep Learning', tags: ['optimizer', 'Adam'] }
];

app.get('/api/recommendations', (req, res) => {
  const authHeader = req.headers.authorization;
  const { limit = 5 } = req.query;
  
  // 未登录用户返回热门论文
  if (!authHeader) {
    const popularPapers = paperDatabase.slice(0, parseInt(limit));
    return res.json({
      recommendations: popularPapers,
      reason: '热门论文推荐'
    });
  }
  
  try {
    const token = authHeader.replace('Bearer ', '');
    const decoded = jwt.verify(token, JWT_SECRET);
    
    // 获取用户的学习进度
    const userProgress = learningProgress.filter(p => p.userId === decoded.id);
    
    // 获取用户的笔记
    const userNotes = notes.filter(n => n.userId === decoded.id);
    
    // 获取用户收藏的论文
    const userFavorites = favorites.filter(f => f.userId === decoded.id);
    
    // 提取用户关注的论文类别和标签
    const userInterests = new Set();
    
    // 从学习进度中提取
    userProgress.forEach(p => {
      const paper = paperDatabase.find(pd => pd.id === p.paperId);
      if (paper) {
        userInterests.add(paper.category);
        paper.tags.forEach(tag => userInterests.add(tag));
      }
    });
    
    // 从笔记中提取
    userNotes.forEach(n => {
      const paper = paperDatabase.find(pd => pd.id === n.paperId);
      if (paper) {
        userInterests.add(paper.category);
        paper.tags.forEach(tag => userInterests.add(tag));
      }
    });
    
    // 从收藏中提取
    userFavorites.forEach(f => {
      const paper = paperDatabase.find(pd => pd.id === f.paperId);
      if (paper) {
        userInterests.add(paper.category);
        paper.tags.forEach(tag => userInterests.add(tag));
      }
    });
    
    // 基于用户兴趣过滤并排序
    const userInterestArray = Array.from(userInterests);
    
    // 计算每篇论文的相关度分数
    const scoredPapers = paperDatabase.map(paper => {
      // 排除用户已学习/收藏/笔记的论文
      const isLearned = userProgress.some(p => p.paperId === paper.id);
      const isFavorited = userFavorites.some(f => f.paperId === paper.id);
      const hasNote = userNotes.some(n => n.paperId === paper.id);
      
      if (isLearned || isFavorited || hasNote) {
        return { ...paper, score: -1 };
      }
      
      // 计算相关度分数
      let score = 0;
      if (userInterestArray.includes(paper.category)) {
        score += 5;
      }
      paper.tags.forEach(tag => {
        if (userInterestArray.includes(tag)) {
          score += 3;
        }
      });
      
      return { ...paper, score };
    });
    
    // 按分数排序，获取推荐
    const recommendations = scoredPapers
      .filter(p => p.score >= 0)
      .sort((a, b) => b.score - a.score)
      .slice(0, parseInt(limit));
    
    res.json({
      recommendations,
      reason: recommendations.length > 0 
        ? `基于您学习的 ${userProgress.length} 篇论文、${userNotes.length} 条笔记和 ${userFavorites.length} 个收藏`
        : '暂无学习历史，为您推荐热门论文',
      userInterests: userInterestArray,
      stats: {
        learnedPapers: userProgress.length,
        notesCount: userNotes.length,
        favoritesCount: userFavorites.length
      }
    });
  } catch (e) {
    // token无效，返回热门论文
    const popularPapers = paperDatabase.slice(0, parseInt(limit));
    res.json({
      recommendations: popularPapers,
      reason: '热门论文推荐'
    });
  }
});

app.listen(PORT, () => {
  console.log(`🚀 后端服务已启动: http://localhost:${PORT}`);
});
