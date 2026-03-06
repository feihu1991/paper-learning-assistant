$ErrorActionPreference = 'SilentlyContinue'
$uri = 'http://localhost:8080/api/analysis/tasks/all'
try {
    $sw = [Diagnostics.Stopwatch]::StartNew()
    $client = New-Object System.Net.WebClient
    $client.Timeout = 8000
    $content = $client.DownloadString($uri)
    $sw.Stop()
    Write-Host "Time: $($sw.ElapsedMilliseconds)ms"
    Write-Host "Content: $content"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
} finally {
    if ($client) { $client.Dispose() }
}
