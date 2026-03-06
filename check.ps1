# Check Clash status

Write-Host "Checking Clash VPN status..." -ForegroundColor Cyan

# Check process
$process = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($process) {
    Write-Host "Clash is running (PID: $($process.Id))" -ForegroundColor Green
} else {
    Write-Host "Clash is not running" -ForegroundColor Red
}

# Check installation
$paths = @("C:\Program Files\Clash for Windows", "$env:LOCALAPPDATA\Programs\Clash for Windows")
$installed = $false

foreach ($path in $paths) {
    $exe = Join-Path $path "Clash for Windows.exe"
    if (Test-Path $exe) {
        Write-Host "Clash is installed: $exe" -ForegroundColor Green
        $installed = $true
        break
    }
}

if (-not $installed) {
    Write-Host "Clash is not installed" -ForegroundColor Red
    Write-Host "Please install first: https://github.com/Fndroid/clash_for_windows_pkg/releases" -ForegroundColor Yellow
}

# Check proxy
Write-Host "`nSystem proxy settings:" -ForegroundColor Cyan
netsh winhttp show proxy

Write-Host "`nRecommendations:" -ForegroundColor Magenta
if (-not $installed) {
    Write-Host "1. Install Clash for Windows" -ForegroundColor Yellow
} elseif (-not $process) {
    Write-Host "1. Start Clash" -ForegroundColor Yellow
    Write-Host "2. Run setup script for auto-start" -ForegroundColor Yellow
} else {
    Write-Host "1. Clash is running normally" -ForegroundColor Green
    Write-Host "2. You can configure scheduled tasks" -ForegroundColor Yellow
}