@echo off
echo ===============================
echo MAICONSOFT - VERIFICACAO DO SISTEMA
echo ===============================
echo.

echo [1/7] Verificando Java...
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java NAO encontrado!
    echo 📥 Baixe Java 17+ em: https://adoptium.net/
    set JAVA_OK=0
) else (
    echo ✅ Java encontrado
    java -version 2>&1 | findstr "version"
    set JAVA_OK=1
)
echo.

echo [2/7] Verificando Maven Wrapper...
if exist "mvnw.cmd" (
    echo ✅ Maven Wrapper encontrado
    set MAVEN_OK=1
) else (
    echo ❌ mvnw.cmd NAO encontrado!
    echo Certifique-se de estar na pasta maiconsoft_api
    set MAVEN_OK=0
)
echo.

echo [3/7] Verificando porta 8090...
netstat -ano | findstr :8090 >nul 2>&1
if errorlevel 1 (
    echo ✅ Porta 8090 livre
    set PORT_OK=1
) else (
    echo ⚠️  Porta 8090 em uso
    echo Processos usando a porta:
    netstat -ano | findstr :8090
    set PORT_OK=0
)
echo.

echo [4/7] Verificando conectividade Supabase...
ping -n 1 db.hmjldrzvmaqgetjcepay.supabase.co >nul 2>&1
if errorlevel 1 (
    echo ❌ Supabase inacessivel
    echo Verifique sua conexao de internet
    set SUPABASE_OK=0
) else (
    echo ✅ Supabase acessivel
    set SUPABASE_OK=1
)
echo.

echo [5/7] Verificando arquivos de configuracao...
if exist "src\main\resources\application.properties" (
    echo ✅ application.properties encontrado
    set CONFIG_OK=1
) else (
    echo ❌ application.properties NAO encontrado!
    set CONFIG_OK=0
)

if exist "src\main\resources\application-supabase.properties" (
    echo ✅ application-supabase.properties encontrado
) else (
    echo ❌ application-supabase.properties NAO encontrado!
    set CONFIG_OK=0
)
echo.

echo [6/7] Verificando estrutura do projeto...
if exist "src\main\java" (
    echo ✅ Pasta src/main/java encontrada
    set STRUCTURE_OK=1
) else (
    echo ❌ Estrutura do projeto incorreta!
    set STRUCTURE_OK=0
)
echo.

echo [7/7] Verificando dependencias...
if exist "pom.xml" (
    echo ✅ pom.xml encontrado
    set POM_OK=1
) else (
    echo ❌ pom.xml NAO encontrado!
    set POM_OK=0
)
echo.

echo ===============================
echo RESUMO DA VERIFICACAO
echo ===============================
if "%JAVA_OK%"=="1" (echo ✅ Java) else (echo ❌ Java)
if "%MAVEN_OK%"=="1" (echo ✅ Maven) else (echo ❌ Maven)
if "%PORT_OK%"=="1" (echo ✅ Porta 8090) else (echo ⚠️  Porta 8090)
if "%SUPABASE_OK%"=="1" (echo ✅ Supabase) else (echo ❌ Supabase)
if "%CONFIG_OK%"=="1" (echo ✅ Configuracao) else (echo ❌ Configuracao)
if "%STRUCTURE_OK%"=="1" (echo ✅ Estrutura) else (echo ❌ Estrutura)
if "%POM_OK%"=="1" (echo ✅ POM) else (echo ❌ POM)
echo.

if "%JAVA_OK%"=="1" if "%MAVEN_OK%"=="1" if "%CONFIG_OK%"=="1" if "%STRUCTURE_OK%"=="1" if "%POM_OK%"=="1" (
    echo 🎉 SISTEMA PRONTO PARA EXECUCAO!
    echo Execute: run-with-supabase.bat
) else (
    echo 🔧 CORRIJA OS PROBLEMAS ACIMA ANTES DE CONTINUAR
    echo.
    echo Solucoes rapidas:
    if "%JAVA_OK%"=="0" echo - Instale Java 17+: https://adoptium.net/
    if "%MAVEN_OK%"=="0" echo - Execute na pasta correta: maiconsoft_api/
    if "%CONFIG_OK%"=="0" echo - Verifique arquivos de configuracao
    if "%PORT_OK%"=="0" echo - Libere porta 8090: netstat -ano ^| findstr :8090
    if "%SUPABASE_OK%"=="0" echo - Verifique internet ou use modo offline
)
echo.
pause