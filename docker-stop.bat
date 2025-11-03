@echo off
echo =============================
echo MAICONSOFT - DOCKER STOP
echo =============================

echo 🛑 Parando containers do Maiconsoft...
docker-compose down

echo 📊 Status dos containers:
docker-compose ps

echo.
echo =============================
echo ✅ CONTAINERS PARADOS!
echo =============================
echo 💡 Para iniciar novamente: docker-start.bat
echo 🗑️ Para limpar volumes: docker-compose down -v
echo =============================

pause