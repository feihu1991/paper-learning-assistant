#!/usr/bin/env node
/**
 * 任务队列监控脚本
 * 定期检查 /dev-team/shared/tasks/ 目录中的新任务
 * 并根据任务类型触发相应的 agent 处理
 */

const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');
const { promisify } = require('util');

const execAsync = promisify(exec);

// 配置
const TASKS_DIR = path.join(__dirname, '..', 'tasks');
const PROCESSING_DIR = path.join(__dirname, '..', 'processing');
const OUTPUT_DIR = path.join(__dirname, '..', 'output');
const LOG_FILE = path.join(__dirname, '..', 'logs', 'task_monitor.log');

// 确保目录存在
[TASKS_DIR, PROCESSING_DIR, OUTPUT_DIR, path.dirname(LOG_FILE)].forEach(dir => {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
});

// 日志函数
function log(message, level = 'INFO') {
  const timestamp = new Date().toISOString();
  const logMessage = `[${timestamp}] [${level}] ${message}\n`;
  
  console.log(logMessage.trim());
  fs.appendFileSync(LOG_FILE, logMessage);
}

// 读取任务文件
function readTaskFile(taskPath) {
  try {
    const content = fs.readFileSync(taskPath, 'utf8');
    return JSON.parse(content);
  } catch (error) {
    log(`无法读取或解析任务文件 ${taskPath}: ${error.message}`, 'ERROR');
    return null;
  }
}

// 根据任务类型确定处理方式
function getAgentForTaskType(taskType) {
  const agentMap = {
    'be': 'backend',
    'fe': 'frontend',
    'qa': 'qa',
    'devops': 'devops',
    'architect': 'architect'
  };
  
  return agentMap[taskType] || 'unknown';
}

// 处理单个任务
async function processTask(taskFile) {
  const taskName = path.basename(taskFile);
  const taskPath = path.join(TASKS_DIR, taskFile);
  const processingPath = path.join(PROCESSING_DIR, taskFile);
  
  log(`开始处理任务: ${taskName}`);
  
  // 读取任务内容
  const task = readTaskFile(taskPath);
  if (!task) {
    return false;
  }
  
  const taskType = task.type;
  const agent = getAgentForTaskType(taskType);
  
  if (agent === 'unknown') {
    log(`未知的任务类型: ${taskType}`, 'WARN');
    return false;
  }
  
  log(`任务类型: ${taskType}, 分配给: ${agent} agent`);
  
  try {
    // 移动任务到处理中目录
    fs.renameSync(taskPath, processingPath);
    log(`任务已移动到处理中目录: ${processingPath}`);
    
    // 调用相应的 agent 处理脚本
    let processScript = null;
    let processFunction = null;
    
    switch (agent) {
      case 'architect':
        processScript = require('./process_architect_task.js');
        processFunction = processScript.processArchitectTask;
        break;
      case 'backend':
        processScript = require('./process_backend_task.js');
        processFunction = processScript.processBackendTask;
        break;
      case 'frontend':
        processScript = require('./process_frontend_task.js');
        processFunction = processScript.processFrontendTask;
        break;
      case 'qa':
        // QA 处理脚本待实现
        log(`QA agent 处理脚本待实现，任务 ${taskFile} 将模拟处理`, 'WARN');
        break;
      case 'devops':
        // DevOps 处理脚本待实现
        log(`DevOps agent 处理脚本待实现，任务 ${taskFile} 将模拟处理`, 'WARN');
        break;
      default:
        log(`未知的 agent 类型: ${agent}`, 'WARN');
    }
    
    if (processFunction) {
      // 调用实际的 agent 处理函数
      log(`调用 ${agent} agent 处理任务...`);
      const success = await processFunction(taskFile);
      
      if (success) {
        log(`${agent} agent 成功处理任务 ${taskFile}`);
        return true;
      } else {
        log(`${agent} agent 处理任务 ${taskFile} 失败`, 'ERROR');
        return false;
      }
    } else {
      // 对于未实现的 agent，创建模拟输出
      const outputDir = path.join(OUTPUT_DIR, agent);
      if (!fs.existsSync(outputDir)) {
        fs.mkdirSync(outputDir, { recursive: true });
      }
      
      const outputFile = path.join(outputDir, `${task.task_id}_result.json`);
      const result = {
        task_id: task.task_id,
        task_type: task.type,
        objective: task.objective,
        status: 'processing',
        started_at: new Date().toISOString(),
        agent: agent,
        notes: `任务已分配给 ${agent} agent，处理脚本待实现`
      };
      
      fs.writeFileSync(outputFile, JSON.stringify(result, null, 2));
      log(`创建了模拟输出文件: ${outputFile}`);
      
      return true;
    }
    
  } catch (error) {
    log(`处理任务时出错: ${error.message}`, 'ERROR');
    // 如果出错，将任务移回原处
    if (fs.existsSync(processingPath)) {
      fs.renameSync(processingPath, taskPath);
    }
    return false;
  }
}

// 主监控函数
async function monitorTasks() {
  log('开始监控任务队列...');
  
  try {
    // 获取所有任务文件
    const taskFiles = fs.readdirSync(TASKS_DIR)
      .filter(file => file.endsWith('.json'))
      .filter(file => !file.startsWith('.')); // 排除隐藏文件
    
    log(`发现 ${taskFiles.length} 个待处理任务`);
    
    // 处理每个任务
    for (const taskFile of taskFiles) {
      const success = await processTask(taskFile);
      if (success) {
        log(`任务 ${taskFile} 处理成功`);
      } else {
        log(`任务 ${taskFile} 处理失败`);
      }
      
      // 避免同时处理太多任务
      await new Promise(resolve => setTimeout(resolve, 1000));
    }
    
    log('任务监控完成');
    
  } catch (error) {
    log(`监控任务时出错: ${error.message}`, 'ERROR');
  }
}

// 运行监控
if (require.main === module) {
  monitorTasks().then(() => {
    process.exit(0);
  }).catch(error => {
    log(`监控脚本执行失败: ${error.message}`, 'ERROR');
    process.exit(1);
  });
}

module.exports = { monitorTasks, processTask, log };