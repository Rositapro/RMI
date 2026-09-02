# Script PowerShell para iniciar el Servidor RMI
$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "     INICIANDO SERVIDOR RMI CON ARCHIVO DE POLÍTICA     " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin/com/rmi/servidor/ServidorRMI.class")) {
    Write-Host "[!] Archivos compilados no encontrados. Compilando primero..." -ForegroundColor Yellow
    & .\compilar.ps1
}

Write-Host "[+] Ejecutando ServidorRMI con -Djava.security.policy=servidor.policy..." -ForegroundColor Green
java "-Djava.security.policy=servidor.policy" -cp bin com.rmi.servidor.ServidorRMI
