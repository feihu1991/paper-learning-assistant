# Setup Clash without admin rights

Write-Host "=== Clash VPN Setup (No Admin) ===" -ForegroundColor Magenta

# 1. Start Clash
Write-Host "`n[1] Starting Clash..." -ForegroundColor Green
$clashPath = "C:\Users\win\AppData\Local\Programs\Clash for Windows\Clash for Windows.exe"

if (Test-Path $clashPath) {
    Start-Process $clashPath
    Write-Host "Clash started successfully" -ForegroundColor Green
} else {
    Write-Host "Clash not found at: $clashPath" -ForegroundColor Red
    exit 1
}

# Wait for Clash to start
Write-Host "Waiting for Clash to start..." -ForegroundColor Cyan
Start-Sleep -Seconds 5

# 2. Download config
Write-Host "`n[2] Downloading config..." -ForegroundColor Green
$configUrl = "https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df"
$configDir = "$env:USERPROFILE\.config\clash"
$configFile = Join-Path $configDir "config.yaml"

# Create config directory
if (-not (Test-Path $configDir)) {
    New-Item -ItemType Directory -Path $configDir -Force | Out-Null
}

try {
    Invoke-WebRequest -Uri $configUrl -OutFile $configFile -UseBasicParsing
    Write-Host "Config downloaded: $configFile" -ForegroundColor Green
} catch {
    Write-Host "Failed to download config: $_" -ForegroundColor Red
}

# 3. Create control scripts
Write-Host "`n[3] Creating control scripts..." -ForegroundColor Green

# Start script
$startScript = @'
# Start Clash and set proxy
$clashPath = "C:\Users\win\AppData\Local\Programs\Clash for Windows\Clash for Windows.exe"

# Start Clash
Start-Process $clashPath
Start-Sleep -Seconds 3

Write-Host "Clash started" -ForegroundColor Green
Write-Host "Proxy: 127.0.0.1:7890" -ForegroundColor Cyan
Write-Host "Note: System proxy needs admin rights to set automatically" -ForegroundColor Yellow
'@

$startScript | Out-File -FilePath "start_clash.ps1" -Encoding UTF8
Write-Host "Created: start_clash.ps1" -ForegroundColor Green

# Stop script
$stopScript = @'
# Stop Clash
$process = Get-Process "Clash for Windows" -ErrorAction SilentlyContinue
if ($process) {
    $process | Stop-Process -Force
    Write-Host "Clash stopped" -ForegroundColor Green
} else {
    Write-Host "Clash not running" -ForegroundColor Yellow
}
'@

$stopScript | Out-File -FilePath "stop_clash.ps1" -Encoding UTF8
Write-Host "Created: stop_clash.ps1" -ForegroundColor Green

# 4. Create shortcuts
Write-Host "`n[4] Creating shortcuts..." -ForegroundColor Green

$desktop = [Environment]::GetFolderPath("Desktop")
$wsh = New-Object -ComObject WScript.Shell

# Start shortcut
$startShortcut = Join-Path $desktop "Start Clash.lnk"
$shortcut = $wsh.CreateShortcut($startShortcut)
$shortcut.TargetPath = "powershell.exe"
$shortcut.Arguments = "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$PWD\start_clash.ps1`""
$shortcut.WorkingDirectory = $PWD
$shortcut.IconLocation = "$clashPath,0"
$shortcut.Save()
Write-Host "Created: Start Clash.lnk on desktop" -ForegroundColor Green

# Stop shortcut
$stopShortcut = Join-Path $desktop "Stop Clash.lnk"
$shortcut = $wsh.CreateShortcut($stopShortcut)
$shortcut.TargetPath = "powershell.exe"
$shortcut.Arguments = "-ExecutionPolicy Bypass -WindowStyle Hidden -File `"$PWD\stop_clash.ps1`""
$shortcut.WorkingDirectory = $PWD
$shortcut.IconLocation = "$clashPath,0"
$shortcut.Save()
Write-Host "Created: Stop Clash.lnk on desktop" -ForegroundColor Green

# 5. Manual setup instructions
Write-Host "`n[5] Manual setup required:" -ForegroundColor Yellow
Write-Host "`nTo set system proxy (requires admin):" -ForegroundColor Cyan
Write-Host "1. Open Clash for Windows" -ForegroundColor White
Write-Host "2. Go to 'General' tab" -ForegroundColor White
Write-Host "3. Turn ON 'System Proxy'" -ForegroundColor White
Write-Host "4. (Optional) Turn ON 'Start with Windows'" -ForegroundColor White

Write-Host "`nTo import config in Clash:" -ForegroundColor Cyan
Write-Host "1. Open Clash for Windows" -ForegroundColor White
Write-Host "2. Go to 'Profiles' tab" -ForegroundColor White
Write-Host "3. Paste URL: https://dash.pqjc.site/api/v1/pq/746a2d817d2cffce820bf2966a0270df" -ForegroundColor White
Write-Host "4. Click 'Download'" -ForegroundColor White
Write-Host "5. Click the profile name to activate" -ForegroundColor White

# 6. Test connection
Write-Host "`n[6] Testing connection..." -ForegroundColor Green
Start-Sleep -Seconds 10

try {
    $response = Invoke-WebRequest -Uri "https://www.google.com" -TimeoutSec 10 -UseBasicParsing -Proxy "http://127.0.0.1:7890" -ErrorAction Stop
    Write-Host "✓ VPN connection test successful!" -ForegroundColor Green
} catch {
    Write-Host "✗ VPN connection test failed" -ForegroundColor Red
    Write-Host "Make sure Clash is running and proxy is set" -ForegroundColor Yellow
}

Write-Host "`n=== Setup Complete ===" -ForegroundColor Magenta
Write-Host "`nWhat to do next:" -ForegroundColor Cyan
Write-Host "1. Double-click 'Start Clash' on desktop to start VPN" -ForegroundColor White
Write-Host "2. Double-click 'Stop Clash' on desktop to stop VPN" -ForegroundColor White
Write-Host "3. Open Clash to configure system proxy (requires admin)" -ForegroundColor White
Write-Host "4. Check detailed guide: dev-team\shared\docs\clash_vpn_setup.md" -ForegroundColor White

Write-Host "`nPress any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")