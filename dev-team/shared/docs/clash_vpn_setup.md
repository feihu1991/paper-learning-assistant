# Clash VPN 自动配置指南

## 概述
本指南将帮助你配置Clash VPN，实现自动开启和切换代理功能。使用你提供的订阅地址：`https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df`

## 安装步骤

### 1. 下载Clash for Windows
1. 访问 GitHub 发布页面：https://github.com/Fndroid/clash_for_windows_pkg/releases
2. 下载最新版本的 `Clash.for.Windows.Setup.x.x.x.exe`
3. 运行安装程序，按照提示完成安装

### 2. 配置订阅
1. 打开 Clash for Windows
2. 点击左侧的 **Profiles** (配置文件)
3. 在顶部输入框粘贴你的订阅地址：
   ```
   https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df
   ```
4. 点击 **Download** 按钮下载配置文件
5. 下载完成后，点击配置文件名称激活

### 3. 系统代理设置
1. 点击左侧的 **General** (常规)
2. 开启 **System Proxy** (系统代理)
3. 开启 **Start with Windows** (开机自启) - 可选

## 自动化脚本

### Windows PowerShell 脚本
```powershell
# clash_auto.ps1 - Clash VPN 自动控制脚本

# 配置参数
$CLASH_PATH = "C:\Program Files\Clash for Windows\Clash for Windows.exe"
$CONFIG_URL = "https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df"
$CONFIG_DIR = "$env:USERPROFILE\.config\clash"

# 函数：启动Clash
function Start-ClashVPN {
    Write-Host "正在启动 Clash VPN..." -ForegroundColor Green
    
    # 检查Clash是否已运行
    $clashProcess = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
    if ($clashProcess) {
        Write-Host "Clash 已在运行" -ForegroundColor Yellow
    } else {
        # 启动Clash
        Start-Process -FilePath $CLASH_PATH
        Write-Host "Clash 启动成功" -ForegroundColor Green
        Start-Sleep -Seconds 5  # 等待启动完成
    }
    
    # 更新配置文件
    Update-ClashConfig
    
    # 设置系统代理
    Set-SystemProxy -Enable $true
}

# 函数：停止Clash
function Stop-ClashVPN {
    Write-Host "正在停止 Clash VPN..." -ForegroundColor Yellow
    
    # 关闭系统代理
    Set-SystemProxy -Enable $false
    
    # 停止Clash进程
    Get-Process "Clash for Windows" -ErrorAction SilentlyContinue | Stop-Process -Force
    Write-Host "Clash 已停止" -ForegroundColor Green
}

# 函数：更新配置文件
function Update-ClashConfig {
    Write-Host "正在更新配置文件..." -ForegroundColor Cyan
    
    # 下载最新配置文件
    $configFile = Join-Path $CONFIG_DIR "config.yaml"
    try {
        Invoke-WebRequest -Uri $CONFIG_URL -OutFile $configFile -UseBasicParsing
        Write-Host "配置文件更新成功" -ForegroundColor Green
    } catch {
        Write-Host "配置文件更新失败: $_" -ForegroundColor Red
    }
}

# 函数：设置系统代理
function Set-SystemProxy {
    param(
        [bool]$Enable
    )
    
    if ($Enable) {
        Write-Host "正在启用系统代理..." -ForegroundColor Cyan
        # 设置代理服务器（Clash默认端口：7890）
        netsh winhttp set proxy proxy-server="http=127.0.0.1:7890;https=127.0.0.1:7890" bypass-list="localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.*"
        Write-Host "系统代理已启用" -ForegroundColor Green
    } else {
        Write-Host "正在禁用系统代理..." -ForegroundColor Cyan
        netsh winhttp reset proxy
        Write-Host "系统代理已禁用" -ForegroundColor Green
    }
}

# 函数：测试连接
function Test-VPNConnection {
    Write-Host "正在测试VPN连接..." -ForegroundColor Cyan
    
    $testUrls = @(
        "https://www.google.com",
        "https://www.youtube.com",
        "https://twitter.com"
    )
    
    foreach ($url in $testUrls) {
        try {
            $response = Invoke-WebRequest -Uri $url -TimeoutSec 5 -UseBasicParsing -ErrorAction Stop
            Write-Host "✓ $url - 连接成功" -ForegroundColor Green
        } catch {
            Write-Host "✗ $url - 连接失败" -ForegroundColor Red
        }
    }
}

# 主菜单
function Show-Menu {
    Clear-Host
    Write-Host "=== Clash VPN 控制中心 ===" -ForegroundColor Magenta
    Write-Host "1. 启动 VPN" -ForegroundColor Cyan
    Write-Host "2. 停止 VPN" -ForegroundColor Cyan
    Write-Host "3. 更新配置" -ForegroundColor Cyan
    Write-Host "4. 测试连接" -ForegroundColor Cyan
    Write-Host "5. 查看状态" -ForegroundColor Cyan
    Write-Host "6. 退出" -ForegroundColor Red
    Write-Host "=========================" -ForegroundColor Magenta
}

# 主程序
while ($true) {
    Show-Menu
    $choice = Read-Host "请选择操作 (1-6)"
    
    switch ($choice) {
        "1" { Start-ClashVPN }
        "2" { Stop-ClashVPN }
        "3" { Update-ClashConfig }
        "4" { Test-VPNConnection }
        "5" { 
            Write-Host "当前状态:" -ForegroundColor Cyan
            $clashProcess = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
            if ($clashProcess) {
                Write-Host "Clash: 运行中" -ForegroundColor Green
            } else {
                Write-Host "Clash: 未运行" -ForegroundColor Red
            }
            
            # 检查代理设置
            $proxy = netsh winhttp show proxy
            if ($proxy -match "Direct access") {
                Write-Host "系统代理: 未启用" -ForegroundColor Yellow
            } else {
                Write-Host "系统代理: 已启用" -ForegroundColor Green
            }
        }
        "6" { 
            Write-Host "再见！" -ForegroundColor Magenta
            exit 0
        }
        default { Write-Host "无效选择" -ForegroundColor Red }
    }
    
    Read-Host "按回车键继续..."
}
```

### 定时任务配置
创建Windows任务计划程序任务，实现自动开启VPN：

```xml
<!-- clash_auto_start.xml - 任务计划程序配置 -->
<?xml version="1.0" encoding="UTF-16"?>
<Task version="1.4" xmlns="http://schemas.microsoft.com/windows/2004/02/mit/task">
  <RegistrationInfo>
    <Date>2024-03-05T21:00:00</Date>
    <Author>SYSTEM</Author>
    <Description>自动启动Clash VPN</Description>
  </RegistrationInfo>
  <Triggers>
    <!-- 登录时触发 -->
    <LogonTrigger>
      <Enabled>true</Enabled>
      <UserId>S-1-5-21-3623811015-3361044348-30300820-1013</UserId>
    </LogonTrigger>
    <!-- 特定时间触发（如工作日9点） -->
    <CalendarTrigger>
      <StartBoundary>2024-03-06T09:00:00</StartBoundary>
      <Enabled>true</Enabled>
      <ScheduleByWeek>
        <DaysOfWeek>
          <Monday />
          <Tuesday />
          <Wednesday />
          <Thursday />
          <Friday />
        </DaysOfWeek>
        <WeeksInterval>1</WeeksInterval>
      </ScheduleByWeek>
    </CalendarTrigger>
  </Triggers>
  <Actions Context="Author">
    <Exec>
      <Command>powershell.exe</Command>
      <Arguments>-ExecutionPolicy Bypass -File "C:\path\to\clash_auto.ps1" -StartVPN</Arguments>
    </Exec>
  </Actions>
</Task>
```

## 与OpenClaw集成

### 1. 创建VPN控制任务
```json
{
  "task_id": "vpn_001",
  "type": "devops",
  "objective": "自动控制Clash VPN连接",
  "specs": {
    "action": "start|stop|update|test",
    "subscription_url": "https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df",
    "schedule": {
      "start_time": "09:00",
      "stop_time": "18:00",
      "weekdays": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]
    }
  }
}
```

### 2. 集成到早报晚报
在早报和晚报中添加VPN状态信息：

```markdown
### 🔒 VPN状态
- **状态**: {运行中/已停止}
- **连接节点**: {当前节点}
- **延迟**: {延迟ms}
- **流量**: {今日使用流量}
```

### 3. 自动化规则
```yaml
vpn_rules:
  # 需要VPN的场景
  require_vpn:
    - "访问国际网站"
    - "获取国外新闻"
    - "数字货币数据更新"
    - "AI模型访问"
    
  # 自动开启条件
  auto_start:
    - "时间: 工作日 09:00-18:00"
    - "任务: 需要访问国外资源"
    - "网络: 检测到网络限制"
    
  # 自动关闭条件  
  auto_stop:
    - "时间: 18:00后"
    - "电池: 电量低于20%"
    - "网络: 切换到移动网络"
```

## 故障排除

### 常见问题
1. **Clash无法启动**
   - 检查防火墙设置
   - 以管理员身份运行
   - 查看Windows事件查看器日志

2. **无法连接节点**
   - 更新订阅配置
   - 尝试其他节点
   - 检查本地网络设置

3. **系统代理不生效**
   - 检查Clash端口设置（默认7890）
   - 重启Clash服务
   - 手动设置代理

### 诊断命令
```powershell
# 检查Clash进程
Get-Process "Clash for Windows"

# 检查代理设置
netsh winhttp show proxy

# 测试端口
Test-NetConnection -ComputerName 127.0.0.1 -Port 7890

# 测试网络连接
curl -x http://127.0.0.1:7890 https://www.google.com
```

## 安全注意事项

### 1. 订阅安全
- 定期更新订阅地址
- 不要分享订阅链接
- 使用HTTPS连接

### 2. 数据安全
- Clash不记录浏览历史
- 使用加密传输
- 定期清理日志

### 3. 合规使用
- 遵守当地法律法规
- 仅用于合法用途
- 尊重网络使用政策

## 高级功能

### 1. 智能路由
配置规则实现智能分流：
```yaml
rules:
  - DOMAIN-SUFFIX,google.com,Proxy
  - DOMAIN-SUFFIX,github.com,Proxy
  - DOMAIN-SUFFIX,baidu.com,DIRECT
  - IP-CIDR,192.168.0.0/16,DIRECT
  - GEOIP,CN,DIRECT
  - MATCH,Proxy
```

### 2. 多配置切换
创建多个配置文件，根据需要切换：
- `work.yaml` - 工作模式（稳定节点）
- `speed.yaml` - 速度模式（低延迟）
- `privacy.yaml` - 隐私模式（高安全）

### 3. 监控和告警
```powershell
# 监控脚本
while ($true) {
    $status = Test-VPNConnection
    if (-not $status) {
        Send-Alert "VPN连接异常"
        Restart-Clash
    }
    Start-Sleep -Seconds 60
}
```

## 更新和维护

### 定期维护任务
1. **每周**：更新订阅配置
2. **每月**：更新Clash客户端
3. **每季度**：审查安全设置

### 备份配置
```powershell
# 备份脚本
$backupDir = "C:\Backup\Clash"
$configDir = "$env:USERPROFILE\.config\clash"
Copy-Item -Path "$configDir\*" -Destination $backupDir -Recurse
```

---

**配置完成**：按照上述步骤操作，即可实现Clash VPN的自动开启和管理。如有问题，请查看故障排除部分或联系技术支持。