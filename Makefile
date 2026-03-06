.PHONY: help install dev build test docker-up docker-down docker-build deploy

help: ## 显示帮助信息
	@echo "可用命令:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

install: ## 安装所有依赖
	@echo "安装根目录依赖..."
	npm install
	@echo "安装后端依赖..."
	cd backend && npm install
	@echo "安装前端依赖..."
	cd frontend && npm install

dev: ## 启动开发服务器
	@echo "启动开发服务器..."
	npm run dev

build: ## 构建生产版本
	@echo "构建后端..."
	cd backend && npm run build
	@echo "构建前端..."
	cd frontend && npm run build

test: ## 运行所有测试
	@echo "运行后端测试..."
	cd backend && npm test
	@echo "运行前端测试..."
	cd frontend && npm test

docker-up: ## 使用Docker启动服务
	@echo "启动Docker服务..."
	docker-compose up -d

docker-down: ## 停止Docker服务
	@echo "停止Docker服务..."
	docker-compose down

docker-build: ## 构建Docker镜像
	@echo "构建Docker镜像..."
	docker-compose build

deploy: build ## 部署到生产环境
	@echo "部署到生产环境..."
	# 这里可以添加实际的部署命令
	@echo "部署完成!"

clean: ## 清理构建文件和依赖
	@echo "清理构建文件..."
	rm -rf backend/dist
	rm -rf frontend/dist
	rm -rf node_modules
	rm -rf backend/node_modules
	rm -rf frontend/node_modules

setup: install ## 完整设置项目
	@echo "复制环境变量文件..."
	cp .env.example .env
	@echo "请编辑 .env 文件并设置必要的环境变量"
	@echo "设置完成!"

db-migrate: ## 运行数据库迁移
	@echo "运行数据库迁移..."
	cd backend && npm run migrate

db-seed: ## 运行数据库种子
	@echo "运行数据库种子..."
	cd backend && npm run seed

lint: ## 运行代码检查
	@echo "检查后端代码..."
	cd backend && npm run lint
	@echo "检查前端代码..."
	cd frontend && npm run lint

format: ## 格式化代码
	@echo "格式化后端代码..."
	cd backend && npm run format
	@echo "格式化前端代码..."
	cd frontend && npm run format

logs: ## 查看Docker日志
	@echo "查看Docker容器日志..."
	docker-compose logs -f

status: ## 查看服务状态
	@echo "查看Docker容器状态..."
	docker-compose ps

restart: docker-down docker-up ## 重启服务
	@echo "服务已重启"

update: ## 更新依赖
	@echo "更新后端依赖..."
	cd backend && npm update
	@echo "更新前端依赖..."
	cd frontend && npm update
	@echo "更新根目录依赖..."
	npm update

version: ## 显示版本信息
	@echo "项目版本: $$(node -p "require('./package.json').version")"
	@echo "Node.js版本: $$(node --version)"
	@echo "npm版本: $$(npm --version)"
	@echo "Docker版本: $$(docker --version)"
	@echo "Docker Compose版本: $$(docker-compose --version)"