@echo off
echo ===============================
echo MAICONSOFT - INICIALIZACAO SUPABASE
echo ===============================
echo.

REM Verifica se o Maven wrapper existe
if not exist "mvnw.cmd" (
    echo [ERRO] mvnw.cmd nao encontrado! Execute este script na pasta maiconsoft_api
    pause
    exit /b 1
)

REM Verifica se Java esta instalado
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java nao encontrado! Instale Java 17 ou superior
    echo Download: https://adoptium.net/
    pause
    exit /b 1
)

echo [INFO] Configuracao do Supabase detectada:
echo   Host: db.hmjldrzvmaqgetjcepay.supabase.co
echo   Port: 5432
echo   Database: postgres
echo   User: postgres
echo   Password: *** (configurada automaticamente)
echo.

echo [INFO] Testando conectividade com Supabase...
ping -n 1 db.hmjldrzvmaqgetjcepay.supabase.co >nul 2>&1
if errorlevel 1 (
    echo [AVISO] Nao foi possivel conectar ao Supabase
    echo Verifique sua conexao de internet
    echo.
    echo Deseja continuar mesmo assim? [S/N]
    set /p choice=
    if /i "%choice%" neq "S" exit /b 1
)

echo [INFO] Iniciando aplicacao Spring Boot...
echo.
.\mvnw.cmd clean spring-boot:run -Dspring-boot.run.profiles=supabase

if errorlevel 1 (
    echo.
    echo [ERRO] Falha ao iniciar a aplicacao!
    echo.
    echo Possiveis causas:
    echo 1. Porta 8090 ja esta em uso
    echo 2. Problemas de conectividade com Supabase
    echo 3. Erro de compilacao
    echo.
    echo Para verificar a porta 8090:
    echo netstat -ano ^| findstr :8090
    echo.
    pause
    exit /b 1
)

echo.
echo ===============================
echo   APLICACAO INICIADA COM SUCESSO!
echo ===============================
echo.
echo 🌐 URLs da aplicacao:
echo   • API: http://localhost:8090
echo   • Swagger: http://localhost:8090/swagger-ui.html
echo   • H2 Console: http://localhost:8090/h2-console (se em modo dev)
echo.
echo 📋 Para testar a API:
echo   • POST /api/auth/login - Login
echo   • GET /api/clientes - Listar clientes
echo   • GET /api/dashboard/stats - Dashboard
echo.
echo ⏹️  Para parar: Ctrl+C
echo.
pause
echo   • API: http://localhost:8090
echo   • Swagger UI: http://localhost:8090/swagger-ui.html
echo   • API Docs: http://localhost:8090/v3/api-docs
echo.
echo 🔑 Sistema de usuários com código automático:
echo   • Códigos de acesso gerados automaticamente (6 caracteres)
echo   • Login case-insensitive (ABC123 = abc123)
echo   • Todos os endpoints CRUD disponíveis
echo   • CORS configurado para desenvolvimento (localhost/127.0.0.1)
echo.
echo 📋 Para testar a API:
echo   1. Acesse o Swagger UI no navegador
echo   2. Crie um usuário (POST /api/users) - código será gerado
echo   3. Use o código gerado para fazer login
echo   4. Teste os outros endpoints CRUD
echo.
echo Pressione qualquer tecla para finalizar...

pause