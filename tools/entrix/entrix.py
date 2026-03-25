#!/usr/bin/env python3
"""
Entrix - 反熵治理引擎
基于 Fitness Function 的代码质量检查系统
"""

import json
import subprocess
import sys
from pathlib import Path
from datetime import datetime

class EntrixEngine:
    """治理引擎"""
    
    def __init__(self, root_dir: Path):
        self.root_dir = root_dir
        self.fitness_dir = root_dir / "docs" / "fitness"
        self.results = []
    
    def load_dimensions(self):
        """加载所有 Fitness 维度"""
        dimensions = []
        for md_file in self.fitness_dir.glob("*.md"):
            dimension = self.parse_dimension(md_file)
            if dimension:
                dimensions.append(dimension)
        return dimensions
    
    def parse_dimension(self, filepath: Path):
        """解析维度文件"""
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 解析 frontmatter
        if not content.startswith('---'):
            return None
        
        end_idx = content.find('---', 3)
        if end_idx == -1:
            return None
        
        frontmatter = content[4:end_idx].strip()
        dimension = {}
        
        for line in frontmatter.split('\n'):
            if ':' in line:
                key, value = line.split(':', 1)
                dimension[key.strip()] = value.strip()
        
        dimension['file'] = filepath.name
        return dimension
    
    def run_check(self, dimension: dict) -> dict:
        """运行单个维度检查"""
        result = {
            'name': dimension.get('dimension', 'Unknown'),
            'weight': float(dimension.get('weight', 0.1)),
            'threshold': float(dimension.get('threshold', 80)),
            'passed': False,
            'score': 0,
            'errors': []
        }
        
        # 运行对应的检查脚本
        check_type = dimension.get('dimension', '').lower()
        
        if 'code quality' in check_type:
            result = self.check_code_quality(result)
        elif 'build' in check_type:
            result = self.check_build(result)
        elif 'api' in check_type:
            result = self.check_api(result)
        elif 'architecture' in check_type:
            result = self.check_architecture(result)
        
        return result
    
    def check_code_quality(self, result: dict) -> dict:
        """代码质量检查"""
        # 文件预算检查
        budget_script = self.root_dir / "tools" / "entrix" / "check-file-budgets.py"
        if budget_script.exists():
            proc = subprocess.run(
                ['python3', str(budget_script)],
                capture_output=True,
                text=True,
                cwd=str(self.root_dir)
            )
            if proc.returncode != 0:
                result['errors'].append(f"文件预算检查失败:\n{proc.stdout}")
            else:
                result['score'] += 40
        
        # 简单检查：统计 Kotlin 文件数量
        kotlin_files = list((self.root_dir / "android-app" / "app" / "src" / "main" / "java").rglob("*.kt"))
        if len(kotlin_files) > 0:
            result['score'] += 60
            result['passed'] = True
        
        return result
    
    def check_build(self, result: dict) -> dict:
        """构建检查（简化版，实际应该运行 gradle）"""
        # 检查关键文件是否存在
        required_files = [
            "android-app/app/build.gradle.kts",
            "android-app/app/src/main/AndroidManifest.xml",
            "android-app/app/src/main/java/com/paperlearning/assistant/MainActivity.kt"
        ]
        
        missing = []
        for f in required_files:
            if not (self.root_dir / f).exists():
                missing.append(f)
        
        if missing:
            result['errors'].append(f"缺少关键文件：{', '.join(missing)}")
        else:
            result['score'] = 100
            result['passed'] = True
        
        return result
    
    def check_api(self, result: dict) -> dict:
        """API 契约检查"""
        required_files = [
            "android-app/app/src/main/java/com/paperlearning/assistant/data/remote/arxiv/ArxivApiService.kt",
            "android-app/app/src/main/java/com/paperlearning/assistant/data/remote/llm/LlmApiService.kt"
        ]
        
        missing = []
        for f in required_files:
            if not (self.root_dir / f).exists():
                missing.append(f)
        
        if missing:
            result['errors'].append(f"缺少 API 接口：{', '.join(missing)}")
            result['score'] = 50
        else:
            result['score'] = 100
            result['passed'] = True
        
        return result
    
    def check_architecture(self, result: dict) -> dict:
        """架构检查"""
        required_dirs = [
            "android-app/app/src/main/java/com/paperlearning/assistant/data",
            "android-app/app/src/main/java/com/paperlearning/assistant/domain",
            "android-app/app/src/main/java/com/paperlearning/assistant/ui",
            "android-app/app/src/main/java/com/paperlearning/assistant/di"
        ]
        
        missing = []
        for d in required_dirs:
            if not (self.root_dir / d).exists():
                missing.append(d)
        
        if missing:
            result['errors'].append(f"缺少架构层：{', '.join(missing)}")
            result['score'] = 50
        else:
            result['score'] = 100
            result['passed'] = True
        
        return result
    
    def run_all(self):
        """运行所有检查"""
        print(f"🔍 Entrix 治理引擎 - {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
        print("=" * 60)
        
        dimensions = self.load_dimensions()
        total_score = 0
        total_weight = 0
        
        for dim in dimensions:
            result = self.run_check(dim)
            self.results.append(result)
            
            status = "✅" if result['passed'] else "❌"
            print(f"{status} {result['name']}: {result['score']}分 (阈值：{result['threshold']})")
            
            if result['errors']:
                for error in result['errors']:
                    print(f"   ⚠️  {error}")
            
            total_score += result['score'] * result['weight']
            total_weight += result['weight']
        
        print("=" * 60)
        
        final_score = total_score / total_weight if total_weight > 0 else 0
        print(f"📊 总分：{final_score:.1f}分")
        
        if final_score >= 80:
            print("✅ 治理检查通过")
            return 0
        else:
            print("❌ 治理检查失败，请修复问题后重试")
            return 1

def main():
    root_dir = Path(__file__).parent.parent.parent
    engine = EntrixEngine(root_dir)
    sys.exit(engine.run_all())

if __name__ == '__main__':
    main()
