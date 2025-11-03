#!/bin/bash
echo "==============================="
echo "MAICONSOFT - INICIALIZACAO SUPABASE"
echo "==============================="
echo

# Verifica se o Maven wrapper existe
if [ ! -f "mvnw" ]; then
    echo "[ERRO] mvnw nao encontrado! Execute este script na pasta maiconsoft_api"
    exit 1
fi

# Verifica se Java esta instalado
if ! command -v java &> /dev/null; then
    echo "[ERRO] Java nao encontrado! Instale Java 17 ou superior"
    echo "Download: https://adoptium.net/"
    exit 1
fi

echo "[INFO] Configuracao do Supabase detectada:"
echo "  Host: db.hmjldrzvmaqgetjcepay.supabase.co"
echo "  Port: 5432" 
echo "  Database: postgres"
echo "  User: postgres"
echo "  Password: *** (configurada automaticamente)"
echo

echo "[INFO] Testando conectividade com Supabase..."
if ! ping -c 1 db.hmjldrzvmaqgetjcepay.supabase.co &> /dev/null; then
    echo "[AVISO] Nao foi possivel conectar ao Supabase"
    echo "Verifique sua conexao de internet"
    echo
    read -p "Deseja continuar mesmo assim? [S/N]: " choice
    if [[ ! "$choice" =~ ^[Ss]$ ]]; then
        exit 1
    fi
fi

echo "[INFO] Iniciando aplicacao Spring Boot..."
echo
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=supabase

if [ $? -ne 0 ]; then
    echo
    echo "[ERRO] Falha ao iniciar a aplicacao!"
    echo
    echo "Possiveis causas:"
    echo "1. Porta 8090 ja esta em uso"
    echo "2. Problemas de conectividade com Supabase" 
    echo "3. Erro de compilacao"
    echo
    echo "Para verificar a porta 8090:"
    echo "lsof -i :8090"
    echo
    exit 1
fi

echo
echo "==============================="
echo "  APLICACAO INICIADA COM SUCESSO!"
echo "==============================="
echo
echo "🌐 URLs da aplicacao:"
echo "  • API: http://localhost:8090"
echo "  • Swagger: http://localhost:8090/swagger-ui.html"
echo "  • H2 Console: http://localhost:8090/h2-console (se em modo dev)"
echo
echo "📋 Para testar a API:"
echo "  • POST /api/auth/login - Login"
echo "  • GET /api/clientes - Listar clientes"
echo "  • GET /api/dashboard/stats - Dashboard"
echo
echo "⏹️  Para parar: Ctrl+C"
echo