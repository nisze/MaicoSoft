#!/bin/bash
echo "==============================="
echo "MAICONSOFT - MODO OFFLINE (H2)"
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

echo "[INFO] Modo OFFLINE ativado - usando H2 Database"
echo "  • Banco: H2 (em memoria)"
echo "  • Dados: Temporarios (perdidos ao reiniciar)"
echo "  • Internet: NAO necessaria"
echo "  • Console H2: http://localhost:8090/h2-console"
echo

echo "[INFO] Iniciando aplicacao Spring Boot..."
echo
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=dev

if [ $? -ne 0 ]; then
    echo
    echo "[ERRO] Falha ao iniciar a aplicacao!"
    echo
    echo "Possiveis causas:"
    echo "1. Porta 8090 ja esta em uso"
    echo "2. Erro de compilacao"
    echo "3. Falta de dependencias"
    echo
    echo "Para verificar a porta 8090:"
    echo "lsof -i :8090"
    echo
    exit 1
fi

echo
echo "==============================="
echo "  APLICACAO INICIADA - MODO OFFLINE!"
echo "==============================="
echo
echo "🌐 URLs da aplicacao:"
echo "  • API: http://localhost:8090"
echo "  • Swagger: http://localhost:8090/swagger-ui.html"
echo "  • H2 Console: http://localhost:8090/h2-console"
echo
echo "🗄️  Credenciais H2 Console:"
echo "  • JDBC URL: jdbc:h2:mem:maiconsoft_dev"
echo "  • User: sa"
echo "  • Password: (vazio)"
echo
echo "📋 Para testar a API:"
echo "  • POST /api/auth/login - Login"
echo "  • GET /api/clientes - Listar clientes"
echo "  • GET /api/dashboard/stats - Dashboard"
echo
echo "⚠️  ATENCAO: Dados serao perdidos ao reiniciar!"
echo "⏹️  Para parar: Ctrl+C"
echo