# Script PowerShell para compilar el proyecto RMI
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "      COMPILANDO PROYECTO JAVA RMI (CLIENTE-SERVIDOR)   " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[+] Compilando clases Java con javac hacia carpeta 'bin/'..." -ForegroundColor Yellow

$sources = Get-ChildItem -Path "src/main/java" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }

javac -encoding UTF-8 -d bin $sources

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n[OK] ¡Compilación exitosa!" -ForegroundColor Green
    Write-Host "Clases compiladas correctamente en la carpeta 'bin/'`n" -ForegroundColor Green
} else {
    Write-Host "`n[-] Error al compilar el proyecto." -ForegroundColor Red
}
