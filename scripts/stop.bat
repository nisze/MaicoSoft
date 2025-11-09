@echo off
REM ===============================
REM MAICONSOFT - PARAR DOCKER
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Parar Aplicacao
echo ====================================
echo.

cd /d "%~dp0..\docker"

echo Parando containers...
docker-compose down

echo.
echo [OK] Containers parados!
echo.

pause
