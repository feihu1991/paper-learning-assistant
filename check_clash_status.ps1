# 检查Clash安装状态

Write-Host "=== Clash VPN 状态检查 ===" -ForegroundColor Magenta

# 1. 检查Clash进程
Write-Host "`n[1] 检查Clash进程..." -ForegroundColor Green
$clashProcess = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($clashProcess) {
    Write-Host "✓ Clash 正在运行 (PID: $($clashProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "✗ Clash 未运行" -ForegroundColor Red
}

# 2. 检查Clash安装路径
Write-Host "`n[2] 检查Clash安装..." -ForegroundColor Green
$commonPaths = @(
    "C:\Program Files\Clash for Windows",
    "$env:LOCALAPPDATA\Programs\Clash for Windows",
    "$env:USERPROFILE\AppData\Local\Programs\Clash for Windows"
)

$found = $false
foreach ($path in $commonPaths) {
    if (Test-Path $path) {
        $clashExe = Join-Path $path "Clash for Windows.exe"
        if (Test-Path $clashExe) {
            Write-Host "✓ Clash 已安装: $clashExe" -ForegroundColor Green
            $found = $true
            break
        }
    }
}

if (-not $found) {
    Write-Host "✗ Clash 未安装" -ForegroundColor Red
    Write-Host "请先安装 Clash for Windows:" -ForegroundColor Yellow
    Write-Host "https://github.com/Fndroid/clash_for_windows_pkg/releases" -ForegroundColor Cyan
}

# 3. 检查系统代理设置
Write-Host "`n[3] 检查系统代理..." -ForegroundColor Green
$proxy = netsh winhttp show proxy
if ($proxy -match "Direct access") {
    Write-Host "✗ 系统代理未启用" -ForegroundColor Red
} else {
    Write-Host "✓ 系统代理已启用" -ForegroundColor Green
    Write-Host "代理设置: $proxy" -ForegroundColor Cyan
}

# 4. 测试网络连接
Write-Host "`n[4] 测试网络连接..." -ForegroundColor Green

# 测试本地连接
try {
    Test-NetConnection -ComputerName 127.0.0.1 -Port 7890 -ErrorAction Stop | Out-Null
    Write-Host "✓ Clash 端口 (7890) 可访问" -ForegroundColor Green
} catch {
    Write-Host "✗ Clash 端口不可访问" -ForegroundColor Red
}

# 测试网络连接
Write-Host "测试中..." -ForegroundColor Cyan

# 5. 检查配置文件
Write-Host "`n[5] 检查配置文件..." -ForegroundColor Green
$configDir = "$env:USERPROFILE\.config\clash"
$configFile = Join-Path $configDir "config.yaml"

if (Test-Path $configFile) {
    Write-Host "✓ 配置文件存在: $configFile" -ForegroundColor Green
    $fileSize = (Get-Item $configFile).Length
    Write-Host "文件大小: $fileSize 字节" -ForegroundColor Cyan
} else {
    Write-Host "✗ 配置文件不存在" -ForegroundColor Red
}

# 总结
Write-Host "`n=== 检查完成 ===" -ForegroundColor Magenta
Write-Host "`n建议操作:" -ForegroundColor Cyan

if (-not $found) {
    Write-Host "1. 安装 Clash for Windows" -ForegroundColor Yellow
}

if (-not $clashProcess) {
    Write-Host "2. 启动 Clash" -ForegroundColor Yellow
}

Write-Host "3. 运行安装脚本: .\clash_simple_setup.ps1" -ForegroundColor Yellow
Write-Host "4. 查看详细文档: dev-team\shared\docs\clash_vpn_setup.md" -ForegroundColor Yellow

Write-Host "`n按任意键退出..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")