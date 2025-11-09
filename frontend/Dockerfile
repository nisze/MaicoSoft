# ===============================
# MAICONSOFT FRONTEND - DOCKERFILE
# ===============================

FROM nginx:alpine

# Instalar wget para healthcheck
RUN apk add --no-cache wget

# Remover configuração padrão do nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiar arquivos do frontend
COPY pages /usr/share/nginx/html/pages
COPY css /usr/share/nginx/html/css
COPY js /usr/share/nginx/html/js
COPY images /usr/share/nginx/html/images
COPY assets /usr/share/nginx/html/assets

# Criar um index.html de redirecionamento para a página de login
RUN echo '<!DOCTYPE html>' > /usr/share/nginx/html/index.html && \
    echo '<html><head>' >> /usr/share/nginx/html/index.html && \
    echo '<meta http-equiv="refresh" content="0; url=/pages/login.html">' >> /usr/share/nginx/html/index.html && \
    echo '</head><body></body></html>' >> /usr/share/nginx/html/index.html

# Copiar configuração customizada do nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expor porta 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
