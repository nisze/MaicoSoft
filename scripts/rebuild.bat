@echo off
REM ===============================
REM MAICONSOFT - REBUILD DOCKER
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Rebuild Containers
echo ====================================
echo.
echo Isso ira recompilar o backend e frontend
echo.

cd /d "%~dp0..\docker"

echo Parando containers...
docker-compose down

echo.
echo Rebuilding e iniciando...
docker-compose up -d --build

echo.
echo [OK] Rebuild completo!
echo.
echo Acesse:
echo  - Frontend: http://localhost:3000
echo  - Backend:  http://localhost:8090/api
echo.

pause
