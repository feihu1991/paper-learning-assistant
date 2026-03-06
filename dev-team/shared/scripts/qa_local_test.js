/**
 * QA 真实页面自动化测试 - 本地版本
 */

const puppeteer = require('puppeteer');
const fs = require('fs');
const path = require('path');

const WEBSITE_URL = 'http://localhost:5173/';
const OUTPUT_DIR = path.join(__dirname, '..', 'output', 'qa');
const REPORT_DIR = path.join(__dirname, '..', 'reports');

console.log('='.repeat(60));
console.log('='.repeat(60));
console.log('\n测试目标:', WEBSITE_URL);
console.log('开始时间:', new Date().toISOString());

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
    console.log('\n[1/8] 启动浏览器...');
    browser = await puppeteer.launch({
      headless: 'new',
      args: ['--no-sandbox', '--disable-setuid-sandbox', '--disable-dev-shm-usage']
    });

    const page = await browser.newPage();
    await page.setViewport({ width: 1280, height: 800 });

    console.log('[2/8] 访问网站...');
    await page.goto(WEBSITE_URL, { waitUntil: 'networkidle0', timeout: 30000 });
    await new Promise(r => setTimeout(r, 2000));

    // 截图
    const screenshotPath = path.join(OUTPUT_DIR, 'local_test_screenshot.png');
    await page.screenshot({ path: screenshotPath, fullPage: true });
    console.log('[3/8] 截图已保存');

    // 测试1: 页面标题
    console.log('[4/8] 测试页面标题...');
    const title = await page.title();
    const hasCorrectTitle = title.includes('Paper') || title.includes('论文');
    results.tests.push({
      name: '页面标题',
      status: hasCorrectTitle ? 'passed' : 'failed',
      details: `标题: ${title}`
    });
    if (hasCorrectTitle) results.summary.passed++;
    else results.summary.failed++;

    // 测试2: 主标题显示
    console.log('[5/8] 测试主标题...');
    const mainHeading = await page.$('h1');
    const headingText = mainHeading ? await page.evaluate(el => el.textContent, mainHeading) : '';
    const hasMainHeading = headingText.includes('Paper') || headingText.includes('论文');
    results.tests.push({
      name: '主标题显示',
      status: hasMainHeading ? 'passed' : 'failed',
      details: `主标题: ${headingText}`
    });
    if (hasMainHeading) results.summary.passed++;
    else results.summary.failed++;

    // 测试3: 搜索框
    console.log('[6/8] 测试搜索框...');
    const searchBox = await page.$('input[type="text"]');
    const searchButton = await page.$('button');
    const hasSearch = searchBox && searchButton;
    results.tests.push({
      name: '搜索框和按钮',
      status: hasSearch ? 'passed' : 'failed',
      details: hasSearch ? '搜索框和Search按钮存在' : '搜索组件缺失'
    });
    if (hasSearch) results.summary.passed++;
    else results.summary.failed++;

    // 测试4: 论文卡片
    console.log('[7/8] 测试论文列表...');
    const papers = await page.evaluate(() => {
      const headings = Array.from(document.querySelectorAll('h2, h3, h4'));
      return headings.map(h => h.textContent.trim());
    });
    
    const hasAttentionPaper = papers.some(t => t.includes('Attention'));
    const hasBERTPaper = papers.some(t => t.includes('BERT'));
    const paperCount = papers.length;
    
    results.tests.push({
      name: '论文卡片显示',
      status: paperCount > 0 ? 'passed' : 'failed',
      details: `找到 ${paperCount} 个论文标题. Attention论文: ${hasAttentionPaper ? '✓' : '✗'}, BERT论文: ${hasBERTPaper ? '✓' : '✗'}`
    });
    if (paperCount > 0) results.summary.passed++;
    else results.summary.failed++;

    // 测试5: Attention Is All You Need 论文详情
    console.log('[8/8] 测试Attention论文详情...');
    const attentionDetails = await page.evaluate(() => {
      const headings = Array.from(document.querySelectorAll('h2, h3, h4'));
      const attentionHeading = headings.find(h => h.textContent.includes('Attention'));
      if (!attentionHeading) return null;
      
      const card = attentionHeading.closest('div[class*="bg-white"]') || attentionHeading.parentElement;
      const text = card ? card.textContent : '';
      return {
        hasHeading: true,
        text: text.substring(0, 200)
      };
    });
    
    const hasAttention = attentionDetails && attentionDetails.hasHeading;
    results.tests.push({
      name: 'Attention Is All You Need 论文',
      status: hasAttention ? 'passed' : 'failed',
      details: hasAttention ? '论文卡片已显示' : '论文未找到'
    });
    if (hasAttention) results.summary.passed++;
    else results.summary.failed++;

  } catch (error) {
    console.error('\n❌ 测试出错:', error.message);
    results.tests.push({
      name: '测试执行',
      status: 'failed',
      details: error.message
    });
    results.summary.failed++;
  } finally {
    if (browser) await browser.close();
  }

  const total = results.summary.passed + results.summary.failed + results.summary.warnings;
  results.summary.passRate = total > 0 ? Math.round((results.summary.passed / total) * 100) + '%' : '0%';
  results.overallStatus = results.summary.failed === 0 ? 'PASSED' : 'FAILED';

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

  const jsonPath = path.join(OUTPUT_DIR, 'local_qa_test_results.json');
  fs.writeFileSync(jsonPath, JSON.stringify(results, null, 2));
  console.log('\nJSON报告:', jsonPath);

  return results;
}

runTests()
  .then(results => {
    console.log('\n✅ 测试完成');
    process.exit(results.overallStatus === 'PASSED' ? 0 : 1);
  })
  .catch(error => {
    console.error('\n❌ 严重错误:', error);
    process.exit(1);
  });
