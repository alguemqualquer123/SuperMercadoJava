@echo off
setlocal EnableExtensions EnableDelayedExpansion
title SuperMercado PDV - Build System

echo.
echo ========================================
echo   SuperMercado PDV - Gerador de Instalador
echo ========================================
echo.

REM 1. Limpar e Compilar o Projeto
echo [1/3] Limpando e instalando dependencias (Maven)...
call mvn clean install -DskipTests
if errorlevel 1 (
    echo [ERRO] Falha ao compilar ou baixar dependencias!
    pause
    exit /b 1
)

REM 2. Verificar WiX Toolset para o jpackage
echo [2/3] Verificando pre-requisitos (WiX Toolset)...
candle -version >nul 2>&1
if errorlevel 1 (
    echo.
    echo [AVISO] WiX Toolset nao encontrado no seu PATH do Windows.
    echo O instalador instalavel (.exe) nao pode ser gerado sem o WiX.
    echo Gerando apenas a "Imagem de App" (Pasta portavel com .exe)...
    echo.
    set BUILD_TYPE=app-image
) else (
    set BUILD_TYPE=exe
)

REM 3. Gerar o Instalador/Executavel usando jpackage
echo [3/3] Gerando pacote final (%BUILD_TYPE%)...
if not exist "dist" mkdir dist

jpackage --input target/ ^
         --name "SuperMercadoPDV" ^
         --main-jar supermercado-pdv-1.0.0.jar ^
         --type %BUILD_TYPE% ^
         --win-shortcut ^
         --win-menu ^
         --vendor "SuperMercado" ^
         --description "Sistema de PDV Profissional" ^
         --app-version "1.0.0" ^
         --dest dist/

if errorlevel 1 (
    echo [ERRO] Falha ao executar jpackage! 
    echo Verifique se o JAR existe em target/ e se voce usa JDK 17+.
    pause
    exit /b 1
)

echo.
echo ========================================
echo   SUCESSO! O resultado esta na pasta: dist/
echo ========================================
echo.
if "%BUILD_TYPE%"=="exe" (
    echo [INFO] Voce gerou um instalador oficial.
) else (
    echo [INFO] Voce gerou uma pasta portavel. Basta zipar e enviar ao cliente.
)
echo.
pause
