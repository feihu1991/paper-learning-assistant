# Clash VPN 简易安装和配置脚本
# 适用于 Windows 系统

Write-Host "=== Clash VPN 自动配置脚本 ===" -ForegroundColor Magenta
Write-Host "订阅地址: https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df" -ForegroundColor Cyan

# 检查是否以管理员身份运行
function Test-Admin {
    $currentUser = New-Object Security.Principal.WindowsPrincipal([Security.Principal.WindowsIdentity]::GetCurrent())
    return $currentUser.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)
}

if (-not (Test-Admin)) {
    Write-Host "请以管理员身份运行此脚本！" -ForegroundColor Red
    Write-Host "右键点击 PowerShell -> 以管理员身份运行" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤1: 检查Clash是否已安装
Write-Host "`n[步骤1] 检查Clash安装状态..." -ForegroundColor Green

$clashInstalled = $false
$clashPaths = @(
    "C:\Program Files\Clash for Windows",
    "$env:LOCALAPPDATA\Programs\Clash for Windows",
    "$env:USERPROFILE\AppData\Local\Programs\Clash for Windows"
)

foreach ($path in $clashPaths) {
    if (Test-Path $path) {
        $clashInstalled = $true
        $clashExe = Join-Path $path "Clash for Windows.exe"
        Write-Host "✓ 发现Clash安装: $clashExe" -ForegroundColor Green
        break
    }
}

if (-not $clashInstalled) {
    Write-Host "✗ Clash未安装，请先安装Clash for Windows" -ForegroundColor Red
    Write-Host "下载地址: https://github.com/Fndroid/clash_for_windows_pkg/releases" -ForegroundColor Yellow
    Write-Host "安装后重新运行此脚本" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤2: 创建配置文件目录
Write-Host "`n[步骤2] 创建配置文件..." -ForegroundColor Green

$configDir = "$env:USERPROFILE\.config\clash"
if (-not (Test-Path $configDir)) {
    New-Item -ItemType Directory -Path $configDir -Force | Out-Null
    Write-Host "✓ 创建配置目录: $configDir" -ForegroundColor Green
}

# 步骤3: 下载订阅配置
Write-Host "`n[步骤3] 下载订阅配置..." -ForegroundColor Green

$subscriptionUrl = "https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df"
$configFile = Join-Path $configDir "config.yaml"

try {
    Invoke-WebRequest -Uri $subscriptionUrl -OutFile $configFile -UseBasicParsing
    Write-Host "✓ 配置文件下载成功: $configFile" -ForegroundColor Green
} catch {
    Write-Host "✗ 配置文件下载失败: $_" -ForegroundColor Red
    Write-Host "请检查网络连接和订阅地址" -ForegroundColor Yellow
    pause
    exit 1
}

# 步骤4: 创建启动脚本
Write-Host "`n[步骤4] 创建启动脚本..." -ForegroundColor Green

$startScript = @'
# clash_start.ps1 - Clash VPN 启动脚本

# 启动Clash
Start-Process -FilePath "C:\Program Files\Clash for Windows\Clash for Windows.exe"

# 等待Clash启动
Start-Sleep -Seconds 5

# 设置系统代理
Write-Host "正在设置系统代理..." -ForegroundColor Cyan
netsh winhttp set proxy proxy-server="http=127.0.0.1:7890;https=127.0.0.1:7890" bypass-list="localhost;127.*;10.*;172.16.*;172.17.*;172.18.*;172.19.*;172.20.*;172.21.*;172.22.*;172.23.*;172.24.*;172.25.*;172.26.*;172.27.*;172.28.*;172.29.*;172.30.*;172.31.*;192.168.*"

Write-Host "✓ Clash VPN 已启动，代理已设置" -ForegroundColor Green
Write-Host "代理地址: 127.0.0.1:7890" -ForegroundColor Cyan
'@

$stopScript = @'
# clash_stop.ps1 - Clash VPN 停止脚本

# 关闭系统代理
Write-Host "正在关闭系统代理..." -ForegroundColor Cyan
netsh winhttp reset proxy

# 停止Clash进程
$clashProcess = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($clashProcess) {
    $clashProcess | Stop-Process -Force
    Write-Host "✓ Clash 已停止" -ForegroundColor Green
} else {
    Write-Host "Clash 未在运行" -ForegroundColor Yellow
}
'@

$startScriptPath = "clash_start.ps1"
$stopScriptPath = "clash_stop.ps1"

$startScript | Out-File -FilePath $startScriptPath -Encoding UTF8
$stopScript | Out-File -FilePath $stopScriptPath -Encoding UTF8

Write-Host "✓ 启动脚本创建: $startScriptPath" -ForegroundColor Green
Write-Host "✓ 停止脚本创建: $stopScriptPath" -ForegroundColor Green

# 步骤5: 创建桌面快捷方式
Write-Host "`n[步骤5] 创建桌面快捷方式..." -ForegroundColor Green

$desktopPath = [Environment]::GetFolderPath("Desktop")
$shortcutStart = Join-Path $desktopPath "启动Clash VPN.lnk"
$shortcutStop = Join-Path $desktopPath "停止Clash VPN.lnk"

# 创建启动快捷方式
$WScriptShell = New-Object -ComObject WScript.Shell
$shortcut = $WScriptShell.CreateShortcut($shortcutStart)
$shortcut.TargetPath = "powershell.exe"
$shortcut.Arguments = "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$PWD\clash_start.ps1`""
$shortcut.WorkingDirectory = $PWD
$shortcut.IconLocation = "$clashExe,0"
$shortcut.Save()

# 创建停止快捷方式
$shortcut = $WScriptShell.CreateShortcut($shortcutStop)
$shortcut.TargetPath = "powershell.exe"
$shortcut.Arguments = "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$PWD\clash_stop.ps1`""
$shortcut.WorkingDirectory = $PWD
$shortcut.IconLocation = "$clashExe,0"
$shortcut.Save()

Write-Host "✓ 桌面快捷方式已创建" -ForegroundColor Green

# 步骤6: 创建定时任务
Write-Host "`n[步骤6] 创建定时任务（可选）..." -ForegroundColor Green

$createTask = Read-Host "是否创建定时任务？(y/n)"
if ($createTask -eq 'y') {
    $taskName = "AutoStartClashVPN"
    
    # 删除已存在的任务
    schtasks /delete /tn $taskName /f 2>$null
    
    # 创建新任务（登录时启动）
    $action = New-ScheduledTaskAction -Execute "powershell.exe" -Argument "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$PWD\clash_start.ps1`""
    $trigger = New-ScheduledTaskTrigger -AtLogOn
    $principal = New-ScheduledTaskPrincipal -UserId "$env:USERDOMAIN\$env:USERNAME" -LogonType Interactive -RunLevel Highest
    $settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -StartWhenAvailable
    
    Register-ScheduledTask -TaskName $taskName -Action $action -Trigger $trigger -Principal $principal -Settings $settings -Force | Out-Null
    
    Write-Host "✓ 定时任务已创建: $taskName" -ForegroundColor Green
    Write-Host "  触发条件: 用户登录时" -ForegroundColor Cyan
}

# 步骤7: 测试连接
Write-Host "`n[步骤7] 测试VPN连接..." -ForegroundColor Green

$testConnection = Read-Host "是否现在测试VPN连接？(y/n)"
if ($testConnection -eq 'y') {
    Write-Host "正在启动Clash..." -ForegroundColor Cyan
    & "powershell.exe" -ExecutionPolicy Bypass -WindowStyle Hidden -File $startScriptPath
    
    Start-Sleep -Seconds 10
    
    Write-Host "测试Google连接..." -ForegroundColor Cyan
    try {
        $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 10 -UseBasicParsing -Proxy "http://127.0.0.1:7890" -ErrorAction Stop
        Write-Host "✓ VPN连接测试成功！" -ForegroundColor Green
    } catch {
        Write-Host "✗ VPN连接测试失败: $_" -ForegroundColor Red
        Write-Host "请检查Clash是否正常运行" -ForegroundColor Yellow
    }
}

# 完成
Write-Host "`n=== 配置完成 ===" -ForegroundColor Magenta
Write-Host "`n使用说明:" -ForegroundColor Cyan
Write-Host "1. 双击桌面上的 '启动Clash VPN' 快捷方式启动VPN" -ForegroundColor Yellow
Write-Host "2. 双击桌面上的 '停止Clash VPN' 快捷方式停止VPN" -ForegroundColor Yellow
Write-Host "3. 代理地址: 127.0.0.1:7890" -ForegroundColor Yellow
Write-Host "4. 配置文件: $configFile" -ForegroundColor Yellow
Write-Host "`n高级配置:" -ForegroundColor Cyan
Write-Host "- 编辑 $configFile 修改节点和规则" -ForegroundColor Yellow
Write-Host "- 运行 clash_start.ps1 和 clash_stop.ps1 脚本控制VPN" -ForegroundColor Yellow
Write-Host "- 查看详细文档: dev-team/shared/docs/clash_vpn_setup.md" -ForegroundColor Yellow

Write-Host "`n按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")