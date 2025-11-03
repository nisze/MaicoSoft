#!/bin/sh

# ===============================
# MAICONSOFT FRONTEND ENTRYPOINT
# ===============================

echo "🚀 Iniciando Maiconsoft Frontend..."

# Definir variáveis padrão se não existirem
API_BASE_URL=${API_BASE_URL:-"http://localhost:8090/api"}
ENVIRONMENT=${ENVIRONMENT:-"production"}

echo "📡 Configurando API_BASE_URL: $API_BASE_URL"
echo "🌍 Ambiente: $ENVIRONMENT"

# Substituir configurações no config.js
CONFIG_FILE="/usr/share/nginx/html/js/config.js"

if [ -f "$CONFIG_FILE" ]; then
    echo "⚙️ Atualizando configurações..."
    
    # Backup do arquivo original
    cp "$CONFIG_FILE" "$CONFIG_FILE.bak"
    
    # Substituir URL da API
    sed -i "s|API_BASE_URL: 'http://localhost:8090/api'|API_BASE_URL: '$API_BASE_URL'|g" "$CONFIG_FILE"
    sed -i "s|http://localhost:8090/api|$API_BASE_URL|g" "$CONFIG_FILE"
    
    # Configurar ambiente para produção
    if [ "$ENVIRONMENT" = "production" ]; then
        sed -i "s|window.FORCE_DEVELOPMENT = true|window.FORCE_DEVELOPMENT = false|g" "$CONFIG_FILE"
        sed -i "s|const CURRENT_ENV = 'development'|const CURRENT_ENV = 'production'|g" "$CONFIG_FILE"
        sed -i "s|DEBUG: true|DEBUG: false|g" "$CONFIG_FILE"
    fi
    
    echo "✅ Configurações atualizadas com sucesso!"
else
    echo "⚠️ Arquivo config.js não encontrado em $CONFIG_FILE"
fi

# Verificar se nginx está disponível
if ! command -v nginx > /dev/null 2>&1; then
    echo "❌ Nginx não encontrado!"
    exit 1
fi

echo "🌐 Iniciando servidor nginx..."
echo "🔗 Frontend disponível em: http://localhost"
echo "📡 API Backend: $API_BASE_URL"

# Executar comando passado como argumento
exec "$@"