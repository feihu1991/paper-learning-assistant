const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
require('dotenv').config();

const paperRoutes = require('./src/routes/papers');
const generateRoutes = require('./src/routes/generate');
const userRoutes = require('./src/routes/users');
const errorHandler = require('./src/middleware/errorHandler');

const app = express();
const PORT = process.env.PORT || 3000;

// 中间件
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 路由
app.use('/api/papers', paperRoutes);
app.use('/api/generate', generateRoutes);
app.use('/api/users', userRoutes);

// 健康检查
app.get('/health', (req, res) => {
  res.json({ status: 'healthy', timestamp: new Date().toISOString() });
});

// 错误处理
app.use(errorHandler);

// 启动服务器
app.listen(PORT, () => {
  console.log(`论文学习助手网站后端服务运行在端口 ${PORT}`);
  console.log(`环境: ${process.env.NODE_ENV || 'development'}`);
});

module.exports = app;