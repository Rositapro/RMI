@echo off
chcp 65001 > nul
echo ========================================================
echo       INICIANDO SERVIDOR RMI CON ARCHIVO DE POLÍTICA
echo ========================================================

if not exist "bin\com\rmi\servidor\ServidorRMI.class" (
    echo [-] No se encontraron los archivos compilados en 'bin/'.
    echo [+] Compilando automáticamente primero...
    call compilar.bat
)

echo [+] Ejecutando ServidorRMI pasando la propiedad -Djava.security.policy=servidor.policy...
echo.
java -Djava.security.policy=servidor.policy -cp bin com.rmi.servidor.ServidorRMI

pause
