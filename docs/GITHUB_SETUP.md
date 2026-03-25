# GitHub 上传指南

## 前提条件

1. 拥有 GitHub 账号
2. 已安装 Git
3. 已配置 SSH 密钥或使用 HTTPS

## 步骤一：创建 GitHub 仓库

1. 访问 https://github.com/new
2. 填写仓库信息:
   - **Repository name**: `paper-tutor`
   - **Description**: "A paper learning and analysis platform"
   - **Visibility**: Public (或 Private)
   - **不要**勾选 "Initialize this repository with a README"
3. 点击 "Create repository"

## 步骤二：初始化本地 Git 仓库

```bash
cd /home/admin/openclaw/workspace/paper-tutor

# 初始化 Git 仓库
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit: Paper Tutor project structure"
```

## 步骤三：关联远程仓库

### 方式 A: 使用 HTTPS

```bash
# 关联远程仓库 (替换 YOUR_USERNAME 为你的 GitHub 用户名)
git remote add origin https://github.com/YOUR_USERNAME/paper-tutor.git

# 推送到 GitHub
git push -u origin main
```

如果提示认证失败，需要使用 Personal Access Token:
1. 访问 https://github.com/settings/tokens
2. 生成新 Token (勾选 `repo` 权限)
3. 使用 Token 代替密码推送

### 方式 B: 使用 SSH (推荐)

```bash
# 生成 SSH 密钥 (如果还没有)
ssh-keygen -t ed25519 -C "your_email@example.com"

# 添加公钥到 GitHub
# 1. 复制公钥内容
cat ~/.ssh/id_ed25519.pub

# 2. 访问 https://github.com/settings/ssh/new
# 3. 粘贴公钥内容并保存

# 关联远程仓库
git remote add origin git@github.com:YOUR_USERNAME/paper-tutor.git

# 推送到 GitHub
git push -u origin main
```

## 步骤四：验证推送

访问 https://github.com/YOUR_USERNAME/paper-tutor 确认文件已上传。

## 后续开发流程

```bash
# 1. 创建新分支
git checkout -b feature/paper-search

# 2. 开发并提交
git add .
git commit -m "feat: implement paper search functionality"

# 3. 推送到远程
git push origin feature/paper-search

# 4. 在 GitHub 上创建 Pull Request
# 访问 https://github.com/YOUR_USERNAME/paper-tutor/pulls
```

## 常用 Git 命令

```bash
# 查看状态
git status

# 查看提交历史
git log --oneline

# 拉取最新代码
git pull origin main

# 撤销未提交的修改
git checkout -- <filename>

# 查看远程仓库
git remote -v
```

## 更新 README 中的链接

推送后，记得更新 README.md 中的占位符:

```bash
# 替换 YOUR_USERNAME 为你的实际用户名
sed -i 's/YOUR_USERNAME/你的 GitHub 用户名/g' README.md

git add README.md
git commit -m "docs: update GitHub links"
git push
```

## 启用 GitHub Actions (可选)

在 `.github/workflows/` 目录创建 CI/CD 工作流文件，实现自动构建和测试。

## 启用 GitHub Pages (可选)

如需展示项目文档:

1. 访问 Settings → Pages
2. Source 选择 `main` 分支和 `/docs` 文件夹
3. 访问 `https://YOUR_USERNAME.github.io/paper-tutor`
