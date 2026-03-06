# Start Clash and set proxy
$clashPath = "C:\Users\win\AppData\Local\Programs\Clash for Windows\Clash for Windows.exe"

# Start Clash
Start-Process $clashPath
Start-Sleep -Seconds 3

Write-Host "Clash started" -ForegroundColor Green
Write-Host "Proxy: 127.0.0.1:7890" -ForegroundColor Cyan
Write-Host "Note: System proxy needs admin rights to set automatically" -ForegroundColor Yellow
