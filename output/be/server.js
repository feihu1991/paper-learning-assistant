const express = require('express');
const path = require('path');
const hljs = require('highlight.js');

const app = express();
const PORT = process.env.PORT || 3000;

// 静态文件目录 - 前端输出目录
const STATIC_DIR = path.join(__dirname, '..', 'fe');

// 托管静态文件
app.use(express.static(STATIC_DIR));

// 代码高亮中间件 - 为 HTML 页面注入 highlight.js 样式和脚本
app.use((req, res, next) => {
  const filePath = req.path;
  
  // 只对 HTML 文件进行处理
  if (filePath.endsWith('.html')) {
    const htmlPath = path.join(STATIC_DIR, filePath);
    
    // 尝试读取文件（如果存在）
    try {
      const fs = require('fs');
      if (fs.existsSync(htmlPath)) {
        let html = fs.readFileSync(htmlPath, 'utf-8');
        
        // 检查是否已经包含 highlight.js
        if (!html.includes('highlight.js') && !html.includes('highlightjs')) {
          // 注入 highlight.js 主题样式
          const hlStyles = `
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github.min.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
  <script>hljs.highlightAll();</script>
</head>`;
          
          html = html.replace('</head>', hlStyles);
          
          // 发送修改后的 HTML
          res.set('Content-Type', 'text/html');
          return res.send(html);
        }
      }
    } catch (err) {
      console.error('Error processing HTML:', err);
    }
  }
  
  next();
});

// SPA 回退支持 - 如果请求的文件不存在，返回 index.html
app.get('*', (req, res) => {
  const indexPath = path.join(STATIC_DIR, 'index.html');
  const fs = require('fs');
  
  if (fs.existsSync(indexPath)) {
    res.sendFile(indexPath);
  } else {
    res.status(404).send(`
      <html>
        <head>
          <title>OpenClaw Docs</title>
          <style>
            body { font-family: system-ui, sans-serif; padding: 40px; text-align: center; }
            h1 { color: #333; }
            p { color: #666; }
          </style>
        </head>
        <body>
          <h1>OpenClaw Installation Guide</h1>
          <p>Frontend files not found in: ${STATIC_DIR}</p>
          <p>Please build the frontend first.</p>
        </body>
      </html>
    `);
  }
});

app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
  console.log(`Serving static files from: ${STATIC_DIR}`);
});
