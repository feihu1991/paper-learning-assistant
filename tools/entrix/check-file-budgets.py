#!/usr/bin/env python3
"""
文件预算检查工具
检查变更文件是否超出预算限制
"""

import json
import subprocess
import sys
from pathlib import Path
from fnmatch import fnmatch

def load_budgets():
    """加载文件预算配置"""
    budget_file = Path(__file__).parent / "file_budgets.json"
    with open(budget_file, 'r', encoding='utf-8') as f:
        return json.load(f)

def get_changed_files():
    """获取 git 变更的文件列表"""
    result = subprocess.run(
        ['git', 'diff', '--cached', '--name-only'],
        capture_output=True,
        text=True,
        cwd=Path(__file__).parent.parent.parent
    )
    return result.stdout.strip().split('\n') if result.stdout.strip() else []

def count_lines(filepath):
    """计算文件行数"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            return sum(1 for _ in f)
    except Exception:
        return 0

def match_budget(filename, budgets):
    """匹配文件预算"""
    # 先检查例外配置
    for pattern, limit in budgets.get('exceptions', {}).items():
        if fnmatch(filename, pattern):
            return limit
    
    # 再检查默认配置
    for pattern, limit in budgets.get('default_budgets', {}).items():
        if fnmatch(filename, pattern):
            return limit
    
    return None

def main():
    budgets = load_budgets()
    changed_files = get_changed_files()
    
    violations = []
    warnings = []
    
    for filepath in changed_files:
        if not filepath or filepath.startswith('tools/entrix/'):
            continue
        
        full_path = Path(__file__).parent.parent.parent / filepath
        if not full_path.exists():
            continue
        
        line_count = count_lines(full_path)
        limit = match_budget(filepath, budgets)
        
        if limit is None:
            continue
        
        if line_count > limit:
            violations.append(f"❌ {filepath}: {line_count} 行 > 预算 {limit} 行")
        elif line_count > limit * 0.8:
            warnings.append(f"⚠️  {filepath}: {line_count} 行（接近预算 {limit} 行）")
    
    # 输出结果
    if warnings:
        print("🟡 预算警告:")
        for w in warnings:
            print(f"  {w}")
        print()
    
    if violations:
        print("🔴 预算违规:")
        for v in violations:
            print(f"  {v}")
        print()
        print("请重构或拆分文件后再提交。")
        sys.exit(1)
    
    if not violations and not warnings:
        print("✅ 文件预算检查通过")
    
    sys.exit(0)

if __name__ == '__main__':
    main()
