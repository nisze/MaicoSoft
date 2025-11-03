#!/bin/bash

echo "============================="
echo "MAICONSOFT - DOCKER START"
echo "============================="

echo "🔍 Verificando se Docker está rodando..."
if ! docker version >/dev/null 2>&1; then
    echo "❌ Docker não está rodando! Por favor, inicie o Docker."
    exit 1
fi

echo "✅ Docker está rodando!"

echo "📋 Verificando arquivo .env..."
if [ ! -f .env ]; then
    echo "⚠️ Arquivo .env não encontrado!"
    echo "📄 Copiando .env.example para .env..."
    cp .env.example .env
    echo "⚙️ Configure as variáveis no arquivo .env antes de continuar."
    echo "💡 Edite o arquivo .env e execute este script novamente."
    exit 1
fi

echo "✅ Arquivo .env encontrado!"

echo "🧹 Parando containers existentes (se houver)..."
docker-compose down

echo "🏗️ Construindo e iniciando containers..."
docker-compose up --build -d

echo "📊 Status dos containers:"
docker-compose ps

echo ""
echo "============================="
echo "🚀 MAICONSOFT INICIADO!"
echo "============================="
echo "🌐 Frontend: http://localhost"
echo "📡 Backend API: http://localhost:8090"
echo "📖 Swagger UI: http://localhost:8090/swagger-ui.html"
echo ""
echo "💡 Para parar: docker-compose down"
echo "📋 Ver logs: docker-compose logs -f"
echo "============================="