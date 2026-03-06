#!/usr/bin/env node
/**
 * 工作流程自动化配置脚本
 * 自动配置 dev-team 工作流程所需的所有组件
 */

const fs = require('fs');
const path = require('path');

// 配置
const SHARED_DIR = path.join(__dirname, '..');
const LOG_FILE = path.join(SHARED_DIR, 'logs', 'setup_workflow.log');

// 确保日志目录存在
if (!fs.existsSync(path.dirname(LOG_FILE))) {
  fs.mkdirSync(path.dirname(LOG_FILE), { recursive: true });
}

// 日志函数
function log(message, level = 'INFO') {
  const timestamp = new Date().toISOString();
  const logMessage = `[${timestamp}] [${level}] ${message}\n`;
  
  console.log(logMessage.trim());
  fs.appendFileSync(LOG_FILE, logMessage);
}

// 创建目录结构
function createDirectoryStructure() {
  log('开始创建目录结构...');
  
  const directories = [
    'tasks',
    'processing', 
    'output/be',
    'output/fe',
    'output/qa',
    'output/devops',
    'output/architect',
    'docs',
    'reports',
    'bugs',
    'scripts',
    'logs'
  ];
  
  let created = 0;
  let existing = 0;
  
  directories.forEach(dir => {
    const fullPath = path.join(SHARED_DIR, dir);
    if (!fs.existsSync(fullPath)) {
      fs.mkdirSync(fullPath, { recursive: true });
      log(`创建目录: ${dir}`);
      created++;
    } else {
      log(`目录已存在: ${dir}`);
      existing++;
    }
  });
  
  log(`目录结构创建完成: 创建了 ${created} 个目录, ${existing} 个已存在`);
  return { created, existing };
}

// 创建 agent 身份文件模板
function createAgentIdentityFiles() {
  log('开始创建 agent 身份文件模板...');
  
  const agents = [
    {
      name: 'QA Agent',
      type: 'qa',
      description: '质量保证代理',
      emoji: '🧪',
      capabilities: [
        '自动化测试 (单元测试、集成测试、端到端测试)',
        '代码质量检查 (ESLint、代码规范、安全扫描)',
        '性能测试 (响应时间、内存使用、负载测试)',
        '测试报告生成 (覆盖率、质量、性能报告)',
        '验收确认 (自动化验收、Bug跟踪)'
      ]
    },
    {
      name: 'DevOps Agent',
      type: 'devops',
      description: '运维自动化代理',
      emoji: '🔧',
      capabilities: [
        '自动化部署 (应用部署、环境管理、回滚机制)',
        'CI/CD 流水线 (代码构建、测试流水线、质量门禁)',
        '基础设施即代码 (环境编排、资源管理、配置漂移检测)',
        '监控告警 (应用监控、日志管理、健康检查)',
        '安全合规 (安全扫描、合规检查、漏洞管理)'
      ]
    },
    {
      name: 'Architect Agent',
      type: 'architect',
      description: '架构师代理',
      emoji: '🏗️',
      capabilities: [
        '系统架构设计 (架构模式选择、组件设计、技术选型)',
        '性能优化 (系统瓶颈分析、优化方案设计、容量规划)',
        '技术债务管理 (债务识别、优先级评估、偿还计划)',
        '系统演进 (演进路线图、重构计划、技术升级)',
        '质量和标准 (架构标准、代码规范、设计模式)'
      ]
    }
  ];
  
  agents.forEach(agent => {
    const identityFile = path.join(SHARED_DIR, 'docs', `${agent.type}_agent_identity.md`);
    
    const content = `# ${agent.name} - 身份文件

## 基本信息
- **名称**: ${agent.name}
- **类型**: ${agent.type}
- **描述**: ${agent.description}
- **Emoji**: ${agent.emoji}
- **创建时间**: ${new Date().toISOString()}

## 核心能力
${agent.capabilities.map(cap => `- ${cap}`).join('\n')}

## 工作原则
1. **质量优先**: 确保所有产出符合质量标准
2. **自动化优先**: 尽可能自动化重复性工作
3. **文档化**: 所有决策和产出都要有文档记录
4. **协作**: 与其他 agent 紧密合作，确保项目成功

## 技术栈
- **待配置**: 根据具体项目需求配置

## 工作流程
1. 监控任务队列中的 ${agent.type} 类型任务
2. 读取任务文件，理解需求
3. 执行相应的处理逻辑
4. 将产出保存到 /dev-team/shared/output/${agent.type}/
5. 生成处理报告

## 质量标准
- 代码质量: ESLint 错误为 0，警告 ≤ 5
- 测试覆盖率: 单元测试 ≥ 80%，集成测试 ≥ 70%
- 性能指标: API 响应时间 P95 < 200ms
- 安全标准: 无 Critical/High 级别漏洞

## 沟通方式
- 通过任务文件接收需求
- 通过产出文件交付结果
- 通过报告文件提供状态更新
- 通过 Bug 系统反馈问题

---
*此文件由工作流程自动化脚本生成*`;

    fs.writeFileSync(identityFile, content);
    log(`创建了 ${agent.name} 身份文件: ${identityFile}`);
  });
  
  log('agent 身份文件创建完成');
}

// 创建任务模板
function createTaskTemplates() {
  log('开始创建任务模板...');
  
  const templates = [
    {
      name: 'backend_task_template.json',
      type: 'be',
      content: {
        task_id: "backend_001",
        type: "be",
        objective: "实现后端API服务",
        specs: {
          endpoints: ["GET /api/users", "POST /api/users"],
          database: "PostgreSQL",
          framework: "Express.js",
          authentication: "JWT"
        },
        shared_context: ["/dev-team/shared/docs/api_style.md"],
        priority: "medium",
        deadline: "2026-03-06T18:00:00Z",
        output_location: "/dev-team/shared/output/be/"
      }
    },
    {
      name: 'frontend_task_template.json',
      type: 'fe',
      content: {
        task_id: "frontend_001",
        type: "fe",
        objective: "实现前端用户界面",
        specs: {
          pages: ["首页", "用户详情页", "设置页"],
          framework: "React",
          styling: "Tailwind CSS",
          state_management: "Redux"
        },
        shared_context: ["/dev-team/shared/docs/ui_style.md"],
        priority: "medium",
        deadline: "2026-03-07T12:00:00Z",
        output_location: "/dev-team/shared/output/fe/"
      }
    },
    {
      name: 'qa_task_template.json',
      type: 'qa',
      content: {
        task_id: "qa_001",
        type: "qa",
        objective: "执行质量保证测试",
        specs: {
          test_types: ["单元测试", "集成测试", "性能测试"],
          coverage_threshold: 80,
          performance_targets: {
            response_time: "200ms",
            error_rate: "0.1%"
          }
        },
        shared_context: ["/dev-team/shared/docs/qa_standards.md"],
        priority: "high",
        deadline: "2026-03-06T16:00:00Z",
        output_location: "/dev-team/shared/output/qa/"
      }
    }
  ];
  
  templates.forEach(template => {
    const templateFile = path.join(SHARED_DIR, 'docs', template.name);
    fs.writeFileSync(templateFile, JSON.stringify(template.content, null, 2));
    log(`创建了 ${template.type} 任务模板: ${templateFile}`);
  });
  
  log('任务模板创建完成');
}

// 创建监控脚本的 package.json
function createPackageJson() {
  log('创建 package.json 用于监控脚本...');
  
  const packageJson = {
    name: "dev-team-workflow",
    version: "1.0.0",
    description: "Dev Team 工作流程自动化",
    main: "scripts/monitor_tasks.js",
    scripts: {
      "monitor": "node scripts/monitor_tasks.js",
      "setup": "node scripts/setup_workflow.js",
      "test": "echo \"Error: no test specified\" && exit 1"
    },
    "dependencies": {
    },
    "devDependencies": {
    },
    "engines": {
      "node": ">=14.0.0"
    }
  };
  
  const packageFile = path.join(SHARED_DIR, 'package.json');
  fs.writeFileSync(packageFile, JSON.stringify(packageJson, null, 2));
  log(`创建了 package.json: ${packageFile}`);
}

// 创建 README 文档
function createReadme() {
  log('创建 README 文档...');
  
  const readmeContent = `# Dev Team 工作流程自动化

## 概述
这是一个自动化的工作流程系统，用于协调多个 AI agent 协作完成项目任务。

## 目录结构
\`\`\`
/dev-team/shared/
├── tasks/          # 任务队列 (PM 创建任务文件)
├── processing/     # 正在处理的任务
├── output/         # 产出目录
│   ├── be/         # 后端产出
│   ├── fe/         # 前端产出
│   ├── qa/         # 测试报告
│   ├── devops/     # 部署配置
│   └── architect/  # 架构设计
├── docs/           # 文档和模板
├── reports/        # 完成报告
├── bugs/           # Bug反馈
├── scripts/        # 自动化脚本
└── logs/           # 系统日志
\`\`\`

## 工作流程

### 1. 任务创建
- PM 在 \`tasks/\` 目录创建 JSON 格式的任务文件
- 任务文件包含: task_id, type, objective, specs, priority, deadline

### 2. 任务监控
- 监控脚本每分钟检查 \`tasks/\` 目录
- 根据任务类型分配给相应的 agent
- 任务被移动到 \`processing/\` 目录

### 3. 任务处理
- 相应的 agent 处理任务
- 产出保存到 \`output/{type}/\` 目录
- 生成处理报告

### 4. 质量检查
- QA Agent 检查所有产出
- 发现问题时提交到 \`bugs/\` 目录
- 生成质量报告

### 5. 项目验收
- PM 验收最终产出
- 生成项目报告
- 归档项目文件

## 快速开始

### 安装依赖
\`\`\`bash
cd /dev-team/shared
npm install
\`\`\`

### 运行监控
\`\`\`bash
npm run monitor
\`\`\`

### 设置工作流程
\`\`\`bash
npm run setup
\`\`\`

## Agent 配置

系统支持以下类型的 agent:
- **BE Agent**: 后端开发
- **FE Agent**: 前端开发  
- **QA Agent**: 质量保证
- **DevOps Agent**: 运维部署
- **Architect Agent**: 架构设计

每个 agent 的详细配置见 \`docs/\` 目录。

## 任务文件格式

任务文件必须是 JSON 格式，包含以下字段:
\`\`\`json
{
  "task_id": "unique_id",
  "type": "be|fe|qa|devops|architect",
  "objective": "任务目标描述",
  "specs": {
    // 具体规格和要求
  },
  "shared_context": [
    "相关文档路径"
  ],
  "priority": "high|medium|low",
  "deadline": "ISO 8601 时间格式",
  "output_location": "产出保存路径"
}
\`\`\`

## 监控和日志

- 监控脚本每分钟运行一次
- 日志保存在 \`logs/\` 目录
- 可以通过 cron 任务配置监控频率

## 故障排除

### 常见问题
1. **任务没有被处理**: 检查监控脚本是否在运行
2. **产出目录不存在**: 运行 \`npm run setup\` 重新创建目录
3. **权限问题**: 确保脚本有读写权限

### 查看日志
\`\`\`bash
tail -f logs/task_monitor.log
\`\`\`

## 扩展和定制

可以通过修改以下文件定制工作流程:
- \`scripts/monitor_tasks.js\`: 任务监控逻辑
- \`scripts/setup_workflow.js\`: 系统设置
- \`docs/*_template.json\`: 任务模板

---

*最后更新: ${new Date().toISOString()}*
*由工作流程自动化脚本生成*`;

  const readmeFile = path.join(SHARED_DIR, 'README.md');
  fs.writeFileSync(readmeFile, readmeContent);
  log(`创建了 README.md: ${readmeFile}`);
}

// 主函数
async function main() {
  log('开始自动化配置工作流程...');
  log('========================================');
  
  try {
    // 1. 创建目录结构
    const dirResult = createDirectoryStructure();
    log('========================================');
    
    // 2. 创建 agent 身份文件
    createAgentIdentityFiles();
    log('========================================');
    
    // 3. 创建任务模板
    createTaskTemplates();
    log('========================================');
    
    // 4. 创建 package.json
    createPackageJson();
    log('========================================');
    
    // 5. 创建 README
    createReadme();
    log('========================================');
    
    log('工作流程自动化配置完成！');
    log(`总计: 创建了 ${dirResult.created} 个新目录`);
    log('下一步: 运行 npm run monitor 开始监控任务队列');
    
  } catch (error) {
    log(`配置过程中出错: ${error.message}`, 'ERROR');
    process.exit(1);
  }
}

// 运行配置
if (require.main === module) {
  main();
}

module.exports = { main };