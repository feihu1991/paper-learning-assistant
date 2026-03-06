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

app.listen(PORT, () => {
  console.log(`🚀 后端服务已启动: http://localhost:${PORT}`);
});
