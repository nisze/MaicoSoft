@echo off
REM ===============================
REM MAICONSOFT - STATUS
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Status dos Containers
echo ====================================
echo.

cd /d "%~dp0.."

docker-compose ps

echo.
echo ====================================
echo  Uso de Recursos
echo ====================================
echo.

docker stats --no-stream

echo.
pause
