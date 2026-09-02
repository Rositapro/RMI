# Script PowerShell para iniciar el Cliente RMI
param (
    [string]$HostServidor = "localhost",
    [int]$Puerto = 1099,
    [switch]$Menu
)

$OutputEncoding = [System.Text.Encoding]::UTF8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "     INICIANDO CLIENTE RMI CON ARCHIVO DE POLÍTICA      " -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin/com/rmi/cliente/ClienteRMI.class")) {
    Write-Host "[!] Archivos compilados no encontrados. Compilando primero..." -ForegroundColor Yellow
    & .\compilar.ps1
}

$argumentos = @("-Djava.security.policy=cliente.policy", "-cp", "bin", "com.rmi.cliente.ClienteRMI", $HostServidor, $Puerto)
if ($Menu) {
    $argumentos += "--menu"
}

Write-Host "[+] Ejecutando ClienteRMI con argumentos: $HostServidor $Puerto" -ForegroundColor Green
java $argumentos
