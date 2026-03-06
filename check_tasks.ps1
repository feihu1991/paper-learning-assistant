$ErrorActionPreference = 'Stop'
try {
    $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/analysis/tasks/all' -TimeoutSec 10
    $response | ConvertTo-Json -Depth 10
} catch {
    Write-Error $_.Exception.Message
}
