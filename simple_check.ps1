# 简单检查Clash状态

Write-Host "检查Clash状态..." -ForegroundColor Cyan

# 检查进程
$process = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "Clash正在运行 (PID: $($process.Id))" -ForegroundColor Green
} else {
    Write-Host "Clash未运行" -ForegroundColor Red
}

# 检查安装
$paths = @("C:\Program Files\Clash for Windows", "$env:LOCALAPPDATA\Programs\Clash for Windows")
$installed = $false

foreach ($path in $paths) {
    $exe = Join-Path $path "Clash for Windows.exe"
    if (Test-Path $exe) {
        Write-Host "Clash已安装: $exe" -ForegroundColor Green
        $installed = $true
        break
    }
}

if (-not $installed) {
    Write-Host "Clash未安装" -ForegroundColor Red
    Write-Host "请先安装: https://github.com/Fndroid/clash_for_windows_pkg/releases" -ForegroundColor Yellow
}

# 检查代理
$proxy = netsh winhttp show proxy
Write-Host "系统代理设置:" -ForegroundColor Cyan
Write-Host $proxy -ForegroundColor Gray

Write-Host "`n建议:" -ForegroundColor Magenta
if (-not $installed) {
    Write-Host "1. 安装Clash for Windows" -ForegroundColor Yellow
} elseif (-not $process) {
    Write-Host "1. 启动Clash" -ForegroundColor Yellow
    Write-Host "2. 运行安装脚本配置自动启动" -ForegroundColor Yellow
} else {
    Write-Host "1. Clash运行正常" -ForegroundColor Green
    Write-Host "2. 可配置定时任务" -ForegroundColor Yellow
}