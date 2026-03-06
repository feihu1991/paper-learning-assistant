$ErrorActionPreference = 'SilentlyContinue'
try {
    $req = [System.Net.HttpWebRequest]::Create("http://localhost:8080/")
    $req.Timeout = 5000
    $resp = $req.GetResponse()
    $stream = $resp.GetResponseStream()
    $reader = New-Object System.IO.StreamReader($stream)
    $content = $reader.ReadToEnd()
    $reader.Close()
    $resp.Close()
    Write-Host "Status: $($resp.StatusCode)"
    Write-Host "Content: $content"
} catch {
    Write-Host "Error: $($_.Exception.Message)"
}
