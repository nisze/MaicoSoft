@echo off
REM ===============================
REM MAICONSOFT - VER LOGS
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Logs dos Containers
echo ====================================
echo.
echo Pressione Ctrl+C para sair
echo.

cd /d "%~dp0..\docker"

docker-compose logs -f --tail=100
