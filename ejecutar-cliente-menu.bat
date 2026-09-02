@echo off
chcp 65001 > nul
echo ========================================================
echo     INICIANDO CLIENTE RMI INTERACTIVO (MODO MENÚ)
echo ========================================================

if not exist "bin\com\rmi\cliente\ClienteRMI.class" (
    echo [-] No se encontraron los archivos compilados en 'bin/'.
    echo [+] Compilando automáticamente primero...
    call compilar.bat
)

echo [+] Conectando con el servidor en modo interactivo...
echo.
java -Djava.security.policy=cliente.policy -cp bin com.rmi.cliente.ClienteRMI localhost 1099 --menu

pause
