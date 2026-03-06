# Stop Clash
$process = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($process) {
    $process | Stop-Process -Force
    Write-Host "Clash stopped" -ForegroundColor Green
} else {
    Write-Host "Clash not running" -ForegroundColor Yellow
}
