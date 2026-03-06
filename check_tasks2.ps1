$ErrorActionPreference = 'Stop'
try {
    $webResp = Invoke-WebRequest -Uri 'http://localhost:8080/api/analysis/tasks/all' -TimeoutSec 15 -UseBasicParsing
    Write-Host "Status:" $webResp.StatusCode
    Write-Host "Content:" $webResp.Content
} catch {
    Write-Host "Error:" $_.Exception.Message
}
