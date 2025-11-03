# 🐳 MAICONSOFT - DOCKER DEPLOYMENT

Sistema completo containerizado com Docker para fácil deploy e desenvolvimento.

## 📋 Pré-requisitos

- **Docker Desktop** (Windows/Mac) ou **Docker Engine** (Linux)
- **Docker Compose** (geralmente incluído no Docker Desktop)
- **Git** (para clonar o repositório)

## 📥 Para Quem Vai Clonar o Projeto

### 🔑 Informações Essenciais:

**1. Senha do Supabase (OBRIGATÓRIA):**
```
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn
```

**2. Configurações de Email (JÁ CONFIGURADAS):**
O projeto já vem com credenciais de email configuradas no `application.properties`:
```
spring.mail.username=empresamaiconsoft@gmail.com
spring.mail.password=cvjznokkvtzuaqzm
```
> ✅ **Sistema de email funciona automaticamente!**

**3. Configurações Opcionais (.env):**
```
# Se quiser usar suas próprias credenciais de email
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app-do-gmail
```

### 🚀 Passo a Passo Rápido:

```bash
# 1. Clonar o repositório
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft

# 2. Copiar arquivo de ambiente
cp .env.example .env

# 3. Executar com Docker
docker-compose up --build -d

# 4. Acessar a aplicação
# Frontend: http://localhost
# Backend: http://localhost:8090
# Swagger: http://localhost:8090/swagger-ui.html
```

> ✅ **Pronto!** O projeto deve funcionar imediatamente - banco e email já estão configurados!

## 🚀 Início Rápido

### 1. Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar variáveis (OBRIGATÓRIO)
# Windows: notepad .env
# Linux/Mac: nano .env
```

**Variáveis obrigatórias no .env:**
```bash
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn
```

> ⚠️ **IMPORTANTE:** Use exatamente esta senha para o projeto funcionar corretamente!

### 2. Iniciar Aplicação

**Windows:**
```bash
# Executar script automatizado
docker-start.bat

# OU manualmente
docker-compose up --build -d
```

**Linux/Mac:**
```bash
# Dar permissão ao script
chmod +x docker-start.sh

# Executar script automatizado
./docker-start.sh

# OU manualmente
docker-compose up --build -d
```

### 3. Acessar Aplicação

- 🌐 **Frontend:** http://localhost
- 📡 **Backend API:** http://localhost:8090
- 📖 **Swagger UI:** http://localhost:8090/swagger-ui.html

## 🛠️ Comandos Úteis

### Gerenciamento Básico
```bash
# Iniciar containers
docker-compose up -d

# Parar containers
docker-compose down

# Reiniciar containers
docker-compose restart

# Ver status
docker-compose ps
```

### Logs e Debugging
```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f maiconsoft-api
docker-compose logs -f maiconsoft-frontend

# Entrar no container do backend
docker-compose exec maiconsoft-api sh

# Entrar no container do frontend
docker-compose exec maiconsoft-frontend sh
```

### Rebuild e Limpeza
```bash
# Rebuild sem cache
docker-compose build --no-cache

# Remover containers e volumes
docker-compose down -v

# Limpar imagens não utilizadas
docker system prune -f
```

## 🏗️ Arquitetura dos Containers

### Backend (Spring Boot)
- **Base Image:** `openjdk:21-jdk-slim`
- **Porta:** 8090
- **Volumes:** 
  - `./uploads:/app/uploads` (arquivos de upload)
  - `./logs:/app/logs` (logs da aplicação)

### Frontend (Nginx)
- **Base Image:** `nginx:alpine`
- **Porta:** 80 (HTTP), 443 (HTTPS - futuro)
- **Proxy:** API requests são proxy para o backend

## 🔧 Configurações Avançadas

### Variáveis de Ambiente Disponíveis

**Backend (.env):**
```bash
# Database (OBRIGATÓRIO)
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn

# Email (OPCIONAL - só se quiser usar suas próprias credenciais)
# O sistema já vem com email configurado no application.properties:
# empresamaiconsoft@gmail.com / cvjznokkvtzuaqzm
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app
```

> 📧 **Email já configurado:** O projeto vem com credenciais funcionais no `application.properties`. Configure no `.env` apenas se quiser usar suas próprias credenciais.

**Configurações do Docker Compose:**
- Ajuste de memória JVM: `JAVA_OPTS`
- Configurações de banco: `SPRING_DATASOURCE_*`
- Configurações de email: `SPRING_MAIL_*`

### Portas Customizadas

Para alterar as portas, edite o `docker-compose.yml`:

```yaml
services:
  maiconsoft-frontend:
    ports:
      - "8080:80"  # Alterar porta do frontend
      
  maiconsoft-api:
    ports:
      - "9090:8090"  # Alterar porta do backend
```

## 🔒 Segurança

### Práticas Implementadas:
- ✅ Containers executam com usuário não-root
- ✅ Variáveis sensíveis via environment
- ✅ Nginx com headers de segurança
- ✅ Health checks configurados

### Recomendações para Produção:
- Use HTTPS (configure certificados SSL)
- Configure firewall adequadamente
- Use Docker secrets para dados sensíveis
- Monitore logs e recursos

## 📊 Monitoramento

### Health Checks
```bash
# Verificar saúde dos containers
docker-compose ps

# Status específico
curl http://localhost:8090/api/health
curl http://localhost/
```

### Logs Estruturados
```bash
# Logs em tempo real
docker-compose logs -f --tail=100

# Logs de erro específicos
docker-compose logs maiconsoft-api | grep ERROR
```

## 🐛 Troubleshooting

### Problemas Comuns

**1. Container não inicia:**
```bash
# Verificar logs
docker-compose logs maiconsoft-api

# Verificar configurações
docker-compose config
```

**2. Erro de conexão com banco:**
```bash
# Verificar variáveis de ambiente
docker-compose exec maiconsoft-api env | grep SPRING

# Testar conectividade
docker-compose exec maiconsoft-api ping db.hmjldrzvmaqgetjcepay.supabase.co
```

**3. Frontend não carrega:**
```bash
# Verificar nginx
docker-compose exec maiconsoft-frontend nginx -t

# Verificar proxy
curl -I http://localhost/api/health
```

**4. Problemas de permissão:**
```bash
# Verificar volumes
docker-compose exec maiconsoft-api ls -la /app/uploads

# Corrigir permissões (se necessário)
sudo chown -R $USER:$USER ./uploads ./logs
```

## 🔄 Backup e Restore

### Backup de Uploads
```bash
# Criar backup
tar -czf uploads-backup-$(date +%Y%m%d).tar.gz uploads/

# Restaurar backup
tar -xzf uploads-backup-YYYYMMDD.tar.gz
```

### Backup de Logs
```bash
# Arquivar logs antigos
docker-compose exec maiconsoft-api sh -c "tar -czf /app/logs/archive-$(date +%Y%m%d).tar.gz /app/logs/*.log && rm /app/logs/*.log"
```

## 📈 Performance

### Otimizações Implementadas:
- Multi-stage build para reduzir tamanho das imagens
- Cache de dependências Maven
- Compressão gzip no Nginx
- Health checks otimizados

### Monitoramento de Recursos:
```bash
# Uso de recursos
docker stats

# Uso específico
docker stats maiconsoft-api maiconsoft-frontend
```

## 🌍 Deploy em Produção

### Cloud Providers
- **AWS:** Use ECS ou EKS
- **Google Cloud:** Use Cloud Run ou GKE
- **Azure:** Use Container Instances ou AKS
- **DigitalOcean:** Use App Platform ou Kubernetes

### CI/CD
O projeto está preparado para integração com:
- GitHub Actions
- GitLab CI
- Jenkins
- Azure DevOps

---

## 📞 Suporte

Para problemas específicos do Docker:
1. Verifique os logs: `docker-compose logs -f`
2. Teste a conectividade de rede
3. Valide as variáveis de ambiente
4. Consulte a documentação oficial do Docker

**Comandos de diagnóstico:**
```bash
docker version
docker-compose version
docker system info
docker network ls
```