# 用户关注领域配置

## 重点关注领域

### 1. 💰 数字货币
#### 主要关注币种
- **比特币 (BTC)**: 价格、趋势、市场情绪
- **以太坊 (ETH)**: 价格、技术发展、生态
- **其他热门币种**: SOL, BNB, XRP 等

#### 关注指标
- 24小时价格变化
- 交易量
- 市场情绪指数
- 监管政策变化
- 技术发展动态

### 2. 🤖 AI人工智能
#### 技术领域
- 大语言模型发展 (GPT, Claude, Gemini等)
- 多模态AI
- 自动驾驶技术
- 机器人技术
- AI芯片和硬件

#### 应用领域
- AI在金融领域的应用
- AI在医疗健康的应用
- AI在教育领域的应用
- AI在制造业的应用
- AI创业和投资

### 3. 📈 股市行情
#### 主要市场
- **A股市场**: 上证指数、深证成指、创业板指
- **美股市场**: 道琼斯、纳斯达克、标普500
- **港股市场**: 恒生指数
- **其他市场**: 日经指数、欧洲股市

#### 关注板块
- 科技板块
- 金融板块
- 新能源板块
- 医药板块
- 消费板块

### 4. 🌍 世界新闻
#### 政治经济
- 中美关系动态
- 欧洲政治经济
- 亚洲地区发展
- 国际组织动态 (UN, WTO, IMF等)

#### 科技前沿
- 全球科技发展趋势
- 科技创新政策
- 国际科技合作
- 科技伦理和监管

## 数据来源配置

### 数字货币数据源
```yaml
crypto_sources:
  - name: "CoinMarketCap"
    url: "https://coinmarketcap.com/"
    api_available: true
    
  - name: "CoinGecko"
    url: "https://www.coingecko.com/"
    api_available: true
    
  - name: "币安"
    url: "https://www.binance.com/"
    api_available: true
```

### AI新闻数据源
```yaml
ai_sources:
  - name: "AI科技评论"
    url: "https://www.leiphone.com/"
    category: "技术深度"
    
  - name: "机器之心"
    url: "https://www.jiqizhixin.com/"
    category: "行业应用"
    
  - name: "Arxiv"
    url: "https://arxiv.org/"
    category: "学术研究"
```

### 股市数据源
```yaml
stock_sources:
  - name: "东方财富"
    url: "https://www.eastmoney.com/"
    markets: ["A股", "港股"]
    
  - name: "新浪财经"
    url: "https://finance.sina.com.cn/"
    markets: ["A股", "美股", "港股"]
    
  - name: "Yahoo Finance"
    url: "https://finance.yahoo.com/"
    markets: ["美股", "全球"]
```

### 世界新闻数据源
```yaml
news_sources:
  - name: "BBC News"
    url: "https://www.bbc.com/news"
    language: "英文"
    
  - name: "Reuters"
    url: "https://www.reuters.com/"
    language: "英文"
    
  - name: "新华社"
    url: "http://www.xinhuanet.com/"
    language: "中文"
    
  - name: "财新网"
    url: "https://www.caixin.com/"
    language: "中文"
```

## 报告生成规则

### 早报内容优先级
1. **数字货币开盘情况** (最高优先级)
2. **AI领域夜间重要发布**
3. **全球股市开盘表现**
4. **重要世界新闻**
5. **天气和日程**
6. **任务建议和名言**

### 晚报内容优先级
1. **数字货币收盘总结** (最高优先级)
2. **AI领域今日动态汇总**
3. **全球股市收盘行情**
4. **世界新闻总结**
5. **工作成果总结**
6. **明日计划和放松建议**

## 个性化设置

### 价格提醒阈值
```yaml
price_alerts:
  bitcoin:
    change_threshold: "5%"  # 价格变化超过5%时提醒
    price_levels: [30000, 40000, 50000]  # 重要价格关口
    
  ethereum:
    change_threshold: "7%"
    price_levels: [2000, 2500, 3000]
```

### 新闻关键词过滤
```yaml
news_keywords:
  crypto: ["比特币", "以太坊", "区块链", "DeFi", "NFT", "Web3"]
  ai: ["人工智能", "机器学习", "深度学习", "大模型", "GPT", "自动驾驶"]
  stocks: ["A股", "美股", "港股", "科技股", "新能源", "医药"]
  world: ["中美关系", "全球经济", "科技政策", "国际合作"]
```

### 时间安排
```yaml
report_schedule:
  morning_report:
    time: "08:00"
    focus: "开盘情况、夜间动态、今日展望"
    
  evening_report:
    time: "20:00"
    focus: "收盘总结、全天动态、明日准备"
    
  market_updates:
    times: ["12:00", "15:00"]
    focus: "盘中快讯、重要公告"
```

## 扩展功能规划

### 短期功能 (1个月内)
1. 实时价格监控和提醒
2. 新闻情感分析
3. 市场趋势预测

### 中期功能 (3个月内)
1. 投资组合跟踪
2. 智能新闻推荐
3. 数据分析报告

### 长期功能 (6个月内)
1. AI辅助投资决策
2. 自动化交易信号
3. 风险预警系统

## 使用说明

### 如何调整关注领域
1. 修改本配置文件中的关键词
2. 调整数据源优先级
3. 设置个性化提醒阈值

### 如何获取实时数据
1. 使用API接口获取数字货币价格
2. 订阅新闻RSS源
3. 使用财经数据API

### 报告定制选项
1. 调整报告发送时间
2. 选择重点关注的市场
3. 设置价格提醒级别
4. 过滤不感兴趣的新闻类别