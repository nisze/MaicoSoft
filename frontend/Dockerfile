# ===============================
# MAICONSOFT FRONTEND - DOCKERFILE
# ===============================
FROM nginx:alpine

# Instalar dependências necessárias
RUN apk add --no-cache \
    curl \
    sed

# Remover configuração padrão do nginx
RUN rm /etc/nginx/conf.d/default.conf

# Copiar configuração customizada do nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiar arquivos do frontend
COPY . /usr/share/nginx/html/

# Criar script para substituir variáveis de ambiente
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

# Expor porta
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:80 || exit 1

# Comando de inicialização
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]