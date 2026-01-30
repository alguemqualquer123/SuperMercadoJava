@echo off
setlocal EnableExtensions EnableDelayedExpansion
title SuperMercado PDV

echo Iniciando SuperMercado PDV...
call mvn -q javafx:run
call mvn -q exec:java -Dexec.mainClass=com.supermercado.SuperMercadoPDV
pause
REM ================================================
REM SuperMercado PDV - Script de Inicialização
REM ================================================

echo.
echo ========================================
echo   SuperMercado PDV - Sistema de Vendas
echo ========================================
echo.

REM Verifica Java
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java nao encontrado!
    echo Instale o JDK 17 ou 21 LTS.
    pause
    exit /b 1
)

REM Verifica Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven nao encontrado!
    pause
    exit /b 1
)

echo [OK] Java instalado
echo [OK] Maven instalado
echo.

:menu
echo Escolha uma opcao:
echo 1 - Compilar o projeto
echo 2 - Executar o sistema
echo 3 - Compilar e Executar
echo 4 - Limpar e Compilar
echo 5 - Sair
echo.

set /p opcao=Digite a opcao: 

if "%opcao%"=="1" goto compilar
if "%opcao%"=="2" goto executar
if "%opcao%"=="3" goto compilar_executar
if "%opcao%"=="4" goto limpar_compilar
if "%opcao%"=="5" exit /b 0

echo Opcao invalida!
pause
cls
goto menu

:compilar
call mvn install -DskipTests
pause
exit /b

:executar
call mvn javafx:run
pause
exit /b

:compilar_executar
call mvn clean install -DskipTests
call mvn javafx:run
pause
exit /b

:limpar_compilar
call mvn clean install
pause
exit /b
