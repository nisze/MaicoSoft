@echo off
REM ===============================
REM MAICONSOFT - RESET COMPLETO
REM ===============================

echo.
echo ====================================
echo  MAICONSOFT - Reset Completo
echo ====================================
echo.
echo ATENCAO: Isso ira:
echo  - Parar todos os containers
echo  - Remover volumes (APAGA DADOS DO BANCO!)
echo  - Limpar cache Docker
echo.

set /p confirm="Tem certeza? (S/N): "
if /i not "%confirm%"=="S" (
    echo.
    echo Operacao cancelada.
    pause
    exit /b 0
)

cd /d "%~dp0..\docker"

echo.
echo [1/3] Parando e removendo containers...
docker-compose down -v

echo.
echo [2/3] Limpando cache Docker...
docker system prune -f

echo.
echo [3/3] Rebuild completo...
docker-compose up -d --build

echo.
echo ====================================
echo  Reset completo!
echo ====================================
echo.
echo Os dados do banco foram resetados.
echo Usuarios de teste recriados:
echo  - ADM001 / 123456
echo  - W36K0D / 123456
echo.

pause
