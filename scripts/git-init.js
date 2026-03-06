#!/usr/bin/env node
/**
 * Git初始化脚本
 * 初始化Git仓库并创建初始提交
 */

const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

const PROJECT_DIR = path.join(__dirname, '..');
const GITHUB_USERNAME = 'yourusername'; // 请替换为你的GitHub用户名
const REPO_NAME = 'paper-learning-assistant';

console.log('开始初始化Git仓库...');

try {
  // 进入项目目录
  process.chdir(PROJECT_DIR);
  
  // 初始化Git仓库
  console.log('1. 初始化Git仓库...');
  execSync('git init', { stdio: 'inherit' });
  
  // 添加所有文件
  console.log('2. 添加文件到暂存区...');
  execSync('git add .', { stdio: 'inherit' });
  
  // 创建初始提交
  console.log('3. 创建初始提交...');
  execSync('git commit -m "Initial commit: Paper Learning Assistant website"', { stdio: 'inherit' });
  
  // 创建GitHub仓库URL
  const githubRepoUrl = `https://github.com/${GITHUB_USERNAME}/${REPO_NAME}.git`;
  
  console.log('\n✅ Git仓库初始化完成！');
  console.log('\n📋 下一步操作：');
  console.log('\n1. 在GitHub上创建新仓库:');
  console.log(`   访问 https://github.com/new`);
  console.log(`   仓库名称: ${REPO_NAME}`);
  console.log(`   描述: Paper Learning Assistant website`);
  console.log(`   选择: Public (公开)`);
  console.log(`   不要初始化README、.gitignore或LICENSE`);
  
  console.log('\n2. 添加远程仓库并推送代码:');
  console.log(`   git remote add origin ${githubRepoUrl}`);
  console.log(`   git branch -M main`);
  console.log(`   git push -u origin main`);
  
  console.log('\n3. 设置GitHub Pages (可选):');
  console.log(`   访问 https://github.com/${GITHUB_USERNAME}/${REPO_NAME}/settings/pages`);
  console.log(`   分支: gh-pages 或 main`);
  console.log(`   文件夹: /frontend/dist`);
  
  console.log('\n4. 配置GitHub Secrets (用于CI/CD):');
  console.log(`   访问 https://github.com/${GITHUB_USERNAME}/${REPO_NAME}/settings/secrets/actions`);
  console.log(`   添加以下secrets:`);
  console.log(`   - VITE_API_URL: 生产环境API地址`);
  console.log(`   - OPENAI_API_KEY: OpenAI API密钥`);
  
  console.log('\n🎉 完成！你的项目现在可以部署到GitHub了。');
  
} catch (error) {
  console.error('初始化Git仓库时出错:', error.message);
  process.exit(1);
}