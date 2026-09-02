@echo off
chcp 65001 > nul
echo ========================================================
echo       COMPILANDO PROYECTO JAVA RMI (CLIENTE-SERVIDOR)
echo ========================================================

if not exist bin (
    mkdir bin
)

echo [+] Compilando clases Java con javac hacia carpeta 'bin/'...
javac -encoding UTF-8 -d bin src/main/java/com/rmi/dto/*.java src/main/java/com/rmi/interfaz/*.java src/main/java/com/rmi/servidor/*.java src/main/java/com/rmi/cliente/*.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo [OK] ¡Compilación exitosa!
    echo Las clases compiladas se encuentran en la carpeta 'bin/'
) else (
    echo.
    echo [-] Error durante la compilación. Verifica la salida anterior.
)
pause
