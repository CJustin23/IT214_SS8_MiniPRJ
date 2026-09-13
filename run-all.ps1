$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

function Start-ServiceJar($module, $waitSeconds) {
    $jar = Get-ChildItem -Path "$root\$module\target" -Filter "$module-*.jar" |
        Where-Object { $_.Name -notlike "*-sources.jar" } |
        Select-Object -First 1
    if (-not $jar) {
        throw "Chua build jar cho $module. Chay: mvn clean package -DskipTests"
    }
    Write-Host "Starting $module ..."
    Start-Process -FilePath "java" -ArgumentList "-jar", $jar.FullName -WorkingDirectory $root
    if ($waitSeconds -gt 0) {
        Start-Sleep -Seconds $waitSeconds
    }
}

Write-Host "FreshMart — boot strap (Config -> Eureka -> services -> Gateway)"
Start-ServiceJar "config-server" 8
Start-ServiceJar "eureka-server" 12
Start-ServiceJar "user-service" 3
Start-ServiceJar "product-service" 3
Start-ServiceJar "order-service" 4
Start-ServiceJar "dashboard-service" 4
Start-ServiceJar "api-gateway" 6

Write-Host ""
Write-Host "UI:     http://localhost:8080"
Write-Host "Eureka: http://localhost:8761"
Write-Host "Config: http://localhost:8888/dashboard-service/default"
Write-Host "API:    http://localhost:8080/api/dashboard/overview"
