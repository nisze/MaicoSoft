@echo off
REM ===============================
REM MAICONSOFT - INICIAR DOCKER
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Iniciar Aplicacao
echo ====================================
echo.

REM Verificar se Docker esta rodando
docker version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERRO] Docker nao esta rodando!
    echo.
    echo Inicie o Docker Desktop e tente novamente.
    echo.
    pause
    exit /b 1
)

echo [OK] Docker esta rodando
echo.

REM Ir para pasta docker
cd /d "%~dp0..\docker"

echo Iniciando containers...
echo.
echo Isso pode demorar 3-5 minutos na primeira execucao
echo (compilacao do backend Maven)
echo.

docker-compose up -d

echo.
echo ====================================
echo  Containers iniciados!
echo ====================================
echo.
echo Acesse:
echo  - Frontend: http://localhost:3000
echo  - Backend:  http://localhost:8090/api
echo  - Banco:    localhost:5432
echo.
echo Para ver logs:
echo   docker-compose logs -f
echo.
echo Para parar:
echo   docker-compose down
echo.

pause
