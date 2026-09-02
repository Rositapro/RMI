@echo off
chcp 65001 > nul
echo ========================================================
echo       INICIANDO CLIENTE RMI CON ARCHIVO DE POLÍTICA
echo ========================================================

if not exist "bin\com\rmi\cliente\ClienteRMI.class" (
    echo [-] No se encontraron los archivos compilados en 'bin/'.
    echo [+] Compilando automáticamente primero...
    call compilar.bat
)

echo [+] Ejecutando ClienteRMI pasando la propiedad -Djava.security.policy=cliente.policy...
echo.
java -Djava.security.policy=cliente.policy -cp bin com.rmi.cliente.ClienteRMI %*

pause
