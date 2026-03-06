/**
 * QA 真实页面自动化测试脚本
 * 使用 Puppeteer 进行真实的浏览器自动化测试
 * 
 * 运行方式: node qa_real_browser_test.js
 */

const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

const WEBSITE_URL = 'https://feihu1991.github.io/paper-learning-assistant/';
const OUTPUT_DIR = path.join(__dirname, '..', 'output', 'qa');
const REPORT_DIR = path.join(__dirname, '..', 'reports');

console.log('='.repeat(60));
console.log('QA 真实页面自动化测试');
console.log('='.repeat(60));
console.log('\n测试目标:', WEBSITE_URL);
console.log('开始时间:', new Date().toISOString());

// 确保输出目录存在
if (!fs.existsSync(OUTPUT_DIR)) {
  fs.mkdirSync(OUTPUT_DIR, { recursive: true });
}

async function runTests() {
  let browser;
  const results = {
    timestamp: new Date().toISOString(),
    url: WEBSITE_URL,
    tests: [],
    summary: { passed: 0, failed: 0, warnings: 0 }
  };

  try {
    console.log('\n[1/6] 启动浏览器...');
    browser = await puppeteer.launch({
      headless: 'new',
      args: [
        '--no-sandbox',
        '--disable-setuid-sandbox',
        '--disable-dev-shm-usage',
        '--disable-blink-features=AutomationControlled'
      ]
    });

    const page = await browser.newPage();
    
    // 设置更长的超时
    page.setDefaultTimeout(30000);

    console.log('[2/6] 访问网站...');
    await page.goto(WEBSITE_URL, { waitUntil: 'networkidle2', timeout: 30000 });
    
    // 等待一下让React渲染完成
    await page.waitForTimeout(2000);

    // 测试1: 页面加载
    console.log('[3/6] 测试页面加载...');
    const title = await page.title();
    results.tests.push({
      name: '页面标题',
      status: title.includes('论文') ? 'passed' : 'failed',
      details: `标题: ${title}`
    });
    if (title.includes('论文')) results.summary.passed++;
    else results.summary.failed++;

    // 测试2: 检查是否有内容渲染
    console.log('[4/6] 检查内容渲染...');
    const content = await page.evaluate(() => {
      const root = document.getElementById('root');
      return root ? root.innerHTML.length : 0;
    });
    
    results.tests.push({
      name: 'React内容渲染',
      status: content > 100 ? 'passed' : 'failed',
      details: `内容长度: ${content} 字符`
    });
    if (content > 100) results.summary.passed++;
    else results.summary.failed++;

    // 测试3: 检查搜索框
    console.log('[5/6] 检查搜索框...');
    const searchBox = await page.$('input[type="text"]');
    results.tests.push({
      name: '搜索框存在',
      status: searchBox ? 'passed' : 'failed',
      details: searchBox ? '搜索框已找到' : '搜索框未找到'
    });
    if (searchBox) results.summary.passed++;
    else results.summary.failed++;

    // 测试4: 检查论文卡片
    console.log('[6/6] 检查论文列表...');
    const paperCards = await page.evaluate(() => {
      // 查找包含论文标题的元素
      const headings = Array.from(document.querySelectorAll('h1, h2, h3, h4, h5, h6'));
      return headings.map(h => h.textContent).filter(t => 
        t.includes('Attention') || t.includes('BERT') || t.includes('Paper') || t.includes('论文')
      );
    });
    
    results.tests.push({
      name: '论文卡片显示',
      status: paperCards.length > 0 ? 'passed' : 'warning',
      details: `找到 ${paperCards.length} 个相关标题: ${paperCards.slice(0, 3).join(', ')}`
    });
    if (paperCards.length > 0) results.summary.passed++;
    else results.summary.warnings++;

    // 额外: 截图保存
    console.log('\n保存截图...');
    const screenshotPath = path.join(OUTPUT_DIR, 'page_screenshot.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log('截图已保存:', screenshotPath);

    // 获取页面HTML内容
    const htmlContent = await page.content();
    const htmlPath = path.join(OUTPUT_DIR, 'page_content.html');
    fs.writeFileSync(htmlPath, htmlContent);
    console.log('HTML已保存:', htmlPath);

  } catch (error) {
    console.error('\n❌ 测试出错:', error.message);
    results.tests.push({
      name: '测试执行',
      status: 'failed',
      details: error.message
    });
    results.summary.failed++;
  } finally {
    if (browser) {
      await browser.close();
    }
  }

  // 计算通过率
  const total = results.summary.passed + results.summary.failed + results.summary.warnings;
  results.summary.passRate = total > 0 ? Math.round((results.summary.passed / total) * 100) + '%' : '0%';
  results.overallStatus = results.summary.failed === 0 ? 'PASSED' : 'FAILED';

  // 打印结果
  console.log('\n' + '='.repeat(60));
  console.log('测试结果');
  console.log('='.repeat(60));
  
  results.tests.forEach((test, i) => {
    const icon = test.status === 'passed' ? '✅' : test.status === 'warning' ? '⚠️' : '❌';
    console.log(`${i + 1}. ${icon} ${test.name}: ${test.status}`);
    console.log(`   ${test.details}`);
  });

  console.log('\n' + '-'.repeat(60));
  console.log(`通过: ${results.summary.passed} | 失败: ${results.summary.failed} | 警告: ${results.summary.warnings}`);
  console.log(`通过率: ${results.summary.passRate}`);
  console.log(`总体状态: ${results.overallStatus}`);
  console.log('='.repeat(60));

  // 保存JSON结果
  const jsonPath = path.join(OUTPUT_DIR, 'real_browser_test_results.json');
  fs.writeFileSync(jsonPath, JSON.stringify(results, null, 2));
  console.log('\nJSON报告:', jsonPath);

  return results;
}

// 运行测试
runTests()
  .then(results => {
    console.log('\n✅ 测试完成');
    process.exit(results.overallStatus === 'PASSED' ? 0 : 1);
  })
  .catch(error => {
    console.error('\n❌ 严重错误:', error);
    process.exit(1);
  });
